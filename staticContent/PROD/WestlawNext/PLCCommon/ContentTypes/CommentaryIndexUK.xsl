<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="CommentaryIndex.xsl"/>
	<xsl:include href="IndexNavigation.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&commentaryDocumentClass; &commentaryDocumentEnhancementClass;'"/>
			</xsl:call-template>
			<div id="&coDocContentHeaderPrelim;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;" >
				<div id="&codocumentHeaderBorder;" class="&documentHeaderBorderClass;"></div>
				<div id="&coDocHeaderContainer;">
					<xsl:call-template name="wrapWithDiv">
						<xsl:with-param name="class" select="'&titleClass;'" />
						<xsl:with-param name="contents">
							<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.descriptions/md.longtitle"/>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="RenderIndexNavigationList"/>
				</div>
			</div>
			<div id="&coDocContentBody;">
				<xsl:apply-templates select="n-docbody/doc/index" />
			</div>
			<xsl:call-template name="EndOfDocument" />
			<xsl:call-template name="DisplayPublisherLogo"/>
		</div>
	</xsl:template>

	<xsl:template name="CommentaryIndexDocTitle">
		<div id="&coDocContentHeaderPrelim;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;" >
			<div id="&codocumentHeaderBorder;" class="&documentHeaderBorderClass;"></div>
			<div id="&coDocHeaderContainer;">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&titleClass;'" />
					<xsl:with-param name="contents">
						<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.descriptions/md.longtitle"/>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="RenderIndexNavigationList"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="subject[following-sibling::cite.query]">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="cite.query" priority="1">
		<xsl:call-template name="citeQuery"/>
		<xsl:if test="following-sibling::node()[not(self::text()) or normalize-space() &gt; 0][1]/self::cite.query">
			<xsl:text><![CDATA[; ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template name="renderSpaceBetweenCiteQueries"/>

</xsl:stylesheet>
