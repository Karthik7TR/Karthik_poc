package com.thomsonreuters.uscl.ereader.gather.services;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;

import com.westgroup.novus.productapi.NortManager;
import com.westgroup.novus.productapi.NortNode;
import com.westgroup.novus.productapi.Novus;

public class NortServiceTest {

	private static final String DOMAIN_NAME = "w_wlbkrexp";
	private static final String FILTER = "BankruptcyExplorer";
	private static Logger LOG = Logger.getLogger(NortServiceTest.class);

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	private File nortDir = null;
	
//	@Autowired
//	protected PublishingStatsService mockJobStatsService;
	
	private NovusFactory mockNovusFactory;
	private Novus mockNovus;
	private NortManager mockNortManager;
	private NortServiceImpl nortService;
	private NortNode[] mockNortNodeRoot;
	private NortNode[] mockNort2NodeRoot;
	private NortNode mockNortNode;
	private NortNode mockNortNode2;
	private NovusUtility mockNovusUtility;	
//	private PublishingStatsService mockpublishingStatsService;

	
	@Before
	public void setUp() {
		this.mockNovusFactory = EasyMock.createMock(NovusFactory.class);
		this.mockNovus = EasyMock.createMock(Novus.class);
		this.mockNovusUtility = EasyMock.createMock(NovusUtility.class);		
		this.mockNortManager = EasyMock.createMock(NortManager.class);
		this.mockNortNode = EasyMock.createMock(NortNode.class);
		this.mockNortNode2 = EasyMock.createMock(NortNode.class);
//		this.mockJobStatsService = EasyMock.createMock(PublishingStatsService.class);
		mockNortNodeRoot = new NortNode[]{mockNortNode};
		mockNort2NodeRoot = new NortNode[]{mockNortNode,mockNortNode2};
		
		nortDir = temporaryFolder.newFolder("junit_nort");
		
//		this.mockpublishingStatsService = EasyMock.createMock(PublishingStatsService.class);

		// The object under test
		this.nortService = new NortServiceImpl();
		
		nortService.setNovusFactory(mockNovusFactory);
		nortService.setNovusUtility(mockNovusUtility);	
//		nortService.setPublishingStatsService(mockpublishingStatsService); 

	}

	@Test
	public void testCreateNortTreeFile () throws Exception {
		File nortFile = new File(nortDir, "NORT"+DOMAIN_NAME+FILTER+EBConstants.XML_FILE_EXTENSION);
		NortNode[] children = new NortNode[]{};
		mockNortNodeRoot[0] = mockNortNode;
		
		Date date = new Date();
		SimpleDateFormat formatter =
	            new SimpleDateFormat("yyyyMMddHHmmss");				
		String YYYYMMDDHHmmss;
		YYYYMMDDHHmmss = formatter.format(date);
		String YYYYM1DDHHmmss  = "" +Long.valueOf(YYYYMMDDHHmmss) +1;
		DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");	       		
		String startDateFinal  = formatterFinal.format(date);
		
		children = getChildNodes(5, 'a', YYYYM1DDHHmmss, 2).toArray(new NortNode[]{});
		
		try {
//			String YYYYMMDDHHmmss = "20120206111111"; 
			// Record expected calls
			EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
			EasyMock.expect(mockNovusUtility.getDocRetryCount()).andReturn("3").times(2);
			EasyMock.expect(mockNovusUtility.getNortRetryCount()).andReturn("3").times(2);
			EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("3").times(2);			
			EasyMock.expect(mockNovus.getNortManager()).andReturn(mockNortManager);
			mockNortManager.setDomainDescriptor(DOMAIN_NAME);
			mockNortManager.setFilterName(FILTER, 0);
			mockNortManager.setShowChildrenCount(true);
			mockNortManager.fillNortNodes(children, 0, 6);
//			mockNortManager.setNortVersion(YYYYMMDDHHmmss);

			EasyMock.expect(mockNortManager.getRootNodes()).andReturn(mockNortNodeRoot);
//			EasyMock.expect(mockNortNode.getLabel()).andReturn(" &lt; Root &amp;  §  &quot; Node&apos;s &gt; ").times(1); 
			EasyMock.expect(mockNortNode.getLabel()).andReturn(" &lt; Root &amp;  §  &quot; Node&apos;s &gt; ").times(1); 
			EasyMock.expect(mockNortNode.getGuid()).andReturn("nortGuid").times(1); 
//			EasyMock.expect(mockNortNode.getPayload()).andReturn(null).anyTimes();
			EasyMock.expect(mockNortNode.getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).anyTimes();
			EasyMock.expect(mockNortNode.getPayloadElement("/n-nortpayload/n-start-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNortNode.getPayloadElement("/n-nortpayload/n-end-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNortNode.getPayloadElement("/n-nortpayload/node-type")).andReturn("").anyTimes();
			EasyMock.expect(mockNortNode.getChildrenCount()).andReturn(5).anyTimes();
			EasyMock.expect(mockNortNode.getChildren()).andReturn(children).anyTimes();
			
			mockNovus.shutdownMQ();
			
			// Invoke the object under test
			nortDir.mkdirs();
			

			// Set up for replay
			EasyMock.replay(mockNovusFactory);
			EasyMock.replay(mockNovus);
			EasyMock.replay(mockNortManager);
			EasyMock.replay(mockNortNode);
			EasyMock.replay(mockNovusUtility);	
			
			GatherResponse gatherResponse = nortService.findTableOfContents(DOMAIN_NAME, FILTER, nortFile, date);
			LOG.debug(gatherResponse);
			
			// Verify created files and directories
			Assert.assertTrue(nortFile.exists());
			
			// Verify all call made as expected
			EasyMock.verify(mockNovusFactory);
			EasyMock.verify(mockNovus);
			EasyMock.verify(mockNortManager);
			EasyMock.verify(mockNortNode);
//			EasyMock.verify(mockpublishingStatsService);

			// compare file contents.
//			assert.stuff
			String tocFromNORT = readFileAsString(nortFile);
			LOG.debug("tocFromNORT =" + tocFromNORT);
			assertTrue(tocFromNORT != null);
			
			StringBuffer expectedTocContent = new StringBuffer(1000);

			expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
			expectedTocContent.append("<EBook>\r\n");
			expectedTocContent.append("<EBookToc><Name> &lt; Root &amp;  §  &quot; Node&apos;s &gt;  (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>nortGuid1</Guid>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 0a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_0a2</Guid><DocumentGuid>UUID_0a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 1a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_1a3</Guid><DocumentGuid>UUID_1a</DocumentGuid></EBookToc>\r\n");
//			expectedTocContent.append("<EBookToc><Name>Child 2a</Name><Guid>NORT_UUID_2a4</Guid><DocumentGuid>UUID_2a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 2a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_2a4</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 3a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_3a5</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 4a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_4a6</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("</EBookToc>\r\n");
			expectedTocContent.append("</EBook>\r\n");
			LOG.debug("expectedTocContent =" + expectedTocContent.toString());

			Assert.assertEquals(expectedTocContent.toString(), tocFromNORT);
			} 
		finally {
			// Temporary file will clean up after itself.
		}
	}

	@Test
	public void testCreateNort2NodeTreeFile () throws Exception {
		File nortFile = new File(nortDir, "DblRootNode"+DOMAIN_NAME+FILTER+EBConstants.XML_FILE_EXTENSION);

		NortNode[] children = new NortNode[]{};
		NortNode[] rootChildren = new NortNode[]{};
		
		Date date = new Date();
		SimpleDateFormat formatter =
	            new SimpleDateFormat("yyyyMMddHHmmss");				
		String YYYYMMDDHHmmss;
		YYYYMMDDHHmmss = formatter.format(date);
		String YYYYM1DDHHmmss  = "" +Long.valueOf(YYYYMMDDHHmmss) +1;
		DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");	       		
		String startDateFinal  = formatterFinal.format(date);
		
		children = getChildNodes(5, 'a', YYYYM1DDHHmmss, 2).toArray(new NortNode[]{});
		rootChildren = getChildNodes(2, 'b', YYYYM1DDHHmmss, 2).toArray(new NortNode[]{});
		mockNort2NodeRoot[0] = mockNortNode;
		mockNort2NodeRoot[1] = mockNortNode2;

		try {
			// Record expected calls
			EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
			EasyMock.expect(mockNovusUtility.getDocRetryCount()).andReturn("3").times(2);
			EasyMock.expect(mockNovusUtility.getNortRetryCount()).andReturn("3").times(2);
			EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("3").times(2);			
			EasyMock.expect(mockNovus.getNortManager()).andReturn(mockNortManager);
			mockNortManager.setDomainDescriptor(DOMAIN_NAME);
			mockNortManager.setFilterName(FILTER, 0);
			mockNortManager.setShowChildrenCount(true);
			mockNortManager.fillNortNodes(children, 0, 6);
			mockNortManager.fillNortNodes(rootChildren, 0, 3);
//			mockNortManager.setNortVersion(YYYYMMDDHHmmss);
        
			EasyMock.expect(mockNortManager.getRootNodes()).andReturn(mockNort2NodeRoot);
			EasyMock.expect(mockNort2NodeRoot[0].getLabel()).andReturn(" &lt; Root 1 &amp;  §  &quot; Node&apos;s &gt; ").times(1); 
			EasyMock.expect(mockNort2NodeRoot[1].getLabel()).andReturn(" &lt; Root 2 &amp;  §  &quot; Node&apos;s &gt; ").times(1); 
			EasyMock.expect(mockNort2NodeRoot[0].getGuid()).andReturn("nortGuid").times(1); 
			EasyMock.expect(mockNort2NodeRoot[1].getGuid()).andReturn("nortGuid").times(1); 
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/node-type")).andReturn("").anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/node-type")).andReturn("").anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/n-end-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/n-end-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/n-start-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/n-start-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getChildrenCount()).andReturn(5).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getChildrenCount()).andReturn(5).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getChildren()).andReturn(children).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getChildren()).andReturn(rootChildren).anyTimes();
			
			mockNovus.shutdownMQ();
			
			// Invoke the object under test
			nortDir.mkdirs();
			

			// Set up for replay
			EasyMock.replay(mockNovusFactory);
			EasyMock.replay(mockNovus);
			EasyMock.replay(mockNortManager);
			EasyMock.replay(mockNortNode);
			EasyMock.replay(mockNortNode2);
			EasyMock.replay(mockNovusUtility);			
			
			nortService.findTableOfContents(DOMAIN_NAME, FILTER, nortFile, date);
			
			// Verify created files and directories
			Assert.assertTrue(nortFile.exists());
			
			// Verify all call made as expected
			EasyMock.verify(mockNovusFactory);
			EasyMock.verify(mockNovus);
			EasyMock.verify(mockNortManager);
			EasyMock.verify(mockNortNode);
			EasyMock.verify(mockNortNode2);

			// compare file contents.
//			assert.stuff
			String tocFromNORT = readFileAsString(nortFile);
			LOG.debug("tocFromNORT2roots =" + tocFromNORT);
			assertTrue(tocFromNORT != null);
			
			StringBuffer expectedTocContent = new StringBuffer(1000);

			expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
			expectedTocContent.append("<EBook>\r\n");
			expectedTocContent.append("<EBookToc><Name> &lt; Root 1 &amp;  §  &quot; Node&apos;s &gt;  (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>nortGuid1</Guid>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 0a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_0a2</Guid><DocumentGuid>UUID_0a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 1a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_1a3</Guid><DocumentGuid>UUID_1a</DocumentGuid></EBookToc>\r\n");
//			expectedTocContent.append("<EBookToc><Name>Child 2a</Name><Guid>NORT_UUID_2a4</Guid><DocumentGuid>UUID_2a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 2a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_2a4</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
			
			expectedTocContent.append("<EBookToc><Name>Child 3a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_3a5</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 4a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_4a6</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("</EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name> &lt; Root 2 &amp;  §  &quot; Node&apos;s &gt;  (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>nortGuid7</Guid>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 0b (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_0b8</Guid><DocumentGuid>UUID_0b</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 1b (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_1b9</Guid><DocumentGuid>UUID_1b</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("</EBookToc>\r\n");
			expectedTocContent.append("</EBook>\r\n");
			LOG.debug("expectedTocContent2roots =" + expectedTocContent.toString());

			Assert.assertEquals(expectedTocContent.toString(), tocFromNORT);
			} 
		finally {
			// Temporary file will clean up after itself.
		}
	}

	@Test
	public void testMissingDocument () throws Exception {
		File nortFile = new File(nortDir, "missingDocument"+DOMAIN_NAME+FILTER+EBConstants.XML_FILE_EXTENSION);

		NortNode[] children = new NortNode[]{};
		
		Date date = new Date();
		SimpleDateFormat formatter =
	            new SimpleDateFormat("yyyyMMddHHmmss");				
		String YYYYMMDDHHmmss;
		YYYYMMDDHHmmss = formatter.format(date);
		String YYYYM1DDHHmmss  = "" +Long.valueOf(YYYYMMDDHHmmss) +1;
		DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");	       		
		String startDateFinal  = formatterFinal.format(date);
		
		children = getChildNodes(5, 'a', YYYYM1DDHHmmss, 2).toArray(new NortNode[]{});
		mockNort2NodeRoot[0] = mockNortNode;
		mockNort2NodeRoot[1] = mockNortNode2;

		try {
			// Record expected calls
			EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
			EasyMock.expect(mockNovusUtility.getDocRetryCount()).andReturn("3").times(2);
			EasyMock.expect(mockNovusUtility.getNortRetryCount()).andReturn("3").times(2);
			EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("3").times(2);			
			EasyMock.expect(mockNovus.getNortManager()).andReturn(mockNortManager);
			mockNortManager.setDomainDescriptor(DOMAIN_NAME);
			mockNortManager.setFilterName(FILTER, 0);
			mockNortManager.setShowChildrenCount(true);
			mockNortManager.fillNortNodes(children, 0, 6);
//			mockNortManager.setNortVersion(YYYYMMDDHHmmss);

			EasyMock.expect(mockNortManager.getRootNodes()).andReturn(mockNort2NodeRoot);
			EasyMock.expect(mockNort2NodeRoot[0].getLabel()).andReturn(" &lt; Root 1 &amp;  §  &quot; Node&apos;s &gt; ").anyTimes(); 
			EasyMock.expect(mockNort2NodeRoot[1].getLabel()).andReturn(" &lt; Root 2 &amp;  §  &quot; Node&apos;s &gt; ").anyTimes(); 
			EasyMock.expect(mockNort2NodeRoot[0].getGuid()).andReturn("nortGuid").times(1); 
			EasyMock.expect(mockNort2NodeRoot[1].getGuid()).andReturn("nortGuid").times(1); 
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/node-type")).andReturn("").anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/node-type")).andReturn("").anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/n-end-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/n-end-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/n-start-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/n-start-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getChildrenCount()).andReturn(5).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getChildrenCount()).andReturn(5).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getChildren()).andReturn(children).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getChildren()).andReturn(null).anyTimes();
			
			mockNovus.shutdownMQ();
			
			// Invoke the object under test
			nortDir.mkdirs();
			

			// Set up for replay
			EasyMock.replay(mockNovusFactory);
			EasyMock.replay(mockNovus);
			EasyMock.replay(mockNortManager);
			EasyMock.replay(mockNortNode);
			EasyMock.replay(mockNortNode2);
			EasyMock.replay(mockNovusUtility);			
//			EasyMock.replay(mockpublishingStatsService);
			
			nortService.findTableOfContents(DOMAIN_NAME, FILTER, nortFile, date);
			
			// Verify created files and directories
			Assert.assertTrue(nortFile.exists());
			
			// Verify all call made as expected
			EasyMock.verify(mockNovusFactory);
			EasyMock.verify(mockNovus);
			EasyMock.verify(mockNortManager);
			EasyMock.verify(mockNortNode);
			EasyMock.verify(mockNortNode2);

			// compare file contents.
//			assert.stuff
			String tocFromNORT = readFileAsString(nortFile);
			LOG.debug("tocFromNORT2roots =" + tocFromNORT);
			assertTrue(tocFromNORT != null);
			
			StringBuffer expectedTocContent = new StringBuffer(1000);

			expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
			expectedTocContent.append("<EBook>\r\n");
			expectedTocContent.append("<EBookToc><Name> &lt; Root 1 &amp;  §  &quot; Node&apos;s &gt;  (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>nortGuid1</Guid>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 0a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_0a2</Guid><DocumentGuid>UUID_0a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 1a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_1a3</Guid><DocumentGuid>UUID_1a</DocumentGuid></EBookToc>\r\n");
//			expectedTocContent.append("<EBookToc><Name>Child 2a</Name><Guid>NORT_UUID_2a4</Guid><DocumentGuid>UUID_2a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 2a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_2a4</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
			
			expectedTocContent.append("<EBookToc><Name>Child 3a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_3a5</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 4a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_4a6</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("</EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name> &lt; Root 2 &amp;  §  &quot; Node&apos;s &gt;  (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>nortGuid7</Guid>\r\n");
			expectedTocContent.append("<MissingDocument></MissingDocument></EBookToc>\r\n");
			expectedTocContent.append("</EBook>\r\n");
			LOG.debug("expectedTocContent2roots =" + expectedTocContent.toString());

			Assert.assertEquals(expectedTocContent.toString(), tocFromNORT);
			} 
		finally {
			// Temporary file will clean up after itself.
		}
	}
	
	@Test
	public void testMissingDoc2Level () throws Exception {
		File nortFile = new File(nortDir, "DblRootNode"+DOMAIN_NAME+FILTER+EBConstants.XML_FILE_EXTENSION);

		NortNode[] children = new NortNode[]{};
		NortNode[] rootChildren = new NortNode[]{};
		
		Date date = new Date();
		SimpleDateFormat formatter =
	            new SimpleDateFormat("yyyyMMddHHmmss");				
		String YYYYMMDDHHmmss;
		YYYYMMDDHHmmss = formatter.format(date);
		String YYYYM1DDHHmmss  = "" +Long.valueOf(YYYYMMDDHHmmss) +1;
		DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");	       		
		String startDateFinal  = formatterFinal.format(date);
		
		children = getChildNodes(5, 'a', YYYYM1DDHHmmss, 2).toArray(new NortNode[]{});
		rootChildren = getChildNodes(1, 'b', YYYYM1DDHHmmss, 0).toArray(new NortNode[]{});
		mockNort2NodeRoot[0] = mockNortNode;
		mockNort2NodeRoot[1] = mockNortNode2;

		try {
			// Record expected calls
			EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
			EasyMock.expect(mockNovusUtility.getDocRetryCount()).andReturn("3").times(2);
			EasyMock.expect(mockNovusUtility.getNortRetryCount()).andReturn("3").times(2);
			EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("3").times(2);			
			EasyMock.expect(mockNovus.getNortManager()).andReturn(mockNortManager);
			mockNortManager.setDomainDescriptor(DOMAIN_NAME);
			mockNortManager.setFilterName(FILTER, 0);
			mockNortManager.setShowChildrenCount(true);
			mockNortManager.fillNortNodes(children, 0, 6);
			mockNortManager.fillNortNodes(rootChildren, 0, 2);
//			mockNortManager.setNortVersion(YYYYMMDDHHmmss);

	       
			EasyMock.expect(mockNortManager.getRootNodes()).andReturn(mockNort2NodeRoot);
			EasyMock.expect(mockNort2NodeRoot[0].getLabel()).andReturn(" &lt; Root 1 &amp;  §  &quot; Node&apos;s &gt; ").times(1); 
			EasyMock.expect(mockNort2NodeRoot[1].getLabel()).andReturn(" &lt; Root 2 &amp;  §  &quot; Node&apos;s &gt; ").times(1); 
			EasyMock.expect(mockNort2NodeRoot[0].getGuid()).andReturn("nortGuid").times(1); 
			EasyMock.expect(mockNort2NodeRoot[1].getGuid()).andReturn("nortGuid").times(1); 
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/node-type")).andReturn("").anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/node-type")).andReturn("").anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/n-end-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/n-end-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getPayloadElement("/n-nortpayload/n-start-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getPayloadElement("/n-nortpayload/n-start-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getChildrenCount()).andReturn(5).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getChildrenCount()).andReturn(5).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[0].getChildren()).andReturn(children).anyTimes();
			EasyMock.expect(mockNort2NodeRoot[1].getChildren()).andReturn(rootChildren).anyTimes();
			
			mockNovus.shutdownMQ();
			
			// Invoke the object under test
			nortDir.mkdirs();
			

			// Set up for replay
			EasyMock.replay(mockNovusFactory);
			EasyMock.replay(mockNovus);
			EasyMock.replay(mockNortManager);
			EasyMock.replay(mockNortNode);
			EasyMock.replay(mockNortNode2);
			EasyMock.replay(mockNovusUtility);			
			
			nortService.findTableOfContents(DOMAIN_NAME, FILTER, nortFile, date);
			
			// Verify created files and directories
			Assert.assertTrue(nortFile.exists());
			
			// Verify all call made as expected
			EasyMock.verify(mockNovusFactory);
			EasyMock.verify(mockNovus);
			EasyMock.verify(mockNortManager);
			EasyMock.verify(mockNortNode);
			EasyMock.verify(mockNortNode2);

			// compare file contents.
//			assert.stuff
			String tocFromNORT = readFileAsString(nortFile);
			LOG.debug("tocFromNORT2roots =" + tocFromNORT);
			assertTrue(tocFromNORT != null);
			
			StringBuffer expectedTocContent = new StringBuffer(1000);

			expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
			expectedTocContent.append("<EBook>\r\n");
			expectedTocContent.append("<EBookToc><Name> &lt; Root 1 &amp;  §  &quot; Node&apos;s &gt;  (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>nortGuid1</Guid>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 0a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_0a2</Guid><DocumentGuid>UUID_0a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 1a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_1a3</Guid><DocumentGuid>UUID_1a</DocumentGuid></EBookToc>\r\n");
//			expectedTocContent.append("<EBookToc><Name>Child 2a</Name><Guid>NORT_UUID_2a4</Guid><DocumentGuid>UUID_2a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 2a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_2a4</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
			
			expectedTocContent.append("<EBookToc><Name>Child 3a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_3a5</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 4a (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_4a6</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("</EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name> &lt; Root 2 &amp;  §  &quot; Node&apos;s &gt;  (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>nortGuid7</Guid>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 0b (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_0b8</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
//			expectedTocContent.append("<EBookToc><Name>Child 1b</Name><Guid>NORT_UUID_1b9</Guid><DocumentGuid>UUID_1b</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("</EBookToc>\r\n");
			expectedTocContent.append("</EBook>\r\n");
			LOG.debug("expectedTocContent2roots =" + expectedTocContent.toString());

			Assert.assertEquals(expectedTocContent.toString(), tocFromNORT);
			} 
		finally {
			// Temporary file will clean up after itself.
		}
	}
	
	@Test
	public void testMissingDocSections () throws Exception {
		File nortFile = new File(nortDir, "MissingSection"+DOMAIN_NAME+FILTER+EBConstants.XML_FILE_EXTENSION);

		NortNode[] children = new NortNode[]{};
		mockNortNodeRoot[0] = mockNortNode;
		
		Date date = new Date();
		SimpleDateFormat formatter =
	            new SimpleDateFormat("yyyyMMddHHmmss");				
		String YYYYMMDDHHmmss;
		YYYYMMDDHHmmss = formatter.format(date);
		String YYYYM1DDHHmmss  = "" +Long.valueOf(YYYYMMDDHHmmss) +1;
		DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");	       		
		String startDateFinal  = formatterFinal.format(date);
		
		children = getChildNodeWithChildNodes('c', YYYYM1DDHHmmss, 0).toArray(new NortNode[]{});
		
		try {
//			String YYYYMMDDHHmmss = "20120206111111"; 
			// Record expected calls
			EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
			EasyMock.expect(mockNovusUtility.getDocRetryCount()).andReturn("3").times(2);
			EasyMock.expect(mockNovusUtility.getNortRetryCount()).andReturn("3").times(2);
			EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("3").times(2);			
			EasyMock.expect(mockNovus.getNortManager()).andReturn(mockNortManager);
			mockNortManager.setDomainDescriptor(DOMAIN_NAME);
			mockNortManager.setFilterName(FILTER, 0);
			mockNortManager.setShowChildrenCount(true);
			mockNortManager.fillNortNodes(children, 0, 1);
//			mockNortManager.setNortVersion(YYYYMMDDHHmmss);

			EasyMock.expect(mockNortManager.getRootNodes()).andReturn(mockNortNodeRoot);
			EasyMock.expect(mockNortNode.getLabel()).andReturn(" &lt; Root &amp;  §  &quot; Node&apos;s &gt; ").times(1); 
			EasyMock.expect(mockNortNode.getGuid()).andReturn("nortGuid").times(1); 
//			EasyMock.expect(mockNortNode.getPayload()).andReturn(null).anyTimes();
			EasyMock.expect(mockNortNode.getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).anyTimes();
			EasyMock.expect(mockNortNode.getPayloadElement("/n-nortpayload/n-start-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNortNode.getPayloadElement("/n-nortpayload/n-end-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(mockNortNode.getPayloadElement("/n-nortpayload/node-type")).andReturn("").anyTimes();
			EasyMock.expect(mockNortNode.getChildrenCount()).andReturn(5).anyTimes();
			EasyMock.expect(mockNortNode.getChildren()).andReturn(children).anyTimes();
			
			mockNovus.shutdownMQ();
			
			// Invoke the object under test
			nortDir.mkdirs();
			

			// Set up for replay
			EasyMock.replay(mockNovusFactory);
			EasyMock.replay(mockNovus);
			EasyMock.replay(mockNortManager);
			EasyMock.replay(mockNortNode);
			EasyMock.replay(mockNovusUtility);	
			
			GatherResponse gatherResponse = nortService.findTableOfContents(DOMAIN_NAME, FILTER, nortFile, date);
			LOG.debug(gatherResponse);
			
			// Verify created files and directories
			Assert.assertTrue(nortFile.exists());
			
			// Verify all call made as expected
			EasyMock.verify(mockNovusFactory);
			EasyMock.verify(mockNovus);
			EasyMock.verify(mockNortManager);
			EasyMock.verify(mockNortNode);
//			EasyMock.verify(mockpublishingStatsService);

			// compare file contents.
//			assert.stuff
			String tocFromNORT = readFileAsString(nortFile);
			LOG.debug("tocFromNORT =" + tocFromNORT);
			assertTrue(tocFromNORT != null);
			
			StringBuffer expectedTocContent = new StringBuffer(1000);
			
			expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
			expectedTocContent.append("<EBook>\r\n");
			expectedTocContent.append("<EBookToc><Name> &lt; Root &amp;  §  &quot; Node&apos;s &gt;  (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>nortGuid1</Guid>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child c (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID2</Guid>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 0b (effective ");
			expectedTocContent.append(startDateFinal);
			expectedTocContent.append(") </Name><Guid>NORT_UUID_0b3</Guid><DocumentGuid>UUID_0b</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("</EBookToc>\r\n");
			expectedTocContent.append("</EBookToc>\r\n");
			expectedTocContent.append("</EBook>\r\n");
			LOG.debug("expectedTocContent =" + expectedTocContent.toString());

			Assert.assertEquals(expectedTocContent.toString(), tocFromNORT);
			} 
		finally {
			// Temporary file will clean up after itself.
		}
	}
	
	/**
	 * Creates child nodes.this is a helper method.
	 * If you change the number of children, be sure to modify the fillNortNodes above.
	 * @param maxChildren number of children to create
	 * @return List of NortNodes with child expects set.
	 * @throws java.io.IOException
	 */
	private List<NortNode> getChildNodes(int maxChildren, char prefix, String YYYYM1DDHHmmss, int noDoc) throws Exception {
		List<NortNode> childNodes = new ArrayList<NortNode>();
		
		for (int i=0; i< maxChildren; i++) {
			NortNode child = EasyMock.createMock(NortNode.class);
			EasyMock.expect(child.getLabel()).andReturn("Child " + i + prefix).anyTimes();
//			EasyMock.expect(child.getPayload()).andReturn("nortGuid1").anyTimes();
			EasyMock.expect(child.getPayloadElement("/n-nortpayload/node-type")).andReturn("").anyTimes();
			EasyMock.expect(child.getPayloadElement("/n-nortpayload/n-start-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(child.getPayloadElement("/n-nortpayload/n-end-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			if (i != noDoc) // make one have no document
			{
				EasyMock.expect(child.getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn("UUID_" + i+ prefix).times(2);
			}
			else
			{
				EasyMock.expect(child.getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).times(2);
			}
			EasyMock.expect(child.getChildrenCount()).andReturn(0).anyTimes();
			EasyMock.expect(child.getChildren()).andReturn(null).anyTimes();
			EasyMock.expect(child.getGuid()).andReturn("NORT_UUID_" + i+ prefix).times(2);
		
			EasyMock.replay(child);
			childNodes.add(child);
		}
		
		// Create a child with subsection to be skipped
		NortNode child = EasyMock.createMock(NortNode.class);
		EasyMock.expect(child.getLabel()).andReturn("Child " + maxChildren + prefix).anyTimes();
//		EasyMock.expect(child.getPayload()).andReturn("nortGuid").anyTimes();
		EasyMock.expect(child.getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).anyTimes();
		EasyMock.expect(child.getPayloadElement("/n-nortpayload/n-end-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
//		EasyMock.expect(child.getPayloadElement("/n-nortpayload/n-start-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
		EasyMock.expect(child.getPayloadElement("/n-nortpayload/node-type")).andReturn("subsection").anyTimes();
		EasyMock.expect(child.getChildrenCount()).andReturn(0).anyTimes();
		EasyMock.expect(child.getChildren()).andReturn(null).anyTimes();
		EasyMock.expect(child.getGuid()).andReturn("NORT_UUID_" + maxChildren+ prefix).times(2);
		
		// Create a child with old date to be skipped
		NortNode child2 = EasyMock.createMock(NortNode.class);
		EasyMock.expect(child2.getLabel()).andReturn("Child " + maxChildren + prefix).anyTimes();
		
//		EasyMock.expect(child2.getPayload()).andReturn(null).anyTimes();

		EasyMock.expect(child2.getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).anyTimes();
		EasyMock.expect(child2.getPayloadElement("/n-nortpayload/n-end-date")).andReturn("19910927235900").anyTimes();
		EasyMock.expect(child2.getPayloadElement("/n-nortpayload/node-type")).andReturn("subsection").anyTimes();
		EasyMock.expect(child2.getChildrenCount()).andReturn(0).anyTimes();
		EasyMock.expect(child2.getChildren()).andReturn(null).anyTimes();
		EasyMock.expect(child2.getGuid()).andReturn("NORT_UUID_" + maxChildren+ prefix).times(2);
		
		EasyMock.replay(child);
		childNodes.add(child);
		return childNodes;
	}
	
	/**
	 * Creates child nodes.this is a helper method.
	 * If you change the number of children, be sure to modify the fillNortNodes above.
	 * @param maxChildren number of children to create
	 * @return List of NortNodes with child expects set.
	 * @throws java.io.IOException
	 */
	private List<NortNode> getChildNodeWithChildNodes(char prefix, String YYYYM1DDHHmmss, int noDoc) throws Exception {
		List<NortNode> childNodes = new ArrayList<NortNode>();
		
		NortNode[] rootChildren = new NortNode[]{};
				
		rootChildren = getChildNodes(1, 'b', YYYYM1DDHHmmss, -1).toArray(new NortNode[]{});
		mockNortManager.fillNortNodes(rootChildren, 0, 2);

		
			NortNode child = EasyMock.createMock(NortNode.class);
			EasyMock.expect(child.getLabel()).andReturn("Child " + prefix).anyTimes();
//			EasyMock.expect(child.getPayload()).andReturn("nortGuid1").anyTimes();
			EasyMock.expect(child.getPayloadElement("/n-nortpayload/node-type")).andReturn("").anyTimes();
			EasyMock.expect(child.getPayloadElement("/n-nortpayload/n-start-date")).andReturn(YYYYM1DDHHmmss).anyTimes();
			EasyMock.expect(child.getPayloadElement("/n-nortpayload/n-end-date")).andReturn(YYYYM1DDHHmmss).anyTimes();

			EasyMock.expect(child.getPayloadElement("/n-nortpayload/n-doc-guid")).andReturn(null).anyTimes();
			
			EasyMock.expect(child.getChildrenCount()).andReturn(2).anyTimes();
			EasyMock.expect(child.getChildren()).andReturn(rootChildren).anyTimes();
			EasyMock.expect(child.getGuid()).andReturn("NORT_UUID").times(1);
		
			EasyMock.replay(child);
			childNodes.add(child);
			return childNodes;
	}
	/**
	 * Reads specified file and returns in string format.this is a helper method.
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
	private static String readFileAsString(File filePath) throws Exception {
	    StringBuffer buffer = new StringBuffer();
	    FileInputStream fis =
	    		new FileInputStream(filePath);
	    try {
	        InputStreamReader isr =
	            new InputStreamReader(fis, "UTF8");
	        Reader in = new BufferedReader(isr);
	        int ch;
	        while ((ch = in.read()) > -1) {
	                buffer.append((char)ch);
	        }
	        in.close();
	        return buffer.toString();
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    } finally {
	    	fis.close();
	    }
	}
	
	@Test (expected=GatherException.class)
	public void testGetNortDataWithNovusException() throws Exception {
		File nortFile = new File(nortDir, "FAIL"+DOMAIN_NAME+FILTER+EBConstants.XML_FILE_EXTENSION);
		
		Date date = new Date();
		SimpleDateFormat formatter =
	            new SimpleDateFormat("yyyyMMddHHmmss");				
		String YYYYMMDDHHmmss;
		YYYYMMDDHHmmss = formatter.format(date);
		
		// Record expected calls
		EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
		EasyMock.expect(mockNovusUtility.getDocRetryCount()).andReturn("3").times(2);
		EasyMock.expect(mockNovusUtility.getNortRetryCount()).andReturn("3").times(2);
		EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("3").times(2);		
		EasyMock.expect(mockNovus.getNortManager()).andReturn(mockNortManager);
		mockNortManager.setShowChildrenCount(true);
		mockNortManager.setDomainDescriptor(DOMAIN_NAME);
		mockNortManager.setFilterName(FILTER, 0);
		mockNortManager.setNortVersion(YYYYMMDDHHmmss);
		mockNovus.shutdownMQ();
		EasyMock.expect(mockNortManager.getRootNodes()).andThrow(new MockNovusException());

		// Replay
		EasyMock.replay(mockNovusFactory);
		EasyMock.replay(mockNovus);
		EasyMock.replay(mockNortManager);
		EasyMock.replay(mockNovusUtility);		
		
		try {
			nortService.findTableOfContents(DOMAIN_NAME, FILTER, nortFile, date);
		} 
		finally
		{
			FileUtils.deleteQuietly(nortFile);
		}

		EasyMock.verify(mockNovusFactory);
		EasyMock.verify(mockNovus);
		EasyMock.verify(mockNortManager);
 	}
	
}
