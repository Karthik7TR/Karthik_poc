package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.SubgroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.GroupCmd;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
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

@Controller
public class ProviewGroupListController extends BaseProviewGroupListController {
    private static final Logger log = LogManager.getLogger(ProviewGroupListController.class);

    @Autowired
    private ProviewHandler proviewHandler;
    @Autowired
    private BookDefinitionService bookDefinitionService;
    @Autowired
    private ProviewAuditService proviewAuditService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private MessageSourceAccessor messageSourceAccessor;
    @Autowired
    private JobRequestService jobRequestService;
    @Autowired
    private PublishingStatsService publishingStatsService;
    @Autowired
    @Qualifier("proviewGroupValidator")
    private Validator validator;

    private String booksNotFoundMsg;
    private int maxNumberOfRetries = 3;

    @InitBinder(ProviewGroupListFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.setValidator(validator);
    }

    /**
     *
     * @param httpSession
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUPS, method = RequestMethod.POST)
    public ModelAndView postSelectionsforGroups(
        @ModelAttribute final ProviewGroupForm form,
        final HttpSession httpSession,
        final Model model) throws Exception {
        final Command command = form.getCommand();
        switch (command) {
        case REFRESH:

            final Map<String, ProviewGroupContainer> allProviewGroups = proviewHandler.getAllProviewGroupInfo();
            final List<ProviewGroup> allLatestProviewGroups =
                proviewHandler.getAllLatestProviewGroupInfo(allProviewGroups);

            saveAllProviewGroups(httpSession, allProviewGroups);
            saveAllLatestProviewGroups(httpSession, allLatestProviewGroups);
            saveSelectedProviewGroups(httpSession, allLatestProviewGroups);

            if (allLatestProviewGroups != null) {
                model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allLatestProviewGroups);
                model.addAttribute(WebConstants.KEY_TOTAL_GROUP_SIZE, allLatestProviewGroups.size());
            }

            model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, new ProviewGroupListFilterForm());

            final ProviewGroupForm proviewGroupForm = fetchProviewGroupForm(httpSession);
            if (proviewGroupForm.getObjectsPerPage() == null) {
                proviewGroupForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
            }
            model.addAttribute(ProviewGroupForm.FORM_NAME, proviewGroupForm);
            model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewGroupForm.getObjectsPerPage());
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
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUPS);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_DOWNLOAD, method = RequestMethod.GET)
    public void downloadProviewGroupExcel(
        final HttpSession httpSession,
        final HttpServletRequest request,
        final HttpServletResponse response) {
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
            log.error(e.getMessage());
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
                        saveAllProviewGroups(httpSession, allProviewGroups);
                    }

                    allLatestProviewGroups = proviewHandler.getAllLatestProviewGroupInfo(allProviewGroups);
                    saveAllLatestProviewGroups(httpSession, allLatestProviewGroups);
                    if (filterForm != null) {
                        selectedProviewGroups = filterProviewGroupList(filterForm, allLatestProviewGroups);
                    } else {
                        selectedProviewGroups = allLatestProviewGroups;
                    }
                    saveSelectedProviewGroups(httpSession, selectedProviewGroups);
                    model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewGroups);
                } catch (final ProviewException e) {
                    model.addAttribute(
                        WebConstants.KEY_ERR_MESSAGE,
                        "Proview Exception occured. Please contact your administrator.");
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

        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUPS);
    }

    /**
     * /ebookManager/proviewGroupAllVersions.mvc?groupIds=<groupID>
     *
     * @param groupId
     * @param httpSession
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_ALL_VERSIONS, method = RequestMethod.GET)
    public ModelAndView singleGroupAllVersions(
        @RequestParam("groupIds") final String groupId,
        final HttpSession httpSession,
        final Model model) throws Exception {
        Map<String, ProviewGroupContainer> allProviewGroups = fetchAllProviewGroups(httpSession);
        if (allProviewGroups == null) {
            allProviewGroups = proviewHandler.getAllProviewGroupInfo();
            saveAllProviewGroups(httpSession, allProviewGroups);
        }

        final ProviewGroupContainer proviewGroupContainer = allProviewGroups.get(groupId);

        if (proviewGroupContainer != null) {
            final List<ProviewGroup> allGroupVersions = proviewGroupContainer.getProviewGroups();
            if (allGroupVersions != null) {
                Collections.sort(allGroupVersions);
                model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allGroupVersions);
                model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, allGroupVersions.size());
            }
        }

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
        } catch (final Exception ex) {
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Exception occured. Please contact your administrator.");
            ex.printStackTrace();
            log.error(ex.getMessage());
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
                    notFound.add(titleId);
                }
            }
        }
        if (notFound.size() > 0) {
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
     * @throws Exception
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
        if (notFound.size() > 0) {
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
        log.debug(form);

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
            if (form.getGroupMembers() != null && form.getGroupMembers().size() > 0) {
                model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
                model.addAttribute(WebConstants.KEY_GROUP_STATUS, form.getGroupStatus());
                model.addAttribute(WebConstants.KEY_BOOK_ID, form.getBookDefinitionId());
                final String groupByVersion = form.getProviewGroupID() + "/" + form.getGroupVersion();
                final ProviewGroupListFilterForm listFilterForm = new ProviewGroupListFilterForm(
                    form.getGroupName(),
                    form.getBookDefinitionId(),
                    form.getGroupIds(),
                    form.getProviewGroupID(),
                    form.getGroupVersion(),
                    groupByVersion,
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

    /**
     *
     * @param form
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_BOOK_PROMOTE, method = RequestMethod.POST)
    public ModelAndView proviewTitlePromotePost(
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) final ProviewGroupListFilterForm form,
        final Model model) throws Exception {
        model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
        try {
            final boolean success = performGroupOperation(form, model, "Promote");
            if (success) {
                model.addAttribute(WebConstants.KEY_GROUP_STATUS, "Final");
            }
        } catch (final Exception e) {
            final String emailBody =
                "Group: " + form.getGroupName() + " could not be promoted to Proview.\n" + e.getMessage();
            final String emailSubject = "Proview Promote Request Status: Unsuccessful";
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed: \n" + e.getMessage());
            sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_PROMOTE);
    }

    /**
     *
     * @param form
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_BOOK_REMOVE, method = RequestMethod.POST)
    public ModelAndView proviewGroupRemovePost(
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) final ProviewGroupListFilterForm form,
        final Model model) throws Exception {
        model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
        try {
            final boolean success = performGroupOperation(form, model, "Remove");
            if (success) {
                model.addAttribute(WebConstants.KEY_GROUP_STATUS, "Remove");
            }
        } catch (final Exception e) {
            final String emailBody =
                "Group: " + form.getGroupName() + " could not be removed from Proview.\n" + e.getMessage();
            final String emailSubject = "Proview Remove Request Status: Unsuccessful";
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed: \n" + e.getMessage());
            sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE);
    }

    /**
     *
     * @param form
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_BOOK_DELETE, method = RequestMethod.POST)
    public ModelAndView proviewGroupDeletePost(
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) final ProviewGroupListFilterForm form,
        final Model model) throws Exception {
        model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
        try {
            final boolean success = performGroupOperation(form, model, "Delete");
            if (success) {
                model.addAttribute(WebConstants.KEY_GROUP_STATUS, "Delete");
            }
        } catch (final Exception e) {
            final String emailBody =
                "Group: " + form.getGroupName() + " could not be Deleted from Proview.\n" + e.getMessage();
            final String emailSubject = "Proview Delete Request Status: Unsuccessful";
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed: \n" + e.getMessage());
            sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_DELETE);
    }

    private void sendEmail(final String emailAddressString, final String subject, final String body) {
        final List<InternetAddress> emailAddresses = new ArrayList<>();
        try {
            emailAddresses.add(new InternetAddress(emailAddressString));
            EmailNotification.send(emailAddresses, subject, body);
        } catch (final AddressException e) {
            log.error(e);
        }
    }

    private boolean performGroupOperation(
        final ProviewGroupListFilterForm form,
        final Model model,
        final String operation) {
        String emailBody = "";
        String emailSubject = "Proview " + operation + " Request Status: ";

        final StringBuffer errorBuffer = new StringBuffer();
        final StringBuffer successBuffer = new StringBuffer();
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
                successBuffer.append(
                    "Title " + title + " version " + version + " has been " + operation + "d successfully \t\n");
            } catch (final Exception e) {
                if (e.getMessage().contains("Title status cannot be changed from Final to Final")) {
                    successBuffer.append(title + "/" + version + " unchanged. Status: Final\n");
                } else {
                    success = false;
                    errorBuffer.append(
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
                if (operation.equalsIgnoreCase("Remove")) {
                    groupRequest = "Delete";
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
                successBuffer.append(successMsg);
                model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: \t\n" + successMsg);
            } catch (final Exception e) {
                success = false;
                errorBuffer.append(
                    "Failed to "
                        + groupRequest
                        + " group "
                        + form.getProviewGroupID()
                        + " and version "
                        + form.getGroupVersion()
                        + "."
                        + e.getMessage());
            }
        } else if (success && !form.isGroupOperation()) {
            final String successMsg = "Selected Titles have been " + operation + "d successfully";
            model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: \t\n" + successMsg);
        } else {
            errorBuffer.append(
                "Group Id "
                    + form.getProviewGroupID()
                    + " Version "
                    + form.getGroupVersion()
                    + " could not be "
                    + operation
                    + "d");
        }

        if (success) {
            emailSubject += "Success";
            emailBody = successBuffer.toString();
        } else {
            emailSubject += "Failed";
            model.addAttribute(WebConstants.KEY_GROUP_STATUS, form.getGroupStatus());
            if (successBuffer.length() > 0) {
                successBuffer.append(errorBuffer);
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Partial failure: \t\n" + successBuffer.toString());
                emailBody = "Partial failure: \n" + successBuffer.toString();
            } else {
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed: \t\n" + errorBuffer.toString());
                emailBody = "Failed: \t\n" + errorBuffer.toString();
            }
        }
        sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
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
        case "Promote": {
            proviewHandler.promoteTitle(title, version);
            TimeUnit.SECONDS.sleep(3);
            break;
        }
        case "Remove": {
            proviewHandler.removeTitle(title, new Version(version));
            TimeUnit.SECONDS.sleep(3);
            break;
        }
        case "Delete": {
            deleteTitleWithRetryLogic(title, version);
            TimeUnit.SECONDS.sleep(3);
            break;
        }
        }
    }

    private void doGroupOperation(final String operation, final String groupIdByVersion) throws Exception {
        switch (operation) {
        case "Promote":
            proviewHandler.promoteGroup(
                StringUtils.substringBeforeLast(groupIdByVersion, "/v"),
                StringUtils.substringAfterLast(groupIdByVersion, "/"));
            break;
        case "Remove":
            proviewHandler.removeGroup(
                StringUtils.substringBeforeLast(groupIdByVersion, "/v"),
                StringUtils.substringAfterLast(groupIdByVersion, "/"));
            break;
        case "Delete":
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
}
