package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class FileContentMatcher extends BaseMatcher<Object> {

    @NotNull
    private File expected;

    public FileContentMatcher(@NotNull final File expected) {
        this.expected = expected;
    }

    public static FileContentMatcher hasSameContentAs(@NotNull final File expected) {
        return new FileContentMatcher(expected);
    }

    @Override
    public boolean matches(final Object item) {
        final File file = (File) item;
        try {
            return FileUtils.contentEqualsIgnoreEOL(expected, file, null);
        } catch (final IOException e) {
            log.error("", e);
            return false;
        }
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("content should be equal");
    }

    @Override
    public void describeMismatch(final Object item, final Description description) {
        final File file = (File) item;
        try {
            description.appendText("file content was \n")
                .appendValue(FileUtils.readFileToString(file))
                .appendText("\n but expected \n")
                .appendValue(FileUtils.readFileToString(expected));
        } catch (final IOException e) {
            log.error("", e);
        }
    }
}
