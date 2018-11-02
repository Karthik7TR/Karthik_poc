<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://www.w3.org/1999/xhtml"
                xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="h">
    <xsl:output method="text" indent="no" omit-xml-declaration="yes"/>
    <xsl:param name="streamType"/>
    <xsl:param name="entitiesDocType"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="h:head"/>

    <xsl:template match="h:body">
        <xsl:choose>
            <xsl:when test="$streamType='main'">
                <xsl:apply-templates select="h:section[not(@class)]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="h:section[@class='tr_footnotes']"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="h:div[@class='footnote_body_main_popup_pane' or ends-with(@class, 'show_in_main') or contains(@class, 'scroll_mode')] | h:sup[ends-with(@class, 'show_in_main')]"/>

	<xsl:template match="h:div[contains(@class, 'footnoteHidden')]" />

    <xsl:template match="h:span[not(contains(@class, 't font_dt'))] | h:sup">
        <xsl:value-of select=".//text()"/>
    </xsl:template>

    <xsl:template match="text()"/>

</xsl:stylesheet>
