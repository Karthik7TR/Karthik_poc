<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="TrialDoc.xsl"/>
	<xsl:include href="CustomTitleAndCourtBlock.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="Document" priority="1">
		 <div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadataForContentType" />
      <xsl:apply-templates/>
      <xsl:call-template name="EndOfDocument" />
    </div>
  </xsl:template>

	<xsl:template match="metadata.block">
		<xsl:apply-templates select="md.references" />
		<xsl:apply-templates select="md.identifiers" />
	</xsl:template>

	<xsl:template match="title.block[not(following-sibling::title.block)]" priority="2">
		<xsl:apply-templates select="../court.block"  mode="customCourtAndTitle" />
		<xsl:call-template name="titleBlock" />
		<xsl:variable name="docketDateContents">
			<xsl:apply-templates select="../docket.block" mode="customCourtAndTitle" />
			<xsl:apply-templates select="../date.block" mode="customCourtAndTitle" />
		</xsl:variable>
		<xsl:if test="string-length($docketDateContents) &gt; 0">
			<div class="&docketDateClass;">
				<xsl:copy-of select="$docketDateContents"/>
			</div>
		</xsl:if>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="docket.line | date.line" priority="5">		
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates/>			
		</div>			
	</xsl:template>

	<xsl:template match="date.line/justified.line | docket.line/justified.line">
		<xsl:call-template name="wrapWithSpan"/>
	</xsl:template>

	<xsl:template match="represented.party.line" priority="2">
		<xsl:text>&#160;</xsl:text>
		<div>
			<xsl:apply-templates/>
		</div>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

</xsl:stylesheet>
