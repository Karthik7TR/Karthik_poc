<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="placeXppMarks.xsl" />

    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
    
	<xsl:param name="materialNumber" />
	<xsl:param name="TOCPartName" />

	<xsl:variable name="root_uuid">
		<xsl:choose>
			<xsl:when test="$materialNumber">
				<xsl:value-of select="concat($materialNumber, '.', replace($TOCPartName, '_', ''))"  />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'table_of_cases'"  />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="x:root">
		<root>
			<xsl:variable name="first_item" select="concat($root_uuid, '1')" />
				
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid" select="$first_item" />
			</xsl:call-template>

			<xsl:call-template name="placeXppHier">
				<xsl:with-param name="uuid" select="$root_uuid" />
				<xsl:with-param name="name" select="'Table of Cases'" />
				<xsl:with-param name="parent_uuid" select="$root_uuid" />
				<xsl:with-param name="doc_family_uuid" select="$first_item" />
			</xsl:call-template>

			<xsl:apply-templates />
		</root>
	</xsl:template>
	
	<xsl:template match="x:pagebreak[@serial-num = 1 or (@serial-num - 1) mod 20 = 0]">
		<xsl:variable name="uuid" select="concat($root_uuid, @serial-num)" />
		<xsl:variable name="firstCase" select="string-join(x:get-tbl-row(self::node())/x:tbl.row.ref//x:t[not(@style='dt')]/text(), '')" />
		<xsl:variable name="lastCase">
			<xsl:variable name="multipleCases" >
				<xsl:choose>
					<xsl:when test="following::x:pagebreak[(@serial-num - 1) mod 20 = 0][1]">
						<xsl:apply-templates select="following::x:pagebreak[(@serial-num - 1)mod 20 = 0][1]" mode="firstPreviousCase" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="following::x:tbl.unit[last()]/x:tbl.row[1]/x:tbl.row.ref//x:t[not(@style='dt')]/text()" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:value-of select="string-join($multipleCases, '')" />
		</xsl:variable>
	
		<xsl:if test="@serial-num != 1">
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid" select="$uuid" />
			</xsl:call-template>
		</xsl:if>
		
		<xsl:copy-of select="." />

		<xsl:call-template name="placeXppHier">
			<xsl:with-param name="uuid" select="$uuid" />
			<xsl:with-param name="name" select="concat(x:substring-before($firstCase, ','), ' - ', x:substring-before($lastCase, ','))" />
			<xsl:with-param name="parent_uuid" select="$root_uuid" />
			<xsl:with-param name="doc_family_uuid" select="$uuid" />
		</xsl:call-template>		
	</xsl:template>
	
	<xsl:template match="x:pagebreak" mode="firstPreviousCase">
		<xsl:value-of select="x:get-tbl-row(self::node())/x:tbl.row.ref//x:t[not(@style='dt')]/text()" />
	</xsl:template>
	
	<xsl:function name="x:substring-before">
		<xsl:param name="source" />
		<xsl:param name="delimiter" />
		<xsl:choose>
			<xsl:when test="contains($source, $delimiter)">
				<xsl:value-of select="substring-before($source, $delimiter)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$source" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="x:get-tbl-row">
		<xsl:param name="currentPagebreak" as="node()"/>
		<xsl:variable name="_ancestor" select="$currentPagebreak/ancestor::x:tbl.row[not(descendant::x:repeat.party)]" />
		<xsl:choose>
			<xsl:when test="$_ancestor">
				<xsl:sequence select="$_ancestor"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$currentPagebreak/following::x:tbl.row[1]//x:repeat.party">
						<xsl:sequence select="$currentPagebreak/preceding::x:tbl.row[not(descendant::x:repeat.party)][1]" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:sequence select="$currentPagebreak/following::x:tbl.row[not(descendant::x:repeat.party)][1]" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>