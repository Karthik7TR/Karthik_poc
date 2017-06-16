package com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.type;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

/**
 * Supported DIVXML components, with file names patterns as parameter.
 */
public enum BundleFileType
{
    MAIN_CONTENT(".*\\.DIVXML"),
    SUMMARY_TABLE_OF_CONTENTS(".*_Summary_Table_of_Contents\\.DIVXML"),
    CORRELATION_TABLE(".*_Correlation_Table\\.DIVXML"),
    INDEX(".*_Index\\.DIVXML"),
    ADDED_REVISED_JUDGES_CARDS(".*_AddedRevised_Judges_Cards\\.DIVXML"),
    FULL_SET_JUDGES_CARDS(".*_Full_Set_Judges_Cards\\.DIVXML"),
    TABLE_OF_ADDED_LRRE(".*_Table_of_Added_LRRE\\.DIVXML"),
    TABLE_OF_LRRE(".*_Table_of_LRRE\\.DIVXML"),
    TABLE_OF_CASES(".*_Table_of_Cases\\.DIVXML"),
    KEY_NUMBER_TABLE(".*_Key_Number_Table\\.DIVXML"),
    TABLE_OF_ADDED_CASES(".*_Table_of_Added_Cases\\.DIVXML"),
    SUMMARY_AND_DETAILED_TABLE_OF_CONTENTS(".*_Summary_and_Detailed_Table_of_Contents\\.DIVXML"),
    DETAILED_TABLE_OF_CONTENTS(".*_Detailed_Table_of_Contents\\.DIVXML"),
    FRONT(".*_Front_vol_\\d*\\.DIVXML"),
    FILLING_INSTRUCTIONS(".*-Filing_Instructions\\.DIVXML"),
    TABLE_OF_ADDED_KEY_NUMBERS(".*_Table_of_Added_Key_Numbers\\.DIVXML"),
    IMPOSITION_LIST(".*_Imposition_List\\.DIVXML");

    private final Pattern originalFileNamePattern;
    private final Pattern htmlDocumentFileNamePattern;
    private final FilenameFilter fileNameFilter;

    BundleFileType(final String pattern)
    {
        originalFileNamePattern = Pattern.compile(pattern + "\\.xml");
        htmlDocumentFileNamePattern = Pattern.compile(pattern + "_\\d+_[a-zA-Z0-9]*\\.[a-z]*");
        fileNameFilter = new HtmlDocumentFileNameFilter(this);
    }

    public FilenameFilter getHtmlDocFileNameFilter()
    {
        return fileNameFilter;
    }

    /**
     * Get instance by file name.
     */
    @NotNull
    public static BundleFileType getByFileName(@NotNull final String fileName)
    {
        return getByName(fileName, true);
    }

    /**
     * Get instance by html document file name
     */
    @NotNull
    public static BundleFileType getByDocumentFileName(@NotNull final String fileName)
    {
        return getByName(fileName, false);
    }

    private static BundleFileType getByName(final String name, final boolean usingOriginalNamePattern)
    {
        final Pattern mainContentPattern = usingOriginalNamePattern
            ? MAIN_CONTENT.originalFileNamePattern
                : MAIN_CONTENT.htmlDocumentFileNamePattern;
        if (!mainContentPattern.matcher(name).matches())
        {
            throw new UnsupportedOperationException("File not supported, file: " + name + " have unsupported name pattern");
        }

        BundleFileType result = BundleFileType.MAIN_CONTENT;
        for (final BundleFileType fileType : values())
        {
            final Pattern pattern = usingOriginalNamePattern
                ? fileType.originalFileNamePattern : fileType.htmlDocumentFileNamePattern;
            if (fileType != result && pattern.matcher(name).matches())
            {
                result = fileType;
                break;
            }
        }
        return result;
    }

    private static final class HtmlDocumentFileNameFilter implements FilenameFilter
    {
        private final BundleFileType bundleFileType;

        private HtmlDocumentFileNameFilter(final BundleFileType bundleFileType)
        {
            this.bundleFileType = bundleFileType;
        }

        @Override
        public boolean accept(final File dir, final String name)
        {
            return bundleFileType == getByDocumentFileName(name);
        }
    }
}
