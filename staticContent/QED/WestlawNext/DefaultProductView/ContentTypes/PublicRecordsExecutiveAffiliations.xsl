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

	<!-- Executive Affiliations -->
	<!--
		Combined People Records - ExecutiveAffiliations has the following products -
		(1) Executive Affiliation (Component: EA-ALL)
		(2) Fictitious Business Name (Component: FBN)
		(3) Executive Bios (Component: EXEC-BIOS)
		(4) Corporate (Component: CORP-ALL)
		(5) Business Finder (Component: BUSFINDCANADA, BUSFINDUS)
	-->
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--Variables-->
	<!-- Stores the name of the collection. Used to determine whether this document is unreported and therefore should not change between weblinks and web2-->
	<xsl:variable name ="collection" select="/Document/map/entry[key = 'md.collection']/value" />

	<!-- Do not render these nodes -->
	<xsl:template match="map|c|s|col.key|agt.d.b|agt.stat.b|l|filg.zip.ext"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsExecutiveAffiliationsClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader" priority="1">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_executiveAffiliationRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn" priority="1">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="exaff.b/exec.info.b"/>
		<xsl:apply-templates select="cont.info.b[1]"/>
		<xsl:apply-templates select="exec.info.b"/>
		<xsl:apply-templates select="prin.info/prin.info.b | reg.agt.info[not(normalize-space(preceding-sibling::prin.info/prin.info.b)
																																or normalize-space(following-sibling::prin.info/prin.info.b))]"/>
		<xsl:apply-templates select="nm.info.b|con.loc.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn" priority="1">
		<xsl:apply-templates select="exaff.b/co.info.b"/>
		<xsl:apply-templates select="co.info.b"/>
		<xsl:apply-templates select="bus.info.b"/>
		<xsl:apply-templates select="name.info"/>
		<xsl:apply-templates select="filg.info.b"/>
		<xsl:apply-templates select="filg.info"/>
		<xsl:apply-templates select="cn.info.b"/>

		<xsl:call-template name="outputOrderDocumentsSection"/>
		<xsl:call-template name="wrapPublicRecordsDisclaimers">
			<xsl:with-param name="disclaimer1">
				<xsl:text>THE PRECEDING PUBLIC RECORD DATA IS FOR INFORMATION PURPOSES ONLY AND IS NOT THE OFFICIAL RECORD. CERTIFIED COPIES CAN ONLY BE OBTAINED FROM THE OFFICIAL SOURCE.</xsl:text>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*********************** VIEW: Executive Affiliation ***********************
	************************************************************************-->

	<xsl:template match="exn.b[not(normalize-space(nm.b))]">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label">
				<xsl:choose>
					<xsl:when test="normalize-space(l)">
						<xsl:value-of select="l"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'&pr_executiveName;'"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="prefixName" select="nm.pre"/>
			<xsl:with-param name="firstName" select="full.exec.nm"/>
			<xsl:with-param name="suffixName" select="suf.nm"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Executive Name -->
	<xsl:template match="exn.b | cont.na.b | na.b | con.na.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label">
				<xsl:choose>
					<xsl:when test="normalize-space(l)">
						<xsl:value-of select="l"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'&pr_executiveName;'"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="prefixName" select="nm.pre | sal | na.pre"/>
			<xsl:with-param name="firstName" select="first.nm | fna | nm.b/first.nm"/>
			<xsl:with-param name="middleName" select="mna | nm.b/mid.nm"/>
			<xsl:with-param name="lastName" select="last.nm | lna | nm.b/last.nm"/>
			<xsl:with-param name="suffixName" select="suf.nm | suf | na.suf"/>
			<xsl:with-param name="professionalSuffixName" select="pro.ttl.cd"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Executive Title -->
	<xsl:template match="full.exec.ttl | ttl.cd | ttl.cdc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_executiveTitle;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ******************************** -->
	<!-- *** (B) BUSINESS INFORMATION *** -->
	<!-- ******************************** -->
	<xsl:template match="co.info.b">
		<xsl:if test="normalize-space(co.nm.b) or normalize-space(addr.b) or normalize-space(phn.b)
								 or normalize-space(trd.sty.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_businessInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="co.nm.b | co.nm"/>
				<xsl:apply-templates select="addr.b | addr"/>
				<xsl:apply-templates select="cnty"/>
				<xsl:apply-templates select="phn.b | phn"/>
				<xsl:apply-templates select="trd.sty.b"/>
			</table>
		</xsl:if>
		<xsl:if test="normalize-space(co.name.b) or normalize-space(filg.addr.b)
							or normalize-space(../name.info/bus.name.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_businessInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="co.name.b"/>
				<xsl:apply-templates select="filg.addr.b"/>
				<xsl:apply-templates select="../name.info.bus.name.b"/>
			</table>
		</xsl:if>
		<xsl:if test="normalize-space(duns.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_otherInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="duns.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Business Address -->
	<xsl:template match="addr.b | addr[name(..)!='addr.b']">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label">
				<xsl:choose>
					<xsl:when test="name()='addr.b' and name(..)='prin'">
						<xsl:value-of select="'&pr_principalAddress;'"/>
					</xsl:when>
					<xsl:when test="normalize-space(l) or name()='addr' or (name()='addr.b' and name(..)='cn.info.b')">
						<xsl:value-of select="'&pr_businessAddress;'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'&pr_executiveAddress;'"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="fullStreet" select="addr | addrc | str"/>
			<xsl:with-param name="streetLineTwo" select="str2"/>
			<xsl:with-param name="city" select="cty | ctyc"/>
			<xsl:with-param name="stateOrProvince" select="st | st.abbr | provc"/>
			<xsl:with-param name="zip" select="zip | post.cd[not(normalize-space(post.cd.b/post.cd))] | post.cd.b/post.cd
							| canzip.cd | zip.b/zip.5.cd"/>
			<xsl:with-param name="zipExt" select="post.cd.b/post.cd.ext | zip.b/zip.4.cd"/>
			<xsl:with-param name="carrierRoute" select="car.cd"/>
			<xsl:with-param name="country" select="ctry"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone -->
	<xsl:template match="phn.b">
		<!-- The check for 'phn' below is to filter out any phone that may have a ctry.acc.cd without a phone number. -->
		<xsl:if test="normalize-space(phn)">
			<xsl:variable name="phone">
				<xsl:apply-templates select="ctry.acc.cd"/>
				<xsl:apply-templates select="phn"/>
			</xsl:variable>
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_phone;'"/>
				<xsl:with-param name="selectNodes" select="$phone"/>
				<xsl:with-param name="nodeType" select="$PHONE"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ctry.acc.cd[normalize-space(.) and normalize-space(following-sibling::phn)]">
		<xsl:apply-templates/>
		<xsl:text>-</xsl:text>
	</xsl:template>

	<!-- Trade Style Name -->
	<xsl:template match="trd.sty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_tradeStyleName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ***************************** -->
	<!-- *** (C) OTHER INFORMATION *** -->
	<!-- ***************************** -->

	<xsl:template match="duns.b">
		<xsl:apply-templates select="duns"/>
	</xsl:template>

	<xsl:template match="bus.duns.b">
		<xsl:apply-templates select="bus.duns"/>
	</xsl:template>

	<!-- DUNS -->
	<xsl:template match="duns | bus.duns">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_duns;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	********************* VIEW: Fictitious Business Name **********************
	************************************************************************-->

	<!-- ********************************* -->
	<!-- *** (A) EXECUTIVE INFORMATION *** -->
	<!-- ********************************* -->
	<xsl:template match="cont.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_executiveInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<!-- Executive Name -->
			<xsl:apply-templates select="cont.nme.b"/>
			<!-- Executive Address -->
			<xsl:apply-templates select="cont.addr.b"/>
		</table>
	</xsl:template>

	<xsl:template match="cont.nme.b">
		<xsl:apply-templates select="cont.nme | cont.na.b"/>
	</xsl:template>

	<!--Executive:Name-->
	<xsl:template match="cont.nme">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_executiveName;'"/>
		</xsl:call-template>
	</xsl:template>


	<!--Executive:Address-->
	<xsl:template match="cont.addr.b">
		<xsl:apply-templates select="cont.addr"/>
		<xsl:if test="not(normalize-space(cont.addr))">
			<xsl:apply-templates select="con.addr"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="cont.addr">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_executiveAddress;'"/>
			<xsl:with-param name="street" select="cont.str"/>
			<xsl:with-param name="streetLineTwo" select="cont.str2"/>
			<xsl:with-param name="city" select="cont.cty"/>
			<xsl:with-param name="stateOrProvince" select="cont.st"/>
			<xsl:with-param name="zip" select="cont.zip"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="con.addr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_executiveAddress;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ******************************** -->
	<!-- *** (B) BUSINESS INFORMATION *** -->
	<!-- ******************************** -->
	<xsl:template match="bus.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_businessInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="bus.nm.b"/>
			<xsl:apply-templates select="bus.addr.b"/>
			<xsl:apply-templates select="bus.phn.b"/>
			<xsl:apply-templates select="sic.b"/>
		</table>
	</xsl:template>

	<!--Business:Name-->
	<xsl:template match="co.name.b">
		<xsl:apply-templates select="co.name"/>
	</xsl:template>

	<!-- Business Name -->
	<xsl:template match="bus.nm | co.nm | co.nmc | co.name">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_businessName;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Address -->
	<xsl:template match="bus.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_businessAddress;'"/>
			<xsl:with-param name="street" select="bus.str"/>
			<xsl:with-param name="streetLineTwo" select="bus.str2"/>
			<xsl:with-param name="city" select="bus.cty"/>
			<xsl:with-param name="stateOrProvince" select="st.abbr"/>
			<xsl:with-param name="zip" select="bus.zip"/>
			<xsl:with-param name="zipExt" select="bus.zip.ext"/>
			<xsl:with-param name="carrierRoute" select="car.cd"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone -->
	<xsl:template match="bus.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Primary SIC Code -->
	<xsl:template match="sic.cd[normalize-space(c.nbr) or normalize-space(c.desc)] | prm.sic.b[normalize-space(c.nbr) or normalize-space(c.desc)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_primarySicCode;'"/>
			<xsl:with-param name="selectNodes" select="c.nbr | c.desc"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="c.nbr[normalize-space(.)]">
		<xsl:apply-templates/>
		<xsl:if test="normalize-space(following-sibling::c.desc)">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- ***************************** -->
	<!-- *** (C) OTHER INFORMATION *** -->
	<!-- ***************************** -->
	<xsl:template match="filg.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_otherInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="filg.d.b"/>
			<xsl:apply-templates select="bus.typ.b"/>
			<xsl:apply-templates select="filg.nbr.b"/>
		</table>
	</xsl:template>

	<!-- Filing Date -->
	<xsl:template match="filg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="filg.reg.b">
		<xsl:apply-templates select="filg.reg"/>
	</xsl:template>

	<!-- Business/Filing Type -->
	<xsl:template match="bus.typ | filg.reg[normalize-space(.)] | ind.cd[normalize-space(.)] | ind.cdc[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessOrFilingType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Identification No. -->
	<xsl:template match="filg.nbr | id.nbr[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_identificationNumber;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	*************************** VIEW: Executive Bios **************************
	************************************************************************-->

	<!-- ********************************* -->
	<!-- *** (A) EXECUTIVE INFORMATION *** -->
	<!-- ********************************* -->
	<xsl:template match="exec.info.b[name(..)='exaff.b']">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_executiveInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="exn.b"/>
			<xsl:apply-templates select="ttl.b"/>
		</table>
	</xsl:template>

	<xsl:template match="exec.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_executiveInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="na.b"/>
			<xsl:apply-templates select="ttl.b"/>
		</table>
	</xsl:template>

	<xsl:template match="ttl.b">
		<xsl:apply-templates select="full.exec.ttl"/>
	</xsl:template>
	
	<!-- County -->
	<xsl:template match="cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone -->
	<xsl:template match="phn[name(..)!='phn.b'] | ph.nbr | ph.nbrc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	***************************** VIEW: CORPORATE *****************************
	************************************************************************-->

	<xsl:template match="prin.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_executiveInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
			<xsl:apply-templates select="../../reg.agt.info"/>
		</table>
	</xsl:template>

	<xsl:template match="reg.agt.info[not(normalize-space(preceding-sibling::prin.info/prin.info.b) or normalize-space(following-sibling::prin.info/prin.info.b))]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_executiveInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Principal Information -->
	<xsl:template match="prin">
		<xsl:apply-templates select="off.name.b"/>
		<xsl:apply-templates select="ti.b"/>
		<xsl:apply-templates select="addr.b"/>
	</xsl:template>

	<!--Principal:Name-->
	<xsl:template match="off.name">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_principalName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Principal:Title-->
	<xsl:template match="ti.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_principalTitle;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Agent:Name-->
	<xsl:template match="agt.name">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_registeredAgent;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Agent:Address-->
	<xsl:template match="agt.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_registeredAgentAddress;'"/>
			<xsl:with-param name="street" select="agt.str"/>
			<xsl:with-param name="streetLineTwo" select="agt.str2"/>
			<xsl:with-param name="city" select="agt.cty"/>
			<xsl:with-param name="stateOrProvince" select="agt.st"/>
			<xsl:with-param name="zip" select="agt.zip[not(normalize-space(agt.zip.b/agt.zip))] | agt.zip.b/agt.zip"/>
			<xsl:with-param name="zipExt" select="agt.zip.b/agt.zip.ext"/>
			<xsl:with-param name="country" select="agt.cntry"/>
		</xsl:call-template>
	</xsl:template>

	<!--Business:Address-->
	<xsl:template match="filg.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_businessAddress;'"/>
			<xsl:with-param name="street" select="filg.str"/>
			<xsl:with-param name="streetLineTwo" select="filg.str2"/>
			<xsl:with-param name="city" select="filg.cty"/>
			<xsl:with-param name="stateOrProvince" select="filg.st"/>
			<xsl:with-param name="zip" select="filg.zip[not(normalize-space(filg.zip.b/filg.zip))] | filg.zip.b/filg.zip"/>
			<xsl:with-param name="zipExt" select="filg.zip.b/filg.zip.ext"/>
			<xsl:with-param name="country" select="filg.cntry"/>
		</xsl:call-template>
	</xsl:template>

	<!--Former Name-->
	<xsl:template match="bus.name.b[normalize-space(bus.name)]">
		<xsl:variable name="label">
			<xsl:apply-templates select="name.t"/>
			<xsl:value-of select="'&pr_name;'"/>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="selectNodes" select="bus.name"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="name.t">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>


	<!-- ***************************** -->
	<!-- *** (C) OTHER INFORMATION *** -->
	<!-- ***************************** -->
	<xsl:template match="filg.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_otherInformationSubheader;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="filg"/>
			<xsl:apply-templates select="../co.info.b/bus.duns.b"/>
			<xsl:apply-templates select="filg.nbr.b"/>
			<xsl:apply-templates select="filg/id.nbr.b"/>
		</table>
	</xsl:template>

	<xsl:template match="filg">
		<xsl:apply-templates select="filg.d.b"/>
		<xsl:apply-templates select="stat.cd.b"/>
		<xsl:apply-templates select="filg.reg.b"/>
	</xsl:template>

	<!--Filing Date-->
	<xsl:template match="filg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Status-->
	<xsl:template match="stat.cd.b[normalize-space(stat.cd)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_status;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="stat.cd">
		<xsl:choose>
			<xsl:when test="contains(node(), '-')">
				<xsl:value-of select="substring-before(node(), '-')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Filing Number-->
	<xsl:template match="filg.nbr[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	******************* VIEW: BUSFIND-USA & BUSFIND-CANADA ********************
	************************************************************************-->

	<!-- ********************************* -->
	<!-- *** (A) EXECUTIVE INFORMATION *** -->
	<!-- ********************************* -->
	<xsl:template match="nm.info.b | con.loc.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_executiveInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="con.na.b"/>
			<xsl:apply-templates select="ttl.cd"/>
			<xsl:apply-templates select="ttl.cdc"/>
		</table>
	</xsl:template>

	<!-- ******************************** -->
	<!-- *** (B) BUSINESS INFORMATION *** -->
	<!-- ******************************** -->
	<xsl:template match="cn.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_businessInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="co.nm | co.nmc"/>
			<xsl:apply-templates select="addr.b"/>
			<xsl:apply-templates select="ph.nbr | ph.nbrc"/>
			<xsl:apply-templates select="prm.sic.b"/>
			<xsl:apply-templates select="ind.cdc"/>
			<!-- Franchise/Specialty info -->
			<xsl:if test="normalize-space(frn.spec.desc)">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_franchiseOrSpecialty;'"/>
					<xsl:with-param name="selectNodes" select="frn.spec.desc"/>
				</xsl:call-template>
			</xsl:if>
		</table>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_otherInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="ind.cd"/>
		</table>
	</xsl:template>

	<xsl:template match="frn.spec.desc[position()!=1]">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

</xsl:stylesheet>
