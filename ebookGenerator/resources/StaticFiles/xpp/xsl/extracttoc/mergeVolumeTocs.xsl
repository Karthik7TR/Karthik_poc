<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="../transform-utils.xsl" />
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />

	<xsl:param name="volumeNum" />
	<xsl:param name="isPocketPart" />

	<xsl:template match="Document">
		<xsl:variable name="doc" select="." />
		<xsl:for-each select="distinct-values((.//x:Volume/text()))">
			<xsl:variable name="volNum" select="." />
			<xsl:if test="$doc//x:EBookToc[not(x:EBookToc)][1]/x:Volume/text() = $volNum">
				<xsl:element name="Volume">
					<xsl:attribute name="volumeNum" select="$volNum" />
					<xsl:attribute name="isPP" select="$isPocketPart" />
					<EBook>
						<xsl:apply-templates select="$doc/x:EBook/x:EBookToc[x:Volume/text() = $volNum or .//x:EBookToc[not(x:EBookToc)][1]/x:Volume/text() = $volNum]" />
					</EBook>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="x:Volume" />

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
						<xsl:element name="Name">
							<xsl:choose>
								<xsl:when test="$isPocketPart and ./x:Name/text() != 'Copyright Page'">
									<xsl:value-of select="concat('Pocket Part', ' ', ./x:Name/text())" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="./x:Name/text()" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:element>
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