<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="transform-utils.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	
	<xsl:param name="entitiesDocType" />

	<xsl:template match="x:document">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE root SYSTEM &#34;</xsl:text>
		<xsl:value-of select="$entitiesDocType" />
		<xsl:text disable-output-escaping="yes">&#34;&gt;</xsl:text>
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

	<xsl:template match="x:tag">
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

	<xsl:template match="x:endtag">
		<xsl:text disable-output-escaping="yes"><![CDATA[</]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
	</xsl:template>

	<xsl:template match="x:t[not(@suppress='true')]">
		<xsl:variable name="text"
			select="x:get-fixed-text(string-join(text(), ''))" />
		<xsl:choose>
			<xsl:when test="@cgt='true'">
				<xsl:element name="cgt">
					<xsl:if test="@style">
						<xsl:attribute name="style" select="@style" />
					</xsl:if>
					<xsl:value-of select="$text" />
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="x:dt">
		<xsl:if test="../parent::x:line[not(following-sibling::x:line)]">
			<xsl:value-of select="." />
		</xsl:if>
	</xsl:template>

	<xsl:template match="x:xref">
		<xsl:copy-of select="self::node()" />
	</xsl:template>

	<xsl:template match="processing-instruction('XPPMetaData') | processing-instruction('XPPHier')">
        <xsl:variable name="apostrophe">'</xsl:variable>
       	<xsl:variable name="tail" select="substring-after(., ' name=')" />
       	<xsl:variable name="name" select="replace(replace($tail, '\s[A-Za-z._]{1,}=.*', ''), $apostrophe, '')" />
                
        <xsl:value-of select="concat('&lt;', name(), ' ')" disable-output-escaping="yes"/>
        <xsl:value-of select="."/>
        <xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
        <xsl:value-of select="$name" disable-output-escaping="yes"/>
        <xsl:value-of select="concat('&lt;/', name(), '&gt;')" disable-output-escaping="yes"/>
    </xsl:template>

	<xsl:template match="text()" />
</xsl:stylesheet>