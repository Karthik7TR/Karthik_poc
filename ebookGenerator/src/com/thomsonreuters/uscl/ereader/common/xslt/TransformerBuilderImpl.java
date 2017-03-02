package com.thomsonreuters.uscl.ereader.common.xslt;

import java.io.File;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import com.thomsonreuters.uscl.ereader.format.parsinghandler.SimpleSAXErrorListener;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component("transformerBuilder")
@Scope(value = "step", proxyMode = ScopedProxyMode.INTERFACES)
public class TransformerBuilderImpl implements TransformerBuilder
{
    private static final Logger LOG = LogManager.getLogger(TransformerBuilderImpl.class);

    private Transformer transformer;

    @Override
    public TransformerBuilderImpl create(@NotNull final File xsl)
    {
        final Source xsltSource = new StreamSource(xsl);
        final TransformerFactory factory = TransformerFactory.newInstance();
        try
        {
            transformer = factory.newTransformer(xsltSource);
            transformer.setErrorListener(new SimpleSAXErrorListener());
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        }
        catch (final Exception e)
        {
            final String message = String.format("Cannot create transformer for xslt file %s", xsl.getAbsolutePath());
            LOG.error(message, e);
            throw new XslTransformationException(message, e);
        }
        return this;
    }

    @Override
    public TransformerBuilderImpl withParameter(@NotNull final String name, @NotNull final Object value)
    {
        transformer.setParameter(name, value);
        return this;
    }

    @Override
    public TransformerBuilderImpl withUriResolver(@NotNull final URIResolver resolver)
    {
        transformer.setURIResolver(resolver);
        return this;
    }

    @Override
    public Transformer build()
    {
        return transformer;
    }
}
