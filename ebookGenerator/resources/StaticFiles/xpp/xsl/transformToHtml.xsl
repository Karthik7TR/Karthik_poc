<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="transform-utils.xsl" />
    <xsl:output method="html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:param name="fileBaseName" />

	<xsl:template match="x:parts">
		<html>
			<head>
      			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
      			<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"></meta>
      			<title>Thomson Reuters eBook</title>
      			<link rel="stylesheet" type="text/css" href="er:#document"></link>
			</head>
			<xsl:element name="body">
            	<xsl:attribute name="fileBaseName" select="$fileBaseName" />
				<xsl:apply-templates />
            </xsl:element>
		</html>
	</xsl:template>

	<xsl:template match="x:part.main">
		<section>
			<xsl:apply-templates />
		</section>
	</xsl:template>

	<xsl:template match="x:pagebreak">
		<xsl:variable name="apostrophe">'</xsl:variable>
		<xsl:variable name="continuation">
			<xsl:if test="@continuation = true()">
				<xsl:value-of select="' (cont.)'" />
			</xsl:if>
		</xsl:variable>
        <xsl:variable name="continuationIndex">
            <xsl:if test="@continuationIndex and  not(@continuationIndex = 0)">
				<xsl:value-of select="concat(' ', @continuationIndex)" />
			</xsl:if>
		</xsl:variable>
		<xsl:processing-instruction name="pb" select="concat('label', '=', $apostrophe, ./@num, $continuation, $continuationIndex, $apostrophe, '?')" />
	</xsl:template>

	<xsl:template match="x:XPPHier">
		<xsl:element name="a">
			<xsl:attribute name="name" select="./@uuid" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="element()">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:value-of select="x:get-class-name(name(.))" />
					<xsl:if test=".[@align]">
						<xsl:value-of select="concat(' ', x:get-class-name(@align))" />
					</xsl:if>
					<xsl:if test="matches(@style, '.*[0-9]')">
						<xsl:value-of select="concat(' ', x:get-class-name(@style))" />
					</xsl:if>
			</xsl:attribute>
			<xsl:copy-of select="./@uuid | ./@tocuuid" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:part.footnotes|x:foots|x:footnote">
		<xsl:copy>
			<xsl:copy-of select="./@id" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:ref|x:xref|x:footnote.reference">
		<xsl:copy-of select="self::node()" />
	</xsl:template>

	<xsl:template match="x:ital|x:bold|x:cgt">
		<xsl:element name="span">
			<xsl:attribute name="class" select="x:get-class-name(name(.))" />
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="x:image.block">
		<xsl:param name="quote">"</xsl:param>
		<xsl:param name="ident">ident="</xsl:param>
		<xsl:variable name='guid' select='substring-before(substring-after(self::node(),$ident),".")'/>
		<xsl:element name="img">
			<xsl:attribute name="class" select="'tr_image'" />
			<xsl:attribute name="assetid" select="concat('er:#', $guid)" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="." />
	</xsl:template>
</xsl:stylesheet>