<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>

	<!--Combined Equifax Records - 
		EXEC-PRO	
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

	<!--Variables-->
	<xsl:variable name="pvalue" select="/Document/n-docbody/r/p"/>
	<xsl:variable name="stateId" select="/Document/n-docbody/r/col.key"/>

	<!-- Do not render these nodes -->
	<xsl:template match="prim.cnty.cd | leg.immed.no | leg.ult.no | aff.immed.no | aff.ult.no |aff.glob.ult.ind | aff.par.ind | aff.link.ind | leg.ult.ind | leg.par.ind | leg.link.ult.ind | leg.entity.ind | efxid | new.efxid | efx.delta"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsExecutiveProfileClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_executiveProfileRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="exec.info.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="bus.info.b"/>
		<xsl:apply-templates select="home.info.b" />
	</xsl:template>

	<!-- ********************************************************************** 
	*******Executive Information section  *************************************
	************************************************************************-->
	<xsl:template match="exec.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_executiveInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="exec.name"/>
			<xsl:apply-templates select="gender"/>
			<xsl:apply-templates select="exec.age"/>
			<xsl:apply-templates select="mr.stat"/>
		</table>
	</xsl:template>

	<!-- Executive Name information-->
	<xsl:template match="exec.name">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_executiveName;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Executive Gener information-->
	<xsl:template match="gender">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Executive Age information-->
	<xsl:template match="exec.age">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_age;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Executive Marital Status information-->
	<xsl:template match="mr.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_maritalStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******Executive Business Information section  ****************************
	************************************************************************-->

	<xsl:template match ="bus.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_executiveBusinessInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="co.name"/>
			<xsl:apply-templates select="exec.title" />
			<xsl:apply-templates select="bus.addr.b" />
			<xsl:apply-templates select="bus.phn" />
			<xsl:apply-templates select="bus.email" />
			<xsl:apply-templates select="bus.cnty" />
			<xsl:apply-templates select="no.empl" />
			<xsl:apply-templates select="sales" />
			<xsl:apply-templates select="sic.b" />
			<xsl:apply-templates select="bus.loc.type" />
		</table>
	</xsl:template>

	<!-- Business name information-->
	<xsl:template match="co.name">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_businessName;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Title information-->
	<xsl:template match="exec.title">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_executiveTitle;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business address information-->
	<xsl:template match="bus.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="bus.str"/>
			<xsl:with-param name="city" select="bus.cty"/>
			<xsl:with-param name="stateOrProvince" select="bus.st"/>
			<xsl:with-param name="zip" select="bus.zip.b/bus.zip"/>
			<xsl:with-param name="zipExt" select="bus.zip.b/bus.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Phone information-->
	<xsl:template match="bus.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Work E-mail information-->
	<xsl:template match="bus.email">
		<xsl:call-template name="wrapPublicRecordsEmail">
			<xsl:with-param name="label" select="'&pr_workEmail;'"/>
			<xsl:with-param name="email" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business County information-->
	<xsl:template match="bus.cnty | home.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Employees at Location information-->
	<xsl:template match="no.empl">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_employeesAtLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Sales from Location information-->
	<xsl:template match="sales">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_salesFromLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Primary SIC information-->
	<xsl:template match="sic.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_primarySic;'"/>
			<xsl:with-param name="selectNodes" select="sic.code | sic.descr"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sic.code">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<!-- Business Location information-->
	<xsl:template match="bus.loc.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******Executive Household Information section  ****************************
	************************************************************************-->

	<xsl:template match ="home.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_executiveHouseholdInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="home.addr.b"/>
			<xsl:apply-templates select="home.res.type" />
			<xsl:apply-templates select="home.occ.type" />
			<xsl:apply-templates select="home.phn" />
			<xsl:apply-templates select="home.email" />
			<xsl:apply-templates select="home.cnty" />
			<xsl:apply-templates select="msa.cd" />
		</table>
	</xsl:template>

	<!-- Home address information-->
	<xsl:template match="home.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="home.str"/>
			<xsl:with-param name="city" select="home.cty"/>
			<xsl:with-param name="stateOrProvince" select="home.st"/>
			<xsl:with-param name="zip" select="home.zip.b/home.zip"/>
			<xsl:with-param name="zipExt" select="home.zip.b/home.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Home Location information-->
	<xsl:template match="home.res.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_locationType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Home Occupancy Type information-->
	<xsl:template match="home.occ.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_occupancyType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Home/Personal Phone information-->
	<xsl:template match="home.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_homeOrPersonalPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Other E-mail information-->
	<xsl:template match="home.email">
		<xsl:call-template name="wrapPublicRecordsEmail">
			<xsl:with-param name="label" select="'&pr_otherEmail;'"/>
			<xsl:with-param name="email" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- MSA information-->
	<xsl:template match="msa.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_msa;'"/>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
