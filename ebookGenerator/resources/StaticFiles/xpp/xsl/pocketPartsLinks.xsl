<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
    <xsl:param name="isPocketPart" />
    <xsl:param name="webBuildProductType" />
    <xsl:param name="docId" />

    <xsl:template match="x:section.front[1]">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
        <xsl:choose>
            <xsl:when test="$isPocketPart">
                <xsl:element name="referenceFromPocketPartToMain">
                    <xsl:attribute name="docId" select="$docId" />
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="referenceFromMainToPocketPart">
                    <xsl:attribute name="docId" select="$docId" />
                    <xsl:attribute name="webBuildProductType" select="$webBuildProductType" />
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>