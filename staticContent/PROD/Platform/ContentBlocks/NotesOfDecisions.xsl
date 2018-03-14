<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<!-- "Headnote.xsl" is included for its "topic.key.ref" template match -->
	<xsl:include href="Headnote.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="nod.block" name="renderNotesOfDecisionsStatutes">
		<xsl:choose>
			<xsl:when test="$IsPersisted = true()" />
			<xsl:when test="$DeliveryMode and $StatutoryTextOnly" />
			<xsl:when test="count(../nod.block//nod.note | ../nod.block//nod.ref) = 0" />
			<xsl:otherwise>
				<div class="&notesOfDecisionsClass; &disableHighlightFeaturesClass; &excludeFromAnnotationsClass;" id="&notesOfDecisionsId;">
					<xsl:choose>
						<xsl:when test="descendant::nod.body/nod.body//N-HIT or descendant::nod.body/nod.body//N-LOCATE or descendant::nod.body/nod.body//N-WITHIN">
							<xsl:apply-templates select="head"/>
							<div>
								<xsl:apply-templates select="nod.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN]" />
							</div>
						</xsl:when>
						<xsl:otherwise>
							<div class="&printHeadingClass;">
								<xsl:choose>
									<xsl:when test="$IsSearched = 'True'">
										<xsl:call-template name="DisplayNODWithZeroSearchResult" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name="DisplayNODHeading" />
									</xsl:otherwise>
								</xsl:choose>
								<div class="&clearClass;"></div>
							</div>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="nod.block/nod.body/nod.body">
		<xsl:apply-templates select="head"/>
		<div>
			<xsl:apply-templates select="nod.note[.//N-HIT or .//N-LOCATE or .//N-WITHIN]" />
		</div>
	</xsl:template>

	<xsl:template match="nod.block/nod.body">
		<xsl:apply-templates select="head"/>
		<div>
			<xsl:apply-templates select="nod.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN]" />
		</div>
	</xsl:template>

	<xsl:template match="nod.block/head/head.info/headtext" priority="2">
		<div class="&printHeadingClass;">
			<div class="&notesOfDecisionsHeadingClass;">
				<xsl:value-of select="concat('&notesOfDecisionsHeadingText;', ' (', count(ancestor::nod.block//nod.note[.//N-HIT or .//N-LOCATE or .//N-WITHIN]), ')')"/>
			</div>
			<xsl:call-template name="renderNotesOfDecisionsLink">
				<xsl:with-param name="count" select="count(ancestor::nod.block[1]/nod.body/nod.body/nod.note)" />
				<xsl:with-param name="docGuid" select="$Guid" />
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
			</xsl:call-template>
			<div class="&clearClass;"></div>
		</div>
		<div class="&notesOfDecisionsExplainationClass;">
			<xsl:text>&notesOfDecisionsExplaination;</xsl:text>
		</div>
	</xsl:template>

	<xsl:template name="DisplayNODHeading">
		<div class="&notesOfDecisionsHeadingClass;">
			<xsl:call-template name="renderNotesOfDecisionsLink">
				<xsl:with-param name="count" select="count(../nod.block//nod.note | ../nod.block//nod.ref)" />
				<xsl:with-param name="docGuid" select="$Guid" />
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
				<xsl:with-param name="text" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&notesOfDecisionsTextKey;', '&notesOfDecisionsText;')"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template name="DisplayNODWithZeroSearchResult">
		<div class="&notesOfDecisionsHeadingClass;">
      <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&notesOfDecisionsTextKey;', '&notesOfDecisionsText;')"/>
      <xsl:text>&#160;</xsl:text>
      <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&notesOfDecisionsZeroSearchTextKey;', '&notesOfDecisionsZeroSearchText;')"/>
      <xsl:text>&#160;</xsl:text>
      <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&notesOfDecisionsZeroSearchValueKey;', '&notesOfDecisionsZeroSearchValue;')"/>
		</div>
		<xsl:call-template name="renderNotesOfDecisionsLink">
			<xsl:with-param name="count" select="count(../nod.block//nod.note)" />
			<xsl:with-param name="docGuid" select="$Guid" />
			<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Suppress -->
	<xsl:template match="nod.block/analysis" />
	<xsl:template match="nod.body/analysis" />
	<xsl:template match="nod.body/head/head.info/label.designator" />

</xsl:stylesheet>
