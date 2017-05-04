<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />
    
    <xsl:param name="mapFilePath" />
    <xsl:param name="map" select="document($mapFilePath)"/>

    <xsl:template match="/">
        <EBook>
            <xsl:apply-templates />
        </EBook>
    </xsl:template>
    
    <xsl:template match="titlebreak">
        <xsl:copy-of select="self::node()" />
    </xsl:template>
    
    <xsl:template match="x:publication.body/x:topic/x:front/x:outline.name.block[@toc.include='y']">
        <xsl:variable name="uuid" select="../../@uuid" />
        <EBookToc>
            <Name><xsl:value-of select="x:name/text()"/></Name>
            <Guid><xsl:value-of select="$uuid"/></Guid>
            <DocumentGuid><xsl:value-of select="x:get-value($uuid)"/></DocumentGuid>
            <xsl:apply-templates select="../../x:topic.body/x:chapter/x:front/x:outline.name.block[@toc.include='y']" mode="toc" />
        </EBookToc>
    </xsl:template>
    
    <xsl:template match="x:publication.body/x:topic/x:topic.body/x:chapter/x:front/x:outline.name.block[@toc.include='y']" mode="toc" >
        <xsl:variable name="uuid" select="../../@uuid" />
        <EBookToc>
            <Name><xsl:value-of select="concat(x:label/text(), ' ', x:designator/text(), ' ', string-join(x:name/text(), ' '))"/></Name>
            <Guid><xsl:value-of select="$uuid"/></Guid>
            <DocumentGuid><xsl:value-of select="x:get-value($uuid)"/></DocumentGuid>
            <xsl:apply-templates select="../x:generated.toc/x:full.toc/x:toc.analytical.level" mode="toc" />
        </EBookToc>
    </xsl:template>

    <xsl:template match="x:toc.analytical.level" mode="toc" >
        <EBookToc>
            <Name><xsl:value-of select="concat(x:outline.name.block/x:designator/text(), ' ', string-join(x:outline.name.block/x:name/text(), ' '))"/></Name>
            <Guid><xsl:value-of select="@tocuuid"/></Guid>
            <DocumentGuid><xsl:value-of select="x:get-value(@tocuuid)"/></DocumentGuid>
            
            <xsl:choose>
                <xsl:when test="./x:toc.analytical.level">
                    <xsl:apply-templates select="x:toc.analytical.level" mode="toc" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:if test="./x:toc.section">
                        <xsl:apply-templates select="x:toc.section" mode="toc" />
                    </xsl:if>
                </xsl:otherwise>
            </xsl:choose>
        </EBookToc>
    </xsl:template>
    
    <xsl:template match="x:toc.section" mode="toc" >
        <xsl:variable name="designator" select="x:outline.name.block/x:designator/text()" />
        <EBookToc>
            <Name><xsl:value-of select="concat($designator, ' ', string-join(x:outline.name.block/x:name/text(), ' '))"/></Name>
            <Guid><xsl:value-of select="x:get-value($designator)"/></Guid>
            <DocumentGuid><xsl:value-of select="x:get-value($designator)"/></DocumentGuid>
        </EBookToc>
    </xsl:template>
    
    <xsl:function name="x:get-value" >
        <xsl:param name="value" />
        <xsl:value-of select="$map//entry[@key=$value]" />
    </xsl:function>
    
    <xsl:template match="text()" mode="#all" />

</xsl:stylesheet>