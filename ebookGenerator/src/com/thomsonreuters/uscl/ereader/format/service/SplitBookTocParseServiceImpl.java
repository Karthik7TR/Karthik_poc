package com.thomsonreuters.uscl.ereader.format.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thomsonreuters.uscl.ereader.format.parsinghandler.SplitBookTocFilter;
import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.ioutil.EntityDecodedOutputStream;
import com.thomsonreuters.uscl.ereader.ioutil.EntityEncodedInputStream;

public class SplitBookTocParseServiceImpl implements SplitBookTocParseService {

	private static final Logger LOG = Logger.getLogger(SplitBookTocParseServiceImpl.class);	
	
	

	@Override
	public Map<String,DocumentInfo> generateSplitBookToc(final InputStream tocXml, final OutputStream splitTocXml,
			final List<String> splitTocGuidList, final String titleBreakLabel){
		try {

			// Obtain a new instance of a SAXParserFactory.
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = factory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			SplitBookTocFilter splitBookTocFilter = new SplitBookTocFilter();
			splitBookTocFilter.setParent(xmlReader);
			Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
			props.setProperty("omit-xml-declaration", "yes");
			Serializer serializer = SerializerFactory.getSerializer(props);
			serializer.setOutputStream(new EntityDecodedOutputStream(splitTocXml, true));

			splitBookTocFilter.setContentHandler(serializer.asContentHandler());
			splitBookTocFilter.setSplitTocGuidList(splitTocGuidList);
			splitBookTocFilter.setTitleBreakText(titleBreakLabel);

			splitBookTocFilter.parse(new InputSource(new EntityEncodedInputStream(tocXml)));
						
			return splitBookTocFilter.getDocumentInfoMap();

		}

		catch (SAXException e) {
			throw new RuntimeException("SAXException occurred while generating splitEbook toc file.", e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Failed to configure SAX Parser when generating splitEbook toc file.", e);
		} catch (IOException e) {
			throw new RuntimeException("An IOException occurred while generating the splitEbook toc file.", e);
		}

	}
	


}
