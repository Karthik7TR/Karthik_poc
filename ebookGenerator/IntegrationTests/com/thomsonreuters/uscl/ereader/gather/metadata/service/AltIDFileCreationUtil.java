/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class AltIDFileCreationUtil {

	private static Logger LOG = LogManager.getLogger(AltIDFileCreationUtil.class);

	@Autowired
	protected DocMetadataService documentMetadataService;

	@Autowired
	private PublishingStatsService publishingStatsService;

	@Autowired
	private BookDefinitionService bookDefinitionService;

	protected DocMetadata docmetadata;

	protected PublishingStats pubStats;

	protected BookDefinition book;

	private static final Long BOOK_DEF_ID = new Long(824); 
	
	@Before
	public void setUp() throws Exception {

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

		String altIdFileName = book.getFullyQualifiedTitleId().replace("/", "_") + ".csv";

		LOG.info("Generating CVS file for BookId " + BOOK_DEF_ID + " and title " + book.getFullyQualifiedTitleId());

		List<PublishingStats> pubStatsList = publishingStatsService.getPubStatsByEbookDefSort(BOOK_DEF_ID);
		
		Long newJobInstanceId = null;
		String newVersion = null;

		// Long preJobInstanceId = null;
		Boolean skip = false;

		if (pubStatsList.size() >= 1) {
			Calendar cal = Calendar.getInstance();
		    cal.setTime(pubStatsList.get(0).getJobSubmitTimestamp());			
			int year = cal.get(Calendar.YEAR);
			if (year != 2015) {
				System.out.println("Book has not been generated this year "
						+ year);
				skip = true;
			}
			newJobInstanceId = new Long(pubStatsList.get(0).getJobInstanceId());
			newVersion = pubStatsList.get(0).getBookVersionSubmitted();
		}

		if (!skip) {
			runCVSFile(newJobInstanceId,newVersion,pubStatsList,altIdFileName);
		}
	}
	
	protected void runCVSFile(Long newJobInstanceId,String newVersion, List<PublishingStats> pubStatsList,String altIdFileName){
		
		

		List<Long> preJobInstanceIdList = new ArrayList<Long>();

		String preMajorMinorVersion = newVersion.substring(0, newVersion.indexOf("."));
		
		System.out.println( " ALL VERSIONS ");
		for (PublishingStats pubstats : pubStatsList) {
			System.out.println(pubstats.getJobInstanceId() + " : "+ pubstats.getBookVersionSubmitted()+" : "+pubstats.getJobSubmitTimestamp());
		}

		for (PublishingStats pubstats : pubStatsList) {
			Calendar cal = Calendar.getInstance();
		    cal.setTime(pubstats.getJobSubmitTimestamp());			
			int year = cal.get(Calendar.YEAR);
			
			String oldVersion = pubstats.getBookVersionSubmitted();
			oldVersion = oldVersion.substring(0, oldVersion.indexOf("."));
			if (!preMajorMinorVersion.equalsIgnoreCase(oldVersion) && year !=2015) {
				preJobInstanceIdList.add(pubstats.getJobInstanceId());
				preMajorMinorVersion = oldVersion;
				System.out.println(pubstats.getJobInstanceId() + " ADDED TO COMPARE " + pubstats.getBookVersionSubmitted());
			}
			
		}

		LOG.info(" newJobInstanceId " + newJobInstanceId);
	

		Set<DocMetadata> newDocSet = getDocAuthorityforJobInstance(newJobInstanceId);
		Map<String, String> olDDocInfo = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		
		Long job = null;

		// This check is to make sure the documentfamilies did not change
		// between older versions
		for (Long preJob : preJobInstanceIdList) {
			List<String> removeKey = new ArrayList<String>();
			System.out.println("PreJob " + preJob);
			Set<DocMetadata> docSet = getDocAuthorityforJobInstance(preJob);
			if (olDDocInfo.size() > 0) {
				for (DocMetadata doc : docSet) {
					String firslineCite = normalizeFirsLineSite(doc.getFirstlineCite());					
					
					if (olDDocInfo.containsKey(firslineCite)
							&& !olDDocInfo.get(firslineCite).equalsIgnoreCase(doc.getDocFamilyUuid())) {
						LOG.debug("Document family uuid changed between old versions for firstLineCite "
								+ firslineCite);
						LOG.debug("JobInstanceId " +job+ " : "+ doc.getJobInstanceId());
						LOG.debug("Documents " +olDDocInfo.get(firslineCite)+ " : "+ doc.getDocFamilyUuid());
						removeKey.add(firslineCite);
					}
				}
			}
			job = preJob;
			olDDocInfo.putAll(getDocInfo(docSet,removeKey));
		}

		try {
			createCVSFile(newDocSet, olDDocInfo, altIdFileName);
		}

		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}
	
	

	protected Set<DocMetadata> getDocAuthorityforJobInstance(Long jobInstanceId) {
		DocumentMetadataAuthority response = null;
		response = documentMetadataService.findAllDocMetadataForTitleByJobId(jobInstanceId);
		Set<DocMetadata> docSet = response.getAllDocumentMetadata();
		return docSet;
	}

	protected Map<String, String> getDocInfo(Set<DocMetadata> docSet, List<String> removeKey) {
		Map<String, String> docInfo = new HashMap<String, String>();
		for (DocMetadata doc : docSet) {
			String firslineCite = normalizeFirsLineSite(doc.getFirstlineCite());
			
			if(!removeKey.contains(firslineCite)){
			docInfo.put(firslineCite, doc.getDocFamilyUuid());
			}
			else{
				LOG.debug("did not add to the list "+firslineCite );
			}
		}
		return docInfo;
	}

	protected String normalizeFirsLineSite(String cite) {
		//Removes any '.' after character
		return cite.replaceAll(
				"(\\D)\\.", "$1");
	}
	
	

	protected void createCVSFile(Set<DocMetadata> newDocSet, Map<String, String> oldDocInfo, String altIdFileName)
			throws Exception {

		File file = new File(altIdFileName);
		System.out.println(file.getAbsolutePath());
		FileWriter outputStream = new FileWriter(file);
		BufferedWriter out = new BufferedWriter(outputStream);
		
		File notFoundfile = new File(altIdFileName+"_NotFound");
		System.out.println(notFoundfile.getAbsolutePath());
		FileWriter outputStream1 = new FileWriter(notFoundfile);
		BufferedWriter out1 = new BufferedWriter(outputStream1);
		
		try {
			int i =0;
			for (DocMetadata newDoc : newDocSet) {
				String cite = normalizeFirsLineSite(newDoc.getFirstlineCite());
				if (oldDocInfo.containsKey(cite)) {
					if (oldDocInfo.get(cite).equalsIgnoreCase(newDoc.getDocFamilyUuid())) {
						LOG.info(newDoc.getDocFamilyUuid()+" DocFamilys are same for Cite " + newDoc.getFirstlineCite());
					} else {
						StringBuffer buffer = new StringBuffer();
						buffer.append(oldDocInfo.get(cite));
						buffer.append(",");
						buffer.append(newDoc.getDocFamilyUuid());
						buffer.append(",");
						buffer.append(newDoc.getFirstlineCite() + ",");
						buffer.append("\n");
						out.write(buffer.toString());
					}
				}
				else{
					i++;
					LOG.error("could not find matching family "+newDoc.getDocFamilyUuid()+" "+newDoc.getFirstlineCite());
					out1.write(newDoc.toString()+"\n");
				}
			}
			System.out.println(i+" documents Could not find match" );
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			out.close();
			out1.close();
		}

	}

}
