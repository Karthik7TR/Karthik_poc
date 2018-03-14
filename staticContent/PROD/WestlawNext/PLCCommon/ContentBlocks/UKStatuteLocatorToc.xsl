<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKGeneralBlocks.xsl"/>
	<xsl:include href="UkStatutesDocumentType.xsl"/>

	<xsl:template name ="BuildDocumentTocContent">
		<xsl:call-template name="WriteTocListOpen">
			<xsl:with-param name="includeClass" select="'&coKhTocOlList; &ukResearchToCList;'"/>
		</xsl:call-template>
		<xsl:choose>
			<xsl:when test="$isArrangementDocument=true()">
				<xsl:call-template name="ArrangementToc" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="ProvisionToc" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:call-template name="WriteTocListClose"/>
	</xsl:template>

	<xsl:template name ="ProvisionToc">
		<xsl:variable name ="provisionTitle" >
			<xsl:choose>
				<xsl:when test="$legisType='&legisTypeBill;'">
					<xsl:value-of select="$legisType"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&provisionPrimaryMenu;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!--provision-->
		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor">
				<xsl:call-template name="GetDocumentUrl">
					<xsl:with-param name ="documentGuid" select="//n-docbody/document/metadata.block/md.references/md.fulltext/@href" />
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="IsAnchor" select="false()"/>
			<xsl:with-param name="TocItemCaption" select="$provisionTitle"/>
		</xsl:call-template>

		<!--provision details -->
		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor" select="'#'" />
			<xsl:with-param name="IsAnchor" select="false()"/>
			<xsl:with-param name="TocItemCaption" select="concat($provisionTitle,'&detailsPrimaryMenu;')"/>
			<xsl:with-param name="TocItemHideBody" select="true()"/>
		</xsl:call-template>

	</xsl:template>

	<xsl:template name ="ArrangementToc">
		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor">
				<xsl:call-template name="GetDocumentUrl">
					<xsl:with-param name ="documentGuid" select="//n-docbody/document/metadata.block/md.references/md.fulltext/@href" />
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="IsAnchor" select="false()"/>
			<xsl:with-param name="TocItemCaption" select="concat('&arrangmentOfPrimaryMenu;',$legisType)"/>
			<xsl:with-param name="TocItemHideBody" select="false()"/>
		</xsl:call-template>

		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor" select="'#'" />
			<xsl:with-param name="IsAnchor" select="false()"/>
			<xsl:with-param name="TocItemCaption" select="concat($legisType,'&detailsPrimaryMenu;')"/>
			<xsl:with-param name="TocItemHideBody" select="true()"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
