package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.SubgroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupForm.Command;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProviewGroupListServiceImpl implements ProviewGroupListService {

    @Autowired
    private ProviewHandler proviewHandler;
    @Autowired
    private ProviewAuditService proviewAuditService;
    @Autowired
    private AllProviewGroupsProvider allProviewGroupsProvider;

    @Override
    public ProviewGroupsContainer getProviewGroups(@NotNull final ProviewGroupForm form,
        final List<ProviewGroup> allLatestProviewGroupsParam) throws ProviewException {
        final ProviewGroupsContainer container = ProviewGroupsContainer.builder()
            .allLatestProviewGroups(allLatestProviewGroupsParam)
            .build();
        final boolean isToRefresh = Command.REFRESH.equals(form.getCommand());
        List<ProviewGroup> allLatestProviewGroups = container.getAllLatestProviewGroups();
        if (allLatestProviewGroups == null || isToRefresh) {
            final Map<String, ProviewGroupContainer> allProviewGroups =
                    allProviewGroupsProvider.getAllProviewGroups(isToRefresh);
            final Map<String, Date> latestUpdateDates = updateGroupTitlesLatestUpdateDates(allProviewGroups.values());
            allLatestProviewGroups = proviewHandler.getAllLatestProviewGroupInfo(allProviewGroups);
            fillLatestUpdateDatesForProviewGroups(allLatestProviewGroups, latestUpdateDates);
            container.setAllLatestProviewGroups(allLatestProviewGroups);
        }
        if (form.areAllFiltersBlank()) {
            container.setSelectedProviewGroups(allLatestProviewGroups);
        } else {
            container.setSelectedProviewGroups(filterProviewGroupList(form, allLatestProviewGroups));
        }
        return container;
    }

    @Override
    public Pair<List<String>, List<GroupDetails>> getGroupDetailsList(@NotNull final ProviewGroupListFilterForm form,
            @NotNull final Map<String, ProviewGroupContainer> allProviewGroups) {
        Pair<List<String>, List<GroupDetails>> notFoundTitlesAndGroupDetailsList =
                new MutablePair<>(Collections.emptyList(), Collections.emptyList());
        final ProviewGroupContainer proviewGroupContainer = allProviewGroups.get(form.getProviewGroupID());
        final String groupVersion = form.getGroupVersion();
        if (proviewGroupContainer != null) {
            final ProviewGroup proviewGroup = proviewGroupContainer.getGroupByVersion(groupVersion);
            List<GroupDetails> groupDetailsList = Collections.emptyList();
            if (proviewGroup.getSubgroupInfoList() != null
                    && proviewGroup.getSubgroupInfoList().get(0).getSubGroupName() != null) {
                notFoundTitlesAndGroupDetailsList = getGroupDetailsWithSubGroups(groupVersion, proviewGroupContainer);
                groupDetailsList = notFoundTitlesAndGroupDetailsList.getRight();
                for (final GroupDetails groupDetail : groupDetailsList) {
                    Collections.sort(groupDetail.getTitleIdList());
                }
            } else if (proviewGroup.getSubgroupInfoList() != null) {
                notFoundTitlesAndGroupDetailsList = getGroupDetailsWithNoSubgroups(proviewGroup);
                groupDetailsList = notFoundTitlesAndGroupDetailsList.getRight();
            }
            Collections.sort(groupDetailsList);
        }
        return notFoundTitlesAndGroupDetailsList;
    }

    private Pair<List<String>, List<GroupDetails>> getGroupDetailsWithSubGroups(final String version,
            final ProviewGroupContainer proviewGroupContainer) {
        final Map<String, GroupDetails> groupDetailsMap = new HashMap<>();
        final List<String> notFoundTitleIds = new ArrayList<>();

        String rootGroupId = proviewGroupContainer.getGroupId();
        rootGroupId = StringUtils.substringAfter(rootGroupId, "_");

        final ProviewGroup selectedGroup = proviewGroupContainer.getGroupByVersion(version);
        // loop through each subgroup identified by ProView
        for (final SubgroupInfo subgroup : selectedGroup.getSubgroupInfoList()) {
            // loop through each distinct title in a subgroup listed by ProView
            for (final String titleIdVersion : subgroup.getTitleIdList()) {
                // gather Identifying information for the title
                final String titleId = StringUtils.substringBeforeLast(titleIdVersion, "/v").trim();
                final String titleMajorVersion = StringUtils.substringAfterLast(titleIdVersion, "/v").trim();
                BigInteger majorVersion = null;
                if (!titleMajorVersion.equals("")) {
                    majorVersion = new BigInteger(titleMajorVersion);
                }

                try {
                    final ProviewTitleContainer container = proviewHandler.getProviewTitleContainer(titleId);
                    if (container != null) {
                        // loop through all the versions of a title on ProView
                        for (final ProviewTitleInfo title : container.getProviewTitleInfos()) {
                            // check if major version in the group matches the
                            // major version of the current title
                            if (title.getMajorVersion().equals(majorVersion)) {
                                final String key =
                                        StringUtils.substringBeforeLast(title.getTitleId(), "_pt") + title.getVersion();
                                // is there already a subgroup for this title?
                                GroupDetails groupDetails = groupDetailsMap.get(key);
                                if (groupDetails == null) {
                                    groupDetails = new GroupDetails();
                                    groupDetailsMap.put(key, groupDetails);

                                    groupDetails.setSubGroupName(subgroup.getSubGroupName());
                                    groupDetails.setId(titleId);
                                    groupDetails.setTitleIdList(new ArrayList<>());
                                    groupDetails.setProviewDisplayName(title.getTitle());
                                    groupDetails.setBookVersion(title.getVersion());
                                    groupDetails.setLastupdate("0");

                                    // check if pilot book, set flag for sorting
                                    final String rootTitleId = titleId.replaceFirst(".*/.*/", "");
                                    if (!rootGroupId.equals(StringUtils.substringBeforeLast(rootTitleId, "_pt"))) {
                                        groupDetails.setPilotBook(true);
                                    }
                                }
                                if (groupDetails.getLastupdate().compareTo(title.getLastupdate()) < 0) {
                                    groupDetails.setLastupdate(title.getLastupdate());
                                }
                                groupDetails.addTitleInfo(title);
                            }
                        }
                    } else {
                        notFoundTitleIds.add(titleId);
                    }
                } catch (final ProviewException e) {
                    log.warn(e.getMessage(), e);
                    notFoundTitleIds.add(titleId);
                }
            }
        }

        return new ImmutablePair<>(
                new CopyOnWriteArrayList<>(notFoundTitleIds),
                new CopyOnWriteArrayList<>(groupDetailsMap.values()));
    }

    /**
     * For single titles. Gets book details from Proview and removed/deleted details from ProviewAudit
     *
     * @param proviewGroup
     * @return
     */
    private Pair<List<String>, List<GroupDetails>> getGroupDetailsWithNoSubgroups(final ProviewGroup proviewGroup) {
        final List<GroupDetails> groupDetailsList = new ArrayList<>();
        final List<String> notFound = new ArrayList<>();

        String rootGroupId = proviewGroup.getGroupId();
        rootGroupId = StringUtils.substringAfter(rootGroupId, "_");

        final SubgroupInfo subgroup = proviewGroup.getSubgroupInfoList().get(0);
        final Set<String> uniqueTitleIds = subgroup.getTitleIdList().stream()
                .map(BookTitleId::getTitleIdWithoutVersion)
                .collect(Collectors.toSet());
        for (final String titleId : uniqueTitleIds) {
            // get group details for each titleID directly from the ProView
            // response parser
            try {
                groupDetailsList.addAll(proviewHandler.getSingleTitleGroupDetails(titleId));
            } catch (final ProviewException ex) {
                final String errorMsg = ex.getMessage();
                // The versions of the title must have been removed.
                if (errorMsg.contains("does not exist")) {
                    notFound.add(titleId);
                } else {
                    log.warn("Unexpected ProviewException: " + ex.getMessage(), ex);
                }
            }
        }
        for (final GroupDetails details : groupDetailsList) {
            // set pilot book flag for sorting
            final String rootTitleId = details.getTitleId().replaceFirst(".*/.*/", "");
            if (!rootGroupId.equals(StringUtils.substringBeforeLast(rootTitleId, "_pt"))) {
                details.setPilotBook(true);
            }
            // add version to the title ID field
            details.setId(details.getTitleId() + "/" + details.getBookVersion());
        }

        return new ImmutablePair<>(
                new CopyOnWriteArrayList<>(notFound),
                new CopyOnWriteArrayList<>(groupDetailsList));
    }

    private Map<String, Date> updateGroupTitlesLatestUpdateDates(
        final Collection<ProviewGroupContainer> proviewGroupContainers) {
        final Set<String> titleIds = Optional.ofNullable(proviewGroupContainers)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(ProviewGroupContainer::getProviewGroups)
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .map(this::getProviewGroupTitleIds)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
        return proviewAuditService.findMaxRequestDateByTitleIds(titleIds);
    }

    private void fillLatestUpdateDatesForProviewGroups(final Collection<ProviewGroup> groups,
        final Map<String, Date> latestUpdateDatesParam) {
        final Map<String, Date> latestUpdateDates = Optional.ofNullable(latestUpdateDatesParam)
            .orElseGet(Collections::emptyMap);
        Optional.ofNullable(groups)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .forEach(proviewGroup -> getProviewGroupTitleIds(proviewGroup).stream()
                .map(latestUpdateDates::get)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .map(date -> DateFormatUtils.format(date, "yyyyMMdd"))
                .ifPresent(proviewGroup::setLatestUpdateDate));
    }

    private Set<String> getProviewGroupTitleIds(@NotNull final ProviewGroup proviewGroup) {
        return Optional.ofNullable(proviewGroup.getSubgroupInfoList())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .flatMap(subgroup -> subgroup.getTitleIdList().stream())
            .collect(Collectors.toSet());
    }

    @NotNull
    private List<ProviewGroup> filterProviewGroupList(@NotNull final ProviewGroupForm form,
        final List<ProviewGroup> allLatestProviewGroups) {
        final List<ProviewGroup> selectedProviewGroupList = new ArrayList<>();

        boolean groupNameBothWayWildCard = false;
        boolean groupNameEndsWithWildCard = false;
        boolean groupNameStartsWithWildCard = false;
        boolean groupIdBothWayWildCard = false;
        boolean groupIdEndsWithWildCard = false;
        boolean groupIdStartsWithWildCard = false;
        String groupNameSearchTerm = form.getGroupFilterName();
        String groupIdSearchTerm = form.getGroupFilterId();

        if (groupNameSearchTerm != null && groupNameSearchTerm.length() > 0) {
            if (groupNameSearchTerm.endsWith("%") && groupNameSearchTerm.startsWith("%")) {
                groupNameBothWayWildCard = true;
            } else if (groupNameSearchTerm.endsWith("%")) {
                groupNameStartsWithWildCard = true;
            } else if (groupNameSearchTerm.startsWith("%")) {
                groupNameEndsWithWildCard = true;
            }

            groupNameSearchTerm = groupNameSearchTerm.replaceAll("%", "");
        }

        if (groupIdSearchTerm != null && groupIdSearchTerm.length() > 0) {
            if (groupIdSearchTerm.endsWith("%") && groupIdSearchTerm.startsWith("%")) {
                groupIdBothWayWildCard = true;
            } else if (groupIdSearchTerm.endsWith("%")) {
                groupIdStartsWithWildCard = true;
            } else if (groupIdSearchTerm.startsWith("%")) {
                groupIdEndsWithWildCard = true;
            }

            groupIdSearchTerm = groupIdSearchTerm.replaceAll("%", "");
        }

        for (final ProviewGroup proviewGroup : allLatestProviewGroups) {
            boolean selected = true;

            if (groupNameSearchTerm != null && groupNameSearchTerm.length() > 0) {
                if (proviewGroup.getGroupName() == null) {
                    selected = false;
                } else {
                    if (groupNameBothWayWildCard) {
                        if (!proviewGroup.getGroupName().contains(groupNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (groupNameEndsWithWildCard) {
                        if (!proviewGroup.getGroupName().endsWith(groupNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (groupNameStartsWithWildCard) {
                        if (!proviewGroup.getGroupName().startsWith(groupNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (!proviewGroup.getGroupName().equals(groupNameSearchTerm)) {
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
                            if (!proviewGroup.getGroupId().endsWith(groupIdSearchTerm)) {
                                selected = false;
                            }
                        } else if (groupIdStartsWithWildCard) {
                            if (!proviewGroup.getGroupId().startsWith(groupIdSearchTerm)) {
                                selected = false;
                            }
                        } else if (!proviewGroup.getGroupId().equals(groupIdSearchTerm)) {
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
