/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.links;

import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.trgr.cobalt.util.urlbuilder.CiteQuery;
import com.trgr.cobalt.util.urlbuilder.Container;
import com.trgr.cobalt.util.urlbuilder.ContainerAwareUrlBuilderFactoryBean;
import com.trgr.cobalt.util.urlbuilder.Parameter;
import com.trgr.cobalt.util.urlbuilder.UrlBuilder;
import com.trgr.cobalt.util.urlbuilder.UrlBuilderException;
import com.trgr.cobalt.util.urlbuilder.UrlBuilderInput;

/**
 * This class serves as an adapter to ensure that any calls to GetCiteQueryLink, during the xslt transformation process, return a Persistent MUD URL.
 * 
 * This class is a Java port of the .NET CiteQueryExtension.cs object and performs the same logical operations.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class CiteQueryAdapter {
	
	private static final Logger LOG = Logger.getLogger(CiteQueryAdapter.class);
	CiteQuery citeQuery = null;
	UrlBuilder urlBuilder = null;
	private String hostname = "https://1.next.westlaw.com";
	
	public void setHostname(final String hostname){
		this.hostname = hostname; //injected by Spring
	}
	
	public CiteQueryAdapter() throws Exception {
		this.citeQuery = new CiteQuery(Container.COBALT.name());
		this.urlBuilder = new ContainerAwareUrlBuilderFactoryBean().getObject(); 
	}
	
	
    //[ExpressionContext,] #STRING, #STRING, #RTREEFRAG, #RTREEFRAG, #STRING
//    public String getCiteQueryLink(String link, String originationContext, DocumentFragment specialVersionParamVariable, DocumentFragment specialRequestSourceParamValue, String transitionTypeParamValue)
//    {
//        return this.getCiteQueryLinkIntermediary(link, "EBOOK", "VR=2.0", "RS=TRAN3.0");
//    }
    
	public String GetCiteQueryLink(String linkElement, String originatingDoc, String keyText, String sourceCite)
    {
        return this.getCiteQueryLinkIntermediary(linkElement, originatingDoc, keyText, sourceCite);
    }

    public String GetCiteQueryLink(String linkElement, String originatingDoc, String keyText, String sourceCite, String param1)
    {
        return this.getCiteQueryLinkIntermediary(linkElement, originatingDoc, keyText, sourceCite, param1);
    }

    public String GetCiteQueryLink(String linkElement, String originatingDoc, String keyText, String sourceCite, String param1, String param2)
    {
        return this.getCiteQueryLinkIntermediary(linkElement, originatingDoc, keyText, sourceCite, param1, param2);
    }

    public String GetCiteQueryLink(String linkElement, String originatingDoc, String keyText, String sourceCite, String param1, String param2, String param3)
    {
        return this.getCiteQueryLinkIntermediary(linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3);
    }

    public String GetCiteQueryLink(String linkElement, String originatingDoc, String keyText, String sourceCite, String param1, String param2, String param3, String param4)
    {
    	return this.getCiteQueryLinkIntermediary(linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3, param4);
    }
    
    public String GetCiteQueryLink(Node linkElement, String originatingDoc, String keyText, String sourceCite, String param1, String param2, String param3, String param4)
    {
    	return this.getCiteQueryLinkIntermediary(linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3, param4);
    }

    public String GetCiteQueryLink(String linkElement, String originatingDoc, String keyText, String sourceCite, String param1, String param2, String param3, String param4, String param5)
    {
    	return this.getCiteQueryLinkIntermediary(linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3, param4, param5);
    }

   
    
    private String getCiteQueryLinkIntermediary(Node linkElement, String originatingDoc, String keyText, String sourceCite, String... parameters)
    {
    	
    	try
        {
        	UrlBuilderInput input = this.citeQuery.getCiteQueryLink(linkElement, originatingDoc, keyText, sourceCite,"ebook");
            if (input == null)
            {
                return "";
            }
            //LOG.debug("Template name is " + input.getUrlTemplateName() + input.getParameters());

            String response = null;
            try
            {
                response = this.hostname + this.urlBuilder.createUrl(Container.COBALT.name(), input.getUrlTemplateName(), input.getParameters());
                System.out.println(response);
            }
            catch (UrlBuilderException e)
            {
                response = "";
                
                LOG.debug("UrlBuilderException is " + e.getMessage());
            }
            return response;
        }
        catch (Exception e)
        {
            return "";           
        }
    }
    
    /// <summary>
    /// Returns a String of the UrlBuilderInput object that contains the appropriate template Name and paramters for the given input.
    /// </summary>
    /// <param Name="linkElement">The String of the XML cite.query element</param>
    /// <param Name="originatingDoc">The guid of the doc hosting the cite.query element</param>
    /// <param Name="keyText">The keytext used for key number links</param>
    /// <param name="sourceCite">The sourceCite of the document, used for CM ref type.</param>
    /// <returns>UrlBuilderInput to pass to UrlBuilder</returns>
    private String getCiteQueryLinkIntermediary(String linkElement, String originatingDoc, String keyText, String sourceCite, String... parameters)
    {
    	
    	try
        {
        	System.out.println("Debug #1 "  + linkElement);
            UrlBuilderInput input = this.citeQuery.getCiteQueryLink(linkElement, originatingDoc, keyText, sourceCite);
            if (input == null)
            {
                return "";
            }
            
            StringBuilder parametersString = new StringBuilder();
            
            for(String str : parameters)
            {
            	parametersString.append(str);
            }

            // Add extra params
            List<Parameter> parameterList = this.urlBuilder.getUrlParameters(parametersString.toString());
            for (Parameter parameter : parameterList)
            {
                input.getParameters().add(parameter);
            }

            String response = null;
            try
            {
                response = this.urlBuilder.createUrl(Container.DEFAULT.name(), input.getUrlTemplateName(), parameterList);
            }
            catch (UrlBuilderException e)
            {
                response = "";
            }
            return response;
        }
        catch (Exception e)
        {
            return "";
        }
    }

}
