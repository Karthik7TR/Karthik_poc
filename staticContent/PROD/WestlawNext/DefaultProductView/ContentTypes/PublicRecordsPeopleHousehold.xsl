<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>

	<!--People Finder Household Centric Record - PEOPLE-HH-->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:preserve-space elements="*"/>

	<!--
		Desired output view:
			content    - renders document (default)
	-->

	<xsl:variable name="fullpath-node-r" select="/Document/n-docbody/r" />

	<!-- Do not render these nodes -->
	<xsl:template match="p|pc|c|col.key|legacy.id|pubid|copyright|m.id"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsRealPropertyTransactionClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_peopleFinderHouseholdCentricRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="member[@no = '1']"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="addr.info"/>
		<!-- If there are additional household members and those additional members have elements that are not encrypted. -->
		<xsl:if test="count(member) > 1 and count(member[@no > 1]/name.b/node()/child::*[name() = 'optout.encrypted']) != count(member[@no > 1]/name.b/node()/node())">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_additionalIndividualsInThisHousehold;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="member[@no > 1]"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- ********************************************************************** 
	******************** (B)"Head of Household" section *********************
	************************************************************************-->

	<xsl:template match="member[@no = '1']">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_headOfHouseholdInfo;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Name-->
	<xsl:template match="name.b">
		<xsl:param name="label" select="'&pr_headOfHousehold;'"/>
		<xsl:param name="trClass"/>
		<tr>
			<xsl:attribute name="class">
				<xsl:text>&pr_item;</xsl:text>
				<xsl:if test="$trClass">
					<xsl:value-of select="concat(' ', $trClass)"/>
				</xsl:if>
			</xsl:attribute>
			<th>
				<xsl:value-of select="$label"/>
			</th>
			<td>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="prefixName">
						<xsl:if test="not(na.t/optout.encrypted)">
							<xsl:value-of select="na.t"/>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="firstName">
						<xsl:if test="not(fna/optout.encrypted)">
							<xsl:value-of select="fna"/>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="middleName">
						<xsl:if test="not(mid/optout.encrypted)">
							<xsl:value-of select="mid"/>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="lastName">
						<xsl:if test="not(lna/optout.encrypted)">
							<xsl:value-of select="lna"/>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="suffixName">
						<xsl:if test="not(na.suf/optout.encrypted)">
							<xsl:value-of select="na.suf"/>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
				<!-- Test to see if this is the first member node. -->
				<xsl:if test="count(../preceding-sibling::member) > 0">
					<xsl:apply-templates select="../relate"/>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!--Gender-->
	<xsl:template match="gender">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Birth Date-->
	<xsl:template match="birth.d[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date Record Added-->
	<xsl:template match="indiv.ver.d[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nameOrAddressConfirmed;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Ethnicity -->
	<xsl:template match="ethnic[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ethnicity;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Confirmed By Multiple Sources-->
	<xsl:template match="ver.sc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_confirmedByMultipleSources;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Marital Status-->
	<xsl:template match="mr.stat[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_maritalStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Marital Date-->
	<xsl:template match="mr.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_maritalDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Lifestyle Indicators-->
	<xsl:template match="card.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lifestyleIndicators;'"/>
			<xsl:with-param name="selectNodes" select="card"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*********************** (C) Household Information *************************
	************************************************************************-->

	<xsl:template match="addr.info">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_householdInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="addr.b"/>
			<xsl:apply-templates select="cnty"/>
			<xsl:apply-templates select="." mode="LatLong"/>
			<xsl:apply-templates select="res.typ"/>
			<xsl:apply-templates select="addr.typ"/>
			<xsl:apply-templates select="dwel_typ"/>
			<xsl:apply-templates select="hh.cnt"/>
			<xsl:apply-templates select="arrv.d"/>
			<xsl:apply-templates select="phn.b"/>
			<xsl:apply-templates select="mr.job"/>
			<xsl:apply-templates select="ms.job"/>
		</table>
	</xsl:template>

	<!--Address-->
	<xsl:template match="addr.b[not(node()/optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="str"/>
			<xsl:with-param name="streetUnit" select="unit"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
			<xsl:with-param name="zipExt" select="zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!--County-->
	<xsl:template match="cnty[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Latitude and Longitude-->
	<xsl:template match="addr.info" mode="LatLong">
		<xsl:if test="normalize-space(lati) and normalize-space(long) and not(lati/optout.encrypted) and not(long/optout.encrypted)">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_latitudeAndLongitude;'"/>
				<xsl:with-param name="selectNodes" select="lati | long"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="lati">
		<xsl:call-template name="LatLongFormat">
			<xsl:with-param name="currentValue" select="node()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="long">
		<xsl:if test="preceding-sibling::lati">
			<xsl:text>/</xsl:text>
		</xsl:if>
		<xsl:call-template name="LatLongFormat">
			<xsl:with-param name="currentValue" select="node()"/>
		</xsl:call-template>
	</xsl:template>

	<!--Residence Type-->
	<xsl:template match="res.typ[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_residenceType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Residence Type-->
	<xsl:template match="addr.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_householdType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Residence Type:Multifamily-->
	<xsl:template match="dwel.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_multifamilyResidenceType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Household Count-->
	<xsl:template match="hh.cnt[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalIndividuals;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date House Added-->
	<xsl:template match="arrv.d[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressFirstReported;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Phone Count-->
	<xsl:template match="phn.cnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalTelephones;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Phone:Person-Phones-->
	<xsl:template match="phn[phn.nbr and not(phn.nbr/optout.encrypted)]">
		<xsl:choose>
			<xsl:when test="@no > 1">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_additionalTelephone;'"/>
					<xsl:with-param name="selectNodes" select="phn.nbr"/>
					<xsl:with-param name="nodeType" select="$PHONE"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_telephone;'"/>
					<xsl:with-param name="selectNodes" select="phn.nbr"/>
					<xsl:with-param name="nodeType" select="$PHONE"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="ver.d"/>
		<xsl:apply-templates select="svc.typ"/>
	</xsl:template>

	<!--Phone Verification Date-->
	<xsl:template match="ver.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_telephoneValidationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Telephone Type-->
	<xsl:template match="svc.typ[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_serviceType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Male Occupation-->
	<xsl:template match="mr.job[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_reportedOccupation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Female Occupation-->
	<xsl:template match="ms.job[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_reportedOccupation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*************** (D) Additional Individuals in Household section ***********
	************************************************************************-->

	<xsl:template match="member[@no > 1]">
		<xsl:apply-templates select="name.b">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="trClass">
				<xsl:if test="position() > 1">
					<xsl:value-of select="'&pr_paddingTop;'"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="demo.b"/>
	</xsl:template>

	<!--Relationship-->
	<xsl:template match="relate[normalize-space(.)]">
		<xsl:text><![CDATA[ ]]>&pr_spouse;</xsl:text>
	</xsl:template>

	<!--Used to format the Latitude and Longitude-->
	<xsl:template name ="LatLongFormat">
		<xsl:param name ="currentValue"/>
		<xsl:value-of select="format-number($currentValue,'###.######')" />
	</xsl:template>

</xsl:stylesheet>