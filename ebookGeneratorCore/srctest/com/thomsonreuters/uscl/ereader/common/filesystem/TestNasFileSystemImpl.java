package com.thomsonreuters.uscl.ereader.common.filesystem;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.thomsonreuters.uscl.ereader.core.book.util.FileUtils.getDir;

@Slf4j
public class TestNasFileSystemImpl implements NasFileSystem {
    private static final String LOCATION_STATIC_CONTENT = "staticContent";
    private static final String LOCATION_COVER_IMAGE = "/generator/images/cover";
    private static final String LOCATION_FRONT_MATTER_IMAGE = "/coreStatic/images";
    private static final String LOCATION_FRONT_MATTER_CSS = "/coreStatic/css";
    private static final String EBOOK_GENERATOR_CSS = "ebook_generator.css";
    private static final String LOCATION_FRONT_MATTER_PDF = "/generator/images/pdf";
    private static final String LOCATION_FRONT_MATTER_CW_PDF = "/generator/images/cw-pdf";
    private static final String LOCATION_PILOT_BOOK_CSV = "/generator/altId";
    private static final String LOCATION_CODES_WORKBENCH = "/codesWorkbench/Test";
    private File tempDir;

    public File getRootDirectory() {
        try {
            if (tempDir == null) {
                tempDir = Files.createTempDirectory("nasDirectory").toFile();
            }
            return tempDir;
        } catch (final IOException e) {
            throw new EBookException(e);
        }
    }

    public void reset() {
        tempDir = null;
    }

    @NotNull
    @Override
    public File getStaticContentDirectory() {
        return getDir(getRootDirectory(), LOCATION_STATIC_CONTENT);
    }

    @NotNull
    @Override
    public File getCoverImagesDirectory() {
        return getDir(getRootDirectory(), LOCATION_COVER_IMAGE);
    }

    @NotNull
    @Override
    public File getFrontMatterImagesDirectory() {
        return getDir(getRootDirectory(), LOCATION_FRONT_MATTER_IMAGE);
    }

    @NotNull
    @Override
    public File getFrontMatterCssDirectory() {
        return getDir(getRootDirectory(), LOCATION_FRONT_MATTER_CSS);
    }

    @NotNull
    @Override
    public File getFrontMatterCssFile() {
        return new File(getFrontMatterCssDirectory(), EBOOK_GENERATOR_CSS);
    }

    @NotNull
    @Override
    public File getFrontMatterUsclPdfDirectory() {
        return getDir(getRootDirectory(), LOCATION_FRONT_MATTER_PDF);
    }

    @NotNull
    @Override
    public File getFrontMatterCwPdfDirectory() {
        return getDir(getRootDirectory(), LOCATION_FRONT_MATTER_CW_PDF);
    }

    @NotNull
    @Override
    public File getPilotBookCsvDirectory() {
        return getDir(getRootDirectory(), LOCATION_PILOT_BOOK_CSV);
    }

    @NotNull
    @Override
    public File getCodesWorkbenchRootDir() {
        return getDir(getRootDirectory(), LOCATION_CODES_WORKBENCH);
    }
}