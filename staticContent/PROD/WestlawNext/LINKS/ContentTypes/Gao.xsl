<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.LINKS.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Gao.xsl" forceDefaultProduct="true"/>
	<xsl:include href="FormsPDFLink.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--
		In order to allow generation of direct links to PDF documents the following FAC should be turned ON: 'GAO LH PDFS OPEN IN LINKS'

		Links product view override: hyperlink to PDF forms have href host stay within Links rather than link out to WLN.
		Example document: http://links.ci.westlaw.com/Document/Ie9b022b066e611df9b58010000000000/View/FullText.html?sp=DEVLINKS-1301&transitionType=Default&contextData=%28sc.Default%29&bhcp=1

		Current known collection -> content types with links to PDF forms:
			Collection         |  Content Type
			==========================================================
			w_codesfedlhshlp01 |  Federal Legislative History
			w_codesfedlhshlp02 |  Federal Legislative History
			w_codesfedlhshlp03 |  Federal Legislative History
			w_codesfedlhshlp04 |  Federal Legislative History
	-->
	
	<xsl:template name="CreateBlobLinkUrl">
		<xsl:param name="guid" />
		<xsl:param name="highResolution" />
		<xsl:param name="targetType" />
		<xsl:param name="maxHeight" />
		<xsl:param name="originationContext" />
		<xsl:param name="prettyName" />
		<xsl:param name="hash" />
		<xsl:param name="docGuid" />
		<xsl:param name="extension" />
		
		<xsl:choose>
			<xsl:when test="DocumentExtension:HasFacGranted('GAO LH PDFS OPEN IN LINKS')">
				<xsl:call-template name="CreateBlobLinkUrlLINKS">
					<xsl:with-param name="guid" select="$guid"/>
					<xsl:with-param name="highResolution" select="$highResolution"/>
					<xsl:with-param name="targetType" select="$targetType"/>
					<xsl:with-param name="maxHeight" select="$maxHeight"/>
					<xsl:with-param name="originationContext" select="$originationContext"/>
					<xsl:with-param name="prettyName" select="$prettyName"/>
					<xsl:with-param name="hash" select="$hash"/>
					<xsl:with-param name="docGuid" select="$docGuid"/>
					<xsl:with-param name="extension" select="$extension"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="CreateBlobLinkUrlPlatform">
					<xsl:with-param name="guid" select="$guid"/>
					<xsl:with-param name="highResolution" select="$highResolution"/>
					<xsl:with-param name="targetType" select="$targetType"/>
					<xsl:with-param name="maxHeight" select="$maxHeight"/>
					<xsl:with-param name="originationContext" select="$originationContext"/>
					<xsl:with-param name="prettyName" select="$prettyName"/>
					<xsl:with-param name="hash" select="$hash"/>
					<xsl:with-param name="docGuid" select="$docGuid"/>
					<xsl:with-param name="extension" select="$extension"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
