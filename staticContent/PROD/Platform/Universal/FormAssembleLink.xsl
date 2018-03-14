<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<xsl:template match="/Document/form-data/formAssembleData">
		<xsl:if test="$DisplayFormAssembleLink">
			<xsl:call-template name="renderFormAssembleHiddenJsonObject" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="renderFormAssembleHiddenJsonObject">
		<xsl:variable name="jsonObject">
			<xsl:call-template name="createFormAssembleJson" />
		</xsl:variable>
		<xsl:if test="string-length($jsonObject) &gt; 0">
			<input>
				<xsl:attribute name="type">hidden</xsl:attribute>
				<xsl:attribute name="id">&formAssembleJsonId;</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:value-of select="$jsonObject" />
				</xsl:attribute>
			</input>
		</xsl:if>
	</xsl:template>	
	
	<xsl:template name="createFormAssembleJson">
		<xsl:variable name="formAssembleTitle">
			<xsl:value-of select="/Document/form-data/formAssembleData/@title" />
		</xsl:variable>
		
		<xsl:variable name="formAssembleGuid">
			<xsl:value-of select="/Document/form-data/formAssembleData/@guid" />
		</xsl:variable>
		
		<xsl:variable name="jsonObject">
			<xsl:text>{</xsl:text>
			<xsl:value-of select="concat('&quot;', '&formAssembleTitle;', '&quot;:&quot;', $formAssembleTitle, '&quot;')" />
			<xsl:value-of select="concat(',&quot;', '&formAssembleGuid;', '&quot;:&quot;', $formAssembleGuid, '&quot;')" />
			<xsl:text>}</xsl:text>
		</xsl:variable>

		<xsl:value-of select="$jsonObject"/>
	</xsl:template>
	
</xsl:stylesheet>

