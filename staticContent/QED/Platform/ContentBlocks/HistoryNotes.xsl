<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<xsl:template match="annotations/hist.note.block">
		<div class="&historyNotesClass; &disableHighlightFeaturesClass;" id="&historyNotesId;">
			<xsl:apply-templates select="head/head.info/headtext"/>
			<xsl:apply-templates select="hist.note.body" />
		</div>
	</xsl:template>

	<xsl:template match="hist.note.body">
		<xsl:apply-templates select="head/head.info/headtext"/>
			<xsl:if test="hist.note">
				<ul>
					<xsl:apply-templates select="hist.note"/>
				</ul>
			</xsl:if>
	<xsl:apply-templates select="hist.note.body" />
	</xsl:template>
	
	<xsl:template match="hist.note">
		<li>
			<xsl:apply-templates select="paratext"/>
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="contents">
					<xsl:apply-templates select="para"/>
				</xsl:with-param>
			</xsl:call-template>
		</li>
	</xsl:template>
	
</xsl:stylesheet>