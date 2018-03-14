<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CanadianCites.xsl"/>
	<xsl:include href="CanadianDate.xsl"/>
	<xsl:include href="CanadianFootnotes.xsl"/>
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- DO NOT RENDER -->
	<xsl:template match="content.metadata.block | title | mtext | message.block.carswell"/>

	<!-- Content Tests -->
	<xsl:variable name="hasImage">
		<xsl:value-of select="boolean(//img)"/>
	</xsl:variable>

	<xsl:variable name="hasURLLink">
		<xsl:value-of select="boolean(//urllink)"/>
	</xsl:variable>

	<xsl:variable name="hasNeither">
		<xsl:value-of select="boolean($hasURLLink = 'false' and $hasImage = 'false')"/>
	</xsl:variable>
	
	
	<!--Document template-->
	<xsl:template match="Document">
		<xsl:variable select="n-docbody/comment" name="commentPath"/>
		<xsl:variable select="n-docbody/comment/doc_heading" name="docHeadingPath"/>

		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswFormsPrecedentsClass;'"/>
			</xsl:call-template>

			<!-- Render Prelim -->
			<div class="&citesClass;">
				<xsl:apply-templates select="document-data/cite"/>
			</div>
			<div class="&headnotesClass; &centerClass;">
				<xsl:if test="$hasImage = 'true'">
					<xsl:apply-templates select="$commentPath//image.block" />
				</xsl:if>
				<xsl:apply-templates select="$docHeadingPath/toc_headings/toc_heading_0"/>
				<xsl:apply-templates select="$docHeadingPath/toc_headings"/>
			</div>
			<div class="&titleClass;">
				<xsl:apply-templates select="$docHeadingPath/doc_title"/>
			</div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>

			<!-- Render rest of document -->
			<xsl:if test="$hasURLLink = 'true'">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&paraMainClass; &layoutTextAlignLeft;'"/>
					<xsl:with-param name="contents">
						<xsl:value-of select="$commentPath/ul/p/text()" />
						<xsl:apply-templates select="$commentPath/ul/p/urllink" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$hasImage = 'true' or $hasNeither = 'true'">
				<xsl:apply-templates select="$commentPath/p[not(img)]"/>
			</xsl:if>
			<xsl:call-template name="RenderFootnoteSection"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="toc_headings">
		<xsl:for-each select="*[not(position() = 1)]">
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="'&paratextMainClass;'" />
			</xsl:call-template>
		</xsl:for-each>		
	</xsl:template>

	<xsl:template match="toc_heading_0 | doc_title | img">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>