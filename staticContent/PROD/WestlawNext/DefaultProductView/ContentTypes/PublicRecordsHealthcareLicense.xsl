<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>

	<!-- Product: Health Care License Record -->
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- Render the CONTENT view. -->
	<xsl:template match="Document">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsHealthcareLicenseClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_healthcareProviderLicenseRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!-- Source Information Section-->
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
			<xsl:with-param name="displaySource" select="false()"/>
		</xsl:apply-templates>

		<!-- Provider Information Section-->
		<xsl:apply-templates select="n-docbody/r/prov.info.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<!--Education Information Section-->
		<xsl:apply-templates select="n-docbody/r/ed.info.b"/>
		<!--Reporting Authority Information-->
		<xsl:apply-templates select="n-docbody/r/s.info.b"/>
		<!-- License Information Section -->
		<xsl:apply-templates select="n-docbody/r/lic.info.b"/>
	</xsl:template>

	<xsl:template name ="HealthcareLicenseSource">
		<xsl:if test="/Document/n-docbody/r/s.info.b/rpt.authority">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_source;'"/>
				<xsl:with-param name="selectNodes" select="/Document/n-docbody/r/s.info.b/rpt.authority/text()"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- **********************************************************************
	********************** Provider Information section *********************
	************************************************************************-->

	<!-- Provider Information -->
	<xsl:template match="prov.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_providerInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="na.b"/>
			<xsl:apply-templates select ="oth.na.b/fmr.lna"/>
			<xsl:apply-templates select="oth.na.b/dba.na"/>
			<xsl:apply-templates select ="oth.na.b/own.na"/>
			<xsl:apply-templates select ="ent.typ"/>
			<xsl:apply-templates select ="birth.d"/>
			<xsl:apply-templates select ="sex"/>
			<xsl:apply-templates select ="phn1.b/phn1"/>
			<xsl:apply-templates select ="phn1.b/phn1.typ"/>
			<xsl:apply-templates select ="phn2.b/phn2"/>
			<xsl:apply-templates select ="phn2.b/phn2.typ"/>
			<xsl:apply-templates select ="addr1.b"/>
			<xsl:apply-templates select ="addr2.b"/>
			<xsl:apply-templates select ="email.b"/>
			<xsl:apply-templates select ="last.rpt.d"/>
			<xsl:apply-templates select ="add.info"/>
		</table>
	</xsl:template>

	<!--Name-->
	<xsl:template match="na.b">
		<tr>
			<th>
				<xsl:text>&pr_name;</xsl:text>
			</th>
			<td>
				<xsl:choose>
					<xsl:when test="full.na">
						<xsl:apply-templates select="full.na"/>
					</xsl:when>
					<xsl:when test="full.na.b">
						<xsl:apply-templates select="full.na.b"/>
					</xsl:when>
					<xsl:when test="na">
						<xsl:apply-templates select="na"/>
					</xsl:when>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="na.prefix | f.name | m.name | l.name">
		<xsl:apply-templates select="text()"/>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>
	<!--Name when elemement is full.na.b-->
	<xsl:template match ="full.na.b | full.na">
		<xsl:apply-templates select ="na.prefix | f.name | m.name | l.name | na.suf"/>
	</xsl:template>

	<!--Former Last Name-->
	<xsl:template match="oth.na.b/fmr.lna">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_formerLastName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--DBA-->
	<xsl:template match="oth.na.b/dba.na">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_doingBusinessAs;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Owner-->
	<xsl:template match="oth.na.b/own.na">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_owner;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Entity Type-->
	<xsl:template match="prov.info.b/ent.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_entityType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date of Birth-->
	<xsl:template match="prov.info.b/birth.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Gender-->
	<xsl:template match="prov.info.b/sex">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Phone Number-->
	<xsl:template match="prov.info.b/phn1.b/phn1">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phoneNumber;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Phone Type-->
	<xsl:template match="prov.info.b/phn1.b/phn1.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phoneType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Phone Number 2-->
	<xsl:template match="prov.info.b/phn2.b/phn2">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone2;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Phone 2 Type-->
	<xsl:template match="prov.info.b/phn2.b/phn2.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone2Type;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Address-->
	<xsl:template match="prov.info.b/addr1.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="defaultLabel" select="'&pr_address;'"/>
			<xsl:with-param name="street" select="addr.str"/>
			<xsl:with-param name="city" select="addr.cty"/>
			<xsl:with-param name="stateOrProvince" select="addr.st"/>
			<xsl:with-param name="zip" select="addr.zip.b/addr.zip"/>
			<xsl:with-param name="zipExt" select="addr.zip.b/addr.zip4"/>
		</xsl:call-template>
	</xsl:template>

	<!--Address 2-->
	<xsl:template match="prov.info.b/addr2.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="defaultLabel" select="'&pr_addressTwo;'"/>
			<xsl:with-param name="street" select="addr.str"/>
			<xsl:with-param name="city" select="addr.cty"/>
			<xsl:with-param name="stateOrProvince" select="addr.st"/>
			<xsl:with-param name="zip" select="addr.zip.b/addr.zip"/>
			<xsl:with-param name="zipExt" select="addr.zip.b/addr.zip4"/>
		</xsl:call-template>
	</xsl:template>

	<!--Email-->
	<xsl:template match="prov.info.b/email.b">
		<xsl:call-template name="wrapPublicRecordsEmail">
			<xsl:with-param name="email" select="full.eml"/>
			<xsl:with-param name="user" select="user.eml"/>
			<xsl:with-param name="domain" select="dom.eml"/>
		</xsl:call-template>
	</xsl:template>

	<!--Last Reported-->
	<xsl:template match="prov.info.b/last.rpt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastReported;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Additional Information-->
	<xsl:template match="prov.info.b/add.info">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalInformation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	********************** Education Information section ********************
	************************************************************************-->
	<xsl:template match="ed.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_educationInfo;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="inst"/>
			<xsl:apply-templates select ="inst.st"/>
			<xsl:apply-templates select ="degree"/>
			<xsl:apply-templates select ="grad.d"/>
		</table>
	</xsl:template>

	<!--Institution-->
	<xsl:template match="ed.info.b/inst">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_institution;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--State-->
	<xsl:template match="ed.info.b/inst.st | s.info.b/s.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_state;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Degree-->
	<xsl:template match="ed.info.b/degree">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_degree;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Graduation Date-->
	<xsl:template match="ed.info.b/grad.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_graduationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	************************ Reporting Authority Information Section **********
	************************************************************************-->

	<xsl:template match="s.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_reportingAuthorityInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="rpt.pub"/>
			<xsl:apply-templates select ="rpt.authority"/>
			<xsl:apply-templates select ="s.st"/>
		</table>
	</xsl:template>

	<!--Publication-->
	<xsl:template match="s.info.b/rpt.pub">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_publication;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Authority-->
	<xsl:template match="s.info.b/rpt.authority">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_authority;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	************************ License Information Section **********************
	************************************************************************-->
	<xsl:template match="lic.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_licenseInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="lic.nbr"/>
			<xsl:apply-templates select="lic.st"/>
			<xsl:apply-templates select="first.lic.d"/>
			<xsl:apply-templates select="last.lic.d"/>
			<xsl:apply-templates select="lic.exp.d"/>
			<xsl:apply-templates select="lic.typ"/>
			<xsl:apply-templates select="lic.stat.b"/>
			<xsl:apply-templates select="lic.restrct.typ"/>
			<xsl:apply-templates select="prov.typ.b/prov.typ"/>
			<xsl:apply-templates select="prov.typ.b/class.typ"/>
			<xsl:apply-templates select="prov.typ.b/spec.typ"/>
		</table>
	</xsl:template>

	<!--License Number-->
	<xsl:template match="lic.info.b/lic.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--License State-->
	<xsl:template match="lic.info.b/lic.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseState;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--License First Issue Date-->
	<xsl:template match="lic.info.b/first.lic.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseFirstIssueDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--License Last Issue Date-->
	<xsl:template match="lic.info.b/last.lic.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseLastIssueDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--License Expiration Date-->
	<xsl:template match="lic.info.b/lic.exp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseExpirationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--License Type-->
	<xsl:template match="lic.info.b/lic.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--License Status-->
	<xsl:template match="lic.info.b/lic.stat.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lic.restrct | lic.act | prob">
		<xsl:apply-templates select="text()"/>
		<xsl:if test="following::*[1]">
			<xsl:text>;<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<!--License Restriction Type-->
	<xsl:template match="lic.info.b/lic.restrct.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseRestrictionType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Provider Type-->
	<xsl:template match="lic.info.b/prov.typ.b/prov.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_providerType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Provider Class-->
	<xsl:template match="lic.info.b/prov.typ.b/class.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_providerClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Provider Specialty-->
	<xsl:template match="lic.info.b/prov.typ.b/spec.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_providerSpecialty;'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
