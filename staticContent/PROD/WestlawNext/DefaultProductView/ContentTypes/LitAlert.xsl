<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="patents.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypePatentsLitAlertClass;'"/>
			</xsl:call-template>
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- Hide these elements. -->
	<xsl:template match="include.copyright" />

	<!-- For Lit Alert we never want the jurisdiction element to be displayed because,
			as per the business, it always contains the same useless value. -->
	<xsl:template name="jurisdiction">
		
	</xsl:template>
	
	<xsl:template match="*[preceding-sibling::title][1]" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="la.patent.info/* | court.info/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>
</xsl:stylesheet>
