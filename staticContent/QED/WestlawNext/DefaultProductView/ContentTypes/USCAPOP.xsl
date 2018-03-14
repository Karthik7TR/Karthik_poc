<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Prelim.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">	
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeUSCAPOPClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="n-docbody/pop.name.table">
		<xsl:call-template name="renderHeader"/>
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id">
				<xsl:if test="@ID|@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="contents">
				<xsl:apply-templates select="*[not(self::content.metadata.block or self::prelim.block)]"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="renderHeader">
		<xsl:if test="prelim.block or content.metadata.block">
			<div class="&documentHeadClass;">
				<xsl:apply-templates select="content.metadata.block/cmd.identifiers/cmd.cites"/>
				<xsl:apply-templates select="prelim.block"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="prelim.block" priority="2" >
		<div class="&simpleContentBlockClass; &prelimBlockClass;">
			<div class="&centerClass;">
				<xsl:apply-templates select="prelim.line"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="table.of.contents.block | enacting.credit.block | short.title | classification.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	
	<xsl:template match="prelim.block/prelim.line | table.of.contents.heading | subsection.heading" priority="2">
		<xsl:param name="divId"/>
		<xsl:call-template name="renderHeadTextDiv">
			<xsl:with-param name="divId" select="$divId"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pop.name.title" priority="2">
		<xsl:param name="divId"/>
		<xsl:call-template name="renderHeadTextDiv">
			<xsl:with-param name="divId" select="$divId"/>
		</xsl:call-template>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="cmd.cites" priority="2">
		<div class="&titleClass; &citesClass;">
			<xsl:choose>
				<xsl:when test="cmd.second.line.cite">
					<xsl:apply-templates select="cmd.second.line.cite"/>
				</xsl:when>
				<xsl:when test="cmd.third.line.cite">
					<xsl:apply-templates select="cmd.third.line.cite"/>
				</xsl:when>
				<xsl:when test="cmd.first.line.cite">
					<xsl:apply-templates select="cmd.first.line.cite"/>
				</xsl:when>
			</xsl:choose>
		</div>
	</xsl:template>
	
	<xsl:template match="md.cites" priority="1">
		<xsl:if test="md.primarycite and not(/Document/n-docbody/*/content.metadata.block/cmd.identifiers/cmd.cites) and not($PreviewMode)">
			<div class="&citesClass;">
				<xsl:apply-templates select="md.primarycite" />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="legend.line | enacting.law.block | amending.law.block | related.act.block | include.currency.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>
	
	<xsl:template match="enacting.credit | table.of.contents.line| revised.title.line | amending.law | enacting.law | related.act">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="include.copyright.block/include.copyright[@n-include_collection = 'w_codes_stamsgp']" priority="1">
		<div class="&copyrightClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Suppressing this -->
	<xsl:template match="printmarkup.block | printmarkup.block/*" />

</xsl:stylesheet>