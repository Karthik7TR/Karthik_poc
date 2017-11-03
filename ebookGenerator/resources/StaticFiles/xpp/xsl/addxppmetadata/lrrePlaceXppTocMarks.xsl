<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="placeXppMarks.xsl" />
	<xsl:import href="../transform-utils.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:param name="volumeName" />

	<xsl:variable name="root_uuid" select="concat($volumeName, '.', 'lrre')" />
	<xsl:variable name="pagesAmount" select="count(//x:pagebreak)" />

	<xsl:template match="x:root">
		<xsl:element name="root">
			<xsl:variable name="first_lrre_item" select="concat($volumeName, '.', 'lrre', '1to', x:get-last-page(1, $pagesAmount))" />
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid" select="$first_lrre_item" />
			</xsl:call-template>

			<xsl:call-template name="placeXppHier">
				<xsl:with-param name="uuid" select="$root_uuid" />
				<xsl:with-param name="name" select="'Table of LRRE'" />
				<xsl:with-param name="parent_uuid" select="$root_uuid" />
				<xsl:with-param name="doc_family_uuid" select="$first_lrre_item" />
			</xsl:call-template>

			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:pagebreak">
		<xsl:copy-of select="." />
		<xsl:if test="$pagesAmount > 20 and (./@serial-num=1 or (./@serial-num  - 1) mod 20 = 0)">
			<xsl:variable name="endPage" select="x:get-last-page(@serial-num, $pagesAmount)" />
			<xsl:variable name="currentLrreUuid"
				select="concat($volumeName, '.', 'lrre', @serial-num, 'to', $endPage)" />
			
			<xsl:if test="@serial-num != 1">
				<xsl:call-template name="placeSectionbreak">
					<xsl:with-param name="sectionuuid" select="$currentLrreUuid" />
				</xsl:call-template>
			</xsl:if>

			<xsl:call-template name="placeXppHier">
				<xsl:with-param name="uuid" select="$currentLrreUuid" />
				<xsl:with-param name="name" select="concat('p. ', ./@serial-num, '-', $endPage)" />
				<xsl:with-param name="parent_uuid" select="$root_uuid" />
				<xsl:with-param name="doc_family_uuid" select="$currentLrreUuid" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>
	
	<xsl:function name="x:get-last-page">
		<xsl:param name="currentPage" />
		<xsl:param name="globalPagesAmount" />
		<xsl:choose>
			<xsl:when test="$globalPagesAmount - $currentPage >= 20">
				<xsl:value-of select="$currentPage + 19" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$pagesAmount" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

</xsl:stylesheet>