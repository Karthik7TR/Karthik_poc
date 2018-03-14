<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKGeneralBlocks.xsl"/>

	<xsl:template name ="BuildDocumentTocContent">
		<xsl:call-template name="WriteTocListOpen"/>

		<xsl:if test="descendant::body/legal.update/list/list.item">
			<xsl:call-template name="WriteTocItem">
				<xsl:with-param name="TocItemAnchor" select="'&legalUpdatesAnchorText;'"/>
				<xsl:with-param name="TocItemCaption" select="'&legalUpdatesSecondaryMenu;'"/>
			</xsl:call-template>
		</xsl:if>

		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor" select="'&caseLinksAnchorText;'"/>
			<xsl:with-param name="TocItemCaption" select="'&caseLinksSecondaryMenu;'"/>
			<xsl:with-param name="TocItemClass" select="'&linksToPageLink;'"/>
		</xsl:call-template>

		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor" select="'&khRelatedContentOffset;'"/>
			<xsl:with-param name="TocItemCaption" select="'&caseContentLinksSecondaryMenu;'"/>
			<xsl:with-param name="TocItemClass" select="'&coRelatedContentQuickLink;'"/>
		</xsl:call-template>

		<xsl:call-template name="WriteTocListClose"/>
	</xsl:template>
	
</xsl:stylesheet>
