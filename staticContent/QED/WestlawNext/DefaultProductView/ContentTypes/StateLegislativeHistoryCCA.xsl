<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CitesFromContentMetaData.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="n-metadata | version.date.line | citator.treatment | hidden.img.text"/>
	
	<xsl:template match="Document">
		<div id="&documentId;">	
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesCourtOrdersClass;'"/>
			</xsl:call-template>

			<xsl:call-template name="firstLineCiteDisplayCenter"/>
				
			<xsl:apply-templates />
			
			<xsl:call-template name="firstLineCiteDisplayLeft" />
			
			<xsl:call-template name="EndOfDocument" />			
		</div>
	</xsl:template>

	<xsl:template match="pending.law.front[not(following-sibling::pending.law.front)] | 
											 delegates[not(ancestor::pending.law.front)]">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
	
	<xsl:template match="summary.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template name="firstLineCiteDisplayLeft">
		<div class="&citationClass;">
			<xsl:apply-templates select="//content.metadata.block/cmd.identifiers/cmd.cites/cmd.first.line.cite" mode="ForceDisplay" />
		</div>
	</xsl:template>
	
	<xsl:template name="firstLineCiteDisplayCenter">
		<div class="&citesClass; &citationClass;">
			<xsl:apply-templates select="//content.metadata.block/cmd.identifiers/cmd.cites/cmd.first.line.cite" mode="ForceDisplay" />
		</div>
	</xsl:template>

	<xsl:template match="cmd.first.line.cite" mode="ForceDisplay">
		<xsl:apply-templates />
	</xsl:template>

</xsl:stylesheet>
