<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
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

	<!-- Don't render the follow data fields -->
	<xsl:template match="col.key | legacyId | pc| metadata | full.addr | prev.full.addr | map" />

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsExperianCreditHeaderClass;'" />
			<xsl:with-param name="dualColumn" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_creditHeader;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsMainColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<xsl:call-template name="individualInformation"/>
		<xsl:apply-templates select="curr.house.info" />
		<xsl:apply-templates select="prev.house.info" />
	</xsl:template>

	<xsl:template name="individualInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_individualInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="individ.info.b/full.name" />
			<xsl:apply-templates select="aka.individ.info.b/full.name" />
			<xsl:apply-templates select="individ.info.b" />
			<xsl:apply-templates select="curr.house.info/phone.b" />
		</table>
	</xsl:template>

	<xsl:template match="phone[normalize-space(.) and not(output.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone1;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="phone[output.encrypted]"/>

	<xsl:template match="phone.number[normalize-space(.) and not(output.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone2;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="phone.number[output.encrypted]"/>

	<xsl:template match="individ.info.b">
		<xsl:apply-templates select="oth.name" />
		<xsl:apply-templates select="ssn.b/ssn" />
		<xsl:apply-templates select="addl.ssn.b/addl.ssn" />
		<xsl:apply-templates select="dob" />
		<xsl:apply-templates select="death.d" />
		<xsl:apply-templates select="gender" />
		<xsl:apply-templates select="rpt.d" />
	</xsl:template>

	<!-- Name -->
	<xsl:template match="individ.info.b/full.name">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Also Known As (AKA) -->
	<xsl:template match="aka.individ.info.b/full.name[not(optout.encrypted) and not(position()>1)]">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_alsoKnownAs;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Other Name -->
	<xsl:template match="individ.info.b/oth.name">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_otherName;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- SSN -->
	<xsl:template match="ssn[normalize-space(.) and not(optout.encrypted) and normalize-space(.) != '000000000']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bestSsn;'"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Additional SSN -->
	<xsl:template match="addl.ssn[normalize-space(.) and not(optout.encrypted) and normalize-space(.) != '000000000']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalSsn;'"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Gender -->
	<xsl:template match="gender">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date Of Birth -->
	<xsl:template match="dob[normalize-space(.) and not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Death Reported-->
	<xsl:template match="death.d[normalize-space(.) and not(optout.encrypted)]">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_deathReported;'" />
			</xsl:call-template>
			<td>
				<xsl:choose>
					<xsl:when test="normalize-space(.)='00000000'">
						<xsl:text>YES</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="FormatNonSensitiveDate"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!-- Report Date -->
	<xsl:template match="rpt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_onFileSince;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="curr.house.info[not(curr.addr.b/addr.b//output.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_lastKnownAddressInformationSubheader;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="curr.addr.b"/>
			<xsl:apply-templates select="addr.rpt.d"/>
			<xsl:apply-templates select="last.addr.rpt.d"/>
		</table>
	</xsl:template>

	<xsl:template match="curr.house.info[curr.addr.b/addr.b//output.encrypted]"/>

	<xsl:template match="prev.house.info[not(prev.addr.b/addr.b//output.encrypted)]">
		<xsl:if test="not(preceding-sibling::prev.house.info)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_otherAddressInfo;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates select="prev.addr.b"/>
			<xsl:apply-templates select="addr.rpt.d"/>
			<xsl:apply-templates select="last.addr.rpt.d"/>
		</table>
	</xsl:template>

	<xsl:template match="prev.house.info[prev.addr.b/addr.b//output.encrypted]"/>

	<!-- Template match for Current Address AND Previous Address -->
	<xsl:template match="curr.addr.b | prev.addr.b">
		<xsl:if test="not(addr.b//optout.encrypted)">
			<xsl:apply-templates select="addr.b"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="addr.b">
		<xsl:variable name="label">
			<xsl:value-of select="count(preceding::addr.b)+1"/>
			<xsl:choose>
				<xsl:when test="name(..)='curr.addr.b'">
					<xsl:text><![CDATA[ ]]>&pr_address;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[ ]]>&pr_previousAddress;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="streetNum" select="str.nbr"/>
			<xsl:with-param name="streetDirection" select="str.post.dir"/>
			<xsl:with-param name="street" select="str.name"/>
			<xsl:with-param name="streetSuffix" select="str.suf"/>
			<xsl:with-param name="streetDirectionSuffix" select="str.pre.dir"/>
			<xsl:with-param name="streetUnitNumber" select="unit.b/unit.typ"/>
			<xsl:with-param name="streetUnit" select="unit.b/unit.id"/>
			<xsl:with-param name="city" select="city"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Format Reported Address Date -->
	<xsl:template match="addr.rpt.d[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressFirstReported;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Format Reported Last Address Date -->
	<xsl:template match="last.addr.rpt.d[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressLastReported;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>

