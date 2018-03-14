<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="DisplayPublisherLogo"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates/>
			<xsl:call-template name="EndOfDocument" />
			<xsl:call-template name="DisplayPublisherLogo"/>
		</div>
	</xsl:template>
	<!--Supressing element-->
	<xsl:template match="md.action.doc.type" />
	<xsl:template match="reg.body">
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id">
				<xsl:if test="@ID|@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="reg.front">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="content.metadata.block/cmd.identifiers/cmd.cites"/>
			<xsl:apply-templates select="prelim.block"/>
			<xsl:apply-templates select="include.copyright[@n-include_collection = 'w_codes_stamsgp']"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:apply-templates select="summary.block"/>
	</xsl:template>

	<xsl:template match="reg.front/prelim.block" priority="2" >
		<div class="&simpleContentBlockClass; &prelimBlockClass;">
			<div class="&centerClass;">
				<xsl:apply-templates select="prelim.head[1]"/>
				<xsl:apply-templates select="prelim.head[4]"/>
				<xsl:apply-templates select="prelim.head[3]"/>
				<xsl:apply-templates select="prelim.head[2]"/>
				<xsl:apply-templates select="date.line"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="prelim.block/prelim.head/head/headtext | prelim.block/date.line" priority="2">
		<xsl:param name="divId"/>
		<xsl:call-template name="renderHeadTextDiv">
			<xsl:with-param name="divId" select="$divId"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="content.metadata.block/cmd.identifiers/cmd.cites" priority="2">
		<div class="&citesClass; &titleClass;">
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

	<!-- overriding global suppression on include.copyright -->
	<xsl:template match="include.copyright[@n-include_collection = 'w_codes_stamsgp']">
		<div class="&copyrightClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

</xsl:stylesheet>
