package com.thomsonreuters.uscl.ereader.request.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.thomsonreuters.uscl.ereader.jms.exception.MessageQueueException;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.request.XPPConstants;
import org.apache.commons.codec.digest.DigestUtils;

public class EBookRequestValidator
{
    public void validate(final EBookRequest request) throws MessageQueueException
    {
        if (request == null
            || request.getEBookSrcFile() == null
            || request.getBundleHash() == null
            || request.getDateTime() == null
            || request.getMessageId() == null
            || request.getVersion() == null)
        {
            throw new MessageQueueException(XPPConstants.ERROR_INCOMPLETE_REQUEST);
        }

        final File ebook = request.getEBookSrcFile();
        if (!ebook.exists())
        {
            throw new MessageQueueException(XPPConstants.ERROR_BUNDLE_NOT_FOUND + ebook.getAbsolutePath());
        }

        final String hash;
        try (InputStream bookStream = new FileInputStream(ebook))
        {
            hash = DigestUtils.md5Hex(bookStream);
        }
        catch (final IOException e)
        {
            throw new MessageQueueException(XPPConstants.ERROR_CREATE_HASH, e);
        }

        if (!request.getBundleHash().equals(hash))
        {
            throw new MessageQueueException(XPPConstants.ERROR_BAD_HASH + ebook.getAbsolutePath());
        }
    }
}
