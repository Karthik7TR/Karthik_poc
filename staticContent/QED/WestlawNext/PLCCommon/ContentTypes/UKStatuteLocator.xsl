<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKStatuteLocator.xsl" forceDefaultProduct="true"/>
	<xsl:include href="UkStatutesDocumentType.xsl"/>
	<xsl:include href="UKStatuteLocatorToc.xsl"/>
	<xsl:include href="UKStatutesHeader.xsl" />

	<xsl:variable name ="contentType" select ="'&ukWestlawContent;'"/>
	<xsl:variable name ="showLoading" select="true()"/>
	<xsl:variable name ="fullTextGuid" select="//n-docbody/document/metadata.block/md.references/md.fulltext/@href"/>

	<!--Document structure-->
	<xsl:template name="BuildSpecificDocument">
		<xsl:call-template name="OneColumnDocument" />
	</xsl:template>

	<xsl:template name="BuildDocumentTypeAttribute">
		<xsl:attribute name ="data-documenttype">
			<!--defined in UkStatuteLocatorToc.xsl-->
			<xsl:value-of select ="$documentType" />
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="BuildDocumentBody">
		<xsl:call-template name="AttachedFileForDocument"/>
		<input type="hidden" id="&coLegislationFullTextId;" value="{$fullTextGuid}" />
		<a id="&ukReferencesOffset;"></a>
	</xsl:template>

	<xsl:template name="AttachedFileForDocument">
		<xsl:if test="//image.block">
			<xsl:variable name="contents">
				<xsl:choose>
					<xsl:when test="$infoType = '&legisActLocTextType;' or $infoType = '&legisScottishActTextType;'">
						<xsl:value-of select="'&viewPdfOfEntireActText;'"/>
					</xsl:when>
					<xsl:when test="$infoType = '&legisSiLocTextType;' or $infoType = '&legisScottishSiTextType;'">
						<xsl:value-of select="'&viewPdfOfEntireSIText;'"/>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>

			<div class="&standardDocAttachment; &hideState;">
				<xsl:call-template name="createDocumentBlobLink">
					<xsl:with-param name="guid" select="//image.block/image.link/@tuuid"/>
					<xsl:with-param name="targetType" select="'&inlineParagraph;'"/>
					<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>