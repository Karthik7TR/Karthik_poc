﻿<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="ExpertQAndA.xsl"/>	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="expert.view.block">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="short.title" mode="heading"/>
			<xsl:apply-templates select="expert.block" mode="expertBlock"/>
			<xsl:apply-templates select="//md.cites"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:apply-templates select="*[not(short.title)]"/>
	</xsl:template>

	<xsl:template match="question.answer.block" priority="2">
		<xsl:apply-templates select="question.answer[not(@future-flag='Y' or @future-flag='y')]" />
		<xsl:call-template name="future-flag" />
	</xsl:template>

	<xsl:template match="question.text">
		<div class="&paratextMainClass;">
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>
	
	<xsl:template match="answer/para/paratext">
		<div class="&paratextMainClass;">
			<i>
				<xsl:apply-templates />
			</i>
		</div>
	</xsl:template>

	<xsl:template match="expert.block" mode="expertBlock">
		<xsl:apply-templates select="expert.name" mode="expert"/>
		<xsl:apply-templates select="area.of.expertise" mode="expert"/>
		<xsl:apply-templates select="location" mode="expert"/>
	</xsl:template>

	<xsl:template match="expert.name | area.of.expertise | location" mode="expert">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="expert.name | area.of.expertise | location" />
	
	<xsl:template match="question.answer[not(@future-flag='Y' or @future-flag='y')]" priority="2">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>
	
	<xsl:template name="future-flag">
		<xsl:variable name="answerWithFutureFlag" select="question.answer[@future-flag='Y' or @future-flag='y']" />
		<xsl:if test="$answerWithFutureFlag">
			<xsl:choose>
				<xsl:when test="count($answerWithFutureFlag)=1">
					<div class="&questionAnswerClass;">
						<strong>Question Supplied by the Expert</strong>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div class="&questionAnswerClass;">
						<strong>Questions Supplied by the Expert</strong>
					</div>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="$answerWithFutureFlag" />
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="cite.query[@w-ref-type = 'A1']" priority="1">
		<xsl:call-template name="GetOtherExpertsResponses" />
	</xsl:template>
	
</xsl:stylesheet>