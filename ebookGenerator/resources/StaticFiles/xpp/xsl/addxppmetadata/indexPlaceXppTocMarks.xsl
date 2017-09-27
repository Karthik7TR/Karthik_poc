<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="placeXppMarks.xsl" />
	<xsl:import href="../transform-utils.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:param name="volumeName" />
	<xsl:variable name="pagesAmount" select="count(//x:pagebreak)" />

	<xsl:variable name="root_uuid" select="concat($volumeName, '.', 'index')" />

	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:root">
		<root>
			<xsl:variable name="firstIndexWord" select="./x:INDEX/x:l1[1]/x:t[1]/text()" />
			<xsl:variable name="lastIndexWord">
				<xsl:value-of select="x:get-last-index-word((//x:pagebreak)[1])" />
			</xsl:variable>
			<xsl:variable name="first_index_item"
				select="concat($volumeName, '.', 'index', x:get-first-word($firstIndexWord), x:get-first-word($lastIndexWord))" />
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid" select="$first_index_item" />
			</xsl:call-template>

			<xsl:call-template name="placeXppHier">
				<xsl:with-param name="uuid" select="$root_uuid" />
				<xsl:with-param name="name" select="'Index'" />
				<xsl:with-param name="parent_uuid" select="$root_uuid" />
				<xsl:with-param name="doc_family_uuid" select="$first_index_item" />
			</xsl:call-template>

			<xsl:apply-templates />
		</root>
	</xsl:template>

	<xsl:template match="x:pagebreak">
		<xsl:copy-of select="." />
		<xsl:if test="$pagesAmount > 20 and (@num=1 or (@num  - 1) mod 20 = 0)">
			<xsl:variable name="firstIndexWord">
				<xsl:apply-templates
					select="(following::x:l1[./x:t] | following::x:mte2[./x:t])[1]"
					mode="extracting" />
			</xsl:variable>
			<xsl:variable name="lastIndexWord">
				<xsl:value-of select="x:get-last-index-word(.)" />
			</xsl:variable>
			<xsl:variable name="currentIndexUuid">
				<xsl:value-of
					select="concat($volumeName, '.', 'index', x:get-first-word($firstIndexWord), x:get-first-word($lastIndexWord))" />
			</xsl:variable>

			<xsl:if test="@num != 1">
				<xsl:call-template name="placeSectionbreak">
					<xsl:with-param name="sectionuuid" select="$currentIndexUuid" />
				</xsl:call-template>
			</xsl:if>

			<xsl:call-template name="placeXppHier">
				<xsl:with-param name="uuid" select="$currentIndexUuid" />
				<xsl:with-param name="name"
					select="concat($firstIndexWord, '-', $lastIndexWord)" />
				<xsl:with-param name="parent_uuid" select="$root_uuid" />
				<xsl:with-param name="doc_family_uuid" select="$currentIndexUuid" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="x:pagebreak" mode="get-last-word">
		<xsl:variable name="indexElement"
			select="ancestor::x:l1[./x:t][1] | preceding::x:l1[./x:t][1]" />
		<xsl:value-of select="x:get-first-word($indexElement[last()]/x:t[1]/text())" />
	</xsl:template>

	<xsl:template match="x:l1 | x:mte2" mode="extracting">
		<xsl:variable name="indexString" select="./x:t[1]/text()" />
		<xsl:value-of select="x:get-first-word($indexString)" />
	</xsl:template>

	<xsl:function name="x:get-last-index-word">
		<xsl:param name="currentPagebreak" />
		<xsl:variable name="lastIndexWord">
			<xsl:variable name="nullableLastIndexWord">
				<xsl:apply-templates
					select="$currentPagebreak/following::x:pagebreak[(@num - 1) mod 20 = 0][1]"
					mode="get-last-word" />
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="not($nullableLastIndexWord = '')">
					<xsl:value-of select="$nullableLastIndexWord" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="notNullLastIndexWord"
						select="x:get-first-word($currentPagebreak/following::x:l1[last()]/x:t[1]/text())" />
					<xsl:value-of select="$notNullLastIndexWord" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="$lastIndexWord" />
	</xsl:function>
</xsl:stylesheet>