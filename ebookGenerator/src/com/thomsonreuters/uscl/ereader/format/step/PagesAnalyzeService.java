package com.thomsonreuters.uscl.ereader.format.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PAGEBREAK;

import java.io.File;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PagesAnalyzeService {
    @Autowired
    private JsoupService jsoup;

    public boolean checkIfDocumentsContainPagebreaks(final File docsDir) {
        for (final File doc : docsDir.listFiles((dir, name) -> name.endsWith(".xml"))) {
            if (documentContainsPagebreak(doc)) {
                return true;
            }
        }
        return false;
    }

    private boolean documentContainsPagebreak(final File document) {
        final Document doc = jsoup.loadDocument(document);
        return doc.getElementsByTag(PAGEBREAK).size() > 0;
    }

}
