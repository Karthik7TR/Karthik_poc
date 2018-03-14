<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="DocLinks.xsl" forcePlatform="true"/>
	<xsl:include href="UKGeneralBlocks.xsl"/>

	<xsl:template name="BuildHMRCArrangementTocContent">
		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor" select="'#'"/>
			<xsl:with-param name="IsAnchor" select="false()"/>
			<xsl:with-param name="TocItemCaption" select="'&arrangementOfManualText;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="BuildHMRCManualTocContent">
		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor" select="'#'"/>
			<xsl:with-param name="IsAnchor" select="false()"/>
			<xsl:with-param name="TocItemCaption" select="'&HMRCManualTextHeading;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="BuildDocumentTocContent">
		<xsl:call-template name="WriteTocListOpen">
			<xsl:with-param name="includeClass" select="'&coKhTocOlList; &ukResearchToCList;'"/>
		</xsl:call-template>

		<xsl:choose>
			<xsl:when test="$documentType='&HRMCArrangementType;'">
				<xsl:call-template name="BuildHMRCArrangementTocContent" />
			</xsl:when>
			<xsl:when test="$documentType='&HRMCManualType;'" >
				<xsl:call-template name="BuildHMRCManualTocContent" />
			</xsl:when>
		</xsl:choose>

		<xsl:call-template name="WriteTocListClose"/>
	</xsl:template>

	<xsl:template name ="anchor">
		<xsl:variable name="positionId">
			<xsl:number count="title" format="1" level="any"/>
		</xsl:variable>
		<xsl:value-of select="concat('&internalLinkIdPrefix;', $positionId)"/>
	</xsl:template>

</xsl:stylesheet>