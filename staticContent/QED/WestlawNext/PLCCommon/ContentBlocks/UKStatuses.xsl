<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">


	<!--Status region-->
	<xsl:template name="DocumentStatusDetailed">
		<xsl:param name="statusCode"/>
		
			<xsl:call-template name="StatusIcon">
				<xsl:with-param name="statusCode" select="$statusCode"/>
				<xsl:with-param name="tag" select="'div'"/>
				<xsl:with-param name="isSmall" select="false()"/>
				<xsl:with-param name="onNewLine" select="true()"/>
			</xsl:call-template>
			<xsl:call-template name="StatusTextLabel">
				<xsl:with-param name="statusCode" select="$statusCode"/>
				<xsl:with-param name="onNewLine" select="true()"/>
			</xsl:call-template>
	</xsl:template>


	<xsl:template name="StatusIcon">
		<xsl:param name="statusCode"/>
		<xsl:param name="tag" select="'span'"/>
		<xsl:param name="isSmall" select="false()"/>
		<xsl:param name="onNewLine" select="true()"/>

		<xsl:variable name="sizeClass">
			<xsl:choose>
				<xsl:when test="$isSmall=true()">
					<xsl:value-of select="'&statusIcon25;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&statusIcon40;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="displayClass">
			<xsl:call-template name="DisplayClass">
				<xsl:with-param name="onNewLine" select="$onNewLine"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:element name="{$tag}">
			<xsl:attribute name="class">
				<xsl:value-of select="concat('&statusIconClass;',' ')"/>
				<xsl:call-template name="StatusIconClass">
					<xsl:with-param name="statusCode" select="$statusCode"/>
					<xsl:with-param name="isSmall" select="$isSmall"/>
				</xsl:call-template>
				<xsl:value-of select="concat(' ',$sizeClass)"/>
				<xsl:value-of select="concat(' ',$displayClass)"/>
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:call-template name="StatusIconHoverOver">
					<xsl:with-param name="statusCode" select="$statusCode"/>
				</xsl:call-template>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>

	<xsl:template name="StatusTextLabel">
		<xsl:param name="statusCode"/>
		<xsl:param name="onNewLine" select="true()"/>
		<xsl:variable name="displayClass">
			<xsl:call-template name="DisplayClass">
				<xsl:with-param name="onNewLine" select="$onNewLine"/>
			</xsl:call-template>
		</xsl:variable>
		<div>
			<xsl:attribute name="class">
				<xsl:text>&statusTextClass; </xsl:text>
				<xsl:call-template name="StatusTextColor">
					<xsl:with-param name="statusCode" select="$statusCode"/>
				</xsl:call-template>
				<xsl:value-of select="concat(' ',$displayClass)"/>
			</xsl:attribute>
			<xsl:call-template name="StatusDisplayText">
				<xsl:with-param name="statusCode" select="$statusCode"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template name="DisplayClass">
		<xsl:param name="onNewLine"/>
		<xsl:choose>
			<xsl:when test="$onNewLine=true()">
				<xsl:value-of select="'&blockStatus;'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'&inlineStatus;'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name ="StatusIconClass" />

	<xsl:template name="StatusIconHoverOver" />
	
	<xsl:template name ="StatusDisplayText" />

	<xsl:template name="StatusTextColor" />
	
</xsl:stylesheet>
