<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="AmpexIndentation.xsl"/>
	<xsl:include href="AppendixToc.xsl"/>
	<xsl:include href="ArticleBody.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="CorrelationTable.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Document.xsl"/>
	<xsl:include href="Dialogue.xsl"/>
	<xsl:include href="FootnoteBlock.xsl"/>
	<xsl:include href="Form.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:include href="Index.xsl"/>
	<xsl:include href="Jurisdictions.xsl"/>
	<xsl:include href="Letter.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="SummaryToc.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
					<xsl:call-template name="GetCommentaryDocumentEnhancementClasses">
						<xsl:with-param name="prependSpace" select="true()"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayPublisherLogo"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:choose>
				<xsl:when test="$EasyEditMode">
					<xsl:apply-templates select="node()" mode="EasyEdit"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="//md.form.flag">
						<xsl:call-template name="EasyEditFlag"/>
					</xsl:if>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="EndOfDocumentCommentary" />
			<xsl:call-template name="DisplayPublisherLogo"/>
		</div>
	</xsl:template>

	<!-- Added template to redefine for UK -->
	<xsl:template name="EndOfDocumentCommentary">
			<xsl:call-template name="EndOfDocument" />
	</xsl:template>

	<xsl:template match="doc" name="docBase">
		<div>
			<xsl:apply-templates select="content.metadata.block[1]" />
			<xsl:apply-templates select="prop.block[1]" mode="PropBlock">
				<xsl:with-param name="appendContent">
					<!-- Display author name in prop.block container. -->
					<xsl:apply-templates select="article/article.front/author.line"/>
					<xsl:apply-templates select="newsletter.article/newsletter.article.front/author.line"/>
					<xsl:apply-templates select="journal.article/journal.article.front/author.line"/>
					<xsl:apply-templates select="section/section.front/author.line"/>
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="prop.block[preceding-sibling::prop.block]" mode="PropBlock"/>

			<xsl:apply-templates select="article/article.front/doc.title"/>
			<xsl:apply-templates select="newsletter.article/newsletter.article.front/doc.title"/>
			<xsl:apply-templates select="journal.article/journal.article.front/doc.title"/>
			<xsl:apply-templates select="*/section.front/doc.title"/>
		</div>
		<xsl:call-template name="docBaseContent" />
	</xsl:template>

	<!-- New template added to redefine for UK -->
	<xsl:template name="docBaseContent">
		<xsl:apply-templates select="node()[not(self::content.metadata.block) or preceding-sibling::content.metadata.block]" />
	</xsl:template>

	<xsl:template match="email.address">
		<a class="&pauseSessionOnClickClass;" target="_blank">
			<xsl:attribute name="href">
				mailto:<xsl:value-of select="@href"/>
				?subject=<xsl:value-of select="@subject"/>
			</xsl:attribute>
			<xsl:value-of select="."/>
		</a>
	</xsl:template>

	<xsl:template name="PublisherLogo">
		<xsl:choose>
			<xsl:when test="/Document/n-metadata/metadata.block/md.subjects/md.subject/md.pubtype.name">
				<xsl:call-template name="DisplayPublisherLogo" />
			</xsl:when>
			<xsl:when test="/Document/n-metadata/metadata.block/md.subjects/md.subject/md.view[last()-2] = '&PublisherAsp;'">
				<xsl:call-template name="DisplayPublisherLogo">
					<xsl:with-param name="PublisherType" select="'&PublisherAsp;'" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="doc/article/article.front | doc/newsletter.article/newsletter.article.front | doc/journal.article/journal.article.front | doc/*/section.front">
		<xsl:apply-templates select="node()[not(self::doc.title or self::author.line)]" />
	</xsl:template>

	<xsl:template match="md.references | md.primarycite | md.parallelcite" />

	<!-- We are overriding the md.cites template so that we can remove duplicate citations that appear in the content.
			Ideally this should be fixed in the content so that we don't have to perform this logic when the document is being displayed. -->
	<xsl:template match="md.cites" priority="1">
		<xsl:if test="not(/Document/n-docbody/*/content.metadata.block/cmd.identifiers/cmd.cites)">
			<xsl:variable name="displayableCites" select="md.primarycite/md.primarycite.info[md.display.primarycite/@display = 'Y' and string-length(md.display.primarycite) &gt; 0 ] | md.parallelcite/md.parallelcite.info[md.display.parallelcite/@display = 'Y' and string-length(md.display.parallelcite) &gt; 0 and not(normalize-space(../preceding-sibling::md.primarycite/md.primarycite.info/md.display.primarycite) = normalize-space(md.display.parallelcite))]" />
			<xsl:if test="string-length($displayableCites) &gt; 0">
				<xsl:choose>
					<xsl:when test="$IsCommentaryEnhancementMode">
						<div>
							<xsl:call-template name="mdCitesCommentary">
								<xsl:with-param name="displayableCites" select="$displayableCites" />
							</xsl:call-template>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="mdCitesCommentary">
							<xsl:with-param name="displayableCites" select="$displayableCites" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="mdCitesCommentary">
		<xsl:param name="displayableCites"/>
		<div class="&citesClass;">
			<xsl:for-each select="$displayableCites">
				<xsl:apply-templates select="." />
				<xsl:if test="position() != last()">
					<xsl:text>,<![CDATA[ ]]></xsl:text>
				</xsl:if>
			</xsl:for-each>
			<xsl:call-template name="docLabelName" />
		</div>
	</xsl:template>

	<!-- Remove co_hAlign2 (center) and co_Align3 (right) CSS classes from justified.line in prelim.block -->
	<xsl:template match="prelim.block//justified.line" priority="1">
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<xsl:variable name="contents">
					<xsl:apply-templates />
				</xsl:variable>
				<xsl:if test="string-length($contents) &gt; 0">
					<div>
						<xsl:copy-of select="$contents"/>
					</div>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="justifiedLine" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="analysis" priority="1">
		<hr class="&horizontalRuleClass;"/>
		<xsl:call-template name="analysis"/>
	</xsl:template>

	<xsl:template match="art.jur.block" priority="1">
		<hr class="&horizontalRuleClass;"/>
		<xsl:call-template name="artJurBlock"/>
	</xsl:template>

	<xsl:template match="index" priority="1">
		<hr class="&horizontalRuleClass;"/>
		<xsl:call-template name="index"/>
	</xsl:template>

	<xsl:template match="article.body" priority="1">
		<hr class="&horizontalRuleClass;"/>
		<xsl:call-template name="articleBody"/>
	</xsl:template>

	<!-- Bug 938701: roll back changes for pdf inline images - show each in separate line without pipes -->
	<xsl:template name="wrapAndRenderImageBlock">
		<xsl:param name="suppressNoImageDisplay" select="false()"/>
		<xsl:variable name="guid" select="image.link/@target" />
		<div>
			<xsl:call-template name="renderImageBlock">
				<xsl:with-param name="suppressNoImageDisplay" select="$suppressNoImageDisplay" />
				<xsl:with-param name="imageClass">
					<xsl:if test="not(/*/ImageMetadata/n-metadata[@guid = $guid]/md.block/md.pdf.block)">
						<xsl:value-of select="&commentaryInlineImage;"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Removing this extra check that causes documents to display twice.  If the footnotes do not
			show up for some conent now, then we will need to look into logic to fix that without
			having it appear twice for the content it shows up in currently. -->
	<!--<xsl:template match="n-docbody" priority="1">
		<xsl:apply-templates />
		<xsl:call-template name="RenderFootnoteSection">
			<xsl:with-param name="renderHorizontalRule" select="true()"/>
		</xsl:call-template>
	</xsl:template>-->

	<!--Document head templates-->
	<xsl:template match="front.matter[not(preceding-sibling::front.matter)]">
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<xsl:call-template name="wrapContentBlockWithCobaltClass">
					<xsl:with-param name="contents">
						<xsl:if test="source[following-sibling::date]">
							<div class="&publicationLineClass;">
								<!--Renders publication title-->
								<xsl:apply-templates select="source[following-sibling::date][1]"/>
								<span class="&verticalDividerClass;">
									<xsl:text><![CDATA[ | ]]></xsl:text>
								</span>
								<!--Renders publication date-->
								<xsl:apply-templates select="date" mode="PublicationLine" />
							</div>
						</xsl:if>
						<!--Renders author -->
						<xsl:apply-templates select="author.line | author.block" mode="PublicationLine" />

						<xsl:apply-templates select="node()[not(self::source[following-sibling::date][not(preceding-sibling::source)] or self::date or self::author.line or self::author.block)]" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapContentBlockWithCobaltClass" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="not(//doc.title)">
			<!-- To avoid DHE duplication for legacy content in existing XSLT tests -->
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<!--Suppress the prop.block and display the prop.block on the top on the content. -->
	<xsl:template match ="prop.block" priority="1"/>
	<xsl:template match ="prop.block" mode="PropBlock">
		<xsl:param name="appendContent"/>
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="contents">
				<xsl:choose>
					<xsl:when test="$IsCommentaryEnhancementMode and prop.head[following-sibling::content.metadata.block[cmd.dates] or following-sibling::date]">
						<div class="&publicationLineClass;">
							<!--Renders publication title-->
							<xsl:apply-templates select="prop.head" mode="PublicationLine" />
							<span class="&verticalDividerClass;">
								<xsl:text><![CDATA[ | ]]></xsl:text>
							</span>
							<!--Renders publication date-->
							<xsl:apply-templates select="content.metadata.block/cmd.dates | date" mode="PublicationLine" />
						</div>
						<!--Render author info, prelim text etc.-->
						<xsl:for-each select="node()">
							<xsl:choose>
								<xsl:when test="self::prop.head or self::content.metadata.block[cmd.dates] or self::date">
									<xsl:apply-templates select="." mode="PublicationLineOutside"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:apply-templates select="."/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="string-length($appendContent) &gt; 0">
					<xsl:copy-of select="$appendContent"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- PublicationLine mode START-->
	<!-- Only first prop.head should be displayed in PublicationLine mode. -->
	<xsl:template match="prop.head" mode="PublicationLine"/>

	<!-- Call core head template if this is first prop.head element. -->
	<xsl:template match="prop.head[following-sibling::content.metadata.block[cmd.dates] or following-sibling::date][1]" mode="PublicationLine">
		<xsl:call-template name="head" />
	</xsl:template>

	<!-- Render content before headtext/endline START -->
	<xsl:template match="prop.head[following-sibling::content.metadata.block[cmd.dates] or following-sibling::date][1][headtext/endline]" mode="PublicationLine">
		<xsl:param name="divId">
			<xsl:if test="string-length(@id|@ID) &gt; 0">
				<xsl:value-of select="concat('&internalLinkIdPrefix;', @id|@ID)"/>
			</xsl:if>
		</xsl:param>
		<xsl:apply-templates select="node()[not(self::label.name) and not(self::label.designator)]" mode="PublicationLine">
			<xsl:with-param name="divId" select="$divId"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="prop.head/headtext[endline]" mode="PublicationLine">
		<xsl:param name="divId"/>
		<xsl:call-template name="renderHeadTextDiv">
			<xsl:with-param name="divId" select="$divId"/>
			<xsl:with-param name="contents">
				<!-- Render content before first endline element -->
				<xsl:apply-templates select="node()[following-sibling::endline[not(preceding-sibling::endline)]]"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<!-- Render content before headtext/endline END -->

	<!--Defines displaying the cmd.dates in publication line. -->
	<xsl:template match ="cmd.dates | date" mode="PublicationLine">
		<xsl:call-template name="dateBlock" />
	</xsl:template>

	<!--Defines displaying the author directly after publication line. -->
	<xsl:template match ="author.line | author.block" mode="PublicationLine">
		<xsl:call-template name="author" />
	</xsl:template>
	<!-- PublicationLine mode END-->

	<!-- PublicationLineOutside START -->

	<!-- Render all prop.head element except the first (it was rendered in PublicationLine mode). -->
	<xsl:template match="prop.head" mode="PublicationLineOutside">
		<xsl:call-template name="head" />
	</xsl:template>
	<xsl:template match="prop.head[following-sibling::content.metadata.block[cmd.dates] or following-sibling::date][1]" mode="PublicationLineOutside"/>

	<!-- Render content after headtext/endline (the content before the element is rendered in PublicationLine). -->
	<xsl:template match="prop.head[following-sibling::content.metadata.block[cmd.dates] or following-sibling::date][1][headtext/endline]" mode="PublicationLineOutside">
		<xsl:param name="divId">
			<xsl:if test="string-length(@id|@ID) &gt; 0">
				<xsl:value-of select="concat('&internalLinkIdPrefix;', @id|@ID)"/>
			</xsl:if>
		</xsl:param>
		<xsl:apply-templates select="node()[not(self::label.name) and not(self::label.designator)]" mode="PublicationLineOutside">
			<xsl:with-param name="divId" select="$divId"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="prop.head/headtext[endline]" mode="PublicationLineOutside">
		<xsl:param name="divId"/>
		<xsl:call-template name="renderHeadTextDiv">
			<xsl:with-param name="divId" select="$divId"/>
			<xsl:with-param name="contents">
				<xsl:apply-templates select="node()[preceding-sibling::endline]"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<!-- Render content after headtext/endline END -->

	<xsl:template match ="cmd.dates | date" mode="PublicationLineOutside"/>
	<xsl:template match ="author.line | author.block" mode="PublicationLineOutside"/>
	<!-- PublicationLineOutside END -->

	<!--end: Document head templates-->

	<xsl:template match="endline" priority="1">
		<xsl:choose>
			<!-- We have issues with parser for <endline> generated as <div> within <strong> in legacy content for XSLT tests-->
			<xsl:when test="parent::bold">
				<br />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="wl.research.text" priority="1">
		<div class="&genericBoxClass;">
			<div class="&genericBoxContentClass;">
				<xsl:apply-templates/>
			</div>
		</div>
	</xsl:template>
	<xsl:template match="search.terms" priority="1">
		<div class="&boldClass;">
			<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		</div>
	</xsl:template>

	<xsl:template match="doc.title" name="docTitle" priority="1">
		<xsl:param name="class" select="'&titleClass;'"/>
		<xsl:if test="$IsCommentaryEnhancementMode">
			<!--Render horizontal divider -->
			<div class="&dividerClass;"></div>
		</xsl:if>
		<xsl:call-template name="titleBlock">
			<xsl:with-param name="class" select="$class"/>
		</xsl:call-template>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="prelim.block/front.matter/title.block | doc/front.matter/title.block" priority="1">
		<xsl:if test="($IsCommentaryEnhancementMode)">
			<!--Render horizontal divider -->
			<div class="&dividerClass;"></div>
		</xsl:if>
		<xsl:call-template name="titleBlock"/>
	</xsl:template>

	<!-- these three template overrides were requested by CSS to make a more consistant class structure-->
	<xsl:template match="form.dialogue.item/form.speaker">
		<div class="&dialogueItemSpeakerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	<xsl:template match="form.dialogue/form.para">
		<!-- don't output a para div-->
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="form.dialogue/form.para/form.text">
		<div class="&dialogueItemTextClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	<!-- end overrides -->

	<!-- This forces the form.para class to get a wrapping paragraph styling. This will get the textrule lines to indent the same as the 
			label.designator and text of the item.  -->
	<xsl:template match="form.para" priority="2">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&paraMainClass;</xsl:text>
				<!-- This will get the textrule lines to indent the label.designator text to the left than text of the item.-->
				<xsl:if test="$IsCommentaryEnhancementMode and form.head/label.designator[not(following-sibling::form.headtext)]">
					<xsl:text><![CDATA[ ]]>&paraIndentHangingClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--This forces all the below elements to be wrapped in a div tag and if there are still styling issues, CSS can take it from here.-->
	<xsl:template match="form.signature.line | form.signature.line/instruction">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="contents">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="action.guide">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" >
			<xsl:with-param name="id">
				<xsl:if test="@id or @ID">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@id | @ID)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- This overrides the default match on the head element to account for possible anchor markup needed first. -->
	<xsl:template match="head[@ID]" priority="1">
		<xsl:call-template name="head">
			<xsl:with-param name="divId">
				<xsl:value-of select="concat('&internalLinkIdPrefix;', @ID)"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="database.link">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="street">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- suppress the first line cite at the bottom of the document -->
	<xsl:template match="content.metadata.block/cmd.identifiers/cmd.cites/cmd.first.line.cite" />

	<!--******************Fix Table Spacing************************-->
	<xsl:template match="appendix[tbl][1]//tbody/row/entry[/Document/document-data/collection = 'w_3rd_lrevintl']" priority="3">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<xsl:variable name="textNodes">
			<xsl:apply-templates select="preceding-sibling::entry" mode="appendixToc"/>
			<xsl:if test="./following-sibling::entry">
				<xsl:apply-templates/>
			</xsl:if>
			<xsl:apply-templates select="following-sibling::entry[following-sibling::entry]" mode="appendixToc"/>
		</xsl:variable>
		<td>
			<xsl:call-template name="RenderTableCell">
				<xsl:with-param name="columnInfo" select="$columnInfo"/>
				<xsl:with-param name="colalign" select="$colalign" />
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="colwidth" select="$colwidth" />
				<xsl:with-param name="contents">
					<xsl:variable name="idRef" >
						<xsl:variable name="headtextId" select="generate-id(/Document/n-docbody/doc/introduction.section/section/appendix[preceding::appendix]/head/headtext[contains(translate(., '&alphabetUppercase; .', '&alphabetLowercase;'), translate($textNodes, '&alphabetUppercase; .', '&alphabetLowercase;'))][1])"/>
						<xsl:choose>
							<xsl:when test="string-length($headtextId) &gt; 0">
								<xsl:value-of select="$headtextId"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="generate-id(/Document/n-docbody/doc/introduction.section/section/appendix[preceding::appendix]/para/paratext[contains(translate(., '&alphabetUppercase; .', '&alphabetLowercase;'), translate($textNodes, '&alphabetUppercase; .', '&alphabetLowercase;'))][1])"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<!-- Apply templates inside a variable so that we don't get links with no text. These links
							 with no text cause delivered word documents to display the link's href as text. Bug 260273 -->
					<xsl:variable name="contents">
						<xsl:apply-templates/>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="string-length($idRef) &gt; 0 and string-length($contents) &gt; 0">
							<xsl:text>&#160;&#160;</xsl:text>
							<a class="&internalLinkClass;" href="#co_g_{$idRef}">
								<xsl:apply-templates/>
							</a>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&#160;&#160;</xsl:text>
							<xsl:apply-templates/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</td>
	</xsl:template>

	<!-- author-->
	<xsl:template match="author.line" priority="2" mode="AnalyticalTreatisesAndAnnoCodes.xsl">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="n-docbody/decision/prelim.block/author.block/author.name">
		<xsl:apply-templates select="/Document/n-docbody/decision/front.matter"/>
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<div>
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>&#160;</xsl:text>
				<div class="&centerClass;">
					<xsl:apply-templates />
					<xsl:text>&#160;</xsl:text>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Author Block Head-->
	<xsl:template match="author.block">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Fixed defect 377423, The template is copied from Web2 XSLT for Intendation-->
	<xsl:template match="para[not(ancestor::tbl) and not(.//node()[@style='flush.left'])]" priority="1">
		<xsl:choose>
			<xsl:when test="parent::para">
				<div>
					<xsl:attribute name="class">
						<!-- Fix spacing issues  -->
						<xsl:if test="$IsCommentaryEnhancementMode">
							<xsl:value-of select="'&paraMainClass; '"/>
						</xsl:if>
						<xsl:value-of select="'&paraIndentLeftClass;'"/>
					</xsl:attribute>
					<!-- add the id attribute in the case that this element is being linked to -->
					<xsl:if test="string-length(@id|@ID) &gt; 0">
						<xsl:attribute name="id">
							<xsl:value-of select="concat('&internalLinkIdPrefix;', @id|@ID)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div>
					<xsl:attribute name="class">
						<!-- Bug #936859 remove extra line spaces: use paraMainClass for <para> instead of wrong paratextMainClass -->
						<xsl:text>&paraMainClass;</xsl:text>
						<xsl:if test="$IsCommentaryEnhancementMode and (label.designator or head/label.designator[not(ancestor::section) or not(following-sibling::headtext)])">
							<xsl:text><![CDATA[ ]]>&paraIndentHangingClass;</xsl:text>
						</xsl:if>
					</xsl:attribute>
					<!-- add the id attribute in the case that this element is being linked to -->
					<xsl:if test="string-length(@id|@ID) &gt; 0">
						<xsl:attribute name="id">
							<xsl:value-of select="concat('&internalLinkIdPrefix;', @id|@ID)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Parallel Cites -->
	<xsl:template match="md.parallelcite/md.parallelcite.info">
		<xsl:if test="md.display.parallelcite/@display = 'Y' or md.display.parallelcite/@userEntered = 'Y'">
			<xsl:apply-templates select="md.display.parallelcite" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="main.text.body/head">
		<xsl:choose>
			<xsl:when test="@type">
				<div >
					<xsl:apply-templates/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="&centerClass;">
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<!-- message block copyright line-->
	<xsl:template match="message.block" priority="2" mode="AnalyticalTreatisesAndAnnoCodes.xsl">
		<xsl:apply-templates />
	</xsl:template>


	<!--**Med lit drug templates start**-->
	<xsl:template match ="drug.generic | drug.brand">
		<div style="text-indent:-0.25in; padding-left:0.250in;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match ="drug.use.block | drug.generic.name | drug.brand.name | drug.family.name | drug.action | drug.xref">
		<xsl:apply-templates />
		<br/>
	</xsl:template>

	<xsl:template match ="drug.use">
		<xsl:apply-templates/>
		<xsl:if test ="following-sibling::drug.use">
			<xsl:text>, </xsl:text>
		</xsl:if>
	</xsl:template>
	<!--**Med lit drug templates end**-->

	<xsl:template name="renderSpaceBetweenCiteQueries" priority="1">
		<xsl:choose>
			<xsl:when test="parent::nav.links and $IsCommentaryEnhancementMode">
				<span class="&verticalDividerClass;">
					<xsl:text><![CDATA[ | ]]></xsl:text>
				</span>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- suppresses all but the first content.metadata.block -->
	<xsl:template match="content.metadata.block[preceding-sibling::content.metadata.block and not(cmd.royalty/cmd.copyright)]" />

	<!-- suppress the first line cite at the bottom of the document -->
	<xsl:template match="content.metadata.block[preceding-sibling::content.metadata.block]/cmd.identifiers/cmd.cites/cmd.first.line.cite" mode="AnalyticalABA.xsl"/>
	<xsl:template match="content.metadata.block[preceding-sibling::content.metadata.block]/cmd.identifiers/cmd.cites/cmd.first.line.cite" mode="AnalyticalForms.xsl"/>
	<xsl:template match="content.metadata.block[preceding-sibling::content.metadata.block]/cmd.identifiers/cmd.cites/cmd.first.line.cite" mode="AnalyticalTreatisePracticeGuides.xsl"/>
	<xsl:template match="content.metadata.block[preceding-sibling::content.metadata.block]/cmd.identifiers/cmd.cites/cmd.first.line.cite" mode="AnalyticalTreatisesAndAnnoCodes.xsl"/>

	<!-- Suppress starpaging for Commentary content. -->
	<xsl:template match="starpage.anchor[contains('|Analytical - ABA Model Rules|Analytical Treatise - other|Analytical - Anno code|Analytical - Treatise - Aspen|Analytical Other - Texts Treatises|', concat('|', /Document/document-data/doc-type, '|'))]" priority="1" />

	<!-- Fixes displaying copyright at top of doc. -->
	<xsl:template match="include.copyright[contains('|Other Treatises - Eagan Treatises|Other Treatises - Restatements|Other Treatises - Rochester Treatises|Other Treatises - WGL|', concat('|', /Document/document-data/doc-type, '|'))]">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&centerClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="nav.links">
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<!-- nav.links left justified in smaller font -->
				<xsl:call-template name="wrapContentBlockWithCobaltClass" />
			</xsl:when>
			<xsl:otherwise>
				<!-- Center nav.links for new content -->
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&centerClass;'" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Fixes displaying footnotes header. -->
	<!-- Copies behaviour  from Footnotes.xsl for all doc types except 'Analytical - Law Review' -->
	<xsl:template match="n-docbody[not(/Document/document-data/doc-type[contains('|Analytical - Law Review|', concat('|', text(), '|'))])]" name="nDocbody" priority="1">
		<xsl:apply-templates/>
		<xsl:call-template name="RenderFootnoteSection"/>
	</xsl:template>

	<!-- Suppress footnote.block template for all document types except 'Analytical - Law Review' -->
	<xsl:template match="footnote.block[not(/Document/document-data/doc-type[contains('|Analytical - Law Review|', concat('|', text(), '|'))])]" />

	<xsl:template match="footnote | form.footnote | endnote | form.endnote" priority="2">
		<xsl:choose>
			<xsl:when test="/Document/document-data/doc-type[contains('|Analytical - Law Review|', concat('|', text(), '|'))]">
				<xsl:apply-templates select="." mode="footnote"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="$DeliveryMode">
					<xsl:choose>
						<!-- added this condition for Table footnotes delivery-->
						<xsl:when test="node()[parent::tbl] or node()[descendant::table.footnote.reference]">
							<xsl:variable name="tablefootnotetext" select="footnote.body/para/paratext/table.footnote.reference/super" />
							<xsl:processing-instruction name="inlineFootnote">
								<xsl:value-of select="$tablefootnotetext" />
							</xsl:processing-instruction>
						</xsl:when>
						<xsl:otherwise>
							<xsl:processing-instruction name="inlineFootnote">
								<xsl:value-of select="@ID"/>
							</xsl:processing-instruction>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- end: Fixes displaying footnotes header. -->

	<!-- AnalyticalTreatisesAndAnnoCodes -->
	<xsl:template match="head[ancestor::section and /Document/document-data/doc-type[contains('|Analytical Treatise - other|Analytical - Anno code|Analytical - Treatise - Aspen|', concat('|', text(), '|'))]]/bop" priority="2">
		<xsl:apply-templates select="." mode="AnalyticalTreatisesAndAnnoCodes"/>
	</xsl:template>
	<xsl:template match="head[ancestor::section]/bop" mode="AnalyticalTreatisesAndAnnoCodes">
		<xsl:if test="not($IsCommentaryEnhancementMode)">
			<br/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="para[ancestor::section and /Document/document-data/doc-type[contains('|Analytical Treatise - other|Analytical - Anno code|Analytical - Treatise - Aspen|', concat('|', text(), '|'))]]" priority="2">
		<xsl:apply-templates select="." mode="AnalyticalTreatisesAndAnnoCodes"/>
	</xsl:template>
	<xsl:template match="para[ancestor::section]" mode="AnalyticalTreatisesAndAnnoCodes">
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<xsl:call-template name="para"/>
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraIndentLeftClass;">
					<!-- add the id attribute in the case that this element is being linked to -->
					<xsl:if test="string-length(@id|@ID) &gt; 0">
						<xsl:attribute name="id">
							<xsl:value-of select="concat('&internalLinkIdPrefix;', @id|@ID)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</div>
				<br/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- end: AnalyticalTreatisesAndAnnoCodes -->

	<!-- AnalyticalALR -->
	<xsl:template match="summary.toc[/Document/document-data/doc-type[contains('|Analytical - ALR|', concat('|', text(), '|'))]]" priority="1">
		<hr class="&horizontalRuleClass;"/>
		<xsl:call-template name="summaryToc"/>
	</xsl:template>

	<xsl:template match="research.references[/Document/document-data/doc-type[contains('|Analytical - ALR|', concat('|', text(), '|'))]]" priority="1">
		<xsl:apply-templates select="." mode="AnalyticalALR_AnalyticalArticleBased"/>
	</xsl:template>
	<xsl:template match="research.references" mode="AnalyticalALR_AnalyticalArticleBased">
		<hr class="&horizontalRuleClass;"/>
		<xsl:call-template name="researchReferences"/>
	</xsl:template>
	<!-- end: AnalyticalALR -->

	<!-- AnalyticalArticleBased -->
	<xsl:template match="research.references[/Document/document-data/doc-type[contains('|Analytical - Article Based|', concat('|', text(), '|'))]]" priority="1">
		<xsl:apply-templates select="." mode="AnalyticalALR_AnalyticalArticleBased"/>
	</xsl:template>
	<!-- end: AnalyticalArticleBased -->

	<xsl:template match="research.references[ancestor::footnote]" priority="1">
		<xsl:apply-templates select="reference.block[topic.key.ref or reference]"/>
	</xsl:template>

	<!-- Research References / Key Number Digest References stylesheet fixes -->
	<!-- Wrap references with <li> in enhanced mode -->
	<xsl:template match="research.references/reference.block[(topic.key.ref or reference) and not(ancestor::footnote)]">
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<xsl:call-template name="wrapResearchRefsWithLi"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="wrapResearchRefsWithLi">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and ($DeliveryFormat = 'Doc' or $DeliveryFormat = 'Rtf')">
				<!-- Wrap in table instead of div due to MS Word issues with borders for containers-->
				<table>
					<tr>
						<td class="&researchReferenceBlockClass;">
							<xsl:apply-templates select="node()[not(self::topic.key.ref or self::reference)]"/>
							<ul class="&bullListClass;">
								<xsl:apply-templates select="topic.key.ref | reference" mode="ResearchReferenceLiWrapped" />
							</ul>
						</td>
					</tr>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<div class="&researchReferenceBlockClass;">
					<xsl:apply-templates select="node()[not(self::topic.key.ref or self::reference)]"/>
					<ul class="&bullListClass;">
						<xsl:apply-templates select="topic.key.ref | reference" mode="ResearchReferenceLiWrapped" />
					</ul>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="topic.key.ref | reference" mode="ResearchReferenceLiWrapped">
		<xsl:call-template name="wrapWithLi"/>
	</xsl:template>
	<!-- end: Research References / Key Number Digest References stylesheet fixes -->

	<xsl:template match="label.designator[ancestor::list and not(ancestor::form) and not(ancestor::section)]" priority="4"/>
	<xsl:template match="section//list.item/head[following-sibling::paratext]/label.designator[not(following-sibling::headtext)]"/>

	<xsl:template match="form.head/label.designator[following-sibling::form.headtext] | section//para/head/label.designator[following-sibling::headtext] | section//para/head/label.name[following-sibling::headtext]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="form.head[not(following-sibling::form.text)]/label.designator | section//para/head[not(following-sibling::paratext)]/label.designator">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="form.text[preceding-sibling::*[1][self::form.head/form.headtext]] | section//para/paratext[preceding-sibling::*[1][self::head/headtext]] | section//list.item/paratext[preceding-sibling::*[1][self::head/headtext]]">
		<xsl:call-template name="renderParagraphTextDiv">
			<xsl:with-param name="suppressLabel" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="codes.para//head/headtext" mode="para-label.designator" priority="1" name="codesParaHeadtextMode">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>
	
	<xsl:template match="codes.para/label.designator[following-sibling::paratext] | case.para/label.designator[following-sibling::paratext]"/>

	<!-- Bug #961484: Add a space before the section body when enhancements are turned off. -->
	<xsl:template match="section.body">
		<xsl:choose>
			<xsl:when test="not($IsCommentaryEnhancementMode)">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="list[list.item/para/head/label.designator or list.item/head/label.designator]" priority="1">
		<xsl:call-template name="list">
			<xsl:with-param name="class">
				<xsl:call-template name="get-enhancement-list-class"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="list/list[list.item/para/head/label.designator or list.item/head/label.designator]" priority="1">
		<xsl:call-template name="listNested">
			<xsl:with-param name="class">
				<xsl:call-template name="get-enhancement-list-class"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="get-enhancement-list-class">
		<xsl:choose>
			<xsl:when test="$IsCommentaryEnhancementMode">
				<xsl:value-of select="'&customBulletListClass;'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'&listClass;'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

  <xsl:template match="list/list.item | entity.link/list.item">
    <li>
      <xsl:if test="ancestor::list.item and not(parent::list[parent::para])">
        <xsl:attribute name="class">&paraIndentLeftClass;</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates />
      <xsl:if test="following-sibling::node()[not(self::text())][1][self::list]">
        <xsl:apply-templates select="following-sibling::node()[not(self::text())][1]" />
      </xsl:if>
    </li>
  </xsl:template>
  
	<!-- This is a duplicate template from para.xsl. It is necessary for eBook style-sheet processor. -->
	<xsl:template match="codes.para/head[following-sibling::node()[1][self::paratext] and child::label.designator]" />
	
</xsl:stylesheet>
