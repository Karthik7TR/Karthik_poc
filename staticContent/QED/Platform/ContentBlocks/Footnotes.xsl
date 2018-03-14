<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="FootnoteReferenceCleaner.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<xsl:key name="distinctFootnoteIds" match="footnote | form.footnote | endnote | form.endnote" use="@ID | @id" />
	<xsl:key name="distinctAlternateFootnoteIds" match="footnote//anchor" use="@ID | @id"/>
	<xsl:key name="distinctFootnoteReferenceRefIds" match="footnote.reference | table.footnote.reference | endnote.reference" use="@refid" />
	<xsl:key name="distinctFootnoteAnchorReferenceRefIds" match="internal.reference"  use="@refid" />

	<!-- Render footnotes at the bottom of the output document -->
	<xsl:template match="n-docbody" name="nDocbody">
		<xsl:apply-templates/>
		<xsl:call-template name="RenderFootnoteSection"/>
	</xsl:template>

	<xsl:template name="RenderFootnoteSection">
		<xsl:param name="renderHorizontalRule"/>
		<xsl:if test=".//footnote or .//form.footnote or .//endnote or .//form.endnote">
			<xsl:if test="$renderHorizontalRule">
				<hr class="&horizontalRuleClass;"/>
			</xsl:if>
			<xsl:call-template name="RenderFootnoteSectionMarkup">
				<xsl:with-param name="contents">
					<xsl:apply-templates select=".//footnote | .//form.footnote | .//endnote | .//form.endnote" mode="footnote"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderFootnoteSectionMarkup">
		<xsl:param name="contents"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="RenderFootnoteSectionMarkupTable">
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderFootnoteSectionMarkupDiv">
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderFootnoteSectionMarkupDiv">
		<xsl:param name="contents"/>
		<div id="&footnoteSectionId;" class="&footnoteSectionClass;">
			<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
      </h2>
			<xsl:copy-of select="$contents"/>
		</div>
	</xsl:template>

	<xsl:template name="RenderFootnoteSectionMarkupTable">
		<xsl:param name="contents"/>
		<table id="&footnoteSectionId;" class="&footnoteSectionClass;">
			<tr>
				<td colspan="2" class="&footnoteSectionTitleClass; &printHeadingClass;">
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
        </td>
			</tr>
			<xsl:copy-of select="$contents"/>
		</table>
	</xsl:template>

	<!-- The link down to the footnote -->
	<xsl:template match="footnote.reference | table.footnote.reference | endnote.reference">
		<xsl:variable name="fromBadFootnote">
			<xsl:call-template name="isFirstChildFromBadFootnote" />
		</xsl:variable>

		<xsl:if test="$fromBadFootnote = 'false'">
			<xsl:variable name="refNumberOutputText">
				<xsl:call-template name="footnoteCleanup">
					<xsl:with-param name="refNumberTextParam" select="." />
				</xsl:call-template>
			</xsl:variable>

			<xsl:call-template name="generateLinkToFootnote">
				<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
				<xsl:with-param name="footnoteRef" select="." />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Capture rogue "super" elements that are not contained within "table.footnote.reference"/"footnote.reference"/endnote.reference" elements -->
	<xsl:template match="super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN') or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn')]">
		<xsl:variable name="refNumberOutputText">
			<xsl:call-template name="footnoteCleanup">
				<xsl:with-param name="refNumberTextParam" select="." />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="generateLinkToFootnote">
			<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
			<xsl:with-param name="footnoteRef" select="." />
		</xsl:call-template>
	</xsl:template>

	<!-- Capture rogue "super" elements in footnote elements that do not have "label.designator" elements -->
	<xsl:template match="footnote[not(label.designator)]//super | form.footnote[not(label.designator)]//super | endnote[not(label.designator)]//super | form.endnote[not(label.designator)]//super" priority="1">
		<xsl:variable name="fromBadFootnote">
			<xsl:call-template name="isFirstChildFromBadFootnote" />
		</xsl:variable>

		<xsl:if test="$fromBadFootnote = 'false'">
			<xsl:call-template name="superTemplate" />
		</xsl:if>
	</xsl:template>

	<!-- Put processing instruction for further processing if not in footnote mode -->
	<xsl:template match="footnote | form.footnote | endnote | form.endnote">
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
	</xsl:template>

	<!-- Marks where the actual footnote is in the document -->
	<xsl:template match="footnote | form.footnote | endnote | form.endnote" mode="footnote">
		<!-- This "if" check eliminates duplicate footnotes (or, more precisely, footnotes with duplicate IDs) -->
		<xsl:if test="((not(@ID) and not(@id)) or (@ID and generate-id(.) = generate-id(key('distinctFootnoteIds', @ID))) or (@id and generate-id(.) = generate-id(key('distinctFootnoteIds', @id))))">
			<xsl:variable name="footnoteContent">
				<xsl:choose>
					<xsl:when test="label.designator or para/paratext/label.designator">
						<xsl:if test ="para/paratext/label.designator">
							<xsl:text>&#160;</xsl:text>
						</xsl:if>
						<xsl:apply-templates />
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="firstTextNode" select="descendant::text()[1]" />
						<xsl:variable name="firstTextNodeHasSuperParent" select="string-length($firstTextNode/parent::super) &gt; 0" />
						<xsl:variable name="firstTextNodeIsInternalReference" select="string-length($firstTextNode/parent::internal.reference) &gt; 0" />
						<xsl:variable name="transformedText">
							<xsl:call-template name="trim-start">
								<xsl:with-param name="string">
									<xsl:call-template name="SpecialCharacterTranslator">
										<xsl:with-param name="textToTranslate" select="$firstTextNode" />
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:variable>
						<xsl:variable name="endsWithPeriod">
							<xsl:call-template name="ends-with">
								<xsl:with-param name="string1" select="$transformedText" />
								<xsl:with-param name="string2" select="'.'" />
							</xsl:call-template>
						</xsl:variable>
						<xsl:variable name="normalizedTextBeforeFirstPeriodPlusSpace" select="normalize-space(substring-before($transformedText, '. '))" />
						<xsl:variable name="normalizedTextBeforeFirstPeriodPlusSpaceWithAlphaCharsRemoved" select="translate($normalizedTextBeforeFirstPeriodPlusSpace, '&englishAlphabetUppercaseAndLowercase;', '')" />
						
						<xsl:variable name="refNumberTextParam">
							<xsl:choose>
								<xsl:when test="((starts-with($transformedText, 'FN') or starts-with($transformedText, '[FN') or starts-with($transformedText, '(FN') or starts-with($transformedText, 'fn') or starts-with($transformedText, '[fn') or starts-with($transformedText, '(fn')) and contains($transformedText, ' '))">
									<!-- Fix for CCH - Bug# 418149 
											 This attempts to handle the situation in which there is no space between the footnote reference number and the footnote text.
											 This will return the ref number up the ending bracket or paren (and the following period if it exists), instead of going up to the first space.
									-->
									<xsl:call-template name="GetRefNumberFromFootnote">
										<xsl:with-param name="transformedText" select="$transformedText"/>
									</xsl:call-template>
								</xsl:when>
								
								<!-- This "when" clause is a content fix for Codes - State Reg Text -->
								<xsl:when test="(starts-with($transformedText, 'FN') or starts-with($transformedText, '[FN') or starts-with($transformedText, '(FN') or starts-with($transformedText, 'fn') or starts-with($transformedText, '[fn') or starts-with($transformedText, '(fn')) and $firstTextNodeIsInternalReference = true()">
									<xsl:value-of select="$transformedText" />
								</xsl:when>
								<!-- This "when" clause is a content fix for Caselaw RIA Tax Cases (Bug #17137) -->
								<xsl:when test="(starts-with($transformedText, 'FN') or starts-with($transformedText, 'fn')) and $endsWithPeriod = 'true' and not(contains($transformedText, ' '))">
									<xsl:value-of select="$transformedText" />
								</xsl:when>
								<!-- This "when" clause is a content fix for Caselaw WPADC -->
								<xsl:when test="(string(number($normalizedTextBeforeFirstPeriodPlusSpace)) != 'NaN' and number($normalizedTextBeforeFirstPeriodPlusSpace) &gt; 0) or (not(contains($normalizedTextBeforeFirstPeriodPlusSpace, ' ')) and string-length($normalizedTextBeforeFirstPeriodPlusSpace) &lt;= 2 and string-length($normalizedTextBeforeFirstPeriodPlusSpaceWithAlphaCharsRemoved) &gt; 0)">
									<xsl:value-of select="$normalizedTextBeforeFirstPeriodPlusSpace" />
								</xsl:when>
								<xsl:when test="$firstTextNodeHasSuperParent = true()">
									<xsl:value-of select="$transformedText" />
								</xsl:when>
								<xsl:when test="label">
									<xsl:value-of select="$transformedText" />
								</xsl:when>
							</xsl:choose>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="string-length($refNumberTextParam) &gt; 0">
								<xsl:variable name="refNumberOutputText">
									<xsl:call-template name="footnoteCleanup">
										<xsl:with-param name="refNumberTextParam" select="$refNumberTextParam" />
									</xsl:call-template>
								</xsl:variable>

								<xsl:call-template name="generateLinkBackToFootnoteReference">
									<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
									<xsl:with-param name="footnoteId" select="@ID | @id" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="RenderFootnoteEmptyColumnMarkup"/>
							</xsl:otherwise>
						</xsl:choose>

						<!-- This "choose" statement is a content fix for Admin Codes that needs to stay in place till Nov. 6th, 2008.
						     Confirm with Kathleen Eagan that all Admin Codes have footnote.body (or equivalent) elements at that date.
								 In reality, though, it may not be a bad thing to have in place as this content issue could certainly happen
								 again in the future... hmm, what to do!? -->
						<xsl:choose>
							<xsl:when test="footnote.body or form.footnote.body or endnote.body or form.endnote.body">
								<xsl:apply-templates />
							</xsl:when>
							<xsl:otherwise>
								<xsl:variable name="contents">
									<xsl:apply-templates />
								</xsl:variable>
								<xsl:if test="string-length($contents) &gt; 0">
									<xsl:call-template name="RenderFootnoteBodyMarkup">
										<xsl:with-param name="contents" select="$contents"/>
									</xsl:call-template>
								</xsl:if>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:call-template name="RenderFootnoteMarkup">
				<xsl:with-param name="contents" select="$footnoteContent"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="GetRefNumberFromFootnote">
		<xsl:param name="transformedText" />
		<xsl:variable name="refNumberToBeforeFirstSpace" select="substring-before($transformedText, ' ')" />

		<!-- Attempts special handling of footnotes. Fallback is to stop at the first space. -->
		<xsl:choose>
			<!-- Handles FN(1), (FN*)., etc. with and without a space after the last character (either ')' or '.') -->
			<xsl:when test="contains($transformedText, ')') and (string-length(substring-before($transformedText, ')')) &lt; string-length($refNumberToBeforeFirstSpace))">
				<xsl:choose>
					<xsl:when test="substring($transformedText, string-length(substring-before($transformedText, ')')) + 2, 1) = '.'">
						<xsl:value-of select="substring($transformedText, 1, string-length(substring-before($transformedText, ')')) + 2)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="substring($transformedText, 1, string-length(substring-before($transformedText, ')')) + 1)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<!-- Handles FN[1], [FN*]., etc. with and without a space after the last character (either ']' or '.') -->
			<xsl:when test="contains($transformedText, ']') and (string-length(substring-before($transformedText, ']')) &lt; string-length($refNumberToBeforeFirstSpace))">
				<xsl:choose>
					<xsl:when test="substring($transformedText, string-length(substring-before($transformedText, ']')) + 2, 1) = '.'">
						<xsl:value-of select="substring($transformedText, 1, string-length(substring-before($transformedText, ']')) + 2)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="substring($transformedText, 1, string-length(substring-before($transformedText, ']')) + 1)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<!-- Handles FN1. with or without a space after the '.' and handles FN* that has a space after the last character.-->
			<xsl:when test="(starts-with($transformedText, 'FN') or starts-with($transformedText, 'fn')) and not(contains($refNumberToBeforeFirstSpace, '(') or contains($refNumberToBeforeFirstSpace, '['))">
				<xsl:choose>
					<xsl:when test="contains($transformedText, '.') and (string-length(substring-before($transformedText, '.')) &lt; string-length($refNumberToBeforeFirstSpace))">
						<xsl:value-of select="substring($transformedText, 1, string-length(substring-before($transformedText, '.')) + 1)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$refNumberToBeforeFirstSpace"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$refNumberToBeforeFirstSpace"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderFootnoteMarkup">
		<xsl:param name="contents"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="RenderFootnoteMarkupTable">
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderFootnoteMarkupDiv">
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderFootnoteMarkupDiv">
		<xsl:param name="contents"/>
		<div>
			<xsl:copy-of select="$contents"/>
		</div>
	</xsl:template>

	<xsl:template name="RenderFootnoteMarkupTable">
		<xsl:param name="contents"/>
		<tr>
			<xsl:copy-of select="$contents"/>
		</tr>
	</xsl:template>

	<xsl:template name="RenderFootnoteEmptyColumnMarkup">
		<xsl:param name="contents"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="RenderFootnoteEmptyColumnMarkupTable">
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderFootnoteEmptyColumnMarkupDiv">
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderFootnoteEmptyColumnMarkupDiv"/>

	<xsl:template name="RenderFootnoteEmptyColumnMarkupTable">
		<td>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</td>
	</xsl:template>

	<xsl:template name="RenderFootnoteBodyMarkup">
		<xsl:param name="contents"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="RenderFootnoteBodyMarkupTable">
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderFootnoteBodyMarkupDiv">
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderFootnoteBodyMarkupDiv">
		<xsl:param name="contents"/>
		<div class="&footnoteBodyClass;">
			<xsl:copy-of select="$contents"/>
		</div>
	</xsl:template>

	<xsl:template name="RenderFootnoteBodyMarkupTable">
		<xsl:param name="contents"/>
		<td class="&footnoteBodyClass;">
			<xsl:copy-of select="$contents"/>
		</td>
	</xsl:template>



	<!-- Marks up the title portion of the footnote -->
	<xsl:template match="footnote/label.designator | form.footnote/label.designator | endnote/label.designator | form.endnote/label.designator">
		<xsl:variable name="refNumberOutputText">
			<xsl:call-template name="footnoteCleanup">
				<xsl:with-param name="refNumberTextParam" select="." />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="generateLinkBackToFootnoteReference">
			<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
			<xsl:with-param name="footnoteId" select="../@ID | ../@id" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="footnote.body | form.footnote.body | endnote.body | form.endnote.body">
		<xsl:call-template name="RenderFootnoteBodyMarkup">
			<xsl:with-param name="contents">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="footnote.body//para" priority="2">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="para">
					<xsl:with-param name="className" select="'&deliveryFootnoteParagraph;'"></xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="para" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="footnote.body/para[1]/paratext[1]" priority="1">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:call-template name="renderParagraphTextDiv">
				<xsl:with-param name="contents" select="$contents" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="footnote.body/para[1]/paratext[1]//internal.reference[1]" priority="1">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:call-template name="internalReference">
				<xsl:with-param name="contents" select="$contents" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Cleanup the first text node within footnotes without "label.designator" elements -->
	<xsl:template name="handleFirstTextNodeOfFootnoteWithoutLabelDesignator" match="footnote[not(label.designator)]//text()[generate-id(.) = generate-id(ancestor::footnote/descendant::text()[1])] | form.footnote[not(label.designator)]//text()[generate-id(.) = generate-id(ancestor::form.footnote/descendant::text()[1])] | endnote[not(label.designator)]//text()[generate-id(.) = generate-id(ancestor::endnote/descendant::text()[1])] | form.endnote[not(label.designator)]//text()[generate-id(.) = generate-id(ancestor::form.endnote/descendant::text()[1])]" priority="1">
		<xsl:variable name="notNormalizedText">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="." />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="footnoteWithInternalLink" select="ancestor::node()[self::footnote]//internal.reference/text()[1]" />
		<xsl:variable name="normalizedText" select="normalize-space($notNormalizedText)" />
		<xsl:variable name="endsWithPeriod">
			<xsl:call-template name="ends-with">
				<xsl:with-param name="string1" select="$notNormalizedText" />
				<xsl:with-param name="string2" select="'.'" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="normalizedTextBeforeFirstPeriodPlusSpace" select="substring-before($normalizedText, '. ')" />
		<xsl:variable name="normalizedTextBeforeFirstPeriodPlusSpaceWithAlphaCharsRemoved" select="translate($normalizedTextBeforeFirstPeriodPlusSpace, '&englishAlphabetUppercaseAndLowercase;', '')" />
		<xsl:variable name="normalizedTextAfterFirstPeriodPlusSpace" select="substring-after($normalizedText, '. ')" />

		<xsl:choose>
			<!-- The space check must look at the "not normalized" text as any spaces on the end would be removed -->
			<xsl:when test="((starts-with($normalizedText, 'FN') or starts-with($normalizedText, '[FN') or starts-with($normalizedText, '(FN') or starts-with($normalizedText, 'fn') or starts-with($normalizedText, '[fn') or starts-with($normalizedText, '(fn')) and contains($notNormalizedText, ' ')) or starts-with($notNormalizedText, '. ') or starts-with($notNormalizedText, ' ')">
				<xsl:call-template name="normalize-space-without-trimming">
					<xsl:with-param name="string">
						<xsl:call-template name="trim-start">
							<xsl:with-param name="string">
								<xsl:variable name="ExtractedRefNumber">
									<xsl:call-template name="GetRefNumberFromFootnote">
										<xsl:with-param name="transformedText" select="$notNormalizedText"/>
									</xsl:call-template>
								</xsl:variable>
								<xsl:value-of select="substring-after($notNormalizedText, $ExtractedRefNumber)" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<!-- This "when" clause is a content fix for Caselaw RIA Tax Cases (Bug #17137) -->
			<xsl:when test="(starts-with($normalizedText, 'FN') or starts-with($normalizedText, 'fn')) and $endsWithPeriod = 'true' and not(contains($notNormalizedText, ' '))">
				<!-- Do nothing... this entire text node should have been a footnote label designator -->
			</xsl:when>
			<!-- This "when" clause is a content fix for Codes - State Reg Text -->
			<xsl:when test="(starts-with($normalizedText, '[FN') or starts-with($normalizedText, '[fn') or starts-with($normalizedText, '(FN') or starts-with($normalizedText, '(fn')) and string-length($footnoteWithInternalLink) &gt; 0">
				<xsl:call-template name="removeFootnoteMarkup">
					<xsl:with-param name="textWithFootnoteParam" select="$normalizedText"/>
				</xsl:call-template>
			</xsl:when>
			<!-- This "when" clause is a content fix for Caselaw WPADC -->
			<xsl:when test="(string(number($normalizedTextBeforeFirstPeriodPlusSpace)) != 'NaN' and number($normalizedTextBeforeFirstPeriodPlusSpace) &gt; 0) or (not(contains($normalizedTextBeforeFirstPeriodPlusSpace, ' ')) and string-length($normalizedTextBeforeFirstPeriodPlusSpace) &lt;= 2 and string-length($normalizedTextBeforeFirstPeriodPlusSpaceWithAlphaCharsRemoved) &gt; 0)">
				<xsl:call-template name="trim-start">
					<xsl:with-param name="string" select="$normalizedTextAfterFirstPeriodPlusSpace" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$notNormalizedText" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Cleanup the first text node within footnotes WITH "label.designator" elements -->
	<xsl:template name="handleFirstTextNodeOfFootnoteWithLabelDesignator" match="footnote[label.designator]/footnote.body//text()[generate-id(.) = generate-id(ancestor::footnote.body/descendant::text()[1])] | form.footnote[label.designator]/form.footnote.body//text()[generate-id(.) = generate-id(ancestor::form.footnote.body/descendant::text()[1])] | endnote[label.designator]/endnote.body//text()[generate-id(.) = generate-id(ancestor::endnote.body/descendant::text()[1])] | form.endnote[label.designator]/form.endnote.body//text()[generate-id(.) = generate-id(ancestor::form.endnote.body/descendant::text()[1])]" priority="1">
		<xsl:variable name="notNormalizedText">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="." />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="normalizedText" select="normalize-space($notNormalizedText)" />
		<xsl:variable name="outputText">
			<xsl:choose>
				<xsl:when test="starts-with($normalizedText, '.')">
					<xsl:value-of select="substring-after($notNormalizedText, '.')" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$notNormalizedText" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- Trim and normalize all but any whitespace at the end of the text node -->
		<xsl:call-template name="normalize-space-without-trimming">
			<xsl:with-param name="string">
				<xsl:call-template name="trim-start">
					<xsl:with-param name="string" select="$outputText" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Strip off the '[' or '(' and 'FN' prefixes and the ']' or ')' and '.' suffixes if present, e.g. in the expected order of '[FN1.]' or '(FN1.)' -->
	<!-- There are two extra steps added [which are marked with comments] as a content fix for Caselaw RIA Tax Cases (Bug #17137).  These designators are in the form of 'FN(1).' -->
	<xsl:template name="footnoteCleanup">
		<xsl:param name="refNumberTextParam"/>

		<xsl:variable name="refNumberText">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="$refNumberTextParam" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="refNumberTextNormalized">
			<xsl:value-of select="normalize-space($refNumberText)"/>
		</xsl:variable>
		<xsl:variable name="refNumberTextAfterBracketPrefix">
			<xsl:choose>
				<xsl:when test="starts-with($refNumberTextNormalized, '[')">
					<xsl:value-of select="substring-after($refNumberTextNormalized, '[')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$refNumberTextNormalized"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="refNumberTextAfterParenthesisPrefix">
			<xsl:choose>
				<xsl:when test="starts-with($refNumberTextAfterBracketPrefix, '(')">
					<xsl:value-of select="substring-after($refNumberTextAfterBracketPrefix, '(')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$refNumberTextAfterBracketPrefix"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="refNumberTextAfterFNPrefix">
			<xsl:choose>
				<xsl:when test="starts-with($refNumberTextAfterParenthesisPrefix, 'FN')">
					<xsl:value-of select="substring-after($refNumberTextAfterParenthesisPrefix, 'FN')"/>
				</xsl:when>
				<xsl:when test="starts-with($refNumberTextAfterParenthesisPrefix, 'fn')">
					<xsl:value-of select="substring-after($refNumberTextAfterParenthesisPrefix, 'fn')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$refNumberTextAfterParenthesisPrefix"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- This "variable" step is a content fix for Caselaw RIA Tax Cases (Bug #17137) -->
		<xsl:variable name="refNumberTextAfterParenthesisInnerPrefix">
			<xsl:choose>
				<xsl:when test="starts-with($refNumberTextAfterFNPrefix, '(')">
					<xsl:value-of select="substring-after($refNumberTextAfterFNPrefix, '(')"/>
				</xsl:when>
				<!-- Fix for CCH (Bug #418149)-->
				<xsl:when test="starts-with($refNumberTextAfterFNPrefix, '[')">
					<xsl:value-of select="substring-after($refNumberTextAfterFNPrefix, '[')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$refNumberTextAfterFNPrefix"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="refNumberBeforeBracketSuffix">
			<xsl:choose>
				<xsl:when test="substring($refNumberTextAfterParenthesisInnerPrefix, string-length($refNumberTextAfterParenthesisInnerPrefix)) = ']'">
					<xsl:value-of select="substring($refNumberTextAfterParenthesisInnerPrefix, 1, string-length($refNumberTextAfterParenthesisInnerPrefix) - 1)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$refNumberTextAfterParenthesisInnerPrefix"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="refNumberBeforeParenthesisSuffix">
			<xsl:choose>
				<xsl:when test="substring($refNumberBeforeBracketSuffix, string-length($refNumberBeforeBracketSuffix)) = ')'">
					<xsl:value-of select="substring($refNumberBeforeBracketSuffix, 1, string-length($refNumberBeforeBracketSuffix) - 1)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$refNumberBeforeBracketSuffix"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="refNumberBeforePeriod">
			<xsl:choose>
				<xsl:when test="substring($refNumberBeforeParenthesisSuffix, string-length($refNumberBeforeParenthesisSuffix)) = '.'">
					<xsl:value-of select="substring($refNumberBeforeParenthesisSuffix, 1, string-length($refNumberBeforeParenthesisSuffix) - 1)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$refNumberBeforeParenthesisSuffix"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- This "variable" step is a content fix for Caselaw RIA Tax Cases (Bug #17137) -->
		<xsl:variable name="refNumberTextBeforeParenthesisInnerSuffix">
			<xsl:choose>
				<xsl:when test="substring($refNumberBeforePeriod, string-length($refNumberBeforePeriod)) = ')'">
					<xsl:value-of select="substring($refNumberBeforePeriod, 1, string-length($refNumberBeforePeriod) - 1)"/>
				</xsl:when>
				<!-- Fix for CCH (Bug #418149) -->
				<xsl:when test="substring($refNumberBeforePeriod, string-length($refNumberBeforePeriod)) = ']'">
					<xsl:value-of select="substring($refNumberBeforePeriod, 1, string-length($refNumberBeforePeriod) - 1)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$refNumberBeforePeriod"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="refNumberOutputText" select="$refNumberTextBeforeParenthesisInnerSuffix" />

		<xsl:value-of select="$refNumberOutputText"/>
	</xsl:template>

	<xsl:template name="generateLinkBackToFootnoteReference">
		<xsl:param name="refNumberText" select="''" />
		<xsl:param name="footnoteId" select="''" />
		<xsl:param name="pertinentFootnote" select="ancestor-or-self::node()[self::footnote or self::form.footnote or self::endnote or self::form.endnote][1]" />

		<xsl:if test="string-length($refNumberText) &gt; 0">
			<xsl:variable name="contents">
				<!-- Make a special call to insert calculated page numbers to handle the normal displacement of footnotes -->
				<xsl:apply-templates select="$pertinentFootnote" mode="starPageCalculation" />

				<span>
					<!-- Sometimes the footnote id isn't correct (doesn't match what the footnote.reference is referencing), in that case 
							 check to see if we can use the anchor elements's ID attribute -->
					<xsl:variable name="updatedFootnoteId">
						<xsl:choose>
							<!-- This checks to see if the footnote id is actually being referenced by a <footnote.reference> element. 
									 If it's not referenced, and there is an anchor child with a valid id that IS being referenced, then use that as the id.-->
							<xsl:when test="not(key('distinctFootnoteReferenceRefIds', $footnoteId)) and key('distinctFootnoteReferenceRefIds', .//anchor/@ID)">
								<xsl:value-of select=".//anchor/@ID"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$footnoteId"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:if test="string-length($updatedFootnoteId) &gt; 0" >
						<xsl:attribute name="id">
							<xsl:value-of select="concat('&footnoteIdPrefix;', $updatedFootnoteId)"/>
						</xsl:attribute>
					</xsl:if>

					<xsl:choose>
						<xsl:when test="not(/*[1]/self::summary or /*[1]/self::summaries) and string-length($updatedFootnoteId) &gt; 0 and (/Document//footnote.reference/@ID=$updatedFootnoteId or /Document//footnote.reference/@refid=$updatedFootnoteId or /Document//internal.reference/@refid=$updatedFootnoteId) ">
							<xsl:choose>
								<xsl:when test="string-length(key('distinctFootnoteReferenceRefIds', $updatedFootnoteId)) &gt; 0">
									<a href="#co_footnoteReference_{$updatedFootnoteId}_{generate-id(key('distinctFootnoteReferenceRefIds', $updatedFootnoteId)[1])}">
										<xsl:value-of select="$refNumberText"/>
									</a>
								</xsl:when>
								<xsl:when test="string-length(key('distinctFootnoteAnchorReferenceRefIds', $updatedFootnoteId)) &gt; 0">
									<a href="#co_footnoteReference_{$updatedFootnoteId}">
										<xsl:value-of select="$refNumberText"/>
									</a>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$refNumberText"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="/Document//super[starts-with(normalize-space(descendant-or-self::text()), concat('[FN',$refNumberText))]">
							<xsl:variable name="tableIdPrefix" select="ancestor::tbl/@ID"></xsl:variable>
							<a id="co_tablefootnoteblock_{concat($tableIdPrefix, translate($refNumberText,'*','s'))}" href="#co_tablefootnote_{concat($tableIdPrefix, translate($refNumberText,'*','s'))}">
								<xsl:value-of select="$refNumberText"/>
							</a>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$refNumberText"/>
						</xsl:otherwise>
					</xsl:choose>
				</span>
			</xsl:variable>
			<xsl:call-template name="RenderFootnoteNumberMarkup">
				<xsl:with-param name="contents" select="$contents"/>
				<xsl:with-param name="refNumberText" select="$refNumberText"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderFootnoteNumberMarkup">
		<xsl:param name="contents"/>
		<xsl:param name="refNumberText" select="''"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="RenderFootnoteNumberMarkupTable">
					<xsl:with-param name="contents" select="$contents"/>
					<xsl:with-param name="refNumberText" select="$refNumberText"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderFootnoteNumberMarkupDiv">
					<xsl:with-param name="contents" select="$contents"/>
					<xsl:with-param name="refNumberText" select="$refNumberText"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderFootnoteNumberMarkupDiv">
		<xsl:param name="contents"/>
		<xsl:param name="refNumberText"/>
		<xsl:choose>
		  <xsl:when test="string-length($refNumberText) &gt; 3">
			<div class="&footnoteNumberLargeClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		  </xsl:when>
		  <xsl:otherwise>
			<div class="&footnoteNumberClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		  </xsl:otherwise>
		</xsl:choose>    
	</xsl:template>

	<xsl:template name="RenderFootnoteNumberMarkupTable">
		<xsl:param name="contents"/>
    <xsl:param name="refNumberText"/>
    <xsl:choose>
      <xsl:when test="string-length($refNumberText) &gt; 3">
        <td class="&footnoteNumberLargeClass;">
          <xsl:copy-of select="$contents"/>
        </td>
      </xsl:when>
      <xsl:otherwise>
        <td class="&footnoteNumberClass;">
          <xsl:copy-of select="$contents"/>
        </td>
      </xsl:otherwise>
    </xsl:choose>
	</xsl:template>

	<xsl:template name="generateLinkToFootnote">
		<xsl:param name="refNumberText" select="''" />
		<xsl:param name="footnoteRef" select="." />

		<xsl:if test="not($EasyEditMode)">
			<xsl:if test="string-length($refNumberText) &gt; 0">
				<xsl:choose>
					<xsl:when test="not(/*[1]/self::summary or /*[1]/self::summaries) and string-length($footnoteRef/@refid) &gt; 0 and (string-length(key('distinctFootnoteIds', $footnoteRef/@refid)) &gt; 0 or count(key('distinctAlternateFootnoteIds', $footnoteRef/@refid)) &gt; 0)">
						<sup>
							<xsl:attribute name="id">
								<xsl:value-of select="concat('&footnoteReferenceIdPrefix;', $footnoteRef/@refid, '_', generate-id($footnoteRef))"/>
							</xsl:attribute>
							<a href="#co_footnote_{$footnoteRef/@refid}" class="&footnoteReferenceClass;">
								<xsl:value-of select="$refNumberText"/>
							</a>
						</sup>
					</xsl:when>
					<xsl:when test="(following-sibling::internal.reference[1]/@refid)">
						<sup>
							<xsl:attribute name="id">
								<xsl:value-of select="concat('&footnoteReferenceIdPrefix;', following-sibling::internal.reference/@refid)"/>
							</xsl:attribute>
							<xsl:value-of select="$refNumberText"/>
						</sup>
					</xsl:when>
					<xsl:when test="starts-with(normalize-space(descendant-or-self::text()),'[FN') and /Document//footnote[contains(normalize-space(descendant-or-self::text()),concat('FN',$refNumberText))]">
						<xsl:variable name="tableIdPrefix" select="ancestor::tbl/@ID"></xsl:variable>
						<sup>
							<xsl:attribute name="id">
								<xsl:value-of select="concat('co_tablefootnote_', concat($tableIdPrefix, translate($refNumberText,'*','s')) )"/>
							</xsl:attribute>
							<a href="#co_tablefootnoteblock_{concat($tableIdPrefix,translate($refNumberText,'*','s'))}" class="&footnoteReferenceClass;">
								<xsl:value-of select="$refNumberText"/>
							</a>
						</sup>
					</xsl:when>
					<xsl:otherwise>
						<sup>
							<xsl:value-of select="$refNumberText"/>
						</sup>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="isFirstChildFromBadFootnote">
		<xsl:choose>
			<xsl:when test="parent::paratext/parent::para/parent::footnote.body/parent::footnote and not(preceding-sibling::node()[not(self::bop or self::bos or self::eos or self::eop)])">
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:when test="parent::paratext/parent::para/parent::form.footnote.body/parent::form.footnote and not(preceding-sibling::node()[not(self::bop or self::bos or self::eos or self::eop)])">
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:when test="parent::paratext/parent::para/parent::endnote.body/parent::endnote and not(preceding-sibling::node()[not(self::bop or self::bos or self::eos or self::eop)])">
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:when test="parent::paratext/parent::para/parent::form.endnote.body/parent::form.endnote and not(preceding-sibling::node()[not(self::bop or self::bos or self::eos or self::eop)])">
				<xsl:value-of select="true()" />
			</xsl:when>
			<!-- The below "when" statements are only there for bad Admin Codes content, e.g. NF80D0AC0CE2F11DA855BCC9AEBC226E6
				     and should be removed by November 6th, 2008, when the content SHOULD be corrected according to Kathleen Eagan. 
						 Verify that the content IS corrected before removing this! -->
			<xsl:when test="parent::paratext/parent::para/parent::footnote and not(preceding-sibling::node()[not(self::bop or self::bos or self::eos or self::eop)])">
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:when test="parent::paratext/parent::para/parent::form.footnote and not(preceding-sibling::node()[not(self::bop or self::bos or self::eos or self::eop)])">
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:when test="parent::paratext/parent::para/parent::endnote and not(preceding-sibling::node()[not(self::bop or self::bos or self::eos or self::eop)])">
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:when test="parent::paratext/parent::para/parent::form.endnote and not(preceding-sibling::node()[not(self::bop or self::bos or self::eos or self::eop)])">
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Suppress footnote references from title metadata -->
	<xsl:template match="/Document/document-data/title//footnote.reference | /Document/document-data/title//table.footnote.reference | /Document/document-data/title//endnote.reference" priority="2" />

	<!-- Capture rogue "super" elements that are not contained within "table.footnote.reference"/"footnote.reference"/endnote.reference" elements -->
	<xsl:template match="/Document/document-data/title//super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN') or starts-with(normalize-space(text()), '(FN') or starts-with(normalize-space(text()), 'fn') or starts-with(normalize-space(text()), '[fn') or starts-with(normalize-space(text()), '(fn')]" priority="2" />

</xsl:stylesheet>
