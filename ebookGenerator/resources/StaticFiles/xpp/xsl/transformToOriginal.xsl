<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="transform-utils.xsl" />
	<xsl:import href="PageNumbers.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:param name="entitiesDocType" />
	<xsl:param name="bundlePartType" />

	<xsl:template match="x:document">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE root SYSTEM &#34;</xsl:text>
		<xsl:value-of select="$entitiesDocType" />
		<xsl:text disable-output-escaping="yes">&#34;&gt;</xsl:text>
		<root>
			<xsl:apply-templates />
		</root>
	</xsl:template>

	<xsl:template match="x:page">
		<xsl:element name="pagebreak">
			<xsl:attribute name="num">
				<xsl:call-template name="page-numbers">
					<xsl:with-param name="bundlePartType" select="$bundlePartType" />
				</xsl:call-template>
			</xsl:attribute>
			<xsl:attribute name="num-string">
				<xsl:call-template name="print-page-numbers" />
			</xsl:attribute>
		</xsl:element>
		<xsl:apply-templates select="x:stream[@type='main']" />
	</xsl:template>

	<xsl:template match="x:tag">
		<xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:apply-templates select="x:attr" />

		<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
	</xsl:template>

	<xsl:template match="x:attr">
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text disable-output-escaping="yes"><![CDATA[="]]></xsl:text>
		<xsl:value-of select="x:get-fixed-text(.)" />
		<xsl:text disable-output-escaping="yes"><![CDATA["]]></xsl:text>
	</xsl:template>

	<xsl:template match="x:endtag">
		<xsl:text disable-output-escaping="yes"><![CDATA[</]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="x:t[not(@suppress='true')]">
		<xsl:variable name="text"
			select="x:get-fixed-text(string-join(text(), ''))" />
		<xsl:copy>
			<xsl:attribute name="style">
				<xsl:value-of select="@style" />
				<xsl:if test="@y!='0'">
					<xsl:value-of select="concat(' ', x:get-vertical-align(@y))" />
				</xsl:if>
				<xsl:if test="@cgt='true'">
					<xsl:value-of select="concat(' ', 'cgt')" />
				</xsl:if>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="./processing-instruction('XPPLink')">
					<xsl:apply-templates select="processing-instruction('XPPLink') | processing-instruction('XPPTOCLink')" />
					<xsl:apply-templates mode="link-text"/>
					<xsl:apply-templates select="processing-instruction('XPPEndLink') | processing-instruction('XPPTOCEndLink')" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$text" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:copy>
		<xsl:apply-templates select="*[not(processing-instruction('XPPLink') | processing-instruction('XPPTOCLink') | processing-instruction('XPPEndLink') | processing-instruction('XPPTOCEndLink'))]" />
	</xsl:template>
	
	<xsl:template match="text()" mode="link-text">
		<xsl:value-of select="x:get-fixed-text(string-join(., ''))" />
	</xsl:template>
	
	<xsl:template match="processing-instruction('XPPLink') | processing-instruction('XPPTOCLink')">
		<xsl:value-of select="concat('&lt;', name(), ' ')" disable-output-escaping="yes"/>
        <xsl:value-of select="."/>
        <xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
	</xsl:template>
	
	<xsl:template match="processing-instruction('XPPEndLink') | processing-instruction('XPPTOCEndLink')">
		<xsl:value-of select="concat('&lt;', '/', replace(name(), 'End', ''), '&gt;')" disable-output-escaping="yes"/>
	</xsl:template>
		
	<xsl:template match="x:dt">
		<xsl:if test="../parent::x:line[not(following-sibling::x:line)]">
			<xsl:element name="t" inherit-namespaces="yes">
				<xsl:attribute name="style">
					<xsl:text>dt</xsl:text>
				</xsl:attribute>
				<xsl:value-of select="." />
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template match="x:xref">
		<xsl:copy-of select="self::node()" />
	</xsl:template>

	<xsl:template match="processing-instruction('XPPMetaData') | processing-instruction('XPPHier')">
        <xsl:variable name="apostrophe">'</xsl:variable>
       	<xsl:variable name="tail" select="substring-after(., ' name=')" />
       	<xsl:variable name="name" select="replace(replace($tail, '\s[A-Za-z._]{1,}=.*', ''), $apostrophe, '')" />
                
        <xsl:value-of select="concat('&lt;', name(), ' ')" disable-output-escaping="yes"/>
        <xsl:value-of select="."/>
        <xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
        <xsl:value-of select="$name" disable-output-escaping="yes"/>
        <xsl:value-of select="concat('&lt;/', name(), '&gt;')" disable-output-escaping="yes"/>
    </xsl:template>

	<xsl:template match="text()" />
</xsl:stylesheet>