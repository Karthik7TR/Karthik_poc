<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternalReferenceWLN.xsl"/>
	<xsl:include href="Publisher.xsl" />
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="IsCCA" select="contains('|w_codesccanvdp|w_codesccadp|w_3rd_irmhist|', concat('|', /Document/document-data/collection, '|'))"/>
	<xsl:variable name="suppressStarPageMetadata" select="$IsCCA"/>
	<xsl:variable name="IsNS" select="contains('|w_codesnslhnvdp|', concat('|', /Document/document-data/collection, '|'))"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
				</xsl:with-param>
			</xsl:call-template>

			<xsl:if test="not($IsCCA = true())">
				<xsl:call-template name="DisplayPublisherLogo" />
			</xsl:if>

			<!-- Suppressing Star Page Metadata will change the behavior of Copy With Reference functionality.
					Note that this will not suppress the Star Page anchors from displaying.
			-->
			<xsl:choose>
				<xsl:when test="not($suppressStarPageMetadata)">
					<xsl:call-template name="StarPageMetadata" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="StarPageMetadataEmpty" />
				</xsl:otherwise>
			</xsl:choose>

			<xsl:if test="$IsNS = true()">
				<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			</xsl:if>

			<xsl:apply-templates />
			<xsl:call-template name="FooterCitation" />
			<xsl:call-template name="EndOfDocument" />

			<xsl:if test="not($IsCCA = true())">
				<xsl:call-template name="DisplayPublisherLogo" />
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="prelim.head/head">
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="cr[d6 and contains('|w_codesccanvdp|w_codesccadp|', concat('|', /Document/document-data/collection, '|'))] | dl[d6 and contains('|w_codesccanvdp|w_codesccadp|', concat('|', /Document/document-data/collection, '|'))]" priority="2">
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates />
		</div>
		<xsl:if test="local-name() = 'dl' and not(following-sibling::dl)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<xsl:template match="smp52[d6 and count(preceding-sibling::smp52) = 0 and contains('|w_codesccanvdp|w_codesccadp|', concat('|', /Document/document-data/collection, '|'))]" priority="2">
		<div class="&alignHorizontalCenterClass; &paraMainClass;">
			<br/>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="dpa0[d6] | dpa1[d6] | dpa2[d6]">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="spa[not(name(preceding-sibling::node()[not(self::bop | self::eop)][1]) = 'spa')]">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
			<br/>
		</div>
	</xsl:template>

	<xsl:template match="rc.gen">
		<div class="&paraMainClass;">
			<br/>
			<xsl:value-of select="d6"/>
		</div>
	</xsl:template>

	<!-- restrict the solution of bug 360607 to this collection only to avoid unintented changes to other collections using the same stylesheet -->
	<xsl:template match="tgroup[/Document/document-data/collection = 'w_3rd_ftxuscold1']" priority="1">
		<xsl:call-template name ="TGroupTemplate">
			<xsl:with-param name ="checkNoColWidthExists" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Suppress Star Page anchors for the following collections.
			Note that this does not suppress the Star Page Metadata from being inserted
			into the page and being used for Copy with Reference functionality. -->
	<xsl:template match="starpage.anchor[contains('|w_3rd_stfdch|w_3rd_stfdch2|w_3rd_stfdch5|w_3rd_nflxaga1|w_3rd_nflxmil1|w_codes_apbankr78|w_codesccanvdp|w_codesccadp|', concat('|', /Document/document-data/collection, '|'))]" priority="2" />

	<!-- Suppress WL star paging for this collection. Bug 883901: Star Paging is Missing on Congressional Record Documents -->
	<xsl:template match="starpage.anchor[contains('w_codes_congalla|w_codes_congallb|w_codes_congallc|w_codes_congalld|w_codes_congalle|w_codes_congallf|', concat('|', /Document/document-data/collection, '|')) and not(starts-with(@ID, 'sp_100003'))]" priority="2" />

	<!-- Output empty StarPage Metadata JSON -->
	<xsl:template name="StarPageMetadataEmpty">
		<xsl:variable name="jsonObject">
			<xsl:text>{ "&citationMapJsonPropertyName;": { </xsl:text>
			<xsl:text> }</xsl:text>
			<xsl:text> }</xsl:text>
		</xsl:variable>

		<input type="hidden" id="&starPageMetadataId;" value="{$jsonObject}" alt="&metadataAltText;" />
	</xsl:template>

	<!-- for USCCAN Legislative History Citation should display (Leg-Hist), hence overriding the Citation -->
	<xsl:template match="md.cites[/Document/document-data/collection = 'w_codes_leghistory']" priority="3">
		<div class="&citesClass;">
			<xsl:apply-templates select ="md.expandedcite"/>
		</div>
	</xsl:template>

	<xsl:template match="ti" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
		<xsl:if test="not(following-sibling::ti or following-sibling::ti2 or following-sibling::til or following-sibling::hg0 or following-sibling::hg1 or following-sibling::hg2 or following-sibling::hg3 or following-sibling::hg4 or following-sibling::hg5 or following-sibling::hg6 or following-sibling::hg7 or following-sibling::hg8 or following-sibling::hg9 or following-sibling::hg10 or following-sibling::hg11 or following-sibling::hg12 or following-sibling::hg13 or following-sibling::hg14 or following-sibling::hg15 or following-sibling::hg16 or following-sibling::hg17 or following-sibling::hg18 or following-sibling::hg19 or following-sibling::snl or following-sibling::srnl or following-sibling::hc2)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<!-- overriding global suppression on include.copyright -->
	<xsl:template match="include.copyright[@n-include_collection = 'w_wlnv_msg']" priority="1">
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="." />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="md.normalized.billtrack.cite"/>

	<xsl:template match="smp52[d6] | smp53[d6]">
		<xsl:if test="string-length(normalize-space(.//text())) &gt; 0">
			<div class="&paraMainClass;">
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="version | version.date.line | date.line | delegates">
		<div class="&paratextMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="hidden.img.text" priority="1"/>

	<xsl:template match="cmd.first.line.cite" priority="1"/>

	<xsl:template match="d8[child::leader]" priority="1">
		<xsl:call-template name="leaderContent">
			<xsl:with-param name="parent" select="."/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fna[child::internal.reference]">
		<xsl:call-template name="d9">
			<xsl:with-param name="lm" select="2" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dpa1" priority="5" mode="Xena2_AnalyticalOthers_FTXIRM.xsl">
		<div class="&paraMainClass;">&#160;</div>
		<xsl:apply-templates/>
	</xsl:template>

  <xsl:template match="vw[contains('|w_3rd_cfrtxt3k|w_3rd_cfrtxt6k|w_3rd_cfrtxt8k|w_3rd_cfrtext10|', concat('|',  /Document/document-data/collection , '|'))]" priority="2" />
  
</xsl:stylesheet>
