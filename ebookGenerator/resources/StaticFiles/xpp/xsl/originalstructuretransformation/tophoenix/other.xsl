<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<!-- To support Index conts -->
	<xsl:template match="x:line">
		<xsl:if test="$bundlePartType = 'INDEX'">
			<xsl:variable name="endTagsCount">
				<xsl:variable name="previousEndTagsCount"
					select="count(preceding::x:endtag[@name = 'mte2'])" />
				<xsl:choose>
					<xsl:when test="x:endtag[@name = 'mte2']">
						<xsl:value-of select="$previousEndTagsCount + 1" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$previousEndTagsCount" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:if test="count(preceding::x:tag[@name = 'mte2']) > $endTagsCount">
				<linebreak />
			</xsl:if>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<!-- TLRkey character support -->
	<xsl:template match="x:TLRkey">
		<xsl:copy />
	</xsl:template>

	<!-- Images -->
	<xsl:template match="x:image">
		<xsl:element name="proview_image">
			<xsl:copy-of select="@*" />
		</xsl:element>
	</xsl:template>

	<!-- External links -->
	<xsl:template match="x:cite.query">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>