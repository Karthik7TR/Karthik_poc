<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl" />
	<xsl:include href="Headnote.xsl" />
	<xsl:include href="OtherHeadnote.xsl"/>
	<xsl:include href="Caveat.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCaselawOfficialClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.references" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.descriptions/md.westlawdescrip/md.doc.caveats" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" />
			<xsl:apply-templates select="n-docbody/node()"/>
			<xsl:call-template name="RenderFootnoteSection"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="classification.group" priority="1">
		<xsl:variable name="docGuid" select="//md.uuid"/>
		<xsl:variable name="digestKeyName" select="." />
		<xsl:if test="$digestKeyName">
			<xsl:variable name="digestNumber" select="$digestKeyName/@digestkey"/>
			<xsl:variable name="persistentUrl">
				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.BrowseNYOKeyNumberTOC', concat('keyNumber=',$digestNumber), concat('docGuid=',$docGuid), 'contentType=nyoDigest2and3', 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
			</xsl:variable>
			<div>
				<xsl:choose>
					<xsl:when test="string-length($persistentUrl) &gt; 0  and $DisplayNYODigestLinks">
						<a>
							<xsl:attribute name="id">
								<xsl:text>&linkIdPrefix;</xsl:text>
								<xsl:value-of select="$digestKeyName/@digestkey"/>
							</xsl:attribute>
							<xsl:attribute name="class">
								<xsl:text>&linkClass;</xsl:text>>
							</xsl:attribute>
							<xsl:attribute name="href">
								<xsl:value-of select="$persistentUrl"/>
							</xsl:attribute>
							<xsl:apply-templates />
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates />
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- overriding global suppression on include.copyright for NY official (bug #14910); this includes collections w_cs_nyor1, w_cs_nyor2, w_cs_nyoram, w_cs_nyorfm, and w_cs_nyorunr -->
	<xsl:template match="include.copyright[@n-include_collection = 'w_wlnv_msg']">
		<xsl:variable name="collection" select="/Document/document-data/collection" />
		<xsl:if test="$collection = 'w_cs_nyor1' or $collection = 'w_cs_nyor2' or $collection = 'w_cs_nyoram' or $collection = 'w_cs_nyorfm' or $collection = 'w_cs_nyorunr'">
			<div>
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="date.block" priority="1">
		<xsl:call-template name="dateBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="crosshatch">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="md.toggle.links" priority="1">
			<xsl:if test="not($DeliveryMode)">
				<div>
					<xsl:apply-templates select ="md.toggle.link[1]" />
				</div>
			</xsl:if>
	</xsl:template>

	<xsl:template match="md.cites[/Document/document-data/collection = 'w_cs_nyorfm']" priority="1">
		<xsl:if test="not(/Document/n-docbody/*/content.metadata.block/cmd.identifiers/cmd.cites)">
			<xsl:variable name="displayableCites" select="md.primarycite/md.primarycite.info[md.display.primarycite/@display = 'Y']" />
			<xsl:if test="string-length($displayableCites) &gt; 0">
				<div class="&citesClass;">
					<xsl:for-each select="$displayableCites">
						<xsl:apply-templates select="." />
						<xsl:if test="position() != last()">
							<xsl:text>,<![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:for-each>
					<xsl:call-template name="docLabelName" />
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Note: the right class for this is &topicLineClass, but it doesn't center the text, which is a requirement. -->
	<xsl:template match="topic.block" priority="1">
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="cite.query[/Document/document-data/collection = 'w_cs_nyorfm']" name="citeQueryNY">
		<xsl:param name="citeQuery" select="."/>
		<xsl:param name="linkContents">
			<xsl:apply-templates select="$citeQuery/node()[not(self::starpage.anchor)]" />
		</xsl:param>
		<xsl:param name="transitionType" select="'&transitionTypeDocumentItem;'" />
		<xsl:variable name="isDraggable">
			<xsl:choose>
				<xsl:when test="not($AllowLinkDragAndDrop)">
					<xsl:text>false</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>true</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$citeQuery">
			<xsl:variable name="query" select="$citeQuery/@w-normalized-cite"/>
			<xsl:variable name="persistentUrl">
				<xsl:choose>
					<xsl:when test="string-length($citeQuery/@w-normalized-cite) &gt; 0">
						<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.SearchResults', concat('query=',$query), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="CiteQuery:GetCiteQueryLink($citeQuery, $Guid, 'originationContext=&docDisplayOriginationContext;', $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&transitionTypeParamName;=', $transitionType))"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="string-length($persistentUrl) &gt; 0">
					<a>
						<xsl:attribute name="id">
							<xsl:text>&linkIdPrefix;</xsl:text>
							<xsl:value-of select="$citeQuery/@w-normalized-cite"/>
						</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:text>&linkClass;</xsl:text>
							<xsl:if test="$isDraggable = 'true'">
								<xsl:text><![CDATA[ ]]>&linkDraggableClass;</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:attribute name="href">
							<xsl:copy-of select="$persistentUrl"/>
						</xsl:attribute>
						<xsl:copy-of select="$linkContents"/>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$linkContents"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="insertHardSpace" priority="2"/>

</xsl:stylesheet>
