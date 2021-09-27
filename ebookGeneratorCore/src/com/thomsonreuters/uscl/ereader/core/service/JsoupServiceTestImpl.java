package com.thomsonreuters.uscl.ereader.core.service;

import org.jsoup.nodes.Document;

public class JsoupServiceTestImpl extends JsoupServiceImpl {
    private static final int INDENT_AMOUNT = 1;
    private static final boolean PRETTY_PRINT = true;

    @Override
    protected void applyPrintSettings(final Document doc) {
        doc.outputSettings().indentAmount(INDENT_AMOUNT).prettyPrint(PRETTY_PRINT);
    }
}
