package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.GroupCmd;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.CurrentSessionUserPreferences;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class ProviewGroupListController {
    private static final String FINAL = "Final";
    private static final String PROMOTE = "Promote";
    private static final String DELETE = "Delete";
    private static final String REMOVE = "Remove";
    private static final String UNSUCCESSFUL = "Unsuccessful";
    private static final int MAX_NUMBER_OF_RETRIES = 3;
    private static final String BRACKETS_OR_BRACES_REGEX = "[\\[\\]{}]";

    @Autowired
    private ProviewGroupListService proviewGroupListService;
    @Autowired
    private ProviewHandler proviewHandler;
    @Autowired
    private AllProviewGroupsProvider allProviewGroupsProvider;
    @Autowired
    private BookDefinitionService bookDefinitionService;
    @Autowired
    private ProviewAuditService proviewAuditService;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OutageService outageService;
    @Autowired
    @Qualifier("proviewGroupValidator")
    private Validator validator;
    @Autowired
    @Qualifier("environmentName")
    private String environmentName;

    @InitBinder(ProviewGroupListFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.setValidator(validator);
    }

    /**
     * /ebookManager/proviewGroups.mvc
     *
     * @param form
     * @param bindingResult
     * @param httpSession
     * @param model
     * @return
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUPS, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_PROVIEW_GROUPS)
    public ModelAndView getSelectionsForGroups(
        @ModelAttribute(ProviewGroupForm.FORM_NAME) final ProviewGroupForm form,
        final BindingResult bindingResult,
        final HttpSession httpSession,
        final Model model) {
        if (bindingResult.hasErrors()) {
            log.debug("Binding errors on Group List page:\n" + bindingResult.getAllErrors().toString());
        }
        if (form.getObjectsPerPage() == null) {
            form.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
        }
        updateUserPreferencesForCurrentSession(form, httpSession);

        ProviewGroupsContainer container;
        try {
            container = proviewGroupListService.getProviewGroups(form, fetchAllLatestProviewGroups(httpSession));
        } catch (final ProviewException e) {
            log.warn(e.getMessage(), e);
            model.addAttribute(WebConstants.KEY_ERROR_OCCURRED, Boolean.TRUE);
            container = ProviewGroupsContainer.initEmpty();
        }

        saveAllLatestProviewGroups(httpSession, container.getAllLatestProviewGroups());
        saveSelectedProviewGroups(httpSession, container.getSelectedProviewGroups()); // required for ProviewGroupExcelExportService
        model.addAttribute(WebConstants.KEY_PAGINATED_LIST, container.getSelectedProviewGroups());
        model.addAttribute(WebConstants.KEY_PAGE_SIZE, form.getObjectsPerPage());
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());

        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUPS);
    }

    private void updateUserPreferencesForCurrentSession(
        @NotNull final ProviewGroupForm form,
        @NotNull final HttpSession httpSession) {
        final Object preferencesSessionAttribute = httpSession.getAttribute(CurrentSessionUserPreferences.NAME);
        if (preferencesSessionAttribute instanceof CurrentSessionUserPreferences) {
            final CurrentSessionUserPreferences sessionPreferences =
                (CurrentSessionUserPreferences) preferencesSessionAttribute;
            sessionPreferences.setGroupFilterName(form.getGroupFilterName());
            sessionPreferences.setGroupFilterId(form.getGroupFilterId());
        }
    }

    private void saveSelectedProviewGroups(
        final HttpSession httpSession,
        final List<ProviewGroup> selectedProviewGroupList) {
        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, selectedProviewGroupList);
    }

    private void saveAllLatestProviewGroups(
        final HttpSession httpSession,
        final List<ProviewGroup> allLatestProviewGroups) {
        httpSession.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS, allLatestProviewGroups);
    }

    private List<ProviewGroup> fetchAllLatestProviewGroups(final HttpSession httpSession) {
        final List<ProviewGroup> allLatestProviewGroupList =
            (List<ProviewGroup>) httpSession.getAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS);
        return allLatestProviewGroupList;
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_DOWNLOAD, method = RequestMethod.GET)
    public void downloadProviewGroupExcel(final HttpSession httpSession, final HttpServletResponse response) {
        final ProviewGroupExcelExportService excelExportService = new ProviewGroupExcelExportService();
        try (final Workbook wb = excelExportService.createExcelDocument(httpSession)) {
            final Date date = new Date();
            final SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
            final String stringDate = s.format(date);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + ProviewGroupExcelExportService.GROUPS_NAME + stringDate + ".xls");
            final ServletOutputStream out = response.getOutputStream();
            wb.write(out);
            out.flush();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * /ebookManager/proviewGroupAllVersions.mvc?groupIds=<groupID>
     *
     * @param groupId
     * @param model
     * @return
     * @throws ProviewException
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_ALL_VERSIONS, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_PROVIEW_GROUP_ALL_VERSIONS)
    public ModelAndView singleGroupAllVersions(@RequestParam("groupIds") final String groupId, final Model model)
            throws ProviewException {
        final Map<String, ProviewGroupContainer> allProviewGroups = allProviewGroupsProvider.getAllProviewGroups(false);
        final ProviewGroupContainer proviewGroupContainer = allProviewGroups.get(groupId);
        int bookSize = 0;
        if (proviewGroupContainer != null) {
            final List<ProviewGroup> allGroupVersions = proviewGroupContainer.getProviewGroups();
            if (allGroupVersions != null) {
                Collections.sort(allGroupVersions);
                model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allGroupVersions);
                bookSize = allGroupVersions.size();
            }
        } else {
            addModelAttributeBooksWereDeleted(model);
        }
        model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, bookSize);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_ALL_VERSIONS);
    }

    private void addModelAttributeBooksWereDeleted(final Model model) {
        model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, "Books were deleted from Proview");
    }

    /**
     * /ebookManager/proviewGroupSingleVersion.mvc?groupIdByVersion= <groupIDsbyVersion>
     *
     * @param groupIdByVersion
     * @param httpSession
     * @param model
     * @param form
     * @return
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_SINGLE_VERSION, method = RequestMethod.GET)
    public ModelAndView singleGroupTitleSingleVersion(
        @RequestParam(WebConstants.KEY_GROUP_BY_VERSION_ID) final String groupIdByVersion,
        final HttpSession httpSession,
        final Model model,
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) final ProviewGroupListFilterForm form) {
        try {
            final String groupId = StringUtils.substringBeforeLast(groupIdByVersion, "/v");
            final String version = StringUtils.substringAfterLast(groupIdByVersion, "/v");
            form.setProviewGroupID(groupId);
            form.setGroupVersion(version);

            final Map<String, ProviewGroupContainer> allProviewGroups =
                    allProviewGroupsProvider.getAllProviewGroups(false);
            final ProviewGroupContainer proviewGroupContainer = allProviewGroups.get(groupId);
            if (proviewGroupContainer != null) {
                final ProviewGroup proviewGroup = proviewGroupContainer.getGroupByVersion(version);

                model.addAttribute(WebConstants.KEY_GROUP_NAME, proviewGroup.getGroupName());
                model.addAttribute(WebConstants.KEY_HEAD_TITLE, proviewGroup.getHeadTitle());
                model.addAttribute(WebConstants.KEY_GROUP_STATUS, proviewGroup.getGroupStatus());
                model.addAttribute(WebConstants.KEY_GROUP_VERSION, version);
                model.addAttribute(WebConstants.KEY_PROVIEW_GROUP_ID, groupId);
                model.addAttribute(WebConstants.KEY_GROUP_BY_VERSION_ID, groupIdByVersion);

                httpSession.setAttribute(WebConstants.KEY_GROUP_NAME, proviewGroup.getGroupName());
                httpSession.setAttribute(WebConstants.KEY_HEAD_TITLE, proviewGroup.getHeadTitle());
                httpSession.setAttribute(WebConstants.KEY_GROUP_STATUS, proviewGroup.getGroupStatus());
                httpSession.setAttribute(WebConstants.KEY_PROVIEW_GROUP_ID, groupId);
                httpSession.setAttribute(WebConstants.KEY_GROUP_BY_VERSION_ID, groupIdByVersion);
                httpSession.setAttribute(WebConstants.KEY_GROUP_VERSION, version);

                setShowSubgroupModelAttribute(httpSession, model, proviewGroup);
                addPilotBookStatusAttribute(model, proviewGroup);
            } else {
                addModelAttributeBooksWereDeleted(model);
            }

            final Pair<List<String>, List<GroupDetails>> notFoundTitlesAndGroupDetailsList =
                    proviewGroupListService.getGroupDetailsList(form, allProviewGroups);
            final List<GroupDetails> groupDetailsList = notFoundTitlesAndGroupDetailsList.getRight();
            if (groupDetailsList != null) {
                savePaginatedList(httpSession, groupDetailsList);
                httpSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, groupDetailsList.size());

                model.addAttribute(WebConstants.KEY_PAGINATED_LIST, groupDetailsList);
                model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, groupDetailsList.size());
                model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, form);
            } else {
                httpSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, "0");
                model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, "0");
            }

            final List<String> booksNotFoundMsg = notFoundTitlesAndGroupDetailsList.getLeft();
            if (!booksNotFoundMsg.isEmpty()) {
                model.addAttribute(WebConstants.KEY_WARNING_MESSAGE,
                        "Books were deleted from Proview " + booksNotFoundMsg);
            }
        } catch (final ProviewException e) {
            final String msg = e.getMessage().replaceAll(BRACKETS_OR_BRACES_REGEX, "");
            model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, Arrays.asList(msg.split("\\s*,\\s*")));
            httpSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, 0);
            log.warn(e.getMessage(), e);
        } catch (final Exception ex) {
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Exception occurred. Please contact your administrator.");
            log.error(ex.getMessage(), ex);
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_SINGLE_VERSION);
    }

    private void addPilotBookStatusAttribute(final Model model, final ProviewGroup proviewGroup) {
        final String headTitleID = proviewGroup.getHeadTitle();
        final BookDefinition bookDef = bookDefinitionService.findBookDefinitionByTitle(headTitleID);
        if (bookDef != null) {
            model.addAttribute(WebConstants.KEY_PILOT_BOOK_STATUS, bookDef.getPilotBookStatus());
        }
    }

    private void setShowSubgroupModelAttribute(final HttpSession httpSession, final Model model, final ProviewGroup proviewGroup) {
        if (proviewGroup.getSubgroupInfoList() != null
            && proviewGroup.getSubgroupInfoList().get(0).getSubGroupName() != null) {
            model.addAttribute(WebConstants.KEY_SHOW_SUBGROUP, true);
            httpSession.setAttribute(WebConstants.KEY_SHOW_SUBGROUP, true);
        } else if (proviewGroup.getSubgroupInfoList() != null) {
            model.addAttribute(WebConstants.KEY_SHOW_SUBGROUP, false);
            httpSession.setAttribute(WebConstants.KEY_SHOW_SUBGROUP, false);
        }
    }

    /**
     * Handle operational buttons that submit a form of selected rows, or when the user changes the number of rows displayed at
     * one time.
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_OPERATION, method = RequestMethod.GET)
    public ModelAndView performGroupOperations(
        final HttpSession httpSession,
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) @Valid final ProviewGroupListFilterForm form,
        final BindingResult errors,
        final Model model) {
        log.debug(form.toString());

        final String groupIdByVersion = String.format("%s/v%s", form.getProviewGroupID(), form.getGroupVersion());
        model.addAttribute(WebConstants.KEY_GROUP_BY_VERSION_ID, groupIdByVersion);

        if (!errors.hasErrors()) {
            final GroupCmd command = form.getGroupCmd();

            try {
                final Map<String, ProviewGroupContainer> allProviewGroups =
                        allProviewGroupsProvider.getAllProviewGroups(false);
                final ProviewGroupContainer proviewGroupContainer = allProviewGroups.get(form.getProviewGroupID());
                if (proviewGroupContainer != null) {
                    final ProviewGroup proviewGroup = proviewGroupContainer.getGroupByVersion(form.getGroupVersion());
                    addPilotBookStatusAttribute(model, proviewGroup);
                    setShowSubgroupModelAttribute(httpSession, model, proviewGroup);
                    model.addAttribute(WebConstants.KEY_PAGINATED_LIST, getGroupDetails(httpSession, form));
                } else {
                    addModelAttributeBooksWereDeleted(model);
                }
            } catch (final ProviewException e) {
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Proview might be unavailable.");
                log.warn(e.getMessage(), e);
            }

            if (form.getGroupMembers() != null && !form.getGroupMembers().isEmpty()) {
                model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
                model.addAttribute(WebConstants.KEY_GROUP_STATUS, form.getGroupStatus());
                model.addAttribute(WebConstants.KEY_BOOK_ID, form.getBookDefinitionId());
                final ProviewGroupListFilterForm listFilterForm = new ProviewGroupListFilterForm(
                    form.getGroupName(),
                    form.getBookDefinitionId(),
                    form.getGroupIds(),
                    form.getProviewGroupID(),
                    form.getGroupVersion(),
                    form.isGroupOperation());
                listFilterForm.setGroupMembers(form.getGroupMembers());
                model.addAttribute(WebConstants.KEY_PROVIEW_GROUP_LIST_FILTER_FORM, listFilterForm);
                model.addAttribute(WebConstants.KEY_IS_COMPLETE, "false");
            }
            if (GroupCmd.PROMOTE.equals(command)) {
                return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_PROMOTE);
            } else if (GroupCmd.REMOVE.equals(command)) {
                return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE);
            }
        }
        // If there is no selection then display the list
        model.addAttribute(WebConstants.KEY_BOOK_ID, form.getBookDefinitionId());
        model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
        model.addAttribute(WebConstants.KEY_GROUP_STATUS, form.getGroupStatus());
        model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, form);
        if (httpSession.getAttribute(WebConstants.KEY_PAGINATED_LIST) != null) {
            model.addAttribute(
                WebConstants.KEY_PAGINATED_LIST,
                httpSession.getAttribute(WebConstants.KEY_PAGINATED_LIST));
            model.addAttribute(
                WebConstants.KEY_TOTAL_BOOK_SIZE,
                httpSession.getAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE));
            model
                .addAttribute(WebConstants.KEY_SHOW_SUBGROUP, httpSession.getAttribute(WebConstants.KEY_SHOW_SUBGROUP));
        }

        model.addAttribute(WebConstants.KEY_GROUP_VERSION, httpSession.getAttribute(WebConstants.KEY_GROUP_VERSION));
        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_SINGLE_VERSION);
    }

    @NotNull
    private List<GroupDetails> getGroupDetails(final HttpSession httpSession, final ProviewGroupListFilterForm form) {
        final List<GroupDetails> groupDetails = new ArrayList<>();
        final List<String> groupIds = new ArrayList<>();
        form.setGroupIds(groupIds);
        Optional.ofNullable(form.getGroupMembers())
                .ifPresent(groupMembers ->
                        fillGroupDetailsWithSubgroups(httpSession, form, groupDetails, groupIds, groupMembers));
        return groupDetails;
    }

    private void fillGroupDetailsWithSubgroups(final HttpSession httpSession, final ProviewGroupListFilterForm form,
            final List<GroupDetails> groupDetails, final List<String> groupIds, final List<String> groupMembers) {
        final List<GroupDetails> paginatedList = Optional.ofNullable(fetchPaginatedList(httpSession))
                .orElseGet(() -> {
                    Map<String, ProviewGroupContainer> allProviewGroups = Collections.emptyMap();
                    try {
                        allProviewGroups = allProviewGroupsProvider.getAllProviewGroups(false);
                    } catch (ProviewException e) {
                        log.debug(e.getMessage(), e);
                    }
                    return proviewGroupListService
                            .getGroupDetailsList(form, allProviewGroups)
                            .getRight();
                });
        groupMembers.forEach(idWithVersion -> paginatedList.stream()
                .filter(subgroup -> subgroup.getIdWithVersion().equals(idWithVersion))
                .forEach(subgroup -> {
                    groupDetails.add(subgroup);
                    if (subgroup.getTitleId() == null) {
                        groupIds.add(subgroup.getTitleIdListWithVersion().toString());
                    } else {
                        groupIds.add(Arrays.toString(subgroup.getTitleIdWithVersionArray()));
                    }
                }));
    }

    private void savePaginatedList(final HttpSession httpSession, final List<GroupDetails> groupDetailsList) {
        httpSession.setAttribute(WebConstants.KEY_PAGINATED_LIST, groupDetailsList);
    }

    private List<GroupDetails> fetchPaginatedList(final HttpSession httpSession) {
        return (List<GroupDetails>) httpSession.getAttribute(WebConstants.KEY_PAGINATED_LIST);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_BOOK_PROMOTE, method = RequestMethod.POST)
    public ModelAndView proviewTitlePromotePost(
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) final ProviewGroupListFilterForm form,
        final Model model, final HttpSession httpSession) {
        sendEmailAndSetAttribute(
            model,
            form,
            httpSession,
            "Proview Promote Request Status: %s %s",
            "Group: %s could not be promoted to Proview.\n %s",
            PROMOTE,
            FINAL);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_PROMOTE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_BOOK_REMOVE, method = RequestMethod.POST)
    public ModelAndView proviewGroupRemovePost(
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) final ProviewGroupListFilterForm form,
        final Model model, final HttpSession httpSession) {
        sendEmailAndSetAttribute(
            model,
            form,
            httpSession,
            "Proview Remove Request Status: %s %s",
            "Group: %s could not be removed from Proview.\n %s",
            REMOVE,
            REMOVE);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE);
    }

    private void sendEmailAndSetAttribute(
        final Model model,
        final ProviewGroupListFilterForm form,
        final HttpSession httpSession,
        final String emailSubject,
        final String emailBodyTemplate,
        final String operation,
        final String groupStatus) {
        model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
        try {
            if (performGroupOperation(form, model, operation, emailSubject)) {
                if (form.isGroupOperation()) {
                    allProviewGroupsProvider.updateGroupStatus(form.getProviewGroupID(), form.getGroupVersion(), groupStatus);
                }
                model.addAttribute(WebConstants.KEY_GROUP_STATUS, groupStatus);
            }
        } catch (final Exception e) {
            final String emailBody = String.format(emailBodyTemplate, form.getGroupName(), e.getMessage());
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed: \n" + e.getMessage());
            sendEmail(String.format(emailSubject, UNSUCCESSFUL, form.getGroupName()), emailBody);
            log.error(e.getMessage(), e);
        }
        model.addAttribute(WebConstants.KEY_PAGINATED_LIST, getGroupDetails(httpSession, form));
    }

    private void sendEmail(final String subject, final String body) {
        final Collection<InternetAddress> emails =
            emailUtil.getEmailRecipientsByUsername(UserUtils.getAuthenticatedUserName());
        emailService
            .send(new NotificationEmail(emails, subject, String.format("Environment: %s%n%s", environmentName, body)));
    }

    private boolean performGroupOperation(
        final ProviewGroupListFilterForm form,
        final Model model,
        final String operation,
        final String emailSubject) {
        String emailBody = "";

        final StringBuilder errorStringBuilder = new StringBuilder();
        final StringBuilder successStringBuilder = new StringBuilder();
        boolean success = true;
        model.addAttribute(WebConstants.KEY_IS_COMPLETE, "true");

        final List<ProviewAudit> auditList = new ArrayList<>();
        String[] titlesString = {};
        for (String bookTitlesWithVersion : form.getGroupIds()) {
            if (!bookTitlesWithVersion.isEmpty()) {
                bookTitlesWithVersion = bookTitlesWithVersion.replaceAll(BRACKETS_OR_BRACES_REGEX, "");
                if (!bookTitlesWithVersion.isEmpty()) {
                    titlesString = bookTitlesWithVersion.split(",");
                }
            }
        }

        for (final String bookTitleWithVersion : titlesString) {
            final String version = StringUtils.substringAfterLast(bookTitleWithVersion, "/").trim();
            final String title = StringUtils.substringBeforeLast(bookTitleWithVersion, "/").trim();
            try {
                doTitleOperation(operation, title, version);
                final ProviewAudit audit = new ProviewAudit();
                audit.setTitleId(title);
                audit.setBookVersion(version);
                auditList.add(audit);
                successStringBuilder.append(
                    "Title " + title + " version " + version + " has been " + operation + "d successfully \t\n");
            } catch (final Exception e) {
                log.error(e.getMessage(), e);
                if (e.getMessage().contains("Title status cannot be changed from Final to Final")) {
                    successStringBuilder.append(title + "/" + version + " unchanged. Status: Final\n");
                } else {
                    success = false;
                    errorStringBuilder.append(
                        "Failed to "
                            + operation
                            + " title "
                            + title
                            + " and version "
                            + version
                            + ".\t\n"
                            + e.getMessage()
                            + "\t\n\n");
                }
            }
        }

        String groupRequest = operation;

        if (success && form.isGroupOperation()) {
            try {
                doGroupOperation(operation, form.getProviewGroupID() + "/v" + form.getGroupVersion());
                // Group will be deleted when users removes group
                if (operation.equalsIgnoreCase(REMOVE)) {
                    groupRequest = DELETE;
                    doGroupOperation(groupRequest, form.getProviewGroupID() + "/v" + form.getGroupVersion());
                }
                final String successMsg = "GroupID "
                    + form.getProviewGroupID()
                    + ", Group version "
                    + form.getGroupVersion()
                    + ", Group name "
                    + form.getGroupName()
                    + " has been "
                    + groupRequest
                    + "d successfully";
                successStringBuilder.append(successMsg);
                model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: \t\n" + successMsg);
            } catch (final Exception e) {
                success = false;
                errorStringBuilder.append(
                    "Failed to "
                        + groupRequest
                        + " group "
                        + form.getProviewGroupID()
                        + " and version "
                        + form.getGroupVersion()
                        + "."
                        + e.getMessage());
                log.error(e.getMessage(), e);
            }
        } else if (success && !form.isGroupOperation()) {
            final String successMsg = "Selected Titles have been " + operation + "d successfully";
            model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: \t\n" + successMsg);
        } else {
            errorStringBuilder.append(
                "Group Id "
                    + form.getProviewGroupID()
                    + " Version "
                    + form.getGroupVersion()
                    + " could not be "
                    + operation
                    + "d");
        }

        final String emailStatus;
        if (success) {
            emailStatus = "Success";
            emailBody = successStringBuilder.toString();
        } else {
            emailStatus = "Failed";
            model.addAttribute(WebConstants.KEY_GROUP_STATUS, form.getGroupStatus());
            if (StringUtils.isNotEmpty(successStringBuilder)) {
                successStringBuilder.append(errorStringBuilder);
                model.addAttribute(
                    WebConstants.KEY_ERR_MESSAGE,
                    "Partial failure: \t\n" + successStringBuilder.toString());
                emailBody = "Partial failure: \n" + successStringBuilder.toString();
            } else {
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed: \t\n" + errorStringBuilder.toString());
                emailBody = "Failed: \t\n" + errorStringBuilder.toString();
            }
        }
        sendEmail(String.format(emailSubject, emailStatus, form.getGroupName()), emailBody);
        for (final ProviewAudit audit : auditList) {
            proviewAuditService.save(form.createAudit(
                audit.getTitleId(),
                audit.getBookVersion(),
                new Date(), // lastUpdate,
                operation.toUpperCase(),
                form.getComments()));
        }
        return success;
    }

    private void doTitleOperation(final String operation, final String title, final String version) throws Exception {
        switch (operation) {
            case PROMOTE:
                proviewHandler.promoteTitle(title, version);
                TimeUnit.SECONDS.sleep(3);
                break;
            case REMOVE:
                proviewHandler.removeTitle(title, new Version(version));
                TimeUnit.SECONDS.sleep(3);
                break;
            case DELETE:
                deleteTitleWithRetryLogic(title, version);
                TimeUnit.SECONDS.sleep(3);
                break;
            default:
                throw new ProviewException(String.format("Unexpected operation on title. Name of the operation: %s",
                    operation));
        }
    }

    private void doGroupOperation(final String operation, final String groupIdByVersion) throws ProviewException {
        switch (operation) {
            case PROMOTE:
                proviewHandler.promoteGroup(
                    StringUtils.substringBeforeLast(groupIdByVersion, "/v"),
                    StringUtils.substringAfterLast(groupIdByVersion, "/"));
                break;
            case REMOVE:
                proviewHandler.removeGroup(
                    StringUtils.substringBeforeLast(groupIdByVersion, "/v"),
                    StringUtils.substringAfterLast(groupIdByVersion, "/"));
                break;
            case DELETE:
                proviewHandler.deleteGroup(
                    StringUtils.substringBeforeLast(groupIdByVersion, "/v"),
                    StringUtils.substringAfterLast(groupIdByVersion, "/"));
                break;
            default:
                throw new ProviewException(String.format("Unexpected operation on group. Name of the operation: %s",
                    operation));
        }
    }

    protected void deleteTitleWithRetryLogic(final String title, final String version) throws ProviewException {
        boolean retryRequest = true;
        int baseRetryInterval = 15; // in seconds
        int retryCount = 0;
        do {
            retryRequest = !proviewHandler.deleteTitle(title, new Version(version));
            if (retryRequest) {
                retryCount++;
                try {
                    TimeUnit.SECONDS.sleep(baseRetryInterval);
                    // increment by 15 seconds every time
                    baseRetryInterval = baseRetryInterval + 15;
                } catch (final InterruptedException e) {
                    log.error("InterruptedException during HTTP retry", e);
                    Thread.currentThread().interrupt();
                }
            }
        } while (retryRequest && retryCount < getMaxNumberOfRetries());
        if (retryRequest && retryCount == getMaxNumberOfRetries()) {
            throw new ProviewRuntimeException(
                "Tried 3 times to delete title and not succeeded. Proview might be down "
                    + "or still in the process of deleting the book. Please try again later. ");
        }
    }

    public int getMaxNumberOfRetries() {
        return MAX_NUMBER_OF_RETRIES;
    }
}
