<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="xml" omit-xml-declaration="yes" />
	<xsl:param name="pattern" />
	<xsl:param name="primarySourcePrefixPattern" />
	<xsl:param name="sectionNumberMapFile" />

	<xsl:variable name="sectionNumberMap" select="document($sectionNumberMapFile)" />

	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:t[not(ancestor::x:XPPLink)]">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:choose>
				<!-- For cases when section number is separated from prefix in Primary 
					Source publication -->
				<xsl:when
					test="x:ends-with-pattern(preceding-sibling::x:t[1]/text(), $primarySourcePrefixPattern)=true()">
					<xsl:call-template name="x:wrap">
						<xsl:with-param name="section-number" select="text()" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="x:recover">
						<xsl:with-param name="text" select="text()" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:copy>
	</xsl:template>

	<xsl:template name="x:recover">
		<xsl:param name="text" />
		<xsl:analyze-string select="$text" regex="{$pattern}">
			<xsl:matching-substring>
				<!-- Example: 4:123-125 
							range: 4:123-125 
							start-from: 4:123 	
							parent-section-number: 4 
							end-with: 125 -->
				<xsl:variable name="range" select="regex-group(1)" />
				<xsl:variable name="start-from" select="regex-group(2)" />
				<xsl:variable name="parent-section-number" select="regex-group(3)" />
				<xsl:variable name="end-with" select="regex-group(4)" />
				<xsl:choose>
					<xsl:when test="string-length($end-with) > 0">
						<xsl:call-template name="x:wrap-range">
							<xsl:with-param name="start-from" select="$start-from" />
							<xsl:with-param name="parent-section-number"
								select="$parent-section-number" />
							<xsl:with-param name="end-with" select="$end-with" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="x:wrap">
							<xsl:with-param name="section-number" select="$range" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:matching-substring>
			<xsl:non-matching-substring>
				<xsl:value-of select="." />
			</xsl:non-matching-substring>
		</xsl:analyze-string>
	</xsl:template>

	<xsl:template name="x:wrap-range">
		<xsl:param name="start-from" />
		<xsl:param name="parent-section-number" />
		<xsl:param name="end-with" />
		<xsl:variable name="normalized-end-with"
			select="concat($parent-section-number, ':', $end-with)" />
		<xsl:call-template name="x:wrap">
			<xsl:with-param name="section-number" select="$start-from" />
		</xsl:call-template>
		<!-- TODO: change to customizable delimeter -->
		<xsl:value-of select="'-'" />
		<xsl:call-template name="x:wrap">
			<xsl:with-param name="section-number" select="$end-with" />
			<xsl:with-param name="normalized-section-number"
				select="$normalized-end-with" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="x:wrap">
		<xsl:param name="section-number" />
		<xsl:param name="normalized-section-number" select="x:remove-prefix($section-number)" />
		<xsl:variable name="matching-guid"
			select="($sectionNumberMap/x:map/x:entry[@section-number=$normalized-section-number]/@guid)[1]" />
		<xsl:choose>
			<xsl:when test="string-length($matching-guid) > 0">
				<xsl:element name="XPPLink">
					<xsl:attribute name="guid" select="$matching-guid" />
					<xsl:value-of select="$section-number" />
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$section-number" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- This is needed to remove the prefix, because Saxon doesn't support 
		the look-behind search for regex -->
	<xsl:function name="x:remove-prefix">
		<xsl:param name="section-number" />
		<xsl:value-of
			select="replace($section-number, $primarySourcePrefixPattern, '')" />
	</xsl:function>

	<xsl:function name="x:ends-with-pattern">
		<xsl:param name="text" />
		<xsl:param name="target-pattern" />
		<xsl:variable name="match-result">
			<xsl:analyze-string regex="{$target-pattern}"
				select="$text">
				<xsl:matching-substring>
					<xsl:value-of select="'true'" />
				</xsl:matching-substring>
				<xsl:non-matching-substring>
					<xsl:value-of select="'false'" />
				</xsl:non-matching-substring>
			</xsl:analyze-string>
		</xsl:variable>
		<xsl:value-of select="ends-with($match-result, 'true')" />
	</xsl:function>
</xsl:stylesheet>
