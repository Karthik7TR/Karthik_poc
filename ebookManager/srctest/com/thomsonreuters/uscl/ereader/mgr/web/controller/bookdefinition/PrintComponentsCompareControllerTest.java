package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponentHistory;
import com.thomsonreuters.uscl.ereader.request.service.PrintComponentHistoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

@RunWith(MockitoJUnitRunner.class)
public final class PrintComponentsCompareControllerTest {
    private static final int HTTP_200 = 200;
    private static final int HTTP_NO_CONTENT = 204;
    private static final Long BOOK_DEFINITION_ID = 12345L;
    private static final Long ANOTHER_BOOK_DEFINITION_ID = 1L;
    private static final String LAST_VERSION_NUMBER = "2.0.0";
    private static final String MATERIAL_NUMBER = "11111111";
    private static final String PRINT_COMPONENTS_JSON = "[{&quot;printComponentId&quot;:&quot;fde&quot;,&quot;componentOrder&quot;:2,&quot;materialNumber&quot;:&quot;-------------&quot;,&quot;componentName&quot;:&quot;SPLITTER&quot;,&quot;splitter&quot;:true},{&quot;printComponentId&quot;:&quot;ffd&quot;,&quot;componentOrder&quot;:4,&quot;materialNumber&quot;:&quot;-------------&quot;,&quot;componentName&quot;:&quot;SPLITTER&quot;,&quot;splitter&quot;:true},{&quot;printComponentId&quot;:&quot;abf&quot;,&quot;componentOrder&quot;:1,&quot;materialNumber&quot;:&quot;11111111&quot;,&quot;componentName&quot;:&quot;CHAL 1&quot;,&quot;splitter&quot;:false},{&quot;printComponentId&quot;:&quot;cda&quot;,&quot;componentOrder&quot;:3,&quot;materialNumber&quot;:&quot;11111111&quot;,&quot;componentName&quot;:&quot;CHAL 2&quot;,&quot;splitter&quot;:false},{&quot;printComponentId&quot;:&quot;dac&quot;,&quot;componentOrder&quot;:5,&quot;materialNumber&quot;:&quot;11111111&quot;,&quot;componentName&quot;:&quot;CHAL 3&quot;,&quot;splitter&quot;:false}]";

    @InjectMocks
    private PrintComponentsCompareController printComponentsCompareController;
    @Mock
    private PrintComponentHistoryService printComponentHistoryService;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private PrintWriter writer;

    private List<String> versions;

    private BookDefinition bookDefinition;

    @Before
    public void init() throws IOException {
        versions = Arrays.asList(LAST_VERSION_NUMBER, "1.1.1", "1.1.0", "1.0.0");
        when(printComponentHistoryService.findPrintComponentVersionsList(BOOK_DEFINITION_ID)).thenReturn(versions);
        when(printComponentHistoryService.findPrintComponentVersionsList(ANOTHER_BOOK_DEFINITION_ID)).thenReturn(Collections.emptyList());
        when(printComponentHistoryService.findPrintComponentByVersion(BOOK_DEFINITION_ID, LAST_VERSION_NUMBER)).thenReturn(getPrintComponentHistoryTableContent());
        when(httpServletResponse.getWriter()).thenReturn(writer);
        bookDefinition = new BookDefinition();
        bookDefinition.setEbookDefinitionId(BOOK_DEFINITION_ID);
    }

    @Test
    public void shouldGetPrintComponentsLastVersion() throws JsonProcessingException {
        printComponentsCompareController.getPrintComponentsLastVersion(BOOK_DEFINITION_ID, httpServletResponse);

        then(httpServletResponse).should().setStatus(eq(HTTP_200));
        then(writer).should().write(eq(PRINT_COMPONENTS_JSON));
    }

    @Test
    public void shouldGetPrintComponentsNoHistory() throws JsonProcessingException {
        when(printComponentHistoryService.findPrintComponentVersionsList(BOOK_DEFINITION_ID)).thenReturn(Collections.emptyList());

        printComponentsCompareController.getPrintComponentsLastVersion(BOOK_DEFINITION_ID, httpServletResponse);

        then(httpServletResponse).should().setStatus(eq(HTTP_NO_CONTENT));
    }

    @Test
    public void shouldGetPrintComponentsByVersion() throws JsonProcessingException {
        printComponentsCompareController.getPrintComponentsByVersion(BOOK_DEFINITION_ID, LAST_VERSION_NUMBER, httpServletResponse);

        then(httpServletResponse).should().setStatus(eq(HTTP_200));
        then(writer).should().write(eq(PRINT_COMPONENTS_JSON));
    }

    @Test
    public void shouldGetPrintComponentsByNonExistingVersion() throws JsonProcessingException {
        printComponentsCompareController.getPrintComponentsByVersion(BOOK_DEFINITION_ID, "0.0.0", httpServletResponse);

        then(httpServletResponse).should().setStatus(eq(HTTP_NO_CONTENT));
    }

    @Test
    public void shouldSetPrintComponentHistoryAttributes() {
        final Model model = new BindingAwareModelMap();
        printComponentsCompareController.setPrintComponentHistoryAttributes(BOOK_DEFINITION_ID, model);

        final Map<String, Object> modelMap =  model.asMap();
        assertEquals(modelMap.get("hasPrintComponentsHistory"), true);
        assertEquals(modelMap.get("printComponentsHistoryVersions"), versions);
        assertEquals(modelMap.get("printComponentsHistoryLastVersionNumber"), LAST_VERSION_NUMBER);
    }

    @Test
    public void shouldSetPrintComponentHistoryAttributes2() {
        final Model model = new BindingAwareModelMap();
        printComponentsCompareController.setPrintComponentHistoryAttributes(ANOTHER_BOOK_DEFINITION_ID, model);

        final Map<String, Object> modelMap =  model.asMap();
        assertEquals(modelMap.get("hasPrintComponentsHistory"), false);
        assertEquals(modelMap.get("printComponentsHistoryVersions"), Collections.emptyList());
        assertEquals(modelMap.get("printComponentsHistoryLastVersionNumber"), "");
    }

    private Set<PrintComponentHistory> getPrintComponentHistoryTableContent() {
        return Stream.of(
                getPrintComponent("abf", 1, "CHAL 1"),
                getSplitterObject("fde", 2),
                getPrintComponent("cda", 3, "CHAL 2"),
                getSplitterObject("ffd", 4),
                getPrintComponent("dac", 5, "CHAL 3")
            ).collect(Collectors.toSet());
    }

    private PrintComponentHistory getPrintComponent(final String printComponentId, final int componentOrder, final String componentName) {
        return getPrintComponentObject(printComponentId, componentOrder, MATERIAL_NUMBER, componentName, false);
    }

    private PrintComponentHistory getSplitterObject(final String printComponentId, final int componentOrder) {
        return getPrintComponentObject(printComponentId, componentOrder, "-------------", "SPLITTER", true);
    }

    private PrintComponentHistory getPrintComponentObject(final String printComponentId, final int componentOrder, final String materialNumber, final String componentName, final boolean isSplitter) {
        final PrintComponentHistory printComponent = new PrintComponentHistory();
        printComponent.setEbookDefinition(bookDefinition);
        printComponent.setPrintComponentId(printComponentId);
        printComponent.setEbookDefinitionVersion("2.0");
        printComponent.setPrintComponentVersion(0);
        printComponent.setComponentOrder(componentOrder);
        printComponent.setMaterialNumber(materialNumber);
        printComponent.setComponentName(componentName);
        printComponent.setSplitter(isSplitter);
        return printComponent;
    }
}
