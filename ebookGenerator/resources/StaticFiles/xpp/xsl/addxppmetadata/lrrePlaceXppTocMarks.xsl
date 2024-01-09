<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="placeXppMarks.xsl" />
	<xsl:import href="../transform-utils.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:param name="materialNumber" />

	<xsl:variable name="root_uuid" select="concat($materialNumber, '.', 'lrre')" />
	<xsl:variable name="pagesAmount" select="count(//x:pagebreak)" />

	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:root">
		<xsl:element name="root">
			<xsl:variable name="first_lrre_item"
				select="concat($materialNumber, '.', 'lrre', '1to', x:get-last-page(1, $pagesAmount))" />
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
		<xsl:choose>
			<xsl:when
				test="$pagesAmount > 20 and (./@serial-num=1 or (./@serial-num  - 1) mod 20 = 0)">
				<xsl:variable name="startPageNumber" select="@serial-num" />
				<xsl:variable name="endPageNumber"
					select="x:get-last-page(@serial-num, $pagesAmount)" />
				<xsl:variable name="uuid"
					select="concat($materialNumber, '.', 'lrre', $startPageNumber, 'to', $endPageNumber)" />

				<xsl:variable name="firstItemName" select="x:get-first-item(.)" />
				<xsl:variable name="lastItemName"
					select="x:get-last-item(//x:pagebreak[@serial-num=$endPageNumber])" />

				<xsl:if test="@serial-num != 1">
					<xsl:call-template name="placeSectionbreak">
						<xsl:with-param name="sectionuuid" select="$uuid" />
					</xsl:call-template>
				</xsl:if>

				<xsl:copy-of select="." />

				<xsl:call-template name="placeXppHier">
					<xsl:with-param name="uuid" select="$uuid" />
					<xsl:with-param name="name"
						select="concat($firstItemName, ' - ', $lastItemName)" />
					<xsl:with-param name="parent_uuid" select="$root_uuid" />
					<xsl:with-param name="doc_family_uuid" select="$uuid" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="." />
			</xsl:otherwise>
		</xsl:choose>
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

	<xsl:function name="x:get-first-item">
		<xsl:param name="currentPagebreak" />
		<xsl:choose>
			<xsl:when
				test="$currentPagebreak/following-sibling::x:t[@style='main.head']">
				<xsl:value-of
					select="$currentPagebreak/following-sibling::x:t[@style='main.head']/text()" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="nextPagebreak"
					select="$currentPagebreak/following::x:pagebreak[1]" />
				<xsl:variable name="followingFirstHeading">
					<xsl:value-of
						select="$currentPagebreak/(following::x:main.head
                                                    intersect
                                                    $nextPagebreak/preceding::x:main.head)[1]/x:t[@style='main.head']/text()" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="not($followingFirstHeading='')">
						<xsl:value-of select="$followingFirstHeading" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of
							select="$currentPagebreak/preceding::x:main.head[1]/x:t[@style='main.head']/text()" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:function name="x:get-last-item">
		<xsl:param name="lastPagebreak" as="node()" />
		<xsl:variable name="followingLastHeading">
			<xsl:choose>
				<xsl:when test="$lastPagebreak/following::x:pagebreak">
					<xsl:value-of
						select="$lastPagebreak/(following::x:main.head
                                            intersect
                                            following::x:pagebreak[1]/preceding::x:main.head)[last()]/x:t[@style='main.head']/text()" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of
						select="$lastPagebreak/following::x:main.head[last()]/x:t[@style='main.head']/text()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="not($followingLastHeading='')">
				<xsl:value-of select="$followingLastHeading" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="$lastPagebreak/preceding::x:main.head[1]/x:t[@style='main.head']/text()" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>
