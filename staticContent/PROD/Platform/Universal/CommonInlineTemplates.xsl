<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Bold -->
	<xsl:template match="bold | b">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<strong>
				<xsl:copy-of select="$contents"/>
			</strong>
		</xsl:if>
	</xsl:template>

	<!-- Italics -->
	<xsl:template match="ital">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:choose>
				<xsl:when test ="$DeliveryMode">
					<i>
						<xsl:copy-of select="$contents"/>
					</i>
				</xsl:when>
				<xsl:otherwise>
					<em>
						<xsl:copy-of select="$contents"/>
					</em>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- Subscript -->
	<xsl:template match="sub">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<sub>
				<xsl:copy-of select="$contents"/>
			</sub>
		</xsl:if>
	</xsl:template>

	<!-- Superscript -->
	<xsl:template match="super" name="superTemplate">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<sup>
				<xsl:copy-of select="$contents"/>
			</sup>
		</xsl:if>
	</xsl:template>

	<!-- Underscore -->
	<xsl:template match="underscore">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<span class="&underlineClass;">
				<xsl:copy-of select="$contents"/>
			</span>
		</xsl:if>
	</xsl:template>

	<!-- Added Material -->
	<xsl:template match="added.material" name="addedMaterial">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<ins>
				<xsl:copy-of select="$contents"/>
			</ins>
		</xsl:if>
	</xsl:template>

	<!-- Deleted Material or Strikethrough -->
	<xsl:template match="deleted.material" name="deletedMaterial">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<del>
				<xsl:copy-of select="$contents"/>
			</del>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="strikethru">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<del>
				<xsl:copy-of select="$contents"/>
			</del>
		</xsl:if>
	</xsl:template>

	<!-- Vetoed Text -->
	<xsl:template match="vetoed.text">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<del>
				<span class="&underlineClass;">
					<xsl:copy-of select="$contents"/>
				</span>
			</del>
		</xsl:if>
	</xsl:template>

	<!-- Stricken Material -->
	<xsl:template match="stricken.material">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<ins>
				<del>
					<xsl:copy-of select="$contents"/>
				</del>
			</ins>
		</xsl:if>
	</xsl:template>

	<xsl:template match="deleted.material/added.material" name="deletedAddedMaterial">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<ins>
				<del>
					<xsl:copy-of select="$contents"/>
				</del>
			</ins>
		</xsl:if>
	</xsl:template>

	<!-- CSC -->
	<xsl:template match="csc">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:choose>
				<xsl:when test=".//justified.line">
					<div class="&smallCapsClass;">
						<xsl:copy-of select="$contents"/>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<span class="&smallCapsClass;">
						<xsl:copy-of select="$contents"/>
					</span>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- UC -->
	<xsl:template match="uc">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<span class="&uppercaseClass;">
				<xsl:copy-of select="$contents"/>
			</span>
		</xsl:if>
	</xsl:template>

	<!-- Crosshatch (intended as double strikethrough) -->
	<xsl:template match="crosshatch">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<span class="&crosshatchClass;">
				<xsl:copy-of select="$contents"/>
			</span>
		</xsl:if>
	</xsl:template>

	<!-- EndLine -->
	<xsl:template match="endline">
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

</xsl:stylesheet>
