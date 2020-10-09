package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.domain.UnifiedTocElement;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

import static com.thomsonreuters.uscl.ereader.assemble.service.TitleManifestFilter.DOCUMENT_GUID;
import static com.thomsonreuters.uscl.ereader.assemble.service.TitleManifestFilter.EBOOK;
import static com.thomsonreuters.uscl.ereader.assemble.service.TitleManifestFilter.EBOOK_TOC;
import static com.thomsonreuters.uscl.ereader.assemble.service.TitleManifestFilter.NAME;
import static com.thomsonreuters.uscl.ereader.assemble.service.TitleManifestFilter.TOC_GUID;
import static com.thomsonreuters.uscl.ereader.format.domain.UnifiedTocElement.TOC_UNIFIED;

/**
 * From
 *
 *  <?xml version="1.0" encoding="UTF-8"?>
 *  <EBook>
 *      <EBookToc>
 *          <Name>Part VIII. Remedies</Name>
 *          <Guid>Ia49676705e2011eaaeca8a99d55be43a1184</Guid>
 *          <EBookToc>
 *              <Name>III. Types of Relief in Equity</Name>
 *              <Guid>Ia4b76bf05e2011eaaeca8a99d55be43a1193</Guid>
 *              <EBookToc>
 *                  <Name>&#167;  20:1. Generally</Name>
 *                  <Guid>I85b24ddca11d11eabafbf5373d14f37c1194</Guid>
 *                  <DocumentGuid>I85b24ddda11d11eabafbf5373d14f37c</DocumentGuid>
 *              </EBookToc>
 *              <EBookToc>
 *                  <Name>&#167;  20:2. Declaratory Relief: Declaration of Trustee Status</Name>
 *                  <Guid>I85b24ddfa11d11eabafbf5373d14f37c1195</Guid>
 *                  <DocumentGuid>I85b24de0a11d11eabafbf5373d14f37c</DocumentGuid>
 *              </EBookToc>
 *          </EBookToc>
 *      </EBookToc>
 *  </EBook>
 *
 * to
 *
 * <toc>
 * 	<entry originalName="Part VIII. Remedies" docId="I85b24ddda11d11eabafbf5373d14f37c" keyName="remedies" uniqueKey="remedies-I85b24ddda11d11eabafbf5373d14f37c-Ia49676705e2011eaaeca8a99d55be43a1184">
 * 		<entry originalName="III. Types of Relief in Equity" docId="I85b24ddda11d11eabafbf5373d14f37c" keyName="typesofreliefinequity" uniqueKey="typesofreliefinequity-I85b24ddda11d11eabafbf5373d14f37c-Ia4b76bf05e2011eaaeca8a99d55be43a1193">
 * 			<entry originalName="&#167;  20:1. Generally" docId="I85b24ddda11d11eabafbf5373d14f37c" keyName="generally" uniqueKey="generally-I85b24ddda11d11eabafbf5373d14f37c-I85b24ddca11d11eabafbf5373d14f37c1194"/>
 * 			<entry originalName="&#167;  20:2. Declaratory Relief: Declaration of Trustee Status" docId="I85b24de0a11d11eabafbf5373d14f37c" keyName="declaratoryreliefdeclarationoftrusteestatus" uniqueKey="declaratoryreliefdeclarationoftrusteestatus-I85b24de0a11d11eabafbf5373d14f37c-I85b24ddfa11d11eabafbf5373d14f37c1195"/>
 * 		</entry>
 * 	</entry>
 * </toc>
 */
@Component
public class TocXmlUnifiedConverter {
    @Autowired
    private JsoupService jsoup;

    public Document convertDocumentToUnifiedFormat(final File tocXmlFile, final Map<String, String> familyGuidMap) {
        Document document = jsoup.loadDocument(tocXmlFile);
        Document unifiedDoc = jsoup.createDocument();
        Element toc = unifiedDoc.appendElement(TOC_UNIFIED);
        convertEntryChildren(document.selectFirst(EBOOK), toc, familyGuidMap);
        return unifiedDoc;
    }

    private void convertEntryChildren(final Element entryParent, final Element unifiedParent, final Map<String, String> familyGuidMap) {
        entryParent.children().stream()
                .filter(entry -> EBOOK_TOC.equals(entry.tagName()))
                .forEach(entry -> convertEntry(entry, unifiedParent, familyGuidMap));
    }

    private void convertEntry(final Element entryEBookToc, final Element unifiedParent, final Map<String, String> familyGuidMap) {
        Element unifiedEntry = new UnifiedTocElement(extractName(entryEBookToc), extractDocumentGuid(entryEBookToc), extractGuid(entryEBookToc));
        unifiedParent.appendChild(unifiedEntry);
        convertEntryChildren(entryEBookToc, unifiedEntry, familyGuidMap);
    }

    private String extractName(final Element entryEBookToc) {
        return entryEBookToc.selectFirst(NAME).text();
    }

    private String extractGuid(final Element entryEBookToc) {
        return entryEBookToc.selectFirst(TOC_GUID).text();
    }

    private String extractDocumentGuid(final Element entryEBookToc) {
        return entryEBookToc.selectFirst(DOCUMENT_GUID).text();
    }
}
