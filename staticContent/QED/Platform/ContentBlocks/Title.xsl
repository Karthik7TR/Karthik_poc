<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>


	<xsl:template match="title.block | title | doc.title | fixed.title" name="titleBlock">
		<xsl:param name="class" select="'&titleClass;'"/>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="$class"/>
		</xsl:call-template>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="primary.title">
		<xsl:choose>
			<!-- Wrap the primary.title in a DIV only if there is a secondary.title or another primary.title right after it -->
			<xsl:when test="following-sibling::secondary.title or following-sibling::node()[1][self::primary.title]">
				<xsl:call-template name="wrapWithDiv" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="secondary.title">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="party.line" name="normalPartyLine">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&partyLineClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="versus">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="and">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="title.block//text.line | title//text.line">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&textLineClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="title.block//title.line">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleLineClass;'" />
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
