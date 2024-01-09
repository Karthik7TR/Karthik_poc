package com.thomsonreuters.uscl.ereader.core.book.util;

import java.io.File;
import java.io.IOException;

/**
 * Contains common methods for unit and integration tests.
 */
public final class BookTestUtil {
    private BookTestUtil() {
    }

    public static File mkdir(final File base, final String... subDirs) {
        final StringBuilder sb = new StringBuilder();
        for (final String subDir : subDirs) {
            sb.append(subDir).append("/");
        }

        final File newDir = new File(base, sb.toString());

        newDir.mkdirs();
        return newDir;
    }

    public static File mkfile(final File base, final String fileName) throws IOException {
        final File newFile = new File(base, fileName);
        newFile.createNewFile();
        return newFile;
    }
}
