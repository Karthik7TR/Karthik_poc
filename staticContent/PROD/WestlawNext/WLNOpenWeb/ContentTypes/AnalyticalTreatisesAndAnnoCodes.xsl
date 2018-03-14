<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLNOpenWeb.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AnalyticalTreatisesAndAnnoCodes.xsl" forceDefaultProduct="true"/>

	<!-- display the title only -->
	<xsl:template match="Document">
		<div id="&documentId;">

			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
					<xsl:value-of select="' &contentTypeAnalyticalTreatisesAndAnnoCodesClass;'"/>
				</xsl:with-param>
			</xsl:call-template>

			<div>
				<xsl:apply-templates select="n-docbody/doc/content.metadata.block[1]"/>
				<xsl:apply-templates select="n-docbody/doc/prop.block[1]"/>	
				
				<xsl:apply-templates select="n-docbody/doc/section/section.front/doc.title"/> 
			</div>
			
			<xsl:call-template name="EndOfDocument" />

		</div>
	</xsl:template>

</xsl:stylesheet>