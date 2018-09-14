package com.thomsonreuters.uscl.ereader.quality.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompareUnit {
    private String source;
    private String target;
}
