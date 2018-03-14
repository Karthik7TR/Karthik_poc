<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="WrappingUtilities.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:template match="table.cases">
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="contents">
				<xsl:if test="/Document/n-metadata/metadata.block/md.publications/md.publication/md.pubname='White Book'">
					<div>
						<xsl:text>&whiteBookText;</xsl:text>
					</div>
				</xsl:if>
				<xsl:apply-templates/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.item">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="case.entry/text()"/>

	<xsl:template match="case.entry/case.name">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&indentTopClass; &boldClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.entry/emph[following-sibling::ref.group]">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>
	
	<xsl:template match="case.entry/ref.group[following-sibling::ref.group]">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[; ]]></xsl:text>
	</xsl:template>

	<xsl:template match="case.entry/ref.group/node()[self::caseref or self::court or self::effect]">
		<xsl:call-template name="wrapWithSpan"/>
		<xsl:if test="following-sibling::node()[self::caseref or self::court or self::effect]">
			<xsl:text><![CDATA[; ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="case.item/reference.targets">
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="contents">
				<xsl:apply-templates mode="case-reference.targets"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cite.query" mode="case-reference.targets">
		<xsl:apply-templates select="."/>
		<xsl:if test="following-sibling::cite.query">
			<xsl:text><![CDATA[, ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="reference.title" mode="case-reference.targets">
		<strong>
			<xsl:apply-templates/>
			<xsl:if test="following-sibling::cite.query">
				<xsl:text><![CDATA[: ]]></xsl:text>
			</xsl:if>
		</strong>
	</xsl:template>

</xsl:stylesheet>