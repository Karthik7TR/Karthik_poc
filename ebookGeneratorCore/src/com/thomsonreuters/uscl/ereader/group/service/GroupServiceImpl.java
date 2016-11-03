/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.group.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;

public class GroupServiceImpl implements GroupService {

	private static final Logger LOG = LogManager.getLogger(GroupServiceImpl.class);
	private GroupDefinitionParser proviewGroupParser = new GroupDefinitionParser();
	private ProviewClient proviewClient;
	private List<String> pilotBooksNotFound;

	/**
	 * Group ID is unique to each major version
	 * 
	 * @param bookDefinition
	 * @param versionNumber
	 * @return
	 */
	public String getGroupId(BookDefinition bookDefinition) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(bookDefinition.getPublisherCodes().getName());
		buffer.append("/");
		String contentType = null;
		if (bookDefinition.getDocumentTypeCodes() != null) {
			contentType = bookDefinition.getDocumentTypeCodes().getAbbreviation();
		}
		if (!StringUtils.isBlank(contentType)) {
			buffer.append(contentType + "_");
		}
		buffer.append(StringUtils.substringAfterLast(bookDefinition.getFullyQualifiedTitleId(), "/"));
		return buffer.toString();
	}

	public List<GroupDefinition> getGroups(BookDefinition bookDefinition) throws Exception {
		String groupId = getGroupId(bookDefinition);
		return getGroups(groupId);
	}

	public List<GroupDefinition> getGroups(String groupId) throws Exception {
		try {
			String response = proviewClient.getProviewGroupById(groupId);
			GroupDefinitionParser parser = new GroupDefinitionParser();
			List<GroupDefinition> groups = parser.parse(response);
			// sort by group versions
			Collections.sort(groups);
			return groups;
		} catch (ProviewRuntimeException ex) {
			String errorMsg = ex.getMessage();
			LOG.debug(errorMsg);
			if (ex.getStatusCode().equals("404") && errorMsg.contains("No such groups exist")) {
				LOG.debug("Group does not exist. Exception can be ignored");
			} else {
				throw new Exception(ex);
			}
		}
		return null;
	}

	public GroupDefinition getGroupInfoByVersion(String groupId, Long groupVersion) throws ProviewException {
		try {
			String response = proviewClient.getProviewGroupInfo(groupId, GroupDefinition.VERSION_NUMBER_PREFIX + groupVersion.toString());
			List<GroupDefinition> groups = proviewGroupParser.parse(response);
			if (groups.size() == 1) {
				return groups.get(0);
			}
		} catch (ProviewRuntimeException ex) {
			if (ex.getStatusCode().equals("400") && ex.toString().contains("No such group id and version exist")) {
				// ignore and return null
			} else {
				throw new ProviewException(ex.getMessage());
			}
		} catch (Exception ex) {
			throw new ProviewException(ex.getMessage());
		}
		return null;
	}

	public GroupDefinition getGroupInfoByVersionAutoDecrement(String groupId, Long groupVersion) throws ProviewException {
		GroupDefinition group = null;
		do {
			group = getGroupInfoByVersion(groupId, groupVersion);
			if (group == null) {
				groupVersion = groupVersion - 1;
			} else {
				break;
			}
		} while (groupVersion > 0);
		return group;
	}

	public GroupDefinition getLastGroup(BookDefinition book) throws Exception {
		String groupId = getGroupId(book);
		return getLastGroup(groupId);
	}

	public GroupDefinition getLastGroup(String groupId) throws Exception {
		List<GroupDefinition> groups = getGroups(groupId);
		if (groups != null && groups.size() > 0) {
			return groups.get(0);
		}
		return null;
	}

	/**
	 * Send Group definition to Proview to create a group
	 */
	public void createGroup(GroupDefinition groupDefinition) throws ProviewException {
		try {
			proviewClient.createGroup(groupDefinition);
		} catch (ProviewRuntimeException ex) {
			String errorMsg = ex.getMessage();
			if (ex.getStatusCode().equalsIgnoreCase("400")) {
				if (errorMsg.contains("This Title does not exist")) {
					throw new ProviewException(CoreConstants.NO_TITLE_IN_PROVIEW);
				} else if (errorMsg.contains("GroupId already exists with same version") || errorMsg.contains(
						"Version Should be greater")) {
					throw new ProviewException(CoreConstants.GROUP_AND_VERSION_EXISTS);
				} else {
					throw new ProviewException(errorMsg);
				}
			} else {
				throw new ProviewException(errorMsg);
			}
		} catch (UnsupportedEncodingException e) {
			throw new ProviewException(e.getMessage());
		}
	}

	public boolean isTitleWithVersion(String fullyQualifiedTitle) {
		// Sample title with version uscl/an/abcd/v1 as opposed to uscl/an/abcd
		if (StringUtils.isNotBlank(fullyQualifiedTitle)) {
			Pattern trimmer = Pattern.compile("/v\\d+(\\.\\d+)?$");
			Matcher m = trimmer.matcher(fullyQualifiedTitle);
			if (m.find()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Group will be created based on user input. splitTitles will be null if book is not a splitbook
	 */
	public GroupDefinition createGroupDefinition(BookDefinition bookDefinition, String bookVersion, List<String> splitTitles)
			throws Exception {
		String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
		String groupName = bookDefinition.getGroupName();
		String subGroupHeading = bookDefinition.getSubGroupHeading();
		String majorVersionStr = StringUtils.substringBefore(bookVersion, ".");
		String titleIdMajorVersion = fullyQualifiedTitleId + "/" + majorVersionStr;
		GroupDefinition lastGroupDef = getLastGroup(bookDefinition);

		boolean newHasSubgroups = StringUtils.isNotBlank(subGroupHeading);
		boolean oldHasSubgroups = false;
		boolean majorVersionChange = false;
		boolean firstGroup = (lastGroupDef == null);

		List<SubGroupInfo> allSubGroupInfo = firstGroup ? new ArrayList<SubGroupInfo>() : lastGroupDef.getSubGroupInfoList();
		if (allSubGroupInfo.size() > 0 && allSubGroupInfo.get(0) != null) {
			oldHasSubgroups = (lastGroupDef.getSubGroupInfoList().get(0).getHeading() != null);
		}

		// check if major version changed
		String previousHeadTitle = lastGroupDef != null ? lastGroupDef.getHeadTitle() : null;
		if (isTitleWithVersion(previousHeadTitle) && !majorVersionStr.equals(StringUtils.substringAfterLast(previousHeadTitle, "/"))) {
			// head titles without version do not have subgroups, making a major version change irrelevant
			majorVersionChange = true;
		}

		// so splitTitles can be used generally
		if (splitTitles == null) {
			splitTitles = new ArrayList<String>();
			splitTitles.add(fullyQualifiedTitleId);
		}

		// Get list of titles in ProView so invalid versions are not added to the group from previous
		List<ProviewTitleInfo> proviewTitleInfoList = getMajorVersionProviewTitles(fullyQualifiedTitleId);
		Set<Integer> majorVersionList = new HashSet<Integer>();
		for (ProviewTitleInfo ProviewTitleInfo : proviewTitleInfoList) {
			majorVersionList.add(ProviewTitleInfo.getMajorVersion());
		}

		List<String> pilotBooks = getPilotBooks(bookDefinition);

		// check errors in book definition compared to previous group
		validate(bookDefinition, lastGroupDef, majorVersionStr);

		GroupDefinition groupDefinition = createNewGroupDefinition(groupName, getGroupId(bookDefinition), fullyQualifiedTitleId,
				majorVersionStr, newHasSubgroups);

		if (firstGroup) {
			// set group version
			groupDefinition.setGroupVersion(1L);

			// add titles
			allSubGroupInfo.add(new SubGroupInfo());
			allSubGroupInfo.get(0).setHeading(subGroupHeading);
			if (subGroupHeading == null) {
				majorVersionStr = null;
			}
			addTitlesToSubGroup(allSubGroupInfo.get(0), splitTitles, majorVersionStr);
			if (bookDefinition.getPilotBooks().size() > 0) {
				addTitlesToSubGroup(allSubGroupInfo.get(0), pilotBooks, null);
			}

		} else {
			// set group version
			if (lastGroupDef.getStatus().equalsIgnoreCase(GroupDefinition.REVIEW_STATUS)) {
				// don't update group version, overwrite
				groupDefinition.setGroupVersion(lastGroupDef.getGroupVersion());
			} else {
				groupDefinition.setGroupVersion(lastGroupDef.getGroupVersion() + 1);
			}

			// update subgroups
			cleanAllSubgroups(fullyQualifiedTitleId, allSubGroupInfo, pilotBooks, majorVersionList);
			if (oldHasSubgroups && newHasSubgroups) {
				// add new title to the appropriate subgroup
				SubGroupInfo selectedSubgroup = null;
				SubGroupInfo previousSubgroup = null;
				for (SubGroupInfo subgroup : allSubGroupInfo) {
					if (subgroup.getHeading().equals(subGroupHeading)) {
						selectedSubgroup = subgroup;
					}
					if (subgroup.getTitles().contains(titleIdMajorVersion)) {
						previousSubgroup = subgroup;
						break;
					}
				}

				if (selectedSubgroup == null) {
					// make new subgroup
					selectedSubgroup = new SubGroupInfo();
					selectedSubgroup.setHeading(subGroupHeading);
					allSubGroupInfo.add(0, selectedSubgroup);
				} else if (bookDefinition.isSplitBook() && majorVersionChange) {
					// new major split titles must be in their own new subgroup
					throw new ProviewException(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE);
				}

				if (majorVersionChange) {
					List<String> titles = selectedSubgroup.getTitles();
					selectedSubgroup.setTitles(new ArrayList<String>());
					addTitlesToSubGroup(selectedSubgroup, splitTitles, majorVersionStr);
					selectedSubgroup.getTitles().addAll(titles);
				} else {
					// check which subgroup the title used to be in and remove it if it does not match the specified one
					if (previousSubgroup != null) {
						cleanPreviousSubGroup(previousSubgroup, fullyQualifiedTitleId, majorVersionStr);
						if (previousSubgroup != selectedSubgroup && previousSubgroup.getTitles().isEmpty()) {
							// Minor updates are bundled into the same subgroup, remove instances in other subgroups
							allSubGroupInfo.remove(previousSubgroup);
						}
					}
					// add the newly generated book
					addTitlesToSubGroup(selectedSubgroup, splitTitles, majorVersionStr);
				}

			} else if (newHasSubgroups) {
				// must be an overwrite of group version 1 (others throw exception in validate() )
				allSubGroupInfo = new ArrayList<SubGroupInfo>();
				allSubGroupInfo.add(new SubGroupInfo());
				allSubGroupInfo.get(0).setHeading(subGroupHeading);
				addTitlesToSubGroup(allSubGroupInfo.get(0), splitTitles, majorVersionStr);

			} else {
				// add new title to the first (only) subgroup (no header)
				allSubGroupInfo = new ArrayList<SubGroupInfo>();
				allSubGroupInfo.add(new SubGroupInfo());
				addTitlesToSubGroup(allSubGroupInfo.get(0), splitTitles, null);
				if (bookDefinition.getPilotBooks().size() > 0) {
					addTitlesToSubGroup(allSubGroupInfo.get(0), pilotBooks, null);
				}
			}
		}
		groupDefinition.setSubGroupInfoList(allSubGroupInfo);

		return groupDefinition;
	}

	/**
	 * Removes all instances of a titleId+majorVersion in a subgroup
	 * 
	 * @param previousSubgroup ---- subgroup from which the given version of the given title should be removed
	 * @param fullyQualifiedTitleId ---- titleID to be removed
	 * @param majorVersionStr ---- major version to be removed, in the format "v#"
	 */
	private void cleanPreviousSubGroup(SubGroupInfo previousSubgroup, String fullyQualifiedTitleId, String majorVersionStr) {
		List<String> iterableTitles = new ArrayList<String>();
		iterableTitles.addAll(previousSubgroup.getTitles());
		for (String title : iterableTitles) {
			String titleId = StringUtils.substringBeforeLast(title, "/");
			if (StringUtils.substringAfterLast(title, "/").equals(majorVersionStr) && StringUtils.substringBeforeLast(titleId, "_pt")
					.equals(fullyQualifiedTitleId)) {
				previousSubgroup.getTitles().remove(title);
			}
		}
	}

	/**
	 * Removes all titles in a SubGroupInfo list that do not exist in ProView, then removes any empty SubGroupInfos
	 * 
	 * @param titleId ---- titleID of the book the group is associated with
	 * @param subGroupInfoList ---- list of subgroups for the group
	 * @param pilotBooks ---- list of titleIDs of pilot books associated with the book
	 * @param majorVersions ---- list of major versions of the book that exist in ProView
	 * @throws ProviewException
	 */
	private void cleanAllSubgroups(String titleId, List<SubGroupInfo> subGroupInfoList, List<String> pilotBooks, Set<Integer> majorVersions)
			throws ProviewException {
		List<String> removedTitles;
		List<SubGroupInfo> emptySubGroups = new ArrayList<SubGroupInfo>();
		for (SubGroupInfo subgroup : subGroupInfoList) {
			removedTitles = new ArrayList<String>();
			for (String title : subgroup.getTitles()) {
				if (!isTitleInProview(title, majorVersions, titleId)) {
					removedTitles.add(title);
				}
			}
			subgroup.getTitles().removeAll(removedTitles);
			if (subgroup.getTitles().isEmpty()) {
				emptySubGroups.add(subgroup);
			}
		}
	}

	/**
	 * Add all splits of a titleId+majorVersion to the front of the subgroup's title list
	 * 
	 * @param subgroup ---- subgroup for titles to be added to
	 * @param titles ---- list of titleIDs to be added, generally split titles
	 * @param majorVersionStr ---- major version to be appended to the titles, if "" no version suffix will be added
	 * @throws ProviewException
	 */
	private void addTitlesToSubGroup(SubGroupInfo subgroup, List<String> titles, String majorVersionStr) throws ProviewException {
		List<String> oldTitles = subgroup.getTitles();
		subgroup.setTitles(new ArrayList<String>());
		if (StringUtils.isNotBlank(majorVersionStr)) {
			majorVersionStr = "/" + majorVersionStr;
		} else {
			majorVersionStr = "";
		}
		for (String title : titles) {
			if (oldTitles.contains(title + majorVersionStr)) {
				oldTitles.remove(title + majorVersionStr);
			}
			subgroup.addTitle(title + majorVersionStr);
		}
		subgroup.getTitles().addAll(oldTitles);
	}

	private void validate(BookDefinition book, GroupDefinition previousGroup, String majorVersionStr) throws ProviewException {
		String currentSubgroupName = book.getSubGroupHeading();
		Integer majorVersion = null;
		if (StringUtils.contains(majorVersionStr, 'v')) {
			majorVersion = Integer.valueOf(StringUtils.substringAfter(majorVersionStr, "v"));
		} else {
			// will cause null pointer exception in the imminent comparison (majorVersion > 1)
			throw new ProviewException("Version information for " + book.getTitleId() + " incorrectly processed");
		}

		if (StringUtils.isBlank(book.getGroupName())) {
			throw new ProviewException(CoreConstants.EMPTY_GROUP_ERROR_MESSAGE);
		}

		if (book.isSplitBook() && StringUtils.isBlank(currentSubgroupName)) {
			throw new ProviewException("Subgroup name cannot be empty for split books");
		}

		if (majorVersion > 1 && StringUtils.isNotBlank(currentSubgroupName) && (previousGroup == null || (previousGroup != null
				&& StringUtils.isBlank(previousGroup.getFirstSubgroupHeading())))) {
			throw new ProviewException(CoreConstants.SUBGROUP_ERROR_MESSAGE);
		}
	}

	private GroupDefinition createNewGroupDefinition(String groupName, String groupId, String fullyQualifiedTitleId, String majorVersion,
			boolean hasSubgroups) throws Exception {
		GroupDefinition groupDefinition = new GroupDefinition();
		groupDefinition.setName(groupName);
		groupDefinition.setGroupId(groupId);
		groupDefinition.setType("standard");
		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();
		groupDefinition.setSubGroupInfoList(subGroupInfoList);

		if (hasSubgroups) {
			groupDefinition.setHeadTitle(fullyQualifiedTitleId + "/" + majorVersion);
		} else {
			groupDefinition.setHeadTitle(fullyQualifiedTitleId);
		}
		return groupDefinition;
	}

	private List<String> getPilotBooks(BookDefinition book) {
		List<String> pilotBooks = new ArrayList<String>();
		for (PilotBook pilotBook : book.getPilotBooks()) {
			pilotBooks.add(pilotBook.getPilotBookTitleId());
		}
		return pilotBooks;
	}

	/**
	 * Check if title exists in ProView and add to the list These titles might have been deleted and doesn't exist in ProView but
	 * exists in group
	 * 
	 * @param title
	 * @return
	 */
	private boolean isTitleInProview(String title, Set<Integer> majorVersionList, String fullyQualifiedTitleId) throws ProviewException {
		if (StringUtils.startsWithIgnoreCase(title, fullyQualifiedTitleId)) {
			if (isTitleWithVersion(title)) {
				String version = StringUtils.substringAfterLast(title, "/v");
				Integer majorVersion = Integer.valueOf(StringUtils.substringBefore(version, "."));
				if (majorVersionList.contains(majorVersion)) {
					return true;
				}
			}

		}
		// It must be a pilot book
		else {
			title = isTitleWithVersion(title) ? StringUtils.substringBeforeLast(title, "/v") : title;
			try {
				proviewClient.getSinglePublishedTitle(title);
				return true;
			} catch (Exception ex) {
				String errorMessage = ex.getMessage();
				if (errorMessage.contains("does not exist")) {
					return false;

				} else {
					throw ex;
				}
			}
		}
		return false;
	}

	public void removeAllPreviousGroups(BookDefinition bookDefinition) throws Exception {
		List<GroupDefinition> GroupDefinition = getGroups(bookDefinition);

		if (GroupDefinition != null) {
			for (GroupDefinition group : GroupDefinition) {
				proviewClient.removeGroup(group.getGroupId(), group.getProviewGroupVersionString());
				TimeUnit.SECONDS.sleep(2);
				proviewClient.deleteGroup(group.getGroupId(), group.getProviewGroupVersionString());
			}
		}

	}

	/**
	 * Get list of ProView titles that belong in the group for given book definition.
	 * 
	 * Deprecated in favor of ProviewClient.getTitleContainer()
	 */
	@Deprecated // Deprecated in favor of ProviewClient.getTitleContainer()
	public Map<String, ProviewTitleInfo> getProViewTitlesForGroup(BookDefinition bookDef) throws Exception {
		Set<SplitNodeInfo> splitNodeInfos = bookDef.getSplitNodes();

		// Get fully qualified title IDs of all split titles
		Set<String> splitTitles = new HashSet<String>();
		if (splitNodeInfos != null && splitNodeInfos.size() > 0) {
			for (SplitNodeInfo splitNodeInfo : splitNodeInfos) {
				splitTitles.add(splitNodeInfo.getSplitBookTitle());
			}
		}
		// Add current fully qualified title Id
		splitTitles.add(bookDef.getFullyQualifiedTitleId());
		List<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();
		for (String title : splitTitles) {
			List<ProviewTitleInfo> proviewTitleInfo = getMajorVersionProviewTitles(title);
			proviewTitleInfos.addAll(proviewTitleInfo);
		}
		// sort split/single books before adding pilot books
		Collections.sort(proviewTitleInfos);

		Map<String, ProviewTitleInfo> proviewTitleMap = new LinkedHashMap<>();
		for (ProviewTitleInfo info : proviewTitleInfos) {
			String key = info.getTitleId() + "/v" + info.getMajorVersion();
			proviewTitleMap.put(key, info);
		}
		return proviewTitleMap;
	}

	@Override
	public List<String> getPilotBooksNotFound() {
		return pilotBooksNotFound;
	}

	public void setPilotBooksNotFound(List<String> pilotBooksNotFound) {
		this.pilotBooksNotFound = pilotBooksNotFound;
	}

	@Override
	public Map<String, ProviewTitleInfo> getPilotBooksForGroup(BookDefinition book) throws Exception {
		Map<String, ProviewTitleInfo> pilotBooks = new LinkedHashMap<String, ProviewTitleInfo>();
		pilotBooksNotFound = new ArrayList<String>();
		List<PilotBook> bookList = book.getPilotBooks();
		List<String> notFoundList = new ArrayList<String>();
		for (PilotBook pilotBook : bookList) {
			try {
				ProviewTitleInfo latest = new ProviewTitleInfo();
				latest.setVersion("v0");
				Integer totalNumberOfVersions = 0;
				ProviewTitleContainer singleBook = proviewClient.getProviewTitleContainer(pilotBook.getPilotBookTitleId());
				if (singleBook == null) {
					notFoundList.add(pilotBook.getPilotBookTitleId());
				} else {
					for (ProviewTitleInfo titleInfo : singleBook.getAllMajorVersions()) {
						totalNumberOfVersions++;
						titleInfo.setTotalNumberOfVersions(totalNumberOfVersions);
						if (latest.getMajorVersion() < titleInfo.getMajorVersion()) {
							latest = titleInfo;
						}
					}
					pilotBooks.put(latest.getTitleId() + "/v" + latest.getMajorVersion(), latest);
				}

			} catch (Exception e) {
				notFoundList.add(pilotBook.getPilotBookTitleId());
			}
		}
		if (notFoundList.size() > 0) {
			for (String pilotBookNotFound : notFoundList) {
				pilotBooksNotFound.add(pilotBookNotFound);
			}
			String msg = notFoundList.toString();
			msg = msg.replaceAll("\\[|\\]|\\{|\\}", "");
		}

		return pilotBooks;
	}

	public List<ProviewTitleInfo> getMajorVersionProviewTitles(String titleId) throws ProviewException {
		try {
			ProviewTitleContainer container = proviewClient.getProviewTitleContainer(titleId);
			if (container != null) {
				return container.getAllMajorVersions();
			}

		} catch (ProviewException ex) {
			String errorMessage = ex.getMessage();

			if (!errorMessage.contains("does not exist")) {
				throw ex;
			}
		}
		return new ArrayList<ProviewTitleInfo>();
	}

	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}

}
