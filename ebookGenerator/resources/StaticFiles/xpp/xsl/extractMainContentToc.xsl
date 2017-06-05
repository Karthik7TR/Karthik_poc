<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />
    
	<xsl:template match="/">
		<EBook>
			<xsl:apply-templates />
		</EBook>
	</xsl:template>
	
	<xsl:template match="x:document.hier[@parent_guid = @guid]">
		<xsl:variable name="guid" select="./@guid" />
		<xsl:call-template name="create-entry">
			<xsl:with-param name="name" select="./@name" />
			<xsl:with-param name="guid" select="$guid" />
			<xsl:with-param name="document" select="$guid" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="x:document.hier[@parent_guid != @guid]">
		<xsl:param name="parent" />
		
		<xsl:if test="./@parent_guid = $parent">
			<xsl:variable name="guid" select="./@guid" />
			<xsl:call-template name="create-entry">
				<xsl:with-param name="name" select="./@name" />
				<xsl:with-param name="guid" select="$guid" />
				<xsl:with-param name="document" select="$guid" />
			</xsl:call-template>
		</xsl:if>	
	</xsl:template>
	
	<xsl:template name="create-entry">
		<xsl:param name="name" />
		<xsl:param name="guid" />
		<xsl:param name="document" />
		<EBookToc>
			<Name>
				<xsl:value-of select="$name" />
			</Name>
			<Guid>
				<xsl:value-of select="$guid" />
			</Guid>
			<DocumentGuid>
				<xsl:value-of select="$document" />
			</DocumentGuid>
			<xsl:apply-templates select="following::x:document.hier[@parent_guid != @guid]">
				<xsl:with-param name="parent" select="$guid" />
			</xsl:apply-templates>
		</EBookToc>
	</xsl:template>

	<xsl:template match="text()" />
</xsl:stylesheet>