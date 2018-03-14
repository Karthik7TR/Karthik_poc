<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n Completed As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<xsl:variable name="uuid" select="//md.uuid" />
	<xsl:variable name="mdView" select="//md.view" />
	<xsl:variable name="nView" select="//n-view" />
	<xsl:variable name="westlawIds" select="//md.westlawids" />
	<xsl:variable name="primary" select="//md.primarycite" />
	<xsl:variable name="firstline" select="//md.first.line.cite" />
	<xsl:variable name="normalized" select="//md.normalizedcite" />
	<xsl:variable name="legacy" select="//md.legacy.id" />
	<xsl:variable name="cfirstline" select="//cmd.first.line.cite" />
	<xsl:variable name="dates" select="//md.dates" />
	<xsl:variable name="pubid" select="//md.pubid" />
	<xsl:variable name="hyphenuuid" select="//md.hyphenated.uuid" />
	<xsl:variable name="docfamuuid" select="//md.doc.family.uuid" />
	<xsl:variable name="legacyheading" select="//md.legacy.headingid" />
	<xsl:variable name="sortkeys" select="//md.sortkeys" />
	<xsl:variable name="parallel" select="//md.parallelcite" />
	<xsl:variable name="mdLegacyHeading" select="//md.legacy.heading.id" />
	<xsl:variable name="relatedBillText" select="//md.related.billtext.uuid" />
	<xsl:variable name="databaseId" select="//md.wl.database.identifier" />
	<xsl:variable name="documentNumber" select="//md.wl.document.number" />
	<xsl:variable name="billID" select="//md.bill.id" />

	<xsl:template match="*">
		<xsl:apply-templates select="$uuid|$mdView|$nView|$westlawIds|$primary|$firstline|$normalized|$legacy|$cfirstline|$dates|$pubid|$hyphenuuid|$docfamuuid|$legacyheading|$sortkeys|$parallel|$mdLegacyHeading|$relatedBillText|$databaseId|$documentNumber|$billID" mode="copyNode-all"/>
	</xsl:template>

	<xsl:template match="*" mode="copyNode-all">
		<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
		<xsl:value-of select="name()" />
		<xsl:for-each select="@*">
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:copy-of select="name()" />
			<xsl:text>=&quot;</xsl:text>
			<xsl:value-of select="." />
			<xsl:text>&quot;</xsl:text>
		</xsl:for-each>
		<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
		<xsl:apply-templates mode="copyNode-all" />
		<xsl:text disable-output-escaping="yes">&lt;/</xsl:text>
		<xsl:value-of select="name()" />
		<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
	</xsl:template>
</xsl:stylesheet>