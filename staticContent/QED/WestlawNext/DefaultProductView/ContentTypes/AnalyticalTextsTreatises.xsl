<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Index.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl" />
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:value-of select="'&contentTypeAdminDecisionClass;'"/>
					<xsl:call-template name="GetCommentaryDocumentClasses">
						<xsl:with-param name="prependSpace" select="true()"/>
					</xsl:call-template>
					<xsl:call-template name="GetCommentaryDocumentEnhancementClasses">
						<xsl:with-param name="prependSpace" select="true()"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="md.cites" priority="1">
		<xsl:if test="not(/Document/n-docbody/*/content.metadata.block/cmd.identifiers/cmd.cites)">
			<xsl:variable name="displayableCites" select="md.parallelcite/md.parallelcite.info[md.display.parallelcite/@display = 'Y' or md.display.parallelcite/@userEntered = 'Y']" />
			<xsl:if test="string-length($displayableCites) &gt; 0">
				<div class="&citesClass;">
					<xsl:for-each select="$displayableCites">
						<xsl:apply-templates select="." />
						<xsl:if test="position() != last()">
							<xsl:text>,<![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:for-each>
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Parallel Cites -->
	<xsl:template match="md.parallelcite/md.parallelcite.info">
		<xsl:if test="md.display.parallelcite/@display = 'Y' or md.display.parallelcite/@userEntered = 'Y'">
			<xsl:choose>
				<xsl:when test="md.display.parallelcite">
					<xsl:apply-templates select="md.display.parallelcite" />
				</xsl:when>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="main.text.body/head">
		<xsl:choose>
			<xsl:when test="@type">
				<div >
					<xsl:apply-templates/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="&centerClass;">
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<!-- Author Block Head-->
	<xsl:template match="author.block">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Suppress Star Paging -->
	<xsl:template match="starpage.anchor" priority="2"/>

	<xsl:template match="front.matter[title.block]">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
		<xsl:if test="not(../front.matter/date.block)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<xsl:template match="endline">
		<br />
	</xsl:template>

	<!-- Fixed defect 377423, The template is copied from Web2 XSLT for Intendation-->
	<xsl:template match="para[not(ancestor::tbl)]" priority="1">
		<xsl:choose>
			<xsl:when test="parent::para">
				<div class="&paraIndentLeftClass;">
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="&paratextMainClass;">
					<xsl:apply-templates />
				</div>
				<xsl:text>&#160;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="n-docbody/decision/prelim.block/author.block/author.name">
		<xsl:apply-templates select="/Document/n-docbody/decision/front.matter"/>
		<xsl:text>&#160;</xsl:text>
		<div class="&centerClass;">
			<xsl:apply-templates />
			<xsl:text>&#160;</xsl:text>
		</div>
	</xsl:template>

</xsl:stylesheet>