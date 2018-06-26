package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

/**
 * Keeps list of directories which is used in XppFormat steps.
 */
public enum XppFormatFileSystemDir {
    FONTS_CSS_DIR("01_Css"),
    ORIGINAL_DIR("02_Original"),
    SOURCE_DIR("03_StructureWithMetadata"),
    VOLUMES_MAP_DIR("04_VolumesMap"),
    SECTIONBREAKS_DIR("05_Sectionbreaks"),
    CROSS_PAGE_FOOTNOTES("06_CrossPageFootnotes"),
    ANCHORS_DIR("07_Anchors"),
    MULTICOLUMNS_UP_DIR("08_MultiColumnsUp"),
    SECTIONBREAKS_UP_DIR("09_SectionbreaksUp"),
    ORIGINAL_PARTS_DIR("10_OriginalParts"),
    ORIGINAL_PAGES_DIR("11_OriginalPages"),
    POCKET_PART_LINKS_DIR("12_PocketPartLinks"),
    HTML_PAGES_DIR("13_HtmlPages"),
    EXTERNAL_LINKS_DIR("14_ExternalLinks"),
    EXTERNAL_LINKS_MAPPING("15_ExternalLinks_Mapping"),
    TOC_DIR("16_Toc"),
    TITLE_METADATA_DIR("17_title_metadata"),
    FAILED_CITE_QUERY_TAGS("18_failed_cite_query_tags"),
    UNESCAPE_DIR("19_Unescape"),
    QUALITY_DIR("20_Quality");

    private final String dirName;

    XppFormatFileSystemDir(final String dirName) {
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }
}
