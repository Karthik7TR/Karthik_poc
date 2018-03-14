<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="Copyright.xsl"/>
  <xsl:include href="PublicRecords.xsl"/>
  <xsl:include href="PublicRecordsAddress.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <!-- Do not render these nodes -->
  <xsl:template match="map|p|col.key"/>

  <!--  apply templates to just n-docbody (not CoverageData) as well -->
  <xsl:template match="n-docbody">
	<xsl:apply-templates select="r"/>
  </xsl:template>
	
  <xsl:template match="r" priority="1">
    <xsl:call-template name="PublicRecordsContent">
      <xsl:with-param name="container" select="'&contentTypePublicRecordsUnclaimedAssetsClass;'" />
    </xsl:call-template>
  </xsl:template>

  <!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
  <xsl:template name="PublicRecordsHeader">
    <xsl:call-template name="wrapPublicRecordsSection">
      <xsl:with-param name="class" select="'&pr_header;'" />
      <xsl:with-param name="contents" select="'&pr_unclaimedPropertyRecord;'" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="PublicRecordsLeftColumn">
    <!-- Render the "Coverage" section -->
    <xsl:apply-templates select="$coverage-block"/>
    <!-- Render the "Owner Information" section -->
    <xsl:apply-templates select="descendant::owner.info.b"/>
  </xsl:template>

  <xsl:template name="PublicRecordsRightColumn">
    <!-- Render the "Unclaimed Property Information" section -->
    <xsl:apply-templates select="descendant::asset.info.b"/>
  </xsl:template>

  <!-- ********************************************************************** 
	*************************  (B)"Owner Information" section  *************************
	************************************************************************-->

  <xsl:template match="owner.info.b">
    <xsl:call-template name="wrapPublicRecordsSection">
      <xsl:with-param name="class" select="'&pr_subheader;'"/>
      <xsl:with-param name="contents" select="'&pr_ownerInformation;'"/>
    </xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="own.na"/>
			<xsl:apply-templates select="own.addr.b"/>
		</table>
  </xsl:template>

  <!--Name-->
  <xsl:template match="own.na">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_name;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Address-->
  <xsl:template match="own.addr.b">
    <xsl:call-template name="wrapPublicRecordsAddress">
      <xsl:with-param name="label" select="'&pr_lastKnownAddress;'"/>
      <xsl:with-param name="street" select="own.str"/>
      <xsl:with-param name="city" select="own.cty"/>
      <xsl:with-param name="stateOrProvince" select="own.st"/>
      <xsl:with-param name="zip" select="own.zip.b/own.zip"/>
      <xsl:with-param name="zipExt" select="own.zip.b/own.zip.ext"/>
    </xsl:call-template>
  </xsl:template>

  <!-- ********************************************************************** 
	*************************  (B)"Unclaimed Property Information" section  *************************
	************************************************************************-->
  <xsl:template match="asset.info.b">
    <xsl:call-template name="wrapPublicRecordsSection">
      <xsl:with-param name="class" select="'&pr_subheader;'"/>
      <xsl:with-param name="contents" select="'&pr_unclaimedPropertyInformation;'"/>
    </xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="as.holder"/>
			<xsl:apply-templates select="as.nbr"/>
			<xsl:apply-templates select="as.type"/>
			<xsl:apply-templates select="escheat.d"/>
			<xsl:apply-templates select="as.value | value.text"/>
			<xsl:apply-templates select="shares.nbr"/>
		</table>
  </xsl:template>

  <!--Asset Holder-->
  <xsl:template match="as.holder">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_assetHolder;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Asset Number-->
  <xsl:template match="as.nbr">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_assetNumber;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Asset Type-->
  <xsl:template match="as.type">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_assetType;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Reported Date-->
	<xsl:template match="escheat.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_reportedDate;'" />
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>
	
	<!--Asset Value-->
	<xsl:template match="as.value">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_assetValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<!--Asset Value-->
	<xsl:template match="value.text">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_assetValue;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-->Number of Shares-->
  <xsl:template match="shares.nbr">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_numberOfShares;'"/>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>