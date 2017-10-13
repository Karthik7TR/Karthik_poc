<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:template match="x:stream[@type='main'] | x:stream[@type='footnote']">
		<xsl:for-each-group select="x:block"
			group-starting-with="x:block[@fipcblk = 'true']">
				<xsl:variable name="alignedVertically">
					<xsl:value-of select="current-group()[1]/@ipcgnum = '0'" />
				</xsl:variable>
				<xsl:variable name="numberOfColumns" select="count(current-group())" />
				<xsl:choose>
					<xsl:when
						test="number($numberOfColumns) = 1 or $alignedVertically = true()">
						<xsl:for-each select="current-group()">
							<xsl:apply-templates select="." />
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<columns />
						<xsl:for-each select="current-group()">
							<column />
							<xsl:apply-templates select="." />
							<endcolumn />
						</xsl:for-each>
						<endcolumns />
					</xsl:otherwise>
				</xsl:choose>
		</xsl:for-each-group>
	</xsl:template>

</xsl:stylesheet>