<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Para.xsl"/>
	<xsl:include href="Image.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternationalFootnote.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeAdminDecisionClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
			
			<xsl:apply-templates select="n-docbody" />

			<!-- Display footnotes at bottom of page -->
			<xsl:call-template name="internationalFootnote" />

			<!--
				******************************************************************************************************
				* Backlog Item 506268: 
				* Remove all logos from International content. 
				* Add copyright message from royality block and message block centered at the bottom of the document.
				******************************************************************************************************
			-->			
			<xsl:apply-templates select="n-docbody/metadata/copyright" />

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

	<xsl:template match ="n-docbody" priority="1">
		<xsl:apply-templates />
		<xsl:if test="not(text)">
			<xsl:call-template name="displayOutline"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="n-docbody/metadata">
		<xsl:call-template name="renderDocumentHeader"/>
		<xsl:call-template name="destid"/>
	</xsl:template>

	<xsl:template name="renderDocumentHeader">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="image.block"/>
			<xsl:apply-templates select="citations" />
			<xsl:apply-templates select="prelim"/>
			<xsl:if test="//n-metadata/metadata.block/md.infotype = 'case-EU'">
				<xsl:apply-templates select="bibliographic-info/author/item"/>
			</xsl:if>
			<xsl:apply-templates select="title"/>
			<xsl:apply-templates select="source"/>
			<xsl:apply-templates select="copyright"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="n-docbody/text">
		<xsl:call-template name="textOutline" />
		<xsl:apply-templates select="../section-head-text" mode="headingText"/>
		<xsl:apply-templates select="../catchwords"  mode="catchWords"/>
		<xsl:apply-templates />
		<xsl:call-template name="displayOutline">
			<xsl:with-param name="context" select=".."/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="section-head-text" mode="headingText">
		<div id="co_{dest-id/@dest}" class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="section-head-text | catchwords"/>

	<xsl:template name="displayOutline">
		<xsl:param name="context" select="." />
		<div>
			<xsl:apply-templates select="$context/metadata/index" />
			<xsl:apply-templates select="$context/metadata/dates" />
			<xsl:apply-templates select="$context/metadata/bibliographic-info" />
			<xsl:apply-templates select="$context/references" mode="references"/>
			<xsl:apply-templates select="$context/national-measures" mode="nationalMeasures"/>
		</div>
	</xsl:template>

	<xsl:template match="n-docbody/text/list">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Suppress these -->
	<xsl:template match="//map | metadata.block | header | sec_entry_type | journal_id | volume_number | issue_number"/>
	<xsl:template match="docid | starpage" />


	<!-- Build Outline and Destination Links -->
	<xsl:template name="destidLink">
		<xsl:param name="dest" />
		<xsl:param name="value" />
		<xsl:if test="$dest and $value">
			<div>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="concat('#','co_', $dest)" />
					</xsl:attribute>
					<xsl:value-of select="$value"/>
				</a>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="destid">
		<!-- build the link to the text index here, as it isn't in the novus xml-->
		<xsl:if test="//dest-id[count(ancestor::*[name() = 'text']) > 0]">
			<div>
				<a>
					<xsl:attribute name="href">
						<xsl:text>#co_textOutline</xsl:text>
					</xsl:attribute>
					<xsl:text>Text outline</xsl:text>
				</a>
			</div>
		</xsl:if>

		<xsl:variable name="Text" select="../section-head-text/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="$Text/@dest" />
			<xsl:with-param name="value" select="$Text/@value" />
		</xsl:call-template>

		<xsl:variable name="Index" select="index/section-head-index/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="$Index/@dest" />
			<xsl:with-param name="value" select="$Index/@value" />
		</xsl:call-template>

		<xsl:variable name="Dates" select="dates/section-head-date/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="$Dates/@dest" />
			<xsl:with-param name="value" select="$Dates/@value" />
		</xsl:call-template>

		<xsl:variable name="Bib" select="bibliographic-info/section-head-bib/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="$Bib/@dest" />
			<xsl:with-param name="value" select="$Bib/@value" />
		</xsl:call-template>

		<xsl:variable name="References" select="../references/section-head-reference/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="$References/@dest" />
			<xsl:with-param name="value" select="$References/@value" />
		</xsl:call-template>

		<xsl:variable name="NationalMeasures" select="../national-measures/section-head-nat/dest-id"/>
		<xsl:call-template name="destidLink">
			<xsl:with-param name="dest" select="$NationalMeasures/@dest" />
			<xsl:with-param name="value" select="$NationalMeasures/@value" />
		</xsl:call-template>

	</xsl:template>

	<xsl:template name="textOutline">
		<xsl:if test="//dest-id[count(ancestor::*[name() = 'text']) > 0]">
			<div id="co_textOutline">
				<div class="&centerClass;">
					<xsl:text>Text outline</xsl:text>
				</div>
				<xsl:for-each select="//dest-id[ancestor::*[name() = 'text']]">
					<div>
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="concat('#', 'co_', ./@dest)" />
							</xsl:attribute>
							<xsl:value-of select="@value"/>
						</a>
					</div>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="metadata/index">
		<div id="co_{ section-head-index/dest-id/@dest}">
			<div class="&centerClass;">
				<xsl:apply-templates select="section-head-index"/>
			</div>
			<xsl:if test="register">
				<div>
					<strong>
						<xsl:text>Register</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="register"/>
				</div>
			</xsl:if>
			<xsl:if test="subject">
				<div>
					<strong>
						<xsl:text>Subject</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="subject"/>
				</div>
			</xsl:if>
			<xsl:if test="keywords">
				<div>
					<strong>
						<xsl:text>Keywords</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="keywords"/>
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="metadata/dates">
		<div id="co_{ section-head-date/dest-id/@dest}">
			<div class="&centerClass;">
				<xsl:apply-templates select="section-head-date"/>
			</div>
			<xsl:if test="date-judgment">
				<div>
					<strong>
						<xsl:text>Date of judgment</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="date-judgment"/>
				</div>
			</xsl:if>
			<xsl:if test="date-lodged">
				<div>
					<strong>
						<xsl:text>Date lodged</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="date-lodged"/>
				</div>
			</xsl:if>
			<xsl:if test="date-publication">
				<div>
					<strong>
						<xsl:text>Date of publication</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="date-publication"/>
				</div>
			</xsl:if>
			<xsl:if test="date-document">
				<div>
					<strong>
						<xsl:text>Date of document</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="date-document"/>
				</div>
			</xsl:if>
			<xsl:if test="date-inforce">
				<div>
					<strong>
						<xsl:text>In force date</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="date-inforce"/>
				</div>
			</xsl:if>
			<xsl:if test="date-validity">
				<div>
					<strong>
						<xsl:text>Validity end date</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="date-validity"/>
				</div>
			</xsl:if>
			<xsl:if test="date-summary">
				<div>
					<strong>
						<xsl:text>Date summary</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="date-summary"/>
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="references" />

	<xsl:template match="references" mode="references">
		<div id="co_{ section-head-reference/dest-id/@dest}">
			<div class="&centerClass;">
				<xsl:apply-templates select="section-head-reference"/>
			</div>
			<xsl:if test="../metadata/bibliographic-info/celex-number">
				<div>
					<strong>
						<xsl:text>Celex number</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="../metadata/bibliographic-info/celex-number"/>
				</div>
			</xsl:if>
			<xsl:if test="case-citations">
				<div>
					<strong>
						<xsl:text>Case citations</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="case-citations"/>
				</div>
			</xsl:if>
			<xsl:if test="concerns">
				<div>
					<strong>
						<xsl:text>Concerns</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="concerns"/>
				</div>
			</xsl:if>
			<xsl:if test="court-decisions">
				<div>
					<strong>
						<xsl:text>Court decisions</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="court-decisions"/>
				</div>
			</xsl:if>
			<xsl:if test="legal-base">
				<div>
					<strong>
						<xsl:text>Legal base</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="legal-base"/>
				</div>
			</xsl:if>
			<xsl:if test="legal-citations">
				<div>
					<strong>
						<xsl:text>Legal citations</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="legal-citations"/>
				</div>
			</xsl:if>
			<xsl:if test="modifies">
				<div>
					<strong>
						<xsl:text>Modifies</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="modifies"/>
				</div>
			</xsl:if>
			<xsl:if test="modified-by">
				<div>
					<strong>
						<xsl:text>Modified by</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="modified-by"/>
				</div>
			</xsl:if>
			<xsl:if test="earlier-acts">
				<div>
					<strong>
						<xsl:text>Earlier acts</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="earlier-acts"/>
				</div>
			</xsl:if>
			<xsl:if test="subsequent-acts">
				<div>
					<strong>
						<xsl:text>Subsequent acts</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="subsequent-acts"/>
				</div>
			</xsl:if>
			<xsl:if test="prep-works">
				<div>
					<strong>
						<xsl:text>Preparatory acts</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="prep-works"/>
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="metadata/bibliographic-info">
		<div id="co_{ section-head-bib/dest-id/@dest}">
			<div class="&centerClass;">
				<xsl:apply-templates select="section-head-bib"/>
			</div>
			<xsl:if test="celex-number">
				<div>
					<strong>
						<xsl:text>Celex number</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="celex-number" />
				</div>
			</xsl:if>
			<xsl:if test="author">
				<div>
					<strong>
						<xsl:text>Authoring institution</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:value-of select="author" />
				</div>
			</xsl:if>
			<xsl:if test="type/type-case">
				<div>
					<strong>
						<xsl:text>Document type</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="type/type-case"/>
				</div>
			</xsl:if>
			<xsl:if test="authentic-language">
				<div>
					<strong>
						<xsl:text>Authentic language</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="authentic-language"/>
				</div>
			</xsl:if>
			<xsl:if test="plaintiff">
				<div>
					<strong>
						<xsl:text>Plaintiff</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="plaintiff"/>
				</div>
			</xsl:if>
			<xsl:if test="defendant">
				<div>
					<strong>
						<xsl:text>Defendant</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="defendant"/>
				</div>
			</xsl:if>
			<xsl:if test="publication-reference">
				<div>
					<strong>
						<xsl:text>Publication reference</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="publication-reference"/>
				</div>
			</xsl:if>
			<xsl:if test="treaty">
				<div>
					<strong>
						<xsl:text>Treaty</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="treaty"/>
				</div>
			</xsl:if>
			<xsl:if test="legal-instrument-case">
				<div>
					<strong>
						<xsl:text>Legal instrument - Case</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="legal-instrument-case"/>
				</div>
			</xsl:if>
			<xsl:if test="legal-instrument-leg">
				<div>
					<strong>
						<xsl:text>Legal instrument - Legislation</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="legal-instrument-leg"/>
				</div>
			</xsl:if>
			<xsl:if test="type-main">
				<div>
					<strong>
						<xsl:text>Type</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="type-main"/>
				</div>
			</xsl:if>
			<xsl:if test="type/type-leg">
				<div>
					<strong>
						<xsl:text>Legislation type</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="type/type-leg"/>
				</div>
			</xsl:if>
			<xsl:if test="procedure">
				<div>
					<strong>
						<xsl:text>Procedure type</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="procedure"/>
				</div>
			</xsl:if>
			<xsl:if test="observers">
				<div>
					<strong>
						<xsl:text>Observers</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="observers"/>
				</div>
			</xsl:if>
			<xsl:if test="judge">
				<div>
					<strong>
						<xsl:text>Judge</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="judge"/>
				</div>
			</xsl:if>
			<xsl:if test="advocate-general">
				<div>
					<strong>
						<xsl:text>Advocate General</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="advocate-general"/>
				</div>
			</xsl:if>
			<xsl:if test="national-court">
				<div>
					<strong>
						<xsl:text>National Court</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="national-court"/>
				</div>
			</xsl:if>
			<xsl:if test="nationality">
				<div>
					<strong>
						<xsl:text>Nationality</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="nationality"/>
				</div>
			</xsl:if>
			<xsl:if test="case-notes">
				<div>
					<strong>
						<xsl:text>Commentary</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="case-notes"/>
				</div>
			</xsl:if>
			<xsl:if test="addressee">
				<div>
					<strong>
						<xsl:text>Addressee</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="addressee"/>
				</div>
			</xsl:if>
			<xsl:if test="additional-info">
				<div>
					<strong>
						<xsl:text>Additional info</xsl:text>
					</strong>
				</div>
				<div>
					<xsl:apply-templates select="additional-info"/>
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="national-measures" />

	<xsl:template match="national-measures" mode="nationalMeasures">
		<div id="co_{section-head-nat/dest-id/@dest}">
			<div class="&centerClass;">
				<xsl:apply-templates mode="national" select="section-head-nat"/>
			</div>
			<xsl:apply-templates select="*" mode="contents">
				<xsl:sort select="itemheader"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="*">
				<xsl:sort select="itemheader"/>
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<xsl:template match="section-head-nat" mode="national">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="national-measures/*" mode="contents">
		<xsl:if test="name()!='section-head-nat'">
			<div>
				<a href="#co_{dest-id/@dest}">
					<xsl:apply-templates select="itemheader"/>
				</a>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="national-measures/*">
		<xsl:if test="name()!='section-head-nat'">
			<div id="co_{dest-id/@dest}">
				<h3>
					<em>
						<xsl:apply-templates select="itemheader"/>
					</em>
				</h3>
				<div>
					<xsl:apply-templates select="item"/>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="item">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="term">
		<xsl:apply-templates/>
		<xsl:if test="not(position() = last())">
			<xsl:text>; </xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="catchwords" mode="catchWords">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="citations">
		<xsl:choose>
			<xsl:when test="cit-doc or cit-celex">
				<xsl:apply-templates select="cit-doc"/>
				<xsl:apply-templates select="cit-celex"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="cit"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="cit-doc">
		<div class="&citesClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="cit-celex">
		<div class="&citesClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="cit">
		<div class="&citesClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="prelim">
		<div class="&simpleContentBlockClass; &prelimBlockClass;">
			<div class="&centerClass;">
				<strong>
					<xsl:apply-templates />
				</strong>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="title">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="copyright | source | bibliographic-info/author/item">
		<div class="&centerClass;">
			<xsl:apply-templates />
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

	<xsl:template match="para">
		<xsl:call-template name="para"/>
	</xsl:template>

	<xsl:template match="ital">
		<xsl:if test=".!=''">
			<em>
				<xsl:apply-templates/>
			</em>
		</xsl:if>
	</xsl:template>

	<xsl:template match="p">
		<div class="&paraMainClass;">
			<xsl:call-template name="renderParagraphTextDiv"/>
		</div>
	</xsl:template>

	<xsl:template match="head1 | head2 | head3 | head4 | head5 | head6 | head7 | head8 | head9 | head10">
		<xsl:element name="div">
			<xsl:attribute name="id">
				<xsl:value-of select="concat('co_', dest-id/@dest)"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>&headtextClass;</xsl:text>
			</xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
		<!--<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="id" select="concat('co_', dest-id/@dest)"/>
		</xsl:call-template>-->
	</xsl:template>

	<xsl:template match="head1//br | head2//br | head3//br | head4//br | head5//br | head6//br | head7//br | head8//br | head9//br | head10//br">
		<xsl:text> - </xsl:text>
	</xsl:template>

	<xsl:template match="xref">
		<xsl:call-template name="xrefLink"/>
	</xsl:template>

	<xsl:template match="copyright-message">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>	

</xsl:stylesheet>
