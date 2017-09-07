<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns="http://www.sdl.com/xpp" xmlns:x="http://www.sdl.com/xpp" 
                exclude-result-prefixes="x">
    <xsl:import href="placeXppMarks.xsl" />
    
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
                      
    <xsl:param name="volumeName" />
                      	
	<xsl:template match="x:root">
		<root>			
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid" select="concat($volumeName, '.', 'summary.toc')" />
			</xsl:call-template>
			<xsl:apply-templates />
		</root>	
	</xsl:template>
	
	<xsl:template match="x:summary.toc">
		<xsl:variable name="uuid" select="concat($volumeName, '.', 'summary.toc')" />
	
		<xsl:call-template name="placeXppHier">
			<xsl:with-param name="uuid" select="$uuid" />
			<xsl:with-param name="name" select="'Summary Table of Contents'" />
			<xsl:with-param name="parent_uuid" select="$uuid" />
			<xsl:with-param name="doc_family_uuid" select="$uuid"/>
		</xsl:call-template>
		<xsl:copy-of select="." />
	</xsl:template>
	
	<xsl:template match="x:full.toc">
		<xsl:variable name="uuid" select="concat($volumeName, '.', 'full.toc')" />
	
		<xsl:call-template name="placeSectionbreak">
			<xsl:with-param name="sectionuuid" select="$uuid" />
		</xsl:call-template>

		<xsl:call-template name="placeXppHier">
			<xsl:with-param name="uuid" select="$uuid" />
			<xsl:with-param name="name" select="'Detailed Table of Contents'" />
			<xsl:with-param name="parent_uuid" select="$uuid" />
			<xsl:with-param name="doc_family_uuid" select="$uuid"/>
		</xsl:call-template>
		<xsl:copy-of select="." />
	</xsl:template>
</xsl:stylesheet>