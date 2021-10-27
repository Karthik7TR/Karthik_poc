package com.thomsonreuters.uscl.ereader.assemble.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;

import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.util.FileUtils;
import com.thomsonreuters.uscl.ereader.core.service.CoverArtUtil;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterTitlePageFilter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MoveResourcesUtil {
    /**
     * The file path to the CSS file to apply on the documents.
     */
    public static final String DOCUMENT_CSS_FILE = "document.css";

    @Autowired
    private CoverArtUtil coverArtUtil;

    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Autowired
    private NasFileSystem nasFileSystem;

    public void setCoverArtUtil(final CoverArtUtil coverArtUtil) {
        this.coverArtUtil = coverArtUtil;
    }

    public void moveCoverArt(final ExecutionContext jobExecutionContext, final File artworkDirectory) {
        final File coverArt = createCoverArt(jobExecutionContext);
        FileUtils.copyFileToDirectory(coverArt, artworkDirectory);
    }

    public File createCoverArt(final ExecutionContext jobExecutionContext) {
        return coverArtUtil.getCoverArt((BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION));
    }

    public void copySourceToDestination(final File sourceDir, final File destinationDirectory) {
        FileUtils.copyDirectory(sourceDir, destinationDirectory, File::isFile);
    }

    public void copyFilesToDestination(final List<File> fileList, final File destinationDirectory) {
        FileUtils.copyFilesToDirectory(fileList, destinationDirectory);
    }

    public void moveFrontMatterImages(
        final BookStepImpl step,
        final File assetsDirectory,
        final boolean move) {
        final BookDefinition bookDefinition = step.getBookDefinition();
        final File frontMatterImagesDir = nasFileSystem.getFrontMatterImagesDirectory();

        final List<File> filter = filterFiles(frontMatterImagesDir, bookDefinition);

        for (final File file : frontMatterImagesDir.listFiles()) {
            if (!filter.contains(file)) {
                FileUtils.copyFileToDirectory(file, assetsDirectory);
            }
        }

        if (move) {
            final List<FrontMatterPdf> pdfList = new ArrayList<>();
            final List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
            for (final FrontMatterPage fmp : fmps) {
                for (final FrontMatterSection fms : fmp.getFrontMatterSections()) {
                    pdfList.addAll(fms.getPdfs());
                }
            }

            File pdfLocation = bookDefinition.isCwBook()
                    ? nasFileSystem.getFrontMatterCwPdfDirectory()
                    : nasFileSystem.getFrontMatterUsclPdfDirectory();
            for (final FrontMatterPdf pdf : pdfList) {
                File pdfFile = new File(pdfLocation, pdf.getPdfFilename());
                if (!pdfFile.exists() && bookDefinition.isCwBook()) {
                    pdfFile = new File(nasFileSystem.getFrontMatterUsclPdfDirectory(), pdf.getPdfFilename());
                }
                FileUtils.copyFileToDirectory(pdfFile, assetsDirectory);
            }

            if (bookDefinition.isCwBook()) {
                Optional.of(formatFileSystem.getFrontMatterPdfImagesDir(step))
                        .filter(File::exists)
                        .ifPresent(from -> FileUtils.copyDirectory(from, assetsDirectory));
            }
        }
    }

    /**
     * Add only image files that are required.
     *
     * @param frontMatterImagesDir
     * @param bookDefinition
     * @return
     * @throws FileNotFoundException
     */
    public List<File> filterFiles(final File frontMatterImagesDir, final BookDefinition bookDefinition) {
        final List<File> filter = new ArrayList<>();
        if (!frontMatterImagesDir.exists()) {
            throw new EBookException(new FileNotFoundException("Directory not found:  " + frontMatterImagesDir.getPath()));
        }
        for (final File file : frontMatterImagesDir.listFiles()) {
            if (!bookDefinition.getFrontMatterTheme().equalsIgnoreCase(FrontMatterTitlePageFilter.AAJ_PRESS_THEME)
                && file.getName().startsWith("AAJ")) {
                filter.add(file);
            }
            if (!bookDefinition.getKeyciteToplineFlag() && file.getName().startsWith("keycite")) {
                filter.add(file);
            }
        }

        return filter;
    }

    protected void moveStylesheet(final File assetsDirectory) {
        File stylesheet = new File(nasFileSystem.getStaticContentDirectory(), DOCUMENT_CSS_FILE);
        FileUtils.copyFileToDirectory(stylesheet, assetsDirectory);
        stylesheet = nasFileSystem.getFrontMatterCssFile();
        FileUtils.copyFileToDirectory(stylesheet, assetsDirectory);
    }

    protected void moveThesaurus(final BookStepImpl step, final File assetsDirectory) {
        if (step.getJobExecutionPropertyBoolean(JobExecutionKey.WITH_THESAURUS)) {
            FileUtils.copyDirectory(formatFileSystem.getThesaurusStaticFilesDirectory(), assetsDirectory);
            FileUtils.copyFileToDirectory(formatFileSystem.getThesaurusXml(step), assetsDirectory);
        }
    }

    public void moveTitlePageImage(final BookDefinition bookDefinition, final File titlePageImage) {
        FileUtils.copyFile(coverArtUtil.getCoverArt(bookDefinition), titlePageImage);
    }
}
