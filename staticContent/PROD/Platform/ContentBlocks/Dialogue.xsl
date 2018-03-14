<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="dialogue.block">
			<div class="&dialogueClass;">
				<xsl:apply-templates />
			</div>
	</xsl:template>

	<xsl:template match="dialogue.item">
		<div class="&dialogueItemClass;">
			<xsl:apply-templates select="./speaker" />
			<xsl:apply-templates select="./dialogue" />
		</div>
	</xsl:template>

	<xsl:template match="speaker">
			<div class="&dialogueItemSpeakerClass;">
				<xsl:apply-templates />
			</div>
	</xsl:template>

	<xsl:template match="dialogue">
			<div class="&dialogueItemTextClass;">
				<xsl:apply-templates />
			</div>
	</xsl:template>

	<!-- override para - we don't want a div here as speaker and dialog must be on same line -->
	<xsl:template match="dialogue/para">
		<xsl:apply-templates 	/>
	</xsl:template>

	<xsl:template match="dialogue/para/paratext">
		<xsl:apply-templates />
	</xsl:template>
</xsl:stylesheet>
