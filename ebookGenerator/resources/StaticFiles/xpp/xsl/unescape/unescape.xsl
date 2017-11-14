<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://www.w3.org/1999/xhtml"
	xmlns="http://www.w3.org/1999/xhtml">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:template match="node() | @*" mode="#all">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" mode="#current" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="h:span[contains(@class, 't ')]">
		<xsl:variable name="hasAmps">
			<xsl:for-each select="self::node()/text()">
				<xsl:if test="contains(self::node(), '&amp;#')">
					<xsl:text>+</xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($hasAmps, '+')">
				<xsl:copy>
					<xsl:copy-of select="@*" />
					<xsl:apply-templates mode="process" />
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="self::node()" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="text()" mode="process">
		<xsl:variable name="htmlAmpsStr"
			select="replace(self::node(), '&amp;', '&amp;#38;')" />
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
		<xsl:value-of select="$cleanAmpsStr"
			disable-output-escaping="yes" />
	</xsl:template>
</xsl:stylesheet>