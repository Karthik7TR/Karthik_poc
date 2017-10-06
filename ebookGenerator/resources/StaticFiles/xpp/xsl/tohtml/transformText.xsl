<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:template name="convertToSpan">
		<xsl:variable name="pagebreakSibling" select="preceding-sibling::x:pagebreak[1]" />
		<xsl:if
			test="$pagebreakSibling and count($pagebreakSibling/following-sibling::x:t intersect preceding-sibling::x:t)=0">
			<xsl:element name="div">
				<xsl:attribute name="class" select="'no-indent'" />
			</xsl:element>
		</xsl:if>
		<xsl:element name="span">
			<xsl:attribute name="class">
                <xsl:value-of select="x:get-class-name(name(.))" />
                <xsl:if test="@style">
                    <xsl:value-of
				select="concat(' font_', x:get-class-name(@style))" />
                    <xsl:if
				test="@style='dt' and count(following::x:pagebreak[1]/preceding::x:t intersect following::x:t)=0">
                        <xsl:value-of select="concat(' ', 'last_dt')" />
                    </xsl:if>
                </xsl:if>
            </xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="." />
	</xsl:template>
</xsl:stylesheet>