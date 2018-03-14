<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="Analysis.xsl"/>
  <xsl:include href="Cites.xsl"/>
  <xsl:include href="CorrelationTable.xsl"/>
  <xsl:include href="Date.xsl"/>
  <xsl:include href="Footnotes.xsl"/>
  <xsl:include href="Form.xsl"/>
  <xsl:include href="Jurisdictions.xsl"/>
  <xsl:include href="Letter.xsl"/>
  <xsl:include href="Title.xsl"/>
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="Copyright.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="Document">
    <div id="&documentId;">
      <xsl:call-template name="AddDocumentClasses">
        <xsl:with-param name="contentType" select="'&contentTypeAnalyticalTreatisesAndAnnoCodesClass;'"/>
      </xsl:call-template>
      <xsl:choose>
        <xsl:when test="$EasyEditMode">
          <xsl:apply-templates select="node()" mode="EasyEdit"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="//md.form.flag">
            <xsl:call-template name="EasyEditFlag"/>
          </xsl:if>
          <xsl:call-template name="StarPageMetadata" />
          <xsl:apply-templates />
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="EndOfDocument" />
    </div>
  </xsl:template>

  <xsl:template match="md.references"/>

  <xsl:template match="head[@ID]" priority="1">
    <xsl:call-template name="head">
      <xsl:with-param name="divId">
        <xsl:value-of select="concat('&internalLinkIdPrefix;', @ID)"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- suppresses all but the first content.metadata.block and the one that contains the copyright -->
  <xsl:template match="content.metadata.block[preceding-sibling::content.metadata.block and not(cmd.royalty/cmd.copyright)]" />

  <!-- suppress the first line cite at the bottom of the document -->
  <xsl:template match="content.metadata.block/cmd.identifiers/cmd.cites/cmd.first.line.cite" />

  <xsl:template match="doc.title" priority="1">
    <xsl:call-template name="titleBlock"/>
    <xsl:comment>&EndOfDocumentHead;</xsl:comment>
  </xsl:template>

</xsl:stylesheet>