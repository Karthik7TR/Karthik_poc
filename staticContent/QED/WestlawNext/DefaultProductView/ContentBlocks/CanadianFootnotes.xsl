<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:DocumentExtension="urn:documentExtension" extension-element-prefixes="DocumentExtension">
	<xsl:include href="CanadianGlobalParams.xsl"/>
	<xsl:include href="Footnotes.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="n-docbody" name="nDocbody" priority="1"/>

	<!--Add a has footnote here-->
	<xsl:template name="HasFootnote">
		<xsl:choose>
			<!-- Platform style footnotes -->
			<xsl:when test="//footnote or .//form.footnote or .//endnote or .//form.endnote">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<!-- Carswell style footnotes -->
			<!-- '^r\d+$' only matches r1, r2, r13 etc. -->
			<xsl:when test="//sup/a[starts-with(@name, 'r')] or //a[DocumentExtension:IsMatch(@href, '^r\d+$')]">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Direct override of platform, checks both WLN style footnotes and CRSW footnotes-->
	<xsl:template name="RenderFootnoteSection" priority="1">
		<xsl:param name="renderHorizontalRule"/>
		<xsl:param name ="title" select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>

		<xsl:variable name="hasFootnote">
			<xsl:call-template name="HasFootnote"/>
		</xsl:variable>

		<xsl:if test="$hasFootnote != 'false'">
			<xsl:if test="$renderHorizontalRule">
				<hr class="&horizontalRuleClass;"/>
			</xsl:if>
			<!--RenderFootnoteSectionMarkup will either call RenderFootnoteSectionMarkupDiv or RenderFootnoteSectionMarkupTable for delivery-->
			<xsl:call-template name="RenderFootnoteSectionMarkup">
				<xsl:with-param name="title" select="$title"/>
				<xsl:with-param name="contents">
					<xsl:choose>
						<!--Platform style footnotes.-->
						<xsl:when test=".//footnote or .//form.footnote or .//endnote or .//form.endnote">
							<xsl:apply-templates select=".//footnote | .//form.footnote | .//endnote | .//form.endnote" mode="footnote"/>
						</xsl:when>
						<xsl:otherwise>
							<!--Carswell style footnotes-->
							<xsl:apply-templates select=" .//a[starts-with(@name, 'f')]"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--Direct override of platform, added the title parameter-->
	<xsl:template name="RenderFootnoteSectionMarkup" priority="1">
		<xsl:param name ="title" select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
		<xsl:param name="contents"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="RenderFootnoteSectionMarkupTable">
					<xsl:with-param name="title" select="$title"/>
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderFootnoteSectionMarkupDiv">
					<xsl:with-param name="title" select="$title"/>
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Direct override of platform, added the title parameter-->
	<xsl:template name="RenderFootnoteSectionMarkupDiv" priority="1">
		<xsl:param name ="title" select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
		<xsl:param name="contents"/>
		<div id="&footnoteSectionId;" class="&footnoteSectionClass;">
			<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
				<xsl:value-of select="$title"/>
			</h2>
			<xsl:copy-of select="$contents"/>
		</div>
	</xsl:template>

	<!--Direct override of platform, added the title parameter-->
	<xsl:template name="RenderFootnoteSectionMarkupTable" priority="1">
		<xsl:param name ="title" select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
		<xsl:param name="contents"/>
		<table id="&footnoteSectionId;" class="&footnoteSectionClass;">
			<tr>
				<td colspan="2" class="&footnoteSectionTitleClass; &printHeadingClass;">
					<xsl:value-of select="$title"/>
				</td>
			</tr>
			<xsl:copy-of select="$contents"/>
		</table>
	</xsl:template>

	<!-- The link down to the footnote for Carswell -->
	<xsl:template match="sup/a[starts-with(@name, 'r')] | footnote.reference" priority="2">
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
		<xsl:variable name="footnoteId">
			<xsl:choose>
				<xsl:when test="@refid">
					<xsl:value-of select="translate(concat('&crswFootnoteIdPrefix;', @refid),'&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;')"/>
				</xsl:when>
				<xsl:when test="starts-with(@href,'#')">
					<xsl:value-of select="concat('&crswFootnoteIdPrefix;', translate(substring(@href, 2), '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;'))"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('&crswFootnoteIdPrefix;', translate(@href, '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;'))"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!--Add processing instructions for inline footnote, the value should equal the id of the footnote at the bottom of the page-->
		<xsl:if test="$DeliveryMode">
			<xsl:processing-instruction name="inlineFootnote">
				<xsl:value-of select="$footnoteId" />
			</xsl:processing-instruction>
		</xsl:if>
	</xsl:template>

	<!--Overrides platform generateLinkToFootnote-->
	<xsl:template name="generateLinkToFootnote" priority="1">
		<xsl:param name="refNumberText" select="''" />
		<xsl:param name="footnoteRef" select="." />

		<xsl:if test="not($EasyEditMode)">
			<xsl:if test="string-length($refNumberText) &gt; 0">
				<xsl:choose>
					<!--Platform style footnotes-->
					<xsl:when test="not(/*[1]/self::summary or /*[1]/self::summaries) and string-length($footnoteRef/@refid) &gt; 0 and string-length(key('distinctFootnoteIds', $footnoteRef/@refid)) &gt; 0">
						<sup>
							<xsl:attribute name="id">
								<xsl:value-of select="translate(concat('&crswfootnoteReferenceIdPrefix;', $footnoteRef/@refid, '_', generate-id($footnoteRef)),'&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;')"/>
							</xsl:attribute>
							<a>
								<xsl:attribute name="href">
									<xsl:value-of select="translate(concat('#&crswFootnoteIdPrefix;', $footnoteRef/@refid),'&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;')"/>
								</xsl:attribute>
								<xsl:attribute name="class">
									<xsl:value-of select="'&footnoteReferenceClass;'"/>
								</xsl:attribute>
								<xsl:value-of select="$refNumberText"/>
							</a>
						</sup>
					</xsl:when>
					<!--Platform style footnotes-->
					<xsl:when test="(following-sibling::internal.reference[1]/@refid)">
						<sup>
							<xsl:attribute name="id">
								<xsl:value-of select="translate(concat('&crswfootnoteReferenceIdPrefix;', following-sibling::internal.reference/@refid),'&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;')"/>
							</xsl:attribute>
							<xsl:value-of select="$refNumberText"/>
						</sup>
					</xsl:when>
					<!--Carswell style footnotes-->
					<xsl:when test="starts-with(@href, 'f') ">
						<sup>
							<xsl:attribute name="id">
								<xsl:value-of select="concat('&crswFootnoteIdPrefix;', translate(@name, '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;'))"/>
							</xsl:attribute>
							<a>
								<xsl:attribute name="href">
									<xsl:value-of select="concat('#&crswFootnoteIdPrefix;', translate(substring(@href, 1), '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;'))"/>
								</xsl:attribute>
								<xsl:attribute name="class">
									<xsl:value-of select="'&footnoteReferenceClass;'"/>
								</xsl:attribute>
								<xsl:value-of select="$refNumberText"/>
							</a>
						</sup>
					</xsl:when>
					<xsl:when test="starts-with(@name, 'r')">
						<sup>
							<xsl:attribute name="id">
								<xsl:value-of select="concat('&crswFootnoteIdPrefix;', translate(@name, '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;'))"/>
							</xsl:attribute>
							<a>
								<xsl:attribute name="href">
									<xsl:value-of select="concat('#&crswFootnoteIdPrefix;', translate(substring(@href, 2), '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;'))"/>
								</xsl:attribute>
								<xsl:attribute name="class">
									<xsl:value-of select="'&footnoteReferenceClass;'"/>
								</xsl:attribute>
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

	<!--Renderes the footnote markup for Carswell, calls RenderFootnoteMarkupCRSW which is similar to platform RenderFootnoteMarkup-->
	<!-- only match f1, f2, f3, etc. or f*,  'f' followed by one or more digits or f followed by * -->
	<xsl:template match=" a[DocumentExtension:IsMatch(@name, '^f[\d\*]+$')] | a[starts-with(@href, 'N_') and substring(@href,string-length(@href)-1)='_']" priority="1">
		<xsl:call-template name="RenderFootnoteMarkupCRSW">
			<xsl:with-param name="contents" select="*"/>
		</xsl:call-template>
	</xsl:template>

	<!--Similar to RenderFootnoteMarkup from platform but calls Carswell specific RenderFootnoteMarkupTableCRSW and RenderFootnoteMarkupDivCRSW-->
	<xsl:template name="RenderFootnoteMarkupCRSW">
		<xsl:param name="contents"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="RenderFootnoteMarkupTableCRSW">
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderFootnoteMarkupDivCRSW">
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Similar to RenderFootnoteMarkupDiv in platform but calls RenderSingleFootnoteNumberAndBody-->
	<xsl:template name="RenderFootnoteMarkupDivCRSW">
		<xsl:param name="contents"/>
		<div>
			<xsl:call-template name="RenderSingleFootnoteNumberAndBody">
				<xsl:with-param name="contents">
					<xsl:apply-templates select="$contents"/>
				</xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!--Similar to RenderFootnoteMarkupTable in platform but calls RenderSingleFootnoteNumberAndBody-->
	<xsl:template name="RenderFootnoteMarkupTableCRSW">
		<xsl:param name="contents"/>
		<tr>
			<xsl:call-template name="RenderSingleFootnoteNumberAndBody">
				<xsl:with-param name="contents">
					<xsl:apply-templates select="$contents"/>
				</xsl:with-param>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<!--Render the footnote number and body. Carswell does not have a footnote body tag-->
	<xsl:template name="RenderSingleFootnoteNumberAndBody">
		<xsl:variable name="refNumberOutputText">
			<xsl:call-template name="footnoteCleanup">
				<xsl:with-param name="refNumberTextParam" select="." />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="cleanedFootnoteId">
			<xsl:call-template name="footnoteCleanup">
				<xsl:with-param name="refNumberTextParam" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="generateLinkBackToFootnoteReference">
			<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
			<xsl:with-param name="footnoteId" select="$cleanedFootnoteId" />
		</xsl:call-template>

		<xsl:call-template name="RenderFootnoteBodyMarkup">
			<xsl:with-param name="contents">
				<xsl:choose>
					<!-- Footnote whose anchor is not w/in a sup -->
					<xsl:when test="name(../.) != 'sup'">
						<!--We strip out the p tag so we need to add it back in for when we have multiple paragraphs in a footnote -->
						<xsl:call-template name="wrapWithDiv">
							<xsl:with-param name="class" select="'&paraMainClass;'"/>
							<xsl:with-param name="contents">
								<xsl:apply-templates select="following-sibling::node()"/>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>

					<!-- Footnote whose anchor is within a sup/cite.query -->
					<!-- Include the following-siblings of the <sup> (parent) and the <cite.query> (grandparent), workaround for bug 573856-->
					<xsl:when test="name(../.) = 'sup' and name(../../.) = 'cite.query'">
						<xsl:apply-templates select="../following-sibling::node()"/>
						<xsl:apply-templates select="../../following-sibling::node()" />						
					</xsl:when>
					<xsl:otherwise>
						<!-- Include following-siblings of the sup (parent) -->
						<xsl:apply-templates select="../following-sibling::node()"/>						
					</xsl:otherwise>
				</xsl:choose>
				<!-- There may be more information to the body of the footnote, extend markup based on content type -->
        <xsl:call-template name="RenderExtendedFootnoteBodyMarkup" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="RenderExtendedFootnoteBodyMarkup">

		<!-- Only apply extended footnote processing for content types that require it -->
		<!-- :WORKAROUND: Would be nicer to have Content Type .xsl be able to specify this but that requires plumbing something down through several layers of platform.
         Workaround is to check against content types -->
		<xsl:choose>
      <xsl:when test="$Doc-Type/text() = 'CA - Text And Annotation' or $Doc-Type/text() ='CA - Legislation - Regs' or $Doc-Type/text() ='CA - Legislative Concordances'">

			<!-- Footnotes are identified p/sup/a where the anchor is the link back to the footnote reference. -->
			<!-- Some content types also have content (e.g. ul's and p's) that occur as siblings to the footnote's containing p element. -->
			<!-- All of these siblings that occur before the next footnote (e.g. next p/sup/a) are considered part of the footnotes content and must be included. -->
			<xsl:variable name="anchorName" select="@name" />

			<!-- 1. Select union of footnote's p (grandparent) and that p's following-siblings -->
			<!-- 2. Filter union to only those nodes where -->
			<!--    a. the first previous p sibling is contains the current footnote anchor (i.e. is the current footnote)-->
			<!--    b. and following sibling itself does not contain a footnote sup/a (i.e. is not a footnote) -->
        <xsl:apply-templates select="(../.. | ../../following-sibling::node())[./preceding-sibling::p[.//sup/a][1]//sup/a[@name=$anchorName] and not(.//sup/a)]" />

		</xsl:when>
			<!-- Footnote does not have a sup tag but multiple p elements without an anchor make up the footnote -->
      <xsl:when test="$Doc-Type/text() = 'CA - CED'">
        <!-- Footnotes are identified p/a where the anchor is the link back to the footnote reference. -->
        <!-- Some content types also have content (e.g. ul's and p's) that occur as siblings to the footnote's containing p element. -->
        <!-- All of these siblings that occur before the next footnote (e.g. next p/a) are considered part of the footnotes content and must be included. -->
        <xsl:variable name="anchorName" select="@name" />

        <!-- 1. Select union of p's following-siblings -->
        <!-- 2. Filter union to only those nodes where -->
        <!--    a. the first previous p sibling with a footnote contains the current footnote anchor (i.e. is the current footnote)-->
        <!--    b. and following sibling itself does not contain a footnote a (i.e. is not a footnote) -->
        <xsl:apply-templates select="(../following-sibling::node())[./preceding-sibling::p[./a][1]//a[@name=$anchorName] and not(./a)]" />
      </xsl:when>
    </xsl:choose>
	</xsl:template>

	<!---Overrides platform specific generateLinkBackToFootnoteReference-->
	<xsl:template name="generateLinkBackToFootnoteReference" priority="1">
		<xsl:param name="refNumberText" select="''" />
		<xsl:param name="footnoteId" select="''" />
		<xsl:param name="pertinentFootnote" select="ancestor-or-self::node()[self::footnote or self::form.footnote or self::endnote or self::form.endnote][1]" />

		<xsl:variable name="footnoteIdFixed">
			<xsl:value-of select="translate($footnoteId, '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;')"/>
		</xsl:variable>

		<xsl:if test="string-length($refNumberText) &gt; 0">
			<xsl:variable name="contents">
				<!-- Make a special call to insert calculated page numbers to handle the normal displacement of footnotes -->
				<xsl:apply-templates select="$pertinentFootnote" mode="starPageCalculation" />
				<span>
					<xsl:if test="$footnoteIdFixed">
						<xsl:attribute name="id">
							<xsl:value-of select="concat('&crswFootnoteIdPrefix;', $footnoteIdFixed)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:choose>
						<!--Platform style footnotes-->
						<xsl:when test="not(/*[1]/self::summary or /*[1]/self::summaries) and string-length($footnoteId) &gt; 0">
							<xsl:choose>
								<xsl:when test="string-length(key('distinctFootnoteReferenceRefIds', $footnoteId)) &gt; 0">
									<a href="#&crswfootnoteReferenceIdPrefix;{$footnoteIdFixed}_{generate-id(key('distinctFootnoteReferenceRefIds', $footnoteId)[1])}">
										<xsl:value-of select="$refNumberText"/>
									</a>
								</xsl:when>
								<!--Carswell style footnotes-->
								<xsl:when test="starts-with(@href, 'r')">
									<a>
										<xsl:attribute name="href">
											<xsl:value-of select="concat('#&crswFootnoteIdPrefix;', translate(substring(@href, 1), '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;'))"/>
										</xsl:attribute>
										<xsl:value-of select="$refNumberText"/>
									</a>
								</xsl:when>
								<xsl:when test="starts-with(@name, 'f')">
									<a>
										<xsl:attribute name="href">
											<xsl:value-of select="concat('#&crswFootnoteIdPrefix;', translate(substring(@href, 2), '&crswBadHtmlIdCharacters;', '&crswGoodHtmlIdCharacters;'))"/>
										</xsl:attribute>
										<xsl:value-of select="$refNumberText"/>
									</a>
								</xsl:when>
								<!--Platform style footnotes-->
								<xsl:when test="string-length(key('distinctFootnoteAnchorReferenceRefIds', $footnoteId)) &gt; 0">
									<a href="#&crswfootnoteReferenceIdPrefix;{$footnoteIdFixed}">
										<xsl:value-of select="$refNumberText"/>
									</a>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$refNumberText"/>
								</xsl:otherwise>
							</xsl:choose>
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

	<!---Overrides platform specific Footnote Paragraph-->
	<xsl:template match="footnote.body//para" priority="3">
		<xsl:call-template name="para" />
	</xsl:template>

</xsl:stylesheet>
