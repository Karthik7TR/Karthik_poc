<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="Date.xsl" forcePlatform="true"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="date.line" priority="1">
    <xsl:for-each select="*">
      <xsl:if test="not(@significance)">
        <div class="&dateClass;">
          <xsl:apply-templates />
        </div>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="doc_date">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&dateClass;'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="md.endeffective | md.starteffective" priority="1">
    <xsl:choose>
      <xsl:when test="string-length(.) &gt; 13 and number(.) != 'NaN'">
        <xsl:call-template name="parseYearMonthDayDateFormat">
          <xsl:with-param name="displayDay" select="'true'" />
          <xsl:with-param name="displayTime" select="'true'" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="parseYearMonthDayDateFormat">
          <xsl:with-param name="displayDay" select="'true'" />
          <xsl:with-param name="displayTime" select="'false'" />
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
