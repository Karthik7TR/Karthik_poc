package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.EditGroupDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.Subgroup;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.EditGroupDefinitionForm.Version;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.Title;

public class EditGroupDefinitionFormTest {
	private static final Long BOOK_DEFINITION_ID = 1L;
	private static final String PROVIEW_DISPLAY_NAME = "Book Name";
	private static final String TITLE_ID = "uscl/an/abcd";
	private static final String GROUP_ID = "uscl/an_abcd";
	private static final String GROUP_NAME = "Group name";
	private static final String GROUP_TYPE = "standard";
	
	private EditGroupDefinitionForm form;
	
	@Before
	public void setup() {
		form = new EditGroupDefinitionForm();
		form.setBookDefinitionId(BOOK_DEFINITION_ID);
		form.setComment("comment");
		form.setFullyQualifiedTitleId(TITLE_ID);
		form.setGroupId(GROUP_ID);
		form.setGroupName(GROUP_NAME);
		form.setGroupType(GROUP_TYPE);
		form.setHasSplitTitles(false);
		form.setVersionType(Version.MAJOR);
	}
	
	@Test
	public void createGroupDefinitionNoSubgroupTest() {
		form.setIncludeSubgroup(false);
		
		Title title = new Title();
		title.setProviewName("Book Title");
		title.setTitleId(TITLE_ID);
		title.setVersion(1);
		
		Subgroup subgroup = new Subgroup();
		subgroup.setHeading(null);
		subgroup.addTitle(title);
		List<Subgroup> subgroups = new ArrayList<Subgroup>();
		subgroups.add(subgroup);
		form.setSubgroups(subgroups);
		
		Collection<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();
		ProviewTitleInfo info = new ProviewTitleInfo();
		info.setTitleId(TITLE_ID);
		proviewTitleInfos.add(info);
		
		GroupDefinition group = form.createGroupDefinition(proviewTitleInfos);
		Assert.assertEquals(GROUP_ID, group.getGroupId());
		Assert.assertNull(group.getFirstSubgroupHeading());
		Assert.assertEquals(TITLE_ID, group.getHeadTitle());
		Assert.assertEquals(GROUP_NAME, group.getName());
		Assert.assertEquals(GROUP_TYPE, group.getType());
		
		List<SubGroupInfo> subgroupInfos = group.getSubGroupInfoList();
		SubGroupInfo subgroupInfo = subgroupInfos.get(0);
		Assert.assertNull(subgroupInfo.getHeading());
		Assert.assertEquals(TITLE_ID, subgroupInfo.getTitles().get(0));
	}
	
	@Test
	public void createGroupDefinitionSubgroupTest() {
		String subgroupHeading = "Subgroup heading";
		form.setIncludeSubgroup(true);
		
		Title title = new Title();
		title.setProviewName("Book Title");
		title.setTitleId(TITLE_ID);
		title.setVersion(1);
		
		Subgroup subgroup = new Subgroup();
		subgroup.setHeading(subgroupHeading);
		subgroup.addTitle(title);
		List<Subgroup> subgroups = new ArrayList<Subgroup>();
		subgroups.add(subgroup);
		form.setSubgroups(subgroups);
		
		Collection<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();
		ProviewTitleInfo info = new ProviewTitleInfo();
		info.setTitleId(TITLE_ID);
		proviewTitleInfos.add(info);
		
		GroupDefinition group = form.createGroupDefinition(proviewTitleInfos);
		Assert.assertEquals(GROUP_ID, group.getGroupId());
		Assert.assertEquals(subgroupHeading, group.getFirstSubgroupHeading());
		Assert.assertEquals(TITLE_ID + "/v" + title.getVersion(), group.getHeadTitle());
		Assert.assertEquals(GROUP_NAME, group.getName());
		Assert.assertEquals(GROUP_TYPE, group.getType());
		
		List<SubGroupInfo> subgroupInfos = group.getSubGroupInfoList();
		SubGroupInfo subgroupInfo = subgroupInfos.get(0);
		Assert.assertEquals(subgroupHeading, subgroupInfo.getHeading());
		Assert.assertEquals(TITLE_ID + "/v" + title.getVersion(), subgroupInfo.getTitles().get(0));
	}
	
	@Test
	public void initializeWithSubgroupsTest() {
		
		BookDefinition book = createBookDef(TITLE_ID);
		Map<String, ProviewTitleInfo> proviewTitleMap = createProviewTitleMap(TITLE_ID);
		GroupDefinition group = new GroupDefinition();
		
		ProviewTitleInfo proviewTitleInfo = new ProviewTitleInfo();
		proviewTitleInfo.setTitleId(TITLE_ID+"_pt2");
		proviewTitleMap.put(TITLE_ID, proviewTitleInfo);

		
		List<SubGroupInfo> subgroupList = new ArrayList<SubGroupInfo>();
		group.setSubGroupInfoList(subgroupList);
		SubGroupInfo subgroup = new SubGroupInfo();
		subgroupList.add(subgroup);
		subgroup.setHeading("a");
		subgroup.addTitle(TITLE_ID);
		
		form.initialize(book, proviewTitleMap, proviewTitleMap, group);
		
		Assert.assertTrue(form.getIncludeSubgroup());
		Assert.assertEquals(1, form.getSubgroups().size());
		Assert.assertEquals(1, form.getNotGrouped().getTitles().size());

	}
	
	private Map<String, ProviewTitleInfo> createProviewTitleMap(String fullyQualifiedTitleId) {
		ProviewTitleInfo info = new ProviewTitleInfo();
		info.setLastupdate("date");
		info.setStatus("Review");
		info.setTitle(PROVIEW_DISPLAY_NAME);
		info.setTitleId(fullyQualifiedTitleId);
		info.setTotalNumberOfVersions(1);
		info.setVersion("v1.0");
		
		Map<String, ProviewTitleInfo> map = new LinkedHashMap<String, ProviewTitleInfo>();
		map.put(fullyQualifiedTitleId + "/v" + info.getMajorVersion(), info);
		return map;
	}
	
	private BookDefinition createBookDef(String fullyQualifiedTitleId) {
		BookDefinition book = new BookDefinition();
		book.setEbookDefinitionId(BOOK_DEFINITION_ID);
    	book.setFullyQualifiedTitleId(fullyQualifiedTitleId);
    	book.setIsDeletedFlag(false);
    	book.setEbookDefinitionCompleteFlag(true);
    	book.setAutoUpdateSupportFlag(true);
    	book.setSearchIndexFlag(true);
    	book.setPublishedOnceFlag(false);
    	book.setOnePassSsoLinkFlag(true);
    	book.setKeyciteToplineFlag(true);
    	book.setIsAuthorDisplayVertical(true);
    	book.setEnableCopyFeatureFlag(false);
    	book.setIsSplitBook(false);
    	book.setIsSplitTypeAuto(true);
    	return book;
	}

}
