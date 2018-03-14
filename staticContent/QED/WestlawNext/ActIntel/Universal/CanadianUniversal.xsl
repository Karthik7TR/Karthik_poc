<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CanadianUniversal.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:template match="urllink">
		<xsl:call-template name="CreateExternalLink">
			<xsl:with-param name="url" select="@href"/>
			<xsl:with-param name ="title" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswCurrencyKey;', '&crswCurrency;')"/>
			<xsl:with-param name="text" select="text()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Create an External Link -->
	<xsl:template name="CreateExternalLink">
		<xsl:param name="url"/>
		<xsl:param name="title"/>
		<xsl:param name="text" select="'&crswClickHere;'"/>
		<xsl:param name="hasImgChild" select="false()"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<a href="{$url}">
					<xsl:copy-of select="$text"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="string($hasImgChild) = 'true'">
						<!-- In this case the <a> encompasses the <img> tag, both of which have an onclick event handler. The img's one is in the platform code, 
             so it had to be overriden in the website_PreventImageClickAction function in Cobalt.Master.CRSW.js -->
						<a href="javascript:void(0);" class="&preventImageActionOnClickClass;" data-external-url="{$url}" data-external-title="{$title}">
							<xsl:copy-of select="$text"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<a href="javascript:void(0);" class="&linkoutShowLightboxOnClickClass;" data-external-url="{$url}" data-external-title="{$title}">
							<xsl:copy-of select="$text"/>
						</a>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="EndOfDocument" priority="1">
		<xsl:choose>
			<xsl:when test="$PreviewMode">
				<xsl:call-template name="AdditionalContent" />
				<xsl:if test="$DeliveryMode">
					<xsl:call-template name="LinkBackToDocDisplay" />
				</xsl:if>
			</xsl:when>
			<xsl:when test="not($EasyEditMode)">
				<table id="&endOfDocumentId;">
					<xsl:if test="$DeliveryMode">
						<tr>
							<td style="border-bottom: solid 1px #BBBBBB; width: 100%">&nbsp;</td>
						</tr>
					</xsl:if>
					<tr>
						<td class="&endOfDocumentCopyrightClass; &textAlignLeftClass;">
							<xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswEndOfDocumentCopyrightTextKey;', '&crswEndOfDocumentCopyrightText;')"/>
						</td>
					</tr>
				</table>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
