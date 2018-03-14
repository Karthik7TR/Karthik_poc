<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>

	<!--BOAT RECORD - BOATS-ALL-->
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:preserve-space elements="*"/>

	<!--
		Desired output view:
			content    - renders document (default)
	-->

	<xsl:variable name="pValue" select="normalize-space(/Document/n-docbody/r/p)" />

	<!-- Do not render these nodes -->
	<xsl:template match="p|pc|c|col.key|legacy.id|pub.d|dppa|car.cd|l|coll.nm|dis.d|label|pubid|map|flag|morg.nm|cntry"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">		
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsBoatRegistrationClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:variable name="sectionTitle">
			<xsl:choose>
				<xsl:when test="$pValue='Watercraft'">
					<xsl:text>&pr_watercraftRecord;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_boatRegistrationRecord;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'"/>
			<xsl:with-param name="contents" select="$sectionTitle"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
			<xsl:with-param name="updateFrequencyValue" select="'&pr_monthly;'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="registnt.info.b"/>
		<xsl:apply-templates select="registnt.info"/>
		<xsl:if test="$pValue='Watercraft'">
			<xsl:apply-templates select="regis.info"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="regis.info.b"/>
		<xsl:if test="not($pValue='Watercraft')">
			<xsl:apply-templates select="regis.info"/>
		</xsl:if>
		<xsl:apply-templates select="wtrcrft.info.b"/>
		<xsl:apply-templates select="wtrcrft.info"/>
	</xsl:template>

	<!-- ********************************************************************** 
	*************** (B1)"Owner/Registrant Information" section *****************
	************************************************************************-->

	<xsl:template match="registnt.info | registnt.info.b">
		<xsl:variable name="subheader">
			<xsl:choose>
				<xsl:when test="$pValue='Watercraft'">
					<xsl:text>&pr_registrantInformation;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_ownerOrRegistrantInformation;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="$subheader"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Registration:Name-->
	<xsl:template match="nm.b[not(normalize-space(persons.nm))] | persons.nm">
		<xsl:choose>
			<xsl:when test="normalize-space(first.nm) or normalize-space(last.nm)">
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="'&pr_name;'"/>
					<xsl:with-param name="firstName" select="first.nm"/>
					<xsl:with-param name="middleName" select="mid.nm"/>
					<xsl:with-param name="lastName" select="last.nm"/>
					<xsl:with-param name="suffixName" select="suffix"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
					<xsl:apply-templates select="full.nm"/>
					<xsl:apply-templates select="co.nm"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="full.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_name;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="co.nm[normalize-space(co.nm)]">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Address-->
	<xsl:template match="mail.addr | mail.addr.b">
		<xsl:choose>
			<xsl:when test="prov = 'UNSPECIFIED'">
				<xsl:call-template name="wrapPublicRecordsAddress">
					<xsl:with-param name="street" select="str | str.b"/>
					<xsl:with-param name="city" select="cty | vsl.bld.cty"/>
					<xsl:with-param name="stateOrProvince" select="st[preceding-sibling::cty]"/>
					<xsl:with-param name="zip" select="zip.b/zip | zip"/>
					<xsl:with-param name="zipExt" select="zip.b/zip.ext[preceding-sibling::zip]"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsAddress">
					<xsl:with-param name="street" select="str | str.b"/>
					<xsl:with-param name="city" select="cty | vsl.bld.cty"/>
					<xsl:with-param name="stateOrProvince" select="st[preceding-sibling::cty] | prov[preceding-sibling::cty]"/>
					<xsl:with-param name="zip" select="zip.b/zip | zip"/>
					<xsl:with-param name="zipExt" select="zip.b/zip.ext[preceding-sibling::zip]"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="str[normalize-space(.) and parent::str.b]">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--County-->
	<xsl:template match="cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******************  (B2)"Registrant Information" section  *****************
	************************************************************************-->
	<xsl:template match="org.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_typeOfOwnership;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	******************** (C) Registration Information *************************
	************************************************************************-->

	<xsl:template match="regis.info.b | regis.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_registrationInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Registration Number-->
	<xsl:template match="regis.info.b/off.vsl.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_registrationNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Registration State-->
	<xsl:template match="reg.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_registrationState;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Registration Date-->
	<xsl:template match="reg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_registrationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Hull ID Number-->
	<xsl:template match="hull.id">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hullIdNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Certifacation:Status-->
	<xsl:template match="cod.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_certificateStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Certifacation:Issue Date-->
	<xsl:template match="cod.iss.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_certificateIssueDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Certifacation:Expiration Date-->
	<xsl:template match="cod.exp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_certificateExpirationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hail.port.b">
		<xsl:apply-templates select="hail.port"/>
		<xsl:choose>
			<xsl:when test="child::hail.port.st">
				<xsl:apply-templates select="hail.port.st"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="hail.port.prov"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Port:Hailing Port-->
	<xsl:template match="hail.port">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hailingPort;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Port:Hailing Port State-->
	<xsl:template match="hail.port.st | hail.port.prov">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hailingPortAddress;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="home.port.b">
		<xsl:apply-templates select="home.port.nm"/>
		<xsl:choose>
			<xsl:when test="child::home.port.st">
				<xsl:apply-templates select="home.port.st"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="home.port.prov"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!--Home Port Name-->
	<xsl:template match="home.port.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_homePortName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Home Port Address-->
	<xsl:template match="home.port.st | home.port.prov">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_homePortAddress;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel:Name-->
	<xsl:template match="vsl.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vesselName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel:Official Port-->
	<xsl:template match="regis.info/off.vsl.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vesselOfficialNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel:Call Sign-->
	<xsl:template match="call.sign">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_callSign;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--License:Type-->
	<xsl:template match="trade.lic.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_tradeLicenseType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Service:Type-->
	<xsl:template match="vsl.srvc.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vesselServiceType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Home Port-->
	<xsl:template match="home.port.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_portOfDocumentation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--IMO Number-->
	<xsl:template match="imo.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_imoNumber;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	********************** (D1) Vessel Information *****************************
	************************************************************************-->

	<xsl:template match="wtrcrft.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_vesselInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Propeller Type-->
	<xsl:template match="prop.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_propellerType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Hull Material-->
	<xsl:template match="hull.mat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hullMaterial;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Fuel Type-->
	<xsl:template match="fuel.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fuelType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Use Type-->
	<xsl:template match="vsl.srvc.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_useType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Make-->
	<xsl:template match="make">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_makeName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Length-->
	<xsl:template match="reg.length[normalize-space(.)]">
		<xsl:call-template name="wrapMeasurementInFeet">
			<xsl:with-param name="label" select="'&pr_registeredLength;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="wrapMeasurementInFeet">
		<xsl:param name="label"/>
		<tr class="&pr_item;">
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="$label"/>
			</xsl:call-template>
			<td>
				<xsl:apply-templates select="text()"/>
				<xsl:text><![CDATA[ ]]>&pr_feet;<![CDATA[ ]]></xsl:text>
			</td>
		</tr>
	</xsl:template>

	<!-- ********************************************************************** 
	********************* (D2) Watercraft Information *************************
	************************************************************************-->
	<xsl:template match="wtrcrft.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_watercraftInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Vessel Information:Hull Design-->
	<xsl:template match="hull.dsn.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hullDesign;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel Information:Hull Number-->
	<xsl:template match="hull.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hullNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel Information:Self Propelled-->
	<xsl:template match="self.prop.ind">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_selfPropelledIndicator;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel Information:Horsepower-->
	<xsl:template match="hrsepwr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_horsepower;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel Information:Horsepower Ahead-->
	<xsl:template match="hp.ahead">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mainHorsepowerAhead;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel Information:Horsepower Stern-->
	<xsl:template match="hp.astern">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mainHorsepowerStern;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel Information:Propulsion Type-->
	<xsl:template match="prop.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_propulsionType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel Information:Build Year-->
	<xsl:template match="bld.yr.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_buildYear;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel Information:Build Year-->
	<xsl:template match="vsl.bld.yr">
		<xsl:choose>
			<xsl:when test="parent::wtrcrft.info.b">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_modelYear;'"/>
					<xsl:with-param name="nodeType" select="$DATE"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_buildYear;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Vessel Information:Build Place-->
	<xsl:template match="vsl.bld.pl.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vesselBuildPlace;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Vessel Information:Hull Build Place-->
	<xsl:template match="hull.bld.pl.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_hullBuildPlace;'"/>
			<xsl:with-param name="city" select="hull.bld.cty | vsl.bld.cntry"/>
			<xsl:with-param name="stateOrProvince" select="hull.bld.st | hull.bld.prov | vsl.bld.st | vsl.bld.prov"/>
			<xsl:with-param name="country" select="hull.bld.cntry | vsl.bld.cntry"/>
		</xsl:call-template>
	</xsl:template>

	<!--Hull Configuration-->
	<xsl:template match="hull.cnf">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hullConfiguration;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Hull Shape-->
	<xsl:template match="hull.shp">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hullShape;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Build Country-->
	<xsl:template match="bld.cntry.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_buildCountry;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Yard Built-->
	<xsl:template match="ship.yd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_yardBuilt;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Ship Builder-->
	<xsl:template match="ship.build">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_shipBuilder;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Registered Gross-->
	<xsl:template match="reg.grs.tons">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_registeredGrossTons;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--ITC:Gross Tons-->
	<xsl:template match="itc.gross.tons">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_itcGrossTons;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Registered Net Tons-->
	<xsl:template match="reg.net.tons">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_registeredNetTons;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--ITC:Net Tons-->
	<xsl:template match="itc.net.tons">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_itcNetTons;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--ITC:Length-->
	<xsl:template match="itc.length">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_itcLength;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Registered Width-->
	<xsl:template match="reg.breadth">
		<xsl:call-template name="wrapMeasurementInFeet">
			<xsl:with-param name="label" select="'&pr_registeredWidth;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--ITC:Width-->
	<xsl:template match="itc.breadth">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_itcWidth;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Registered Depth-->
	<xsl:template match="reg.depth">
		<xsl:call-template name="wrapMeasurementInFeet">
			<xsl:with-param name="label" select="'&pr_registeredDepth;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--ITC:Depth-->
	<xsl:template match="itc.depth">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_itcDepth;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="morg.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_measuringOrganizationName;'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>