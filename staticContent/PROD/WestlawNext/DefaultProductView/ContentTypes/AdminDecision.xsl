<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Index.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl" />
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="ReferenceTable.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="IsFedPersonnet" select="contains('|w_adm_eeocmd|w_adm_fgcbca3|w_adm_fmanuals|w_adm_fwsm|w_adm_fwstds|w_adm_gpds|w_adm_gstds|w_adm_nr1|w_adm_travreg|w_adm_eeoccm|w_adm_dscdo|', concat('|', /Document/document-data/collection, '|'))"/>
	<xsl:variable name="suppressStarPageMetadata" select="$IsFedPersonnet"/>

	<!-- Fix for bug 475408: add in ALJ names into documents with different XML tags than story 446607 -->
	<xsl:template name="dateLine" match="date.line[date][not(ancestor::date.block)]" priority="1">

		<xsl:variable name="judgeAuthors" select="//arbitrator.block/arbitrator[not(. = preceding-sibling::*)]"/>
		<xsl:if test="$judgeAuthors and string-length(normalize-space($judgeAuthors)) &gt; 0">
			<div class="&adminLawJudgeClass;">
				<div>
					<xsl:for-each select="$judgeAuthors">
						<xsl:value-of select="."/>
						<xsl:if test="position() != last()">
							<xsl:text>, </xsl:text>
						</xsl:if>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>

		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div class="&dateClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Date.Block - Overridden here to insert the ALJ name for enhancement 446607-->
	<xsl:template name="dateBlock" match="date.block | date | prop.block/content.metadata.block/cmd.dates | pub.date[not(parent::date.block or parent::date or parent::prop.block/content.metadata.block/cmd.dates)]" priority ="1">
		<xsl:param name="extraClasses" />

		<!-- Enhancement 446607: Adding ALJ Name to the heading section -->
		<xsl:variable name="judgeAuthors" select="//author.block/author.name/cite.query
                  [@w-ref-type='RQ'
                  and
                  not(. = ../preceding-sibling::*/cite.query[@w-ref-type='RQ'])
                  ]"/>
		<xsl:if test="$judgeAuthors and string-length(normalize-space($judgeAuthors)) &gt; 0">
			<div class="&adminLawJudgeClass;">
				<div>
					<xsl:for-each select="$judgeAuthors">
						<xsl:value-of select="."/>
						<xsl:if test="position() != last()">
							<xsl:text>, </xsl:text>
						</xsl:if>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>

		<xsl:if test=".//text()">
			<xsl:variable name="classes">
				<xsl:text>&dateClass;</xsl:text>
				<xsl:if test="$extraClasses">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:value-of select="$extraClasses"/>
				</xsl:if>
			</xsl:variable>

			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="$classes" />
			</xsl:call-template>

		</xsl:if>
	</xsl:template>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:choose>
				<!-- In order to make the  font uniform all over the document-->
				<xsl:when test="/Document/document-data/collection = 'w_3rd_arbbio'">
					<xsl:call-template name="AddDocumentClasses"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeAdminDecisionClass;'"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="not($suppressStarPageMetadata)">
					<xsl:call-template name="StarPageMetadata" />
				</xsl:when>
			</xsl:choose>
			<xsl:apply-templates />
			<xsl:call-template name="RenderFootnote" />
			<xsl:call-template name="FooterCitation" />
			<xsl:apply-templates select="n-docbody/header/prelim/copyright"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- For displaying the document title for w_3rd_icdr collection, formatting is different for this collection	-->
	<xsl:template match="n-docbody/header[/Document/document-data/collection = 'w_3rd_icdr']" name="DisplayDocumentTitle">
		<xsl:if test="citation and string-length(normalize-space(citation)) &gt; 0">
			<div class="&citesClass;">
				<xsl:apply-templates select="citation"/>
			</div>
		</xsl:if>
		<xsl:if test="prelim/dtype and string-length(normalize-space(prelim/dtype)) &gt; 0">
			<div class="&citesClass;">
				<xsl:apply-templates select="prelim/dtype"/>
			</div>
		</xsl:if>
	
		<!-- This is to avoid duplicate content display	-->
		<xsl:if test="not(/Document/n-metadata/resultList/institution)">
			<xsl:variable name="institution" select="prelim/institution"/>
			<div class="&citesClass;">
				<xsl:value-of	select="$institution"	/>
			</div>
		</xsl:if>
		<xsl:if test="source and string-length(normalize-space(source)) &gt; 0">
			<div class="&citesClass;">
				<xsl:apply-templates select="source"/>
			</div>
		</xsl:if>
		<xsl:if test="court and string-length(normalize-space(court)) &gt; 0">
			<div class="&citesClass;">
				<xsl:apply-templates select="court"/>
			</div>
		</xsl:if>
		<xsl:if test="title and string-length(normalize-space(title)) &gt; 0">
			<xsl:variable name="title" select="title"/>
			<div class="&citesClass;">
				<xsl:value-of	select="$title"	/>
			</div>
		</xsl:if>
		<xsl:if test="prelim/docket.number and string-length(normalize-space(prelim/docket.number)) &gt; 0">
			<div class="&citesClass;">
				<xsl:apply-templates select="prelim/docket.number"/>
			</div>
		</xsl:if>
		<xsl:text>&#160;</xsl:text>
		<xsl:if test="arbitration.industry.code and string-length(normalize-space(arbitration.industry.code)) &gt; 0">
			<div class="&paratextMainClass;">
				<strong>
					<xsl:apply-templates select="arbitration.industry.code"/>
				</strong>
			</div>
		</xsl:if>
		<xsl:if test="prelim/ctype and string-length(normalize-space(prelim/ctype)) &gt; 0">
			<div class="&paratextMainClass;">
				<strong>
					<xsl:apply-templates select="prelim/ctype"/>
				</strong>
			</div>
		</xsl:if>
		<xsl:if test="arbitration.award.amount.plaintiff and string-length(normalize-space(arbitration.award.amount.plaintiff)) &gt; 0">
			<xsl:variable name="plaintiff" select="arbitration.award.amount.plaintiff"/>
			<div class="&paratextMainClass;">
				<strong>
					<xsl:value-of	select="$plaintiff"/>
				</strong>
			</div>
		</xsl:if>
		<xsl:if test="attorney.plaintiff and string-length(normalize-space(attorney.plaintiff)) &gt; 0">
			<xsl:variable name="plaintiff" select="attorney.plaintiff"/>
			<div class="&paratextMainClass;">
				<strong>
					<xsl:value-of	select="$plaintiff"/>
				</strong>
			</div>
		</xsl:if>
		<xsl:if test="attorney.defendant and string-length(normalize-space(attorney.defendant)) &gt; 0">
			<div class="&paratextMainClass;">
				<strong>
					<xsl:apply-templates select="attorney.defendant"/>
				</strong>
			</div>
		</xsl:if>
		<xsl:if test="dates and string-length(normalize-space(dates)) &gt; 0">
			<xsl:variable name="dates" select="dates/date"/>
			<div class="&paratextMainClass;">
				<strong>
					<xsl:value-of	select="$dates"/>
				</strong>
			</div>
		</xsl:if>
		<xsl:if test="arbitrator and string-length(normalize-space(arbitrator)) &gt; 0">
			<xsl:variable name="arbitrator" select="arbitrator"/>
			<div class="&paratextMainClass;">
				<strong>
					<xsl:value-of	select="$arbitrator"/>
				</strong>
			</div>
		</xsl:if>
		<xsl:if test="prelim/country and string-length(normalize-space(prelim/country)) &gt; 0">
			<div class="&paratextMainClass;">
				<strong>
					<xsl:apply-templates select="prelim/country"/>
				</strong>
			</div>
		</xsl:if>
	</xsl:template>
	
	<!--For displaying the list tag for  w_3rd_icdr collection -->
	<xsl:template match="sup[child::a[starts-with(@name, 'r')]]">
		<xsl:variable name="footnoteNo" select="a/text()" />
		<xsl:variable name="footnoteLink" select="substring(a/@href, 2)" />
		<sup>
			<xsl:attribute name="id">
				<xsl:value-of select="concat('&footnoteReferenceIdPrefix;', a/@name)"/>
			</xsl:attribute>
			<a href="#co_footnote_{$footnoteLink}" class="&footnoteReferenceClass;">
				<xsl:value-of select="$footnoteNo"/>
			</a>
		</sup>
	</xsl:template>

	<!--Supress the sup tag content in the end,since this is displayed as Footnotes for w_3rd_icdr collection-->
	<xsl:template match="p[child::sup[child::a[starts-with(@name, 'f')]]]"/>

	<xsl:template name="RenderFootnote">
		<xsl:param name="renderHorizontalRule"/>
		<xsl:if test=".//sup[child::a[starts-with(@name, 'f')]]">
			<xsl:if test="$renderHorizontalRule">
				<hr class="&horizontalRuleClass;"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<table id="&footnoteSectionId;" class="&footnoteSectionClass;">
						<tr>
							<td colspan="2" class="&footnoteSectionTitleClass; &printHeadingClass;">
                <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
              </td>
						</tr>
						<xsl:for-each select="//sup[child::a[starts-with(@name, 'f')]]">
							<xsl:variable name="footnoteNo" select="a/text()" />
							<xsl:variable name="footnoteLink" select="substring(a/@href, 2)" />
							<tr>
								<td class="&footnoteNumberClass;">
									<span>
										<xsl:attribute name="id">
											<xsl:value-of select="concat('&footnoteIdPrefix;',  a/@name)"/>
										</xsl:attribute>
										<a href="#co_footnoteReference_{$footnoteLink}">
											<xsl:value-of select="$footnoteNo"/>
										</a>
									</span>
								</td>
								<td class="&footnoteBodyClass;">
									<xsl:apply-templates select="following-sibling::node()" />
								</td>
							</tr>
						</xsl:for-each>
					</table>
				</xsl:when>
				<xsl:otherwise>
					<div id="&footnoteSectionId;" class="&footnoteSectionClass;">
						<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
              <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
            </h2>
						<xsl:for-each select=".//sup[child::a[starts-with(@name, 'f')]]">
							<xsl:variable name="footnoteLink" select="substring(a/@href, 2)" />
							<xsl:variable name="footnoteNo" select="a/text()" />
							<div>
								<div class="&footnoteNumberClass;">
									<span>
										<xsl:attribute name="id">
											<xsl:value-of select="concat('&footnoteIdPrefix;', a/@name)"/>
										</xsl:attribute>
										<a href="#co_footnoteReference_{$footnoteLink}">
											<xsl:value-of select="$footnoteNo"/>
										</a>
									</span>
								</div>
								<div class="&footnoteBodyClass;">
									<xsl:apply-templates select="following-sibling::node()" />
								</div>
							</div>
						</xsl:for-each>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!--For displaying the list tag for w_3rd_icdr collection -->
	<xsl:template match="n-docbody/text/list">
		<xsl:for-each select="item | sublist/item">
			<div class="&paraMainClass;">
				<xsl:apply-templates select="." />
			</div>
		</xsl:for-each>
	</xsl:template>

	<!--For displaying the copy right tag for w_3rd_icdr collection -->
	<xsl:template match="n-docbody/header/prelim/copyright">
		<div class="&copyrightClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- To allow preliminary information to be styled differently (ie. centered), we need to split the document
	     at the last occurence of the front.matter element -->
	<xsl:template match="decision">
		<xsl:if test="(prelim.block[front.matter] and count(prelim.block/node()[following-sibling::front.matter]) &gt; 0) or (not(prelim.block[front.matter]) and count(node()[following-sibling::front.matter]) &gt; 0)">
			<div class="&preFrontMatterClass;">
				<xsl:choose>
					<xsl:when test="prelim.block[front.matter]">
						<xsl:apply-templates select="prelim.block/node()[following-sibling::front.matter]"></xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="node()[following-sibling::front.matter]"></xsl:apply-templates>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>

		<xsl:choose>
			<xsl:when test="prelim.block[front.matter]">
				<xsl:apply-templates select="prelim.block/node()[preceding-sibling::front.matter[not(following-sibling::front.matter)] or self::front.matter[not(following-sibling::front.matter)]]"></xsl:apply-templates>
				<xsl:apply-templates select="prelim.block/following-sibling::node()"/>
			</xsl:when>
			<xsl:when test="front/caption/prelim.block">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="node()[preceding-sibling::front.matter[not(following-sibling::front.matter)] or self::front.matter[not(following-sibling::front.matter)]]"></xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="front/caption/prelim.block | front/caption/source">
		<div class="&simpleContentBlockClass; &prelimBlockClass;">
			<div class="&centerClass;">
				<xsl:apply-templates select="node()"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="front/caption/expandedcite" >
		<div class="&citesClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="caption/date.line">
		<div class="&centerClass;">
			<xsl:call-template name="dateLine"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="related.index">
		<div class="&indexClass;">
			<xsl:apply-templates select="node()"/>
		</div>
	</xsl:template>

	<!-- Admin Decisions sometimes uses primary.title elements as party.line elements, so override this template	-->
	<xsl:template match="primary.title" priority="1">
		<xsl:choose>
		<!-- 	Consider the primary.title to be party.line elements -->
			<xsl:when test="(preceding-sibling::primary.title or following-sibling::primary.title) and (preceding-sibling::versus or following-sibling::versus)">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&partyLineClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="preceding-sibling::primary.title or following-sibling::primary.title">
				<xsl:call-template name="wrapWithDiv" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Fix for duplicate primary.titles in w_adm_oeswage documents; suppress the ones with attribute hidden='Y'. -->
	<xsl:template match="primary.title[(@hidden='Y') and (contains('|w_adm_oeswage|', concat('|', /Document/document-data/collection, '|')))]" priority="1" />

	<xsl:template match="front.matter[date.block]">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="main.text/date.block[/Document/document-data/collection = 'w_adm_ust1k']">
		<div class="&centerClass; &dateClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="head[following-sibling::para]">
		<div class="&centerClass;">
			<xsl:call-template name="head"/>
		</div>
	</xsl:template>

	<xsl:template match="arbitration.case.type | arbitration.award.amount | arbitration.award.date | arbitration.award | arbitrator.name |arbitration.industry.code">
		<div>
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="attorney.line[/Document/document-data/collection = 'w_adm_aaarbawd' or /Document/document-data/collection = 'w_adm_arbaward']">
		<div>
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="arbitrator.block">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="topic.line">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="arbitration.award.range" />

	<!-- Fixes for the collection w_adm_aaarbawd-->
	<xsl:template match="main.text//head">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates/>
		</div>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<xsl:template match="decision//attorney.block | main.text//attorney.name">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Fixes for the collection w_adm_smaas-->
	<xsl:template match="prelim.synopsis//head[/Document/document-data/collection = 'w_adm_smaas']">
		<xsl:text>&#160;</xsl:text>
		<div>
			<xsl:apply-templates/>
		</div>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<xsl:template match="main.text/main.text.body/head[@type = 'left']">
		<xsl:text>&#160;</xsl:text>
		<div >
			<xsl:apply-templates/>
		</div>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<!-- Fixes for the collection w_3rd_arbbio-->
	<xsl:template match="prelim.block//headtext">
		<div class="&centerClass;">
			<b>
				<xsl:apply-templates/>
			</b>
		</div>
	</xsl:template>

	<!-- Arbitrator -->
	<xsl:template match="arbitrator.block[not(descendant::cite.query)]">
		<xsl:call-template name="wrapContentBlockWithGenericClass" >
			<xsl:with-param name="content" select="arbitrator.block/arbitrator"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="arbitrator.block/arbitrator[not(descendant::cite.query)]" priority="1">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template name="arbitratorBlock" match="arbitrator.block">
		<table cellpadding="0" cellspacing="0">
			<tbody>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="arbitrator"/>
					</td>
				</tr>
			</tbody>
		</table>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<!--Using a table tag in order to align the content-->
	<xsl:template match="biographical.information">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates select="head/headtext"/>
		</div>
		<table>
			<tbody>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="arbitrator.number/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="arbitrator.number/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="arbitrator.block/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="arbitrator.block/arbitrator"/>
					</td>
				</tr>
				<xsl:apply-templates select="business.address"/>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="home.address/label"/>
						</b>
					</td>
				</tr>
				<tr>
					<td>
						<xsl:apply-templates select="home.address/address/label"/>
					</td>
					<td>
						<div>
							<xsl:if test="home.address/address/street">
								<xsl:apply-templates select="home.address/address/street"/>
							</xsl:if>
						</div>
						<div>
							<xsl:if test="home.address/address/city">
								<xsl:variable name="homeCity" select="home.address/address/city"/>
								<xsl:if test ="$homeCity != ''">
									<xsl:apply-templates select="home.address/address/city"/>
									<xsl:text>, </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="home.address/address/state">
								<xsl:variable name="homeState" select="home.address/address/state"/>
								<xsl:if test ="$homeState != ''">
									<xsl:apply-templates select="home.address/address/state"/>&nbsp;
								</xsl:if>
							</xsl:if>
							<xsl:if test="home.address/address/zip">
								<xsl:variable name="homeZip" select="home.address/address/zip"/>
								<xsl:if test ="$homeZip != ''">
									<xsl:apply-templates select="home.address/address/zip"/>
								</xsl:if>
							</xsl:if>
							<xsl:if test="home.address/address/country">
								<xsl:variable name="homeCountry" select="home.address/address/country"/>
								<xsl:if test ="$homeCountry != ''">
									<xsl:apply-templates select="home.address/address/country"/>
								</xsl:if>
							</xsl:if>
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<xsl:apply-templates select="home.address/phone/label"/>
					</td>
					<td>
						<xsl:apply-templates select="home.address/phone/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<xsl:apply-templates select="home.address/fax/label"/>
					</td>
					<td>
						<xsl:apply-templates select="home.address/fax/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<xsl:apply-templates select="home.address/email/label"/>
					</td>
					<td>
						<xsl:apply-templates select="home.address/email/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="status/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="status/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="dob/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="dob/text"/>
					</td>
				</tr>

				<tr>
					<td>
						<b>
							<xsl:apply-templates select="birth.place/label"/>
						</b>
					</td>
					<td>
						<xsl:variable name="city" select="birth.place/city"/>
						<xsl:if test ="string-length($city) &gt; 0" >
							<xsl:apply-templates select="birth.place/city"/>
							<xsl:variable name="state" select="birth.place/state"/>
							<xsl:if test ="string-length($state) &gt; 0" >
								<xsl:text>, </xsl:text>
								<xsl:apply-templates select="birth.place/state"/>
								<xsl:variable name="country" select="birth.place/country"/>
								<xsl:if test ="string-length($country) &gt; 0" >
									<xsl:text>, </xsl:text>
									<xsl:apply-templates select="birth.place/country"/>
								</xsl:if>
							</xsl:if>
						</xsl:if>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="occupation/label"/>
						</b>
					</td>
					<td>
						<xsl:for-each select="occupation/text">
							<div>
								<xsl:apply-templates select="."/>
							</div>
						</xsl:for-each>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="cancel.fee/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="cancel.fee/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="cancel.fee.amount/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="cancel.fee.amount/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="cancel.fee.reason/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="cancel.fee.reason/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="perdiem/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="perdiem/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="perdiem.item/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="perdiem.item/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="hearing.procedure/label"/>
						</b>
					</td>
					<td>
						<xsl:for-each select="hearing.procedure/text">
							<div>
								<xsl:apply-templates select="."/>
							</div>
						</xsl:for-each>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="national.academy.member/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="national.academy.member/text"/>
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="issues.industries">
		<table width="600" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<xsl:apply-templates select="issues/head/headtext"/>
				</td>
				<td>
					<xsl:apply-templates select="industries/head/headtext"/>
				</td>
			</tr>
			<tr  valign="top">
				<td>
					<xsl:apply-templates select="issues/list"/>
				</td>
				<td>
					<xsl:apply-templates select="industries/list"/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="issues/list | industries/list">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="list.item" mode="listDelivery" />
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<xsl:apply-templates select="list.item" mode="listDisplay" />
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="list.item" mode="listDelivery">
		<div>
			<span class="&excludeFromAnnotationsClass;">&bull;</span>
			<xsl:text>&#160;</xsl:text>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="list.item" mode="listDisplay">
		<li>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="education.block/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="education">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<div>
					<span class="&excludeFromAnnotationsClass;">&bull;</span>
					<xsl:text>&#160;</xsl:text>
					<xsl:if test="degree">
						<xsl:apply-templates select="degree"/>
						<xsl:text> , </xsl:text>
					</xsl:if>
					<xsl:if test="major">
						<xsl:apply-templates select="major"/>
						<xsl:text> , </xsl:text>
					</xsl:if>
					<xsl:if test="school">
						<xsl:apply-templates select="school"/>
						<xsl:text> , </xsl:text>
					</xsl:if>
					<xsl:if test="date">
						<xsl:apply-templates select="date"/>
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<li>
						<xsl:if test="degree">
							<xsl:apply-templates select="degree"/>
							<xsl:text> , </xsl:text>
						</xsl:if>
						<xsl:if test="major">
							<xsl:apply-templates select="major"/>
							<xsl:text> , </xsl:text>
						</xsl:if>
						<xsl:if test="school">
							<xsl:apply-templates select="school"/>
							<xsl:text> , </xsl:text>
						</xsl:if>
						<xsl:if test="date">
							<xsl:apply-templates select="date"/>
						</xsl:if>
					</li>
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="certification.block/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="certification">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<div>
					<span class="&excludeFromAnnotationsClass;">&bull;</span>
					<xsl:text>&#160;</xsl:text>
					<xsl:if test="state">
						<xsl:apply-templates select="state"/>
						<xsl:text> ; </xsl:text>
					</xsl:if>
					<xsl:if test="year">
						<xsl:apply-templates select="year"/>
						<xsl:text> ; </xsl:text>
					</xsl:if>
					<xsl:if test="certificate">
						<xsl:apply-templates select="certificate"/>
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<li>
						<xsl:if test="state">
							<xsl:apply-templates select="state"/>
							<xsl:text> ; </xsl:text>
						</xsl:if>
						<xsl:if test="year">
							<xsl:apply-templates select="year"/>
							<xsl:text> ; </xsl:text>
						</xsl:if>
						<xsl:if test="certificate">
							<xsl:apply-templates select="certificate"/>
						</xsl:if>
					</li>
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="panel.memberships/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="panel.memberships/list">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="list.item" mode="listDelivery" />
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<xsl:apply-templates select="list.item" mode="listDisplay" />
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="employment.history//headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates/>
			</b>
		</div>
	</xsl:template>

	<xsl:template match="employment.history//employment">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<div>
					<span class="&excludeFromAnnotationsClass;">&bull;</span>
					<xsl:text>&#160;</xsl:text>
					<xsl:if test="employer">
						<xsl:apply-templates select="employer"/>
						<xsl:text> , </xsl:text>
					</xsl:if>
					<xsl:if test="from.date">
						<xsl:apply-templates select="from.date"/>
						<xsl:text> , </xsl:text>
					</xsl:if>
					<xsl:if test="to.date">
						<xsl:apply-templates select="to.date"/>
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<li>
						<xsl:if test="employer">
							<xsl:apply-templates select="employer"/>
							<xsl:text> , </xsl:text>
						</xsl:if>
						<xsl:if test="from.date">
							<xsl:apply-templates select="from.date"/>
							<xsl:text> , </xsl:text>
						</xsl:if>
						<xsl:if test="to.date">
							<xsl:apply-templates select="to.date"/>
						</xsl:if>
					</li>
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="labor.relations/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="labor.relations/list">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="list.item" mode="listDelivery" />
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<xsl:apply-templates select="list.item" mode="listDisplay"/>
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="professional.memberships/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="professional.memberships/list">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="list.item" mode="listDelivery" />
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<xsl:apply-templates select="list.item"  mode="listDisplay" />
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="awards/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="awards/list">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="list.item" mode="listDelivery" />
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<xsl:apply-templates select="list.item"  mode="listDisplay" />
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="publications/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates/>
			</b>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="publication">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<div>
					<span class="&excludeFromAnnotationsClass;">&bull;</span>
					<xsl:text>&#160;</xsl:text>
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<li>
						<xsl:apply-templates/>
					</li>
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="article">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:variable name="displayableCitesForContentType" select="n-docbody/decision/front.matter/title.block/primary.title"/>
	<xsl:variable name="displayableCite" select="$displayableCitesForContentType[1]" />
	<xsl:variable name="displayableCiteId">
		<xsl:choose>
			<xsl:when test="$displayableCite/@ID">
				<xsl:value-of select="$displayableCite/@ID" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="generate-id($displayableCite)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- Specific formats for Personnet -->
	<xsl:template match="n-docbody/decision/front.matter/title.block/primary.title/starpage.anchor[/Document/document-data/collection = 'w_adm_cle_prsnnet']" priority="1">
		<xsl:call-template name="displayStarPage">
			<xsl:with-param name="starPageText">
				<xsl:apply-templates />
			</xsl:with-param>
			<xsl:with-param name="numberOfStars" select="1" />
			<xsl:with-param name="pageset" select="$displayableCiteId" />
		</xsl:call-template>
		<xsl:if test="$IncludeCopyWithRefLinks = true()">
			<xsl:call-template name="generateCopyWithReferenceLink" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="preformatted.text.block/preformatted.text.line[/Document/document-data/collection = 'w_adm_cle_prsnnet']" priority="5">
		<xsl:variable name="contents">
			<xsl:call-template name="PreformattedTextCleaner" />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div>
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
		<xsl:if test="string-length($contents) = 0">
			<xsl:text>&#160;</xsl:text>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="docket.block[/Document/document-data/collection = 'w_adm_cle_prsnnet']" priority="5">
		<div class="&alignHorizontalCenterClass; &docketDateClass;">
			<xsl:apply-templates/>			
		</div>			
	</xsl:template>

	<xsl:template match="author.name[/Document/document-data/collection = 'w_adm_cle_prsnnet']" priority="1">
		<div>
			<xsl:value-of select="text()[position()]"/>
			<xsl:call-template name="citeQuery">
				<xsl:with-param name="citeQueryElement" select="cite.query"/>
			</xsl:call-template>
			<xsl:apply-templates select="text()[position() &gt; 1]"/>
		</div>
	</xsl:template>

	<!-- We suppress Westlaw cite star page information for these 
			 Legislative History - Admin Material and Admin Decisions - Third Party collections. -->
	<xsl:template match="starpage.anchor[/Document/document-data/collection = 'w_adm_ilmtext' and @pageset = 'Starpage_999']" priority="5"/>
	<xsl:template match="starpage.anchor[contains('|w_adm_bicent|w_adm_naicpro|w_adm_naicwp|' , concat('|', /Document/document-data/collection, '|'))]" />

	<xsl:template match="arbitrator.block/arbitrator">
		<xsl:call-template name="citeQuery">
			<xsl:with-param name="citeQueryElement" select="cite.query"/>
			<xsl:with-param name="linkContents">
				<xsl:choose>
					<xsl:when test="cite.query/node()">
						<xsl:for-each select="cite.query/node()">
							<xsl:apply-templates select="."/>
							<xsl:if test="position() != last()">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="cite.query/node()[not(self::starpage.anchor)]"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:apply-templates select="text()[position() &gt; 1]"/>
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Fix for the Bug 335986-->
	<xsl:template match="n-docbody/decision/arbitrator.block" priority="1">
		<xsl:apply-templates  select ="arbitrator.name"/>
		<xsl:apply-templates select="arbitrator.title"/>
	</xsl:template>

	<!-- In order to remove the extra line that is displaying(Bug 323082)-->
	<xsl:template match="citation.block">
	</xsl:template>

	<!-- Fix for the bug 380357-->
	<xsl:template match="address.block/addressee.name">
		<xsl:text>&#160;</xsl:text>
		<div>
			<xsl:call-template name="wrapWithDiv" />
		</div>
	</xsl:template>
	<!-- Fix for the bug 380357-->

	<!-- Applying the templates when there are multiple business.address blocks-->
	<xsl:template match ="business.address">
		<xsl:variable name ="numOfBus">
			<xsl:value-of select ="count(preceding-sibling::business.address) + 1"/>
		</xsl:variable>
		<tr>
			<td>
				<xsl:choose>
					<xsl:when test ="$numOfBus = 2">
						<b>
							<xsl:text>2nd </xsl:text>
						</b>
					</xsl:when>
					<xsl:when test ="$numOfBus > 2">
						<b>
							<xsl:text>Additional </xsl:text>
						</b>
					</xsl:when>
					<xsl:otherwise/>
				</xsl:choose>
				<b>
					<xsl:apply-templates select ="label" />
				</b>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="professional.title/label"/>
			</td>
			<td>
				<xsl:apply-templates select="professional.title/text"/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="organization/label"/>
			</td>
			<td>
				<xsl:apply-templates select="organization/text"/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="address/label"/>
			</td>
			<td>
				<div>
					<xsl:if test="address/street">
						<xsl:apply-templates select="address/street"/>
					</xsl:if>
				</div>
				<div>
					<xsl:if test="address/city">
						<xsl:if test =". != ''">
							<xsl:apply-templates select="address/city"/>
							<xsl:text>, </xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test="address/state">
						<xsl:apply-templates select="address/state"/>&nbsp;
					</xsl:if>
					<xsl:if test="address/zip">
						<xsl:apply-templates select="address/zip"/>
					</xsl:if>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="phone/label"/>
			</td>
			<td>
				<xsl:apply-templates select="phone/text"/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="fax/label"/>
			</td>
			<td>
				<xsl:apply-templates select="fax/text"/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="email/label"/>
			</td>
			<td>
				<xsl:apply-templates select="email/text"/>
			</td>
		</tr>
	</xsl:template>

	<!-- Author Block Head -->
	<xsl:template match="n-docbody/decision/main.text/author.block/head[/Document/document-data/collection = 'w_adm_fsecadm']">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Fix for bug 558843 -->
	<xsl:template match="/Document/n-docbody/decision/prelim.block/prelim.synopsis[/Document/document-data/collection = 'w_adm_dscdo']" priority="2">
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates/>
		</div>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<!-- Fix for bugs 566784 -->
	<xsl:template match="title.info[contains('w_adm_fmanuals|w_adm_dscdo|', concat('|', /Document/document-data/collection, '|'))]" priority="2">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<!-- Suppress starpaging as fix for bugs 558889 and 616813 -->
	<xsl:template match="starpage.anchor[contains('|w_adm_eeocmd|w_adm_fgcbca3|w_adm_fmanuals|w_adm_fwsm|w_adm_fwstds|w_adm_gpds|w_adm_gstds|w_adm_nr1|w_adm_travreg|w_adm_eeoccm|w_adm_dscdo|w_adm_cle_prsnmspi|w_adm_fdamau|', concat('|', /Document/document-data/collection, '|'))]" priority="2"/>

	<!--Author name block -->
	<xsl:template match="author.name[/Document/document-data/collection = 'w_adm_nasdarb']" priority="1">
		<div>
			<xsl:text>&#160;</xsl:text>
		</div>
		<!-- Add extra space between the author names -->
		<xsl:apply-templates />
	</xsl:template>

	<!-- Fix for the defect 372278 -->
	<xsl:template name="FooterCitation">
		<xsl:choose>
			<xsl:when test="/Document/document-data/doc-type/text() = 'Admin Decisions - Treaties'">
				<xsl:variable name="citation" select="n-metadata/metadata.block/md.identifiers/md.cites//md.display.primarycite"/>
				<div class="&citationClass;">
					<xsl:value-of	select="$citation"	/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Checking Citation Type and getting the Jurisdiction court -->
	<!-- Fixed defect 379645, duplicated template from Cite.xsl since the condition doesnot satisfy for this case-->
	<!-- Parallel Cites -->
	<xsl:template match="md.parallelcite/md.parallelcite.info[/Document/document-data/doc-type/text() = 'Admin Decisions - Treaties']" priority="1">
		<xsl:if test="md.display.parallelcite/@display = 'Y' or md.display.parallelcite/@userEntered = 'Y'">
			<xsl:choose>
				<xsl:when test="md.adj.display.parallelcite">
					<xsl:apply-templates select="md.adj.display.parallelcite" />
					<xsl:call-template name="jurisdiction">
						<xsl:with-param name="citeType" select="md.adj.display.parallelcite/@type"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="md.display.parallelcite">
					<xsl:apply-templates select="md.display.parallelcite" />
					<xsl:call-template name="jurisdiction">
						<xsl:with-param name="citeType" select="md.display.parallelcite/@type"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- Fixed defect 377423-->
	<xsl:template match="para" priority="1">
		<xsl:choose>
			<xsl:when test="contains('|w_adm_fgatt|w_adm_north|w_adm_ust1|w_adm_ust1k|w_adm_ust2|w_adm_canada|w_3rd_tif|w_adm_naicpro|w_adm_naicwp|w_adm_fgccab|w_adm_eeocguid|w_adm_eeock|w_adm_eeocmd|w_adm_fgcadm2|w_adm_fgcbca3|w_adm_flbadm|w_adm_fmanuals|w_adm_fwsm|w_adm_fwstds|w_adm_gpds|w_adm_gstds|w_adm_nr1|w_adm_travreg|w_adm_cjract|' , concat('|', /Document/document-data/collection, '|'))">
				<xsl:call-template name="nestedParas"/>
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass;" >
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Fix for table spacing issue in NAIC -->
	<xsl:template match="tgroup[contains('|w_adm_naicpro|w_adm_naicwp|' , concat('|', /Document/document-data/collection, '|'))]" priority="1">
		<xsl:call-template name ="TGroupTemplate">
			<xsl:with-param name ="checkNoColWidthExists" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Supress rendering of md.references for Admin Decisions - Federal (USCCAN collection) -->
	<xsl:template match="md.references[/Document/document-data/collection = 'w_adm_presday']" priority="3" />
	
</xsl:stylesheet>
