<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="LinkedToc.xsl"/>
	<xsl:include href="Suppressions.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div>
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			
			<xsl:variable name="uuid" select="n-metadata/metadata.block/md.identifiers/md.uuid" />
			<input type="hidden" id="&documentGuid;" value="{$uuid}" alt="&documentGuid;" />
			
			<xsl:apply-templates/>
			<xsl:if test="not($IsRuleBookMode)">
				<xsl:variable name="IsLastChild">
					<xsl:choose>
						<xsl:when test="parent::documents and not(following-sibling::Document)">
							<xsl:value-of select="true()" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="false()" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:if test="$IsLastChild = 'true'">
					<xsl:call-template name="EndOfDocument" />
				</xsl:if>
			</xsl:if>
			<xsl:if test="$IsRuleBookMode">
				<xsl:apply-templates select="/" mode="Custom"/>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="n-metadata | popular.name.doc.title | hide.historical.version"/>

	<xsl:template name="AddProductDocumentClasses">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:text>&documentFixedHeaderView;</xsl:text>
	</xsl:template>

	<xsl:template name="email-link" match="paratext/web.address">
		<xsl:variable name="emailid">
			<xsl:value-of select="node()"/>
		</xsl:variable>
		<a>
			<xsl:attribute name="class">
				<xsl:text>&linkClass;</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="href">
				<xsl:value-of select="concat('mailto:', $emailid)"/>
			</xsl:attribute>
			<xsl:copy-of select="$emailid"/>
		</a>
	</xsl:template>

	<xsl:template match="include.currency.block" priority="2">
		<xsl:if test="not($IsRuleBookMode)">
			<xsl:if test="/Document/document-data/datetime &lt; /Document/n-metadata/metadata.block/md.dates/md.endeffective">
				<xsl:call-template name="wrapContentBlockWithCobaltClass">
					<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;',currency.id/@ID)"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!--Code included in order to fix the Bug 499904-->
	<xsl:template match="placeholder.text" />

	<!--Code included in order to fix the Bug 319747-->
	<xsl:template match="n-docbody/refs.annos/grade.notes/ed.note.grade">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="subsection.hovertext">
		<!--<xsl:variable name="originalText" select="'26 CFR Â§â€‚1.0-1(b)'"></xsl:variable>-->
		<xsl:variable name="originalText" select="@text"></xsl:variable>
		<span class="&hoverText;" title="{DocumentExtension:ToXmlEncodedString($originalText)}">
			<xsl:apply-templates />
		</span>

		<!--Setup internal anchors for the hidden TOC-->
		<xsl:if test="$IsRuleBookMode">
			<xsl:variable name="hashValue">
				<xsl:value-of select="ancestor::subsection[1]/@ID"/>
			</xsl:variable>

			<xsl:if test="$hashValue">
				<a id="&internalLinkIdPrefix;{$hashValue}"></a>
			</xsl:if>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
