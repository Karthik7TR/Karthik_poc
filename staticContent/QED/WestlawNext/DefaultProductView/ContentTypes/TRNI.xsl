<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Bin.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Suppressed - Do no show editor summary info in WLN - only used by webcontent module-->
	<xsl:template match="abstract" priority="1"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:choose>
				<xsl:when test="not($IsIpad) and not($IsIphone)">
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeBinClass;'"/>
					</xsl:call-template>
					<xsl:if test="not($DeliveryMode)">
						<xsl:call-template name="PublisherLogo" />
					</xsl:if>
					<xsl:apply-templates />
					<xsl:call-template name="EndOfDocument" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeBinClass; &coNewsClass;'"/>
					</xsl:call-template>
					<xsl:if test="not($DeliveryMode)">
						<xsl:call-template name="PublisherLogo" />
					</xsl:if>
					<xsl:apply-templates />
					<xsl:call-template name="EndOfArticle" />
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template name="PublisherLogo" priority="1">
		<xsl:if test="n-docbody/document/pub-info/src-data/src-journal-code='KPEC'">
			<xsl:call-template name="DisplayPublisherLogo">
				<xsl:with-param name="PublisherType" select="'&AlisonFrankel;'" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>