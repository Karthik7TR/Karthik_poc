<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Finra.xsl" forceDefaultProduct="true" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeRegulatoryGuidanceSummaryClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata"/>
			<xsl:apply-templates/>
			<xsl:if test="not($IsRuleBookMode)">
				<xsl:apply-templates select="n-docbody//content.metadata.block" mode="footerCustomCitation" />
				<!-- Copyright block -->
				<xsl:call-template name="EndOfDocument">
					<xsl:with-param name="endOfDocumentCopyrightText" select="'&nonUSGovernmentCopyrightText;'" />
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$IsRuleBookMode">
				<xsl:apply-templates select="/" mode="Custom"/>
			</xsl:if>
		</div>
	</xsl:template>
</xsl:stylesheet>
