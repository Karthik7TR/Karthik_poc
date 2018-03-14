<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
  <xsl:include href="BusinessInvestigatorName.xsl"/>

	<!-- Database Signon: FBN-ALL -->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--  Do not render these nodes -->
	<xsl:template match="map|p|c|pre|coll.name|cnty.b|col.key|prism-clipdate|l|filg.off.b|misc.info.b|filg.duns|filg.nbr.b|agt.d.b|agt.stat.b|filg.cntry" />

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsFictitiousBusinessNameClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_fictitiousBusinessNameRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="co.info.b | bus.info.b"/>
		<xsl:if test="normalize-space(name.info)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_nameInformationSubheader;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="name.info"/>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="filg.info | filg.info.b"/>
		<!--Tracker 127390 - Changed calling order-->
		<xsl:if test="normalize-space(prin.info) or normalize-space(cont.info.b) or normalize-space(reg.agt.info)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_ownerInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="prin.info | cont.info.b | reg.agt.info"/>
			</table>
		</xsl:if>
		<xsl:call-template name="outputOrderDocumentsSection"/>
	</xsl:template>

	<!-- 
	********************************************************************** 
	*********************  (B) BUSINESS INFORMATION ***********************
	**********************************************************************	
	-->

	<!--  Company Info -->
	<xsl:template match="co.info.b | bus.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_businessInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
			<xsl:if test="not(normalize-space(../name.info))">
				<xsl:call-template name="StateOrCountryOfCorporation"/>
			</xsl:if>
		</table>
	</xsl:template>

	<xsl:template name="StateOrCountryOfCorporation">
		<xsl:apply-templates select="../filg.info/filg/st.inc.b" />
		<xsl:if test="not(normalize-space(../filg.info/filg/st.inc.b))">
			<xsl:apply-templates select="../filg.info/filg/cntry.inc.b" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="bus.nm.b">
		<xsl:apply-templates select="bus.nm"/>
	</xsl:template>

  <!--  Business Name -->
  <xsl:template match="bus.nm">
    <xsl:call-template name="wrapBusinessInvestigatorName">
      <xsl:with-param name="label" select="'&pr_name;'"/>
      <xsl:with-param name="companyName" select="."/>
    </xsl:call-template>
  </xsl:template>
  
	<!--  Business Name -->
	<xsl:template match="co.name.b| off.name | agt.name | cont.nme">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_name;'"/>
    </xsl:call-template>
	</xsl:template>

	<!--  County -->
	<xsl:template match="cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone -->
	<xsl:template match="bus.phn.b">
		<xsl:apply-templates select="bus.phn"/>
	</xsl:template>

	<xsl:template match="cont.phn.b">
		<xsl:apply-templates select="cont.phn"/>
	</xsl:template>

	<xsl:template match="bus.phn | cont.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Primary SIC Code -->
	<xsl:template match="sic.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_primarySicCode;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- SIC Description -->
	<xsl:template match="c.desc">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<!-- 
	********************************************************************** 
	**********************  (C) NAME INFORMATION *************************
	**********************************************************************	
	-->

	<xsl:template match="name.info">
		<xsl:apply-templates/>
		<xsl:if test="not(following-sibling::name.info)">
			<xsl:call-template name="StateOrCountryOfCorporation"/>
		</xsl:if>
	</xsl:template>

	<!-- Legal, Assumed, Trade, etc. Name -->
	<xsl:template match="bus.name.b">
		<xsl:variable name="label">
			<xsl:apply-templates select="name.t"/>
			<xsl:text><![CDATA[ ]]>&pr_name;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="selectNodes" select="bus.name"/>
		</xsl:call-template>
	</xsl:template>

	<!-- DUNS -->
	<xsl:template match="bus.duns.b/bus.duns">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_duns;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Country of Incorporation -->
	<xsl:template match="cntry.inc.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countryOfIncorporation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- State of Incorporation -->
	<xsl:template match="st.inc.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateOfIncorporation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- 
	********************************************************************** 
	*********************  (D) FILING INFORMATION ***********************
	**********************************************************************	
	-->

	<xsl:template match="filg.info | filg.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_filingInfo;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="filg.d.b | filg/filg.d.b" />
			<!-- Added filg.nbr.b here so that limit by id works, and also shows filing information in some other non-limit by use-cases where it didn't before -->
			<xsl:apply-templates select="id.nbr.b | filg/id.nbr.b | filg.nbr.b" />
			<xsl:apply-templates select="bus.typ.b" />
			<xsl:apply-templates select="stat.cd.b | filg/stat.cd.b" />
			<xsl:apply-templates select="filg.reg.b | filg/filg.reg.b" />
			<xsl:apply-templates select="following-sibling::notes.b"/>
		</table>
	</xsl:template>

	<xsl:template match="filg.d.b">
		<xsl:apply-templates select="filg.d"/>
	</xsl:template>

	<!-- Filing Date -->
	<xsl:template match="filg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Identification Number -->
	<xsl:template match="id.nbr.b | filg.nbr.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_identificationNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Status -->
	<xsl:template match="stat.cd.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_identificationNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Type -->
	<xsl:template match="filg.reg.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Fee -->
	<xsl:template match="fee.b">
		<xsl:apply-templates select="fee"/>
	</xsl:template>

	<xsl:template match="fee">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingFee;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Record Type -->
	<xsl:template match="rec.typ.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_recordType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Term -->
	<xsl:template match="term.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingTerm;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="term">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]>Years</xsl:text>
	</xsl:template>

	<!-- Record Type -->
	<xsl:template match="bus.typ.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="aband.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateFilingAbandoned;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- 
	********************************************************************** 
	**********************  (E) OWNER INFORMATION ************************
	**********************************************************************	
	-->

	<!-- added prin here so that we get the owner info in case it is in the prin.info block instead of reg.agt.info or cont.info.b -->
	<xsl:template match="prin">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="cont.info.b">
		<xsl:apply-templates select="cont.na.b | cont.nme.b | cont.ti.b/cont.ti | cont.addr.b" />
	</xsl:template>

	<!-- Name -->
	<xsl:template match="cont.na.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="prefixName" select="sal"/>
			<xsl:with-param name="firstName" select="first.nm"/>
			<xsl:with-param name="lastName" select="last.nm"/>
			<xsl:with-param name="suffixName" select="suf"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cont.nme.b">
		<xsl:apply-templates select="cont.nme"/>
		<xsl:if test="not(normalize-space(cont.nme))">
			<xsl:apply-templates select="cont.na.b"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="con.addr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_address;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Title -->
	<xsl:template match="cont.ti">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_title;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address -->
	<xsl:template match="agt.addr.b | addr.b | cont.addr | filg.addr.b | bus.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="str | agt.str | cont.str | filg.str | bus.str"/>
			<xsl:with-param name="streetLineTwo" select="str2 | agt.str2 | cont.str2 | filg.str2 | bus.str2"/>
			<xsl:with-param name="city" select="cty | agt.cty | cont.cty | filg.cty | bus.cty"/>
			<xsl:with-param name="stateOrProvince" select="st | agt.st | cont.st | filg.st | st.abbr"/>
			<xsl:with-param name="zip" select="post.cd | post.cd.b/post.cd | agt.zip | agt.zip.b/agt.zip | cont.zip | filg.zip | filg.zip.b/filg.zip | bus.zip"/>
			<xsl:with-param name="zipExt" select="post.cd.b/post.cd.ext | agt.zip.b/agt.zip.ext | filg.zip.b/filg.zip.ext | bus.zip.ext"/>
			<xsl:with-param name="carrierRoute" select="car.cd"/>
			<xsl:with-param name="country" select="cntry | agt.cntry | filg.cntry"/>
		</xsl:call-template>
		<xsl:apply-templates select="cnty.b/cnty"/>
	</xsl:template>

</xsl:stylesheet>
