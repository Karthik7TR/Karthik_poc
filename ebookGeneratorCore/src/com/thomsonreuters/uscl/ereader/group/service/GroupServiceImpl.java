package com.thomsonreuters.uscl.ereader.group.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.GROUP_TYPE_EREFERENCE;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.GROUP_TYPE_STANDARD;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.GROUP_TYPE_PERIODICAL;

@Slf4j
public class GroupServiceImpl implements GroupService {
    private BookDefinitionService bookDefinitionService;
    private ProviewHandler proviewHandler;
    private List<String> pilotBooksNotFound;

    /**
     * Group ID is unique to each major version
     *
     * @param bookDefinition
     * @return
     */
    @Override
    public String getGroupId(final BookDefinition bookDefinition) {
        final StringBuffer buffer = new StringBuffer();
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

    @Override
    public List<GroupDefinition> getGroups(final BookDefinition bookDefinition) throws Exception {
        final String groupId = getGroupId(bookDefinition);
        return getGroups(groupId);
    }

    @Override
    public List<GroupDefinition> getGroups(final String groupId) throws Exception {
        try {
            final List<GroupDefinition> groups = proviewHandler.getGroupDefinitionsById(groupId);
            // sort by group versions
            Collections.sort(groups);
            return groups;
        } catch (final ProviewRuntimeException ex) {
            final String errorMsg = ex.getMessage();
            log.debug(errorMsg);
            if (ex.getStatusCode().equals("404") && errorMsg.contains("No such groups exist")) {
                log.debug("Group does not exist. Exception can be ignored");
            } else {
                throw new Exception(ex);
            }
        }
        return null;
    }

    @Override
    public GroupDefinition getGroupInfoByVersion(final String groupId, final Long groupVersion)
        throws ProviewException {
        try {
            return proviewHandler.getGroupDefinitionByVersion(groupId, groupVersion);
        } catch (final ProviewRuntimeException ex) {
            if (ex.getStatusCode().equals("400") && ex.toString().contains("No such group id and version exist")) {
                return null;
            } else {
                throw new ProviewException(ex.getMessage());
            }
        }
    }

    @Override
    public GroupDefinition getGroupInfoByVersionAutoDecrement(final String groupId, Long groupVersion)
        throws ProviewException {
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

    @Override
    public GroupDefinition getLastGroup(final BookDefinition book) throws Exception {
        final String groupId = getGroupId(book);
        return getLastGroup(groupId);
    }

    @Override
    public GroupDefinition getLastGroup(final String groupId) throws Exception {
        final List<GroupDefinition> groups = getGroups(groupId);
        if (groups != null && groups.size() > 0) {
            return groups.get(0);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public GroupDefinition getGroupOfTitle(final String title) {
        BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(title);
        return getLastGroup(book);
    }

    /**
     * Send Group definition to Proview to create a group
     */
    @Override
    public void createGroup(final GroupDefinition groupDefinition) throws ProviewException {
        try {
            proviewHandler.createGroup(groupDefinition);
        } catch (final ProviewRuntimeException ex) {
            final String errorMsg = ex.getMessage();
            final HttpStatus status = HttpStatus.valueOf(Integer.parseInt(ex.getStatusCode()));
            if (status.is4xxClientError() || status.is5xxServerError()) {
                if (errorMsg.contains("This Title does not exist")) {
                    throw new ProviewException(CoreConstants.NO_TITLE_IN_PROVIEW, ex);
                } else if (errorMsg.contains("GroupId already exists with same version")
                    || errorMsg.contains("Version Should be greater")) {
                    throw new ProviewException(CoreConstants.GROUP_AND_VERSION_EXISTS);
                } else {
                    throw new ProviewException(errorMsg);
                }
            } else {
                throw new ProviewException(errorMsg);
            }
        } catch (final UnsupportedEncodingException e) {
            throw new ProviewException(e.getMessage());
        }
    }

    @Override
    public boolean isTitleWithVersion(final String fullyQualifiedTitle) {
        // Sample title with version uscl/an/abcd/v1 as opposed to uscl/an/abcd
        if (StringUtils.isNotBlank(fullyQualifiedTitle)) {
            final Pattern trimmer = Pattern.compile("/v\\d+(\\.\\d+)?$");
            final Matcher m = trimmer.matcher(fullyQualifiedTitle);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Group will be created based on user input. splitTitles will be null if book is not a splitbook
     */
    @Override
    public GroupDefinition createGroupDefinition(
        final BookDefinition bookDefinition,
        final String bookVersion,
        List<String> splitTitles) throws Exception {
        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final String groupName = bookDefinition.getGroupName();
        final String subGroupHeading = bookDefinition.getSubGroupHeading();
        String majorVersionStr = StringUtils.substringBefore(bookVersion, ".");
        final String titleIdMajorVersion = fullyQualifiedTitleId + "/" + majorVersionStr;
        final GroupDefinition lastGroupDef = getLastGroup(bookDefinition);

        final boolean newHasSubgroups = StringUtils.isNotBlank(subGroupHeading);
        boolean oldHasSubgroups = false;
        boolean majorVersionChange = false;
        final boolean firstGroup = (lastGroupDef == null);

        List<SubGroupInfo> allSubGroupInfo = new ArrayList<>();

        if (!firstGroup && lastGroupDef != null) {
            allSubGroupInfo = lastGroupDef.getSubGroupInfoList();
        }

        if (allSubGroupInfo.size() > 0 && allSubGroupInfo.get(0) != null && lastGroupDef != null) {
            oldHasSubgroups = (lastGroupDef.getSubGroupInfoList().get(0).getHeading() != null);
        }

        // Get list of titles in ProView so invalid versions are not added to the group from previous
        final List<ProviewTitleInfo> proviewTitleInfoList = getMajorVersionProviewTitles(fullyQualifiedTitleId);
        final Set<BigInteger> majorVersionSet = proviewTitleInfoList.stream()
                .map(ProviewTitleInfo::getMajorVersion)
                .collect(Collectors.toSet());
        majorVersionChange = isMajorVersionChanged(majorVersionSet, majorVersionStr);

        // so splitTitles can be used generally
        if (splitTitles == null) {
            splitTitles = new ArrayList<>();
            splitTitles.add(fullyQualifiedTitleId);
        }


        final List<String> pilotBooks = getPilotBooks(bookDefinition);

        // check errors in book definition compared to previous group
        validate(bookDefinition, lastGroupDef, majorVersionStr);

        final GroupDefinition groupDefinition = createNewGroupDefinition(
            groupName,
            getGroupId(bookDefinition),
            fullyQualifiedTitleId,
            majorVersionStr,
            newHasSubgroups);

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
            cleanAllSubgroups(fullyQualifiedTitleId, allSubGroupInfo, pilotBooks, majorVersionSet);
            if (oldHasSubgroups && newHasSubgroups) {
                // add new title to the appropriate subgroup
                SubGroupInfo selectedSubgroup = null;
                SubGroupInfo previousSubgroup = null;
                for (final SubGroupInfo subgroup : allSubGroupInfo) {
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
                    final List<String> titles = selectedSubgroup.getTitles();
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
                allSubGroupInfo = new ArrayList<>();
                allSubGroupInfo.add(new SubGroupInfo());
                allSubGroupInfo.get(0).setHeading(subGroupHeading);
                addTitlesToSubGroup(allSubGroupInfo.get(0), splitTitles, majorVersionStr);
            } else {
                // add new title to the first (only) subgroup (no header)
                allSubGroupInfo = new ArrayList<>();
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

    @NotNull
    private Boolean isMajorVersionChanged(final Set<BigInteger> majorVersionSet, final String finalMajorVersionStr) {
        return majorVersionSet.stream()
                .max(BigInteger::compareTo)
                .map(item -> Version.VERSION_PREFIX + item)
                .map(item -> !item.equals(finalMajorVersionStr))
                .orElse(false);
    }

    /**
     * Removes all instances of a titleId+majorVersion in a subgroup
     *
     * @param previousSubgroup ---- subgroup from which the given version of the given title should be removed
     * @param fullyQualifiedTitleId ---- titleID to be removed
     * @param majorVersionStr ---- major version to be removed, in the format "v#"
     */
    private void cleanPreviousSubGroup(
        final SubGroupInfo previousSubgroup,
        final String fullyQualifiedTitleId,
        final String majorVersionStr) {
        final List<String> iterableTitles = new ArrayList<>();
        iterableTitles.addAll(previousSubgroup.getTitles());
        for (final String title : iterableTitles) {
            final String titleId = StringUtils.substringBeforeLast(title, "/");
            if (StringUtils.substringAfterLast(title, "/").equals(majorVersionStr)
                && StringUtils.substringBeforeLast(titleId, "_pt").equals(fullyQualifiedTitleId)) {
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
    private void cleanAllSubgroups(
        final String titleId,
        final List<SubGroupInfo> subGroupInfoList,
        final List<String> pilotBooks,
        final Set<BigInteger> majorVersions) throws ProviewException {
        List<String> removedTitles;
        final List<SubGroupInfo> emptySubGroups = new ArrayList<>();
        for (final SubGroupInfo subgroup : subGroupInfoList) {
            removedTitles = new ArrayList<>();
            for (final String title : subgroup.getTitles()) {
                if (!isTitleInProview(title, majorVersions, titleId)) {
                    removedTitles.add(title);
                }
            }
            subgroup.getTitles().removeAll(removedTitles);
            if (subgroup.getTitles().isEmpty()) {
                emptySubGroups.add(subgroup);
            }
        }
        subGroupInfoList.removeAll(emptySubGroups);
    }

    /**
     * Add all splits of a titleId+majorVersion to the front of the subgroup's title list
     *
     * @param subgroup ---- subgroup for titles to be added to
     * @param titles ---- list of titleIDs to be added, generally split titles
     * @param majorVersionStr ---- major version to be appended to the titles, if "" no version suffix will be added
     * @throws ProviewException
     */
    private void addTitlesToSubGroup(final SubGroupInfo subgroup, final List<String> titles, String majorVersionStr)
        throws ProviewException {
        final List<String> oldTitles = subgroup.getTitles();
        subgroup.setTitles(new ArrayList<String>());
        if (StringUtils.isNotBlank(majorVersionStr)) {
            majorVersionStr = "/" + majorVersionStr;
        } else {
            majorVersionStr = "";
        }
        for (final String title : titles) {
            if (oldTitles.contains(title + majorVersionStr)) {
                oldTitles.remove(title + majorVersionStr);
            }
            subgroup.addTitle(title + majorVersionStr);
        }
        subgroup.getTitles().addAll(oldTitles);
    }

    private void validate(final BookDefinition book, final GroupDefinition previousGroup, final String majorVersionStr)
        throws ProviewException {
        final String currentSubgroupName = book.getSubGroupHeading();
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

        if (majorVersion > 1
            && StringUtils.isNotBlank(currentSubgroupName)
            && (previousGroup == null || StringUtils.isBlank(previousGroup.getFirstSubgroupHeading()))) {
            throw new ProviewException(CoreConstants.SUBGROUP_ERROR_MESSAGE);
        }
    }

    private GroupDefinition createNewGroupDefinition(
        final String groupName,
        final String groupId,
        final String fullyQualifiedTitleId,
        final String majorVersion,
        final boolean hasSubgroups) {
        final GroupDefinition groupDefinition = new GroupDefinition();
        groupDefinition.setName(groupName);
        groupDefinition.setGroupId(groupId);
        groupDefinition.setType(getGroupType(fullyQualifiedTitleId));
        final List<SubGroupInfo> subGroupInfoList = new ArrayList<>();
        groupDefinition.setSubGroupInfoList(subGroupInfoList);

        if (hasSubgroups) {
            groupDefinition.setHeadTitle(fullyQualifiedTitleId + "/" + majorVersion);
        } else {
            groupDefinition.setHeadTitle(fullyQualifiedTitleId);
        }
        return groupDefinition;
    }

    private String getGroupType(String titleId) {
        BookDefinition bookDefinition = bookDefinitionService.findBookDefinitionByTitle(titleId);
        return bookDefinition.isELooseleafsEnabled() && bookDefinition.isCwBook() ? GROUP_TYPE_EREFERENCE :
        bookDefinition.isELooseleafsEnabled() && bookDefinition.isUSCLBook() ? GROUP_TYPE_PERIODICAL : GROUP_TYPE_STANDARD;
    }

    private List<String> getPilotBooks(final BookDefinition book) {
        final List<String> pilotBooks = new ArrayList<>();
        for (final PilotBook pilotBook : book.getPilotBooks()) {
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
    private boolean isTitleInProview(
        String title,
        final Set<BigInteger> majorVersionSet,
        final String fullyQualifiedTitleId) throws ProviewException {
        if (StringUtils.startsWithIgnoreCase(title, fullyQualifiedTitleId)) {
            if (isTitleWithVersion(title)) {
                final String version = StringUtils.substringAfterLast(title, "/v");
                final BigInteger majorVersion = new BigInteger(StringUtils.substringBefore(version, "."));
                return majorVersionSet.contains(majorVersion);
            }
        }
        // It must be a pilot book
        else {
            title = isTitleWithVersion(title) ? StringUtils.substringBeforeLast(title, "/v") : title;
            return proviewHandler.isTitleInProview(title);
        }
        return false;
    }

    @Override
    public void removeAllPreviousGroups(final BookDefinition bookDefinition) throws Exception {
        final List<GroupDefinition> GroupDefinition = getGroups(bookDefinition);

        if (GroupDefinition != null) {
            for (final GroupDefinition group : GroupDefinition) {
                proviewHandler.removeGroup(group.getGroupId(), group.getProviewGroupVersionString());
                TimeUnit.SECONDS.sleep(2);
                proviewHandler.deleteGroup(group.getGroupId(), group.getProviewGroupVersionString());
            }
        }
    }

    /**
     * Get list of ProView titles that belong in the group for given book definition.
     *
     * Deprecated in favor of ProviewHandler.getTitleContainer()
     */
    @Override
    @Deprecated // Deprecated in favor of ProviewHandler.getTitleContainer()
    public Map<String, ProviewTitleInfo> getProViewTitlesForGroup(final BookDefinition bookDef) throws Exception {
        final Set<SplitNodeInfo> splitNodeInfos = bookDef.getSplitNodes();

        // Get fully qualified title IDs of all split titles
        final Set<String> splitTitles = new HashSet<>();
        if (splitNodeInfos != null && splitNodeInfos.size() > 0) {
            for (final SplitNodeInfo splitNodeInfo : splitNodeInfos) {
                splitTitles.add(splitNodeInfo.getSplitBookTitle());
            }
        }
        // Add current fully qualified title Id
        splitTitles.add(bookDef.getFullyQualifiedTitleId());
        final List<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();
        for (final String title : splitTitles) {
            final List<ProviewTitleInfo> proviewTitleInfo = getMajorVersionProviewTitles(title);
            proviewTitleInfos.addAll(proviewTitleInfo);
        }
        // sort split/single books before adding pilot books
        Collections.sort(proviewTitleInfos);

        final Map<String, ProviewTitleInfo> proviewTitleMap = new LinkedHashMap<>();
        for (final ProviewTitleInfo info : proviewTitleInfos) {
            final String key = info.getTitleId() + "/v" + info.getMajorVersion();
            proviewTitleMap.put(key, info);
        }
        return proviewTitleMap;
    }

    @Override
    public List<String> getPilotBooksNotFound() {
        return pilotBooksNotFound;
    }

    public void setPilotBooksNotFound(final List<String> pilotBooksNotFound) {
        this.pilotBooksNotFound = pilotBooksNotFound;
    }

    @Override
    public Map<String, ProviewTitleInfo> getPilotBooksForGroup(final BookDefinition book) throws Exception {
        final Map<String, ProviewTitleInfo> pilotBooks = new LinkedHashMap<>();
        pilotBooksNotFound = new ArrayList<>();
        final List<PilotBook> bookList = book.getPilotBooks();
        final List<String> notFoundList = new ArrayList<>();
        for (final PilotBook pilotBook : bookList) {
            try {
                ProviewTitleInfo latest = new ProviewTitleInfo();
                latest.setVersion("v0.0");
                Integer totalNumberOfVersions = 0;
                final ProviewTitleContainer singleBook =
                    proviewHandler.getProviewTitleContainer(pilotBook.getPilotBookTitleId());
                if (singleBook == null) {
                    notFoundList.add(pilotBook.getPilotBookTitleId());
                } else {
                    for (final ProviewTitleInfo titleInfo : singleBook.getAllMajorVersions()) {
                        totalNumberOfVersions++;
                        titleInfo.setTotalNumberOfVersions(totalNumberOfVersions);
                        if (latest.getMajorVersion().compareTo(titleInfo.getMajorVersion()) < 0) {
                            latest = titleInfo;
                        }
                    }
                    pilotBooks.put(latest.getTitleId() + "/v" + latest.getMajorVersion(), latest);
                }
            } catch (final Exception e) {
                notFoundList.add(pilotBook.getPilotBookTitleId());
            }
        }
        if (notFoundList.size() > 0) {
            for (final String pilotBookNotFound : notFoundList) {
                pilotBooksNotFound.add(pilotBookNotFound);
            }
            String msg = notFoundList.toString();
            msg = msg.replaceAll("\\[|\\]|\\{|\\}", "");
        }

        return pilotBooks;
    }

    @Override
    public List<ProviewTitleInfo> getMajorVersionProviewTitles(final String titleId) throws ProviewException {
        try {
            final ProviewTitleContainer container = proviewHandler.getProviewTitleContainer(titleId);
            if (container != null) {
                return container.getAllMajorVersions();
            }
        } catch (final ProviewException ex) {
            final String errorMessage = ex.getMessage();

            if (!errorMessage.contains("does not exist")) {
                throw ex;
            }
        }
        return new ArrayList<>();
    }

    @Autowired
    public void setBookDefinitionService(final BookDefinitionService bookDefinitionService) {
        this.bookDefinitionService = bookDefinitionService;
    }

    @Required
    public void setProviewHandler(final ProviewHandler proviewHandler) {
        this.proviewHandler = proviewHandler;
    }
}
