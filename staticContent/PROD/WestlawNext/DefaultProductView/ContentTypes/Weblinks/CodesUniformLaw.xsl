<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="NotesOfDecisions.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="ContextAndAnalysis.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="LinkedToc.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="Annotations.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesStatutesClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates/>
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" mode="footerCustomCitation" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="subsection//headtext">
		<div>
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>

	<!-- Suppress these two elements since they look weird. -->
	<xsl:template match="md.secondary.cites | popular.name.doc.title" />

	<xsl:template match="include.head.block">
		<div class="&headtextClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="body.footnote.block" />
	
	<!--Section : Reference block -->
	<xsl:template match="reference.block/library.reference.block | reference.block/law.review.reference.block" priority="1">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="library.reference.block//reference.text | law.review.reference.block//reference.text" priority="1">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Section: Footer Citation -->
	<xsl:template match="n-metadata/metadata.block/md.identifiers/md.cites" mode="footerCustomCitation">
		<div class="&citationClass; &centerClass;">
			<xsl:apply-templates select ="md.expandedcite"/>
		</div>
	</xsl:template>
</xsl:stylesheet>