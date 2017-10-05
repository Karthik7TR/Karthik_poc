package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * This class is responsible for serializing the gzipped tarball
 * into the body of the HTTP request being sent to ProView.
 *
 * <p><i>Used only during PUT (publish) operations.</i></p>
 */
public class ProviewMessageConverter<T> extends AbstractHttpMessageConverter<File> {
    private static final Logger LOG = LogManager.getLogger(ProviewMessageConverter.class);

    @Override
    protected File readInternal(final Class<? extends File> arg0, final HttpInputMessage arg1)
        throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected boolean supports(final Class<?> clazz) {
        return Boolean.TRUE;
    }

    @Override
    protected void writeInternal(final File fileToSend, final HttpOutputMessage httpOutputMessage)
        throws IOException, HttpMessageNotWritableException {
        try {
            IOUtils.copy(new FileInputStream(fileToSend), httpOutputMessage.getBody());
        } catch (final IOException e) {
            throw new HttpMessageNotWritableException("Could not write HTTP message.", e);
        }
    }
}
