<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="Cites.xsl"/>
  <xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
  <xsl:include href="Title.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeAnalyticalShortTopicClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
      <xsl:apply-templates />
      <xsl:call-template name="EndOfDocument" />
    </div>
  </xsl:template>

  <!-- suppresses all but the first content.metadata.block -->
  <xsl:template match="content.metadata.block[preceding-sibling::content.metadata.block]" />
	
	<xsl:template match="doc.title" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

</xsl:stylesheet>
