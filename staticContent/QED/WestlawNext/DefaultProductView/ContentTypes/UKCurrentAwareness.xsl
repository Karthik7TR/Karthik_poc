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
	<xsl:include href="InternationalLogos.xsl"/>

	<xsl:variable name="primarycite" select="Document/n-docbody/document/metadata.block/md.identifiers/md.cites/md.primarycite" />
	<!-- DO NOT RENDER -->
	<xsl:template match="//map | metadata.block | header | sec_entry_type | journal_id | volume_number | issue_number" />
	<xsl:template match="docid | starpage" />

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<xsl:apply-templates select="n-docbody/document/data"/>
			<!--
				******************************************************************************************************
				* Backlog Item 506268: 
				* Remove all logos from International content. 
				* Add copyright message from royality block and message block centered at the bottom of the document.
				******************************************************************************************************
			-->
			<xsl:apply-templates select="n-docbody/copyright-message" />

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>

	<xsl:template match="data">
		<div class="&centerClass;">
			<xsl:apply-templates select="entry_type"/>
			<xsl:apply-templates select="title"/>
			<xsl:apply-templates select="in_force"/>
			<xsl:apply-templates select="contributors"/>
			<xsl:apply-templates select="citation"/>
			<xsl:apply-templates select="court" />
			<xsl:apply-templates select="judge" />
			<xsl:apply-templates select="judgment_date" />
			<xsl:apply-templates select="publication_date" />
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>

		<div class="&paraMainClass;">&#160;</div>

		<xsl:apply-templates select="royal_assent"/>

		<xsl:apply-templates select="progress"/>

		<xsl:apply-templates select="subjects"/>
		<xsl:apply-templates select="keywords"/>
		<xsl:apply-templates select="//document/metadata.block/md.identifiers/md.cites/md.primarycite"/>
		<xsl:apply-templates select="abstract"/>
		<xsl:apply-templates select="outcome"/>
		<xsl:apply-templates select="cases"/>
		<xsl:apply-templates select="legislation"/>

		<xsl:apply-templates select="body/para"/>

		<xsl:apply-templates select="publisher"/>
		<xsl:apply-templates select="series"/>
		<xsl:apply-templates select="companies"/>
			<xsl:apply-templates select="price"/>
		<xsl:apply-templates select="isbn"/>
		<xsl:apply-templates select="urls"/>
	</xsl:template>

	<xsl:template match="author | entry_type | title | contributors | citation | court | judge | companies | urls | abstract ">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="publication_date">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukPublicationDate;</xsl:text>
			</strong>
			<xsl:choose>
				<xsl:when test="@day">
					<xsl:call-template name="FormatDateValue">
						<xsl:with-param name="thisDay" select="@day"/>
						<xsl:with-param name="thisMonth" select="@month"/>
						<xsl:with-param name="thisYear" select="@year"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="judgment_date">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukJudgmentDate;</xsl:text>
			</strong>
			<!-- 
			sample judgment date: 20 November 2012 
			sample XML: judgment_date day="20" month="11" year="2012"
		-->
			<xsl:call-template name="FormatDateValue">
				<xsl:with-param name="thisDay" select="@day"/>
				<xsl:with-param name="thisMonth" select="@month"/>
				<xsl:with-param name="thisYear" select="@year"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="author">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="publisher">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukPublisher;</xsl:text>
			</strong>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="series">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukSeries;</xsl:text>
			</strong>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="isbn">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukISBN;</xsl:text>
			</strong>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="price">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukPrice;</xsl:text>
			</strong>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="cases">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukCasesReferred;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="case">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="legislation">
		<xsl:apply-templates select="leg_enabling"/>
		<xsl:apply-templates select="leg_amended"/>
		<xsl:apply-templates select="leg_repealed"/>
		<xsl:apply-templates select="leg_referred"/>
	</xsl:template>

	<xsl:template match="legislation/leg_enabling">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukEnablingLegislation;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="legislation/leg_amended">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukLegislationAmended;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="legislation/leg_repealed">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukLegislationRepealed;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="legislation/leg_referred">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukLegislationReferred;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="leg_citation">
		<xsl:if test="preceding-sibling::leg_citation">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="copyright-message">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="progress">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukProgress;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="subjects[1]">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukSubject;</xsl:text>
			</strong>
			<xsl:apply-templates select="subject_heading"  mode="display"/>
			<xsl:apply-templates select="main_subject"  mode="display"/>
		</div>
	</xsl:template>

	<xsl:template match="subjects[2]">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukOtherRelatedSubject;</xsl:text>
			</strong>
			<xsl:call-template name="otherSubjects"/>
		</div>
	</xsl:template>

	<xsl:template name="otherSubjects">
		<xsl:for-each select="../subjects">
			<xsl:if test="position() != 1">
				<xsl:if test="position() > 2">
					<xsl:text>; </xsl:text>
				</xsl:if>
				<xsl:apply-templates select="subject_heading"  mode="display"/>
				<xsl:apply-templates select="main_subject"  mode="display"/>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="subject_heading" />
		
	<xsl:template match="subject_heading" mode="display">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="main_subject"/>
	
	<xsl:template match="main_subject"  mode="display">
			<xsl:if test=". != '' and preceding-sibling::subject_heading">
			<xsl:text> - </xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="keywords">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukKeywords;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="keyword">
		<xsl:apply-templates/>
		<xsl:if test="following-sibling::keyword">; </xsl:if>
	</xsl:template>

	<xsl:template match="abstract">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukAbstract;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="outcome">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukOutcome;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="royal_assent">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukRoyalAssent;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="//document/metadata.block/md.identifiers/md.cites/md.primarycite">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukAdded;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="body/para | para">
		<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="companies">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukCompanies;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="companies/company">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::company">
			<xsl:text>; </xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="urls">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukURL;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="urls/url">
		<div>
			<a target="_new" class="&pauseSessionOnClickClass;">
				<xsl:attribute name="href">
					<xsl:value-of select="."/>
				</xsl:attribute>
				<xsl:apply-templates />
			</a>
		</div>
	</xsl:template>

	<!-- Document body -->

	<xsl:template match="title">
		<strong>
			<xsl:apply-templates/>
		</strong>
	</xsl:template>

	<xsl:template match="in_force">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukInForce;</xsl:text>
			</strong>
			<xsl:choose>
				<xsl:when test="@day">
					<xsl:call-template name="FormatDateValue">
						<xsl:with-param name="thisDay" select="@day"/>
						<xsl:with-param name="thisMonth" select="@month"/>
						<xsl:with-param name="thisYear" select="@year"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="ital">
		<em>
			<xsl:apply-templates/>
		</em>
	</xsl:template>

	<xsl:template match="link">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- 
		sample judgement date: 20 November 2012 
		sample XML: judgment_date day="20" month="11" year="2012"
	-->
	<xsl:template name="FormatDateValue">
		<xsl:param name="thisDay" select="@day"/>
		<xsl:param name="thisMonth" select="@month"/>
		<xsl:param name="thisYear" select="@year"/>

		<xsl:value-of select ="$thisDay"/>
		<xsl:text>&#160;</xsl:text>
		<xsl:choose>
			<xsl:when test ="$thisMonth = 01">January</xsl:when>
			<xsl:when test ="$thisMonth = 02">February</xsl:when>
			<xsl:when test ="$thisMonth = 03">March</xsl:when>
			<xsl:when test ="$thisMonth = 04">April</xsl:when>
			<xsl:when test ="$thisMonth = 05">May</xsl:when>
			<xsl:when test ="$thisMonth = 06">June</xsl:when>
			<xsl:when test ="$thisMonth = 07">July</xsl:when>
			<xsl:when test ="$thisMonth = 08">August</xsl:when>
			<xsl:when test ="$thisMonth = 09">September</xsl:when>
			<xsl:when test ="$thisMonth = 10">October</xsl:when>
			<xsl:when test ="$thisMonth = 11">November</xsl:when>
			<xsl:when test ="$thisMonth = 12">December</xsl:when>
		</xsl:choose>
		<xsl:text>&#160;</xsl:text>
		<xsl:value-of select ="$thisYear"/>
	</xsl:template>

</xsl:stylesheet>
