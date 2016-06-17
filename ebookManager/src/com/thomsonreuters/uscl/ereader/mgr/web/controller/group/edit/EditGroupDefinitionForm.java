/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.AutoPopulatingList;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;

public class EditGroupDefinitionForm {
	//private static final Logger log = Logger.getLogger(EditGroupDefinitionForm.class);
	public static final String FORM_NAME = "editGroupDefinitionForm";
	
	public enum Version {
		NONE, MAJOR, OVERWRITE
	};
	
	private Long bookDefinitionId;
	private String fullyQualifiedTitleId;
	private String groupId;
	private String groupType;
	private Version versionType;
	private Boolean hasSplitTitles;
	private Boolean includeSubgroup;
	private Boolean includePilotBook;
	private String groupName;
	private Subgroup notGrouped;
	private List<Subgroup> subgroups;
	private Subgroup pilotBooks;
	private String comment;
	
	public EditGroupDefinitionForm() {
		super();
		this.notGrouped = new Subgroup();
		this.pilotBooks = new Subgroup();
		this.subgroups = new AutoPopulatingList<Subgroup>(Subgroup.class);
		this.hasSplitTitles = false;
		this.includeSubgroup = false;
	}
	
	public GroupDefinition createGroupDefinition(Collection<ProviewTitleInfo> proviewTitleInfos) {
		GroupDefinition groupDefinition = new GroupDefinition();
		groupDefinition.setGroupId(groupId);
		groupDefinition.setName(groupName);
		groupDefinition.setType(groupType);
		
		if(includeSubgroup) {
			List<SubGroupInfo> subgroupInfos = new ArrayList<>();
			for(Subgroup subgroup: subgroups) {
				SubGroupInfo subgroupInfo = new SubGroupInfo();
				subgroupInfo.setHeading(subgroup.getHeading());
				
				for(Title titleInfo: subgroup.getTitles()) {
					String titleId = titleInfo.getTitleId() + "/v" + titleInfo.getVersion();
					// Set first book as head title
					if(StringUtils.isBlank(groupDefinition.getHeadTitle())) {
						groupDefinition.setHeadTitle(titleId);
					}
					subgroupInfo.addTitle(titleId);
				}
				subgroupInfos.add(subgroupInfo);
			}
			groupDefinition.setSubGroupInfoList(subgroupInfos);
		} else {
			// No subgroups. Only single titles
			groupDefinition.setHeadTitle(fullyQualifiedTitleId);
			
			// Get unique fully qualified title ids.
			Set<String> proViewTitles = new LinkedHashSet<>();
			for(ProviewTitleInfo info: proviewTitleInfos) {
				proViewTitles.add(info.getTitleId());
			}
			
			SubGroupInfo subgroupInfo = new SubGroupInfo();
			subgroupInfo.setTitles(new ArrayList<String>(proViewTitles));
			groupDefinition.addSubGroupInfo(subgroupInfo);
		}
		
		return groupDefinition;
	}
	
	public void initialize(BookDefinition book, Map<String, ProviewTitleInfo> proviewTitleMap,
			Map<String, ProviewTitleInfo> pilotBookMap, GroupDefinition group) {
		includeSubgroup = false;
		includePilotBook = false;
		if(book != null) {
			Boolean containsSplitTitle = false;
			for(ProviewTitleInfo titleInfo: proviewTitleMap.values()) {
				if(titleInfo.getTitleId().matches("^.+\\_pt\\d+$")) {
					containsSplitTitle = true;
					break;
				}
			}
			setHasSplitTitles(containsSplitTitle);
		}
		
		if(group != null) {
			setGroupName(group.getName());
			
			if(group.subgroupExists()) {
				includeSubgroup = true;
				
				// Create current ProView group in form
				List<SubGroupInfo> subgroupInfos = group.getSubGroupInfoList();
				for(SubGroupInfo subgroupInfo: subgroupInfos) {
					Subgroup subgroup = new Subgroup();
					subgroup.setHeading(subgroupInfo.getHeading());
					
					List<String> titleStrs = subgroupInfo.getTitles();
					for(String titleStr: titleStrs) {
						ProviewTitleInfo info =  proviewTitleMap.remove(titleStr);
						
						if(info != null) {
							Title title = new Title();
							title.setProviewName(info.getTitle());
							title.setVersion(info.getMajorVersion());
							title.setTitleId(info.getTitleId());
							subgroup.addTitle(title);
						}
					}
					subgroups.add(subgroup);
				}
			}
		}
		
		pilotBooks = new Subgroup();
		for (ProviewTitleInfo titleInfo: pilotBookMap.values()) {
			includePilotBook = true;
			Title title = new Title();
			title.setVersion(titleInfo.getMajorVersion());
			title.setTitleId(titleInfo.getTitleId());
			pilotBooks.addTitle(title);
		}
		
		// Add titles that are not subgrouped
		Subgroup subgroup = new Subgroup();
		for(ProviewTitleInfo titleInfo: proviewTitleMap.values()) {
			Title title = new Title();
			title.setProviewName(titleInfo.getTitle());
			title.setVersion(titleInfo.getMajorVersion());
			title.setTitleId(titleInfo.getTitleId());
			subgroup.addTitle(title);
		}
		notGrouped = subgroup;
	}

	public Long getBookDefinitionId() {
		return bookDefinitionId;
	}

	public void setBookDefinitionId(Long bookDefinitionId) {
		this.bookDefinitionId = bookDefinitionId;
	}

	public String getFullyQualifiedTitleId() {
		return fullyQualifiedTitleId;
	}

	public void setFullyQualifiedTitleId(String fullyQualifiedTitleId) {
		this.fullyQualifiedTitleId = fullyQualifiedTitleId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public Version getVersionType() {
		return versionType;
	}

	public void setVersionType(Version versionType) {
		this.versionType = versionType;
	}

	public Boolean getHasSplitTitles() {
		return hasSplitTitles;
	}

	public void setHasSplitTitles(Boolean hasSplitTitles) {
		this.hasSplitTitles = hasSplitTitles;
	}

	public Boolean getIncludeSubgroup() {
		return includeSubgroup;
	}

	public void setIncludeSubgroup(Boolean includeSubgroup) {
		this.includeSubgroup = includeSubgroup;
	}

	public Boolean getIncludePilotBook() {
		return includePilotBook;
	}

	public void setIncludePilotBook(Boolean includePilotBook) {
		this.includePilotBook = includePilotBook;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Subgroup getNotGrouped() {
		return notGrouped;
	}

	public void setNotGrouped(Subgroup notGrouped) {
		this.notGrouped = notGrouped;
	}
	
	public Subgroup getPilotBooks() {
		return pilotBooks;
	}
	
	public void setPilotBooks(Subgroup pilotBooks) {
		this.pilotBooks = pilotBooks;
	}

	public List<Subgroup> getSubgroups() {
		return subgroups;
	}

	public void setSubgroups(List<Subgroup> subgroups) {
		this.subgroups = subgroups;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
