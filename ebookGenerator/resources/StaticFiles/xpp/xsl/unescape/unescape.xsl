<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://www.w3.org/1999/xhtml"
	xmlns="http://www.w3.org/1999/xhtml">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:template match="node() | @*" >
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="h:span[contains(@class, 't ')]/text()[contains(., '&amp;')]" >
		<xsl:value-of select="h:unescape(self::node())"
			disable-output-escaping="yes" />
	</xsl:template>
	
	<xsl:function name="h:unescape">
		<xsl:param name="textString" />
		<!-- All extra special cases like &amp;apos; should go BEFORE below section -->
		<xsl:variable name="htmlAmpsStr"
			select="replace($textString, '&amp;', '&amp;#38;')" />
		<xsl:variable name="cleanAmpsStr">
			<xsl:choose>
				<xsl:when test="contains($htmlAmpsStr, '&amp;#38;#')">
					<xsl:value-of select="replace($htmlAmpsStr, '&amp;#38;#', '&amp;#')" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$htmlAmpsStr" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="$cleanAmpsStr" />
	</xsl:function>
</xsl:stylesheet>