<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
    xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">

    <xsl:template match="x:table">
        <xsl:element name="table">
            <xsl:attribute name="cols" select="./x:tgroup/@cols" />
            <xsl:attribute name="tgroupstyle" select="./x:tgroup/@tgroupstyle" />
            <xsl:attribute name="frame" select="@frame" />
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:function name="x:number">
        <xsl:param name="numberString" />
        <xsl:value-of select="number(substring-before($numberString, 'i'))" />
    </xsl:function>

	<xsl:template match="x:tgroup">
		<xsl:variable name="summaryColumnsWidth" select="sum(./x:colspec/x:number(@colwidth))" />
		<xsl:element name="colgroup">
			<xsl:for-each select="./x:colspec">
				<xsl:element name="col">
					<xsl:attribute name="width" select="concat(format-number(100 * x:number(@colwidth) div $summaryColumnsWidth, '##'), '%')" />
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

		<xsl:apply-templates />
	</xsl:template>
    
    <xsl:template match="x:tgroup//x:row/x:entry">
        <xsl:variable name="lindent" select=".//x:line[1]/@lindent" />
        <xsl:element name="entry">
            <xsl:if test="@align">
                <xsl:attribute name="align" select="@align" />
            </xsl:if>
            <xsl:if test="@namest and @nameend">
                <xsl:attribute name="colspan" select="x:calculateColspan(., @namest, @nameend)" />
            </xsl:if>
            <xsl:if test="@morerows">
                <xsl:attribute name="rowspan" select="@morerows+1" />
            </xsl:if>
            <xsl:if test="$lindent and not($lindent='0')">
                <!-- TODO: change it to numeric value instead of boolean 
                when table with multiple different row left indentations is found -->
                <xsl:attribute name="lindent" select="'true'" />
            </xsl:if>
            <xsl:if test="@valign">
            	<xsl:attribute name="valign" select="@valign" />
            </xsl:if>
            <xsl:if test="@rowsep='1'">
                <xsl:attribute name="rowsep" select="@rowsep" />
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

    <xsl:template match="x:thead[.//x:t[1] and count(.//x:t[not(@cgt)])=0]//x:t[following::*[1][name() = 'xref' and @type='footnote']]"/>
    <xsl:template match="x:thead[.//x:t[1] and count(.//x:t[not(@cgt)])=0]//x:xref[@type='footnote']"/>

    <xsl:template
        match="x:tag[@name = 'table' or @name = 'tgroup' or @name = 'thead' or @name = 'tbody' or @name = 'row' or @name = 'entry']" />
    <xsl:template
        match="x:endtag[@name = 'table' or @name = 'tgroup' or @name = 'thead' or @name = 'tbody' or @name = 'row' or @name = 'entry']" />
</xsl:stylesheet>
