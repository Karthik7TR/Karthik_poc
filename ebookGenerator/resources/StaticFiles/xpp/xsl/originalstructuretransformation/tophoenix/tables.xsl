<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
    xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

    <xsl:template match="x:table">
        <xsl:element name="table">
            <xsl:attribute name="cols" select="./x:tgroup/@cols" />
            <xsl:attribute name="tgroupstyle" select="./x:tgroup/@tgroupstyle" />
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="x:tgroup//x:row/x:entry">
        <xsl:element name="entry">
            <xsl:if test="@align">
                <xsl:attribute name="align" select="@align" />
            </xsl:if>
            <xsl:if test="@namest and @nameend">
                <xsl:attribute name="colspan" select="x:calculateColspan(., @namest, @nameend)" />
            </xsl:if>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:function name="x:calculateColspan">
        <xsl:param name="currentNode" as="node()"/>
        <xsl:param name="startColumnName"/>
        <xsl:param name="endColumnName"/>
        
        <xsl:variable name="tgroup" select="$currentNode/ancestor::x:tgroup[1]"/>
        <xsl:variable name="startColumn" select="$tgroup/x:colspec[@colname=$startColumnName]"/>
        <xsl:variable name="endColumn" select="$tgroup/x:colspec[@colname=$endColumnName]"/>
        
        <xsl:value-of select="$endColumn/@colnum - $startColumn/@colnum + 1" />
    </xsl:function>

    <xsl:template match="x:thead|x:tbody|x:tgroup//x:row">
        <xsl:copy>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

    <xsl:template
        match="x:tag[@name = 'table' or @name = 'tgroup' or @name = 'thead' or @name = 'tbody' or @name = 'row' or @name = 'entry']" />
    <xsl:template
        match="x:endtag[@name = 'table' or @name = 'tgroup' or @name = 'thead' or @name = 'tbody' or @name = 'row' or @name = 'entry']" />

</xsl:stylesheet>