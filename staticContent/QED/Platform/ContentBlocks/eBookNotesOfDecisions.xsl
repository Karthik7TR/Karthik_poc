<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="nod.block" name="renderNotesOfDecisionsStatutes">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and $StatutoryTextOnly" />
			<xsl:when test="count(../nod.block//nod.note | ../nod.block//nod.ref) = 0" />
			<xsl:otherwise>
				<span class="&notesOfDecisionsWrapperClass;">
					<xsl:apply-templates mode="toc" />
					<xsl:apply-templates />
				</span>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="nod.block/head" mode="toc">
		<div class="&printHeadingClass; &notesOfDecisionsHeaderClass;">
			<h2>
				<xsl:value-of select="head.info"/>
			</h2>
		</div>
	</xsl:template>
	<xsl:template match="nod.block/head"/>	
	<xsl:template match="nod.block/analysis"/>

	<xsl:template match="nod.block/analysis/analysis.entry" mode="toc">
		<xsl:if test="analysis.line/label.designator/internal.reference">
			<h4 class="&notesOfDecisionsTocClass;"><xsl:apply-templates select="analysis.line"/></h4>
		</xsl:if>
	</xsl:template>

	<xsl:template match="nod.block/nod.body" mode="toc">
		<xsl:apply-templates select="head" mode="toc"/>
		<ul><xsl:apply-templates select="analysis" mode="toc"/></ul>
	</xsl:template>

	<xsl:template match="nod.block/nod.body/head" mode="toc">
		<h4 class="&notesOfDecisionsTocHeaderClass;">
			<xsl:apply-templates select="bos"/>
			<xsl:apply-templates select="head.info/label.designator"/>. 
			<xsl:value-of select="head.info/headtext"/>
		</h4>
	</xsl:template>

	<xsl:template match="nod.block/nod.body/head">
		<h2>
			<xsl:apply-templates select="head.info/label.designator"/>. 
			<xsl:value-of select="head.info/headtext"/>
		</h2>
	</xsl:template>

	<xsl:template match="nod.block/nod.body/analysis/head" mode="toc"/>
	<xsl:template match="nod.block/nod.body/analysis" />

	<xsl:template match="nod.block/nod.body/analysis/analysis.entry" mode="toc">
		<xsl:element name="li">
			<xsl:attribute name="style">
				margin-left:<xsl:value-of select="analysis.line/@indent-left"/>em;
			</xsl:attribute>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="nod.block/nod.body/nod.body" mode="toc"/>
	<xsl:template match="nod.block/nod.body/nod.body">
		<h2><xsl:apply-templates select="head"/></h2>
		<xsl:apply-templates select="nod.note"/>
	</xsl:template>

	<xsl:template match="nod.block/nod.body/nod.body/head">
		<xsl:apply-templates select="bos"/>
		<xsl:apply-templates select="head.info/label.designator"/>. 
		<xsl:value-of select="head.info/headtext"/>
	</xsl:template>

	<xsl:template match="nod.block//head/bos">
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;',../@ID)"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="nod.block//analysis.line/bos">
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;',../../@ID)"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
