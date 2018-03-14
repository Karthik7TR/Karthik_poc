<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*" />

	<!-- Suppress cites that are not full cites -->
	<xsl:template match="/FormattedCites/Cite" />

	<xsl:template match="/FormattedCites/Cite[@citeType = 'FULL_CITE']">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="/FormattedCites/Cite/CiteText//Title">
		<xsl:call-template name="wrapWithFormattedStyles">
			<xsl:with-param name="stylingElement" select="/FormattedCites/Formatting/Title" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/FormattedCites/Cite/CiteText//IntraPhrase">
		<xsl:call-template name="wrapWithFormattedStyles">
			<xsl:with-param name="stylingElement" select="/FormattedCites/Formatting/IntraPhrase" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/FormattedCites/Cite/CiteText//HistoryPhrase">
		<xsl:call-template name="wrapWithFormattedStyles">
			<xsl:with-param name="stylingElement" select="/FormattedCites/Formatting/HistoryPhrase" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/FormattedCites/Cite/CiteText//SpecialRefKwd">
		<xsl:call-template name="wrapWithFormattedStyles">
			<xsl:with-param name="stylingElement" select="/FormattedCites/Formatting/SpecialRefKwd" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/FormattedCites/Cite/CiteText//Pub">
		<xsl:call-template name="wrapWithFormattedStyles">
			<xsl:with-param name="stylingElement" select="/FormattedCites/Formatting/Pub" />
		</xsl:call-template>
	</xsl:template>

	<!-- Callable template to output various wrapping elements for styling -->
	<xsl:template name="wrapWithFormattedStyles">
		<xsl:param name="stylingElement" />
		<xsl:variable name="contentsAfterItalic">
			<xsl:choose>
				<xsl:when test="$stylingElement/@italic = 'Y'">
					<em>
						<xsl:apply-templates />
					</em>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="contentsAfterUnderline">
			<xsl:choose>
				<xsl:when test="$stylingElement/@underline = 'Y'">
					<span style="text-decoration: underline;">
						<xsl:copy-of select="$contentsAfterItalic" />
					</span>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$contentsAfterItalic" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:copy-of select="$contentsAfterUnderline" />
	</xsl:template>

</xsl:stylesheet>