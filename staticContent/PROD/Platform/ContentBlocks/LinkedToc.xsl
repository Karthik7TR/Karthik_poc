<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Prelim.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="node()[prelim.block or content.metadata.block or doc.title][1]" name="renderCodeStatuteHeader">
		<xsl:variable name="displayCurrentness">
			<xsl:choose>
				<xsl:when test="(/Document/document-data/datetime &gt; /Document/n-metadata/metadata.block/md.dates/md.endeffective)">
					<xsl:value-of select="false()"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="true()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="IsRegChangeDocument">
			<xsl:choose>
				<xsl:when test="/Document/n-metadata[n-view = 'CHG']">
					<xsl:value-of select="true()" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="false()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="prelim.block or content.metadata.block or doc.title">
			<div class="&documentHeadClass;">
				<xsl:apply-templates select="prelim.block" mode="statueHeader"/>
				<xsl:choose>
					<xsl:when test="$displayCurrentness = 'false'">
						<xsl:call-template name="currentStatute"/>
					</xsl:when>
					<xsl:when test="$IsRegChangeDocument = 'true'">
						<xsl:call-template name="regChange"/>
					</xsl:when>
				</xsl:choose>
				<xsl:call-template name="DisplayEffectiveDates" />
				<xsl:apply-templates select="content.metadata.block" mode="statueHeader"/>
				<!-- Fix to get USCA Tables content to display added " | prelim.block/doc.title" -->
				<xsl:apply-templates select="doc.title | prelim.block/doc.title" mode="statueHeader"/>
				<xsl:if test="$displayCurrentness = 'true'">
					<xsl:call-template name="renderCurrentnessLink"/>
				</xsl:if>
			</div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>

		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id">
				<xsl:if test="@ID|@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="content.metadata.block" priority="1" mode="statueHeader">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="doc.title" mode="statueHeader">
		<xsl:call-template name="titleBlock"/>
	</xsl:template>

	<xsl:template name="currentStatute">
		<div class="&centerClass; &versionUpdateClass;">
			<xsl:text>&currentStatuteStart;</xsl:text>
			<xsl:call-template name="createCiteQueryLinkByParameters">
				<xsl:with-param name="linkContents" select="'here'"/>
				<xsl:with-param name="findType" select="'L'" />
				<xsl:with-param name="pubNum" select="/Document/n-metadata/metadata.block/md.publications/md.publication/md.pubid" />
				<xsl:with-param name="cite" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.normalizedcite" />
			</xsl:call-template>
			<xsl:text>&currentStatuteEnd;</xsl:text>
		</div>
	</xsl:template>

	<xsl:template name="regChange">
		<div class="&centerClass; &versionUpdateClass; &regChangeDocumentClass; &hideStateClass;">
			<xsl:text>&currentStatuteStart;</xsl:text>
			<span class="&linkToCleanCopyClass;"></span>
			<xsl:text>&currentStatuteEnd;</xsl:text>
		</div>
	</xsl:template>

	<xsl:template name="renderCurrentnessLink">
		<xsl:variable name="contents">
			<xsl:apply-templates select="prelim.block//hide.historical.version[internal.reference]" mode="docHeader"/>
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div class="&currentnessClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="prelim.block" priority="2"/>
	<xsl:template match="content.metadata.block" priority="1" />
	<xsl:template match="doc.title" priority="3" />

	<xsl:template match="prelim.block" mode="statueHeader">
		<xsl:variable name="head">
			<xsl:apply-templates select="prelim.head/head"/>
		</xsl:variable>
		<xsl:variable name="headText">
			<xsl:apply-templates select="prelim.head/head/head.info/headtext"/>
		</xsl:variable>
		<xsl:variable name="subHead">
			<xsl:apply-templates select="prelim.head/prelim.head"/>
		</xsl:variable>
		<xsl:if test="string-length($head) &gt; 0">
			<div class="&genericBoxClass;">
				<div class="&genericBoxHeaderClass;">
					<span></span>
				</div>
				<div class="&genericBoxContentClass;">
					<div class="&genericBoxContentRightClass;">
						<xsl:if test="string-length($subHead) &gt; 0">
							<a class="&widgetCollapseIconClass;" href="#"></a>
						</xsl:if>
						<div>
							<xsl:attribute name="class">
								<xsl:text>&simpleContentBlockClass; &prelimBlockClass;</xsl:text>
								<xsl:if test="string-length($headText) &gt; 0">
									<xsl:text><![CDATA[ ]]>&headtextClass;</xsl:text>
									<xsl:choose>
										<xsl:when test="@style='c' or ancestor::head/@style = 'c' or ancestor::form.head/@style = 'c' or @align='center' or ancestor::head/@align = 'center' or ancestor::form.head/@align = 'center' or ancestor::fa.head/@align = 'center'">
											<xsl:text><![CDATA[ ]]>&alignHorizontalCenterClass;</xsl:text>
										</xsl:when>
										<xsl:when test="@style = 'l'">
											<xsl:text><![CDATA[ ]]>&alignHorizontalLeftClass;</xsl:text>
										</xsl:when>
										<xsl:otherwise />
									</xsl:choose>
								</xsl:if>
							</xsl:attribute>
							<xsl:copy-of select="$head"/>
							<xsl:choose>
								<xsl:when test="not($DeliveryMode)">
									<xsl:if test="string-length($subHead) &gt; 0">
										<div id="&prelimContainerId;">
											<xsl:copy-of select="$subHead"/>
										</div>
									</xsl:if>
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="string-length($subHead) &gt; 0">
										<xsl:copy-of select="$subHead"/>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>
						</div>
					</div>
				</div>
				<div class="&genericBoxFooterClass;">
					<span></span>
				</div>
			</div>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="hide.historical.version" mode="docHeader">
    <xsl:choose>
      <xsl:when test ="./cite.query/text() = '(Refs &amp; Annos)' and ./internal.reference/text() = 'Currentness'">
        <xsl:apply-templates select="*[not(name()='cite.query')]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
	</xsl:template>

	<xsl:template match="hide.historical.version" />

	<xsl:template match="hide.historical.version[cite.query]" priority="1">
    <xsl:choose>
      <xsl:when test ="./cite.query/text() = '(Refs &amp; Annos)' and ./internal.reference/text() = 'Currentness'">
        <xsl:apply-templates select="*[not(name()='internal.reference')]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
	</xsl:template>

	<xsl:template match="include.currency.block" priority="1">
		<xsl:if test="/Document/document-data/datetime &lt; /Document/n-metadata/metadata.block/md.dates/md.endeffective">
			<xsl:call-template name="wrapContentBlockWithCobaltClass">
				<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;',currency.id/@ID)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
