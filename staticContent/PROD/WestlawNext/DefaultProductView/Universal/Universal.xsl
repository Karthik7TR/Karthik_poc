<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="Universal.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="EndOfDocumentCopyright">
		<xsl:param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText"/>
		<xsl:param name="endOfDocumentCopyrightTextVerbatim" select="false()"/>
		<tr>
			<td>
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&endOfDocumentTextKey;', '&endOfDocumentText;')"/>
			</td>
			<xsl:choose>
				<xsl:when test="$endOfDocumentCopyrightTextVerbatim">			
					<xsl:choose>
						<xsl:when test="$ClarivateAnalyricsCopyright">
							<td class="&endOfDocumentCopyrightClass;">
								<p><xsl:copy-of select="$ClarivateAnalyricsCopyright"/></p>
								<p><xsl:copy-of select="$endOfDocumentCopyrightText"/></p>
							</td>
						</xsl:when>
						<xsl:otherwise>
							<td class="&endOfDocumentCopyrightClass;"><xsl:copy-of select="$endOfDocumentCopyrightText"/></td>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>	
					<xsl:choose>
						<xsl:when test="$ClarivateAnalyricsCopyright">
							<td class="&endOfDocumentCopyrightClass;">
								<p><xsl:copy-of select="$ClarivateAnalyricsCopyright"/></p>
								<p>&copy;<xsl:text><![CDATA[ ]]></xsl:text><xsl:value-of select="$currentYear"/><xsl:text><![CDATA[ ]]></xsl:text><xsl:copy-of select="$endOfDocumentCopyrightText"/></p>
							</td>
						</xsl:when>
						<xsl:otherwise>
							<td class="&endOfDocumentCopyrightClass;">&copy;<xsl:text><![CDATA[ ]]></xsl:text><xsl:value-of select="$currentYear"/><xsl:text><![CDATA[ ]]></xsl:text><xsl:copy-of select="$endOfDocumentCopyrightText"/></td>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</tr>
	</xsl:template>

	<xsl:template name="AdditionalContent">
		<xsl:choose>
			<xsl:when test="$PreviewMode and not($OutOfPlanInlinePreviewMode)">
				<div id="&additionalContentId;" class="&additionalContentClass;">&additionalContentText;</div>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="AddProductDocumentClasses" priority="1">
		<xsl:if test="($ShowCasesAndBriefsInlineKCFlag) and (//inlineKeyCiteFlag) and not($DeliveryMode) and not($IsIpad)">
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:text>&hideStateClass;</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>