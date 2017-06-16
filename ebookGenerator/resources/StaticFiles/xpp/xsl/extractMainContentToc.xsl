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
	
	<xsl:template match="x:XPPHier[@parent_uuid = @uuid]">
		<xsl:variable name="uuid" select="./@uuid" />
		<xsl:call-template name="create-entry">
			<xsl:with-param name="name" select="./@name" />
			<xsl:with-param name="uuid" select="$uuid" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="x:XPPHier[@parent_uuid != @uuid]">
		<xsl:param name="parent" />
		
		<xsl:if test="./@parent_uuid = $parent">
			<xsl:variable name="uuid" select="./@uuid" />
			<xsl:call-template name="create-entry">
				<xsl:with-param name="name" select="./@name" />
				<xsl:with-param name="uuid" select="$uuid" />
			</xsl:call-template>
		</xsl:if>	
	</xsl:template>
	
	<xsl:template name="create-entry">
		<xsl:param name="name" />
		<xsl:param name="uuid" />
		<EBookToc>
			<Name>
				<xsl:value-of select="$name" />
			</Name>
			<Guid>
				<xsl:value-of select="$uuid" />
			</Guid>
			<xsl:choose>
				<xsl:when test="following::x:XPPHier[@parent_uuid = $uuid]">
					<xsl:apply-templates select="following::x:XPPHier[@parent_uuid != @uuid]">
						<xsl:with-param name="parent" select="$uuid" />
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="following::x:XPPMetaData[@parent_uuid = $uuid]">
					<DocumentGuid>
						<xsl:value-of select="following::x:XPPMetaData[@parent_uuid = $uuid][1]/@md.doc_family_uuid" />
					</DocumentGuid>
				</xsl:when>
				<xsl:otherwise>
					<DocumentGuid>
						<xsl:value-of select="preceding::x:XPPMetaData[1]/@md.doc_family_uuid" />
					</DocumentGuid>
				</xsl:otherwise>
			</xsl:choose>
		</EBookToc>
	</xsl:template>

	<xsl:template match="text()" />
</xsl:stylesheet>