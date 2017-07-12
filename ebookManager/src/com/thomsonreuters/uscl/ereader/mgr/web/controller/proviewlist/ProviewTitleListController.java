package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProviewTitleListController
{
    private ProviewHandler proviewHandler;
    private BookDefinitionService bookDefinitionService;
    private ProviewAuditService proviewAuditService;
    private ManagerService managerService;
    private MessageSourceAccessor messageSourceAccessor;
    private JobRequestService jobRequestService;
    private ProviewTitleListService proviewTitleListService;

    private static final Logger log = LogManager.getLogger(ProviewTitleListController.class);

    /**
     * @param httpSession
     * @return
     */
    private List<ProviewTitleInfo> fetchSelectedProviewTitleInfo(final HttpSession httpSession)
    {
        final List<ProviewTitleInfo> allLatestProviewTitleInfo =
            (List<ProviewTitleInfo>) httpSession.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES);
        return allLatestProviewTitleInfo;
    }

    /**
     * @param httpSession
     * @param selectedProviewTitleInfo
     */
    private void saveSelectedProviewTitleInfo(final HttpSession httpSession, final List<ProviewTitleInfo> selectedProviewTitleInfo)
    {
        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, selectedProviewTitleInfo);
    }

    /**
     * @param httpSession
     * @param allLatestProviewTitleInfo
     */
    private void saveAllLatestProviewTitleInfo(
        final HttpSession httpSession,
        final List<ProviewTitleInfo> allLatestProviewTitleInfo)
    {
        httpSession.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES, allLatestProviewTitleInfo);
    }

    /**
     * @param httpSession
     * @return
     */
    private List<ProviewTitleInfo> fetchAllLatestProviewTitleInfo(final HttpSession httpSession)
    {
        final List<ProviewTitleInfo> allLatestProviewTitleInfo =
            (List<ProviewTitleInfo>) httpSession.getAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES);
        return allLatestProviewTitleInfo;
    }

    /**
     * @param httpSession
     * @return
     */
    private Map<String, ProviewTitleContainer> fetchAllProviewTitleInfo(final HttpSession httpSession)
    {
        final Map<String, ProviewTitleContainer> allProviewTitleInfo =
            (Map<String, ProviewTitleContainer>) httpSession.getAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES);
        return allProviewTitleInfo;
    }

    /**
     * @param httpSession
     * @param allProviewTitleInfo
     */
    private void saveAllProviewTitleInfo(
        final HttpSession httpSession,
        final Map<String, ProviewTitleContainer> allProviewTitleInfo)
    {
        httpSession.setAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES, allProviewTitleInfo);
    }

    /**
     * @param httpSession
     * @return
     */
    protected ProviewListFilterForm fetchSavedProviewListFilterForm(final HttpSession httpSession)
    {
        ProviewListFilterForm form = (ProviewListFilterForm) httpSession.getAttribute(ProviewListFilterForm.FORM_NAME);
        if (form == null)
        {
            form = new ProviewListFilterForm();
        }
        return form;
    }

    /**
     * @param httpSession
     * @return
     */
    protected ProviewTitleForm fetchSavedProviewTitleForm(final HttpSession httpSession)
    {
        final ProviewTitleForm form = (ProviewTitleForm) httpSession.getAttribute(ProviewTitleForm.FORM_NAME);
        return form;
    }

    /**
     * @param httpSession
     * @param form
     */
    private void saveProviewTitleForm(final HttpSession httpSession, final ProviewTitleForm form)
    {
        httpSession.setAttribute(ProviewTitleForm.FORM_NAME, form);
    }

    private void sendEmail(final String emailAddressString, final String subject, final String body)
    {
        final List<InternetAddress> emailAddresses = new ArrayList<>();
        try
        {
            emailAddresses.add(new InternetAddress(emailAddressString));
            EmailNotification.send(emailAddresses, subject, body);
        }
        catch (final AddressException e)
        {
            //Intentionally left blank
        }
    }

    /**
     *
     * @param httpSession
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLES, method = RequestMethod.POST)
    public ModelAndView postSelections(@ModelAttribute final ProviewTitleForm form, final HttpSession httpSession, final Model model)
        throws Exception
    {
        final Command command = form.getCommand();

        switch (command)
        {
        case REFRESH:

            final Map<String, ProviewTitleContainer> allProviewTitleInfo = proviewHandler.getAllProviewTitleInfo();
            final List<ProviewTitleInfo> allLatestProviewTitleInfo =
                proviewHandler.getAllLatestProviewTitleInfo(allProviewTitleInfo);

            saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
            saveAllLatestProviewTitleInfo(httpSession, allLatestProviewTitleInfo);
            saveSelectedProviewTitleInfo(httpSession, allLatestProviewTitleInfo);

            if (allLatestProviewTitleInfo != null)
            {
                model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allLatestProviewTitleInfo);
                model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, allLatestProviewTitleInfo.size());
            }

            model.addAttribute(ProviewListFilterForm.FORM_NAME, new ProviewListFilterForm());

            ProviewTitleForm proviewTitleForm = fetchSavedProviewTitleForm(httpSession);
            if (proviewTitleForm == null)
            {
                proviewTitleForm = new ProviewTitleForm();
                proviewTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
                saveProviewTitleForm(httpSession, proviewTitleForm);
            }
            model.addAttribute(ProviewTitleForm.FORM_NAME, proviewTitleForm);
            model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewTitleForm.getObjectsPerPage());
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
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DOWNLOAD, method = RequestMethod.GET)
    public void downloadPublishingStatsExcel(
        final HttpSession httpSession,
        final HttpServletRequest request,
        final HttpServletResponse response)
    {
        final ProviewListExcelExportService excelExportService = new ProviewListExcelExportService();
        try
        {
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
        }
        catch (final Exception e)
        {
            log.error(e.getMessage());
        }
    }

    /**
     *
     * @param httpSession
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLES, method = RequestMethod.GET)
    public ModelAndView allLatestProviewTitleInfo(final HttpSession httpSession, final Model model) throws Exception
    {
        List<ProviewTitleInfo> selectedProviewTitleInfo = fetchSelectedProviewTitleInfo(httpSession);

        if (selectedProviewTitleInfo == null)
        {
            List<ProviewTitleInfo> allLatestProviewTitleInfo = fetchAllLatestProviewTitleInfo(httpSession);
            if (allLatestProviewTitleInfo == null)
            {
                Map<String, ProviewTitleContainer> allProviewTitleInfo = fetchAllProviewTitleInfo(httpSession);
                try
                {
                    if (allProviewTitleInfo == null)
                    {
                        allProviewTitleInfo = proviewHandler.getAllProviewTitleInfo();
                        saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
                    }

                    allLatestProviewTitleInfo = proviewHandler.getAllLatestProviewTitleInfo(allProviewTitleInfo);
                    saveAllLatestProviewTitleInfo(httpSession, allLatestProviewTitleInfo);

                    selectedProviewTitleInfo = allLatestProviewTitleInfo;

                    saveSelectedProviewTitleInfo(httpSession, selectedProviewTitleInfo);
                }
                catch (final ProviewException e)
                {
                    model.addAttribute(
                        WebConstants.KEY_ERR_MESSAGE,
                        "Proview Exception occured. Please contact your administrator.");
                }
            }
        }

        if (selectedProviewTitleInfo != null)
        {
            model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewTitleInfo);
            model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, selectedProviewTitleInfo.size());
        }

        model.addAttribute(ProviewListFilterForm.FORM_NAME, fetchSavedProviewListFilterForm(httpSession));

        ProviewTitleForm proviewTitleForm = fetchSavedProviewTitleForm(httpSession);
        if (proviewTitleForm == null)
        {
            proviewTitleForm = new ProviewTitleForm();
            proviewTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
            saveProviewTitleForm(httpSession, proviewTitleForm);
        }

        model.addAttribute(ProviewTitleForm.FORM_NAME, proviewTitleForm);
        model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewTitleForm.getObjectsPerPage());

        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES);
    }

    /**
     *
     * @param titleId
     * @param httpSession
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS, method = RequestMethod.GET)
    public ModelAndView singleTitleAllVersions(
        @RequestParam("titleId") final String titleId,
        final HttpSession httpSession,
        final Model model) throws Exception
    {
        Assert.notNull(titleId);

        Map<String, ProviewTitleContainer> allProviewTitleInfo = fetchAllProviewTitleInfo(httpSession);
        if (allProviewTitleInfo == null)
        {
            allProviewTitleInfo = proviewHandler.getAllProviewTitleInfo();
            saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
        }

        final BookDefinition bookDef = proviewTitleListService.getBook(new TitleId(titleId));
        final ProviewTitleContainer proviewTitleContainer = allProviewTitleInfo.get(titleId);
        if (proviewTitleContainer != null)
        {
            final List<ProviewTitleInfo> allTitleVersions = proviewTitleContainer.getProviewTitleInfos();
            final List<ProviewTitle> proviewTitles = proviewTitleListService.getProviewTitles(allTitleVersions, bookDef);
            model.addAttribute(WebConstants.KEY_PAGINATED_LIST, proviewTitles);
            model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, proviewTitles.size());
        }
        final String infoMessage = getBookLevelMessage(bookDef);
        model.addAttribute(WebConstants.KEY_INFO_MESSAGE, infoMessage);
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_ALL_VERSIONS);
    }

    @Nullable
    private String getBookLevelMessage(@Nullable final BookDefinition bookDef)
    {
        if (bookDef == null)
        {
            return "Book was not found in DB";
        }
        else if (bookDef.getPilotBookStatus() == PilotBookStatus.IN_PROGRESS)
        {
            return "Pilot book marked as 'In Progress' for notes migration. Once the note migration csv file is in place, update the Pilot Book status, and regenerate the book before Promoting.";
        }
        return null;
    }

    /**
     *
     * @param titleId
     * @param versionNumber
     * @param status
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DELETE, method = RequestMethod.GET)
    public ModelAndView proviewTitleDelete(
        @RequestParam("titleId") final String titleId,
        @RequestParam("versionNumber") final String versionNumber,
        @RequestParam("status") final String status,
        @RequestParam("lastUpdate") final String lastUpdate,
        final Model model) throws Exception
    {
        model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, versionNumber);
        model.addAttribute(WebConstants.KEY_STATUS, status);
        model.addAttribute(
            WebConstants.KEY_PROVIEW_TITLE_INFO_FORM,
            new ProviewTitleForm(titleId, versionNumber, status, lastUpdate));

        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_DELETE);
    }

    /**
     *
     * @param titleId
     * @param versionNumber
     * @param status
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_REMOVE, method = RequestMethod.GET)
    public ModelAndView proviewTitleRemove(
        @RequestParam("titleId") final String titleId,
        @RequestParam("versionNumber") final String versionNumber,
        @RequestParam("status") final String status,
        @RequestParam("lastUpdate") final String lastUpdate,
        final Model model) throws Exception
    {
        model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, versionNumber);
        model.addAttribute(WebConstants.KEY_STATUS, status);
        model.addAttribute(
            WebConstants.KEY_PROVIEW_TITLE_INFO_FORM,
            new ProviewTitleForm(titleId, versionNumber, status, lastUpdate));
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_REMOVE);
    }

    /**
     *
     * @param titleId
     * @param versionNumber
     * @param status
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_PROMOTE, method = RequestMethod.GET)
    public ModelAndView proviewTitlePromote(
        @RequestParam("titleId") final String titleId,
        @RequestParam("versionNumber") final String versionNumber,
        @RequestParam("status") final String status,
        @RequestParam("lastUpdate") final String lastUpdate,
        final Model model) throws Exception
    {
        model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, versionNumber);
        model.addAttribute(WebConstants.KEY_STATUS, status);
        model.addAttribute(
            WebConstants.KEY_PROVIEW_TITLE_INFO_FORM,
            new ProviewTitleForm(titleId, versionNumber, status, lastUpdate));
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE);
    }

    /**
     *
     * @param model
     * @param titleId
     * @param version
     * @return
     */
    private boolean isJobRunningForBook(final Model model, final String titleId, String version)
    {
        boolean isJobRunning = false;

        if (version.startsWith("v"))
        {
            version = version.substring(1);
        }
        final BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(titleId);
        if (book != null)
        {
            if (jobRequestService.isBookInJobRequest(book.getEbookDefinitionId()))
            {
                final Object[] args = {book.getFullyQualifiedTitleId(), "", "This book is already in the job queue"};
                final String infoMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.fail", args);
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE, infoMessage);
                isJobRunning = true;
            }

            else
            {
                final JobExecution runningJobExecution = managerService.findRunningJob(book);

                if (runningJobExecution != null)
                {
                    final Object[] args = {book.getFullyQualifiedTitleId(), version, runningJobExecution.getId().toString()};
                    final String infoMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.in.progress", args);
                    model.addAttribute(WebConstants.KEY_ERR_MESSAGE, infoMessage);
                    isJobRunning = true;
                }
            }
        }
        return isJobRunning;
    }

    /**
     *
     * @param form
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_REMOVE, method = RequestMethod.POST)
    public ModelAndView proviewTitleRemovePost(
        @ModelAttribute(ProviewTitleForm.FORM_NAME) final ProviewTitleForm form,
        final Model model) throws Exception
    {
        model.addAttribute(WebConstants.KEY_TITLE_ID, form.getTitleId());
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, form.getVersion());
        model.addAttribute(WebConstants.KEY_STATUS, form.getStatus());
        model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM, form);

        String emailBody = "";
        String emailSubject = "Proview Remove Request Status: ";

        try
        {
            if (!isJobRunningForBook(model, form.getTitleId(), form.getVersion()))
            {
                proviewHandler.removeTitle(form.getTitleId(), new Version(form.getVersion()));
                model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: removed from Proview.");
                emailBody =
                    "Title id: " + form.getTitleId() + ", version: " + form.getVersion() + " removed from Proview.";
                emailSubject += "Success";
                sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
                proviewAuditService.save(form.createAudit());
            }
        }
        catch (final Exception e)
        {
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed to remove from proview. " + e.getMessage());
            emailBody = "Title id: "
                + form.getTitleId()
                + ", version: "
                + form.getVersion()
                + " could not be removed from Proview.";
            emailSubject += "Unsuccessful";
            sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_REMOVE);
    }

    /**
     *
     * @param form
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_PROMOTE, method = RequestMethod.POST)
    public ModelAndView proviewTitlePromotePost(
        @ModelAttribute(ProviewTitleForm.FORM_NAME) final ProviewTitleForm form,
        final Model model) throws Exception
    {
        model.addAttribute(WebConstants.KEY_TITLE_ID, form.getTitleId());
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, form.getVersion());
        model.addAttribute(WebConstants.KEY_STATUS, form.getStatus());
        model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM, form);

        String emailBody = "";
        String emailSubject = "Proview Promote Request Status: ";

        try
        {
            if (!isJobRunningForBook(model, form.getTitleId(), form.getVersion()))
            {
                proviewHandler.promoteTitle(form.getTitleId(), form.getVersion());
                model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: promoted to Final in Proview.");

                emailBody =
                    "Title id: " + form.getTitleId() + ", version: " + form.getVersion() + " promoted from Proview.";
                emailSubject += "Success";

                sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
                proviewAuditService.save(form.createAudit());
            }
        }
        catch (final Exception e)
        {
            model.addAttribute(
                WebConstants.KEY_ERR_MESSAGE,
                "Failed to promote this version in proview. " + e.getMessage());
            emailBody = "Title id: "
                + form.getTitleId()
                + ", version: "
                + form.getVersion()
                + " could not be promoted from Proview.";
            emailSubject += "Unsuccessful";
            sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE);
    }

    /**
     *
     * @param form
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DELETE, method = RequestMethod.POST)
    public ModelAndView proviewTitleDeletePost(
        @ModelAttribute(ProviewTitleForm.FORM_NAME) final ProviewTitleForm form,
        final Model model) throws Exception
    {
        model.addAttribute(WebConstants.KEY_TITLE_ID, form.getTitleId());
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, form.getVersion());
        model.addAttribute(WebConstants.KEY_STATUS, form.getStatus());
        model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM, form);

        String emailBody = "";
        String emailSubject = "Proview Delete Request Status: ";

        try
        {
            if (!isJobRunningForBook(model, form.getTitleId(), form.getVersion()))
            {
                proviewHandler.deleteTitle(form.getTitleId(), new Version(form.getVersion()));
                model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: deleted from Proview.");
                emailBody =
                    "Title id: " + form.getTitleId() + ", version: " + form.getVersion() + " deleted from Proview.";
                emailSubject += "Success";
                sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
                proviewAuditService.save(form.createAudit());
            }
        }
        catch (final Exception e)
        {
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed to delete from proview. " + e.getMessage());
            emailBody = "Title id: "
                + form.getTitleId()
                + ", version: "
                + form.getVersion()
                + " could not be deleted from Proview.";
            emailSubject += "Unsuccessful";
            sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
        }

        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_DELETE);
    }

    /**
     *
     * @param proviewHandler
     */
    @Required
    public void setProviewHandler(final ProviewHandler proviewHandler)
    {
        this.proviewHandler = proviewHandler;
    }

    @Required
    public void setBookDefinitionService(final BookDefinitionService service)
    {
        bookDefinitionService = service;
    }

    @Required
    public void setProviewAuditService(final ProviewAuditService service)
    {
        proviewAuditService = service;
    }

    public ManagerService getManagerService()
    {
        return managerService;
    }

    @Required
    public void setManagerService(final ManagerService managerService)
    {
        this.managerService = managerService;
    }

    public MessageSourceAccessor getMessageSourceAccessor()
    {
        return messageSourceAccessor;
    }

    @Required
    public void setMessageSourceAccessor(final MessageSourceAccessor messageSourceAccessor)
    {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    public JobRequestService getJobRequestService()
    {
        return jobRequestService;
    }

    @Required
    public void setJobRequestService(final JobRequestService jobRequestService)
    {
        this.jobRequestService = jobRequestService;
    }

    @Required
    public void setProviewTitleListService(final ProviewTitleListService proviewTitleListService)
    {
        this.proviewTitleListService = proviewTitleListService;
    }
}
