<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:include href="CanadianCites.xsl"/>
	<xsl:include href="CanadianDate.xsl"/>
	<xsl:include href="CanadianFootnotes.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--Varible check for Insolvency Court Filing documents-->
	<xsl:variable name="isInsolvencyCourtFilingDoc">
		<xsl:choose>
			<xsl:when test="//md.royalty/md.royalty.code = 'CANFIL'">
				<xsl:value-of select="'true'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'false'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="Document">    
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswRegulatoryMaterialsClass;'"/>
			</xsl:call-template>
			
			<xsl:call-template name="StarPageMetadata" />

			<div class="&citesClass;">
				<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites"/>
			</div>
			
			
			<div class="&headnotesClass; &centerClass;">
				<xsl:apply-templates select="n-docbody/comment/doc_heading"/>
			</div>		
			
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			
			<!--Document Content-->
      <xsl:apply-templates select="n-docbody/comment/node()[not(self::doc_heading | self::p//sup/a[starts-with(@name, 'f')]  )]"/>
      
      <xsl:call-template name="RenderFootnoteSection"/>      
			<xsl:call-template name="EndOfDocument"/>				
		</div>
	</xsl:template>

  <!-- Do not render -->
	<xsl:template match="message.block.carswell | content.metadata.block" />

	<xsl:template match="doc_heading">
		<xsl:choose>
			<xsl:when test="$isInsolvencyCourtFilingDoc = 'true'">
				<xsl:apply-templates select="node()[not(self::doc_title)]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="toc_headings">
		<div>
			<xsl:for-each select="*">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="&titleClass;"/>
				</xsl:call-template>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template match="doc_title">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="section" priority="1">
		<xsl:apply-templates/>
	</xsl:template>

</xsl:stylesheet>
