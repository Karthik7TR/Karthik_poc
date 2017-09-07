<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns="http://www.sdl.com/xpp" xmlns:x="http://www.sdl.com/xpp" 
                exclude-result-prefixes="x">
                      
	<xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template name="placeSectionbreak">
    	<xsl:param name="sectionuuid" />
    	<xsl:element name="sectionbreak">
			<xsl:attribute name="sectionuuid" select="$sectionuuid" />
		</xsl:element>
    </xsl:template>
    
    <xsl:template name="placeXppHier">
    	<xsl:param name="uuid" />
    	<xsl:param name="name" />
    	<xsl:param name="parent_uuid" />
    	<xsl:param name="doc_family_uuid" />
    	
    	<xsl:element name="XPPHier">
			<xsl:attribute name="uuid" select="$uuid" />
			<xsl:attribute name="name" select="$name" />
			<xsl:attribute name="parent_uuid" select="$parent_uuid" />
			<xsl:attribute name="md.doc_family_uuid" select="$doc_family_uuid" />
			<xsl:value-of select="$name" />
		</xsl:element>
    </xsl:template>
</xsl:stylesheet>