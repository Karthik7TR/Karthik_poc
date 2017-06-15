<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">

    <xsl:template match="x:pagebreak">
      <xsl:if test="not(preceding-sibling::*[1]/name() = 'sectionbreak') and not(following-sibling::*[1]/name() = 'sectionbreak')">
          <xsl:copy-of select="."/>
      </xsl:if>
    </xsl:template>
    
	<xsl:template match="x:sectionbreak">
        <xsl:call-template name="insertCloseTags" />
        <xsl:copy-of select="."/>
        <xsl:call-template name="insertPagebreak" />
        <xsl:call-template name="insertOpenTags" />
	</xsl:template>
    
    <xsl:template name="insertCloseTags">
        <xsl:for-each select="ancestor::node()">
			<xsl:sort select="position()" data-type="number" order="descending" />
			<xsl:if test="local-name(.) != '' and local-name(.) != 'root'">
				<xsl:text disable-output-escaping="yes"><![CDATA[</]]></xsl:text>
				<xsl:value-of select="local-name(.)" />
				<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
			</xsl:if>
		</xsl:for-each>
    </xsl:template>
    
    <xsl:template name="insertPagebreak">
        <xsl:variable name="firstPrecedingSibling" select="preceding-sibling::*[1]" />
        <xsl:variable name="firstFollowingSibling" select="following-sibling::*[1]" />
        <xsl:choose>
            <xsl:when test="$firstPrecedingSibling/name() = 'pagebreak'">
                <xsl:copy-of select="$firstPrecedingSibling"/>
            </xsl:when>
            <xsl:when test="$firstFollowingSibling/name() = 'pagebreak'">
                <xsl:copy-of select="$firstFollowingSibling"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="insertContinuationPagebreak" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="insertContinuationPagebreak">
        <xsl:element name="pagebreak">
            <xsl:attribute name="num" select="preceding::x:pagebreak[1]/@num" />
            <xsl:attribute name="continuation" select="true()" />
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="insertOpenTags">
        <xsl:for-each select="ancestor::node()">
			<xsl:if test="local-name(.) != '' and local-name(.) != 'root'">
				<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
				<xsl:value-of select="local-name(.)" />
				<xsl:for-each select="./@*">
					<xsl:text> </xsl:text>
					<xsl:value-of select="local-name(.)" />
					<xsl:text disable-output-escaping="yes"><![CDATA[="]]></xsl:text>
					<xsl:value-of select="." />
					<xsl:text disable-output-escaping="yes"><![CDATA["]]></xsl:text>
				</xsl:for-each>
				<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
			</xsl:if>
		</xsl:for-each>
    </xsl:template>

	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>