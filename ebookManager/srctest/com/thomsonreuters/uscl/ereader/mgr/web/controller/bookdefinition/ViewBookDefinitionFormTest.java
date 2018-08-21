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

public final class ViewBookDefinitionFormTest {
    private static final String printComponentsJson =
        "[{&quot;printComponentId&quot;:&quot;1&quot;,&quot;componentOrder&quot;:1,&quot;materialNumber&quot;:&quot;123&quot;,&quot;componentName&quot;:&quot;c1&quot;,&quot;splitter&quot;:false,&quot;componentInArchive&quot;:false,&quot;supplement&quot;:false}]";

    private static final String escaleXMLPrintComp =
        "[{&quot;printComponentId&quot;:&quot;1&quot;,&quot;componentOrder&quot;:1,&quot;materialNumber&quot;:&quot;123&quot;,&quot;componentName&quot;:&quot;c&apos;1&quot;,&quot;splitter&quot;:false,&quot;componentInArchive&quot;:false,&quot;supplement&quot;:false}]";

    private static final String specialCharacterPrintComp =
        "[{&quot;printComponentId&quot;:&quot;1&quot;,&quot;componentOrder&quot;:1,&quot;materialNumber&quot;:&quot;123&quot;,&quot;componentName&quot;:&quot;c@1&quot;,&quot;splitter&quot;:true,&quot;componentInArchive&quot;:false,&quot;supplement&quot;:false}]";

    private ViewBookDefinitionForm form;

    @Before
    public void setUp() {
        form = new ViewBookDefinitionForm();
    }

    @Test
    public void shouldReturnPrintComponentsInJsonFormat() throws JsonParseException, JsonMappingException, IOException {
        final BookDefinition bookDef = new BookDefinition();
        bookDef.setPrintComponents(getPrintComponents());
        form.setBookDefinition(bookDef);

        Assert.assertEquals(printComponentsJson, form.getPrintComponents());
    }

    @Test
    public void testEscaleXML() throws JsonParseException, JsonMappingException, IOException {
        final BookDefinition bookDef = new BookDefinition();
        final List<PrintComponent> printComponents = getPrintComponents();
        printComponents.get(0).setComponentName("c'1");
        bookDef.setPrintComponents(printComponents);
        form.setBookDefinition(bookDef);

        Assert.assertEquals(escaleXMLPrintComp, form.getPrintComponents());
    }

    @Test
    public void testSpecialCharacter() throws JsonParseException, JsonMappingException, IOException {
        final BookDefinition bookDef = new BookDefinition();
        final List<PrintComponent> printComponents = getPrintComponents();
        printComponents.get(0).setComponentName("c@1");
        printComponents.get(0).setSplitter(true);
        bookDef.setPrintComponents(printComponents);
        form.setBookDefinition(bookDef);

        Assert.assertEquals(specialCharacterPrintComp, form.getPrintComponents());
    }

    private List<PrintComponent> getPrintComponents() {
        final PrintComponent printComponent = new PrintComponent();
        printComponent.setPrintComponentId("1");
        printComponent.setComponentOrder(1);
        printComponent.setMaterialNumber("123");
        printComponent.setComponentName("c1");
        return Collections.singletonList(printComponent);
    }
}
