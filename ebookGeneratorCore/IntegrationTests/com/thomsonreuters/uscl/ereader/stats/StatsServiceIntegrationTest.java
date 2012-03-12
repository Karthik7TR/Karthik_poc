package com.thomsonreuters.uscl.ereader.stats;

import java.sql.Timestamp;
import java.util.Calendar;

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


import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration 
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class StatsServiceIntegrationTest {
	private static Logger LOG = Logger.getLogger(StatsServiceIntegrationTest.class);
	public Timestamp UPDATE_DATE = getCurrentTimeStamp();
	private Long jobInstanceId = (long) 1234567890;
	
	@Autowired
	protected PublishingStatsService jobStatsService;

	protected PublishingStats jobStats;
	
	@Autowired
	protected EBookAuditService ebookAuditService;
	protected EbookAudit ebookAudit;
	/**
	 * Mock up the DAO and the Entity.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// mock up the job stats
		
		saveAuditId();
		Long ebookMax =  ebookAuditService.findEbookAuditByEbookDefId((long) 1);
		savePublishingStats(new Integer("1"), ebookMax);

	}
	/**
	 * Operation Unit Test Delete an existing Publishing Stats entity
	 * 
	 */
	@After
	public void deleteJobStats() {
		jobStatsService.deleteJobStats(jobStats);
	}
	/**
	 * Operation Unit Test Delete an existing DocMetadata entity
	 * 
	 */
	@After
	public void deleteEbookAudit() {
		ebookAuditService.deleteEBookAudit(ebookAudit);
	}

	/**
	 * Operation Unit Test Save an existing Publishing Stats entity
	 * 
	 */
	public void savePublishingStats(Integer seqNum, Long auditId) {
		jobStats = new com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats();
		jobStats.setEbookDefId((long) 1);
		jobStats.setAuditId(auditId);
		jobStats.setJobInstanceId( (long) jobInstanceId+seqNum);
		jobStats.setBookVersionSubmitted( "1.1");
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
	public void saveAuditId() {
		ebookAudit = new com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit();
		ebookAudit.setEbookDefinitionId((long) 1);
		ebookAudit.setAuditId((long) 999);
		ebookAudit.setTitleId("Integration\\Test\\Title");
		ebookAudit.setCopyright("copyright");
		ebookAudit.setMaterialId("1234");
		ebookAudit.setAuditType("ADD");
		ebookAudit.setIsTocFlag("Y");
		ebookAudit.setAutoUpdateSupportFlag("Y");
		ebookAudit.setEbookDefinitionCompleteFlag("Y");
		ebookAudit.setIsDeletedFlag(false);
		ebookAudit.setIsProviewTableViewFlag(false);
		ebookAudit.setKeyciteToplineFlag("Y");
		ebookAudit.setOnePassSsoLinkFlag("Y");
		ebookAudit.setSearchIndexFlag("Y");
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
	public void findPublishingStatsByJobIdTest() {


		PublishingStats jobstats = new PublishingStats();
		jobstats.setJobInstanceId(jobInstanceId+1);
		jobstats.setGatherDocExpectedCount(1);
		jobstats.setGatherDocRetrievedCount(1);
		jobstats.setGatherDocRetryCount(0);

		int updateCount = jobStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GATHERDOC);
		
		Assert.assertEquals(updateCount,1);
		
		
		PublishingStats responseJobStats = jobStatsService.findPublishingStatsByJobId(jobInstanceId+1);

		PublishingStats expectedJobStats = new com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats();
		expectedJobStats.setEbookDefId((long) 1);
		expectedJobStats.setAuditId((long) 999);
		expectedJobStats.setJobInstanceId( (long) jobInstanceId+1);
		expectedJobStats.setBookVersionSubmitted( "1.1");
		expectedJobStats.setJobHostName("jobHostName Integration test");
		expectedJobStats.setJobSubmitterName("jobSubmitterName Integration test");
		expectedJobStats.setJobSubmitTimestamp(UPDATE_DATE);
		expectedJobStats.setPublishStartTimestamp(UPDATE_DATE);
		expectedJobStats.setLastUpdated(UPDATE_DATE);
		expectedJobStats.setGatherDocExpectedCount(1);
		expectedJobStats.setGatherDocRetrievedCount(1);
		expectedJobStats.setGatherDocRetryCount(0);

		LOG.debug(" response " + responseJobStats); //12345678900011

		Assert.assertEquals(responseJobStats,expectedJobStats);
	}
	
	@Test
	public void findJobStatsByPubStatsPKTest() {


		PublishingStats jobstats = new PublishingStats();
		jobstats.setJobInstanceId(jobInstanceId+1);
		jobstats.setGatherDocExpectedCount(1);
		jobstats.setGatherDocRetrievedCount(1);
		jobstats.setGatherDocRetryCount(0);

		int updateCount = jobStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GATHERDOC);
		
		Assert.assertEquals(updateCount,1);
		
		PublishingStatsPK pubPK = new PublishingStatsPK();
		pubPK.setJobInstanceId(jobInstanceId+1);
		PublishingStats responseJobStats =  jobStatsService.findJobStatsByPubStatsPK(pubPK);
		
		PublishingStats expectedJobStats = new com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats();
		expectedJobStats.setEbookDefId((long) 1);
		expectedJobStats.setAuditId((long) 999);
		expectedJobStats.setJobInstanceId( (long) jobInstanceId+1);
		expectedJobStats.setBookVersionSubmitted( "1.1");
		expectedJobStats.setJobHostName("jobHostName Integration test");
		expectedJobStats.setJobSubmitterName("jobSubmitterName Integration test");
		expectedJobStats.setJobSubmitTimestamp(UPDATE_DATE);
		expectedJobStats.setPublishStartTimestamp(UPDATE_DATE);
		expectedJobStats.setLastUpdated(UPDATE_DATE);
		expectedJobStats.setGatherDocExpectedCount(1);
		expectedJobStats.setGatherDocRetrievedCount(1);
		expectedJobStats.setGatherDocRetryCount(0);

		LOG.debug(" response " + responseJobStats); //12345678900011

		Assert.assertEquals(responseJobStats,expectedJobStats);
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
		PublishingStats responseJobStats = jobStatsService.findPublishingStatsByJobId(jobInstanceId+1);
		LOG.debug("responseJobStats audit_id is " + responseJobStats.getAuditId());
	
		Long ebookMax =  ebookAuditService.findEbookAuditByEbookDefId((long) 1);
		LOG.debug(" ebookMax " + ebookMax); 
		
		EbookAudit ebookData =  ebookAuditService.findEBookAuditByPrimaryKey(responseJobStats.getAuditId());
		LOG.debug("ebookAudit audit_id is " + ebookData.getAuditId());

	

		EbookAudit responseEbookAudit = jobStatsService.findAuditInfoByJobId((long) jobInstanceId+1);
		EbookAudit expectedEbookAudit = new com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit();
//		expectedEbookAudit.setEbookDefinition(1);
		expectedEbookAudit.setAuditId(ebookMax);

		LOG.debug(" response " + responseEbookAudit); //12345678900011

		Assert.assertEquals(responseEbookAudit.getAuditId(),expectedEbookAudit.getAuditId());
	}
/**
 * Get the current timestamp
 * 
 * @return Timestamp
 */
private Timestamp getCurrentTimeStamp() {
	// create a java calendar instance
	Calendar calendar = Calendar.getInstance();
	return new java.sql.Timestamp(calendar.getTime().getTime());

}
}
