package com.thomsonreuters.uscl.ereader.stats;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public final class StatsServiceIntegrationTest
{
    private static Logger LOG = LogManager.getLogger(StatsServiceIntegrationTest.class);
    private Timestamp UPDATE_DATE = getCurrentTimeStamp();
    private static Long JOB_INSTANCE_ID = (long) 1234567890;
    private static String BOOK_TITLE = "uscl/an/TEST";

    @Autowired
    protected PublishingStatsService jobStatsService;

    protected PublishingStats jobStats;

    @Autowired
    protected EBookAuditService ebookAuditService;
    protected EbookAudit ebookAudit;

    @Autowired
    protected BookDefinitionService bookDefinitionService;
    protected BookDefinition eBook;

    @Autowired
    protected CodeService codeService;

    /**
     * Mock up the DAO and the Entity.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        // mock up the job stats

        saveBook(); // book will continue to exist and only be updated subsequently
        eBook = bookDefinitionService.findBookDefinitionByTitle(BOOK_TITLE);
        saveAuditId(eBook.getEbookDefinitionId());
        final Long ebookMax = ebookAuditService.findEbookAuditByEbookDefId(eBook.getEbookDefinitionId());
        savePublishingStats(Integer.valueOf(1), ebookMax);
    }

    /**
     * Operation Unit Test Delete an existing Publishing Stats entity
     *
     */
    @After
    public void deleteJobStats()
    {
        jobStatsService.deleteJobStats(jobStats);
    }

    /**
     * Operation Unit Test Delete an existing DocMetadata entity
     *
     */
    @After
    public void deleteEbookAudit()
    {
        ebookAuditService.deleteEBookAudit(ebookAudit);
    }

    /**
     * Operation Unit Test Save an existing Publishing Stats entity
     *
     */
    public void savePublishingStats(final Integer seqNum, final Long auditId)
    {
        jobStats = new com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats();
        final BookDefinition bookDef = bookDefinitionService.findBookDefinitionByTitle(BOOK_TITLE);
        jobStats.setEbookDefId(bookDef.getEbookDefinitionId());
        final EbookAudit audit = new EbookAudit();
        audit.setAuditId(auditId);
        jobStats.setAudit(audit);
        jobStats.setJobInstanceId(JOB_INSTANCE_ID + seqNum);
        jobStats.setBookVersionSubmitted("1.1");
        jobStats.setJobHostName("jobHostName Integration test");
        jobStats.setJobSubmitterName("jobSubmitterName Integration test");
        jobStats.setJobSubmitTimestamp(UPDATE_DATE);
        jobStats.setPublishStartTimestamp(UPDATE_DATE);
        jobStats.setLastUpdated(UPDATE_DATE);
        jobStatsService.savePublishingStats(jobStats);
    }

    /**
     * Operation Unit Test Save an existing Audit entity
     *
     */
    public void saveBook()
    {
        eBook = new com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition();

        //eBook.setEbookDefinitionId((long) 10000);
        eBook.setFullyQualifiedTitleId(BOOK_TITLE);
        eBook.setProviewDisplayName("Integration Test Book");
        eBook.setCopyright("2012 Copyright Integration Test");
        eBook.setDocCollectionName("invalidCollection");
        eBook.setRootTocGuid("roottocguid");
        eBook.setTocCollectionName("invalidTocCollection");
        eBook.setIsbn("1234");
        eBook.setMaterialId("12345");
        eBook.setSourceType(SourceType.TOC);
        eBook.setAutoUpdateSupportFlag(true);
        eBook.setEbookDefinitionCompleteFlag(true);
        eBook.setIsDeletedFlag(false);
        eBook.setKeyciteToplineFlag(true);
        eBook.setOnePassSsoLinkFlag(true);
        eBook.setSearchIndexFlag(true);
        eBook.setPublishedOnceFlag(false);
        eBook.setLastUpdated(UPDATE_DATE);
        eBook.setAuthors(new HashSet<Author>());
        eBook.setEbookNames(new HashSet<EbookName>());
        eBook.setFrontMatterPages(new HashSet<FrontMatterPage>());

        final DocumentTypeCode dc = codeService.getDocumentTypeCodeById((long) 1);
        eBook.setDocumentTypeCodes(dc);

        final PublisherCode publisherCode = codeService.getPublisherCodeById((long) 1);
        eBook.setPublisherCodes(publisherCode);

        bookDefinitionService.saveBookDefinition(eBook);
    }

    /**
     * Operation Unit Test Save an existing Audit entity
     *
     */
    public void saveAuditId(final Long eBookDefinitionId)
    {
        ebookAudit = new com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit();
        ebookAudit.setEbookDefinitionId(eBookDefinitionId);
        ebookAudit.setTitleId(BOOK_TITLE);
        ebookAudit.setCopyright("2012 Copyright Integration Test");
        ebookAudit.setMaterialId("12345");
        ebookAudit.setAuditType("ADD");
        eBook.setSourceType(SourceType.TOC);
        ebookAudit.setAutoUpdateSupportFlag(true);
        ebookAudit.setEbookDefinitionCompleteFlag(true);
        ebookAudit.setIsDeletedFlag(false);
        ebookAudit.setKeyciteToplineFlag(true);
        ebookAudit.setOnePassSsoLinkFlag(true);
        ebookAudit.setSearchIndexFlag(true);
        ebookAudit.setUpdatedBy("StatsServiceIntegrationTest");
        ebookAudit.setLastUpdated(UPDATE_DATE);
        ebookAuditService.saveEBookAudit(ebookAudit);
    }

    /**
     * Operation Unit Test
     *
     * @author Kirsten Gunn
     */
    @Test
    public void findPublishingStatsByJobIdTest()
    {
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(JOB_INSTANCE_ID + 1);
        jobstats.setGatherDocExpectedCount(1);
        jobstats.setGatherDocRetrievedCount(1);
        jobstats.setGatherDocRetryCount(0);
        jobstats.setPublishStatus("Complete");

        final int updateCount = jobStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GATHERDOC);

        Assert.assertEquals(updateCount, 1);

        final PublishingStats responseJobStats = jobStatsService.findPublishingStatsByJobId(JOB_INSTANCE_ID + 1);

        final PublishingStats expectedJobStats = new com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats();
        expectedJobStats.setEbookDefId((long) 1);
        expectedJobStats.setAudit(ebookAudit);
        expectedJobStats.setJobInstanceId(JOB_INSTANCE_ID + 1);
        expectedJobStats.setBookVersionSubmitted("1.1");
        expectedJobStats.setJobHostName("jobHostName Integration test");
        expectedJobStats.setJobSubmitterName("jobSubmitterName Integration test");
        expectedJobStats.setJobSubmitTimestamp(UPDATE_DATE);
        expectedJobStats.setPublishStartTimestamp(UPDATE_DATE);
        expectedJobStats.setLastUpdated(UPDATE_DATE);
        expectedJobStats.setGatherDocExpectedCount(1);
        expectedJobStats.setGatherDocRetrievedCount(1);
        expectedJobStats.setGatherDocRetryCount(0);
        expectedJobStats.setPublishStatus("Complete");

        LOG.debug(" response " + responseJobStats); //12345678900011

        Assert.assertEquals(responseJobStats, expectedJobStats);
    }

    @Test
    public void findJobStatsByPubStatsPKTest()
    {
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(JOB_INSTANCE_ID + 1);
        jobstats.setPublishEndTimestamp(UPDATE_DATE);
        jobstats.setPublishStatus("Complete");

        final int updateCount = jobStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.FINALPUBLISH);

        Assert.assertEquals(updateCount, 1);

        final PublishingStatsPK pubPK = new PublishingStatsPK();
        pubPK.setJobInstanceId(JOB_INSTANCE_ID + 1);
        final PublishingStats responseJobStats = jobStatsService.findJobStatsByPubStatsPK(pubPK);

        final PublishingStats expectedJobStats = new com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats();
        expectedJobStats.setEbookDefId(eBook.getEbookDefinitionId());
        expectedJobStats.setAudit(ebookAudit);
        expectedJobStats.setJobInstanceId(JOB_INSTANCE_ID + 1);
        expectedJobStats.setBookVersionSubmitted("1.1");
        expectedJobStats.setJobHostName("jobHostName Integration test");
        expectedJobStats.setJobSubmitterName("jobSubmitterName Integration test");
        expectedJobStats.setJobSubmitTimestamp(UPDATE_DATE);
        expectedJobStats.setPublishStartTimestamp(UPDATE_DATE);
        expectedJobStats.setLastUpdated(UPDATE_DATE);
        expectedJobStats.setPublishEndTimestamp(UPDATE_DATE);
        expectedJobStats.setPublishStatus("Complete");

        LOG.debug(" response " + responseJobStats); //12345678900011

        Assert.assertEquals(expectedJobStats, responseJobStats);
    }

    /**
     * Operation Unit Test
     *
     * @author Kirsten Gunn
     */
//	@Ignore
    @Test
    public void findJobStatsAuditByEbookDefTest()
    {
        final PublishingStats responseJobStats = jobStatsService.findPublishingStatsByJobId(JOB_INSTANCE_ID + 1);
        LOG.debug("responseJobStats audit_id is " + responseJobStats.getAudit().getAuditId());

        final Long ebookMax = ebookAuditService.findEbookAuditByEbookDefId(eBook.getEbookDefinitionId());
        LOG.debug(" ebookMax " + ebookMax);

        final EbookAudit ebookData = ebookAuditService.findEBookAuditByPrimaryKey(responseJobStats.getAudit().getAuditId());
        LOG.debug("ebookAudit audit_id is " + ebookData.getAuditId());

        final EbookAudit responseEbookAudit = jobStatsService.findAuditInfoByJobId(JOB_INSTANCE_ID + 1);
        final EbookAudit expectedEbookAudit = new com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit();
//		expectedEbookAudit.setEbookDefinition(1);
        expectedEbookAudit.setAuditId(ebookMax);

        LOG.debug(" response " + responseEbookAudit); //12345678900011

        Assert.assertEquals(responseEbookAudit.getAuditId(), expectedEbookAudit.getAuditId());
    }

    /**
     * Get the current timestamp
     *
     * @return Timestamp
     */
    private Timestamp getCurrentTimeStamp()
    {
        // create a java calendar instance
        final Calendar calendar = Calendar.getInstance();
        return new java.sql.Timestamp(calendar.getTime().getTime());
    }
}
