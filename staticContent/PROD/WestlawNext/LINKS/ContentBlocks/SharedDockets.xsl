﻿<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.LINKS.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl" forceDefaultProduct="true"/>
	<xsl:include href="SharedDockets.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="getDocketsUpdateLink">
		<xsl:call-template name="GetDocumentUrl">
			<xsl:with-param name ="documentGuid" select="$Guid" />
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
