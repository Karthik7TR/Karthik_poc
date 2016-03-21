package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

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
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.GroupCmd;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

@Controller
public class ProviewGroupListController {
	
	private ProviewClient proviewClient;
	private BookDefinitionService bookDefinitionService;
	private ProviewAuditService proviewAuditService;
	private ManagerService managerService;
	private MessageSourceAccessor messageSourceAccessor;
	private JobRequestService jobRequestService;
	private PublishingStatsService publishingStatsService;

	private static final Logger log = Logger.getLogger(ProviewGroupListController.class);
	private int SLEEP_TIME = 3000; 
	// retry parameters
    private int baseRetryInterval = 10000; // in milliseconds    
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

			List<ProviewGroup> allProviewGroups = proviewClient.getAllProviewGroupInfo();
			List<ProviewGroup> allLatestProviewGroups = allProviewGroups; //getAdditionDetailsFromEbook(allProviewGroups, httpSession);

			saveAllProviewGroups(httpSession, allProviewGroups);
			saveAllLatestProviewGroups(httpSession,
					allLatestProviewGroups);
			saveSelectedProviewGroups(httpSession, allLatestProviewGroups);

			if (allLatestProviewGroups != null) {
				model.addAttribute(WebConstants.KEY_PAGINATED_LIST,
						allLatestProviewGroups);
				model.addAttribute(WebConstants.KEY_TOTAL_GROUP_SIZE,
						allLatestProviewGroups.size());
			}

			model.addAttribute(ProviewGroupListFilterForm.FORM_NAME,
			  	new ProviewGroupListFilterForm());

			ProviewGroupForm proviewGroupForm = fetchSavedProviewGroupForm(httpSession);
			if (proviewGroupForm == null) {
				proviewGroupForm = new ProviewGroupForm();
				proviewGroupForm
						.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
				saveProviewGroupForm(httpSession, proviewGroupForm);
			}
			model.addAttribute(ProviewGroupForm.FORM_NAME, proviewGroupForm);
			model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewGroupForm.getObjectsPerPage());
			break;

		case PAGESIZE:
			saveProviewGroupForm(httpSession, form);
			List<ProviewGroup> selectedProviewGroup = fetchSelectedProviewGroups(httpSession);
			model.addAttribute(WebConstants.KEY_PAGINATED_LIST,
					selectedProviewGroup);
			model.addAttribute(WebConstants.KEY_TOTAL_GROUP_SIZE,
					selectedProviewGroup.size());
			model.addAttribute(WebConstants.KEY_PAGE_SIZE,
					form.getObjectsPerPage());
			model.addAttribute(ProviewGroupListFilterForm.FORM_NAME,
					fetchSavedProviewGroupListFilterForm(httpSession));
			model.addAttribute(ProviewGroupForm.FORM_NAME, form);

			break;
		}

		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUPS);
	}

	@RequestMapping(value = WebConstants.MVC_PROVIEW_GROUPS, method = RequestMethod.GET)
	public ModelAndView allLatestProviewGroupsList(HttpSession httpSession, Model model) {
		
		List<ProviewGroup> selectedProviewGroups = fetchSelectedProviewGroups(httpSession);

		if (selectedProviewGroups == null) {

			List<ProviewGroup> allLatestProviewGroups = fetchAllLatestProviewGroups(httpSession);
			if (allLatestProviewGroups == null) {

				List<ProviewGroup> allProviewGroups = fetchAllProviewGroups(httpSession);
				
				try {
					if (allProviewGroups == null) {
						allProviewGroups = proviewClient.getAllProviewGroupInfo();
					}

					saveAllLatestProviewGroups(httpSession, allProviewGroups);

					selectedProviewGroups = allProviewGroups;

					saveSelectedProviewGroups(httpSession, selectedProviewGroups);

				} catch (ProviewException e) {
					model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
							"Proview Exception occured. Please contact your administrator.");

				}
			}

		}

		if (selectedProviewGroups != null) {

			model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewGroups);
			model.addAttribute(WebConstants.KEY_TOTAL_GROUP_SIZE, selectedProviewGroups.size());
		}

		model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, fetchSavedProviewGroupListFilterForm(httpSession));

		ProviewGroupForm proviewGroupForm = fetchSavedProviewGroupForm(httpSession);
		if (proviewGroupForm == null) {
			proviewGroupForm = new ProviewGroupForm();
			proviewGroupForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
			saveProviewGroupForm(httpSession, proviewGroupForm);
		}

		model.addAttribute(ProviewGroupForm.FORM_NAME, proviewGroupForm);
		model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewGroupForm.getObjectsPerPage());

		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUPS);
	}
	
	@RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_BOOK_VERSIONS, method = RequestMethod.GET)
	public ModelAndView singleGroupTitleAllVersions(@RequestParam("groupIdByVersion") String groupIdByVersion,
			HttpSession httpSession, Model model,
			@ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) ProviewGroupListFilterForm form) throws Exception {

		try {
			
			String groupId = StringUtils.substringBeforeLast(groupIdByVersion, "/v");
			String version = StringUtils.substringAfterLast(groupIdByVersion, "/v");
			
			String proviewResponse = null;
			
			List<GroupDetails> groupDetailsList = new ArrayList<GroupDetails>();
				try {

					proviewResponse = getGroupInfoByVersion(groupId, new Long(version));
				} catch (Exception ex) {
					model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
							"Proview Exception occured. Please contact your administrator.");
					log.debug(ex.getMessage());
				}			

			
			if (proviewResponse != null) {
				PilotBookStatus pilotBookStatus = PilotBookStatus.FALSE;
				String bookDefinitionId = "";

				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setNamespaceAware(true);
				XMLReader reader = parserFactory.newSAXParser().getXMLReader();
				GroupXMLParser groupXMLParser = new GroupXMLParser();
				reader.setContentHandler(groupXMLParser);
				reader.parse(new InputSource(new StringReader(proviewResponse)));
				String headTitleID = StringUtils.substringBeforeLast(groupXMLParser.getHeadTitle(), "/v");
				BookDefinition bookDefinition = bookDefinitionService.findBookDefinitionByTitle(headTitleID);
				
				model.addAttribute(WebConstants.KEY_GROUP_NAME, groupXMLParser.getGroupName());
				model.addAttribute(WebConstants.KEY_GROUP_STATUS, groupXMLParser.getGroupStatus());
				model.addAttribute(WebConstants.KEY_PROVIEW_GROUP_ID, groupId);
				model.addAttribute(WebConstants.KEY_GROUP_VERSION, version);
				model.addAttribute(WebConstants.KEY_GROUP_BY_VERSION_ID, groupIdByVersion);

				httpSession.setAttribute(WebConstants.KEY_GROUP_BY_VERSION_ID, groupIdByVersion);
				httpSession.setAttribute(WebConstants.KEY_GROUP_VERSION, version);
				httpSession.setAttribute(WebConstants.KEY_GROUP_STATUS, groupXMLParser.getGroupStatus());
				httpSession.setAttribute(WebConstants.KEY_GROUP_NAME, groupXMLParser.getGroupName());


				if (bookDefinition != null) {

					pilotBookStatus = bookDefinition.getPilotBookStatus();
					bookDefinitionId = bookDefinition.getEbookDefinitionId().toString();

					Map<String, String> subGroupVersionMap = groupXMLParser.getSubGroupVersionMap();
					if(subGroupVersionMap != null && subGroupVersionMap.size() > 0){
						groupDetailsList = getGroupDetailsforSplitTitles(bookDefinition, subGroupVersionMap, bookDefinitionId, model);
					}
					
					model.addAttribute(WebConstants.KEY_PILOT_BOOK_STATUS, pilotBookStatus);
					model.addAttribute(WebConstants.KEY_BOOK_ID, bookDefinitionId);
					
					httpSession.setAttribute(WebConstants.KEY_PILOT_BOOK_STATUS, pilotBookStatus);
					httpSession.setAttribute(WebConstants.KEY_BOOK_ID, bookDefinitionId);
				}

			}

			if (groupDetailsList != null) {	
				
				model.addAttribute(WebConstants.KEY_PAGINATED_LIST, groupDetailsList);
				model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, groupDetailsList.size());
				model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, form);
				//Is this needed
				model.addAttribute(WebConstants.KEY_PROVIEW_GROUP_ID, groupId);

				httpSession.setAttribute(ProviewGroupListFilterForm.FORM_NAME, form);
				httpSession.setAttribute(WebConstants.KEY_PAGINATED_LIST, groupDetailsList);
				httpSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, groupDetailsList.size());

			}
			

		} catch (Exception ex) {
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Exception occured. Please contact your administrator.");
			log.debug(ex.getStackTrace());
		}

		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_TITLE_ALL_VERSIONS);		
		
	}
	
	
	protected Map<String,List<String>> getSplitTitlesFromEbook(List<SplitNodeInfo> splitNodes){
		Map<String,List<String>> versionSplitTitleMap = new HashMap<String,List<String>>();
		for(SplitNodeInfo splitNode : splitNodes ){
			String splitTitle = splitNode.getSplitBookTitle();
			String splitBookVersion = "v"+splitNode.getBookVersionSubmitted();
			if (versionSplitTitleMap.containsKey(splitBookVersion)){
				List<String> splitTitleList = new ArrayList<String>();
				splitTitleList  = versionSplitTitleMap.get(splitBookVersion);
				splitTitleList.add(splitTitle);
				versionSplitTitleMap.put(splitBookVersion,splitTitleList);
			}
			else{
				
				List<String> splitTitleList = new ArrayList<String>();	
				//This will give the Fist Title of the parts
				splitTitleList.add(StringUtils.substringBeforeLast(splitTitle, "_pt"));
				splitTitleList.add(splitTitle);
				versionSplitTitleMap.put(splitBookVersion,splitTitleList);
			}
		}
		return versionSplitTitleMap;
	}
	
	protected Map<String,List<String>> getTitleInfoFromProview(String fullyQualifiedTitleId, Map<String, String> subGroupVersionMap, Map<String, GroupDetails> proviewTitleInfoList) throws Exception{
		List<String> allMajorVersions = new ArrayList<String>();
		
		Map<String,List<String>> versionTitleMap = new HashMap<String,List<String>>();
		for (Map.Entry<String, String> entry : subGroupVersionMap.entrySet()) {
			String majorVersionOfSubgroup = "v"+StringUtils.substringBefore(entry.getValue(), ".");
			if(!allMajorVersions.contains(majorVersionOfSubgroup)){
				allMajorVersions.add(majorVersionOfSubgroup);
			}
		}
		
		//If title status is removed Proview throws 404 status code
		try{
			String response = proviewClient.getSinglePublishedTitle(fullyQualifiedTitleId);
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			SingleTitleParser singleTitleParser = new SingleTitleParser();
			reader.setContentHandler(singleTitleParser);
			reader.parse(new InputSource(new StringReader(response)));			
		    List<GroupDetails> proviewGroupDetails = singleTitleParser.getGroupDetailsList();
		
			for(GroupDetails groupDetail : proviewGroupDetails){
				//This condition will make sure only versions that are in the group are added
				String majorVersion = StringUtils.substringBefore(groupDetail.getBookVersion(), ".");
				if(allMajorVersions.contains(majorVersion)){
					proviewTitleInfoList.put(groupDetail.getBookVersion(), groupDetail );
					List<String> titles = new ArrayList<String>();
					titles.add(fullyQualifiedTitleId);
					versionTitleMap.put(groupDetail.getBookVersion(), titles);
				}
			}
		}
		catch (ProviewException ex) {
				String errorMsg = ex.getMessage();
				//The versions of the title must have been removed.
				if (errorMsg.startsWith("404") && errorMsg.contains("does not exist")){
					return versionTitleMap;
				}
			
		}
		
		return versionTitleMap;
	}
	
	protected Map<String,List<String>> mergeVersionTitlesMap(Map<String,List<String>> splitMap, Map<String,List<String>> titleMap){
		Map<String,List<String>> versionSingleSplitMap = new HashMap<String,List<String>>();
		if (splitMap.isEmpty() ){
			return titleMap;
		}
		else if(titleMap.isEmpty()){
			return splitMap;
		}
		else{
			versionSingleSplitMap.putAll(splitMap);
			for (Map.Entry<String, List<String>> entry : titleMap.entrySet()) {
				if(!splitMap.containsKey(entry.getKey())){
					versionSingleSplitMap.put(entry.getKey(), entry.getValue());
				}				
			}
		}
		return versionSingleSplitMap;
	}
	
	 
	
	protected List<GroupDetails> getGroupDetailsforSplitTitles(BookDefinition bookDefinition,
			Map<String, String> subGroupVersionMap, String bookDefinitionId, Model model) throws Exception {
		List<GroupDetails> groupDetailsList = new ArrayList<GroupDetails>();
		List<SplitNodeInfo> splitNodes = bookDefinition.getSplitNodesAsList();

		// This gives all the parts of of titleId for each version
		Map<String, List<String>> versionSplitTitleMap = getSplitTitlesFromEbook(splitNodes);
		// ProviewDisplayName,status information from Proview
		Map<String, GroupDetails> proviewTitleInfoList = new HashMap<String, GroupDetails>();
		// This gives version and their corresponding titles from Proview
		Map<String, List<String>> versionTitleMap = getTitleInfoFromProview(bookDefinition.getFullyQualifiedTitleId(),
				subGroupVersionMap, proviewTitleInfoList);
		// Both titles from SplitTitles and Proview titles are merged together
		versionSplitTitleMap = mergeVersionTitlesMap(versionSplitTitleMap, versionTitleMap);

		List<String> uniqueVersion = new ArrayList<String>();

		if (!subGroupVersionMap.isEmpty()) {
			// entry.getValue is major version
			for (Map.Entry<String, String> entry : subGroupVersionMap.entrySet()) {

				Map<String, List<String>> filteredSplitTitles = filterSplitTitles(versionSplitTitleMap,
						entry.getValue());

				for (Map.Entry<String, List<String>> map : filteredSplitTitles.entrySet()) {

					String splitVersion = map.getKey();

					List<String> splitTitlesofVersion = map.getValue();

					GroupDetails groupDetails = null;
					for (String splitTitleId : splitTitlesofVersion) {

						if (!uniqueVersion.contains(splitVersion)) {

							uniqueVersion.add(splitVersion);

							groupDetails = new GroupDetails();
							groupDetails.setSubGroupName(entry.getKey());

							List<String> titleIdList = new ArrayList<String>();
							titleIdList.add(splitTitleId);
							groupDetails.setTitleIdList(titleIdList);

							groupDetails.setBookVersion(map.getKey());

							groupDetails.setId(bookDefinitionId + "/" + splitVersion);

							List<String> titlesWithVersion = new ArrayList<String>();
							titlesWithVersion.add(splitTitleId + "/" + splitVersion);
							groupDetails.setTitleIdListWithVersion(titlesWithVersion);

							if (proviewTitleInfoList.containsKey(splitVersion)) {
								GroupDetails titileInfo = proviewTitleInfoList.get(splitVersion);
								groupDetails.setProviewDisplayName(titileInfo.getProviewDisplayName());
								groupDetails.setBookStatus(titileInfo.getBookStatus());
								groupDetailsList.add(groupDetails);

							} else {
								String status = proviewAuditService.getBookStatus(splitTitleId, splitVersion);
								groupDetails.setProviewDisplayName(bookDefinition.getProviewDisplayName());
								groupDetails.setBookStatus(status);
								groupDetailsList.add(groupDetails);
							}

						} else {

							List<String> titleIdList = groupDetails.getTitleIdList();
							titleIdList.add(splitTitleId);
							groupDetails.setTitleIdList(titleIdList);

							List<String> titlesWithVersion = groupDetails.getTitleIdListWithVersion();
							titlesWithVersion.add(splitTitleId + "/" + groupDetails.getBookVersion());
							groupDetails.setTitleIdListWithVersion(titlesWithVersion);

						}
					}
				}
			}

		}
		return groupDetailsList;
	}

	/**
	 * Gets all the titles of minor/major version
	 * @param versionSplitTitleMap
	 * @param version
	 * @return
	 */
	public Map<String, List<String>> filterSplitTitles(Map<String,List<String>> versionSplitTitleMap, String version){
		Map<String, List<String>> filterTitles = new HashMap<String, List<String>>();
		for (Map.Entry<String, List<String>> map : versionSplitTitleMap.entrySet()) {
			String minorVersion = map.getKey();
			if(minorVersion.startsWith("v"+version)){				
				filterTitles.put(minorVersion, map.getValue());
			}
		}
		return filterTitles;
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
			if (form.getGroupIds() != null && form.getGroupIds().size() > 0) {				
				
				model.addAttribute(WebConstants.KEY_GROUP_NAME, form.getGroupName());
				model.addAttribute(WebConstants.KEY_GROUP_STATUS, form.getGroupStatus());
				model.addAttribute(WebConstants.KEY_BOOK_ID, form.getBookDefinitionId());
				String groupByVersion = form.getProviewGroupID()+"/"+form.getGroupVersion();
				model.addAttribute(WebConstants.KEY_PROVIEW_GROUP_LIST_FILTER_FORM,
						new ProviewGroupListFilterForm(form.getGroupName(), form.getBookDefinitionId(), form.getGroupIds(),
								form.getProviewGroupID(), form.getGroupVersion(),groupByVersion, form.isGroupOperation()));
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
		}
		
		model.addAttribute(WebConstants.KEY_GROUP_VERSION,httpSession.getAttribute(WebConstants.KEY_GROUP_VERSION));
		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUP_TITLE_ALL_VERSIONS);
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
	
	private boolean performGroupOperation(ProviewGroupListFilterForm form, Model model, String operation ){
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
			
			String[] titlesString = {} ;
			for (String bookTitlesWithVersion : form.getGroupIds()) {
				if(!bookTitlesWithVersion.isEmpty()){
					bookTitlesWithVersion=bookTitlesWithVersion.replaceAll("\\[|\\]", "");
					titlesString = bookTitlesWithVersion.split(",");	
				}
			}

			for (String bookTitleWithVersion : titlesString) {
				String version = StringUtils.substringAfterLast(bookTitleWithVersion, "/").trim();
				String title = StringUtils.substringBeforeLast(bookTitleWithVersion, "/").trim();
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

			
			if (success && form.isGroupOperation()) {
				try {
					doGroupOperation(operation, form.getProviewGroupID(),"v"+form.getGroupVersion());
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
			else if(success && !form.isGroupOperation()){
				String successMsg = "Selected Titles have been "+operation+"d successfully";
				model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: \t\n" + successMsg);
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
						operation.toUpperCase(), form.getComments()));
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
		
	protected void deleteTitleWithRetryLogic(String title, String version, int tocCount) throws ProviewException {
		boolean retryRequest = true;

		int retryCount = 0;
		String errorMsg = "";
		do {
			try {
				proviewClient.deleteTitle(title, version);
				retryRequest = false;
			} catch (ProviewRuntimeException ex) {
				errorMsg = ex.getMessage();
				if (errorMsg.startsWith("400") && errorMsg.contains("Title already exists in publishing queue")){

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

	
	protected Map<String, List<String>> getVersionTitleMapFromSplitNodeList(List<SplitNodeInfo> splitNodes){
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
	
	protected String getGroupInfoByVersion(String groupId, Long groupVersion) throws Exception {
		String response = null;	
		do {
			try {
				response = proviewClient.getProviewGroupInfo(groupId, "v" + groupVersion.toString());
				return response;
			} catch (ProviewRuntimeException ex) {
				if (ex.getMessage().startsWith("400") && ex.toString().contains("No such group id and version exist")) {
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
	 * 
	 * @param httpSession
	 * @param form
	 */
	private void saveProviewGroupForm(HttpSession httpSession,
			ProviewGroupForm form) {
		httpSession.setAttribute(ProviewGroupForm.FORM_NAME, form);

	}
	
	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	protected ProviewGroupForm fetchSavedProviewGroupForm(
			HttpSession httpSession) {
		ProviewGroupForm form = (ProviewGroupForm) httpSession
				.getAttribute(ProviewGroupForm.FORM_NAME);

		return form;
	}

	
	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	protected ProviewGroupListFilterForm fetchSavedProviewGroupListFilterForm(
			HttpSession httpSession) {
		ProviewGroupListFilterForm form = (ProviewGroupListFilterForm) httpSession
				.getAttribute(ProviewGroupListFilterForm.FORM_NAME);
		if (form == null) {
			form = new ProviewGroupListFilterForm();
		}
		return form;
	}
	
	/**
	 * 
	 * @param httpSession
	 * @param selectedProviewGroup
	 */
	private void saveSelectedProviewGroups(HttpSession httpSession,
			List<ProviewGroup> selectedProviewGroups) {
		httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS,
				selectedProviewGroups);

	}
	
	
	/**
	 * 
	 * @param httpSession
	 * @param allProviewGroups
	 */
	private void saveAllProviewGroups(HttpSession httpSession,
			List<ProviewGroup> allProviewGroups) {
		httpSession.setAttribute(WebConstants.KEY_ALL_PROVIEW_GROUPS,
				allProviewGroups);

	}
	
	/**
	 * 
	 * @param httpSession
	 * @param allLatestProviewGroups
	 */
	private void saveAllLatestProviewGroups(HttpSession httpSession,
			List<ProviewGroup> allLatestProviewGroups) {
		httpSession.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS,
				allLatestProviewGroups);

	}

	
	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ProviewGroup> fetchAllLatestProviewGroups(
			HttpSession httpSession) {
		List<ProviewGroup> allLatestProviewGroups = (List<ProviewGroup>) httpSession
				.getAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS);
		return allLatestProviewGroups;
	}

	@SuppressWarnings("unchecked")
	private List<ProviewGroup> fetchSelectedProviewGroups(HttpSession httpSession) {
		List<ProviewGroup> allLatestProviewGroups = (List<ProviewGroup>) httpSession
				.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS);
		return allLatestProviewGroups;
	}
	
	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ProviewGroup> fetchAllProviewGroups(
			HttpSession httpSession) {
		List<ProviewGroup> allProviewGroups = (List<ProviewGroup>) httpSession
				.getAttribute(WebConstants.KEY_ALL_PROVIEW_GROUPS);
		return allProviewGroups;
	}

	/**
	 * 
	 * @param proviewClient
	 */
	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
	
	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
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
	public void setMessageSourceAccessor(
			MessageSourceAccessor messageSourceAccessor) {
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
