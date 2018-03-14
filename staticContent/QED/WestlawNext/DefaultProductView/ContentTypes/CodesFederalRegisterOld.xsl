<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="n-docbody/document/body">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="primarycite/published.cite"/>
			<xsl:apply-templates select="document.type"/>
			<xsl:apply-templates select="agency.name"/>
			<xsl:apply-templates select="agency.division.name"/>
			<xsl:apply-templates select="target.cite"/>
			<xsl:apply-templates select="doc.heading"/>
			<xsl:apply-templates select="//citedate"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:apply-templates select="*[not(self::primarycite or self::document.type or self::agency.name or self::agency.division.name or self::target.cite or self::doc.heading or self::citedate)]"/>
	</xsl:template>

	<xsl:template match="doc.heading[position() = 1] | agency.name[not(//doc.heading)]">
		<div class="&titleClass;">
			<xsl:value-of select="."/>
		</div>
	</xsl:template>

	<xsl:template match="published.cite | agency.name[//doc.heading] | agency.division.name[not(parent::agency.line)] | doc.heading[position() &gt; 1] | citedate | target.cite">
		<div class="&centerClass;">
			<xsl:value-of select="."/>
		</div>
	</xsl:template>

	<xsl:template match="summary">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="tracking.number | dashedshortcite | shortcite | cfr.section.referenced | image.keywords"/>
</xsl:stylesheet>