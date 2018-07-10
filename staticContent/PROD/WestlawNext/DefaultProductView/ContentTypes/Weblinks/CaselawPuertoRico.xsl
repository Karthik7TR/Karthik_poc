<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="CustomTitleAndCourtBlock.xsl"/>
	<xsl:include href="Synopsis.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Headnote.xsl" />
	<xsl:include href="OtherHeadnote.xsl"/>
	<xsl:include href="WestlawDescription.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCaselawPuertoRicoClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.references" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" />
			<xsl:apply-templates select="n-docbody/node()"/>
			<xsl:call-template name="RenderFootnoteSection"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
	<xsl:template match="title.block[not(following-sibling::title.block)]" priority="2">
		<xsl:call-template name="titleBlock" />
		<xsl:variable name="docketDateContents">
			<xsl:apply-templates select="../court.block"  mode="customCourtAndTitle" />
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

</xsl:stylesheet>