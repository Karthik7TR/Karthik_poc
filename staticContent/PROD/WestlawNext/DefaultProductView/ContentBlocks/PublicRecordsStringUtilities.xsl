<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:preserve-space elements="*" />

	<!-- Splits a string and capitalizes the first letter of each word and forces the rest of each word to lower case. -->
	<xsl:template name="splitAndFixCase">
		<xsl:param name="string" select="."/>
		<xsl:if test="$string">
			<xsl:if test="not($string=.)">
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:if>
			<xsl:call-template name="fixCase">
				<xsl:with-param name="string" select="substring-before(concat($string,' '),' ')"/>
			</xsl:call-template>
			<xsl:call-template name="splitAndFixCase">
				<xsl:with-param name="string" select="substring-after($string, ' ')"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- Capitilize the first letter of the string passed in and force the rest of the string lower case.  When no string is
	     passed, the text for the current node is used. -->
	<xsl:template name="fixCase">
		<xsl:param name="string" select="."/>
		<xsl:value-of select="concat(translate(substring($string,1,1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'),
								translate(substring($string,2), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'))"/>
	</xsl:template>
	
</xsl:stylesheet>
