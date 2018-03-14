<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Index.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="ProfileHeading" />
			<xsl:call-template name="renderStatistics" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Swallow up data elements since these will be injected sepearately -->
	<xsl:template match="prelim.block" />
	<xsl:template match="arbitrator.block" />
	<xsl:template match="profile.block" />
	<xsl:template match="individual.stats.doc" />
	<xsl:template match="natl.avg.stats.doc" />

	<!-- For rendering the Profile Heading at the top of the page -->
	<xsl:template name="ProfileHeading">
		<xsl:param name="bio" select="/Document/n-docbody/biography" />
		<h1 class="&arbTitle;">
			<xsl:apply-templates select="$bio/prelim.block/prelim.head/head/headtext/node()[not(self::bop or self::bos or self::eos or self::eop)]" />
		</h1>
		<div>
			<xsl:apply-templates select="$bio/profile.block/profile.arbitrator.name" />
		</div>
		<div>
			<xsl:apply-templates select="$bio/profile.block/profile.organization" />
			<!--<xsl:choose>
				<xsl:when test="$bio/profile.block/profile.organization/cite.query">
					<xsl:apply-templates select="$bio/profile.block/profile.organization/node()[not(self::cite.query)] |
										 $bio/profile.block/profile.organization/cite.query/node()" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="$bio/profile.block/profile.organization" />
				</xsl:otherwise>
			</xsl:choose>-->
      
		</div>
		<xsl:apply-templates select="$bio/profile.block/profile.business.address" />
		<div>
			<xsl:apply-templates select="$bio/profile.block/profile.phone" />
		</div>
		<div>
			<xsl:apply-templates select="$bio/profile.block/profile.fax" />
		</div>
		<div>
			<xsl:apply-templates select="$bio/profile.block/profile.email" />
		</div>
	</xsl:template>

	<xsl:template match="profile.business.address">
			<div>
				<xsl:apply-templates />
			</div>
	</xsl:template>

	<!-- For rendering the Box containing the statistics charts and tables-->
	<xsl:template name="renderStatistics">
		<div id="co_allTableChartContainer">
			<!-- provide the selector -->
			<xsl:if test="not($IsMobile)">
				<div id="arbChartSelectorWrapper" class="&formTextSelect;">
					<label for="arbChartSelector" class="&accessiblityLabel;">Filter Dataset</label>
					<select id="arbChartSelector">
						<option value="FinraIndividualAllYears|FinraNationalPastYear|FinraNationalAllYears" selected="true">Compare All Data</option>
						<option value="FinraAllCases">All Cases</option>
						<option value="FinraCustomerVMember">Customer v. Member</option>
						<option value="FinraMemberVAssociatedPerson">Member v. Associated Person</option>
						<option value="FinraCustomerVMemberAssociatedPerson">Customer v. Member &amp; Associated Person</option>
						<option value="FinraOther">Other</option>
					</select>
				</div>
				<div class="&clearClass;"></div>
			</xsl:if>
			
			<!-- add in the tables -->
			<xsl:call-template name="RenderAllTables" />
		</div>
	</xsl:template>

	<xsl:template name="RenderAllTables">
		<!-- 
			There's seven tables in total that need to get built.  They are:
				Individual (all years)
				National (past year)
				National (all years)
				
				Customer v. Member
				Member v. Associated Person
				Customer v. Member & Associated Person
				Other
		-->
		<div id="co_arbChartContainerFinraIndividualAllYears" class="&arbChartSection;" name="FinraIndividualAllYears">
			<h2 class="&arbChartSectionHeading;">
				<xsl:value-of select="$arbitratorName"/>
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderIndividualAllYearsTable"/>
		</div>

		<div id="co_arbChartContainerFinraNationalPastYear" class="&arbChartSection;" name="FinraNationalPastYear">
			<h2 class="&arbChartSectionHeading;">
				All FINRA Arbitrators: 
				<span>
					<xsl:value-of select="$nationalPastYearDateRange"/>
				</span>
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderNationalPastYearTable"/>
		</div>

		<div id="co_arbChartContainerFinraNationalAllYears" class="&arbChartSection;" name="FinraNationalAllYears">
			<h2 class="&arbChartSectionHeading;">
				All FINRA Arbitrators: 
				<span>
				<xsl:value-of select="$nationalAllYearsDateRange"/>
				</span>
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderNationalAllYearsTable"/>
		</div>

		<div id="co_arbChartContainerFinraAllCases" class="&arbChartSection;">
			<h2 class="&arbChartSectionHeading;">
				All Cases
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderAllCasesTable"/>
		</div>

		<div id="co_arbChartContainerFinraCustomerVMember" class="&arbChartSection;">
			<h2 class="&arbChartSectionHeading;">
				Customer v. Member
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderCustomerVMemberTable"/>
		</div>

		<div id="co_arbChartContainerFinraMemberVAssociatedPerson" class="&arbChartSection;">
			<h2 class="&arbChartSectionHeading;">
				Member v. Associated Person
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderMemberVAssociatedPersonTable"/>
		</div>

		<div id="co_arbChartContainerFinraCustomerVMemberAssociatedPerson" class="&arbChartSection;">
			<h2 class="&arbChartSectionHeading;">
				Customer v. Member &amp; Associated Person
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderCustomerVMemberAssociatedPersonTable"/>
		</div>

		<div id="co_arbChartContainerFinraOther" class="&arbChartSection;">
			<h2 class="&arbChartSectionHeading;">
				Other
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderOtherTable"/>
		</div>
		
		<div id="co_asteriskText">
			<xsl:if test="not($IsMobile)">
				<p>*Statistics are present for each prevailing party.</p>
				<p>**Includes cases with two or more party types as Claimant or Respondent, Cross-claims, and cases where Claimant and Respondent are same party type.</p>
			</xsl:if>
		</div>

		<input id="documentGuid" type="hidden">
			<xsl:attribute name="value">
				<xsl:value-of select="$Guid"/>
			</xsl:attribute>
		</input>
		<input id="websiteHost" type="hidden">
			<xsl:attribute name="value">
				<xsl:value-of select="$Website"/>
			</xsl:attribute>
		</input>
	</xsl:template>

	<xsl:template name="PrevailingText">
		<xsl:if test="not($IsMobile)">
		<div class="&centerClass;">
			Prevailing Party*
		</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="profile.email">
		<xsl:choose>
			<xsl:when test="cite.query">
				<a class="&pauseSessionOnClickClass;">
					<xsl:attribute name="href">mailto:<xsl:value-of select="cite.query/@w-normalized-cite"/></xsl:attribute>
					<!-- apply templates to everything under cite.query.  we don't want the normal cite query templates to render this
					because it'll generate a bad WestlawNext link. Using node() should supply apply-templates with everything underneath
					the cite.query node, that way for example if there is a highlight on the text it'll get rendered. -->
					<xsl:apply-templates select="cite.query/node()" />
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--
		Gather the table data.
	
		These are declared at the global level so that the multiple table
		building functions (there are 7 of them) can all access the data.  Some of the tables
		are just a different view of the data and reuse the same xpath data points.
		
		Declaring these globally reduces parameter and xpath code duplication as well as reducing the
		xpath execution time.
	-->
		
	<xsl:variable name="individualSourceTable" select="//individual.stats.doc/tbl/table/tgroup/tbody" />
	<xsl:variable name="pastYearSourceTable" select="//natl.avg.stats.doc/tbl[1]/table/tgroup/tbody" />
	<xsl:variable name="allYearsSourceTable" select="//natl.avg.stats.doc/tbl[2]/table/tgroup/tbody" />
	
	<!-- Individual - Customer v. Member -->
	<xsl:variable name="individualCustomerVMemberCustomerCount" select="$individualSourceTable/row[2]/entry[3]" />
	<xsl:variable name="individualCustomerVMemberMemberCount" select="$individualSourceTable/row[2]/entry[4]" />
	<xsl:variable name="individualCustomerVMemberAssociatedPersonCount" select="$individualSourceTable/row[2]/entry[5]" />
	<xsl:variable name="individualCustomerVMemberMemberAssociatedPersonCount" select="$individualSourceTable/row[2]/entry[6]" />
	<xsl:variable name="individualCustomerVMemberSplitCount" select="$individualSourceTable/row[2]/entry[7]" />

	<xsl:variable name="individualCustomerVMemberCustomerPercentage" select="$individualSourceTable/row[3]/entry[3]" />
	<xsl:variable name="individualCustomerVMemberMemberPercentage" select="$individualSourceTable/row[3]/entry[4]" />
	<xsl:variable name="individualCustomerVMemberAssociatedPersonPercentage" select="$individualSourceTable/row[3]/entry[5]" />
	<xsl:variable name="individualCustomerVMemberMemberAssociatedPersonPercentage" select="$individualSourceTable/row[3]/entry[6]" />
	<xsl:variable name="individualCustomerVMemberSplitPercentage" select="$individualSourceTable/row[3]/entry[7]" />

	<!-- Individual - Member v. Associated Person-->
	<xsl:variable name="individualMemberVAssociatedPersonCustomerCount" select="$individualSourceTable/row[4]/entry[3]" />
	<xsl:variable name="individualMemberVAssociatedPersonMemberCount" select="$individualSourceTable/row[4]/entry[4]" />
	<xsl:variable name="individualMemberVAssociatedPersonAssociatedPersonCount" select="$individualSourceTable/row[4]/entry[5]" />
	<xsl:variable name="individualMemberVAssociatedPersonMemberAssociatedPersonCount" select="$individualSourceTable/row[4]/entry[6]" />
	<xsl:variable name="individualMemberVAssociatedPersonSplitCount" select="$individualSourceTable/row[4]/entry[7]" />

	<xsl:variable name="individualMemberVAssociatedPersonCustomerPercentage" select="$individualSourceTable/row[5]/entry[3]" />
	<xsl:variable name="individualMemberVAssociatedPersonMemberPercentage" select="$individualSourceTable/row[5]/entry[4]" />
	<xsl:variable name="individualMemberVAssociatedPersonAssociatedPersonPercentage" select="$individualSourceTable/row[5]/entry[5]" />
	<xsl:variable name="individualMemberVAssociatedPersonMemberAssociatedPersonPercentage" select="$individualSourceTable/row[5]/entry[6]" />
	<xsl:variable name="individualMemberVAssociatedPersonSplitPercentage" select="$individualSourceTable/row[5]/entry[7]" />

	<!-- Individual - Customer v Member & Associated Person -->
	<xsl:variable name="individualCustomerVMemberAssociatedPersonCustomerCount" select="$individualSourceTable/row[6]/entry[3]" />
	<xsl:variable name="individualCustomerVMemberAssociatedPersonMemberCount" select="$individualSourceTable/row[6]/entry[4]" />
	<xsl:variable name="individualCustomerVMemberAssociatedPersonAssociatedPersonCount" select="$individualSourceTable/row[6]/entry[5]" />
	<xsl:variable name="individualCustomerVMemberAssociatedPersonMemberAssociatedPersonCount" select="$individualSourceTable/row[6]/entry[6]" />
	<xsl:variable name="individualCustomerVMemberAssociatedPersonSplitCount" select="$individualSourceTable/row[6]/entry[7]" />

	<xsl:variable name="individualCustomerVMemberAssociatedPersonCustomerPercentage" select="$individualSourceTable/row[7]/entry[3]" />
	<xsl:variable name="individualCustomerVMemberAssociatedPersonMemberPercentage" select="$individualSourceTable/row[7]/entry[4]" />
	<xsl:variable name="individualCustomerVMemberAssociatedPersonAssociatedPersonPercentage" select="$individualSourceTable/row[7]/entry[5]" />
	<xsl:variable name="individualCustomerVMemberAssociatedPersonMemberAssociatedPersonPercentage" select="$individualSourceTable/row[7]/entry[6]" />
	<xsl:variable name="individualCustomerVMemberAssociatedPersonSplitPercentage" select="$individualSourceTable/row[7]/entry[7]" />

	<!-- Individual - Other -->
	<xsl:variable name="individualOtherCustomerCount" select="$individualSourceTable/row[8]/entry[3]" />
	<xsl:variable name="individualOtherMemberCount" select="$individualSourceTable/row[8]/entry[4]" />
	<xsl:variable name="individualOtherAssociatedPersonCount" select="$individualSourceTable/row[8]/entry[5]" />
	<xsl:variable name="individualOtherMemberAssociatedPersonCount" select="$individualSourceTable/row[8]/entry[6]" />
	<xsl:variable name="individualOtherSplitCount" select="$individualSourceTable/row[8]/entry[7]" />

	<xsl:variable name="individualOtherCustomerPercentage" select="$individualSourceTable/row[9]/entry[3]" />
	<xsl:variable name="individualOtherMemberPercentage" select="$individualSourceTable/row[9]/entry[4]" />
	<xsl:variable name="individualOtherAssociatedPersonPercentage" select="$individualSourceTable/row[9]/entry[5]" />
	<xsl:variable name="individualOtherMemberAssociatedPersonPercentage" select="$individualSourceTable/row[9]/entry[6]" />
	<xsl:variable name="individualOtherSplitPercentage" select="$individualSourceTable/row[9]/entry[7]" />

	<!-- National Past Year - Customer v. Member-->
	<xsl:variable name="nationalPastYearCustomerVMemberCustomerCount" select="$pastYearSourceTable/row[2]/entry[3]" />
	<xsl:variable name="nationalPastYearCustomerVMemberMemberCount" select="$pastYearSourceTable/row[2]/entry[4]" />
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonCount" select="$pastYearSourceTable/row[2]/entry[5]" />
	<xsl:variable name="nationalPastYearCustomerVMemberMemberAssociatedPersonCount" select="$pastYearSourceTable/row[2]/entry[6]" />
	<xsl:variable name="nationalPastYearCustomerVMemberSplitCount" select="$pastYearSourceTable/row[2]/entry[7]" />

	<xsl:variable name="nationalPastYearCustomerVMemberCustomerPercentage" select="$pastYearSourceTable/row[3]/entry[3]" />
	<xsl:variable name="nationalPastYearCustomerVMemberMemberPercentage" select="$pastYearSourceTable/row[3]/entry[4]" />
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonPercentage" select="$pastYearSourceTable/row[3]/entry[5]" />
	<xsl:variable name="nationalPastYearCustomerVMemberMemberAssociatedPersonPercentage" select="$pastYearSourceTable/row[3]/entry[6]" />
	<xsl:variable name="nationalPastYearCustomerVMemberSplitPercentage" select="$pastYearSourceTable/row[3]/entry[7]" />

	<!-- National Past Year - Member v. Associated Person-->
	<xsl:variable name="nationalPastYearMemberVAssociatedPersonCustomerCount" select="$pastYearSourceTable/row[4]/entry[3]" />
	<xsl:variable name="nationalPastYearMemberVAssociatedPersonMemberCount" select="$pastYearSourceTable/row[4]/entry[4]" />
	<xsl:variable name="nationalPastYearMemberVAssociatedPersonAssociatedPersonCount" select="$pastYearSourceTable/row[4]/entry[5]" />
	<xsl:variable name="nationalPastYearMemberVAssociatedPersonMemberAssociatedPersonCount" select="$pastYearSourceTable/row[4]/entry[6]" />
	<xsl:variable name="nationalPastYearMemberVAssociatedPersonSplitCount" select="$pastYearSourceTable/row[4]/entry[7]" />

	<xsl:variable name="nationalPastYearMemberVAssociatedPersonCustomerPercentage" select="$pastYearSourceTable/row[5]/entry[3]" />
	<xsl:variable name="nationalPastYearMemberVAssociatedPersonMemberPercentage" select="$pastYearSourceTable/row[5]/entry[4]" />
	<xsl:variable name="nationalPastYearMemberVAssociatedPersonAssociatedPersonPercentage" select="$pastYearSourceTable/row[5]/entry[5]" />
	<xsl:variable name="nationalPastYearMemberVAssociatedPersonMemberAssociatedPersonPercentage" select="$pastYearSourceTable/row[5]/entry[6]" />
	<xsl:variable name="nationalPastYearMemberVAssociatedPersonSplitPercentage" select="$pastYearSourceTable/row[5]/entry[7]" />

	<!-- National Past Year - Customer v. Member & Associated Person-->
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonCustomerCount" select="$pastYearSourceTable/row[6]/entry[3]" />
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonMemberCount" select="$pastYearSourceTable/row[6]/entry[4]" />
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonAssociatedPersonCount" select="$pastYearSourceTable/row[6]/entry[5]" />
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonMemberAssociatedPersonCount" select="$pastYearSourceTable/row[6]/entry[6]" />
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonSplitCount" select="$pastYearSourceTable/row[6]/entry[7]" />

	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonCustomerPercentage" select="$pastYearSourceTable/row[7]/entry[3]" />
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonMemberPercentage" select="$pastYearSourceTable/row[7]/entry[4]" />
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonAssociatedPersonPercentage" select="$pastYearSourceTable/row[7]/entry[5]" />
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonMemberAssociatedPersonPercentage" select="$pastYearSourceTable/row[7]/entry[6]" />
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonSplitPercentage" select="$pastYearSourceTable/row[7]/entry[7]" />

	<!-- National Past Year -Other-->
	<xsl:variable name="nationalPastYearOtherCustomerCount" select="$pastYearSourceTable/row[8]/entry[3]" />
	<xsl:variable name="nationalPastYearOtherMemberCount" select="$pastYearSourceTable/row[8]/entry[4]" />
	<xsl:variable name="nationalPastYearOtherAssociatedPersonCount" select="$pastYearSourceTable/row[8]/entry[5]" />
	<xsl:variable name="nationalPastYearOtherMemberAssociatedPersonCount" select="$pastYearSourceTable/row[8]/entry[6]" />
	<xsl:variable name="nationalPastYearOtherSplitCount" select="$pastYearSourceTable/row[8]/entry[7]" />

	<xsl:variable name="nationalPastYearOtherCustomerPercentage" select="$pastYearSourceTable/row[9]/entry[3]" />
	<xsl:variable name="nationalPastYearOtherMemberPercentage" select="$pastYearSourceTable/row[9]/entry[4]" />
	<xsl:variable name="nationalPastYearOtherAssociatedPersonPercentage" select="$pastYearSourceTable/row[9]/entry[5]" />
	<xsl:variable name="nationalPastYearOtherMemberAssociatedPersonPercentage" select="$pastYearSourceTable/row[9]/entry[6]" />
	<xsl:variable name="nationalPastYearOtherSplitPercentage" select="$pastYearSourceTable/row[9]/entry[7]" />

	<!-- National All Years - Customer v. Member-->
	<xsl:variable name="nationalAllYearsCustomerVMemberCustomerCount" select="$allYearsSourceTable/row[2]/entry[3]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberMemberCount" select="$allYearsSourceTable/row[2]/entry[4]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonCount" select="$allYearsSourceTable/row[2]/entry[5]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberMemberAssociatedPersonCount" select="$allYearsSourceTable/row[2]/entry[6]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberSplitCount" select="$allYearsSourceTable/row[2]/entry[7]" />

	<xsl:variable name="nationalAllYearsCustomerVMemberCustomerPercentage" select="$allYearsSourceTable/row[3]/entry[3]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberMemberPercentage" select="$allYearsSourceTable/row[3]/entry[4]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonPercentage" select="$allYearsSourceTable/row[3]/entry[5]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberMemberAssociatedPersonPercentage" select="$allYearsSourceTable/row[3]/entry[6]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberSplitPercentage" select="$allYearsSourceTable/row[3]/entry[7]" />

	<!-- National All Years - Member v. Associated Person-->
	<xsl:variable name="nationalAllYearsMemberVAssociatedPersonCustomerCount" select="$allYearsSourceTable/row[4]/entry[3]" />
	<xsl:variable name="nationalAllYearsMemberVAssociatedPersonMemberCount" select="$allYearsSourceTable/row[4]/entry[4]" />
	<xsl:variable name="nationalAllYearsMemberVAssociatedPersonAssociatedPersonCount" select="$allYearsSourceTable/row[4]/entry[5]" />
	<xsl:variable name="nationalAllYearsMemberVAssociatedPersonMemberAssociatedPersonCount" select="$allYearsSourceTable/row[4]/entry[6]" />
	<xsl:variable name="nationalAllYearsMemberVAssociatedPersonSplitCount" select="$allYearsSourceTable/row[4]/entry[7]" />

	<xsl:variable name="nationalAllYearsMemberVAssociatedPersonCustomerPercentage" select="$allYearsSourceTable/row[5]/entry[3]" />
	<xsl:variable name="nationalAllYearsMemberVAssociatedPersonMemberPercentage" select="$allYearsSourceTable/row[5]/entry[4]" />
	<xsl:variable name="nationalAllYearsMemberVAssociatedPersonAssociatedPersonPercentage" select="$allYearsSourceTable/row[5]/entry[5]" />
	<xsl:variable name="nationalAllYearsMemberVAssociatedPersonMemberAssociatedPersonPercentage" select="$allYearsSourceTable/row[5]/entry[6]" />
	<xsl:variable name="nationalAllYearsMemberVAssociatedPersonSplitPercentage" select="$allYearsSourceTable/row[5]/entry[7]" />

	<!-- National All Years - Customer v. Memvber & Associated Person-->
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonCustomerCount" select="$allYearsSourceTable/row[6]/entry[3]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonMemberCount" select="$allYearsSourceTable/row[6]/entry[4]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonAssociatedPersonCount" select="$allYearsSourceTable/row[6]/entry[5]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonMemberAssociatedPersonCount" select="$allYearsSourceTable/row[6]/entry[6]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonSplitCount" select="$allYearsSourceTable/row[6]/entry[7]" />

	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonCustomerPercentage" select="$allYearsSourceTable/row[7]/entry[3]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonMemberPercentage" select="$allYearsSourceTable/row[7]/entry[4]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonAssociatedPersonPercentage" select="$allYearsSourceTable/row[7]/entry[5]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonMemberAssociatedPersonPercentage" select="$allYearsSourceTable/row[7]/entry[6]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonSplitPercentage" select="$allYearsSourceTable/row[7]/entry[7]" />

	<!-- National All Years - Other-->
	<xsl:variable name="nationalAllYearsOtherCustomerCount" select="$allYearsSourceTable/row[8]/entry[3]" />
	<xsl:variable name="nationalAllYearsOtherMemberCount" select="$allYearsSourceTable/row[8]/entry[4]" />
	<xsl:variable name="nationalAllYearsOtherAssociatedPersonCount" select="$allYearsSourceTable/row[8]/entry[5]" />
	<xsl:variable name="nationalAllYearsOtherMemberAssociatedPersonCount" select="$allYearsSourceTable/row[8]/entry[6]" />
	<xsl:variable name="nationalAllYearsOtherSplitCount" select="$allYearsSourceTable/row[8]/entry[7]" />

	<xsl:variable name="nationalAllYearsOtherCustomerPercentage" select="$allYearsSourceTable/row[9]/entry[3]" />
	<xsl:variable name="nationalAllYearsOtherMemberPercentage" select="$allYearsSourceTable/row[9]/entry[4]" />
	<xsl:variable name="nationalAllYearsOtherAssociatedPersonPercentage" select="$allYearsSourceTable/row[9]/entry[5]" />
	<xsl:variable name="nationalAllYearsOtherMemberAssociatedPersonPercentage" select="$allYearsSourceTable/row[9]/entry[6]" />
	<xsl:variable name="nationalAllYearsOtherSplitPercentage" select="$allYearsSourceTable/row[9]/entry[7]" />

	<!-- Totals -->
	<xsl:variable name="individualCustomerVMemberTotalCount" select="$individualSourceTable/row[2]/entry[2]" />
	<xsl:variable name="individualMemberVAssociatedPersonTotalCount" select="$individualSourceTable/row[4]/entry[2]" />
	<xsl:variable name="individualCustomerVMemberAssociatedPersonTotalCount" select="$individualSourceTable/row[6]/entry[2]" />
	<xsl:variable name="individualOtherTotalCount" select="$individualSourceTable/row[8]/entry[2]" />

	<xsl:variable name="nationalPastYearCustomerVMemberTotalCount" select="$pastYearSourceTable/row[2]/entry[2]" />
	<xsl:variable name="nationalPastYearMemberVAssociatedPersonTotalCount" select="$pastYearSourceTable/row[4]/entry[2]" />
	<xsl:variable name="nationalPastYearCustomerVMemberAssociatedPersonTotalCount" select="$pastYearSourceTable/row[6]/entry[2]" />
	<xsl:variable name="nationalPastYearOtherTotalCount" select="$pastYearSourceTable/row[8]/entry[2]" />

	<xsl:variable name="nationalAllYearsCustomerVMemberTotalCount" select="$allYearsSourceTable/row[2]/entry[2]" />
	<xsl:variable name="nationalAllYearsMemberVAssociatedPersonTotalCount" select="$allYearsSourceTable/row[4]/entry[2]" />
	<xsl:variable name="nationalAllYearsCustomerVMemberAssociatedPersonTotalCount" select="$allYearsSourceTable/row[6]/entry[2]" />
	<xsl:variable name="nationalAllYearsOtherTotalCount" select="$allYearsSourceTable/row[8]/entry[2]" />

	<!-- Individual All Cases -->
	<xsl:variable name="individualAllCasesCustomerCount" select="$individualCustomerVMemberCustomerCount + $individualMemberVAssociatedPersonCustomerCount + $individualCustomerVMemberAssociatedPersonCustomerCount + $individualOtherCustomerCount"/>
	<xsl:variable name="individualAllCasesMemberCount" select="$individualCustomerVMemberMemberCount + $individualMemberVAssociatedPersonMemberCount + $individualCustomerVMemberAssociatedPersonMemberCount + $individualOtherMemberCount"/>
	<xsl:variable name="individualAllCasesAssociatedPersonCount" select="$individualCustomerVMemberAssociatedPersonCount + $individualMemberVAssociatedPersonAssociatedPersonCount + $individualCustomerVMemberAssociatedPersonAssociatedPersonCount + $individualOtherAssociatedPersonCount"/>
	<xsl:variable name="individualAllCasesMemberAssociatedPersonCount" select="$individualCustomerVMemberMemberAssociatedPersonCount + $individualMemberVAssociatedPersonMemberAssociatedPersonCount + $individualCustomerVMemberAssociatedPersonMemberAssociatedPersonCount + $individualOtherMemberAssociatedPersonCount"/>
	<xsl:variable name="individualAllCasesSplitCount" select="$individualCustomerVMemberSplitCount + $individualMemberVAssociatedPersonSplitCount + $individualCustomerVMemberAssociatedPersonSplitCount + $individualOtherSplitCount"/>
	<xsl:variable name="individualAllCasesTotalCount" select="$individualAllCasesCustomerCount + $individualAllCasesMemberCount + $individualAllCasesAssociatedPersonCount + $individualAllCasesMemberAssociatedPersonCount + $individualAllCasesSplitCount" />

	<xsl:variable name="individualAllCasesCustomerPercentage" select="concat(round($individualAllCasesCustomerCount div $individualAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="individualAllCasesMemberPercentage" select="concat(round($individualAllCasesMemberCount div $individualAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="individualAllCasesAssociatedPersonPercentage" select="concat(round($individualAllCasesAssociatedPersonCount div $individualAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="individualAllCasesMemberAssociatedPersonPercentage" select="concat(round($individualAllCasesMemberAssociatedPersonCount div $individualAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="individualAllCasesSplitPercentage" select="concat(round($individualAllCasesSplitCount div $individualAllCasesTotalCount * 100), '%')"/>
	
	<!-- National Past Year - All Cases -->
	<xsl:variable name="nationalPastYearAllCasesCustomerCount" select="$nationalPastYearCustomerVMemberCustomerCount + $nationalPastYearMemberVAssociatedPersonCustomerCount + $nationalPastYearCustomerVMemberAssociatedPersonCustomerCount + $nationalPastYearOtherCustomerCount"/>
	<xsl:variable name="nationalPastYearAllCasesMemberCount" select="$nationalPastYearCustomerVMemberMemberCount + $nationalPastYearMemberVAssociatedPersonMemberCount + $nationalPastYearCustomerVMemberAssociatedPersonMemberCount + $nationalPastYearOtherMemberCount"/>
	<xsl:variable name="nationalPastYearAllCasesAssociatedPersonCount" select="$nationalPastYearCustomerVMemberAssociatedPersonCount + $nationalPastYearMemberVAssociatedPersonAssociatedPersonCount + $nationalPastYearCustomerVMemberAssociatedPersonAssociatedPersonCount + $nationalPastYearOtherAssociatedPersonCount"/>
	<xsl:variable name="nationalPastYearAllCasesMemberAssociatedPersonCount" select="$nationalPastYearCustomerVMemberMemberAssociatedPersonCount + $nationalPastYearMemberVAssociatedPersonMemberAssociatedPersonCount + $nationalPastYearCustomerVMemberAssociatedPersonMemberAssociatedPersonCount + $nationalPastYearOtherMemberAssociatedPersonCount"/>
	<xsl:variable name="nationalPastYearAllCasesSplitCount" select="$nationalPastYearCustomerVMemberSplitCount + $nationalPastYearMemberVAssociatedPersonSplitCount + $nationalPastYearCustomerVMemberAssociatedPersonSplitCount + $nationalPastYearOtherSplitCount"/>
	<xsl:variable name="nationalPastYearAllCasesTotalCount" select="$nationalPastYearAllCasesCustomerCount + $nationalPastYearAllCasesMemberCount + $nationalPastYearAllCasesAssociatedPersonCount + $nationalPastYearAllCasesMemberAssociatedPersonCount + $nationalPastYearAllCasesSplitCount" />

	<xsl:variable name="nationalPastYearAllCasesCustomerPercentage" select="concat(round($nationalPastYearAllCasesCustomerCount div $nationalPastYearAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="nationalPastYearAllCasesMemberPercentage" select="concat(round($nationalPastYearAllCasesMemberCount div $nationalPastYearAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="nationalPastYearAllCasesAssociatedPersonPercentage" select="concat(round($nationalPastYearAllCasesAssociatedPersonCount div $nationalPastYearAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="nationalPastYearAllCasesMemberAssociatedPersonPercentage" select="concat(round($nationalPastYearAllCasesMemberAssociatedPersonCount div $nationalPastYearAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="nationalPastYearAllCasesSplitPercentage" select="concat(round($nationalPastYearAllCasesSplitCount div $nationalPastYearAllCasesTotalCount * 100), '%')"/>
	
	<!-- National All Years - All Cases -->
	<xsl:variable name="nationalAllYearsAllCasesCustomerCount" select="$nationalAllYearsCustomerVMemberCustomerCount + $nationalAllYearsMemberVAssociatedPersonCustomerCount + $nationalAllYearsCustomerVMemberAssociatedPersonCustomerCount + $nationalAllYearsOtherCustomerCount"/>
	<xsl:variable name="nationalAllYearsAllCasesMemberCount" select="$nationalAllYearsCustomerVMemberMemberCount + $nationalAllYearsMemberVAssociatedPersonMemberCount + $nationalAllYearsCustomerVMemberAssociatedPersonMemberCount + $nationalAllYearsOtherMemberCount"/>
	<xsl:variable name="nationalAllYearsAllCasesAssociatedPersonCount" select="$nationalAllYearsCustomerVMemberAssociatedPersonCount + $nationalAllYearsMemberVAssociatedPersonAssociatedPersonCount + $nationalAllYearsCustomerVMemberAssociatedPersonAssociatedPersonCount + $nationalAllYearsOtherAssociatedPersonCount"/>
	<xsl:variable name="nationalAllYearsAllCasesMemberAssociatedPersonCount" select="$nationalAllYearsCustomerVMemberMemberAssociatedPersonCount + $nationalAllYearsMemberVAssociatedPersonMemberAssociatedPersonCount + $nationalAllYearsCustomerVMemberAssociatedPersonMemberAssociatedPersonCount + $nationalAllYearsOtherMemberAssociatedPersonCount"/>
	<xsl:variable name="nationalAllYearsAllCasesSplitCount" select="$nationalAllYearsCustomerVMemberSplitCount + $nationalAllYearsMemberVAssociatedPersonSplitCount + $nationalAllYearsCustomerVMemberAssociatedPersonSplitCount + $nationalAllYearsOtherSplitCount"/>
	<xsl:variable name="nationalAllYearsAllCasesTotalCount" select="$nationalAllYearsAllCasesCustomerCount + $nationalAllYearsAllCasesMemberCount + $nationalAllYearsAllCasesAssociatedPersonCount + $nationalAllYearsAllCasesMemberAssociatedPersonCount + $nationalAllYearsAllCasesSplitCount" />

	<xsl:variable name="nationalAllYearsAllCasesCustomerPercentage" select="concat(round($nationalAllYearsAllCasesCustomerCount div $nationalAllYearsAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="nationalAllYearsAllCasesMemberPercentage" select="concat(round($nationalAllYearsAllCasesMemberCount div $nationalAllYearsAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="nationalAllYearsAllCasesAssociatedPersonPercentage" select="concat(round($nationalAllYearsAllCasesAssociatedPersonCount div $nationalAllYearsAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="nationalAllYearsAllCasesMemberAssociatedPersonPercentage" select="concat(round($nationalAllYearsAllCasesMemberAssociatedPersonCount div $nationalAllYearsAllCasesTotalCount * 100), '%')"/>
	<xsl:variable name="nationalAllYearsAllCasesSplitPercentage" select="concat(round($nationalAllYearsAllCasesSplitCount div $nationalAllYearsAllCasesTotalCount * 100), '%')"/>
	
	<!-- Column Names -->
	<xsl:variable name="arbitratorName" select="//individual.stats.doc/tbl/head/headtext" />
	<xsl:variable name="nationalPastYearDateRange" select="//natl.avg.stats.doc/tbl[1]/head/headtext" />
	<xsl:variable name="nationalAllYearsDateRange" select="//natl.avg.stats.doc/tbl[2]/head/headtext" />

	<xsl:template name="RenderIndividualAllYearsTable">
		<table id="co_arb_individualAllYears">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Cases</th>
					<th class="&arbThGreen;" colspan="2">Customer</th>
					<th class="&arbThBlue;" colspan="2">Member</th>
					<th class="&arbThOrange;" colspan="2">Associated Person</th>
					<th class="&arbThPurple;" colspan="2">Member &amp; Associated Person</th>
					<th class="&arbThRed;" colspan="2">Split</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							All Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesCustomerPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesMemberPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualAllCasesCustomerCount = '0' and $individualAllCasesMemberCount = '0' and $individualAllCasesAssociatedPersonCount = '0' and $individualAllCasesMemberAssociatedPersonCount = '0' and $individualAllCasesSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualAllCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Customer v. Member
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberCustomerPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberMemberPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberSplitPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualCustomerVMemberCustomerCount = '0' and $individualCustomerVMemberMemberCount = '0' and $individualCustomerVMemberAssociatedPersonCount = '0' and $individualCustomerVMemberMemberAssociatedPersonCount = '0' and $individualCustomerVMemberSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualCustomerVMemberTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Member v. Associated Person
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualMemberVAssociatedPersonCustomerPercentage"/>
							<xsl:with-param name="count" select="$individualMemberVAssociatedPersonCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualMemberVAssociatedPersonCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualMemberVAssociatedPersonMemberPercentage"/>
							<xsl:with-param name="count" select="$individualMemberVAssociatedPersonMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualMemberVAssociatedPersonMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualMemberVAssociatedPersonAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualMemberVAssociatedPersonAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualMemberVAssociatedPersonAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualMemberVAssociatedPersonMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualMemberVAssociatedPersonMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualMemberVAssociatedPersonMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualMemberVAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$individualMemberVAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualMemberVAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualMemberVAssociatedPersonCustomerCount = '0' and $individualMemberVAssociatedPersonMemberCount = '0' and $individualMemberVAssociatedPersonAssociatedPersonCount = '0' and $individualMemberVAssociatedPersonMemberAssociatedPersonCount = '0' and $individualMemberVAssociatedPersonSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualMemberVAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Customer v. Member &amp; Associated Person
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberAssociatedPersonCustomerPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberAssociatedPersonCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberAssociatedPersonCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberAssociatedPersonMemberPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberAssociatedPersonMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberAssociatedPersonMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberAssociatedPersonAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberAssociatedPersonAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberAssociatedPersonAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberAssociatedPersonMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualCustomerVMemberAssociatedPersonCustomerCount = '0' and $individualCustomerVMemberAssociatedPersonMemberCount = '0' and $individualCustomerVMemberAssociatedPersonAssociatedPersonCount = '0' and $individualCustomerVMemberAssociatedPersonMemberAssociatedPersonCount = '0' and $individualCustomerVMemberAssociatedPersonSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualCustomerVMemberAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Other**
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualOtherCustomerPercentage"/>
							<xsl:with-param name="count" select="$individualOtherCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualOtherCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualOtherMemberPercentage"/>
							<xsl:with-param name="count" select="$individualOtherMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualOtherMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualOtherAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualOtherAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualOtherAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualOtherMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualOtherMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualOtherMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualOtherSplitPercentage"/>
							<xsl:with-param name="count" select="$individualOtherSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualOtherSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualOtherCustomerCount = '0' and $individualOtherMemberCount = '0' and $individualOtherAssociatedPersonCount = '0' and $individualOtherMemberAssociatedPersonCount = '0' and $individualOtherSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualOtherTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter">
					<xsl:with-param name="numberOfColumns" select="13" />
				</xsl:call-template>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderNationalPastYearTable">
		<table id="co_arb_nationalPastYear">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Cases</th>
					<th class="&arbThGreen;" colspan="2">Customer</th>
					<th class="&arbThBlue;" colspan="2">Member</th>
					<th class="&arbThOrange;" colspan="2">Associated Person</th>
					<th class="&arbThPurple;" colspan="2">Member &amp; Associated Person</th>
					<th class="&arbThRed;" colspan="2">Split</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							All Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalPastYearAllCasesCustomerCount = '0' and $nationalPastYearAllCasesMemberCount = '0' and $nationalPastYearAllCasesAssociatedPersonCount = '0' and $nationalPastYearAllCasesMemberAssociatedPersonCount = '0' and $nationalPastYearAllCasesSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearAllCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Customer v. Member
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalPastYearCustomerVMemberCustomerCount = '0' and $nationalPastYearCustomerVMemberMemberCount = '0' and $nationalPastYearCustomerVMemberAssociatedPersonCount = '0' and $nationalPastYearCustomerVMemberMemberAssociatedPersonCount = '0' and $nationalPastYearCustomerVMemberSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearCustomerVMemberTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Member v. Associated Person
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearMemberVAssociatedPersonCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearMemberVAssociatedPersonCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearMemberVAssociatedPersonCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearMemberVAssociatedPersonMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearMemberVAssociatedPersonMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearMemberVAssociatedPersonMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearMemberVAssociatedPersonAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearMemberVAssociatedPersonAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearMemberVAssociatedPersonAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearMemberVAssociatedPersonMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearMemberVAssociatedPersonMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearMemberVAssociatedPersonMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearMemberVAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearMemberVAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearMemberVAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalPastYearMemberVAssociatedPersonCustomerCount = '0' and $nationalPastYearMemberVAssociatedPersonMemberCount = '0' and $nationalPastYearMemberVAssociatedPersonAssociatedPersonCount = '0' and $nationalPastYearMemberVAssociatedPersonMemberAssociatedPersonCount = '0' and $nationalPastYearMemberVAssociatedPersonSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearMemberVAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Customer v. Member &amp; Associated Person
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberAssociatedPersonCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberAssociatedPersonCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberAssociatedPersonCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberAssociatedPersonMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberAssociatedPersonMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberAssociatedPersonMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberAssociatedPersonAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberAssociatedPersonAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberAssociatedPersonAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberAssociatedPersonMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalPastYearCustomerVMemberAssociatedPersonCustomerCount = '0' and $nationalPastYearCustomerVMemberAssociatedPersonMemberCount = '0' and $nationalPastYearCustomerVMemberAssociatedPersonAssociatedPersonCount = '0' and $nationalPastYearCustomerVMemberAssociatedPersonMemberAssociatedPersonCount = '0' and $nationalPastYearCustomerVMemberAssociatedPersonSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearCustomerVMemberAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Other**
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearOtherCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearOtherCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearOtherCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearOtherMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearOtherMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearOtherMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearOtherAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearOtherAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearOtherAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearOtherMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearOtherMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearOtherMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearOtherSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearOtherSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearOtherSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalPastYearOtherCustomerCount = '0' and $nationalPastYearOtherMemberCount = '0' and $nationalPastYearOtherAssociatedPersonCount = '0' and $nationalPastYearOtherMemberAssociatedPersonCount = '0' and $nationalPastYearOtherSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearOtherTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter">
					<xsl:with-param name="numberOfColumns" select="13" />
				</xsl:call-template>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderNationalAllYearsTable">
		<table id="co_arb_nationalAllYears">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Cases</th>
					<th class="&arbThGreen;" colspan="2">Customer</th>
					<th class="&arbThBlue;" colspan="2">Member</th>
					<th class="&arbThOrange;" colspan="2">Associated Person</th>
					<th class="&arbThPurple;" colspan="2">Member &amp; Associated Person</th>
					<th class="&arbThRed;" colspan="2">Split</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							All Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsAllCasesCustomerCount = '0' and $nationalAllYearsAllCasesMemberCount = '0' and $nationalAllYearsAllCasesAssociatedPersonCount = '0' and $nationalAllYearsAllCasesMemberAssociatedPersonCount = '0' and $nationalAllYearsAllCasesSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsAllCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Customer v. Member
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsCustomerVMemberCustomerCount = '0' and $nationalAllYearsCustomerVMemberMemberCount = '0' and $nationalAllYearsCustomerVMemberAssociatedPersonCount = '0' and $nationalAllYearsCustomerVMemberMemberAssociatedPersonCount = '0' and $nationalAllYearsCustomerVMemberSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsCustomerVMemberTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Member v. Associated Person
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsMemberVAssociatedPersonCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsMemberVAssociatedPersonCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsMemberVAssociatedPersonCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsMemberVAssociatedPersonMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsMemberVAssociatedPersonMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsMemberVAssociatedPersonMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsMemberVAssociatedPersonAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsMemberVAssociatedPersonAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsMemberVAssociatedPersonAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsMemberVAssociatedPersonMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsMemberVAssociatedPersonMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsMemberVAssociatedPersonMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsMemberVAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsMemberVAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsMemberVAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsMemberVAssociatedPersonCustomerCount = '0' and $nationalAllYearsMemberVAssociatedPersonMemberCount = '0' and $nationalAllYearsMemberVAssociatedPersonAssociatedPersonCount = '0' and $nationalAllYearsMemberVAssociatedPersonMemberAssociatedPersonCount = '0' and $nationalAllYearsMemberVAssociatedPersonSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsMemberVAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Customer v. Member &amp; Associated Person
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberAssociatedPersonCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberAssociatedPersonCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberAssociatedPersonCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberAssociatedPersonMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberAssociatedPersonMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberAssociatedPersonMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberAssociatedPersonAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberAssociatedPersonAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberAssociatedPersonAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberAssociatedPersonMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsCustomerVMemberAssociatedPersonCustomerCount = '0' and $nationalAllYearsCustomerVMemberAssociatedPersonMemberCount = '0' and $nationalAllYearsCustomerVMemberAssociatedPersonAssociatedPersonCount = '0' and $nationalAllYearsCustomerVMemberAssociatedPersonMemberAssociatedPersonCount = '0' and $nationalAllYearsCustomerVMemberAssociatedPersonSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsCustomerVMemberAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Other**
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsOtherCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsOtherCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsOtherCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsOtherMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsOtherMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsOtherMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsOtherAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsOtherAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsOtherAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsOtherMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsOtherMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsOtherMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsOtherSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsOtherSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsOtherSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsOtherCustomerCount = '0' and $nationalAllYearsOtherMemberCount = '0' and $nationalAllYearsOtherAssociatedPersonCount = '0' and $nationalAllYearsOtherMemberAssociatedPersonCount = '0' and $nationalAllYearsOtherSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsOtherTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter">
					<xsl:with-param name="numberOfColumns" select="13" />
				</xsl:call-template>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderAllCasesTable">
		<table id="co_arb_allCases">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">All Cases</th>
					<th class="&arbThGreen;" colspan="2">Customer</th>
					<th class="&arbThBlue;" colspan="2">Member</th>
					<th class="&arbThOrange;" colspan="2">Associated Person</th>
					<th class="&arbThPurple;" colspan="2">Member &amp; Associated Person</th>
					<th class="&arbThRed;" colspan="2">Split</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$arbitratorName"/>
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesCustomerPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesMemberPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualAllCasesCustomerCount = '0' and $individualAllCasesMemberCount = '0' and $individualAllCasesAssociatedPersonCount = '0' and $individualAllCasesMemberAssociatedPersonCount = '0' and $individualAllCasesSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualAllCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalPastYearDateRange"/>
						</strong>
						<br/>All FINRA Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
					  <xsl:choose>
						<xsl:when test="$nationalPastYearAllCasesCustomerCount = '0' and $nationalPastYearAllCasesMemberCount = '0' and $nationalPastYearAllCasesAssociatedPersonCount = '0' and $nationalPastYearAllCasesMemberAssociatedPersonCount = '0' and $nationalPastYearAllCasesSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearAllCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalAllYearsDateRange"/>
						</strong>
						<br/>All FINRA Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsAllCasesCustomerCount = '0' and $nationalAllYearsAllCasesMemberCount = '0' and $nationalAllYearsAllCasesAssociatedPersonCount = '0' and $nationalAllYearsAllCasesMemberAssociatedPersonCount = '0' and $nationalAllYearsAllCasesSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsAllCasesTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter">
					<xsl:with-param name="numberOfColumns" select="13" />
				</xsl:call-template>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderCustomerVMemberTable">
		<table id="co_arb_customerVMember">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Customer v. Member</th>
					<th class="&arbThGreen;" colspan="2">Customer</th>
					<th class="&arbThBlue;" colspan="2">Member</th>
					<th class="&arbThRed;" colspan="2">Split</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$arbitratorName"/>
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberCustomerPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberMemberPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberSplitPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						  <xsl:choose>
							<xsl:when test="$individualCustomerVMemberCustomerCount = '0' and $individualCustomerVMemberMemberCount = '0' and $individualCustomerVMemberSplitCount = '0'">0%</xsl:when>
							<xsl:otherwise>100%</xsl:otherwise>
						  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualCustomerVMemberTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalPastYearDateRange"/>
						</strong>
						<br/>All FINRA Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						  <xsl:choose>
							<xsl:when test="$nationalPastYearCustomerVMemberCustomerCount = '0' and $nationalPastYearCustomerVMemberMemberCount = '0' and $nationalPastYearCustomerVMemberSplitCount = '0'">0%</xsl:when>
							<xsl:otherwise>100%</xsl:otherwise>
						  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearCustomerVMemberTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalAllYearsDateRange"/>
						</strong>
						<br/>All FINRA Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsCustomerVMemberCustomerCount = '0' and $nationalAllYearsCustomerVMemberMemberCount = '0' and $nationalAllYearsCustomerVMemberSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
						</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsCustomerVMemberTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter">
					<xsl:with-param name="numberOfColumns" select="9" />
				</xsl:call-template>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderMemberVAssociatedPersonTable">
		<table id="co_arb_memberVAssociatedPerson">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThBlueBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Member v. Associated Person</th>
					<th class="&arbThBlue;" colspan="2">Member</th>
					<th class="&arbThOrange;" colspan="2">Associated Person</th>
					<th class="&arbThRed;" colspan="2">Split</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$arbitratorName"/>
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualMemberVAssociatedPersonMemberPercentage"/>
							<xsl:with-param name="count" select="$individualMemberVAssociatedPersonMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualMemberVAssociatedPersonMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualMemberVAssociatedPersonAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualMemberVAssociatedPersonAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualMemberVAssociatedPersonAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualMemberVAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$individualMemberVAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualMemberVAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
					<xsl:choose>
					<xsl:when test="$individualMemberVAssociatedPersonMemberCount = '0' and $individualMemberVAssociatedPersonAssociatedPersonCount = '0' and $individualMemberVAssociatedPersonSplitCount = '0'">0%</xsl:when>
					<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualMemberVAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalPastYearDateRange"/>
						</strong>
						<br/>All FINRA Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearMemberVAssociatedPersonMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearMemberVAssociatedPersonMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearMemberVAssociatedPersonMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearMemberVAssociatedPersonAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearMemberVAssociatedPersonAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearMemberVAssociatedPersonAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearMemberVAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearMemberVAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearMemberVAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
					<xsl:choose>
					<xsl:when test="$nationalPastYearMemberVAssociatedPersonMemberCount = '0' and $nationalPastYearMemberVAssociatedPersonAssociatedPersonCount = '0' and $nationalPastYearMemberVAssociatedPersonSplitCount = '0'">0%</xsl:when>
					<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearMemberVAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalAllYearsDateRange"/>
						</strong>
						<br/>All FINRA Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsMemberVAssociatedPersonMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsMemberVAssociatedPersonMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsMemberVAssociatedPersonMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsMemberVAssociatedPersonAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsMemberVAssociatedPersonAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsMemberVAssociatedPersonAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsMemberVAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsMemberVAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsMemberVAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
					<xsl:choose>
					<xsl:when test="$nationalAllYearsMemberVAssociatedPersonMemberCount = '0' and $nationalAllYearsMemberVAssociatedPersonAssociatedPersonCount = '0' and $nationalAllYearsMemberVAssociatedPersonSplitCount = '0'">0%</xsl:when>
					<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsMemberVAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter">
					<xsl:with-param name="numberOfColumns" select="9" />
				</xsl:call-template>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderCustomerVMemberAssociatedPersonTable">
		<table id="co_arb_customerVMemberAssociatedPerson">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Customer v. Member &amp; Associated Person</th>
					<th class="&arbThGreen;" colspan="2">Customer</th>
					<th class="&arbThPurple;" colspan="2">Member &amp; Associated Person</th>
					<th class="&arbThRed;" colspan="2">Split</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$arbitratorName"/>
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberAssociatedPersonCustomerPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberAssociatedPersonCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberAssociatedPersonCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberAssociatedPersonMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualCustomerVMemberAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$individualCustomerVMemberAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualCustomerVMemberAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
					<xsl:choose>
					<xsl:when test="$individualCustomerVMemberAssociatedPersonCustomerCount = '0' and $individualCustomerVMemberAssociatedPersonMemberAssociatedPersonCount = '0' and $individualCustomerVMemberAssociatedPersonSplitCount = '0'">0%</xsl:when>
					<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualCustomerVMemberAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalPastYearDateRange"/>
						</strong>
						<br/>All FINRA Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberAssociatedPersonCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberAssociatedPersonCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberAssociatedPersonCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberAssociatedPersonMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearCustomerVMemberAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearCustomerVMemberAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearCustomerVMemberAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
					<xsl:when test="$nationalPastYearCustomerVMemberAssociatedPersonCustomerCount = '0' and $nationalPastYearCustomerVMemberAssociatedPersonMemberAssociatedPersonCount = '0' and $nationalPastYearCustomerVMemberAssociatedPersonSplitCount = '0'">0%</xsl:when>
					<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearCustomerVMemberAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalAllYearsDateRange"/>
						</strong>
						<br/>All FINRA Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberAssociatedPersonCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberAssociatedPersonCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberAssociatedPersonCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberAssociatedPersonMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberAssociatedPersonMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsCustomerVMemberAssociatedPersonSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsCustomerVMemberAssociatedPersonSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsCustomerVMemberAssociatedPersonSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
					<xsl:choose>
					<xsl:when test="$nationalAllYearsCustomerVMemberAssociatedPersonCustomerCount = '0' and $nationalAllYearsCustomerVMemberAssociatedPersonMemberAssociatedPersonCount = '0' and $nationalAllYearsCustomerVMemberAssociatedPersonSplitCount = '0'">0%</xsl:when>
					<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsCustomerVMemberAssociatedPersonTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter">
					<xsl:with-param name="numberOfColumns" select="9" />
				</xsl:call-template>
			</tbody>
		</table>
	</xsl:template>
	<xsl:template name="RenderOtherTable">
		<table id="co_arb_other">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Other</th>
					<th class="&arbThGreen;" colspan="2">Customer</th>
					<th class="&arbThBlue;" colspan="2">Member</th>
					<th class="&arbThOrange;" colspan="2">Associated Person</th>
					<th class="&arbThPurple;" colspan="2">Member &amp; Associated Person</th>
					<th class="&arbThRed;" colspan="2">Split</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$arbitratorName"/>
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualOtherCustomerPercentage"/>
							<xsl:with-param name="count" select="$individualOtherCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualOtherCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualOtherMemberPercentage"/>
							<xsl:with-param name="count" select="$individualOtherMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualOtherMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualOtherAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualOtherAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualOtherAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualOtherMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$individualOtherMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualOtherMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualOtherSplitPercentage"/>
							<xsl:with-param name="count" select="$individualOtherSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualOtherSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
					  <xsl:choose>
						<xsl:when test="$individualOtherCustomerCount = '0' and $individualOtherMemberCount = '0' and $individualOtherAssociatedPersonCount = '0' and $individualOtherMemberAssociatedPersonCount = '0' and $individualOtherSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualOtherTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalPastYearDateRange"/>
						</strong>
						<br/>All FINRA Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearOtherCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearOtherCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearOtherCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearOtherMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearOtherMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearOtherMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearOtherAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearOtherAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearOtherAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearOtherMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearOtherMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearOtherMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearOtherSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearOtherSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearOtherSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						 <xsl:choose>
						<xsl:when test="$nationalPastYearOtherCustomerCount = '0' and $nationalPastYearOtherMemberCount = '0' and $nationalPastYearOtherAssociatedPersonCount = '0' and $nationalPastYearOtherMemberAssociatedPersonCount = '0' and $nationalPastYearOtherSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearOtherTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalAllYearsDateRange"/>
						</strong>
						<br/>All FINRA Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsOtherCustomerPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsOtherCustomerCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsOtherCustomerCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsOtherMemberPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsOtherMemberCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsOtherMemberCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsOtherAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsOtherAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsOtherAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsOtherMemberAssociatedPersonPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsOtherMemberAssociatedPersonCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsOtherMemberAssociatedPersonCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsOtherSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsOtherSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsOtherSplitCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						 <xsl:choose>
						<xsl:when test="$nationalAllYearsOtherCustomerCount = '0' and $nationalAllYearsOtherMemberCount = '0' and $nationalAllYearsOtherAssociatedPersonCount = '0' and $nationalAllYearsOtherMemberAssociatedPersonCount = '0' and $nationalAllYearsOtherSplitCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsOtherTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter">
					<xsl:with-param name="numberOfColumns" select="13" />
				</xsl:call-template>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="determinePercentage">
		<xsl:param name="percentage" />
		<xsl:param name="count" />

		<xsl:choose>
			<xsl:when test ="$percentage = 'NaN%'">
				<xsl:text>0%</xsl:text>
			</xsl:when> 
			<xsl:when test="$percentage = '0%' and $count &gt; 0">
				<xsl:text>&lt;1%</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$percentage"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="mobileTableFooter">
		<xsl:param name="numberOfColumns" />
		<xsl:if test="$IsMobile">
			<tfoot>
				<tr>
					<td>
						<xsl:attribute name="colspan">
							<xsl:value-of select="$numberOfColumns"/>
						</xsl:attribute>
						*Statistics are present for each prevailing party.
					</td>
				</tr>
				<tr>
					<td>
						<xsl:attribute name="colspan">
							<xsl:value-of select="$numberOfColumns"/>
						</xsl:attribute>
						**Includes cases with two or more party types as Claimant or Respondent, Cross-claims, and cases where Claimant and Respondent are same party type.
					</td>
				</tr>
			</tfoot>
		</xsl:if>
	</xsl:template>

	<xsl:template name="mobilePrevailingPartyText">
		<xsl:if test="$IsMobile">
			<span style="font-weight:normal">Prevailing Party*</span>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>