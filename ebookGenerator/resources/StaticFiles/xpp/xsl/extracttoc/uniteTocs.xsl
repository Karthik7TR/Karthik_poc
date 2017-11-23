<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:import href="../transform-utils.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:param name="depthThreshold" />

	<xsl:template match="Document">
		<EBook>
			<xsl:choose>
				<xsl:when test="count(x:Volume)>1">
					<xsl:for-each select="x:Volume">
						<xsl:element name="EBookToc">
							<xsl:element name="Name">
								<xsl:choose>
									<xsl:when test="./@isPP = true()">
										<xsl:value-of select="concat('Volume ', ./@volumeNum, ' - ', 'Pocket Part')" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="concat('Volume ', ./@volumeNum)" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:element>
							<xsl:element name="Guid">
								<xsl:value-of
									select=".//x:EBookToc[./x:Name[contains(., $volNamePlaceholder)]]/x:Guid" />
							</xsl:element>
							<xsl:element name="DocumentGuid">
								<xsl:value-of
									select="(.//x:EBookToc[./x:Name[not(contains(., $volNamePlaceholder))]])[1]/x:DocumentGuid" />
							</xsl:element>
							<xsl:apply-templates select="x:EBook" />
						</xsl:element>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="x:Volume/node()" />
				</xsl:otherwise>
			</xsl:choose>
		</EBook>
	</xsl:template>

	<xsl:template match="x:EBook">
		<xsl:apply-templates select="node()|@*" />
	</xsl:template>

	<xsl:template match="x:EBookToc">
		<xsl:variable name="depth" select="count(ancestor::x:EBookToc)" />
		<xsl:variable name="deepestVisibleDocUUID">
			<xsl:choose>
				<xsl:when test="$depth lt $depthThreshold">
					<xsl:value-of select="''" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="ancestor::x:EBookToc[$depth - $depthThreshold + 1]/x:DocumentGuid/text()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:if test=".[not(./x:Name[contains(., $volNamePlaceholder)])] and 
			($deepestVisibleDocUUID != ./x:DocumentGuid/text() or
			descendant::x:EBookToc[./x:DocumentGuid != $deepestVisibleDocUUID])">
			<xsl:copy>
				<xsl:apply-templates select="node()|@*" />
			</xsl:copy>
		</xsl:if>
	</xsl:template>

	<xsl:template match="node()|@*">
		<xsl:if test=".[not(./x:Name[contains(., $volNamePlaceholder)])]">
			<xsl:copy>
				<xsl:apply-templates select="node()|@*" />
			</xsl:copy>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>