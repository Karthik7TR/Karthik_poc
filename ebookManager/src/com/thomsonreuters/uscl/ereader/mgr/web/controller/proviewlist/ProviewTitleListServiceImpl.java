package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.*;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm.Command;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
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
        final Command command = form.getCommand();
        final boolean isRefresh = Command.REFRESH.equals(command);
        final Map<String, ProviewTitleContainer> allProviewTitleInfo = getAllProviewTitleInfo(isRefresh);
        final List<ProviewTitleInfo> allLatestProviewTitleInfo = getAllLatestProviewTitleInfo();
        if (isRefresh || !hasAnyLastStatusUpdateDates(allLatestProviewTitleInfo)) {
            fillLatestUpdateDatesForTitleInfos(allLatestProviewTitleInfo, allProviewTitleInfo.keySet());
        }
        final List<ProviewTitleInfo> selectedProviewTitleInfo;
        if (form.areAllFiltersBlank()) {
            selectedProviewTitleInfo = allLatestProviewTitleInfo;
        } else {
            selectedProviewTitleInfo = getFilteredProviewTitleInfos(form, allLatestProviewTitleInfo);
        }
        return selectedProviewTitleInfo;
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

    private Map<String, Date> updateLatestUpdateDates(final Collection<String> titleIds) {
        return Optional.ofNullable(titleIds)
                .filter(CollectionUtils::isNotEmpty)
                .map(proviewAuditService::findMaxRequestDateByTitleIds)
                .orElseGet(Collections::emptyMap);
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
                selectedProviewTitleInfo.add(titleInfo);
            }
        }
        return selectedProviewTitleInfo;
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
}
