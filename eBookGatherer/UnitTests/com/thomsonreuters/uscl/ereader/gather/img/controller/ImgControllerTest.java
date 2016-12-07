/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doThrow;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import com.thomsonreuters.uscl.ereader.gather.img.service.NovusImageService;

@SuppressWarnings("null")
@RunWith(MockitoJUnitRunner.class)
public class ImgControllerTest {
	@InjectMocks
	private ImgController controller;
	@Mock
	private NovusImageService novusImageService;
	@Mock
	private ImageRequestParameters parameters;
	@Mock
	private Model model;

	@Test
	public void shouldReturnResponse() throws Exception {
		// given
		GatherResponse response = new GatherResponse();
		given(novusImageService.getImagesFromNovus(parameters)).willReturn(response);
		// when
		controller.fetchImages(new GatherImgRequest(), model);
		// then
		then(parameters).should().setDocToImageManifestFile(any(File.class));
		then(parameters).should().setDynamicImageDirectory(any(File.class));
		then(parameters).should().setFinalStage(anyBoolean());
		then(model).should().addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, response);
	}

	@Test
	public void shouldReturnEmptyResponseIfServiceFails() throws Exception {
		// given
		GatherResponse response = new GatherResponse();
		doThrow(new GatherException("")).when(novusImageService).getImagesFromNovus(parameters);
		// when
		controller.fetchImages(new GatherImgRequest(), model);
		// then
		then(model).should().addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, response);
	}

}
