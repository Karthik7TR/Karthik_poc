<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<xsl:include href="Universal.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*" />

	<xsl:template match="*">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Process these elements here so they don't appear themselves in the output. -->
	<xsl:template match="n-docbody | n-metadata | n-document | n-load | html | head | meta | title | body | metadata.block | md.identifiers | md.references">
		<xsl:apply-templates select="*" />
	</xsl:template>

	<xsl:template match="Document">
		<!-- Add document class -->
		<!-- Don't remove this style even if CSS asks, trust me you will murder wkHtmlToPdf delivery - Tim S.-->
		<div id="&documentClass;" style="font-family: 'Times New Roman', serif;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeLocatePrecedentAgreements;'"/>
			</xsl:call-template>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<xsl:if test="not(string(//md.source.rendition.id))">
				<div id="&documentLinksContainer;" class="&layoutTextAlignLeft;"></div>
			</xsl:if>

			<xsl:apply-templates/>
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText"></xsl:with-param>
				<xsl:with-param name="endOfDocumentCopyrightTextVerbatim">true()</xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="text[@xml:space = 'preserve']">
		<pre>
			<xsl:apply-templates />
		</pre>
	</xsl:template>

	<xsl:template match="p" priority="1">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Internal links don't work without an id attribute. Add it here. -->
	<xsl:template match="a">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*" />
			<xsl:if test="@name">
				<xsl:attribute name="id">
					<xsl:value-of select="@name"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- Ignore these elements -->
	<xsl:template match="document-data | header | md.cites | eos | eop | bos | bop | document.date.metadata | document.title.metadata | governinglaw.metadata | governingLaw.metadata | party.metadata | lawfirm.metadata | lawFirm.metadata | lawyer.name.metadata | classifications | classification | concept"/>

	<!-- All of the useful text is inside elements. Ignore orphan text outside elements. -->
	<xsl:template match="body/text()"/>

	<!-- Below, we change wingdings characters over to Unicode so they render in Firefox correctly since Firefox
	does not support Wingdings font.  However, when the document is delivered, the font is still Wingdings but being
	applied to Unicode, so the characters come out incorrectly.  In addition to replacing Wingdings characters with
	unicode, replace Wingdings font with "inherit" so it renders properly in both browser and delivery. -->
	<xsl:template match="*[contains(translate(@face, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ ', 'abcdefghijklmnopqrstuvwxyz'), 'wingdings')] | 
						 *[contains(translate(@style, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ ', 'abcdefghijklmnopqrstuvwxyz'), 'wingdings')]" priority="2">
		<xsl:copy>
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}">
					<xsl:call-template name="replacethis">
						<xsl:with-param name="string" select="translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ ', 'abcdefghijklmnopqrstuvwxyz')" />
						<xsl:with-param name="pattern" select="'wingdings'" />
						<xsl:with-param name="replacement" select="' inherit '" />
					</xsl:call-template>
				</xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<!-- Without this the spaces in the text are not preserved -->
	<!-- This template also does wingding character conversions to unicode as FireFox does not handle Wingdings font. 
	Without this conversion the documents look ok in browsers like Chrome and IE but FireFox displays unexpected characters. -->
	<xsl:template match="text()" priority="1">
		<xsl:param name="textToTranslate" select="." />

		<xsl:variable name="isWingdingsFont">
			<xsl:choose>
				<xsl:when test="contains(translate(../@face, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ ', 'abcdefghijklmnopqrstuvwxyz'), 'wingdings') or
								contains(translate(../@style, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ ', 'abcdefghijklmnopqrstuvwxyz'), 'wingdings')">
					<xsl:value-of select="true()"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="false()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="wingdingsCharacterO">
			<xsl:choose>
				<xsl:when test="$isWingdingsFont=string(true())">
					<xsl:value-of select="'&#9744;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'o'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="wingdingsTwoCharacterPoundSign">
			<xsl:choose>
				<xsl:when test="$isWingdingsFont=string(true())">
					<xsl:value-of select="'&#9744;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&#xa3;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="wingdingsTwoCharacterT">
			<xsl:choose>
				<xsl:when test="$isWingdingsFont=string(true())">
					<xsl:value-of select="'&#9746;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&#x54;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="wingdingsCharacterQ">
			<xsl:choose>
				<xsl:when test="$isWingdingsFont=string(true())">
					<xsl:value-of select="'&#9746;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'Q'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="wingdingsCharacterR">
			<xsl:choose>
				<xsl:when test="$isWingdingsFont=string(true())">
					<xsl:value-of select="'&#9745;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'R'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="wingdingsCharacterX">
			<xsl:choose>
				<xsl:when test="$isWingdingsFont=string(true())">
					<xsl:value-of select="'&#9746;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'x'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="textWithOForEmptyCheckboxReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textToTranslate" />
				<xsl:with-param name="pattern" select="'&#x6F;'" />
				<xsl:with-param name="replacement" select="$wingdingsCharacterO"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithPoundSignForEmptyCheckboxReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithOForEmptyCheckboxReplaced" />
				<xsl:with-param name="pattern" select="'&#xa3;'" />
				<xsl:with-param name="replacement" select="$wingdingsTwoCharacterPoundSign"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithTForCheckedCheckboxReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithPoundSignForEmptyCheckboxReplaced" />
				<xsl:with-param name="pattern" select="'&#x54;'" />
				<xsl:with-param name="replacement" select="$wingdingsTwoCharacterT"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithQForCrossedCheckboxReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithTForCheckedCheckboxReplaced" />
				<xsl:with-param name="pattern" select="'&#x51;'" />
				<xsl:with-param name="replacement" select="$wingdingsCharacterQ"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithRForCheckedCheckboxReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithQForCrossedCheckboxReplaced" />
				<xsl:with-param name="pattern" select="'&#x52;'" />
				<xsl:with-param name="replacement" select="$wingdingsCharacterR"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithXForCheckedCheckboxReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithRForCheckedCheckboxReplaced" />
				<xsl:with-param name="pattern" select="'&#x78;'" />
				<xsl:with-param name="replacement" select="$wingdingsCharacterX"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithUForCheckReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithXForCheckedCheckboxReplaced" />
				<xsl:with-param name="pattern" select="'&#xFC;'" />
				<xsl:with-param name="replacement" select="'&#10003;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithYForCrossedCheckboxReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithUForCheckReplaced" />
				<xsl:with-param name="pattern" select="'&#xFD;'" />
				<xsl:with-param name="replacement" select="'&#9746;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithPForCheckedCheckboxReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithYForCrossedCheckboxReplaced" />
				<xsl:with-param name="pattern" select="'&#xFE;'" />
				<xsl:with-param name="replacement" select="'&#9745;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithNoReplacedWithNonBreakingSpace">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithPForCheckedCheckboxReplaced" />
				<xsl:with-param name="pattern" select="'No '" />
				<xsl:with-param name="replacement" select="'No&nbsp;'" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithYesReplacedWithNonBreakingSpace">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithNoReplacedWithNonBreakingSpace" />
				<xsl:with-param name="pattern" select="'Yes&#xa;'" />
				<xsl:with-param name="replacement" select="'Yes&nbsp;'" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="replacethis">
			<xsl:with-param name="string" select="$textWithYesReplacedWithNonBreakingSpace" />
			<xsl:with-param name="pattern" select="'&#xA8;'" />
			<xsl:with-param name="replacement" select="'&#9744;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Input xHTML contains img elements that have image guids as src attribute value. Create image link from this guid.
	   Retain height and width from input. -->
	<xsl:template match="img">
		<xsl:variable name="imageGuid" select="@src"/>
		<xsl:variable name="targetType" select="//n-metadata[@guid=$imageGuid]//@ttype" />
		<xsl:variable name="renderType" select="//n-metadata[@guid=$imageGuid]//md.image.renderType" />
		<xsl:variable name="highResolution" select="$renderType = 'AmaAtlasHrLink'" />
		<xsl:variable name="mimeType" select="//n-metadata[@guid=$imageGuid]//md.image.format" />
		<xsl:variable name="height">
			<xsl:choose>
				<xsl:when test="string-length(@height) > 0">
					<xsl:value-of select="@height"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="//n-metadata[@guid=$imageGuid]//md.image.height"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="width">
			<xsl:choose>
				<xsl:when test="string-length(@width) > 0">
					<xsl:value-of select="@width"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="//n-metadata[@guid=$imageGuid]//md.image.width"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="style">
			<xsl:if test="@style">
				<xsl:call-template name="ValidateStyle">
					<xsl:with-param name="styleValue" select="@style" />
				</xsl:call-template>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="error" select="//error" />

		<!-- blobSrc needs to be the internal image host and not the MUD (forImgTag = true) -->
		<xsl:variable name="blobSrc">
			<xsl:call-template name="createBlobLink">
				<xsl:with-param name="guid" select="@src"/>
				<xsl:with-param name="highResolution" select="$highResolution"/>
				<xsl:with-param name="targetType" select="$targetType"/>
				<xsl:with-param name="mimeType" select="$mimeType"/>
				<xsl:with-param name="forImgTag" select="$DeliveryMode" />
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- blobHref needs to be the MUD host and not the image host, this is the clickable link in PDFs. (Delivery Only, checked in DocLinks.xsl) -->
		<xsl:variable name="blobHref">
			<xsl:call-template name="createBlobLink">
				<xsl:with-param name="guid" select="@src"/>
				<xsl:with-param name="highResolution" select="$highResolution"/>
				<xsl:with-param name="targetType" select="$targetType"/>
				<xsl:with-param name="mimeType" select="$mimeType"/>
				<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$renderType = 'BlobError'">
				<span class="&imageNonDisplayableClass;">&tableOrGraphicNotDisplayableText;</span>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="imageClassName" select="'&imageClass;'" />
				<span class="&imageBlockClass;">
					<xsl:if test="@style">
						<xsl:attribute name="style">
							<xsl:call-template name="ValidateStyle">
								<xsl:with-param name="styleValue" select="@style" />
							</xsl:call-template>
						</xsl:attribute>
					</xsl:if>
					<a class="&imageLinkClass;" href="{$blobHref}" type="{$mimeType}">
						<xsl:call-template name="buildBlobImageElement">
							<xsl:with-param name="src" select="$blobSrc"/>
							<xsl:with-param name="height" select="$height"/>
							<xsl:with-param name="width" select="$width"/>
							<xsl:with-param name="class" select="$imageClassName"/>
							<xsl:with-param name="style" select="$style"/>
						</xsl:call-template>
					</a>
				</span>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="buildBlobImageElement">
		<xsl:param name="src"/>
		<xsl:param name="height"/>
		<xsl:param name="width"/>
		<xsl:param name="class"/>
		<xsl:param name="style"/>
		<xsl:element name="img">
			<xsl:attribute name="src">
				<xsl:value-of select="$src" />
			</xsl:attribute>
			<xsl:if test="number($height)">
				<xsl:attribute name="height">
					<xsl:value-of select="$height"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="number($width)">
				<xsl:attribute name="width">
					<xsl:value-of select="$width"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="class">
				<xsl:value-of select="$class"/>
			</xsl:attribute>
			<xsl:if test="string-length($style) &gt; 0">
				<xsl:attribute name="style">
					<xsl:value-of select="$style"/>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<xsl:template match="md.source.rendition.id">
		<div id="&documentLinksContainer;" class="&layoutTextAlignLeft;">
			<xsl:choose>
				<xsl:when test="$DeliveryMode and not($DisplayOriginalImageLink)">
					<!-- Do nothing -->
				</xsl:when>
				<xsl:when test="not(string(.))">
					<!-- Show message when guid is not yet available -->
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createDocumentBlobLink">
						<xsl:with-param name="guid" select="."/>
						<xsl:with-param name="targetType" select="@ttype"/>
						<xsl:with-param name="mimeType" select="'&pdfMimeType;'" />
						<xsl:with-param name="contents">
							<xsl:text>&fdOriginalDocumentLinkText;</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
						<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
						<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
						<xsl:with-param name="originationContext" select="'&docOriginalImageOriginationContext;'" />
						<xsl:with-param name="prettyName" select="translate(/Document/document-data/cite//text(),'&space;', '&lowline;')" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<!--There are limits for query string. Introduced templates to make it shorter.-->
	<!--dg{quid} - document guid-->
	<!--cg{quid} - clause guid-->
	<xsl:template match="clause.begin[@ID] | document.begin[@ID]">
		<xsl:element name="span">
			<xsl:attribute name="id">
				<xsl:text>co_internalToc_</xsl:text>
				<xsl:value-of select="substring(name(),1,1)"/>
				<xsl:text>g</xsl:text>
				<xsl:value-of select="@ID"/>
			</xsl:attribute>
			<xsl:attribute name="class">co_internalTocMarker</xsl:attribute>
			<xsl:text>&nbsp;</xsl:text>
		</xsl:element>

		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template name="replacethis">
		<xsl:param name="string" select="." />
		<xsl:param name="pattern" select="''" />
		<xsl:param name="replacement" select="''" />

		<xsl:choose>
			<xsl:when test="contains($string, $pattern)">
				<xsl:value-of select="substring-before($string, $pattern)" />
				<xsl:copy-of select="$replacement" />
				<xsl:call-template name="replacethis">
					<xsl:with-param name="string" select="substring-after($string, $pattern)" />
					<xsl:with-param name="pattern" select="$pattern" />
					<xsl:with-param name="replacement" select="$replacement" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$string"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Workaround for content issue with styles. Add 'px' to 'top', 'bottom', 'left', 'right', 'width', 'height' properties if it is missed-->
	<xsl:template match="div" priority="1">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*" />
			<xsl:if test="@style">
				<xsl:attribute name="style">
					<xsl:call-template name="ValidateStyle">
						<xsl:with-param name="styleValue" select="@style" />
					</xsl:call-template>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template name="ValidateStyle">
		<xsl:param name="styleValue" />
		<xsl:param name="delimiter" select="';'" />
		<xsl:if test="string-length($styleValue) &gt; 0">
			<xsl:choose>
				<xsl:when test="contains($styleValue, $delimiter)">
					<xsl:call-template name="ModifyProperty">
						<xsl:with-param name="propertyValue" select="substring-before($styleValue, $delimiter)" />
					</xsl:call-template>
					<xsl:value-of select="$delimiter"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="ModifyProperty">
						<xsl:with-param name="propertyValue" select="$styleValue"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="ValidateStyle">
				<xsl:with-param name="styleValue" select="substring-after($styleValue, $delimiter)"/>
				<xsl:with-param name="delimiter" select="$delimiter" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="ModifyProperty">
		<xsl:param name="propertyValue" />
		<xsl:variable name="mesure" select="'px'"/>
		<xsl:variable name="clipDelimiter" select="','"/>
		<xsl:variable name="clipOpenBracket" select="'('"/>
		<xsl:variable name="clipCloseBracket" select="')'"/>
		<xsl:variable name="normalizedPropertyValue" select="normalize-space($propertyValue)"/>
		<xsl:choose>
			<xsl:when test="(starts-with($normalizedPropertyValue, 'top')
							or starts-with($normalizedPropertyValue, 'left')
							or starts-with($normalizedPropertyValue, 'right')
							or starts-with($normalizedPropertyValue, 'bottom')
							or starts-with($normalizedPropertyValue, 'width')
							or starts-with($normalizedPropertyValue, 'height')
							or number($normalizedPropertyValue) = number($normalizedPropertyValue)) 
							and substring($normalizedPropertyValue, string-length($normalizedPropertyValue) - string-length($mesure) +1) != $mesure">
				<xsl:value-of select="concat($normalizedPropertyValue, $mesure)" />
			</xsl:when>
			<xsl:when test="starts-with($normalizedPropertyValue, 'clip')">
				<xsl:value-of select="substring-before($normalizedPropertyValue, $clipOpenBracket)" />
				<xsl:value-of select="$clipOpenBracket" />
				<xsl:call-template name="ValidateStyle">
					<xsl:with-param name="styleValue" select="substring-before(substring-after($normalizedPropertyValue, $clipOpenBracket), $clipCloseBracket)"/>
					<xsl:with-param name="delimiter" select="$clipDelimiter" />
				</xsl:call-template>
				<xsl:value-of select="$clipCloseBracket" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$normalizedPropertyValue" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
