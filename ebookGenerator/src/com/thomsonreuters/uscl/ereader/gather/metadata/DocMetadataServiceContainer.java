package com.thomsonreuters.uscl.ereader.gather.metadata;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocMetadataServiceContainer {
    private DocMetadataService docMetadataService;
    private Long jobInstanceId;
    private String titleId;

    public DocMetadata findDocMetadataByPrimaryKey(final String docUuid) {
        return docMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docUuid);
    }
}
