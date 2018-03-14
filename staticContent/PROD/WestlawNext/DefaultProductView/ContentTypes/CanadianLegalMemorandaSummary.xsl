<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n Not Required As Of 6/27/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>	
	<xsl:include href="CanadianFootnotes.xsl"/>
	<xsl:include href="CanadianCites.xsl"/>
	<xsl:include href="CanadianLegalMemo.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--Do not render these-->
	<xsl:template match="headers"/>
	<xsl:template match="lmdocbody"/>
	<xsl:template match="prelims"/>
	<xsl:template match="message.block.carswell" />

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswLegalMemo;'"/>
			</xsl:call-template>
		
			<xsl:variable name="legalmemosummary" select="n-docbody/legalmemorandum/legalmemosummary"/>

			<div class="&headnotesClass; &centerClass;">
				<xsl:apply-templates select="$legalmemosummary/doc_heading/prelims/prelimtype"/>
				<xsl:apply-templates select="$legalmemosummary/doc_heading/prelims/prelimcite"/>
				<xsl:apply-templates select="$legalmemosummary/doc_heading/prelims/prelimsize"/>
				<xsl:apply-templates select="$legalmemosummary/doc_heading/prelims/prelimjuris"/>
				<xsl:apply-templates select="$legalmemosummary/doc_heading/prelims/prelimdate"/>
				<xsl:apply-templates select="$legalmemosummary/legalmemolinks//footnote.reference"/>
			</div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			
			<xsl:apply-templates select="$legalmemosummary"/>
			<xsl:call-template name="RenderFootnoteSection"/>
      <xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="prelimsize | prelimjuris | prelimdate">
		<xsl:call-template name="wrapWithDiv">
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="prelimtype">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="prelimcite">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&citesClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="legalissue" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass; &crswGrayBox;'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
