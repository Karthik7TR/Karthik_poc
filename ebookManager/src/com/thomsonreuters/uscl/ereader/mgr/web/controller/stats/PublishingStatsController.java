package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.CurrentSessionUserPreferences;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort.SortProperty;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.displaytag.pagination.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class PublishingStatsController {
    private final PublishingStatsService publishingStatsService;
    private final OutageService outageService;
    private final Validator validator;
    public static final int MAX_EXCEL_SHEET_ROW_NUM = 65535;

    @Autowired
    public PublishingStatsController(final PublishingStatsService publishingStatsService,
            final OutageService outageService, @Qualifier("publishingStatsFormValidator") final Validator validator) {
        this.publishingStatsService = publishingStatsService;
        this.outageService = outageService;
        this.validator = validator;
    }

    @InitBinder(PublishingStatsFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_STATS, method = RequestMethod.GET)
    public ModelAndView stats(final HttpSession httpSession,
            @ModelAttribute(PublishingStatsFilterForm.FORM_NAME) @Valid PublishingStatsFilterForm filterForm,
            final BindingResult errors, final Model model) {
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
        if (errors.hasErrors()) {
            log.debug("Incorrect parameters passed to StatsFilterForm:\n" + errors.toString());
            filterForm = getUserPreferencesForCurrentSession(httpSession);
        }
        updateUserPreferencesForCurrentSession(filterForm, httpSession);
        model.addAttribute(WebConstants.KEY_PAGINATED_LIST, createPaginatedList(filterForm,httpSession));
        model.addAttribute(WebConstants.KEY_PAGE_SIZE, filterForm.getObjectsPerPage());

        return new ModelAndView(WebConstants.VIEW_STATS);
    }

    private PublishingStatsFilterForm getUserPreferencesForCurrentSession(final HttpSession httpSession) {
        final Object preferencesSessionAttribute = httpSession.getAttribute(CurrentSessionUserPreferences.NAME);
        if (preferencesSessionAttribute instanceof CurrentSessionUserPreferences) {
            final CurrentSessionUserPreferences sessionPreferences =
                    (CurrentSessionUserPreferences) preferencesSessionAttribute;
            return sessionPreferences.getStatsPreferences();
        }
        return new PublishingStatsFilterForm();
    }

    private void saveSelectedPublishingStats(
            final HttpSession httpSession,
            final List<PublishingStats> selectedPublishingStats) {
        httpSession.setAttribute(WebConstants.KEY_PUBLISHING_STATS_LIST, selectedPublishingStats);
    }

    private void updateUserPreferencesForCurrentSession(final PublishingStatsFilterForm form,
            final HttpSession httpSession) {
        final Object preferencesSessionAttribute = httpSession.getAttribute(CurrentSessionUserPreferences.NAME);
        if (preferencesSessionAttribute instanceof CurrentSessionUserPreferences) {
            final CurrentSessionUserPreferences sessionPreferences =
                    (CurrentSessionUserPreferences) preferencesSessionAttribute;
            sessionPreferences.setStatsPreferences(form);
        }
    }

    /**
     * Create the partial paginated list used by DisplayTag to render to current page number of
     * list list of objects.
     * @param filterForm contains current page number, sort column, and sort direction (asc/desc).
     * @return an implemented DisplayTag paginated list interface
     */
    private PaginatedList createPaginatedList(final PublishingStatsFilterForm filterForm,
                                              HttpSession httpSession) {
        final PublishingStatsFilter publishingStatsFilter = createStatsFilter(filterForm);
        final PublishingStatsSort publishingStatsSort = createStatsSort(filterForm);

        // Lookup all the Stats objects by their primary key
        //Will have max 65,535 records downloaded to Excel
        final List<PublishingStats> statsSelected =
                publishingStatsService.findPublishingStatsForExcelReport(publishingStatsFilter,
                    publishingStatsSort, MAX_EXCEL_SHEET_ROW_NUM);
        final List<PublishingStats> stats =
                publishingStatsService.findPublishingStats(publishingStatsFilter, publishingStatsSort);
        final int numberOfStats = publishingStatsService.numberOfPublishingStats(publishingStatsFilter);

        //For Download Excel report
        saveSelectedPublishingStats(httpSession,statsSelected);

        // Instantiate the object used by DisplayTag to render a partial list
        return new PublishingStatsPaginatedList(
                stats,
                numberOfStats,
                filterForm.getPage(),
                filterForm.getObjectsPerPage(),
                filterForm.getSort(),
                filterForm.isAscendingSort());
    }

    private PublishingStatsFilter createStatsFilter(final PublishingStatsFilterForm filterForm) {
        return new PublishingStatsFilter(
                filterForm.getFromDate(),
                filterForm.getToDate(),
                filterForm.getTitleId(),
                filterForm.getProviewDisplayName(),
                filterForm.getBookDefinitionId());
    }

    /**
     * Map the sort property name returned by display tag to the business object property name
     * for sort used in the service.
     * I.e. map a PageAndSortForm.DisplayTagSortProperty to a PublishingStatsSort.SortProperty
     * @param filterForm contains current page number, sort column, and sort direction (asc/desc).
     * @return a ebookAudit sort business object used by the service to fetch the audit entities.
     */
    private PublishingStatsSort createStatsSort(final PublishingStatsFilterForm filterForm) {
        return new PublishingStatsSort(
                SortProperty.valueOf(filterForm.getSort().toString()),
                filterForm.isAscendingSort(),
                filterForm.getPage(),
                filterForm.getObjectsPerPage());
    }

    @RequestMapping(value = WebConstants.MVC_STATS_DOWNLOAD, method = RequestMethod.GET)
    public void downloadPublishingStatsExcel(final HttpSession httpSession, final HttpServletResponse response) {

        final PublishingStatsExcelExportService excelExportService = new PublishingStatsExcelExportService();
        try (final Workbook wb = excelExportService.createExcelDocument(httpSession)) {
            final Date date = new Date();
            final SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
            final String stringDate = s.format(date);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=publishingStats_" + stringDate + ".xls");
            final ServletOutputStream out = response.getOutputStream();
            wb.write(out);
            out.flush();
        } catch (final IOException e) {
            log.error(e.getMessage());
        }
    }
}
