package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.SAXParserFactory;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerAdapter;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupInfo;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;

public class GroupListControllerTest {
	
	private GroupListController groupListController;
	List<SplitNodeInfo> splitNodes;
	String ebookDefinitionId = "1";
	Map<String,String> versionSubGroupMap;
	//private HandlerAdapter handlerAdapter;
	private MockHttpServletRequest request;
	//private MockHttpServletResponse response;
	private ProviewAuditService mockAuditService;
	
	@Before
	public void setUp() throws Exception {
		groupListController = new GroupListController();
		request = new MockHttpServletRequest();
		//response = new MockHttpServletResponse();
		
		
		this.mockAuditService = EasyMock.createMock(ProviewAuditService.class);
		groupListController.setProviewAuditService(mockAuditService);
		
		splitNodes = new ArrayList<SplitNodeInfo>();
		SplitNodeInfo splitNodeInfo1 = new SplitNodeInfo();
		splitNodeInfo1.setBookVersionSubmitted("1");
		splitNodeInfo1.setSpitBookTitle("abcde_pt2");
		splitNodes.add(splitNodeInfo1);
		
		SplitNodeInfo splitNodeInfo2 = new SplitNodeInfo();
		splitNodeInfo2.setBookVersionSubmitted("1");
		splitNodeInfo2.setSpitBookTitle("abcde_pt3");
		splitNodes.add(splitNodeInfo2);
		
		SplitNodeInfo splitNodeInfo3 = new SplitNodeInfo();
		splitNodeInfo3.setBookVersionSubmitted("1.1");
		splitNodeInfo3.setSpitBookTitle("abcde_pt2");
		splitNodes.add(splitNodeInfo3);
		
		SplitNodeInfo splitNodeInfo4 = new SplitNodeInfo();
		splitNodeInfo4.setBookVersionSubmitted("1.1");
		splitNodeInfo4.setSpitBookTitle("abcde_pt3");
		splitNodes.add(splitNodeInfo4);
		
		versionSubGroupMap = new HashMap<String,String>();
		versionSubGroupMap.put("v1", "2014");
	}

	@Test
	public void testPreviewContentSelection() throws Exception{
		String proviewResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group id=\"uscl/book_lohisplitnodeinfo\" status=\"Review\"><name>SplitNodeInfo</name>"
				+ "<type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title>"
				+ "<title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		GroupXMLHandler groupXMLHandler = new GroupXMLHandler();
		reader.setContentHandler(groupXMLHandler);
		reader.parse(new InputSource(new StringReader(proviewResponse)));
		Map<String, String> versionSubGroupMap = groupXMLHandler.getSubGroupVersionMap();
		Assert.assertEquals(versionSubGroupMap.size(),1);	
		
	}
	
	@Test
	public void testVersionTitleMap() throws Exception{		
		Map<String, List<String>> versionTitlesMap = groupListController.getVersionTitleMapFromSplitNodeList(splitNodes);
		Assert.assertEquals(versionTitlesMap.size(),2);	
		/* Iterator it = versionTitlesMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        System.out.println(pair.getKey() + " = " + pair.getValue());
		    }*/
	}
	
	@Test
	public void tesBuildProviewList1() throws Exception{	
		Model model =  new ExtendedModelMap();
		
		List<String> status = new ArrayList<String>();
		status.add("Review");
	
		
		EasyMock.expect(mockAuditService.getBookStatus(EasyMock.startsWith("abcde"),EasyMock.startsWith("v"))).andReturn(status).times(5);
    	EasyMock.replay(mockAuditService);
    	HttpSession session = request.getSession();
		
		List<ProviewGroupInfo> proviewGroupInfoList = groupListController.buildProviewGroupInfoList(splitNodes, ebookDefinitionId, versionSubGroupMap, model,session);
		Assert.assertEquals(proviewGroupInfoList.size(),2);	
		/*for(ProviewGroupInfo proviewGroupInfo : proviewGroupInfoList ){
			System.out.println(proviewGroupInfo.getSplitTitles());
			System.out.println(proviewGroupInfo.toString());
		}*/
		
	}
	
	@Test
	public void tesBuildProviewList2() throws Exception{	
	
		Model model =  new ExtendedModelMap();
		
		List<String> status = new ArrayList<String>();
		status.add("Review");
	
		
		EasyMock.expect(mockAuditService.getBookStatus(EasyMock.startsWith("abcde"),EasyMock.startsWith("v"))).andReturn(status).times(5);
    	EasyMock.replay(mockAuditService);
    	
    	splitNodes.remove(1);
    	HttpSession session = request.getSession();
		
		List<ProviewGroupInfo> proviewGroupInfoList = groupListController.buildProviewGroupInfoList(splitNodes, ebookDefinitionId, versionSubGroupMap, model,session);
		Assert.assertEquals(proviewGroupInfoList.size(),2);	
		
		/*for(ProviewGroupInfo proviewGroupInfo : proviewGroupInfoList ){
			System.out.println(proviewGroupInfo.getSplitTitles());
			System.out.println(proviewGroupInfo.toString());
		}*/
		
	}
}
