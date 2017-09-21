<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:x="http://www.sdl.com/xpp"
	exclude-result-prefixes="x">

	<xsl:variable name="characters" select="document('specialCharacters.xml')" />
	<xsl:variable name="olds" select="string-join($characters/specialChars/charsPair/@old, '')" />
	<xsl:variable name="news" select="string-join($characters/specialChars/charsPair/@new, '')" />
	<xsl:variable name="volNamePlaceholder" select="'VOLUME_NAME_PLACEHOLDER'" />
	

	<xsl:function name="x:get-fixed-text">
		<xsl:param name="value" />
		<xsl:value-of
			select="translate($value, $olds, $news)" />
	</xsl:function>
	
	<xsl:function name="x:fix-lrre-page">
		<xsl:param name="pageNum" />
		<xsl:value-of select="replace($pageNum, 'L&amp;R', 'L&amp;amp;R')" />
	</xsl:function>
	
	<xsl:function name="x:get-class-name">
		<xsl:param name="value" />
		<xsl:value-of select="translate(translate($value,'.','_'), ';', '_')" />
	</xsl:function>
	
	<!-- for inline index numbers -->
	<xsl:function name="x:get-vertical-align">
		<xsl:param name="y" />
		<xsl:choose>
			<xsl:when test="number(substring($y, 1, string-length($y)-1)) > 0" >
				<xsl:text>sub</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>super</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<!-- for external links -->
	<xsl:function name="x:get-href-by-id">
		<xsl:param name="id" />
		<xsl:param name="mappingFile" />
		<xsl:value-of
			select="$mappingFile/mapping/entry[@id=$id]/@href" />
	</xsl:function>
	
	<!-- for index breaks -->
	<xsl:function name="x:get-first-word">
		<xsl:param name="indexString" />
		<xsl:choose>
			<xsl:when test="contains($indexString, ' ')">
				<xsl:value-of select="substring-before($indexString, ' ')" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$indexString" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>