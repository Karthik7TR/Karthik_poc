<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:param name="fileBaseName" />
	<xsl:param name="fileType" />

	<xsl:template match="x:root">
		<xsl:for-each-group select="*" group-starting-with="x:pagebreak">
			<xsl:variable name="fileNameSuffix" select="concat($fileBaseName, '_', position())" />
			<xsl:variable name="fileName"
				select="concat('./', $fileNameSuffix, '_', $fileType, '.part')" />
			<xsl:result-document href="{$fileName}">
				<xsl:element name="{concat('part.', $fileType)}">
					<xsl:attribute name="fileName" >
						<xsl:value-of select="$fileNameSuffix" />
					</xsl:attribute>
					<xsl:apply-templates select="current-group()" />
				</xsl:element>
			</xsl:result-document>
		</xsl:for-each-group>
	</xsl:template>

	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:pagebreak" />
</xsl:stylesheet>