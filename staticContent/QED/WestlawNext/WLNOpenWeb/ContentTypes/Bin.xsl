<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLNOpenWeb.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Bin.xsl" forceDefaultProduct="true"/>


	<!-- OPEN WEB - Limit text displayed -->
	<xsl:template match="content">
		<xsl:variable name="Contents">
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class">&textClass;</xsl:with-param>
				<xsl:with-param name="contents">
					<xsl:apply-templates select="abstract" />
					<xsl:apply-templates select="text | note" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:copy-of select="DocumentExtension:LimitTextByCharacterCount($Contents,&LimitTextByCharacterCountValue;)"/>
	</xsl:template>

</xsl:stylesheet>