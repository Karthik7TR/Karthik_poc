<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="ExpertQAndA.xsl"/>	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="question.view.block">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="short.title" mode="heading"/>
			<xsl:apply-templates select="//md.cites"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:apply-templates select="*[not(short.title)]"/>
	</xsl:template>

	<xsl:template match="question.text">
		<h2 class="&textClass;">
			<xsl:text>&question;</xsl:text>
		</h2>
		<div class="&paratextMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="answer.block | answer[parent::follow-up][1]">
		<h2 class="&textClass;">
			<xsl:text>&answers;</xsl:text>
		</h2>
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="follow-up">
		<h2 class="&textClass;">
			<xsl:text>&followup;</xsl:text>
		</h2>
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>
	
	<xsl:template match="answer.entry | expert.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<xsl:template match="answer/para/paratext">
		<div class="&paratextMainClass;">
			<i>
				<xsl:apply-templates />
			</i>
		</div>
	</xsl:template>

	<xsl:template match="expert.name | area.of.expertise | location">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	</xsl:stylesheet>