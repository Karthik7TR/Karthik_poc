/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;

public class CoreDaoImpl implements CoreDao {
	//private static final Logger log = Logger.getLogger(CoreDaoImpl.class);
	private SessionFactory sessionFactory;
	
	@Override
	public BookDefinition findBookDefinition(BookDefinitionKey key) {
//FUT		return (BookDefinition) hibernateSession.get(BookDefinition.class, key);
// STUB
BookDefinition bookDef = createStubBookDefinition(key);
return bookDef;
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<BookDefinition> findAllBookDefinitions() {
		
		List<BookDefinition> bookDefinitions;
//FUT		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BookDefinition.class);
//FUT		return criteria.list();
		
		{// STUB		
			bookDefinitions = new ArrayList<BookDefinition>();
			for (long i = 0; i < 10; i++) {
				BookDefinitionKey key = new BookDefinitionKey(("title_id_"+i), i);
				BookDefinition bookDef = createStubBookDefinition(key);
				bookDefinitions.add(bookDef);
			}
		}//STUB
		return bookDefinitions;
	}
	
// STUB
private static BookDefinition createStubBookDefinition(BookDefinitionKey key) {  // STUB
	BookDefinition bookDef = new BookDefinition();
	bookDef.setBookDefinitionKey(key);
	bookDef.setName(String.format("Stub Book Name - %s", key.getBookTitleId()));
	return bookDef;
}

	@Required
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}

