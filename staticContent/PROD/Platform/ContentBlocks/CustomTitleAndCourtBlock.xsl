<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="title.block[not(following-sibling::title.block)]" priority="1">
		<xsl:call-template name="titleBlock" />
		<xsl:apply-templates select="../court.block"  mode="customCourtAndTitle" />
		<xsl:apply-templates select="../docket.block" mode="customCourtAndTitle" />
		<xsl:apply-templates select="../date.block" mode="customCourtAndTitle" />
	</xsl:template>

	<xsl:template match="court.block" priority="1" />
	<xsl:template match="docket.block" priority="1" />
	<xsl:template match="date.block" priority="1" />

	<xsl:template match="court.block | docket.block" mode="customCourtAndTitle">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="date.block" mode="customCourtAndTitle">
		<xsl:call-template name="dateBlock" />
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>


	<!--
	!!!
	!!! Title Block overrides specific to Caselaw-NRS
	!!!
	-->

	<xsl:template match="primary.title" priority="1">
		<xsl:choose>
			<!-- Wrap the primary.title in a DIV only if there is a secondary.title sibling after it -->
			<xsl:when test="following-sibling::secondary.title">
				<div>
					<xsl:choose>
						<xsl:when test="party.line">
							<xsl:apply-templates select="party.line[substring(., string-length(.)) = '.'
															                    and not(following-sibling::node()[not(self::bos or self::eos or self::bop or self::eop)][1][self::versus or self::and])] 
															         | party.line[not(following-sibling::party.line)] | footnote" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates />
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="party.line">
						<xsl:apply-templates select="party.line[substring(., string-length(.)) = '.'
															                    and not(following-sibling::node()[not(self::bos or self::eos or self::bop or self::eop)][1][self::versus or self::and])] 
															         | party.line[not(following-sibling::party.line)] | footnote" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Match on party.line elements that end with a period and the last party.line element -->
	<xsl:template match="party.line[substring(., string-length(.)) = '.'
								                  and not(following-sibling::node()[not(self::bos or self::eos or self::bop or self::eop)][1][self::versus or self::and])]
								       | party.line[not(following-sibling::party.line)]" priority="1">
		<xsl:variable name="previousEndOfSuitPartyLineNode" select="preceding-sibling::party.line[substring(., string-length(.)) = '.'
									                                                                            and not(following-sibling::node()[not(self::bos or self::eos or self::bop or self::eop)][1][self::versus or self::and])][1]" />
		<div class="&suitClass;">
			<xsl:choose>
				<xsl:when test="$previousEndOfSuitPartyLineNode">
					<xsl:apply-templates select="preceding-sibling::node()[not(generate-id(.) = generate-id($previousEndOfSuitPartyLineNode)) and not(following-sibling::node()[generate-id(.) = generate-id($previousEndOfSuitPartyLineNode)])]" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="preceding-sibling::node()" />
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="normalPartyLine" />
		</div>
	</xsl:template>

	<xsl:template match="versus" priority="1">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="and" priority="1">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

</xsl:stylesheet>
