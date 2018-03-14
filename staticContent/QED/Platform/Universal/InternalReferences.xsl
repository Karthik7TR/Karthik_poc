<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:key name="allElementIds" match="*[@id|@ID]" use="@id|@ID"/>

  <xsl:template match="internal.reference" name="internalReference">
    <xsl:param name="id" />
    <xsl:param name="refid" select="@refid" />
    <xsl:param name="additionalClass"/>
    <xsl:param name="contents" />
    <xsl:param name="forceLink" select="false()"/>
    <xsl:param name="fixhighlights"/>
    <xsl:choose>
      <xsl:when test="key('allElementIds', $refid) or $forceLink = true()">
        <xsl:variable name="linkPrefix">
          <xsl:choose>
            <xsl:when test ="preceding-sibling::anchor[1][preceding-sibling::footnote.reference][1]">
              <xsl:value-of select="'#&footnoteIdPrefix;'"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="'#&internalLinkIdPrefix;'"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <a href="{concat($linkPrefix, $refid)}">
          <xsl:attribute name="class">
            <xsl:text>&internalLinkClass;</xsl:text>
            <!--Fix for the bug #730203-->
            <xsl:if test="string-length($fixhighlights) &gt; 0">
              <xsl:value-of select="$fixhighlights"/>
            </xsl:if>
            <xsl:if test="string-length($additionalClass) &gt; 0">
              <xsl:text><![CDATA[ ]]></xsl:text>
              <xsl:value-of select="$additionalClass"/>
            </xsl:if>
          </xsl:attribute>
          <xsl:if test="string-length($id) &gt; 0">
            <xsl:attribute name="id">
              <xsl:value-of select="$id"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:choose>
            <xsl:when test="string-length($contents) &gt; 0">
              <xsl:copy-of select="$contents"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates />
            </xsl:otherwise>
          </xsl:choose>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="string-length($contents) &gt; 0">
            <xsl:copy-of select="$contents"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
