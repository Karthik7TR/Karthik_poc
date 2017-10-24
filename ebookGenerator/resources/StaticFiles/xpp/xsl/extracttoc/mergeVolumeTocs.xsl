<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="../transform-utils.xsl" />
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />

	<xsl:template match="Document">
		<xsl:element name="Volume">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="x:EBook/x:EBookToc">
		<xsl:choose>
			<xsl:when test="./x:Name[not(contains(., $volNamePlaceholder))]">
				<xsl:variable name="guid" select="./x:Guid/text()" />
				<xsl:if
					test="count(preceding::x:EBook/x:EBookToc/x:Guid[text() = $guid]) = 0">
					<xsl:copy>
						<xsl:copy-of select="./x:Name" />
						<xsl:copy-of select="./x:Guid" />
						<xsl:copy-of select="./x:DocumentGuid" />
						<xsl:for-each select="../../x:EBook/x:EBookToc">
							<xsl:if test="self::node()/x:Guid/text() = $guid">
								<xsl:apply-templates select="x:EBookToc" />
							</xsl:if>
						</xsl:for-each>
					</xsl:copy>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates select="node()|@*" />
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>