package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jetbrains.annotations.NotNull;

public final class FileHierarchyMatcher extends BaseMatcher<Object> {
    @NotNull
    private File actual;

    private FileHierarchyMatcher(@NotNull final File actual) {
        this.actual = actual;
    }

    public static FileHierarchyMatcher hasSameFileHierarchy(@NotNull final File actual) {
        return new FileHierarchyMatcher(actual);
    }

    @Override
    public boolean matches(final Object item) {
        final File file = (File) item;

        final List<File> actualFileList =
            (List<File>) FileUtils.listFiles(actual, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        final List<File> expectedFileList =
            (List<File>) FileUtils.listFiles(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        final List<String> actualPathList = getStringPathList(actualFileList, actual.getAbsolutePath());
        final List<String> expectedPathList = getStringPathList(expectedFileList, file.getAbsolutePath());

        return CollectionUtils.isEqualCollection(actualPathList, expectedPathList);
    }

    private List<String> getStringPathList(final List<File> fileList, final String rootPath) {
        final List<String> stringList = new ArrayList<>();
        for (final File fileElement : fileList) {
            stringList.add(fileElement.getAbsolutePath().replace(rootPath, ""));
        }
        return stringList;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("file hierarchy should be equal");
    }
}
