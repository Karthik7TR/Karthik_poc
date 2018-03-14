<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="address.block">
		<xsl:if test="street.address and string-length(street.address &gt; 0)">
			<xsl:apply-templates select="street.address"/>
		</xsl:if>
		<xsl:if test="city.state.zip and string-length(city.state.zip &gt; 0)">
			<xsl:apply-templates select="city.state.zip"/>
		</xsl:if>
		<xsl:if test="county and string-length(county &gt; 0)">
			<xsl:apply-templates select="county"/>
		</xsl:if>
		<xsl:if test="country and string-length(country &gt; 0)">
			<xsl:apply-templates select="country"/>
		</xsl:if>		
	</xsl:template>

	<xsl:template match="street.address">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="address">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="city.state.zip">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="country">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="county">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Do not display city or state, they are included in city.state.zip -->
	<xsl:template match="city"/>
	<xsl:template match="state"/>

</xsl:stylesheet>