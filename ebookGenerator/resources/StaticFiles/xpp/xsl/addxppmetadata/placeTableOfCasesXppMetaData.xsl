<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="placeXppMarks.xsl" />

    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
    
	<xsl:param name="volumeName" />
	<xsl:param name="TOCPartName" />

	<xsl:variable name="root_uuid">
		<xsl:choose>
			<xsl:when test="$volumeName">
				<xsl:value-of select="concat($volumeName, '.', replace($TOCPartName, '_', ''))"  />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'table_of_cases'"  />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="x:root">
		<root>
			<xsl:variable name="firstCase" select=".//x:tbl.row[1]/tbl.row.ref//x:t/text()" />
			<xsl:variable name="lastCase">
				<xsl:apply-templates select="following::pagebreak[@num = 20]" mode="firstPreviousCase" />
			</xsl:variable>
			<xsl:variable name="first_item" select="concat($root_uuid, '1')" />
				
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid" select="$first_item" />
			</xsl:call-template>

			<xsl:call-template name="placeXppHier">
				<xsl:with-param name="uuid" select="$root_uuid" />
				<xsl:with-param name="name" select="'Table Of Cases'" />
				<xsl:with-param name="parent_uuid" select="$root_uuid" />
				<xsl:with-param name="doc_family_uuid" select="$first_item" />
			</xsl:call-template>

			<xsl:apply-templates />
		</root>
	</xsl:template>
	
	<xsl:template match="x:pagebreak[@num = 1 or @num mod 20 = 0]">
		<xsl:variable name="uuid" select="concat($root_uuid, @num)" />
		<xsl:variable name="firstCase" select="string-join(following::x:tbl.row[1]/x:tbl.row.ref//x:t/text(), '')" />
		<xsl:variable name="lastCase">
			<xsl:choose>
				<xsl:when test="following::x:pagebreak[@num mod 20 = 0][1]">
					<xsl:apply-templates select="following::x:pagebreak[@num mod 20 = 0][1]" mode="firstPreviousCase" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="following::x:tbl.unit[last()]/x:tbl.row[1]/x:tbl.row.ref//x:t/text()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
	
		<xsl:if test="@num != 1">
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid" select="$uuid" />
			</xsl:call-template>
		</xsl:if>
		
		<xsl:copy-of select="." />

		<xsl:call-template name="placeXppHier">
			<xsl:with-param name="uuid" select="$uuid" />
			<xsl:with-param name="name" select="concat(substring($firstCase, 1, 3), ' - ', substring($lastCase, 1, 3))" />
			<xsl:with-param name="parent_uuid" select="$root_uuid" />
			<xsl:with-param name="doc_family_uuid" select="$uuid" />
		</xsl:call-template>		
	</xsl:template>
	
	<xsl:template match="x:pagebreak" mode="firstPreviousCase">
		<xsl:value-of select="preceding::x:tbl.row[1]/x:tbl.row.ref//x:t/text()" />
	</xsl:template>
</xsl:stylesheet>