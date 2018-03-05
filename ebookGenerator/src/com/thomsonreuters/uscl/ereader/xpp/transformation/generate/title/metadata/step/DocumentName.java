package com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import org.jetbrains.annotations.NotNull;

/**
 * Representation of transformed XPP document name
 */
public class DocumentName {
    private static final String BASE_NAME_GROUP = "baseName";
    private static final String DOCUMENT_ORDER_GROUP = "documentOrderGroup";
    private static final String TYPE_GROUP = "type";
    private static final String DOCUMENT_FAMILY_UUID_GROUP = "familyUuid";

    private static final Pattern PARTS_DOCUMENT_NAMING_PATTERN = Pattern.compile(
        String.format(
            "((?<%s>.*\\.DIVXML)_(?<%s>\\d+)(_(?<%s>%s|%s)){0,1}_(?<%s>[a-zA-Z0-9.]{1,})\\.[a-z]*)",
            BASE_NAME_GROUP,
            DOCUMENT_ORDER_GROUP,
            TYPE_GROUP,
            PartType.MAIN.getName(),
            PartType.FOOTNOTE.getName(),
            DOCUMENT_FAMILY_UUID_GROUP));

    private final Matcher matcher;
    private final String originalFileName;

    public DocumentName(@NotNull final String fileName) {
        final Pattern pattern = PARTS_DOCUMENT_NAMING_PATTERN;
        final Matcher documentNameMatcher = pattern.matcher(fileName);
        if (!documentNameMatcher.find()) {
            throw new IllegalArgumentException(
                "Provided file name: " + fileName + ", does not correspond to name pattern");
        }
        matcher = documentNameMatcher;
        originalFileName = fileName;
    }

    /**
     * base name
     */
    @NotNull
    public String getBaseName() {
        return matcher.group(BASE_NAME_GROUP);
    }

    /**
     * Extract file order from name
     */
    @NotNull
    public int getOrder() {
        return Integer.valueOf(matcher.group(DOCUMENT_ORDER_GROUP));
    }

    /**
     * part type. Applicable only for parts.
     */
    @NotNull
    public PartType getPartType() {
        return PartType.valueOfByName(matcher.group(TYPE_GROUP));
    }

    /**
     * Extract family uuid from file name
     */
    @NotNull
    public String getDocFamilyUuid() {
        return matcher.group(DOCUMENT_FAMILY_UUID_GROUP);
    }

    @NotNull
    public String getOriginalFileName() {
        return originalFileName;
    }
}
