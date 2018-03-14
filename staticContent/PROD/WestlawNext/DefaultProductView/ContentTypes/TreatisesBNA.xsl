<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SharedBNA.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeTreatisesClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="DisplayPublisherLogo"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="DisplayPublisherLogo"/>
			<xsl:call-template name="EndOfDocument" >
				<xsl:with-param name="endOfDocumentCopyrightText">
					<xsl:call-template name="BNACopyrightMessage"/>
				</xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="title.block[not(following-sibling::date.block or following-sibling::date.block[child::date.line/@hidden]) or not(following-sibling::date.block[following-sibling::main.text or following-sibling::profile.block])]" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="/Document/n-docbody/bna.other.doc/source" priority="1"/>

	<xsl:template match="date.block[preceding-sibling::title.block and (following-sibling::main.text or following-sibling::profile.block)]" priority="1">
		<xsl:call-template name="dateBlock" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- Turn off automatic bolding of headtext elements -->
	<xsl:template match="headtext" priority="2">
		<div>&#160;</div>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Set a blank line within the author block above the author name -->
	<xsl:template match="author.block/author.name" priority="2">
		<div>&#160;</div>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Set a line feed within the author block above the first author info in a group of author infos -->
	<xsl:template match="author.block/author.info" priority="2">
		<div></div>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Set a blank line within the author block above any subsequent author info in a group of author infos -->
	<xsl:template match="author.info[preceding-sibling::*[1][local-name()='author.info']]" priority="3">
		<div>&#160;</div>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="headnote | headnote.source | headnote.body | topic.line | key.line">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- suppress if supposed to be hidden -->
	<xsl:template match="source[@hidden]" priority="2"/>

	<!-- Suppress star pagination -->
	<xsl:template match="starpage.anchor" priority="1"/>

</xsl:stylesheet>