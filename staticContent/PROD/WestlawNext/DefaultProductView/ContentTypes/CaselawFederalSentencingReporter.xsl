<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
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
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="IsFSRCases" select="contains('|w_3rd_fsrcc|', concat('|', /Document/document-data/collection, '|'))"/>
	<xsl:variable name="suppressStarPageMetadata" select="$IsFSRCases"/>

	<xsl:key name="distinctEligibleStarPagesForDisplay" use="concat(@pageset, concat('_', text()))" match="starpage.anchor[@pageset and not(ancestor::docket.block or ancestor::date.block or ancestor::court.block or ancestor::message.block or ancestor::headnote.block or ancestor::court.headnote.block or ancestor::synopsis or ancestor::archive.headnote.block or ancestor::trial.type.block or ancestor::headnote.publication.block or ancestor::layout.control.block or ancestor::content.layout.block or ancestor::error.block or ancestor::withdrawn.block or ancestor::archive.brief.reference.block)]" />

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>

			<!--Suppressing Star Page Metadata will change the behavior of Copy With Reference functionality.
					Note that this will not suppress the Star Page anchors from displaying.-->
			<xsl:choose>
				<xsl:when test="not($suppressStarPageMetadata)">
					<xsl:call-template name="StarPageMetadata" />
				</xsl:when>
			</xsl:choose>

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
			</div>
		</div>
			<xsl:apply-templates select="attorney.block[attorney.name]"/>
			<xsl:apply-templates select="author.block[author.name]"/>
		<div class="&simpleContentBlockClass; &prelimBlockClass;">
			<xsl:text>&#160;</xsl:text>
			<xsl:apply-templates select="*[not(self::prelim.synopsis[descendant::grade.head] or self::front.matter or self::author.block[author.name]  or self::attorney.block[attorney.name])]"/>
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

	<!-- Suppress Star Page anchors for the following collections.
			Note that this does not suppress the Star Page Metadata from being inserted
			into the page and being used for Copy with Reference functionality. -->
	<xsl:template match="starpage.anchor[contains('|w_3rd_fsrcc|', concat('|', /Document/document-data/collection, '|'))]" priority="2" />

	<!-- Suppress WL star paging for this collection-->
	<xsl:template match="starpage.anchor[/Document/document-data/collection = 'w_3rd_fsrdm' and starts-with(@ID, 'sp_999')]" priority="5"/>

	<xsl:template match="date[preceding-sibling::date]" priority="2">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="para" priority="2">
		<xsl:call-template name="nestedParas"/>
	</xsl:template>

	<xsl:template match="author.block" priority="2">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

</xsl:stylesheet>
