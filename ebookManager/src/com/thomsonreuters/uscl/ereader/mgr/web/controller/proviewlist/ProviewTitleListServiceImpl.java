package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.*;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.*;
import static java.util.Optional.ofNullable;

@Service("proviewTitleListService")
@Slf4j
public class ProviewTitleListServiceImpl implements ProviewTitleListService {
    private static final String TITLE_ID_S_VERSION_S = "Title id: %s, version: %s %s";
    private static final String SUCCESS = "Success";
    private static final String UNSUCCESSFUL = "Unsuccessful";
    private static final String EMAIL_BODY = "Environment: %s%n%s";
    private static final String SUCCESSFULLY_UPDATED = "Successfully updated:";
    private static final String FAILED_TO_UPDATE = "Failed to update:";
    private static final String PURE_STATUS_PATTERN = String.format("\\%s?\\%s?",
            ERROR_SIGN_SOME_PARTS_ARE_IN_FINAL_SOME_IN_REVIEW_STATE,
            ERROR_SIGN_SOME_PARTS_HAVE_INCONSISTENT_STATUS_OR_ABSENT);

    private final ProviewAuditService proviewAuditService;
    private final BookDefinitionService bookDefinitionService;
    private final ProviewHandler proviewHandler;
    private final ProviewTitlesProvider proviewTitlesProvider;
    private final EmailUtil emailUtil;
    private final EmailService emailService;
    private final String environmentName;

    @Autowired
    public ProviewTitleListServiceImpl(
        final BookDefinitionService bookDefinitionService,
        final ProviewAuditService proviewAuditService,
        final ProviewHandler proviewHandler,
        final ProviewTitlesProvider proviewTitlesProvider,
        EmailUtil emailUtil,
        EmailService emailService,
        @Qualifier("environmentName")
        String environmentName) {
        this.bookDefinitionService = bookDefinitionService;
        this.proviewAuditService = proviewAuditService;
        this.proviewHandler = proviewHandler;
        this.proviewTitlesProvider = proviewTitlesProvider;
        this.emailUtil = emailUtil;
        this.emailService = emailService;
        this.environmentName = environmentName;
    }

    @Override
    public List<ProviewTitleInfo> getSelectedProviewTitleInfo(final ProviewListFilterForm form)
            throws ProviewException {
        final com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm.Command command = form.getCommand();
        final boolean isRefresh = com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm.Command.REFRESH.equals(command);
        final Map<String, ProviewTitleContainer> allProviewTitleInfo = getAllProviewTitleInfo(isRefresh);
        final List<ProviewTitleInfo> allLatestProviewTitleInfo = getAllLatestProviewTitleInfo();
        if (isRefresh || !hasAnyLastStatusUpdateDates(allLatestProviewTitleInfo)) {
            fillLatestUpdateDatesForTitleInfos(allLatestProviewTitleInfo, allProviewTitleInfo.keySet());
        }
        //Update Job Submitter user name from ProviewAudit
        fillLatestJobSubmitterNameForTitleInfos(allLatestProviewTitleInfo);
        final List<ProviewTitleInfo> selectedProviewTitleInfo;
        List<ProviewTitleInfo> lstProviewTitleInfoSortFinal = null;
        if (form.areAllFiltersBlank()) {
            selectedProviewTitleInfo = allLatestProviewTitleInfo;
        }
        else {
            selectedProviewTitleInfo = getFilteredProviewTitleInfos(form, allLatestProviewTitleInfo);
        }
        form.setProviewTitleListFullSize(selectedProviewTitleInfo.size());
        form.setProviewTitleListFull(selectedProviewTitleInfo);
        lstProviewTitleInfoSortFinal = pagingAndSortingProviewTitleInfoList(form, selectedProviewTitleInfo);

        return lstProviewTitleInfoSortFinal;
    }

    private List<ProviewTitleInfo> pagingAndSortingProviewTitleInfoList(
            @NotNull final ProviewListFilterForm form,
            final List<ProviewTitleInfo> selectedProviewTitleInfoList) {

        final int totalProviewGroupRecords = selectedProviewTitleInfoList.size();

        //Now sort the list and retrieve page wise data
        int minIndex = (form.getPage() - 1) * (form.getObjectsPerPage());
        int maxIndex = form.getObjectsPerPage() + minIndex;
        if (minIndex > totalProviewGroupRecords) {
            minIndex = totalProviewGroupRecords;
        }
        if (maxIndex > totalProviewGroupRecords) {
            maxIndex = totalProviewGroupRecords;
        }

        List<ProviewTitleInfo> subList = null;
        List<ProviewTitleInfo> foundList = null;
        if ("PROVIEW_DISPLAY_NAME".equalsIgnoreCase(form.getSort().name().toString()) &&
                form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getTitle,
                            Comparator.nullsLast(Comparator.naturalOrder())));
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("PROVIEW_DISPLAY_NAME".equalsIgnoreCase(form.getSort().name().toString()) &&
                !form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getTitle,
                            Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("TITLE_ID".equalsIgnoreCase(form.getSort().name().toString()) &&
                form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getTitleId,
                            Comparator.nullsLast(Comparator.naturalOrder())));
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("TITLE_ID".equalsIgnoreCase(form.getSort().name().toString()) &&
                !form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getTitleId,
                            Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("JOB_SUBMITTER_NAME".equalsIgnoreCase(form.getSort().name().toString()) &&
                form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getJobSubmitterName,
                            Comparator.nullsLast(Comparator.naturalOrder())));
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("JOB_SUBMITTER_NAME".equalsIgnoreCase(form.getSort().name().toString()) &&
                !form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getJobSubmitterName,
                            Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("TOTAL_VERSIONS".equalsIgnoreCase(form.getSort().name().toString()) &&
                form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getTotalNumberOfVersions,
                            Comparator.nullsLast(Comparator.naturalOrder())));
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("TOTAL_VERSIONS".equalsIgnoreCase(form.getSort().name().toString()) &&
                !form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getTotalNumberOfVersions,
                            Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("LATEST_VERSION".equalsIgnoreCase(form.getSort().name().toString()) &&
                form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getVersion, //get minor or majaor or just version ?
                            Comparator.nullsLast(Comparator.naturalOrder())));
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("LATEST_VERSION".equalsIgnoreCase(form.getSort().name().toString()) &&
                !form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getVersion,
                            Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("LATEST_STATUS_UPDATE".equalsIgnoreCase(form.getSort().name().toString()) &&
                form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList, new Comparator<ProviewTitleInfo>() {
                @Override
                public int compare(ProviewTitleInfo o1, ProviewTitleInfo o2) {
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    try {
                        return dateFormat.parse((o1.getLastStatusUpdateDate() == null || "".equals(o1.getLastStatusUpdateDate())
                                        ? "29991231" : o1.getLastStatusUpdateDate())).
                                compareTo(dateFormat.parse((o2.getLastStatusUpdateDate() == null || "".equals(o2.getLastStatusUpdateDate())
                                        ? "29991231" : o2.getLastStatusUpdateDate())));
                    } catch (final Exception e) {
                        log.error("Failed to parse last Update date: ", e);
                        return 0;
                    }
                }
            });

            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("LATEST_STATUS_UPDATE".equalsIgnoreCase(form.getSort().name().toString()) &&
                !form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList, new Comparator<ProviewTitleInfo>() {
                @Override
                public int compare(ProviewTitleInfo o1, ProviewTitleInfo o2) {
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    try {
                        return dateFormat.parse((o2.getLastStatusUpdateDate() == null || "".equals(o2.getLastStatusUpdateDate())
                                        ? "29991231" : o2.getLastStatusUpdateDate())).
                                compareTo(dateFormat.parse((o1.getLastStatusUpdateDate() == null || "".equals(o1.getLastStatusUpdateDate())
                                        ? "29991231" : o1.getLastStatusUpdateDate())));
                    } catch (final Exception e) {
                        log.error("Failed to parse last Update date: ", e);
                        return 0;
                    }
                }
            });
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);

        } else if ("STATUS".equalsIgnoreCase(form.getSort().name().toString()) &&
                form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getStatus,
                            Comparator.nullsLast(Comparator.naturalOrder())));
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
            foundList = new ArrayList<>(subList);
        } else if ("STATUS".equalsIgnoreCase(form.getSort().name().toString()) &&
                !form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getStatus,
                            Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            subList = selectedProviewTitleInfoList.subList(minIndex, maxIndex);
            foundList = new ArrayList<>(subList);

          } else if ("PUBLISHER".equalsIgnoreCase(form.getSort().name().toString()) &&
            form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                Comparator.comparing(ProviewTitleInfo::getPublisher, //get minor or majaor or just version ?
                        Comparator.nullsLast(Comparator.naturalOrder())));
            subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
              foundList = new ArrayList<>(subList);
         } else if ("PUBLISHER".equalsIgnoreCase(form.getSort().name().toString()) &&
            !form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList,
                    Comparator.comparing(ProviewTitleInfo::getPublisher,
                            Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            subList = selectedProviewTitleInfoList.subList(minIndex, maxIndex);
            foundList = new ArrayList<>(subList);
         } else if ("SPLIT_PARTS".equalsIgnoreCase(form.getSort().name().toString()) &&
            form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList, (o1, o2) -> o1.getSplitParts().size() - o2.getSplitParts().size());
            subList = selectedProviewTitleInfoList.subList(minIndex, maxIndex);
            foundList = new ArrayList<>(subList);

        } else if ("SPLIT_PARTS".equalsIgnoreCase(form.getSort().name().toString()) &&
                !form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList, (o1, o2) -> o2.getSplitParts().size() - o1.getSplitParts().size());
            subList = selectedProviewTitleInfoList.subList(minIndex, maxIndex);
            foundList = new ArrayList<>(subList);

    } else if ("LAST_UPDATE".equalsIgnoreCase(form.getSort().name().toString()) &&
            form.isAscendingSort()) {
        Collections.sort(selectedProviewTitleInfoList, new Comparator<ProviewTitleInfo>() {
            @Override
            public int compare(ProviewTitleInfo o1, ProviewTitleInfo o2) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                try {
                    return dateFormat.parse((o1.getLastupdate() == null || "".equals(o1.getLastupdate())
                                    ? "29991231" : o1.getLastupdate())).
                            compareTo(dateFormat.parse((o2.getLastupdate() == null || "".equals(o2.getLastupdate())
                                    ? "29991231" : o2.getLastupdate())));
                } catch (final Exception e) {
                    log.error("Failed to parse last Update date: ", e);
                    return 0;
                }
            }
        });

        subList = selectedProviewTitleInfoList.subList(minIndex,maxIndex);
        foundList = new ArrayList<>(subList);
    } else if ("LAST_UPDATE".equalsIgnoreCase(form.getSort().name().toString()) &&
            !form.isAscendingSort()) {
            Collections.sort(selectedProviewTitleInfoList, new Comparator<ProviewTitleInfo>() {
                @Override
                public int compare(ProviewTitleInfo o1, ProviewTitleInfo o2) {
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    try {
                        return dateFormat.parse((o2.getLastupdate() == null || "".equals(o2.getLastupdate())
                                        ? "29991231" : o2.getLastupdate())).
                                compareTo(dateFormat.parse((o1.getLastupdate() == null || "".equals(o1.getLastupdate())
                                        ? "29991231" : o1.getLastupdate())));
                    } catch (final Exception e) {
                        log.error("Failed to parse last Update date: ", e);
                        return 0;
                    }
                }
            });
            subList = selectedProviewTitleInfoList.subList(minIndex, maxIndex);
            foundList = new ArrayList<>(subList);
        }
        return foundList;
}


        public List <ProviewTitleReportInfo> getSelectedProviewTitleReportInfo(final ProviewTitlesReportFilterForm form)
            throws ProviewException
    {
        final List<ProviewTitleReportInfo> allLatestProviewTitleReportInfo = getAllLatestProviewTitleReportInfo();
        final List<ProviewTitleReportInfo> selectedProviewTitleReportInfo;
        if (form.areAllFiltersBlank()) {
            selectedProviewTitleReportInfo = allLatestProviewTitleReportInfo;
        } else {
            selectedProviewTitleReportInfo = getFilteredProviewTitleReportInfos(form, allLatestProviewTitleReportInfo);
        }

        return selectedProviewTitleReportInfo;
    }

    private boolean hasAnyLastStatusUpdateDates(final List<ProviewTitleInfo> allLatestProviewTitleInfo) {
        return Optional.ofNullable(allLatestProviewTitleInfo)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .anyMatch(titleInfo -> titleInfo.getLastStatusUpdateDate() != null);
    }

    private void fillLatestUpdateDatesForTitleInfos(final List<ProviewTitleInfo> latestTitleInfos,
            final Collection<String> titleIds) {
        final Map<String, Date> latestUpdateDates = updateLatestUpdateDates(titleIds);
        Optional.ofNullable(latestTitleInfos)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .forEach(titleInfo -> titleInfo.setLastStatusUpdateDate(
                        mapDateToString(latestUpdateDates.get(titleInfo.getTitleId()))));
    }

    private void fillLatestJobSubmitterNameForTitleInfos(final List<ProviewTitleInfo> latestTitleInfos) {
        List<ProviewAudit> lstTitleSubmitter = findJobSubmitterNameForAllTitlesLatestVersion();

        //update Job Submitter Name for each title
        latestTitleInfos.forEach(titleInfo -> {
            ProviewAudit proviewAudit = lstTitleSubmitter.stream().filter(submitter ->
                    submitter.getTitleId().equals(titleInfo.getTitleId()) &&
                    submitter.getBookVersion().equals(titleInfo.getVersion())).findFirst().orElse(null);
            if (proviewAudit != null) {
                titleInfo.setJobSubmitterName(proviewAudit.getUsername());
            }
        });

    }

    private Map<String, Date> updateLatestUpdateDates(final Collection<String> titleIds) {
        return Optional.ofNullable(titleIds)
                .filter(CollectionUtils::isNotEmpty)
                .map(proviewAuditService::findMaxRequestDateByTitleIds)
                .orElseGet(Collections::emptyMap);
    }

    private List<ProviewAudit> findJobSubmitterNameForAllTitlesLatestVersion() {
        return proviewAuditService.findJobSubmitterNameForAllTitlesLatestVersion();
    }

    private String mapDateToString(final Date date) {
        return Optional.ofNullable(date)
                .map(nonNullDate -> DateFormatUtils.format(nonNullDate, "yyyyMMdd"))
                .orElse(null);
    }

    private List<ProviewTitleInfo> getFilteredProviewTitleInfos(final ProviewListFilterForm filterForm,
            final List<ProviewTitleInfo> allLatestProviewTitleInfo) {
        List<ProviewTitleInfo> selectedProviewTitleInfo = new ArrayList<>();
        boolean proviewDisplayNameBothWayWildCard = false;
        boolean proviewDisplayNameEndsWithWildCard = false;
        boolean proviewDisplayNameStartsWithWildCard = false;
        boolean titleIdBothWayWildCard = false;
        boolean titleIdEndsWithWildCard = false;
        boolean titleIdStartsWithWildCard = false;
        String proviewDisplayNameSearchTerm = filterForm.getProviewDisplayName();
        String titleIdSearchTerm = filterForm.getTitleId();
        String statusSearchTerm = filterForm.getStatus();
        Date fromDate=filterForm.getFromDate();
        Date toDate=filterForm.getToDate();

        if (filterForm.getProviewDisplayName() != null) {
            if (filterForm.getProviewDisplayName().endsWith("%")
                    && filterForm.getProviewDisplayName().startsWith("%")) {
                proviewDisplayNameBothWayWildCard = true;
            } else if (filterForm.getProviewDisplayName().endsWith("%")) {
                proviewDisplayNameStartsWithWildCard = true;
            } else if (filterForm.getProviewDisplayName().startsWith("%")) {
                proviewDisplayNameEndsWithWildCard = true;
            }

            proviewDisplayNameSearchTerm = proviewDisplayNameSearchTerm.replaceAll("%", "");
        }

        if (filterForm.getTitleId() != null) {
            if (filterForm.getTitleId().endsWith("%") && filterForm.getTitleId().startsWith("%")) {
                titleIdBothWayWildCard = true;
            } else if (filterForm.getTitleId().endsWith("%")) {
                titleIdStartsWithWildCard = true;
            } else if (filterForm.getTitleId().startsWith("%")) {
                titleIdEndsWithWildCard = true;
            }

            titleIdSearchTerm = titleIdSearchTerm.toLowerCase().replaceAll("%", "");
        }

        for (final ProviewTitleInfo titleInfo : allLatestProviewTitleInfo) {
            boolean selected = true;
            if (proviewDisplayNameSearchTerm != null) {
                if (titleInfo.getTitle() == null) {
                    selected = false;
                } else {
                    if (proviewDisplayNameBothWayWildCard) {
                        if (!titleInfo.getTitle().contains(proviewDisplayNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (proviewDisplayNameEndsWithWildCard) {
                        if (!titleInfo.getTitle().endsWith(proviewDisplayNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (proviewDisplayNameStartsWithWildCard) {
                        if (!titleInfo.getTitle().startsWith(proviewDisplayNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (!titleInfo.getTitle().equals(proviewDisplayNameSearchTerm)) {
                        selected = false;
                    }
                }
            }
            if (selected) {
                if (titleIdSearchTerm != null) {
                    if (titleInfo.getTitleId() == null) {
                        selected = false;
                    } else {
                        if (titleIdBothWayWildCard) {
                            if (!titleInfo.getTitleId().contains(titleIdSearchTerm)) {
                                selected = false;
                            }
                        } else if (titleIdEndsWithWildCard) {
                            if (!titleInfo.getTitleId().endsWith(titleIdSearchTerm)) {
                                selected = false;
                            }
                        } else if (titleIdStartsWithWildCard) {
                            if (!titleInfo.getTitleId().startsWith(titleIdSearchTerm)) {
                                selected = false;
                            }
                        } else if (!titleInfo.getTitleId().equals(titleIdSearchTerm)) {
                            selected = false;
                        }
                    }
                }
            }
            if (selected) {
                if (!(titleInfo.getTotalNumberOfVersions() >= filterForm.getMinVersionsInt())) {
                    selected = false;
                }
            }
            if (selected) {
                if (!(titleInfo.getTotalNumberOfVersions() <= filterForm.getMaxVersionsInt())) {
                    selected = false;
                }
            }
            if (selected) {
                if (fromDate != null) {
                    if (!(parseDate(titleInfo.getLastupdate()).compareTo(fromDate) > 0 ||
                            parseDate(titleInfo.getLastupdate()).compareTo(fromDate) == 0)) {
                        selected = false;
                    }
                }
            }

            if (selected) {
                if (toDate != null) {
                    if (!(parseDate(titleInfo.getLastupdate()).compareTo(toDate) < 0 ||
                            parseDate(titleInfo.getLastupdate()).compareTo(toDate) == 0)) {
                        selected = false;
                    }
                }
            }

            if (selected) {
                if (statusSearchTerm != null) {
                    if (!StringUtils.containsIgnoreCase(titleInfo.getStatus(), statusSearchTerm)) {
                        selected = false;
                    }
                }
            }
            if (selected) {
                selectedProviewTitleInfo.add(titleInfo);
            }
        }
        return selectedProviewTitleInfo;
    }

    private List<ProviewTitleReportInfo> getFilteredProviewTitleReportInfos(final ProviewTitlesReportFilterForm filterForm,
                                                                final List<ProviewTitleReportInfo> allLatestProviewTitleReportInfo) {
        List<ProviewTitleReportInfo> selectedProviewTitleReportInfo = new ArrayList<ProviewTitleReportInfo>();
        boolean proviewDisplayNameBothWayWildCard = false;
        boolean proviewDisplayNameEndsWithWildCard = false;
        boolean proviewDisplayNameStartsWithWildCard = false;
        boolean titleIdBothWayWildCard = false;
        boolean titleIdEndsWithWildCard = false;
        boolean titleIdStartsWithWildCard = false;
        String proviewDisplayNameSearchTerm = filterForm.getProviewDisplayName();
        String titleIdSearchTerm = filterForm.getTitleId();
        String statusSearchTerm = filterForm.getStatus();

        if (filterForm.getProviewDisplayName() != null) {
            if (filterForm.getProviewDisplayName().endsWith("%")
                    && filterForm.getProviewDisplayName().startsWith("%")) {
                proviewDisplayNameBothWayWildCard = true;
            } else if (filterForm.getProviewDisplayName().endsWith("%")) {
                proviewDisplayNameStartsWithWildCard = true;
            } else if (filterForm.getProviewDisplayName().startsWith("%")) {
                proviewDisplayNameEndsWithWildCard = true;
            }

            proviewDisplayNameSearchTerm = proviewDisplayNameSearchTerm.replaceAll("%", "");
        }

        if (filterForm.getTitleId() != null) {
            if (filterForm.getTitleId().endsWith("%") && filterForm.getTitleId().startsWith("%")) {
                titleIdBothWayWildCard = true;
            } else if (filterForm.getTitleId().endsWith("%")) {
                titleIdStartsWithWildCard = true;
            } else if (filterForm.getTitleId().startsWith("%")) {
                titleIdEndsWithWildCard = true;
            }

            titleIdSearchTerm = titleIdSearchTerm.toLowerCase().replaceAll("%", "");
        }

        for (final ProviewTitleReportInfo titleInfo : allLatestProviewTitleReportInfo) {
            boolean selected = true;
            if (proviewDisplayNameSearchTerm != null) {
                if (titleInfo.getId() == null) {
                    selected = false;
                } else {
                    if (proviewDisplayNameBothWayWildCard) {
                        if (!titleInfo.getId().contains(proviewDisplayNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (proviewDisplayNameEndsWithWildCard) {
                        if (!titleInfo.getId().endsWith(proviewDisplayNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (proviewDisplayNameStartsWithWildCard) {
                        if (!titleInfo.getId().startsWith(proviewDisplayNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (!titleInfo.getId().equals(proviewDisplayNameSearchTerm)) {
                        selected = false;
                    }
                }
            }
            if (selected) {
                if (titleIdSearchTerm != null) {
                    if (titleInfo.getId() == null) {
                        selected = false;
                    } else {
                        if (titleIdBothWayWildCard) {
                            if (!titleInfo.getId().contains(titleIdSearchTerm)) {
                                selected = false;
                            }
                        } else if (titleIdEndsWithWildCard) {
                            if (!titleInfo.getId().endsWith(titleIdSearchTerm)) {
                                selected = false;
                            }
                        } else if (titleIdStartsWithWildCard) {
                            if (!titleInfo.getId().startsWith(titleIdSearchTerm)) {
                                selected = false;
                            }
                        } else if (!titleInfo.getId().equals(titleIdSearchTerm)) {
                            selected = false;
                        }
                    }
                }
            }
            if (selected) {
                if (!(titleInfo.getTotalNumberOfVersions() >= filterForm.getMinVersionsInt())) {
                    selected = false;
                }
            }
            if (selected) {
                if (!(titleInfo.getTotalNumberOfVersions() <= filterForm.getMaxVersionsInt())) {
                    selected = false;
                }
            }
            if (selected) {
                if (statusSearchTerm != null) {
                    if (!StringUtils.containsIgnoreCase(titleInfo.getStatus(), statusSearchTerm)) {
                        selected = false;
                    }
                }
            }
            if (selected) {
                selectedProviewTitleReportInfo.add(titleInfo);
            }
        }
        return selectedProviewTitleReportInfo;
    }

    @Override
    public Map<String, ProviewTitleContainer> getAllProviewTitleInfo(final boolean isRefresh) throws ProviewException {
        return proviewTitlesProvider.provideAll(isRefresh);
    }

    @Override
    public List<ProviewTitleInfo> getAllLatestProviewTitleInfo() throws ProviewException {
        return proviewTitlesProvider.provideAllLatest();
    }

    @Override
    public List<ProviewTitleReportInfo> getAllLatestProviewTitleReportInfo() throws ProviewException {
        return proviewTitlesProvider.provideAllLatestProviewTitleReport();
    }

    @Override
    public void markTitleSuperseded(final String titleId) throws ProviewException {
        proviewHandler.markTitleSuperseded(titleId);
    }

    @Override
    @NotNull
    public List<ProviewTitle> getProviewTitles(
        @NotNull final List<ProviewTitleInfo> titleInfos,
        @Nullable final BookDefinition book) {
        return titleInfos.stream()
                .map(titleInfo -> ofNullable(book)
                        .map(item -> {
                            final String status = titleInfo.getStatus();
                            final boolean canPromote = book.getPilotBookStatus() != PilotBookStatus.IN_PROGRESS && canPromote(status);
                            final boolean canRemove = canRemove(status);
                            final boolean canDelete = canDelete(status);
                            return new ProviewTitle(titleInfo, canPromote, canRemove, canDelete);
                        })
                        .orElse(new ProviewTitle(titleInfo)))
                .collect(Collectors.toList());
    }

    @Override
    public boolean canDelete(String status) {
        return CAN_DELETE_STATUSES.contains(getPureStatus(status));
    }

    @Override
    public boolean canRemove(String status) {
        return CAN_REMOVE_STATUSES.contains(getPureStatus(status));
    }

    @Override
    public boolean canPromote(String status) {
        return CAN_PROMOTE_STATUSES.contains(status);
    }

    @NotNull
    private String getPureStatus(String status) {
        return status.replaceAll(PURE_STATUS_PATTERN,"");
    }

    @Override
    @Nullable
    public BookDefinition getBook(@NotNull final TitleId titleId) {
        BookDefinition bookDef = bookDefinitionService.findBookDefinitionByTitle(titleId.getTitleId());
        if (bookDef == null) {
            bookDef = bookDefinitionService.findBookDefinitionByTitle(titleId.getHeadTitleId());
        }
        return bookDef;
    }

    @SneakyThrows
    @Override
    public List<String> getAllSplitBookTitleIdsOnProview(final String headTitle, final Version version,
        final String... titleStatuses) {
        final List<String> splitBookTitles = new ArrayList<>();
        final Set<String> includedStatuses = new HashSet<>(Arrays.asList(titleStatuses));
        final Map<String, ProviewTitleContainer> proviewTitleInfo = proviewHandler.getAllProviewTitleInfo();
        proviewTitleInfo.keySet().stream()
            .filter(title -> headTitle.equals(new TitleId(title).getHeadTitleId()))
            .forEach(title -> proviewTitleInfo.get(title).getProviewTitleInfos().stream()
                .filter(titleInfo -> version.equals(new Version(titleInfo.getVersion())))
                .filter(titleInfo -> includedStatuses.contains(titleInfo.getStatus()))
                .findAny()
                .ifPresent(titleInfo -> splitBookTitles.add(titleInfo.getTitleId())));
        return splitBookTitles;
    }

    @SneakyThrows
    @Override
    public TitleActionResult updateTitleStatusesInProview(final ProviewTitleForm form, final Consumer<String> action,
        final String... titleStatuses) {
        final TitleActionResult actionResult = new TitleActionResult();
        final String headTitleId = new TitleId(form.getTitleId()).getHeadTitleId();
        final List<String> titleIds = getAllSplitBookTitleIdsOnProview(headTitleId,
            new Version(form.getVersion()), titleStatuses);
        actionResult.getTitlesToUpdate().addAll(titleIds);
        titleIds.forEach(title -> {
            try {
                action.accept(title);
                actionResult.getTitlesToUpdate().remove(title);
                actionResult.getUpdatedTitles().add(title);
            } catch (Exception e) {
                actionResult.setErrorMessage(e.getMessage());
                log.error(e.getMessage(), e);
            }
        });
        return actionResult;
    }

    @SneakyThrows
    @Override
    public void promoteTitleOnProview(final ProviewTitleForm form,  final String title) {
        proviewHandler.promoteTitle(title, form.getVersion());
    }

    @SneakyThrows
    @Override
    public void removeTitleFromProview(final ProviewTitleForm form, final String title) {
        proviewHandler.removeTitle(title, new Version(form.getVersion()));
    }

    @SneakyThrows
    @Override
    public void deleteTitleFromProview(final ProviewTitleForm form, final String title) {
        proviewHandler.deleteTitle(title, new Version(form.getVersion()));
    }

    @SneakyThrows
    @Override
    public TitleActionResult executeTitleAction(final ProviewTitleForm form, final TitleAction action,
        final boolean isJobRunningForBook) {
        final String headTitleId = new TitleId(form.getTitleId()).getHeadTitleId();
        final String version = form.getVersion();
        final String username = UserUtils.getAuthenticatedUserName();
        TitleActionResult titleActionResult = new TitleActionResult();
        if (!isJobRunningForBook) {
            titleActionResult = action.getAction().call();
            if (titleActionResult.hasErrorMessage()) {
                sendFailureEmail(username, action, titleActionResult, headTitleId, version);
            } else {
                sendSuccessEmail(username, action, headTitleId, version);
            }
            titleActionResult.getUpdatedTitles().forEach(title ->
                    proviewAuditService.save(form.createAudit(title)));
        }
        return titleActionResult;
    }

    private void sendSuccessEmail(final String username, final TitleAction action,
        final String titleId, final String version) {
        final String emailBody = String.format(TITLE_ID_S_VERSION_S, titleId, version,
            action.getEmailBodySuccess());
        sendEmail(username, String.format(action.getEmailSubjectTemplate(), SUCCESS, titleId), emailBody);
    }

    private void sendFailureEmail(final String username, final TitleAction action,
        final TitleActionResult actionResult, final String titleId, final String version) {
        StringBuilder partsInfo = new StringBuilder();
        final List<String> updatedTitles = actionResult.getUpdatedTitles();
        final List<String> titlesToUpdate = actionResult.getTitlesToUpdate();
        if (hasSeveralParts(updatedTitles, titlesToUpdate)) {
            if (!updatedTitles.isEmpty()) {
                addSuccessfullyUpdatedPartsToEmailBody(partsInfo, updatedTitles);
            }
            addPartsFailedToUpdateToEmailBody(partsInfo, titlesToUpdate);
        }
        final String emailBody = String
            .format(TITLE_ID_S_VERSION_S, titleId, version, action.getEmailBodyUnsuccessful());
        sendEmail(username, String.format(action.getEmailSubjectTemplate(), UNSUCCESSFUL, titleId),
            emailBody + System.lineSeparator() + partsInfo);
    }

    private void addPartsFailedToUpdateToEmailBody(final StringBuilder partsInfo,
        final List<String> titlesToUpdate) {
        addTitlesToEmailBody(partsInfo, FAILED_TO_UPDATE, titlesToUpdate);
    }

    private void addSuccessfullyUpdatedPartsToEmailBody(final StringBuilder partsInfo,
        final List<String> updatedTitles) {
        addTitlesToEmailBody(partsInfo, SUCCESSFULLY_UPDATED, updatedTitles);
    }

    private void addTitlesToEmailBody(final StringBuilder partsInfo, final String sectionTitle,
        final List<String> titles) {
        partsInfo.append(System.lineSeparator())
                .append(sectionTitle)
                .append(System.lineSeparator());
        titles.sort(Comparator.naturalOrder());
        titles.forEach(title -> partsInfo.append(title)
                .append(System.lineSeparator()));
    }

    private void sendEmail(final String username, final String subject, final String body) {
        final Collection<InternetAddress> emails =
            emailUtil.getEmailRecipientsByUsername(username);
        emailService
            .send(new NotificationEmail(emails, subject, String.format(EMAIL_BODY, environmentName, body)));
    }

    private boolean hasSeveralParts(final List<String> updatedTitles, final List<String> titlesToUpdate) {
        return updatedTitles.size() + titlesToUpdate.size() > 1;
    }

    @Override
    public List<String> getPreviousVersions(final String titleId) {
        return ofNullable(titleId)
                .map(e -> fetchAllProviewTitleInfo())
                .map(titles -> titles.get(titleId))
                .map(ProviewTitleContainer::getProviewTitleInfos)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ProviewTitleInfo::getVersion)
                .sorted(new VersionComparatorDesc())
                .collect(Collectors.toList());
    }

    private Map<String, ProviewTitleContainer> fetchAllProviewTitleInfo() {
        try {
            return this.getAllProviewTitleInfo(false);
        } catch (ProviewException e) {
            throw new EBookException(e);
        }
    }

    public static Date parseDate(final String dateString) {
        Date date = null;
        try {
            if (StringUtils.isNotBlank(dateString)) {
                final String[] parsePatterns = {CoreConstants.DIR_DATE_FORMAT};
                date = DateUtils.parseDate(dateString, parsePatterns);
            }
        } catch (final ParseException e) {
            //Intentionally left blank
        }
        return date;
    }

}
