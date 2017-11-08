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

	<xsl:template name="define-page-number">
		<xsl:param name="bundlePartType" />
		<xsl:variable name="serialNumber" select="@p4" />
		<xsl:variable name="closestNumber">
			<xsl:call-template name="closest-page-number" />
		</xsl:variable>
					
		<xsl:value-of>
			<xsl:choose>
				<xsl:when test="$bundlePartType = 'FRONT' or $bundlePartType = 'SUMMARY_AND_DETAILED_TABLE_OF_CONTENTS' or $bundlePartType = 'SUMMARY_TABLE_OF_CONTENTS' or $bundlePartType = 'DETAILED_TABLE_OF_CONTENTS' or matches($closestNumber, '^[ivxlcdm]+$')">
					<xsl:value-of select="x:transform-arabic-to-roman($serialNumber)" />
				</xsl:when>
				<xsl:when test="matches($closestNumber, '^[0-9]+\-[0-9]+$')">
					<xsl:value-of select="concat(substring-before($closestNumber, '-'), '-', $serialNumber)" />
				</xsl:when>
				<xsl:when test="matches($closestNumber, '^[0-9]+\-[ivxlcdm]+$')">
					<xsl:value-of select="concat(substring-before($closestNumber, '-'), '-', x:transform-arabic-to-roman($serialNumber))" />
				</xsl:when>				
				<xsl:when test="matches($closestNumber, '^[A-Za-z&amp;\- ]+\-[0-9]+$')">
					<xsl:value-of select="concat(substring-before($closestNumber, '-'), '-', $serialNumber)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$serialNumber" />
				</xsl:otherwise>					
			</xsl:choose>
		</xsl:value-of>
	</xsl:template>

	<xsl:template name="closest-page-number">
		<xsl:variable name="previousClosestNumber" select="(preceding::x:page//x:line[x:t='@FSTART@']/x:t[. != '@FSTART@' and . != '@FEND@' and . != '0'])[1]" />
		<xsl:choose>
			<xsl:when test="$previousClosestNumber != ''">
				<xsl:value-of select="$previousClosestNumber" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="(following::x:page//x:line[x:t='@FSTART@']/x:t[. != '@FSTART@' and . != '@FEND@' and . != '0'])[1]" />
			</xsl:otherwise>
		</xsl:choose>
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