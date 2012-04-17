package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

/**
 * Handles the web container throwing a bubbled-up application exception and displays
 * the exception stack trace on its own page.
 */
@Controller
public class ImageController {
	// private static final Logger log = Logger.getLogger(AppExceptionController.class);
	
	@RequestMapping(value=WebConstants.MVC_COVER_IMAGE, method = RequestMethod.GET)
	public ResponseEntity<byte[]> getCoverImage(@RequestParam String imageName, HttpServletRequest request) {
		InputStream fin = null;
		byte[] content = null;
		
		File image = new File(WebConstants.KEY_COVER_IMAGE_LOCATION, imageName);
		
		try{
			// Show missingCover image if file is not found
			if(!image.isFile()) {
				ServletContext ctx = request.getSession().getServletContext();
				fin = ctx.getResourceAsStream("/theme/images/missingCover.png");
			} else {
				fin = new FileInputStream(image);
			}
			content = IOUtils.toByteArray(fin);

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(fin != null) {
					fin.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_PNG);
		return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
	}

}
