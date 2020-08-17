package com.thomsonreuters.uscl.ereader.common.filesystem;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Getter
@Component(value = "nasFileSystem")
public class NasFileSystemImpl implements NasFileSystem {
    private static final String EBOOK_GENERATOR_CSS = "ebook_generator.css";
    @Value("${static.content.dir}")
    private File staticContentDirectory;
    @Value("${cover.images.dir}")
    private File coverImagesDirectory;
    @Value("${front.matter.images.dir}")
    private File frontMatterImagesDirectory;
    @Value("${front.matter.css.dir}")
    private File frontMatterCssDirectory;
    @Value("${front.matter.uscl.pdf.dir}")
    private File frontMatterUsclPdfDirectory;
    @Value("${front.matter.cw.pdf.dir}")
    private File frontMatterCwPdfDirectory;
    @Value("${pilot.book.csv.dir}")
    private File pilotBookCsvDirectory;
    @Value("${codes.workbench.root.dir}")
    private File codesWorkbenchRootDir;
    @Value("${isbn.file.dir}")
    private File isbnFileDir;
    @Value("${isbn.file.archive.dir}")
    private File isbnFileArchiveDir;

    @NotNull
    @Override
    public File getFrontMatterCssFile() {
        return new File(getFrontMatterCssDirectory(), EBOOK_GENERATOR_CSS);
    }
}
