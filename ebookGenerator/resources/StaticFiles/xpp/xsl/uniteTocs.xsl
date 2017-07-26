<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<EBook>
			<xsl:for-each select="x:Volume">
				<xsl:element name="EBookToc">
					<xsl:element name="Name">
						<xsl:value-of select="concat('Volume ', position())" />
					</xsl:element>
					<xsl:element name="Guid">
						<xsl:value-of select="concat('vol', position())" />
					</xsl:element>
					<xsl:element name="DocumentGuid">
						<xsl:value-of select="(.//*[name() = 'DocumentGuid'])[1]" />
					</xsl:element>
					<xsl:apply-templates select="x:EBook" />
				</xsl:element>
			</xsl:for-each>
		</EBook>
	</xsl:template>

	<xsl:template match="x:EBook">
		<xsl:apply-templates select="node()|@*" />
	</xsl:template>

	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>