package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
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
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupForm.DisplayGroupSortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupListFilterForm.GroupCmd;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

@Controller
public class GroupListController extends AbstractGroupController {

	private ProviewClient proviewClient;
	private PublishingStatsService publishingStatsService;
	private BookDefinitionService bookDefinitionService;
	
	private JobRequestService jobRequestService;
	private MessageSourceAccessor messageSourceAccessor;
	private ManagerService managerService;
	private ProviewAuditService proviewAuditService;
	private static final Logger log = Logger.getLogger(GroupListController.class);
	private String classGroupVersion;
	private List<String> classGroupIdList;
	private Validator validator;
	private int SLEEP_TIME = 3000; 
	// retry parameters
    private int baseRetryInterval = 10000; // in ms    
	private int maxNumberOfRetries = 3;
	
	@InitBinder(GroupListFilterForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	public ManagerService getManagerService() {
		return managerService;
	}

	@Required
	public void setValidator(GroupListValidator validator) {
		this.validator = validator;
	}
	
	@Required
	public void setManagerService(ManagerService managerService) {
		this.managerService = managerService;
	}

	@Required
	public void setProviewAuditService(ProviewAuditService service) {
		this.proviewAuditService = service;
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

	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}

	/**
	 * Handle initial in-bound HTTP get request to the page. No query string
	 * parameters are expected.
	 */
	@RequestMapping(value = WebConstants.MVC_BOOK_GROUP_LIST, method = RequestMethod.GET)
	public ModelAndView groupList(HttpSession httpSession, Model model) {
		GroupListFilterForm groupListForm = fetchSavedGroupListForm(httpSession);

		return setupInitialView(model, groupListForm, httpSession);
	}

	/**
	 * Setup of Form and sorting shared by two different incoming HTTP get
	 * request
	 */
	private ModelAndView setupInitialView(Model model, GroupListFilterForm groupListForm, HttpSession httpSession) {
		PageAndSort<DisplayGroupSortProperty> savedPageAndSort = fetchSavedPageAndSort(httpSession);

		GroupForm groupForm = new GroupForm();
		groupForm.setObjectsPerPage(savedPageAndSort.getObjectsPerPage());

		setUpModel(groupListForm, savedPageAndSort, httpSession, model);
		model.addAttribute(GroupForm.FORM_NAME, groupForm);

		return new ModelAndView(WebConstants.VIEW_BOOK_GROUP_LIST);
	}

	/**
	 * Handle paging and sorting of audit list. Handles clicking of column
	 * headers to sort, or use of page number navigation links, like prev/next.
	 */
	@RequestMapping(value = WebConstants.MVC_BOOK_GROUP_LIST_PAGE_AND_SORT, method = RequestMethod.GET)
	public ModelAndView groupListPagingAndSorting(HttpSession httpSession,
			@ModelAttribute(GroupForm.FORM_NAME) GroupForm form, Model model) {
		GroupListFilterForm groupListForm = fetchSavedGroupListForm(httpSession);
		PageAndSort<DisplayGroupSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
		form.setObjectsPerPage(pageAndSort.getObjectsPerPage());
		Integer nextPageNumber = form.getPage();

		if (nextPageNumber != null) { // PAGING
			pageAndSort.setPageNumber(nextPageNumber);
		} else { // SORTING
			pageAndSort.setPageNumber(1);
			pageAndSort.setSortProperty(form.getSort());
			pageAndSort.setAscendingSort(form.isAscendingSort());
		}
		setUpModel(groupListForm, pageAndSort, httpSession, model);

		return new ModelAndView(WebConstants.VIEW_BOOK_GROUP_LIST);
	}

	/**
	 * Handle URL request that the number of rows displayed in table be changed.
	 */
	@RequestMapping(value = WebConstants.MVC_BOOK_GROUP_CHANGE_ROW_COUNT, method = RequestMethod.POST)
	public ModelAndView handleChangeInItemsToDisplay(HttpSession httpSession,
			@ModelAttribute(GroupForm.FORM_NAME) @Valid GroupForm form, Model model) {
		PageAndSort<DisplayGroupSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
		pageAndSort.setPageNumber(1); // Always start from first page again once
										// changing row count to avoid index out
										// of bounds
		pageAndSort.setObjectsPerPage(form.getObjectsPerPage()); 
		// Restore the state of the search filter
		GroupListFilterForm groupForm = fetchSavedGroupListForm(httpSession);
		setUpModel(groupForm, pageAndSort, httpSession, model);
		return new ModelAndView(WebConstants.VIEW_BOOK_GROUP_LIST);
	}

	public String getGroupId(String titleId) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(StringUtils.substringBefore(titleId, "/"));
		buffer.append("/");
		buffer.append(StringUtils.substringAfterLast(titleId, "/"));
		return buffer.toString();
	}

	public Long getGroupVersionByBookDefinition(Long bookDefinitionId) {
		return publishingStatsService.getMaxGroupVersionById(bookDefinitionId);
	}

	/**
	 * This gives titles within the group
	 * 
	 * @param titleId
	 * @param httpSession
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_GROUP_BOOK_ALL_VERSIONS, method = RequestMethod.GET)
	public ModelAndView singleGroupAllVersions(@RequestParam("id") String ebookDefinitionId, HttpSession httpSession,
			Model model, @ModelAttribute(GroupListFilterForm.FORM_NAME) GroupListFilterForm form) throws Exception {
		try {
			BookDefinition bookDefinition = bookDefinitionService.findBookDefinitionByEbookDefId(Long
					.valueOf(ebookDefinitionId));
			String proviewGroupId = getGroupId(bookDefinition.getFullyQualifiedTitleId());
			Long lastGroupVersionSubmitted = getGroupVersionByBookDefinition(bookDefinition.getEbookDefinitionId());
			String proviewResponse = null;
			List<ProviewGroupInfo> proviewGroupInfoList = new ArrayList<ProviewGroupInfo>();

			if (lastGroupVersionSubmitted != null) {
				try {
					proviewResponse = getGroupInfoByVersion(proviewGroupId, lastGroupVersionSubmitted);
				} catch (Exception ex) {
					model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
							"Proview Exception occured. Please contact your administrator.");
					log.debug(ex.getMessage());
				}
			}

			if (proviewResponse != null) {
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setNamespaceAware(true);
				XMLReader reader = parserFactory.newSAXParser().getXMLReader();
				GroupXMLHandler groupXMLHandler = new GroupXMLHandler();
				reader.setContentHandler(groupXMLHandler);
				reader.parse(new InputSource(new StringReader(proviewResponse)));
				Map<String, String> versionSubGroupMap = groupXMLHandler.getVersionSubGroupMap();

				if (!versionSubGroupMap.isEmpty()) {

					model.addAttribute(WebConstants.KEY_GROUP_NAME, groupXMLHandler.getGroupName());
					model.addAttribute(WebConstants.KEY_GROUP_STATUS, groupXMLHandler.getGroupStatus());
					model.addAttribute(WebConstants.KEY_PROVIEW_GROUP_ID, proviewGroupId);
					model.addAttribute(WebConstants.KEY_GROUP_VERSION, classGroupVersion);

					httpSession.setAttribute(WebConstants.KEY_GROUP_VERSION, classGroupVersion);
					httpSession.setAttribute(WebConstants.KEY_GROUP_STATUS, groupXMLHandler.getGroupStatus());

					List<SplitNodeInfo> splitNodes = bookDefinition.getSplitNodesAsList();
					proviewGroupInfoList = buildProviewGroupInfoList(splitNodes, ebookDefinitionId, versionSubGroupMap,
							model, httpSession);
				}

			}

			if (proviewGroupInfoList != null) {
				model.addAttribute(WebConstants.KEY_PAGINATED_LIST, proviewGroupInfoList);
				model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, proviewGroupInfoList.size());
				model.addAttribute(GroupListFilterForm.FORM_NAME, form);

				httpSession.setAttribute(GroupListFilterForm.FORM_NAME, form);
				httpSession.setAttribute(WebConstants.KEY_PAGINATED_LIST, proviewGroupInfoList);
				httpSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, proviewGroupInfoList.size());

			}
			model.addAttribute(WebConstants.KEY_PILOT_BOOK_STATUS, bookDefinition.getPilotBookStatus());
			model.addAttribute(WebConstants.KEY_BOOK_ID, ebookDefinitionId);
		} catch (Exception ex) {
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Exception occured. Please contact your administrator.");
			log.debug(ex.getMessage());
		}

		return new ModelAndView(WebConstants.VIEW_GROUP_TITLE_ALL_VERSIONS);

	}
	
	public List<ProviewGroupInfo> buildProviewGroupInfoList(List<SplitNodeInfo> splitNodes, String ebookDefinitionId, Map<String,String> versionSubGroupMap, Model model, HttpSession httpSession){
		List<ProviewGroupInfo> proviewGroupInfoList = new ArrayList<ProviewGroupInfo>();
		List<String> groupIds = new ArrayList<String>();
		
		Map<String, List<String>> versionTitlesMap = getVersionTitleMapFromSplitNodeList(splitNodes);
		List<String> uniqueVersion = new ArrayList<String>();
		
		for (SplitNodeInfo splitNodeInfo : splitNodes) {			

			// Split Title nodes doesn't have prefix v in version
			String splitTitle = splitNodeInfo.getSplitBookTitle();
			String version = "v" + splitNodeInfo.getBookVersionSubmitted();
			String majorVersion = null;
			if (StringUtils.contains(version, '.')) {
				majorVersion = StringUtils.substringBefore(version, ".");
			} else {
				majorVersion = version;
			}
			if (versionSubGroupMap.containsKey(majorVersion)) {

				String subgroupName = versionSubGroupMap.get(majorVersion);
				if (!uniqueVersion.contains(version)) {
					ProviewGroupInfo proviewGroupInfo = new ProviewGroupInfo();
					proviewGroupInfo.setSubGroupName(subgroupName);
					proviewGroupInfo.setVersion(version);
					String bookStatus = proviewAuditService.getBookStatus(splitTitle,version);
					if (bookStatus == null) {
						bookStatus = "REVIEW";
					}
					else if (bookStatus.equalsIgnoreCase("PROMOTE")) {
						bookStatus = "FINAL";
					}
					proviewGroupInfo.setStatus(bookStatus);
					
					uniqueVersion.add(version);
					proviewGroupInfo.setId(ebookDefinitionId + "/" + version);
					groupIds.add(ebookDefinitionId + "/" + version);
					proviewGroupInfoList.add(proviewGroupInfo);
				}
			}
		}
		
		for (ProviewGroupInfo pGroupInfo : proviewGroupInfoList) {
			if (versionTitlesMap.containsKey(pGroupInfo.getVersion())) {
				pGroupInfo.setSplitTitles(versionTitlesMap.get(pGroupInfo.getVersion()));
			}
		}
		model.addAttribute(WebConstants.KEY_GROUP_IDS, groupIds);
		httpSession.setAttribute(WebConstants.KEY_GROUP_IDS, groupIds);
		return proviewGroupInfoList;
	}


	public String getGroupInfoByVersion(String groupId, Long groupVersion) throws Exception {
		String response = null;	
		do {
			try {
				response = proviewClient.getProviewGroupInfo(groupId, "v" + groupVersion.toString());
				classGroupVersion = "v" + groupVersion;
				return response;
			} catch (ProviewRuntimeException ex) {
				if (ex.getStatusCode().equals("400") && ex.toString().contains("No such group id and version exist")) {
					// go down the version by one if the current version is
					// deleted in Proview
					groupVersion = groupVersion - 1;
				} else {
					throw new Exception(ex);
				}
			}
			
		} while (groupVersion > 0);
		return response;
	}

	/**
	 * Handle operational buttons that submit a form of selected rows, or when
	 * the user changes the number of rows displayed at one time.
	 */
	@RequestMapping(value = WebConstants.MVC_GROUP_OPERATION, method = RequestMethod.POST)
	public ModelAndView performGroupOperations(HttpSession httpSession,
			@ModelAttribute(GroupListFilterForm.FORM_NAME) @Valid GroupListFilterForm form, BindingResult errors,
			Model model) {
		
		log.debug(form);
		classGroupIdList = new ArrayList<String>();
		
		if (!errors.hasErrors()) {
			GroupCmd command = form.getGroupCmd();
			if (form.getGroupIds() != null && form.getGroupIds().size() > 0) {
				classGroupIdList.addAll(form.getGroupIds());
				model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
				model.addAttribute(WebConstants.KEY_GROUP_STATUS, form.getGroupStatus());
				model.addAttribute(WebConstants.KEY_BOOK_ID, form.getBookDefinitionId());
				model.addAttribute(WebConstants.KEY_PROVIEW_GROUP_FORM,
						new GroupListFilterForm(form.getGroupName(), form.getBookDefinitionId(), form.getGroupIds(),
								form.getProviewGroupID(), form.getGroupVersion()));
				model.addAttribute(WebConstants.KEY_IS_COMPLETE, "false");

			}
			if (command.equals(GroupCmd.PROMOTE)) {
				return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_PROMOTE);
			}
			else if (command.equals(GroupCmd.REMOVE)) {
				return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_REMOVE);
			}
			else if (command.equals(GroupCmd.DELETE)) {
				return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_DELETE);
			}

		}		
		//If there is no selection then display the list
		model.addAttribute(WebConstants.KEY_BOOK_ID, form.getBookDefinitionId());
		model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
		model.addAttribute(WebConstants.KEY_GROUP_STATUS, form.getGroupStatus());
		model.addAttribute(GroupListFilterForm.FORM_NAME, form);
		if ( httpSession.getAttribute(WebConstants.KEY_PAGINATED_LIST) != null){
			model.addAttribute(WebConstants.KEY_PAGINATED_LIST,httpSession.getAttribute(WebConstants.KEY_PAGINATED_LIST));
			model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, httpSession.getAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE));
		}
		model.addAttribute(WebConstants.KEY_GROUP_VERSION,httpSession.getAttribute(WebConstants.KEY_GROUP_VERSION));
		return new ModelAndView(WebConstants.VIEW_GROUP_TITLE_ALL_VERSIONS);
	}
	
	
	public Map<String, List<String>> getVersionTitleMapFromSplitNodeList(List<SplitNodeInfo> splitNodes){
		Map<String, List<String>> versionTitlesMap = new HashMap<String, List<String>>();
		
		for (SplitNodeInfo splitNode : splitNodes) {
			String version = "v"+splitNode.getBookVersionSubmitted();
			String splitTitle = splitNode.getSplitBookTitle();

			if (versionTitlesMap.containsKey(version)) {
				List<String> titles = versionTitlesMap.get(version);
				titles.add(splitTitle);
				versionTitlesMap.put(version, titles);
			} else {
				List<String> titles = new ArrayList<String>();
				String firstSplitTitle = StringUtils.substringBeforeLast(splitTitle, "_pt");
				titles.add(firstSplitTitle);
				titles.add(splitTitle);
				versionTitlesMap.put(version, titles);
			}
		}
		return versionTitlesMap;
	}

	/**
	 * 
	 * @param form
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_PROMOTE, method = RequestMethod.POST)
	public ModelAndView proviewTitlePromotePost(
			@ModelAttribute(GroupListFilterForm.FORM_NAME) GroupListFilterForm form, Model model) throws Exception {

		
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

		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_PROMOTE);
	}
	
	
	private boolean performGroupOperation(GroupListFilterForm form, Model model, String operation){
		String emailBody = "";
		String emailSubject = "Proview "+operation+" Request Status: ";
		
		StringBuffer errorBuffer = new StringBuffer();
		StringBuffer successBuffer = new StringBuffer();
		boolean success = true;
		model.addAttribute(WebConstants.KEY_IS_COMPLETE, "true");
		
		List<ProviewAudit> auditList = new ArrayList<ProviewAudit>();
		if (!isJobRunningForBook(model,form.getBookDefinitionId())) {
			BookDefinition bookDefinition = bookDefinitionService.findBookDefinitionByEbookDefId(new Long(form
					.getBookDefinitionId()));
			Date lastUpdate = bookDefinition.getLastUpdated();
			
			PublishingStats stats = publishingStatsService.findStatsByLastUpdated(bookDefinition.getEbookDefinitionId());
			
			List<SplitNodeInfo> splitNodes = bookDefinition.getSplitNodesAsList();
			Map<String, List<String>> versionTitlesMap = getVersionTitleMapFromSplitNodeList(splitNodes);

			for (String groupId : classGroupIdList) {
				String version = StringUtils.substringAfterLast(groupId, "/");
				
				if (versionTitlesMap.containsKey(version)) {
					List<String> titles = versionTitlesMap.get(version);
					for (String title : titles) {
						try {
							doTitleOperation(operation, title, version, stats.getGatherTocNodeCount().intValue());
							ProviewAudit audit = new ProviewAudit();
							audit.setTitleId(title);
							audit.setBookVersion(version);
							auditList.add(audit);
							successBuffer.append("Title " + title + " version " + version
									+ " has been "+operation+"d successfully \t\n");
						} catch (Exception e) {
							success = false;
							errorBuffer.append("Failed to "+operation+" title " + title + " and version " + version + ".\t\n"
									+ e.getMessage() + "\t\n\n");
						}
					}
				}
			}

			if (success) {
				try {
					doGroupOperation(operation, form.getProviewGroupID(),form.getGroupVersion());
					String successMsg = "GroupID " + form.getProviewGroupID() + ", Group version "
							+ form.getGroupVersion() + ", Group name " + form.getGroupName()
							+ " has been "+operation+"d successfully";
					successBuffer.append(successMsg);
					model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: \t\n" + successMsg);
				} catch (Exception e) {
					success = false;
					errorBuffer.append("Failed to "+operation+" group " + form.getProviewGroupID() + " and version "
							+ form.getGroupVersion() + "." + e.getMessage());
				}
			}
			else{
				errorBuffer.append("Group Id "+form.getProviewGroupID()+" Version "+form.getGroupVersion()+" could not be "+operation+"d");
			}

			if (success) {
				emailSubject += "Success";
				emailBody = successBuffer.toString();
			} else {
				emailSubject += "Failed";
				model.addAttribute(WebConstants.KEY_GROUP_STATUS, form.getGroupStatus());
				if (successBuffer.length() > 0) {
					successBuffer.append(errorBuffer);
					model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
							"Partial failure: \t\n" + successBuffer.toString());
					emailBody = "Partial failure: \n" + successBuffer.toString();
				} else {
					model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed: \t\n" + errorBuffer.toString());
					emailBody = "Failed: \t\n" + errorBuffer.toString();
				}

			}

			sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);

			for (ProviewAudit audit : auditList) {
				proviewAuditService.save(form.createAudit(audit.getTitleId(), audit.getBookVersion(), lastUpdate,
						operation.toUpperCase()));
			}

		}
		return success;
	}
	
	private void doTitleOperation(String operation, String title, String version, int tocCount) throws Exception {
		switch (operation) {
			case "Promote": {
				proviewClient.promoteTitle(title, version);
				Thread.sleep(SLEEP_TIME);
				break;
			}
			case "Remove": {
				proviewClient.removeTitle(title, version);
				Thread.sleep(SLEEP_TIME);
				break;
			}
			case "Delete": {
				deleteTitleWithRetryLogic(title, version, tocCount);
				Thread.sleep(baseRetryInterval);
				break;
			}
		}
	}
	
	private void doGroupOperation(String operation, String groupId, String groupVersion) throws Exception {
		switch (operation) {
			case "Promote": {
				proviewClient.promoteGroup(groupId,groupVersion);
				break;
			}
			case "Remove": {
				proviewClient.removeGroup(groupId,groupVersion);
				break;
			}
			case "Delete": {
				proviewClient.deleteGroup(groupId,groupVersion);
				break;
			}
		}
	}
	
	public void deleteTitleWithRetryLogic(String title, String version, int tocCount) throws ProviewException {
		boolean retryRequest = true;

		int retryCount = 0;
		String errorMsg = "";
		do {
			try {
				proviewClient.deleteTitle(title, version);
				retryRequest = false;
			} catch (ProviewRuntimeException ex) {
				errorMsg = ex.getMessage();
				if (ex.getStatusCode().equals("400") && errorMsg.contains("Title already exists in publishing queue")){

					// retry a retriable request
					int computedRetryInterval = baseRetryInterval + (baseRetryInterval * tocCount);
					log.warn("Retriable status received: waiting " + SLEEP_TIME + "ms (retryCount: "
							+ retryCount +")");

					retryRequest = true;
					retryCount++;

					try {
						Thread.sleep(computedRetryInterval);
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
	
	public int getMaxNumberOfRetries() {
        return this.maxNumberOfRetries;
    }	
	
	
	/**
	 * 
	 * @param form
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_REMOVE, method = RequestMethod.POST)
	public ModelAndView proviewGroupRemovePost(
			@ModelAttribute(GroupListFilterForm.FORM_NAME) GroupListFilterForm form,
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

		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_REMOVE);
	}
	
	/**
	 * 
	 * @param form
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_DELETE, method = RequestMethod.POST)
	public ModelAndView proviewGroupDeletePost(
			@ModelAttribute(GroupListFilterForm.FORM_NAME) GroupListFilterForm form,
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

		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_DELETE);
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

	private boolean isJobRunningForBook(Model model, Long ebookDefId) {

		boolean isJobRunning = false;

		BookDefinition book = bookDefinitionService.findBookDefinitionByEbookDefId(ebookDefId);
		if (book != null) {

			if (jobRequestService.isBookInJobRequest(ebookDefId)) {
				Object[] args = { book.getFullyQualifiedTitleId(), "", "This book is already in the job queue" };
				String infoMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.fail", args);
				model.addAttribute(WebConstants.KEY_ERR_MESSAGE, infoMessage);
				isJobRunning = true;
			}

			else {
				JobExecution runningJobExecution = managerService.findRunningJob(book.getEbookDefinitionId());

				if (runningJobExecution != null) {
					Object[] args = { book.getGroupName(), book.getProviewDisplayName(),
							runningJobExecution.getId().toString() };
					String infoMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.in.progress", args);
					model.addAttribute(WebConstants.KEY_ERR_MESSAGE, infoMessage);
					isJobRunning = true;
				}
			}
		}
		return isJobRunning;
	}

	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
	}

}
