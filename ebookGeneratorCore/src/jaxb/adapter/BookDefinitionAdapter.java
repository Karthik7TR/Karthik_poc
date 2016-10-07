package jaxb.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

public class BookDefinitionAdapter extends XmlAdapter<String, BookDefinition> {

	@Override
	public BookDefinition unmarshal(String xml) throws Exception {
		BookDefinition book = new BookDefinition();
		book.setEbookDefinitionId(Long.parseLong(xml));
		return book;
	}

	@Override
	public String marshal(BookDefinition book) throws Exception {
		// ebookDefinitionID should never be null
		return book.getEbookDefinitionId().toString();
	}

}