<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:import href="moveElementUpUtil.xsl" />
    <xsl:include href="footnotesCopyColumns.xsl" />
    
    <xsl:param name="fileType"/>
    <xsl:param name="footnotesFile"/>
    
    <xsl:variable name="footnotesDocument" select="document($footnotesFile)" />

    <xsl:template match="x:sectionbreak">
        <xsl:call-template name="insertCloseTags" />
        <xsl:copy-of select="."/>
        <xsl:call-template name="insertOpenTags" />
    </xsl:template>

    <xsl:template match="node()|@*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="#current" />
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>