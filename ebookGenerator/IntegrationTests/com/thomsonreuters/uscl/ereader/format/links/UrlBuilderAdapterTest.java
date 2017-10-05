package com.thomsonreuters.uscl.ereader.format.links;

import org.junit.Before;
import org.junit.Test;

public final class UrlBuilderAdapterTest {
    private UrlBuilderAdapter urlBuilderAdapter;

    @Before
    public void setUp() throws Exception {
        urlBuilderAdapter = new UrlBuilderAdapter();
    }

    @Test
    public void testDocketBlob() {
        //UrlBuilder:CreatePersistentUrl('Page.Document', concat('guid=',$documentGuid), 'viewType=FullText', 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
        final String template = "Page.DocketBlob";
        final String param1 = "platform=";
        final String param2 = "court=";
        final String param3 = "id=";
        final String param4 = "filename=";
        final String param5 = "courtnumber=";
        final String param6 = "casenumber=";
        final String param7 = "originationContext=document";
        final String param8 = "";
        final String param9 = "";
        final String param10 = "transitionType=DocumentImage";

        urlBuilderAdapter.CreatePersistentUrl(
            template,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9,
            param10);
    }

    @Test
    public void testDocketsPdfBatchDownload() {
        final String template = "Page.DocketsPdfBatchDownload";
        final String param1 = "docGuid=Ie043fac0675a11da90ebf04471783734";
        final String param2 = "pdfIndex=";
        final String param3 = "checkSum=";
        final String param4 = "docPersistId=";
        final String param5 = "originationContext=document";
        final String param6 = "transitionType=DocumentImage";
        final String param7 = "";
        final String param8 = "";
        urlBuilderAdapter.CreatePersistentUrl(template, param1, param2, param3, param4, param5, param6, param7, param8);
    }

    @Test
    public void testDocument() {
        //UrlBuilder:CreatePersistentUrl('Page.Document', concat('guid=',$documentGuid), 'viewType=FullText', 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
        final String template = "Page.Document";
        final String param1 = "guid=Ie043fac0675a11da90ebf04471783734";
        final String param2 = "viewType=FullText";
        final String param3 = "originationContext=document";
        final String param4 = "transitionType=DocumentImage";
        final String param5 = "";
        final String param6 = "";
        urlBuilderAdapter.CreatePersistentUrl(template, param1, param2, param3, param4, param5, param6);
    }

    @Test
    public void testDocumentByLookup() {
        final String template = "Page.DocumentByLookup";
        final String param1 = "findType=Ie043fac0675a11da90ebf04471783734";
        final String param2 = "serNum=";
        final String param3 = "pubNum=";
        final String param4 = "cite=";
        final String param5 = "viewType=FullText";
        final String param6 = "originationContext=document";
        final String param7 = "";
        final String param8 = "";
        final String param9 = "transitionType=DocumentImage";
        urlBuilderAdapter
            .CreatePersistentUrl(template, param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    @Test
    public void testDocumentHighResolutionBlob() {
        //UrlBuilder:CreateRelativePersistentUrl('Page.DocumentHighResolutionBlob', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs))
        final String template = "Page.DocumentHighResolutionBlob";
        final String param1 = "imageGuid=Ie043fac0675a11da90ebf04471783734";
        final String param2 = "extension=jpg";
        final String param3 = "maxHeight=";
        final String param4 = "targetType= ";
        final String param5 = "originationContext=document";
        final String param6 = "";
        final String param7 = "";
        final String param8 = "transitionType=DocumentImage";
        final String param9 = "uniqueIdParamName=";
        urlBuilderAdapter.CreateRelativePersistentUrl(
            template,
            param1,
            param2,
            param3,
            param4,
            param5,
            param6,
            param7,
            param8,
            param9);
    }

    @Test
    public void testDocumentOrderReview() {
        final String template = "Page.DocumentOrderReview";
        final String param1 = "docGuid=Ie043fac0675a11da90ebf04471783734";
        final String param2 = "orderedIndex=";
        final String param3 = "originationContext=document";
        final String param4 = "transitionType=DocumentImage";
        final String param5 = "";
        final String param6 = "";
        urlBuilderAdapter.CreatePersistentUrl(template, param1, param2, param3, param4, param5, param6);
    }

    @Test
    public void testImageMetadataBlock() {
        System.out.println(urlBuilderAdapter);

        final String template = "Page.DocumentBlobV1";
        final String param1 = "imageGuid= Ie043fac0675a11da90ebf04471783734";
        final String param2 = "extension=jpg";
        final String param3 = "maxHeight=";
        final String param4 = "targetType= ";
        final String param5 = "originationContext=document";
        final String param6 = "";
        final String param7 = "";
        final String param8 = "transitionType=DocumentImage";

        urlBuilderAdapter.CreatePersistentUrl(template, param1, param2, param3, param4, param5, param6, param7, param8);
    }

    @Test
    public void testKeyciteFlagUrl() {
        final String template = "Page.KeyCiteFlagHistoryByLookup";
        final String param1 = "docGuid=Ie043fac0675a11da90ebf04471783734";
        final String param2 = "originationContext=document";
        final String param3 = "";
        final String param4 = "";
        urlBuilderAdapter.CreatePersistentUrl(template, param1, param2, param3, param4);
    }

    @Test
    public void testPdfImageMetadataBlock() {
        final String template = "Page.DocumentBlobV1";
        final String param1 = "imageGuid= Ie043fac0675a11da90ebf04471783734";
        final String param2 = "extension=pdf";
        final String param3 = "maxHeight=";
        final String param4 = "targetType= ";
        final String param5 = "originationContext=document";
        final String param6 = "";
        final String param7 = "";
        final String param8 = "transitionType=DocumentImage";
        final String param9 = "uniqueId=";

        urlBuilderAdapter
            .CreatePersistentUrl(template, param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    @Test
    public void testRedirectToExpertQA() {
        final String template = "Page.RedirectToExpertQA";
        final String param1 = "query=";
        final String param2 = "originationContext=document";
        final String param3 = "transitionType=DocumentImage";
        final String param4 = "";
        final String param5 = "";
        urlBuilderAdapter.CreatePersistentUrl(template, param1, param2, param3, param4, param5);
    }
}
