<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AdminDecision.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata"/>
			<xsl:call-template name="primaryCite"/>
			<div class="&centerClass;">
				<h2>Arbitrator Resume</h2>
			</div>

			<xsl:apply-templates />

			<xsl:call-template name="EndOfDocument"/>
		</div>
	</xsl:template>

	<xsl:template name="primaryCite">
		<xsl:apply-templates select="//md.display.primarycite"/>
	</xsl:template>
	
	<xsl:template match="md.primarycite/md.primarycite.info"/>

	<!-- Div id requested by testers to assist with automated testing -->
	<xsl:template match="decision/arbitrator.block/arbitrator.name">
		<div id="co_arbitratorName">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="head[following-sibling::para]">
		<div>
			<xsl:call-template name="head"/>
		</div>
	</xsl:template>

	<xsl:template match="//para[@ampexmnem='dpa0']" priority="1">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>