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

    FORMAT_FRONT_MATTER_PDF_IMAGES_DIR("FrontMatterPdfImages"),
    FORMAT_TRANSFORM_TOC("00.TransformToc"),
    FORMAT_FRONT_MATTER_HTML_DIR("01.FrontMatterHTML"),
    FORMAT_IMAGE_METADATA_DIR("02.ImageMetadata"),
    FORMAT_PREPROCESS_DIR("03.Preprocess"),
    FORMAT_TRANSFORM_CHAR_SEQUENCES_DIR("04.TransformCharSequences"),
    FORMAT_TRANSFORMED_DIR("05.Transformed"),
    FORMAT_SPLIT_TOC_DIR("06.splitToc"),
    FORMAT_POST_TRANSFORM_DIR("07.PostTransform"),
    FORMAT_CREATED_LINKS_TRANSFORM_DIR("08.CreatedLinksTransform"),
    FORMAT_FIXED_TRANSFORM_DIR("09.FixedTransform"),
    FORMAT_HTML_WRAPPER_DIR("10.HTMLWrapper"),
    FORMAT_PROCESS_PAGES_DIR("11.ProcessPages"),
    FORMAT_ADD_THESAURUS_TO_DOCUMENTS_DIR("11.AddThesaurusToDocuments"),
    FORMAT_JSOUP_TRANSFORMATION_DIR("11.JsoupTransformations"),
    FORMAT_SPLIT_EBOOK_DIR("12.splitEbook"),

    TOC_FILE("toc.xml"),
    GATHER_INDEX_TOC_DIR("indexToc"),
    GATHER_INDEX_DOCS_DIR("indexDocs"),
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
    FORMAT_THESAURUS_FIELDS_XML_FILE("fields.xml"),
    FORMAT_THESAURUS_TEMPLATE_XML_FILE("template.xml"),
    FORMAT_THESAURUS_XML_FILE("thesaurus.xml"),

    ASSEMBLE_TITLE_FILE("title.xml"),

    ASSEMBLE_DOCUMENTS_DIR("documents"),
    ASSEMBLE_ASSETS_DIR("assets"),
    ASSEMBLE_ARTWORK_DIR("artwork"),
    ASSEMBLE_MINOR_VERSIONS_MAPPING_XML_FILE("mapping.xml");

    private final String name;

    NortTocCwbFileSystemConstants(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}