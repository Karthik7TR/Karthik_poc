<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.LINKS.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Image.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:template name="getGatewayBlobUrl">
		<xsl:param name="county" />
		<xsl:param name="court" />
		<xsl:param name="courtNumber" />
		<xsl:param name="casenumber" />
		<xsl:param name="id" />
		<xsl:param name="filename" />
		<xsl:param name="platform" />
		<xsl:param name="mimeType" />
		<xsl:param name="originationContext" />
		<xsl:param name="extension" />
		<xsl:param name="newId" />
		<xsl:variable name="courtNorm">
			<xsl:value-of select="/Document/n-docbody/r/case.information.block/court.block/court.norm | /Document/n-docbody/r/c/court.norm | /Document/n-docbody/r/court.block/court.norm" />
		</xsl:variable>		
		<!-- need to translate pipes to commas in the id -->
		<xsl:value-of select="UrlBuilder:CreatePersistentLINKSUrl('Page.DocketBlob', concat('platform=', $platform), concat('court=', $court), concat('courtNorm=', $courtNorm), concat('id=', translate(substring-before($newId, ';'), '|', ',')), concat('filename=', $filename), concat('county=', $county), concat('courtnumber=', $courtNumber), concat('casenumber=', $casenumber), concat('extension=', $extension), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', 'contextData=(sc.Default)', concat('&uniqueIdParamName;=', $UniqueIdForBlobs))"/>
	</xsl:template>

</xsl:stylesheet>
