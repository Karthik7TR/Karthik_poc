package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm.FilterCommand;
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
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ProviewListFilterController {

    private final OutageService outageService;

    @Autowired
    public ProviewListFilterController(final OutageService outageService) {
        this.outageService = outageService;
    }

    @InitBinder(ProviewListFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     *
     * @param httpSession
     * @return
     */
    private List<ProviewTitleInfo> fetchAllLatestProviewTitleInfo(final HttpSession httpSession) {
        final List<ProviewTitleInfo> allLatestProviewTitleInfo =
            (List<ProviewTitleInfo>) httpSession.getAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES);
        return allLatestProviewTitleInfo;
    }

    /**
     *
     * @param httpSession
     * @param selectedProviewTitleInfo
     */
    private void saveSelectedProviewTitleInfo(
        final HttpSession httpSession,
        final List<ProviewTitleInfo> selectedProviewTitleInfo) {
        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, selectedProviewTitleInfo);
    }

    /**
     *
     * @param httpSession
     * @param filterForm
     */
    private void saveProviewListFilterForm(final HttpSession httpSession, final ProviewListFilterForm filterForm) {
        httpSession.setAttribute(ProviewListFilterForm.FORM_NAME, filterForm);
    }

    /**
     *
     * @param httpSession
     * @return
     */
    protected ProviewTitleForm fetchSavedProviewTitleForm(final HttpSession httpSession) {
        ProviewTitleForm form = (ProviewTitleForm) httpSession.getAttribute(ProviewTitleForm.FORM_NAME);
        if (form == null) {
            form = new ProviewTitleForm();
        }
        return form;
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_LIST_FILTERED_POST, method = RequestMethod.GET)
    public ModelAndView doFilterGet() {
        return new ModelAndView(new RedirectView(WebConstants.MVC_PROVIEW_TITLES));
    }

    /**
     * Handle submit/post of a new set of filter criteria.
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_LIST_FILTERED_POST, method = RequestMethod.POST)
    public ModelAndView doFilterPost(
        final HttpSession httpSession,
        @ModelAttribute(ProviewListFilterForm.FORM_NAME) final ProviewListFilterForm filterForm,
        final BindingResult errors,
        final Model model) {
        List<ProviewTitleInfo> selectedProviewTitleInfo = new ArrayList<ProviewTitleInfo>();
        final List<ProviewTitleInfo> allLatestProviewTitleInfo = fetchAllLatestProviewTitleInfo(httpSession);

        if (FilterCommand.RESET.equals(filterForm.getFilterCommand())) {
            filterForm.initNull();
            selectedProviewTitleInfo = allLatestProviewTitleInfo;
        } else {
            boolean proviewDisplayNameBothWayWildCard = false;
            boolean proviewDisplayNameEndsWithWildCard = false;
            boolean proviewDisplayNameStartsWithWildCard = false;
            boolean titleIdBothWayWildCard = false;
            boolean titleIdEndsWithWildCard = false;
            boolean titleIdStartsWithWildCard = false;
            String proviewDisplayNameSearchTerm = filterForm.getProviewDisplayName();
            String titleIdSearchTerm = filterForm.getTitleId();

            if (filterForm.getProviewDisplayName() != null) {
                if (filterForm.getProviewDisplayName().endsWith("%")
                    && filterForm.getProviewDisplayName().startsWith("%")) {
                    proviewDisplayNameBothWayWildCard = true;
                } else if (filterForm.getProviewDisplayName().endsWith("%")) {
                    proviewDisplayNameStartsWithWildCard = true;
                } else if (filterForm.getProviewDisplayName().startsWith("%")) {
                    proviewDisplayNameEndsWithWildCard = true;
                }

                proviewDisplayNameSearchTerm = proviewDisplayNameSearchTerm.replaceAll("%", "");
            }

            if (filterForm.getTitleId() != null) {
                if (filterForm.getTitleId().endsWith("%") && filterForm.getTitleId().startsWith("%")) {
                    titleIdBothWayWildCard = true;
                } else if (filterForm.getTitleId().endsWith("%")) {
                    titleIdStartsWithWildCard = true;
                } else if (filterForm.getTitleId().startsWith("%")) {
                    titleIdEndsWithWildCard = true;
                }

                titleIdSearchTerm = titleIdSearchTerm.toLowerCase().replaceAll("%", "");
            }

            for (final ProviewTitleInfo titleInfo : allLatestProviewTitleInfo) {
                boolean selected = true;

                if (proviewDisplayNameSearchTerm != null) {
                    if (titleInfo.getTitle() == null) {
                        selected = false;
                    } else {
                        if (proviewDisplayNameBothWayWildCard) {
                            if (!titleInfo.getTitle().contains(proviewDisplayNameSearchTerm)) {
                                selected = false;
                            }
                        } else if (proviewDisplayNameEndsWithWildCard) {
                            if (!titleInfo.getTitle().endsWith(proviewDisplayNameSearchTerm)) {
                                selected = false;
                            }
                        } else if (proviewDisplayNameStartsWithWildCard) {
                            if (!titleInfo.getTitle().startsWith(proviewDisplayNameSearchTerm)) {
                                selected = false;
                            }
                        } else if (!titleInfo.getTitle().equals(proviewDisplayNameSearchTerm)) {
                            selected = false;
                        }
                    }
                }
                if (selected) {
                    if (titleIdSearchTerm != null) {
                        if (titleInfo.getTitleId() == null) {
                            selected = false;
                        } else {
                            if (titleIdBothWayWildCard) {
                                if (!titleInfo.getTitleId().contains(titleIdSearchTerm)) {
                                    selected = false;
                                }
                            } else if (titleIdEndsWithWildCard) {
                                if (!titleInfo.getTitleId().endsWith(titleIdSearchTerm)) {
                                    selected = false;
                                }
                            } else if (titleIdStartsWithWildCard) {
                                if (!titleInfo.getTitleId().startsWith(titleIdSearchTerm)) {
                                    selected = false;
                                }
                            } else if (!titleInfo.getTitleId().equals(titleIdSearchTerm)) {
                                selected = false;
                            }
                        }
                    }
                }

                if (selected) {
                    if (!(titleInfo.getTotalNumberOfVersions() >= filterForm.getMinVersionsInt())) {
                        selected = false;
                    }
                }
                if (selected) {
                    if (!(titleInfo.getTotalNumberOfVersions() <= filterForm.getMaxVersionsInt())) {
                        selected = false;
                    }
                }
                if (selected) {
                    selectedProviewTitleInfo.add(titleInfo);
                }
            }
        }

        saveSelectedProviewTitleInfo(httpSession, selectedProviewTitleInfo);
        saveProviewListFilterForm(httpSession, filterForm);

        final ProviewTitleForm proviewTitleForm = fetchSavedProviewTitleForm(httpSession);
        if (proviewTitleForm.getObjectsPerPage() == null) {
            proviewTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
        }
        model.addAttribute(ProviewTitleForm.FORM_NAME, proviewTitleForm);
        model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewTitleForm.getObjectsPerPage());

        model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewTitleInfo);
        model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, selectedProviewTitleInfo.size());
        model.addAttribute(ProviewListFilterForm.FORM_NAME, filterForm);
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());

        return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES);
    }
}
