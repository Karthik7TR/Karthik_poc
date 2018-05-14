package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KeywordCodeForm {
    public static final String FORM_NAME = "KeywordCodeForm";

    private Long codeId;
    private String name;
    private boolean isRequired;

    public void initialize(final KeywordTypeCode code) {
        codeId = code.getId();
        name = code.getName();
        isRequired = code.getIsRequired();
    }

    public KeywordTypeCode makeCode() {
        final KeywordTypeCode code = new KeywordTypeCode();
        code.setId(codeId);
        code.setName(name);
        code.setIsRequired(isRequired);
        return code;
    }
}
