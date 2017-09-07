<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"

	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

 	<xsl:param name="TOCPartName" select="TOCPartName" />
 	<xsl:param name="volumeName" select="volumeName" />
 	<xsl:variable name="front_matter_uuid" select="concat($volumeName,'.',replace($TOCPartName,'_','.'))" />
 	<xsl:variable name="human_readable_name">
 		<xsl:call-template name="defineTOCPartName">
			<xsl:with-param name="file_name" select="$TOCPartName" />
		</xsl:call-template>
 	</xsl:variable>
 	
 	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
 	
	<xsl:template match="x:root">
		<root>
			<xsl:element name="sectionbreak">
				<xsl:attribute name="sectionuuid" select="$front_matter_uuid" />
			</xsl:element>
			<xsl:element name="XPPHier">
				<xsl:attribute name="uuid" select="$front_matter_uuid" />
				<xsl:attribute name="name" select="$human_readable_name"/>
				<xsl:attribute name="parent_uuid" select="$front_matter_uuid" />
				<xsl:attribute name="md.doc_family_uuid" select="$front_matter_uuid" />
				<xsl:value-of select="$human_readable_name" />
			</xsl:element>
			<xsl:apply-templates />
		</root>
	</xsl:template>

	<xsl:template name="defineTOCPartName">
		<xsl:param name="file_name" />
		<xsl:choose>
			<xsl:when test="$file_name = 'SUMMARY_TABLE_OF_CONTENTS'">
				<xsl:value-of select="'Summary Table of Contents'" />
			</xsl:when>
			<xsl:when test="$file_name = 'DETAILED_TABLE_OF_CONTENTS'">
				<xsl:value-of select="'Detailed Table of Contents'" />
			</xsl:when>
			<xsl:when test="$file_name = 'IMPOSITION_LIST'">
				<xsl:value-of select="'Imposition List'" />
			</xsl:when>
			<xsl:when test="$file_name = 'CORRELATION_TABLE'">
				<xsl:value-of select="'Correlation Table'" />
			</xsl:when>
			<xsl:when test="$file_name = 'KEY_NUMBER_TABLE'">
				<xsl:value-of select="'Key Number Table'" />
			</xsl:when>
			<xsl:when test="$file_name = 'TABLE_OF_ADDED_KEY_NUMBERS'">
				<xsl:value-of select="'Table of Added Key Numbers'" />
			</xsl:when>
			<xsl:when test="$file_name = 'FILLING_INSTRUCTIONS'">
				<xsl:value-of select="'Filing Instructions'" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>