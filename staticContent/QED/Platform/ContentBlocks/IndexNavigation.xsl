<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CommentaryIndex.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:variable name="indexNavigationSymbolsUppercase" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&indexNavigationSymbolsUppercaseKey;', '&indexNavigationSymbolsUppercase;')"/>

	<xsl:template match="index/index.entry/subject" name="indexNavigationSubject">
		<xsl:variable name="symbol" select="translate(substring(normalize-space(.),1,1), '&alphabetLowercase;', '&alphabetUppercase;')"/>
		<xsl:if test="not(../preceding-sibling::index.entry/subject[translate(substring(normalize-space(.),1,1), '&alphabetLowercase;', '&alphabetUppercase;') = $symbol])">
			<xsl:call-template name="RenderNavigationLink">
				<xsl:with-param name="symbol" select="$symbol"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="index/doc.title" name="indexNavigationDocTitle">
		<xsl:variable name="symbol" select="translate(substring(normalize-space(.),1,1), '&alphabetLowercase;', '&alphabetUppercase;')"/>
		<xsl:if test="not(../preceding-sibling::index/doc.title[translate(substring(normalize-space(.),1,1), '&alphabetLowercase;', '&alphabetUppercase;') = $symbol])">
			<xsl:call-template name="RenderNavigationLink">
				<xsl:with-param name="symbol" select="$symbol"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:call-template name="indexDocTitle"/>
	</xsl:template>

	<xsl:template name="RenderNavigationLink">
		<xsl:param name="symbol"/>
		<a>
			<xsl:attribute name="id">
				<xsl:value-of select="concat('&indexNavigationLinkIdPrefix;', $symbol)"/>
			</xsl:attribute>
		</a>
	</xsl:template>

	<xsl:template name="RenderIndexNavigationList">
		<xsl:if test="not($DeliveryMode)">
			<div class="&indexNavigationClass;">
				<ul class="&indexInlineListClass;">
					<xsl:call-template name="RenderIndexNavigationListItem"/>
				</ul>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderIndexNavigationListItem">
		<xsl:param name="indexSymbols" select="$indexNavigationSymbolsUppercase"/>
		<xsl:param name="position" select="1"/>
		<xsl:if test="$position &lt;= string-length($indexSymbols)">
			<xsl:variable name="indexSymbol" select="substring($indexSymbols,$position,1)"/>
			<li>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="concat('#&indexNavigationLinkIdPrefix;', $indexSymbol)"/>
					</xsl:attribute>
					<xsl:if test="not(//index/index.entry/subject[translate(substring(normalize-space(.),1,1), '&alphabetLowercase;', '&alphabetUppercase;') = $indexSymbol]) and not (//index/doc.title[translate(substring(normalize-space(.),1,1), '&alphabetLowercase;', '&alphabetUppercase;') = $indexSymbol])">
						<xsl:attribute name="class">
							<xsl:text>co_disabled</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="$indexSymbol"/>
				</a>
			</li>
			<xsl:call-template name="RenderIndexNavigationListItem">
				<xsl:with-param name="indexSymbols" select="$indexSymbols"/>
				<xsl:with-param name="position" select="$position + 1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
