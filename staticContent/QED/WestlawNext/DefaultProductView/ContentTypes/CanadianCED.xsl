<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:include href="CanadianFootnotes.xsl"/>
	<xsl:include href="CanadianCites.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <!--Do not render-->
  <xsl:template match="content.metadata.block | message.block.carswell"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswCED;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />

			<div class="&headnotesClass; &centerClass;">
				<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites"/>
				<xsl:apply-templates select="n-docbody/comment/doc_heading/toc_headings"/>
				<xsl:apply-templates select="n-docbody/comment/doc_heading/doc_msg"/>
			</div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>

			<!-- Do *NOT* render the UL elements that contain the p/a tags right now because those belong to the footnote and will be rendered later. -->
			<xsl:apply-templates select="n-docbody/comment/node()[not(self::doc_heading | self::ul//p/a[starts-with(@name, 'f') and starts-with(@href, '#r')])]"/>
			<xsl:call-template name="RenderFootnoteSection"/>
      <xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="toc_headings">
		<xsl:for-each select="*">
			<xsl:choose>
				<xsl:when test="position() &lt; 3"> <!-- Make two first line bold -->
					<xsl:call-template name="wrapWithDiv">
						<xsl:with-param name="class" select="'&titleClass;'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="wrapWithDiv"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<!-- Currency Line and Link -->
	<xsl:template match="doc_msg">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&crswCurrencyLine;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="paragr">
		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class" select="'&crswBottomMargin;'"/>
		</xsl:call-template>
	</xsl:template>
	
		<xsl:template match="urllink">
		<xsl:call-template name="CreateExternalLink">
			<xsl:with-param name="url" select="@href"/>
			<xsl:with-param name ="title" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswCurrencyKey;', '&crswCurrency;')"/>
			<xsl:with-param name="text" select="text()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Create an External Link -->
	<xsl:template name="CreateExternalLink">
		<xsl:param name="url"/>
		<xsl:param name="title"/>		
		<xsl:param name="text" select="'&crswClickHere;'"/>		
		<xsl:param name="hasImgChild" select="false()"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<a href="{$url}">
					<xsl:copy-of select="$text"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="string($hasImgChild) = 'true'">
						<!-- In this case the <a> encompasses the <img> tag, both of which have an onclick event handler. The img's one is in the platform code, 
             so it had to be overriden in the website_PreventImageClickAction function in Cobalt.Master.CRSW.js -->
						<a href="javascript:void(0);" class="&preventImageActionOnClickClass;" data-external-url="{$url}" data-external-title="{$title}">
							<xsl:copy-of select="$text"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<a href="javascript:void(0);" class="&linkoutShowLightboxOnClickClass;" data-external-url="{$url}" data-external-title="{$title}">
							<xsl:copy-of select="$text"/>
						</a>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>
