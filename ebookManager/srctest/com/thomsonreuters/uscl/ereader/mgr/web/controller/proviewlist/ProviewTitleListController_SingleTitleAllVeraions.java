package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static java.util.Arrays.asList;

import static com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListMatchers.container;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListMatchers.title;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListMatchers.titleInfo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public class ProviewTitleListController_SingleTitleAllVeraions
{
    @InjectMocks
    private ProviewTitleListController controller;
    @Mock
    private ProviewHandler proviewHandler;
    @Mock
    private ProviewTitleListService proviewTitleListService;
    @Mock
    private BookDefinition book;
    private MockMvc mockMvc;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnTitlesList() throws Exception
    {
        // given
        final List<ProviewTitleInfo> titleInfos = asList(titleInfo("v1.0", "Final"), titleInfo("v1.1", "Review"));
        final List<ProviewTitle> titles = asList(title("v1.0", "Final"), title("v1.1", "Review"));
        givenTitles(titleInfos, titles);
        // when - then
        mockMvc
            .perform(get("/" + WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS).param(WebConstants.KEY_TITLE_ID, "titleId"))
            .andExpect(model().attribute(WebConstants.KEY_PAGINATED_LIST, titles))
            .andExpect(model().attribute(WebConstants.KEY_TOTAL_BOOK_SIZE, 2))
            .andExpect(model().attributeDoesNotExist(WebConstants.KEY_INFO_MESSAGE));
    }

    @Test
    public void shouldReturnInfoMessageIfNoBookFound() throws Exception
    {
        // given
        // when - then
        mockMvc
            .perform(get("/" + WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS).param(WebConstants.KEY_TITLE_ID, "titleId"))
            .andExpect(model().attributeExists(WebConstants.KEY_INFO_MESSAGE));
    }

    @Test
    public void shouldReturnInfoMessageInProgressBook() throws Exception
    {
        // given
        final List<ProviewTitleInfo> titleInfos = asList(titleInfo("v1.0", "Final"), titleInfo("v1.1", "Review"));
        final List<ProviewTitle> titles = asList(title("v1.0", "Final"), title("v1.1", "Review"));
        givenTitles(titleInfos, titles);

        final Map<String, ProviewTitleContainer> allTitleInfos = new HashMap<>();
        allTitleInfos.put("titleId", container(titleInfos));
        given(book.getPilotBookStatus()).willReturn(PilotBookStatus.IN_PROGRESS);
        // when - then
        mockMvc
            .perform(
                get("/" + WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS)
                    .sessionAttr(WebConstants.KEY_ALL_PROVIEW_TITLES, allTitleInfos)
                    .param(WebConstants.KEY_TITLE_ID, "titleId"))
            .andExpect(model().attributeExists(WebConstants.KEY_INFO_MESSAGE));
    }

    private void givenTitles(final List<ProviewTitleInfo> titleInfos, final List<ProviewTitle> titles) throws ProviewException
    {
        final Map<String, ProviewTitleContainer> allTitleInfos = new HashMap<>();
        allTitleInfos.put("titleId", container(titleInfos));
        given(proviewHandler.getAllProviewTitleInfo()).willReturn(allTitleInfos);
        given(proviewTitleListService.getBook(any(TitleId.class))).willReturn(book);
        given(proviewTitleListService.getProviewTitles(titleInfos, book)).willReturn(titles);
    }
}
