<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:template match="x:proview_image">
		<xsl:variable name="floatClass">
			<xsl:if test="parent::x:fm.image.wrap/@quad">
				<xsl:value-of select="concat('quad', '_', parent::x:fm.image.wrap/@quad)" />
			</xsl:if>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$floatClass != ''">
				<xsl:element name="div">
					<xsl:attribute name="class" select="$floatClass" />
					<xsl:call-template name="create-image-element" />
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="create-image-element" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="create-image-element">
		<xsl:variable name="guid">
			<xsl:call-template name="substring-before-last">
				<xsl:with-param name="string" select="@id" />
				<xsl:with-param name="delim" select="'.'" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="quadImage">
			<xsl:if test="ancestor::x:fm.image.wrap/@quad">
				<xsl:value-of select="'quadImage'" />
			</xsl:if>
		</xsl:variable>
		
		<xsl:element name="img">
			<xsl:variable name="class" select="x:get-class(.)" />
			<xsl:if test="$class!=''">
				<xsl:attribute name="class" select="$class" />
			</xsl:if>
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
	
	<xsl:function name="x:get-class">
		<xsl:param name="node" />
		<xsl:variable name="tr_image">
			<xsl:if test="$node/@table-viewer='true'">
				<xsl:value-of select="'tr_image'" />
			</xsl:if>
		</xsl:variable>
		<xsl:value-of select="string-join(($tr_image, $node/@classes)[.!=''], ' ')" />
	</xsl:function>
</xsl:stylesheet>