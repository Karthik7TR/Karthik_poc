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
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Do not render these nodes -->
	<xsl:template match="legacy.id|col.key|p|pc|birth.d"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsMotorVehicleClass;'" />
			<xsl:with-param name="dualColumn" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_motorVehicleRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsMainColumn">
		<!--  Source Information  -->
		<xsl:apply-templates select="$coverage-block"/>		
		
		<!-- Vehicle Information section -->
		<xsl:apply-templates select="veh.info.b"/>
		<xsl:call-template name="RegistrationInfo"/>
		<xsl:call-template name="TitleInfo"/>
		<xsl:call-template name="LienHolderInfo"/>

		<!-- Historical Information section -->
		<xsl:apply-templates select="hist.dmv.b"/>
	</xsl:template>

	<!--MOTOR VEHICLE RECORD - DMV-ALL-->
	<xsl:template match="veh.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_vehicleInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:call-template name="VehicleInfo" />
		</table>
	</xsl:template>

	<xsl:template name="VehicleInfo">
		<xsl:apply-templates select="vin"/>
		<xsl:apply-templates select="veh.typ"/>
		<xsl:apply-templates select="mdl.yr"/>
		<xsl:apply-templates select="mk"/>
		<xsl:apply-templates select="style"/>

		<xsl:call-template name="ModelSeries">
			<xsl:with-param name="Model" select="mdl"/>
			<xsl:with-param name="Series" select="ser"/>
		</xsl:call-template>

		<xsl:apply-templates select="fst.clr | color"/>
		<xsl:apply-templates select="sec.clr"/>
		<xsl:if test="contains(veh.typ, 'HEAVY TRUCK') or contains(veh.typ, 'TRAILER')">
			<xsl:apply-templates select="veh.wgt"/>
			<xsl:apply-templates select="veh.lngth"/>
			<xsl:apply-templates select="axl.nbr"/>
		</xsl:if>
		<!--  from 550 type -->
		<xsl:apply-templates select="lic.plt"/>
		<xsl:apply-templates select="plt.st"/>
		<xsl:apply-templates select="renew.d"/>
		<xsl:apply-templates select="exp.d"/>
	</xsl:template>

	<xsl:template name="RegistrationInfo">
		<xsl:if test="normalize-space(regist.info.b) or normalize-space(own.info.b)">
			<!-- for 667 type -->
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_registrationInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:for-each select="regist.info.b">
					<xsl:apply-templates select="lic.info.b/lic.plt"/>
					<xsl:apply-templates select="lic.info.b/dcl.nbr"/>
					<xsl:apply-templates select="lic.info.b/lic.plt.st"/>
					<xsl:apply-templates select="lic.info.b/plt.typ"/>
					<xsl:apply-templates select="lic.info.b/prev.lic.plt"/>
					<xsl:apply-templates select="lic.info.b/prev.lic.plt.st"/>
					<xsl:apply-templates select="own.info.b/renew.d"/>
					<xsl:apply-templates select="own.info.b/exp.d"/>
					<xsl:apply-templates select="own.info.b/reg.d"/>
					<xsl:apply-templates select="own.info.b/na.b | own.info.b/org.na"/>
					<xsl:apply-templates select="own.info.b/reg.dob"/>
					<xsl:apply-templates select="own.info.b/own.typ"/>
					<xsl:apply-templates select="own.info.b/nm.typ"/>
					<xsl:apply-templates select="own.info.b/mail.addr.b"/>
					<xsl:apply-templates select="own.info.b/mail.addr.b/cnty"/>
					<xsl:apply-templates select="own.info.b/phy.addr.b"/>
					<xsl:apply-templates select="own.info.b/phy.addr.b/cnty"/>
				</xsl:for-each>

				<!-- for 550 type -->
				<xsl:for-each select="own.info.b">
					<xsl:apply-templates select="reg.d"/>
					<xsl:apply-templates select="na.b | org.na"/>
					<xsl:apply-templates select="dob"/>
					<xsl:apply-templates select="own.typ"/>
					<xsl:apply-templates select="nm.typ"/>
					<xsl:apply-templates select="mail.addr"/>
					<xsl:apply-templates select="mail.addr/cnty"/>
					<xsl:apply-templates select="phy.addr"/>
					<xsl:apply-templates select="phy.addr/cnty"/>
				</xsl:for-each>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="TitleInfo">
		<xsl:if test="normalize-space(ttl.hist.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_titleInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:for-each select="ttl.hist.info.b/ttl.b">
					<xsl:apply-templates select="orig.ttl.d"/>
					<xsl:apply-templates select="ttl.nbr"/>
				</xsl:for-each>

				<xsl:for-each select="ttl.hist.info.b">
					<xsl:apply-templates select="ttl.info.b/na.b"/>
					<xsl:apply-templates select="ttl.info.b/org.na"/>
					<xsl:apply-templates select="ttl.info.b/own.typ"/>
					<xsl:apply-templates select="ttl.info.b/nm.typ"/>
					<xsl:apply-templates select="ttl.info.b/ttl.tran.d"/>
					<xsl:apply-templates select="ttl.info.b/mail.addr.b"/>
					<xsl:apply-templates select="ttl.info.b/mail.addr.b/cnty"/>
					<xsl:apply-templates select="ttl.info.b/phy.addr.b"/>
					<xsl:apply-templates select="ttl.info.b/phy.addr.b/cnty"/>
				</xsl:for-each>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- **********************************************************************
	***************** (E)"Lien Holder Information" section ********************
	************************************************************************-->
	<xsl:template name="LienHolderInfo">
		<xsl:if test="normalize-space(lienhold.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_lienHolderInfo;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:for-each select="lienhold.info.b">
					<xsl:apply-templates select="lienhold.b/na.b"/>
					<xsl:apply-templates select="lienhold.b/org.na"/>
					<xsl:apply-templates select="lienhold.b/own.typ"/>
					<xsl:apply-templates select="lienhold.b/nm.typ"/>
					<xsl:apply-templates select="lienhold.b/mail.addr.b"/>
					<xsl:apply-templates select="lienhold.b/mail.addr.b/cnty"/>
					<xsl:apply-templates select="lienhold.b/phy.addr.b"/>
					<xsl:apply-templates select="lienhold.b/phy.addr.b/cnty"/>
				</xsl:for-each>
			</table>
		</xsl:if>
	</xsl:template>

	<!--  VIN   -->
	<xsl:template match="vin">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vin;'"/>
			<xsl:with-param name="nodeType" select="$VINNUMBER"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Vehicle Type   -->
	<xsl:template match="veh.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vehicleType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Model Year   -->
	<xsl:template match="mdl.yr">
		<tr class="&pr_item;">
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_modelYear;'" />
			</xsl:call-template>
			<td>
				<xsl:value-of select="substring(., 1, 4)"/>
			</td>
		</tr>
	</xsl:template>

	<!--  Make   -->
	<xsl:template match="mk">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_make;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Body Style   -->
	<xsl:template match="style">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bodyStyle;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Model/Series   -->
	<xsl:template name="ModelSeries">
		<xsl:param name="Model" select="/.." />
		<xsl:param name="Series" select="/.." />

		<xsl:if test="$Model or $Series">
			<tr class="&pr_item;">
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="contents" select="'&pr_modelSeries;'" />
				</xsl:call-template>
				<td>
					<xsl:if test="$Model">
						<xsl:apply-templates select="$Model" />
						<xsl:text><![CDATA[ ]]></xsl:text>
					</xsl:if>
					<xsl:if test="$Series">
						<xsl:apply-templates select="$Series" />
					</xsl:if>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!--  Color   -->
	<xsl:template match="color">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_color;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Primary Color   -->
	<xsl:template match="fst.clr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_primaryColor;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Secondary Color   -->
	<xsl:template match="sec.clr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_secondaryColor;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Licence Plate   -->
	<xsl:template match="lic.plt | hist.lic.plt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licensePlate;'"/>
			<xsl:with-param name="nodeType" select="$LICENSEPLATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Decal Number   -->
	<xsl:template match="dcl.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_decalNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Plate State   -->
	<xsl:template match="plt.st | lic.plt.st | hist.lic.plt.st | hist.plt.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_issuingState;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Plate Type   -->
	<xsl:template match="plt.typ | hist.plt.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_plateType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Previous Plate Number   -->
	<xsl:template match="prev.lic.plt | hist.prev.lic.plt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousPlateNumber;'"/>
			<xsl:with-param name="nodeType" select="$LICENSEPLATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Previous Plate State   -->
	<xsl:template match="prev.lic.plt.st | hist.prev.lic.plt.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousPlateState;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Renewed Date   -->
	<xsl:template match="renew.d | hist.renew.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel"           select="'&pr_registrationRenewalDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Expiration Date   -->
	<xsl:template match="exp.d | hist.exp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel"           select="'&pr_expirationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Vehicle Weight   -->
	<xsl:template match="veh.wgt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vehicleWeight;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Vehicle Length   -->
	<xsl:template match="veh.lngth">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vehicleLength;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Axle Count   -->
	<xsl:template match="axl.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_axleCount;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Registrant since -->
	<xsl:template match="reg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_registrantSince;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Name -->
	<xsl:template match="na.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="prefixName" select="na.pre"/>
			<xsl:with-param name="firstName" select="fna"/>
			<xsl:with-param name="middleName" select="mna"/>
			<xsl:with-param name="lastName" select="lna"/>
			<xsl:with-param name="suffixName" select="na.suf"/>
			<xsl:with-param name="professionalSuffixName" select="na.prof.suf"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Historical Name -->
	<xsl:template match="hist.na.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="hist.fna"/>
			<xsl:with-param name="middleName" select="hist.mna"/>
			<xsl:with-param name="lastName" select="hist.lna"/>
			<xsl:with-param name="suffixName" select="hist.na.suf"/>
			<xsl:with-param name="professionalSuffixName" select="hist.na.prof.suf"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Organization Name -->
	<xsl:template match="org.na | hist.org.na">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Owner Type -->
	<xsl:template match="own.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ownerType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Approximate Date of Birth -->
	<xsl:template match="reg.dob | dob | hist.dob | hist.reg.dob">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_approximateDateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Interest -->
	<xsl:template match="nm.typ | hist.nm.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_interest;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Physical Address -->
	<xsl:template match="phy.addr | phy.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_physicalAddress;'"/>
			<xsl:with-param name="fullStreet" select="full.str"/>
			<xsl:with-param name="streetNum" select="hse.nbr"/>
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="streetSuffix" select="str.sfx"/>
			<xsl:with-param name="streetLineTwo" select="po.addr"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
			<xsl:with-param name="zipExt" select="zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Historical Physical Address -->
	<xsl:template match="hist.phy.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_physicalAddress;'"/>
			<xsl:with-param name="fullStreet" select="hist.full.str"/>
			<xsl:with-param name="streetNum" select="hist.hse.nbr"/>
			<xsl:with-param name="street" select="hist.str | hist.rur.box"/>
			<xsl:with-param name="streetSuffix" select="hist.str.sfx"/>
			<xsl:with-param name="streetLineTwo" select="hist.po.addr"/>
			<xsl:with-param name="city" select="hist.cty"/>
			<xsl:with-param name="stateOrProvince" select="hist.st"/>
			<xsl:with-param name="zip" select="hist.zip"/>
			<xsl:with-param name="zipExt" select="hist.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Mailing Address -->
	<xsl:template match="mail.addr | mail.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_mailingAddress;'"/>
			<xsl:with-param name="fullStreet" select="full.str"/>
			<xsl:with-param name="streetNum" select="hse.nbr"/>
			<xsl:with-param name="streetDirection" select="dir.pfx"/>
			<xsl:with-param name="street" select="str | rur.box"/>
			<xsl:with-param name="streetSuffix" select="str.sfx"/>
			<xsl:with-param name="streetDirectionSuffix" select="dir.sfx"/>
			<xsl:with-param name="streetUnitNumber" select="unt.nbr"/>
			<xsl:with-param name="streetUnit" select="unt"/>
			<xsl:with-param name="streetLineTwo" select="po.addr"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
			<xsl:with-param name="zipExt" select="zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Hist Mailing Address -->
	<xsl:template match="hist.mail.addr.b | hist.mail.addr">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_mailingAddress;'"/>
			<xsl:with-param name="fullStreet" select="hist.full.str"/>
			<xsl:with-param name="streetNum" select="hist.hse.nbr"/>
			<xsl:with-param name="street" select="hist.str"/>
			<xsl:with-param name="streetSuffix" select="hist.str.sfx"/>
			<xsl:with-param name="streetLineTwo" select="hist.po.addr"/>
			<xsl:with-param name="city" select="hist.cty"/>
			<xsl:with-param name="stateOrProvince" select="hist.st"/>
			<xsl:with-param name="zip" select="hist.zip"/>
			<xsl:with-param name="zipExt" select="hist.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- County -->
	<xsl:template match="cnty | hist.phy.addr.b/hist.cnty | hist.mail.addr.b/hist.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Original Title Date -->
	<xsl:template match="orig.ttl.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalTitleDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Title Number -->
	<xsl:template match="ttl.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_titleNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Title Transaction Date -->
	<xsl:template match="ttl.tran.d | hist.ttl.trans.d">
		<xsl:if test ="parent::ttl.info.b">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_titleTransactionDate;'"/>
				<xsl:with-param name="nodeType" select="$DATE"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test ="parent::hist.info.b">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_titleTransferDate;'"/>
				<xsl:with-param name="nodeType" select="$DATE"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Historical Information Information section    -->
	<xsl:template match ="hist.dmv.b">
		<!-- Counts how many DMV History records there are-->
		<xsl:variable name ="numOfRecords">
			<xsl:value-of select ="count(preceding-sibling::hist.dmv.b) + 1"/>
		</xsl:variable>
		<xsl:call-template name="wrapWithPublicRecordsSectionMultiContent">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_historicalDMVRecord;'"/>
			<xsl:with-param name="contents2" select="$numOfRecords"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:for-each select="hist.info.b">
				<xsl:apply-templates select="hist.renew.d"/>
				<xsl:apply-templates select="hist.exp.d"/>
				<xsl:apply-templates select="hist.ttl.trans.d"/>
				<xsl:apply-templates select="hist.lic.info.b/hist.lic.plt"/>
				<xsl:apply-templates select="hist.lic.info.b/hist.lic.plt.st"/>
				<xsl:apply-templates select="hist.lic.info.b/hist.plt.typ"/>
				<xsl:apply-templates select="hist.lic.info.b/hist.prev.lic.plt"/>
				<xsl:apply-templates select="hist.lic.info.b/hist.prev.lic.plt.st"/>
				<xsl:apply-templates select="hist.own.info.b/hist.na.b | hist.own.info.b/hist.org.na"/>
				<xsl:apply-templates select="hist.own.info.b/hist.reg.dob"/>
				<xsl:apply-templates select="hist.own.info.b/hist.nm.typ"/>
				<xsl:apply-templates select="hist.own.info.b/hist.mail.addr.b"/>
				<xsl:apply-templates select="hist.own.info.b/hist.mail.addr.b/hist.cnty"/>
				<xsl:apply-templates select="hist.own.info.b/hist.phy.addr.b"/>
				<xsl:apply-templates select="hist.own.info.b/hist.phy.addr.b/hist.cnty"/>
			</xsl:for-each>
		</table>
	</xsl:template>

</xsl:stylesheet>