<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SpecialCharacters.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Get rid of "[" or "(" if it is the last character in the text node that immediately precedes a footnote reference or rogue "super" element -->
	<xsl:template name="handleTextNodePrecedingFootnoteReference" match="text()[following-sibling::node()[not(self::bop or self::bos or self::eos or self::eop or self::anchor)][1][self::footnote.reference or self::table.footnote.reference or self::endnote.reference or self::super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN') or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn')]]]" priority="2">
		<!-- Step 1: Translate special characters, pass on to Step 2 -->
		<xsl:variable name="text">
			<xsl:call-template name="SpecialCharacterTranslator"/>
		</xsl:variable>

		<!-- Step 2: Check text at end and the following non-footnote.reference text node, handle, pass on to Step 3 -->
		<xsl:variable name="textTrimmedAtEnd">
			<xsl:call-template name="trim-end">
				<xsl:with-param name="string" select="$text" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textEndsWithOpeningBracket">
			<xsl:call-template name="ends-with">
				<xsl:with-param name="string1" select="$textTrimmedAtEnd" />
				<xsl:with-param name="string2" select="'['" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textEndsWithOpeningParenthesis">
			<xsl:call-template name="ends-with">
				<xsl:with-param name="string1" select="$textTrimmedAtEnd" />
				<xsl:with-param name="string2" select="'('" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textFollowingRef">
			<xsl:call-template name="trim-start">
				<xsl:with-param name="string">
					<xsl:call-template name="SpecialCharacterTranslator">
						<xsl:with-param name="textToTranslate">
							<xsl:call-template name="findRelevantFollowingText" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textFollowingRefStartsWithClosingBracket" select="starts-with($textFollowingRef, ']')" />
		<xsl:variable name="textFollowingRefStartsWithClosingParenthesis" select="starts-with($textFollowingRef, ')')" />

		<!-- Step 3: Check text at start and the preceding non-footnote.reference text node, handle, return -->
		<xsl:variable name="textTrimmedAtStart">
			<xsl:call-template name="trim-start">
				<xsl:with-param name="string" select="$text" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textStartsWithClosingBracket" select="starts-with($textTrimmedAtStart, ']')" />
		<xsl:variable name="textStartsWithClosingParenthesis" select="starts-with($textTrimmedAtStart, ')')" />
		<xsl:variable name="precedingSiblingIsFootnoteReference" select="boolean(preceding-sibling::node()[not(self::bop or self::bos or self::eos or self::eop or self::anchor)][1][self::footnote.reference or self::table.footnote.reference or self::endnote.reference or self::super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN') or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn')]])" />
		<xsl:variable name="textPrecedingRef">
			<xsl:call-template name="trim-end">
				<xsl:with-param name="string">
					<xsl:call-template name="SpecialCharacterTranslator">
						<xsl:with-param name="textToTranslate">
							<xsl:call-template name="findRelevantPrecedingText" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textPrecedingRefEndsWithOpeningBracket">
			<xsl:call-template name="ends-with">
				<xsl:with-param name="string1" select="$textPrecedingRef" />
				<xsl:with-param name="string2" select="'['" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textPrecedingRefEndsWithOpeningParenthesis">
			<xsl:call-template name="ends-with">
				<xsl:with-param name="string1" select="$textPrecedingRef" />
				<xsl:with-param name="string2" select="'('" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Clean end: opening bracket/parenthesis - "[" or "(" -->
		<xsl:variable name="textAfterEndIsCleaned">
			<xsl:choose>
				<xsl:when test="$textEndsWithOpeningBracket = 'true' and $textFollowingRefStartsWithClosingBracket = true()">
					<xsl:call-template name="trim-end">
						<xsl:with-param name="string" select="substring($textTrimmedAtEnd, 1, (string-length($textTrimmedAtEnd) - 1))" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$textEndsWithOpeningParenthesis = 'true' and $textFollowingRefStartsWithClosingParenthesis = true()">
					<xsl:call-template name="trim-end">
						<xsl:with-param name="string" select="substring($textTrimmedAtEnd, 1, (string-length($textTrimmedAtEnd) - 1))" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$textTrimmedAtEnd" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="textAfterEndIsCleanedTrimmedAtStart">
			<xsl:call-template name="trim-start">
				<xsl:with-param name="string" select="$textAfterEndIsCleaned" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Clean start: closing bracket/parenthesis - "]" or ")" -->
		<xsl:variable name="textAfterStartIsCleaned">
			<xsl:choose>
				<xsl:when test="$textStartsWithClosingBracket = true() and $precedingSiblingIsFootnoteReference = true() and $textPrecedingRefEndsWithOpeningBracket = 'true'">
					<xsl:value-of select="substring($textAfterEndIsCleanedTrimmedAtStart, 2)" />
				</xsl:when>
				<xsl:when test="$textStartsWithClosingParenthesis = true() and $precedingSiblingIsFootnoteReference = true() and $textPrecedingRefEndsWithOpeningParenthesis = 'true'">
					<xsl:value-of select="substring($textAfterEndIsCleanedTrimmedAtStart, 2)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$textAfterEndIsCleaned" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- This "variable" and "choose" statement is a content fix for Medical Litigator content types. -->
		<!-- If is surrounded on both side by footnote references, check if contents is only equal to a dash (-), en dash (&x2013;), or hyphen (&x2010;), or comma (,).  If so, superscript this text node -->
		<xsl:variable name="textBetweenIsDashOrComma" select="normalize-space($textAfterEndIsCleaned) = ',' or normalize-space($textAfterEndIsCleaned) = '-' or normalize-space($textAfterEndIsCleaned) = '&#x2013;' or  normalize-space($textAfterEndIsCleaned) = '&#x2010;'" />
		<xsl:choose>
			<xsl:when test="$precedingSiblingIsFootnoteReference = true() and $textBetweenIsDashOrComma = true()">
				<sup>
					<xsl:value-of select="$textAfterStartIsCleaned" />
				</sup>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$textAfterStartIsCleaned" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Get rid of "]" or ")" if it is the first character in the text node that immediately follows a footnote reference or rogue "super" element -->
	<xsl:template name="handleTextNodeFollowingFootnoteReference" match="text()[preceding-sibling::node()[not(self::bop or self::bos or self::eos or self::eop or self::anchor)][1][self::footnote.reference or self::table.footnote.reference or self::endnote.reference or self::super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN' or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn'))]]]">
		<!-- Step 1: Translate special characters, pass on to Step 2 -->
		<xsl:variable name="text">
			<xsl:call-template name="SpecialCharacterTranslator"/>
		</xsl:variable>

		<!-- Step 2: Check text at start and the preceding non-footnote.reference text node, handle, pass on to Step 3 -->
		<xsl:variable name="textTrimmedAtStart">
			<xsl:call-template name="trim-start">
				<xsl:with-param name="string" select="$text" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textStartsWithClosingBracket" select="starts-with($textTrimmedAtStart, ']')" />
		<xsl:variable name="textStartsWithClosingParenthesis" select="starts-with($textTrimmedAtStart, ')')" />
		<xsl:variable name="textPrecedingRef">
			<xsl:call-template name="trim-end">
				<xsl:with-param name="string">
					<xsl:call-template name="SpecialCharacterTranslator">
						<xsl:with-param name="textToTranslate">
							<xsl:call-template name="findRelevantPrecedingText" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textPrecedingRefEndsWithOpeningBracket">
			<xsl:call-template name="ends-with">
				<xsl:with-param name="string1" select="$textPrecedingRef" />
				<xsl:with-param name="string2" select="'['" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textPrecedingRefEndsWithOpeningParenthesis">
			<xsl:call-template name="ends-with">
				<xsl:with-param name="string1" select="$textPrecedingRef" />
				<xsl:with-param name="string2" select="'('" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Step 3: Check text at end and the following non-footnote.reference text node, handle, return -->
		<xsl:variable name="textTrimmedAtEnd">
			<xsl:call-template name="trim-end">
				<xsl:with-param name="string" select="$text" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textEndsWithOpeningBracket">
			<xsl:call-template name="ends-with">
				<xsl:with-param name="string1" select="$textTrimmedAtEnd" />
				<xsl:with-param name="string2" select="'['" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textEndsWithOpeningParenthesis">
			<xsl:call-template name="ends-with">
				<xsl:with-param name="string1" select="$textTrimmedAtEnd" />
				<xsl:with-param name="string2" select="'('" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="followingSiblingIsFootnoteReference" select="boolean(following-sibling::node()[not(self::bop or self::bos or self::eos or self::eop or self::anchor)][1][self::footnote.reference or self::table.footnote.reference or self::endnote.reference or self::super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN') or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn')]])" />
		<xsl:variable name="textFollowingRef">
			<xsl:call-template name="trim-start">
				<xsl:with-param name="string">
					<xsl:call-template name="SpecialCharacterTranslator">
						<xsl:with-param name="textToTranslate">
							<xsl:call-template name="findRelevantFollowingText" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="textFollowingRefStartsWithClosingBracket" select="starts-with($textFollowingRef, ']')" />
		<xsl:variable name="textFollowingRefStartsWithClosingParenthesis" select="starts-with($textFollowingRef, ')')" />

		<!-- Clean start: closing bracket/parenthesis - "]" or ")" -->
		<xsl:variable name="textAfterStartIsCleaned">
			<xsl:choose>
				<xsl:when test="$textStartsWithClosingBracket = true() and $textPrecedingRefEndsWithOpeningBracket = 'true'">
					<xsl:value-of select="substring($textTrimmedAtStart, 2)" />
				</xsl:when>
				<xsl:when test="$textStartsWithClosingParenthesis = true() and $textPrecedingRefEndsWithOpeningParenthesis = 'true'">
					<xsl:value-of select="substring($textTrimmedAtStart, 2)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$text" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="textAfterStartIsCleanedTrimmedAtEnd">
			<xsl:call-template name="trim-end">
				<xsl:with-param name="string" select="$textAfterStartIsCleaned" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Clean end: opening bracket/parenthesis - "[" or "(" -->
		<xsl:variable name="textAfterEndIsCleaned">
			<xsl:choose>
				<xsl:when test="$textEndsWithOpeningBracket = 'true' and $followingSiblingIsFootnoteReference = true() and $textFollowingRefStartsWithClosingBracket = true()">
					<xsl:call-template name="trim-end">
						<xsl:with-param name="string" select="substring($textAfterStartIsCleanedTrimmedAtEnd, 1, (string-length($textAfterStartIsCleanedTrimmedAtEnd) - 1))" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$textEndsWithOpeningParenthesis = 'true' and $followingSiblingIsFootnoteReference = true() and $textFollowingRefStartsWithClosingParenthesis = true()">
					<xsl:call-template name="trim-end">
						<xsl:with-param name="string" select="substring($textAfterStartIsCleanedTrimmedAtEnd, 1, (string-length($textAfterStartIsCleanedTrimmedAtEnd) - 1))" />
					</xsl:call-template>
				</xsl:when>
				<!-- Special "when" condition to ensure space at the end is always trimmed if a footnote reference follows -->
				<xsl:when test="$followingSiblingIsFootnoteReference = true()">
					<xsl:call-template name="trim-end">
						<xsl:with-param name="string" select="$textAfterStartIsCleanedTrimmedAtEnd" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$textAfterStartIsCleaned" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- This "variable" and "choose" statement is a content fix for Medical Litigator content types. -->
		<!-- If is surrounded on both side by footnote references, check if contents is only equal to a dash (-), en dash (&#x2013;), or hyphen (&x2010;), or comma (,).  If so, superscript this text node -->
		<xsl:variable name="textBetweenIsDashOrComma" select="normalize-space($textAfterEndIsCleaned) = ',' or normalize-space($textAfterEndIsCleaned) = '-' or normalize-space($textAfterEndIsCleaned) = '&#x2013;' or  normalize-space($textAfterEndIsCleaned) = '&#x2010;'" />
		<xsl:choose>
			<xsl:when test="$followingSiblingIsFootnoteReference = true() and $textBetweenIsDashOrComma = true()">
				<sup>
					<xsl:value-of select="$textAfterEndIsCleaned" />
				</sup>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$textAfterEndIsCleaned" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Remove footnote markup like [FN] from a string  -->
	<xsl:template name="removeFootnoteMarkup">
		<xsl:param name="textWithFootnoteParam" select="."/>				
		<xsl:variable name="normalizedText">
			<xsl:copy-of select="normalize-space($textWithFootnoteParam)"/>
		</xsl:variable>
		<xsl:variable name="textBeforeOpeningBracketOrParenAndFN">
			<xsl:choose>
				<xsl:when test="contains($normalizedText, '[FN')">
					<xsl:copy-of select="substring-before($normalizedText, '[FN')"/>
				</xsl:when>
				<xsl:when test="contains($normalizedText, '[fn')">
					<xsl:copy-of select="substring-before($normalizedText, '[fn')"/>
				</xsl:when>
				<xsl:when test="contains($normalizedText, '(FN')">
					<xsl:copy-of select="substring-before($normalizedText, '(FN')"/>
				</xsl:when>
				<xsl:when test="contains($normalizedText, '(fn')">
					<xsl:copy-of select="substring-before($normalizedText, '(fn')"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="textAfterOpeningBracketOrParenAndFN">			
			<xsl:choose>
				<xsl:when test="contains($normalizedText, '[FN')">
					<xsl:copy-of select="substring-after($normalizedText, '[FN')"/>
				</xsl:when>
				<xsl:when test="contains($normalizedText, '[fn')">
					<xsl:copy-of select="substring-after($normalizedText, '[fn')"/>
				</xsl:when>
				<xsl:when test="contains($normalizedText, '(FN')">
					<xsl:copy-of select="substring-after($normalizedText, '(FN')"/>
				</xsl:when>
				<xsl:when test="contains($normalizedText, '(fn')">
					<xsl:value-of select="substring-after($normalizedText, '(fn')"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="textAfterClosingBracketOrParenAndFN">
			<xsl:if test="string-length($textAfterOpeningBracketOrParenAndFN) &gt; 0">
				<xsl:choose>
					<xsl:when test="contains($textAfterOpeningBracketOrParenAndFN, ']')">
						<xsl:copy-of select="substring-after($textAfterOpeningBracketOrParenAndFN, ']')"/>
					</xsl:when>
					<xsl:when test="contains($textAfterOpeningBracketOrParenAndFN, ')')">
						<xsl:copy-of select="substring-after($textAfterOpeningBracketOrParenAndFN, ')')"/>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="string-length($textBeforeOpeningBracketOrParenAndFN) &gt; 0 or string-length($textAfterOpeningBracketOrParenAndFN) &gt; 0">
				<xsl:copy-of select="$textBeforeOpeningBracketOrParenAndFN"/>
				<xsl:if test="string-length($textAfterClosingBracketOrParenAndFN) &gt; 0">
					<xsl:copy-of select="$textAfterClosingBracketOrParenAndFN"/>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$normalizedText"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- This template and "choose" statement is an update for Medical Litigator content types (and others). -->
	<!-- If is surrounded on both side by footnote references, check if contents is only equal to a dash (-), en dash (&x2013;), or hyphen (&x2010;), or comma (,).  
		If so, look further to find the actual wrapping text to remove the "(" and ")". -->
	<xsl:template name="findRelevantFollowingText">
		<xsl:param name="index" select="1" />

		<xsl:variable name="textFollowing" select="normalize-space(following-sibling::node()[not(self::bop or self::bos or self::eos or self::eop or self::anchor or self::footnote.reference or self::table.footnote.reference or self::endnote.reference or self::super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN') or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn')])][$index][self::text()])"/>
		<xsl:choose>
			<xsl:when test="$textFollowing = ',' or $textFollowing = '-' or $textFollowing = '&#x2013;' or $textFollowing = '&#x2010;'" >
				<xsl:call-template name="findRelevantFollowingText">
					<xsl:with-param name="index" select="number($index) + 1" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$textFollowing"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- This template and "choose" statement is an update for Medical Litigator content types (and others). -->
	<!-- If is surrounded on both side by footnote references, check if contents is only equal to a dash (-), en dash (&x2013;), or hyphen (&x2010;), or comma (,).  
		If so, look further to find the actual wrapping text to remove the "(" and ")". -->
	<xsl:template name="findRelevantPrecedingText">
		<xsl:param name="index" select="1" />

		<xsl:variable name="textPreceding" select="preceding-sibling::node()[not(self::bop or self::bos or self::eos or self::eop or self::anchor or self::footnote.reference or self::table.footnote.reference or self::endnote.reference or self::super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN') or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn')])][$index][self::text()]" />
		<xsl:choose>
			<xsl:when test="$textPreceding = ',' or $textPreceding = '-' or $textPreceding = '&#x2013;' or $textPreceding = '&#x2010;'" >
				<xsl:call-template name="findRelevantPrecedingText">
					<xsl:with-param name="index" select="number($index) + 1" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$textPreceding"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

</xsl:stylesheet>
