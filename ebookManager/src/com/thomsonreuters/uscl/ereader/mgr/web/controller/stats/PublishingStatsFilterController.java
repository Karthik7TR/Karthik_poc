package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsFilterForm.FilterCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
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
public class PublishingStatsFilterController extends BasePublishingStatsController
{
    private final Validator validator;

    @Autowired
    public PublishingStatsFilterController(final PublishingStatsService publishingStatsService,
                                           @Qualifier("publishingStatsFormValidator") final Validator validator)
    {
        super(publishingStatsService);
        this.validator = validator;
    }

    @InitBinder(PublishingStatsFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder)
    {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    /**
     * Handle submit/post of a new set of filter criteria.
     */
    @RequestMapping(value = WebConstants.MVC_STATS_FILTER, method = RequestMethod.POST)
    public ModelAndView doFilterPost(
        final HttpSession httpSession,
        @ModelAttribute(PublishingStatsFilterForm.FORM_NAME) @Valid final PublishingStatsFilterForm filterForm,
        final BindingResult errors,
        final Model model)
    {
        // Restore state of paging and sorting
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        final PublishingStatsForm jobSummaryForm = new PublishingStatsForm();
        jobSummaryForm.setObjectsPerPage(pageAndSort.getObjectsPerPage());

        if (FilterCommand.RESET.equals(filterForm.getFilterCommand()))
        {
            filterForm.initialize();
        }

        pageAndSort.setPageNumber(1);

        setUpModel(filterForm, pageAndSort, httpSession, model);
        model.addAttribute(PublishingStatsForm.FORM_NAME, jobSummaryForm);

        return new ModelAndView(WebConstants.VIEW_STATS);
    }
}
