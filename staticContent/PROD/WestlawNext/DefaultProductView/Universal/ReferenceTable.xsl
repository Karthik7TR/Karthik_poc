<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="Table.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--Table formatting code lifted from html-ssecc-document.xsl (Web2 file). --> 

	<!--Table formatting for specific, limited cases.  The reference.table tag will 
			render a two-column table of reference.label and reference.text columns.  -->
	
	<xsl:template match="reference.block/reference.table">
		<table style="border-collapse:collapse;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="reference.table/reference">
		<tr>
			<xsl:apply-templates />
		</tr>
	</xsl:template>

	<xsl:template match="reference.table/reference/reference.label">
		<td>
			<xsl:apply-templates />
		</td>
	</xsl:template>

	<xsl:template match="reference.table/reference/reference.text">
		<xsl:if test="not(name(preceding-sibling::*[1]) = 'reference.label')">
			<!-- Add an empty cell, because reference.text needs to be in the right column -->
			<td></td>
		</xsl:if>
		<td>
			<xsl:apply-templates />
		</td>
	</xsl:template>

	<xsl:template match="reference.text[ancestor::reference.body]">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- No spacing needed, only line break, for last reference.text in document -->
	<xsl:template match="reference.body[position() = last()]//reference[(position() = last()) and not(descendant::reference)]/reference.text">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="manufacturer.narrative">
		<xsl:apply-templates />
	</xsl:template>
	
</xsl:stylesheet>
