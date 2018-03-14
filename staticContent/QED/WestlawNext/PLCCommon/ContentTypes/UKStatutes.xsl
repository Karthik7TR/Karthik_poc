<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKStatutes.xsl" forceDefaultProduct="true"/>
	<xsl:include href="UkStatutesDocumentType.xsl"/>
	<xsl:include href="UKStatutesToc.xsl" />
	<xsl:include href="UKStatutesHeader.xsl" />
	<xsl:include href="UKStatutesStatuses.xsl" />
	<xsl:include href="PrevNextNavigation.xsl"/>
	<xsl:include href="ProvisionsTree.xsl"/>

	<xsl:param name="PrevProvisionTitle" />
	<xsl:param name="NextProvisionTitle" />

	<xsl:variable name = "contentType" select ="'&ukWestlawContent;'"/>
	<xsl:variable name = "showLoading" select="true()"/>
	<xsl:variable name="pubIdAct" select="121177"/>
	<xsl:variable name="pubIdsSi">121175,214454,214455</xsl:variable>
	<xsl:variable name="pubIdsHistoricalDoc">214337,214340</xsl:variable>
	<xsl:variable name="pageWidth" select="number(500)"/>
	<xsl:variable name="pageHeight" select="number(630)"/>
	<xsl:variable name="ContentsForFootnotesSection" select="//updatenote[not(.=preceding::updatenote) and not(../ins/update/del and not(../ins/update/ins) and not(../ins/*[name()!='update']) and not(../ins/text())) and not(ancestor::snippet-text) and not(ancestor::del)]
				|//footnote-text[not(ancestor::del) and (not(.=preceding::footnote-text))]|//footnote[not(ancestor::del) and (not(.=preceding::footnote))]
				|//mnote[not(ancestor::del) and (not(.=preceding::mnote))]
				|//amendnote[not(.=preceding::amendnote)]" />

	<!--Document structure-->
	<xsl:template name="BuildSpecificDocument">
		<xsl:choose>
			<xsl:when test="$isArrangementDocument=true()">
				<xsl:call-template name="OneColumnDocument" />
			</xsl:when>
			<xsl:when test="count(descendant::fulltext_metadata) > 0">
				<xsl:call-template name="GeneralDocument" />
			</xsl:when>
			<xsl:when test="$isKeyLegalConceptsDocument = true()">
				<xsl:call-template name="BuildKeyLegalConceptsDocument" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="BuildAnnotationDocument" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="BuildKeyLegalConceptsDocument">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&khContent; &ukWestlawContent; &keyLegalConceptsDocumentClass;'" />
			</xsl:call-template>
			<xsl:call-template name="BuildDocumentHeader"/>
			<div id="&coDocContentBody;">
				<xsl:call-template name="BuildDocumentBodyContent" />
				<xsl:call-template name="BuildCopyright" />
			</div>
		</div>
	</xsl:template>

	<xsl:template name="BuildAnnotationDocument">
		<!-- For embedded annotation documents we need just render text with simple layout -->
		<div id="&documentClass;">
			<xsl:call-template name="BuildDocumentBody" />
		</div>
	</xsl:template>
	<!--End of Document structure-->


	<xsl:template name="BuildDocumentBody">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&ukMainDocumentContent;<![CDATA[ ]]>&docDivision;<![CDATA[ ]]>&simpleContentBlockClass;</xsl:text>
			</xsl:attribute>
			<xsl:call-template name="BuildDocumentBodyContent" />
		</div>
		<a id="&ukReferencesOffset;"></a>
	</xsl:template>

	<xsl:template name="BuildDocumentBodyContent">
		<xsl:choose>
			<xsl:when test="$isArrangementDocument=true()">
				<xsl:call-template name="BuildArrangementDocumentBodyContent" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="BuildPrevNextNavigation">
					<xsl:with-param name="prevProvisionTitle" select="$PrevProvisionTitle"/>
					<xsl:with-param name="nextProvisionTitle" select="$NextProvisionTitle"/>
				</xsl:call-template>
				<xsl:apply-templates select="//fulltext_metadata/shadow-version"/>
				<xsl:apply-templates select="//fulltext_metadata//banner-text"/>
				<xsl:apply-templates select="//fulltext_metadata//bill-end"/>
				<xsl:apply-templates select="*[not(self::footnote-text or self::updatenote)]"/>
				<!-- Render the footnotes at the bottom of the document (if any) -->
				<xsl:call-template name="RenderFootnotes">
					<xsl:with-param name="footNoteTitle" select="'&notesSectionHeading;'" />
				</xsl:call-template>

				<xsl:if test ="string-length($annotationGuid) &gt; 0 and $annotationGuid != $Guid">
					<span id="&legilsationAnnotationId;" class="&hideStateClass;">
						<xsl:value-of select="$annotationGuid" />
					</span>
					<xsl:call-template name="RenderStatutoryAnnotations" />
				</xsl:if>
				<xsl:call-template name="AttachedFileForDocument" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="BuildDocumentTypeAttribute">
		<xsl:attribute name ="data-documenttype">
			<!--defined in UkStatutesToc.xsl-->
			<xsl:value-of select="$documentType"/>
		</xsl:attribute>
	</xsl:template>
				
	<xsl:template match="paragraph | section | convention | chapter | section-group | schedule | schedule-part | schedule-subpart 
						| para-group | rule | sub-annotation | article | regulation | act-part">
		<xsl:if test="number or title">
			<h4>
				<xsl:if test="title/@align = 'center' or schedule-part">
					<xsl:attribute name="class">
						<xsl:text>&centerClass;</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="number" />
				<xsl:apply-templates select="title" />
			</h4>
		</xsl:if>
		<xsl:apply-templates select="*[not(self::number or self::title)]"/>
	</xsl:template>

	<xsl:template match="title">
		<xsl:choose>
			<xsl:when test="parent::section or parent::paragraph or parent::convention or parent::chapter or parent::section-group or parent::schedule or parent::schedule-part 
							or parent::schedule-subpart or parent::para-group or parent::rule or parent::main-annotation or parent::sub-annotation or parent::article 
							or parent::form or parent::regulation or parent::act-part">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="parent::note-version">
			</xsl:when>
			<xsl:when test="parent::table">
				<h4>
					<xsl:apply-templates />
				</h4>
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="para-group/title">
		<xsl:apply-templates />
	</xsl:template>


	<xsl:template match="sub2">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>&indentLeft2Class;</xsl:text>
				<xsl:if test="not(parent::update)">
					<xsl:text> &paraMainClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="number">
		<xsl:apply-templates />
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<xsl:template match="entry[ancestor::ins]">
		<xsl:choose>
			<xsl:when test="../../parent::update and not(preceding-sibling::entry)">
				<td>
					<div class="&paraMainClass;">
						<xsl:text>[</xsl:text>
						<xsl:apply-templates />
					</div>
				</td>
			</xsl:when>
			<xsl:when test="../../parent::update and not(following-sibling::entry)">
				<td>
					<div class="&paraMainClass;">
						<xsl:apply-templates />
						<xsl:text>]</xsl:text>
						<xsl:call-template name="RenderFootnoteSuperscript" >
							<xsl:with-param name="currentFootnote">
								<xsl:apply-templates select="../../preceding-sibling::updatenote/text()|../../preceding-sibling::updatenote/child::* | preceding-sibling::updatenote/text() | preceding-sibling::updatenote/child::*"/>
							</xsl:with-param>
						</xsl:call-template>
					</div>
				</td>
			</xsl:when>
			<xsl:otherwise>
				<td>
					<div class="&paraMainClass;">
						<xsl:apply-templates />
					</div>
				</td>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--"Other Application" by default should be always be at the bottom of the various jurisdictions – England, Wales, Scotland-->
	<xsl:template match ="fulltext[1]">
		<xsl:choose>
			<xsl:when test="//n-docbody/document/fulltext/@application">
		<xsl:call-template name="BuildFulltextListContent">
			<xsl:with-param name="fulltextListSelector" select="//n-docbody/document/fulltext[@application != '&ukOtherApplicationCode;']"/>
		</xsl:call-template>
		<xsl:call-template name="BuildFulltextListContent">
			<xsl:with-param name="fulltextListSelector" select="//n-docbody/document/fulltext[@application = '&ukOtherApplicationCode;']"/>
		</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="BuildFulltextListContent">
					<xsl:with-param name="fulltextListSelector" select="//n-docbody/document/fulltext"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="BuildFulltextListContent">
		<xsl:param name="fulltextListSelector"/>
		<xsl:for-each select="$fulltextListSelector">
			<xsl:sort select="@application"/>
			<xsl:call-template name="buildFulltextContent"/>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="buildFulltextContent">
		<xsl:if test="node() or text()">
			<div class="&docDivision;">
				<a>
					<xsl:attribute name ="id">
						<xsl:call-template name="GetAnchorId">
							<xsl:with-param name="section" select ="@application"/>
						</xsl:call-template>
					</xsl:attribute>
				</a>
				<xsl:if test="count(ancestor::n-docbody/descendant::fulltext) > 1">
					<h2 class="&docHeadText;">
						<b>
							<xsl:call-template name="putJurisdictionName"/>
						</b>
					</h2>
				</xsl:if>
				<div class="&paraMainClass;">
					<xsl:if test="string-length(normalize-space(text())) &gt; 0">
						<div class="&boldClass;">
							<xsl:copy-of select="text()"/>
						</div>
					</xsl:if>
					<xsl:apply-templates select="child::node()[not(self::text())]" />
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="para-text">
		<xsl:choose>
			<xsl:when test="preceding-sibling::number and (parent::sub1 or parent::sub2 or parent::sub3 or parent::sub4)">
				<xsl:text>&#160;</xsl:text>
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="preceding-sibling::number and not(parent::sub1 or parent::sub2 or parent::sub3 or parent::sub4)">
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:when test="parent::entry and ancestor::ins">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="parent::entry and not(preceding-sibling::para-text)">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="emphasis">
		<xsl:choose>
			<xsl:when test="(ancestor::thead) or (@type='very-strong') ">
				<em>
					<b>
						<xsl:apply-templates />
					</b>
				</em>
			</xsl:when>
			<xsl:when test="@type='strong'">
				<b>
					<xsl:apply-templates />
				</b>
			</xsl:when>
			<xsl:otherwise>
				<em>
					<xsl:apply-templates />
				</em>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match ="legislation.locator">
		<!--Place holder for locator document related xsl-->
		<BR></BR>
	</xsl:template>

	<xsl:template match="form">
		<xsl:if test="number or title">
			<h4>
				<xsl:if test="title/@align = 'center'">
					<xsl:attribute name="class">
						<xsl:text>&centerClass;</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="number" />
				<xsl:apply-templates select="title" />
			</h4>
		</xsl:if>
		<xsl:apply-templates select="links"/>
		<xsl:apply-templates select="*[not(self::number or self::title or self::links)]"/>
	</xsl:template>

	<xsl:template match="links" priority="1">
		<xsl:variable name="contents">
			<xsl:choose>
				<xsl:when test="not(parent::para-text) and not(parent::definition) and (ancestor::longquote or parent::schedule)">
					<xsl:text>(</xsl:text>
					<xsl:apply-templates />
					<xsl:text>)</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="not(parent::para-text) and not(parent::definition) and (parent::form or parent::schedule)">
				<div>
					<xsl:if test="parent::form or ancestor::longquote">
						<xsl:attribute name="class">
							<xsl:text>&alignRightClass;</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:copy-of select="$contents"/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$contents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="amendnotes">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="amendnote | mnote">
		<xsl:call-template name="RenderFootnoteSuperscript" >
			<xsl:with-param name="currentFootnote">
				<xsl:apply-templates select="./text()"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sub4">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>&indentLeft4Class;</xsl:text>
				<xsl:if test="not(parent::update)">
					<xsl:text> &paraMainClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::text()">
				<div class="&paraMainClass;">&#160;</div>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!-- annotations -->
	<xsl:template name="RenderStatutoryAnnotations">
		<xsl:choose>
			<xsl:when test="not($AddStatutoryAnnotations) or not($DeliveryMode)" />
			<xsl:otherwise>
				<div id="&coStatutoryAnnotationsContainerId;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
					<div id="&coAnnotationDocumentId;">
						<xsl:apply-templates select="//annotation" />
					</div>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="note-reference/link">
		<xsl:variable name="documentGuid" select="@tuuid"/>
		<a class="&keyLegalConceptsLinkClass;" role="button">
			<xsl:attribute name="href">
				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.Glossary.Guid', concat('docGuid=',$documentGuid), '&glossaryTypeParamName;=&glossaryTypeUkKeyLegalConcepts;', '&transitionTypeParamName;=&transitionTypeDocumentItem;', 'contextData=(sc.Default)')"/>
			</xsl:attribute>
			<xsl:attribute name="data-key-legal-concept">
				<xsl:value-of select="@tuuid"/>
			</xsl:attribute>
			<xsl:value-of select="./cite.query"/>
		</a>
	</xsl:template>

	<xsl:template match="main-annotation">
		<xsl:apply-templates select="derivations"/>
		<h4>
			<xsl:apply-templates select="number" />
			<xsl:apply-templates select="title" />
		</h4>
		<xsl:apply-templates select="*[not(self::number or self::title or self::derivations)]"/>
	</xsl:template>

	<xsl:template match="annotation-version">
		<div class="&docDivision;">
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<a id="&coLegislationKeyConceptLinkId;" class="&floatRight;">
						<xsl:attribute name="href">
							<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.Glossary', '&glossaryTypeParamName;=&glossaryTypeUkKeyLegalConcepts;', '&transitionTypeParamName;=&transitionTypeDocumentItem;', 'contextData=(sc.Default)')"/>
						</xsl:attribute>
						<xsl:text>&keyConceptsLibraryText;</xsl:text>
					</a>
					<p>&nbsp;</p>
					<h2>&annotationSectionHeading;</h2>
					<p>&nbsp;</p>
					<div id="&coAnnotationDocumentSourceId;">
						<h3>
							<xsl:value-of select="title" />
							<p>&nbsp;</p>
						</h3>
						<xsl:apply-templates select="main-annotation" />
						<xsl:apply-templates select="sub-annotation" />
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div id="&coAnnotationDocumentSourceId;">
						<h3>
							<xsl:value-of select="title" />
						</h3>
						<xsl:apply-templates select="main-annotation" />
						<xsl:apply-templates select="sub-annotation" />
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="derivations">
		<xsl:choose>
			<xsl:when test="ancestor::main-annotation">
				<div class="&paraMainClass;">
					<h4>&derivationOfSectionText;</h4>
					<div>
						<xsl:apply-templates />
					</div>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="derivation">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="sub-annotation">
		<h4>
			<xsl:apply-templates select="number" />
			<xsl:apply-templates select="title" />
		</h4>
			<xsl:apply-templates select="*[not(self::number or self::title)]"/>
	</xsl:template>

	<xsl:template match="definition">
		<xsl:choose>
			<xsl:when test="ancestor::definitions">
				<div class="&paraMainClass;">
					<xsl:value-of select="./term-name"/>
					<xsl:text> &mdash; </xsl:text>
					<xsl:apply-templates select="*[not(self::term-name)]"/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="quote">
		<xsl:choose>
			<xsl:when test="ancestor::annotation">
				<blockquote>
					<xsl:apply-templates />
				</blockquote>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<!--Renaming the footnotes to Amendments-->
	<xsl:template name="RenderFootnoteSectionMarkupDiv">
		<xsl:param name="footNoteTitle" />
		<xsl:param name="contents"/>
		<div id="&footnoteSectionId;" class="&footnoteSectionClass; &docDivision;">
			<a>
				<xsl:attribute name ="id">
					<xsl:call-template name="GetAnchorId">
						<xsl:with-param name="section" select ="'&notesSecondaryMenu;'"/>
					</xsl:call-template>
				</xsl:attribute>
			</a>
			<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
				<xsl:value-of select="$footNoteTitle" />
			</h2>
			<xsl:copy-of select="$contents"/>
		</div>
	</xsl:template>

	<xsl:template match="shadow-version">
		<xsl:if test="contains(text(), '|')">
			<div class="&paraMainClass; &billShadowVersionClass;">
				<xsl:value-of select="substring-before(text(), '|')"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="banner-text">
		<div class="&paraMainClass; &billShadowVersionClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="bill-end">
		<div class="&paraMainClass; &billEndClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="presented | ordered">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- images -->
	<xsl:template match="figure">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="figure[not(descendant::link)]">
		<div class="&centerClass;">
			<xsl:text>&figureNotAvailableText;</xsl:text>
		</div>
	</xsl:template>

	<xsl:template match="link[parent::form or parent::graphic]">
		<xsl:if test="string-length(@tuuid) &gt; 0">
			<xsl:variable name="guid" select="@tuuid"/>
			<xsl:variable name="imageInfo" select="/*/ImageMetadata/n-metadata[@guid = $guid]"/>
			<xsl:variable name="blobHref">
				<xsl:call-template name="createBlobLink">
					<xsl:with-param name="guid" select="$guid"/>
					<xsl:with-param name="targetType" select="@ttype"/>
					<xsl:with-param name="mimeType" select="'&xPngMimeType;'"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="dpi" select="$imageInfo//md.image.dpi"/>
			<xsl:variable name="height" select="$imageInfo//md.image.height"/>
			<xsl:variable name="width" select="$imageInfo//md.image.width"/>
			<xsl:variable name="imageHeightAfterScaling">
				<xsl:call-template name="CalculateMaxImageHeightOrWidth">
					<xsl:with-param name="imageWidth" select="$width"/>
					<xsl:with-param name="imageHeight" select="$height"/>
					<xsl:with-param name="dpi" select="$dpi"/>
					<xsl:with-param name="pageWidth">
						<xsl:if test="$DeliveryMode">
							<xsl:value-of select="$pageWidth"/>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="pageHeight">
						<xsl:if test="$DeliveryMode">
							<xsl:value-of select="$pageHeight"/>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="returnDimension" select="'height'" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="imageWidthAfterScaling">
				<xsl:call-template name="CalculateMaxImageHeightOrWidth">
					<xsl:with-param name="imageWidth" select="$width"/>
					<xsl:with-param name="imageHeight" select="$height"/>
					<xsl:with-param name="dpi" select="$dpi"/>
					<xsl:with-param name="pageWidth">
						<xsl:if test="$DeliveryMode">
							<xsl:value-of select="$pageWidth"/>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="pageHeight">
						<xsl:if test="$DeliveryMode">
							<xsl:value-of select="$pageHeight"/>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="returnDimension" select="'width'" />
				</xsl:call-template>
			</xsl:variable>
			<div>
				<a class="&imageLinkClass;" type="&xPngMimeType;">
					<xsl:if test="string-length($blobHref) &gt; 0">
						<xsl:attribute name="href">
							<xsl:value-of select="$blobHref"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="buildBlobImageElement">
						<xsl:with-param name="class" select="'&imageClass;'"/>
						<xsl:with-param name="src" select="$blobHref"/>
						<xsl:with-param name="width" select="$imageWidthAfterScaling"/>
						<xsl:with-param name="height" select="$imageHeightAfterScaling"/>
					</xsl:call-template>
				</a>
			</div>
		</xsl:if>
	</xsl:template>
	<!-- end images-->

	<xsl:template match="subscript">
		<sub>
			<xsl:apply-templates/>
		</sub>
	</xsl:template>

	<xsl:template match="superscript">
		<xsl:choose>
			<xsl:when test="ancestor::note-version">
				<p>&nbsp;</p>
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<sup>
					<xsl:apply-templates/>
				</sup>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--list-->
	<xsl:template match="defnlist">
		<div class="&paraMainClass; &paraIndentLeftClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template name="listItemsDisplay">
		<xsl:for-each select="item">
			<li>
				<xsl:apply-templates/>
			</li>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="list[@prefix-rules='unordered']">
		<ul class="&paraMainClass; &docUnorderedList;">
			<xsl:call-template name="listItemsDisplay"/>
		</ul>
	</xsl:template>

	<xsl:template match="list[@prefix-rules='none']">
		<ul class="&paraMainClass;">
			<xsl:call-template name="listItemsDisplay"/>
		</ul>
	</xsl:template>

	<xsl:template match="list[@prefix-rules='ordered']">
		<xsl:element name="ol">
			<xsl:attribute name="class">
				<xsl:text>&paraMainClass; &decimalListClass;</xsl:text>
				<xsl:if test="not($isKeyLegalConceptsDocument)">
					<xsl:text><![CDATA[ ]]>&indentLeft2Class;</xsl:text>
				</xsl:if>
				<xsl:if test="$DeliveryMode">
					<xsl:text><![CDATA[ ]]>&khList;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:call-template name="listItemsDisplay"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="list[@prefix-rules='specified']">
		<ul class="&paraMainClass;">
			<xsl:for-each select="item">
				<li>
					<xsl:if test="not($DeliveryMode and $isKeyLegalConceptsDocument)">
						<xsl:value-of select="./@prefix"/>
						<xsl:text>&nbsp;&nbsp;</xsl:text>
					</xsl:if>
					<xsl:apply-templates/>
				</li>
			</xsl:for-each>
		</ul>
	</xsl:template>

	<xsl:template match="term-defined">
		<i>
			<xsl:apply-templates />
			<xsl:text>&#160;</xsl:text>
		</i>
	</xsl:template>

	<xsl:template match="defnlist-item">
		<xsl:choose>
			<xsl:when test="parent::ins">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass; &paraIndentLeftClass;">
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="item/para">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and $isKeyLegalConceptsDocument">
				<div class="&paraMainClass;">
					<xsl:if test="../@prefix">
						<xsl:value-of select="../@prefix"/>
						<xsl:text>&nbsp;&nbsp;</xsl:text>
					</xsl:if>
					<xsl:apply-templates/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="item">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and $isKeyLegalConceptsDocument">
				<xsl:apply-templates />
				<xsl:if test="following-sibling::node()[1][self::list]">
					<xsl:apply-templates select="following-sibling::node()[1]" />
				</xsl:if>
			</xsl:when>
			<xsl:when test="$DeliveryMode">
				<div>
					<xsl:apply-templates />
					<xsl:if test="following-sibling::node()[1][self::list]">
						<xsl:apply-templates select="following-sibling::node()[1]" />
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<li>
					<xsl:choose>
						<xsl:when test="following-sibling::node()[1][self::list]">
							<xsl:apply-templates select="following-sibling::node()[1]" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates />
						</xsl:otherwise>
					</xsl:choose>
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="BuildEndOfDocument">
		<xsl:call-template name="EmptyEndOfDocument">
			<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="n-docbody/document">
		<xsl:apply-templates select="*[not(self::fulltext_metadata or self::metadata.block or self::para[parent::note-version][1])]"/>
	</xsl:template>

	<xsl:template name="AttachedFileForDocument">
		<xsl:if test="//image.block">

			<xsl:variable name="mdPubId" select="//metadata.block//md.pubid"/>
			<xsl:variable name="wPubNumber" select="//heading[@rank='1']//cite.query/@w-pub-number"/>

			<xsl:variable name="contents">
				<xsl:choose>
					<xsl:when test="($pubIdAct = $mdPubId) or ($pubIdAct = $wPubNumber)">
						<xsl:value-of select="'&viewPdfOfEntireActText;'"/>
					</xsl:when>
					<xsl:when test="contains($pubIdsSi, $mdPubId) or contains($pubIdsSi, $wPubNumber)">
						<xsl:value-of select="'&viewPdfOfEntireSIText;'"/>
					</xsl:when>
					<xsl:when test="contains($pubIdsHistoricalDoc, $mdPubId) or contains($pubIdsHistoricalDoc, $wPubNumber)">
						<xsl:value-of select="'&viewPdfOfHistoricalDocText;'"/>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>

			<div class="&standardDocAttachment; &hideState;">
				<xsl:call-template name="createDocumentBlobLink">
					<xsl:with-param name="guid" select="//image.block/image.link/@tuuid"/>
					<xsl:with-param name="targetType" select="'&inlineParagraph;'"/>
					<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>

	<!--metadata-->
	<xsl:template name="BuildMetaInfoColumnContent">
		<xsl:if test="$isProvisionDocument=true()">
			<xsl:if test="not($statusCode = 'U' or $statusCode = 'X')">
				<xsl:call-template name="DocumentStatusDetailed">
					<xsl:with-param name="statusCode" select="$statusCode"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$statusCode != 'V'">
				<xsl:call-template name="docViewBillAmendedVersion"/>
				<xsl:call-template name="docVersion"/>
				<xsl:call-template name="docStatusDate"/>
				<xsl:call-template name="docVersionsList"/>
				<xsl:call-template name="docSubjects"/>
				<xsl:call-template name="docKeywords"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="bill/type">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template name="docVersion">
		<xsl:if test="//fulltext_metadata/commencement/navigator">
			<xsl:call-template name="BuildMetaField">
				<xsl:with-param name="fieldClass" select="'&metaVersion;'"/>
				<xsl:with-param name="fieldContent">
					<xsl:text>&ukVersion;<![CDATA[ ]]></xsl:text>
					<xsl:value-of select="//fulltext_metadata/commencement/navigator/@version"/>
					<xsl:text><![CDATA[ ]]>&ukOf;<![CDATA[ ]]></xsl:text>
					<xsl:value-of select="//fulltext_metadata/commencement/navigator/@versions"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="docStatusDate">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaDateProvision;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="descendant::fulltext_metadata/commencement"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="commencement">
		<xsl:call-template name="docStartDate">
			<xsl:with-param name="startDate" select="start-date" />
			<xsl:with-param name="type" select="@type" />
		</xsl:call-template>

		<xsl:if test ="start-date and $statusCode != 'R' and ((end-date and not(starts-with(end-date, '2099'))) 
			or (@type='lif' or @type='sif' or @type='sif_dateknown' or @type='partial'))">
			<xsl:text> &ukTo; </xsl:text>
		</xsl:if>

		<xsl:call-template name="docEndDate">
			<xsl:with-param name="startDate" select="start-date" />
			<xsl:with-param name="endDate" select="end-date" />
			<xsl:with-param name="type" select="@type" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="docStartDate">
		<xsl:param name="startDate" />
		<xsl:param name="type" />
		<xsl:choose>
			<xsl:when test="not($startDate) and $type='lif'" />
			<xsl:when test="$type='sif'">
				<xsl:text>&ukDateNotAvailable;</xsl:text>
			</xsl:when>
			<xsl:when test ="not($startDate) or starts-with($startDate, '2099')">
				<xsl:text>&ukDateToBeAppointedText;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="$startDate"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="docEndDate">
		<xsl:param name="startDate" />
		<xsl:param name="endDate" />
		<xsl:param name="type" />
		<xsl:choose>
			<xsl:when test ="$statusCode='R' or (not($startDate) and not($endDate)) or ($type='nyif' and starts-with($endDate, '2099'))"/>
			<xsl:when test="starts-with($endDate, '2099') and not($type='lif')">
				<xsl:text>&ukDateToBeAppointedText;</xsl:text>
			</xsl:when>
			<xsl:when test="($type='basic' or ($type='sif' and $endDate) or ($type='sif_dateknown' and $endDate) or ($type='nyif' and $endDate)) and $startDate">
				<xsl:apply-templates select="$endDate"/>
			</xsl:when>
			<xsl:when test="$type='lif' or $type='sif' or $type='sif_dateknown' or $type='partial'">
				<xsl:text>&ukPresentText;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="$endDate"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="docVersionsList">
		<xsl:variable name="currentVersion" select="//fulltext_metadata/commencement/navigator/@version"/>
		<xsl:if test="n-docbody/document/fulltext_metadata/version_list">
			<table class="&metaProvisionVersionsTable; &hideStateClass; &excludeFromAnnotationsClass;">
				<tbody>
					<xsl:for-each select="n-docbody/document/fulltext_metadata/version_list/version">
						<xsl:sort select="@number" data-type="number" order="descending"/>
						<tr>
							<xsl:if test="@number = $currentVersion">
								<xsl:attribute name="class">
									<xsl:text>&metaCurrentProvisionVersion;</xsl:text>
								</xsl:attribute>
							</xsl:if>
							<td>
								<a>
									<xsl:attribute name="href">
										<xsl:call-template name="GetDocumentUrl">
											<xsl:with-param name="documentGuid" select="link/@tuuid" />
										</xsl:call-template>
									</xsl:attribute>
									<xsl:value-of select="@number"/>
								</a>
							</td>
							<td>
								<xsl:call-template name="docStartDate">
									<xsl:with-param name="startDate" select="version-start-date" />
									<xsl:with-param name="type" select="@type" />
								</xsl:call-template>
							</td>
							<td>
								<xsl:call-template name="docEndDate">
									<xsl:with-param name="startDate" select="version-start-date" />
									<xsl:with-param name="endDate" select="version-end-date" />
									<xsl:with-param name="type" select="@type" />
								</xsl:call-template>
							</td>
						</tr>
					</xsl:for-each>
				</tbody>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="version-start-date | version-end-date | start-date | end-date">
		<xsl:call-template name="formatYearMonthDayToDDMMMYYYY">
			<xsl:with-param name="date" select="."/>
			<xsl:with-param name="dayFormatWithZero" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="docSubjects">
		<xsl:param name="caption" select ="'&ukSubjects;'" />
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaMainSubject;'"/>
			<xsl:with-param name="fieldCaption" select="$caption"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="n-docbody/document/metadata.block/md.subjects/md.subject/md.keyphrases/md.keyphrase">
					<xsl:sort select="."/>
					<xsl:apply-templates/>
					<xsl:if test="position()!=last()">
						<xsl:text>; </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="docKeywords">
		<xsl:param name="caption" select ="'&euKeywords;'" />
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaKeywords;'"/>
			<xsl:with-param name="fieldCaption" select="$caption"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="n-docbody/document/metadata.block/md.subjects/md.subject/md.keywords/md.keyword">
					<xsl:sort select="."/>
					<xsl:apply-templates/>
					<xsl:if test="position()!=last()">
						<xsl:text>; </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="docViewBillAmendedVersion">
		<xsl:variable name="guid" select="n-docbody/document/fulltext_metadata/shadow-version/link/@tuuid"/>

		<xsl:if test="$guid">
			<xsl:call-template name="BuildMetaField">
				<xsl:with-param name="fieldClass" select="'&metaViewBillAmendedVersion;'"/>
				<xsl:with-param name="fieldContent">
					<a>
						<xsl:attribute name="href">
							<xsl:call-template name="GetDocumentUrl">
								<xsl:with-param name="documentGuid" select="$guid"/>
							</xsl:call-template>
						</xsl:attribute>
						<xsl:value-of select="'&ukViewBillAmendedVersionText;'"/>
					</a>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BuildArrangementDocumentBodyContent">
		<xsl:call-template name="BuildPrevNextNavigation">
			<xsl:with-param name="prevProvisionTitle" select="$PrevProvisionTitle"/>
			<xsl:with-param name="nextProvisionTitle" select="$NextProvisionTitle"/>
		</xsl:call-template>
		<xsl:if test="$documentType=concat('&arrangmentOfPrimaryMenu;','&legisTypeBill;')">
			<xsl:apply-templates select="//fulltext_metadata//banner-text"/>
		</xsl:if>
		<xsl:if test="$isArrangmentOfProvisions">
			<xsl:apply-templates select="//fulltext/*[not(self::explanatory-note)]" />
		</xsl:if>
		<xsl:call-template name="BuildProvisionsTree" />
		<!-- Render the footnotes at the bottom of the document (if any) -->
		<xsl:call-template name="RenderFootnotes">
			<xsl:with-param name="footNoteTitle" select="'&notesSectionHeading;'" />
		</xsl:call-template>
		<xsl:call-template name="AttachedFileForDocument" />
		<div class="&paraMainClass;">
			<xsl:call-template name="docSubjects" />
		</div>
		<div class="&paraMainClass;">
			<xsl:call-template name="docKeywords" />
		</div>
	</xsl:template>

	


	<!--signee block-->
	<xsl:template match="signee">
		<div class="&paraMainClass;">
			<div class="&paraMainClass;">
				<xsl:value-of select ="./signee-text" />
		</div>
		<div class="&paraMainClass;">
			<em>
					<xsl:value-of select ="./signee-name" />
			</em>
		</div>
			<div class="&paraMainClass;">
				<xsl:value-of select ="./signee-title" />
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:value-of select ="./signee-department" />
			</div>
		<div class="&paraMainClass;">
				<xsl:value-of select ="./date-signed" />
			</div>
		</div>
	</xsl:template>
	<!--end signee-->

	<!-- Dates -->
	<xsl:template match="dates">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="made|laid|in-force-text|scotlaid|sub-in-force-text">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates />
			<xsl:if test="@date != '00000000'">
				<xsl:text>: </xsl:text>
				<xsl:call-template name="formatDate">
					<xsl:with-param name="dateTime" select="@date" />
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="parent::in-force/@date != '00000000'">
				<xsl:text>: </xsl:text>
				<xsl:call-template name="formatDate">
					<xsl:with-param name="dateTime" select="parent::in-force/@date" />
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:template>
	<!-- End Dates -->

	<!-- Remove these -->
	<xsl:template match="fulltext"/>
	<xsl:template match="modifications"/>
	<xsl:template match="schedule/number[not(ancestor::longquote)] | schedule/title[not(ancestor::longquote)]"/>
	<xsl:template match="annotation-document"/>

</xsl:stylesheet>
