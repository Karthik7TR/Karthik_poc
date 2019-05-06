package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.SubgroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.GroupCmd;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Workbook;
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
public class ProviewGroupListController extends BaseProviewGroupListController {
    private static final String FINAL = "Final";
    private static final String PROMOTE = "Promote";
    private static final String DELETE = "Delete";
    private static final String REMOVE = "Remove";
    private static final String UNSUCCESSFUL = "Unsuccessful";

    @Autowired
    private ProviewHandler proviewHandler;
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

    private String booksNotFoundMsg;
    private int maxNumberOfRetries = 3;

    @InitBinder(ProviewGroupListFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUPS, method = RequestMethod.POST)
    @ShowOnException(errorViewName = WebConstants.VIEW_PROVIEW_GROUPS)
    public ModelAndView postSelectionsforGroups(
        @ModelAttribute final ProviewGroupForm form,
        final HttpSession httpSession,
        final Model model) throws ProviewException {
        model.addAttribute(ProviewGroupForm.FORM_NAME, new ProviewGroupForm());
        model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, new ProviewGroupListFilterForm());
        final Command command = form.getCommand();
        switch (command) {
        case REFRESH:
            final Map<String, ProviewGroupContainer> allProviewGroups = proviewHandler.getAllProviewGroupInfo();
            updateGroupTitlesLatestUpdateDates(allProviewGroups.values(), httpSession);
            final List<ProviewGroup> allLatestProviewGroups =
                proviewHandler.getAllLatestProviewGroupInfo(allProviewGroups);
            fillLatestUpdateDatesForProviewGroups(allLatestProviewGroups, httpSession);

            saveAllProviewGroups(httpSession, allProviewGroups);
            saveAllLatestProviewGroups(httpSession, allLatestProviewGroups);
            saveSelectedProviewGroups(httpSession, allLatestProviewGroups);

            if (allLatestProviewGroups != null) {
                model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allLatestProviewGroups);
                model.addAttribute(WebConstants.KEY_TOTAL_GROUP_SIZE, allLatestProviewGroups.size());
            }

            final ProviewGroupForm proviewGroupForm = fetchProviewGroupForm(httpSession);
            if (proviewGroupForm.getObjectsPerPage() == null) {
                proviewGroupForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
            }
            model.addAttribute(ProviewGroupForm.FORM_NAME, proviewGroupForm);
            model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewGroupForm.getObjectsPerPage());
            model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
            break;
        case PAGESIZE:
            saveProviewGroupForm(httpSession, form);
            final List<ProviewGroup> selectedProviewGroups = fetchSelectedProviewGroups(httpSession);
            model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewGroups);
            model.addAttribute(WebConstants.KEY_TOTAL_GROUP_SIZE, selectedProviewGroups.size());
            model.addAttribute(WebConstants.KEY_PAGE_SIZE, form.getObjectsPerPage());
            model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, fetchProviewGroupListFilterForm(httpSession));
            model.addAttribute(ProviewGroupForm.FORM_NAME, form);
            break;
        default:
            throw new ProviewException(String.format("Unexpected command %s in request %s.", command, WebConstants.MVC_PROVIEW_TITLES));
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUPS);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_DOWNLOAD, method = RequestMethod.GET)
    public void downloadProviewGroupExcel(final HttpSession httpSession, final HttpServletResponse response) {
        final ProviewGroupExcelExportService excelExportService = new ProviewGroupExcelExportService();
        try {
            final Workbook wb = excelExportService.createExcelDocument(httpSession);
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
     * /ebookManager/proviewGroups.mvc
     *
     * @param httpSession
     * @param model
     * @return
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUPS, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_PROVIEW_GROUPS)
    public ModelAndView allLatestProviewGroupsList(final HttpSession httpSession, final Model model) {
        List<ProviewGroup> selectedProviewGroups = fetchSelectedProviewGroups(httpSession);
        final ProviewGroupListFilterForm filterForm = fetchProviewGroupListFilterForm(httpSession);

        if (selectedProviewGroups == null) {
            List<ProviewGroup> allLatestProviewGroups = fetchAllLatestProviewGroups(httpSession);
            if (allLatestProviewGroups == null) {
                Map<String, ProviewGroupContainer> allProviewGroups = fetchAllProviewGroups(httpSession);

                try {
                    if (allProviewGroups == null) {
                        allProviewGroups = proviewHandler.getAllProviewGroupInfo();
                        updateGroupTitlesLatestUpdateDates(allProviewGroups.values(), httpSession);
                        saveAllProviewGroups(httpSession, allProviewGroups);
                    }

                    allLatestProviewGroups = proviewHandler.getAllLatestProviewGroupInfo(allProviewGroups);
                    fillLatestUpdateDatesForProviewGroups(allLatestProviewGroups, httpSession);
                    saveAllLatestProviewGroups(httpSession, allLatestProviewGroups);
                    if (filterForm != null) {
                        selectedProviewGroups = filterProviewGroupList(filterForm, allLatestProviewGroups);
                    } else {
                        selectedProviewGroups = allLatestProviewGroups;
                    }
                    saveSelectedProviewGroups(httpSession, selectedProviewGroups);
                    model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewGroups);
                } catch (final ProviewException e) {
                    log.warn(e.getMessage(), e);
                    model.addAttribute(WebConstants.KEY_ERROR_OCCURRED, Boolean.TRUE);
                }
            }
        } else {
            model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewGroups);
            model.addAttribute(WebConstants.KEY_TOTAL_GROUP_SIZE, selectedProviewGroups.size());
        }
        model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, fetchProviewGroupListFilterForm(httpSession));

        ProviewGroupForm proviewGroupForm = fetchProviewGroupForm(httpSession);
        if (proviewGroupForm.getObjectsPerPage() == null) {
            proviewGroupForm = new ProviewGroupForm();
            proviewGroupForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
            saveProviewGroupForm(httpSession, proviewGroupForm);
        }

        model.addAttribute(ProviewGroupForm.FORM_NAME, proviewGroupForm);
        model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewGroupForm.getObjectsPerPage());
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUPS);
    }

    /**
     * /ebookManager/proviewGroupAllVersions.mvc?groupIds=<groupID>
     *
     * @param groupId
     * @param httpSession
     * @param model
     * @return
     * @throws ProviewException
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_ALL_VERSIONS, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_PROVIEW_GROUP_ALL_VERSIONS)
    public ModelAndView singleGroupAllVersions(
        @RequestParam("groupIds") final String groupId,
        final HttpSession httpSession,
        final Model model) throws ProviewException {
        Map<String, ProviewGroupContainer> allProviewGroups = fetchAllProviewGroups(httpSession);
        if (allProviewGroups == null) {
            allProviewGroups = proviewHandler.getAllProviewGroupInfo();
            saveAllProviewGroups(httpSession, allProviewGroups);
        }

        final ProviewGroupContainer proviewGroupContainer = allProviewGroups.get(groupId);

        int bookSize = 0;
        if (proviewGroupContainer != null) {
            final List<ProviewGroup> allGroupVersions = proviewGroupContainer.getProviewGroups();
            if (allGroupVersions != null) {
                Collections.sort(allGroupVersions);
                model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allGroupVersions);
                bookSize = allGroupVersions.size();
            }
        }
        model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, bookSize);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_ALL_VERSIONS);
    }

    /**
     * /ebookManager/proviewGroupSingleVersion.mvc?groupIdByVersion= <groupIDsbyVersion>
     *
     * @param groupIdByVersion
     * @param httpSession
     * @param model
     * @param form
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_SINGLE_VERSION, method = RequestMethod.GET)
    public ModelAndView singleGroupTitleSingleVersion(
        @RequestParam(WebConstants.KEY_GROUP_BY_VERSION_ID) final String groupIdByVersion,
        final HttpSession httpSession,
        final Model model,
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) final ProviewGroupListFilterForm form) throws Exception {
        try {
            final String groupId = StringUtils.substringBeforeLast(groupIdByVersion, "/v");
            final String version = StringUtils.substringAfterLast(groupIdByVersion, "/v");
            form.setProviewGroupID(groupId);

            Map<String, ProviewGroupContainer> allProviewGroups = fetchAllProviewGroups(httpSession);

            if (allProviewGroups == null) {
                allProviewGroups = proviewHandler.getAllProviewGroupInfo();
                saveAllProviewGroups(httpSession, allProviewGroups);
            }

            final ProviewGroupContainer proviewGroupContainer = allProviewGroups.get(groupId);
            final ProviewGroup proviewGroup = proviewGroupContainer.getGroupByVersion(version);
            List<GroupDetails> groupDetailsList = null;

            final String headTitleID = proviewGroup.getHeadTitle();

            model.addAttribute(WebConstants.KEY_GROUP_NAME, proviewGroup.getGroupName());
            model.addAttribute(WebConstants.KEY_HEAD_TITLE, headTitleID);
            model.addAttribute(WebConstants.KEY_GROUP_STATUS, proviewGroup.getGroupStatus());
            model.addAttribute(WebConstants.KEY_GROUP_VERSION, version);
            model.addAttribute(WebConstants.KEY_PROVIEW_GROUP_ID, groupId);
            model.addAttribute(WebConstants.KEY_GROUP_BY_VERSION_ID, groupIdByVersion);

            httpSession.setAttribute(WebConstants.KEY_GROUP_NAME, proviewGroup.getGroupName());
            httpSession.setAttribute(WebConstants.KEY_HEAD_TITLE, headTitleID);
            httpSession.setAttribute(WebConstants.KEY_GROUP_STATUS, proviewGroup.getGroupStatus());
            httpSession.setAttribute(WebConstants.KEY_GROUP_BY_VERSION_ID, groupIdByVersion);
            httpSession.setAttribute(WebConstants.KEY_GROUP_VERSION, version);

            booksNotFoundMsg = null;
            if (proviewGroup.getSubgroupInfoList() != null
                && proviewGroup.getSubgroupInfoList().get(0).getSubGroupName() != null) {
                model.addAttribute(WebConstants.KEY_SHOW_SUBGROUP, true);
                httpSession.setAttribute(WebConstants.KEY_SHOW_SUBGROUP, true);
                groupDetailsList = getGroupDetailsWithSubGroups(version, proviewGroupContainer);
                for (final GroupDetails groupDetail : groupDetailsList) {
                    Collections.sort(groupDetail.getTitleIdList());
                }
            } else if (proviewGroup.getSubgroupInfoList() != null) {
                model.addAttribute(WebConstants.KEY_SHOW_SUBGROUP, false);
                httpSession.setAttribute(WebConstants.KEY_SHOW_SUBGROUP, false);

                groupDetailsList = getGroupDetailsWithNoSubgroups(proviewGroup);
            }

            if (groupDetailsList != null) {
                Collections.sort(groupDetailsList);
                savePaginatedList(httpSession, groupDetailsList);
                httpSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, groupDetailsList.size());

                model.addAttribute(WebConstants.KEY_PAGINATED_LIST, groupDetailsList);
                model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, groupDetailsList.size());
                model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, form);
            } else {
                httpSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, "0");
                model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, "0");
            }

            final BookDefinition bookDef = bookDefinitionService.findBookDefinitionByTitle(headTitleID);
            if (bookDef != null) {
                model.addAttribute(WebConstants.KEY_PILOT_BOOK_STATUS, bookDef.getPilotBookStatus());
            }

            if (booksNotFoundMsg != null) {
                booksNotFoundMsg = booksNotFoundMsg.replaceAll("\\[|\\]|\\{|\\}", "");
                model.addAttribute(
                    WebConstants.KEY_WARNING_MESSAGE,
                    "Books were deleted from Proview " + Arrays.asList(booksNotFoundMsg.split("\\s*,\\s*")));
            }
        } catch (final ProviewException e) {
            final String msg = e.getMessage().replaceAll("\\[|\\]|\\{|\\}", "");
            model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, Arrays.asList(msg.split("\\s*,\\s*")));
            httpSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, 0);
            log.warn(e.getMessage(), e);
        } catch (final Exception ex) {
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Exception occured. Please contact your administrator.");
            log.error(ex.getMessage(), ex);
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_SINGLE_VERSION);
    }

    protected List<GroupDetails> getGroupDetailsWithSubGroups(
        final String version,
        final ProviewGroupContainer proviewGroupContainer) throws Exception {
        final Map<String, GroupDetails> groupDetailsMap = new HashMap<>();
        final List<String> notFound = new ArrayList<>();

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
                Integer majorVersion = null;
                if (!titleMajorVersion.equals("")) {
                    majorVersion = Integer.valueOf(titleMajorVersion);
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
                                    groupDetails.setTitleIdList(new ArrayList<ProviewTitleInfo>());
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
                        // accumulate list of titleIDs not found by ProView
                        notFound.add(titleId);
                    }
                } catch (final ProviewException e) {
                    log.warn(e.getMessage(), e);
                    notFound.add(titleId);
                }
            }
        }
        if (!notFound.isEmpty()) {
            setBooksNotFoundMsg(notFound.toString());
        }
        return new ArrayList<>(groupDetailsMap.values());
    }

    /**
     * For single titles. Gets book details from Proview and removed/deleted details from ProviewAudit
     *
     * @param fullyQualifiedTitleId
     * @param bookdefId
     * @return
     */
    protected List<GroupDetails> getGroupDetailsWithNoSubgroups(final ProviewGroup proviewGroup) throws Exception {
        final List<GroupDetails> groupDetailsList = new ArrayList<>();
        final List<String> notFound = new ArrayList<>();

        String rootGroupId = proviewGroup.getGroupId();
        rootGroupId = StringUtils.substringAfter(rootGroupId, "_");

        final SubgroupInfo subgroup = proviewGroup.getSubgroupInfoList().get(0);
        for (final String titleId : subgroup.getTitleIdList()) {
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
                    // unexpected exception
                    throw ex;
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
        // display all title IDs that were not found
        if (!notFound.isEmpty()) {
            setBooksNotFoundMsg(notFound.toString());
        }
        return groupDetailsList;
    }

    /**
     * Handle operational buttons that submit a form of selected rows, or when the user changes the number of rows displayed at
     * one time.
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_OPERATION, method = RequestMethod.POST)
    public ModelAndView performGroupOperations(
        final HttpSession httpSession,
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) @Valid final ProviewGroupListFilterForm form,
        final BindingResult errors,
        final Model model) {
        log.debug(form.toString());

        if (!errors.hasErrors()) {
            final GroupCmd command = form.getGroupCmd();
            final List<GroupDetails> groupDetails = new ArrayList<>();
            final List<String> groupIds = new ArrayList<>();
            for (final String id : ((form.getGroupMembers() == null) ? groupIds : form.getGroupMembers())) {
                for (final GroupDetails subgroup : fetchPaginatedList(httpSession)) {
                    if (subgroup.getIdWithVersion().equals(id)) {
                        groupDetails.add(subgroup);
                        if (subgroup.getTitleId() == null) {
                            groupIds.add(subgroup.getTitleIdListWithVersion().toString());
                        } else {
                            groupIds.add(Arrays.toString(subgroup.getTitleIdWithVersionArray()));
                        }
                        break;
                    }
                }
            }
            model.addAttribute(WebConstants.KEY_PAGINATED_LIST, groupDetails);
            form.setGroupIds(groupIds);
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
            if (command.equals(GroupCmd.PROMOTE)) {
                return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_PROMOTE);
            } else if (command.equals(GroupCmd.REMOVE)) {
                return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE);
            } else if (command.equals(GroupCmd.DELETE)) {
                return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_DELETE);
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

    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_BOOK_PROMOTE, method = RequestMethod.POST)
    public ModelAndView proviewTitlePromotePost(
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) final ProviewGroupListFilterForm form,
        final Model model) {
        sendEmailAndSetAttribute(
            model,
            form,
            "Proview Promote Request Status: %s %s",
            "Group: %s could not be promoted to Proview.\n %s",
            PROMOTE,
            FINAL);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_PROMOTE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_BOOK_REMOVE, method = RequestMethod.POST)
    public ModelAndView proviewGroupRemovePost(
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) final ProviewGroupListFilterForm form,
        final Model model) {
        sendEmailAndSetAttribute(
            model,
            form,
            "Proview Remove Request Status: %s %s",
            "Group: %s could not be removed from Proview.\n %s",
            REMOVE,
            REMOVE);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_BOOK_DELETE, method = RequestMethod.POST)
    public ModelAndView proviewGroupDeletePost(
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) final ProviewGroupListFilterForm form,
        final Model model) {
        sendEmailAndSetAttribute(
            model,
            form,
            "Proview Delete Request Status: %s %s",
            "Group: %s could not be Deleted from Proview.\n %s",
            DELETE,
            DELETE);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_DELETE);
    }

    private void sendEmailAndSetAttribute(
        final Model model,
        final ProviewGroupListFilterForm form,
        final String emailSubject,
        final String emailBodyTemplate,
        final String operation,
        final String groupStatus) {
        model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
        try {
            if (performGroupOperation(form, model, operation, emailSubject)) {
                model.addAttribute(WebConstants.KEY_GROUP_STATUS, groupStatus);
            }
        } catch (final Exception e) {
            final String emailBody = String.format(emailBodyTemplate, form.getGroupName(), e.getMessage());
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed: \n" + e.getMessage());
            sendEmail(String.format(emailSubject, UNSUCCESSFUL, form.getGroupName()), emailBody);
            log.error(e.getMessage(), e);
        }
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
                bookTitlesWithVersion = bookTitlesWithVersion.replaceAll("\\[|\\]|\\{|\\}", "");
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
        case PROMOTE: {
            proviewHandler.promoteTitle(title, version);
            TimeUnit.SECONDS.sleep(3);
            break;
        }
        case REMOVE: {
            proviewHandler.removeTitle(title, new Version(version));
            TimeUnit.SECONDS.sleep(3);
            break;
        }
        case DELETE: {
            deleteTitleWithRetryLogic(title, version);
            TimeUnit.SECONDS.sleep(3);
            break;
        }
        }
    }

    private void doGroupOperation(final String operation, final String groupIdByVersion) throws Exception {
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
        }
    }

    protected void deleteTitleWithRetryLogic(final String title, final String version) throws ProviewException {
        boolean retryRequest = true;
        int baseRetryInterval = 15; // in milliseconds
        int retryCount = 0;
        do {
            retryRequest = !proviewHandler.deleteTitle(title, new Version(version));
            if (retryRequest) {
                retryCount++;
                try {
                    TimeUnit.SECONDS.sleep(baseRetryInterval);
                    // increment by 15 milliseconds every time
                    baseRetryInterval = baseRetryInterval + 15;
                } catch (final InterruptedException e) {
                    log.error("InterruptedException during HTTP retry", e);
                }
            }
        } while (retryRequest && retryCount < getMaxNumberOfRetries());
        if (retryRequest && retryCount == getMaxNumberOfRetries()) {
            throw new ProviewRuntimeException(
                "Tried 3 times to delete title and not succeeded. Proview might be down "
                    + "or still in the process of deleting the book. Please try again later. ");
        }
    }

    public String getBooksNotFoundMsg() {
        return booksNotFoundMsg;
    }

    public void setBooksNotFoundMsg(final String booksNotFoundMsg) {
        this.booksNotFoundMsg = booksNotFoundMsg;
    }

    public int getMaxNumberOfRetries() {
        return maxNumberOfRetries;
    }

    private void updateGroupTitlesLatestUpdateDates(final Collection<ProviewGroupContainer> proviewGroupContainers,
                                                    final HttpSession session) {
        final Set<String> titleIds = Optional.ofNullable(proviewGroupContainers)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(ProviewGroupContainer::getProviewGroups)
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .map(this::getProviewGroupTitleIds)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
        final Map<String, Date> latestDates = proviewAuditService.findMaxRequestDateByTitleIds(titleIds);
        session.setAttribute(WebConstants.KEY_LATEST_UPDATE_DATES_GROUPS, latestDates);
    }

    private void fillLatestUpdateDatesForProviewGroups(final Collection<ProviewGroup> groups, final HttpSession session) {
        final Map<String, Date> latestUpdateDates = Optional.ofNullable(session.getAttribute(WebConstants.KEY_LATEST_UPDATE_DATES_GROUPS))
            .map(attribute -> (Map<String, Date>) attribute)
            .orElseGet(Collections::emptyMap);

        Optional.ofNullable(groups)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .forEach(proviewGroup -> {
                getProviewGroupTitleIds(proviewGroup).stream()
                    .map(latestUpdateDates::get)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder())
                    .map(date -> DateFormatUtils.format(date, "yyyyMMdd"))
                    .ifPresent(proviewGroup::setLatestUpdateDate);
            });
    }

    private Set<String> getProviewGroupTitleIds(final ProviewGroup proviewGroup) {
        return Optional.ofNullable(proviewGroup.getSubgroupInfoList())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .flatMap(Subgroup -> Subgroup.getTitleIdList().stream())
            .collect(Collectors.toSet());
    }
}
