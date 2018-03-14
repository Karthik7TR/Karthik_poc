<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="Copyright.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="Document">
    <div id="&documentId;">
      <xsl:call-template name="AddDocumentClasses">
        <xsl:with-param name="contentType" select="'&contentTypeGoldsheetsBasisPoint;'"/>
      </xsl:call-template>
      <xsl:call-template name="HeadText" />
      <xsl:apply-templates select="//image.block" />
      <xsl:apply-templates select="//cmd.copyright" />
      <xsl:call-template name="EndOfDocument" />
    </div>
  </xsl:template>

  <xsl:template name="HeadText">
    <xsl:variable name="date" select="//prop.block/prop.head[3]/headtext/text()" />
    <div class="&simpleContentBlockClass; &propBlockClass;">
      <div class="&headtextClass;">
        <xsl:value-of select="//prop.block/prop.head[1]/headtext/text()" />
      </div>
      <div class="&headtextClass;">
        <xsl:value-of select="//prop.block/prop.head[2]/headtext/text()" />
      </div>
      <div class="&headtextClass;">
        <xsl:value-of select="concat(substring-before(substring-after($date, ' '), ' '), ' ', substring-before($date, ' '), ' ', substring-after(substring-after($date, ' '), ' '))"/>
      </div>
    </div>
    <xsl:comment>&EndOfDocumentHead;</xsl:comment>
  </xsl:template>
</xsl:stylesheet>