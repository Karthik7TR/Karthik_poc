package com.thomsonreuters.uscl.ereader.common.xslt;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;

import org.jetbrains.annotations.NotNull;

/**
 * Builder to create transformers
 */
public interface TransformerBuilder
{
    /**
     * Builder to create transformers with given parameters
     *
     * @param xsl file used for transformation
     */
    TransformerBuilder create(@NotNull File xsl);

    /**
     * Add parameter
     */
    TransformerBuilder withParameter(@NotNull String name, @NotNull Object value);

    /**
     * Add URL resolver
     */
    TransformerBuilderImpl withUriResolver(@NotNull URIResolver resolver);

    /**
     * Returns transformer instance
     */
    Transformer build();
}
