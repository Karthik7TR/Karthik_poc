<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="Copyright.xsl"/>
  <xsl:include href="PublicRecords.xsl"/>
  <xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <!-- Do not render these nodes -->
  <xsl:template match="legacy.id|col.key|p|pc|birth.d"/>

  <!--  apply templates to just n-docbody (not CoverageData) as well -->
  <xsl:template match="n-docbody">
    <xsl:apply-templates select="r"/>
  </xsl:template>
	
  <xsl:template match="r" priority="1">
    <xsl:call-template name="PublicRecordsContent">
      <xsl:with-param name="container" select="'&contentTypePublicRecordsCCWClass;'" />
    </xsl:call-template>
  </xsl:template>

  <!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
  <xsl:template name="PublicRecordsHeader">
    <xsl:call-template name="wrapPublicRecordsSection">
      <xsl:with-param name="class" select="'&pr_header;'" />
      <xsl:with-param name="contents" select="'&pr_CCWRecord;'" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="PublicRecordsLeftColumn">
	<xsl:apply-templates select="$coverage-block">
	  <xsl:with-param name="displayCoverageState" select="true()"/>
	</xsl:apply-templates>  
	  
    <xsl:apply-templates select="person.b"/>
  </xsl:template>

  <xsl:template name="PublicRecordsRightColumn">
    <xsl:apply-templates select="ccw.permit.b"/>
  </xsl:template>

  <!--**********************************************************************
	****************  "Name and Personal Information" section  ***************
	************************************************************************-->
  <xsl:template match="person.b">
    <xsl:call-template name="wrapPublicRecordsSection">
      <xsl:with-param name="class" select="'&pr_subheader;'"/>
      <xsl:with-param name="contents" select="'&pr_nameAndPersonalInformation;'"/>
    </xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select ="name.b"/>
			<xsl:apply-templates select ="prior"/>
			<xsl:apply-templates select ="addr.b"/>
			<xsl:apply-templates select ="mail.addr.b"/>
			<xsl:apply-templates select ="ph.b"/>
			<xsl:apply-templates select ="hph.nbr"/>
			<xsl:apply-templates select ="wph.nbr"/>
			<xsl:apply-templates select ="oph.nbr"/>
		</table>
  </xsl:template>

  <!-- Name -->
  <xsl:template match="name.b">
    <xsl:call-template name="wrapPublicRecordsName">
      <xsl:with-param name="label" select="'&pr_name;'"/>
      <xsl:with-param name="firstName" select="fname"/>
      <xsl:with-param name="middleName" select="mname"/>
      <xsl:with-param name="lastName" select="lname"/>
      <xsl:with-param name="suffixName" select="suf"/>
    </xsl:call-template>
  </xsl:template>
  
  <!--Prior names-->
  <xsl:template match="prior">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_otherNames;'"/>
    </xsl:call-template>
  </xsl:template>

  <!-- Address -->
  <xsl:template match="addr.b">
    <xsl:call-template name="wrapPublicRecordsAddress">
      <xsl:with-param name="label" select="'&pr_address;'"/>
      <xsl:with-param name="street" select="str"/>
      <xsl:with-param name="city" select="cty"/>
      <xsl:with-param name="stateOrProvince" select="st"/>
      <xsl:with-param name="zip" select="zip.b/zip"/>
      <xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
    </xsl:call-template>
  </xsl:template>

  <!-- Mailing address -->
  <xsl:template match="mail.addr.b">
    <xsl:call-template name="wrapPublicRecordsAddress">
      <xsl:with-param name="label" select="'&pr_mailingAddress;'"/>
      <xsl:with-param name="street" select="str"/>
      <xsl:with-param name="city" select="cty"/>
      <xsl:with-param name="stateOrProvince" select="st"/>
      <xsl:with-param name="zip" select="zip.b/zip"/>
      <xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
    </xsl:call-template>
  </xsl:template>

  <!--Phone numbers block-->
  <xsl:template match="ph.b">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- Home Phone number -->
  <xsl:template match="hph.nbr">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_homePhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
    </xsl:call-template>
  </xsl:template>

  <!-- Work Phone number -->
  <xsl:template match="wph.nbr">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_workPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
    </xsl:call-template>
  </xsl:template>

  <!-- Other Phone number -->
  <xsl:template match="oph.nbr">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_otherPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
    </xsl:call-template>
  </xsl:template>

  <!--**********************************************************************
	*********************  "Permit Information" section  ********************
	************************************************************************-->
  <xsl:template match="ccw.permit.b">
    <xsl:call-template name="wrapPublicRecordsSection">
      <xsl:with-param name="class" select="'&pr_subheader;'"/>
      <xsl:with-param name="contents" select="'&pr_permitInfo;'"/>
    </xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select ="permit.lic.nbr"/>
			<xsl:apply-templates select ="weapon.t"/>
			<xsl:apply-templates select ="permit.iss.d"/>
			<xsl:apply-templates select ="permit.exp.d"/>
			<xsl:apply-templates select ="permit.stat"/>
		</table>
  </xsl:template>

  <!--Permit/License Number-->
  <xsl:template match="permit.lic.nbr[normalize-space(.)]">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_permitNumber;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Weapon Type-->
  <xsl:template match="weapon.t[normalize-space(.)]">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_weaponType;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Date Permit Issued-->
  <xsl:template match="permit.iss.d">
    <tr>
      <xsl:call-template name="wrapWithTableHeader">
        <xsl:with-param name="contents" select="'&pr_datePermitIssued;'" />
      </xsl:call-template>
      <td>
        <xsl:call-template name="parseYearMonthDayDateFormat"/>
      </td>
    </tr>
  </xsl:template>

  <!--Permit Expiration Date-->
  <xsl:template match="permit.exp.d">
    <tr>
      <xsl:call-template name="wrapWithTableHeader">
        <xsl:with-param name="contents" select="'&pr_permitExpirationDate;'" />
      </xsl:call-template>
      <td>
        <xsl:call-template name="parseYearMonthDayDateFormat"/>
      </td>
    </tr>
  </xsl:template>

  <!--Permit Status-->
  <xsl:template match="permit.stat[normalize-space(.)]">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_permitStatus;'"/>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>