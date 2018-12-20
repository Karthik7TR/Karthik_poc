<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="xml" omit-xml-declaration="yes" />
	<xsl:param name="pattern" />
	<xsl:param name="primarySourcePrefixPattern" />
	<xsl:param name="sectionNumberMapFile" />

	<xsl:variable name="sectionNumberMap" select="document($sectionNumberMapFile)" />
	<xsl:variable name="regex-workaround-prefix" select="'REGEX_WORKAROUND_PREFIX'"/>

	<xsl:template match="node()">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:for-each-group select="*" group-adjacent="boolean(self::x:t)">
				<xsl:choose>
					<xsl:when test="current-grouping-key()=true()">
						<xsl:for-each-group select="current-group()" group-starting-with="x:t[not(starts-with(@style, 'unknown'))
																			  or
																			  boolean(preceding-sibling::x:t)=false()]">
							<xsl:element name="t">
								<xsl:for-each select="current-group()[1]/@*">
									<xsl:copy-of select="current()"/>
								</xsl:for-each>
								<!-- A workaround for cases when line starts with prefix and has no space, e.g.
                                    ^Art.123$. Removed inside x:wrap template due to XSLT limitations on variable
                                    value, which can only be plain text -->
								<xsl:variable name="modified-text" select="x:add-workaround-prefix(string-join(current-group()/text()))"/>
								<xsl:call-template name="x:recover">
									<xsl:with-param name="text" select="$modified-text" />
								</xsl:call-template>
							</xsl:element>
						</xsl:for-each-group>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="current-group()"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each-group>
		</xsl:copy>
	</xsl:template>

	<xsl:function name="x:add-workaround-prefix">
		<xsl:param name="text"/>
		<!-- Check that line starts with prefix -->
		<xsl:variable name="text-nodes">
			<xsl:analyze-string select="$text" regex="{concat('^', $primarySourcePrefixPattern)}" flags=";j">
				<xsl:matching-substring>
					<xsl:value-of select="concat($regex-workaround-prefix, ' ', .)"/>
				</xsl:matching-substring>
				<xsl:non-matching-substring>
					<xsl:value-of select="."/>
				</xsl:non-matching-substring>
			</xsl:analyze-string>
		</xsl:variable>
		<xsl:value-of select="string-join($text-nodes)"/>
	</xsl:function>

	<xsl:function name="x:remove-workaround-prefix">
		<xsl:param name="text"/>
		<xsl:value-of select="replace($text, concat($regex-workaround-prefix, '\s'), '')"/>
	</xsl:function>

	<xsl:template name="x:recover">
		<xsl:param name="text" />
		<!-- The ";j" flag is the key here to turn on look-around features supported 
			by Java specification -->
		<xsl:analyze-string select="$text" regex="{$pattern}"
			flags=";j">
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
				<xsl:variable name="primary-source-match" select="regex-group(5)" />
				<xsl:choose>
					<xsl:when test="not($primary-source-match='')">
						<xsl:call-template name="x:wrap">
							<xsl:with-param name="normalized-section-number"
								select="$primary-source-match" />
							<xsl:with-param name="section-number" select="$primary-source-match" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="x:process-section-number">
							<xsl:with-param name="range" select="$range" />
							<xsl:with-param name="start-from" select="$start-from" />
							<xsl:with-param name="parent-section-number"
								select="$parent-section-number" />
							<xsl:with-param name="end-with" select="$end-with" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:matching-substring>
			<xsl:non-matching-substring>
				<xsl:value-of select="x:remove-workaround-prefix(.)" />
			</xsl:non-matching-substring>
		</xsl:analyze-string>
	</xsl:template>

	<xsl:template name="x:process-section-number">
		<xsl:param name="range" />
		<xsl:param name="start-from" />
		<xsl:param name="parent-section-number" />
		<xsl:param name="end-with" />
		<xsl:choose>
			<xsl:when test="string-length($end-with) > 0">
				<xsl:call-template name="x:wrap-range">
					<xsl:with-param name="start-from" select="$start-from" />
					<xsl:with-param name="parent-section-number" select="$parent-section-number" />
					<xsl:with-param name="end-with" select="$end-with" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="x:wrap">
					<xsl:with-param name="section-number" select="$range" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="x:wrap-range">
		<xsl:param name="start-from" />
		<xsl:param name="parent-section-number" />
		<xsl:param name="end-with" />
		<xsl:variable name="normalized-end-with"
			select="x:get-normalized-end-with($parent-section-number, $end-with)" />
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

	<xsl:function name="x:get-normalized-end-with">
		<xsl:param name="parent-section-number" />
		<xsl:param name="end-with" />
		<xsl:choose>
			<xsl:when test="not(contains($end-with, ':'))">
				<xsl:value-of select="concat($parent-section-number, ':', $end-with)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$end-with" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:template name="x:wrap">
		<xsl:param name="section-number" />
		<xsl:param name="normalized-section-number" select="$section-number" />
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

	<xsl:function name="x:ends-with-pattern">
		<xsl:param name="text" />
		<xsl:param name="target-pattern" />
		<xsl:variable name="match-result">
			<xsl:analyze-string regex="{$target-pattern}"
				select="$text" flags=";j">
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
