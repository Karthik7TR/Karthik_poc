package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import lombok.Setter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.LABEL_NO;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PAGEBREAK;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PAGEBREAK_WRAPPER_CLOSE;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PAGEBREAK_WRAPPER_OPEN;

/**
 * Filter that handles various content changes
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class XMLContentChangerFilter extends XMLFilterImpl {
    private static final String GUID_ATTR = "n-include_guid";
    private static final String CURRENCY_TAG = "include.currency";
    private static final String SECTION = "section";
    private static final String FOOTNOTE_BODY = "footnote.body";
    private static final String APPENDIX_BODY = "appendix.body";
    private static final String CONTENT_METADATA_BLOCK = "content.metadata.block";
    private static final String PROP_BLOCK = "prop.block";
    private static final String MESSAGE_BLOCK = "message.block";
    private static final String INDEX = "index";
    private static final String CTBL_PI = "ctbl";
    private boolean isChanging;
    private List<DocumentCopyright> copyrights;
    private List<DocumentCopyright> copyCopyrights;

    private static final String COPYRIGHT_TAG = "include.copyright";
    private List<DocumentCurrency> currencies;
    private List<DocumentCurrency> copyCurrencies;

    @Setter
    private boolean protectPagebreaks;
    private boolean canSavePagebreaks;

    public XMLContentChangerFilter(
        final List<DocumentCopyright> copyrights,
        final List<DocumentCopyright> copyCopyrights,
        final List<DocumentCurrency> currencies,
        final List<DocumentCurrency> copyCurrencies,
        final boolean protectPagebreaks) {
        super();
        this.copyrights = copyrights;
        this.copyCopyrights = copyCopyrights;
        this.currencies = currencies;
        this.copyCurrencies = copyCurrencies;
        this.protectPagebreaks = protectPagebreaks;
    }

    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (!CTBL_PI.equalsIgnoreCase(target)) super.processingInstruction(target, data);
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (qName.equalsIgnoreCase(CURRENCY_TAG)) {
            final String guid = atts.getValue(GUID_ATTR);
            for (final DocumentCurrency currency : currencies) {
                if (currency.getCurrencyGuid().equalsIgnoreCase(guid)) {
                    isChanging = true;
                    copyCurrencies.remove(currency);
                    replaceMessageElement(uri, localName, qName, atts, currency.getNewText());
                }
            }
        } else if (qName.equalsIgnoreCase(COPYRIGHT_TAG)) {
            final String guid = atts.getValue(GUID_ATTR);
            for (final DocumentCopyright copyright : copyrights) {
                if (copyright.getCopyrightGuid().equalsIgnoreCase(guid)) {
                    isChanging = true;
                    copyCopyrights.remove(copyright);
                    replaceMessageElement(uri, localName, qName, atts, copyright.getNewText());
                }
            }
        } else if (qName.equals(SECTION) || qName.equals(FOOTNOTE_BODY) || qName.equals(APPENDIX_BODY) || qName.equals(INDEX)
                || qName.equals(CONTENT_METADATA_BLOCK) || qName.equals(PROP_BLOCK) || qName.equals(MESSAGE_BLOCK)) {
            canSavePagebreaks = true;
        } else if (qName.equals(PAGEBREAK)) {
            isChanging = true;
            protectPagebreak(atts);
        }
        // Only use current element is it is not changing
        if (!isChanging) {
            super.startElement(uri, localName, qName, atts);
        }
    }

    private void replaceMessageElement(
        final String uri,
        final String localName,
        final String qName,
        final Attributes atts,
        final String message) throws SAXException {
        super.startElement(uri, localName, qName, atts);
        super.characters(message.toCharArray(), 0, message.length());
        super.endElement(uri, localName, qName);
    }

    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException {
        if (!isChanging) {
            super.characters(buf, offset, len);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (qName.equals(SECTION) || qName.equals(FOOTNOTE_BODY) || qName.equals(APPENDIX_BODY) || qName.equals(INDEX)
                || qName.equals(CONTENT_METADATA_BLOCK) || qName.equals(PROP_BLOCK) || qName.equals(MESSAGE_BLOCK)) {
            canSavePagebreaks = false;
        }
        if (isChanging) {
            isChanging = false;
        } else {
            super.endElement(uri, localName, qName);
        }
    }

    private void protectPagebreak(final Attributes atts) throws SAXException {
        if (protectPagebreaks && canSavePagebreaks) {
            final String message = wrapPagebreak(atts.getValue(LABEL_NO));
            super.characters(message.toCharArray(), 0, message.length());
        }
    }

    private String wrapPagebreak(final String pageNumber) {
        return PAGEBREAK_WRAPPER_OPEN + LABEL_NO + "=\"" + pageNumber + "\"" + PAGEBREAK_WRAPPER_CLOSE;
    }
}
