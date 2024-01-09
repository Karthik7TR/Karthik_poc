<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:template match="x:tag[@name = 'footnote']">
		<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:apply-templates select="x:attr" />
		<xsl:text disable-output-escaping="yes"><![CDATA[ id="]]></xsl:text>
		<xsl:value-of select="ancestor::x:block/@id" />
		<xsl:text disable-output-escaping="yes"><![CDATA[">]]></xsl:text>
	</xsl:template>
</xsl:stylesheet>