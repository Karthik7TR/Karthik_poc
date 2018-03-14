<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="row/entry/para | row/entry/para/paratext" priority="1">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="entry" mode="appendixToc">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="entry//leaderchar|entry//charfill"/>

	<xsl:template match="appendix[tbl][1]//tbody/row/entry" priority="1">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<xsl:variable name="textNodes">
			<xsl:apply-templates select="preceding-sibling::entry" mode="appendixToc"/>
			<xsl:if test="./following-sibling::entry">
				<xsl:apply-templates/>
			</xsl:if>
			<xsl:apply-templates select="following-sibling::entry[following-sibling::entry]" mode="appendixToc"/>
		</xsl:variable>
		<td>
			<xsl:call-template name="RenderTableCell">
				<xsl:with-param name="columnInfo" select="$columnInfo"/>
				<xsl:with-param name="colalign" select="$colalign" />
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="colwidth" select="$colwidth" />
				<xsl:with-param name="contents">
					<xsl:variable name="idRef" >
						<xsl:variable name="headtextId" select="generate-id(/Document/n-docbody/doc/introduction.section/section/appendix[preceding::appendix]/head/headtext[contains(translate(., '&alphabetUppercase; .', '&alphabetLowercase;'), translate($textNodes, '&alphabetUppercase; .', '&alphabetLowercase;'))][1])"/>
						<xsl:choose>
							<xsl:when test="string-length($headtextId) &gt; 0">
								<xsl:value-of select="$headtextId"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="generate-id(/Document/n-docbody/doc/introduction.section/section/appendix[preceding::appendix]/para/paratext[contains(translate(., '&alphabetUppercase; .', '&alphabetLowercase;'), translate($textNodes, '&alphabetUppercase; .', '&alphabetLowercase;'))][1])"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<!-- Apply templates inside a variable so that we don't get links with no text. These links
							 with no text cause delivered word documents to display the link's href as text. Bug 260273 -->
					<xsl:variable name="contents">
						<xsl:apply-templates/>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="string-length($idRef) &gt; 0 and string-length($contents) &gt; 0">
							<a class="&internalLinkClass;" href="#co_g_{$idRef}">
								<xsl:apply-templates/>
							</a>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</td>
	</xsl:template>

	<!-- END TABLE CODE -->
	<xsl:template match="appendix" priority="1">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="appendix/para/paratext" priority="1">
		<xsl:call-template name="renderParagraphTextDiv">
			<xsl:with-param name="divId">
				<xsl:value-of select="concat('co_g_', generate-id(.))"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="appendix/head/headtext" priority="1">
		<xsl:call-template name="renderHeadTextDiv">
			<xsl:with-param name="divId">
				<xsl:value-of select="concat('co_g_', generate-id(.))"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
