<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
	<xsl:include href="SpecialCharacters.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- *** IMPORTANT ***
	     Whether or not this stays preformatted as it should is completely dependent on an inversion of control
			 through the calling template to override the template match for text nodes within the descendent tree
			 of the element indicates the preformatted nature of its contents. See the "PreformattedText" stylesheet
			 in Content Blocks for an example.
	-->
	<xsl:template name="PreformattedTextCleaner">
		<xsl:param name="nodeToClean" select="." />
		<xsl:variable name="contents">
			<xsl:apply-templates select="$nodeToClean/node()" />
		</xsl:variable>
		<xsl:variable name="textWithAllNonStandardSpacesRemoved">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="$contents" />
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Only show if there is something in this other than whitespaces (including tabs, line feeds, and carriage returns) -->
		<xsl:if test="string-length(normalize-space($textWithAllNonStandardSpacesRemoved)) &gt; 0">
			<xsl:copy-of select="$contents" />
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>