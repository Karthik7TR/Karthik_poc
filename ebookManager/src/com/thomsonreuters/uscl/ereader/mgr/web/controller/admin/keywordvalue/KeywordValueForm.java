package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KeywordValueForm {
    public static final String FORM_NAME = "KeywordValueForm";

    private Long typeId;
    private String name;
    private KeywordTypeCode keywordTypeCode;

    public void initialize(final KeywordTypeValue value) {
        typeId = value.getId();
        name = value.getName();
        keywordTypeCode = value.getKeywordTypeCode();
    }

    public KeywordTypeValue makeKeywordTypeValue() {
        final KeywordTypeValue value = new KeywordTypeValue();
        value.setId(typeId);
        value.setName(name);
        value.setKeywordTypeCode(keywordTypeCode);
        return value;
    }
}
