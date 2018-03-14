<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Headnote.xsl" />
	<xsl:include href="OtherHeadnote.xsl"/>
	<xsl:include href="Caveat.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses" />
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="insertStateCopyright" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="overview" priority="1">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="matters.included | treated.elsewhere" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="scopenote/head">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template name="insertStateCopyright">
		<br/>
		<div class="&nyScopeCopyrightClass;">
			<xsl:text>&copyrightStart;</xsl:text><xsl:value-of select="$currentYear"/><xsl:text>&nyScopeCopyrightEnd;</xsl:text>
		</div>
	</xsl:template>

</xsl:stylesheet>