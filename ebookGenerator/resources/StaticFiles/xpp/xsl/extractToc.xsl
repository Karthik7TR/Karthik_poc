<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
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
			<xsl:with-param name="name">
				<xsl:apply-templates select="x:sep|text()" mode="extract-name" />
			</xsl:with-param>
			<xsl:with-param name="uuid" select="$uuid" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="x:XPPHier[@parent_uuid != @uuid]">
		<xsl:param name="parent" />

		<xsl:if test="./@parent_uuid = $parent">
			<xsl:variable name="uuid" select="./@uuid" />
			<xsl:call-template name="create-entry">
				<xsl:with-param name="name">
					<xsl:apply-templates select="x:sep|text()" mode="extract-name" />
				</xsl:with-param>
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
			<DocumentGuid>
				<xsl:choose>
					<xsl:when test="@md.doc_family_uuid">
						<xsl:value-of select="@md.doc_family_uuid" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="following::x:XPPMetaData[@parent_uuid = $uuid]">
								<xsl:value-of
									select="following::x:XPPMetaData[@parent_uuid = $uuid][1]/@md.doc_family_uuid" />
							</xsl:when>
							<xsl:when test="preceding::x:XPPMetaData[1]/@md.doc_family_uuid">
								<xsl:value-of select="preceding::x:XPPMetaData[1]/@md.doc_family_uuid" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="following::x:XPPMetaData[1]/@md.doc_family_uuid" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</DocumentGuid>

			<xsl:if test="following::x:XPPHier[@parent_uuid = $uuid]">
				<xsl:apply-templates select="following::x:XPPHier[@parent_uuid != @uuid]">
					<xsl:with-param name="parent" select="$uuid" />
				</xsl:apply-templates>
			</xsl:if>
		</EBookToc>
	</xsl:template>

	<xsl:template match="x:sep" mode="extract-name">
		<xsl:value-of select="'—'" />
	</xsl:template>

	<xsl:template match="text()" mode="extract-name">
		<xsl:copy-of select="." />
	</xsl:template>

	<xsl:template match="text()" />
</xsl:stylesheet>