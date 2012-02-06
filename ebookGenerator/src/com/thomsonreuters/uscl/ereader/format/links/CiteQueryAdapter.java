/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.links;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import com.sun.org.apache.xpath.internal.NodeSet;

/**
 * This class serves as an adapter to ensure that any calls to GetCiteQueryLink, during the xslt transformation process, return a Persistent MUD URL.
 * 
 * This class is a Java port of the .NET CiteQueryExtension.cs object and performs the same logical operations.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class CiteQueryAdapter {
	//CiteQuery citeQuery = null;
	//UrlBuilder urlBuilder = null;
	
	public CiteQueryAdapter() throws Exception {
		//this.citeQuery = new CiteQuery(Container.DEFAULT.name());
	}
	
//	public CiteQueryAdapter(UrlBuilder urlBuilder) throws Exception {
//		this.citeQuery = new CiteQuery(Container.DEFAULT.name());
//		this.urlBuilder = urlBuilder;
//	}
	
	//CiteQuery:GetCiteQueryLink($citeQueryElement, $Guid, '', $sourceCite, 'originationContext=&docDisplayOriginationContext;', $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentItem;')
    public String getCiteQueryLink(NodeIterator link, NodeIterator originatingDoc, String keyText, DocumentFragment sourceCiteXpath, String originationContext, DocumentFragment specialVersionParamVariable, DocumentFragment specialRequestSourceParamValue, String transitionTypeParamValue)
    {
    	Node linkElement = link.nextNode();
    	Node originatingDocGuid = originatingDoc.nextNode();
    	String sourceCite = sourceCiteXpath.getNodeValue();
        return this.getCiteQueryLinkIntermediary(linkElement.getLocalName(), originatingDocGuid.getNodeValue(), keyText, sourceCite, "EBOOK", "VR=2.0", "RS=TRAN3.0");
    }

    //[ExpressionContext,] #STRING, #STRING, #RTREEFRAG, #RTREEFRAG, #STRING
    public String getCiteQueryLink(String link, String originationContext, DocumentFragment specialVersionParamVariable, DocumentFragment specialRequestSourceParamValue, String transitionTypeParamValue)
    {
        return this.getCiteQueryLinkIntermediary(link, "EBOOK", "VR=2.0", "RS=TRAN3.0");
    }
    
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

    public String GetCiteQueryLink(String linkElement, String originatingDoc, String keyText, String sourceCite, String param1, String param2, String param3, String param4, String param5)
    {
        return this.getCiteQueryLinkIntermediary(linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3, param4, param5);
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
//            UrlBuilderInput input = this.citeQuery.getCiteQueryLink(linkElement, originatingDoc, keyText, sourceCite);
//            if (input == null)
//            {
//                return "";
//            }

            // Add extra params
            //List<String> params = this.urlBuilder.getUrlParameters(parameters);
//            for (Parameter parameter : parameterList)
//            {
//                input.getParameters().add(parameter);
//            }

            String response = null;
//            try
//            {
//                //response = this.urlBuilder.createUrl(Container.DEFAULT.name(), input.getUrlTemplateName(), parameterList);
//            }
//            catch (UrlBuilderException e)
//            {
//                response = "";
//            }
            return response;
        }
        catch (Exception e)
        {
            return "";
        }
    }
}
