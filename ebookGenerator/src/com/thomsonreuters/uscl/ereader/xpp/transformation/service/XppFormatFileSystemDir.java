package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

/**
 * Keeps list of directories which is used in XppFormat steps.
 */
public enum XppFormatFileSystemDir {
    FONTS_CSS_DIR("01_Css"),
    ORIGINAL_DIR("02_Original"),
    SOURCE_DIR("03_StructureWithMetadata"),
    SECTIONBREAKS_DIR("04_Sectionbreaks"),
    CROSS_PAGE_FOOTNOTES("05_CrossPageFootnotes"),
    ANCHORS_DIR("06_Anchors"),
    MULTICOLUMNS_UP_DIR("07_MultiColumnsUp"),
    SECTIONBREAKS_UP_DIR("08_SectionbreaksUp"),
    ORIGINAL_PARTS_DIR("09_OriginalParts"),
    ORIGINAL_PAGES_DIR("10_OriginalPages"),
    HTML_PAGES_DIR("11_HtmlPages"),
    EXTERNAL_LINKS_DIR("12_ExternalLinks"),
    EXTERNAL_LINKS_MAPPING("12_ExternalLinks_Mapping"),
    TOC_DIR("13_Toc"),
    TITLE_METADATA_DIR("14_title_metadata"),
    FAILED_CITE_QUERY_TAGS("15_failed_cite_query_tags");

    private final String dirName;

    XppFormatFileSystemDir(final String dirName) {
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }
}
