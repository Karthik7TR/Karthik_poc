package com.thomsonreuters.uscl.ereader.gather.util;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.SeekableStream;

public class ImageConverter {	

	private static final Logger Log = Logger.getLogger(ImageConverter.class);
	
	public static void convertByteImg(byte[] imgBytes,String outputImagePath, String formatName) throws Exception {
		
		FileOutputStream outputStream = null;
		SeekableStream stream = null;
        try {
        	stream = new ByteArraySeekableStream(imgBytes);
        	outputStream = new FileOutputStream(outputImagePath);
        	BufferedImage image = ImageIO.read(stream);
        	ImageIO.write(image, formatName, outputStream);
        }
        catch (IOException ioe) {
        	Log.error("IOException at ImageConverter " + ioe);
            throw new IOException(ioe);
        } 
        catch (Exception ex) {
        	Log.error("Exception at ImageConverter " + ex);
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