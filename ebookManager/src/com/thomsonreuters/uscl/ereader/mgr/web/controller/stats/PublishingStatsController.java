package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PublishingStatsController extends BasePublishingStatsController {
    private static final Logger log = LogManager.getLogger(PublishingStatsController.class);

    @Autowired
    public PublishingStatsController(final PublishingStatsService publishingStatsService,
        final OutageService outageService) {
        super(publishingStatsService, outageService);
    }

    /**
     * Handle initial in-bound HTTP get request to the page.
     * No query string parameters are expected.
     */
    @RequestMapping(value = WebConstants.MVC_STATS, method = RequestMethod.GET)
    public ModelAndView stats(final HttpSession httpSession, final Model model) {
        final PublishingStatsFilterForm filterForm = fetchSavedFilterForm(httpSession);

        return setupInitialView(model, filterForm, httpSession);
    }

    /**
     * Handle initial in-bound HTTP get request for specific book definition publishing stats.
     * Used from the View Book Definition page.
     */
    @RequestMapping(value = WebConstants.MVC_STATS_SPECIFIC_BOOK, method = RequestMethod.GET)
    public ModelAndView specificBookStat(
        final HttpSession httpSession,
        @RequestParam("id") final Long id,
        final Model model) {
        final PublishingStatsFilterForm filterForm = new PublishingStatsFilterForm(id); // from session

        return setupInitialView(model, filterForm, httpSession);
    }

    /**
     * Setup of Form and sorting shared by two different incoming HTTP get request
     */
    private ModelAndView setupInitialView(
        final Model model,
        final PublishingStatsFilterForm filterForm,
        final HttpSession httpSession) {
        final PageAndSort<DisplayTagSortProperty> savedPageAndSort = fetchSavedPageAndSort(httpSession);

        final PublishingStatsForm publishingStatsForm = new PublishingStatsForm();
        publishingStatsForm.setObjectsPerPage(savedPageAndSort.getObjectsPerPage());

        setUpModel(filterForm, savedPageAndSort, httpSession, model);
        model.addAttribute(PublishingStatsForm.FORM_NAME, publishingStatsForm);

        return new ModelAndView(WebConstants.VIEW_STATS);
    }

    /**
     * Handle paging and sorting of audit list.
     * Handles clicking of column headers to sort, or use of page number navigation links, like prev/next.
     */
    @RequestMapping(value = WebConstants.MVC_STATS_PAGE_AND_SORT, method = RequestMethod.GET)
    public ModelAndView publishingStatsPagingAndSorting(
        final HttpSession httpSession,
        @ModelAttribute(PublishingStatsForm.FORM_NAME) final PublishingStatsForm form,
        final Model model) {
        final PublishingStatsFilterForm filterForm = fetchSavedFilterForm(httpSession);
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        form.setObjectsPerPage(pageAndSort.getObjectsPerPage());
        final Integer nextPageNumber = form.getPage();

        // If there was a page=n query string parameter, then we assume we are paging since this
        // parameter is not present on the query string when display tag sorting.
        if (nextPageNumber != null) { // PAGING
            pageAndSort.setPageNumber(nextPageNumber);
        } else { // SORTING
            pageAndSort.setPageNumber(1);
            pageAndSort.setSortProperty(form.getSort());
            pageAndSort.setAscendingSort(form.isAscendingSort());
        }
        setUpModel(filterForm, pageAndSort, httpSession, model);

        return new ModelAndView(WebConstants.VIEW_STATS);
    }

    /**
     * Handle URL request that the number of rows displayed in table be changed.
     */
    @RequestMapping(value = WebConstants.MVC_STATS_CHANGE_ROW_COUNT, method = RequestMethod.POST)
    public ModelAndView handleChangeInItemsToDisplay(
        final HttpSession httpSession,
        @ModelAttribute(PublishingStatsForm.FORM_NAME) @Valid final PublishingStatsForm form,
        final Model model) {
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        pageAndSort.setPageNumber(1); // Always start from first page again once changing row count to avoid index out of bounds
        pageAndSort.setObjectsPerPage(form.getObjectsPerPage()); // Update the new number of items to be shown at one time
        // Restore the state of the search filter
        final PublishingStatsFilterForm filterForm = fetchSavedFilterForm(httpSession);
        setUpModel(filterForm, pageAndSort, httpSession, model);
        return new ModelAndView(WebConstants.VIEW_STATS);
    }

    @RequestMapping(value = WebConstants.MVC_STATS_DOWNLOAD, method = RequestMethod.GET)
    public void downloadPublishingStatsExcel(
        final HttpSession httpSession,
        final HttpServletRequest request,
        final HttpServletResponse response) {
        final PublishingStatsExcelExportService excelExportService = new PublishingStatsExcelExportService();

        try {
            final Workbook wb = excelExportService.createExcelDocument(httpSession);
            final Date date = new Date();
            final SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
            final String stringDate = s.format(date);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=publishingStats_" + stringDate + ".xls");
            final ServletOutputStream out = response.getOutputStream();
            wb.write(out);
            out.flush();
        } catch (final Exception e) {
            log.error(e.getMessage());
        }
    }
}
