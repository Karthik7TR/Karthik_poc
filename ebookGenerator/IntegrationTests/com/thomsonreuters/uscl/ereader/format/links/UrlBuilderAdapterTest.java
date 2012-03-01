package com.thomsonreuters.uscl.ereader.format.links;

import org.junit.Before;
import org.junit.Test;


public class UrlBuilderAdapterTest
{
    UrlBuilderAdapter urlBuilderAdapter;

    @Before
    public void setUp() throws Exception
    {
        urlBuilderAdapter = new UrlBuilderAdapter();
    }

    @Test
    public void testDocketBlob()
    {
        //UrlBuilder:CreatePersistentUrl('Page.Document', concat('guid=',$documentGuid), 'viewType=FullText', 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
        String template = "Page.DocketBlob";
        String param1 = "platform=";
        String param2 = "court=";
        String param3 = "id=";
        String param4 = "filename=";
        String param5 = "courtnumber=";
        String param6 = "casenumber=";
        String param7 = "originationContext=document";
        String param8 = "";
        String param9 = "";
        String param10 = "transitionType=DocumentImage";

        urlBuilderAdapter.CreatePersistentUrl(
            template, param1, param2, param3, param4, param5, param6, param7, param8, param9,
            param10);
    }

    @Test
    public void testDocketsPdfBatchDownload()
    {
        String template = "Page.DocketsPdfBatchDownload";
        String param1 = "docGuid=Ie043fac0675a11da90ebf04471783734";
        String param2 = "pdfIndex=";
        String param3 = "checkSum=";
        String param4 = "docPersistId=";
        String param5 = "originationContext=document";
        String param6 = "transitionType=DocumentImage";
        String param7 = "";
        String param8 = "";
        urlBuilderAdapter.CreatePersistentUrl(
            template, param1, param2, param3, param4, param5, param6, param7, param8);
    }

    @Test
    public void testDocument()
    {
        //UrlBuilder:CreatePersistentUrl('Page.Document', concat('guid=',$documentGuid), 'viewType=FullText', 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
        String template = "Page.Document";
        String param1 = "guid=Ie043fac0675a11da90ebf04471783734";
        String param2 = "viewType=FullText";
        String param3 = "originationContext=document";
        String param4 = "transitionType=DocumentImage";
        String param5 = "";
        String param6 = "";
        urlBuilderAdapter.CreatePersistentUrl(
            template, param1, param2, param3, param4, param5, param6);
    }

    @Test
    public void testDocumentByLookup()
    {
        String template = "Page.DocumentByLookup";
        String param1 = "findType=Ie043fac0675a11da90ebf04471783734";
        String param2 = "serNum=";
        String param3 = "pubNum=";
        String param4 = "cite=";
        String param5 = "viewType=FullText";
        String param6 = "originationContext=document";
        String param7 = "";
        String param8 = "";
        String param9 = "transitionType=DocumentImage";
        urlBuilderAdapter.CreatePersistentUrl(
            template, param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    @Test
    public void testDocumentHighResolutionBlob()
    {
        //UrlBuilder:CreateRelativePersistentUrl('Page.DocumentHighResolutionBlob', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs))
        String template = "Page.DocumentHighResolutionBlob";
        String param1 = "imageGuid=Ie043fac0675a11da90ebf04471783734";
        String param2 = "extension=jpg";
        String param3 = "maxHeight=";
        String param4 = "targetType= ";
        String param5 = "originationContext=document";
        String param6 = "";
        String param7 = "";
        String param8 = "transitionType=DocumentImage";
        String param9 = "uniqueIdParamName=";
        urlBuilderAdapter.CreateRelativePersistentUrl(
            template, param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    @Test
    public void testDocumentOrderReview()
    {
        String template = "Page.DocumentOrderReview";
        String param1 = "docGuid=Ie043fac0675a11da90ebf04471783734";
        String param2 = "orderedIndex=";
        String param3 = "originationContext=document";
        String param4 = "transitionType=DocumentImage";
        String param5 = "";
        String param6 = "";
        urlBuilderAdapter.CreatePersistentUrl(
            template, param1, param2, param3, param4, param5, param6);
    }

    @Test
    public void testImageMetadataBlock()
    {
        System.out.println(urlBuilderAdapter);

        String template = "Page.DocumentBlobV1";
        String param1 = "imageGuid= Ie043fac0675a11da90ebf04471783734";
        String param2 = "extension=jpg";
        String param3 = "maxHeight=";
        String param4 = "targetType= ";
        String param5 = "originationContext=document";
        String param6 = "";
        String param7 = "";
        String param8 = "transitionType=DocumentImage";

        urlBuilderAdapter.CreatePersistentUrl(
            template, param1, param2, param3, param4, param5, param6, param7, param8);
    }

    @Test
    public void testKeyciteFlagUrl()
    {
        String template = "Page.KeyCiteFlagHistoryByLookup";
        String param1 = "docGuid=Ie043fac0675a11da90ebf04471783734";
        String param2 = "originationContext=document";
        String param3 = "";
        String param4 = "";
        urlBuilderAdapter.CreatePersistentUrl(template, param1, param2, param3, param4);
    }

    @Test
    public void testPdfImageMetadataBlock()
    {
        String template = "Page.DocumentBlobV1";
        String param1 = "imageGuid= Ie043fac0675a11da90ebf04471783734";
        String param2 = "extension=pdf";
        String param3 = "maxHeight=";
        String param4 = "targetType= ";
        String param5 = "originationContext=document";
        String param6 = "";
        String param7 = "";
        String param8 = "transitionType=DocumentImage";
        String param9 = "uniqueId=";

        urlBuilderAdapter.CreatePersistentUrl(
            template, param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    @Test
    public void testRedirectToExpertQA()
    {
        String template = "Page.RedirectToExpertQA";
        String param1 = "query=";
        String param2 = "originationContext=document";
        String param3 = "transitionType=DocumentImage";
        String param4 = "";
        String param5 = "";
        urlBuilderAdapter.CreatePersistentUrl(template, param1, param2, param3, param4, param5);
    }
}
