package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProviewGroupListServiceImplTest {
    private static final String GROUP_ID = "groupId";
    private static final String GROUP_NAME = "groupName";
    private static final String GROUP_FILTER_NAME = "%group%";

    @InjectMocks
    private ProviewGroupListServiceImpl service;
    @Mock
    private ProviewHandler proviewHandler;
    @Mock
    private ProviewAuditService proviewAuditService;
    @Mock
    private AllProviewGroupsProvider allProviewGroupsProvider;

    @Test
    public void getProviewGroups_formWithFiltersIsGiven_someProviewGroupsAreFilteredOut() throws ProviewException {
        final ProviewGroupForm form = new ProviewGroupForm();
        form.setGroupFilterName(GROUP_FILTER_NAME);
        final ProviewGroup proviewGroup = createProviewGroup(GROUP_NAME, GROUP_ID);
        final ProviewGroup proviewGroupToFilterOut = createProviewGroup("filter", "out");
        final List<ProviewGroup> allLatestProviewGroups = Arrays.asList(proviewGroup, proviewGroupToFilterOut);
        final List<ProviewGroup> filteredProviewGroups = Collections.singletonList(proviewGroup);

        final ProviewGroupsContainer container = service.getProviewGroups(form, allLatestProviewGroups);

        //Will have filtered data only (partial list) if filters applied and not full list
        assertEquals(filteredProviewGroups, container.getAllLatestProviewGroups());
        //Will have sorted & paginated data list
        assertEquals(filteredProviewGroups, container.getSelectedProviewGroups());
    }

    @Test
    public void getProviewGroups_noProviewGroupParamsAreGiven_proviewGroupsFromProviewHandlerAreReturned() throws ProviewException {
        final ProviewGroupForm form = new ProviewGroupForm();
        final Map<String, ProviewGroupContainer> allProviewGroups = Collections.emptyMap();
        final ProviewGroup proviewGroup1 = createProviewGroup(GROUP_NAME, GROUP_ID);
        final ProviewGroup proviewGroup2 = createProviewGroup("name", "id");
        final List<ProviewGroup> allLatestProviewGroups = Arrays.asList(proviewGroup1, proviewGroup2);
        when(proviewAuditService.findMaxRequestDateByTitleIds(any())).thenReturn(null);
        when(proviewHandler.getAllLatestProviewGroupInfo(allProviewGroups)).thenReturn(allLatestProviewGroups);
        when(allProviewGroupsProvider.getAllProviewGroups(false)).thenReturn(allProviewGroups);

        final ProviewGroupsContainer container = service.getProviewGroups(form, null);

        //Will have full list, if no filters
        assertEquals(allLatestProviewGroups, container.getAllLatestProviewGroups());
        //Will have sorted & paginated data list
        assertEquals(allLatestProviewGroups, container.getSelectedProviewGroups());
        verify(proviewHandler).getAllLatestProviewGroupInfo(allProviewGroups);
    }

    private ProviewGroup createProviewGroup(final String groupName, final String groupId) {
        final ProviewGroup proviewGroup = new ProviewGroup();
        proviewGroup.setGroupName(groupName);
        proviewGroup.setGroupId(groupId);
        return proviewGroup;
    }
}
