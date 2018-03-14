<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Index.xsl"/>
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Document.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<xsl:apply-templates select="." mode="CheckEasyEdit">
			<xsl:with-param name="contentType">
				<xsl:call-template name="GetCommentaryDocumentClasses"/>
				<xsl:choose>
					<xsl:when test="$IsCommentaryEnhancementMode">
						<xsl:value-of select="' &commentaryDocumentEnhancementClass;'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="' &contentTypeTreatisesClass;'"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="displayPublisherLogo" select="true()"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="n-metadata"/>

	<xsl:template match="doc">
		<div>
			<xsl:apply-templates select="/Document/n-metadata/node()" />
			<xsl:apply-templates select="node()[following-sibling::section]"/>
			<xsl:apply-templates select="section/section.front/doc.title"/>
		</div>
		<xsl:apply-templates select="section"/>
		<xsl:apply-templates select="node()[preceding-sibling::section]" />
	</xsl:template>

	<xsl:template match="section.front">
		<xsl:variable name="contents">
			<xsl:apply-templates select="node()[not(self::doc.title)]"/>
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:call-template name="wrapContentBlockWithCobaltClass">
				<xsl:with-param name="contents" select="$contents"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="md.cites" priority="2">
		<xsl:call-template name="getCitation" />
	</xsl:template>

	<xsl:template match="include.copyright" priority="1">
			<xsl:apply-templates />
	</xsl:template>

	<!-- override the begin.quote (from para.xsl) as it is rendering blockquote elements; this appears to be specific to this content type and should probably be fixed in content instead of XSL, but for now we'll apply this suppression-->
	<xsl:template match="paratext[/Document/document-data/collection = 'w_3rd_wgltreat']" priority="2">
		<xsl:call-template name="renderParagraphTextDiv"/>
	</xsl:template>

	<!-- Suppress the normal "*.cites" elements -->
	<xsl:template match="cmd.cites | md.cites" />

	<xsl:template match="doc.title[last()]" priority="1">
		<xsl:if test="$IsCommentaryEnhancementMode">
			<div class="&dividerClass;"></div>
		</xsl:if>
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
	
	<xsl:template match="prop.head[not(following::doc.title)][last()]" priority="1">
		<xsl:apply-templates />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
	
	<xsl:template match="title.block[not(//doc.title or //prop.head)]" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="database.link">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- BUG 618922 - Indent nest paragraphs -->
	<xsl:template match="para[contains('|w_3rd_wcmaids2_doc|' , concat('|', /Document/document-data/collection, '|'))]" priority="1">
			<xsl:call-template name="nestedParas"/>
	</xsl:template>
	
</xsl:stylesheet>
