<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>


	<xsl:template match="subject.headings.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="subject.heading.list">
		<ul>
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<xsl:template match="subject.heading.list/subject.heading">
		<li>
			<xsl:apply-templates />
		</li>
	</xsl:template>

	<xsl:template match="subject.heading/subject.heading/topic">
		<xsl:choose>
			<xsl:when test="@level = 'major'">
				<xsl:text>/</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>, </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates />
	</xsl:template>

</xsl:stylesheet>
