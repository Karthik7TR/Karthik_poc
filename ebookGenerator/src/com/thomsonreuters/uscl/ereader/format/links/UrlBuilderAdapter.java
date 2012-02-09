/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.links;

import com.trgr.cobalt.util.urlbuilder.UrlBuilder;
import com.trgr.cobalt.util.urlbuilder.UrlBuilderException;

/**
 * Instances of this class are invoked by Xalan as a result of executing the WLN stylesheets.
 * 
 * <p>Calls to instances of this class will delegate to a pre-configured Cobalt URLBuilder.</p>
 * <p>This class is a direct port of the .NET implementation of UrlBuilderExtension.cs with minor tweaks (the Cobalt URLBuilder is a class-scoped field in our implementation).</p>
 * <p><em>Methods implemented in this class match, exactly, the methods as declared in the WLN XSL Stylesheets. 
 * We <strong>deliberately</strong> broke from Java method naming conventions due to the requirement to be compatible
 *  with the WLN XSL Stylesheets (capitalized public method names are a good example).</em></p>
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class UrlBuilderAdapter {

	private static UrlBuilder URL_BUILDER;
	
	public UrlBuilderAdapter(){		
	}
	
	/**
	 * Sets the static UrlBuilder instance to be shared by all UrlBuilderAdapters.
	 * @param urlBuilder the UrlBuilder to set.
	 */
	public void setUrlBuilder(UrlBuilder urlBuilder){
		UrlBuilderAdapter.URL_BUILDER = urlBuilder;
	}
	
	
	public String CreatePersistentUrl(String input, String param1)
    {
        return this.persistentIntermediary(input, param1);
    }

    public String CreatePersistentUrl(String input, String param1, String param2)
    {
        return this.persistentIntermediary(input, param1, param2);
    }

    public String CreatePersistentUrl(String input, String param1, String param2, String param3)
    {
        return this.persistentIntermediary(input, param1, param2, param3);
    }

    public String CreatePersistentUrl(String input, String param1, String param2, String param3, String param4)
    {
        return this.persistentIntermediary(input, param1, param2, param3, param4);
    }

    public String CreatePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5)
    {
        return this.persistentIntermediary(input, param1, param2, param3, param4, param5);
    }

    public String CreatePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6)
    {
        return this.persistentIntermediary(input, param1, param2, param3, param4, param5, param6);
    }

    public String CreatePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7)
    {
        return this.persistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7);
    }

    public String CreatePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8)
    {
        return this.persistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7, param8);
    }

    public String CreatePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9)
    {
        return this.persistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    public String CreatePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9, String param10)
    {
        return this.persistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10);
    }

	public String CreatePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9, String param10, String param11)
	{
		return this.persistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11);
	}

	public String CreatePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9, String param10, String param11, String param12)
	{
		return this.persistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12);
	}

	private String persistentIntermediary(String input, String... parameters)
	{
		String response = null;
		try
		{
			//TODO: Determine how to map to UrlBuilder calls.
			//response = URL_BUILDER.createPersistentUrl(input, parameters);
		}
		catch (UrlBuilderException e)
		{
			response = "";
		}
		return response;
	}

	public String CreateRelativePersistentUrl(String input, String param1)
	{
		return this.relativePersistentIntermediary(input, param1);
	}

	public String CreateRelativePersistentUrl(String input, String param1, String param2)
	{
		return this.relativePersistentIntermediary(input, param1, param2);
	}

	public String CreateRelativePersistentUrl(String input, String param1, String param2, String param3)
	{
		return this.relativePersistentIntermediary(input, param1, param2, param3);
	}

	public String CreateRelativePersistentUrl(String input, String param1, String param2, String param3, String param4)
	{
		return this.relativePersistentIntermediary(input, param1, param2, param3, param4);
	}

	public String CreateRelativePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5)
	{
		return this.relativePersistentIntermediary(input, param1, param2, param3, param4, param5);
	}

	public String CreateRelativePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6)
	{
		return this.relativePersistentIntermediary(input, param1, param2, param3, param4, param5, param6);
	}

	public String CreateRelativePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7)
	{
		return this.relativePersistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7);
	}

	public String CreateRelativePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8)
	{
		return this.relativePersistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7, param8);
	}

	public String CreateRelativePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9)
	{
		return this.relativePersistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7, param8, param9);
	}

	public String CreateRelativePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9, String param10)
	{
		return this.relativePersistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10);
	}

	public String CreateRelativePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9, String param10, String param11)
	{
		return this.relativePersistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11);
	}

	public String CreateRelativePersistentUrl(String input, String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8, String param9, String param10, String param11, String param12)
	{
		return this.relativePersistentIntermediary(input, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12);
	}

    private String relativePersistentIntermediary(String input, String... parameters)
    {
        String response = null;
        try
        {
        	//TODO: Determine how to map to UrlBuilder calls.
            //response = URL_BUILDER.createRelativePersistentUrl(input, parameters);
        }
        catch (UrlBuilderException e)
        {
            response = "";
        }
        return response;
    }
	
}
