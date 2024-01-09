<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />
	<xsl:param name="pattern" />

	<xsl:template match="/">
		<xsl:element name="map">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template
		match="x:section.front/x:outline.name.block/x:designator//x:t[1]|
    x:primary.section.front/x:primary.outline.name.block/x:designator//x:t[1]">
		<xsl:element name="entry">
			<xsl:attribute name="section-number" select="concat(text(), string-join(following-sibling::x:t/text()))" />
			<xsl:attribute name="guid" select="x:get-guid(node())" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:rw.doc.front/x:rw.doc.outline.name.block">
		<xsl:element name="entry">
			<xsl:attribute name="section-number">
				<!-- The ";j" flag is required to turn on look-around features -->
                <xsl:analyze-string select="string-join(.//x:t/text())"
				regex="{$pattern}" flags=";j">
                    <xsl:matching-substring>
                        <xsl:value-of select="regex-group(2)" />
                    </xsl:matching-substring>
                    <xsl:non-matching-substring />
                </xsl:analyze-string>
            </xsl:attribute>
			<xsl:attribute name="guid" select="x:get-guid(node())" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="text()" />

	<xsl:function name="x:get-guid">
		<xsl:param name="text-node" />
		<xsl:value-of select="$text-node/preceding::x:XPPHier[1]/@uuid" />
	</xsl:function>
</xsl:stylesheet>
