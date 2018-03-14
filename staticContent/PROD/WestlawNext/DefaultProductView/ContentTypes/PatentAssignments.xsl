<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Patents.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypePatentsAssignmentsClass;'"/>
			</xsl:call-template>
			<xsl:apply-templates />
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="source">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="assign.patent.info/* | assign.parties/* | summary/*">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="affected.patent/* | assignee/* | assignor.block/* | agent/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- If there is a content block that ends in a link here then Microsoft Word and RTF 
			documents will not use the correct styles for that block (the styles will get ignored).
			Adding a space after the link will cause RTF to deliver properly. -->
	<xsl:template match="patent.number" priority="1">
		<div>
			<xsl:apply-templates />
			<xsl:text>&#160;</xsl:text>
		</div>
	</xsl:template>
</xsl:stylesheet>