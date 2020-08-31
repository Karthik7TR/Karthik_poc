package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

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
        String output = baseFrontMatterService.generateTitlePage(bookDefinition, false)
                .replace(CSS_PLACEHOLDER, CSS_REPLACEMENT);
        output = replaceImages(output);
        return output;
    }

    @Override
    public String getCopyrightPagePreview(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException {
        String output = baseFrontMatterService.generateCopyrightPage(bookDefinition, false)
                .replace(CSS_PLACEHOLDER, CSS_REPLACEMENT);
        output = replaceImages(output);
        return output;
    }

    @Override
    public String getAdditionalFrontPagePreview(final BookDefinition bookDefinition, final Long frontMatterPageId) throws EBookFrontMatterGenerationException {
        String output = baseFrontMatterService.generateAdditionalFrontMatterPage(bookDefinition, frontMatterPageId, Collections.emptyMap())
                .replace(CSS_PLACEHOLDER, CSS_REPLACEMENT);
        output = replacePdfs(bookDefinition, output);
        return output;
    }

    @Override
    public String getResearchAssistancePagePreview(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException {
        return baseFrontMatterService.generateResearchAssistancePage(bookDefinition, false)
                .replace(CSS_PLACEHOLDER, CSS_REPLACEMENT);
    }

    @Override
    public String getWestlawNextPagePreview(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException {
        return baseFrontMatterService.generateWestlawNextPage(false)
                .replace(CSS_PLACEHOLDER, CSS_REPLACEMENT)
                .replace(WLN_LOGO_PLACEHOLDER, String.format(IMAGE_REPLACEMENT_TEMPLATE, frontMatterLogoPlaceHolder.get(WLN_LOGO_PLACEHOLDER))
                );
    }

    private String replaceImages(String output) {
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
}
