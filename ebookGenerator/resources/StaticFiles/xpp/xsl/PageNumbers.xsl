<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:variable name="romanNumbersMap">
			<entry key="1">I</entry>
			<entry key="4">IV</entry>
			<entry key="5">V</entry>
			<entry key="9">IX</entry>
			<entry key="10">X</entry>
			<entry key="40">XL</entry>
			<entry key="50">L</entry>
			<entry key="90">XC</entry>
			<entry key="100">C</entry>
			<entry key="400">CD</entry>
			<entry key="500">D</entry>
			<entry key="900">CM</entry>
			<entry key="1000">M</entry>
	</xsl:variable>
	
	<xsl:template name="page-numbers">
		<xsl:param name="bundlePartType" />
		<xsl:variable name="pageNumber" select="@p4" />
		<xsl:choose>
			<xsl:when test="$bundlePartType = 'FRONT'">
				<xsl:value-of select="x:transform-arabic-to-roman($pageNumber)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$pageNumber" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="print-page-numbers">
		<xsl:for-each select=".//x:line[x:t='@FSTART@']/x:t">
			<xsl:if test=". != '@FSTART@' and . != '@FEND@'">
				<xsl:value-of select="." />
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:function name="x:transform-arabic-to-roman">
		<xsl:param name="number" />
		<xsl:if test="number($number) = number($number)">
			<xsl:choose>
				<xsl:when test="$romanNumbersMap/x:entry[@key=$number] != ''">
					<xsl:value-of select="$romanNumbersMap/x:entry[@key=$number]" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="maxKeyValue" select="max($romanNumbersMap/x:entry[number(@key) &lt; $number]/@key)" />
					<xsl:value-of select="concat($romanNumbersMap/x:entry[@key=$maxKeyValue], x:transform-arabic-to-roman($number - $maxKeyValue))" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:function>
	
</xsl:stylesheet>