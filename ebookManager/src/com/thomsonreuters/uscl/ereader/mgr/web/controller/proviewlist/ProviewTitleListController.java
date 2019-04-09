package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static com.thomsonreuters.uscl.ereader.mgr.web.controller.ControllerUtils.handleRequest;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.Data;
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
    private static final String PROVIEW_ERROR_MESSAGE = "Proview Exception occured. Please contact your administrator.";
    private static final String TITLE_ID_S_VERSION_S = "Title id: %s, version: %s %s";
    private static final String SUCCESS = "Success";
    private static final String UNSUCCESSFUL = "Unsuccessful";

    private final ProviewHandler proviewHandler;
    private final BookDefinitionService bookDefinitionService;
    private final ProviewAuditService proviewAuditService;
    private final ManagerService managerService;
    private final MessageSourceAccessor messageSourceAccessor;
    private final JobRequestService jobRequestService;
    private final ProviewTitleListService proviewTitleListService;
    private final OutageService outageService;
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
        final ProviewTitleListService proviewTitleListService,
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
        this.proviewTitleListService = proviewTitleListService;
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
    public ModelAndView postSelections(
        @ModelAttribute final ProviewTitleForm form,
        final HttpSession httpSession,
        final Model model) {
        return handleRequest(() -> {
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

                model.addAttribute(ProviewListFilterForm.FORM_NAME, new ProviewListFilterForm());

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
        }, () -> {
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, PROVIEW_ERROR_MESSAGE);
            model.addAttribute(ProviewTitleForm.FORM_NAME, new ProviewTitleForm());
            model.addAttribute(ProviewListFilterForm.FORM_NAME, new ProviewListFilterForm());
        }, WebConstants.VIEW_PROVIEW_TITLES);
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
                    model.addAttribute(WebConstants.KEY_ERR_MESSAGE, PROVIEW_ERROR_MESSAGE);
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

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_PROMOTE, method = RequestMethod.GET)
    public ModelAndView proviewTitlePromote(
        @RequestParam("titleId") final String titleId,
        @RequestParam("versionNumber") final String versionNumber,
        @RequestParam("status") final String status,
        @RequestParam("lastUpdate") final String lastUpdate,
        final Model model) {
        addDefaultAttributes(titleId, versionNumber, status, lastUpdate, model);
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
            () -> removeTitle(form, httpSession),
            "Proview Remove Request Status: %s",
            "removed from Proview.",
            "could not be removed from Proview.",
            "Success: removed from Proview.",
            "Failed to remove from Proview. ");
        sendEmailAndAudit(model, form, action);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_REMOVE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_PROMOTE, method = RequestMethod.POST)
    public ModelAndView proviewTitlePromotePost(
        @ModelAttribute(ProviewTitleForm.FORM_NAME) final ProviewTitleForm form,
        final HttpSession httpSession,
        final Model model) {
        final TitleAction action = new TitleAction(
            () -> promoteTitle(form, httpSession),
            "Proview Promote Request Status: %s",
            "promoted to Final in Proview.",
            "could not be promoted to Final in Proview.",
            "Success: promoted to Final in Proview.",
            "Failed to promote this version in Proview. ");
        sendEmailAndAudit(model, form, action);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DELETE, method = RequestMethod.POST)
    public ModelAndView proviewTitleDeletePost(
        @ModelAttribute(ProviewTitleForm.FORM_NAME) final ProviewTitleForm form,
        final HttpSession httpSession,
        final Model model) {
        final TitleAction action = new TitleAction(
            () -> deleteTitle(form, httpSession),
            "Proview Delete Request Status: %s",
            "deleted from Proview.",
            "could not be deleted from Proview.",
            "Success: deleted from Proview.",
            "Failed to delete from Proview. ");
        sendEmailAndAudit(model, form, action);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_DELETE);
    }

    private void sendEmailAndAudit(final Model model, final ProviewTitleForm form, final TitleAction action) {
        model.addAttribute(WebConstants.KEY_TITLE_ID, form.getTitleId());
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, form.getVersion());
        model.addAttribute(WebConstants.KEY_STATUS, form.getStatus());
        model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM, form);
        try {
            if (!isJobRunningForBook(model, form.getTitleId(), form.getVersion())) {
                action.action.run();
                model.addAttribute(WebConstants.KEY_INFO_MESSAGE, action.attributeSuccess);
                final String emailBody =
                    String.format(TITLE_ID_S_VERSION_S, form.getTitleId(), form.getVersion(), action.emailBodySuccess);
                sendEmail(String.format(action.emailSubjectTemplate, SUCCESS), emailBody);
                proviewAuditService.save(form.createAudit());
            }
        } catch (final Exception e) {
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, action.attributeUnsuccessful + e.getMessage());
            final String emailBody =
                String.format(TITLE_ID_S_VERSION_S, form.getTitleId(), form.getVersion(), action.emailBodyUnsuccessful);
            sendEmail(String.format(action.emailSubjectTemplate, UNSUCCESSFUL), emailBody);
            log.error(e.getMessage(), e);
        }
    }

    private void sendEmail(final String subject, final String body) {
        final Collection<InternetAddress> emails =
            emailUtil.getEmailRecipientsByUsername(UserUtils.getAuthenticatedUserName());
        emailService
            .send(new NotificationEmail(emails, subject, String.format("Environment: %s%n%s", environmentName, body)));
    }

    @SneakyThrows
    private void promoteTitle(final ProviewTitleForm form, final HttpSession httpSession) {
        if (proviewHandler.promoteTitle(form.getTitleId(), form.getVersion())) {
            changeStatusForTitle(form, httpSession, "Final");
        }
    }

    @SneakyThrows
    private void removeTitle(final ProviewTitleForm form, final HttpSession httpSession) {
        if (proviewHandler.removeTitle(form.getTitleId(), new Version(form.getVersion()))) {
            changeStatusForTitle(form, httpSession, "Removed");
        }
    }

    @SneakyThrows
    private void deleteTitle(final ProviewTitleForm form, final HttpSession httpSession) {
        if (proviewHandler.deleteTitle(form.getTitleId(), new Version(form.getVersion()))) {
            final Map<String, ProviewTitleContainer> proviewTitleContainerMap = fetchAllProviewTitleInfo(httpSession);
            proviewTitleContainerMap.computeIfPresent(form.getTitleId(), (key, value) -> {
                value.getProviewTitleInfos().removeIf(item -> item.getVersion().equals(form.getVersion()));
                return value;
            });
            if (proviewTitleContainerMap.containsKey(form.getTitleId())
                && CollectionUtils.isEmpty(proviewTitleContainerMap.get(form.getTitleId()).getProviewTitleInfos())) {
                proviewTitleContainerMap.remove(form.getTitleId());
            }
            final List<ProviewTitleInfo> latestProviewTitleInfo =
                proviewHandler.getAllLatestProviewTitleInfo(proviewTitleContainerMap);
            final List<ProviewTitleInfo> selectedProviewTitleInfo = fetchSelectedProviewTitleInfo(httpSession);

            if (selectedProviewTitleInfo.removeIf(item ->
                item.getTitleId().equals(form.getTitleId()) && item.getVersion().equals(form.getVersion()))) {
                if (proviewTitleContainerMap.containsKey(form.getTitleId())) {
                    final ProviewTitleInfo updatedLatestVersion =
                        proviewTitleContainerMap.get(form.getTitleId()).getLatestVersion();
                    selectedProviewTitleInfo.add(updatedLatestVersion);
                }
            }
            saveAllLatestProviewTitleInfo(httpSession, latestProviewTitleInfo);
            saveSelectedProviewTitleInfo(httpSession, selectedProviewTitleInfo);
            saveAllProviewTitleInfo(httpSession, proviewTitleContainerMap);
        }
    }

    @SneakyThrows
    private void changeStatusForTitle(final ProviewTitleForm form, final HttpSession httpSession, final String updatedStatus) {
        final Map<String, ProviewTitleContainer> proviewTitleContainerMap = fetchAllProviewTitleInfo(httpSession);
        proviewTitleContainerMap.computeIfPresent(form.getTitleId(), (key, value) -> {
            value.getProviewTitleInfos().stream()
                .filter(item -> item.getVersion().equals(form.getVersion()))
                .forEach(item -> item.setStatus(updatedStatus));
            return value;
        });
        final List<ProviewTitleInfo> latestProviewTitleInfo =
            proviewHandler.getAllLatestProviewTitleInfo(proviewTitleContainerMap);
        final List<ProviewTitleInfo> selectedProviewTitleInfo = fetchSelectedProviewTitleInfo(httpSession);
        selectedProviewTitleInfo.stream()
            .filter(item -> item.getTitleId().equals(form.getTitleId()))
            .filter(item -> item.getVersion().equals(form.getVersion()))
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
        private final Runnable action;
        private final String emailSubjectTemplate;
        private final String emailBodySuccess;
        private final String emailBodyUnsuccessful;
        private final String attributeSuccess;
        private final String attributeUnsuccessful;
    }
}
