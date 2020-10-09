package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.domain.UnifiedTocElement;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.thomsonreuters.uscl.ereader.assemble.service.TitleManifestFilter.ENTRY_ELEMENT;
import static com.thomsonreuters.uscl.ereader.assemble.service.TitleManifestFilter.TEXT_ELEMENT;
import static com.thomsonreuters.uscl.ereader.assemble.service.TitleManifestFilter.TOC_ELEMENT;
import static com.thomsonreuters.uscl.ereader.format.domain.UnifiedTocElement.TOC_UNIFIED;

/**
 * From
 *
 <?xml version="1.0" encoding="utf-8"?>
 <title>
     ...
     <toc>
         <entry s="ellis_fdc_com_20_html/toc0970">
             <text>Chapter 20 — Remedies</text>
             <entry s="ellis_fdc_com_20_html/toc0977">
                 <text>3. — Types of Relief in Equity</text>
                 <entry s="ellis_fdc_com_20_html/toc0978">
                    <text>(1) — Generally</text>
                 </entry>
                 <entry s="ellis_fdc_com_20_html/toc0979">
                    <text>(2) — Declaratory Relief: Declaration of Trustee Status</text>
                 </entry>
             </entry>
         </entry>
     </toc>
 </title>
 *
 * to
 *
 * <toc>
 * 	<entry originalName="Chapter 20 — Remedies" docId="ellis_fdc_com_20_html" keyName="remedies" uniqueKey="remedies-ellis_fdc_com_20_html-toc0970">
 * 		<entry originalName="3. — Types of Relief in Equity" docId="ellis_fdc_com_20_html" keyName="typesofreliefinequity" uniqueKey="typesofreliefinequity-ellis_fdc_com_20_html-toc0977">
 * 			<entry originalName="(1) — Generally" docId="ellis_fdc_com_20_html" keyName="generally" uniqueKey="generally-ellis_fdc_com_20_html-toc0978"/>
 * 			<entry originalName="(2) — Declaratory Relief: Declaration of Trustee Status" docId="ellis_fdc_com_20_html" keyName="declaratoryreliefdeclarationoftrusteestatus" uniqueKey="declaratoryreliefdeclarationoftrusteestatus-ellis_fdc_com_20_html-toc0979"/>
 * 		</entry>
 * 	</entry>
 * </toc>
 */
@Component
public class TitleXmlUnifiedConverter {
    @Autowired
    private JsoupService jsoup;

    public Document convertDocumentToUnifiedFormat(final String oldVersionTitleXml) {
        Document document = jsoup.parseXml(oldVersionTitleXml);
        Document unifiedDoc = jsoup.createDocument();
        Element toc = unifiedDoc.appendElement(TOC_UNIFIED);
        convertEntryChildren(document.selectFirst(TOC_ELEMENT), toc);
        return unifiedDoc;
    }

    private void convertEntryChildren(final Element entryParent, final Element unifiedParent) {
        entryParent.children().stream()
                .filter(entry -> ENTRY_ELEMENT.equals(entry.tagName()))
                .forEach(entry -> convertEntry(entry, unifiedParent));
    }

    private void convertEntry(final Element entry, final Element unifiedParent) {
        Element unifiedEntry = new UnifiedTocElement(extractName(entry), extractDocumentId(entry), extractAnchorId(entry));
        unifiedParent.appendChild(unifiedEntry);
        convertEntryChildren(entry, unifiedEntry);
    }

    private String extractName(final Element entry) {
        return entry.selectFirst(TEXT_ELEMENT).text();
    }

    private String extractDocumentId(final Element entry) {
        String documentRef = entry.attr("s");
        return documentRef.split("/")[0];
    }

    private String extractAnchorId(final Element entry) {
        String anchorId = entry.attr("s");
        return anchorId.split("/")[1];
    }
}
