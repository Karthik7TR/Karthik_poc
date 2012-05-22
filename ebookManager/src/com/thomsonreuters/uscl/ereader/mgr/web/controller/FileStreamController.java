package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

/**
 * Controller used to stream files from NAS location to the web application.
 */
@Controller
public class FileStreamController {
	private static final Logger log = Logger.getLogger(AppExceptionController.class);
	
	@RequestMapping(value=WebConstants.MVC_COVER_IMAGE, method = RequestMethod.GET)
	public void getCoverImage(@RequestParam String imageName, HttpServletRequest request, HttpServletResponse response) {

		retrieveFile(request, response, WebConstants.LOCATION_KEY_COVER_IMAGE, imageName, MediaType.IMAGE_PNG);
	}
	
	@RequestMapping(value=WebConstants.MVC_FRONT_MATTER_IMAGE, method = RequestMethod.GET)
	public void getFrontMatterImage(@RequestParam String imageName, HttpServletRequest request, HttpServletResponse response) {
		
		retrieveFile(request, response, WebConstants.LOCATION_FRONT_MATTER_IMAGE, imageName, MediaType.IMAGE_PNG);
	}
	
	@RequestMapping(value=WebConstants.MVC_FRONT_MATTER_CSS, method = RequestMethod.GET)
	public void getFrontMatterCss(@RequestParam String cssName, HttpServletRequest request, HttpServletResponse response) {

		retrieveFile(request, response, WebConstants.LOCATION_FRONT_MATTER_CSS, cssName, MediaType.TEXT_HTML);
	}
	
	private void retrieveFile(HttpServletRequest request, HttpServletResponse response, String nasLocation,String filename, MediaType mediaType) {
		InputStream fin = null;
		byte[] content = null;
		
		File file = new File(nasLocation, filename);
		
		try{
			if(!file.isFile() && nasLocation.equalsIgnoreCase(WebConstants.LOCATION_KEY_COVER_IMAGE)) {
				ServletContext ctx = request.getSession().getServletContext();
				fin = ctx.getResourceAsStream("/theme/images/missingCover.png");
			} else {
				fin = new FileInputStream(file);
			}

			content = IOUtils.toByteArray(fin);
			response.setContentType(mediaType.toString());
			response.setContentLength(content.length);
			
			ServletOutputStream out = response.getOutputStream();
			out.write(content);
			out.flush();
		} catch(Exception e) {
			log.error("Error streaming file: ",e);
		} finally {
			try {
				if(fin != null) {
					fin.close();
				}
			} catch (Exception e) {
				log.error("Error closing input stream: ",e);
			}
		}
	}

}
