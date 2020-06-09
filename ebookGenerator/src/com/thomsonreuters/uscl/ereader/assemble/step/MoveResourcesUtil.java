package com.thomsonreuters.uscl.ereader.assemble.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.service.PdfToImgConverter;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterTitlePageFilter;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MoveResourcesUtil {
    /**
     * The file path of the user generated files for front matter pdfs.
     */
    private static final String EBOOK_FRONT_MATTER_PDF_IMAGES_FILEPATH = "/apps/eBookBuilder/generator/images/pdf/";
    /**
     * The directory of the static files for front matter logos and keycite
     * logo.
     */
    public static final String EBOOK_GENERATOR_IMAGES_DIR = "/apps/eBookBuilder/coreStatic/images";
    /**
     * The file path to the CSS file to apply on the documents.
     */
    public static final String DOCUMENT_CSS_FILE = "document.css";
    /**
     * The file path to the ebookGenerator CSS file used by front matter.
     */
    public static final String EBOOK_GENERATOR_CSS_FILE = "/apps/eBookBuilder/coreStatic/css/ebook_generator.css";

    public static final String THESAURUS_XML = "thesaurus.xml";

    @Value("${static.content.dir}")
    private File staticContentDirectory;

    @Autowired
    private CoverArtUtil coverArtUtil;

    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Value("${thesaurus.static.files.dir}")
    private File thesaurusStaticFilesDir;

    @Autowired
    private PdfToImgConverter pdfToImgConverter;

    public void setCoverArtUtil(final CoverArtUtil coverArtUtil) {
        this.coverArtUtil = coverArtUtil;
    }

    public void moveCoverArt(final ExecutionContext jobExecutionContext, final File artworkDirectory)
        throws IOException {
        final File coverArt = createCoverArt(jobExecutionContext);
        FileUtils.copyFileToDirectory(coverArt, artworkDirectory);
    }

    public File createCoverArt(final ExecutionContext jobExecutionContext) {
        final File coverArt =
            coverArtUtil.getCoverArt((BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION));
        jobExecutionContext.putString(JobExecutionKey.COVER_ART_PATH, coverArt.getAbsolutePath());
        return coverArt;
    }

    public void copySourceToDestination(final File sourceDir, final File destinationDirectory) throws IOException {
        FileUtils.copyDirectory(sourceDir, destinationDirectory);
    }

    public void copyFilesToDestination(final List<File> fileList, final File destinationDirectory) throws IOException {
        for (final File file : fileList) {
            FileUtils.copyFileToDirectory(file, destinationDirectory);
        }
    }

    public void moveFrontMatterImages(
        final ExecutionContext jobExecutionContext,
        final File assetsDirectory,
        final boolean move) throws IOException {
        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);
        final File frontMatterImagesDir = new File(EBOOK_GENERATOR_IMAGES_DIR);

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

            for (final FrontMatterPdf pdf : pdfList) {
                final File pdfFile = new File(EBOOK_FRONT_MATTER_PDF_IMAGES_FILEPATH + pdf.getPdfFilename());
                FileUtils.copyFileToDirectory(pdfFile, assetsDirectory);

                if (bookDefinition.isCwBook()) {
                    pdfToImgConverter.convert(pdfFile, assetsDirectory);
                }
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
    public List<File> filterFiles(final File frontMatterImagesDir, final BookDefinition bookDefinition)
        throws FileNotFoundException {
        final List<File> filter = new ArrayList<>();
        if (!frontMatterImagesDir.exists()) {
            throw new FileNotFoundException("Directory not found:  " + frontMatterImagesDir.getPath());
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

    protected void moveStylesheet(final File assetsDirectory) throws IOException {
        File stylesheet = new File(staticContentDirectory, DOCUMENT_CSS_FILE);
        FileUtils.copyFileToDirectory(stylesheet, assetsDirectory);
        stylesheet = new File(EBOOK_GENERATOR_CSS_FILE);
        FileUtils.copyFileToDirectory(stylesheet, assetsDirectory);
    }

    protected void moveThesaurus(final BookStepImpl step, final File assetsDirectory) throws IOException {
        if (step.getJobExecutionPropertyBoolean(JobExecutionKey.WITH_THESAURUS)) {
            File thesaurusFile = new File(formatFileSystem.getFormatDirectory(step), THESAURUS_XML);
            FileUtils.copyDirectory(thesaurusStaticFilesDir, assetsDirectory);
            FileUtils.copyFileToDirectory(thesaurusFile, assetsDirectory);
        }
    }
}
