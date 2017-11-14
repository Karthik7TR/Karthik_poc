<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:template match="@* | node()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()" />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="x:cite.query">
		<xsl:choose>
			<xsl:when
				test=".//x:endtag[not(preceding-sibling::x:tag/@name=@name)] 
		| .//x:tag[not(following-sibling::x:endtag/@name=@name)]">
				<xsl:apply-templates mode="process" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="." />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="x:t" mode="process">
		<xsl:choose>
			<xsl:when test="ancestor::x:cite.query and not(./x:tag)">
				<xsl:element name="cite.query">
					<xsl:copy-of select="ancestor::x:cite.query/@*" />
					<xsl:copy-of select="." />
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="." />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="@* 
		| node()" mode="process">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>