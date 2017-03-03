<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:template match="x:document">
		<xsl:element name="root">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template
		match="x:tag[@name != 'row' and @name != 'entry' and @name != 'image']">
		<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:if
			test="@name = 'chapter.back' or @name = 'form.vertical.space' or @name = 'endline' or @name = 'leaveout' or @name = 'leader.fill' or @name = 'mte3' or @name='toc.volbreak' or @name = 'INDEX' or @name = 'CITE' or @name = 'l4' or @name = 'l3' or @name = 'l2' or @name = 'l1' or @name = 'mte2' or @name = 'mtf2' or @name = 'pa' or @name = 'foliolr' or @name = 'c'">
			<xsl:text disable-output-escaping="yes"><![CDATA[/]]></xsl:text>
		</xsl:if>
		<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
	</xsl:template>

	<xsl:template
		match="x:endtag[@name != 'tbody' and @name != 'tgroup' and @name != 'table' and @name != 'tbl' and @name != 'row' and @name != 'entry' and @name != 'signature.group' and @name != 'signature.block']">
		<xsl:text disable-output-escaping="yes"><![CDATA[</]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
	</xsl:template>

	<xsl:template match="x:t[not(@suppress) or @suppress!='true']">
		<xsl:element name="line">
			<xsl:attribute name="xfinal" select="../@xfinal" />
			<xsl:attribute name="qdtype" select="../@qdtype" />
			<xsl:copy-of select="self::node()" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:page">
		<xsl:element name="pagebreak">
			<xsl:attribute name="num" select="@p4" />
		</xsl:element>
		<xsl:apply-templates select="x:stream[@type='main']" />
	</xsl:template>

	<xsl:template match="text()" />
</xsl:stylesheet>