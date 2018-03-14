<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="SearchTerms.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">

		<xsl:variable name="bodyElementsWithContent" select="count(n-docbody//html/body//text()[string-length(translate(.,'&#09;&#10;&#13;&#32;&#160;&#8192;&#8193;&#8194;&#8195;&#8196;&#8197;&#8198;&#8199;&#8200;&#8201;&#8202;&#8203;&#8203;&#8239;&#8239;&#65279;','')) &gt; 0]) &gt; 0" />
		<xsl:variable name="bodyElementWithHiddenDiv" select="n-docbody//html/body/div/@style='visibility: hidden'" />
		<xsl:variable name="divCount" select="count(n-docbody//html/body/div)" />
		<xsl:variable name="bodyElementParagraph" select="count(n-docbody//html/body/p/span//text()[string-length(translate(.,'&#09;&#10;&#13;&#32;&#160;&#8192;&#8193;&#8194;&#8195;&#8196;&#8197;&#8198;&#8199;&#8200;&#8201;&#8202;&#8203;&#8203;&#8239;&#8239;&#65279;','')) &gt; 0]) &gt; 0" />

		<xsl:variable name="thisIsAnEmptyDocument">
			<xsl:choose>
				<xsl:when test="$bodyElementsWithContent">false</xsl:when>
				<xsl:otherwise>true</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="thisIsAnEmptyPDFDocument">
			<xsl:choose>
				<xsl:when test="$bodyElementParagraph">false</xsl:when>
				<xsl:otherwise>true</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$thisIsAnEmptyDocument='true' or ($thisIsAnEmptyPDFDocument='true' and $bodyElementWithHiddenDiv='true')">
				<div id="&documentClass;" class="&selectedTextInvalid; &disableHighlightFeaturesClass;">
					<h1>This document contains no viewable text.  Please view original or open in native format.</h1>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div id="&documentClass;">
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeUserDocumentClass;'"/>
					</xsl:call-template>
					<xsl:apply-templates select="n-docbody//html" />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="html/head">
		<xsl:apply-templates select="style/comment()" />
	</xsl:template>

	<xsl:template match="@*[ancestor::body and not(self::N-HIT | self::N-LOCATE | self::N-WITHIN)] | node()[ancestor::body and not(self::N-HIT | self::N-LOCATE | self::N-WITHIN)]">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="style/comment()">
		<xsl:if test="not($DeliveryMode)">
			<style type="text/css">
				<xsl:text disable-output-escaping="yes"><![CDATA[<!--]]></xsl:text>
				<xsl:value-of disable-output-escaping="yes" select="."/>
				<xsl:text disable-output-escaping="yes"><![CDATA[-->]]></xsl:text>
			</style>
		</xsl:if>
	</xsl:template>

	<!-- Supressions -->
	<xsl:template match="n-metadata"/>
	<xsl:template match="title" />

</xsl:stylesheet>
