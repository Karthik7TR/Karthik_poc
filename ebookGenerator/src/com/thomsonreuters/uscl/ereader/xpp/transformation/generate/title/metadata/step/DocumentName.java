package com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

/**
 * Representation of transformed XPP document name
 */
public class DocumentName
{
    private static final Pattern DOCUMENT_NAMING_PATTERN = Pattern.compile(".*\\.DIVXML_(\\d+)_([a-zA-Z0-9]{1,})\\.[a-z]*");
    private static final int DOCUMENT_ORDER_GROUP = 1;
    private static final int DOCUMENT_FAMILY_UUID_GROUP = 2;

    private final Matcher matcher;
    private final String originalFileName;

    public DocumentName(@NotNull final String fileName)
    {
        final Matcher documentNameMatcher = DOCUMENT_NAMING_PATTERN.matcher(fileName);
        if (!documentNameMatcher.find())
        {
            throw new IllegalArgumentException(
                "Provided file name: " + fileName + ", does not corresponds to name pattern");
        }
        matcher = documentNameMatcher;
        originalFileName = fileName;
    }

    /**
     * Extract file order from name
     */
    @NotNull
    public int getOrder()
    {
        return Integer.valueOf(matcher.group(DOCUMENT_ORDER_GROUP));
    }

    /**
     * Extract family uuid from file name
     */
    @NotNull
    public String getDocFamilyGuid()
    {
        return matcher.group(DOCUMENT_FAMILY_UUID_GROUP);
    }

    @NotNull
    public String getOriginalFileName()
    {
        return originalFileName;
    }
}
