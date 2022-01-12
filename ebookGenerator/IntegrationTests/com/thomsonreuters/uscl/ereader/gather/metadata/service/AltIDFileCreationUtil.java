package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
@Ignore
@Slf4j
public class AltIDFileCreationUtil {

    @Autowired
    protected DocMetadataService documentMetadataService;

    @Autowired
    private PublishingStatsService publishingStatsService;

    @Autowired
    private BookDefinitionService bookDefinitionService;

    protected DocMetadata docmetadata;

    protected PublishingStats pubStats;

    protected BookDefinition book;

    private static final Long BOOK_DEF_ID = Long.valueOf(824);

    @Before
    public void setUp() {
        //Intentionally left blank
    }

    @After
    public void doNothing() {
        // Do nothing
    }

    /**
     * Operation Unit Test
     */
    @Test
    public void generateCVSFile() {
        book = bookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEF_ID);

        final String altIdFileName = book.getFullyQualifiedTitleId().replace("/", "_") + ".csv";

        log.info("Generating CVS file for BookId " + BOOK_DEF_ID + " and title " + book.getFullyQualifiedTitleId());

        final List<PublishingStats> pubStatsList = publishingStatsService.findPublishingStatsByEbookDef(BOOK_DEF_ID);
        Collections.sort(pubStatsList, Collections.reverseOrder());

        Long newJobInstanceId = null;
        String newVersion = null;

        // Long preJobInstanceId = null;
        Boolean skip = false;

        if (pubStatsList.size() >= 1) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(pubStatsList.get(0).getJobSubmitTimestamp());
            final int year = cal.get(Calendar.YEAR);
            if (year != 2015) {
                System.out.println("Book has not been generated this year " + year);
                skip = true;
            }
            newJobInstanceId = Long.valueOf(pubStatsList.get(0).getJobInstanceId());
            newVersion = pubStatsList.get(0).getBookVersionSubmitted();
        }

        if (!skip) {
            runCVSFile(newJobInstanceId, newVersion, pubStatsList, altIdFileName);
        }
    }

    protected void runCVSFile(
        final Long newJobInstanceId,
        final String newVersion,
        final List<PublishingStats> pubStatsList,
        final String altIdFileName) {
        final List<Long> preJobInstanceIdList = new ArrayList<>();

        String preMajorMinorVersion = newVersion.substring(0, newVersion.indexOf("."));

        System.out.println(" ALL VERSIONS ");
        for (final PublishingStats pubstats : pubStatsList) {
            System.out.println(
                pubstats.getJobInstanceId()
                    + " : "
                    + pubstats.getBookVersionSubmitted()
                    + " : "
                    + pubstats.getJobSubmitTimestamp());
        }

        for (final PublishingStats pubstats : pubStatsList) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(pubstats.getJobSubmitTimestamp());
            final int year = cal.get(Calendar.YEAR);

            String oldVersion = pubstats.getBookVersionSubmitted();
            oldVersion = oldVersion.substring(0, oldVersion.indexOf("."));
            if (!preMajorMinorVersion.equalsIgnoreCase(oldVersion) && year != 2015) {
                preJobInstanceIdList.add(pubstats.getJobInstanceId());
                preMajorMinorVersion = oldVersion;
                System.out
                    .println(pubstats.getJobInstanceId() + " ADDED TO COMPARE " + pubstats.getBookVersionSubmitted());
            }
        }

        log.info(" newJobInstanceId " + newJobInstanceId);

        final Set<DocMetadata> newDocSet = getDocAuthorityforJobInstance(newJobInstanceId);
        final Map<String, String> olDDocInfo = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Long job = null;

        // This check is to make sure the documentfamilies did not change
        // between older versions
        for (final Long preJob : preJobInstanceIdList) {
            final List<String> removeKey = new ArrayList<>();
            System.out.println("PreJob " + preJob);
            final Set<DocMetadata> docSet = getDocAuthorityforJobInstance(preJob);
            if (olDDocInfo.size() > 0) {
                for (final DocMetadata doc : docSet) {
                    final String firslineCite = normalizeFirsLineSite(doc.getFirstlineCite());

                    if (olDDocInfo.containsKey(firslineCite)
                        && !olDDocInfo.get(firslineCite).equalsIgnoreCase(doc.getDocFamilyUuid())) {
                        log.debug(
                            "Document family uuid changed between old versions for firstLineCite " + firslineCite);
                        log.debug("JobInstanceId " + job + " : " + doc.getJobInstanceId());
                        log.debug("Documents " + olDDocInfo.get(firslineCite) + " : " + doc.getDocFamilyUuid());
                        removeKey.add(firslineCite);
                    }
                }
            }
            job = preJob;
            olDDocInfo.putAll(getDocInfo(docSet, removeKey));
        }

        try {
            createCVSFile(newDocSet, olDDocInfo, altIdFileName);
        }

        catch (final Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    protected Set<DocMetadata> getDocAuthorityforJobInstance(final Long jobInstanceId) {
        DocumentMetadataAuthority response = null;
        response = documentMetadataService.findAllDocMetadataForTitleByJobId(jobInstanceId);
        final Set<DocMetadata> docSet = response.getAllDocumentMetadata();
        return docSet;
    }

    protected Map<String, String> getDocInfo(final Set<DocMetadata> docSet, final List<String> removeKey) {
        final Map<String, String> docInfo = new HashMap<>();
        for (final DocMetadata doc : docSet) {
            final String firslineCite = normalizeFirsLineSite(doc.getFirstlineCite());

            if (!removeKey.contains(firslineCite)) {
                docInfo.put(firslineCite, doc.getDocFamilyUuid());
            } else {
                log.debug("did not add to the list " + firslineCite);
            }
        }
        return docInfo;
    }

    protected String normalizeFirsLineSite(final String cite) {
        //Removes any '.' after character
        return cite.replaceAll("(\\D)\\.", "$1");
    }

    protected void createCVSFile(
        final Set<DocMetadata> newDocSet,
        final Map<String, String> oldDocInfo,
        final String altIdFileName) {
        final File file = new File(altIdFileName);
        log.debug(file.getAbsolutePath());

        final File notFoundfile = new File(altIdFileName + "_NotFound");
        log.debug(notFoundfile.getAbsolutePath());

        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            try (BufferedWriter out1 = new BufferedWriter(new FileWriter(file))) {
                int i = 0;
                for (final DocMetadata newDoc : newDocSet) {
                    final String cite = normalizeFirsLineSite(newDoc.getFirstlineCite());
                    if (oldDocInfo.containsKey(cite)) {
                        if (oldDocInfo.get(cite).equalsIgnoreCase(newDoc.getDocFamilyUuid())) {
                            log.info(
                                newDoc.getDocFamilyUuid()
                                    + " DocFamilys are same for Cite "
                                    + newDoc.getFirstlineCite());
                        } else {
                            final StringBuffer buffer = new StringBuffer();
                            buffer.append(oldDocInfo.get(cite));
                            buffer.append(",");
                            buffer.append(newDoc.getDocFamilyUuid());
                            buffer.append(",");
                            buffer.append(newDoc.getFirstlineCite() + ",");
                            buffer.append("\n");
                            out.write(buffer.toString());
                        }
                    } else {
                        i++;
                        log.error(
                            "could not find matching family "
                                + newDoc.getDocFamilyUuid()
                                + " "
                                + newDoc.getFirstlineCite());
                        out1.write(newDoc.toString() + "\n");
                    }
                }
                log.debug(i + " documents Could not find match");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
