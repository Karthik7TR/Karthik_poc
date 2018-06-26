<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
                xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="text" indent="no" omit-xml-declaration="yes"/>
    <xsl:param name="streamType"/>
    <xsl:param name="entitiesDocType"/>

    <xsl:template match="x:document">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="x:stream[@type='frills']//x:tag[@name='custserv']">
        <xsl:if test="$streamType='main'">
            <xsl:value-of select="following-sibling::x:t[1]/text()"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="x:stream[@type=$streamType]//x:t[@suppress]"/>

    <xsl:template match="x:stream[@type=$streamType]//x:t[not(@suppress)]">
        <xsl:value-of select="text()"/>
    </xsl:template>

    <xsl:template match="text()"/>

</xsl:stylesheet>
