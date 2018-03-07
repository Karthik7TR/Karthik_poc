<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:template match="x:proview_image">
		<xsl:variable name="guid">
			<xsl:call-template name="substring-before-last">
				<xsl:with-param name="string" select="@id" />
				<xsl:with-param name="delim" select="'.'" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:element name="img">
			<xsl:attribute name="class" select="'tr_image'" />
			<xsl:attribute name="assetid" select="concat('er:#', $guid)" />
			<xsl:attribute name="src" select="concat('er:#', $guid)" />
		</xsl:element>
	</xsl:template>

	<xsl:template name="substring-before-last">
		<xsl:param name="string" select="''" />
		<xsl:param name="delim" select="''" />

		<xsl:if test="$string != '' and $delim != ''">
			<xsl:variable name="head" select="substring-before($string, $delim)" />
			<xsl:variable name="tail" select="substring-after($string, $delim)" />
			<xsl:value-of select="$head" />
			<xsl:if test="contains($tail, $delim)">
				<xsl:value-of select="$delim" />
				<xsl:call-template name="substring-before-last">
					<xsl:with-param name="string" select="$tail" />
					<xsl:with-param name="delim" select="$delim" />
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>