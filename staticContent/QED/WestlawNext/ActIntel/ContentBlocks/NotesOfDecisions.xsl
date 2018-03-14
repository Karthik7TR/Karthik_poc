<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="NotesOfDecisions.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Render Notes of Decisions bar as a button -->
	<xsl:template name="renderNotesOfDecisionsButton">
		<xsl:param name="count" /> <!-- Number of notes of decisions -->
		<xsl:param name="docGuid" /> <!-- The GUID of a document-->
		<xsl:param name="originationContext" /> <!-- Origination context -->
		
		<div class="&notesOfDecisionsHeadingClass;">
			<xsl:call-template name="renderNotesOfDecisionsLink">
				<xsl:with-param name="count" select="$count" />
				<xsl:with-param name="docGuid" select="$docGuid" />
				<xsl:with-param name="originationContext" select="$originationContext"/>
				<xsl:with-param name="text" select="'&notesOfDecisionsButtonCaption;'"/>
			</xsl:call-template>
		</div>
		<div class="&clearClass;"></div>
	</xsl:template>
	
	<!-- Relevant notes of decisions are found -->
	<xsl:template match="nod.block/head/head.info/headtext" priority="2">
		<div class="&printHeadingClass;">
			<xsl:call-template name="renderNotesOfDecisionsButton">
				<xsl:with-param name="count" select="count(ancestor::nod.block[1]/nod.body/nod.body/nod.note)" />
				<xsl:with-param name="docGuid" select="$Guid" />
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
			</xsl:call-template>
		</div>

		<div class="&notesOfDecisionsExplainationClass;">
			<xsl:text>&notesOfDecisionsExplaination;</xsl:text>
		</div>
	</xsl:template>
	
	<!-- No search, therefore no relevant notes of decisions -->
	<xsl:template name="DisplayNODHeading">
		<xsl:call-template name="renderNotesOfDecisionsButton">
			<xsl:with-param name="count" select="count(../nod.block//nod.note | ../nod.block//nod.ref)" />
			<xsl:with-param name="docGuid" select="$Guid" />
			<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- No relevant notes of decisions are found -->
	<xsl:template name="DisplayNODWithZeroSearchResult">
		<xsl:call-template name="renderNotesOfDecisionsButton">
			<xsl:with-param name="count" select="count(../nod.block//nod.note)" />
			<xsl:with-param name="docGuid" select="$Guid" />
			<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
