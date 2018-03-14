<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="AnalyticalReferenceBlock.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:include href="TribalCourtHeadnote.xsl"/>
	<xsl:include href="WestlawDescription.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		 <div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-docbody"/>
			<xsl:call-template name="EndOfDocument" >
				<xsl:with-param name="endOfDocumentCopyrightText">&veraInstituteOfJusticeCopyrightText;</xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="doc[child::content.metadata.block]">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="content.metadata.block/cmd.identifiers/cmd.cites"/>
			<xsl:apply-templates select="front.matter"/>
		</div>
		<xsl:apply-templates select="*[not(self::front.matter or self::content.metadata.block)]" />
	</xsl:template>

	<xsl:template match="prelim.block">
		<xsl:apply-templates select="//md.cites"/>
		<div class="&simpleContentBlockClass; &prelimBlockClass;">
			<div class="&documentHeadClass;">
				<xsl:apply-templates select="prelim.synopsis[descendant::grade.head]"/>
				<xsl:apply-templates select="front.matter"/>
				<xsl:apply-templates select="author.block[author.name]"/>
			</div>
			<xsl:apply-templates select="*[not(self::prelim.synopsis[descendant::grade.head] or self::front.matter or self::author.block[author.name])]"/>
		</div>
	</xsl:template>

	<xsl:template match="main.text.body/head[position()=last() and parent::node()[following-sibling::main.text.footnote.block]]" priority="2">
		<xsl:choose>
			<xsl:when test="bold/text() = 'FOOTNOTES' or bold/text() = 'NOTES'">
				<!-- Supress these two headings to avoid duplicates -->
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="head" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="introduction.section/section/appendix[position()=last() and ancestor::node()[2][following-sibling::footnote.block]]/head" priority="2">
		<xsl:choose>
			<xsl:when test="count(headtext) = 1 and (headtext/bold/text() = 'FOOTNOTES' or headtext/bold/text() = 'NOTES')">
				<!-- Supress these two headings to avoid duplicates -->
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="front.matter" priority="1">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

</xsl:stylesheet>
