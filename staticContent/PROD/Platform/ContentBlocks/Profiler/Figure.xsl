<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<xsl:template match="figure">
		<div>
			<xsl:if test="figure.body/image.block/image.link/@ttype='rtgexpert-img'">
				<xsl:variable name="attrComp" select="figure.body/image.block/image.link/@tuuid"/>
				<xsl:apply-templates select="/Document/ImageMetadata/n-metadata[@guid = $attrComp]" mode="MakeImageLink">
					<xsl:with-param name="guid" select="$attrComp"/>
				</xsl:apply-templates>
			</xsl:if>

			<xsl:if test="image.block/image.link/@ttype='rtgexpert-img'">
				<xsl:variable name="attrComp" select="image.block/image.link/@tuuid"/>
				<xsl:apply-templates select="/Document/ImageMetadata/n-metadata[@guid = $attrComp]" mode="MakeImageLink">
					<xsl:with-param name="guid" select="$attrComp"/>
				</xsl:apply-templates>
			</xsl:if>

			<xsl:if test="figure.body/image.block/image.link[@ttype='cv-pdf' or @ttype='cvgen-pdf' or @ttype='prflr-ew-pdf' or @ttype='prflr-ew-alm-pdf']">
				<xsl:call-template name="createDocumentBlobLink">
					<xsl:with-param name="guid" select="figure.body/image.block/image.link/@tuuid"/>
					<xsl:with-param name="targetType" select="figure.body/image.block/image.link/@ttype"/>
					<xsl:with-param name="mimeType" select="'&pdfMimeType;'" />
					<xsl:with-param name="contents" select="'&ewOriginalImageOfExpertResume;'"/>
					<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
					<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
					<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
					<xsl:with-param name="originationContext" select="'&docOriginalImageOriginationContext;'" />
					<xsl:with-param name="prettyName" select="figure.caption" />
				</xsl:call-template>
			</xsl:if>

			<xsl:if test="image.block/image.link[@ttype='cv-pdf' or @ttype='cvgen-pdf' or @ttype='prflr-ew-pdf' or @ttype='prflr-ew-alm-pdf']">
				<xsl:call-template name="createDocumentBlobLink">
					<xsl:with-param name="guid" select="image.block/image.link/@tuuid"/>
					<xsl:with-param name="targetType" select="image.block/image.link/@ttype"/>
					<xsl:with-param name="mimeType" select="'&pdfMimeType;'" />
					<xsl:with-param name="contents" select="'&ewOriginalImageOfExpertResume;'"/>
					<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
					<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
					<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
					<xsl:with-param name="originationContext" select="'&docOriginalImageOriginationContext;'" />
					<xsl:with-param name="prettyName" select="figure.caption" />
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:template>
	
</xsl:stylesheet>
