<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Prelim.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="HtmlHelper.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<xsl:apply-templates select="n-docbody" mode="EUCases"/>
			<div class="&centerClass;">
				<xsl:call-template name="copyrightBlock">
					<xsl:with-param name="copyrightNode" select="n-docbody/metadata/copyright" />
				</xsl:call-template>
			</div>

			<div class="&alignHorizontalLeftClass;">
				<xsl:if test ="n-docbody/metadata/citations/cit-doc">
					<xsl:value-of select="n-docbody/metadata/citations/cit-doc/text()" />
				</xsl:if>
				<xsl:if test ="n-docbody/metadata/citations/cit-celex">
					<div>
						<xsl:value-of select="n-docbody/metadata/citations/cit-celex/text()" />
					</div>
				</xsl:if>
			</div>
			
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>

	<xsl:template match="n-docbody" mode="EUCases">
		<xsl:apply-templates select="metadata"/>
		<xsl:apply-templates select="text"/>
		<xsl:apply-templates select="metadata/index" />
		<xsl:apply-templates select="metadata/dates" />
		<xsl:apply-templates select="metadata/bibliographic-info" />
		<xsl:apply-templates select="references" />
		<xsl:apply-templates select="national-measures" />
		<xsl:call-template name="textOutline" />
	</xsl:template>

	<!-- EU CASES -->
	<xsl:template match="metadata[//n-metadata/metadata.block/md.infotype = 'case-EU']">
		<xsl:apply-templates select="citations/cit-doc" />
		<xsl:apply-templates select="citations/cit-celex" />
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates select="prelim"/>
			<xsl:apply-templates select="bibliographic-info/author/item"/>
		</div>
		<xsl:apply-templates select="title"/>
		<xsl:apply-templates select="source"/>
		<!--
			******************************************************************************************************
			* Backlog Item 506268: 
			* Remove all logos from International content. 
			* Add copyright message from royality block and message block centered at the bottom of the document.
			******************************************************************************************************
		-->
		<xsl:apply-templates select="copyright"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<!--create links to sections-->
		<xsl:call-template name="destid"/>
		<div>&#160;</div>
	</xsl:template>

	<!-- EU LEGIS SIGN ONS -->
	<xsl:template match="metadata">
		<xsl:apply-templates select="citations/cit-doc" />
		<xsl:apply-templates select="citations/cit-celex" />
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates select="prelim"/>
		</div>
		<xsl:apply-templates select="title"/>
		<xsl:apply-templates select="source"/>
		<xsl:apply-templates select="copyright"/>
		<xsl:apply-templates select="image.block"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<!--create links to sections-->
		<xsl:call-template name="destid"/>
		<div>&#160;</div>
	</xsl:template>

	<xsl:template name="destidLink">
		<xsl:param name="dest" />
		<xsl:param name="value" />
		<xsl:if test="$dest and $value">
			<div class="&paratextMainClass;">
				<a href="#{$dest}">
					<xsl:value-of select="$value"/>
				</a>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="destid">
		<!-- don't build the text index here, so ignore descendants of the 'text' node-->
		<!-- some dest attribute values have a space, so remove the space as this is not allowed in the value of an id attribute -->
		<xsl:variable name="Index" select="index/section-head-index/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="translate($Index/@dest, ' ', '')" />
			<xsl:with-param name="value" select="$Index/@value" />
		</xsl:call-template>

		<xsl:variable name="Dates" select="dates/section-head-date/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="translate($Dates/@dest, ' ', '')" />
			<xsl:with-param name="value" select="$Dates/@value" />
		</xsl:call-template>

		<xsl:variable name="Bib" select="bibliographic-info/section-head-bib/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="translate($Bib/@dest, ' ', '')" />
			<xsl:with-param name="value" select="$Bib/@value" />
		</xsl:call-template>

		<xsl:variable name="References" select="../references/section-head-reference/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="translate($References/@dest, ' ', '')" />
			<xsl:with-param name="value" select="$References/@value" />
		</xsl:call-template>

		<xsl:variable name="NationalMeasures" select="../national-measures/section-head-nat/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="translate($NationalMeasures/@dest, ' ', '')" />
			<xsl:with-param name="value" select="$NationalMeasures/@value" />
		</xsl:call-template>

		<xsl:variable name="Text" select="../section-head-text/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="translate($Text/@dest, ' ', '')" />
			<xsl:with-param name="value" select="$Text/@value" />
		</xsl:call-template>

		<!-- build the link to the text index here, as it isn't in the novus xml-->
		<xsl:if test="//dest-id[count(ancestor::*[name() = 'text']) > 0]">
			<a href="#textOutline">
				<xsl:text>&euTextOutline;</xsl:text>
			</a>
		</xsl:if>
	</xsl:template>

	<xsl:template name="textOutline">
		<xsl:if test="//dest-id[count(ancestor::*[name() = 'text']) > 0]">
			<div class="&paraMainClass; &centerClass;">
				<span id="textOutline" class="&paraMainClass;"></span>
				<xsl:text>&euTextOutline;</xsl:text>
			</div>
			<xsl:for-each select="//dest-id[ancestor::*[name() = 'text']]">
				<div>
					<a>
						<xsl:attribute name="href">
							<xsl:value-of select="concat('#', translate(./@dest, ' ', ''))" />
						</xsl:attribute>
						<xsl:value-of select="@value"/>
					</a>
				</div>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<!-- create internal doc link for outline -->
	<xsl:template match="dest-id">
		<xsl:if test="@dest and string-length(@dest &gt; 0)">
			<xsl:variable name="destId">
				<xsl:value-of select="translate(@dest, ' ', '')"/>
				<!--<xsl:value-of select="@dest" />-->
			</xsl:variable>
			<span id="{$destId}" class="&paraMainClass;"></span>
		</xsl:if>
	</xsl:template>

	<xsl:template match="index">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates select="section-head-index"/>
		</div>
		<xsl:if test="register">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euRegister;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="register"/>
			</div>
		</xsl:if>
		<xsl:if test="subject">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euSubject;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="subject"/>
			</div>
		</xsl:if>
		<xsl:if test="keywords">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euKeywords;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="keywords"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="dates">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates select="section-head-date"/>
		</div>
		<xsl:if test="date-judgment">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euDateOfJudgment;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="date-judgment"/>
			</div>
		</xsl:if>
		<xsl:if test="date-lodged">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euDateLodged;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="date-lodged"/>
			</div>
		</xsl:if>
		<xsl:if test="date-publication">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euDateOfPublication;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="date-publication"/>
			</div>
		</xsl:if>
		<xsl:if test="date-document">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euDateOfDocument;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="date-document"/>
			</div>
		</xsl:if>
		<xsl:if test="date-inforce">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euInForceDate;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="date-inforce"/>
			</div>
		</xsl:if>
		<xsl:if test="date-validity">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euValidityEndDate;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="date-validity"/>
			</div>
		</xsl:if>
		<xsl:if test="date-summary">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euDateSummary;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="date-summary"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="references">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates select="section-head-reference"/>
		</div>
		<xsl:if test="../metadata/bibliographic-info/celex-number">
			<div class="&paraMainClass;">
				<strong>
					<xsl:text>&euCelexNumber;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="../metadata/bibliographic-info/celex-number"/>
			</div>
		</xsl:if>
		<xsl:if test="case-citations">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euCaseCitations;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="case-citations"/>
			</div>
		</xsl:if>
		<xsl:if test="concerns">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euConcerns;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="concerns"/>
			</div>
		</xsl:if>
		<xsl:if test="court-decisions">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euCourtDecisions;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="court-decisions"/>
			</div>
		</xsl:if>
		<xsl:if test="legal-base">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euLegalBase;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="legal-base"/>
			</div>
		</xsl:if>
		<xsl:if test="legal-citations">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euLegalCitations;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="legal-citations"/>
			</div>
		</xsl:if>
		<xsl:if test="modifies">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euModifies;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="modifies"/>
			</div>
		</xsl:if>
		<xsl:if test="modified-by">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euModifiedBy;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="modified-by"/>
			</div>
		</xsl:if>
		<xsl:if test="earlier-acts">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euEarlierActs;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="earlier-acts"/>
			</div>
		</xsl:if>
		<xsl:if test="subsequent-acts">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euSubsequentActs;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="subsequent-acts"/>
			</div>
		</xsl:if>
		<xsl:if test="prep-works">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euPreparatoryActs;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="prep-works"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="bibliographic-info">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates select="section-head-bib"/>
		</div>
		<xsl:if test="celex-number">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euCelexNumber;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="celex-number" />
			</div>
		</xsl:if>
		<xsl:if test="author">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euAuthoringInstitution;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:value-of select="author" />
			</div>
		</xsl:if>
		<xsl:if test="type/type-case">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euDocumentType;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="type/type-case"/>
			</div>
		</xsl:if>
		<xsl:if test="authentic-language">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euAuthenticLanguage;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="authentic-language"/>
			</div>
		</xsl:if>
		<xsl:if test="plaintiff">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euPlaintiff;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="plaintiff"/>
			</div>
		</xsl:if>
		<xsl:if test="defendant">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euDefendant;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="defendant"/>
			</div>
		</xsl:if>
		<xsl:if test="publication-reference">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euPublicationReference;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="publication-reference"/>
			</div>
		</xsl:if>
		<xsl:if test="treaty">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euTreaty;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="treaty"/>
			</div>
		</xsl:if>
		<xsl:if test="legal-instrument-case">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euLegalInstrumentCase;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="legal-instrument-case"/>
			</div>
		</xsl:if>
		<xsl:if test="legal-instrument-leg">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euLegalInstrumentLegislation;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="legal-instrument-leg"/>
			</div>
		</xsl:if>
		<xsl:if test="type-main">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euType;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="type-main"/>
			</div>
		</xsl:if>
		<xsl:if test="type/type-leg">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euLegislationType;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="type/type-leg"/>
			</div>
		</xsl:if>
		<xsl:if test="procedure">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euProcedureType;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="procedure"/>
			</div>
		</xsl:if>
		<xsl:if test="observers">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euObservers;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="observers"/>
			</div>
		</xsl:if>
		<xsl:if test="judge">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euJudge;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="judge"/>
			</div>
		</xsl:if>
		<xsl:if test="advocate-general">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euAdvocateGeneral;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="advocate-general"/>
			</div>
		</xsl:if>
		<xsl:if test="national-court">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euNationalCourt;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="national-court"/>
			</div>
		</xsl:if>
		<xsl:if test="nationality">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euNationality;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="nationality"/>
			</div>
		</xsl:if>
		<xsl:if test="case-notes">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euCommentary;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="case-notes"/>
			</div>
		</xsl:if>
		<xsl:if test="addressee">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euAddressee;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="addressee"/>
			</div>
		</xsl:if>
		<xsl:if test="additional-info">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&euAdditionalInfo;</xsl:text>
				</strong>
			</div>
			<div>
				<xsl:apply-templates select="additional-info"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="national-measures">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates mode="national" select="section-head-nat"/>
		</div>
		<xsl:apply-templates select="*" mode="contents">
			<xsl:sort select="itemheader"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="*">
			<xsl:sort select="itemheader"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="section-head-nat" mode="national">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="national-measures/*" mode="contents">
		<xsl:if test="name()!='section-head-nat'">
			<div class="&paraMainClass;">
				<a href="#{dest-id/@dest}">
					<xsl:apply-templates select="itemheader"/>
				</a>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="national-measures/*">
		<xsl:if test="name()!='section-head-nat'">
			<em>
				<h3 id="{dest-id/@dest}">
					<xsl:apply-templates select="itemheader"/>
				</h3>
			</em>
			<div class="&paraMainClass;">
				<xsl:apply-templates select="item"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="text">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates select="../section-head-text"/>
		</div>
		<xsl:apply-templates select="../catchwords" />
		<xsl:apply-templates />
	</xsl:template>

	<!-- Document body -->

	<xsl:template match="item">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="head1">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="term">
		<xsl:apply-templates/>
		<xsl:if test="not(position() = last())">
			<xsl:text>; </xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="catchwords">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="cit-celex|cit-doc|bibliographic-info/author/item|title|source|copyright">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="p">
		<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="b">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<xsl:template match="sg">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="br">
		<div>&#160;</div>
	</xsl:template>

	<xsl:template match="prelim">
		<strong>
			<xsl:apply-templates/>
		</strong>
	</xsl:template>

	<xsl:template match="para">
		<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="ital">
		<xsl:if test=".!=''">
			<em>
				<xsl:apply-templates/>
			</em>
		</xsl:if>
	</xsl:template>

	<xsl:template match="xref">
		<xsl:call-template name="xrefLink" />
	</xsl:template>

	<xsl:template match="list">
		<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- HTML sup element in document markup -->
	<xsl:template match="sup">
		<xsl:call-template name="CopyCurrentWithStyling" />
	</xsl:template>

	<!-- DO NOT RENDER -->
	<xsl:template match="//map | metadata.block | header | sec_entry_type | journal_id | volume_number | issue_number" />
	<xsl:template match="docid | starpage" />

</xsl:stylesheet>