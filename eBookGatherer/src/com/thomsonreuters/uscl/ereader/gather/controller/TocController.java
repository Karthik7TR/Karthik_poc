/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.thomsonreuters.uscl.ereader.gather.domain.EbookRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.DocService;
import com.thomsonreuters.uscl.ereader.gather.services.TocService;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.util.EBookTocXmlHelper;

@Controller
public class TocController {

	@Autowired
	public TocService tocService;

	@Autowired
	public DocService docService;

	@Autowired
	public EBookTocXmlHelper eBookTocXmlHelper;

	@RequestMapping(value = "/tocDummy", method = RequestMethod.POST)
	public ModelAndView getTocDataDummy(HttpServletRequest request,@RequestBody GatherTocRequest gatherTocRequest,
			    Model model)
	{
		System.out.println("In getTocData method with out ...");
		GatherResponse gatherResponse = new GatherResponse();
		model.addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, gatherResponse);
		return new ModelAndView(EBConstants.VIEW_RESPONSE );

	}

	/**
	 * This method assist in processing form data ,
	 * 
	 * @param ebookRequest
	 * @param result
	 * @return
	 */

	@RequestMapping(value = "/toc", method = RequestMethod.POST)
	public ModelAndView getTocData(HttpServletRequest request,@RequestBody GatherTocRequest gatherTocRequest,
			    Model model)
	{
		GatherResponse gatherResponse = new GatherResponse();
		
		List<EBookToc> eBookTocList = new ArrayList<EBookToc>();
		
		
		/*** retrieve TOC structure form Novus ***/
		try {
			
			eBookTocList = tocService.getTocDataFromNovus(gatherTocRequest.getGuid(), gatherTocRequest.getCollectionName());
			
		} catch (GatherException e1) {
			/**
			 * TODO: communicate exception to response 1) Toc not found 2)
			 * Failed to retrieve TOC child element.
			 */
			//System.out.println("Exception in controller ="+e1);
			gatherResponse = new GatherResponse(GatherResponse.CODE_NOVUS_ERROR,e1.getMessage());
			e1.printStackTrace();

		} 

		
		
		/*** Create EBook TOC file on specified path  ***/
		try {

			File tocXmlFile = new File(gatherTocRequest.getDestinationDirectory(),EBConstants.TOC_XML_BASE_NAME);
			eBookTocXmlHelper.processTocListToCreateEBookTOC(eBookTocList,tocXmlFile);
			
		} catch (GatherException e) {
			/**
			 * TODO communicate exception to response 1)Failed to create DOM
			 * object ... 2)Failed while printing DOM to specified path 3) Failed to find file path...
			 */
			System.out.println("Exception in controller ="+e);
			e.printStackTrace();
			gatherResponse = new GatherResponse(GatherResponse.CODE_FILE_ERROR,e.getMessage());
			

		}
		
		model.addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, gatherResponse);
		 
		
		
		return new ModelAndView(EBConstants.VIEW_RESPONSE );

	}

//	/**
//	 * creates dynamic destination path.
//	 * @param jobId
//	 * @param titleId
//	 * @return
//	 */
//	private static File createTocDestinationFile(int jobId, String titleId,Date jobStartDate,String rootDir) {
//
//		String filePath = String.format("%s/%s/%s/%d/Gather/toc/%s", rootDir, getFormatedDate("yyyyMMdd",jobStartDate),titleId,jobId,EBConstants.TOC_XML_BASE_NAME);
//	
//		return new File(filePath);
//	}


}
