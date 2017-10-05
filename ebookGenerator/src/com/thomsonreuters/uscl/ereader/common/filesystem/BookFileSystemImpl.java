package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("bookFileSystem")
public class BookFileSystemImpl implements BookFileSystem {
    @Value("${root.work.directory}")
    private File rootWorkDirectory;
    @Resource(name = "environmentName")
    private String environmentName;

    @Override
    public File getWorkDirectory(@NotNull final BookStep step) {
        final String date = new SimpleDateFormat(CoreConstants.DIR_DATE_FORMAT).format(step.getSubmitTimestamp());
        final String titleId = step.getBookDefinition().getTitleId();
        final long jobInstanceId = step.getJobInstanceId();

        final String dynamicPath =
            String.format("%s/%s/%s/%s/%d", environmentName, CoreConstants.DATA_DIR, date, titleId, jobInstanceId);
        return new File(rootWorkDirectory, dynamicPath);
    }
}
