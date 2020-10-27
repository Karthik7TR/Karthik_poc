package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class TableStylesAddition implements JsoupTransformation {
    private static final String TABLE_SELECTOR = "table.co_borderedTable";
    private static final String CW_FIXED_TABLE = "cw_fixed_table";
    private static final String TH = "th";
    private static final String TD = "td";
    private static final String CW_PADDING_4 = "cw_padding_4";

    @Override
    public void transform(final String fileName, final Document document, final BookStep bookStep) {
        if (bookStep.getBookDefinition().isCwBook()) {
            Elements tables = document.select(TABLE_SELECTOR);
            tables.forEach(table -> {
                table.addClass(CW_FIXED_TABLE);
                table.select(TH).addClass(CW_PADDING_4);
                table.select(TD).addClass(CW_PADDING_4);
            });
        }
    }
}
