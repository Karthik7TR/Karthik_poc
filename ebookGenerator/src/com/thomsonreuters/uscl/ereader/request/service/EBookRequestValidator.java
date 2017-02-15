package com.thomsonreuters.uscl.ereader.request.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.thomsonreuters.uscl.ereader.jms.exception.MessageQueueException;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class EBookRequestValidator
{
    public static final String ERROR_INCOMPLETE_REQUEST = "null field detected";
    public static final String ERROR_EBOOK_NOT_FOUND = "ebook not found at specified location: ";
    public static final String ERROR_CREATE_HASH = "problem encountered while hashing file";
    public static final String ERROR_BAD_HASH = "verification hash does not match for ";
    public static final String ERROR_DUPLICATE_REQUEST = "Request already received";

    private static final Logger LOG = LogManager.getLogger(EBookRequestValidator.class);

    public void validate(final EBookRequest request) throws MessageQueueException
    {
        if (request == null
            || request.getEBookSrcFile() == null
            || request.getBundleHash() == null
            || request.getDateTime() == null
            || request.getMessageId() == null
            || request.getVersion() == null)
        {
            throw new MessageQueueException(ERROR_INCOMPLETE_REQUEST);
        }

        final File ebook = request.getEBookSrcFile();
        if (!ebook.exists())
        {
            throw new MessageQueueException(ERROR_EBOOK_NOT_FOUND + ebook.getAbsolutePath());
        }

        final String hash;
        InputStream bookStream = null;
        try
        {
            bookStream = new FileInputStream(ebook);
            hash = DigestUtils.md5Hex(bookStream);
        }
        catch (final IOException e)
        {
            throw new MessageQueueException(ERROR_CREATE_HASH, e);
        }
        finally
        {
            try
            {
                if (bookStream != null)
                {
                    bookStream.close();
                }
            }
            catch (final IOException e)
            {
                LOG.error("Unable to close eBook bundle file.", e);
            }
        }

        if (!request.getBundleHash().equals(hash))
        {
            throw new MessageQueueException(ERROR_BAD_HASH + ebook.getAbsolutePath());
        }
    }
}
