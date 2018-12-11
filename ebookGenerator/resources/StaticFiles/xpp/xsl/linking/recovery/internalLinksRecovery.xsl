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
			<!-- A hack for cases when line starts with prefix and has no space, e.g.
				^Art.123$ -->
			<xsl:variable name="modified-text">
				<!-- Check that line starts with prefix -->
				<xsl:analyze-string select="text()" regex="{concat('^', $primarySourcePrefixPattern)}">
					<xsl:matching-substring>
						<xsl:value-of select="concat(' ', .)"/>
					</xsl:matching-substring>
					<xsl:non-matching-substring>
						<xsl:value-of select="."/>
					</xsl:non-matching-substring>
				</xsl:analyze-string>
			</xsl:variable>
			<xsl:variable name="text-to-recover">
				<xsl:choose>
					<!-- For cases when section number is detached from prefix in Primary
						Source publication text is concatenated with following x:t siblings -->
					<xsl:when
						test="x:ends-with-pattern($modified-text, $primarySourcePrefixPattern)=true()">
						<xsl:value-of
							select="concat($modified-text, string-join(x:next-detached-siblings(.)|x:next-detached-t(.)))" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$modified-text" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:call-template name="x:recover">
				<xsl:with-param name="text" select="$text-to-recover" />
			</xsl:call-template>
		</xsl:copy>
	</xsl:template>

	<xsl:function name="x:next-detached-siblings">
		<xsl:param name="current-t" />
		<xsl:variable name="next-not-t-sibling"
					  select="$current-t/following-sibling::*[not(self::x:t)][1]" />
		<xsl:choose>
			<xsl:when test="boolean($next-not-t-sibling)">
				<xsl:value-of select="$current-t/following-sibling::x:t intersect $next-not-t-sibling/preceding-sibling::x:t"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$current-t/following-sibling::x:t"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- x:t after this node's parent x:cite, but should actually be in the 
		same text node since prefix is detached -->
	<xsl:function name="x:next-detached-t">
		<xsl:param name="current-t" />
		<xsl:variable name="ancestor-cite"
			select="$current-t/(ancestor::x:CITE|ancestor::x:cite)" />
		<xsl:variable name="next-not-t-sibling"
			select="$ancestor-cite/following-sibling::*[not(self::x:t)][1]" />
		<xsl:variable name="next-prefix-ending-t"
			select="$ancestor-cite/following::x:t[x:ends-with-pattern(text(), $primarySourcePrefixPattern)=true()][1]" />
		<xsl:variable name="next-separator"
			select="($next-not-t-sibling|$next-prefix-ending-t)[1]" />
		<xsl:variable name="detached-ts"
			select="$next-separator/preceding::x:t
					intersect
					$ancestor-cite/following-sibling::x:t" />
		<xsl:choose>
			<xsl:when test="not($detached-ts='')">
				<xsl:value-of select="$detached-ts" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="$current-t/(ancestor::x:CITE|ancestor::x:cite)/following-sibling::x:t" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- Removes following x:t siblings for case of detached prefix -->
	<xsl:template match="x:t[x:has-preceding-prefix(.)=true()]" />

    <!-- x:t after this node's parent x:cite -->
    <xsl:function name="x:has-preceding-prefix">
		<xsl:param name="current-t" />
		<!-- using last() because of parenthesis discarding axis reversal -->
		<xsl:variable name="preceding-prefix-node"
			select="$current-t/(preceding-sibling::x:t|(preceding-sibling::x:CITE|preceding-sibling::x:cite)/x:t)[x:ends-with-pattern(text(), $primarySourcePrefixPattern)=true()][last()]" />
		<xsl:variable name="not-t-nodes-after-prefix"
			select="$preceding-prefix-node/following::*[not(name()='t')]" />
		<xsl:variable name="preceding-nodes" select="$current-t/preceding::*" />
		<xsl:value-of
			select="x:ends-with-pattern($current-t/text(), $primarySourcePrefixPattern)=false() and boolean($preceding-prefix-node)
								  and boolean($not-t-nodes-after-prefix intersect $preceding-nodes)=false()" />
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
				<xsl:value-of select="." />
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
