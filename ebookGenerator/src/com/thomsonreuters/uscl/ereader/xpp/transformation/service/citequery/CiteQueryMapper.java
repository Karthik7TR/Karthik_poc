package com.thomsonreuters.uscl.ereader.xpp.transformation.service.citequery;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

public interface CiteQueryMapper {
    /**
     * @param htmlFile input HTML file with <cite.query> tags
     * @param materialNumber material number of current volume
     * @param step book step on which mapping should occur
     * @return an absolute path to file, containing mappings in XML format
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    @NotNull
    CiteQueryMapperResponse createMappingFile(
        @NotNull File htmlFile,
        @NotNull String materialNumber,
        @NotNull XppTransformationStep step);
}
