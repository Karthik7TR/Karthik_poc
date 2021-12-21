package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.CurrentSessionUserPreferences;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.CLEANUP_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.FINAL_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REMOVED_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REVIEW_BOOK_STATUS;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProviewTitleListController {
    private final BookDefinitionService bookDefinitionService;
    private final ManagerService managerService;
    private final MessageSourceAccessor messageSourceAccessor;
    private final JobRequestService jobRequestService;
    private final GroupService groupService;
    private final ProviewTitleListService proviewTitleListService;
    private final OutageService outageService;
    private final VersionIsbnService versionIsbnService;

    @InitBinder(ProviewListFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    private List<ProviewTitleInfo> fetchSelectedProviewTitleInfo(final HttpSession httpSession) {
        return (List<ProviewTitleInfo>) httpSession.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES);
    }

    private void saveSelectedProviewTitleInfo(
        final HttpSession httpSession,
        final List<ProviewTitleInfo> selectedProviewTitleInfo) {
        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, selectedProviewTitleInfo);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLES, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_PROVIEW_TITLES)
    public ModelAndView getSelections(@ModelAttribute final ProviewListFilterForm form,
            final BindingResult bindingResult, final HttpSession httpSession, final Model model)
            throws ProviewException {
        if (bindingResult.hasErrors()) {
            log.error("Binding errors on Proview List page:\n" + bindingResult.getAllErrors().toString());
        }
        if (form.getObjectsPerPage() == null) {
            form.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
        }
        updateUserPreferencesForCurrentSession(form, httpSession);
        model.addAttribute(WebConstants.KEY_PAGE_SIZE, form.getObjectsPerPage());
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
        List<ProviewTitleInfo> selectedProviewTitleInfo = Collections.emptyList();
        try {
            selectedProviewTitleInfo = proviewTitleListService.getSelectedProviewTitleInfo(form);
        } catch (final ProviewException e) {
            log.warn(e.getMessage(), e);
            model.addAttribute(WebConstants.KEY_ERROR_OCCURRED, Boolean.TRUE);
        }
        saveSelectedProviewTitleInfo(httpSession, selectedProviewTitleInfo); // required for Excel Export Service
        model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewTitleInfo);
        model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, selectedProviewTitleInfo.size());
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES);
    }

    private void updateUserPreferencesForCurrentSession(@NotNull final ProviewListFilterForm form,
            @NotNull final HttpSession httpSession) {
        final Object preferencesSessionAttribute = httpSession.getAttribute(CurrentSessionUserPreferences.NAME);
        if (preferencesSessionAttribute instanceof CurrentSessionUserPreferences) {
            final CurrentSessionUserPreferences sessionPreferences =
                    (CurrentSessionUserPreferences) preferencesSessionAttribute;
            sessionPreferences.setProviewDisplayName(form.getProviewDisplayName());
            sessionPreferences.setTitleId(form.getTitleId());
            sessionPreferences.setMinVersions(form.getMinVersions());
            sessionPreferences.setMaxVersions(form.getMaxVersions());
            sessionPreferences.setProviewListObjectsPerPage(form.getObjectsPerPage());
            sessionPreferences.setStatus(form.getStatus());
        }
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DOWNLOAD, method = RequestMethod.GET)
    public void downloadPublishingStatsExcel(final HttpSession httpSession, final HttpServletResponse response) {
        final ProviewListExcelExportService excelExportService = new ProviewListExcelExportService();
        try (final Workbook wb = excelExportService.createExcelDocument(httpSession)) {
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

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS, method = RequestMethod.GET)
    public ModelAndView singleTitleAllVersions(@RequestParam("titleId") final String titleId, final Model model)
            throws ProviewException {
        Assert.notNull(titleId);
        final Map<String, ProviewTitleContainer> allProviewTitleInfo =
                proviewTitleListService.getAllProviewTitleInfo(false);
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
        addDefaultAttributes(titleId, versionNumber, status, lastUpdate, model, proviewTitleListService::canDelete);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_REMOVE, method = RequestMethod.GET)
    public ModelAndView proviewTitleRemove(
        @RequestParam("titleId") final String titleId,
        @RequestParam("versionNumber") final String versionNumber,
        @RequestParam("status") final String status,
        @RequestParam("lastUpdate") final String lastUpdate,
        final Model model) {
        addDefaultAttributes(titleId, versionNumber, status, lastUpdate, model, proviewTitleListService::canRemove);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_REMOVE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_MARK_SUPERSEDED, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_TITLE_ID_NOT_FOUND)
    public ModelAndView proviewTitleMarkSuperseded(@RequestParam("titleId") final String titleId)
            throws ProviewException {
        proviewTitleListService.markTitleSuperseded(titleId);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_MARK_SUPERSEDED_SUCCESS);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_PROMOTE, method = RequestMethod.GET)
    public ModelAndView proviewTitlePromote(
        @RequestParam("titleId") final String titleId,
        @RequestParam("versionNumber") final String versionNumber,
        @RequestParam("status") final String status,
        @RequestParam("lastUpdate") final String lastUpdate,
        final Model model) {
        addDefaultAttributes(titleId, versionNumber, status, lastUpdate, model, proviewTitleListService::canPromote);
        addGroupInfo(model, titleId);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE);
    }

    private void addDefaultAttributes(
        final String titleId,
        final String versionNumber,
        final String status,
        final String lastUpdate,
        final Model model,
        final Function<String, Boolean> isOperationAllowed) {
        model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, versionNumber);
        model.addAttribute(WebConstants.KEY_STATUS, status);
        model.addAttribute(WebConstants.KEY_IS_OPERATION_ALLOWED, isOperationAllowed.apply(status));
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
        final TitleAction action = new TitleAction(TitleActionName.REMOVE,
            () -> proviewTitleListService.updateTitleStatusesInProview(form,
                (title) -> proviewTitleListService.removeTitleFromProview(form, title),
                REVIEW_BOOK_STATUS, FINAL_BOOK_STATUS),
            "Proview Remove Request Status: %s %s",
            "removed from Proview.",
            "could not be removed from Proview.",
            "Success: removed from Proview.",
            "Failed to remove from Proview. ");
        executeTitleAction(model, form, action, httpSession);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_REMOVE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_PROMOTE, method = RequestMethod.POST)
    public ModelAndView proviewTitlePromotePost(
        @ModelAttribute(ProviewTitleForm.FORM_NAME) final ProviewTitleForm form,
        final HttpSession httpSession,
        final Model model) {
        final TitleAction action = new TitleAction(TitleActionName.PROMOTE,
            () -> proviewTitleListService.updateTitleStatusesInProview(form,
                (title) -> proviewTitleListService.promoteTitleOnProview(form, title),
                REVIEW_BOOK_STATUS),
                "Proview Promote Request Status: %s %s",
                "promoted to Final in Proview.",
                "could not be promoted to Final in Proview.",
                "Success: promoted to Final in Proview.",
                "Failed to promote this version in Proview. ");
        executeTitleAction(model, form, action, httpSession);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DELETE, method = RequestMethod.POST)
    public ModelAndView proviewTitleDeletePost(
        @ModelAttribute(ProviewTitleForm.FORM_NAME) final ProviewTitleForm form,
        final HttpSession httpSession,
        final Model model) {
        final TitleAction action = new TitleAction(TitleActionName.DELETE,
            () -> proviewTitleListService.updateTitleStatusesInProview(form,
                (title) -> proviewTitleListService.deleteTitleFromProview(form, title),
                REMOVED_BOOK_STATUS, CLEANUP_BOOK_STATUS),
            "Proview Delete Request Status: %s %s",
            "deleted from Proview.",
            "could not be deleted from Proview.",
            "Success: deleted from Proview.",
            "Failed to delete from Proview. ");
        executeTitleAction(model, form, action, httpSession);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_DELETE);
    }

    private void executeTitleAction(final Model model, final ProviewTitleForm form,
        final TitleAction action, final HttpSession httpSession) {
        final String headTitleId = new TitleId(form.getTitleId()).getHeadTitleId();
        final String version = form.getVersion();
        final String previousStatus = form.getStatus();
        boolean isJobRunningForBook = isJobRunningForBook(model, headTitleId, version);
        addTitleActionAttributesToModel(model, form);
        TitleActionResult titleActionResult = proviewTitleListService.executeTitleAction(form, action,
            isJobRunningForBook);
        if (!isJobRunningForBook) {
            if (titleActionResult.hasErrorMessage()) {
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
                    action.getAttributeUnsuccessful() + titleActionResult.getErrorMessage());
            } else {
                model.addAttribute(WebConstants.KEY_INFO_MESSAGE, action.getAttributeSuccess());
            }
            try {
                updateTitleStatusesAfterAction(headTitleId, action, previousStatus, titleActionResult, version, httpSession);
            } catch (final ProviewException e) {
                log.warn(e.getMessage(), e);
                model.addAttribute(WebConstants.KEY_ERROR_OCCURRED, Boolean.TRUE);
            }
        }
    }

    private void addTitleActionAttributesToModel(final Model model, final ProviewTitleForm form) {
        model.addAttribute(WebConstants.KEY_TITLE_ID, form.getTitleId());
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, form.getVersion());
        model.addAttribute(WebConstants.KEY_STATUS, form.getStatus());
        model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM, form);
    }

    private void updateTitleStatusesAfterAction(final String headTitleId, final TitleAction action,
        final String previousStatus, final TitleActionResult actionResult, final String titleVersion,
        final HttpSession httpSession) throws ProviewException {
        TitleActionName actionName = action.getActionName();
        OperationResult operationResult = actionResult.getOperationResult();
        if (TitleActionName.PROMOTE == actionName || TitleActionName.REMOVE == actionName) {
            changeStatusForTitle(headTitleId, titleVersion, httpSession, actionName.getStatus(operationResult, previousStatus));
        } else if (TitleActionName.DELETE == actionName) {
            if (operationResult != OperationResult.SUCCESSFUL) {
                changeStatusForTitle(headTitleId, titleVersion, httpSession, actionName.getStatus(operationResult, previousStatus));
            } else {
                updateProviewTitleInfo(headTitleId, titleVersion, httpSession);
                versionIsbnService.deleteIsbn(headTitleId, titleVersion);
            }
        }
    }

    private void updateProviewTitleInfo(final String title, final String version, final HttpSession httpSession)
            throws ProviewException {
        final Map<String, ProviewTitleContainer> proviewTitleContainerMap = updateAllProviewTitleInfo(title, version);
        updateSelectedProviewTitleInfo(proviewTitleContainerMap, title, version, httpSession);
    }

    private Map<String, ProviewTitleContainer> updateAllProviewTitleInfo(final String title, final String version)
            throws ProviewException {
        final Map<String, ProviewTitleContainer> proviewTitleContainerMap =
                proviewTitleListService.getAllProviewTitleInfo(false);
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
        final boolean isAnyRemoved = selectedProviewTitleInfo.removeIf(item -> item.getTitleId().equals(title) && item.getVersion().equals(version));
        if (isAnyRemoved && proviewTitleContainerMap.containsKey(title)) {
            final ProviewTitleInfo updatedLatestVersion = proviewTitleContainerMap.get(title).getLatestVersion();
            selectedProviewTitleInfo.add(updatedLatestVersion);
        }
        saveSelectedProviewTitleInfo(httpSession, selectedProviewTitleInfo);
    }

    @SneakyThrows
    private void changeStatusForTitle(final String titleId, final String version, final HttpSession httpSession,
            final String updatedStatus) {
        final Map<String, ProviewTitleContainer> proviewTitleContainerMap =
                proviewTitleListService.getAllProviewTitleInfo(false);
        proviewTitleContainerMap.computeIfPresent(titleId, (key, value) -> {
            value.getProviewTitleInfos().stream()
                .filter(item -> item.getVersion().equals(version))
                .forEach(item -> item.setStatus(updatedStatus));
            return value;
        });
        final List<ProviewTitleInfo> selectedProviewTitleInfo = fetchSelectedProviewTitleInfo(httpSession);
        selectedProviewTitleInfo.stream()
            .filter(item -> item.getTitleId().equals(titleId))
            .filter(item -> item.getVersion().equals(version))
            .forEach(item -> item.setStatus(updatedStatus));
        saveSelectedProviewTitleInfo(httpSession, selectedProviewTitleInfo);
    }
}
