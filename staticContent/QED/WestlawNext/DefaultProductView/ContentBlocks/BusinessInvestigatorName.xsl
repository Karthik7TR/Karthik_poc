<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="wrapBusinessInvestigatorName">
	<xsl:param name="label" select="/.."/>
	<xsl:param name="companyName" select="/.."/>
	<xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>
	<tr class="&pr_item;">
		<th>
		<xsl:value-of select="$label"/>
		</th>
		<td>
			<xsl:choose>
        <!--xsl:when test="$searchableLink='nope' or $searchableLink='nope'"-->
        <xsl:when test="$searchableLink='true' or $searchableLink='TRUE'">
					<xsl:call-template name="CreateLinkedBusiness">
						<xsl:with-param name="companyName" select="$companyName"/>
						<xsl:with-param name="searchableLink" select="$searchableLink"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$companyName"/>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</tr>
	</xsl:template>
	
	<xsl:template name="CreateLinkedBusiness">
		<xsl:param name="companyName" select="/.."/>

		<xsl:variable name="searchUrl">
      <xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'searchType=CompanyName', concat('CompanyName=', $companyName), concat('PU=', $PermissibleUse), $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
		</xsl:variable>
		<a>
			<xsl:attribute name="class">
			<xsl:text>&pr_link;</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="href">
			<xsl:copy-of select="$searchUrl"/>
			</xsl:attribute>
			<xsl:value-of select="$companyName"/>
		</a>
	</xsl:template>
</xsl:stylesheet>