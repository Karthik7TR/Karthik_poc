﻿<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="TitleHtml.xsl"/>
	<xsl:include href="GlobalParams.xsl"/>

	<xsl:template match="Titles">
		<Titles>
			<xsl:apply-templates />
		</Titles>
	</xsl:template>
	
	<xsl:template match="Title">
		<Title>
			<xsl:apply-templates />
		</Title>
	</xsl:template>
	
	<xsl:template match="Title//n-private-char" priority="1">
		<xsl:call-template name="Title-n-private-char"/>
	</xsl:template>
	
</xsl:stylesheet>

