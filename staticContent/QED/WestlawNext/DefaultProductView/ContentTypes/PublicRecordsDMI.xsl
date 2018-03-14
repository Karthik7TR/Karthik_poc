<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsDate.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Do not render these nodes -->
	<xsl:template match ="col.key|p|pc|c|thead/row|l|pre|mrc|van.ti|mail.addr|sufx"/>

	<!-- Database Signon: DMI-US -->

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsDMIClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="dbLogo"/>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_dAndBMarketIdentifiersRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!--  Source Information  -->
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="coverageBeginDateLabel" select="'&pr_dAndBCompletedAnalysis;'"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates select="co.info.b"/>
		<xsl:apply-templates select="execs.b"/>
		<xsl:apply-templates select="bus.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="sales.b"/>
		<xsl:apply-templates select="emp.info.b/emp.b"/>
		<xsl:apply-templates select="relation.b"/>
		<xsl:apply-templates select="this.b"/>
		<xsl:call-template name="dbLogo"/>
	</xsl:template>

	<!-- ********************************************************************** 
	*************************  (C) Company Information (left column)  *********
	************************************************************************-->
	<xsl:template match ="co.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_companyInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match ="duns[ancestor::co.info.b]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_duns;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Name-->
	<xsl:template match ="pri">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Related Names-->
	<xsl:template match ="sec">
    <xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_relatedNames;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address -->
	<xsl:template match="pri.addr">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_address;'"/>
			<xsl:with-param name="streetNum" select="hse.nbr"/>
			<xsl:with-param name="streetDirection" select="str.dir"/>
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="streetSuffix" select="str.typ"/>
			<xsl:with-param name="streetUnitNumber" select="apt.nbr"/>
			<xsl:with-param name="city" select="cty.st.b/cty"/>
			<xsl:with-param name="stateOrProvince" select="cty.st.b/st"/>
			<xsl:with-param name="zip" select="zip"/>
		</xsl:call-template>
		<xsl:apply-templates select="cnty.b"/>
	</xsl:template>

	<!-- County-->
	<xsl:template match="cnty.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Telephone-->
	<xsl:template match ="phone">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_telephone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- State of Incorporation-->
	<xsl:template match ="st.incorp">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateOfIncorporation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date of Incorporation-->
	<xsl:template match ="d.incorp">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfIncorporation;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Current Management Control-->
	<xsl:template match ="yr.srtd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_yearStarted;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*************************  (D) Executive(s) Information (left column)  ****
	************************************************************************-->
	<xsl:template match ="execs.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_executivesInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="exec"/>
		</table>
	</xsl:template>

	<!-- Executive Name -->
	<xsl:template match="exec">
		<xsl:apply-templates select="." mode="name"/>
		<xsl:apply-templates select="funct.ti"/>
	</xsl:template>

	<xsl:template match="exec" mode="name">
		<xsl:variable name="countExecs" select="count(preceding-sibling::exec)+1"/>
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_executiveName;'"/>
			<xsl:with-param name="indexNumber" select="$countExecs"/>
			<xsl:with-param name="selectNodes" select="name"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="name">
		<xsl:call-template name="FormatName">
			<xsl:with-param name="firstName" select="first"/>
			<xsl:with-param name="middleName" select="mid"/>
			<xsl:with-param name="lastName" select="last"/>
			<xsl:with-param name="suffixName" select="sufx"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="funct.ti">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_executiveTitle;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*************************  (E) Business Description (left column)  ********
	************************************************************************-->
	<xsl:template match ="bus.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_businessDescription;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Line of Business-->
	<xsl:template match ="line.bus">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lineOfBusiness;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Industry Group -->
	<xsl:template match ="indus">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_industryGroup;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Primary SIC, Secondary SIC etc.-->
	<xsl:template match="pri.sic|sec.sic|third.sic|fourth.sic|fifth.sic|sixth.sic">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test ="self::pri.sic">
					<xsl:text>&pr_primarySic;</xsl:text>
				</xsl:when>
				<xsl:when test="self::sec.sic">
					<xsl:text>&pr_secondarySic;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sic">
		<div>
			<xsl:apply-templates select="c.nbr/base"/>
			<xsl:if test="c.nbr/ext">
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:if>
			<xsl:apply-templates select="c.nbr/ext"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:apply-templates select="c.desc"/>
		</div>
	</xsl:template>

	<!-- ********************************************************************** 
	*************************  (F) Sales Information (right column)  **********
	************************************************************************-->
	<xsl:template match ="sales.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_salesInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<!--<xsl:apply-templates select="sales"/>-->
			<xsl:apply-templates select="sales/rev.d.b/rev.d"/>
			<xsl:apply-templates select="descendant::latest.sales"/>
			<xsl:apply-templates select="descendant::trend.sales"/>
			<xsl:apply-templates select="descendant::base.sales"/>
			<xsl:apply-templates select="sales/sales.growth.b"/>
			<xsl:apply-templates select="ter.b"/>
			<xsl:apply-templates select="../finance/net.worth.b"/>
			<xsl:apply-templates select="acct.b/accts.b/accts"/>
		</table>
	</xsl:template>

	<xsl:template match ="rev.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_annualSalesRevisionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="latest.sales">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_annualSalesUS;'"/>
			<xsl:with-param name="selectNodes" select="following-sibling::sales.rely | preceding-sibling::sales.rely"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="sales.rely">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match ="trend.sales">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_oneYrAgo;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="base.sales">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_threeYrAgo;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Sales Growth -->
	<xsl:template match ="sales.growth.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_salesGrowth;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sales.growth">
		<xsl:apply-templates/>
		<xsl:text>%</xsl:text>
	</xsl:template>

	<!--Sales Territory -->
	<xsl:template match ="ter.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_salesTerritory;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Net Worth -->
	<xsl:template match="net.worth.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Number of Accts-->
	<xsl:template match ="accts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfAccounts;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*************************  (G) Employee Information (right column)  *******
	************************************************************************-->

	<!--******** (6)Employee Information **********-->
	<xsl:template match="emp.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_employeeInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="descendant::latest.cnt"/>
			<xsl:apply-templates select="descendant::trend.cnt"/>
			<xsl:apply-templates select="descendant::base.cnt"/>
			<xsl:apply-templates select="here.b"/>
			<xsl:apply-templates select="emp.here.rely"/>
			<xsl:apply-templates select="growth.b"/>
		</table>
	</xsl:template>

	<!-- Employees Total-->
	<xsl:template match ="latest.cnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_employeesTotal;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Employees Here-->
	<xsl:template match ="here.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<xsl:template match ="emp.here.rely">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Employment Growth-->
	<xsl:template match ="growth.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_employmentGrowth;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="growth">
		<xsl:apply-templates/>
		<xsl:text>%</xsl:text>
	</xsl:template>

	<xsl:template match ="trend.cnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_oneYrAgo;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="base.cnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_threeYrAgo;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	***********(H) Company History/Operations/Relationships (right column)  ***
	************************************************************************-->
	<xsl:template match ="relation.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_companyHistory;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--This Company's Specifics -->
	<xsl:template match ="this.b">
		<tr>
			<th>
				<xsl:text>&pr_thisCompSpecifics;</xsl:text>
			</th>
		</tr>
		<xsl:apply-templates select ="duns.nbr.b/duns.b"/>
		<xsl:apply-templates select ="loc.in.b/msa.cd.b"/>
		<xsl:apply-templates select ="loc.in.b/msa.name.b"/>
		<xsl:apply-templates select ="build"/>
		<xsl:apply-templates select ="../../sales.b/acct.b/acct.firm.b"/>
		<xsl:apply-templates select ="bus.is.b"/>
		<xsl:apply-templates select ="this.is.b"/>
		<xsl:apply-templates select ="duns.nbr.b/corp.fam.duns.b/par.b"/>		
		<xsl:apply-templates select ="duns.nbr.b/corp.fam.duns.b/ult.b"/>
		<xsl:apply-templates select ="duns.nbr.b/corp.fam.duns.b/hqtr.b"/>
		<xsl:apply-templates select ="hotlist"/>
	</xsl:template>

	<!-- MSA Code -->
	<xsl:template match ="msa.cd.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- MSA Name -->
	<xsl:template match ="msa.name.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Square Footage -->
	<xsl:template match ="footage.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

  <xsl:template match="acct.firm.b">
    <xsl:call-template name="wrapPublicRecordsItem"/>
  </xsl:template>
  
	<xsl:template match="acct.firm">
    <xsl:call-template name="FormatCompany">
      <xsl:with-param name="companyName" select="."/>
    </xsl:call-template>
	</xsl:template>

	<!-- Occupancy Type -->
	<xsl:template match ="own.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Business Is A -->
	<xsl:template match ="bus.is.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Part of "Business Is A" -->
	<xsl:template match ="estab|sm.bus|home|structure|manuf|imp.exp">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Establishment Is -->
	<xsl:template match ="this.is.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<xsl:template match="minor|foreign|public">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--Ultimate Company Name -->
	<xsl:template match ="ult.name">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ultimateCompanyName;'"/>
      <xsl:with-param name="nodeType" select="$COMPANY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Headquarters Company Name-->
	<xsl:template match ="hqtr.name">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_headquartersCompanyName;'"/>
      <xsl:with-param name="nodeType" select="$COMPANY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="par.name">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_parentCompanyName;'"/>
      <xsl:with-param name="nodeType" select="$COMPANY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Ultimate DUNS Number and Headquarters DUNS Number-->
	<xsl:template match ="duns">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test ="parent::ult.b">
					<xsl:text>Ultimate DUNS Number:</xsl:text>
				</xsl:when>
				<xsl:when test ="parent::hqtr.b">
					<xsl:text>Headquarters DUNS Number:</xsl:text>
				</xsl:when>
				<xsl:when test ="parent::par.b">
					<xsl:text>Parent Company DUNS Number:</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>DUNS:</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Latest Update to Record-->
	<xsl:template match ="rpt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_latestUpdateToRecord;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="hotlist">
		<tr>
			<th>
				<xsl:value-of select="l"/>
			</th>
			<td>
				<xsl:if test="new">
					<div>
						<xsl:choose>
							<xsl:when test="contains(new, '1')">
								<xsl:text>NEW TO THE WORLD</xsl:text>
							</xsl:when>
							<xsl:when test="contains(new, '2')">
								<xsl:text>NEW LEGAL ENTITY</xsl:text>
							</xsl:when>
							<xsl:when test="contains(new, '3')">
								<xsl:text>NEW TO D&amp;B</xsl:text>
							</xsl:when>
						</xsl:choose>
						<xsl:if test="new.d/@iso.d">
							<xsl:text><![CDATA[ ]]>(</xsl:text>
							<xsl:apply-templates select="new.d/@iso.d"/>
							<xsl:text>)</xsl:text>
						</xsl:if>
					</div>
				</xsl:if>

				<xsl:if test="owner.change">
					<div>
						<xsl:text>OWNERSHIP CHANGE</xsl:text>
						<xsl:if test="owner.change.d/@iso.d">
							<xsl:text><![CDATA[ ]]>(</xsl:text>
							<xsl:apply-templates select="owner.change.d/@iso.d"/>
							<xsl:text>)</xsl:text>
						</xsl:if>
					</div>
				</xsl:if>

				<xsl:if test="ceo.change">
					<div>
						<xsl:text>CEO CHANGE</xsl:text>
						<xsl:if test="ceo.change.d/@iso.d">
							<xsl:text><![CDATA[ ]]>(</xsl:text>
							<xsl:apply-templates select="ceo.change.d/@iso.d"/>
							<xsl:text>)</xsl:text>
						</xsl:if>
					</div>
				</xsl:if>

				<xsl:if test="co.name.change">
					<div>
						<xsl:text>COMPANY NAME CHANGE</xsl:text>
						<xsl:if test="co.name.change.d/@iso.d">
							<xsl:text><![CDATA[ ]]>(</xsl:text>
							<xsl:apply-templates select="co.name.change.d/@iso.d"/>
							<xsl:text>)</xsl:text>
						</xsl:if>
					</div>
				</xsl:if>

				<xsl:if test="addr.change">
					<div>
						<xsl:text>ADDRESS CHANGE</xsl:text>
						<xsl:if test="addr.change.d/@iso.d">
							<xsl:text><![CDATA[ ]]>(</xsl:text>
							<xsl:apply-templates select="addr.change.d/@iso.d"/>
							<xsl:text>)</xsl:text>
						</xsl:if>
					</div>
				</xsl:if>

				<xsl:if test="phone.change">
					<div>
						<xsl:text>TELEPHONE CHANGE</xsl:text>
						<xsl:if test="phone.change.d/@iso.d">
							<xsl:text><![CDATA[ ]]>(</xsl:text>
							<xsl:apply-templates select="phone.change.d/@iso.d"/>
							<xsl:text>)</xsl:text>
						</xsl:if>
					</div>
				</xsl:if>

			</td>
		</tr>
	</xsl:template>

	<xsl:template match="new.d/@iso.d | owner.change.d/@iso.d | ceo.change.d/@iso.d | co.name.change.d/@iso.d | addr.change.d/@iso.d | phone.change.d/@iso.d">
		<xsl:call-template name="FormatDate"/>
	</xsl:template>

	<xsl:template name="dbLogo">
		<table width="98%">
			<tr>
				<td align="left"></td>
				<td align="right">
					<img src="{$Images}&pr_dAndBLogoPath;" alt="&pr_dAndBLogoAltText;"></img>
				</td>
			</tr>
		</table>
	</xsl:template>

</xsl:stylesheet>