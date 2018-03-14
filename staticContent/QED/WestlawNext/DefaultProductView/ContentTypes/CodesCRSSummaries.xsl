<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CitesFromContentMetaData.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Don't display these -->
	<xsl:template match="map|iso.date"/>
	<xsl:template match="n-metadata | version.date.line | citator.treatment | hidden.img.text"/>

	<!-- Document Display -->
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&codesCRSSummariesClass;'"/>
			</xsl:call-template>
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Render Doc -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select ="descendant::cmd.first.line.cite"/>
		<xsl:apply-templates select ="descendant::cmd.second.line.cite"/>
		<xsl:apply-templates select ="descendant::cmd.third.line.cite"/>
		<xsl:apply-templates select ="descendant::prelim.head"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:apply-templates select ="descendant::bill.number"/>
		<xsl:apply-templates select ="descendant::doc.title"/>
		<xsl:apply-templates select ="descendant::author.block"/>
		<xsl:apply-templates select ="descendant::latest.action"/>
		<xsl:apply-templates select ="descendant::status"/>
		<xsl:apply-templates select ="descendant::body"/>
	</xsl:template>

	<!-- Header section -->
	<xsl:template match="cmd.second.line.cite | cmd.first.line.cite | cmd.third.line.cite | prelim.head">
		<xsl:if test ="not(preceding-sibling::prelim.head)">
			<div>&nbsp;</div>
			<div class="&alignHorizontalCenterClass;">
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Remove Inherited Formatting -->
	<xsl:template match="head | headtext | summary/front/author.block">
		<xsl:apply-templates/>
	</xsl:template>


	<xsl:template match="doc.title | sponsor.line | latest.action | status | co.sponsors | bill.number | body">
		<div>&nbsp;</div>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="co.sponsor.line">
		<xsl:if test="not(preceding-sibling::co.sponsor.line)">
			<div>&nbsp;</div>
		</xsl:if>
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="date.line">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="n-docbody/summary/body/para">
		<xsl:if test="not(preceding-sibling::para)">
			<div>&nbsp;</div>
		</xsl:if>
		<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
