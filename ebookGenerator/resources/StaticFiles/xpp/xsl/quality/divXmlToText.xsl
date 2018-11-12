<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns:h="http://www.w3.org/1999/xhtml" xmlns="http://www.sdl.com/xpp"
	exclude-result-prefixes="x">
	<xsl:output method="text" indent="no" omit-xml-declaration="yes" />
	<xsl:param name="streamType" />
	<xsl:param name="entitiesDocType" />
	<xsl:include href="../transform-utils.xsl" />
	<xsl:include href="../unescape/unescape-function.xsl" />

	<!--Extracts text from all nodes inside <document/>, preserving parent tag-->
	<xsl:template match="x:document">
		<xsl:value-of
			select="'&lt;document version=&quot;1.0&quot; xmlns=&quot;http://www.sdl.com/xpp&quot;&gt;'" />
		<xsl:apply-templates />
		<xsl:value-of select="'&lt;/document&gt;'" />
	</xsl:template>

	<xsl:template match="x:stream[@type='frills']//x:tag[@name='custserv']">
		<xsl:if test="$streamType='main'">
			<xsl:value-of select="following-sibling::x:t[1]/text()" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="x:stream[@type=$streamType]//x:t[@suppress and not(.//x:cite.query)]" />

	<xsl:template
		match="x:stream[@type=$streamType]//x:t[not(@suppress)]/text()
						| x:stream[@type=$streamType]//x:t[@suppress='true' and child::x:cite.query]/text()
    					| x:stream[@type=$streamType]//x:t/x:cite.query/text()
    					| x:stream[@type=$streamType]//x:case.history/text()">
		<xsl:value-of select="h:unescape(replace(replace(x:get-fixed-text(self::node()), '&gt;', '&amp;gt;'), '&lt;', '&amp;lt;'))"
			disable-output-escaping="yes" />
	</xsl:template>

	<xsl:template match="text()" />

</xsl:stylesheet>
