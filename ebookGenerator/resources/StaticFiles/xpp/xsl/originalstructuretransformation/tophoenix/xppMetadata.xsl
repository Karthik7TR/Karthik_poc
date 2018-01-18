<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:template
		match="processing-instruction('XPPMetaData') | processing-instruction('XPPHier')">
		<xsl:variable name="apostrophe">'</xsl:variable>
		<xsl:variable name="tail" select="substring-after(., ' name=')" />
		<xsl:variable name="name"
			select="replace(replace($tail, '\s[A-Za-z._]{1,}=.*', ''), $apostrophe, '')" />

		<xsl:value-of select="concat('&lt;', name(), ' ')"
			disable-output-escaping="yes" />
		<xsl:value-of select="." />
		
		<xsl:variable name="pageNum" select="ancestor::x:page/@p4" />
		<xsl:variable name="streamType" select="ancestor::x:stream/@type" />
		<xsl:if test="not(following::x:t[ancestor::x:page/@p4 = $pageNum and ancestor::x:stream/@type = $streamType])">
			<xsl:text disable-output-escaping="yes"><![CDATA[ no-page-content="true"]]></xsl:text>
		</xsl:if>
		
		<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
		<xsl:value-of select="$name" disable-output-escaping="yes" />
		<xsl:value-of select="concat('&lt;/', name(), '&gt;')"
			disable-output-escaping="yes" />
	</xsl:template>

	<xsl:template match="processing-instruction('XPPSummaryTOCAnchor')">
		<xsl:value-of select="concat('&lt;', name(), ' ')"
			disable-output-escaping="yes" />
		<xsl:value-of select="." />
		<xsl:text disable-output-escaping="yes"><![CDATA[/>]]></xsl:text>
	</xsl:template>

	<xsl:template match="processing-instruction('XPPTOCLink') | processing-instruction('XPPLink') | processing-instruction('XPPSummaryTOCLink')">
		<xsl:value-of select="concat('&lt;', name(), ' ')"
			disable-output-escaping="yes" />
		<xsl:value-of select="." />
		<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
	</xsl:template>

	<xsl:template match="processing-instruction('XPPTOCEndLink') | processing-instruction('XPPEndLink') | processing-instruction('XPPSummaryTOCEndLink')">
		<xsl:value-of
			select="concat('&lt;', '/', replace(name(), 'End', ''), '&gt;')"
			disable-output-escaping="yes" />
	</xsl:template>
</xsl:stylesheet>