<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AddedDeletedMaterial.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="node()[prelim.block or content.metadata.block or doc.title][1]" priority="1" name="rulebookHeaderRenderer">
		<xsl:choose>
			<xsl:when test="not($IsRuleBookMode)">
				<xsl:call-template name="renderCodeStatuteHeader"/>
			</xsl:when>
			<xsl:otherwise>
				<div id="&coDocContentHeaderPrelim;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;" >
					<div id="&codocumentHeaderBorder;" class="&documentHeaderBorderClass;"></div>
					<div id="&coDocHeaderContainer;">
						<xsl:choose>
							<xsl:when test="bill.section.doc.title">
								<xsl:apply-templates select="bill.section.doc.title" mode="statueHeader"/>
							</xsl:when>
							<xsl:when test="doc.title">
								<xsl:apply-templates select="doc.title" mode="statueHeader"/>
							</xsl:when>
							<xsl:when test ="/Document/n-metadata/metadata.block/md.descriptions/md.title">
								<xsl:call-template name="wrapWithDiv">
									<xsl:with-param name="class" select="'&titleClass;'" />
									<xsl:with-param name="contents">
										<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.descriptions/md.title"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:when>
						</xsl:choose>

						<xsl:variable name="dateContent">
							<xsl:value-of select="/Document/n-metadata/metadata.block/md.dates/md.starteffective"/>
						</xsl:variable>
						<xsl:if test="number($dateContent) = $dateContent">
							<div id="&redlineStartdate;" class="&effectiveDateClass;">
								<xsl:text>Effective: </xsl:text>
								<xsl:call-template name="parseYearMonthDayDateFormat">
									<xsl:with-param name="date" select="$dateContent" />
									<xsl:with-param name="displayDay" select="true()" />
									<xsl:with-param name="displayTime" select="false()" />
								</xsl:call-template>
							</div>
						</xsl:if>

						<xsl:apply-templates select="content.metadata.block" mode="statueHeader"/>
					</div>
				</div>
				<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				<div id="&coDocContentBody;">
					<xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
						<xsl:with-param name="additionalClass" select="'&sectionClass;'" />
						<xsl:with-param name="id">
							<xsl:if test="@ID|@id">
								<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="RenderFootnoteSection"/>
					<xsl:call-template name="EndOfDocument" />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Render footnotes at the bottom of the output document -->
	<xsl:template match="n-docbody[not(content.metadata.block)]" priority="1">
		<xsl:apply-templates/>
		<xsl:if test="not($IsRuleBookMode)">
			<xsl:call-template name="RenderFootnoteSection"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="n-docbody[content.metadata.block]" priority="1">
		<xsl:call-template name="rulebookHeaderRenderer"/>
		<xsl:if test="not($IsRuleBookMode)">
			<xsl:call-template name="RenderFootnoteSection"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="bill.section.doc.title/head/head.info/headtext">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="doc.title/head/head.info/headtext">
		<xsl:param name="divId"/>
		<xsl:choose>
			<xsl:when test="not($IsRuleBookMode)">
				<xsl:call-template name="renderHeadTextDiv">
					<xsl:with-param name="divId" select="$divId"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="bill.section.doc.title" mode="statueHeader">
		<xsl:call-template name="titleBlock"/>
	</xsl:template>

	<xsl:template match="bill.section.doc.title"/>

	<xsl:template match="nod.block" priority="1">
		<xsl:if test="not($IsRuleBookMode)">
			<xsl:call-template name="renderNotesOfDecisionsStatutes" />
		</xsl:if>
	</xsl:template>

	<!--TOC creation section : START -->
	<!-- To get subsections changing in the floating header on the page, a specific structure must be adhered to
		-firstly, the internal anchors must be set up on the page
		-the toc container must be wrapped in a div with id kh_toc-wrapper-->
	<xsl:template match="/" mode="Custom">
		<div id="&coIntTocContainer;">
			<!--kh_toc-wrapper-->
			<xsl:attribute name="class">
				<xsl:text>&hideState; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass; &coTocKeepHidden;</xsl:text>
				<xsl:text> </xsl:text>
			</xsl:attribute>

			<xsl:call-template name="wrapWithTag">
				<xsl:with-param name="tag" select="string('div')" />
				<xsl:with-param name="class" select="'&coKhOverlayClass;'" />
				<xsl:with-param name ="contents" select="'&nbsp;'" />
			</xsl:call-template>

			<div id="&coKhTocContainer;" class="&coKhToc;">
				<div class="&coKhTocInner;">
					<div class="&coKhTocHeader;">
						<xsl:if test="not($DeliveryMode)">
							<a href="&hashTagText;" class="&coMenuToggle;">
								<span class="&khIcon; &coMenuToggleIconMenu;">&coActionTogglerSpanText;</span>
								<span>
									<xsl:if test="not($IsMobile)">
										<xsl:attribute name="class">&hideState;</xsl:attribute>
									</xsl:if>
									<xsl:text>&coTableOfContentsHeaderText;</xsl:text>
								</span>
							</a>
							<xsl:if test="not($IsMobile)">
								<a class="&coTocIconCross;" href="#">&closeTOCText;</a>
								<p class="&coAccessibilityLabel;">&coTocToggleHelpText;</p>
							</xsl:if>
						</xsl:if>
					</div>
					<div class="&coKhTocContent;">
						<!--<xsl:if test="$DeliveryMode = 'True'">
								<xsl:attribute name="style">display: block;</xsl:attribute>
							</xsl:if>-->
						<ol class="&coKhTocOlList;">
							<xsl:for-each select="/*//subsection">
								<li>
									<a href="#co_anchor_{@ID}">
										<xsl:value-of select="descendant::subsection.hovertext[1]/@text"/>
									</a>
								</li>
							</xsl:for-each>
						</ol>
					</div>
				</div>
			</div>
		</div>

	</xsl:template>

	<!--TOC creation section : END-->

	<!--UTILITIES : START-->

	<xsl:template name="wrapWithTag">
		<xsl:param name="tag" select="div" />
		<xsl:param name="class"/>
		<xsl:param name="id"/>
		<xsl:param name="href"/>
		<xsl:param name="contents"/>
		<xsl:param name="style"/>
		<xsl:element name="{$tag}">
			<xsl:if test="$class">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($id) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($href) &gt; 0">
				<xsl:attribute name="href">
					<xsl:value-of select="$href"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($style) &gt; 0">
				<xsl:attribute name="style">
					<xsl:value-of select="$style"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

	<!--UTILITIES : END-->

</xsl:stylesheet>
