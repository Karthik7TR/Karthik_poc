/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.frontmatter.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;

/**
 * Service that generates HTML for all the Front Matter pages.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public interface CreateFrontMatterService 
{
	/**
	 * Creates all the Front Matter pages for this eBook and writes them to the specified NAS directory.
	 * 
	 * @param outputDir the target directory to which the generated front matter pages will be written
	 * @param bookDefinition defines the book for which front matter is being generated
	 */
	public void generateAllFrontMatterPages(final File outputDir, final BookDefinition bookDefinition)
		throws EBookFrontMatterGenerationException;
	
	/**
	 * Helper method that generates the preview HTML for the Title page that is displayed in the Manager.
	 * 
	 * @param bookDefinition defines the book for which front matter is being previewed
	 * @return HTML that will be rendered for the Title page
	 */
	public String getTitlePage(final BookDefinition bookDefinition)
			throws EBookFrontMatterGenerationException;
	
	/**
	 * Helper method that generates the preview HTML for the Copyright page that is displayed in the Manager.
	 * 
	 * @param bookDefinition defines the book for which front matter is being previewed
	 * @return HTML that will be rendered for the Copyright page
	 */
	public String getCopyrightPage(final BookDefinition bookDefinition)
			throws EBookFrontMatterGenerationException;
	
	/**
	 * Helper method that generates the preview HTML for the Additional Front Matter page requested.
	 * 
	 * @param bookDefinition defines the book for which front matter is being previewed
	 * @param front_matter_page_id identifier of the additional front matter page
	 * @return HTML that will be rendered for the identified additional front matter page
	 */
	public String getAdditionalFrontPage(
			final BookDefinition bookDefinition, final Long front_matter_page_id)
					throws EBookFrontMatterGenerationException;
	
	/**
	 * Helper method that generates the preview HTML for the Research Assistance page that 
	 * is displayed in the Manager.
	 * 
	 * @param bookDefinition defines the book for which front matter is being previewed
	 * @return HTML that will be rendered for the Research Assistance page
	 */
	public String getResearchAssistancePage(final BookDefinition bookDefinition)
			throws EBookFrontMatterGenerationException;
	
	/**
	 * Helper method that generates the preview HTML for the WestlawNext page that 
	 * is displayed in the Manager.
	 * 
	 * @param bookDefinition defines the book for which front matter is being previewed
	 * @return HTML that will be rendered for the WestlawNext page
	 */
	public String getWestlawNextPage(final BookDefinition bookDefinition)
			throws EBookFrontMatterGenerationException;
}
