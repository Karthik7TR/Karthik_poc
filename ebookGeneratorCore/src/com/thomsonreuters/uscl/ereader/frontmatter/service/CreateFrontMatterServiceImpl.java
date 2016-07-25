/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.frontmatter.service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterAdditionalFrontMatterPageFilter;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterCopyrightPageFilter;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterResearchAssistancePageFilter;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterTitlePageFilter;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterWestlawNextPageFilter;
import com.thomsonreuters.uscl.ereader.ioutil.EntityDecodedOutputStream;
import com.thomsonreuters.uscl.ereader.ioutil.EntityEncodedInputStream;

/**
 * Service that generates HTML for all the Front Matter pages.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class CreateFrontMatterServiceImpl implements CreateFrontMatterService, ResourceLoaderAware
{
	private static final Logger LOG = LogManager.getLogger(CreateFrontMatterServiceImpl.class);
	private static final String HTML_EXTENSION = ".html";
	private static final String CSS_PLACEHOLDER = "er:#ebook_generator";
	private static final String WLN_LOGO_PLACEHOLDER = "er:#EBook_Generator_WestlawNextLogo";
	
	private Map<String,String> frontMatterLogoPlaceHolder = new HashMap<String,String>();	

	private ResourceLoader resourceLoader;
	private String frontMatterTitlePageTemplateLocation;
	private String frontMatterCopyrightPageTemplateLocation;
	private String frontMatterAdditionalPagesTemplateLocation;
	private String frontMatterResearchAssistancePageTemplateLocation;
	private String frontMatterWestlawNextPageTemplateLocation;

	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#generateAllFrontMatterPages(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
	 */
	@Override
	public void generateAllFrontMatterPages(File outputDir, BookDefinition bookDefinition)
		throws EBookFrontMatterGenerationException
	{
		File titlePage = new File(outputDir, FrontMatterFileName.FRONT_MATTER_TITLE + HTML_EXTENSION);
		writeHTMLFile(titlePage, generateTitlePage(bookDefinition));
		
		LOG.debug("Front Matter Title HTML page generated.");
		
		File copyrightPage = new File(outputDir, FrontMatterFileName.COPYRIGHT + HTML_EXTENSION);
		writeHTMLFile(copyrightPage, generateCopyrightPage(bookDefinition));
		
		LOG.debug("Front Matter Copyright HTML page generated.");
		
		for (FrontMatterPage page : bookDefinition.getFrontMatterPages())
		{
			File additionalPage = new File(outputDir, FrontMatterFileName.ADDITIONAL_FRONT_MATTER +
					page.getId() + HTML_EXTENSION);
			writeHTMLFile(additionalPage, generateAdditionalFrontMatterPage(bookDefinition, page.getId()));
			
			LOG.debug("Front Matter Additional HTML page " + page.getId() + " generated.");
		}
		
		File researchAssistancePage = new File(outputDir, FrontMatterFileName.RESEARCH_ASSISTANCE + 
				HTML_EXTENSION);
		writeHTMLFile(researchAssistancePage, generateResearchAssistancePage(bookDefinition));
		
		LOG.debug("Front Matter Research Assistance HTML page generated.");
		
		File westlawNextPage = new File(outputDir, FrontMatterFileName.WESTLAW + HTML_EXTENSION);
		writeHTMLFile(westlawNextPage, generateWestlawNextPage());
		
		LOG.debug("Front Matter WestlawNext HTML page generated.");
	}

	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getTitlePage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
	 */
	@Override
	public String getTitlePage(BookDefinition bookDefinition) 
		throws EBookFrontMatterGenerationException
	{
		String output = generateTitlePage(bookDefinition).replace(CSS_PLACEHOLDER, 
				"frontMatterCss.mvc?cssName=ebook_generator.css");
		for (Map.Entry<String, String> entry : this.frontMatterLogoPlaceHolder.entrySet()) {
			output = output.replace(entry.getKey(), "frontMatterImage.mvc?imageName="+entry.getValue());
		}	   
		return output;
	}
	
	public Map<String, String> getFrontMatterLogoPlaceHolder() {
		return frontMatterLogoPlaceHolder;
	}

	public void setFrontMatterLogoPlaceHolder(Map<String, String> frontMatterLogoPlaceHolder) {
		this.frontMatterLogoPlaceHolder = frontMatterLogoPlaceHolder;
	}


	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getCopyrightPage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
	 */
	@Override
	public String getCopyrightPage(BookDefinition bookDefinition) 
		throws EBookFrontMatterGenerationException
	{
		String output = generateCopyrightPage(bookDefinition).replace(CSS_PLACEHOLDER, 
				"frontMatterCss.mvc?cssName=ebook_generator.css");
		return output;
	}

	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getAdditionalFrontPage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition, java.lang.Long)
	 */
	@Override
	public String getAdditionalFrontPage(BookDefinition bookDefinition, Long front_matter_page_id) 
		throws EBookFrontMatterGenerationException
	{
		String output = generateAdditionalFrontMatterPage(bookDefinition, front_matter_page_id).replace(
				CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css");
		return output;
	}

	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getResearchAssistancePage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
	 */
	@Override
	public String getResearchAssistancePage(BookDefinition bookDefinition) 
		throws EBookFrontMatterGenerationException
	{
		String output = generateResearchAssistancePage(bookDefinition).replace(
				CSS_PLACEHOLDER,  "frontMatterCss.mvc?cssName=ebook_generator.css");
		return output;
	}

	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getWestlawNextPage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
	 */
	@Override
	public String getWestlawNextPage(BookDefinition bookDefinition) 
		throws EBookFrontMatterGenerationException
	{
		String output = generateWestlawNextPage().replace(
				CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css").replace(
				WLN_LOGO_PLACEHOLDER, "frontMatterImage.mvc?imageName=EBook_Generator_WestlawNextLogo.png");
		return output;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) 
	{
		this.resourceLoader = resourceLoader;
	}

	public void setFrontMatterTitlePageTemplateLocation(String frontMatterTitlePageTemplateLocation) 
	{
		this.frontMatterTitlePageTemplateLocation = frontMatterTitlePageTemplateLocation;
	}
	
	public void setFrontMatterCopyrightPageTemplateLocation(String frontMatterCopyrightPageTemplateLocation) 
	{
		this.frontMatterCopyrightPageTemplateLocation = frontMatterCopyrightPageTemplateLocation;
	}
	
	public void setFrontMatterAdditionalPagesTemplateLocation(
			String frontMatterAdditionalPagesTemplateLocation) 
	{
		this.frontMatterAdditionalPagesTemplateLocation = frontMatterAdditionalPagesTemplateLocation;
	}
	
	public void setFrontMatterResearchAssistancePageTemplateLocation(
			String frontMatterResearchAssistancePageTemplateLocation) 
	{
		this.frontMatterResearchAssistancePageTemplateLocation = 
				frontMatterResearchAssistancePageTemplateLocation;
	}
	
	public void setFrontMatterWestlawNextPageTemplateLocation(
			String frontMatterWestlawNextPageTemplateLocation) 
	{
		this.frontMatterWestlawNextPageTemplateLocation = frontMatterWestlawNextPageTemplateLocation;
	}
	
	/**
	 * Writes the passed in text to the specified file on the system.
	 * 
	 * @param aFile target file
	 * @param text HTML text to be writen to the file
	 * @throws EBookFrontMatterGenerationException encountered issues while attempting to write
	 * to the specified file
	 */
	protected void writeHTMLFile(File aFile, String text) throws EBookFrontMatterGenerationException
	{
		Writer out = null;

		try
		{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aFile), "UTF8"));
			out.write(text);
			out.close();
		}
		catch (IOException e)
		{
			String errMessage = "Failed to write the following file to NAS: " + aFile.getAbsolutePath();
			LOG.error(errMessage);
			throw new EBookFrontMatterGenerationException(errMessage, e);
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			}
			catch (IOException e)
			{
				LOG.error("Unable to close I/O streams.", e);
			}
		}
	}

	/**
	 * Transforms the template using text from BookDefinition to generate HTML for the Title Page.
	 * 
	 * @param bookDefinition defines the book for which front matter is being generated
	 * @return HTML that represents the Title page
	 * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
	 */
	protected String generateTitlePage(BookDefinition bookDefinition)
		throws EBookFrontMatterGenerationException
	{
		return transformTemplate(new FrontMatterTitlePageFilter(bookDefinition), 
				getFrontMatterTitlePageTemplateLocation());
	}

	/**
	 * Transforms the template using text from BookDefinition to generate HTML for the Copyright Page.
	 * 
	 * @param bookDefinition defines the book for which front matter is being generated
	 * @return HTML that represents the Title page
	 * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
	 */
	protected String generateCopyrightPage(BookDefinition bookDefinition)
		throws EBookFrontMatterGenerationException
	{
		return transformTemplate(new FrontMatterCopyrightPageFilter(bookDefinition), 
				getFrontMatterCopyrightTemplateLocation());
	}

	/**
	 * Transforms the template using text from BookDefinition to generate HTML for the Copyright Page.
	 * 
	 * @param bookDefinition defines the book for which front matter is being generated
	 * @param pageId additional front matter page identifier
	 * @return HTML that represents the Title page
	 * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
	 */
	protected String generateAdditionalFrontMatterPage(BookDefinition bookDefinition, Long pageId)
		throws EBookFrontMatterGenerationException
	{
		return transformTemplate(new FrontMatterAdditionalFrontMatterPageFilter(bookDefinition, pageId),
				getFrontMatterAdditionalPagesTemplateLocation());
	}

	/**
	 * Transforms the template using text from BookDefinition to generate HTML for the 
	 * Research Assistance Page.
	 * 
	 * @param bookDefinition defines the book for which front matter is being generated
	 * @return HTML that represents the Title page
	 * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
	 */
	protected String generateResearchAssistancePage(BookDefinition bookDefinition)
		throws EBookFrontMatterGenerationException
	{
		return transformTemplate(new FrontMatterResearchAssistancePageFilter(bookDefinition), 
				getFrontMatterResearchAssistanceTemplateLocation());
	}

	/**
	 * Transforms the template to generate HTML for the WestlawNext Page.
	 * 
	 * @return HTML that represents the Title page
	 * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
	 */
	protected String generateWestlawNextPage()
		throws EBookFrontMatterGenerationException
	{
		return transformTemplate(new FrontMatterWestlawNextPageFilter(), 
				getFrontMatterWestlawNextTemplateLocation());
	}
	
	/**
	 * Helper method that applies the passed in filter on the template to generate the HTML
	 * 
	 * @param filter XML filter to be applied on the template
	 * @param template template file associated with the filter
	 * @return HTML representing the rendered page
	 * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
	 */
	private String transformTemplate(XMLFilterImpl filter, Resource template)
		throws EBookFrontMatterGenerationException
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(2048);
				
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		
		Serializer serializer = SerializerFactory.getSerializer(props);
		serializer.setOutputStream(new EntityDecodedOutputStream(outStream, true));
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		
		try 
		{
			filter.setParent(saxParserFactory.newSAXParser().getXMLReader());
			filter.setContentHandler(serializer.asContentHandler());
			filter.parse(new InputSource(new EntityEncodedInputStream(template.getInputStream())));
		}
		catch (IOException e) 
		{
			String message = "An IOException occurred while generating the Front Matter Title Page.";
			LOG.error(message);
			throw new EBookFrontMatterGenerationException(message, e);
		} 
		catch (SAXException e) 
		{
			String message = "Could not generate Front Matter Title Page.";
			LOG.error(message);
			throw new EBookFrontMatterGenerationException(message, e);
		}
		catch (ParserConfigurationException e) 
		{
			String message = "An exception occurred when configuring " +
					"the parser to generate the Front Matter Title Page.";
			LOG.error(message);
			throw new EBookFrontMatterGenerationException(message, e);
		}
		
		String output = null;
		try
		{
			output = outStream.toString("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			String message = "Could not encode front matter HTML into UTF-8.";
			LOG.error(message);
			throw new EBookFrontMatterGenerationException(message, e);
		}
		
		return output;
	}
	
	private Resource getFrontMatterTitlePageTemplateLocation() 
	{
		return this.resourceLoader.getResource(this.frontMatterTitlePageTemplateLocation);
	}

	private Resource getFrontMatterCopyrightTemplateLocation() 
	{
		return this.resourceLoader.getResource(this.frontMatterCopyrightPageTemplateLocation);
	}

	private Resource getFrontMatterAdditionalPagesTemplateLocation() 
	{
		return this.resourceLoader.getResource(this.frontMatterAdditionalPagesTemplateLocation);
	}

	private Resource getFrontMatterResearchAssistanceTemplateLocation() 
	{
		return this.resourceLoader.getResource(this.frontMatterResearchAssistancePageTemplateLocation);
	}

	private Resource getFrontMatterWestlawNextTemplateLocation() 
	{
		return this.resourceLoader.getResource(this.frontMatterWestlawNextPageTemplateLocation);
	}
}
