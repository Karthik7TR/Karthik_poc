package com.thomsonreuters.uscl.ereader.core.book.util;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Wrapper for org.apache.commons.io.FileUtils for avoiding checked exceptions
 */
public class FileUtils {

    public static void copyDirectory(File srcDir, File destDir) {
        try {
            org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            throw new EBookException(e);
        }
    }

    public static void copyDirectory(File srcDir, File destDir, FileFilter filter) {
        try {
            org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir, filter);
        } catch (IOException e) {
            throw new EBookException(e);
        }
    }

    public static void copyFileToDirectory(File srcFile, File destDir) {
        try {
            org.apache.commons.io.FileUtils.copyFileToDirectory(srcFile, destDir);
        } catch (IOException e) {
            throw new EBookException(e);
        }
    }

    public static void copyFilesToDirectory(final List<File> fileList, final File destDir) {
        fileList.forEach(file -> copyFileToDirectory(file, destDir));
    }

    public static Collection<File> listFiles(final File directory) {
        try {
            return org.apache.commons.io.FileUtils.listFiles(directory, null, false);
        } catch (Exception e) {
            throw new EBookException(e);
        }
    }
}
