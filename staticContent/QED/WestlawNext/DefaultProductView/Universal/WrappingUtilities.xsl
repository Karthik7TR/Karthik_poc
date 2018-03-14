<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:import href="WrappingUtilities.xsl" forcePlatform="true" />
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  
  <!-- Callable template for content blocks -->
  <xsl:template name="wrapContentBlockWithCobaltClass">
    <xsl:param name="id"/>
    <xsl:param name="contents" />
    <xsl:variable name="xmlBasedClassName">
      <xsl:call-template name="escape-to-class">
        <xsl:with-param name="prefix" select="'co_'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="id" select="$id" />
      <xsl:with-param name="class">
        <xsl:text>&simpleContentBlockClass; </xsl:text>
        <xsl:text>&briefItStateClass; </xsl:text>
        <xsl:value-of select="$xmlBasedClassName"/>
      </xsl:with-param>
      <xsl:with-param name="contents" select="$contents" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="wrapContentBlockWithAdditionalCobaltClasses">
    <xsl:param name="id"/>
    <xsl:param name="additionalClass"/>
    <xsl:param name="contents" />
    <xsl:variable name="xmlBasedClassName">
      <xsl:call-template name="escape-to-class">
        <xsl:with-param name="prefix" select="'co_'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="id" select="$id" />
      <xsl:with-param name="class">
        <xsl:text>&simpleContentBlockClass; </xsl:text>
        <xsl:text>&briefItStateClass; </xsl:text>
        <xsl:value-of select="$xmlBasedClassName"/>
        <xsl:if test="string-length($additionalClass) &gt; 0">
          <xsl:text><![CDATA[ ]]></xsl:text>
          <xsl:value-of select="$additionalClass"/>
        </xsl:if>
      </xsl:with-param>
      <xsl:with-param name="contents" select="$contents" />
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>