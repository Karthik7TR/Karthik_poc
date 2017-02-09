package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class KeywordValueForm
{
    //private static final Logger log = LogManager.getLogger(EditBookDefinitionForm.class);
    public static final String FORM_NAME = "KeywordValueForm";

    private Long id;
    private String name;
    private KeywordTypeCode keywordTypeCode;

    public KeywordValueForm()
    {
        super();
    }

    public void initialize(final KeywordTypeValue value)
    {
        id = value.getId();
        name = value.getName();
        keywordTypeCode = value.getKeywordTypeCode();
    }

    public KeywordTypeValue makeKeywordTypeValue()
    {
        final KeywordTypeValue value = new KeywordTypeValue();
        value.setId(id);
        value.setName(name);
        value.setKeywordTypeCode(keywordTypeCode);

        return value;
    }

    public Long getTypeId()
    {
        return id;
    }

    public void setTypeId(final Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public KeywordTypeCode getKeywordTypeCode()
    {
        return keywordTypeCode;
    }

    public void setKeywordTypeCode(final KeywordTypeCode keywordTypeCode)
    {
        this.keywordTypeCode = keywordTypeCode;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
