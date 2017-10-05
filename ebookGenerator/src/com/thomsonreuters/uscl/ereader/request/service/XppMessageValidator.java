package com.thomsonreuters.uscl.ereader.request.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.thomsonreuters.uscl.ereader.request.XPPConstants;
import com.thomsonreuters.uscl.ereader.request.XppMessageException;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import org.apache.commons.codec.digest.DigestUtils;

public class XppMessageValidator {
    public void validate(final XppBundleArchive request) throws XppMessageException {
        if (request == null
            || request.getEBookSrcFile() == null
            || request.getBundleHash() == null
            || request.getDateTime() == null
            || request.getMessageId() == null
            || request.getMaterialNumber() == null
            || request.getVersion() == null) {
            throw new XppMessageException(XPPConstants.ERROR_INCOMPLETE_REQUEST);
        }

        final File ebook = request.getEBookSrcFile();
        if (!ebook.exists()) {
            throw new XppMessageException(XPPConstants.ERROR_BUNDLE_NOT_FOUND + ebook.getAbsolutePath());
        }

        final String hash;
        try (InputStream bookStream = new FileInputStream(ebook)) {
            hash = DigestUtils.md5Hex(bookStream);
        } catch (final IOException e) {
            throw new XppMessageException(XPPConstants.ERROR_CREATE_HASH, e);
        }

        if (!request.getBundleHash().equals(hash)) {
            throw new XppMessageException(XPPConstants.ERROR_BAD_HASH + ebook.getAbsolutePath());
        }
    }
}
