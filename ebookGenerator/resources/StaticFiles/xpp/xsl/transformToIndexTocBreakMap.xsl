<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="transform-utils.xsl" />
	<xsl:output indent="yes"/>
	
	<xsl:template match="x:root">
		<indexbreaks>
			<xsl:if test="count(//x:pagebreak)>20">
				<xsl:apply-templates />
			</xsl:if>
		</indexbreaks>
	</xsl:template>

	<xsl:template match="x:pagebreak">
		<xsl:variable name="pageNum" select="./@num" />

		<xsl:if test="$pageNum = 1 or $pageNum mod 20 = 0">
			<xsl:variable name="word">
				<xsl:apply-templates select="following::x:l1[1]"
					mode="extracting" />
			</xsl:variable>

			<xsl:element name="indexbreak">
				<xsl:attribute name="startWord" select="$word" />
				<xsl:attribute name="endWord">
					<xsl:variable name="lastWord">
						<xsl:apply-templates select="following::x:pagebreak[@num mod 20 = 0][1]"
							mode="get-last-word" />
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="$lastWord = ''">
							<xsl:value-of select="x:get-first-word(following::x:l1[last()]/x:t[1]/text())" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$lastWord" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template match="x:pagebreak" mode="get-last-word">
		<xsl:variable name="indexString" select="preceding::x:l1[1]/x:t[1]/text()" />
	<xsl:value-of select="x:get-first-word($indexString)" />
	</xsl:template>

	<xsl:template match="x:l1" mode="extracting">
		<xsl:variable name="indexString" select="./x:t[1]/text()" />
		<xsl:value-of select="x:get-first-word($indexString)" />
	</xsl:template>

	<xsl:template match="text()" />
</xsl:stylesheet>