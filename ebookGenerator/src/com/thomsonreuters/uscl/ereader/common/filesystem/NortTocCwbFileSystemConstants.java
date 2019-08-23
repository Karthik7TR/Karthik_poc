package com.thomsonreuters.uscl.ereader.common.filesystem;

/**
 * Keeps list of directories and files which is used in Nort, Toc and CWB steps.
 */
public enum NortTocCwbFileSystemConstants {
    GATHER_DIR("Gather"),
    FORMAT_DIR("Format"),
    ASSEMBLE_DIR("Assemble"),

    GATHER_TOC_DIR("Toc"),
    GATHER_DOCS_DIR("Docs"),
    GATHER_IMAGES_DIR("Images"),
    GATHER_IMAGES_DYNAMIC_DIR("Dynamic"),
    GATHER_IMAGES_STATIC_DIR("Static"),

    FORMAT_FRONT_MATTER_HTML_DIR("FrontMatterHTML"),
    FORMAT_IMAGE_METADATA_DIR("ImageMetadata"),
    FORMAT_PREPROCESS_DIR("Preprocess"),
    FORMAT_TRANSFORMED_DIR("Transformed"),
    FORMAT_SPLIT_TOC_DIR("splitToc"),
    FORMAT_POST_TRANSFORM_DIR("PostTransform"),
    FORMAT_CREATED_LINKS_TRANSFORM_DIR("CreatedLinksTransform"),
    FORMAT_FIXED_TRANSFORM_DIR("FixedTransform"),
    FORMAT_HTML_WRAPPER_DIR("HTMLWrapper"),
    FORMAT_PROCESS_PAGES_DIR("ProcessPages"),
    FORMAT_SPLIT_EBOOK_DIR("splitEbook"),

    GATHER_TOC_FILE("toc.xml"),
    GATHER_DOCS_METADATA_DIR("Metadata"),
    GATHER_DOCS_GUIDS_FILE("docs-guids.txt"),
    GATHER_DOC_MISSING_GUIDS_FILE("Docs_doc_missing_guids.txt"),
    GATHER_DYNAMIC_IMAGE_GUIDS_FILE("dynamic-image-guids.txt"),
    GATHER_STATIC_IMAGE_MANIFEST_FILE("static-image-manifest.txt"),
    GATHER_IMAGES_MISSING_IMAGE_GUIDS_FILE("missing_image_guids.txt"),

    FORMAT_DE_DUPPING_ANCHOR_FILE("eBG_deDupping_anchors.txt"),
    FORMAT_DOC_TO_IMAGE_MANIFEST_FILE("doc-to-image-manifest.txt"),
    FORMAT_SPLIT_TOC_FILE("splitToc.xml"),
    FORMAT_SPLIT_TOC_DOC_TO_SPLIT_BOOK_FILE("doc-To-SplitBook.txt"),
    FORMAT_SPLIT_TOC_SPLIT_NODE_INFO_FILE("splitNodeInfo.txt"),
    FORMAT_SPLIT_EBOOK_SPLIT_TITLE_FILE("splitTitle.xml"),

    ASSEMBLE_TITLE_FILE("title.xml"),

    ASSEMBLE_DOCUMENTS_DIR("documents"),
    ASSEMBLE_ASSETS_DIR("assets"),
    ASSEMBLE_ARTWORK_DIR("artwork");

    private final String name;

    NortTocCwbFileSystemConstants(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
