package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.core.service.PdfToImgConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_FRONT_MATTER_PDF_IMAGES_DIR;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PdfImagesServiceImpl implements PdfImagesService {
    private final NasFileSystem nasFileSystem;
    private final PdfToImgConverter pdfToImgConverter;

    @Override
    public Map<String, List<String>> generatePdfImages(final Set<String> frontMatterPdfFileNames, final File frontMatterTargetDir) {
        File pdfDestDir = new File(frontMatterTargetDir, FORMAT_FRONT_MATTER_PDF_IMAGES_DIR.getName());
        return frontMatterPdfFileNames.stream()
                .collect(Collectors.toMap(Function.identity(), pdfFileName -> {
                    File pdfFile = new File(nasFileSystem.getFrontMatterCwPdfDirectory(), pdfFileName);
                    if (!pdfFile.exists()) {
                        pdfFile = new File(nasFileSystem.getFrontMatterUsclPdfDirectory(), pdfFileName);
                    }
                    return pdfToImgConverter.convert(pdfFile, pdfDestDir);
                }));
    }
}
