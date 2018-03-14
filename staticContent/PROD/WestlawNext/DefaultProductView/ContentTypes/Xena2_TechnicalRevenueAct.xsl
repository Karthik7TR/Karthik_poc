<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="tocElements" select="'hg0.gen|hg0|hg1|hg2|hg3|hg4|hg5|hg6|hg7|stnl|pnl'" />

	<xsl:template match="hg0.gen | hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | stnl | pnl" priority="2">
		<xsl:variable name="numberOfPreceding" select="count(preceding-sibling::node()[contains($tocElements, local-name())])" />
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="$numberOfPreceding" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="gnh">
		<xsl:call-template name="d3" />
	</xsl:template>

	<xsl:template match="tmp | tble">
		<xsl:call-template name="d7" />
	</xsl:template>
		
	<xsl:template match="cpr" />
	<xsl:template match="rank" />
	<xsl:template match="adt.gen" />

</xsl:stylesheet>
