package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ViewBookDefinitionFormTest
{
    private static final String printComponentsJson = "[{\"printComponentId\":\"1\",\"componentOrder\":1,\"materialNumber\":\"123\",\"componentName\":\"c1\"}]";

    private ViewBookDefinitionForm form;

    @Before
    public void setUp()
    {
        form = new ViewBookDefinitionForm();
    }

    @Test
    public void shouldReturnPrintComponentsInJsonFormat() throws JsonParseException, JsonMappingException, IOException
    {
        final BookDefinition bookDef = new BookDefinition();
        bookDef.setPrintComponents(getPrintComponents());
        form.setBookDefinition(bookDef);

        Assert.assertEquals(printComponentsJson, form.getPrintComponents());
    }

    private List<PrintComponent> getPrintComponents()
    {
        final PrintComponent printComponent = new PrintComponent();
        printComponent.setPrintComponentId("1");
        printComponent.setComponentOrder(1);
        printComponent.setMaterialNumber("123");
        printComponent.setComponentName("c1");
        return Collections.singletonList(printComponent);
    }
}
