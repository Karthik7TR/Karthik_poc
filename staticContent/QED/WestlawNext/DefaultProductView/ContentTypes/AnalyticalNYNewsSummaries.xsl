<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
					<xsl:value-of select="' &contentTypeAnalyticalNYNewsSummariesClass;'"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="RenderFootnoteSection"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="nylj.summary">
		<xsl:call-template name="documentHeader" />
		<xsl:apply-templates select="*[not(self::prelim or self::west.authored.line or self::document.heading)]" />
	</xsl:template>

	<xsl:template name="documentHeader">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="//md.cites" mode="heading"/>
			<xsl:apply-templates select="prelim" mode="heading" />
			<xsl:apply-templates select="west.authored.line" mode="heading" />
			<xsl:apply-templates select="document.heading" mode="heading"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="md.cites" mode="heading">
		<div>
			<xsl:choose>
				<xsl:when test="md.second.line.cite">
					<xsl:apply-templates select="md.second.line.cite"/>
				</xsl:when>
				<xsl:when test="md.third.line.cite">
					<xsl:apply-templates select="md.third.line.cite"/>
				</xsl:when>
				<xsl:when test="md.first.line.cite">
					<xsl:apply-templates select="md.first.line.cite"/>
				</xsl:when>
				<xsl:when test="md.primarycite">
					<xsl:apply-templates select="md.primarycite"/>
				</xsl:when>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="prelim | west.authored.line" mode="heading">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&centerClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="document.heading" mode="heading">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="load.date | court.line | topic.subtopic">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="title[ancestor::source.line]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="judge | source.line">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match ="source.line/wl.cite">
		<xsl:if test="preceding-sibling::title">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match ="source.line/file.date">
		<xsl:if test="preceding-sibling::wl.cite or preceding-sibling::title">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="string-length(.) = 8 and not(number(.) = NaN)">
				<xsl:call-template name="parseYearMonthDayDateFormat">
					<xsl:with-param name="displayDay" select="'true'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="content.metadata.block | md.cites" />

</xsl:stylesheet>