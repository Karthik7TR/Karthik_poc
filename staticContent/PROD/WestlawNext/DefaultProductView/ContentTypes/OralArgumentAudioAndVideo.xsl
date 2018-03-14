<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Generic.xsl"/>	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>			
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="prop.block" priority="1">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="docket.block | date.block | message.block" priority="5">		
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates/>			
		</div>			
	</xsl:template>
	
	<xsl:template match="attorney.block">
		<xsl:if test="attorney.line">
			<div class="&alignHorizontalLeftClass;">
				<xsl:apply-templates select="attorney.line"/>
			</div>
			<xsl:text>&#160;</xsl:text>
		</xsl:if>
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates select="plaintiff.attorney.line"/>			
		</div>
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates select="defendant.attorney.line"/>			
		</div>		
	</xsl:template>

	<xsl:template match="message.block[/Document/document-data/collection = 'w_lt_td_avtt']" priority="5">
		<!--<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates select="message.line"/>
		</div>-->
		<xsl:apply-templates/>
	</xsl:template>

</xsl:stylesheet>

