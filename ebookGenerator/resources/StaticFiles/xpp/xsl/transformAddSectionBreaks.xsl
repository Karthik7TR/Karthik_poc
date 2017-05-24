<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

    <xsl:template match="node()|@*">
        <xsl:param name="rootVisited" select="false()" />
        <xsl:param name="firstSameLevelUuid" />
        
        <xsl:variable name="isNode" select="self::* and @uuid" />
        <xsl:variable name="isRoot" select="$isNode and not($rootVisited)" />

        <xsl:if test="($isNode and not(@uuid = $firstSameLevelUuid)) or $isRoot">
            <xsl:call-template name="addSectionbreak" />
        </xsl:if>

        <xsl:copy>
            <xsl:apply-templates select="node()|@*" >
                <xsl:with-param name="rootVisited" select="$isRoot or $rootVisited" />
                <xsl:with-param name="firstSameLevelUuid">
                    <xsl:call-template name="getFirstLowerLevelUuid" />
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="getFirstLowerLevelUuid">
        <xsl:if test="self::*">
            <xsl:value-of select="(child::*/@uuid)[1]" />
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="addSectionbreak">
        <xsl:element name="sectionbreak">
            <xsl:attribute name="sectionuuid" >
                <xsl:value-of select="@uuid" />
            </xsl:attribute>
        </xsl:element>
	</xsl:template>

</xsl:stylesheet>