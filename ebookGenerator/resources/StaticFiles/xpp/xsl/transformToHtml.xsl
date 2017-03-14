<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:param name="fileBaseName" />

	<xsl:template match="x:page">
		<html>
            <xsl:element name="body">
                <xsl:attribute name="fileBaseName" select="$fileBaseName" />
				<xsl:apply-templates />
            </xsl:element>
		</html>
	</xsl:template>

	<xsl:template match="element()">
		<xsl:element name="div">
			<xsl:attribute name="class" select="x:get-class-name(name(.))" />
			<xsl:copy-of select="./@uuid | ./@tocuuid" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:ref">
		<xsl:copy-of select="self::node()" />
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="." />
	</xsl:template>

	<!-- get CSS friendly class name -->
	<xsl:function name="x:get-class-name">
		<xsl:param name="value" />
		<xsl:value-of select="translate($value,'.','_')" />
	</xsl:function>
</xsl:stylesheet>