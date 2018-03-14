<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="patents.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypePatentsStatusClass;'"/>
			</xsl:call-template>
			<xsl:apply-templates />
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
	<!-- Hide these elements. -->
	<xsl:template match="include.copyright" />

	<!-- For Pat Stat we never want the jurisdiction element to be displayed because,
			as per the business, it always contains the same useless value. -->
	<xsl:template name="jurisdiction">

	</xsl:template>

	<xsl:template match="ps.patent.info/* | status.info/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>
</xsl:stylesheet>
