package com.thomsonreuters.uscl.ereader.proview;

import java.io.InputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.junit.Before;
import org.junit.Test;

public class GatherTocUnmarshalTest {
	private TitleMetadata titleMetadata;
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testTitleMetadataMarshallsCorrectly() throws Exception {
		InputStream inputStream = GatherTocUnmarshalTest.class.getResourceAsStream("gathered_toc_test.xml");
		 IBindingFactory bfact = 
					BindingDirectory.getFactory(com.thomsonreuters.uscl.ereader.gather.TableOfContents.class);
			IUnmarshallingContext unmtcx = bfact.createUnmarshallingContext();
			com.thomsonreuters.uscl.ereader.gather.TableOfContents actualTableOfContents = (com.thomsonreuters.uscl.ereader.gather.TableOfContents) unmtcx.unmarshalDocument(inputStream, "UTF-8");
			System.out.println(actualTableOfContents.toString());
	}
}
