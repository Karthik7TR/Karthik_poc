package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class TableStylesAddition implements JsoupTransformation {
    private static final String TABLE_SELECTOR = "table.co_borderedTable";
    private static final String FIXED_TABLE = "fixed_table";
    private static final String TH = "th";
    private static final String TD = "td";
    private static final String PADDING_4 = "padding_4";
    private static final String TEXT_INDENT_0 = "text_indent_0";

    @Override
    public void transform(final File file, final Document document, final BookStep bookStep) {
        Elements tables = document.select(TABLE_SELECTOR);
        tables.forEach(table -> {
            table.addClass(FIXED_TABLE);
            table.select(TH).addClass(PADDING_4);
            table.select(TD).addClass(PADDING_4)
                    .addClass(TEXT_INDENT_0);
        });
    }
}
