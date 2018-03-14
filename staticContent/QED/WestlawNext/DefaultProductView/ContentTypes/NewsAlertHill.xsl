<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
					<xsl:value-of select="' &contentTypeNewsAlertHillClass;'"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite"/>
			<xsl:call-template name="displayCopyright"/>
			<xsl:apply-templates select="n-metadata/metadata.block/md.publications/md.publication/md.pubname"/>
			<xsl:apply-templates select="n-docbody/news.alert.document/article/article.title"/>
			<xsl:apply-templates select="n-docbody/news.alert.document/article/article.date"/>
			<xsl:apply-templates select="n-docbody/news.alert.document/article/author.byline"/>
			<div id="&textClass;">
				<xsl:apply-templates select="n-docbody/news.alert.document/article/lead.para"/>
				<xsl:apply-templates select="n-docbody/news.alert.document/article/para"/>
			</div>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&citesClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="n-metadata/metadata.block/md.publications/md.publication/md.pubname">
		<xsl:call-template name="wrapWithDiv" >
			<xsl:with-param name="class" select="'&binJournalClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="n-docbody/news.alert.document/article/article.title">
		<xsl:call-template name="wrapWithDiv" >
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="n-docbody/news.alert.document/article/article.date">
		<xsl:call-template name="wrapWithDiv" >
			<xsl:with-param name="class" select="'&pubDateClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="n-docbody/news.alert.document/article/author.byline">
		<xsl:call-template name="wrapWithDiv" >
			<xsl:with-param name="class" select="'&authorBylineClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="n-docbody/news.alert.document/article/lead.para">
		<xsl:call-template name="wrapWithDiv" >
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="n-docbody/news.alert.document/article/lead.para/paratext">
		<xsl:apply-templates />
	</xsl:template>

	<!--Additional copyright message for Hill documents-->
	<xsl:template name="displayCopyright">
		<xsl:variable name="copyright_node" select="concat('Copyright','&copy; ', $currentYear, ' &hillCopyright;') "/>
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="$copyright_node"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
