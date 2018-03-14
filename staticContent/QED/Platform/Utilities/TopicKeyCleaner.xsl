<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:include href="SpecialCharacters.xsl"/>
	<xsl:include href="NPrivateChar.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="cleanKeyText">
		<xsl:param name="value">
			<xsl:apply-templates/>
		</xsl:param>
		<xsl:param name="beginningText" select="false()" />
		<xsl:param name="endingText" select="false()" />

		<!-- Trim spaces based on if node is beginning or end or both -->
		<xsl:variable name="trimmedKeyText">
			<xsl:variable name="trimmedBeginning">
				<xsl:choose>
					<xsl:when test="$beginningText">
						<xsl:call-template name="trim-start">
							<xsl:with-param name="string">
								<xsl:value-of select="$value" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$value" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$endingText">
					<xsl:call-template name="trim-end">
						<xsl:with-param name="string">
							<xsl:value-of select="$trimmedBeginning" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$trimmedBeginning" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- Removing ' k. ' from beginning -->
		<xsl:variable name="strippedKText">
			<xsl:choose>
				<xsl:when test="$beginningText and starts-with($trimmedKeyText, 'k. ')">
					<!-- keyText starts with 'k. ' - strip it off -->
					<xsl:value-of select="substring($trimmedKeyText, 4, string-length($trimmedKeyText) - 3)"/>
				</xsl:when>
				<!--suppress the node if it just 'k.'-->
				<xsl:when test="$beginningText and $trimmedKeyText = 'k.'"/>
				<xsl:otherwise>
					<xsl:value-of select="$trimmedKeyText"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- check for a '.' on the last character; if it is not a '.', then output it, otherwise get rid of it -->
		<xsl:variable name="lastChar" select="substring($strippedKText, string-length($strippedKText))"/>
		<xsl:choose>
			<xsl:when test="$endingText and $lastChar = '.'">
				<xsl:value-of select="substring($strippedKText, 1, string-length($strippedKText) - 1)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$strippedKText"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template name="getKeyTextForTitles">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:call-template name="cleanKeyText">
			<xsl:with-param name="beginningText" select="true()" />
			<xsl:with-param name="endingText" select="true()" />
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
