<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="FootnoteBlock.xsl"/>
	<xsl:output omit-xml-declaration="yes" method="xml" indent="no"/>

	<xsl:template match="Document">
		<div id="&documentId;">	
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypemedLitLippincottJournalClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- This template is needed to skip putting a duplicate <table> tag -->
	<xsl:template match="footnote.block[ancestor::body]" priority="3">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="footnote | endnote" priority="2">
		<xsl:call-template name="RenderFootnoteWithoutBlockAncestorMarkup"/>
	</xsl:template>

	<xsl:template match="endnote.block" priority="1">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="id">
				<xsl:if test="@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', @id)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<!-- endnote without label.designator text needs to be treated like an internal reference -->
	<xsl:template match="endnote[not(child::label.designator) or child::label.designator = '']" priority="3">
		<xsl:variable name="link">
			<xsl:value-of select="concat('&internalLinkIdPrefix;', (@id | @ID))" />
		</xsl:variable>
		<span>
			<xsl:attribute name="id">
				<xsl:value-of select="$link"/>
			</xsl:attribute>
			<xsl:apply-templates mode="internalReference" />
		</span>
	</xsl:template>

	<xsl:template match="endnote.body" mode="internalReference">
		<xsl:apply-templates />
	</xsl:template>

	<!-- issn -->
	<xsl:template match="issn">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- Publisher -->
	<xsl:template match="publisher.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="publisher.block/publisher.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Source -->
	<xsl:template match="source.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="source.block/source.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="title.block" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="message.block/include.copyright" priority="1">
		<div class="&copyrightClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Suppress the internal refrence link for the Authors to the Affiliation per NPD on 9/19/2014. -->
	<xsl:template match="authors//label.name/internal.reference" priority="1">
		<xsl:value-of select="."/>
	</xsl:template>

</xsl:stylesheet>
