<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="ny.headnote.block | pr.headnote.block | prag.headnote.block">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="ny.headnote">
    <xsl:variable name="headnoteNumberText">
      <xsl:apply-templates select=".//headnote.reference"/>
    </xsl:variable>
    <div class="&headnoteClass;">
      <xsl:if test="string-length($headnoteNumberText) &gt; 0">
        <xsl:attribute name="id">
          <xsl:value-of select="concat('&internalLinkIdPrefix;&internalLinkRelatedInfoHeadnoteIdPrefix;', $headnoteNumberText)"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="ny.headnote.body | pr.headnote.body | prag.headnote.body">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

  <xsl:template match="ny.key.line | pr.key.line | prag.key.line">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="ny.key" />

	<xsl:template match="classification.group">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>
