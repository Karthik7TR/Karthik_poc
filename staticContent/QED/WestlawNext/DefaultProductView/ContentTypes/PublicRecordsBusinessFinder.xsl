<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>

	<!-- Database Signon: BUSFIND-US -->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--  Do not render these nodes -->
	<xsl:template match="map|p|pc|c|coll.nm|col.key" />

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<!-- Render the XML based on the desired VIEW. -->
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsBusinessFindClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_businessFinderRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="cn.info.b"/>
		<xsl:apply-templates select="nm.info.b | con.loc.b"/>
		<xsl:call-template name="outputOrderDocumentsSection"/>
	</xsl:template>

	<!--****************************************************************** 
	*********************  (B) COMPANY INFORMATION ***********************
	*******************************************************************-->
	<xsl:template match="cn.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_companyInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="co.nm | co.nmc" />
			<xsl:apply-templates select="addr.b" />
			<xsl:apply-templates select="ph.nbr | ph.nbrc" />
			<xsl:apply-templates select="cnty.nm | cnty.nmc" />
			<xsl:apply-templates select="prm.sic.b" />
			<xsl:apply-templates select="bus.sic.b" />
			<xsl:if test="frn.spec.desc">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_franchiseOrSpecialty;'"/>
					<xsl:with-param name="selectNodes" select="frn.spec.desc"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:apply-templates select="ind.spec.cd" />
			<xsl:apply-templates select="ind.cd | ind.cdc" />
			<xsl:apply-templates select="hdq.bus.cd | hdq.bus.cdc" />
			<xsl:apply-templates select="pub.cd | pub.cdc" />
			<xsl:apply-templates select="sls.vol.cd | sls.vol.cdc" />
			<xsl:apply-templates select="emp.sz.cd | emp.sz.cdc" />
			<xsl:apply-templates select="pop.cd | pop.cdc" />
			<xsl:apply-templates select="adsz.cd | adsz.cdc" />
		</table>
	</xsl:template>

	<!--Business:Name-->
	<xsl:template match="co.nm | co.nmc">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_businessName;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Business:Address-->
	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_businessAddress;'"/>
			<xsl:with-param name="street" select="addr | addrc"/>
			<xsl:with-param name="city" select="cty | ctyc"/>
			<xsl:with-param name="stateOrProvince" select="st.abbr | provc"/>
			<xsl:with-param name="zip" select="zip.b/zip.5.cd | canzip.cd"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.4.cd"/>
			<xsl:with-param name="carrierRoute" select="car.cd"/>
		</xsl:call-template>
	</xsl:template>

	<!--Phone-->
	<xsl:template match="ph.nbr | ph.nbrc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--County-->
	<xsl:template match="cnty.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Region-->
	<xsl:template match="cnty.nmc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_region;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Primary SIC-->
	<xsl:template match="prm.sic.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_primarySic;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Primary SIC Description -->
	<xsl:template match="c.desc">
		<xsl:if test="preceding-sibling::c.nbr">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Business Specific SIC-->
	<xsl:template match="bus.sic.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessSpecificSic;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Business Specific Description-->
	<xsl:template match="bus.sic.desc">
		<xsl:if test="preceding-sibling::bus.sic.nbr">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Franchise Specialty:Description-->
	<xsl:template match="frn.spec.desc">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--Additional Business Information-->
	<xsl:template match="ind.spec.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalBusinessInformation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Organization-->
	<xsl:template match="ind.cd | ind.cdc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_organization;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Status-->
	<xsl:template match="hdq.bus.cd | hdq.bus.cdc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_status;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Publicly-Held Business-->
	<xsl:template match="pub.cd | pub.cdc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_publiclyHeldBusiness;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Sales from the current location-->
	<xsl:template match="sls.vol.cd | sls.vol.cdc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_salesFromLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Employees at Location-->
	<xsl:template match="emp.sz.cd | emp.sz.cdc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_employeesAtLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Population of Area-->
	<xsl:template match="pop.cd | pop.cdc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_populationOfArea;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Ad Size-->
	<xsl:template match="adsz.cd | adsz.cdc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_adSize;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--****************************************************************** 
	************************  (C) NAME INFORMATION ***********************
	*******************************************************************-->

	<!--  Name Info -->
	<xsl:template match="nm.info.b[count(descendant::*) > 0] | con.loc.b[count(descendant::*) > 0]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_nameInformationSubheader;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Name Information:Name-->
	<xsl:template match="con.na.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_contactForLocation;'"/>
			<xsl:with-param name="firstName" select="first.nm | first.nmc"/>
			<xsl:with-param name="lastName" select="last.nm | last.nmc"/>
			<xsl:with-param name="professionalSuffixName" select="pro.ttl.cd | pro.ttl.cdc"/>
		</xsl:call-template>
	</xsl:template>

	<!--Title-->
	<xsl:template match="ttl.cd | ttl.cdc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_title;'"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
