<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="fein">
		<xsl:call-template name="FormatFein">
		</xsl:call-template>
	</xsl:template>
		
	<xsl:template name="FormatFein">
		<!-- FEIN numbers should not contain spaces -->
		<xsl:param name="feinNumber" select="normalize-space(.)"/>
		<xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>
		<!-- FEIN formatted with hyphen for display and search -->
		<xsl:variable name="formattedFein">
			<xsl:choose>
				<!-- If FEIN contains a hyphen, display as it is-->
				<xsl:when test="contains($feinNumber,'-')">
					<xsl:value-of select="$feinNumber"/>
				</xsl:when>
				<xsl:when test="string-length($feinNumber) = 9">
					<xsl:value-of select="substring($feinNumber, 1, 2)" />
					<xsl:text>-</xsl:text>
					<xsl:value-of select="substring($feinNumber, 3, 7)" />
				</xsl:when>
				<!-- If FEIN is not the required length, display as is-->
				<xsl:otherwise>
					<xsl:value-of select="$feinNumber"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$searchableLink='true' or $searchableLink='TRUE'">
				<xsl:variable name="searchUrl">
				<!-- Search implementation searches both with and without hyphen given hyphen search term -->
					<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'searchType=FEIN', concat('FEIN=', $formattedFein), concat('PU=', $PermissibleUse), $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
				</xsl:variable>
				<a>
					<xsl:attribute name="class">
						<xsl:text>&pr_link;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="$searchUrl"/>
					</xsl:attribute>
					<!-- We always wish to display the FEIN with hyphen format -->
					<xsl:copy-of select="$formattedFein"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<!-- We always wish to display the FEIN with hyphen format -->
				<xsl:copy-of select="$formattedFein"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>