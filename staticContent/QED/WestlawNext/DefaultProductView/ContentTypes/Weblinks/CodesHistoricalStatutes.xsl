<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<!-- Native includes -->
	<!--<xsl:include href="Analysis.xsl"/>-->
	<!--<xsl:include href="Annotations.xsl"/>-->
	<xsl:include href="CitesFromContentMetadata.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="ContextAndAnalysis.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="LinkedToc.xsl"/>
	<!--<xsl:include href="NotesOfDecisions.xsl"/>-->
	<!-- Xena includes -->
	<xsl:include href="Xena2Shared.xsl"/>
	<!-- Common includes -->
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:choose>
				<xsl:when test="n-docbody/doc">
					<!-- Process Xena markup -->
					<xsl:apply-templates select="n-docbody" mode="xena"/>
					<xsl:apply-templates select="n-docbody/doc/mx"/>
					<xsl:call-template name="displayCopyright"/>
				</xsl:when>
				<xsl:otherwise>
					<!-- Process Naive markup -->
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- BEGIN Native Styling -->
	<!-- This is based on a copy of CodeAdminCodes.xsl -->
	<xsl:template match="n-metadata | popular.name.doc.title | hide.historical.version"/>

	<xsl:template match="hide.historical.version[cite.query]" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="hide.historical.version" mode="docHeader">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- overriding global suppression on include.copyright -->
	<xsl:template match="include.copyright.block/include.copyright[@n-include_collection = 'w_codes_stamsgu']" priority="1">
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="." />
		</xsl:call-template>
	</xsl:template>
	<!-- END Native -->

	<!-- BEGIN XENA Styling -->
	<!-- This is based on a copy of Xena2_HistoricalStatutes.xsl-->

	<!-- 	"LinkedToc.xsl" has a nasty template with match="n-docbody/*". This hi-jacks the processing for xena.
		 		Using mode="xena" on the doc template avoids the conflict for xena markup.	-->
	<xsl:template match="doc" mode="xena">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="//md.cites"/>
			<xsl:apply-templates select="hg0 |hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | hg8 | hg9 | hg10 | hg11 | so | snl | srnl | dl | ti | ti2 | dj | dj1 | cpr1 | */s270 | */s271 | */s272 | */s273 | */s274 | */s275 | */s276 | */s277 | */s278 | */s279 | */s040"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:apply-templates select="*[not(self::hg0 or self::hg1 or self::hg2 or self::hg3 or self::hg4 or self::hg5 or self::hg6 or self::hg7 or self::hg8 or self::hg9 or self::hg10 or self::hg11 or self::so or self::snl or self::srnl or self::dl or self::ti or self::ti2 or self::dj or self::dj2 or self::mx or self::cpr1 or child::s270 or child::s271 or child::s272 or child::s273 or child::s274 or child::s275 or child::s276 or child::s277 or child::s278 or child::s279 or child::s040)]" />
	</xsl:template>

	<xsl:variable name="tocElements" select="'hg0|hg1|hg2|hg3|hg4|hg5|hg6|hg7'" />
	<xsl:variable name="dtagNames" select="'d2|d3|d4|d5|d6'"/>
	<xsl:template match=" hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7" priority="2">
		<xsl:variable name="numberOfPreceding" select="count(preceding-sibling::node()[contains($tocElements, local-name())])" />
		<xsl:choose>
			<xsl:when test=" descendant::node()[contains($dtagNames,local-name) and not(@lm)]">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="$numberOfPreceding" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="hstvr//d1" priority="2">
		<xsl:call-template name="d6"/>
	</xsl:template>

	<xsl:template match="md.cites" priority="3">
		<div class="&citesClass;">
			<xsl:choose>
				<xsl:when test="md.second.line.cite">
					<xsl:apply-templates select="md.second.line.cite"/>
				</xsl:when>
				<xsl:when test="md.third.line.cite">
					<xsl:apply-templates select="md.third.line.cite"/>
				</xsl:when>
				<xsl:when test="md.first.line.cite">
					<xsl:apply-templates select="md.first.line.cite"/>
				</xsl:when>
			</xsl:choose>
		</div>
	</xsl:template>

	<!-- Overrode these to avoid duplicate DHE -->
	<xsl:template match="ti | ti2 | til | hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | hg8 | hg9 | hg10 | hg11 | hg12 | hg13 | hg14 | hg15 | hg16 | hg17 | hg18 | hg19 | snl | srnl | hc2" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<!--Supress the copyright message-->
	<xsl:template match="cop | copmx | copsd"/>

	<xsl:template name="displayCopyright">
		<xsl:variable name="copyright_node" select="//cop | //copmx | //copsd"/>
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="$copyright_node"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sf10 | sf00 | xhsu.shfn | xhsu.gnp | xhsu.gnp21 | xhsu.gnp32">
		<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="nd.gnp | xcr.gnp">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--Supress ADDTERM -->
	<xsl:template match="adt.gen"/>

	<!--Supress prtid and tocid -->
	<xsl:template match="prtid | tocid | tlrg | tlep | tlan | tlrp | tlftc | rctax | plcrpe | ce1 | ssrh21 | ssrh23"/>
	<!-- END XENA -->

	<!-- Code for the Collection w_codes_stanenv11-->
	<xsl:template match="analysis.entry">
		<xsl:call-template name="analysisEntry"/>
	</xsl:template>

	<xsl:template name="analysisEntry" >
		<div class="&tocCellWithoutLeadersClass;">
			<xsl:if test="@ID|@id">
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="internalReference">
				<xsl:with-param name="contents">
					<xsl:apply-templates/>
				</xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="analysis.line">
		<xsl:if test="@indent-left and @indent-left!='0'">
			<xsl:if test="@indent-left='1'">
				<div class="&indentLeft1Class;">
					<xsl:apply-templates />
				</div>
			</xsl:if>
			<xsl:if test="@indent-left='2'">
				<div class="&indentLeft2Class;">
					<xsl:apply-templates/>
				</div>
			</xsl:if>
			<xsl:if test="@indent-left='3'">
				<div class="&indentLeft3Class;">
					<xsl:apply-templates/>
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="annotations/reference.block/*">
		<xsl:if test="descendant::library.reference.body or descendant::research.reference.body or descendant::cross.reference.body or descendant::us.sup.ct.reference or descendant::usca.reference or descendant::law.review.reference">
			<div class="&contextAndAnalysisClass; &disableHighlightFeaturesClass;">
				<xsl:apply-templates select="head"/>
				<xsl:apply-templates select="mv.source.head"/>
				<div>
					<xsl:apply-templates select="library.reference.body | research.reference.body | cross.reference.body | us.sup.ct.reference | usca.reference | law.review.reference"/>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="library.reference.block//reference.text | law.review.reference.block//reference.text | research.reference.body//reference.text | cross.reference.body//reference.text | research.reference.body//reference.text | us.sup.ct.reference/reference.text | usca.reference.block//reference.text | law.review.reference/reference.text" priority="1">
		<xsl:choose>
			<xsl:when test="@indent-left and @indent-left!='0'">
				<xsl:if test="@indent-left='1'">
					<div class="&indentLeft1Class;">
						<xsl:apply-templates />
					</div>
				</xsl:if>
				<xsl:if test="@indent-left='2'">
					<div class="&indentLeft2Class;">
						<xsl:apply-templates/>
					</div>
				</xsl:if>
				<xsl:if test="@indent-left='3'">
					<div class="&indentLeft3Class;">
						<xsl:apply-templates/>
					</div>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="ancestor::us.sup.ct.reference or ancestor::law.review.reference">
						<div class="&paraMainClass;">
							<xsl:apply-templates/>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div>
							<xsl:apply-templates/>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Code for the Collection w_codes_stanenv11-->

	<xsl:template match="hist.note.block//headtext | library.reference.block//headtext |cross.reference.block//headtext |research.reference.block//headtext | us.sup.ct.reference.block//headtext | usca.reference.block//headtext | law.review.reference.block//headtext" priority="1">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--<xsl:template match="content.metadata.block[2]" priority="2">-->
	<xsl:template match="content.metadata.block[2][.//cmd.expandedcite]" priority="2">
		<div class="&citationClass;">
			<xsl:apply-templates select=".//cmd.expandedcite" mode="bottomOfPage"/>
		</div>
	</xsl:template>

	<xsl:template match="cmd.expandedcite" mode="bottomOfPage">
		<xsl:apply-templates />
	</xsl:template>

</xsl:stylesheet>
