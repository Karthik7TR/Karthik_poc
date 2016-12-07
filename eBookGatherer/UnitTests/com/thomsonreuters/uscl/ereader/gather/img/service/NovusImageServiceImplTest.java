/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.service;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import com.thomsonreuters.uscl.ereader.gather.img.util.DocToImageManifestUtil;

@SuppressWarnings("null")
@RunWith(MockitoJUnitRunner.class)
public class NovusImageServiceImplTest {
	@InjectMocks
	private NovusImageServiceImpl service;
	@Mock
	private DocToImageManifestUtil docUtil;
	@Mock
	private NovusImageProcessor processor;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void shouldProcessEveryImageOnce() throws Exception {
		// given
		Map<String, List<String>> docsMap = new HashMap<>();
		docsMap.put("docId", asList("image1", "image2"));

		given(docUtil.getDocsWithImages(any(File.class))).willReturn(docsMap);
		given(processor.isProcessed("image2")).willReturn(true);
		// when
		GatherResponse imagesFromNovus = service.getImagesFromNovus(new ImageRequestParameters());
		// then
		assertThat(imagesFromNovus, not(nullValue()));
		then(processor).should().process(eq("image1"), anyString());
		then(processor).should(never()).process(eq("image2"), anyString());
		then(processor).should().close();
	}

	@Test
	public void shouldThrowExceptionIfFailsToProcess() throws Exception {
		// given
		thrown.expect(GatherException.class);
		thrown.expectMessage("Cannot process images from Novus");
		
		Map<String, List<String>> docsMap = new HashMap<>();
		docsMap.put("docId", asList("image1", "image2"));

		given(docUtil.getDocsWithImages(any(File.class))).willReturn(docsMap);
		doThrow(new RuntimeException()).when(processor).isProcessed(anyString());
		// when
		service.getImagesFromNovus(new ImageRequestParameters());
		// then
		then(processor).should().close();
	}

}
