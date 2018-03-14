<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsDate.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsMaskedData.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- This file is a common include for handling
	 1)dbsign-on=DEATH-ALL{Logical type=death}
	 2)dbsign-on=DEATH{Logical type=death}
	 3)dbsign-on=DECEASED-ALL{Logical type=deceased}
	 4)dbsign-on=DECEASED-XX{Logical type=deceased}
	 -->

	<!-- Variable(s) -->
	<xsl:variable name="pvalue" select="/Document/n-docbody/r/p"/>

	<!-- Do not render these nodes -->
	<xsl:template match="map|p|c|eff.d|s|col.key|vol.yr"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsDeathAndDeceasedClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>

		<xsl:choose>
			<xsl:when test="$pvalue='&pr_deathRecords;'">
				<xsl:apply-templates select="." mode="identify_info"/>
			</xsl:when>
			<xsl:when test="$pvalue='&pr_stateDeathRecordCaps;'">
				<xsl:apply-templates select="identify.info.b"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:choose>
			<xsl:when test="$pvalue='&pr_deathRecords;'">
				<xsl:apply-templates select="." mode="deceased_info"/>
			</xsl:when>
			<xsl:when test="$pvalue='&pr_stateDeathRecordCaps;'">
				<xsl:apply-templates select="descendant::death.info.b"/>
				<xsl:apply-templates select="descendant::deceased.info.b"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:choose>
			<xsl:when test="$pvalue='&pr_deathRecords;'">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_header;'" />
					<xsl:with-param name="contents" select="'&pr_ssaDeathRecord;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$pvalue='&pr_stateDeathRecordCaps;'">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_header;'" />
					<xsl:with-param name="contents" select="'&pr_stateDeathRecord;'" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- ********************* TEMPLATES FOR SSA DEATH RECORD(DEATH CONTENT) **************************-->
	<xsl:template match="r" mode="identify_info">
		<xsl:if test="normalize-space(na) or normalize-space(ssn.b) or normalize-space(st.issd)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_identifyingInformation;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="na"/>
				<xsl:apply-templates select="ssn.b"/>
				<xsl:apply-templates select="st.issd"/>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ssn.b">
		<xsl:apply-templates select="ssn"/>
		<xsl:apply-templates select="ssn.frag"/>
	</xsl:template>

	<!-- Full Name of a dead person -->
	<xsl:template match="na">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="first"/>
			<xsl:with-param name="middleName" select="na.mid"/>
			<xsl:with-param name="lastName" select="last"/>
			<xsl:with-param name="suffixName" select="na.suf"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Social security template -->
	<xsl:template match="ssn[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ssn;'"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
		</xsl:call-template>
	</xsl:template>

	<!-- State Where SSN Issued -->
	<xsl:template match="st.issd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateWhereSSNIssued;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	************** (C)"Information Regarding Deceased" section ****************
	************************************************************************-->
	<xsl:template match="r" mode="deceased_info">
		<xsl:if test="normalize-space(birth.d) or normalize-space(death.d) or normalize-space(age.b/age)
				or normalize-space(age.b/ver.prf) or normalize-space(lst.res) or normalize-space(pay.res)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_informationRegardingDeceased;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="birth.d"/>
				<xsl:apply-templates select="death.d"/>
				<xsl:apply-templates select="age.b"/>
				<xsl:apply-templates select="lst.res"/>
				<xsl:apply-templates select="pay.res"/>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="age.b">
		<xsl:apply-templates select="age"/>
		<xsl:apply-templates select="ver.prf"/>
	</xsl:template>

	<!--Birth Date-->
	<xsl:template match="birth.d">
		<tr>
			<th>
				<xsl:choose>
					<xsl:when test="$pvalue='&pr_deathRecords;'">
						<xsl:text>&pr_dateOfBirth;</xsl:text>
					</xsl:when>
					<xsl:when test="$pvalue='&pr_stateDeathRecordCaps;'">
						<xsl:text>&pr_birthDate;</xsl:text>
					</xsl:when>
				</xsl:choose>
			</th>
			<td>
				<xsl:choose>
					<xsl:when test="$pvalue='&pr_deathRecords;'">
						<xsl:call-template name="FormatNonSensitiveDate" />
					</xsl:when>
					<xsl:when test="$pvalue='&pr_stateDeathRecordCaps;'">
						<xsl:call-template name="FormatNonSensitiveDate" />
					</xsl:when>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!-- Date of death -->
	<xsl:template match="death.d">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_dateOfDeath;'" />
			</xsl:call-template>
			<td>
				<xsl:choose>
					<xsl:when test="$pvalue='&pr_deathRecords;'">
						<xsl:apply-templates/>
					</xsl:when>
					<xsl:when test="$pvalue='&pr_stateDeathRecordCaps;'">
						<xsl:call-template name="parseYearMonthDayDateFormat"/>
					</xsl:when>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!-- Age at Death -->
	<xsl:template match="age">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ageAtDeath;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Verification of Death -->
	<xsl:template match="ver.prf">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_verificationOfDeath;'" />
			</xsl:call-template>
			<td>
				<xsl:choose>
					<xsl:when test="(contains(.,&quot;P&quot;))">
						<xsl:text>&pr_proofOfDeathCertObserved;</xsl:text>
					</xsl:when>
					<xsl:when test="(contains(.,&quot;V&quot;))">
						<xsl:text>&pr_reportVerifiedWithFamily;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!-- Last residence -->
	<xsl:template match="lst.res">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_lastResidence;'"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.cd"/>
			<xsl:with-param name="country" select="ctry"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Payment residence -->
	<xsl:template match="pay.res">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_paymentDelivery;'"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.cd"/>
			<xsl:with-param name="country" select="ctry"/>
		</xsl:call-template>
		<tr class="&pr_item;">
			<td colspan="2">
				<xsl:text>&pr_lumpSumDeathPayment;</xsl:text>
			</td>
		</tr>
	</xsl:template>


	<!-- *********************END OF TEMPLATES FOR SSA DEATH RECORD(DEATH CONTENT) **************************-->

	<!-- ********************* TEMPLATES FOR STATE DEATH RECORD(DECEASED CONTENT) **************************-->
	<xsl:template match="identify.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_identifyingInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="nm.b"/>
			<xsl:apply-templates select="maid.nm"/>
			<xsl:apply-templates select="aka.b"/>
			<xsl:if test="death.cert.nbr or vol.nbr">
				<xsl:call-template name="DeathCertificateNumber"/>
			</xsl:if>
			<xsl:apply-templates select="ssn.b/ssn"/>
			<xsl:apply-templates select="ssn.b/ssn.frag"/>
			<xsl:apply-templates select="gender"/>
		</table>
	</xsl:template>

	<!-- Full Name of a dead person -->
	<xsl:template match="nm.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="fna"/>
			<xsl:with-param name="middleName" select="mid"/>
			<xsl:with-param name="lastName" select="lna"/>
		</xsl:call-template>
	</xsl:template>

	<!--Maiden Name of a dead person -->
	<xsl:template match="maid.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_maidenName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Alias Name of a dead person -->
	<xsl:template match="aka.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_alias;'"/>
			<xsl:with-param name="firstName" select="aka.fna"/>
			<xsl:with-param name="middleName" select="aka.mid"/>
			<xsl:with-param name="lastName" select="aka.lna"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Death Certificate Number-->
	<!--This template is used to display the Death Certificate Number and Volume Number -->
	<xsl:template name="DeathCertificateNumber">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_deathCertNumber;'" />
			</xsl:call-template>
			<td>
				<xsl:apply-templates select="death.cert.nbr"/>
				<xsl:apply-templates select="vol.nbr"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="vol.nbr">
		<xsl:if test="preceding-sibling::death.cert.nbr">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:text>VOLUME<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Gender of a dead person -->
	<xsl:template match="gender">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	************** (C)"Information Surrounding Death" section ****************
	************************************************************************-->

	<xsl:template match="death.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_informationSurroundingDeath;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="death.d"/>
			<xsl:apply-templates select="age.death.d"/>
			<xsl:if test="death.cty or death.st">
				<xsl:call-template name="DeathLocation"/>
			</xsl:if>
			<xsl:apply-templates select="death.cnty"/>
			<xsl:apply-templates select="autopsy"/>
			<xsl:apply-templates select="cause.death"/>
			<xsl:apply-templates select="manner.death"/>
			<xsl:apply-templates select="plc.acc.inj"/>
			<xsl:apply-templates select="hospital.stat"/>
			<xsl:apply-templates select="disp.body"/>
		</table>
	</xsl:template>

	<!-- Age at Death -->
	<xsl:template match="age.death.d">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_ageAtDeath;'" />
			</xsl:call-template>
			<td>
				<xsl:apply-templates/>
				<xsl:text><![CDATA[ ]]>&pr_years;</xsl:text>
			</td>
		</tr>
	</xsl:template>

	<!-- Death Location -->
	<xsl:template name="DeathLocation">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_deathLocation;'" />
			</xsl:call-template>
			<td>
				<xsl:choose>
					<xsl:when test="death.loc">
						<xsl:apply-templates select="death.loc"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="death.cty"/>
						<xsl:apply-templates select="death.st"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="death.st">
		<xsl:if test="preceding-sibling::death.cty">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Death County -->
	<xsl:template match="death.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_deathCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Autopsy-->
	<xsl:template match="autopsy">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_autopsy;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Underlying Cause of Death-->
	<xsl:template match="cause.death">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_underlyingCauseOfDeath;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Manner of Death -->
	<xsl:template match="manner.death">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mannerOfDeath;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Place of Accident or Injury -->
	<xsl:template match="plc.acc.inj">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_placeOfInjury;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Where Death Occurred -->
	<xsl:template match="hospital.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_whereDeathOccurred;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Disposition-->
	<xsl:template match="disp.body">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_disposition;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	************** (C)"Information Regarding Deceased" section ****************
	************************************************************************-->

	<xsl:template match="deceased.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_informationRegardingDeceased;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="decease.occ"/>
			<xsl:apply-templates select="decease.indus"/>
			<xsl:apply-templates select="edu"/>
			<xsl:apply-templates select="vet.stat"/>
			<xsl:apply-templates select="mar.stat"/>
			<xsl:apply-templates select="spouse.nm"/>
			<xsl:apply-templates select="addr.b"/>
			<xsl:apply-templates select="cnty"/>
			<xsl:apply-templates select="birth.d"/>
			<xsl:apply-templates select="birth.loc"/>
			<xsl:apply-templates select="birth.cnty"/>
			<xsl:apply-templates select="birth.cen"/>
			<xsl:apply-templates select="father.nm"/>
			<xsl:apply-templates select="father.birth.plc"/>
			<xsl:apply-templates select="mother.nm"/>
			<xsl:apply-templates select="mother.birth.plc"/>
		</table>
	</xsl:template>

	<!--Deceased Occupation -->
	<xsl:template match="decease.occ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_occupation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Industry -->
	<xsl:template match="decease.indus">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_industry;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Education-->
	<xsl:template match="edu">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_education;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Veteran Status-->
	<xsl:template match="vet.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_veteranStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Marital Status -->
	<xsl:template match="mar.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_maritalStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Spouse Name-->
	<xsl:template match="spouse.nm">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_spouseName;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Death Residence-->
	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_residenceAtDeath;'"/>
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="city" select="city"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased County Of Residence-->
	<xsl:template match="cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_residenceCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Birth Place-->
	<xsl:template match="birth.loc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_birthPlace;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Birth County-->
	<xsl:template match="birth.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_birthCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Birth  Century-->
	<xsl:template match="birth.cen">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_birthCentury;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Fathers Name-->
	<xsl:template match="father.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fatherName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Fathers Birth Place-->
	<xsl:template match="father.birth.plc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fatherBirthplace;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Mothers Name-->
	<xsl:template match="mother.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_motherName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Deceased Mothers Birth Place-->
	<xsl:template match="mother.birth.plc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_motherBirthplace;'"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
