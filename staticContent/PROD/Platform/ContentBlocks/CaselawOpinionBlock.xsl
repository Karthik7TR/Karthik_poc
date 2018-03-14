<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="get-non-precedential-case-of-Pennsylvania-flag">
		<xsl:variable name="nonPrecedentialDecision">NON–PRECEDENTIAL DECISION</xsl:variable>
		<xsl:variable name="nonPrecedentialDecisionShortDash">NON-PRECEDENTIAL DECISION</xsl:variable>

		<xsl:variable name="editorialNoteText">
			<xsl:call-template name="upper-case">
				<xsl:with-param name="string" select="/Document/n-docbody/decision/content.block/editorial.note.block/editorial.note.body/editorial.note/text.line" />
			</xsl:call-template>
		</xsl:variable>

		<!--Returns 'true' if:
			1) the editorial note isn't empty
			2) the document is of Pennsylvania state ONLY
			3) editorial's text starts with 'NON-PRECEDENTIAL DECISION' text.
			-->
		<xsl:value-of select="boolean(string-length($editorialNoteText) &gt; 0 and $JurisdictionNumber = '3460' and (starts-with($editorialNoteText, $nonPrecedentialDecision) or starts-with($editorialNoteText, $nonPrecedentialDecisionShortDash)))" />
	</xsl:template>
	
	<xsl:variable name="IsNonPrecedentialCaseOfPennsylvania">
		<xsl:call-template name="get-non-precedential-case-of-Pennsylvania-flag"/>
	</xsl:variable>
	
	<xsl:template match="opinion.block" priority="1">
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="contents">
				<xsl:choose>
					<xsl:when test=". = ../opinion.block[1]">
						<xsl:choose>
							<xsl:when test="opinion.block.body/opinion.lead/opinion.body/section[1]/head or $IsNonPrecedentialCaseOfPennsylvania = 'true'">
								<h2 id="&opinionId;"/>
							</xsl:when>
							<xsl:otherwise>
								<h2 id="&opinionId;" class="&printHeadingClass;">
									<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&opinionLabelKey;', '&opinionLabel;')"/>
								</h2>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="opinion.block.body/opinion.concurrance">
								<h2 id="&concurranceOpinionID;"/>
							</xsl:when>
							<xsl:when test="opinion.block.body/opinion.dissent">
								<h2 id="&dissentOpinionID;"/>
							</xsl:when>
						</xsl:choose>
						<xsl:choose>
							<xsl:when test="opinion.block.body/opinion.cipdip">
								<h2 id="&cipdipOpinionID;"/>
							</xsl:when>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
				<div class="&deliveryDottedLineClass;">
					<img src="{$Images}&docDeliveryDottedLine;" alt="" />
				</div>
				<xsl:apply-templates/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>