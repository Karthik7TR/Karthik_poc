package com.thomsonreuters.uscl.ereader.gather.metadata;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is intended to extract
 * collection name: w_codesstaaznvdp and
 * docUuid: N5D8487409E3411E183F7C076EF385880-001305
 * from file names like: 4-w_codesstaaznvdp-N5D8487409E3411E183F7C076EF385880-001305.xml
 */
@Slf4j
@Getter
public class FileNameMetadata {
    private static final String METADATA_FILE_NAME_REGEX = "[^\\-]*-(?<%s>[^\\-]*)-(?<%s>.*).xml";
    private static final String COLLECTION_NAME = "collectionName";
    private static final String DOC_UUID = "docUuid";
    private static final String DEFAULT_COLLECTION_NAME = "defaultCollectionName";
    private static final Pattern METADATA_FILE_NAME_PATTERN = Pattern.compile(String.format(METADATA_FILE_NAME_REGEX, COLLECTION_NAME, DOC_UUID));

    private final String collectionName;
    private final String docUuid;

    public FileNameMetadata(final File metadataFile) {
        this(metadataFile.getName());
    }

    public FileNameMetadata(final String metadataFileName) {
        Matcher matcher = METADATA_FILE_NAME_PATTERN.matcher(metadataFileName);
        if (matcher.matches()) {
            collectionName = matcher.group(COLLECTION_NAME);
            docUuid = matcher.group(DOC_UUID);
        } else {
            collectionName = DEFAULT_COLLECTION_NAME;
            docUuid = FilenameUtils.removeExtension(metadataFileName);
            log.warn("Unexpected metadata file name format {}", metadataFileName);
        }
    }
}
