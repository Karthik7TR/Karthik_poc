<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	
	<xsl:import href="placeXppMarks.xsl" />
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	
	<xsl:param name="materialNumber" select="materialNumber" />
	
	<xsl:template match="x:generated.toc/x:full.toc[1]/x:toc.rw.hierarchy[1]">
		
		<xsl:variable name="uuid" select="concat($materialNumber,'.',name())" />
		
		<xsl:call-template name="placeXppHier">
			<xsl:with-param name="uuid" select="$uuid" />
			<xsl:with-param name="name" select="'Table Of Contents'" />
			<xsl:with-param name="parent_uuid" select="preceding::x:XPPHier[1]/@uuid" />
		</xsl:call-template>
				
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
   
</xsl:stylesheet>