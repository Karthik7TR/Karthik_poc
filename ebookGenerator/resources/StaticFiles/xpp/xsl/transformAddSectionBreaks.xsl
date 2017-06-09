<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

    <xsl:template match="node()|@*">
        <xsl:param name="firstSameLevelUuid" />
        
        <xsl:variable name="hasXppHierAsChild" select="self::* and (child::*/name()='XPPHier')" />
        
        <xsl:if test="$hasXppHierAsChild">
            <xsl:variable name="xppHier" select="x:XPPHier[1]" />
            <xsl:variable name="xppHierUuid" select="$xppHier/@uuid" />
            <xsl:variable name="isRoot" select="$xppHierUuid = $xppHier/@parent_uuid" />
            
            <xsl:if test="not($xppHierUuid = $firstSameLevelUuid) or $isRoot">
                <xsl:call-template name="addSectionbreak" >
                    <xsl:with-param name="sectionuuid" select="$xppHierUuid" />
                </xsl:call-template>
            </xsl:if>
        </xsl:if>

        <xsl:copy>
            <xsl:apply-templates select="node()|@*" >
                <xsl:with-param name="firstSameLevelUuid">
                    <xsl:choose>
                        <xsl:when test="$hasXppHierAsChild">
                            <xsl:value-of select="((.//x:XPPHier)[2])/@uuid" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$firstSameLevelUuid" />
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="addSectionbreak">
        <xsl:param name="sectionuuid" />
        <xsl:element name="sectionbreak">
            <xsl:attribute name="sectionuuid" select="$sectionuuid" />
        </xsl:element>
	</xsl:template>

</xsl:stylesheet>