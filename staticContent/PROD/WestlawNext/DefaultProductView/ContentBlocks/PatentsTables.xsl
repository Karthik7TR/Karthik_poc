<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	
	<xsl:include href="PatentsTablesCollapsibleRows.xsl"/>
	
	<xsl:template name="displayPatentDocument">
		<xsl:call-template name="displayPatentHeader" />
		<table class="&layout_table;">
			<xsl:call-template name="displayAbstract" />
			<xsl:call-template name="displayPatentInfo" />
			<xsl:call-template name="displayPatentBody" />
		</table>
	</xsl:template>
	
	<xsl:template name="displayPatentHeader">
		<table class="&layout_table; &layout_headerTable;">
			<tr>
				<td>
					<div>
						<h2>
							<xsl:value-of select="//patent.title" />
						</h2>
					</div>
					<div>
						<xsl:value-of select="//cmd.first.line.cite" />
					</div>
				</td>
				<td align="right" class="&noWrapClass;">
					<xsl:if test="//md.ip.image.link">
						<xsl:apply-templates select="//md.ip.image.link" />
					</xsl:if>
					<xsl:if test="//md.gateway.image.link">
						<xsl:apply-templates select="//md.gateway.image.link" />
					</xsl:if>
				</td>
			</tr>
		</table>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template name="displayPatentInfo">
		<tr class="&borderTopClass;">
				<td>
					<div class="&panelBlockClass;">
						<strong>
							<xsl:text>Patent</xsl:text>
						</strong>
					</div>
				</td>
				<td>
					<xsl:apply-templates select="//patent.info/* | //other.parties/*" />
				</td>
			</tr>
	</xsl:template>

	<xsl:template name="displayAbstract">
		<xsl:if test="//abstract/*">
			<tr class="&borderTopClass;">
					<td>
						<div>
							<strong>
								<xsl:value-of select="//abstract/head/headtext/bold/underscore" />
							</strong>
						</div>
					</td>
					<td>
						<xsl:apply-templates select="//abstract/*[not(name() = 'head')]" />
					</td>
				</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template name="displayPatentBody">
		<xsl:apply-templates select="//reissue.info" />
		<xsl:apply-templates select="//priority.info" />
		<xsl:apply-templates select="//class.info" />
		<xsl:apply-templates select="//claims.block" />
		<xsl:apply-templates select="//specification" />
		<xsl:apply-templates select="//reference.block" />
		<xsl:apply-templates select="node()[name() != reissue.info and name() != priority.info and name() != class.info and name() != claims.block and name() != specification and name() != reference.block and name() != abstract and name() != patent.info and name() != other.parties]" />
	</xsl:template>

	<xsl:template match="reissue.info[.//text()] | priority.info[.//text()] | reference.block[.//text()]" priority="1">
			<!-- Currently, claims.block and specification will not be caught here and will be displayed by the PatentsTablesCollapsibleRows stylesheet. -->
			<tr class="&borderTopClass;">
				<td>
					<xsl:variable name="nodeName" select="name(.)" />
					<xsl:variable name="nodeNameKey" select="translate(name(.), '&#46;', '')" />
					<xsl:variable name="defaultText" select="./head/headtext//text()" />
					<div class="&panelBlockClass;">
						<strong>
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
						</strong>
					</div>
				</td>
				<td>
					<xsl:apply-templates select="./*[not(name() = 'head')]" />
				</td>
			</tr>
	</xsl:template>

	<xsl:template match="class.info[.//text()]" priority="1">
			<tr class="&borderTopClass;">
				<td>
					<xsl:variable name="nodeName" select="name(.)" />
					<xsl:variable name="nodeNameKey" select="translate(name(.), '&#46;', '')" />
					<xsl:variable name="defaultText" select="./head/headtext//text()" />
					<div class="&panelBlockClass;">
						<strong>
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
						</strong>
					</div>
				</td>
				<td>
					<xsl:apply-templates select="./*[not(name() = 'head')]" />
					<xsl:apply-templates select="../drawings.info" />
					<xsl:apply-templates select="../source.lang" />
				</td>
			</tr>
	</xsl:template>

</xsl:stylesheet>