package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import com.thomsonreuters.uscl.ereader.core.book.domain.VersionIsbn;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleReportInfo;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.CurrentSessionUserPreferences;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitlesReportFilterForm.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProviewTitleReportsController {

    private final ProviewTitleListService proviewTitleListService;
    private final VersionIsbnService versionIsbnService;

    @InitBinder(ProviewTitlesReportFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
       binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    private List<ProviewTitleReportInfo> fetchSelectedProviewTitleReportInfo(final HttpSession httpSession) {
        return (List<ProviewTitleReportInfo>) httpSession.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES_REPORT);
    }

    private void saveSelectedProviewTitleReportInfo(
        final HttpSession httpSession,
        final List<ProviewTitleReportInfo> selectedProviewTitleInfo) {
        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES_REPORT, selectedProviewTitleInfo);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLES_REPORT, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_PROVIEW_TITLES_REPORT)
    public ModelAndView getSelectionsTitleReport(@ModelAttribute final ProviewTitlesReportFilterForm form,
                                                 final BindingResult bindingResult,
                                                 final HttpSession httpSession,
                                                 final Model model)
            throws ProviewException {
        if (bindingResult.hasErrors()) {
            log.error("Binding errors on Proview Titles Report page:\n" + bindingResult.getAllErrors().toString());
        }

        final Command command = form.getCommand();
        final boolean isRefresh = Command.REFRESH.equals(command);
        //List<ProviewTitleReportInfo>  currentSessionProviewTitleReportInfoList =
        //    fetchSelectedProviewTitleReportInfo(httpSession);
        List<ProviewTitleReportInfo> selectedProviewTitleReportInfoList = Collections.emptyList();

        /*
        if (!isRefresh) {
            //If Not refresh and No filters and session has data return
            if (currentSessionProviewTitleReportInfoList != null
                    && currentSessionProviewTitleReportInfoList.size() > 0
                    && form.areAllFiltersBlank()) {
                return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES_REPORT);
            }
        }
        //If refresh or session has no current List<ProviewTitleReportInfo> data
        //or current session has data but Filters present got to Proview
         */

        updateUserPreferencesForCurrentSession(form, httpSession);
        try {
            selectedProviewTitleReportInfoList = proviewTitleListService.getSelectedProviewTitleReportInfo(form);
        } catch (final ProviewException e) {
            log.warn(e.getMessage(), e);
            model.addAttribute(WebConstants.KEY_ERROR_OCCURRED, Boolean.TRUE);
        }
        //update Material Id from VERSION_ISBN (MaterialId) & SubMaterial id from EBOOK_DEFINITION (MaterialId)
        List<VersionIsbn> lstVersionIsbn = versionIsbnService.getAllVersionIsbnEbookDefinition();
        selectedProviewTitleReportInfoList.forEach((report) -> {
            VersionIsbn currIsbn =  lstVersionIsbn.stream().filter(vi -> vi.getEbookDefinition().getFullyQualifiedTitleId().equals(report.getId()) &&
                    vi.getVersion().equals(report.getVersion().substring(1))).findFirst().orElse(null);
            if (currIsbn != null && currIsbn.getMaterialId() != null) {
                report.setMaterialId(currIsbn.getMaterialId());
                report.setSubMaterialId(currIsbn.getEbookDefinition().getMaterialId());
            }
        });
        saveSelectedProviewTitleReportInfo(httpSession,selectedProviewTitleReportInfoList); // required for Title excel report
        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES_REPORT);
    }
        
    private void updateUserPreferencesForCurrentSession(@NotNull final ProviewTitlesReportFilterForm form,
            @NotNull final HttpSession httpSession) {
        final Object preferencesSessionAttribute = httpSession.getAttribute(CurrentSessionUserPreferences.NAME);
        if (preferencesSessionAttribute instanceof CurrentSessionUserPreferences) {
            final CurrentSessionUserPreferences sessionPreferences =
                    (CurrentSessionUserPreferences) preferencesSessionAttribute;
            sessionPreferences.setProviewDisplayName(form.getProviewDisplayName());
            sessionPreferences.setTitleId(form.getTitleId());
            sessionPreferences.setMinVersions(form.getMinVersions());
            sessionPreferences.setMaxVersions(form.getMaxVersions());
            sessionPreferences.setStatus(form.getStatus());
        }
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_REPORT_DOWNLOAD, method = RequestMethod.GET)
    public void downloadPublishingTitleReportExcel(final HttpSession httpSession, final HttpServletResponse response) {
        final ProviewListExcelTitleReportExportService excelExportService = new ProviewListExcelTitleReportExportService();
        try (final Workbook wb = excelExportService.createExcelDocument(httpSession)) {
            final Date date = new Date();
            final SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
            final String stringDate = s.format(date);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=" + ProviewListExcelTitleReportExportService.TITLES_NAME + stringDate + ".xls");
            final ServletOutputStream out = response.getOutputStream();
            wb.write(out);
            out.flush();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }
        
}
