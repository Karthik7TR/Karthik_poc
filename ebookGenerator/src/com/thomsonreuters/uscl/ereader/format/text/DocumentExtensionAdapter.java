/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.format.text;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

/**
 * This class serves as an adapter to ensure that any calls to DocumentExtension, during the
 * xslt transformation process, return a String.  This class is a Java port of the
 * .NET DocumentXslExtension.cs object and performs the same logical operations.
 *
 */
public class DocumentExtensionAdapter
{
    private static final Logger LOG = Logger.getLogger(DocumentExtensionAdapter.class);


    public DocumentExtensionAdapter() throws Exception
    {}

    /**
     * Returns the default text.
     * @param context
     * @param key
     * @param defaultText
     * @return
     */
    public String RetrieveContextValue(String context, String key, String defaultText)
    {
    	return defaultText;
    }
    
    /**
     * Returns the default text.
     * @param context
     * @param key
     * @param defaultText
     * @return
     */
    public String RetrieveLocaleValue(String language, String context, String key, String defaultText)
    {
    	return defaultText;
    }
    
	/**
	 *  Generates hash for Sponsor ID.
	 * @param sponsorId
	 * @param documentGuid
	 * @return Hash for use with sponsor.
	 */
    public String GenerateSponsorHash(String sponsorId, String documentGuid)
	{
		if (StringUtils.isEmpty(sponsorId) || StringUtils.isEmpty(documentGuid))
		{
			return null;
		}
		
		String str = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(sponsorId.getBytes("UTF-8"), 0, sponsorId.length());
			byte[] hashedSponsor  = md.digest();
			
			SecretKeySpec localMac = new SecretKeySpec(hashedSponsor, "HmacSHA256");
			Mac hmacSha256 = Mac.getInstance("HmacSHA256");
			hmacSha256.init(localMac);
			byte[] hmac = hmacSha256.doFinal(documentGuid.getBytes("UTF-8"));
			str = DatatypeConverter.printHexBinary(hmac);
			
		} catch (NoSuchAlgorithmException e) {
			LOG.debug(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			LOG.debug(e.getMessage());
		} catch (InvalidKeyException e) {
			LOG.debug(e.getMessage());
		}
		
		return str;
	}
    
    /**
     * 
     * @param inputText - The text that we want to check for encoding issues
     * @return The encoded text.
     */
	public String ToXmlEncodedString(String inputText)
	{
		if (inputText == null)
		{
			return null;
		}

		StringBuilder outputText = new StringBuilder();
		char c = ' ';
		for (int i = 0; i < inputText.length(); i++)
		{
			c = inputText.charAt(i);
			if ((c == '\u00C2') || (c == '\u00E2') || (c == '\u20AC'))
			{
				outputText.append("");
			}
			else if (c == '\u201A')
			{
				outputText.append(" ");
			}
			else if (c == '\u2002')
			{
				outputText.append(" ");
			}
			else
			{
				outputText.append(c);
			}
		}
		return outputText.toString();
	}
	
	/**
	 * IsMatch - simple regular expression match
	 * @param input string to search
	 * @param pattern regex pattern to match
	 * @return
	 */
	public boolean IsMatch(String input, String pattern)
	{
		Validate.notEmpty(pattern);

		// Must support null/empty inputs, but they don't count as a match
		if (StringUtils.isEmpty(input))
		{
			return false;
		}

		return input.matches(pattern);
	}
	
	
	/// <summary>
	/// Applies TOC capitalization rules to the <paramref name="tocNodeName"/> string
	/// </summary>
	/// <param name="tocNodeName">A string to apply capitalization rules to</param>
	/// <returns><paramref name="tocNodeName"/> after applying capitalization rules</returns>
	public String TocToTitleCase(String tocNodeName)
	{
		return WordUtils.capitalize(tocNodeName);
	}
}
