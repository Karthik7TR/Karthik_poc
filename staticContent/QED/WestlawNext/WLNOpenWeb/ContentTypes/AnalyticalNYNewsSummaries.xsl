<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLNOpenWeb.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AnalyticalNYNewsSummaries.xsl" forceDefaultProduct="true"/>


	<!-- OPEN WEB - Limit text displayed -->
	<xsl:template match="nylj.summary">
		<xsl:call-template name="documentHeader" />
		<xsl:variable name="Contents">
			<div>
				<xsl:apply-templates select="*[not(self::prelim or self::west.authored.line or self::document.heading)]" />
			</div>
		</xsl:variable>
		<xsl:copy-of select="DocumentExtension:LimitTextByCharacterCount($Contents,&LimitTextByCharacterCountValue;)"/>
	</xsl:template>

</xsl:stylesheet>
