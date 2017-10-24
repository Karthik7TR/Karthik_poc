<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:variable name="romanNumbersMap">
		<entry key="1">i</entry>
		<entry key="4">iv</entry>
		<entry key="5">v</entry>
		<entry key="9">ix</entry>
		<entry key="10">x</entry>
		<entry key="40">xl</entry>
		<entry key="50">l</entry>
		<entry key="90">xc</entry>
		<entry key="100">c</entry>
		<entry key="400">cd</entry>
		<entry key="500">d</entry>
		<entry key="900">cm</entry>
		<entry key="1000">m</entry>
	</xsl:variable>

	<xsl:template match="x:page" mode="prev_page">
		<xsl:call-template name="print-page-numbers" />
	</xsl:template>

	<xsl:template name="page-numbers">
		<xsl:param name="bundlePartType" />
		<xsl:variable name="pn" select="@p4" />
		<xsl:variable name="pageNumber">
			<xsl:choose>
				<xsl:when
					test="$bundlePartType = 'FRONT' or $bundlePartType = 'SUMMARY_AND_DETAILED_TABLE_OF_CONTENTS' or $bundlePartType = 'SUMMARY_TABLE_OF_CONTENTS' or $bundlePartType = 'DETAILED_TABLE_OF_CONTENTS'">
					<xsl:value-of select="x:transform-arabic-to-roman($pn)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$pn" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:for-each select="1 to count(preceding::x:page[@p4=$pn])">
			<xsl:value-of select="'-'" />
		</xsl:for-each>
		<xsl:value-of select="$pageNumber" />
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
					<xsl:variable name="maxKeyValue"
						select="max($romanNumbersMap/x:entry[number(@key) &lt; $number]/@key)" />
					<xsl:value-of
						select="concat($romanNumbersMap/x:entry[@key=$maxKeyValue], x:transform-arabic-to-roman($number - $maxKeyValue))" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:function>

</xsl:stylesheet>