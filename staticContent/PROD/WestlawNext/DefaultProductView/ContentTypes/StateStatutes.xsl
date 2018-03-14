<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters.All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="NotesOfDecisions.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Prelim.xsl"/>	
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="ContextAndAnalysis.xsl"/>
	<xsl:include href="HistoryNotes.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">	
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesStatutesClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:apply-templates select="n-docbody//content.metadata.block[position() &gt; 1]/cmd.identifiers/cmd.cites/cmd.expandedcite" mode="expandedcite"/>
			<xsl:apply-templates select="n-docbody//include.currency.block" mode="currency"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="n-docbody/*">
		<xsl:call-template name="renderStatuteHeader"/>
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id">
				<xsl:if test="@ID|@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="contents">
				<xsl:apply-templates select="*[not(self::prelim.block or self::content.metadata.block[1] or self::doc.title)]"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="renderStatuteHeader">
		<xsl:if test="prelim.block or content.metadata.block or doc.title">
			<div class="&documentHeadClass;">
				<xsl:apply-templates select="prelim.block" mode="heading"/>
				<xsl:apply-templates select="content.metadata.block[1]/cmd.identifiers/cmd.cites" mode="cite"/>
				<xsl:apply-templates select="doc.title" mode="title"/>
			</div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<xsl:template match="cmd.cites">
		<xsl:if test="$PreviewMode">
			<xsl:apply-templates select="." mode="cite"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="cmd.cites" mode="cite">
		<div class="&citesClass;">
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

	<xsl:template match="doc.title" priority="1">
		<xsl:if test="$PreviewMode">
			<xsl:apply-templates select="." mode="title"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="doc.title" mode="title">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hide.historical.version[internal.reference]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="prelim.block">
		<xsl:if test="$PreviewMode">
			<xsl:apply-templates select="." mode="heading"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="prelim.block" mode="heading">
		<xsl:variable name="head">
			<xsl:apply-templates select="prelim.head/head"/>
		</xsl:variable>
		<xsl:variable name="subHead">
			<xsl:apply-templates select="prelim.head/prelim.head"/>
		</xsl:variable>
		<xsl:if test="string-length($head) &gt; 0">
			<div class="&genericBoxClass;">
				<div class="&genericBoxHeaderClass;">
					<span></span>
				</div>
				<div class="&genericBoxContentClass;">
					<div class="&genericBoxContentRightClass;">
						<xsl:if test="string-length($subHead) &gt; 0">
							<a class="&widgetCollapseIconClass;" href="#"></a>
						</xsl:if>
						<div class="&simpleContentBlockClass; &prelimBlockClass;">
							<xsl:copy-of select="$head"/>
							<xsl:if test="string-length($subHead) &gt; 0">
								<div id="&prelimContainerId;">
									<xsl:copy-of select="$subHead"/>
								</div>
							</xsl:if>
						</div>
					</div>
				</div>
				<div class="&genericBoxFooterClass;">
					<span></span>
				</div>
			</div>
		</xsl:if>

	</xsl:template>

	<xsl:template match="n-docbody/section/content.metadata.block[position() &gt; 1]/cmd.identifiers/cmd.cites/cmd.expandedcite" mode="expandedcite">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>
	
	<xsl:template match="hide.historical.version" />

	<xsl:template match="hide.historical.version[cite.query]" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="include.currency.block" />
	
	<xsl:template match="include.currency.block" mode="currency">
		<xsl:if test="/Document/document-data/versioned = 'False' or (/Document/document-data/versioned = 'True' and 
														/Document/document-data/datetime &gt; /Document/n-metadata/metadata.block/md.dates/md.starteffective and 
														/Document/document-data/datetime &lt; /Document/n-metadata/metadata.block/md.dates/md.endeffective)">
			<xsl:call-template name="wrapContentBlockWithCobaltClass">
				<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;',currency.id/@ID)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
<!-- Fix for Bug #432627 to include the disposition table in the doc-->
	<xsl:template match="include.ed.note.grade" />
	<xsl:template match="n-docbody/refs.annos/annotations/ed.note.grade">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and $StatutoryTextOnly" />
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<xsl:template match="ed.note.text">
		<div class="&printHeadingClass;">
			<h2>&editorsNotes;</h2>
		</div>
		<xsl:apply-templates />
	</xsl:template>

</xsl:stylesheet>

