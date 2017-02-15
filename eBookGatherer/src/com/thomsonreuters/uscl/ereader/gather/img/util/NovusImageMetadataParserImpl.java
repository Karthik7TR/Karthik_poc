package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class NovusImageMetadataParserImpl implements NovusImageMetadataParser
{
    private static final Logger LOG = LogManager.getLogger(NovusImageMetadataParserImpl.class);

    @Override
    @NotNull
    public ImgMetadataInfo parse(@NotNull final String metadata)
    {
        final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        try
        {
            final XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            final ImageMetadataHandler imageMetadataHandler = new ImageMetadataHandler();
            reader.setContentHandler(imageMetadataHandler);
            reader.parse(new InputSource(new StringReader(metadata)));
            return imageMetadataHandler.getImgMetadataInfo();
        }
        catch (final Exception e)
        {
            final String msg = "Cannot parse image metadata from Novus";
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
