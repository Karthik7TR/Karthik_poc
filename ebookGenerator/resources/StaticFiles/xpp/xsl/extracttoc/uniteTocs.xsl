<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:import href="../transform-utils.xsl" />
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
    <xsl:param name="depthThreshold" />
    <xsl:param name="isSplitted" />

    <xsl:template match="Document">
        <EBook>
            <xsl:choose>
                <xsl:when test="count(distinct-values(.//x:Volume/@volumeNum))=1 and not($isSplitted)">
                    <xsl:for-each select=".//x:Volume">
                        <xsl:call-template name="processVolumeWithPocketPart" />
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each-group select="x:Volume" group-by="@volumeNum">
                        <xsl:element name="EBookToc">
                            <xsl:call-template name="addtTocItemHead">
                                <xsl:with-param name="name" select="concat('Volume ', ./@volumeNum)"/>
                            </xsl:call-template>
                            <xsl:for-each select="current-group()">
                                <xsl:call-template name="processVolumeWithPocketPart" />
                            </xsl:for-each>
                        </xsl:element>
                    </xsl:for-each-group>
                </xsl:otherwise>
            </xsl:choose>
        </EBook>
    </xsl:template>

    <xsl:template name="processVolumeWithPocketPart">
        <xsl:choose>
            <xsl:when test="./@isPP = true()">
                <xsl:element name="EBookToc">
                    <xsl:call-template name="addtTocItemHead">
                        <xsl:with-param name="name" select="'Pocket Part'"/>
                    </xsl:call-template>
                    <xsl:apply-templates select="x:EBook" />
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="x:EBook" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="addtTocItemHead">
        <xsl:param name="name" />
        <xsl:element name="Name">
            <xsl:value-of select="$name" />
        </xsl:element>
        <xsl:element name="Guid">
            <xsl:value-of select="x:getGuid(.)" />
        </xsl:element>
        <xsl:element name="DocumentGuid">
            <xsl:value-of select="x:getDocumentGuid(.)" />
        </xsl:element>
    </xsl:template>

    <xsl:function name="x:getGuid">
        <xsl:param name="currentNode" as="node()" />
        <xsl:value-of select="$currentNode//x:EBookToc[./x:Name[contains(., $volNamePlaceholder)]]/x:Guid" />
    </xsl:function>

    <xsl:function name="x:getDocumentGuid">
        <xsl:param name="currentNode" as="node()" />
        <xsl:value-of select="($currentNode//x:EBookToc[./x:Name[not(contains(., $volNamePlaceholder))]])[1]/x:DocumentGuid" />
    </xsl:function>

    <xsl:template match="x:EBook">
        <xsl:apply-templates select="node()|@*" />
    </xsl:template>

    <xsl:template match="x:EBookToc">
        <xsl:variable name="depth" select="count(ancestor::x:EBookToc)" />
        <xsl:variable name="deepestVisibleDocUUID">
            <xsl:choose>
                <xsl:when test="$depth lt $depthThreshold">
                    <xsl:value-of select="''" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="ancestor::x:EBookToc[$depth - $depthThreshold + 1]/x:DocumentGuid/text()" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:if test=".[not(./x:Name[contains(., $volNamePlaceholder)])] and 
            ($deepestVisibleDocUUID != ./x:DocumentGuid/text() or
            descendant::x:EBookToc[./x:DocumentGuid != $deepestVisibleDocUUID])">
            <xsl:copy>
                <xsl:apply-templates select="node()|@*" />
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:if test=".[not(./x:Name[contains(., $volNamePlaceholder)])]">
            <xsl:copy>
                <xsl:apply-templates select="node()|@*" />
            </xsl:copy>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>