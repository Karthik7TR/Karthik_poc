/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;

/**
 * Component tests for InternalLinkResolverFilter.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class InternalLinkResolverFilterTest {

	InternalLinkResolverFilter internalLinkResolverFilter;
	
	@Before
	public void setUp() throws Exception {
		DocumentMetadataAuthority mockDocumentMetadataAuthority = EasyMock.createMock(DocumentMetadataAuthority.class);
		internalLinkResolverFilter = new InternalLinkResolverFilter(mockDocumentMetadataAuthority);
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testGetDocumentUuidFromResourceUrl() throws Exception {
		String resourceUrl = "https://a.next.westlaw.com/Document/Iff5a5aaa7c8f11da9de6e47d6d5aa7a5/View/FullText.html?transitionType=Default&contextData=(sc.Default)";
		String documentUuid = internalLinkResolverFilter.getDocumentUuid(resourceUrl);
		String expectedUuid = "Iff5a5aaa7c8f11da9de6e47d6d5aa7a5";
		Assert.assertTrue(expectedUuid.equals(documentUuid));
		resourceUrl = "https://a.next.westlaw.com/Document/Iff5a5aaa7c-8f11da9de6e-47d6d5aa7a5/View/FullText.html?transitionType=Default&contextData=(sc.Default)";
		documentUuid = internalLinkResolverFilter.getDocumentUuid(resourceUrl);
		expectedUuid = "Iff5a5aaa7c-8f11da9de6e-47d6d5aa7a5";
		Assert.assertTrue(expectedUuid.equals(documentUuid));
	}
	
	@Test
	public void testIsDocumentUrl() throws Exception {
		String resourceUrl = "https://a.next.westlaw.com/Document/Iff5a5aaa7c8f11da9de6e47d6d5aa7a5/View/FullText.html?transitionType=Default&contextData=(sc.Default)";
		boolean isDocumentUrl = internalLinkResolverFilter.isDocumentUrl(resourceUrl);
		Assert.assertTrue(isDocumentUrl);
		resourceUrl = "https://a.next.westlaw.com/Document/Iff5a5aaa7c-8f11da9de6e-47d6d5aa7a5/View/FullText.html?transitionType=Default&contextData=(sc.Default)";
		isDocumentUrl = internalLinkResolverFilter.isDocumentUrl(resourceUrl);
		Assert.assertTrue(isDocumentUrl);
		resourceUrl = "https://a.next.westlaw.com/Document/YARR_PIRATES/View/FullText.html?transitionType=Default&contextData=(sc.Default)";
		isDocumentUrl = internalLinkResolverFilter.isDocumentUrl(resourceUrl);
		Assert.assertFalse(isDocumentUrl);
	}
	
	@Test
	public void testGetSerialNumberFromResourceUrl() throws Exception {
		String resourceUrl = "https://a.next.westlaw.com/Document/FullText?findType=Y&amp;serNum=123456&amp;transitionType=Default&contextData=(sc.Default)";
		String serialNumber = internalLinkResolverFilter.getSerialNumber(resourceUrl);
		String expectedSerialNumber = "123456";
		Assert.assertTrue(expectedSerialNumber.equals(serialNumber));
	}
	
	@Test
	public void testGetNormalizedCiteFromResourceUrl() throws Exception {
		String resourceUrl = "https://1.next.westlaw.com/Link/Document/FullText?findType=Y&amp;pubNum=119616&amp;cite=SECOPINIONs39%3A7&amp;originationContext=ebook";
		String normalizedCite = internalLinkResolverFilter.getNormalizedCite(resourceUrl);
		String expectedNormalizedCite = "SECOPINIONs39:7";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
		
		resourceUrl = "https://1.next.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000600&amp;cite=USFRCPR20&amp;originatingDoc=I86827039c15111ddb9c7909664ff7808&amp;refType=LQ&amp;originationContext=ebook";
		normalizedCite = internalLinkResolverFilter.getNormalizedCite(resourceUrl);
		System.out.println(normalizedCite);
		expectedNormalizedCite = "USFRCPR20";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
	}
	
	@Test
	public void testGetNormalizedCiteDocumentUuidFromResourceUrl() throws Exception {
		String resourceUrl = "https://1.next.westlaw.com/Link/Document/FullText?findType=l&amp;pubNum=1077005&amp;cite=UUID%28ID4D58042D3-43461C8C9EE-73AA2A319F3%29&amp;originationContext=ebook";
		String normalizedCite = internalLinkResolverFilter.getNormalizedCite(resourceUrl);
		String expectedNormalizedCite = "UUID(ID4D58042D3-43461C8C9EE-73AA2A319F3)";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));		
		
		String documentUuid = internalLinkResolverFilter.getNormalizedCiteUuid(resourceUrl);
		expectedNormalizedCite= "ID4D58042D3-43461C8C9EE-73AA2A319F3";
		Assert.assertTrue(expectedNormalizedCite.equals(documentUuid));
		
	}
}
