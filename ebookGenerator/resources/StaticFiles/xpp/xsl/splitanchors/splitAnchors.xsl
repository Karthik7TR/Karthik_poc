<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://www.w3.org/1999/xhtml"
	xmlns="http://www.w3.org/1999/xhtml">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="h:a[node()//processing-instruction('pb')]">
		 <xsl:apply-templates mode="wrap-links" />
	</xsl:template>

	<xsl:template match="node() | @*" mode="wrap-links">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*"  mode="wrap-links"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="h:span" mode="wrap-links">
		<xsl:element name="a">
			<xsl:copy-of select="ancestor::h:a[1]/@*" />
			<xsl:copy-of select="self::node()" />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
