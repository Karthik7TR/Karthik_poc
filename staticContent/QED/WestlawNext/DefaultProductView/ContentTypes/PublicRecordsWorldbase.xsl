<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
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
	<xsl:template match="map|p|pc|pre|col.key|link|reg.addr|st.prov|rpts|sic.cd.b|figure.d|r.link|cntry.cd|hrchy|dias|fam.tree.b|co.nm/l"/>

	<!-- Database Signon: WORLDBASE -->
	<!--
			This content is comprised of 9 major sections:
			(A) Coverage Information
			(B) Company Information	
			(C) Executive(s) Information
			(D) Business Description
			(E) Financial Information
			(F) Sales Information
			(G) Employee Information
			(H) Company History/Operations/Relationships & Other Information
			(I) Miscellaneous
		-->
	
	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsWorldbaseClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<table width="98%">
			<tr>
				<td align="left"></td>
				<td align="right">
					<img src="{$Images}&pr_dAndBLogoPath;" alt="&pr_dAndBLogoAltText;"></img>
				</td>
			</tr>
		</table>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_dAndBWorldBaseRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!--(A) Coverage Information-->
		<xsl:apply-templates select="$coverage-block"/>
		<!--(B) Company Information-->
		<xsl:apply-templates select="co.info.b"/>
		<!--(C) Executive(s) Information-->
		<xsl:apply-templates select="execs.b"/>
		<!--(D) Business Description-->
		<xsl:apply-templates select="bus.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<!--(E) Financial Information-->
		<xsl:apply-templates select="finance"/>
		<!--(F) Sales Information-->
		<xsl:apply-templates select="sales.b"/>
		<!--(G) Employee Information-->
		<xsl:apply-templates select="emp.info"/>
		<!--(H) Company History/Operations/Relationships & Other Information-->
		<xsl:apply-templates select="relation.b"/>
		<!--(I) Miscellaneous-->
	</xsl:template>

	<!-- 
	********************************************************************** 
	******************* (B) COMPANY INFORMATION **************************
	**********************************************************************	
	-->
	<xsl:template match="co.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_companyInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="duns.b/duns"/>
			<xsl:apply-templates select="co.nm.b"/>
			<xsl:apply-templates select="pri.addr.b"/>
			<xsl:apply-templates select="mail.addr.b"/>
			<xsl:apply-templates select="phone.b/phone"/>
			<xsl:apply-templates select="phone.b/fax.b"/>
			<xsl:apply-templates select="incorp.b"/>
		</table>
	</xsl:template>

	<!-- DUNS -->
	<xsl:template match="duns.b/duns">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_duns;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
		<!--<xsl:apply-templates/>-->
	</xsl:template>

	<xsl:template match="co.nm/pri">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Related Name(s) -->
	<xsl:template match="sec.nm">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_relatedNames;'"/>
			<xsl:with-param name="companyName" select="sec"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address -->
	<xsl:template match="pri.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="l"/>
			<xsl:with-param name="street" select="pri.addr/str"/>
			<xsl:with-param name="city" select="pri.addr/cty.b/cty"/>
			<xsl:with-param name="stateOrProvince" select="pri.addr/st.prov.b/st.prov.ab"/>
			<xsl:with-param name="zip" select="pri.addr/zip"/>
			<xsl:with-param name="country" select="pri.addr/cntry.b/cntry"/>
		</xsl:call-template>		
		<xsl:apply-templates select="pri.addr/cnty.b/cnty"/>
		<xsl:apply-templates select="pri.addr/cont"/>
	</xsl:template>

	<!-- County -->
	<xsl:template match="pri.addr/cnty.b/cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Continent -->
	<xsl:template match="cont">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_continent;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Mailing Address -->
	<xsl:template match="mail.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="l"/>
			<xsl:with-param name="street" select="mail.addr/str"/>
			<xsl:with-param name="streetLineTwo" select="mail.addr/str2"/>
			<xsl:with-param name="city" select="mail.addrcty.b/cty"/>
			<xsl:with-param name="stateOrProvince" select="mail.addr/st.prov.b/st.prov.ab"/>
			<xsl:with-param name="zip" select="mail.addr/zip"/>
			<xsl:with-param name="country" select="mail.addr/cntry.b/cntry"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone Block -->
	<xsl:template match="phone.b/phone">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="selectNodes" select="phone.b/phone"/>
			<xsl:with-param name="defaultLabel" select="'&pr_telephone;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Access Code -->
	<xsl:template match="access">
		<xsl:text>(</xsl:text>
		<xsl:apply-templates/>
		<xsl:text>)</xsl:text>
	</xsl:template>

	<!-- Number -->
	<xsl:template match="nbr">
		<xsl:if test="preceding-sibling::access">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Fax -->
	<xsl:template match="phone.b/fax.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Year of Start -->
	<xsl:template match="yr.srtd.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Operating Status -->
	<xsl:template match="op.stat.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- 
	********************************************************************** 
	*********************  (C) EXECUTIVE(S) INFORMATION ******************
	**********************************************************************	
	-->
	<xsl:template match="execs.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_executivesInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="execs/ceo.b/ceo"/>
			<xsl:apply-templates select="execs/exec"/>
		</table>
	</xsl:template>

	<xsl:template match ="ceo/name">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ceoName;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="ceo/ti">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ceoTitle;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ExecutiveInfo -->
	<xsl:template match="exec">
		<xsl:apply-templates select="name.b"/>
		<xsl:apply-templates select="ti.b/ti"/>
	</xsl:template>

	<!-- Executive Name(s) -->
	<xsl:template match="name.b">
		<xsl:variable name="countExecs" select="count(preceding::exec)+1"/>
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_executiveName;'"/>
			<xsl:with-param name="indexNumber" select="$countExecs"/>
			<xsl:with-param name="selectNodes" select="name"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Name -->
	<xsl:template match="name">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Executive Title -->
	<xsl:template match="ti.b/ti">
		<xsl:call-template name="wrapPublicRecordsIndexedItem">
			<xsl:with-param name="defaultLabel" select="'&pr_executiveTitle;'"/>
		</xsl:call-template>
	</xsl:template>
	<!-- 
	********************************************************************** 
	*********************  (D) BUSINESS DESCRIPTION***********************
	**********************************************************************	
	-->
	<xsl:template match="bus.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_businessDescription;'" />
		</xsl:call-template>
		<table class="&pr_table;">			
			<xsl:apply-templates select="line.bus.b"/>
			<xsl:apply-templates select="sic.cds/pri.sic.b"/>
			<xsl:apply-templates select="sic.cds/sec.sics.b"/>
			<xsl:apply-templates select="preceding-sibling::co.info.b/nat.id.b"/>
		</table>
	</xsl:template>

	<!-- Line of Business -->
	<xsl:template match="line.bus.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!--Primary SIC | Secondary SIC(s)-->	
	<xsl:template match="sic.cds/pri.sic.b | sic.cds/sec.sics.b ">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>
	
	<xsl:template match="sic">
		<div>
			<xsl:apply-templates select="c.nbr | c.nbr"/>
			<xsl:text><![CDATA[ ]]><![CDATA[ ]]><![CDATA[ ]]><![CDATA[ ]]></xsl:text>
			<xsl:apply-templates select="c.desc| c.desc"/>
		</div>
	</xsl:template>

	<!-- National ID Block -->
	<xsl:template match="nat.id.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!--  National ID -->
	<xsl:template match="id.sys">
		<xsl:text><![CDATA[ ]]><![CDATA[ ]]><![CDATA[ ]]><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>


	<!-- 
	********************************************************************** 
	********************* (E) FINANCIAL INFORMATION **********************
	**********************************************************************	
	-->
	<!-- Financial Information -->
	<xsl:template match="finance">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_financialInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="net.worth"/>
			<xsl:apply-templates select="profit"/>
			<xsl:apply-templates select="cur.t.b"/> <!-- currency  -->
			<xsl:apply-templates select="cur.rate.b"/>	<!-- Exchange rate  -->
			<xsl:apply-templates select="conv.d.b"/>	<!-- coversion date  -->
		</table>
	</xsl:template>

	<!-- Net Worth(US) | Profit(US)|Annual Sales (US) -->
	<xsl:template match="us.dol.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Net Worth(Local) | Profit(Local) | Annual Sales (Local) -->
	<xsl:template match="loc.cur.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Currency -->
	<xsl:template match="cur.t.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Currency Exchange Rate -->
	<xsl:template match="cur.rate.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Currency Conversion date -->
	<xsl:template match="conv.d.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>


	<!-- 
	********************************************************************** 
	********************* (F) SALES INFORMATION ***********************
	**********************************************************************	
	-->
	<xsl:template match="sales.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_salesInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- 
	********************************************************************** 
	********************* (G) EMPLOYEE INFORMATION ***********************
	**********************************************************************	
	-->
	<xsl:template match="emp.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_employeeInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Total Employees |Employees Here -->
	<xsl:template match="emp.tot.b|emp.here.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<xsl:template match="prin.incld">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	<!-- 
	********************************************************************** 
	*** (H) COMPANY HISTORY/OPERATIONS/RELATIONSHIP & OTHER INFORMATION **
	**********************************************************************	
	-->
	<xsl:template match="relation.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_companyHistory;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="this.b"/>
			<xsl:apply-templates select="following-sibling::linkage/hqtr.par/par.duns" />
			<xsl:apply-templates select="following-sibling::linkage/hqtr.par/name" />
			<xsl:apply-templates select="following-sibling::linkage/hqtr.par/addr" />
			<xsl:apply-templates select="following-sibling::linkage/dom.ult/duns" />
			<xsl:apply-templates select="following-sibling::linkage/dom.ult/name" />
			<xsl:apply-templates select="following-sibling::linkage/dom.ult/addr" />
			<xsl:apply-templates select="following-sibling::linkage/glob.ult/duns" />
			<xsl:apply-templates select="following-sibling::linkage/glob.ult/name" />
			<xsl:apply-templates select="following-sibling::linkage/glob.ult/addr" />
			<xsl:apply-templates select="following-sibling::linkage/glob.ult/nbr.fam.mems" />
			<tr>
				<td align="left"></td>
				<td align="right">
					<img src="{$Images}&pr_dAndBLogoPath;" alt="&pr_dAndBLogoAltText;"></img>
				</td>
			</tr>
		</table>

	</xsl:template>

	<!-- Company's Specifics-->
	<xsl:template match="this.b">
		<tr>
			<th colspan="2">
				<!--<br />-->
				<xsl:apply-templates select="l"/>
			</th>
		</tr>
		<xsl:apply-templates select="duns.nbr.b/duns.b/duns"/>
		<xsl:apply-templates select="duns.nbr.b/pre.duns.b/pre.duns"/>
		<xsl:apply-templates select="loc.in.b/cnty.b"/>
		<xsl:apply-templates select="loc.in.b/regn.b"/>
		<xsl:apply-templates select="leg.stat.b"/>
		<xsl:apply-templates select="own.b"/>
		<xsl:apply-templates select="bus.is.b"/>
	</xsl:template>

	<xsl:template match="duns.nbr.b/duns.b/duns">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_duns;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Previous DUNS Number  -->
	<xsl:template match="duns.nbr.b/pre.duns.b/pre.duns">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousDuns;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Location -->
	<xsl:template match="loc.in.b/cnty.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<xsl:template match="loc.in.b/regn.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Legal Status Info -->
	<xsl:template match="leg.stat.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Ownership Info -->
	<xsl:template match="own.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Business Info -->
	<xsl:template match="bus.is.b">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<xsl:template match="bus.is.b/est">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="bus.is.b/subsid">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="bus.is.b/imp.exp">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="bus.is.b/glob.loc">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Parent Company DUNS -->
	<xsl:template match="hqtr.par/par.duns | dom.ult/duns | glob.ult/duns">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test ="ancestor::hqtr.par">
					<xsl:text>&pr_parentDUNS;</xsl:text>
				</xsl:when>
				<xsl:when test="ancestor::dom.ult">
					<xsl:text>&pr_ultimateCompanyDuns;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_headquartersDunsNumber;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Name -->
	<xsl:template match="hqtr.par/name | dom.ult/name | glob.ult/name">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test ="ancestor::hqtr.par">
					<xsl:text>&pr_parentCompanyName;</xsl:text>
				</xsl:when>
				<xsl:when test="ancestor::dom.ult">
					<xsl:text>&pr_ultimateCompanyName;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_headquartersCompanyName;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Company Address -->
	<xsl:template match="hqtr.par/addr | dom.ult/addr | glob.ult/addr">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test ="ancestor::hqtr.par">
					<xsl:text>&pr_parentCompanyAddress;</xsl:text>
				</xsl:when>
				<xsl:when test="ancestor::dom.ult">
					<xsl:text>&pr_ultimateCompanyAddress;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_headquartersCompanyAddress;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="street" select="str/ln1"/>
			<xsl:with-param name="streetLineTwo" select="str/ln2"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st.prov.ab"/>
			<xsl:with-param name="zip" select="zip"/>
			<xsl:with-param name="country" select="cntry"/>
		</xsl:call-template>
		<xsl:apply-templates select="cont"/>
	</xsl:template>

	<!-- Number of Family Members -->
	<xsl:template match="nbr.fam.mems">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalNumberOfFamilyMembers;'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>