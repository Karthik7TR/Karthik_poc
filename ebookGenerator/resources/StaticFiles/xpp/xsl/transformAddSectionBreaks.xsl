<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="x:XPPHier">
        <xsl:variable name="closestFollowingMetaElement" select="following::*[name() = 'XPPMetaData' or name() = 'XPPHier'][1]" />
        <xsl:if test="$closestFollowingMetaElement/name() = 'XPPMetaData' and ($closestFollowingMetaElement/@parent_uuid = @uuid or $closestFollowingMetaElement/@parent_uuid = @guid)" >
            <xsl:call-template name="addSectionbreak" >
                <xsl:with-param name="sectionuuid" select="$closestFollowingMetaElement/@md.doc_family_uuid" />
            </xsl:call-template>
        </xsl:if>
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="x:XPPMetaData">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
        <xsl:variable name="closestPrecedingMetaElement" select="preceding::*[name() = 'XPPMetaData' or name() = 'XPPHier'][1]" />
        
        <xsl:if test="($closestPrecedingMetaElement/name() = 'XPPMetaData') or ($closestPrecedingMetaElement/name() = 'XPPHier' and not($closestPrecedingMetaElement/@uuid = @parent_uuid) and not($closestPrecedingMetaElement/@guid = @parent_uuid))" >
            <xsl:call-template name="addSectionbreak" >
                <xsl:with-param name="sectionuuid" select="@md.doc_family_uuid" />
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template name="addSectionbreak">
        <xsl:param name="sectionuuid" />
        <xsl:element name="sectionbreak">
            <xsl:attribute name="sectionuuid" select="$sectionuuid" />
        </xsl:element>
	</xsl:template>

</xsl:stylesheet>