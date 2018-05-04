package com.thomsonreuters.uscl.ereader.request.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.dao.PrintComponentHistoryDao;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponentHistory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class PrintComponentHistoryServiceTest {
    private static final long BOOK_DEFINITION_ID = 12345L;
    private static final String VERSION = "2.0.1";
    private static final String BOOK_DEFINITION_VERSION = "2.0";
    private static final Integer PRINT_COMPONENT_VERSION = 1;
    private static final String VERSION_10 = "10.2.5";
    private static final String VERSION_1 = "1.1.1";
    private static final String VERSION_9 = "9.0.1";

    @InjectMocks
    private PrintComponentHistoryServiceImpl service;

    @Mock
    private PrintComponentHistoryDao printComponentHistoryDao;

    private List<String> versions;
    private Set<PrintComponentHistory> printComponentHistoryTable;

    @Before
    public void init() {
        versions = getPrintComponentsVersionsFromDatabase();
        printComponentHistoryTable = new HashSet<>();
        when(printComponentHistoryDao.findPrintComponentVersionsByEbookDefinitionId(BOOK_DEFINITION_ID)).thenReturn(versions);
        when(printComponentHistoryDao.findPrintComponentByVersion(BOOK_DEFINITION_ID, VERSION)).thenReturn(printComponentHistoryTable);
        when(printComponentHistoryDao.getLatestPrintComponentHistoryVersion(BOOK_DEFINITION_ID, BOOK_DEFINITION_VERSION)).thenReturn(Optional.of(PRINT_COMPONENT_VERSION));
    }

    @Test
    public void shouldGetListOfVersions() {
        final List<String> sortedVersions = service.findPrintComponentVersionsList(BOOK_DEFINITION_ID);

        assertEquals(sortedVersions.get(0), VERSION_10);
        assertEquals(sortedVersions.get(1), VERSION_9);
        assertEquals(sortedVersions.get(2), VERSION_1);
    }

    @Test
    public void shouldGetPrintComponentOfGivenVersion() {
        assertEquals(service.findPrintComponentByVersion(BOOK_DEFINITION_ID, VERSION), printComponentHistoryTable);
    }

    @Test
    public void shouldGetLatestPrintComponentHistoryVersionNumber() {
        assertEquals(service.getLatestPrintComponentHistoryVersion(BOOK_DEFINITION_ID, BOOK_DEFINITION_VERSION).orElse(Integer.MIN_VALUE), PRINT_COMPONENT_VERSION);
    }

    @Test
    public void shouldSavePrintComponents() {
        final BookDefinition bookDefinition = new BookDefinition();
        final String materialNumber = "11111111";
        final String componentName = "CLNY";

        final PrintComponent printComponent = new PrintComponent();
        printComponent.setBookDefinition(bookDefinition);
        printComponent.setPrintComponentId("abc");
        printComponent.setComponentOrder(1);
        printComponent.setMaterialNumber(materialNumber);
        printComponent.setComponentName(componentName);
        printComponent.setSplitter(false);

        service.savePrintComponents(Collections.singletonList(printComponent), BOOK_DEFINITION_ID, BOOK_DEFINITION_VERSION, PRINT_COMPONENT_VERSION);

        then(printComponentHistoryDao).should().save(
            argThat(
                new ArgumentMatcher<PrintComponentHistory>() {
                    @Override
                    public boolean matches(final Object argument) {
                        final PrintComponentHistory componentToSave = (PrintComponentHistory) argument;

                        assertNotEquals(componentToSave.getPrintComponentId(), printComponent.getPrintComponentId());
                        assertEquals(componentToSave.getEbookDefinition(), printComponent.getBookDefinition());
                        assertEquals(componentToSave.getEbookDefinitionVersion(), BOOK_DEFINITION_VERSION);
                        assertTrue(componentToSave.getPrintComponentVersion() == PRINT_COMPONENT_VERSION);
                        assertEquals(componentToSave.getComponentOrder(), printComponent.getComponentOrder());
                        assertEquals(componentToSave.getMaterialNumber(), printComponent.getMaterialNumber());
                        assertEquals(componentToSave.getComponentName(), printComponent.getComponentName());
                        assertTrue(componentToSave.getSplitter() == printComponent.getSplitter());

                        return true;
                    }
                  }
            ));
    }

    @Test
    public void shouldSavePrintComponents2() {
        final List<PrintComponent> printComponents = getPrintComponents();
        service.savePrintComponents(printComponents, BOOK_DEFINITION_ID, BOOK_DEFINITION_VERSION, PRINT_COMPONENT_VERSION);

        then(printComponentHistoryDao).should(times(printComponents.size())).save(any(PrintComponentHistory.class));
    }

    private List<PrintComponent> getPrintComponents() {
        return Arrays.asList(
                new PrintComponent(),
                new PrintComponent(),
                new PrintComponent()
            );
    }

    private List<String> getPrintComponentsVersionsFromDatabase() {
        return Arrays.asList(
                VERSION_1,
                VERSION_10,
                VERSION_9
            );
    }
}
