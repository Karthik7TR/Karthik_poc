/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepExecution;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.MapEntryKeyComparator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.StepStartTimeComparator;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

/**
 * A View Data Object (VDO) wrapper around a Spring Batch JobExecution object
 * (Decorator/VDO patterns). Exists to provide convenience methods to expose
 * complex calculated data and values which would otherwise be very messy to
 * calculate directly within the JSP.
 */
public class BookLibraryVdo {
	private static final Logger log = Logger.getLogger(BookLibraryVdo.class);
	private static final Comparator<StepExecution> stepStartTimeComparator = new StepStartTimeComparator();
	/** Comparator to sort lists of properties into ascending key order */
	private static final Comparator<Map.Entry<String, ?>> mapEntryKeyComparator = new MapEntryKeyComparator();
	private BookDefinition bookDefinition;

	public BookDefinition getBookDefinition() {
		return bookDefinition;
	}

	public void setBookDefinition(BookDefinition bookDefinition) {
		this.bookDefinition = bookDefinition;
	}

	public Long getMajorVersion() {
		return bookDefinition.getMajorVersion();
	}

	public String getTitleId() {
		return bookDefinition.getPrimaryKey().getTitleId();
	}

	public String getFullyQualifiedTitleId() {
		return bookDefinition.getPrimaryKey().getFullyQualifiedTitleId();
	}

	public String getBookName() {
		return bookDefinition.getBookName();
	}

	public String getAuthor() {
		return bookDefinition.getAuthorInfo();
	}

	/**
	 * Create a ascending key sorted list of map entries from the specified map.
	 * 
	 * @param map
	 *            the map whose entries will be extracted into a list sorted by
	 *            key in ascending order.
	 * @return a list of map entries sorted by key.
	 */
	private List<Map.Entry<String, ?>> createMapEntryList(
			Set<Map.Entry<String, ?>> entrySet) {
		List<Map.Entry<String, ?>> mapEntryList = new ArrayList<Map.Entry<String, ?>>();
		Iterator<Map.Entry<String, ?>> entryIterator = entrySet.iterator();
		while (entryIterator.hasNext()) {
			mapEntryList.add(entryIterator.next());
		}
		Collections.sort(mapEntryList, mapEntryKeyComparator);
		return mapEntryList;
	}
}
