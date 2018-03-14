<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" mode="CheckEasyEdit">
		<xsl:param name="contentType" />
		<xsl:param name="displayPublisherLogo" select="false()"/>
		<xsl:param name="citationText" />
		<div id="&documentId;">		
			<xsl:choose>
				<xsl:when test="string-length($contentType) &gt; 0">
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="$contentType"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="AddDocumentClasses" />
				</xsl:otherwise>
			</xsl:choose>
			
			<xsl:if test="string-length($citationText) &gt; 0">
				<xsl:call-template name="DisplayCitation">
					<xsl:with-param name="citation" select="$citationText"/>
				</xsl:call-template>
			</xsl:if>
			
			<xsl:if test="$displayPublisherLogo">
				<xsl:call-template name="DisplayPublisherLogo"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$EasyEditMode">
					<xsl:apply-templates select="node()" mode="EasyEdit"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="//md.form.flag">
						<xsl:call-template name="EasyEditFlag"/>
					</xsl:if>
					<xsl:call-template name="StarPageMetadata" />
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
			
			<xsl:if test="string-length($citationText) &gt; 0">
				<xsl:call-template name="DisplayCitation">
					<xsl:with-param name="citation" select="$citationText"/>
				</xsl:call-template>
			</xsl:if>
			
			<xsl:call-template name="EndOfDocument" />
			<xsl:if test="$displayPublisherLogo">
				<xsl:call-template name="DisplayPublisherLogo"/>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="DisplayCitation">
		<xsl:param name="citation" />
		<div class="&citationClass;">
			<xsl:value-of	select="$citation"	/>
		</div>
	</xsl:template>
	
</xsl:stylesheet>
