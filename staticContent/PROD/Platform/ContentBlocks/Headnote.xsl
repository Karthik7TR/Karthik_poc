<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="TopicKeyCleaner.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="headnote.block | keysummary.block">
		<xsl:if test="not($HeadnoteDisplayOption = '&noKeyNumbers;')">
			<div id="&headnotesId;">
				<xsl:attribute name="class">
					<xsl:text>&headnotesClass;</xsl:text>
					<!-- Go single column unless there are search terms in the topics -->
					<xsl:if test="not(./descendant::topic.key.hierarchy/descendant::N-HIT[1] or ./descendant::topic.key.hierarchy/descendant::N-LOCATE[1] or ./descendant::topic.key.hierarchy/descendant::N-WITHIN[1]) or $DeliveryMode">
						<xsl:text><![CDATA[ ]]>&fancyHeadnotesClass;</xsl:text>
					</xsl:if>
					<!-- Check for any search terms -->
					<xsl:if test="./descendant::N-HIT[1] or ./descendant::N-LOCATE[1] or ./descendant::N-WITHIN[1]">
						<xsl:text><![CDATA[ ]]>&containsSearchTermsClass;</xsl:text>
					</xsl:if>
				</xsl:attribute>
				<h2 id="&headnoteHeaderId;" class="&headnoteHeaderClass; &printHeadingClass;">
					<span class="&headnoteHeaderSpanClass;">
						<xsl:choose>
							<xsl:when test="self::headnote.block">
                <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&westHeadnotesTextKey;', '&westHeadnotesText;')"/>
								<xsl:text><![CDATA[ ]]></xsl:text>
								<xsl:value-of select="concat('(', count(expanded.headnote), ')')"/>
							</xsl:when>
							<xsl:when test="self::keysummary.block">
								<xsl:text>&westKeySummaryText;</xsl:text>
							</xsl:when>
						</xsl:choose>
					</span>
				</h2>
				<div class="&headnotesContentContainerClass;">
					<div id="&expandedHeadnotesId;">
						<xsl:apply-templates select="expanded.headnote | expanded.keysummary"/>
					</div>
				</div>
				<div>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</div>
			</div>
			<xsl:processing-instruction name="chunkMarker"/>
			<a id="&endOfHeadnotesMarkerId;" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="headnote.block/expanded.headnote | keysummary.block/expanded.keysummary">
		<xsl:variable name="headnoteNumber">
			<xsl:apply-templates select="headnote.number | keysummary.number"/>
		</xsl:variable>

		<xsl:call-template name="startUnchunkableBlock" />

		<div class="&headnoteRowClass;">
			<xsl:if test="(headnote.number or keysummary.number) and node()[self::headnote.number or self::keysummary.number]/@chdid">
				<xsl:variable name="headnoteId" select="substring(node()[self::headnote.number or self::keysummary.number]/@chdid, 1, 13)"/>
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', $headnoteId)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:variable name="headnoteNumberId" select="./@ID"/>
			<div class="&headnoteCellClass;">
				<div class="&headnoteCellInnerClass;">
					<xsl:choose>
						<xsl:when test="$DeliveryMode">
							<!-- The table display is used only for Delivery! -->
							<xsl:call-template name="RenderHeadnoteAsTable">
								<xsl:with-param name="headnoteNumber" select="$headnoteNumber" />
								<xsl:with-param name="headnoteId" select="$headnoteNumberId" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<!-- This is the normal display online -->
							<xsl:apply-templates select="expanded.classification" mode="enhancedKeyTopic">
								<xsl:with-param name="headnoteNumber" select="$headnoteNumber"/>
								<xsl:with-param name="headnoteId" select="$headnoteNumberId" />
							</xsl:apply-templates>
							<xsl:apply-templates select="headnote | keysummary">
								<xsl:with-param name="headnoteNumber" select="$headnoteNumber"/>
							</xsl:apply-templates>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</div>
			<xsl:if test="not($DeliveryMode)">
				<xsl:call-template name="HeadNoteTopics" />
			</xsl:if>
			<div class="&clearClass;"></div>
		</div>

		<xsl:call-template name="endUnchunkableBlock" />

		<xsl:if test="$AllowHeadnotesToChunk">
			<xsl:processing-instruction name="chunkMarker"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderHeadnoteAsTable">
		<xsl:param name="headnoteNumber" />
		<xsl:param name="headnoteId" />
		<table id="&headnotesTableId;" class="&deliveryHeadnotesTableClass;">
			<!-- Collapsed View -->
			<tr>
				<td class="&deliveryHeadnotesTableLeftClass;">
					<xsl:call-template name="getHeadnoteNumber">
						<xsl:with-param name="headnoteNumber" select="$headnoteNumber"/>
						<xsl:with-param name="isNotFirstTopicKeyHierarchy" select="false()"/>
						<xsl:with-param name="headnoteId" select="$headnoteId" />
					</xsl:call-template>
				</td>
				<td class="&deliveryHeadnotesTableRightClass;">
					<xsl:apply-templates select="expanded.classification" mode="enhancedKeyTopic">
						<xsl:with-param name="headnoteNumber" select="$headnoteNumber"/>
					</xsl:apply-templates>
				</td>
			</tr>
			<!-- Expanded View -->
			<xsl:if test="$HeadnoteDisplayOption = '&expandedKeyNumbers;'">
				<tr>
					<xsl:call-template name="HeadNoteTopics" />
				</tr>
			</xsl:if>
			<!-- Body View -->
			<tr>
				<xsl:apply-templates select="headnote | keysummary">
					<xsl:with-param name="headnoteNumber" select="$headnoteNumber"/>
				</xsl:apply-templates>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="HeadNoteTopics">
		<xsl:variable name="expandedClassification">
			<xsl:apply-templates select="expanded.classification" mode="topicKey" />
		</xsl:variable>
		<!-- Create the empty column for delivery -->
		<xsl:call-template name="wrapWithTableCellIfDeliveryMode">
			<xsl:with-param name="class" select="'&deliveryHeadnotesTableLeftClass;'"/>
		</xsl:call-template>
		<!-- Create the second table column if delivery otherwise create a div for online -->
		<xsl:call-template name="wrapWithTableCellIfDeliveryMode">
			<xsl:with-param name="class" select="'&deliveryHeadnotesTableRightClass;'"/>
			<xsl:with-param name="contents">
				<div class="&headnoteTopicsCellClass;">
					<xsl:choose>
						<xsl:when test="string-length($expandedClassification) &gt; 0">
							<xsl:copy-of select="$expandedClassification"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="headnote | keysummary">
		<xsl:param name="headnoteNumber" />
		<xsl:variable name="persistentUrl">
			<xsl:call-template name="createHeadnotesCitedCaseRefLink">
				<xsl:with-param name="guid" select="$Guid" />
				<xsl:with-param name="headnoteId" select="@chdid" />
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
				<xsl:with-param name="docSource" select="$DocSource"/>
				<xsl:with-param name="rank" select="$Rank"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- Create the empty column for delivery -->
		<xsl:call-template name="wrapWithTableCellIfDeliveryMode">
			<xsl:with-param name="class" select="'&deliveryHeadnotesTableLeftClass;'"/>
		</xsl:call-template>
		<!-- Create the second table column if delivery otherwise create a div for online -->
		<xsl:call-template name="wrapWithTableCellIfDeliveryMode">
			<xsl:with-param name="class" select="'&deliveryHeadnotesTableRightClass;'"/>
			<xsl:with-param name="contents">
				<div class="&headnoteClass;">
					<xsl:apply-templates select="headnote.body | keysummary.body" />

					<xsl:apply-templates select="parent::expanded.headnote/library.reference" />

					<xsl:if test="string-length($persistentUrl) &gt; 0">
						<div>
							<xsl:attribute name="id">
								<xsl:text>&headnoteCitedCaseRefPrefix;</xsl:text>
								<xsl:value-of select="@chdid"/>
							</xsl:attribute>
							<xsl:attribute name="class">
								<xsl:text>&headnoteCitedCaseRefClass;</xsl:text>
							</xsl:attribute>
							<a>
								<xsl:attribute name="id">
									<xsl:value-of select="concat('&linkIdPrefix;', generate-id(.))"/>
								</xsl:attribute>
								<xsl:attribute name="class">
									<xsl:text>&linkClass; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;</xsl:text>
								</xsl:attribute>
								<xsl:attribute name="href">
									<xsl:copy-of select="$persistentUrl"/>
								</xsl:attribute>
								<xsl:if test="string-length($headnoteNumber) &gt; 0">
									<xsl:attribute name="title">
										<xsl:value-of select="concat('&casesThatCiteHeadNoteX;', $headnoteNumber)"/>
									</xsl:attribute>
								</xsl:if>
								<xsl:text>&casesThatCiteToThisHeadNote;</xsl:text>
							</a>
						</div>
					</xsl:if>
					<xsl:if test="$DisplayInternalHeadnoteInfo">
						<xsl:call-template name="headnotePublicationBlock">
							<xsl:with-param name="headnotePublicationBlockContent" 
								select="../../../..//headnote.publication.block | ../../../..//keysummary.publication.block"/>
              <xsl:with-param name="metadata" select="/Document/n-metadata/metadata.block"/>
              <xsl:with-param name="headnoteBlock" select="../../../..//headnote.block | ../../../..//keysummary.block"/>
						</xsl:call-template>
					</xsl:if>
				</div>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="expanded.classification" mode="enhancedKeyTopic">
		<!-- 
		1.  display first topic.key's keytext
		2.  display the key symbol
		3.  display last topic.key's keytext (after removing the "k. " at the beginning and the "." at the end) that is not a prohibited phrase
		-->
		<xsl:param name="headnoteNumber" />
		<xsl:param name="headnoteId" />

    <xsl:if test="$DisplayInternalHeadnoteInfo and not($DeliveryMode)">
      <xsl:call-template name="renderKeyHierarchy">
        <xsl:with-param name="generateRefKeyTable" select="true()"/>
        <xsl:with-param name="generateTopicKeyHierarchy" select="true()"/>
        <xsl:with-param name="generateKeyIconImage" select="false()"/>
      </xsl:call-template>
    </xsl:if>

		<xsl:variable name="topicContents">
			<xsl:apply-templates select="./topic.key.hierarchy/topic.key[1]/keytext"/>
		</xsl:variable>

		<xsl:variable name="keyNumberTextContents">
			<xsl:call-template name="findLastUnprohibitedKeyText"/>
		</xsl:variable>

		<xsl:variable name="isNotFirstTopicKeyHierarchy" select="preceding-sibling::expanded.classification/topic.key.hierarchy"/>

		<xsl:if test="string-length($topicContents) &gt; 0">
			<div>
				<xsl:attribute name="class">
					<xsl:choose>
						<xsl:when test="$DeliveryMode">
							<xsl:text>&deliveryHeadnoteNodesClass;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="$isNotFirstTopicKeyHierarchy">
									<xsl:text>&secondaryHeadnoteNodesClass;</xsl:text>
								</xsl:when>
								<xsl:when test="string-length($headnoteNumber) = 0">
									<xsl:text>&secondaryHeadnoteNodesClass;</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>&primaryHeadnoteNodesClass;</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test="not($DeliveryMode)">
					<xsl:call-template name="getHeadnoteNumber">
						<xsl:with-param name="headnoteNumber" select="$headnoteNumber"/>
						<xsl:with-param name="isNotFirstTopicKeyHierarchy" select="$isNotFirstTopicKeyHierarchy"/>
						<xsl:with-param name="headnoteId" select="$headnoteId" />
					</xsl:call-template>
				</xsl:if>
				<span class="&headnoteNodeClass;">
					<xsl:if test="preceding-sibling::expanded.classification/topic.key.hierarchy/topic.key[1]/keytext[normalize-space(.) = normalize-space($topicContents)]">
						<xsl:attribute name="class">
							<xsl:text>&hideTopicContentsClass;</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<strong>
						<xsl:call-template name="getHeadnoteTopicUrl">
							<xsl:with-param name="citeQueryElement" select="./topic.key.hierarchy/topic.key[1]/key/cite.query" />
							<xsl:with-param name="text" select="$topicContents" />
						</xsl:call-template>
					</strong>
				</span>
				<div class="&keyIconClass; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
					<!-- Place hierarchy here -->
					<xsl:choose>
						<xsl:when test="$DeliveryMode">
							<xsl:apply-templates select="topic.key.hierarchy">
								<xsl:with-param name="generateRefKeyTable" select="false()"/>
								<xsl:with-param name="generateTopicKeyHierarchy" select="false()"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="topic.key.hierarchy">
								<xsl:with-param name="generateRefKeyTable" select="false()"/>
							</xsl:apply-templates>
						</xsl:otherwise>
					</xsl:choose>
				</div>
				<xsl:if test="string-length($keyNumberTextContents) &gt; 0">
					<span class="&lastKeyTextClass;">
						<xsl:copy-of select="$keyNumberTextContents"/>
					</span>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="findLastUnprohibitedKeyText">
		<xsl:apply-templates select="descendant::topic.key.ref" mode="findLastUnprohibitedKey" />
	</xsl:template>

	<xsl:template match="topic.key | topic.key.ref" mode="findLastUnprohibitedKey">
		<xsl:variable name="cleanedKeyText">
			<xsl:apply-templates select="./keytext" />
		</xsl:variable>
		<xsl:if test="string-length($cleanedKeyText) &gt; 0">
			<xsl:choose>
				<!-- When this key's keytext (minus punctuation and casing) is NOT exactly equal to any of the prohibited keytext values -->
				<xsl:when test="translate($cleanedKeyText, '&alphabetUppercase;.,;:', '&alphabetLowercase;') != translate('&prohibitedPhrase1;', '&alphabetUppercase;.,;:', '&alphabetLowercase;')
										and translate($cleanedKeyText, '&alphabetUppercase;.,;:', '&alphabetLowercase;') != translate('&prohibitedPhrase2;', '&alphabetUppercase;.,;:', '&alphabetLowercase;')
										and translate($cleanedKeyText, '&alphabetUppercase;.,;:', '&alphabetLowercase;') != translate('&prohibitedPhrase3;', '&alphabetUppercase;.,;:', '&alphabetLowercase;')
										and translate($cleanedKeyText, '&alphabetUppercase;.,;:', '&alphabetLowercase;') != translate('&prohibitedPhrase4;', '&alphabetUppercase;.,;:', '&alphabetLowercase;')">
					<xsl:call-template name="getHeadnoteTopicUrl">
						<xsl:with-param name="citeQueryElement" select="./key/cite.query" />
						<xsl:with-param name="text" select="$cleanedKeyText" />
					</xsl:call-template>
				</xsl:when>
				<!-- Otherwise when there are additional keytext elements in ancestor keys -->
				<xsl:when test="ancestor::topic.key[keytext]">
					<xsl:apply-templates select="ancestor::topic.key[keytext][1]" mode="findLastUnprohibitedKey" />
				</xsl:when>
				<!-- Otherwise if this is the most "ancestored" key with keytext, just use it -->
				<xsl:otherwise>
					<xsl:call-template name="getHeadnoteTopicUrl">
						<xsl:with-param name="citeQueryElement" select="./key/cite.query" />
						<xsl:with-param name="text" select="$cleanedKeyText" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getHeadnoteTopicUrl">
		<xsl:param name="citeQueryElement"/>
		<xsl:param name="text"/>
		<xsl:variable name="url">
			<xsl:call-template name="createHeadnotesTopicLink">
				<xsl:with-param name="citeQueryElement" select="$citeQueryElement"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="string-length($url) &gt; 0">
				<a>
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&linkIdPrefix;', generate-id(.))"/>
					</xsl:attribute>
					<xsl:attribute name="class">
						<xsl:text>&linkClass;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="$url"/>
					</xsl:attribute>
					<xsl:copy-of select="$text"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- start intentionally do nothing -->
	<xsl:template match="expanded.classification"/>
	<xsl:template match="expanded.classification/classification"/>
	<xsl:template match="headnote.block.head | keysummary.block.head"/>
	<!-- end intentionally do nothing -->



	<!--  START - THINGS NOT PORTED OVER  -->


	<xsl:template match="expanded.classification" mode="topicKey">
		<xsl:apply-templates select="topic.key.hierarchy">
			<xsl:with-param name="generateKeyIconImage" select="not($DeliveryMode)"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="topic.hierarchy/topic/topic.line"/>
	</xsl:template>

	<xsl:template match="expanded.classification/prior.classification">
		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class" select="'&headnotePriorClassificationClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Also used in tribal.court.headnote.block -->
	<xsl:template match="topic.line">
		<span class="&topicLineClass;">
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<xsl:template match="headnote.reference">
		<xsl:variable name="prevSiblingText" select="normalize-space(preceding-sibling::text()[1])"/>
		<xsl:variable name="precedingLastChar" select="substring($prevSiblingText, string-length($prevSiblingText)-1)"/>
		<xsl:variable name="followingFirstChar" select="normalize-space(substring(following-sibling::text()[1], 1,1))"/>
		<xsl:if test="$DeliveryMode and preceding-sibling::headnote.reference">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:call-template name="internalReference">
			<xsl:with-param name="id">
				<xsl:if test="string-length(@ID) &gt; 0">
					<xsl:value-of select="concat('&pinpointIdPrefix;', translate(@ID, ';', ''))"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="additionalClass">
				<xsl:if test="not($precedingLastChar = '(') and not($followingFirstChar = ')')">
					<xsl:value-of select="'&headnoteLinkClass; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;'"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="/*[self::summary or self::summaries]//headnote.reference" priority="2">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="headnote.case.title">
		<xsl:call-template name="wrapWithSpan"/>
	</xsl:template>

	<xsl:template match="headnote.reference//text()">
		<xsl:variable name="textWithoutBrackets">
			<xsl:value-of select="translate(., '[]()', '')"/>
		</xsl:variable>
		<xsl:variable name="textWithBrackets">
			<xsl:text>[</xsl:text><xsl:value-of select="$textWithoutBrackets"/><xsl:text>]</xsl:text>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="SpecialCharacterTranslator">
					<xsl:with-param name="textToTranslate" select="$textWithBrackets"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="SpecialCharacterTranslator">
					<xsl:with-param name="textToTranslate" select="$textWithoutBrackets"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="/*[self::summary or self::summaries]//headnote.number/internal.reference | /*[self::summary or self::summaries]//keysummary/internal.reference" priority="2">
		<xsl:variable name="transformedText">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:value-of select="translate($transformedText, '[]', '')" />
	</xsl:template>

	<xsl:template match="headnote.block/expanded.headnote/headnote.number | keysummary.block/expanded.keysummary/keysummary.number">
		<xsl:variable name="internalText">
			<xsl:choose>
				<xsl:when test="$DisplayInternalHeadnoteInfo">
					<xsl:value-of select="normalize-space(.)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="normalize-space(text())"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="headnoteNumberText">
			<xsl:choose>
				<xsl:when test="internal.reference">
					<xsl:apply-templates />
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="string-length($internalText) &gt; 0">
							<xsl:value-of select="translate($internalText, '[]()', '')"/>
						</xsl:when>
						<xsl:when test="self::headnote.number">
							<xsl:value-of select="count(preceding::headnote.number) + 1"/>
						</xsl:when>
						<xsl:when test="self::keysummary.number">
							<xsl:value-of select="count(preceding::keysummary.number) + 1"/>
						</xsl:when>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class" select="'&headnoteNumberClass;'" />
			<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;&internalLinkRelatedInfoHeadnoteIdPrefix;', $headnoteNumberText)"/>
			<xsl:with-param name="contents" select="$headnoteNumberText"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="headnote.number/internal.reference | keysummary.number/internal.reference" priority="1">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="internalReference">
					<xsl:with-param name="contents" select="text()"/>
				</xsl:call-template>								
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="transformedText">
					<xsl:apply-templates />
				</xsl:variable>
				<xsl:variable name="internalReferenceNumberCleaned" select="translate($transformedText, '[]', '')" />
				<xsl:call-template name="internalReference">
					<xsl:with-param name="contents" select="$internalReferenceNumberCleaned"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="headnote.number/cite.query | keysummary.number/cite.query" priority="1">
		<xsl:variable name="transformedText">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:variable name="internalReferenceNumberCleaned" select="translate($transformedText, '[]', '')" />
		<xsl:call-template name="citeQuery">
			<xsl:with-param name="linkContents" select="$internalReferenceNumberCleaned"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="getHeadnoteNumber">
		<xsl:param name="headnoteNumber"/>
		<xsl:param name="isNotFirstTopicKeyHierarchy"/>
		<xsl:param name="headnoteId" />
		<xsl:choose>
			<xsl:when test="$isNotFirstTopicKeyHierarchy">
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="string-length($headnoteNumber) &gt; 0">
						<span class="&headnoteNumberClass; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
							<xsl:if test="string-length($headnoteId) &gt; 0">
								<xsl:attribute name="id">
									<xsl:value-of select="concat('&internalLinkIdPrefix;', $headnoteId)" />
								</xsl:attribute>
							</xsl:if>
							<xsl:choose>
								<xsl:when test="$DeliveryMode and headnote.number[not(internal.reference)]">
												<xsl:text>[</xsl:text><xsl:value-of select="$headnoteNumber"/><xsl:text>]</xsl:text>
								
								</xsl:when>
								<xsl:otherwise>
									<xsl:copy-of select="$headnoteNumber"/>
								</xsl:otherwise>
							</xsl:choose>
						</span>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text><![CDATA[ ]]></xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Rare... mainly for court.headnote.block -->
	<xsl:template match="headnote.number | keysummary.number">
		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class" select="'&headnoteNumberClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="topic.key.hierarchy">
		<xsl:param name="generateRefKeyTable" select="true()"/>
		<xsl:param name="generateTopicKeyHierarchy" select="true()"/>
		<xsl:param name="generateKeyIconImage" select="true()"/>

		<xsl:variable name="topicKeyContents">
			<xsl:apply-templates select="topic.key" />
		</xsl:variable>
		<xsl:if test="string-length($topicKeyContents) &gt; 0">
			<xsl:variable name="topicKeyRefKey">
				<xsl:apply-templates select="descendant::topic.key.ref/key" />
			</xsl:variable>
			<xsl:variable name="topicKeyRefKeyText">
				<xsl:apply-templates select="descendant::topic.key.ref/keytext" />
			</xsl:variable>
			<xsl:variable name="priorClassification">
				<xsl:apply-templates select="../prior.classification" />
			</xsl:variable>

			<div class="&headnoteTopicsClass; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
				<xsl:call-template name="RenderKeyIconImage">
					<xsl:with-param name="generateRefKeyTable" select="$generateRefKeyTable"/>
					<xsl:with-param name="generateKeyIconImage" select="$generateKeyIconImage"/>
				</xsl:call-template>
				<xsl:if test="$generateTopicKeyHierarchy">
					<xsl:call-template name="RenderTopicKeyHierarchy">
						<xsl:with-param name="generateRefKeyTable" select="$generateRefKeyTable"/>
						<xsl:with-param name="topicKeyContents" select="$topicKeyContents"/>
						<xsl:with-param name="topicKeyRefKey" select="$topicKeyRefKey"/>
						<xsl:with-param name="topicKeyRefKeyText" select="$topicKeyRefKeyText"/>
						<xsl:with-param name="priorClassification" select="$priorClassification"/>
					</xsl:call-template>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderKeyIconImage">
		<xsl:param name="generateRefKeyTable" select="true()"/>
		<xsl:param name="generateKeyIconImage" select="true()"/>
		<xsl:if test="$generateKeyIconImage">
			<img src="{$Images}&headnoteKeyPath;" class="&headnoteKeyIconClass;">
				<xsl:choose>
					<xsl:when test="$generateRefKeyTable">
						<xsl:attribute name="alt">
							<xsl:text>&westHeadnotesDualColumnKeyImageText;</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:text>&westHeadnotesDualColumnKeyImageText;</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="alt">
							<xsl:text>&westHeadnotesSingleColumnKeyImageText;</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:text>&westHeadnotesSingleColumnKeyImageText;</xsl:text>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</img>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderTopicKeyHierarchy">
		<xsl:param name="generateRefKeyTable" select="true()"/>
		<xsl:param name="topicKeyContents"/>
		<xsl:param name="topicKeyRefKey"/>
		<xsl:param name="topicKeyRefKeyText"/>
		<xsl:param name="priorClassification"/>
		<div class="&fancyKeyciteContainerBottomClass;">
			<div class="&fancyKeyciteContainerClass;">
				<xsl:choose>
					<xsl:when test="$generateRefKeyTable">
						<div class="&topicKeyContentTableClass;">
							<xsl:copy-of select="$topicKeyContents"/>
							<xsl:if test="string-length($topicKeyRefKey) &gt; 0 and string-length($topicKeyRefKeyText) &gt; 0">
								<div class="&headnoteKeyPairClass;">
									<span class="&headnoteRefNumberClass;">
										<xsl:copy-of select="$topicKeyRefKey"/>
									</span>
									<span class="&headnoteTopicKeyClass;">
										<xsl:copy-of select="$topicKeyRefKeyText"/>
									</span>
								</div>
							</xsl:if>
							<xsl:if test="string-length($priorClassification) &gt; 0">
								<div class="&headnoteKeyPairClass;">
									<span class="&headnoteTopicKeyClass;">
										<xsl:copy-of select="$priorClassification"/>
									</span>
								</div>
							</xsl:if>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div class="&popupHeadnoteClass;"></div>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="topic.key">
		<xsl:variable name="headnoteRefNumber">
			<xsl:apply-templates select="key"/>
		</xsl:variable>
		<xsl:variable name="headnoteTopicKey">
			<xsl:apply-templates select="keytext"/>
		</xsl:variable>
		<xsl:if test="string-length($headnoteRefNumber) &gt; 0 and string-length($headnoteTopicKey) &gt; 0">
			<div class="&headnoteKeyPairClass;">
				<span class="&headnoteRefNumberClass;">
					<xsl:copy-of select="$headnoteRefNumber"/>
				</span>
				<span class="&headnoteTopicKeyClass;">
					<xsl:copy-of select="$headnoteTopicKey"/>
				</span>
			</div>
		</xsl:if>
		<xsl:apply-templates select="topic.key"/>
	</xsl:template>

	<xsl:template match="topic.key.ref">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>

		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:choose>
				<xsl:when test="ancestor::topic.key.hierarchy">
					<div class="&headnoteTopicKeyClass;">
						<xsl:copy-of select="$contents"/>
						<!-- prior.classification needs to be rendered in the last topic.key/topic.key.ref for it to display correctly -->
						<xsl:if test="not(child::topic.key or child::topic.key.ref) and ancestor::topic.key.hierarchy/following-sibling::node()[1][self::prior.classification]">
							<xsl:apply-templates select="ancestor::topic.key.hierarchy/following-sibling::node()[1]"/>
						</xsl:if>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<span>
						<xsl:copy-of select="$contents"/>
					</span>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="key">
		<xsl:choose>
			<xsl:when test="cite.query">
				<xsl:apply-templates select="cite.query"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Take the keytext, trim it, strip off the 'k. ' from the front (if it appears), and strip off the '.' from the end (if it appears) -->
	<xsl:template match="keytext//text()">
		<xsl:call-template name="cleanKeyText">
			<xsl:with-param name="value" select="."/>
			<xsl:with-param name="beginningText" select="position() = 1" />
			<xsl:with-param name="endingText" select="position() = last()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="headnote.courtyear">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&headnoteCourtYearClass;'" />
		</xsl:call-template>
	</xsl:template>


	<!-- Special rules for titles -->
	<xsl:template match="/Document/document-data/title//keytext" priority="1">
		<xsl:call-template name="getKeyTextForTitles" />
	</xsl:template>


	<!-- Court Headnotes -->
	<xsl:template match="court.headnote.block">
		<div id="&courtHeadnotesId;">
			<xsl:if test="head">
				<div id="&headnoteHeaderId;" class="&headnoteHeaderClass;">
					<xsl:apply-templates select="head"/>
				</div>
			</xsl:if>
			<xsl:apply-templates select="headnote" />
		</div>
	</xsl:template>

	<xsl:template match="court.headnote.block/headnote">
		<div class="&headnoteClass;">
			<xsl:if test="string-length(@ID) &gt; 0">
				<xsl:attribute name="id">
					<xsl:text>&internalLinkIdPrefix;</xsl:text>
					<xsl:value-of select="@ID"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="head" />
			<xsl:apply-templates select="headnote.number" />
			<xsl:apply-templates select="catchphrase.para" />
			<xsl:apply-templates select="footnote" />
			<xsl:apply-templates select="headnote.body" />
			<xsl:apply-templates select="library.reference" />
		</div>
	</xsl:template>

	<xsl:template match="library.reference">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&libraryReferenceClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="para[ancestor::headnote.body or ancestor::keysummary.body]" priority="1">
		<xsl:call-template name="para">
			<xsl:with-param name="className" select="'&headnoteParagraphClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="wrapWithTableCellIfDeliveryMode">
		<xsl:param name="class"/>
		<xsl:param name="contents">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:param>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<td>
					<xsl:attribute name="class">
						<xsl:value-of select="$class"/>
					</xsl:attribute>
					<xsl:copy-of select="$contents"/>
				</td>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$contents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="headnotePublicationBlock">
	</xsl:template>

  <xsl:template name="renderKeyHierarchy">
  </xsl:template>
</xsl:stylesheet>
