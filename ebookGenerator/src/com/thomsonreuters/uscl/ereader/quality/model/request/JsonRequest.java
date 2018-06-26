package com.thomsonreuters.uscl.ereader.quality.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonRequest {
    private Request[] requests;
}
