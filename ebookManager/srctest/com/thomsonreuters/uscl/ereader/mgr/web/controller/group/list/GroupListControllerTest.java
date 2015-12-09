package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupInfo;

public class GroupListControllerTest {
	
	private GroupListController groupListController;
	
	@Before
	public void setUp() throws Exception {
		groupListController = new GroupListController();
	}

	@Ignore
	public void testPreviewContentSelection() {
		String proviewResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group id=\"uscl/book_lohisplitnodeinfo\" status=\"Review\"><name>SplitNodeInfo</name>"
				+ "<type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title>"
				+ "<title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		//groupListController.getMetadataFromString(proviewResponse);
		List<ProviewGroupInfo> proviewGroupInfoList = groupListController.getProviewGroupInfoList();
		System.out.println(proviewGroupInfoList.size());
		for (ProviewGroupInfo proviewGroupInfo : proviewGroupInfoList){
			System.out.println(proviewGroupInfo.toString());
		} 
		
	}
	
	@Ignore
	public void testPreviewContentSelection2() {
		String proviewResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group id=\"uscl/book_lohisplitnodeinfo\" status=\"Review\"><name>SplitNodeInfo</name>"
				+ "<type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title>"
				+ "<title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup><subgroup heading=\"2013\"><title>ll/v1</title>"
				+ "<title>ll2/v1</title></subgroup></members></group>";
		//groupListController.getMetadataFromString(proviewResponse);
		List<ProviewGroupInfo> proviewGroupInfoList = groupListController.getProviewGroupInfoList();
		System.out.println(proviewGroupInfoList.size());
		for (ProviewGroupInfo proviewGroupInfo : proviewGroupInfoList){
			System.out.println(proviewGroupInfo.toString());
		} 
		
	}
}
