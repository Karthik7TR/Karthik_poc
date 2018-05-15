<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="placeXppMarks.xsl" />
	<xsl:import href="../transform-utils.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:param name="materialNumber" />
	<xsl:param name="indexId" />
	<xsl:param name="indexName" />
	<xsl:variable name="pagesAmount" select="count(//x:pagebreak)" />

	<xsl:variable name="root_uuid" select="concat($materialNumber, '.', $indexId)" />

	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:root">
		<root>
			<xsl:variable name="firstIndexWord" select="x:get-first-word((.//x:t[@index-header='true']/text() | .//x:INDEX/x:l1[1]/x:t[1]/text())[1])" />
			<xsl:variable name="lastIndexWord">
				<xsl:value-of select="x:get-last-index-word((//x:pagebreak)[1])" />
			</xsl:variable>
			<xsl:variable name="first_index_item"
				select="concat($materialNumber, '.', $indexId, x:get-first-word($firstIndexWord), x:get-first-word($lastIndexWord))" />
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid" select="$first_index_item" />
			</xsl:call-template>

			<xsl:call-template name="placeXppHier">
				<xsl:with-param name="uuid" select="$root_uuid" />
				<xsl:with-param name="name" select="$indexName" />
				<xsl:with-param name="parent_uuid" select="$root_uuid" />
				<xsl:with-param name="doc_family_uuid" select="$first_index_item" />
			</xsl:call-template>

			<xsl:apply-templates />
		</root>
	</xsl:template>

	<xsl:template match="x:pagebreak">
		<xsl:copy-of select="." />
		<xsl:if test="$pagesAmount > 20 and (@serial-num=1 or (@serial-num  - 1) mod 20 = 0)">
			<xsl:variable name="firstIndexWord">
				<xsl:value-of select="x:get-first-index-word(.)" />
			</xsl:variable>
			<xsl:variable name="lastIndexWord">
				<xsl:value-of select="x:get-last-index-word(.)" />
			</xsl:variable>
			<xsl:variable name="currentIndexUuid">
				<xsl:value-of
					select="concat($materialNumber, '.', $indexId, $firstIndexWord, $lastIndexWord)" />
			</xsl:variable>
			
			<xsl:if test="not(@serial-num = 1)">
				<xsl:call-template name="placeSectionbreak">
					<xsl:with-param name="sectionuuid" select="$currentIndexUuid" />
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="not($firstIndexWord = '') and not($lastIndexWord = '')">
				<xsl:call-template name="placeXppHier">
					<xsl:with-param name="uuid" select="$currentIndexUuid" />
					<xsl:with-param name="name"
						select="concat($firstIndexWord, ' - ', $lastIndexWord)" />
					<xsl:with-param name="parent_uuid" select="$root_uuid" />
					<xsl:with-param name="doc_family_uuid" select="$currentIndexUuid" />
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
	<xsl:function name="x:get-first-index-word">
		<xsl:param name="currentPagebreak" />
		<xsl:variable name="nextPagebreak" select="$currentPagebreak/following::x:pagebreak[1]" />
		<xsl:variable name="rutterIndexHeader">
			<xsl:choose>
				<xsl:when test="not($nextPagebreak='')">
					<xsl:value-of select="$currentPagebreak/following::x:t[@index-header='true'][1] intersect $nextPagebreak/preceding::x:t" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$currentPagebreak/following::x:t[@index-header='true'][1]" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="usualIndexHeader" select="(($currentPagebreak/following::x:l1[./x:t]/x:t[1])[not(normalize-space(text())='') and not(contains(@style, 'cgt'))] | $currentPagebreak/following::x:mte2[./x:t]/x:t[1])[1]" />
		<xsl:variable name="t" select="($rutterIndexHeader | $usualIndexHeader)[1]" />
		<xsl:value-of select="x:get-first-word($t)" />
	</xsl:function>

	<xsl:function name="x:get-last-index-word">
		<xsl:param name="currentPagebreak" as="node()" />
		<xsl:variable name="lastPagebreak" select="(
			($currentPagebreak/following::x:pagebreak[@serial-num mod 20 = 0 and x:has-header(.) = true()][1]) 
			| (($currentPagebreak/following::x:pagebreak[x:has-header(.) = true()][last()] | $currentPagebreak/self::node()[x:has-header(.)])[last()])
		)[1]" />
		
		<xsl:variable name="nextPagebreak" select="$lastPagebreak/following::x:pagebreak[1]" />
		<xsl:choose>
			<xsl:when test="not($nextPagebreak)">
				<xsl:value-of select="x:get-first-word(x:get-header($lastPagebreak)[last()])" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="x:get-first-word((x:get-header($lastPagebreak) intersect $nextPagebreak/preceding::x:t)[last()])" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="x:has-header">
		<xsl:param name="pagebreak" />
		<xsl:variable name="nextPagebreak" select="$pagebreak/following::x:pagebreak[1]" />
		<xsl:variable name="headerSuspects" select="$pagebreak/following::x:t[@index-header='true'] | $pagebreak/following::x:l1[./x:t[not(normalize-space(./text())='')]]/x:t | $pagebreak/following::x:mte2[./x:t]/x:t" />
		<xsl:choose>
			<xsl:when test="not($nextPagebreak)">
				<xsl:value-of select="boolean($headerSuspects)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="boolean($headerSuspects intersect $nextPagebreak/preceding::x:t)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="x:get-header">
		<xsl:param name="pagebreak" />
		<xsl:sequence select="$pagebreak/following::x:t[@index-header='true'] 
			| $pagebreak/following::x:l1[./x:t[not(normalize-space(./text())='')]]/x:t[1] 
			| $pagebreak/following::x:mte2[./x:t]/x:t[1]" />
	</xsl:function>
</xsl:stylesheet>