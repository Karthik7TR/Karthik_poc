<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SpecialCharacters.xsl"/>
	<xsl:include href="CommonInlineTemplates.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Fraction -->
	<xsl:template match="fraction">
		<xsl:variable name="numerator" select="number(descendant::numerator)" />
		<xsl:variable name="denominator" select="number(descendant::denominator)" />
		<xsl:variable name="actualFraction">
			<xsl:choose>
				<xsl:when test="$numerator = 1 and $denominator = 4">
					<xsl:text>&frac14;</xsl:text>
				</xsl:when>
				<xsl:when test="$numerator = 1 and $denominator = 2">
					<xsl:text>&frac12;</xsl:text>
				</xsl:when>
				<xsl:when test="$numerator = 3 and $denominator = 4">
					<xsl:text>&frac34;</xsl:text>
				</xsl:when>
				<xsl:when test="$numerator = 1 and $denominator = 3">
					<xsl:text>&#x2153;</xsl:text>
				</xsl:when>
				<xsl:when test="$numerator = 1 and $denominator = 8">
					<xsl:text>&#x215B;</xsl:text>
				</xsl:when>
				<xsl:when test="$numerator = 3 and $denominator = 8">
					<xsl:text>&#x215C;</xsl:text>
				</xsl:when>
				<xsl:when test="$numerator = 5 and $denominator = 8">
					<xsl:text>&#x215D;</xsl:text>
				</xsl:when>
				<xsl:when test="$numerator = 7 and $denominator = 8">
					<xsl:text>&#x215E;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="descendant::numerator"/>
					<xsl:text>/</xsl:text>
					<xsl:apply-templates select="descendant::denominator"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- Display the fraction itself -->
		<xsl:copy-of select="$actualFraction"/>
	</xsl:template>

	<xsl:template match="fraction//numerator|fraction//denominator" priority="2">
		<xsl:if test="normalize-space(.) !=''">
			<xsl:choose>
				<xsl:when test="descendant::sub or descendant::sup">
					<xsl:apply-templates/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="create-element">
						<xsl:choose>
							<xsl:when test="local-name() = 'numerator'">sup</xsl:when>
							<xsl:otherwise>sub</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:element name="{$create-element}">
						<xsl:call-template name="SpecialCharacterTranslator"/>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
