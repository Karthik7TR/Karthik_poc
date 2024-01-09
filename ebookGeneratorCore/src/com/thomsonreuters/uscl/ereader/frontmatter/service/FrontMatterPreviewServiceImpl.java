package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.CoverArtUtil;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Setter
public class FrontMatterPreviewServiceImpl implements FrontMatterPreviewService{
    private static final String CSS_PLACEHOLDER = "er:#ebook_generator";
    private static final String WLN_LOGO_PLACEHOLDER = "er:#WestlawLogo";
    private static final String PDF_PREVIEW_LINK_TEMPLATE = CoreConstants.MVC_FRONT_MATTER_PDF_PREVIEW + "?pdfName=%s&publisher=%s";
    private static final String IMAGE_REPLACEMENT_TEMPLATE = CoreConstants.MVC_FRONT_MATTER_IMAGE_PREVIEW + "?imageName=%s";
    private static final String CSS_REPLACEMENT = CoreConstants.MVC_FRONT_MATTER_CSS_PREVIEW + "?cssName=" + CoreConstants.EBOOK_GENERATOR_CSS;
    private static final String PROVIEW_ANCHOR_TEMPLATE = "er:#%s";

    @Value("#{${frontMatter.logoPlaceHolder}}")
    private Map<String, String> frontMatterLogoPlaceHolder;

    private final BaseFrontMatterService baseFrontMatterService;

    @Autowired
    public FrontMatterPreviewServiceImpl(final BaseFrontMatterService baseFrontMatterService) {
        this.baseFrontMatterService = baseFrontMatterService;
    }

    @Override
    public String getTitlePagePreview(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException {
        return of(baseFrontMatterService.generateTitlePage(bookDefinition, false))
                .map(this::replaceCss)
                .map(this::replaceFrontMatterImages)
                .map(out -> replaceCoverImage(bookDefinition, out))
                .orElse(EMPTY);
    }

    @Override
    public String getCopyrightPagePreview(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException {
        return of(baseFrontMatterService.generateCopyrightPage(bookDefinition, false))
                .map(this::replaceCss)
                .map(this::replaceFrontMatterImages)
                .orElse(EMPTY);
    }

    @Override
    public String getAdditionalFrontPagePreview(final BookDefinition bookDefinition, final Long frontMatterPageId) throws EBookFrontMatterGenerationException {
        return of(baseFrontMatterService.generateAdditionalFrontMatterPage(bookDefinition, frontMatterPageId, Collections.emptyMap()))
                .map(this::replaceCss)
                .map(out -> replacePdfs(bookDefinition, out))
                .orElse(EMPTY);
    }

    @Override
    public String getResearchAssistancePagePreview(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException {
        return replaceCss(baseFrontMatterService.generateResearchAssistancePage(bookDefinition, false));
    }

    @Override
    public String getWestlawNextPagePreview(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException {
        return of(baseFrontMatterService.generateWestlawNextPage(false))
                .map(this::replaceCss)
                .map(this::replaceWestlawLogo)
                .orElse(EMPTY);
    }

    private String replaceFrontMatterImages(String output) {
        for (final Map.Entry<String, String> entry : frontMatterLogoPlaceHolder.entrySet()) {
            output = output.replace(entry.getKey(), String.format(IMAGE_REPLACEMENT_TEMPLATE, entry.getValue()));
        }
        return output;
    }

    private String replacePdfs(final BookDefinition bookDefinition, String output) {
        for (String pdfFile : bookDefinition.getFrontMatterPdfFileNames()) {
            output = output.replace(String.format(PROVIEW_ANCHOR_TEMPLATE, FilenameUtils.removeExtension(pdfFile)),
                    String.format(PDF_PREVIEW_LINK_TEMPLATE, pdfFile, bookDefinition.getPublisherCodes().getName()));
        }
        return output;
    }

    private String replaceCss(final String output) {
        return output.replace(CSS_PLACEHOLDER, CSS_REPLACEMENT);
    }

    private String replaceCoverImage(final BookDefinition bookDefinition, final String output) {
        return output.replace(CoverArtUtil.getCoverArtOnProView(bookDefinition), CoverArtUtil.getCoverArtOnEbookManager(bookDefinition));
    }

    private String replaceWestlawLogo(final String output) {
        return output.replace(WLN_LOGO_PLACEHOLDER, String.format(IMAGE_REPLACEMENT_TEMPLATE, frontMatterLogoPlaceHolder.get(WLN_LOGO_PLACEHOLDER)));
    }
}
