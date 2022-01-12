package com.thomsonreuters.uscl.ereader.request.step.service;

import java.io.File;
import java.io.IOException;

import com.thomsonreuters.uscl.ereader.request.XPPConstants;
import com.thomsonreuters.uscl.ereader.request.XppMessageException;
import com.thomsonreuters.uscl.ereader.request.dao.XppBundleArchiveDao;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppMessageValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class RetrieveBundleServiceImpl implements RetrieveBundleService {

    private final XppBundleArchiveDao xppBundleArchiveDao;
    private final XppMessageValidator xppMessageValidator;

    public RetrieveBundleServiceImpl(final XppBundleArchiveDao xppBundleArchiveDao,
                                     final XppMessageValidator xppMessageValidator) {
        this.xppBundleArchiveDao = xppBundleArchiveDao;
        this.xppMessageValidator = xppMessageValidator;
    }

    @Override
    @Transactional
    public void retrieveBundle(@NotNull final XppBundleArchive request, @NotNull final File bundleDestDir) throws XppMessageException {
        invalidateDuplicateRequest(request);

        final File bundleFile = request.getEBookSrcFile();
        request.setEBookSrcFile(new File(bundleDestDir, bundleFile.getName()));
        archiveRequest(request);

        if (!bundleDestDir.exists() && !bundleDestDir.mkdirs()) {
            throw new RuntimeException("Cannot create directory: " + bundleDestDir.getAbsolutePath());
        }

        try {
            FileUtils.moveFileToDirectory(bundleFile, bundleDestDir, false);
        } catch (final IOException e) {
            if (e.getMessage().contains("Failed to delete original file")) {
                // bundle copied, but not deleted (likely permissions issue)
                // TODO handle this type of error
                log.error(e.getMessage(), e);
            } else {
                throw new RuntimeException(e);
            }
        }

        try {
            xppMessageValidator.validate(request);
        } catch (final XppMessageException e) {
            log.error("Bundle invalidated during move to archive location " + request.getEBookSrcPath(), e);
            throw e;
        }
        log.debug("Bundle moved successfully: integrity verified");
    }

    private void invalidateDuplicateRequest(@NotNull final XppBundleArchive request) throws XppMessageException {
        final XppBundleArchive dup = xppBundleArchiveDao.findFirstByMessageId(request.getMessageId());
        if (dup != null) {
            final String message = dup.isSimilar(request) ? XPPConstants.ERROR_DUPLICATE_REQUEST + request
                : "non-identical duplicate request received";
            throw new XppMessageException(message);
        }
    }

    private void archiveRequest(@NotNull final XppBundleArchive request) {
        final long pk = xppBundleArchiveDao.save(request).getXppBundleArchiveId();
        request.setXppBundleArchiveId(pk);
    }
}
