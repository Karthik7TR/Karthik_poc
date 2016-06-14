package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

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
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
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
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.SubgroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
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

@Controller
public class ProviewGroupListController extends BaseProviewGroupListController{
	
	private ProviewClient proviewClient;
	private ProviewAuditService proviewAuditService;
	private ManagerService managerService;
	private MessageSourceAccessor messageSourceAccessor;
	private JobRequestService jobRequestService;
	private PublishingStatsService publishingStatsService;
	
	private static final Logger log = Logger.getLogger(ProviewGroupListController.class);
	// retry parameters
	private int maxNumberOfRetries = 3;
	private Validator validator;
	
	
	@InitBinder(ProviewGroupListFilterForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	public Validator getValidator() {
		return validator;
	}
	
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	
	/**
	 * 
	 * @param httpSession
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_GROUPS, method = RequestMethod.POST)
	public ModelAndView postSelectionsforGroups(@ModelAttribute ProviewGroupForm form,
			HttpSession httpSession, Model model) throws Exception {
		
		Command command = form.getCommand();
		switch (command) {
		
		case REFRESH:
			
			Map<String, ProviewGroupContainer> allProviewGroups = proviewClient.getAllProviewGroupInfo();
			List<ProviewGroup> allLatestProviewGroups = proviewClient.getAllLatestProviewGroupInfo(allProviewGroups);
						
			saveAllProviewGroups(httpSession, allProviewGroups);
			saveAllLatestProviewGroups(httpSession, allLatestProviewGroups);
			
			if (allLatestProviewGroups != null) {
				model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allLatestProviewGroups);
				model.addAttribute(WebConstants.KEY_TOTAL_GROUP_SIZE, allLatestProviewGroups.size());
			}
			
			model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, new ProviewGroupListFilterForm());
			
			ProviewGroupForm proviewGroupForm = fetchProviewGroupForm(httpSession);
			if (proviewGroupForm == null) {
				proviewGroupForm = new ProviewGroupForm();
				proviewGroupForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
				saveProviewGroupForm(httpSession, proviewGroupForm);
			}
			model.addAttribute(ProviewGroupForm.FORM_NAME, proviewGroupForm);
			model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewGroupForm.getObjectsPerPage());
			break;
			
		case PAGESIZE:
			saveProviewGroupForm(httpSession, form);
			List<ProviewGroup> selectedProviewGroup = fetchSelectedProviewGroups(httpSession);
			model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewGroup);
			model.addAttribute(WebConstants.KEY_TOTAL_GROUP_SIZE, selectedProviewGroup.size());
			model.addAttribute(WebConstants.KEY_PAGE_SIZE, form.getObjectsPerPage());
			model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, fetchProviewGroupListFilterForm(httpSession));
			model.addAttribute(ProviewGroupForm.FORM_NAME, form);
			break;
		}
		
		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUPS);
	}
	
	/**
	 * /ebookManager/proviewGroups.mvc
	 * 
	 * @param httpSession
	 * @param model
	 * @return
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_GROUPS, method = RequestMethod.GET)
	public ModelAndView allLatestProviewGroupsList(HttpSession httpSession, Model model) {
		
		List<ProviewGroup> selectedProviewGroups = fetchSelectedProviewGroups(httpSession);
		ProviewGroupListFilterForm filterForm = fetchProviewGroupListFilterForm(httpSession);
		
		if (selectedProviewGroups == null) {
			
			List<ProviewGroup> allLatestProviewGroups = fetchAllLatestProviewGroups(httpSession);
			if (allLatestProviewGroups == null) {
				
				Map<String, ProviewGroupContainer> allProviewGroups = fetchAllProviewGroups(httpSession);
				
				try {
					if (allProviewGroups == null) {
						allProviewGroups = proviewClient.getAllProviewGroupInfo();
						saveAllProviewGroups(httpSession, allProviewGroups);
					}
					
					allLatestProviewGroups = proviewClient.getAllLatestProviewGroupInfo(allProviewGroups);
					saveAllLatestProviewGroups(httpSession, allLatestProviewGroups);
					if (filterForm!=null) {
						selectedProviewGroups = filterProviewGroupList(filterForm, allLatestProviewGroups);
					}else{
						selectedProviewGroups = allLatestProviewGroups;
					}
					saveSelectedProviewGroups(httpSession, selectedProviewGroups);
					model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewGroups);
					
					
				} catch (ProviewException e) {
					model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
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
	public ModelAndView singleGroupAllVersions(@RequestParam("groupIds") String groupId,
			HttpSession httpSession, Model model) throws Exception{
		
		Map<String, ProviewGroupContainer> allProviewGroups = fetchAllProviewGroups(httpSession);
		if (allProviewGroups == null) {
			allProviewGroups = proviewClient.getAllProviewGroupInfo();
			saveAllProviewGroups(httpSession, allProviewGroups);
		}
		
		ProviewGroupContainer proviewGroupContainer = allProviewGroups.get(groupId);
		
		if (proviewGroupContainer != null) {
			List<ProviewGroup> allGroupVersions = proviewGroupContainer.getProviewGroups();
			if (allGroupVersions != null) {
				Collections.sort(allGroupVersions);
				model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allGroupVersions);
				model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, allGroupVersions.size());
			}
		}
		
		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_ALL_VERSIONS);
	}
	
	/**
	 * /ebookManager/proviewGroupSingleVersion.mvc?groupIdByVersion=<groupIDsbyVersion>
	 * 
	 * @param groupIdByVersion
	 * @param httpSession
	 * @param model
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_SINGLE_VERSION, method = RequestMethod.GET)
	public ModelAndView singleGroupTitleSingleVersion(@RequestParam(WebConstants.KEY_GROUP_BY_VERSION_ID) String groupIdByVersion,
			HttpSession httpSession, Model model,
			@ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) ProviewGroupListFilterForm form) throws Exception {
		
		try {
			String groupId = StringUtils.substringBeforeLast(groupIdByVersion, "/v");
			String version = StringUtils.substringAfterLast(groupIdByVersion, "/v");
			form.setProviewGroupID(groupId);
			
			Map<String, ProviewGroupContainer> allProviewGroups = fetchAllProviewGroups(httpSession);
			
			if (allProviewGroups == null) {
				allProviewGroups = proviewClient.getAllProviewGroupInfo();
				saveAllProviewGroups(httpSession, allProviewGroups);
			}
			
			ProviewGroupContainer proviewGroupContainer = allProviewGroups.get(groupId);
			ProviewGroup proviewGroup = proviewGroupContainer.getGroupByVersion(version);
			List<GroupDetails> groupDetailsList = null;
			
			String headTitleID = proviewGroup.getHeadTitle();
			
			model.addAttribute(WebConstants.KEY_GROUP_NAME, proviewGroup.getGroupName());
			model.addAttribute(WebConstants.KEY_HEAD_TITLE, headTitleID);
			model.addAttribute(WebConstants.KEY_GROUP_STATUS, proviewGroup.getGroupStatus());
			model.addAttribute(WebConstants.KEY_GROUP_VERSION, version);
			model.addAttribute(WebConstants.KEY_GROUP_BY_VERSION_ID, groupIdByVersion);
			
			httpSession.setAttribute(WebConstants.KEY_GROUP_NAME, proviewGroup.getGroupName());
			httpSession.setAttribute(WebConstants.KEY_HEAD_TITLE, headTitleID);
			httpSession.setAttribute(WebConstants.KEY_GROUP_STATUS, proviewGroup.getGroupStatus());
			httpSession.setAttribute(WebConstants.KEY_GROUP_BY_VERSION_ID, groupIdByVersion);
			httpSession.setAttribute(WebConstants.KEY_GROUP_VERSION, version);
			
			if(proviewGroup != null && proviewGroup.getSubgroupInfoList() != null 
					&& proviewGroup.getSubgroupInfoList().get(0).getSubGroupName() != null) {
				model.addAttribute(WebConstants.KEY_SHOW_SUBGROUP, true);
				httpSession.setAttribute(WebConstants.KEY_SHOW_SUBGROUP, true);
				groupDetailsList = getGroupDetailsWithSubGroups(version, proviewGroupContainer);
				for (GroupDetails groupDetail:groupDetailsList){
					Collections.sort(groupDetail.getTitleIdList());
				}
			}
			else if (proviewGroup != null && proviewGroup.getSubgroupInfoList() != null) {
				model.addAttribute(WebConstants.KEY_SHOW_SUBGROUP, false);
				httpSession.setAttribute(WebConstants.KEY_SHOW_SUBGROUP, false);
				
				groupDetailsList = getGroupDetailsWithNoSubgroups(proviewGroup);
			}
			
			if (groupDetailsList != null) {
				Collections.sort(groupDetailsList);
				savePaginatedList(httpSession,groupDetailsList);
				httpSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, groupDetailsList.size());
				
				model.addAttribute(WebConstants.KEY_PAGINATED_LIST, groupDetailsList);
				model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, groupDetailsList.size());
				model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, form);
			} else {
				httpSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, "0");
				model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, "0");
			}
		
		} catch (ProviewException e) {
			String msg = e.getMessage().replaceAll("\\[|\\]|\\{|\\}", "");
			model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, Arrays.asList(msg.split("\\s*,\\s*")));
			httpSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, 0);
		} catch (Exception ex) {
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Exception occured. Please contact your administrator.");
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
		
		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_SINGLE_VERSION);
	}
	
	protected List<GroupDetails> getGroupDetailsWithSubGroups(String version, ProviewGroupContainer proviewGroupContainer) throws Exception {
		
		Map<String,GroupDetails> groupDetailsMap = new HashMap<String,GroupDetails>();
		List<String> notFound = new ArrayList<String>();
		
		ProviewGroup selectedGroup = proviewGroupContainer.getGroupByVersion(version);
		for (SubgroupInfo subgroup : selectedGroup.getSubgroupInfoList()) {		// multiple subgroups in a group
			for (String titleIdVersion : subgroup.getTitleIdList()){		// for each (split) title in a subgroup
				String titleId = StringUtils.substringBeforeLast(titleIdVersion, "/v").trim();
				String titleMajorVersion = StringUtils.substringAfterLast(titleIdVersion, "/v").trim();	// selected major version for book
				Integer  majorVersion = null;
				if (!titleMajorVersion.equals("")){
					majorVersion = Integer.valueOf(titleMajorVersion);
				}
				try {
					ProviewTitleContainer container = proviewClient.getProviewTitleContainer(titleId);
					if (container != null) {
						for (ProviewTitleInfo title : container.getProviewTitleInfos()){		// for each minor version
							if (title.getMajorVersion().equals(majorVersion)){					// check its major version
								GroupDetails groupDetails = groupDetailsMap.get(title.getVersion());
								if (groupDetails == null) {
									groupDetails = new GroupDetails();
									groupDetailsMap.put(title.getVersion(),groupDetails);
									
									groupDetails.setSubGroupName(subgroup.getSubGroupName());
									groupDetails.setId(titleId);
									groupDetails.setTitleIdList(new ArrayList<ProviewTitleInfo>());
									groupDetails.setProviewDisplayName(title.getTitle());
									groupDetails.setBookVersion(title.getVersion());
								}
								groupDetails.addTitleInfo(title);
							}
						}
					} else {
						notFound.add(titleId);
					}
				} catch (ProviewException e) {
					notFound.add(titleId);
				}
			}
		}
		//	attempt at displaying titles in minor versions not in latest version
		/*
		for (ProviewGroup proviewGroup : proviewGroupContainer.getProviewGroups()){
			if (!proviewGroup.getVersion().equals(Integer.valueOf(version))){
				for (SubgroupInfo subgroup : proviewGroup.getSubgroupInfoList()) {
					for (String titleIdVersion : subgroup.getTitleIdList()){
						String titleId = StringUtils.substringBeforeLast(titleIdVersion, "/v").trim();
						ProviewTitleContainer container = proviewClient.getProviewTitleContainer(titleId);
						for (ProviewTitleInfo title : container.getProviewTitleInfos()){
							GroupDetails groupDetails = groupDetailsMap.get(title.getVersion());
							if (groupDetails != null && !groupDetails.getTitleIdList().contains(title.getTitleId())) {
								groupDetails.getTitleIdList().add(title.getTitleId());
								groupDetails.getTitleIdListWithVersion().add(title.getTitleId()+title.getVersion());
							}
						}
					}
				}
			}
		}
		*/
		if (notFound.size()>0) {
			throw new ProviewException(notFound.toString());
		}
		return new ArrayList<GroupDetails>(groupDetailsMap.values());
	}
	
	/**
	 * For single titles. Gets book details from Proview and removed/deleted details from ProviewAudit
	 * @param fullyQualifiedTitleId
	 * @param bookdefId
	 * @return
	 * @throws Exception
	 */
	protected List<GroupDetails> getGroupDetailsWithNoSubgroups(ProviewGroup proviewGroup)
			throws Exception {
		
		List<GroupDetails> groupDetailsList = new ArrayList<GroupDetails>();
		List<String> notFound = new ArrayList<String>();
		SubgroupInfo subgroup = proviewGroup.getSubgroupInfoList().get(0);
			for (String titleId : subgroup.getTitleIdList()) {
				try {
					groupDetailsList.addAll(proviewClient.getSingleTitleGroupDetails(titleId));
				} catch (ProviewException ex) {
					String errorMsg = ex.getMessage();
					// The versions of the title must have been removed.
					if (errorMsg.contains("does not exist")) {
						notFound.add(titleId);
					}
				}
			}
		for (GroupDetails details : groupDetailsList) {
				details.setId(details.getTitleId() + "/" + details.getBookVersion());
		}
		if (notFound.size()>0) {
			throw new ProviewException(notFound.toString());
		}
		return groupDetailsList;
	}
	
	/**
	 * Handle operational buttons that submit a form of selected rows, or when
	 * the user changes the number of rows displayed at one time.
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_OPERATION, method = RequestMethod.POST)
	public ModelAndView performGroupOperations(HttpSession httpSession,
			@ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) @Valid ProviewGroupListFilterForm form, BindingResult errors,
			Model model) {
		
		log.debug(form);
		
		if (!errors.hasErrors()) {
			GroupCmd command = form.getGroupCmd();
			List<GroupDetails> groupDetails = new ArrayList<GroupDetails>();
			List<String> groupIds = new ArrayList<String>();
			for(String id:((form.getGroupMembers() == null) ? groupIds : form.getGroupMembers())){
				for(GroupDetails subgroup: fetchPaginatedList(httpSession)){
					if(subgroup.getIdWithVersion().equals(id)){
						groupDetails.add(subgroup);
						if(subgroup.getTitleId() == null){
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
				String groupByVersion = form.getProviewGroupID()+"/"+form.getGroupVersion();
				ProviewGroupListFilterForm listFilterForm = new ProviewGroupListFilterForm(form.getGroupName(), form.getBookDefinitionId(), form.getGroupIds(),
						form.getProviewGroupID(), form.getGroupVersion(),groupByVersion, form.isGroupOperation());
				listFilterForm.setGroupMembers(form.getGroupMembers());
				model.addAttribute(WebConstants.KEY_PROVIEW_GROUP_LIST_FILTER_FORM, listFilterForm);
				model.addAttribute(WebConstants.KEY_IS_COMPLETE, "false");
				
			}
			if (command.equals(GroupCmd.PROMOTE)) {
				return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_PROMOTE);
			}
			else if (command.equals(GroupCmd.REMOVE)) {
				return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE);
			}
			else if (command.equals(GroupCmd.DELETE)) {
				return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_DELETE);
			}
			
		}
		//If there is no selection then display the list
		model.addAttribute(WebConstants.KEY_BOOK_ID, form.getBookDefinitionId());
		model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
		model.addAttribute(WebConstants.KEY_GROUP_STATUS, form.getGroupStatus());
		model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, form);
		if ( httpSession.getAttribute(WebConstants.KEY_PAGINATED_LIST) != null){
			model.addAttribute(WebConstants.KEY_PAGINATED_LIST,httpSession.getAttribute(WebConstants.KEY_PAGINATED_LIST));
			model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, httpSession.getAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE));
			model.addAttribute(WebConstants.KEY_SHOW_SUBGROUP, httpSession.getAttribute(WebConstants.KEY_SHOW_SUBGROUP));
		}
		
		model.addAttribute(WebConstants.KEY_GROUP_VERSION,httpSession.getAttribute(WebConstants.KEY_GROUP_VERSION));
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
			@ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) ProviewGroupListFilterForm form, Model model) throws Exception {
		
		model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
		try {
			boolean success = performGroupOperation(form,model,"Promote");
			if(success){
				model.addAttribute(WebConstants.KEY_GROUP_STATUS, "Final");
			}
		} catch (Exception e) {
			
			String emailBody = "Group: " + form.getGroupName() + " could not be promoted to Proview.\n" + e.getMessage();
			String emailSubject = "Proview Promote Request Status: Unsuccessful";
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
			@ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) ProviewGroupListFilterForm form,
			Model model) throws Exception {
		
		model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
		try {
			boolean success =  performGroupOperation(form,model,"Remove");
			if(success){
				model.addAttribute(WebConstants.KEY_GROUP_STATUS, "Remove");
			}
		} catch (Exception e) {
			
			String emailBody = "Group: " + form.getGroupName() + " could not be removed from Proview.\n" + e.getMessage();
			String emailSubject = "Proview Remove Request Status: Unsuccessful";
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
			@ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) ProviewGroupListFilterForm form,
			Model model) throws Exception {
		
		model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
		try {
			boolean success = performGroupOperation(form,model,"Delete");
			if(success){
				model.addAttribute(WebConstants.KEY_GROUP_STATUS, "Delete");
			}
		} catch (Exception e) {
			
			String emailBody = "Group: " + form.getGroupName() + " could not be Deleted from Proview.\n" + e.getMessage();
			String emailSubject = "Proview Delete Request Status: Unsuccessful";
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed: \n" + e.getMessage());
			sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
		}
		
		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_BOOK_DELETE);
	}
	
	
	private void sendEmail(String emailAddressString, String subject, String body) {
		
		ArrayList<InternetAddress> emailAddresses = new ArrayList<InternetAddress>();
		try {
			emailAddresses.add(new InternetAddress(emailAddressString));
			EmailNotification.send(emailAddresses, subject, body);
		} catch (AddressException e) {
			log.error(e);
		}
	}
	
	private boolean performGroupOperation(ProviewGroupListFilterForm form, Model model, String operation ){
		
		String emailBody = "";
		String emailSubject = "Proview "+operation+" Request Status: ";
		
		StringBuffer errorBuffer = new StringBuffer();
		StringBuffer successBuffer = new StringBuffer();
		boolean success = true;
		model.addAttribute(WebConstants.KEY_IS_COMPLETE, "true");
		
		List<ProviewAudit> auditList = new ArrayList<ProviewAudit>();
		String[] titlesString = {};
		for (String bookTitlesWithVersion : form.getGroupIds()) {
			if (!bookTitlesWithVersion.isEmpty()) {
				bookTitlesWithVersion = bookTitlesWithVersion.replaceAll("\\[|\\]|\\{|\\}", "");
				if (!bookTitlesWithVersion.isEmpty()) {
					titlesString = bookTitlesWithVersion.split(",");
				}
			}
		}
		
		for (String bookTitleWithVersion : titlesString) {
			String version = StringUtils.substringAfterLast(bookTitleWithVersion, "/").trim();
			String title = StringUtils.substringBeforeLast(bookTitleWithVersion, "/").trim();
			try {
				doTitleOperation(operation, title, version);
				ProviewAudit audit = new ProviewAudit();
				audit.setTitleId(title);
				audit.setBookVersion(version);
				auditList.add(audit);
				successBuffer.append(
						"Title " + title + " version " + version + " has been " + operation + "d successfully \t\n");
			} catch (Exception e) {
				if (e.getMessage().contains("Title status cannot be changed from Final to Final")) {
					successBuffer.append(title + "/" + version + " unchanged. Status: Final\n");
				} else {
					success = false;
					errorBuffer.append("Failed to " + operation + " title " + title + " and version " + version
							+ ".\t\n" + e.getMessage() + "\t\n\n");
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
				String successMsg = "GroupID " + form.getProviewGroupID() + ", Group version " + form.getGroupVersion()
						+ ", Group name " + form.getGroupName() + " has been " + groupRequest + "d successfully";
				successBuffer.append(successMsg);
				model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: \t\n" + successMsg);
			} catch (Exception e) {
				success = false;
				errorBuffer.append("Failed to " + groupRequest + " group " + form.getProviewGroupID() + " and version "
						+ form.getGroupVersion() + "." + e.getMessage());
			}
		} else if (success && !form.isGroupOperation()) {
			String successMsg = "Selected Titles have been " + operation + "d successfully";
			model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: \t\n" + successMsg);
		} else {
			errorBuffer.append("Group Id " + form.getProviewGroupID() + " Version " + form.getGroupVersion()
					+ " could not be " + operation + "d");
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
		for (ProviewAudit audit : auditList) {
			proviewAuditService.save(form.createAudit(audit.getTitleId(), audit.getBookVersion(), new Date(), // lastUpdate,
					operation.toUpperCase(), form.getComments()));
		}
		return success;
	}
	
	private void doTitleOperation(String operation, String title, String version) throws Exception {
		switch (operation) {
			case "Promote": {
				proviewClient.promoteTitle(title, version);
				TimeUnit.SECONDS.sleep(3);
				break;
			}
			case "Remove": {
				proviewClient.removeTitle(title, version);
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
	
	private void doGroupOperation(String operation, String groupIdByVersion) throws Exception {
		switch (operation) {
			case "Promote": {
				proviewClient.promoteGroup(StringUtils.substringBeforeLast(groupIdByVersion,"/v"),
						StringUtils.substringAfterLast(groupIdByVersion,"/"));
				break;
			}
			case "Remove": {
				proviewClient.removeGroup(StringUtils.substringBeforeLast(groupIdByVersion,"/v"),
						StringUtils.substringAfterLast(groupIdByVersion,"/"));
				break;
			}
			case "Delete": {
				proviewClient.deleteGroup(StringUtils.substringBeforeLast(groupIdByVersion,"/v"),
						StringUtils.substringAfterLast(groupIdByVersion,"/"));
				break;
			}
		}
	}
	
	protected void deleteTitleWithRetryLogic(String title, String version) throws ProviewException {
		boolean retryRequest = true;
		int baseRetryInterval = 15; // in milliseconds 
		int retryCount = 0;
		String errorMsg = "";
		do {
			try {
				proviewClient.deleteTitle(title, version);
				retryRequest = false;
			} catch (ProviewRuntimeException ex) {
				errorMsg = ex.getMessage();
				if (ex.getStatusCode().equals("400") && errorMsg.contains("Title already exists in publishing queue")){
					retryRequest = true;
					retryCount++;
					
					try {
						TimeUnit.SECONDS.sleep(baseRetryInterval);
						//increment by 15 seconds every time
						baseRetryInterval = baseRetryInterval + 15;
					} catch (InterruptedException e) {
						log.error("InterruptedException during HTTP retry", e);
					};
				}else {
					throw new ProviewRuntimeException(errorMsg);
				}
			}
		} while (retryRequest && retryCount < getMaxNumberOfRetries());
		if (retryRequest && retryCount == getMaxNumberOfRetries()) {
			throw new ProviewRuntimeException(
					"Tried 3 times to delete titile and not succeeded. Proview might be down "
					+ "or still in the process of deleting the book. Please try again later. ");
		}
	}
	
	/**
	 * @param proviewClient
	 */
	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
	
	@Required
	public void setProviewAuditService(ProviewAuditService service) {
		this.proviewAuditService = service;
	}

	public ManagerService getManagerService() {
		return managerService;
	}

	@Required
	public void setManagerService(ManagerService managerService) {
		this.managerService = managerService;
	}

	public MessageSourceAccessor getMessageSourceAccessor() {
		return messageSourceAccessor;
	}

	@Required
	public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
		this.messageSourceAccessor = messageSourceAccessor;
	}

	public JobRequestService getJobRequestService() {
		return jobRequestService;
	}

	@Required
	public void setJobRequestService(JobRequestService jobRequestService) {
		this.jobRequestService = jobRequestService;
	}

	public PublishingStatsService getPublishingStatsService() {
		return publishingStatsService;
	}

	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
	
	public int getMaxNumberOfRetries() {
		return this.maxNumberOfRetries;
	}
}
