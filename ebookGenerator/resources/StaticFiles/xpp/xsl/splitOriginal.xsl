<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:param name="fileBaseName" />

	<xsl:template match="x:root">
		<xsl:for-each-group select="*" group-starting-with="x:pagebreak">
			<xsl:variable name="fileName"
				select="concat('./', $fileBaseName,'_', position(), '.part')" />
			<xsl:result-document href="{$fileName}">
				<page>
					<xsl:apply-templates select="current-group()" />
				</page>
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