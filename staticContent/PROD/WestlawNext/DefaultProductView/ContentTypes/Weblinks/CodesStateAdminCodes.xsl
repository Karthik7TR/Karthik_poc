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
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Annotations.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesStateAdminCodesClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates/>
			<xsl:apply-templates select="n-docbody/section/content.metadata.block/cmd.identifiers/cmd.cites/cmd.expandedcite" mode="bottom"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="node()[prelim.block or content.metadata.block or doc.title][1]" priority="1">
		<xsl:call-template name="renderCodeStatuteHeader"/>
	</xsl:template>

	<xsl:template match="doc.title" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="n-docbody/section/content.metadata.block/cmd.identifiers/cmd.cites/cmd.expandedcite" mode="bottom">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<xsl:template match="ed.note.grade">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="include.copyright.block/include.copyright[@n-include_collection = 'w_codes_stamsgp']" priority="1">
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="." />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="popular.name.doc.title"/>

</xsl:stylesheet>
