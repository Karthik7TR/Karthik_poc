package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.FilterCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProviewGroupListFilterController extends BaseProviewGroupListController {
    /**
     * Handle submit/post of a new set of filter criteria.
     */
    private final OutageService outageService;

    @Autowired
    public ProviewGroupListFilterController(final OutageService outageService) {
        super();
        this.outageService = outageService;
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_LIST_FILTERED, method = RequestMethod.GET)
    public ModelAndView doFilterGet(
        final HttpSession httpSession,
        @ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) final ProviewGroupListFilterForm filterForm,
        final BindingResult errors,
        final Model model) {
        try {
            List<ProviewGroup> selectedProviewGroupList;
            final List<ProviewGroup> allLatestProviewGroupList = fetchAllLatestProviewGroups(httpSession);

            if (FilterCommand.RESET.equals(filterForm.getFilterCommand())) {
                filterForm.initNull();
                selectedProviewGroupList = allLatestProviewGroupList;
            } else {
                selectedProviewGroupList = filterProviewGroupList(filterForm, allLatestProviewGroupList);
            }
            saveSelectedProviewGroups(httpSession, selectedProviewGroupList);
            model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewGroupList);
            model.addAttribute(WebConstants.KEY_TOTAL_GROUP_SIZE, selectedProviewGroupList.size());
        } catch (Exception e) {
            model.addAttribute(WebConstants.KEY_ERROR_OCCURRED, Boolean.TRUE);
        }

        saveProviewGroupListFilterForm(httpSession, filterForm);

        final ProviewGroupForm proviewGroupForm = fetchProviewGroupForm(httpSession);
        if (proviewGroupForm.getObjectsPerPage() == null) {
            proviewGroupForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
        }
        model.addAttribute(ProviewGroupForm.FORM_NAME, proviewGroupForm);
        model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewGroupForm.getObjectsPerPage());
        model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, filterForm);
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
        return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUPS);
    }
}
