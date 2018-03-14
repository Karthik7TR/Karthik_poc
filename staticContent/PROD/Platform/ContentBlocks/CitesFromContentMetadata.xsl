<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="cmd.cites">
		<xsl:variable name="contents">
			<xsl:call-template name="concatCites" />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div class="&citesClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="cmd.first.line.cite" />
	<xsl:template match="cmd.alternative.cite/internal.reference"/>
	<xsl:template match="cmd.expandedcite"/>
	<xsl:template match="cmd.tax.expandedcite"/>

	<xsl:template name="concatCites">
		<xsl:variable name="pertinentCites" select=".//cmd.second.line.cite | .//cmd.alternative.cite"/>
		<xsl:for-each select="$pertinentCites">
			<xsl:apply-templates select="." />
			<xsl:if test="position() != last()">
				<xsl:text>,<![CDATA[ ]]></xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
