/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.util;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DocToImageManifestUtilImplTest {
	@InjectMocks
	private DocToImageManifestUtilImpl util;

	@Test
	public void shouldReadManifestCorrectly() throws Exception {
		// given
		File manifestFile = new File(DocToImageManifestUtilImplTest.class.getResource("docToImageManifest_correct.txt").toURI());
		// when
		Map<String, List<String>> docsWithImages = util.getDocsWithImages(manifestFile);
		// then
		assertThat(docsWithImages, hasEntry(is("N50612690E73511DD9B10B565B0929296"), contains("Id14b3db0f58911e28bd8c4c6dcfa6cd5")));
	}
	
	@Test
	public void shouldReturnEmptyMapIfNoImages() throws Exception {
		// given
		File manifestFile = new File(DocToImageManifestUtilImplTest.class.getResource("docToImageManifest_empty.txt").toURI());
		// when
		Map<String, List<String>> docsWithImages = util.getDocsWithImages(manifestFile);
		// then
		assertThat(docsWithImages.keySet(), empty());
	}
	
}
