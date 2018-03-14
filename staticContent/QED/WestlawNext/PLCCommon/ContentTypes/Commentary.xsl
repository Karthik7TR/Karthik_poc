<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Commentary.xsl" forcePlatform="true"/>
	<xsl:include href="doclinks.xsl" />
	<xsl:include href="TableCases.xsl" />
	<xsl:include href="TableStatutes.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:variable name="IsCommentaryEnhancementMode" select="true()"/>

	<xsl:template match="doc" priority="1">
		<div id="&coDocContentHeaderPrelim;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;" >
			<div id="&codocumentHeaderBorder;" class="&documentHeaderBorderClass;"></div>
			<div id="&coDocHeaderContainer;">
				<div class="&titleClass;">
					<xsl:choose>
						<xsl:when test="//table.cases/doc.title">
							<div class="&centerClass;">
								<xsl:apply-templates select="//table.cases/doc.title//text()"/>
							</div>
						</xsl:when>
						<xsl:when test="//table.statutes/doc.title">
							<div class="&centerClass;">
								<xsl:apply-templates select="//table.statutes/doc.title//text()"/>
							</div>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.descriptions/md.longtitle"/>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</div>
		</div>
		<div id="&coDocContentBody;">
			<xsl:call-template name="docBase"/>
		</div>
	</xsl:template>

	<xsl:template name="docBaseContent">
		<!-- Render links to related content -->
		<xsl:if test="sidebar.metadata/group">
			<div class ="&ukRelatedContentLinks;" id="&internalLinkIdPrefix;{generate-id(sidebar.metadata/group[1]/node()[1])}">
				<ul>
					<xsl:apply-templates select="sidebar.metadata/group/head" mode="relatedContentHeader" />
				</ul>
			</div>
		</xsl:if>
		<!-- End links to related content -->
		<!-- Then suppress rendering group elements -->
		<xsl:apply-templates select="node()[not(self::sidebar.metadata/group) and (not(self::content.metadata.block) or preceding-sibling::content.metadata.block)]" />
		<!-- Render related content section -->
		<xsl:if test="sidebar.metadata/group">
			<div class="&ukRelatedContentSection;">
				<xsl:apply-templates select="sidebar.metadata/group" mode="relatedContentSection"/>
			</div>
		</xsl:if>
		<!-- End related content section -->
	</xsl:template>

	<xsl:template match="md.longtitle" mode="docTitle">
		<xsl:call-template name="docTitle"/>
	</xsl:template>

	<xsl:template name="EndOfDocumentCommentary">
		<xsl:variable name="copyrightText" select="//message.block/include.copyright/text()" />
		<xsl:choose>
			<xsl:when test = "string-length($copyrightText) &gt; 0">
				<xsl:call-template name="EndOfDocument">
					<xsl:with-param name="endOfDocumentCopyrightText" select="$copyrightText" />
					<xsl:with-param name="endOfDocumentCopyrightTextVerbatim" select="true()"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="EndOfDocument"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="sidebar.metadata/group/head" mode="relatedContentHeader">
		<li>
			<a href="#&internalLinkIdPrefix;{generate-id(headtext/text())}">
				<xsl:apply-templates select="headtext/text()" />
			</a>
		</li>
	</xsl:template>

	<xsl:template match="sidebar.metadata/group" mode="relatedContentSection">
		<div id="&internalLinkIdPrefix;{generate-id(head/headtext/text())}">
			<h2>
				<xsl:apply-templates select="head/headtext/text()" />
			</h2>
			<ul>
				<!-- Template below will be changed when content be uploaded -->
				<xsl:apply-templates select="link | xref" mode="relatedContentLinks"/>
				<li>
					<a href="#&internalLinkIdPrefix;{generate-id(parent::node()/group[1]/node()[1])}" class="&ukRelatedContentBackLink;">Back to top</a>
				</li>
			</ul>
		</div>
	</xsl:template>

	<xsl:template match="link | xref" mode="relatedContentLinks">
		<li>
			<xsl:apply-templates select="." />
		</li>
	</xsl:template>

	<xsl:template match="codes.para/head[label.designator]" priority="2">
		<xsl:choose>
			<xsl:when test="@level and not(headtext)">
				<xsl:call-template name="renderHeadLevelText">
					<xsl:with-param name="level" select="@level"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="headtext or not(following-sibling::paratext)">
				<xsl:call-template name="head"/>
			</xsl:when>
			<xsl:otherwise />
		</xsl:choose>
	</xsl:template>

	<xsl:template match="headtext | form.headtext" priority="2">
		<xsl:param name="divId"/>
		<xsl:call-template name="renderHeadTextDiv">
			<xsl:with-param name="divId" select="$divId"/>
			<xsl:with-param name="extraClass">
				<xsl:if test="parent::head or parent::form.head">
					<xsl:text>&scrollingHeader;</xsl:text>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="headtext[ancestor::head[@level]] | form.headtext[ancestor::head[@level]]" priority="4">
		<xsl:param name="divId"/>
		<xsl:call-template name="renderHeadLevelText">
			<xsl:with-param name="headId" select="$divId"/>
			<xsl:with-param name="level" select="ancestor::head/@level"/>
			<xsl:with-param name="extraClass">
				<xsl:if test="parent::head or parent::form.head">
					<xsl:text>&scrollingHeader;</xsl:text>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="grey.material.excerpt/document/doc.title" priority="2">
		<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.descriptions/md.longtitle" mode="docTitle"/>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="grey.material.excerpt/document/doc.title/head/headtext" priority="3">
		<xsl:param name="divId"/>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="id" select="$divId"/>
			<xsl:with-param name="contents">
				<xsl:choose>
					<xsl:when test ="$DeliveryMode">
						<i>
							<xsl:apply-templates/>
						</i>
					</xsl:when>
					<xsl:otherwise>
						<em>
							<xsl:apply-templates/>
						</em>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="table.cases/doc.title | table.statutes/doc.title" priority="2"/>

	<xsl:template match="codes.para/paratext[preceding-sibling::*[1][self::head/headtext or self::head/@level]]">
		<xsl:call-template name="renderParagraphTextDiv">
			<xsl:with-param name="suppressLabel" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="text()" mode="ital">
		<xsl:if test="string-length(.) &gt; 0">
			<xsl:choose>
				<xsl:when test ="$DeliveryMode">
					<i>
						<xsl:value-of select="."/>
					</i>
				</xsl:when>
				<xsl:otherwise>
					<em>
						<xsl:value-of select="."/>
					</em>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="text()" mode="strong">
		<xsl:if test="string-length(.) &gt; 0">
			<strong>
				<xsl:value-of select="."/>
			</strong>
		</xsl:if>
	</xsl:template>

	<xsl:template match="cite.query" priority="1">
		<xsl:choose>
			<xsl:when test="@w-ref-type='UC' or @w-ref-type='UL'">
				<xsl:call-template name="citeQuery">
					<xsl:with-param name="transitionType" select="'&transitionTypeCommentaryUKLink;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@w-ref-type='UO'">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="citeQuery" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Display the text under the document link-->
	<xsl:template match="form/form.caption[preceding-sibling::image.link]"/>

	<!--Render PDF link-->
	<xsl:template match="form/image.link[@ttype='pdf']">
		<div class="&imageBlockClass;">
			<xsl:call-template name="createDocumentBlobLink">
				<xsl:with-param name="guid" select="@tuuid"/>
				<xsl:with-param name="targetType" select="@ttype"/>
				<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
				<xsl:with-param name="contents">
					<xsl:apply-templates select="following-sibling::form.caption" mode="imageText"/>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:with-param>
				<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
				<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
				<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!--Render RTF link-->
	<xsl:template match="form/image.link[@ttype='jpeg' and string-length(@ID) &gt; 3 and substring(@ID, string-length(@ID)-3) = '.rtf']">
		<div class="&imageBlockClass;">
			<xsl:call-template name="createDocumentBlobLink">
				<xsl:with-param name="guid" select="@tuuid"/>
				<xsl:with-param name="targetType" select="@ttype"/>
				<xsl:with-param name="mimeType" select="'&crswRtfMimeType;'"/>
				<xsl:with-param name="contents">
					<xsl:apply-templates select="following-sibling::form.caption" mode="imageText"/>
          <xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:with-param>
				<xsl:with-param name="displayIcon" select="'&documentIconPath;'"/>
				<xsl:with-param name="displayIconClassName" select="'&rtfIconClass;'"/>
				<xsl:with-param name="displayIconAltText" select="'&rtfAltText;'"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Displays the element with dotted underline and hover text that contains the @wordform attribute. -->
	<xsl:template match="abbrev[@wordform]">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<span class="&leaderDotsClass;">
				<xsl:attribute name="title">
					<xsl:value-of select="@wordform"/>
				</xsl:attribute>
				<xsl:copy-of select="$contents"/>
			</span>
		</xsl:if>
	</xsl:template>

	<xsl:template match="codes.numbers">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&boldClass; &centerClass; &coFontSize23;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="codes.numbers[year]/chapter.number | codes.numbers[chapter.number]/year">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="codes.numbers[number]/year">
		<xsl:text>SI </xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="codes.numbers/number ">
		<xsl:text>/</xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="codes.numbers/series.number">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="codes.numbers[preceding-sibling::official.number or following-sibling::official.number]"/>

	<xsl:template match="official.number">
		<xsl:variable name="class">
			<xsl:value-of select ="'&boldClass; &centerClass; &coFontSize23;'"/>
			<xsl:if test="parent::node()[preceding-sibling::head]">
				<xsl:value-of select="' &indentTopClass; &indentBottomClassHalfLineSpace;'"/>
			</xsl:if>
		</xsl:variable>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="$class"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="date.block[node()[self::inforce.date or self::made.date or self::laid.date][@iso.d]]">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&centerClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="date.block/made.date[@iso.d] | date.block/laid.date[@iso.d]" name="isoDate">
		<xsl:param name="date" select="@iso.d"/>
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="contents">
				<xsl:apply-templates/>
				<xsl:text><![CDATA[: ]]></xsl:text>
				<xsl:call-template name="parseYearMonthDayDateFormat">
					<xsl:with-param name="date" select="$date"/>
					<xsl:with-param name="displayDay" select="true()"/>
					<xsl:with-param name="displayDayFormat" select="'#'"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="date.block/inforce.date[@iso.d]/paratext">
		<xsl:call-template name="isoDate">
			<xsl:with-param name="date" select="../@iso.d"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="para.group">
		<xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
			<xsl:with-param name="id">
				<xsl:if test="@ID | @id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', @ID|@id)"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="additionalClass" select="'&indentTopClassHalfLineSpace; &indentBottomClassHalfLineSpace;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- <Para.group> <label.designator> and <headtext> to be in larger font to distinguish it from <para> <head>. -->
	<xsl:template match="para.group/head">
		<xsl:param name="divId"/>
		<div class="&titleClass;">
			<xsl:call-template name="head">
				<xsl:with-param name="divId" select="$divId"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Display the <label.designator> above <paratext> elements even if they don’t have <headtext>. -->
	<xsl:template match="para/head/label.designator[not(ancestor::footnote)] | para/head/label.name[not(ancestor::footnote)]" priority="1">
		<xsl:apply-templates select="." mode="strong"/>
	</xsl:template>

	<xsl:template match="para/label.designator[not(ancestor::footnote)] | para/label.name[not(ancestor::footnote)]">
		<xsl:call-template name="wrapWithH">
			<xsl:with-param name="level" select="2"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Override Commentary.xsl platform <list>//<label.designator> suppressing -->
	<xsl:template match="list//codes.para/head/label.designator" priority="5">
		<xsl:apply-templates select="." mode="head-label"/>
	</xsl:template>

	<!-- Display list.item label-->
	<xsl:template name="render-sibling-name-designator">
		<xsl:if test="preceding-sibling::label.name">
			<xsl:apply-templates select="preceding-sibling::label.name"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:if test="preceding-sibling::label.designator">
			<xsl:apply-templates select="preceding-sibling::label.designator"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>

		<xsl:apply-templates select="parent::head[parent::list.item]/preceding-sibling::label.designator" mode="head-label"/>
		<xsl:if test="ancestor::list[1][@prefix-rules='ordered' and string-length(@prefix-type) &gt; 0]">
			<xsl:call-template name="listLabelOrdered">
				<xsl:with-param name="count" select="count(ancestor::list.item[1]/preceding-sibling::list.item) + 1"/>
				<xsl:with-param name="format" select="ancestor::list[1]/@prefix-type"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="ancestor::list[1][@prefix-rules='unordered']">
			<xsl:call-template name="listLabelUnordered"/>
		</xsl:if>
		<xsl:if test="parent::head[not(preceding-sibling::head)]/parent::appendix[@label]">
			<xsl:value-of select="parent::head/parent::appendix[1]/@label"/>
			<xsl:text> &mdash; </xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="label.designator" mode="head-label">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="label.designator[ancestor::head[@level]]" mode="head-label">
		<xsl:call-template name="renderHeadLevelText">
			<xsl:with-param name="level" select="ancestor::head/@level"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Override platform indentation logic. -->
	<xsl:template match="para[not(ancestor::tbl) and not(.//node()[@style='flush.left'])]" priority="1">
		<xsl:call-template name="para"/>
	</xsl:template>

	<xsl:template name="RenderParaLabel">
		<xsl:if test="not(parent::para)">
			<xsl:apply-templates select="preceding-sibling::node()[not(self::text())][1][self::label.designator[not(parent::list.item)] or child::label.designator[not(parent::codes.para) and not(parent::head and following-sibling::headtext)] or self::label.name or child::label.name[not(parent::para)]]" mode="para-label.designator" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="label.designator[ancestor::case.para or ancestor::codes.para]" mode="para-label.designator">
		<xsl:apply-templates select="." mode="strong"/>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template name="BeforeRenderTable">
		<xsl:call-template name="renderParaNumber">
			<xsl:with-param name="paraNumber" select="ancestor::tbl[1]/@para-number | ancestor::codes.para[1]/@para-number"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="form">
		<div class="&formClass;">
			<xsl:call-template name="renderParaNumber"/>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template name="renderParaNumber">
		<xsl:param name="paraNumber" select="@para-number"/>
		<xsl:if test="$paraNumber">
			<div class="&paraAttrs;">
				<xsl:call-template name="generateParaNumberLabel">
					<xsl:with-param name="text" select="$paraNumber"/>
				</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderParaTextAttributes">
		<!--Select @para-number attribute of <paratext> element or of <list> element if it satisfies the path list[@para-number]/list.item[1]/paratext[1]  -->
		<xsl:variable name="paraNumber" select="@para-number | self::paratext[not(preceding-sibling::paratext)]/parent::list.item[not(preceding-sibling::list.item)]/parent::list/@para-number"/>
		<xsl:variable name="hasParaNumber" select="$paraNumber and not((parent::e.update or parent::update.para) and key('paratextsWithNumber', $paraNumber))"/>
		<xsl:variable name="hasUpdatedMaterials" select="(@supplemented = 'true' and not(preceding-sibling::paratext[1][@supplemented = 'true'] and parent::entry) ) or (not(preceding-sibling::paratext) and parent::update.highlighted) or node()[1][self::update.highlighted] or (@para-number and ancestor::update.highlighted)"/>
		<xsl:variable name="hasOnlineMaterials" select="@para-number and ancestor::*[@service='whitebookplus']"/>
		<xsl:if test="$hasParaNumber or $hasUpdatedMaterials or $hasOnlineMaterials">
			<div class="&paraAttrs;">
				<xsl:if test="$hasParaNumber">
					<xsl:call-template name="generateParaNumberLabel">
						<xsl:with-param name="text" select="$paraNumber"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="not(ancestor::tbl)">
					<xsl:if test="$hasUpdatedMaterials">
						<xsl:call-template name="generateUpdatedMaterialsLabel">
							<xsl:with-param name="title" select="'&updatedMaterialsHover;'"/>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="$hasOnlineMaterials">
						<xsl:call-template name="generateParagraphOnlineIcon">
							<xsl:with-param name="title" select="'&onlineMaterialsHover;'"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:if>
			</div>
			<xsl:if test="ancestor::tbl">
				<xsl:if test="$hasUpdatedMaterials">
					<xsl:call-template name="generateUpdatedMaterialsLabel">
						<xsl:with-param name="title" select="'&updatedMaterialsHover;'"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="$hasOnlineMaterials">
					<xsl:call-template name="generateParagraphOnlineIcon">
						<xsl:with-param name="title" select="'&onlineMaterialsHover;'"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="hasParagraphTextContents">
		<xsl:param name="contents" />
		<xsl:choose>
			<xsl:when test=".//text() or .//leader or .//image.block or string-length($contents) &gt; 0">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="footnote.body/para[1]/paratext[1]" priority="2">
		<xsl:variable name="contents">
			<xsl:if test="ancestor::footnote[@supplemented='true']">
				<div class="&paraAttrs;">
					<xsl:call-template name="generateUpdatedMaterialsLabel">
						<xsl:with-param name="title" select="'&updatedFootnoteMaterialsHover;'"/>
					</xsl:call-template>
				</div>
			</xsl:if>
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:call-template name="renderParagraphTextDiv">
				<xsl:with-param name="contents" select="$contents" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="generateParaNumberLabel">
		<xsl:param name="text"/>
		<span class="&paraNumber; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
			<xsl:value-of select="$text"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</span>
	</xsl:template>

	<xsl:template name="generateUpdatedMaterialsLabel">
		<xsl:param name="title"/>
		<xsl:choose>
			<xsl:when test ="$DeliveryMode">
				<xsl:call-template name="buildBlobImageElement">
					<xsl:with-param name="displayIcon" select="'&ukUpdatedMaterialsIconPath;'"/>
					<xsl:with-param name="displayIconAltText" select="$title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<span class="&ukUpdatedMaterialsIcon;" title="{$title}">
					<xsl:text><![CDATA[ ]]></xsl:text>
				</span>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="generateParagraphOnlineIcon">
		<xsl:param name="title"/>
		<xsl:choose>
			<xsl:when test ="$DeliveryMode">
				<xsl:call-template name="buildBlobImageElement">
					<xsl:with-param name="displayIcon" select="'&ukParagraphOnlineIconPath;'"/>
					<xsl:with-param name="displayIconAltText" select="$title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<span class="&ukParagraphOnlineIcon;" title="{$title}">
					<xsl:text><![CDATA[ ]]></xsl:text>
				</span>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Add a class to highlight paratext with updated content -->
	<xsl:template name="getParatextMainClass">
		<xsl:text>&paratextMainClass;</xsl:text>
		<xsl:if test="ancestor::update.highlighted">
			<xsl:text><![CDATA[ ]]>&ukUpdatedMaterials;</xsl:text>
		</xsl:if>
		<xsl:if test="parent::list.item/label.designator or ancestor::list[1][@prefix-rules]">
			<xsl:text><![CDATA[ ]]>&indentLeft2Class;</xsl:text>
		</xsl:if>
		<xsl:if test="preceding-sibling::*[1][self::paratext]">
			<xsl:text><![CDATA[ ]]>&indentTopClass;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="paratext/update.highlighted">
		<xsl:variable name="nonFirst" select="boolean(preceding-sibling::node()[not(self::text()) or normalize-space() &gt; 0])"/>
		<!--Highlight particular text in paragraph.-->
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class">
				<!--Don't need in-line class if the element is the first.-->
				<xsl:if test="$nonFirst">
					<xsl:value-of select="'&inlineParagraphPlatform; '"/>
				</xsl:if>
				<xsl:value-of select="'&ukUpdatedMaterials;'"/>
			</xsl:with-param>
			<xsl:with-param name="contents">
				<!--Don't need paraAttrs container class if the element is the first.-->
				<xsl:if test="$nonFirst">
					<div class="&inlineParagraphPlatform; &paraAttrs;">
						<xsl:call-template name="generateUpdatedMaterialsLabel">
							<xsl:with-param name="title" select="'&updatedMaterialsHover;'"/>
						</xsl:call-template>
					</div>
				</xsl:if>
				<xsl:apply-templates/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Redefine platform template from Footnotes.xsl -->
	<xsl:template match="footnote.reference">
		<xsl:variable name="fromBadFootnote">
			<xsl:call-template name="isFirstChildFromBadFootnote" />
		</xsl:variable>

		<xsl:if test="$fromBadFootnote = 'false'">
			<xsl:variable name="refNumberOutputText">
				<xsl:call-template name="footnoteCleanup">
					<xsl:with-param name="refNumberTextParam" select="." />
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="footnote" select="key('distinctFootnoteIds', @refid)"/>

			<xsl:choose>
				<xsl:when test="$footnote[@supplemented='true']">
					<!-- Wrap in span to highlight reference to updated footnote -->
					<span class="&ukUpdatedMaterials;">
						<xsl:call-template name="generateLinkToFootnote">
							<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
							<xsl:with-param name="footnoteRef" select="." />
						</xsl:call-template>
					</span>
					<xsl:call-template name="generateUpdatedMaterialsLabel">
						<xsl:with-param name="title" select="'&updatedMaterialsHover;'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="generateLinkToFootnote">
						<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
						<xsl:with-param name="footnoteRef" select="." />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="article.block//para[@role='article']//text()">
		<xsl:apply-templates select="." mode="ital"/>
	</xsl:template>

	<xsl:template match="list.item/label.designator[following-sibling::*[1][self::paratext] or following-sibling::*[1][self::list]]" priority="5">
		<xsl:call-template name="listLabel">
			<xsl:with-param name="label">
				<xsl:apply-templates/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="listLabel">
		<xsl:param name="label"/>
		<div class="&coFloatLeft; &paraIndentRightClass;">
			<xsl:copy-of select="$label"/>
		</div>
	</xsl:template>

	<xsl:template name="listLabelUnordered">
		<xsl:call-template name="listLabel">
			<xsl:with-param name="label" select="'&mdash;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="listLabelOrdered">
		<xsl:param name="count"/>
		<xsl:param name="format"/>
		<xsl:call-template name="listLabel">
			<xsl:with-param name="label">
				<xsl:number value="$count" format="$format" />
				<xsl:text><![CDATA[.]]></xsl:text>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.excerpt | codes.excerpt">
		<div class = "&greyExcerpt;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="grey.material.excerpt">
		<div class = "&greyExcerpt; &indentTopClass; &indentBottomClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="journal.cite">
		<xsl:apply-templates select="." mode="ital"/>
	</xsl:template>

	<xsl:template match="ednote[not(child::footnote.reference)]">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="enacting.words">
		<xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
			<xsl:with-param name="additionalClass" select="'&indentTopClass; &indentBottomClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="citation[parent::schedule]">
		<xsl:variable name="alignClass">
			<xsl:choose>
				<xsl:when test="preceding-sibling::head[@level]">
					<xsl:call-template name="alignClassByLevel">
						<xsl:with-param name="level" select="preceding-sibling::head/@level"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&alignHorizontalLeftClass;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class">
				<xsl:text>&boldClass; </xsl:text>
				<xsl:value-of select="$alignClass"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="term">
		<xsl:apply-templates select="." mode="ital"/>
		<xsl:call-template name="renderSpace"/>
	</xsl:template>

	<xsl:template name="renderSpace">
		<xsl:variable name="nextElement">
			<xsl:choose>
				<xsl:when test="string-length(following-sibling::node()[1][self::text()]) &gt; 0">
					<xsl:value-of select="following-sibling::node()[1][self::text()]"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="following-sibling::*[1]//text()[1]"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="substring($nextElement, 1, 1) != ' '">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="address">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&addressClass; &indentTopClass;</xsl:text>
				<xsl:if test="following-sibling::*[1][not(self::address)]">
					<xsl:text> &indentBottomClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:call-template name="renderParaNumber"/>
			<xsl:apply-templates select="address.text | name"/>
		</div>
	</xsl:template>

	<xsl:template match="address/address.text">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="address/name">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&boldClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sidebar.metadata/fields" />

	<xsl:template match="predefined.charstring">
		<xsl:apply-templates select="@charcode"/>
	</xsl:template>

	<xsl:template match="paratext[child::node()[self::emph]]">
		<xsl:call-template name="renderParagraphTextDiv"/>
	</xsl:template>

	<!-- list.xsl overriding: START -->

	<!-- All lists have @prefix-rules which can be:
					specified – the prefix value to be used is in the <list.item>/<label-designator>
					ordered – there will be no <label.designator> but there will also be an @prefix-type on list which will contain 1, a, I or I to say the style of prefix to be generated for each <list.item> depending on the nesting level of the list ie 1, 2, 3 or a, b, c 
					unordered - there will be no <label.designator> but bullet points followed by em-dashes should be generated foras the prefix each <list.item> depending on nesting depth
					none - there will be no <label.designator> and no prefix is required.
	-->

	<!-- Wrapping the nested <ul> in a <div> for Doc delivery -->
	<xsl:template match="list">
		<xsl:param name="class" select="'&listClass;'"/>
		<xsl:choose>
			<xsl:when test="ancestor::list and $DeliveryMode and ($DeliveryFormat = 'Doc' or $DeliveryFormat = 'Rtf')">
				<div>
					<xsl:call-template name="list">
						<xsl:with-param name="class" select="$class"/>
					</xsl:call-template>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="list">
					<xsl:with-param name="class" select="$class"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="list/list.item/paratext/pinpoint.anchor" priority="1"/>
	<xsl:template match="list/list.item/paratext/pinpoint.anchor" mode="list.item-pinpoint.anchor">
		<xsl:call-template name="pinpointAnchor"/>
	</xsl:template>

	<xsl:template match="list/list.item">
		<li>
			<xsl:apply-templates select="paratext/pinpoint.anchor" mode="list.item-pinpoint.anchor"/>
			<xsl:apply-templates/>
		</li>
	</xsl:template>

	<xsl:template match="list[@prefix-rules='ordered' and string-length(@prefix-type) &gt; 0]/list.item[not(head)]">
		<li>
			<xsl:apply-templates select="paratext/pinpoint.anchor" mode="list.item-pinpoint.anchor"/>
			<xsl:call-template name="listLabelOrdered">
				<xsl:with-param name="count" select="count(preceding-sibling::list.item) + 1"/>
				<xsl:with-param name="format" select="parent::list/@prefix-type"/>
			</xsl:call-template>
			<xsl:apply-templates>
				<!-- don't display label.designator with paratext element -->
				<xsl:with-param name="suppressLabel" select="true()"/>
			</xsl:apply-templates>
		</li>
	</xsl:template>

	<xsl:template match="list[@prefix-rules='unordered']/list.item[not(head)]">
		<li>
			<xsl:apply-templates select="paratext/pinpoint.anchor" mode="list.item-pinpoint.anchor"/>
			<xsl:call-template name="listLabelUnordered"/>
			<xsl:apply-templates>
				<!-- don't display label.designator with paratext element -->
				<xsl:with-param name="suppressLabel" select="true()"/>
			</xsl:apply-templates>
		</li>
	</xsl:template>

	<!-- list.xsl overriding: END -->

	<xsl:key name="paratextsWithNumber"  match="paratext[@para-number and not(parent::e.update or parent::update.para)]" use="@para-number" />
	<xsl:key name="paratextUpdateBoxes" match="e.update/paratext[@para-number] | update.para/paratext[@para-number]" use="@para-number"/>

	<xsl:template match="paratext[@para-number]">
		<xsl:param name="suppressLabel"/>
		<xsl:variable name="paraNumber" select="@para-number"/>
		<xsl:choose>
			<!--Display paratext comment-->
			<xsl:when test="parent::e.update or parent::update.para">
				<!--Display paratext comment as usual text if there is no paratext the comment belongs to-->
				<xsl:if test="not(key('paratextsWithNumber', $paraNumber))">
					<xsl:call-template name="renderParagraphTextDiv">
						<xsl:with-param name="suppressLabel" select="$suppressLabel"/>
					</xsl:call-template>
				</xsl:if>
				<!--Otherwise suppress-->
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="updateLinksDivId" select="concat('&internalLinkIdPrefix;', @ID | @id)"/>
				<xsl:variable name="paratextUpdateBoxes" select="key('paratextUpdateBoxes', $paraNumber)"/>

				<xsl:call-template name="RenderParaUpdateLinks">
					<xsl:with-param name="divId" select="$updateLinksDivId"/>
					<xsl:with-param name="paratextUpdateBoxes" select="$paratextUpdateBoxes"/>
				</xsl:call-template>

				<xsl:call-template name="renderParagraphTextDiv">
					<xsl:with-param name="suppressLabel" select="$suppressLabel"/>
				</xsl:call-template>

				<xsl:apply-templates select="$paratextUpdateBoxes" mode="UpdateBox">
					<xsl:with-param name="backLinkId" select="$updateLinksDivId"/>
				</xsl:apply-templates>

			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderParaUpdateLinks">
		<xsl:param name="divId"/>
		<xsl:param name="paratextUpdateBoxes"/>
		<xsl:if test = "count($paratextUpdateBoxes) &gt; 0">
			<div id="{$divId}" class="&ukUpdatedMaterialsContent;">
				<div class="&noteHeader;">
					<xsl:call-template name="generateUpdatedMaterialsLabel"/>
					<span class="&floatRight;">
						<xsl:for-each select="$paratextUpdateBoxes">
							<xsl:variable name="linkId" select="concat('&internalLinkIdPrefix;', @ID|@id)"/>
							<a href="#{$linkId}">
								<xsl:value-of select="parent::e.update/@label | parent::update.para/@label"/>
							</a>
							<xsl:if test="position() != last()">
								<xsl:text><![CDATA[ | ]]></xsl:text>
							</xsl:if>
						</xsl:for-each>
					</span>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="paratext" mode="UpdateBox">
		<xsl:param name="backLinkId"/>
		<xsl:variable name="divId" select="concat('&internalLinkIdPrefix;', @ID|@id)"/>
		<div id="{$divId}" class="&ukUpdatedMaterialsContent;">
			<div class="&noteHeader;">
				<h3>
					<xsl:value-of select="parent::node()/@label"/>
				</h3>
				<a href="#{$backLinkId}" class="&floatRight;">&backToMainWork;</a>
			</div>
			<div class="&noteText;">
				<div class="&noteParaNumber;">
					<xsl:value-of select="@para-number"/>
				</div>
				<xsl:call-template name="renderParagraphTextDiv"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="web.address">
		<xsl:variable name="proto" select="normalize-space(substring-before(@href, ':'))"/>
		<xsl:choose>
			<!-- Do not show links that do not start with ['http://', 'https://'] or empty schema (they are most likely relative to plc and not domain level) -->
			<!-- Block all external links on the iPad -->
			<xsl:when test="not(string-length($proto)=0 or $proto='http' or $proto='https') or $IsIpad = 'true'">
				<xsl:apply-templates />
			</xsl:when>
			<!-- Default is rendering the link. -->
			<xsl:otherwise>
				<xsl:call-template name="renderWebAddressLinkOnClick">
					<xsl:with-param name="uri">
						<xsl:choose>
							<xsl:when test="string-length($proto) &gt; 0">
								<xsl:value-of select="normalize-space(@href)"/>
							</xsl:when>
							<xsl:otherwise>
								<!--Handle links without schema-->
								<xsl:value-of select="concat('//', normalize-space(@href))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="renderWebAddressLinkOnClick">
		<xsl:param name="uri"/>
		<a target="_new" class="&pauseSessionOnClickClass;">
			<xsl:attribute name="href">
				<xsl:value-of select="$uri"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>

	<xsl:template match="analysis" priority="1">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="image.block[@para-number]">
		<div class="&paraAttrs;">
			<xsl:call-template name="generateParaNumberLabel">
				<xsl:with-param name="text" select="@para-number"/>
			</xsl:call-template>
		</div>
		<xsl:call-template name="imageBlock"/>
	</xsl:template>

	<xsl:template match="direction">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&italicClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="text.format/typo.format">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class">
				<xsl:choose>
					<xsl:when test="@align = 'right'">
						<xsl:text>&alignHorizontalRightClass;</xsl:text>
					</xsl:when>
					<xsl:when test="@align = 'left'">
						<xsl:text>&alignHorizontalLeftClass;</xsl:text>
					</xsl:when>
					<xsl:when test="@align = 'center'">
						<xsl:text>&alignHorizontalCenterClass;</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="contents">
				<xsl:call-template name="renderParaNumber"/>
				<xsl:apply-templates/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="GetRowSpan">
		<xsl:param name="entry" />
		<xsl:choose>
			<xsl:when test="string(number($entry/@morerows)) != 'NaN'">
				<xsl:value-of select="number($entry/@morerows) + 1" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderEmptyCell" />

	<xsl:template match="head[@level]" priority="1">
		<xsl:choose>
			<xsl:when test="not(headtext)">
				<xsl:call-template name="renderHeadLevelText">
					<xsl:with-param name="level" select="@level"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise >
				<xsl:call-template name="head"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="alignClassByLevel">
		<xsl:param name="level"/>
		<xsl:variable name="style" select="substring($level, 1, 1)" />
		<xsl:choose>
			<xsl:when test="$style = 'c' or $style = 'C'">
				<xsl:text>&alignHorizontalCenterClass;</xsl:text>
			</xsl:when>
			<xsl:when test="$style = 'l' or $style = 'L'">
				<xsl:text>&alignHorizontalLeftClass;</xsl:text>
			</xsl:when>
			<xsl:otherwise />
		</xsl:choose>
	</xsl:template>

	<xsl:template name="renderHeadLevelText">
		<xsl:param name="headId"/>
		<xsl:param name="level"/>
		<xsl:param name="extraClass"/>
		<xsl:if test=".//text() or .//textrule &gt; 0">
			<xsl:variable name="size" select="substring($level, 2)" />
			<xsl:variable name="classes">
				<xsl:call-template name="alignClassByLevel">
					<xsl:with-param name="level" select="$level"/>
				</xsl:call-template>
				<xsl:if test="string-length($extraClass) &gt; 0">
					<xsl:value-of select="concat(' ', $extraClass)"/>
				</xsl:if>
				<xsl:if test="ancestor::update.highlighted">
					<xsl:text><![CDATA[ ]]>&ukUpdatedMaterials;</xsl:text>
				</xsl:if>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$size = '1' or $size = '2' or $size = '3' or $size = '4' or $size = '5'">
					<xsl:element name="h{$size}">
						<xsl:if test="string-length($headId) &gt; 0">
							<xsl:attribute name="id">
								<xsl:value-of select="$headId"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:attribute name="class">
							<xsl:text>&documentHeader;</xsl:text>
							<xsl:value-of select="concat(' ', $classes)"/>
						</xsl:attribute>
						<xsl:call-template name="render-sibling-name-designator"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="$size = '6'">
					<span>
						<xsl:attribute name="class">
							<xsl:text>&documentHeader; &italicClass;</xsl:text>
							<xsl:value-of select="concat(' ', $classes)"/>
						</xsl:attribute>
						<xsl:call-template name="render-sibling-name-designator"/>
					</span>
				</xsl:when>
				<xsl:otherwise />
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="table/title">
		<xsl:call-template name="titleBlock">
			<xsl:with-param name="class" select="'&titleClass; &scrollingHeader;'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
