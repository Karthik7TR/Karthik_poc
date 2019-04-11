package com.thomsonreuters.uscl.ereader.mgr.annotaion.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnnotationMetadata {
    private String methodName;
    private String errorViewName;
    private String errorRedirectMvcName;
}
