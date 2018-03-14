<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="TitleHtml.xsl" forcePlatform="true"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="md.merger.parties">
    <xsl:apply-templates select="md.target.party" />
    <xsl:apply-templates select="md.acquirer.party" />
    <xsl:apply-templates select="md.merger.party/md.merger.party.type[.='T']" />
    <xsl:apply-templates select="md.merger.party/md.merger.party.type[.='A']" />
  </xsl:template>

  <xsl:template match="md.target.party | md.acquirer.party" priority="1">
    <xsl:value-of select="md.companyname"/>
    <xsl:if test="md.merger.party.type = 'T'"> | </xsl:if>
  </xsl:template>

  <xsl:template match="md.merger.party.type">
    <xsl:value-of select="../md.companyname"/>
    <xsl:if test=".='T'"> | </xsl:if>
  </xsl:template>

  <xsl:template match="//md.related.merger.parties" />
  <xsl:template match="//md.merger" />
  <xsl:template match="//md.linkid.block" />
  <xsl:template match="//md.events" />
  <xsl:template match="//md.mergerfilings" />
</xsl:stylesheet>