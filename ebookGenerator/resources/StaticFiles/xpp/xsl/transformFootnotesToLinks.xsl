<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="transform-utils.xsl" />
	<xsl:output method="html" indent="yes" omit-xml-declaration="yes"/>
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="x:xref[@type = 'footnote']">
		<xsl:element name="sup">
			<xsl:element name="a">
				<xsl:attribute name="class" select="concat('tr_ftn', ' ', 'footnote_access_point')" />
				<xsl:attribute name="name" select="concat('f', @id)" />
				<xsl:attribute name="href" select="'javascript:void(0)'" />
				<xsl:attribute name="ftnname" select="concat('ftn.', @id)" />
				<xsl:variable name="fn.ref" select="@id" />
				<xsl:value-of select="//x:footnote[@id = $fn.ref]/x:footnote.reference/node()" />
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="x:foots">
		<xsl:element name="p" />
		<xsl:element name="hr">
			<xsl:attribute name="class" select="'hr'" />
		</xsl:element>
		<xsl:element name="section">
			<xsl:attribute name="id" select="'er_rp_footnotes'" />
			<xsl:attribute name="class" select="'tr_footnotes'" />
			<!-- TODO: add erdocid attr -->
			<!-- TODO: add ereid attr -->
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="x:footnote">
		<xsl:element name="div">
			<xsl:attribute name="class" select="'tr_footnote'" />
			<xsl:element name="div"> 
				<xsl:attribute name="class" select="x:get-class-name(name(.))" />
				<xsl:apply-templates />
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="x:footnote.reference">
		<xsl:element name="sup">
			<xsl:element name="a">
				<xsl:attribute name="class" select="'tr_ftn'" />
				<xsl:attribute name="href" select="'javascript:void(0)'" />
				<xsl:variable name="fn.id" select="ancestor::x:footnote/@id" />
				<xsl:attribute name="name" select="concat('ftn.', $fn.id)" />
				<xsl:attribute name="ftnname" select="concat('f', $fn.id)" />
				<xsl:element name="sup">
					<xsl:value-of select="./node()" />
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>