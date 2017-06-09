<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="transform-utils.xsl" />
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	
	<xsl:template match="x:document">
		<root>
			<xsl:apply-templates />
		</root>
	</xsl:template>

	<xsl:template match="x:page">
		<xsl:element name="pagebreak">
			<xsl:attribute name="num" select="@p4" />
		</xsl:element>
		<xsl:apply-templates select="x:stream[@type='main']" />
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

	<xsl:template match="x:t">
		<xsl:choose>
			<xsl:when test="@suppress='true'" />
			<xsl:when test="@cgt='true'">
				<xsl:element name="cgt">
					<xsl:value-of select="x:get-fixed-text(self::node())" />
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="x:get-fixed-text(self::node())" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="x:xref">
		<xsl:copy-of select="self::node()" />
	</xsl:template>
    
    <xsl:template match="processing-instruction('XPPHier')">
        <xsl:text disable-output-escaping="yes"><![CDATA[<XPPHier ]]></xsl:text>
        <xsl:value-of select="."/>
        <xsl:text disable-output-escaping="yes"><![CDATA[/>]]></xsl:text>
    </xsl:template>

    <xsl:template match="text()" />
</xsl:stylesheet>