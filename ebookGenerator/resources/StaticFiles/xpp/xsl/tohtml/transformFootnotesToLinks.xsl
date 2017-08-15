<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">

    <xsl:template match="x:xref[@type='footnote']" />
    
    <xsl:template match="x:part.footnotes">
        <section class="tr_footnotes">
            <xsl:apply-templates />
        </section>
    </xsl:template>
    
    <xsl:template match="x:footnote">
        <div class="tr_footnote">
            <div class="footnote">
                <xsl:apply-templates />
            </div>
        </div>
    </xsl:template>
    
    <xsl:template match="x:foots">
        <xsl:copy>
            <xsl:copy-of select="./@id" />
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="x:footnote.reference">
        <xsl:variable name="refId" select="ancestor::x:footnote[1]/@id" />
        <xsl:element name="sup">
            <xsl:element name="a">
                <xsl:attribute name="class" select="'tr_ftn'" />
                <xsl:attribute name="href" select="'javascript:void(0)'" />
                <xsl:attribute name="name" select="concat('ftn.', $refId)" />
                <xsl:attribute name="ftnname" select="concat('f', $refId)" />
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="x:footnote.reference/x:cgt">
        <sup><xsl:apply-templates /></sup>
    </xsl:template>
    
    <xsl:function name="x:footnote-reference-id">
        <xsl:param name="node" as="node()"/>
        
        <xsl:variable name="cgtSuper" select="contains($node/@style, 'cgt') and contains($node/@style, 'super')"/>
        <xsl:if test="$cgtSuper" >
            <xsl:value-of select="$node/following-sibling::*[1]/self::x:xref/@id"/>
        </xsl:if>
    </xsl:function>
    
    <xsl:template name="addFootnoteReferenceTag">
        <xsl:param name="refId" />
        
        <xsl:element name="sup">
            <xsl:attribute name="class">
                <xsl:value-of select="x:get-class-name(name(.))" />
                <xsl:if test="@style">
                    <xsl:value-of select="concat(' font_', replace(x:get-class-name(@style), 'super', ''))" />
                </xsl:if>
            </xsl:attribute>

            <xsl:variable name="ftnname" select="concat('ftn.', $refId)"/>
            <xsl:element name="a">
                <xsl:attribute name="ftnname" select="$ftnname" />
                <xsl:attribute name="name" select="concat('f', $refId)" />
                <xsl:attribute name="href" select="concat('#', $ftnname)" />
                <xsl:attribute name="class" select="'tr_ftn footnote_access_point'" />
                <xsl:apply-templates />
            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>