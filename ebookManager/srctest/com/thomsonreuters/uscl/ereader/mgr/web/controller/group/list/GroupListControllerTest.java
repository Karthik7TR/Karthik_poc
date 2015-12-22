package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public class GroupListControllerTest {
	
	private GroupListController groupListController;
	List<SplitNodeInfo> splitNodes;
	String ebookDefinitionId = "1";
	Map<String,String> versionSubGroupMap;
	private HandlerAdapter handlerAdapter;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ProviewAuditService mockAuditService;
	private BookDefinitionService mockBookDefinitionService;
	private PublishingStatsService publishingStatsService;
	private static final Long BOOK_DEF_ID = new Long(1234);
	private BookDefinition mockBookDef;
	private ProviewClient proviewClient;
	
	@Before
	public void setUp() throws Exception {
		groupListController = new GroupListController();
		mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
		groupListController.setBookDefinitionService(mockBookDefinitionService);
		
		publishingStatsService = EasyMock.createMock(PublishingStatsService.class);
		groupListController.setPublishingStatsService(publishingStatsService);
				
		proviewClient = EasyMock.createMock(ProviewClient.class);
		groupListController.setProviewClient(proviewClient);
		
		handlerAdapter = new AnnotationMethodHandlerAdapter();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		mockBookDef = EasyMock.createMock(BookDefinition.class);
		
		
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
		
		String status = "Review";
	
		
		EasyMock.expect(mockAuditService.getBookStatus(EasyMock.startsWith("abcde"),EasyMock.startsWith("v"))).andReturn(status).times(5);
    	EasyMock.replay(mockAuditService);
    	HttpSession session = request.getSession();
		
		List<ProviewGroupInfo> proviewGroupInfoList = groupListController.buildProviewGroupInfoList(splitNodes, ebookDefinitionId, versionSubGroupMap, model,session);
		Assert.assertEquals(proviewGroupInfoList.size(),2);	
		Assert.assertEquals(3,proviewGroupInfoList.get(0).getSplitTitles().size());	
		Assert.assertEquals(3,proviewGroupInfoList.get(1).getSplitTitles().size());	
		/*for(ProviewGroupInfo proviewGroupInfo : proviewGroupInfoList ){
			System.out.println(proviewGroupInfo.getSplitTitles());
			System.out.println(proviewGroupInfo.toString());
		}*/
		
	}
	
	@Test
	public void tesBuildProviewList2() throws Exception{	
	
		Model model =  new ExtendedModelMap();
		
		String status = "Review";;
	
		
		EasyMock.expect(mockAuditService.getBookStatus(EasyMock.startsWith("abcde"),EasyMock.startsWith("v"))).andReturn(status).times(5);
    	EasyMock.replay(mockAuditService);
    	
    	splitNodes.remove(1);
    	HttpSession session = request.getSession();
		
		List<ProviewGroupInfo> proviewGroupInfoList = groupListController.buildProviewGroupInfoList(splitNodes, ebookDefinitionId, versionSubGroupMap, model,session);
		Assert.assertEquals(proviewGroupInfoList.size(),2);	
		Assert.assertEquals(proviewGroupInfoList.size(),2);	
		Assert.assertEquals(2,proviewGroupInfoList.get(0).getSplitTitles().size());	
		Assert.assertEquals(3,proviewGroupInfoList.get(1).getSplitTitles().size());	
		
		/*for(ProviewGroupInfo proviewGroupInfo : proviewGroupInfoList ){
			System.out.println(proviewGroupInfo.getSplitTitles());
			System.out.println(proviewGroupInfo.toString());
		}*/
		
	}
	
	@Test
	public void testViewAllVersions() throws Exception{
		request.setRequestURI("/" + WebConstants.MVC_GROUP_BOOK_ALL_VERSIONS);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter(WebConstants.KEY_ID, "1234");
		
		EasyMock.expect(mockBookDef.getFullyQualifiedTitleId()).andReturn("uscl/an/abcd");
		
		EasyMock.expect(mockBookDef.getEbookDefinitionId()).andReturn(BOOK_DEF_ID);
		
		EasyMock.expect(mockBookDef.getSplitNodesAsList()).andReturn(splitNodes);
		EasyMock.replay(mockBookDef);
		
		
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEF_ID)).andReturn(mockBookDef);
		EasyMock.replay(mockBookDefinitionService);
		
		EasyMock.expect(publishingStatsService.getMaxGroupVersionById(BOOK_DEF_ID)).andReturn(new Long(1));
		EasyMock.replay(publishingStatsService);
		
		
		String proviewResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group id=\"uscl/book_lohisplitnodeinfo\" status=\"Review\"><name>SplitNodeInfo</name>"
				+ "<type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title>"
				+ "<title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		
		EasyMock.expect(proviewClient.getProviewGroupInfo("uscl/abcd", "v1")).andReturn(proviewResponse);
		EasyMock.replay(proviewClient);
		
		try {
			ModelAndView mav = handlerAdapter.handle(request, response, groupListController);
			Assert.assertNotNull(mav);
			Assert.assertEquals(WebConstants.VIEW_GROUP_TITLE_ALL_VERSIONS, mav.getViewName());
			// Verify the model
			Map<String,Object> model = mav.getModel();
			Assert.assertEquals("SplitNodeInfo", model.get(WebConstants.KEY_GROUP_NAME));
			
			Assert.assertEquals("Exception occured. Please contact your administrator.", model.get(WebConstants.KEY_ERR_MESSAGE));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGroupOperationPromote() throws Exception{
		request.setRequestURI("/" + WebConstants.MVC_GROUP_OPERATION);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("groupCmd", GroupListFilterForm.GroupCmd.PROMOTE.toString());
		
		try {
			ModelAndView mav = handlerAdapter.handle(request, response, groupListController);
			Assert.assertNotNull(mav);
			Assert.assertEquals(WebConstants.VIEW_PROVIEW_GROUP_PROMOTE, mav.getViewName());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGroupOperationRemove() throws Exception{
		request.setRequestURI("/" + WebConstants.MVC_GROUP_OPERATION);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("groupCmd", GroupListFilterForm.GroupCmd.REMOVE.toString());
		
		try {
			ModelAndView mav = handlerAdapter.handle(request, response, groupListController);
			Assert.assertNotNull(mav);
			Assert.assertEquals(WebConstants.VIEW_PROVIEW_GROUP_REMOVE, mav.getViewName());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
