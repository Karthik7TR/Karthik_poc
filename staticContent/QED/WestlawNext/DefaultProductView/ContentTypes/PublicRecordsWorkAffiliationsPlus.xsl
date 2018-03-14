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

	<!-- Work Affiliations -->
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--Variables-->
	<xsl:variable name="lowerCase" select="'abcdefghijklmnopqrstuvwxyz'"/>
	<xsl:variable name="upperCase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
	<xsl:variable name="pvalue" select="translate(normalize-space(/Document/n-docbody/r/p),$lowerCase,$upperCase)"/>
	<xsl:variable name="pcvalue" select="translate(normalize-space(/Document/n-docbody/r/pc),$lowerCase,$upperCase)"/>
	<xsl:variable name="cvalue" select="translate(normalize-space(/Document/n-docbody/r/coll.nm),$lowerCase,$upperCase)"/>

	<!-- Do not render these nodes -->
	<xsl:template match="bus.descr.b | prim.cnty.cd | leg.immed.no | leg.ult.no | aff.immed.no | aff.ult.no |aff.glob.ult.ind | aff.par.ind
					| aff.link.ind | leg.ult.ind | leg.par.ind | leg.link.ult.ind | leg.entity.ind | efx.id | new.efx.id | efx.delta"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsWorkAffiliationsPlusClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader" priority="1">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_workAffiliationsPlusRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn" priority="1">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
			<xsl:with-param name="displayCurrentThroughDate" select="false()"/>
			<xsl:with-param name="displayCurrentDate" select="false()"/>
		</xsl:apply-templates>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_individualInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="individ.info.b/na.b"/>
			<xsl:apply-templates select="individ.info.b/ssn.b"/>
			<xsl:apply-templates select="individ.info.b/lic.b"/>
			<xsl:apply-templates select="individ.info.b/birth.d"/>
			<xsl:apply-templates select="individ.info.b/gen"/>
			<xsl:apply-templates select="individ.info.b/mil.act.dty"/>
			<xsl:apply-templates select="l.rptd.d"/>
		</table>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_addressInfo;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="individ.info.b/addr.b"/>
		</table>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn" priority="1">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_emailAndPhoneInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="individ.info.b/phn.b"/>
			<xsl:apply-templates select="individ.info.b/email.b"/>
			<xsl:apply-templates select="ip.addr"/>
		</table>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_workplaceInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="workp.b/emp.na"/>
			<xsl:apply-templates select="workp.b/work.addr.b"/>
		</table>
		<xsl:if test="assoc.b">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_associates;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="assoc.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- ********************************************************************** 
	*******************  INDIVIDUAL INFORMATION  *************************************
	************************************************************************-->

	<xsl:template match="na.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="lastName" select="last"/>
			<xsl:with-param name="firstName" select="first"/>
			<xsl:with-param name="middleName" select="na.mid"/>
			<xsl:with-param name="suffixName" select="na.suf"/>
 		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="ssn.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ssn;'"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lic.b">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="lic.no">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_driversLicenseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="iss.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dLState;'"/>
		</xsl:call-template>
	</xsl:template>

  <xsl:template match="birth.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="gen">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mil.act.dty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_militaryActiveDuty;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="l.rptd.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastReportedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- ********************************************************************** 
	*******************  ADDRESS INFORMATION  *************************************
	************************************************************************-->

	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="defaultLabel" select="'&pr_address;'"/>
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
		</xsl:call-template>
		<xsl:apply-templates select="move.in.d"/>
		<xsl:apply-templates select="res.stat"/>
		<xsl:apply-templates select="res.len"/>
	</xsl:template>

	<xsl:template match="move.in.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_moveInDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="res.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_residenceStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="res.len">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_residenceLength;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******************  EMAIL & PHONE INFORMATION  *************************************
	************************************************************************-->

	<xsl:template match="phn.b">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="h.phn.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_homePhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="m.phn.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mobilePhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="w.phn1.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_workPhone1;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="w.phn2.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_workPhone2;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="w.phn3.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_workPhone3;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="email.b">
		<xsl:call-template name="wrapPublicRecordsEmail">
			<xsl:with-param name="defaultLabel" select="'&pr_emailAddress;'"/>
			<xsl:with-param name="email" select="email"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ip.addr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ipAddress;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******************  WORKPLACE INFORMATION  *************************************
	************************************************************************-->

	<xsl:template match="emp.na">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_employerName;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="work.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="defaultLabel" select="'&pr_address;'"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******************  ASSOCIATES  *************************************
	************************************************************************-->

	<xsl:template match="assoc.b">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="assoc.fullname">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="defaultLabel" select="'&pr_associateName;'"/>
			<xsl:with-param name="lastName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="assoc.rel">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_relationship;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="assoc.phn.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_associatePhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
