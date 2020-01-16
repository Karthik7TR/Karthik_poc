package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.CLEANUP_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.FINAL_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REMOVED_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REVIEW_BOOK_STATUS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class ProviewTitleListController {
    private static final String TITLE_ID_S_VERSION_S = "Title id: %s, version: %s %s";
    private static final String SUCCESS = "Success";
    private static final String UNSUCCESSFUL = "Unsuccessful";
    private static final String EMAIL_BODY = "Environment: %s%n%s";
    private static final String SUCCESSFULLY_UPDATED = "Successfully updated:";
    private static final String FAILED_TO_UPDATE = "Failed to update:";

    private final ProviewHandler proviewHandler;
    private final BookDefinitionService bookDefinitionService;
    private final ProviewAuditService proviewAuditService;
    private final ManagerService managerService;
    private final MessageSourceAccessor messageSourceAccessor;
    private final JobRequestService jobRequestService;
    private final GroupService groupService;
    private final ProviewTitleListService proviewTitleListService;
    private final OutageService outageService;
    private final VersionIsbnService versionIsbnService;
    private final EmailUtil emailUtil;
    private final EmailService emailService;
    private final String environmentName;

    @Autowired
    public ProviewTitleListController(
        final ProviewHandler proviewHandler,
        final BookDefinitionService bookDefinitionService,
        final ProviewAuditService proviewAuditService,
        final ManagerService managerService,
        final MessageSourceAccessor messageSourceAccessor,
        final JobRequestService jobRequestService,
        final GroupService groupService,
        final ProviewTitleListService proviewTitleListService,
        final VersionIsbnService versionIsbnService,
        final EmailUtil emailUtil,
        final EmailService emailService,
        final OutageService outageService,
        @Qualifier("environmentName")
        final String environmentName) {
        this.proviewHandler = proviewHandler;
        this.bookDefinitionService = bookDefinitionService;
        this.proviewAuditService = proviewAuditService;
        this.managerService = managerService;
        this.messageSourceAccessor = messageSourceAccessor;
        this.jobRequestService = jobRequestService;
        this.groupService = groupService;
        this.proviewTitleListService = proviewTitleListService;
        this.versionIsbnService = versionIsbnService;
        this.emailUtil = emailUtil;
        this.emailService = emailService;
        this.outageService = outageService;
        this.environmentName = environmentName;
    }

    private List<ProviewTitleInfo> fetchSelectedProviewTitleInfo(final HttpSession httpSession) {
        return (List<ProviewTitleInfo>) httpSession.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES);
    }

    private void saveSelectedProviewTitleInfo(
        final HttpSession httpSession,
        final List<ProviewTitleInfo> selectedProviewTitleInfo) {
        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, selectedProviewTitleInfo);
    }

    private void saveAllLatestProviewTitleInfo(
        final HttpSession httpSession,
        final List<ProviewTitleInfo> allLatestProviewTitleInfo) {
        httpSession.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES, allLatestProviewTitleInfo);
    }

    private List<ProviewTitleInfo> fetchAllLatestProviewTitleInfo(final HttpSession httpSession) {
        return (List<ProviewTitleInfo>) httpSession.getAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES);
    }

    private Map<String, ProviewTitleContainer> fetchAllProviewTitleInfo(final HttpSession httpSession) {
        return (Map<String, ProviewTitleContainer>) httpSession.getAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES);
    }

    private void saveAllProviewTitleInfo(
        final HttpSession httpSession,
        final Map<String, ProviewTitleContainer> allProviewTitleInfo) {
        httpSession.setAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES, allProviewTitleInfo);
    }

    protected ProviewListFilterForm fetchSavedProviewListFilterForm(final HttpSession httpSession) {
        final ProviewListFilterForm form =
            (ProviewListFilterForm) httpSession.getAttribute(ProviewListFilterForm.FORM_NAME);
        return Optional.ofNullable(form).orElseGet(ProviewListFilterForm::new);
    }

    protected ProviewTitleForm fetchSavedProviewTitleForm(final HttpSession httpSession) {
        return (ProviewTitleForm) httpSession.getAttribute(ProviewTitleForm.FORM_NAME);
    }

    private void saveProviewTitleForm(final HttpSession httpSession, final ProviewTitleForm form) {
        httpSession.setAttribute(ProviewTitleForm.FORM_NAME, form);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLES, method = RequestMethod.POST)
    @ShowOnException(errorViewName = WebConstants.VIEW_PROVIEW_TITLES)
    public ModelAndView postSelections(
        @ModelAttribute final ProviewTitleForm form,
        final HttpSession httpSession,
        final Model model) throws ProviewException {
        model.addAttribute(ProviewTitleForm.FORM_NAME, new ProviewTitleForm());
        model.addAttribute(ProviewListFilterForm.FORM_NAME, new ProviewListFilterForm());
        final Command command = form.getCommand();
        switch (command) {
        case REFRESH:
            final Map<String, ProviewTitleContainer> allProviewTitleInfo = proviewHandler.getAllProviewTitleInfo();
            final List<ProviewTitleInfo> allLatestProviewTitleInfo =
                proviewHandler.getAllLatestProviewTitleInfo(allProviewTitleInfo);
	        updateLatestUpdateDates(allProviewTitleInfo.keySet(), httpSession);
            fillLatestUpdateDatesForTitleInfos(allLatestProviewTitleInfo, httpSession);

            saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
            saveAllLatestProviewTitleInfo(httpSession, allLatestProviewTitleInfo);
            saveSelectedProviewTitleInfo(httpSession, allLatestProviewTitleInfo);

            if (allLatestProviewTitleInfo != null) {
                model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allLatestProviewTitleInfo);
                model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, allLatestProviewTitleInfo.size());
            }

            ProviewTitleForm proviewTitleForm = fetchSavedProviewTitleForm(httpSession);
            if (proviewTitleForm == null) {
                proviewTitleForm = new ProviewTitleForm();
                proviewTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
                saveProviewTitleForm(httpSession, proviewTitleForm);
            }
            model.addAttribute(ProviewTitleForm.FORM_NAME, proviewTitleForm);
            model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewTitleForm.getObjectsPerPage());
            model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
            break;
        case PAGESIZE:
            saveProviewTitleForm(httpSession, form);
            final List<ProviewTitleInfo> selectedProviewTitleInfo = fetchSelectedProviewTitleInfo(httpSession);
            model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewTitleInfo);
            model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, selectedProviewTitleInfo.size());
            model.addAttribute(WebConstants.KEY_PAGE_SIZE, form.getObjectsPerPage());
            model.addAttribute(ProviewListFilterForm.FORM_NAME, fetchSavedProviewListFilterForm(httpSession));
            model.addAttribute(ProviewTitleForm.FORM_NAME, form);
            break;
        default:
            throw new ProviewException(String.format("Unexpected command %s in request %s.", command, WebConstants.MVC_PROVIEW_TITLES));
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DOWNLOAD, method = RequestMethod.GET)
    public void downloadPublishingStatsExcel(final HttpSession httpSession, final HttpServletResponse response) {
        final ProviewListExcelExportService excelExportService = new ProviewListExcelExportService();
        try {
            final Workbook wb = excelExportService.createExcelDocument(httpSession);
            final Date date = new Date();
            final SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
            final String stringDate = s.format(date);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + ProviewListExcelExportService.TITLES_NAME + stringDate + ".xls");
            final ServletOutputStream out = response.getOutputStream();
            wb.write(out);
            out.flush();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLES, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_PROVIEW_TITLES)
    public ModelAndView allLatestProviewTitleInfo(final HttpSession httpSession, final Model model) {
        List<ProviewTitleInfo> selectedProviewTitleInfo = fetchSelectedProviewTitleInfo(httpSession);

        if (selectedProviewTitleInfo == null) {
            List<ProviewTitleInfo> allLatestProviewTitleInfo = fetchAllLatestProviewTitleInfo(httpSession);
            if (allLatestProviewTitleInfo == null) {
                Map<String, ProviewTitleContainer> allProviewTitleInfo = fetchAllProviewTitleInfo(httpSession);
                try {
                    if (allProviewTitleInfo == null) {
                        allProviewTitleInfo = proviewHandler.getAllProviewTitleInfo();
                        updateLatestUpdateDates(allProviewTitleInfo.keySet(), httpSession);
                        saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
                    }

                    allLatestProviewTitleInfo = proviewHandler.getAllLatestProviewTitleInfo(allProviewTitleInfo);
                    fillLatestUpdateDatesForTitleInfos(allLatestProviewTitleInfo, httpSession);
                    saveAllLatestProviewTitleInfo(httpSession, allLatestProviewTitleInfo);

                    selectedProviewTitleInfo = allLatestProviewTitleInfo;

                    saveSelectedProviewTitleInfo(httpSession, selectedProviewTitleInfo);
                } catch (final ProviewException e) {
                    model.addAttribute(WebConstants.KEY_ERROR_OCCURRED, Boolean.TRUE);
                }
            }
        }

        if (selectedProviewTitleInfo != null) {
            model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewTitleInfo);
            model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, selectedProviewTitleInfo.size());
        }

        model.addAttribute(ProviewListFilterForm.FORM_NAME, fetchSavedProviewListFilterForm(httpSession));

        ProviewTitleForm proviewTitleForm = fetchSavedProviewTitleForm(httpSession);
        if (proviewTitleForm == null) {
            proviewTitleForm = new ProviewTitleForm();
            proviewTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
            saveProviewTitleForm(httpSession, proviewTitleForm);
        }

        model.addAttribute(ProviewTitleForm.FORM_NAME, proviewTitleForm);
        model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewTitleForm.getObjectsPerPage());
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());

        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS, method = RequestMethod.GET)
    public ModelAndView singleTitleAllVersions(
        @RequestParam("titleId") final String titleId,
        final HttpSession httpSession,
        final Model model) throws Exception {
        Assert.notNull(titleId);

        Map<String, ProviewTitleContainer> allProviewTitleInfo = fetchAllProviewTitleInfo(httpSession);
        if (allProviewTitleInfo == null) {
            allProviewTitleInfo = proviewHandler.getAllProviewTitleInfo();
            saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
        }

        final BookDefinition bookDef = proviewTitleListService.getBook(new TitleId(titleId));
        final ProviewTitleContainer proviewTitleContainer = allProviewTitleInfo.get(titleId);
        if (proviewTitleContainer != null) {
            final List<ProviewTitleInfo> allTitleVersions = proviewTitleContainer.getProviewTitleInfos();
            final List<ProviewTitle> proviewTitles =
                proviewTitleListService.getProviewTitles(allTitleVersions, bookDef);
            model.addAttribute(WebConstants.KEY_PAGINATED_LIST, proviewTitles);
            model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, proviewTitles.size());
        } else {
            model.addAttribute(WebConstants.KEY_PAGINATED_LIST, Collections.emptyList());
            model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, 0);
        }
        final String infoMessage = getBookLevelMessage(bookDef);
        model.addAttribute(WebConstants.KEY_INFO_MESSAGE, infoMessage);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_ALL_VERSIONS);
    }

    @Nullable
    private String getBookLevelMessage(@Nullable final BookDefinition bookDef) {
        if (bookDef == null) {
            return "Book was not found in DB";
        } else if (bookDef.getPilotBookStatus() == PilotBookStatus.IN_PROGRESS) {
            return "Pilot book marked as 'In Progress' for notes migration. Once the note migration csv file is in place, update the Pilot Book status, and regenerate the book before Promoting.";
        }
        return null;
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DELETE, method = RequestMethod.GET)
    public ModelAndView proviewTitleDelete(
        @RequestParam("titleId") final String titleId,
        @RequestParam("versionNumber") final String versionNumber,
        @RequestParam("status") final String status,
        @RequestParam("lastUpdate") final String lastUpdate,
        final Model model) {
        addDefaultAttributes(titleId, versionNumber, status, lastUpdate, model);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_REMOVE, method = RequestMethod.GET)
    public ModelAndView proviewTitleRemove(
        @RequestParam("titleId") final String titleId,
        @RequestParam("versionNumber") final String versionNumber,
        @RequestParam("status") final String status,
        @RequestParam("lastUpdate") final String lastUpdate,
        final Model model) {
        addDefaultAttributes(titleId, versionNumber, status, lastUpdate, model);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_REMOVE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_MARK_SUPERSEDED, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_TITLE_ID_NOT_FOUND)
    public ModelAndView proviewTitleMarkSuperseded(
        @RequestParam("titleId") final String titleId) throws ProviewException {
        proviewHandler.markTitleSuperseded(titleId);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_MARK_SUPERSEDED_SUCCESS);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_PROMOTE, method = RequestMethod.GET)
    public ModelAndView proviewTitlePromote(
        @RequestParam("titleId") final String titleId,
        @RequestParam("versionNumber") final String versionNumber,
        @RequestParam("status") final String status,
        @RequestParam("lastUpdate") final String lastUpdate,
        final Model model) {
        addDefaultAttributes(titleId, versionNumber, status, lastUpdate, model);
        addGroupInfo(model, titleId);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE);
    }

    private void addDefaultAttributes(
        final String titleId,
        final String versionNumber,
        final String status,
        final String lastUpdate,
        final Model model) {
        model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, versionNumber);
        model.addAttribute(WebConstants.KEY_STATUS, status);
        model.addAttribute(
            WebConstants.KEY_PROVIEW_TITLE_INFO_FORM,
            new ProviewTitleForm(titleId, versionNumber, status, lastUpdate));
    }

    private void addGroupInfo(final Model model, final String titleId) {
        final GroupDefinition group = groupService.getGroupOfTitle(titleId);
        if (group != null) {
            model.addAttribute(WebConstants.KEY_IS_GROUP_FINAL, FINAL_BOOK_STATUS.equalsIgnoreCase(group.getStatus()));
            model.addAttribute(WebConstants.KEY_GROUP_NAME, group.getName());
        }
    }

    private boolean isJobRunningForBook(final Model model, final String titleId, String version) {
        boolean isJobRunning = false;

        if (version.startsWith("v")) {
            version = version.substring(1);
        }
        final BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(titleId);
        if (book != null) {
            if (jobRequestService.isBookInJobRequest(book.getEbookDefinitionId())) {
                final Object[] args = {book.getFullyQualifiedTitleId(), "", "This book is already in the job queue"};
                final String infoMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.fail", args);
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE, infoMessage);
                isJobRunning = true;
            } else {
                final JobExecution runningJobExecution = managerService.findRunningJob(book);
                if (runningJobExecution != null) {
                    final Object[] args =
                        {book.getFullyQualifiedTitleId(), version, runningJobExecution.getId().toString()};
                    final String infoMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.in.progress", args);
                    model.addAttribute(WebConstants.KEY_ERR_MESSAGE, infoMessage);
                    isJobRunning = true;
                }
            }
        }
        return isJobRunning;
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_REMOVE, method = RequestMethod.POST)
    public ModelAndView proviewTitleRemovePost(
        @ModelAttribute(ProviewTitleForm.FORM_NAME) final ProviewTitleForm form,
        final HttpSession httpSession,
        final Model model) {
        final TitleAction action = new TitleAction(
            () -> updateTitleStatusesInProview(form,
                (title) -> removeTitleFromProview(form, title, httpSession), REVIEW_BOOK_STATUS, FINAL_BOOK_STATUS),
            "Proview Remove Request Status: %s %s",
            "removed from Proview.",
            "could not be removed from Proview.",
            "Success: removed from Proview.",
            "Failed to remove from Proview. ");
        executeTitleAction(model, form, action);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_REMOVE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_PROMOTE, method = RequestMethod.POST)
    public ModelAndView proviewTitlePromotePost(
        @ModelAttribute(ProviewTitleForm.FORM_NAME) final ProviewTitleForm form,
        final HttpSession httpSession,
        final Model model) {
        final TitleAction action = new TitleAction(
            () -> updateTitleStatusesInProview(form,
                (title) -> promoteTitleOnProview(form, title, httpSession), REVIEW_BOOK_STATUS),
                "Proview Promote Request Status: %s %s",
                "promoted to Final in Proview.",
                "could not be promoted to Final in Proview.",
                "Success: promoted to Final in Proview.",
                "Failed to promote this version in Proview. ");
        executeTitleAction(model, form, action);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DELETE, method = RequestMethod.POST)
    public ModelAndView proviewTitleDeletePost(
        @ModelAttribute(ProviewTitleForm.FORM_NAME) final ProviewTitleForm form,
        final HttpSession httpSession,
        final Model model) {
        final TitleAction action = new TitleAction(
            () -> updateTitleStatusesInProview(form,
                (title) -> deleteTitleFromProview(form, title, httpSession), REMOVED_BOOK_STATUS, CLEANUP_BOOK_STATUS),
            "Proview Delete Request Status: %s %s",
            "deleted from Proview.",
            "could not be deleted from Proview.",
            "Success: deleted from Proview.",
            "Failed to delete from Proview. ");
        executeTitleAction(model, form, action);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_DELETE);
    }

    @SneakyThrows
    private void executeTitleAction(final Model model, final ProviewTitleForm form, final TitleAction action) {
        final String headTitleId = new TitleId(form.getTitleId()).getHeadTitleId();
        addTitleActionAttributesToModel(model, form);
        if (!isJobRunningForBook(model, headTitleId, form.getVersion())) {
            final TitleActionResult titleActionResult = action.action.call();
            String errorMessage = titleActionResult.getErrorMessage();
            if (errorMessage == null) {
                model.addAttribute(WebConstants.KEY_INFO_MESSAGE, action.attributeSuccess);
                sendSuccessEmail(form, action, headTitleId);
            } else {
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE, action.attributeUnsuccessful + errorMessage);
                sendFailureEmail(form, action, titleActionResult, headTitleId);
            }
            titleActionResult.getUpdatedTitles().forEach(form::createAudit);
        }
    }

    private void addTitleActionAttributesToModel(final Model model, final ProviewTitleForm form) {
        model.addAttribute(WebConstants.KEY_TITLE_ID, form.getTitleId());
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, form.getVersion());
        model.addAttribute(WebConstants.KEY_STATUS, form.getStatus());
        model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM, form);
    }

    private void sendSuccessEmail(final ProviewTitleForm form, final TitleAction action, final String titleId) {
        final String emailBody = String.format(TITLE_ID_S_VERSION_S, titleId, form.getVersion(), action.emailBodySuccess);
        sendEmail(String.format(action.emailSubjectTemplate, SUCCESS, titleId), emailBody);
    }

    private void sendFailureEmail(final ProviewTitleForm form, final TitleAction action,
        final TitleActionResult actionResult, final String titleId) {
        StringBuilder partsInfo = new StringBuilder();
        final List<String> updatedTitles = actionResult.getUpdatedTitles();
        final List<String> titlesToUpdate = actionResult.getTitlesToUpdate();
        if (isHasSeveralParts(updatedTitles, titlesToUpdate)) {
            if (!updatedTitles.isEmpty()) {
                addSuccessfullyUpdatedPartsToEmailBody(partsInfo, updatedTitles);
            }
            addPartsFailedToUpdatedToEmailBody(partsInfo, titlesToUpdate);
        }
        final String emailBody = String
            .format(TITLE_ID_S_VERSION_S, titleId, form.getVersion(), action.emailBodyUnsuccessful);
        sendEmail(String.format(action.emailSubjectTemplate, UNSUCCESSFUL, titleId),
            emailBody + System.lineSeparator() + partsInfo);
    }

    private boolean isHasSeveralParts(final List<String> updatedTitles, final List<String> titlesToUpdate) {
        return updatedTitles.size() + titlesToUpdate.size() > 1;
    }

    private void addSuccessfullyUpdatedPartsToEmailBody(final StringBuilder partsInfo,
        final List<String> updatedTitles) {
        partsInfo.append(System.lineSeparator())
            .append(SUCCESSFULLY_UPDATED)
            .append(System.lineSeparator());
        updatedTitles.sort(Comparator.naturalOrder());
        updatedTitles.forEach(title -> partsInfo.append(title)
            .append(System.lineSeparator()));
    }

    private void addPartsFailedToUpdatedToEmailBody(final StringBuilder partsInfo,
        final List<String> titlesToUpdate) {
        partsInfo.append(System.lineSeparator())
            .append(FAILED_TO_UPDATE)
            .append(System.lineSeparator());
        titlesToUpdate.sort(Comparator.naturalOrder());
        titlesToUpdate.forEach(title -> partsInfo.append(title)
            .append(System.lineSeparator()));
    }

    private void sendEmail(final String subject, final String body) {
        final Collection<InternetAddress> emails =
            emailUtil.getEmailRecipientsByUsername(UserUtils.getAuthenticatedUserName());
        emailService
            .send(new NotificationEmail(emails, subject, String.format(EMAIL_BODY, environmentName, body)));
    }

    @SneakyThrows
    private TitleActionResult updateTitleStatusesInProview(final ProviewTitleForm form, final Consumer<String> action,
        final String... titleStatuses) {
        final TitleActionResult actionResult = new TitleActionResult(new ArrayList<>(), new ArrayList<>());
        final String headTitleId = new TitleId(form.getTitleId()).getHeadTitleId();
        final List<String> titleIds = proviewTitleListService.getAllSplitBookTitleIdsOnProview(headTitleId,
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
    private void promoteTitleOnProview(final ProviewTitleForm form,  final String title, final HttpSession httpSession) {
        if (proviewHandler.promoteTitle(title, form.getVersion())) {
            changeStatusForTitle(title, form.getVersion(), httpSession, FINAL_BOOK_STATUS);
        }
    }

    @SneakyThrows
    private void removeTitleFromProview(final ProviewTitleForm form, final String title, final HttpSession httpSession) {
        if (proviewHandler.removeTitle(title, new Version(form.getVersion()))) {
            changeStatusForTitle(title, form.getVersion(), httpSession, REMOVED_BOOK_STATUS);
        }
    }

    @SneakyThrows
    private void deleteTitleFromProview(final ProviewTitleForm form, final String title, final HttpSession httpSession) {
        final String version = form.getVersion();
        if (proviewHandler.deleteTitle(title, new Version(version))) {
            updateProviewTitleInfo(title, version, httpSession);
            final String headTitle = new TitleId(title).getHeadTitleId();
            if (headTitle.equals(title)) {
                versionIsbnService.deleteIsbn(headTitle, version);
            }
        }
    }

    private void updateProviewTitleInfo(final String title, final String version, final HttpSession httpSession) {
        final Map<String, ProviewTitleContainer> proviewTitleContainerMap =
            updateAllProviewTitleInfo(title, version, httpSession);
        updateSelectedProviewTitleInfo(proviewTitleContainerMap, title, version, httpSession);
        updateLatestProviewTitleInfo(proviewTitleContainerMap, httpSession);
        saveAllProviewTitleInfo(httpSession, proviewTitleContainerMap);
    }

    private Map<String, ProviewTitleContainer> updateAllProviewTitleInfo(final String title, final String version,
        final HttpSession httpSession) {
        final Map<String, ProviewTitleContainer> proviewTitleContainerMap = fetchAllProviewTitleInfo(httpSession);
        proviewTitleContainerMap.computeIfPresent(title, (key, value) -> {
            value.getProviewTitleInfos().removeIf(item -> item.getVersion().equals(version));
            return value;
        });
        if (proviewTitleContainerMap.containsKey(title) && CollectionUtils.isEmpty(
            proviewTitleContainerMap.get(title).getProviewTitleInfos())) {
            proviewTitleContainerMap.remove(title);
        }
        return proviewTitleContainerMap;
    }

    private void updateSelectedProviewTitleInfo(final Map<String, ProviewTitleContainer> proviewTitleContainerMap,
        final String title, final String version, final HttpSession httpSession) {
        final List<ProviewTitleInfo> selectedProviewTitleInfo = fetchSelectedProviewTitleInfo(httpSession);
        if (selectedProviewTitleInfo.removeIf(item -> item.getTitleId().equals(title) && item.getVersion().equals(version))) {
            if (proviewTitleContainerMap.containsKey(title)) {
                final ProviewTitleInfo updatedLatestVersion = proviewTitleContainerMap.get(title).getLatestVersion();
                selectedProviewTitleInfo.add(updatedLatestVersion);
            }
        }
        saveSelectedProviewTitleInfo(httpSession, selectedProviewTitleInfo);
    }

    @SneakyThrows
    private void updateLatestProviewTitleInfo(final Map<String, ProviewTitleContainer> proviewTitleContainerMap,
        final HttpSession httpSession) {
        final List<ProviewTitleInfo> latestProviewTitleInfo = proviewHandler.getAllLatestProviewTitleInfo(proviewTitleContainerMap);
        saveAllLatestProviewTitleInfo(httpSession, latestProviewTitleInfo);
    }

    @SneakyThrows
    private void changeStatusForTitle(final String titleId, final String version, final HttpSession httpSession, final String updatedStatus) {
        final Map<String, ProviewTitleContainer> proviewTitleContainerMap = fetchAllProviewTitleInfo(httpSession);
        proviewTitleContainerMap.computeIfPresent(titleId, (key, value) -> {
            value.getProviewTitleInfos().stream()
                .filter(item -> item.getVersion().equals(version))
                .forEach(item -> item.setStatus(updatedStatus));
            return value;
        });
        final List<ProviewTitleInfo> latestProviewTitleInfo =
            proviewHandler.getAllLatestProviewTitleInfo(proviewTitleContainerMap);
        final List<ProviewTitleInfo> selectedProviewTitleInfo = fetchSelectedProviewTitleInfo(httpSession);
        selectedProviewTitleInfo.stream()
            .filter(item -> item.getTitleId().equals(titleId))
            .filter(item -> item.getVersion().equals(version))
            .forEach(item -> item.setStatus(updatedStatus));
        saveAllLatestProviewTitleInfo(httpSession, latestProviewTitleInfo);
        saveAllProviewTitleInfo(httpSession, proviewTitleContainerMap);
        saveSelectedProviewTitleInfo(httpSession, selectedProviewTitleInfo);
    }

    private void fillLatestUpdateDatesForTitleInfos(final List<ProviewTitleInfo> titleInfos, final HttpSession session) {
        final Map<String, Date> latestUpdateDates = Optional.ofNullable(session.getAttribute(WebConstants.KEY_LATEST_UPDATE_DATES_TITLE))
            .map(attribute -> (Map<String, Date>) attribute)
            .orElseGet(Collections::emptyMap);

        Optional.ofNullable(titleInfos)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .forEach(titleInfo -> titleInfo.setLastStatusUpdateDate(
                mapDateToString(latestUpdateDates.get(titleInfo.getTitleId()))));
    }

    private String mapDateToString(final Date date) {
        return Optional.ofNullable(date)
            .map(nonNullDate -> DateFormatUtils.format(nonNullDate, "yyyyMMdd"))
            .orElse(null);
    }

    private void updateLatestUpdateDates(final Collection<String> titleIds, final HttpSession session) {
        final Map<String, Date> latestUpdateDates = Optional.ofNullable(titleIds)
            .filter(CollectionUtils::isNotEmpty)
            .map(proviewAuditService::findMaxRequestDateByTitleIds)
            .orElseGet(Collections::emptyMap);
        session.setAttribute(WebConstants.KEY_LATEST_UPDATE_DATES_TITLE, latestUpdateDates);
    }

    @Data
    private class TitleAction {
        private final Callable<TitleActionResult> action;
        private final String emailSubjectTemplate;
        private final String emailBodySuccess;
        private final String emailBodyUnsuccessful;
        private final String attributeSuccess;
        private final String attributeUnsuccessful;
    }

    @Data
    private class TitleActionResult {
        @NonNull
        private List<String> titlesToUpdate;
        @NonNull
        private List<String> updatedTitles;
        private String errorMessage;
    }
}
