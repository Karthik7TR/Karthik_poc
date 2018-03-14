<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKCases.xsl" forceDefaultProduct="true"/>
	<xsl:include href="UKCasesToc.xsl" />
	<xsl:include href="UKCasesStatuses.xsl" />
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name ="contentType" select ="'&ukWestlawContent;'"/>
	<xsl:variable name ="showLoading" select="true()"/>

	<xsl:variable name="isCaseJudgment">
		<xsl:choose>
			<xsl:when test="//metadata.block//md.fulltext[@href.format='&officialTranscriptText;' and @href=$currentDocument]">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template name="BuildDocumentTypeAttribute">
		<xsl:attribute name ="data-documenttype">
			<xsl:choose>
				<xsl:when test="$isCaseJudgment = 'true'">
					<xsl:value-of select="'&judgmentType;'" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&lawReportType;'" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="BuildDocumentBody">
		<xsl:call-template name="StarPageMetadata" />
		<div>
			<xsl:attribute name="class">
				<xsl:text>&ukMainDocumentContent;<![CDATA[ ]]>&docDivision;<![CDATA[ ]]>&simpleContentBlockClass;</xsl:text>
			</xsl:attribute>
			<a>
				<xsl:attribute name="id">
					<xsl:value-of select ="concat('&internalLinkIdPrefix;', '&ukMainDocumentContent;')"/>
				</xsl:attribute>
			</a>
			<xsl:if test="$isCaseJudgment = 'false'">
				<xsl:apply-templates select="//document/metadata.block//md.wl.database.identifier" />
			</xsl:if>
			<xsl:apply-templates select="//report"/>
		</div>
		<a id="&ukReferencesOffset;"></a>
	</xsl:template>

	<!-- Match on main report element -->
	<xsl:template match="report">
		<xsl:apply-templates select="maintitle/othername"/>
		<xsl:apply-templates select="maintitle/shipname"/>		
		<xsl:apply-templates select="maintitle/titlecaseno"/>
		<xsl:apply-templates select="court"/>
		<xsl:if test="$isCaseJudgment = 'true'">
			<xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
				<xsl:with-param name="additionalClass" select="'&centerClass;'" />
				<xsl:with-param name="contents">
					<xsl:value-of select="maintitle/title-paracite" />
					<xsl:if test="string-length(maintitle/title-paracite) &gt; 0">
						<xsl:text>, </xsl:text>
					</xsl:if>
					<xsl:value-of select="maintitle/titlecite" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates select="maintitle/joincase/titlecaseno"/>
		<xsl:apply-templates select="judge" />
		<xsl:apply-templates select="date" />
		<xsl:apply-templates select="reference"/>
		<xsl:apply-templates select="intro"/>
		<xsl:apply-templates select="judgment/preceding-sibling::opinion"/>
		<xsl:apply-templates select="judgment" />
		<xsl:apply-templates select="reporter"/>
		<xsl:apply-templates select="representation"/>
		<xsl:apply-templates select="costs"/>
		<xsl:apply-templates select="ruling"/>
		<xsl:apply-templates select="judgment/following-sibling::opinion"/>
		<xsl:apply-templates select="quest"/>
		<xsl:apply-templates select="final"/>
		<xsl:apply-templates select="commentary"/>
		<xsl:apply-templates select="appendix"/>
		<xsl:apply-templates select="practice"/>
		<xsl:call-template name="AttachedFileForDocument" />
		<xsl:call-template name="internationalFootnote" />
	</xsl:template>

	<xsl:template match="md.wl.database.identifier">
		<xsl:choose>
			<xsl:when test="./text()='UK-BLR-RPTS' or ./text()='UK-ICR-RPTS' or ./text()='UK-LAW-RPTS' or ./text()='UK-PTS-RPTS' or ./text()='UK-WLR-RPTS'">
				<xsl:call-template name="displayLawReportsLogo">
					<xsl:with-param name="contents">
						<xsl:text>&ukLawReportsIclrLogo;</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="./text()='UK-PEN-RPTS'">
				<xsl:call-template name="displayLawReportsLogo">
					<xsl:with-param name="contents">
						<xsl:text>&ukLawReportsIdsLogo;</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="displayLawReportsLogo">
		<xsl:param name="contents"/>
		<div class="&ukLawReportsLogo;">
			<img src="{$Images}{$contents}" alt=""/>
		</div>
	</xsl:template>

	<xsl:template name="AttachedFileForDocument">
		<xsl:variable name="guid">
			<xsl:choose>
				<xsl:when test="$isCaseJudgment = 'true'">
					<xsl:value-of select="//document/metadata.block/md.references/md.print.rendition.id"/>
				</xsl:when>
				<xsl:when test="$isCaseJudgment = 'false'">
					<xsl:value-of select="//document/metadata.block/md.references/md.print.rendition.id[@pub != 'OFFICIAL']" />
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="contents">
			<xsl:choose>
				<xsl:when test="$isCaseJudgment = 'true'">
					<xsl:value-of select="'&viewOfficialTranscriptText;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&viewPdfOfCaseReport;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length($guid) &gt; 0">
			<div class="&standardDocAttachment; &hideState;">
				<xsl:call-template name="createDocumentBlobLink">
					<xsl:with-param name="guid" select="$guid"/>
					<xsl:with-param name="targetType" select="'&inlineParagraph;'"/>
					<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ruling-intro | judges | summary | statprov | headnote | reference | foreword | practice | summary/narr-paragraph | paragrp/narr-paragraph | judges/narr-paragraph | costs/narr-paragraph">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="judge/name">
		<xsl:apply-templates />
		<xsl:if test="string-length(parent::*/text()) = 0">
			<xsl:text disable-output-escaping="yes"><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="para-text | p">
		<xsl:choose>
			<!--If paragraph is empty do not display it-->
			<xsl:when test="count(child::*)=0 and .=''">&#160;</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<!--If there are only list or table child elements ignore this paragraph and process children separately-->
					<xsl:when test="count(child::*[normalize-space()])=1 and child::table">
						<xsl:apply-templates/>
					</xsl:when>
					<xsl:otherwise>
						<div class="&paraMainClass;">
							<!--Check for indent and align attributes-->
							<xsl:variable name="indentClass">
								<xsl:if test="@indent and @indent != '0'">
									<xsl:choose>
										<xsl:when test="@align">
											<xsl:choose>
												<!--The attribute for centred text is unhelpfully called 'centre' - rename it to 'center' for display use-->
												<xsl:when test="@align='centre'">
													<xsl:value-of select="'&paraIndentHangingClass;'"/>
												</xsl:when>
												<xsl:when test="@align='left'">
													<xsl:if test="@indent='1'">
														<xsl:value-of select="'&indentLeft1Class;'"/>
													</xsl:if>
													<xsl:if test="@indent='2'">
														<xsl:value-of select="'&indentLeft2Class;'"/>
													</xsl:if>
													<xsl:if test="@indent='3'">
														<xsl:value-of select="'&indentLeft3Class;'"/>
													</xsl:if>
													<xsl:if test="@indent='4'">
														<xsl:value-of select="'&indentLeft4Class;'"/>
													</xsl:if>
												</xsl:when>
												<xsl:when test="@align='right'">
													<xsl:value-of select="'&paraIndentRightClass;'"/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="'&paraIndentFirstLineClass;'"/>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="'&indentLeft2Class;'"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:if>
							</xsl:variable>
							
							<xsl:variable name="alignClass">
								<xsl:if test="@align='right'">
									<xsl:value-of select="'&alignRightClass;'"/>
								</xsl:if>
							</xsl:variable>
							
							<xsl:attribute name="class">
								<xsl:value-of select="'&paraMainClass;'"/>
								<xsl:if test="$indentClass != ''">
									<xsl:value-of select="concat(' ', $indentClass)"/>
								</xsl:if>
								<xsl:if test="$alignClass != ''">
									<xsl:value-of select="concat(' ', $alignClass)"/>
								</xsl:if>
							</xsl:attribute>

							<xsl:if test="parent::*/@number">
								<xsl:value-of select="parent::*/@number"/>&#160;
							</xsl:if>
							<xsl:if test="name(preceding-sibling::*[position()=1])='number'">
								<xsl:value-of select="preceding-sibling::number"/>&#160;
							</xsl:if>
							<xsl:if test="name(preceding-sibling::*[position()=1])='header' and preceding-sibling::header/number">
								<xsl:value-of select="preceding-sibling::header/number"/>&#160;
							</xsl:if>
							<xsl:if test="parent::narr-paragraph/@type='held' and position()=1">
								<xsl:text>&ukHeld;</xsl:text>
								<!--the stylesheet used to output a comma after the held
								     label, however lots of fulltext have a comma in the text
								     and so 2 commas were displayed, so only output comma if 
								     there isn't one there-->
								<xsl:if test="substring(normalize-space(.), 1, 1) != ','">
									<xsl:text>, </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="parent::narr-paragraph/@type='opinion' and position()=1">
								<xsl:text>&opinionLabel;</xsl:text>
								<xsl:if test="substring(normalize-space(.), 1, 1) != ','">
									<xsl:text>, </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:apply-templates/>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="catchphr">
		<div class="&paraMainClass;">
			<xsl:if test="@number">
				<xsl:value-of select="@number"/>
				<xsl:text>&nbsp;</xsl:text>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="@type = 'italic'">
					<em>
						<xsl:apply-templates />
					</em>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="emphasis">
		<xsl:choose>
			<xsl:when test="@type = 'strong'">
				<strong>
					<xsl:apply-templates />
				</strong>
			</xsl:when>
			<xsl:when test="@type = 'italic'">
				<i>
					<xsl:apply-templates />
				</i>
			</xsl:when>
			<xsl:when test="@type = 'weak'">
				<u>
					<xsl:apply-templates />
				</u>
			</xsl:when>
			<xsl:when test="@type = 'smallcaps'">
				<span class="&smallCapsFontVariant;">
					<xsl:apply-templates />
				</span>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="sub1">
		<xsl:if test="parent::narr-paragraph/@type='held' and position()=1">
			<div class="&paraMainClass;">
				<xsl:text>&ukHeld;</xsl:text>:
			</div>
		</xsl:if>
		<xsl:if test="name(preceding-sibling::*[position()=1])='header' and preceding-sibling::header/number">
			<xsl:value-of select="preceding-sibling::header/number"/>&#160;
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--images-->
	<xsl:template match="image.block" priority="1">
		<div class="&paraMainClass; &docImageResearch;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="figure">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="graphic" />
	<!--end images-->

	<!--lists-->
	<xsl:template match="caselist | narr-paragraph">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="caseref">
		<em>
			<xsl:apply-templates/>
		</em>
	</xsl:template>

	<xsl:template match="final">
		<div class="&paraMainClass; &docContentFinalText;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="list">
		<xsl:if test="$DeliveryMode">
			<br/>
		</xsl:if>
		<ul class="&docContentList;">
			<xsl:apply-templates/>
		</ul>
	</xsl:template>

	<xsl:template match="item">
		<li>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
			<xsl:if test="following-sibling::*[2][self::starpage.anchor]">
				<xsl:call-template name="buildStarPageContent" >
					<xsl:with-param name="contents" select="following-sibling::*[2]"/>
					<xsl:with-param name="pageset" select="following-sibling::*[2][self::starpage.anchor]/@pageset"/>
				</xsl:call-template>
			</xsl:if>
		</li>
	</xsl:template>
	<!--end lists-->

	<xsl:template match="longquotation">
		<blockquote>
			<xsl:choose>
				<xsl:when test="para-text or narr-paragraph/para-text or list or table">
					<xsl:apply-templates/>
				</xsl:when>
				<xsl:otherwise>
					<div class="&paraMainClass;">
						<xsl:apply-templates/>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</blockquote>
	</xsl:template>

	<xsl:template match="ruling">
		<div class="&paraMainClass; &headtextClass;">
			<xsl:text>&orderText;</xsl:text>
		</div>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="representation">
		<div class="&paraMainClass; &headtextClass;">
			<xsl:if test="@number">
				<span><xsl:value-of select="concat(@number, ' ')"/></span>
			</xsl:if>
			<strong>
				<xsl:text>&ukRepresentation;</xsl:text>
			</strong>
		</div>
		<xsl:choose>
			<xsl:when test="descendant::list">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="date">
		<xsl:if test="string-length(.) &gt; 0">
			<xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
				<xsl:with-param name="additionalClass" select="'&centerClass;'" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="role">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="header/title">
		<xsl:variable name="className">
			<xsl:choose>
				<xsl:when test="parent::header/parent::subgroup">
					<xsl:text>&subheadTextClass;</xsl:text>
				</xsl:when>
				<xsl:when test="parent::header/parent::*">
					<xsl:text>&headtextClass;</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="buildSubheaderContent">
			<xsl:with-param name="className" select="$className" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="buildSubheaderContent">
		<xsl:param name="className" />
		<div>
			<xsl:attribute name="class">
				<xsl:text>&paraMainClass;</xsl:text>
				<xsl:if test="string-length($className) &gt; 0">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:value-of select="$className"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="ancestor::caselist/@number">
					<span><xsl:value-of select="concat(ancestor::caselist/@number, ' ')" /></span>
				</xsl:when>
				<xsl:when test="ancestor::statprov/@number">
					<span><xsl:value-of select="concat(ancestor::statprov/@number, ' ')" /></span>
				</xsl:when>
				<xsl:when test="(count(./parent::*/preceding-sibling::*) = 0) and parent::header/parent::*/@number">
					<xsl:value-of select="parent::header/parent::*/@number" />
					<xsl:text>&#160;</xsl:text>
				</xsl:when>
				<xsl:when test="preceding-sibling::number">
					<xsl:value-of select="preceding-sibling::number" />
					<xsl:text>&#160;</xsl:text>
				</xsl:when>
			</xsl:choose>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template name="BuildEndOfDocument">
		<xsl:call-template name="EmptyEndOfDocument">
			<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Star Pages -->
	<xsl:template match="starpage.anchor" priority="5">
		<xsl:if test="not(parent::list)">
			<xsl:call-template name="buildStarPageContent" >
				<xsl:with-param name="contents" select="."/>
				<xsl:with-param name="pageset" select="@pageset"/>
			</xsl:call-template>
		</xsl:if>
		</xsl:template>

	<xsl:template name="buildStarPageContent">
		<xsl:param name="contents" />
		<xsl:param name="pageset" />
		<xsl:call-template name="displayStarPage">
			<xsl:with-param name="pageset" select="$pageset" />
			<xsl:with-param name="starPageText">
				<xsl:value-of select ="$contents"/>
			</xsl:with-param>
			<xsl:with-param name="numberOfStars" select="1" />
		</xsl:call-template>
		<xsl:if test="$IncludeCopyWithRefLinks = true()">
			<xsl:call-template name="generateCopyWithReferenceLink" />
		</xsl:if>
	</xsl:template>

	<!--	metadata-->

	<xsl:template name="BuildMetaInfoColumnContent">
		<xsl:variable name="statusCode" select="n-metadata/metadata.block/md.history/md.keycite/md.flag.color.code" />
		<xsl:call-template name="DocumentStatusDetailed">
			<xsl:with-param name="statusCode" select="$statusCode"/>
		</xsl:call-template>
		<xsl:call-template name="MetaCourt"/>
		<xsl:call-template name="MetaDate"/>
		<xsl:if test="$isCaseJudgment = 'false'">
			<xsl:call-template name="MetaReportCitation"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="MetaCourt">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaCourt;'" />
			<xsl:with-param name="fieldCaption" select="'&ukCourtText;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/report/cases-locator/court.name"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaDate">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaDate;'" />
			<xsl:with-param name="fieldCaption" select="'&casesCaptionJudgmentDate;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:call-template name="formatYearMonthDayToDDMMMYYYY">
					<xsl:with-param name="date" select="n-docbody/document/report//date[1]/@date"/>
					<xsl:with-param name="dayFormatWithZero" select="false()"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	

	<xsl:template name="MetaReportCitation">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaCasesReportCitation;'" />
			<xsl:with-param name="fieldCaption" select="'&casesCaptionReportCitation;'"/>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
			<xsl:with-param name="fieldContent">
				<xsl:call-template name="titleCiteDisplay">
					<xsl:with-param name="contents" select="//report//title-paracite"/>
				</xsl:call-template>
				<xsl:call-template name="titleCiteDisplay">
					<xsl:with-param name="contents" select="//report//titlecite"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="titleCiteDisplay">
		<xsl:param name="contents"/>
		<xsl:if test="string-length($contents) &gt; 0">
			<div>
				<xsl:value-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Display party names -->
	<xsl:template match="partya">
		<xsl:if test="parent::joincase/preceding-sibling::joincase">
			<br/>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="partyb|partyc">
		<xsl:text> v </xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="joincase">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template name="BuildCaseHeaderContent">
		<xsl:param name="joinCaseSelector"/>
		<xsl:param name="joinCasePartyASelector"/>
		<xsl:param name="mainTitlePartyASelector"/>
		<xsl:param name="mainTitlePartyBSelector"/>
		<xsl:param name="mainTitlePartyCSelector"/>
		<h1 class="&titleClass;">
			<xsl:choose>
				<xsl:when test="$mainTitlePartyASelector">
					<xsl:apply-templates select="$mainTitlePartyASelector"/>
					<xsl:apply-templates select="$mainTitlePartyBSelector"/>
					<xsl:apply-templates select="$mainTitlePartyCSelector"/>
					<xsl:if test="$joinCasePartyASelector">
						<span class="&ukWestlawExtraTitle;">
							<xsl:for-each select="$joinCaseSelector">
								<xsl:apply-templates select="partya"/>
								<xsl:apply-templates select="partyb"/>
								<xsl:apply-templates select="partyc"/>
							</xsl:for-each>
						</span>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="//metadata.block/md.descriptions/md.title"/>
				</xsl:otherwise>
			</xsl:choose>
		</h1>
	</xsl:template>

	<xsl:template name="BuildDocumentHeaderContent">
		<xsl:call-template name="BuildCaseHeaderContent">
			<xsl:with-param name="joinCaseSelector" select="n-docbody/document/report/maintitle/joincase"/>
			<xsl:with-param name="joinCasePartyASelector" select="n-docbody/document/report/maintitle/joincase/partya"/>
			<xsl:with-param name="mainTitlePartyASelector" select="n-docbody/document/report/maintitle/partya"/>
			<xsl:with-param name="mainTitlePartyBSelector" select="n-docbody/document/report/maintitle/partyb"/>
			<xsl:with-param name="mainTitlePartyCSelector" select="n-docbody/document/report/maintitle/partyc"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="BuildLoggedOutDocumentHeaderContent">
		<xsl:call-template name="BuildCaseHeaderContent">
			<xsl:with-param name="joinCaseSelector" select="n-docbody/maintitle/joincase"/>
			<xsl:with-param name="joinCasePartyASelector" select="n-docbody/maintitle/joincase/partya"/>
			<xsl:with-param name="mainTitlePartyASelector" select="n-docbody/maintitle/partya"/>
			<xsl:with-param name="mainTitlePartyBSelector" select="n-docbody/maintitle/partyb"/>
			<xsl:with-param name="mainTitlePartyCSelector" select="n-docbody/maintitle/partyc"/>
		</xsl:call-template>
	</xsl:template>
	
	<!--Copyright-->
	<xsl:template name="BuildCopyright">
		<xsl:param name="copyrightMessage" select="n-docbody/copyright-message"/>
		<xsl:call-template name="displayCopyright">
			<xsl:with-param name="content">
				<xsl:value-of select="//md.copyright[1]" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="displayCopyright">
			<xsl:with-param name="content">
				<xsl:value-of select="$copyrightMessage" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="displayCopyright">
		<xsl:param name="content"/>
		<xsl:if test="string-length($content) &gt; 0">
			<div class="&centerClass;">
				<xsl:call-template name="copyrightBlock">
					<xsl:with-param name="copyrightNode">
						<xsl:value-of select="$content" />
					</xsl:with-param>
				</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Delete these elements -->
	<xsl:template match="practice/date"/>
	<xsl:template match="md.print.rendition.id"/>
	<xsl:template match="title-paracite"/>
	<xsl:template match="titlecite"/>

</xsl:stylesheet>