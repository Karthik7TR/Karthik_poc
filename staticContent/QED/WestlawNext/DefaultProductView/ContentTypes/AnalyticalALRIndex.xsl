<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Index.xsl"/>
	<xsl:include href="ArticleBody.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="Document">
    <div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeAnalyticalALRIndexClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
      <xsl:apply-templates />
      <xsl:call-template name="EndOfDocument" />
    </div>
  </xsl:template>

	<xsl:template match="doc">
		<xsl:apply-templates select="content.metadata.block[1]//cmd.cites" />
		<xsl:apply-templates select="node()[not(self::content.metadata.block)]" />
	</xsl:template>

	<xsl:template match="cmd.copyright"/>

	<xsl:template match="index" priority="1">
		<xsl:call-template name="index"/>
	</xsl:template>

	<xsl:template match="article.body" priority="1">
		<xsl:call-template name="articleBody"/>
	</xsl:template>

	<xsl:template match="prop.block" priority="1">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="prop.head[1]" priority="1">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="cmd.currency.default" mode="cmdCurrency">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

</xsl:stylesheet>
