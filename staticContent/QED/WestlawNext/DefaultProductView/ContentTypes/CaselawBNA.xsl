<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="SharedBNA.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="createBnaIcon"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="createBnaIcon"/>
			<xsl:call-template name="EndOfDocument" >
				<xsl:with-param name="endOfDocumentCopyrightText">
					<xsl:call-template name="BNACopyrightMessage"/>
				</xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template name="createBnaIcon">
		<div class="&bnaHeaderImageClass;">
			<img src="{$Images}&bnaPath;" alt="&bnaText;"/>
		</div>
	</xsl:template>

	<!-- Suppress West Headnotes -->
	<xsl:template match="headnote.block | keysummary.block"/>

	<!-- Use CSS class to center date blocks -->
	<xsl:template match="date.block" priority="1">
		<xsl:call-template name="dateBlock">
			<xsl:with-param name="extraClasses">
				<xsl:text>&centerClass;</xsl:text>
			</xsl:with-param>	
		</xsl:call-template>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
	
	<!-- Use CSS class to center docket blocks -->
	<xsl:template match="docket.block">
		<div class="&centerClass;">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
		</div>
	</xsl:template>

</xsl:stylesheet>
