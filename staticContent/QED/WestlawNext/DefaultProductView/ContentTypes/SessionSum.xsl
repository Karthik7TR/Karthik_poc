﻿<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Generic.xsl"/>
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">			
			<xsl:apply-templates />
			<xsl:apply-templates select="copyright.line"/>
			<xsl:call-template name="EndOfDocument" />
			<xsl:call-template name="RemoveSaveToWidget" />
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</div>
	</xsl:template>

	<xsl:template match="copyright.line" priority="5">
		<br/>
		<xsl:apply-templates />
	</xsl:template>

</xsl:stylesheet>
