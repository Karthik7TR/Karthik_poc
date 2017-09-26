<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="../transform-utils.xsl" />
	<xsl:import href="pageNumbers.xsl" />
	<xsl:include href="footnotes.xsl" />
	<xsl:include href="xppMetadata.xsl" />
	<xsl:include href="tables.xsl" />
	<xsl:include href="columns.xsl" />
	<xsl:include href="other.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:param name="type" />
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
		<xsl:variable name="printNumber">
			<xsl:call-template name="print-page-numbers" />
		</xsl:variable>
		<xsl:element name="pagebreak">
			<xsl:attribute name="num">
				<xsl:call-template name="page-numbers">
					<xsl:with-param name="bundlePartType" select="$bundlePartType" />
				</xsl:call-template>
			</xsl:attribute>
			<xsl:attribute name="num-string" select="$printNumber" />
			<xsl:attribute name="prev_page">
				<xsl:apply-templates select="preceding::x:page[1]"
				mode="prev_page" />
			</xsl:attribute>
		</xsl:element>

		<!-- place page.number.ref if there is no references to footnotes in maiin stream -->
		<xsl:if test="$type = 'main' and not(./x:stream[@type='main']//x:xref) and $printNumber != '' and $printNumber != '0'">
			<xsl:element name="page.number.ref">
				<xsl:attribute name="page-number" select="$printNumber" />
			</xsl:element>
		</xsl:if>

		<xsl:apply-templates select="x:stream[@type=$type]" />
		
		<!-- if we work with footnotes place page.number at the end of page -->
		<xsl:if test="$type = 'footnote' and $printNumber != '' and $printNumber != '0'">
			<xsl:element name="page.number">
				<xsl:attribute name="page-num" select="$printNumber" />
			</xsl:element>
		</xsl:if>
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
		<xsl:value-of select="replace(x:get-fixed-text(.), '&quot;', '&amp;quot')" />
		<xsl:text disable-output-escaping="yes"><![CDATA["]]></xsl:text>
	</xsl:template>

	<xsl:template match="x:endtag">
		<xsl:text disable-output-escaping="yes"><![CDATA[</]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="x:t[not(@suppress='true')]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="x:t[not(@suppress='true')]/text()">
		<xsl:element name="t">
			<xsl:attribute name="style">
				<xsl:value-of select="../@style" />
				<xsl:if test="../@y!='0'">
					<xsl:value-of select="concat(' ', x:get-vertical-align(../@y))" />
				</xsl:if>
				<xsl:if test="../@cgt='true'">
					<xsl:value-of select="concat(' ', 'cgt')" />
				</xsl:if>
			</xsl:attribute>
			<xsl:value-of select="x:get-fixed-text(.)" />
		</xsl:element>
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
		<xsl:if test="./@type = 'footnote'">
			<xsl:variable name="lastPageId" select="(ancestor::x:stream//x:xref[@type = 'footnote']/@id)[last()]" />
			
			<!-- place page.number.ref after last footnote reference at current page -->
			<xsl:variable name="pageNumber">
				<xsl:apply-templates select="ancestor::x:page[1]" mode="pageRef" />
			</xsl:variable>
			<xsl:if test="./@id = $lastPageId and $pageNumber != '' and $pageNumber != '0'">
				<xsl:element name="page.number.ref">
					<xsl:attribute name="page-number" select="$pageNumber" />
				</xsl:element>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="x:page" mode="pageRef">
		<xsl:call-template name="print-page-numbers" />
	</xsl:template>

	<xsl:template match="text()" />
</xsl:stylesheet>