<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Prelim.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- This overrides a variable in StarPages.xsl to as this content does not always have types associated with the citations -->
	<xsl:variable name="eligibleCites" select="$displayableCitesWithoutStatus[@ID = $eligiblePagesets/@pageset and ((@type != 'Westlaw') or not(@type))]" />

	<xsl:variable name="judgmentDate" select="/Document/n-docbody/case.head/date.group/date.line/date[@significance='judgment']" />
	<xsl:variable name="primaryCitation" select="/Document/n-docbody/case.head/citation.group/primary.citation" />
	<xsl:variable name="courtAbbreviation" select="/Document/n-docbody/case.head/court.line/court/@abbrev" />
	<xsl:variable name="infoType" select="/Document/n-metadata//md.infotype"/>
	<!--InfoType Values (Updated to accomodate MAF)-->
	<xsl:variable name="reportedInfoType" select="'hk-reported'"/>
	<xsl:variable name="unReportedInfoType" select="'hk-unreported'"/>
	<xsl:variable name="digestInfoType" select="'hk-digest'"/>
	<xsl:variable name="cfaInfoType" select="'hk-cfa'"/>
	<xsl:variable name="cnsInfoType" select="'hk-cns'"/>
	<xsl:variable name="currentAwarenessInfoType" select="'hk-ca'"/>
	<xsl:variable name="otherInfoType" select="'hk-other'"/>
	<xsl:variable name="hkCaseLocatorInfoType" select="'hk-caseloc'"/>
	<!--Original InfoType Values-->
	<xsl:variable name="reportedInfoTypeOriginal" select="'reported'"/>
	<xsl:variable name="unReportedInfoTypeOriginal" select="'unreported'"/>
	<xsl:variable name="digestInfoTypeOriginal" select="'digest'"/>
	<xsl:variable name="cfaInfoTypeOriginal" select="'cfa'"/>
	<xsl:variable name="cnsInfoTypeOriginal" select="'cns'"/>
	<xsl:variable name="currentAwarenessInfoTypeOriginal" select="'currentawareness'"/>
	<xsl:variable name="otherInfoTypeOriginal" select="'other'"/>

	<!--*************************** MAIN DOC STRUCTURE ***************************-->
	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<xsl:choose>
				<!-- ************* WLI-HKDIGEST ************* -->
				<xsl:when test="$infoType=$digestInfoType or $infoType=$digestInfoTypeOriginal">
					<xsl:apply-templates select="n-docbody/case.head"/>
					<!--<xsl:apply-templates select="ref.group"/>-->
					<xsl:apply-templates select="n-docbody/catchwords.group"/>
					<xsl:apply-templates select="n-docbody/headnotes"/>
					<xsl:apply-templates select="n-docbody/judgment"/>
				</xsl:when>

				<!-- ************* WLI-HKCNS ************* -->
				<!-- **    Layout based on Ampex docs   **-->
				<xsl:when test="$infoType=$cnsInfoType or $infoType=$cnsInfoTypeOriginal">
					<xsl:apply-templates select="n-docbody/case.head"/>
					<xsl:apply-templates select="n-docbody/ref.group"/>
					<xsl:apply-templates select="n-docbody/catchwords.group"/>
					<!--<xsl:apply-templates select="headnotes"/>-->
					<!--<xsl:apply-templates select="judgment"/>-->
				</xsl:when>

				<!-- *********** HK-CASE-LOCATOR ***********-->
				<xsl:when test="$infoType=$hkCaseLocatorInfoType">
					<xsl:call-template name="HKCaseLocatorHeader"/>
					<xsl:apply-templates select="n-docbody/document/casegroup/proceeding/content/taxonomy"/>
					<xsl:apply-templates select="n-docbody/document/casegroup/proceeding/content/abstract"/>
					<xsl:apply-templates select="n-docbody/document/casegroup/proceeding/relationships"/>
				</xsl:when>

				<!-- ************* DEFAULT (works for WLI_HKLRPTS and WLI_HKLJAS and HK-CFA) ************* -->
				<xsl:otherwise>
					<xsl:apply-templates select="n-docbody/case.head"/>
					<xsl:apply-templates select="n-docbody/ref.group"/>
					<xsl:apply-templates select="n-docbody/catchwords.group"/>
					<xsl:apply-templates select="n-docbody/headnotes"/>
					<xsl:apply-templates select="n-docbody/judgment"/>
				</xsl:otherwise>
			</xsl:choose>

			<!-- End of document common to all collections -->
			<xsl:call-template name="RenderFootnotes" />
			<!--
				******************************************************************************************************
				* Backlog Item 506268: 
				* Remove all logos from International content. 
				* Add copyright message from royality block and message block centered at the bottom of the document.
				******************************************************************************************************
			-->
			<xsl:apply-templates select="n-docbody/copyright-message"/>
			<div class="&alignHorizontalLeftClass;">
				<xsl:apply-templates select="$primaryCitation"/>
			</div>
			
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>

	<!--*************************** CASE.HEAD Block ***************************-->

	<xsl:template match="case.head">
		<xsl:choose>
			<!-- ************* WLI-HKDIGEST ************* -->
			<xsl:when test="$infoType=$digestInfoType or $infoType=$digestInfoTypeOriginal">


				<div class="&centerClass;">
					<xsl:apply-templates select="citation.group"/>
					<xsl:apply-templates select="party.line"/>
					<xsl:apply-templates select="../image.block" />
					<xsl:apply-templates select="$judgmentDate"/>
					<div class="&paratextMainClass;">&#160;</div>
					<xsl:apply-templates select="court.line/court"/>
					<xsl:call-template name="chooseCaseRefNo" />
				</div>

				<xsl:apply-templates select="judge.line"/>
				<!--<xsl:apply-templates select="counsel.group"/>-->
			</xsl:when>

			<!-- ************* WLI-HKCNS ************* -->
			<!-- **    Layout based on Ampex docs   **-->
			<xsl:when test="$infoType=$cnsInfoType or $infoType=$cnsInfoTypeOriginal">

				<div class="&centerClass;">
					<xsl:apply-templates select="citation.group"/>
					<xsl:apply-templates select="party.line"/>
					<xsl:apply-templates select="$judgmentDate"/>
					<div class="&paratextMainClass;">&#160;</div>
					<xsl:apply-templates select="../image.block" />
					<!-- image.block comes AFTER date in WLI-HKCNS-->
					<xsl:apply-templates select="court.line/court"/>
					<xsl:call-template name="chooseCaseRefNo" />
				</div>

				<xsl:apply-templates select="judge.line"/>
				<xsl:apply-templates select="counsel.group"/>
			</xsl:when>

			<!-- ************* DEFAULT (works for WLI-HKLRPTS and WLI_HKLJAS and HK-CFA) ************* -->
			<xsl:otherwise>

				<div class="&centerClass;">
					<xsl:apply-templates select="citation.group"/>
					<xsl:apply-templates select="party.line"/>
					<xsl:apply-templates select="../image.block" />
					<xsl:apply-templates select="$judgmentDate"/>
					<div class="&paratextMainClass;">&#160;</div>
					<xsl:apply-templates select="court.line/court"/>
					<xsl:call-template name="chooseCaseRefNo" />
				</div>

				<xsl:apply-templates select="judge.line"/>
				<xsl:apply-templates select="counsel.group"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- ************************ Header for HK-CASE-LOCATOR ***********************-->
	<xsl:template name="HKCaseLocatorHeader">
		<div class="&centerClass;">
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite"/>
			<xsl:apply-templates select="n-docbody/document/casegroup/fullcasename"/>
			<xsl:apply-templates select="n-docbody/document/casegroup/proceeding/structural/judgment_date"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<div>&#160;</div>
	</xsl:template>

	<!--*************** CITATION elements ***************-->
	<!--****        Order based on Ampex docs        ****-->

	<xsl:template match="citation.group">
		<div class="&citesClass; &centerClass;">
			<xsl:apply-templates select="parallel.citation">
				<xsl:with-param name="type">WL.cite</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="parallel.citation">
				<xsl:with-param name="type">additional</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="primary.citation"/>
			<xsl:apply-templates select="parallel.citation">
				<xsl:with-param name="type">unreported</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="parallel.citation">
				<xsl:with-param name="type">parallelColumn</xsl:with-param>
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<xsl:template match="parallel.citation">
		<xsl:param name="type" />
		<xsl:if test="$type='WL.cite'">
			<xsl:if test="./@type ='WL.cite'">
				<xsl:apply-templates />
				<xsl:text> (</xsl:text>
				<xsl:value-of select="$courtAbbreviation" />
				<xsl:text>),&#13;</xsl:text>
			</xsl:if>
		</xsl:if>
		<xsl:if test="$type='additional'">
			<xsl:if test="./@type ='report' or ./@type ='digest' or ./@type ='PD' or ./@type ='Other'">
				<xsl:apply-templates />
				<xsl:text>, </xsl:text>
			</xsl:if>
		</xsl:if>
		<xsl:if test="$type='unreported'">
			<xsl:if test="./@type ='unreported'">
				<xsl:text>, </xsl:text>
				<xsl:apply-templates />
			</xsl:if>
		</xsl:if>
		<xsl:if test="$type='parallelColumn'">
			<xsl:choose>
				<!-- Swallow WL.cite in parallel citataion column-->
				<xsl:when test="./@type ='WL.cite'" />
				<!-- Swallow West serial # in parallel citataion column-->
				<xsl:when test="./@type ='West.serial'" />
				<xsl:otherwise>
					<div>
						<xsl:apply-templates />
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="primary.citation">
		<xsl:apply-templates/>
	</xsl:template>

	<!--*************** ADDITIONAL CASE.HEAD elements ***************-->

	<xsl:template match="case.head/party.line">
		<div class="&titleClass;">
			<div class="&suitClass;">
				<div class="&partyLineClass;">
					<xsl:apply-templates />
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="date" priority="2">
		<div class="&dateClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="court">
		<div class="&simpleContentBlockClass; &centerClass;">
			<xsl:choose>
				<xsl:when test="$infoType=$reportedInfoType or $infoType=$reportedInfoTypeOriginal">
					<!-- Add parentheses for WLI-HKLRPTS only -->
					<div>
						<xsl:text>(</xsl:text>
						<xsl:apply-templates />
						<xsl:text>)</xsl:text>
					</div>
					<div>
						<xsl:value-of select="$courtAbbreviation"/>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
					<div>
						<xsl:value-of select="$courtAbbreviation"/>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template name="chooseCaseRefNo">
		<!-- case ref group: content may contain multiple case.ref formats-->
		<!-- within multiple case.ref.group elements -->
		<!-- we only want ONE case.ref format (prioritize: long / short / abbrev) -->
		<xsl:choose>
			<xsl:when test="case.ref.no.group/case.ref.no[@type='long']">
				<xsl:apply-templates select="case.ref.no.group/case.ref.no[@type='long']"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="case.ref.no.group/case.ref.no[@type='short']">
						<xsl:apply-templates select="case.ref.no.group/case.ref.no[@type='short']"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="case.ref.no.group/case.ref.no[@type='abbrev']"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="case.ref.no.group">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="case.ref.no">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="judge.line">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="heading">
		<div class="&headtextClass;">
			<xsl:apply-templates/>
		</div>
		<xsl:if test="parent::para.group or parent::judge.block">
			<!--Is a space wanted here?-->
		</xsl:if>
	</xsl:template>

	<xsl:template match="judge.body">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="counsel.group">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="counsel.line">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--*************** REF.GROUP Block ***************-->

	<xsl:template match="ref.group">
		<div>
			<xsl:apply-templates select="case.cited" />
		</div>
	</xsl:template>

	<xsl:template match="case.cited">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="case.ref[not(parent::xref)]">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="case.ref">
		<xsl:apply-templates />
		<!-- Create a div only if the case.ref contains something, but not if it is case.considered, which is displayed inline in paragraphs -->
		<!--<xsl:choose>
			<xsl:when test="normalize-space(.) and not(ancestor::case.considered)">
				<div>
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>-->
	</xsl:template>

	<!--*************** CATCHWORDS.GROUP Block ***************-->

	<xsl:template match="catchwords.group">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="catchwords">
		<div class="&paraMainClass;">
			<xsl:apply-templates select="catchword" />
		</div>
	</xsl:template>

	<xsl:template match="catchword">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::catchword">
			<xsl:text> - </xsl:text>
		</xsl:if>
	</xsl:template>

	<!--*************** HEADNOTES Block ***************-->

	<xsl:template match="headnotes">
		<xsl:apply-templates />
		<div>&#160;</div>
	</xsl:template>

	<!--*************** JUDGMENT Block ***************-->

	<xsl:template match="judgment">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="judge.block">
		<!--Replace?-->
		<div class="&paraIndentFirstLineClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--*************** LIST elements ***************-->
	<xsl:template match="list">
		<!--Document//para//list[parent::para[string-length(string())!=0]]-->
		<xsl:if test="not(parent::para) or self::node()[string-length(parent::para)!=0]">
			<div>&#160;</div>
		</xsl:if>
		<ul class="&listClass; &paraMainClass;">
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<xsl:template match="list/list.item">
		<li class="&paraMainClass;">
			<xsl:apply-templates/>
		</li>
		<xsl:if test="following-sibling::list.item or ((not(following-sibling::list.item) and not(../../following-sibling::*[1][self::para][child::list])) and not(ancestor::judge.block or ancestor::judgment))">
			<!--TODO: Line break here?-->
		</xsl:if>
	</xsl:template>


	<!--*************** General text elements ***************-->
	<xsl:template match="para.group" priority="2">
		<div class ="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="para" priority="2">
		<xsl:if test="child::list and not(preceding-sibling::*[1][self::heading]) and not(ancestor::judge.block or ancestor::judgment)">
			<!--Line break?-->
		</xsl:if>
		<xsl:choose>
			<xsl:when test="parent::block.quote">
				<div class="&paraMainClass;">

					<xsl:apply-templates />

				</div>
				<xsl:if test="following-sibling::*">
					<!--Line break?-->
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass;">
					<xsl:apply-templates/>
				</div>
				<xsl:if test="(ancestor::judge.block or ancestor::judgment)">
					<!--Line break?-->
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="label">
		<xsl:apply-templates />
		<xsl:text>&nbsp;</xsl:text>
	</xsl:template>

	<xsl:template match="block.quote">
		<xsl:if test="child::para">
			<div>&#160;</div>
		</xsl:if>

		<div class="&paraMainClass;">
			<blockquote>
				<xsl:apply-templates />
			</blockquote>
		</div>
		<!--Line break wanted?
		<xsl:if test="following-sibling::*">
			<br />
		</xsl:if>
		-->
	</xsl:template>

	<!--****************Text Styling**************-->
	<xsl:template match="emphasis">
		<xsl:choose>
			<xsl:when test="@type='italic'">
				<i>
					<xsl:apply-templates />
				</i>
			</xsl:when>
			<xsl:when test="@type='bold'">
				<b>
					<xsl:apply-templates />
				</b>
			</xsl:when>
			<xsl:when test="@type='underline'">
				<span class="&underlineClass;">
					<xsl:apply-templates />
				</span>
			</xsl:when>
			<xsl:when test="@type='bold_italic'">
				<b>
					<i>
						<xsl:apply-templates />
					</i>
				</b>
			</xsl:when>
			<xsl:when test="@type='bold_underline'">
				<b>
					<span class="&underlineClass;">
						<xsl:apply-templates />
					</span>
				</b>
			</xsl:when>
			<xsl:when test="@type='italic_underline'">
				<i>
					<span class="&underlineClass;">
						<xsl:apply-templates />
					</span>
				</i>
			</xsl:when>
			<xsl:when test="@type='bold_italic_underline'">
				<b>
					<i>
						<span class="&underlineClass;">
							<xsl:apply-templates />
						</span>
					</i>
				</b>
			</xsl:when>
			<xsl:when test="@type='sup'">
				<sup>
					<xsl:apply-templates />
				</sup>
			</xsl:when>
			<xsl:when test="@type='sub'">
				<sub>
					<xsl:apply-templates />
				</sub>
			</xsl:when>
			<xsl:when test="@type='double'">
				<!-- Todo: Placeholder -->
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--******************Fix Table Spacing************************-->
	<!--
	* Override the default table handling. Create a div with a special class
	* that increases the column spacing. Then call then default table handling.
	* Fixes Bug #489054.
	-->

	<xsl:template match="tbl" priority="2">
		<xsl:if test=".//text()">
			<div>
				<xsl:if test="@id or @ID">
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', @id | @ID)"/>
					</xsl:attribute>
					<xsl:attribute name="class">&extraPaddingClass;</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>

	<!--******************Document Links***************************-->
	<!--
	* Match the first document link element that has the necessary attributes
	* Do not create a link if the child of another valid link
	-->
	<xsl:template match="xref[parent::case.cited]">
		<div>
			<xsl:call-template name="xrefLink" />
		</div>
	</xsl:template>

	<xsl:template match="xref">
		<xsl:call-template name="xrefLink" />
	</xsl:template>

	<!--<xsl:template match="xref[@pubid and @wlserial]">
		<xsl:call-template name="xrefLink" />
	</xsl:template>-->

	<!-- overrule the included cite.query template if the node will not create a valid link-->

	<!--<xsl:template match="cite.query[not((@w-pub-number and @w-serial-number) or (@w-normalized-cite and @w-pub-number))]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="cite.query[ancestor::xref[@pubid and @wlserial]]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="xref[ancestor::cite.query[(@w-pub-number and @w-serial-number) or (@w-normalized-cite and @w-pub-number)]]">
		<xsl:apply-templates />
	</xsl:template>-->

	<!--<xsl:template match="xref" priority="5">
		<xsl:apply-templates />
	</xsl:template>-->


	<!--************************Footnotes ********************* -->
	<!--*************Suppress regular footnote display**********-->
	<xsl:template match="/Document/n-docbody/judgment/para[descendant::a[starts-with(@name,'f')]]" priority="3" />

	<!--*****************Links in document**********************-->
	<xsl:template match="a[starts-with(@name,'r')]">
		<a class="&footnoteReferenceClass;">
			<xsl:attribute name ="href">
				<xsl:value-of select="normalize-space(@href)"/>
			</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="normalize-space(@name)"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>


	<!--******************* Render Footnotes********************-->
	<xsl:template name="RenderFootnotes">
		<xsl:variable name="FootnoteNodes" select="/Document/n-docbody/judgment/para/emphasis/a[starts-with(@name,'f')]" />
		<xsl:if test="$FootnoteNodes and (not($infoType=$cnsInfoType or $infoType=$cnsInfoTypeOriginal))">
			<div id="&footnoteSectionClass;" class="&footnoteSectionClass;">
				<h2 class="&footnoteSectionTitleClass;">
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
        </h2>
				<xsl:for-each select="$FootnoteNodes">
					<div>
						<div class="&footnoteNumberClass;">
							<span>
								<a>
									<xsl:attribute name ="href">
										<xsl:value-of select="normalize-space(@href)"/>
									</xsl:attribute>
									<xsl:attribute name="id">
										<xsl:value-of select="normalize-space(@name)"/>
									</xsl:attribute>
									<xsl:value-of select="self::node()" />
								</a>
							</span>
						</div>
						<div class="&footnoteBodyClass;">
							<div class="&paraMainClass;">
								<!--<xsl:apply-templates select="ancestor::para" mode="footnote"/>-->
								<xsl:apply-templates select="ancestor::para/node()[not(self::emphasis/a)]" />
							</div>
						</div>
					</div>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>


	<!--****************** HK-CASE-LOCATOR SPECIFIC *******************-->
	<xsl:template match="fullcasename">
		<div class="&titleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="md.primarycite">
		<div class="&citesClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="judgment_date">
		<div class="&dateClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="taxonomy">
		<div class="&headtextClass;">
			<xsl:text>&hk_SubjectsText;</xsl:text>
		</div>
		<div class="&paraMainClass;">
			<xsl:apply-templates select="subjects"/>
		</div>
		<div class="&headtextClass;">
			<xsl:text>&hk_PhrasesText;</xsl:text>
		</div>
		<div class="&paraMainClass;">
			<xsl:apply-templates select="catchphrase"/>
		</div>
	</xsl:template>

	<xsl:template match="subject.principal | subject.associated">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::subject.principal or following-sibling::subject.associated">
			<xsl:text> - </xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="catchphrase">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="catchphrase/keyword | catchphrase/phraseitem">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::keyword | following-sibling::phraseitem">
			<xsl:text> - </xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="abstract">
		<div class="&headtextClass;">
			<xsl:text>&hk_AbstractText;</xsl:text>
		</div>
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="relationships">
		<xsl:if test="not(normalize-space(.)='')">
			<div class="&headtextClass;">
				<xsl:text>&hk_RelationshipsText;</xsl:text>
			</div>
			<div>
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getRelationshipEffectText">
		<xsl:choose>
			<xsl:when test="@effect = 'correcting'">&hk_CorrectingRelationshipText;</xsl:when>
			<xsl:when test="@effect = 'reversing'">&hk_ReversingRelationshipText;</xsl:when>
			<xsl:when test="@effect = 'distinguishing'">&hk_DistinguishingRelationshipText;</xsl:when>
			<xsl:when test="@effect = 'distinguished'">&hk_DistinguishedRelationshipText;</xsl:when>
			<xsl:otherwise>&hk_OtherRelationshipText;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getRelationshipTypeText">
		<xsl:choose>
			<xsl:when test="@type = 'source'">&hk_TargetRelationshipTypeText;</xsl:when>
			<xsl:when test="@type = 'target'">&hk_SourceRelationshipTypeText;</xsl:when>
			<xsl:otherwise>&hk_OtherRelationshipText;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="relationship">
		<div class="&headtextClass;">
			<xsl:call-template name="getRelationshipEffectText"/>
		</div>
		<xsl:apply-templates select="caseref" />
	</xsl:template>

	<xsl:template match="cases_cited">
		<div class="&headtextClass;">
			<xsl:text>&hk_CasesCitedText;</xsl:text>
		</div>
		<xsl:apply-templates select="caseref" />
	</xsl:template>

	<xsl:template match="cases_citing_this">
		<div class="&headtextClass;">
			<xsl:text>&hk_CasesCitingThisText;</xsl:text>
		</div>
		<xsl:apply-templates select="caseref" />
	</xsl:template>


	<xsl:template match="relationship/caseref">
		<div class="&headtextClass;">
			<xsl:call-template name="getRelationshipTypeText"/>
		</div>
		<div class="&paraMainClass;">
			<div>
				<xsl:apply-templates select="casename" />
			</div>
			<div>
				<xsl:apply-templates select="link" />
			</div>
		</div>
	</xsl:template>

	<xsl:template match="cases_cited/caseref | cases_citing_this/caseref">
		<div class="&headtextClass;">
			<xsl:call-template name="getRelationshipEffectText"/>
		</div>
		<div class="&paraMainClass;">
			<div>
				<xsl:apply-templates select="casename" />
			</div>
			<div>
				<xsl:apply-templates select="link" />
			</div>
		</div>
	</xsl:template>

	<xsl:template match="link[@tuuid]">
		<a>
			<xsl:attribute name="href">
				<xsl:call-template name="GetDocumentUrl">
					<xsl:with-param name="documentGuid" select="@tuuid"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:apply-templates select="child::node()" />
		</a>
	</xsl:template>

	<xsl:template match="copyright-message">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>

</xsl:stylesheet>
