<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="Dtags.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href ="InternalReferenceWLN.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-docbody"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="doc">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="//md.cites"/>
			<xsl:apply-templates select ="so"/>
			<xsl:apply-templates select ="ti"/>
			<xsl:apply-templates select ="dl"/>
			<xsl:apply-templates select ="tk1"/>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</div>
		<xsl:apply-templates select="*[not(self::so or self::ti or self::dl or self::tk1)]" />
	</xsl:template>

	<xsl:template match="ti" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="so" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="tk1" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dl" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dpa0[d6] | dpa1[d6] | dpa2[d6]">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

</xsl:stylesheet>