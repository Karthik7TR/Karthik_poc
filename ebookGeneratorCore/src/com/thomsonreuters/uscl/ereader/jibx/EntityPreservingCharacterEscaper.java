/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.jibx;

import java.io.IOException;
import java.io.Writer;

import org.jibx.runtime.ICharacterEscaper;

/**
 * @author u0081674
 *
 */
public class EntityPreservingCharacterEscaper implements ICharacterEscaper {

	/* (non-Javadoc)
	 * @see org.jibx.runtime.ICharacterEscaper#writeAttribute(java.lang.String, java.io.Writer)
	 */
	@Override
	public void writeAttribute(String text, Writer writer) throws IOException {
		writer.write(text);
	}

	/* (non-Javadoc)
	 * @see org.jibx.runtime.ICharacterEscaper#writeCData(java.lang.String, java.io.Writer)
	 */
	@Override
	public void writeCData(String text, Writer writer) throws IOException {
		writer.write(text);
	}

	/* (non-Javadoc)
	 * @see org.jibx.runtime.ICharacterEscaper#writeContent(java.lang.String, java.io.Writer)
	 */
	@Override
	public void writeContent(String text, Writer writer) throws IOException {
		writer.write(text);
	}

}
