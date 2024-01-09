package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jetbrains.annotations.NotNull;

@Slf4j
public final class DirectoryContentMatcher extends BaseMatcher<Object> {
    @NotNull
    private File actual;
    private boolean recursive;

    /**
     * @param actual
     */
    private DirectoryContentMatcher(final File actual, final boolean recursive) {
        this.actual = actual;
        this.recursive = recursive;
    }

    public static DirectoryContentMatcher hasSameContentAs(@NotNull final File actual, final boolean recursive) {
        return new DirectoryContentMatcher(actual, recursive);
    }

    @Override
    public boolean matches(final Object item) {
        final List<File> items = recursive
            ? (List<File>) FileUtils.listFiles((File) item, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
            : (List<File>) FileUtils.listFiles((File) item, TrueFileFilter.INSTANCE, null);
        final List<File> actualItems = recursive
            ? (List<File>) FileUtils.listFiles(actual, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
            : (List<File>) FileUtils.listFiles(actual, TrueFileFilter.INSTANCE, null);
        if (!(items.size() == actualItems.size()))
            return false;
        for (final File file : items) {
            final File actualFile = actualItems.get(items.indexOf(file));
            try {
                if (!FileUtils.contentEquals(file, actualFile)) {
                    return false;
                }
            } catch (final IOException e) {
                log.error("Exception while comparing contents", e);
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeMismatch(final Object item, final Description description) {
        final File file = (File) item;
        description.appendText("contents of \n")
            .appendValue(Paths.get(file.toURI()).toString())
            .appendText("\n do not match to contents of \n")
            .appendValue(Paths.get(actual.toURI()).toString());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("content should be equal");
    }

}
