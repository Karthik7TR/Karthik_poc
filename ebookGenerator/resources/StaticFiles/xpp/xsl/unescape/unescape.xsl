<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://www.w3.org/1999/xhtml"
	xmlns="http://www.w3.org/1999/xhtml">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:include href="unescape-function.xsl" />

	<xsl:template match="node() | @*" >
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="h:span[contains(@class, 't ')]/text()[contains(., '&amp;')]" >
		<xsl:value-of select="h:unescape(self::node())"
			disable-output-escaping="yes" />
	</xsl:template>
</xsl:stylesheet>
