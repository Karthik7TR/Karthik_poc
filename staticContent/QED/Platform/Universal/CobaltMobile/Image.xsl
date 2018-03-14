<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<xsl:template name="buildBlobImageElement">
		<xsl:param name="alt"/>
		<xsl:value-of select="$alt"/>
	</xsl:template>

    <xsl:template name="GetHrefForImageLink">
      <xsl:param name="blobHref"/>
      <xsl:param name="hasAttachments"/>
      <xsl:choose>
        <xsl:when test="string-length($blobHref) &gt; 0">
          <xsl:value-of select="$blobHref"/>
          <xsl:if test="string-length($hasAttachments) &gt; 0">
            <xsl:text>&amp;attachments=</xsl:text>
            <xsl:value-of select="$hasAttachments"/>
          </xsl:if>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text></xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:template>

    <xsl:template name="GetOnclickForImageLink">
      <xsl:text></xsl:text>
    </xsl:template>
</xsl:stylesheet>
