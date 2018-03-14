<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKGeneralBlocks.xsl"/>

	<xsl:variable name ="currentDocumentGuid" select="/Document/n-metadata/metadata.block/md.identifiers/md.uuid"/>
	<xsl:variable name ="locatorDocumentGuid" select="/Document/n-metadata/metadata.block/md.references/md.locatordoc[@anchorref='true']/@href"/>
	<xsl:variable name ="lawReport" select="/Document/n-metadata/metadata.block/md.references/md.fulltext[@anchorref='true']/@href"/>
	<xsl:variable name ="officialTranscript" select="/Document/n-metadata/metadata.block/md.references/md.fulltext[@anchorref='true' and @href.format='Official Transcript']/@href"/>

	<xsl:template name ="BuildDocumentTocInner">
		<xsl:param name="currentDocumentGuid" />
		<xsl:param name="currentDocumentType" />
		<xsl:param name="locatorDocumentGuid" />
		<xsl:param name="officialTranscriptGuid" />


		<xsl:call-template name="WriteTocListOpen">
			<xsl:with-param name="includeClass" select="'&coKhTocOlList; &ukResearchToCList;'"/>
		</xsl:call-template>


		<!--Case Anatysis Section-->

		<xsl:choose>
			<xsl:when test="($currentDocumentType='&caseAnalysisType;')">

				<xsl:call-template name="WriteTocItem">
					<xsl:with-param name="TocItemAnchor" select="'#'" />
					<xsl:with-param name="IsAnchor" select="false()"/>
					<xsl:with-param name="TocItemCaption" select="'&tocCaseAnalysis;'"/>
					<xsl:with-param name="TocItemClose" select="false()"/>
					<xsl:with-param name="TocItemHideBody" select="false()"/>
				</xsl:call-template>

				<xsl:choose>
					<xsl:when test="(string-length(//content/abstract) &gt; 0) ">
						<xsl:call-template name="WriteTocListOpen">
							<xsl:with-param name="includeClass" select="' '"/>
						</xsl:call-template>

						<xsl:if test="string-length(//content/abstract) &gt; 0">
							<xsl:call-template name="WriteTocItem">
								<xsl:with-param name="TocItemAnchor" select="concat('&internalLinkIdPrefix;', '&ukMainDocumentContent;')"/>
								<xsl:with-param name="TocItemCaption" select="'&tocCaseDigest;'"/>
								<xsl:with-param name="TocItemHideBody" select="false()"/>
							</xsl:call-template>
						</xsl:if>

						<xsl:call-template name="WriteTocListClose"/>
					</xsl:when>
				</xsl:choose>
				<xsl:call-template name="WriteTocItemClose"/>

			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="WriteTocItem">
					<xsl:with-param name="TocItemAnchor">
						<xsl:call-template name="GetDocumentUrl">
							<xsl:with-param name ="documentGuid" select="$locatorDocumentGuid" />
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="IsAnchor" select="false()"/>
					<xsl:with-param name="TocItemCaption" select="'&tocCaseAnalysis;'"/>
					<xsl:with-param name="TocItemHideBody" select="false()"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>

		<xsl:if test="$officialTranscript">
			<xsl:call-template name="WriteTocItem">
				<xsl:with-param name="TocItemAnchor">
					<xsl:choose>
						<xsl:when test="($currentDocumentType='&judgmentType;')">
							<xsl:value-of select="'#'" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="GetDocumentUrl">
								<xsl:with-param name ="documentGuid" select="$officialTranscript" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="IsAnchor" select="false()"/>
				<xsl:with-param name="TocItemCaption" select="'&tocJudgment;'"/>
			</xsl:call-template>
		</xsl:if>

		<xsl:call-template name="WriteTocListClose"/>
	</xsl:template>

</xsl:stylesheet>

