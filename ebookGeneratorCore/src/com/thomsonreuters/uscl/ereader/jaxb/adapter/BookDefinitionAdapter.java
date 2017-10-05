package com.thomsonreuters.uscl.ereader.jaxb.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

public class BookDefinitionAdapter extends XmlAdapter<String, BookDefinition> {
    /** IMPORTANT
     *  returns a dummy BookDefinition, all fields beyond the bookDefinitionId are default values
     */
    @Override
    public BookDefinition unmarshal(final String xml) throws Exception {
        final BookDefinition book = new BookDefinition();
        book.setEbookDefinitionId(Long.parseLong(xml));
        return book;
    }

    @Override
    public String marshal(final BookDefinition book) throws Exception {
        // ebookDefinitionID should never be null
        return book.getEbookDefinitionId().toString();
    }
}
