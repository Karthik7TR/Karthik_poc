<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:template match="x:table">
		<xsl:element name="table">
			<xsl:attribute name="cols" select="./x:tgroup/@cols" />
			<xsl:attribute name="tgroupstyle" select="./x:tgroup/@tgroupstyle" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:thead|x:tbody|x:tgroup//x:row|x:tgroup//x:row/x:entry">
		<xsl:copy>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<xsl:template
		match="x:tag[@name = 'table' or @name = 'tgroup' or @name = 'thead' or @name = 'tbody' or @name = 'row' or @name = 'entry']" />
	<xsl:template
		match="x:endtag[@name = 'table' or @name = 'tgroup' or @name = 'thead' or @name = 'tbody' or @name = 'row' or @name = 'entry']" />

</xsl:stylesheet>