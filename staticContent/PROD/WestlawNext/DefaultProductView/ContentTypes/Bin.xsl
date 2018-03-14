<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Indexing.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="MobileNews.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Suppressed -->
	<xsl:template match="map|sort-pub-date|sort-pub-timestamp|rank-date|currency-timestamp|md.first.line.cite" />
	<xsl:template match="pres-subject-code|pres-industry-code|pres-location-code|pres-term-code" />
	<xsl:template match="pages" />
	<xsl:template match="copyright-year | copyright-holder"/>
	<xsl:template match="total-char-count" />
	<xsl:template match="derived-word-count" />
	<xsl:template match="word-count" />
	<xsl:template match="src-data" />
	<xsl:template match="original-pub" />
	<xsl:template match="supplier-num|supplier-dtds|supplier-note" />
	<xsl:template match="internal-acc-num" />
	<xsl:template match="rec-status" />
	<xsl:template match="nitf-meta-content" />
	<!-- 
		Remove the following indexing elements.
		The indexing.xsl should take care of this. However, add rules to explicitely 
		filter out these elements - just in case. This applies to BIN docs only.	
	-->
	<xsl:template match="company-ids" />
	<xsl:template match="rcs-block" />
	<xsl:template match="extr-company-wrap" />
	<xsl:template match="norm-company-wrap" />
	<!-- RIC code removal -->
	<xsl:template match="ric-code-wrap" />


	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:choose>
				<xsl:when test="not($IsIpad) and not($IsIphone)">
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeBinClass;'"/>
					</xsl:call-template>
					<xsl:call-template name="PublisherLogo" />
					<xsl:apply-templates />
					<xsl:call-template name="EndOfDocument" />
					<xsl:call-template name="PublisherLogo" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeBinClass; &coNewsClass;'"/>
					</xsl:call-template>
					<xsl:call-template name="PublisherLogo" />
					<xsl:apply-templates />
					<xsl:call-template name="EndOfArticle" />
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="document">
		<xsl:choose>
			<xsl:when test="not($IsIpad) and not($IsIphone)">
				<xsl:apply-templates select="pub-info"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="mobile-pub-info" >
					<xsl:with-param name="pubInfo" select="pub-info"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="content" />
		<xsl:call-template name="indexingTemplate"/>
		<xsl:apply-templates select="content/derived-word-count"/>
	</xsl:template>

	<xsl:template match="md.cites">
		<xsl:if test="not($IsIpad) and not($IsIphone)">
			<xsl:apply-templates select="md.second.line.cite"/>
			<xsl:apply-templates select="md.parallelcite"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="md.second.line.cite | md.display.parallelcite">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&citesClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pub-info">
		<xsl:apply-templates select="journal/sort-journal" />
		<xsl:apply-templates select="pub-date" />
		<xsl:apply-templates select="volume-issue-pages" />
		<xsl:apply-templates select="section" />
		<xsl:apply-templates select="preceding::title-info" />
		<xsl:apply-templates select="preceding::author-info" />
	</xsl:template>

	<xsl:template match="sort-journal">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class">&binJournalClass;</xsl:with-param>
		</xsl:call-template>
		<xsl:choose>
			<xsl:when test="following::content/publisher-copyright">
				<xsl:apply-templates select="following::content/publisher-copyright"/>
			</xsl:when>
			<xsl:when test="preceding::supplier-info/supplier-copyright">
				<xsl:apply-templates select="preceding::supplier-info/supplier-copyright"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Also defined in SimpleContentBlocks.xsl -->
	<xsl:template match="section" priority="1">
		<div class="&sectionClass;">
			<xsl:text>&sectionLabel;</xsl:text>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="volume-issue-pages">
		<div class="&volumeIssueClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="volume | issue">
		<xsl:text>&volumeLabel;</xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="issue">
		<xsl:if test="preceding-sibling::volume">
			<xsl:text>&semiColon;</xsl:text>
		</xsl:if>
		<xsl:text>&issueLabel;</xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="pub-date">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class">&pubDateClass;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="full-pub-date">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="title-info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'"/>
		</xsl:call-template>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="title">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="author-info">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="author | author-note | author-wrap">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&binAuthorClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Supplier Information -->
	<xsl:template match="supplier-copyright">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&supplierCopyrightClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="publisher-copyright">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&publisherCopyrightClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Content -->
	<xsl:template match="content">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class">&textClass;</xsl:with-param>
			<xsl:with-param name="contents">
				<xsl:apply-templates select="abstract" />
				<xsl:apply-templates select="text | note" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!--Abstract-->
	<xsl:template match="abstract" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class">
				<xsl:value-of select="concat('&paraMainClass; ', '&simpleContentBlockClass; ', '&xabstractClass;')"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="derived-word-count">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class">&wordCountClass;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="section-title">
		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class">&underlineClass;</xsl:with-param>
			<xsl:with-param name="contents">
				<strong>
					<xsl:apply-templates />
				</strong>
			</xsl:with-param>
		</xsl:call-template>
		<br/>
	</xsl:template>

	<xsl:template match="text[@type='links']/p/a">
		<xsl:variable name="extension" select="substring(@href, string-length(@href)-3)"/>
		<xsl:choose>
			<!-- Supress link with common image file extensions -->
			<xsl:when test="$extension = '.jpg' or $extension = '.gif' or $extension = '.bmp' or $extension = '.png' or contains($extension, '.tif')"></xsl:when>
			<xsl:otherwise>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@href" />
					</xsl:attribute>
					<xsl:value-of select="text()"/>
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="br">
		<br />
	</xsl:template>

	<xsl:template name="PublisherLogo">
		<xsl:choose>
			<xsl:when test="n-metadata/metadata.block/md.publications/md.sourcepubid='WLBUSCURRENTS'">
				<!-- Do not display publisher logo for NY Commentaries -->
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="DisplayPublisherLogo">
					<xsl:with-param name="PublisherType" select="'&PublisherBIN;'" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>