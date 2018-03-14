<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<!--This DefaultProduct KnowHow.xsl refers to DefaultProductView/Enterprise/ContentBlocks/KnowHow.xsl-->
	<xsl:include href="KnowHow.xsl" forcePlatform="true"/>
	
	<xsl:template match="n-docbody" priority="2">
		<xsl:call-template name="BuildDocumentHeader"/>
		<div id="&coDocContentBody;">
			<div>
				<xsl:apply-templates select="*/abstract | abstract"/>
				<xsl:apply-templates select="*/selection.block/jur.select" />
				<!-- Moving the internal toc below the abstract as per the story : 838597 -->
				<xsl:if test="$DeliveryMode or $IsMobile">
					<xsl:apply-templates select="*/internal.toc" />
				</xsl:if>
				<xsl:call-template name="DisplayJumpLinks" />
				<xsl:apply-templates select="*/summary"/>
				<xsl:apply-templates select="*/related.drafting.notes"/>
				<xsl:if test="$IsUsWhatsMarketCollection = 'false' and $IsStandardDocument = 'false'">
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				</xsl:if>
			</div>
			<xsl:if test="$IsUsWhatsMarketCollection = 'true' or $IsStandardDocument = 'true'">
				<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$ShowDocument">
					<xsl:apply-templates select="*/body" />
					<xsl:call-template name="AttachedFileForEmptyDocument" />
				</xsl:when>
				<xsl:otherwise>
					<!-- used for DraftingNotesOnly delivery option. If we're not showing the document, we're showing the drafting notes -->
					<xsl:apply-templates select="//drafting.note"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="*/rev.history" />
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText" select="'&nonUSGovernmentCopyrightText;'" />
			</xsl:call-template>
		</div>
		<xsl:if test="not($DeliveryMode or $IsMobile)">
			<xsl:apply-templates select="*/internal.toc" />
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
