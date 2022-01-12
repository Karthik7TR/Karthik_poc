package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

@Slf4j
public class NovusImageMetadataParserImpl implements NovusImageMetadataParser {

    @Override
    @NotNull
    public ImgMetadataInfo parse(@NotNull final String metadata) {
        final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        try {
            final XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            final ImageMetadataHandler imageMetadataHandler = new ImageMetadataHandler();
            reader.setContentHandler(imageMetadataHandler);
            reader.parse(new InputSource(new StringReader(metadata)));
            return imageMetadataHandler.getImgMetadataInfo();
        } catch (final Exception e) {
            final String msg = "Cannot parse image metadata from Novus";
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
