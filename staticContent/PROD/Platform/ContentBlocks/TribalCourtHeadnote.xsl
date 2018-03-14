<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="tribal.court.headnote.block">
		<div class="&tribalCourtHeadnoteClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="tribal.court.headnote.block//expanded.headnote">
		<div class="&expandedHeadnoteClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Necessary to counteract the suppression in the main Headnote stylesheet -->
	<xsl:template match="tribal.court.headnote.block//expanded.classification">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="tribal.court.headnote.block//topic.line">
		<div class="&topicLineClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="tribal.court.headnote.block//headnote">
		<div class="&headnoteClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>
