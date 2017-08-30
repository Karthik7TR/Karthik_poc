<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
    xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

    <xsl:template match="x:stream">
        <xsl:for-each-group select="x:block" group-starting-with="x:block[@fipcblk = 'true']">
            <xsl:variable name="numberOfColumns" select="count(current-group())" />
            <xsl:choose>
                <xsl:when test="$numberOfColumns = 1">
                    <xsl:apply-templates select="."/>
                </xsl:when>
                <xsl:otherwise>
                    <columns/>
                    <xsl:for-each select="current-group()">
                        <column/>
                        <xsl:apply-templates select="."/>
                        <endcolumn/>
                    </xsl:for-each>
                    <endcolumns/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each-group>
    </xsl:template>

</xsl:stylesheet>