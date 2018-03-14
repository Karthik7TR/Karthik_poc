<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="FootnoteBlock.xsl" forcePlatform="true" />
  <xsl:include href="Footnotes.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
  
  <xsl:template name="RenderFootnoteBlockMarkupDiv">
    <xsl:param name="suppressHeading" select="false()" />
    <xsl:param name="id"/>
    <div class="&footnoteSectionClass; &briefItStateClass;">
      <xsl:if test="string-length($id) &gt; 0">
        <xsl:attribute name="id">
          <xsl:value-of select="$id"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="not($suppressHeading)">
        <h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
        </h2>
      </xsl:if>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template name="RenderFootnoteWithoutBlockAncestorMarkupDiv">
    <xsl:variable name="footnoteContent">
      <xsl:apply-templates select="." mode="footnote"/>
    </xsl:variable>
    <xsl:if test="string-length($footnoteContent) &gt; 0">
      <div class="&footnoteSectionClass; &briefItStateClass;">
        <xsl:copy-of select="$footnoteContent"/>
      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>