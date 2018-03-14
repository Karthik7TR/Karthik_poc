<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SearchTerms.xsl" forceDefaultProduct="true"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
  
  <xsl:template match="N-HIT" name="nHit">
    <xsl:param name="contents">
      <xsl:apply-templates/>
    </xsl:param>
    <xsl:call-template name="RenderTerm">
      <xsl:with-param name="contents" select="$contents" />
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="N-LOCATE" name="nLocate">
    <xsl:param name="contents">
      <xsl:apply-templates/>
    </xsl:param>
    <xsl:call-template name="RenderTerm">
      <xsl:with-param name="contents" select="$contents" />
    </xsl:call-template>
  </xsl:template>
    
  <xsl:template match="N-WITHIN" name="nWithin">
    <xsl:param name="contents">
      <xsl:apply-templates/>
    </xsl:param>
    <xsl:call-template name="RenderTerm">
      <xsl:with-param name="contents" select="$contents" />
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template name="RenderTerm">
    <xsl:param name="contents">
      <xsl:apply-templates/>
    </xsl:param>
      <xsl:copy-of select="$contents" />
  </xsl:template>

</xsl:stylesheet>