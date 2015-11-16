package com.thomsonreuters.uscl.ereader.gather.util;

import java.awt.image.RenderedImage;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codec.SeekableStream;

public class ImageConverter {	
	
	private static final String TIFF_FORMAT = "tiff";
	private static final Logger Log = Logger.getLogger(ImageConverter.class);
	
	public static void convertByteImg(byte[] imgBytes,String outputImagePath, String formatName) throws Exception {
		
		FileOutputStream outputStream = null;
		SeekableStream stream = null;
        try {
        	stream = new ByteArraySeekableStream(imgBytes);
            ImageDecoder decoder = ImageCodec.createImageDecoder(TIFF_FORMAT, stream, null);
            RenderedImage renderedImage = decoder.decodeAsRenderedImage(0);
            outputStream = new FileOutputStream(outputImagePath);
        	
        	PNGEncodeParam  param =   PNGEncodeParam.getDefaultEncodeParam(renderedImage);        	 
        	ImageEncoder   encoder=   ImageCodec.createImageEncoder(formatName, outputStream, param); 
        	 
        	encoder.encode(renderedImage);
        	
        	
        }
        catch (java.io.IOException ioe) {
        	Log.error("IOException at ImageConverter "+ioe);
            throw new Exception(ioe);
        } 
        catch (Exception ex) {
        	Log.error("IOException at ImageConverter "+ex);
            throw new Exception(ex);
        }
        finally{
        	if(outputStream != null){
        	outputStream.close();
        	}
        	if(stream != null){
        		stream.close();
            }
        }
	}
	

}