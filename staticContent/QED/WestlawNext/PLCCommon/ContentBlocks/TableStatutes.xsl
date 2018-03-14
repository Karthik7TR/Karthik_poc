<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CustomFunctions.xsl" />
	<xsl:include href="WrappingUtilities.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:template match="table.statutes">
		<div>
			<xsl:attribute name="class">
				<xsl:variable name="xmlBasedClassName">
					<xsl:call-template name="escape-to-class">
						<xsl:with-param name="prefix">
							<xsl:call-template name="getEscapeToClassPrefix"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<xsl:value-of select="concat('&fullscreenTableClass; ', $xmlBasedClassName)"/>
			</xsl:attribute>
			<xsl:if test="/Document/n-metadata/metadata.block/md.publications/md.publication/md.pubname='White Book'">
				<div>
					<xsl:text>&whiteBookText;</xsl:text>
				</div>
			</xsl:if>
			<table class="&borderedTableClass;">
				<tr>
					<th>
						<xsl:text>&tableStatutesLegislation;</xsl:text>
					</th>
					<th>
						<xsl:text>&tableStatutesParagraphs;</xsl:text>
					</th>
				</tr>
				<xsl:apply-templates/>
			</table>
		</div>
	</xsl:template>

	<xsl:template match="stat.name">
		<tr>
			<td class="&boldClass;">
				<xsl:apply-templates />
			</td>
			<td>
				<xsl:call-template name="render-sibling-reference.targets"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="stat.section">
		<xsl:param name="level" select="0"/>
		<xsl:apply-templates>
			<xsl:with-param name="level" select="$level + 1"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="stat.section/legis.cite | stat.section/grey.material.cite">
		<xsl:param name="level"/>
		<xsl:variable name="nodeName" select="name()"/>
		<xsl:if test="not(preceding-sibling::*[1][name() = $nodeName])">
			<tr>
				<td class="&boldClass;">
					<xsl:call-template name="wrapWithDiv">
						<xsl:with-param name="class" select="concat('&paraIndentLeftClass;', $level)"/>
						<xsl:with-param name="contents">
							<xsl:apply-templates/>
							<xsl:call-template name="render-sibling-nodes-or-text">
								<xsl:with-param name="nodeName" select="$nodeName"/>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</td>
				<td>
					<xsl:call-template name="render-sibling-reference.targets">
						<xsl:with-param name="nodeName" select="$nodeName"/>
					</xsl:call-template>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template name="render-sibling-nodes-or-text">
		<xsl:param name="nodeName" />
		<xsl:for-each select="following-sibling::node()[1][name() = $nodeName or self::text()]">
			<xsl:choose>
				<xsl:when test="name() = $nodeName">
					<xsl:apply-templates/>
				</xsl:when>
				<xsl:when test="self::text()">
					<xsl:value-of select="."/>
				</xsl:when>
			</xsl:choose>
			<xsl:call-template name="render-sibling-nodes-or-text">
				<xsl:with-param name="nodeName" select="$nodeName"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="render-sibling-reference.targets">
		<xsl:param name="nodeName" />
		<xsl:for-each select="following-sibling::node()[1][name() = $nodeName or self::text() or self::reference.targets]">
			<xsl:choose>
				<xsl:when test="name() = $nodeName or self::text()">
					<xsl:call-template name="render-sibling-reference.targets">
						<xsl:with-param name="nodeName" select="$nodeName"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates mode="reference.targets"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="stat.section/text()[preceding-sibling::*[1][self::legis.cite or self::grey.material.cite]]"/>

	<xsl:template match="stat.section/reference.targets[preceding-sibling::*[1][self::legis.cite or self::grey.material.cite]] | stat.item/reference.targets[preceding-sibling::*[1][self::stat.name]]"/>

	<xsl:template match="cite.query" mode="reference.targets">
		<xsl:apply-templates select="."/>
		<xsl:if test="following-sibling::cite.query">
			<xsl:text><![CDATA[, ]]></xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
