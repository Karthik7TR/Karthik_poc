<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="transform-utils.xsl" />
	<xsl:import href="PageNumbers.xsl" />

	<xsl:param name="bundlePartType" />

	<xsl:template match="x:document">
		<root>
			<xsl:apply-templates />
		</root>
	</xsl:template>

	<xsl:template match="x:page">
		<xsl:element name="pagebreak">
			<xsl:attribute name="num">
				<xsl:call-template name="page-numbers">
					<xsl:with-param name="bundlePartType" select="$bundlePartType" />
				</xsl:call-template>
			</xsl:attribute>
			<xsl:attribute name="num-string">
				<xsl:call-template name="print-page-numbers" />
			</xsl:attribute>
			<xsl:attribute name="prev_page">
				<xsl:apply-templates select="preceding::x:page[1]" mode="prev_page" />
			</xsl:attribute>
		</xsl:element>
		<xsl:apply-templates select="x:stream[@type='footnote']" />
	</xsl:template>

	<xsl:template match="x:tag[@name = 'footnote']">
		<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:apply-templates select="x:attr" />
		<xsl:text disable-output-escaping="yes"><![CDATA[ id="]]></xsl:text>
		<xsl:value-of select="ancestor::x:block/@id" />
		<xsl:text disable-output-escaping="yes"><![CDATA[">]]></xsl:text>
	</xsl:template>

	<xsl:template
		match="x:tag">
		<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:apply-templates select="x:attr" />
		<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
	</xsl:template>

	<xsl:template match="x:attr">
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text disable-output-escaping="yes"><![CDATA[="]]></xsl:text>
		<xsl:value-of select="x:get-fixed-text(.)" />
		<xsl:text disable-output-escaping="yes"><![CDATA["]]></xsl:text>
	</xsl:template>

	<xsl:template
		match="x:endtag">
		<xsl:text disable-output-escaping="yes"><![CDATA[</]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
	</xsl:template>

	<xsl:template match="x:t[not(@suppress='true')]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="x:t[not(@suppress='true')]/text()">
		<xsl:element name="t">
			<xsl:attribute name="style">
				<xsl:value-of select="../@style" />
				<xsl:if test="../@y!='0'">
					<xsl:value-of select="concat(' ', x:get-vertical-align(../@y))" />
				</xsl:if>
				<xsl:if test="../@cgt='true'">
					<xsl:value-of select="concat(' ', 'cgt')" />
				</xsl:if>
			</xsl:attribute>
			<xsl:value-of select="x:get-fixed-text(.)" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="text()" />
</xsl:stylesheet>