/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

@SuppressWarnings("null")
@RunWith(MockitoJUnitRunner.class)
public class NovusImageMetadataParserImplTest {
	@InjectMocks
	private NovusImageMetadataParserImpl parser;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void shouldParseCorrect() throws Exception {
		// given
		File correct = new File(NovusImageMetadataParserImplTest.class.getResource("imageMetadata_correct.xml").toURI());
		String metadataStr = FileUtils.readFileToString(correct);
		// when
		ImgMetadataInfo metadataInfo = parser.parse(metadataStr);
		// then
		assertThat(metadataInfo.getWidth(), is(2402l));
	}
	
	@Test
	public void shouldThrowExceptionIfXmlIsIncorrect() throws Exception {
		// given
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("Cannot parse image metadata from Novus");
		File incorrect = new File(NovusImageMetadataParserImplTest.class.getResource("imageMetadata_incorrect.xml").toURI());
		String metadataStr = FileUtils.readFileToString(incorrect);
		// when
		parser.parse(metadataStr);
		// then
	}

}
