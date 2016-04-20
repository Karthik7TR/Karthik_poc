package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;

public abstract class BaseProviewGroupListController {
	
	protected List<ProviewGroup> filterProviewGroupList(ProviewGroupListFilterForm filterForm, List<ProviewGroup> allLatestProviewGroupList ){
		
		List<ProviewGroup> selectedProviewGroupList = new ArrayList<ProviewGroup>();
		
		boolean groupNameBothWayWildCard = false;
		boolean groupNameEndsWithWildCard = false;
		boolean groupNameStartsWithWildCard = false;
		boolean groupIdBothWayWildCard = false;
		boolean groupIdEndsWithWildCard = false;
		boolean groupIdStartsWithWildCard = false;
		String  groupNameSearchTerm = filterForm.getGroupName();
		String groupIdSearchTerm = filterForm.getProviewGroupID();
		
		if( (groupNameSearchTerm == null) && (groupIdSearchTerm == null) ) {
			return allLatestProviewGroupList;
		}
		
		if ( groupNameSearchTerm != null && groupNameSearchTerm.length() > 0 ) {
			if (groupNameSearchTerm.endsWith("%")
					&& groupNameSearchTerm.startsWith("%")) {
				groupNameBothWayWildCard = true;
			
			} else if (groupNameSearchTerm.endsWith("%")) {
				groupNameStartsWithWildCard  = true;
			
			} else if (groupNameSearchTerm.startsWith("%")) {
				groupNameEndsWithWildCard = true;
			}
			
			groupNameSearchTerm = groupNameSearchTerm.replaceAll("%", "");
		}
		
		if (groupIdSearchTerm != null && groupIdSearchTerm.length() > 0) {
			if (groupIdSearchTerm.endsWith("%")
					&& groupIdSearchTerm.startsWith("%")) {
				groupIdBothWayWildCard = true;
			
			} else if (groupIdSearchTerm.endsWith("%")) {
				groupIdStartsWithWildCard = true;
			
			} else if (groupIdSearchTerm.startsWith("%")) {
				groupIdEndsWithWildCard = true;
			}
			
			groupIdSearchTerm = groupIdSearchTerm.replaceAll("%", "");
		}
		
		for (ProviewGroup proviewGroup : allLatestProviewGroupList) {
			
			boolean selected = true;
			
			if (groupNameSearchTerm != null && groupNameSearchTerm.length() > 0) {
				if (proviewGroup.getGroupName() == null) {
					selected = false;
				} else {
					if (groupNameBothWayWildCard) {
						if (!proviewGroup.getGroupName().contains(
								groupNameSearchTerm)) {
							selected = false;
						}
					} else if (groupNameEndsWithWildCard) {
						if (!proviewGroup.getGroupName().endsWith(
								groupNameSearchTerm)) {
							selected = false;
						}
					} else if (groupNameStartsWithWildCard) {
						if (!proviewGroup.getGroupName().startsWith(
								groupNameSearchTerm)) {
							selected = false;
						}
					} else if (!proviewGroup.getGroupName().equals(
							groupNameSearchTerm)) {
						selected = false;
					}
				}
			}
			if (selected) {
				if (groupIdSearchTerm != null && groupIdSearchTerm.length() > 0) {
					if (proviewGroup.getGroupId() == null) {
						selected = false;
					} else {
						
						if (groupIdBothWayWildCard) {
							if (!proviewGroup.getGroupId().contains(groupIdSearchTerm)) {
								selected = false;
							}
						} else if (groupIdEndsWithWildCard) {
							if (!proviewGroup.getGroupId().endsWith(
									groupIdSearchTerm)) {
								selected = false;
							}
						} else if (groupIdStartsWithWildCard) {
							if (!proviewGroup.getGroupId().startsWith(
									groupIdSearchTerm)) {
								selected = false;
							}
						} else if (!proviewGroup.getGroupId().equals(
								groupIdSearchTerm)) {
							selected = false;
						}
					}
				}
			}
			if (selected) {
				selectedProviewGroupList.add(proviewGroup);
			}
		}
		return selectedProviewGroupList;
	}
}
