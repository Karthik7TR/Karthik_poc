package com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.type;

import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

/**
 * Supported DIVXML components, with file names patterns as parameter.
 */
public enum BundleFileType
{
    MAIN_CONTENT(".*\\.DIVXML\\.xml"),
    SUMMARY_TABLE_OF_CONTENTS(".*_Summary_Table_of_Contents\\.DIVXML\\.xml"),
    CORRELATION_TABLE(".*_Correlation_Table\\.DIVXML\\.xml"),
    INDEX(".*_Index\\.DIVXML\\.xml"),
    ADDED_REVISED_JUDGES_CARDS(".*_AddedRevised_Judges_Cards\\.DIVXML\\.xml"),
    FULL_SET_JUDGES_CARDS(".*_Full_Set_Judges_Cards\\.DIVXML\\.xml"),
    TABLE_OF_ADDED_LRRE(".*_Table_of_Added_LRRE\\.DIVXML\\.xml"),
    TABLE_OF_LRRE(".*_Table_of_LRRE\\.DIVXML\\.xml"),
    TABLE_OF_CASES(".*_Table_of_Cases\\.DIVXML\\.xml"),
    KEY_NUMBER_TABLE(".*_Key_Number_Table\\.DIVXML\\.xml"),
    TABLE_OF_ADDED_CASES(".*_Table_of_Added_Cases\\.DIVXML\\.xml"),
    SUMMARY_AND_DETAILED_TABLE_OF_CONTENTS(".*_Summary_and_Detailed_Table_of_Contents\\.DIVXML\\.xml"),
    DETAILED_TABLE_OF_CONTENTS(".*_Detailed_Table_of_Contents\\.DIVXML\\.xml"),
    FRONT(".*_Front_vol_\\d*\\.DIVXML\\.xml"),
    FILLING_INSTRUCTIONS(".*-Filing_Instructions\\.DIVXML\\.xml"),
    TABLE_OF_ADDED_KEY_NUMBERS(".*_Table_of_Added_Key_Numbers\\.DIVXML\\.xml"),
    IMPOSITION_LIST(".*_Imposition_List\\.DIVXML\\.xml");

    private final Pattern pattern;

    BundleFileType(final String pattern)
    {
        this.pattern = Pattern.compile(pattern);
    }

    /**
     * Get instance by file name.
     * @param fileName
     * @return
     */
    @NotNull
    public static BundleFileType getByFileName(@NotNull final String fileName)
    {
        if (!MAIN_CONTENT.pattern.matcher(fileName).matches())
        {
            throw new UnsupportedOperationException("File not supported, file: " + fileName + " have unsupported name pattern");
        }

        BundleFileType result = BundleFileType.MAIN_CONTENT;
        for (final BundleFileType fileType : values())
        {
            if (fileType != result && fileType.pattern.matcher(fileName).matches())
            {
                result = fileType;
                break;
            }
        }
        return result;
    }
}
