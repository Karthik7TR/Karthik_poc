/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.image.service.play;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageModule
{
    protected static final String HOST = "http://imageservice.westlan.com/image/v1/images";
    protected static final String UTF8 = "UTF-8";
    
    public Integer getImageMeta(String ttype, String guid) throws Exception
    {
        int retVal = 0;     
    
        String metaUrlString = HOST + "/ttype/" + ttype + "/guid/" + guid + "/meta";
        
        String jsonResponse = doGetString(metaUrlString);
        
        System.out.println(jsonResponse);
        System.out.println();
        
        String metaHeight = getJsonVal(jsonResponse, "Height");
        String metaWidth = getJsonVal(jsonResponse, "Width");
        String metaDpi = getJsonVal(jsonResponse, "DPI");
        String metaMimeType = getJsonVal(jsonResponse, "MimeType");
        String metaDimensionUnit = getJsonVal(jsonResponse, "DimensionUnit");
        String metaByteCount = getJsonVal(jsonResponse, "ByteCount");        
        
        System.out.println("Image Height = " + metaHeight);
        System.out.println("Image Width = " + metaWidth);
        System.out.println("Image DPI = " + metaDpi);
        System.out.println("Image Mimetype = " + metaMimeType);
        System.out.println("Image Dimension Unit = " + metaDimensionUnit);
        System.out.println("Image Byte Count = " + metaByteCount);
        
        if (metaByteCount != null)
        {
            retVal = Integer.parseInt(metaByteCount);
        }
            
        return retVal;
    }
    
    public void saveImageData(String ttype, String guid, String fileName) throws Exception
    {
        String urlString = HOST + "/ttype/" + ttype + "/guid/" + guid;
        
        byte[] imageByteArray = doGet(urlString);
        
        byteArrayToFile(imageByteArray, new File(fileName));
    }

    
    public static void byteArrayToFile(byte[] byteArray, File file) throws Exception
    {
        java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
        fos.write(byteArray);
        fos.close();
    }  
    

    protected static String doGetString(String urlString) throws Exception
    {
        return new String(doPost(urlString, null, false), UTF8);
    }
    
    protected static String doPostString(String urlString) throws Exception
    {
        return new String(doPost(urlString, null, false), UTF8);
    }
    
    protected static byte[] doGet(String urlString) throws Exception
    {
        return doPost(urlString, null, false);
    }

    protected static byte[] doPost(String urlString, String postInput) throws Exception
    {
        return doPost(urlString, postInput, true);
    }

    protected static byte[] doPost(String urlString, String postInput, boolean isPost) throws Exception
    {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        
        if (isPost)
        {
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(postInput);
            out.close();
        }

        InputStream is = null;
        ByteArrayOutputStream baos = null;
        byte[] retVal;
        
        try
        {
            is = connection.getInputStream();
            baos = new ByteArrayOutputStream();
            
            byte[] data = new byte[2056];
            int bytesRead = is.read(data);
            while (bytesRead > -1)
            {
                baos.write(data, 0, bytesRead);
                bytesRead = is.read(data);
            }
            
            retVal = baos.toByteArray();
        }
        finally
        {
            if (is != null)
                is.close();
            if (baos != null)
                baos.close();
        }
        
        return retVal;

    }
    
    
    /**
     * Simple JSON scanner that just gets a value associated with a 
     * key.  For better results, use an actual JSON parser like Jackson
     * @param json - Json to read.
     * @param key - the key of the value to retrieve
     * @return - the value of the key given.  Null if key not found.
     */
    private static String getJsonVal(String json, String key) {
       Pattern p = Pattern.compile("\"" + key + "\":([^,}]*)", Pattern.CASE_INSENSITIVE);
       Matcher m = p.matcher(json);

       if (m.find()) {
          String retVal = m.group(1);
        
          if (retVal.startsWith("\"") && retVal.endsWith("\"")) {
             retVal = retVal.substring(1, retVal.length() - 1);
          }

          return retVal;
       }

       return null;
    }
    
}
