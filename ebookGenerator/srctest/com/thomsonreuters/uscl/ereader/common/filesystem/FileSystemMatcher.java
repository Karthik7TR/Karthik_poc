package com.thomsonreuters.uscl.ereader.common.filesystem;

import static org.hamcrest.Matchers.containsString;

import java.io.File;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jetbrains.annotations.NotNull;

public class FileSystemMatcher extends BaseMatcher<Object> {
    private @NotNull String path;

    public FileSystemMatcher(@NotNull final String path) {
        this.path = path;
    }

    //TODO: make it apply multiple path parts
    public static FileSystemMatcher hasPath(@NotNull final String path) {
        return new FileSystemMatcher(path);
    }

    @Override
    public boolean matches(final Object item) {
        final File file = (File) item;
        final String absolutePath = file.getAbsolutePath().replaceAll("\\\\", "/");
        return containsString(path).matches(absolutePath);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("should have path ").appendText(path);
    }

    @Override
    public void describeMismatch(final Object item, final Description description) {
        final File file = (File) item;
        description.appendText("file path was ").appendValue(file.getAbsolutePath());
    }
}
