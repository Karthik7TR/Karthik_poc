<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Image.xsl" forcePlatform="true" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<!-- Create an image element -->
	<xsl:template match="graphic[@format = 'image/png']/link[@tuuid]" priority="1">
		<xsl:variable name="src">
			<xsl:call-template name="createBlobLink">
				<xsl:with-param name="guid" select="@tuuid" />
				<xsl:with-param name="targetType" select="'&inlineTargetType;'" />
				<xsl:with-param name="mimeType" select="'&xPngMimeType;'" />
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="buildBlobImageElement">
			<xsl:with-param name="alt" select="'&imagePlaceholderAltText;'" />
			<xsl:with-param name="src" select="$src" />
			<xsl:with-param name="class" select="'&imageClass;'" />
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
