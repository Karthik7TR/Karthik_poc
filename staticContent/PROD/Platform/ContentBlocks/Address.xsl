<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="Universal.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="address">
		<div class="&addressClass;">
			<xsl:apply-templates select="company.name | address.text"/>
			<div>
				<xsl:apply-templates select="city | state | postal.code"/>
			</div>
		</div>
	</xsl:template>
	
	<xsl:template match="address/company.name | address/address.text">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="address/city">
		<xsl:apply-templates/>
		<xsl:text>,<![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="address/state">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
