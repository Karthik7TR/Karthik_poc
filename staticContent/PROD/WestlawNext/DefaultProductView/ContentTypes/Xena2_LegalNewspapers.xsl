<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-docbody"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="doc">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="//md.cites"/>
			<xsl:apply-templates select="hg0 |hg1 | hg2 | hg3 | hg4 | hg5 | so | dl | ti | ti2 | dj | dj1 | dj2 "/>
		</div>
		<xsl:apply-templates select="*[not(self::hg0 or self::hg1 or self::hg2 or self::hg3 or self::hg4 or self::hg5 or self::so or self::dl or self::ti or self::ti2 or self::dj or self::dj1 or self::dj2 or self::cr)]" />
	</xsl:template>

	<xsl:template match="md.cites" priority="2">
		<div class="&citesClass;">
			<xsl:choose>
				<xsl:when test="md.second.line.cite">
					<xsl:apply-templates select="md.second.line.cite"/>
				</xsl:when>
				<xsl:when test="md.third.line.cite">
					<xsl:apply-templates select="md.third.line.cite"/>
				</xsl:when>
				<xsl:when test="md.first.line.cite">
					<xsl:apply-templates select="md.first.line.cite"/>
				</xsl:when>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="ti" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
		<xsl:if test="not(following-sibling::ti2 or following-sibling::til)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<xsl:template match="centv | centdol"/>

	<!-- Supress empty leaderchar -->
	<xsl:template match="tbody/row/entry[descendant::leader]" priority="2"> 
		<xsl:if test="descendant::leaderchar[child::n-private-char]">
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
