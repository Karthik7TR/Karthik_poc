<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="transform-utils.xsl" />

	<xsl:template match="x:document">
		<root>
			<xsl:apply-templates />
		</root>
	</xsl:template>

	<xsl:template match="x:page">
		<xsl:element name="pagebreak">
			<xsl:attribute name="num" select="@p4" />
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
		match="x:tag[@name != 'row' and @name != 'entry' and @name != 'image' and @name != 'foots' and @name != 'footnote']">
		<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:apply-templates select="x:attr" />
		<xsl:if
			test="@name = 'chapter.back' or @name = 'form.vertical.space' or @name = 'endline' or @name = 'leaveout' or @name = 'leader.fill' or @name = 'mte3' or @name='toc.volbreak' or @name = 'INDEX' or @name = 'CITE' or @name = 'l4' or @name = 'l3' or @name = 'l2' or @name = 'l1' or @name = 'mte2' or @name = 'mtf2' or @name = 'pa' or @name = 'foliolr' or @name = 'c'">
			<xsl:text disable-output-escaping="yes"><![CDATA[/]]></xsl:text>
		</xsl:if>
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
		match="x:endtag[@name != 'tbody' and @name != 'tgroup' and @name != 'table' and @name != 'tbl' and @name != 'row' and @name != 'entry' and @name != 'signature.group' and @name != 'signature.block']">
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

	<xsl:template match="text()" />
</xsl:stylesheet>