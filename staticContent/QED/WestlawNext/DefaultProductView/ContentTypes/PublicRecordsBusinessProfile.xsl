<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>

	<!--Combined Equifax Records - 
		BUSINESS-PRO	
	-->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!--
		Desired output view:
			content    - renders document (default)
			citelistitem - transform the citelist item description into string
			GatherImageMetadata - used by PrintController to determine if  the document has a corresponding 
			PDF image.  This content will never have a PDF image, so do nothing for this VIEW.
	-->

	<!-- Do not render these nodes -->
	<xsl:template match="prim.cnty.cd | leg.immed.no | leg.ult.no | aff.immed.no | aff.ult.no |aff.glob.ult.ind | aff.par.ind | aff.link.ind | leg.ult.ind | leg.par.ind
																		| leg.link.ult.ind | leg.entity.ind | efx.id | new.efx.id | efx.delta"/>

	<!-- Transform the citelist item description into string 
	<xsl:template name="citelistitem">
		<xsl:apply-templates mode="metadata"/>
	</xsl:template>-->

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsBusinessProfileClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_businessProfileRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<div class="&pr_leftSection;">
			<!-- Display coverage information -->
			<xsl:if test="$coverage-block">
				<xsl:apply-templates select="$coverage-block">
					<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
					<xsl:with-param name="displaySource" select="false()"/>
				</xsl:apply-templates>
				<!--<xsl:apply-templates select="rec.updt.b"/>-->
			</xsl:if>
			<xsl:apply-templates select="bus.info.b"/>
			<xsl:apply-templates select="exec.info.b"/>
			<xsl:apply-templates select="other.exec.info.b"/>
		</div>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<div class="&pr_rightSection;">
			<xsl:apply-templates select="bus.descr.b"/>
		</div>
	</xsl:template>

	<xsl:template match="rec.updt.b">
		<xsl:apply-templates select="rec.updt.d"/>
		<xsl:apply-templates select="rec.not.updt.d"/>
	</xsl:template>

	<!--Record Updated Date-->
	<xsl:template match="rec.updt.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_recordLastUpdated;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Record No Longer Updated as of-->
	<xsl:template match="rec.not.updt.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_recordNoLongerUpdatedAsOf;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******(A)"Coverage" Business Information section  ************************
	************************************************************************-->

	<xsl:template match ="bus.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_businessInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="bus.name"/>
			<xsl:apply-templates select="leg.bus.name"/>
			<xsl:apply-templates select="prim.addr.b"/>
			<xsl:apply-templates select="prim.cnty.nm"/>
			<xsl:apply-templates select="prim.cntry.nm"/>
			<xsl:apply-templates select="secd.addr.b"/>
			<xsl:apply-templates select="secd.cntry.nm"/>
			<xsl:apply-templates select="bus.phn"/>
			<xsl:apply-templates select="bus.fax"/>
			<xsl:apply-templates select="bus.email.b"/>
			<xsl:apply-templates select="bus.url"/>
			<xsl:apply-templates select="yr.est"/>
			<xsl:apply-templates select="no.empl.b"/>
			<xsl:apply-templates select="sales.b"/>
		</table>
	</xsl:template>

	<!-- Business name information-->
	<xsl:template match="bus.name">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_businessName;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Legal Business name information-->
	<xsl:template match="leg.bus.name">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_legalBusinessName;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Primary address information-->
	<xsl:template match="prim.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_primaryAddress;'"/>
			<xsl:with-param name="street" select="prim.str"/>
			<xsl:with-param name="city" select="prim.cty"/>
			<xsl:with-param name="stateOrProvince" select="prim.st"/>
			<xsl:with-param name="zip" select="prim.zip.b/prim.zip"/>
			<xsl:with-param name="zipExt" select="prim.zip.b/prim.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- County information-->
	<xsl:template match="prim.cnty.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Primary Country information-->
	<xsl:template match="prim.cntry.nm  | secd.cntry.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_country;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Secondary address information-->
	<xsl:template match="secd.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_secondaryAddress;'"/>
			<xsl:with-param name="street" select="secd.str"/>
			<xsl:with-param name="city" select="secd.cty"/>
			<xsl:with-param name="stateOrProvince" select="secd.st"/>
			<xsl:with-param name="zip" select="secd.zip.b/secd.zip"/>
			<xsl:with-param name="zipExt" select="secd.zip.b/secd.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone information-->
	<xsl:template match="bus.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Fax information-->
	<xsl:template match="bus.fax">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessFax;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Email information-->
	<xsl:template match="bus.email.b">
		<xsl:call-template name="wrapPublicRecordsEmail">
			<xsl:with-param name="email" select="bus.email"/>
			<xsl:with-param name="user" select="user.nm"/>
			<xsl:with-param name="domain" select="domain.nm"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Web Address information-->
	<xsl:template match="bus.url">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_webAddress;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Year Established information-->
	<xsl:template match="yr.est">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_yearEstablished;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Employee Location information-->
	<xsl:template match="no.empl.b">
		<tr>
			<th>
				<xsl:text>&pr_employeesAtLocationYear;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="no.empl.loc"/>
				<xsl:if test="normalize-space(no.empl.yr)">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:if test="not(contains(., '('))">
						<xsl:text>(</xsl:text>
					</xsl:if>
					<xsl:apply-templates select="no.empl.yr"/>
					<xsl:if test="not(contains(., ')'))">
						<xsl:text>)</xsl:text>
					</xsl:if>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!-- Sales from Location information-->
	<xsl:template match="sales.b">
		<tr>
			<th>
				<xsl:text>&pr_salesFromLocationYear;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="sales.loc"/>
				<xsl:if test="normalize-space(sales.yr)">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:if test="not(contains(., '('))">
						<xsl:text>(</xsl:text>
					</xsl:if>
					<xsl:apply-templates select="sales.yr"/>
					<xsl:if test="not(contains(., ')'))">
						<xsl:text>)</xsl:text>
					</xsl:if>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!-- ********************************************************************** 
	*******Executive Information section  *************************************
	************************************************************************-->

	<xsl:template match="exec.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_executiveInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="exec.name"/>
			<xsl:apply-templates select="title"/>
		</table>
	</xsl:template>

	<!-- Executive Name information-->
	<xsl:template match="exec.name">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_businessExecutiveContact;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

<!-- Executive Title information-->
	<xsl:template match="title">
		<xsl:variable name="label">
			<xsl:text>&pr_executiveContactsTitle;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- ********************************************************************** 
	*******Additional Executive Information section  *************************************
	************************************************************************-->

	<xsl:template match="other.exec.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_additionalExecutiveInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="ceo/name"/>
			<xsl:apply-templates select="ceo/title"/>
			<xsl:apply-templates select="cio/name"/>
			<xsl:apply-templates select="cio/title"/>
			<xsl:apply-templates select="cfo/name"/>
			<xsl:apply-templates select="cfo/title"/>
		</table>
	</xsl:template>
	
		<!-- Additional Executive Name information-->
	<xsl:template match="name">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- CEO Title information-->
	<xsl:template match="ceo/title">
		<xsl:variable name="label">
			<xsl:text>&pr_title;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- CIO Title information-->
	<xsl:template match="cio/title">
				<xsl:variable name="label">
					<xsl:text>&pr_title;</xsl:text>
					</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- CFO Title information-->
	<xsl:template match="cfo/title">
				<xsl:variable name="label">
					<xsl:text>&pr_title;</xsl:text>
					</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******Business Description Information section  **************************
	************************************************************************-->

	<xsl:template match="bus.descr.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_businessDescription;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="sic.info.b/prim.sic.b"/>
			<xsl:apply-templates select="sic.info.b/sic.b"/>
			<xsl:apply-templates select="naics.info.b/prim.naics.b"/>
			<xsl:apply-templates select="naics.info.b/naics.b"/>
			<xsl:apply-templates select="bus.leg.stat"/>
			<xsl:apply-templates select="tax.id.type"/>
			<xsl:apply-templates select="tax.id.num"/>
			<xsl:apply-templates select="leg.immed.par"/>
			<xsl:apply-templates select="leg.ult.par"/>
			<xsl:apply-templates select="aff.immed.par"/>
			<xsl:apply-templates select="aff.ult.par"/>
			<xsl:apply-templates select="sec.id.type"/>
			<xsl:apply-templates select="sec.id.num"/>
			<xsl:apply-templates select="gov.entity"/>
			<xsl:apply-templates select="non-prof.entity"/>
			<xsl:apply-templates select="stk.exch"/>
			<xsl:apply-templates select="ticker"/>
			<xsl:apply-templates select="out.of.bus"/>
			<xsl:apply-templates select="uk.house.num"/>
			<xsl:apply-templates select="corp.filing.num"/>
		</table>
	</xsl:template>

	<!--Primary SIC Code-->
	<xsl:template match="prim.sic.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_primarySicCode;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sic.b">
		<tr>
			<th>
				<xsl:text>&pr_secondarySicCode;<![CDATA[ ]]></xsl:text>
				<xsl:value-of select="position()"/>
				<xsl:text>:</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match ="prim.naics.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_primaryNaicsCode;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="naics.info.b/naics.b">
		<tr>
			<th>
				<xsl:text>&pr_secondaryNaicsCode;<![CDATA[ ]]></xsl:text>
				<xsl:value-of select="position() + 1"/>
				<xsl:text>:</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="prim.sic.cd | sic.cd | prim.naics.cd | naics.cd">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<!--Business Legal Status-->
	<xsl:template match="bus.leg.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessLegalStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Tax ID Type-->
	<xsl:template match="tax.id.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxIdType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Tax ID Number-->
	<xsl:template match="tax.id.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxIdNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Legal Immediate Parent-->
	<xsl:template match="leg.immed.par">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_legalImmediateParent;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Legal Ultimate Parent-->
	<xsl:template match="leg.ult.par">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_legalUltimateParent;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Affiliate Immediate Parent-->
	<xsl:template match="aff.immed.par">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_affiliateImmediateParent;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Affiliate Ultimate Parent-->
	<xsl:template match="aff.ult.par">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_affiliateUltimateParent;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Security ID Typle-->
	<xsl:template match="sec.id.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_securityIdType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Security ID Number-->
	<xsl:template match="sec.id.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_securityIdNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Government Entity-->
	<xsl:template match="gov.entity">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_governmentEntity;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Non-Profit Entity-->
	<xsl:template match="non-prof.entity">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nonProfitEntity;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Primary Stock Exchange Code-->
	<xsl:template match="stk.exch">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_primaryStockExchangeCode;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Stock Ticker Symbol-->
	<xsl:template match="ticker">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stockTickerSymbol;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Suspected to be Out of Business-->
	<xsl:template match="out.of.bus">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_suspectedToBeOutOfBusiness;'"/>
		</xsl:call-template>
	</xsl:template>

<!--UK Companies House ID-->
	<xsl:template match="uk.house.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ukCompaniesHouseId;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<!--US Corporate Filings Number-->
	<xsl:template match="corp.filing.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_usCorporateFilingsNumber;'"/>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
