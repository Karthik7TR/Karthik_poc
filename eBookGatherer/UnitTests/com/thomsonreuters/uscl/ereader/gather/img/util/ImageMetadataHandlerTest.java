package com.thomsonreuters.uscl.ereader.gather.img.util;

import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.thomsonreuters.uscl.ereader.gather.img.util.ImageMetadataHandler;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

public class ImageMetadataHandlerTest {
	private ImageMetadataHandler imageMetadataHandler;
	XMLReader reader;
	
	@Before
	public void setUp() throws Exception{
		this.imageMetadataHandler = new ImageMetadataHandler();
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		reader = parserFactory.newSAXParser().getXMLReader();
		imageMetadataHandler = new ImageMetadataHandler();
		reader.setContentHandler(imageMetadataHandler);
		
	}
	
	@Test
	public void testMetadataString() throws Exception{
		String imgMetadata = "<n-metadata><img.md.block><img.md.image.format>application/pdf</img.md.image.format><img.md.pdf.block>"
				+ "<img.md.image.bytes>80995</img.md.image.bytes><img.md.image.dpi>400</img.md.image.dpi><img.md.image.height>4400</img.md.image.height>"
				+ "<img.md.image.width>3400</img.md.image.width><img.md.image.units>px</img.md.image.units></img.md.pdf.block></img.md.block></n-metadata>";
		reader.parse(new InputSource(new StringReader(imgMetadata)));
		ImgMetadataInfo imgMetadataInfo = imageMetadataHandler.getImgMetadataInfo();
		assertTrue(imgMetadataInfo!=null);
		assertTrue(imgMetadataInfo.getDimUnit().equals("px"));
		assertTrue(imgMetadataInfo.getDpi().equals(new Long(400)));
		assertTrue(imgMetadataInfo.getHeight().equals(new Long(4400)));
		assertTrue(imgMetadataInfo.getWidth().equals(new Long(3400)));
	}
	
	@Test
	public void testMetadataString2() throws Exception{
		String imgMetadata = "<n-metadata><img.md.block><format>application/pdf</format><img.md.pdf.block>"
				+ "<img.md.image.bytes>80995</img.md.image.bytes><dpi>400</dpi><height>4400</height>"
				+ "<width>3400</width><units>px</units></img.md.pdf.block></img.md.block></n-metadata>";
		reader.parse(new InputSource(new StringReader(imgMetadata)));
		ImgMetadataInfo imgMetadataInfo = imageMetadataHandler.getImgMetadataInfo();
		assertTrue(imgMetadataInfo!=null);
		assertTrue(imgMetadataInfo.getDimUnit().equals("px"));
		assertTrue(imgMetadataInfo.getDpi().equals(new Long(400)));
		assertTrue(imgMetadataInfo.getHeight().equals(new Long(4400)));
		assertTrue(imgMetadataInfo.getWidth().equals(new Long(3400)));
	}
	
	@Test
	public void testMetadataStringNoInfo() throws Exception{
		String imgMetadata = "<n-metadata/>";
		reader.parse(new InputSource(new StringReader(imgMetadata)));
		ImgMetadataInfo imgMetadataInfo = imageMetadataHandler.getImgMetadataInfo();
		assertTrue(imgMetadataInfo!=null);
		assertTrue(imgMetadataInfo.getDimUnit() == null);
		assertTrue(imgMetadataInfo.getDpi() == null);
		assertTrue(imgMetadataInfo.getHeight() == null);
		assertTrue(imgMetadataInfo.getWidth() == null);
	}
	
	@Test
	public void testMetadataStringNoDpi() throws Exception{
		String imgMetadata = "<n-metadata><img.md.block><img.md.image.format>application/pdf</img.md.image.format><img.md.pdf.block>"
				+ "<img.md.image.bytes>80995</img.md.image.bytes><img.md.image.dpi/><img.md.image.height>4400</img.md.image.height>"
				+ "<img.md.image.width>3400</img.md.image.width><img.md.image.units>px</img.md.image.units></img.md.pdf.block></img.md.block></n-metadata>";
		reader.parse(new InputSource(new StringReader(imgMetadata)));
		ImgMetadataInfo imgMetadataInfo = imageMetadataHandler.getImgMetadataInfo();
		assertTrue(imgMetadataInfo!=null);
		assertTrue(imgMetadataInfo.getDimUnit().equals("px"));
		assertTrue(imgMetadataInfo.getDpi().equals(new Long(0)));
		assertTrue(imgMetadataInfo.getHeight().equals(new Long(4400)));
		assertTrue(imgMetadataInfo.getWidth().equals(new Long(3400)));
	}

	
	@Test
	public void testMetadataStringNoWidth() throws Exception{
		String imgMetadata = "<n-metadata><img.md.block><img.md.image.format>application/pdf</img.md.image.format><img.md.pdf.block>"
				+ "<img.md.image.bytes>80995</img.md.image.bytes><img.md.image.dpi/><img.md.image.height>4400</img.md.image.height>"
				+ "<img.md.image.width/><img.md.image.units>px</img.md.image.units></img.md.pdf.block></img.md.block></n-metadata>";
		Boolean thrown = false;
		try{
		reader.parse(new InputSource(new StringReader(imgMetadata)));
		}
		catch(Exception ex){
			thrown = true;			
		}
		assertTrue(thrown);
	}

}
