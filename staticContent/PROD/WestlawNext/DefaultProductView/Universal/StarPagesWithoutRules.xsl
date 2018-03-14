<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="StarPages.xsl"/>
	<xsl:include href="StarPagesWithoutRules.xsl" forcePlatform="true" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="displayableCitesForContentType" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info/md.display.primarycite[@display = 'Y'] | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info/md.display.parallelcite[@display = 'Y' or @userEntered = 'Y']" />
	<xsl:variable name="displayableCite" select="$displayableCitesForContentType[1]" />
	<xsl:variable name="displayableCiteId">
		<xsl:choose>
			<xsl:when test="$displayableCite/@ID">
				<xsl:value-of select="$displayableCite/@ID" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="generate-id($displayableCite)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="isWestlawCite" select="$displayableCite/@type = 'Westlaw'"/>

	<!-- Do not process the Star Pages with the normal rules, just display them outright! -->
	<xsl:template match="starpage.anchor" priority="1">
		<xsl:call-template name="spWoRulesAnchorWithParams">
			<xsl:with-param name="displayableCiteIdParam" select="$displayableCiteId" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="StarPageMetadataForContentType">
		<xsl:call-template name="spWoRulesMetadataForContentTypeWithParams">
			<xsl:with-param name="displayableCiteParam" select="$displayableCite" />
			<xsl:with-param name="displayableCiteIdParam" select="$displayableCiteId" />
			<xsl:with-param name="isWestlawCiteParam" select="$isWestlawCite" />
		</xsl:call-template>
	</xsl:template>
	
	<!-- Special footnote-matching mode to insert hidden star page anchors into the content
	     for Copy With Reference page calculations when the footnotes have been moved! -->
	<xsl:template match="footnote | form.footnote | endnote | form.endnote" mode="starPageCalculation" priority="1">
		<xsl:call-template name="spWoRulesCalculationWithParams">
			<xsl:with-param name="displayableCiteIdParam" select="$displayableCiteId" />
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
