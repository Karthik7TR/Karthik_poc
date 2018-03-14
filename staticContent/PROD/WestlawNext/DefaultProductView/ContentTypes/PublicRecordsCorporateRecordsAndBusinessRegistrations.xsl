<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Variables -->
	<xsl:variable name ="stateInfo" select ="/Document/n-docbody/r/pre/upd.freq/@st"/>
	<xsl:variable name ="dateInfo" select ="/Document/n-docbody/r/pre/upd.d.b/upd.d"/>
	<xsl:variable name ="sbStateInfo" select ="/Document/n-docbody/r/pre/s.b/s"/>
	<xsl:variable name ="sbFilgStatInfo" select ="/Document/n-docbody/r/filg.info/filg/stat.d.b/stat.d"/>

	<!-- don't render these elements-->
	<xsl:template match="map|p|pc|ld.d|upd.freq|upd.d|dis.d|s.b|coll.name|col.key|prism-clipdate|l|name.t|filg.duns.b"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<!-- Render the XML based on the desired VIEW. -->
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsCorporateRecordsAndBusinessRegistrationsClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_corporateRecordsAndBusinessRegistrations;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!--(A)Coverage section -->
		<xsl:apply-templates select="$coverage-block"/>
		<!--(B)Miscellaneous Information-->
		<xsl:call-template name="MiscInfo"/>
		<!--(C) Information Blocks which consists of ten subsection - (C)-1 to (C)-2 -->
		<xsl:apply-templates select="co.info.b"/>
		<xsl:apply-templates select="filg.info"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<!--(C) Information Blocks which consists of ten subsection - (C)-3 to (C)-10 -->
		<xsl:apply-templates select="reg.agt.info"/>
		<xsl:apply-templates select="name.info"/>
		<xsl:apply-templates select="assoc.ent.info"/>
		<xsl:apply-templates select="prin.info"/>
		<xsl:apply-templates select="amd.info"/>
		<xsl:apply-templates select="stk.info"/>
		<xsl:apply-templates select="tax.info"/>
		<xsl:apply-templates select="misc.info.b"/>
		<!--(D)Document Footer-->
		<xsl:call-template name="DocFooter"/>
	</xsl:template>

	<!-- ************(B) Miscellaneous Information ***************** -->
	<xsl:template name ="MiscInfo">
		<xsl:choose>
			<xsl:when test ="$stateInfo='NJ' or $stateInfo='PR' or $stateInfo='VI'">
				<div class="&pr_item;">
					<xsl:text>THE CORPORATE DETAILS PROVIDED BELOW MAY HAVE BEEN SUBMITTED BY THE MANAGEMENT OF
					THE SUBJECT BUSINESS AND MAY NOT HAVE BEEN VERIFIED WITH THE GOVERNMENT AGENCY
					WHICH RECORDS SUCH DATA.</xsl:text>
				</div>
			</xsl:when>
			<xsl:when test ="$stateInfo='NY'">
				<div class="&pr_item;">
					<xsl:text>THE FOLLOWING DATA IS NOT AN OFFICIAL RECORD OF THE DEPARTMENT OF STATE OR
					THE STATE OF NEW YORK AND WEST, A THOMSON BUSINESS IS NOT AN EMPLOYEE OR
					AGENT THEREOF.  ALL WARRANTIES, EXPRESS OR IMPLIED, REGARDING THE
					INFORMATION PROVIDED HEREIN, ARE DISCLAIMED BY THE DEPARTMENT OF STATE.</xsl:text>
				</div>
			</xsl:when>
			<xsl:when test ="$stateInfo='CA'">
				<div class="&pr_item;">
					<xsl:text>THIS DATA IS FOR INFORMATION PURPOSES ONLY.  CERTIFICATION CAN ONLY BE
					OBTAINED THROUGH THE SACRAMENTO OFFICE OF THE CALIFORNIA SECRETARY OF STATE.</xsl:text>
				</div>
			</xsl:when>
			<xsl:when test ="$stateInfo='MI'">
				<div class="&pr_item;">
					<xsl:text>THIS DATA IS FOR INFORMATION PURPOSES ONLY.  CERTIFICATION CAN ONLY BE OBTAINED
					THROUGH THE MICHIGAN DEPARTMENT OF ENERGY, LABOR AND ECONOMIC GROWTH, CORPORATE DIVISION.</xsl:text>
				</div>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- ************End of Miscellaneous  Information ***************** -->

	<!--**************(C)-1 Company Information ****************** -->
	<xsl:template match="co.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_companyInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Company Name-->
	<xsl:template match="co.name">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="$sbStateInfo='NY SOS'">
					<xsl:text>&pr_companyName;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_name;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Care of Name-->
	<xsl:template match="careof.name">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="$sbStateInfo='NY SOS'">
					<xsl:text>&pr_processName;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_careOfName;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Company Address -->
	<xsl:template match="filg.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="streetNum" select="filg.str"/>
			<xsl:with-param name="street" select="filg.str2"/>
			<xsl:with-param name="city" select="filg.cty"/>
			<xsl:with-param name="stateOrProvince" select="filg.st"/>
			<xsl:with-param name="zip" select="filg.zip|filg.zip.b/filg.zip"/>
			<xsl:with-param name="zipExt" select="filg.zip.b/filg.zip.ext"/>
			<xsl:with-param name="country" select="filg.cntry"/>
		</xsl:call-template>
	</xsl:template>

	<!-- County -->
	<xsl:template match="cnty.prin.off">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- D & B DUNS -->
	<xsl:template match="bus.duns[not(following-sibling::bus.duns.INF or preceding-sibling::bus.duns.INF)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dAndBDuns;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bus.duns"/>

	<xsl:template match="bus.duns.INF">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dAndBDuns;'"/>
			<xsl:with-param name="selectNodes" select="cite.query"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!--***************End of Company Information************* -->

	<!--************(C)-2 Filing Information ******************** -->
	<xsl:template match="filg.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_filingInfo;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- To make the display order according to the Spec.-->
	<xsl:template match="filg">
		<xsl:apply-templates select ="id.nbr.b"/>
		<xsl:apply-templates select ="filg.d.b"/>
		<xsl:apply-templates select ="filg.act.b"/>
		<xsl:apply-templates select ="st.inc.b"/>
		<xsl:apply-templates select ="frgn.inc.b"/>
		<xsl:apply-templates select ="cntry.inc.b"/>
		<xsl:apply-templates select ="inc.d.b"/>
		<xsl:apply-templates select ="exp.d.b"/>
		<xsl:apply-templates select ="perp.b"/>
		<xsl:apply-templates select ="stat.cd.b"/>
		<xsl:apply-templates select ="stat.d.b"/>
		<xsl:apply-templates select ="filg.corp.b"/>
		<xsl:apply-templates select ="filg.reg.b"/>
		<xsl:apply-templates select ="corp.class.b"/>
		<xsl:apply-templates select ="filg.addr.t.b"/>
		<xsl:apply-templates select ="fein.b"/>
		<xsl:apply-templates select ="report.due.b"/>
	</xsl:template>

	<!--Filing Number-->
	<xsl:template match="filg.nbr|ent.filg.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Identification Number-->
	<xsl:template match="id.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_identificationNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Date-->
	<xsl:template match="filg.d|nm.flg.d|creation.d|ent.filg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Formed Under Act Number-->
	<xsl:template match="filg.act.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_formedUnderActNumbers;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="act2[preceding-sibling::act1]">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="act3[preceding-sibling::act1 or preceding-sibling::act2]">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<!--State of Incorporation-->
	<xsl:template match="st.inc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateOfIncorporation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Foreign Incorporation-->
	<xsl:template match="frgn.inc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_foreignIncorporation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Country of Incorporation-->
	<xsl:template match="cntry.inc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countryOfIncorporation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date Incorporated-->
	<xsl:template match="inc.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateIncorporated;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Expiration Date-->
	<xsl:template match="exp.d|name.rnew.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_expirationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Duration-->
	<xsl:template match="perp">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_duration;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Status-->
	<xsl:template match="stat.cd">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_status;</xsl:text>
			</th>
			<td>
				<xsl:choose>
					<xsl:when test="(string-length(.) > 8) and ($sbStateInfo='NY SOS')">
						<xsl:value-of select="substring(.,1, string-length(.)-11)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!--Status Attained Date-->
	<xsl:template match="stat.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_statusAttainedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Corporation Type-->
	<xsl:template match="filg.corp">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_corporationType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Business Type-->
	<xsl:template match="filg.reg|filg.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Corporate Classification-->
	<xsl:template match="corp.class">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_corporateClassification;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Address Type-->
	<xsl:template match="filg.addr.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Federal Identification Number-->
	<xsl:template match="fein">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="parent::node()/l"/>
			<xsl:with-param name="nodeType" select="$FEIN"/>
		</xsl:call-template>
	</xsl:template>

	<!--Report Due Date-->
	<xsl:template match="report.due.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_reportDueDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--LLC Jurisdiction-->
	<xsl:template match="llc.juris.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_llcJurisdiction;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--LLC Business Type-->
	<xsl:template match="llc.bus.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_llcBusinessType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--LLC Management-->
	<xsl:template match="llc.mgmt.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_llcManagement;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Original Filing Number-->
	<xsl:template match="orig.filg.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalFilingNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Original Filing Date-->
	<xsl:template match="orig.filg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalFilingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Original Filing County-->
	<xsl:template match="orig.filg.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalFilingCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Where Filed Address -->
	<xsl:template match="filg.off.b">
		<xsl:apply-templates select="filg.off"/>
		<xsl:apply-templates select="off.addr.b"/>
	</xsl:template>

	<xsl:template match="filg.off">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_whereFiled;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Address -->
	<xsl:template match="off.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="''"/>
			<xsl:with-param name="streetNum" select="off.str"/>
			<xsl:with-param name="street" select="off.str2"/>
			<xsl:with-param name="city" select="off.cty"/>
			<xsl:with-param name="stateOrProvince" select="off.st"/>
			<xsl:with-param name="zip" select="off.zip|off.zip.b/off.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!--Paid-In-Capitial-->
	<xsl:template match="pd.cap">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paidInCapital;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date Paid-In-Capital-->
	<xsl:template match="pd.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_datePaidInCapital;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>
	<!--***************End of Filing Information******************** -->

	<!--*************(C)-3 Registered Agent Information *************** -->
	<xsl:template match="reg.agt.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_registeredAgentInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Registered Agent Information Name-->
	<xsl:template match="agt.name|off.name|loc.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_name;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Registered Agent Information Name-->
	<xsl:template match="ent.name">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Registered Agent Information Address-->
	<xsl:template match="agt.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="streetNum" select="agt.str"/>
			<xsl:with-param name="street" select="agt.str2"/>
			<xsl:with-param name="city" select="agt.cty"/>
			<xsl:with-param name="stateOrProvince" select="agt.st"/>
			<xsl:with-param name="zip" select="agt.zip|agt.zip.b/agt.zip"/>
			<xsl:with-param name="zipExt" select="agt.zip.b/agt.zip.ext"/>
			<xsl:with-param name="country" select="agt.cntry"/>
		</xsl:call-template>
	</xsl:template>

	<!--Registered Agent Appointed Date-->
	<xsl:template match="agt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_appointedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Registered Agent Status-->
	<xsl:template match="agt.stat|name.stat|fran.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_status;'"/>
		</xsl:call-template>
	</xsl:template>
	<!-- ******** End of Registered Agent Information *******************-->

	<!--*************(C)-4 Name Information**************** -->
	<xsl:template match="name.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_nameInformationSubheader;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Name-->
	<xsl:template match="bus.name">
		<xsl:variable name="label">
			<xsl:if test ="preceding-sibling::name.t">
				<xsl:value-of select="preceding-sibling::name.t"/>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="($sbStateInfo='NY SOS')">
					<xsl:value-of select="preceding-sibling::l"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_name;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Inactive Date-->
	<xsl:template match="cancl.d">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="ancestor::Document/descendant::col.key = 'OK'">
					<xsl:text>&pr_expirationDate;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_inactiveDate;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Inactive Date-->
	<xsl:template match="cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_counties;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ****************End of Name Information ***********-->

	<!--*************(C)-5 Associated Entities Information**************** -->
	<xsl:template match="assoc.ent.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_associatedEntitiesInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Associated Entities type-->
	<xsl:template match="ent.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_type;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Associated Entities Jurisdiction-->
	<xsl:template match="ent.jur.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_jurisdiction;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Associated Entities Capacity-->
	<xsl:template match="ent.capacity">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_capacity;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Associated Entities Inactive Date-->
	<xsl:template match="ent.inact.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_inactiveDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--*************End of Associated Entities Information**************** -->

	<!--*************(C)-6 Principal Information**************** -->
	<!--Tracker 119585-->
	<xsl:template match="prin">
		<xsl:if test="(.!='') and off.name.b or ti.b or addr.b">
			<xsl:if test="not(preceding-sibling::prin)">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_principalInformation;'"/>
				</xsl:call-template>
			</xsl:if>
			<table class="&pr_table;">
				<xsl:apply-templates select="off.name.b"/>
				<xsl:apply-templates select="ti.b"/>
				<xsl:apply-templates select="addr.b"/>
			</table>
		</xsl:if>
		<xsl:if test="office.loc.b">
			<xsl:if test="not(preceding-sibling::prin)">
				<xsl:variable name="subheader">
					<xsl:if test="$sbStateInfo!='NY SOS'">
						<xsl:text>&pr_principal;<![CDATA[ ]]></xsl:text>
					</xsl:if>
					<xsl:text>&pr_executiveOfficeInformation;</xsl:text>
				</xsl:variable>
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="$subheader"/>
				</xsl:call-template>
			</xsl:if>
			<table class="&pr_table;">
				<xsl:apply-templates select="office.loc.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!--Principal Information Title-->
	<xsl:template match="ti.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_title;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ti2|ti3">
		<xsl:text>,<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Principal Information Address -->
	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="streetNum" select="str"/>
			<xsl:with-param name="street" select="str2"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="post.cd|post.cd.b/post.cd"/>
			<xsl:with-param name="zipExt" select="post.cd.b/post.cd.ext"/>
			<xsl:with-param name="country" select="cntry"/>
		</xsl:call-template>
	</xsl:template>

	<!--Principal Executive Information Address -->
	<xsl:template match="loc.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="streetNum" select="loc.str"/>
			<xsl:with-param name="street" select="loc.str2"/>
			<xsl:with-param name="city" select="loc.cty"/>
			<xsl:with-param name="stateOrProvince" select="loc.st"/>
			<xsl:with-param name="zip" select="loc.zip.b/lic.zip"/>
			<xsl:with-param name="zipExt" select="loc.zip.b/lic.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!--*************End of Principal Information ****************** -->

	<!-- *********** (C)-7 Amendment Information ***********-->
	<xsl:template match="amd.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_amendmentInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="amd.b"/>
		</table>
	</xsl:template>

	<!--Amendments-->
	<xsl:template match="amd.b[not(preceding-sibling::amd.b)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_amendments;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="amd.b[preceding-sibling::amd.b]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="' '"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="reas|amd.desc">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- ******** End of Amendment Information *******************-->

	<!--*******(C)-8 Stock Information ****** -->
	<xsl:template match="stk.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_stockInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Authorized Capital-->
	<xsl:template match="auth.cap">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_authorizedCapital;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Actual Amount Invested-->
	<xsl:template match="act.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actualAmountInvested;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Paid On Credit-->
	<xsl:template match="pd.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paidOnCredit;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Stock-->
	<xsl:template match="stk.t[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stock;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- indented list under Stock:-->
	<!--Dollar Value-->
	<xsl:template match="dol.val">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dollarValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Authorized Shares-->
	<xsl:template match="auth.qty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_authorizedShares;'"/>
			<xsl:with-param name="nodeType" select="$NUMBER"/>
		</xsl:call-template>
	</xsl:template>

	<!--Stock Issued-->
	<xsl:template match="iss.qty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stockIssued;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Par Value-->
	<xsl:template match="par.val">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_parValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Voting Rights-->
	<xsl:template match="votg">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_votingRights;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Convertible-->
	<xsl:template match="convt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_convertible;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Stock Class-->
	<xsl:template match="stk.cl">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stockClass;'"/>
		</xsl:call-template>
	</xsl:template>
	<!-- end of indented lists-->

	<!-- ****************End of Stock Information ***********-->

	<!-- ************ (C)-9 Tax Information ************************-->
	<xsl:template match="tax.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_taxInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Franchise Tax Details-->
	<xsl:template match="fran.detail.b">
		<xsl:call-template name="wrapTaxDetailSection">
			<xsl:with-param name="label" select="'&pr_franchiseTaxDetails;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- indented lists under frachise Tax Detail:-->
	<!--Amount Paid-->
	<xsl:template match="fran.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_amountPaid;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date Paid-->
	<xsl:template match="fran.pd.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_datePaid;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>
	<!-- end of indented lists-->

	<!--Franchise Tax Balance Amount-->
	<xsl:template match="fran.stat.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_franchiseTaxBalanceAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Corporate Tax Details-->
	<xsl:template match="corp.detail.b">
		<xsl:call-template name="wrapTaxDetailSection">
			<xsl:with-param name="label" select="'&pr_corporateTaxDetails;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- indented list under corporate Tax Details:-->
	<xsl:template name="wrapTaxDetailSection">
		<xsl:param name="label"/>
		<tr class="&pr_item;">
			<th>
				<xsl:value-of select="$label"/>
			</th>
			<td><![CDATA[ ]]></td>
		</tr>
		<tr class="&pr_item;">
			<td colspan="2">
				<table class="&pr_tablePaddingLeft;">
					<xsl:apply-templates/>
				</table>
			</td>
		</tr>
	</xsl:template>

	<!--Tax Factor-->
	<xsl:template match="tax.pctg">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxFactor;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Tax Paid Amount-->
	<xsl:template match="tax.pd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxPaidAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Tax Capital-->
	<xsl:template match="tax.cap">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxCapital;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Total Capital-->
	<xsl:template match="tot.tax.cap">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalCapital;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Annual Report Filed-->
	<xsl:template match="fil.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_annualReportFiled;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Tax Balance Amount-->
	<xsl:template match="tax.bal">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxBalanceAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ************End of Tax Information ************************-->

	<!--********* (C)-10 Additional Detail Information ********* -->
	<xsl:template match="addn.det.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_additionalDetailInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Additional Details-->
	<xsl:template match="addn.det">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalDetails;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--********* End of Additional Detail Information********* -->

	<!-- *********** (D)Document Footer *********-->
	<xsl:template name ="DocFooter">
		<xsl:call-template name="wrapPublicRecordsDisclaimers">
			<xsl:with-param name="disclaimer1">
				<xsl:text>The preceding public record data is for information purposes only and is not the official record.
					Certified copies can only be obtained from the official source.</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="disclaimer2">
				<xsl:text>The public record items reported above may have been paid, terminated, vacated or released prior to today's date.</xsl:text>
			</xsl:with-param>
		</xsl:call-template>

		<xsl:call-template name="outputOrderDocumentsSection"/>
	</xsl:template>

</xsl:stylesheet>