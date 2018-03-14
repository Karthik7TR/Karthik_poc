<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="Para.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="nestedParas">
		<xsl:param name="class" />

		<xsl:choose>
			<xsl:when test="parent::para">
				<xsl:variable name="allClasses">
					<xsl:value-of select="'&paraIndentLeftClass;'"/>
					<xsl:if test="string-length($class) &gt; 0">
						<xsl:text><![CDATA[ ]]></xsl:text>
						<xsl:value-of select="$class" />
					</xsl:if>
				</xsl:variable>
				<xsl:call-template name="para">
					<xsl:with-param name="className" select="$allClasses" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="para" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Fix for bug 877373. Supress this element in general. -->
	<xsl:template match="paratext/centd" priority="1" />

</xsl:stylesheet>
