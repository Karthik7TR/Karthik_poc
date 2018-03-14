<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<xsl:template match="index" name="index">
		<div class="&indexClass;">
			<xsl:if test="@ID|@id">
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', @ID|@id)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="node()[not(self::index.entry)]"/>
			<xsl:if test="index.entry">
			<div class="&tocMainClass;">
					<xsl:apply-templates select="index.entry"/>
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="index/doc.title" name="indexDocTitle">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&coHeadingClass;3 &uppercaseClass;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<!--Suppress co_headtext class-->
	<xsl:template match="index/doc.title/head/headtext">
		<xsl:param name="divId"/>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="id" select="$divId"/>
		</xsl:call-template>			
	</xsl:template>

	<xsl:template match="index.entry" name="indexEntry">
		<xsl:param name="headClass">
			<xsl:if test="parent::index">
				<xsl:value-of select="'&uppercaseClass;'"/>
			</xsl:if>
			<xsl:if test="index.entry">
				<xsl:if test="parent::index">
					<xsl:value-of select="' '"/>
				</xsl:if>
				<xsl:value-of select="'&boldClass;'"/>
			</xsl:if>
		</xsl:param>
		<xsl:param name="refClass">
			<xsl:value-of select="'&paraIndentLeftClass;'"/>
		</xsl:param>
		<xsl:param name="headId">
			<xsl:if test="@ID|@id">
				<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
			</xsl:if>
		</xsl:param>
		<xsl:param name="headContents">
			<xsl:apply-templates select="node()[not(self::index.entry or self::ref.verb or self::index.xref)]" />
		</xsl:param>
		<div class="&tocCellWithoutLeadersClass;">
			<xsl:if test="string-length($headContents) &gt; 0">
				<xsl:choose>
					<xsl:when test="parent::index">
						<xsl:call-template name="wrapWithH">
							<xsl:with-param name="level" select="3"/>
							<xsl:with-param name="class" select="$headClass"/>
							<xsl:with-param name="id" select="$headId"/>
							<xsl:with-param name="contents" select="$headContents"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="wrapWithDiv">
							<xsl:with-param name="class" select="$headClass"/>
							<xsl:with-param name="id" select="$headId"/>
							<xsl:with-param name="contents" select="$headContents"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:if test="ref.verb or index.xref">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="$refClass"/>
					<xsl:with-param name="contents" >
						<xsl:apply-templates select="node()[self::ref.verb or self::index.xref ]"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="index.entry">
				<div class="&tocMainClass;">
					<xsl:apply-templates select="index.entry"/>
				</div>
			</xsl:if>
		</div>
	</xsl:template>
	
	<xsl:template match="index.division">
		<div class="&tocMainClass;">
			<xsl:apply-templates select="index.entry"/>
		</div>
	</xsl:template>

	<!--Suppress, rendered in index.xref template -->
	<xsl:template match="ref.verb" />

	<!-- Render internal links for See also -->
	<xsl:template match="index.xref[@refid]">
		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="concat('#&internalLinkIdPrefix;',string(./@refid))" />
			</xsl:attribute>
			<xsl:apply-templates select="preceding-sibling::*[1][self::ref.verb]/node()" />
			<xsl:text><![CDATA[ ]]></xsl:text>
			<span class="&uppercaseClass;">
				<xsl:apply-templates />
			</span>
			<xsl:if test="following-sibling::ref.verb">
				<xsl:text>;<![CDATA[ ]]></xsl:text>
			</xsl:if>
		</a>
	</xsl:template>

</xsl:stylesheet>
