<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<!--Combined Voter Records - N_VOTERS_ALL has following products -
		VOTE1
		VOTE2
	-->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- Do not render these nodes -->
	<xsl:template match="legacy.id|col.key|p|pc|map|oph.nbr|race"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsVoterRegistrationClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_voterRegistrationRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!--  Source Information  -->
		<xsl:apply-templates select="$coverage-block"/>
		<!-- Render the "Name and Personal Information" section -->
		<xsl:apply-templates select="person.b"/>
		<!-- Render the "Registration Information" section -->
		<xsl:apply-templates select="regist.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<!-- Render the "Voter History" section -->
		<xsl:apply-templates select="votehist.b"/>
		<!-- Render the "Demographic Information" section -->
		<xsl:apply-templates select="demogr.b"/>
	</xsl:template>

	<!--Email  -->
	<xsl:template match="email.b">
		<xsl:call-template name="wrapPublicRecordsEmail">
			<xsl:with-param name="email" select="email"/>
			<xsl:with-param name="user" select="user.nm"/>
			<xsl:with-param name="domain" select="domain.nm"/>
		</xsl:call-template>
	</xsl:template>


	<!--**********************************************************************
	*********************  (B)NAME AND PERSONAL INFORMATION  *********************
	**********************************************************************-->
	<xsl:template match="person.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_nameAndPersonalInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
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

	<!--Prior name-->
	<xsl:template match="prior[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_maidenNames;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address -->
	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- County -->
	<xsl:template match="cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
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

	<!-- Home Phone number -->
	<xsl:template match="hph.nbr[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_homePhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Work Phone number -->
	<xsl:template match="wph.nbr[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_workPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--**********************************************************************
	*********************  (C)REGISTRATION INFORMATION  *********************
	**********************************************************************-->

	<xsl:template match="regist.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_registrationInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Voter Identification Number-->
	<xsl:template match="vote.nbr[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_voterIdentificationNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Motor Voter Identification Number-->
	<xsl:template match="motor.nbr[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_motorVoterIdentificationNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Registration Date-->
	<xsl:template match="filg.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_registrationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--**********************************************************************
	*********************  (D)VOTER HISTORY  *********************
	**********************************************************************-->

	<xsl:template match="votehist.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_voterHistory;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="voterecord.b">
				<xsl:sort select="@sort" data-type="number" order="ascending"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="lvote.d"/>
		</table>
	</xsl:template>

	<!--Voter Record-->
	<xsl:template match="voterecord.b[normalize-space(.)]">
		<tr>
			<th>
				<xsl:apply-templates select="label"/>
			</th>
			<td>
				<xsl:text>&pr_voted;</xsl:text>
			</td>
		</tr>
	</xsl:template>

	<!--Last Date Voted-->
	<xsl:template match="lvote.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastDateVoted;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--**********************************************************************
	*********************  (E)DEMOGRAPHIC INFORMATION  *********************
	**********************************************************************-->

	<xsl:template match="demogr.b[count(child::*) > 1 or
											 (count(child::*) = 1 and (not(child::job) or (child::job != '' and //r/@r10 = 'MA')))]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_demographicInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>


	<!-- Date of Birth -->
	<xsl:template match="birth.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Age  -->
	<xsl:template match="age[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_age;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Place of Birth-->
	<xsl:template match="birth.loc[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_placeOfBirth;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Occupation-->
	<xsl:template match="job[normalize-space(.) and //r/@r10 = 'MA']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_occupation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Gender-->
	<xsl:template match="sex">
		<tr>
			<th>
				<xsl:text>&pr_gender;</xsl:text>
			</th>
			<td>
				<xsl:choose>
					<xsl:when test="normalize-space(.) = 'F'">
						<xsl:text>&pr_female;</xsl:text>
					</xsl:when>
					<xsl:when test="normalize-space(.) = 'M'">
						<xsl:text>&pr_male;</xsl:text>
					</xsl:when>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!-- Political party-->
	<xsl:template match="party[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_politicalParty;'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
