<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AnalysisTable.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="CustomTitleAndCourtBlock.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
 	<xsl:include href="StarPagesWithoutRules.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCPAExhibitClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadataForContentType" />
			<xsl:if test="not($PreviewMode)">
				<xsl:apply-templates select="n-metadata/metadata.block/md.references" />
				<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" />
			</xsl:if>
			<xsl:apply-templates select="n-docbody/node()"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="title.block[not(following-sibling::title.block)]" priority="2">
		<xsl:apply-templates select="../court.block"  mode="customCourtAndTitle" />
		<xsl:call-template name="titleBlock" />
		<xsl:variable name="docketDateContents">
			<xsl:apply-templates select="../docket.block" mode="customCourtAndTitle" />
			<xsl:apply-templates select="../date.block" mode="customCourtAndTitle" />
		</xsl:variable>
		<xsl:if test="string-length($docketDateContents) &gt; 0">
			<div class="&docketDateClass;">
				<xsl:copy-of select="$docketDateContents"/>
			</div>
		</xsl:if>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- Date.Line -->
	<xsl:template match="date.line | docket.line">
		<xsl:choose>
			<xsl:when test="child::justified.line">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithSpan"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="date.line/justified.line | docket.line/justified.line">
		<xsl:call-template name="wrapWithSpan"/>
	</xsl:template>

	<xsl:template match="md.related.docs"/>

	<xsl:template match="exhibit.head.block | source.doc.title.block | exhibit.reference.block | source.docket.number.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	
	<xsl:template match="label">
		<xsl:call-template name="wrapWithSpan" />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

</xsl:stylesheet>
