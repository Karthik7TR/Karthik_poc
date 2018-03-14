<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:include href="CanadianCites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="CanadianFootnotes.xsl"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswLegislativeConcordances;'"/>
			</xsl:call-template>

			<div class="&citesClass;">
				<xsl:apply-templates select="document-data/cite"/>
			</div>
			<div class="&headnotesClass; &centerClass;">
				<xsl:apply-templates select="n-docbody/index/doc_heading/doc_title"/>
				<xsl:apply-templates select="n-docbody/index/doc_heading/toc_headings"/>
			</div>

			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<!-- Render Document Content (Skip footnotes) -->
			<xsl:apply-templates select="n-docbody/index/index.block/index.body/node()[not(./preceding-sibling::p[./sup/a][1]//sup/a[starts-with(@name,'f')] or .//sup/a[starts-with(@name,'f')])]"/>

			<!-- Render footnotes -->
			<xsl:call-template name="RenderFootnoteSection"/>

			<!--Get the Carswell End of Document-->
			<xsl:call-template name="EndOfDocument"/>

		</div>
	</xsl:template>

	<!-- Do not render -->
	<xsl:template match="message.block.carswell" />

	<xsl:template match="toc_headings">
		<xsl:for-each select="*">
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="'&paraMainClass;'" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="cite">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass; &crswSmallText;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="index.body">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="doc_title">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass; &titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- *************************** New legis concord layout ******************************************** -->
	<!-- Block the whole list into one element and set padding -->

	<xsl:template match="list_items">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Parse all nodes that start with list_item_ and handle n, and numeric cases separately -->
	<xsl:template match="*[starts-with(local-name(), 'list_item_')]">

		<!-- Numeric Cases -->
		<xsl:if test="not(substring-after(local-name(),'list_item_')='n')">
			<!-- Make sure a new line occurs for a new bullet -->
			<!-- First layer of bullets, no indent-->
			<xsl:if test="number(substring-after(local-name(),'list_item_'))=1">
				<div class="&paraMainClass; &paraIndentLeftClass; &indentLeft1Class;">
					<xsl:text>&bull;<![CDATA[ ]]></xsl:text>
					<xsl:text>&#160;<![CDATA[ ]]></xsl:text>
					<xsl:apply-templates/>
				</div>
			</xsl:if>
			<xsl:if test="not(number(substring-after(local-name(),'list_item_'))=1)">
				<div class="&paraMainClass; &paraIndentLeftClass; &indentLeft2Class;">
					<xsl:text>&bull;<![CDATA[ ]]></xsl:text>
					<xsl:text>&#160;<![CDATA[ ]]></xsl:text>
					<xsl:apply-templates />
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!--Content that is to go into a list item-->
	<xsl:template match="list" priority="2">
		<xsl:apply-templates/>
	</xsl:template>



</xsl:stylesheet>