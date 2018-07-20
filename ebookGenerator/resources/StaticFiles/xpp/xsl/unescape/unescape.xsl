<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://www.w3.org/1999/xhtml"
	xmlns="http://www.w3.org/1999/xhtml">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:variable name="predefinedHtmlCharacters" select="document('predefinedHtmlCharacters.xml')" />
	<xsl:variable name="numericCharEntitiyRegex" select="'(&amp;#[0-9]+;)'" />
	<xsl:variable name="stringCharEntitiyRegex" select="'(&amp;[a-zA-Z0-9]+;)'" />
	<xsl:variable name="charEntitiyRegex" select="concat($stringCharEntitiyRegex, '|', $numericCharEntitiyRegex)" />

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

		<xsl:variable name="cleanAmpsStr">
			<xsl:choose>
				<xsl:when test="matches($textString, $charEntitiyRegex) = true()">
					<xsl:analyze-string select="$textString" regex="{$charEntitiyRegex}">
						<xsl:matching-substring>
							<xsl:choose>
								<xsl:when test="h:isPredefinedCharacterEntity(.) = true() or matches(., $numericCharEntitiyRegex) = true()">
									<xsl:value-of select="."/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="h:replaceAmpersand(.)"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:matching-substring>
						<xsl:non-matching-substring>
							<xsl:value-of select="h:replaceAmpersand(.)"/>
						</xsl:non-matching-substring>
					</xsl:analyze-string>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="h:replaceAmpersand($textString)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="$cleanAmpsStr" />
	</xsl:function>

	<xsl:function name="h:replaceAmpersand">
		<xsl:param name="stringWithAmpersand"/>
		<xsl:value-of select="replace($stringWithAmpersand, '&amp;', '&amp;#38;')" />
	</xsl:function>

	<xsl:function name="h:isPredefinedCharacterEntity">
		<xsl:param name="entity"/>
		<xsl:value-of select="$predefinedHtmlCharacters/chars/char/@code=$entity" />
	</xsl:function>
</xsl:stylesheet>