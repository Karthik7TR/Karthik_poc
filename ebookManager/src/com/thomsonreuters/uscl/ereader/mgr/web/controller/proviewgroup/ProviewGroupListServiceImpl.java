package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupForm.Command;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProviewGroupListServiceImpl implements ProviewGroupListService {

    @Autowired
    private ProviewHandler proviewHandler;
    @Autowired
    private ProviewAuditService proviewAuditService;

    @Override
    public AllProviewGroupsContainer getProviewGroups(@NotNull final ProviewGroupForm form,
        final Map<String, ProviewGroupContainer> allProviewGroupsParam,
        final List<ProviewGroup> allLatestProviewGroupsParam) throws ProviewException {
        final AllProviewGroupsContainer container = AllProviewGroupsContainer.builder()
            .allProviewGroups(allProviewGroupsParam)
            .allLatestProviewGroups(allLatestProviewGroupsParam)
            .build();
        final boolean isToRefresh = Command.REFRESH.equals(form.getCommand());
        List<ProviewGroup> allLatestProviewGroups = container.getAllLatestProviewGroups();
        if (allLatestProviewGroups == null || isToRefresh) {
            Map<String, ProviewGroupContainer> allProviewGroups = container.getAllProviewGroups();
            if (allProviewGroups == null || isToRefresh) {
                allProviewGroups = proviewHandler.getAllProviewGroupInfo();
                container.setAllProviewGroups(allProviewGroups);
            }
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
