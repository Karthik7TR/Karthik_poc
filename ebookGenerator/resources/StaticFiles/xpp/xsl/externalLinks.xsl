<!-- <xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x"> -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="transform-utils.xsl" />
	<xsl:output method="xml" omit-xml-declaration="yes" />
	<xsl:param name="mappingFile" />

	<xsl:template match="@*|node()[not(self::*)]">
		<xsl:copy />
	</xsl:template>

	<xsl:template match="*">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="node()|@*" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:cite.query">
		<xsl:element name="a">
			<xsl:attribute name="href">
				<xsl:value-of select="x:get-href-by-id(@ID, document($mappingFile))"
				disable-output-escaping="yes" />
			</xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="processing-instruction()">
		<xsl:copy />
	</xsl:template>

</xsl:stylesheet>