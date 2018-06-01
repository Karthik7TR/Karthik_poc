<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />
    
    <xsl:param name="titleMetadataDoc" />
    <xsl:param name="titleMetadata" select="document($titleMetadataDoc)" />

	<xsl:template match="Document">
		<xsl:element name="title">
			<xsl:attribute name="apiversion" select="$titleMetadata/ManifestMetadata/apiVersion/text()" /> 
			<xsl:attribute name="titleversion" select="$titleMetadata/ManifestMetadata/titleVersion/text()" />
			<xsl:attribute name="id" select="$titleMetadata/ManifestMetadata/titleId/text()" />
			<xsl:attribute name="lastupdated" select="$titleMetadata/ManifestMetadata/lastUpdated/text()" />
			<xsl:attribute name="language" select="$titleMetadata/ManifestMetadata/language/text()" />
			<xsl:attribute name="status" select="$titleMetadata/ManifestMetadata/status/text()" />
			<xsl:attribute name="onlineexpiration" select="$titleMetadata/ManifestMetadata/onlineexpiration/text()" />
			
			<xsl:copy-of select="$titleMetadata/ManifestMetadata/features" />
			<xsl:copy-of select="$titleMetadata/ManifestMetadata/material" />
			<xsl:copy-of select="$titleMetadata/ManifestMetadata/artwork" />
			<xsl:copy-of select="$titleMetadata/ManifestMetadata/assets" />
			<xsl:copy-of select="$titleMetadata/ManifestMetadata/name" />
			<xsl:copy-of select="$titleMetadata/ManifestMetadata/authors" />
			<xsl:copy-of select="$titleMetadata/ManifestMetadata/keywords" />
			<xsl:copy-of select="$titleMetadata/ManifestMetadata/copyright" />
			
			<xsl:element name="toc">
				<xsl:apply-templates select="x:EBook" />
			</xsl:element>
			
			<xsl:copy-of select="$titleMetadata/ManifestMetadata/docs" />
			
			<xsl:copy-of select="$titleMetadata/ManifestMetadata/isbn" />
		</xsl:element>
	</xsl:template>
    
	<xsl:template match="x:EBook">
		<xsl:if test="./@titleBreak">
			<xsl:element name="titlebreak">
				<xsl:value-of select="./@titleBreak" />
			</xsl:element>
		</xsl:if>
		<xsl:apply-templates select="x:EBookToc" mode="toc">
			<xsl:with-param name="uuidPrefix" select="./@uuidPrefix" />
		</xsl:apply-templates>		
	</xsl:template>
	
	<xsl:template match="x:EBookToc" mode="toc">
		<xsl:param name="uuidPrefix" />
		<xsl:element name="entry">
			<xsl:variable name="docGuid">
				<xsl:value-of select="./x:DocumentGuid"/>
			</xsl:variable>
			<xsl:attribute name="s" select="concat($uuidPrefix, $docGuid, '/', x:Guid)" />
			<xsl:element name="text">
				<xsl:value-of select="replace(x:Name/text(), '&amp;([^#])', '&amp;amp;$1')" disable-output-escaping="yes" />
			</xsl:element>
			<xsl:apply-templates select="x:EBookToc" mode="toc">
				<xsl:with-param name="uuidPrefix" select="$uuidPrefix" />
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="x:EBookToc" mode="finding_doc_guid">
		<xsl:choose>
			<xsl:when test="./x:EBookToc">
				<xsl:apply-templates select="child::x:EBookToc[1]" mode="finding_doc_guid" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="x:DocumentGuid" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>