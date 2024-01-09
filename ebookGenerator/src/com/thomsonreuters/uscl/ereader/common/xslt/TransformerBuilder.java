package com.thomsonreuters.uscl.ereader.common.xslt;

import java.io.File;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import com.thomsonreuters.uscl.ereader.format.parsinghandler.SimpleSAXErrorListener;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * Builder to create transformers
 */
@Slf4j
public class TransformerBuilder {

    @NotNull
    private TransformerFactory factory;
    private Transformer transformer;

    TransformerBuilder(@NotNull final TransformerFactory factory) {
        this.factory = factory;
    }

    /**
     * Builder to create transformers with given parameters
     *
     * @param xsl file used for transformation
     */
    public TransformerBuilder withXsl(@NotNull final File xsl) {
        final Source xsltSource = new StreamSource(xsl);
        try {
            transformer = factory.newTransformer(xsltSource);
            transformer.setErrorListener(new SimpleSAXErrorListener());
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        } catch (final Exception e) {
            final String message = String.format("Cannot create transformer for xslt file %s", xsl.getAbsolutePath());
            log.error(message, e);
            throw new XslTransformationException(message, e);
        }
        return this;
    }

    /**
     * Add parameter
     */
    public TransformerBuilder withParameter(@NotNull final String name, @NotNull final Object value) {
        transformer.setParameter(name, value);
        return this;
    }

    /**
     * Add URL resolver
     */
    public TransformerBuilder withUriResolver(@NotNull final URIResolver resolver) {
        transformer.setURIResolver(resolver);
        return this;
    }

    /**
     * Returns transformer instance
     */
    public Transformer build() {
        return transformer;
    }
}
