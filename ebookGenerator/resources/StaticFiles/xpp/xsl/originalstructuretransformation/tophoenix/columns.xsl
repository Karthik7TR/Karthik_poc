<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:template match="x:stream[@type='main'] | x:stream[@type='footnote']">
		<xsl:for-each-group select="x:block"
			group-starting-with="x:block[@fipcblk = 'true' and not(@ipcgnum = '0')]">
			<xsl:variable name="indexOfLastColumn">
				<xsl:for-each select="current-group()">
					<xsl:if test="not(@ipcgnum = '0') and @lipcblk = 'true'">
						<xsl:value-of select="position()" />
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<xsl:variable name="safeIndexOfLastColumn">
				<xsl:choose>
					<xsl:when test="$indexOfLastColumn=''">
						<xsl:number value="0" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$indexOfLastColumn" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="numberOfColumns" select="count(current-group())" />
			<xsl:choose>
				<xsl:when
					test="number($numberOfColumns) = 1 or $safeIndexOfLastColumn = 1">
					<xsl:for-each select="current-group()">
						<xsl:apply-templates select="." />
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="current-group()">
						<xsl:variable name="index" select="position()" />
						<xsl:variable name="nextEl" select="current-group()[$index+1]" />
						<xsl:variable name="endOfColumn" select="not($nextEl/@ipcgnum = '0')" />
						<xsl:if test="$index = 1">
							<columns />
						</xsl:if>
						<xsl:if test="not(./@ipcgnum = '0')">
							<column />
						</xsl:if>
						<xsl:apply-templates select="." />
						<xsl:if test="position()=last() or $endOfColumn=true()">
							<endcolumn />
						</xsl:if>
						<xsl:choose>
							<xsl:when test="position()=last() and $safeIndexOfLastColumn > 0">
								<endcolumns />
							</xsl:when>
							<xsl:when test="position()=last() and $safeIndexOfLastColumn = 0">
								<column />
								<endcolumn />
								<endcolumns />
							</xsl:when>
							<xsl:otherwise/>
						</xsl:choose>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each-group>
	</xsl:template>

</xsl:stylesheet>