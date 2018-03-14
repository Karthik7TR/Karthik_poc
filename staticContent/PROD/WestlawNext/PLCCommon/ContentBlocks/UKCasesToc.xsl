<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR.
Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKCasesTocCommon.xsl"/>

	<xsl:variable name ="currentDocument" select="/Document/n-metadata/metadata.block/md.identifiers/md.uuid"/>
	<xsl:variable name ="locatorDocument" select="/Document/n-metadata/metadata.block/md.references/md.locatordoc[@anchorref='true']/@href"/>
	<xsl:variable name ="officialTranscript" select="/Document/n-metadata/metadata.block/md.references/md.fulltext[@anchorref='true' and @href.format='&officialTranscriptText;']/@href"/>

	<xsl:template name ="BuildDocumentTocContent">
		<xsl:call-template name="BuildDocumentTocInner">
			<xsl:with-param  name="currentDocumentGuid"  select="$currentDocument"/>
			<xsl:with-param  name="currentDocumentType" >
				<xsl:choose>
					<xsl:when test="($currentDocument=$officialTranscript)">
						<xsl:value-of select="'&judgmentType;'" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'&lawReportType;'" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param  name="locatorDocumentGuid" select ="$locatorDocument"/>
			<xsl:with-param  name="officialTranscriptGuid" select="$officialTranscript"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
