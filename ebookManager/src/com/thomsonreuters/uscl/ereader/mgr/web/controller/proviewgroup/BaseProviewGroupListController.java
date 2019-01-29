package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseProviewListController;

public abstract class BaseProviewGroupListController extends BaseProviewListController {
    protected List<ProviewGroup> filterProviewGroupList(
        final ProviewGroupListFilterForm filterForm,
        final List<ProviewGroup> allLatestProviewGroups) {
        final List<ProviewGroup> selectedProviewGroupList = new ArrayList<>();

        boolean groupNameBothWayWildCard = false;
        boolean groupNameEndsWithWildCard = false;
        boolean groupNameStartsWithWildCard = false;
        boolean groupIdBothWayWildCard = false;
        boolean groupIdEndsWithWildCard = false;
        boolean groupIdStartsWithWildCard = false;
        String groupNameSearchTerm = filterForm.getGroupName();
        String groupIdSearchTerm = filterForm.getProviewGroupID();

        if (groupNameSearchTerm != null && groupNameSearchTerm.length() > 0) {
            if (groupNameSearchTerm.endsWith("%") && groupNameSearchTerm.startsWith("%")) {
                groupNameBothWayWildCard = true;
            } else if (groupNameSearchTerm.endsWith("%")) {
                groupNameStartsWithWildCard = true;
            } else if (groupNameSearchTerm.startsWith("%")) {
                groupNameEndsWithWildCard = true;
            }

            groupNameSearchTerm = groupNameSearchTerm.replaceAll("%", "");
        }

        if (groupIdSearchTerm != null && groupIdSearchTerm.length() > 0) {
            if (groupIdSearchTerm.endsWith("%") && groupIdSearchTerm.startsWith("%")) {
                groupIdBothWayWildCard = true;
            } else if (groupIdSearchTerm.endsWith("%")) {
                groupIdStartsWithWildCard = true;
            } else if (groupIdSearchTerm.startsWith("%")) {
                groupIdEndsWithWildCard = true;
            }

            groupIdSearchTerm = groupIdSearchTerm.replaceAll("%", "");
        }

        for (final ProviewGroup proviewGroup : allLatestProviewGroups) {
            boolean selected = true;

            if (groupNameSearchTerm != null && groupNameSearchTerm.length() > 0) {
                if (proviewGroup.getGroupName() == null) {
                    selected = false;
                } else {
                    if (groupNameBothWayWildCard) {
                        if (!proviewGroup.getGroupName().contains(groupNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (groupNameEndsWithWildCard) {
                        if (!proviewGroup.getGroupName().endsWith(groupNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (groupNameStartsWithWildCard) {
                        if (!proviewGroup.getGroupName().startsWith(groupNameSearchTerm)) {
                            selected = false;
                        }
                    } else if (!proviewGroup.getGroupName().equals(groupNameSearchTerm)) {
                        selected = false;
                    }
                }
            }
            if (selected) {
                if (groupIdSearchTerm != null && groupIdSearchTerm.length() > 0) {
                    if (proviewGroup.getGroupId() == null) {
                        selected = false;
                    } else {
                        if (groupIdBothWayWildCard) {
                            if (!proviewGroup.getGroupId().contains(groupIdSearchTerm)) {
                                selected = false;
                            }
                        } else if (groupIdEndsWithWildCard) {
                            if (!proviewGroup.getGroupId().endsWith(groupIdSearchTerm)) {
                                selected = false;
                            }
                        } else if (groupIdStartsWithWildCard) {
                            if (!proviewGroup.getGroupId().startsWith(groupIdSearchTerm)) {
                                selected = false;
                            }
                        } else if (!proviewGroup.getGroupId().equals(groupIdSearchTerm)) {
                            selected = false;
                        }
                    }
                }
            }
            if (selected) {
                selectedProviewGroupList.add(proviewGroup);
            }
        }
        return selectedProviewGroupList;
    }

    /**
     * @param httpSession
     * @param filterForm
     */
    protected void saveProviewGroupListFilterForm(
        final HttpSession httpSession,
        final ProviewGroupListFilterForm filterForm) {
        httpSession.setAttribute(ProviewGroupListFilterForm.FORM_NAME, filterForm);
    }

    /**
     * @param httpSession
     * @return
     */
    protected ProviewGroupListFilterForm fetchProviewGroupListFilterForm(final HttpSession httpSession) {
        ProviewGroupListFilterForm form =
            (ProviewGroupListFilterForm) httpSession.getAttribute(ProviewGroupListFilterForm.FORM_NAME);
        if (form == null) {
            form = new ProviewGroupListFilterForm();
        }
        return form;
    }

    /**
     * @param httpSession
     * @param allProviewGroups
     */
    protected void saveAllProviewGroups(
        final HttpSession httpSession,
        final Map<String, ProviewGroupContainer> allProviewGroups) {
        httpSession.setAttribute(WebConstants.KEY_ALL_PROVIEW_GROUPS, allProviewGroups);
    }

    /**
     * @param httpSession
     * @return
     */
    protected Map<String, ProviewGroupContainer> fetchAllProviewGroups(final HttpSession httpSession) {
        final Map<String, ProviewGroupContainer> allProviewGroups =
            (Map<String, ProviewGroupContainer>) httpSession.getAttribute(WebConstants.KEY_ALL_PROVIEW_GROUPS);
        return allProviewGroups;
    }

    /**
     * @param httpSession
     * @param selectedProviewGroupList
     */
    protected void saveSelectedProviewGroups(
        final HttpSession httpSession,
        final List<ProviewGroup> selectedProviewGroupList) {
        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, selectedProviewGroupList);
    }

    /**
     *
     * @param httpSession
     * @return
     */
    protected List<ProviewGroup> fetchSelectedProviewGroups(final HttpSession httpSession) {
        return (List<ProviewGroup>) httpSession.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS);
    }

    protected void savePaginatedList(final HttpSession httpSession, final List<GroupDetails> groupDetailsList) {
        httpSession.setAttribute(WebConstants.KEY_PAGINATED_LIST, groupDetailsList);
    }

    protected List<GroupDetails> fetchPaginatedList(final HttpSession httpSession) {
        return (List<GroupDetails>) httpSession.getAttribute(WebConstants.KEY_PAGINATED_LIST);
    }

    /**
     * @param httpSession
     * @param form
     */
    protected void saveProviewGroupForm(final HttpSession httpSession, final ProviewGroupForm form) {
        httpSession.setAttribute(ProviewGroupForm.FORM_NAME, form);
    }

    /**
     * @param httpSession
     * @return
     */
    protected ProviewGroupForm fetchProviewGroupForm(final HttpSession httpSession) {
        ProviewGroupForm form = (ProviewGroupForm) httpSession.getAttribute(ProviewGroupForm.FORM_NAME);
        if (form == null) {
            form = new ProviewGroupForm();
        }
        return form;
    }

    /**
     * @param httpSession
     * @param allLatestProviewGroups
     */
    protected void saveAllLatestProviewGroups(
        final HttpSession httpSession,
        final List<ProviewGroup> allLatestProviewGroups) {
        httpSession.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS, allLatestProviewGroups);
    }

    /**
     * @param httpSession
     * @return
     */
    protected List<ProviewGroup> fetchAllLatestProviewGroups(final HttpSession httpSession) {
        final List<ProviewGroup> allLatestProviewGroupList =
            (List<ProviewGroup>) httpSession.getAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS);
        return allLatestProviewGroupList;
    }
}
