<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsDate.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--Do not render these nodes-->
	<xsl:template match="map|p|pc|col.key|legacy.id|source|uid.number|profile.updated|profile.created|covert.d|profile.type|keyword.code|uri|pass.num|ssn.b|ssn|n-view|associated.na.INF"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<!-- Render the XML based on the desired VIEW. -->
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsBusinessFindClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_worldWatchListProfile;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!-- Coverage / Source Information -->
		<xsl:apply-templates select="$coverage-block"/>
		<xsl:apply-templates select="profiles.info.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="related.b"/>
		<xsl:apply-templates select="keywords.b"/>
		<xsl:apply-templates select="external.sources"/>
		<!--Display of Discalimer Message-->
		<xsl:call-template name="DisclaimerMessage"/>
	</xsl:template>

	<!--**********************************************************************
	**********************  "Individual Profile" section  ********************
	************************************************************************-->

	<xsl:template match="profiles.info.b">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="name.b/entity.na">
					<xsl:text>&pr_entityProfile;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_individualProfile;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="$label"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="name.b"/>
			<xsl:apply-templates select="alternative.spelling"/>
			<xsl:apply-templates select="alias.b"/>
			<xsl:apply-templates select="title"/>
			<xsl:apply-templates select="position"/>
			<xsl:apply-templates select="category"/>
			<xsl:apply-templates select="subcategory.b"/>
			<xsl:apply-templates select="dob"/>
			<xsl:apply-templates select="age.b"/>
			<xsl:apply-templates select="age.of.date"/>
			<xsl:apply-templates select="place.birth"/>
			<xsl:apply-templates select="deceased"/>
			<xsl:apply-templates select="passport.b/passport/pass.ctry"/>
			<xsl:apply-templates select="passport.b/passport/pass.num"/>
			<xsl:apply-templates select="ssn"/>
			<xsl:apply-templates select="locations.b"/>
			<xsl:apply-templates select="location"/>
			<xsl:apply-templates select="loc.cty"/>
			<xsl:apply-templates select="loc.st"/>
			<xsl:apply-templates select="loc.ctry"/>
			<xsl:apply-templates select="cntries.b"/>
			<xsl:apply-templates select="further.info"/>
		</table>
	</xsl:template>

	<xsl:template match="name.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_names;'"/>
			<xsl:with-param name="firstName" select="fna"/>
			<xsl:with-param name="lastName" select="lna"/>
		</xsl:call-template>
	</xsl:template>

	<!--Alternative Spelling-->
	<xsl:template match="alternative.spelling">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_alternativeSpellings;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="alternative.spelling//text()">
		<xsl:value-of disable-output-escaping="yes" select="." />
	</xsl:template>

	<!--Alias-->
	<xsl:template match="alias.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_alias;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="alias">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--Title-->
	<xsl:template match="title">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_title;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Position-->
	<xsl:template match="position">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_position;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Category-->
	<xsl:template match="category">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_category;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Subcategory-->
	<xsl:template match="subcategory.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_subcategory;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date of Birth-->
	<xsl:template match="dob">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Place of Birth-->
	<xsl:template match="place.birth">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_placeOfBirth;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Age-->
	<xsl:template match="age.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_age;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Age As Of date-->
	<xsl:template match="age.of.date">
		<xsl:if test="preceding-sibling::*">
			<xsl:text>&pr_asOf;</xsl:text>
		</xsl:if>
		<xsl:call-template name="FormatDate" />
	</xsl:template>

	<!--Date of Death-->
	<xsl:template match="deceased">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfDeath;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Passport Countries-->
	<xsl:template match="passport.b/passport/pass.ctry">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_passportCountry;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Passport Number-->
	<xsl:template match="passport.b/passport/pass.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_passportNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Social security number-->
	<xsl:template match="ssn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ssn;'"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
		</xsl:call-template>
	</xsl:template>

	<!--Locations-->
	<xsl:template match="locations.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_locations;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="location">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="city" select="loc.cty"/>
				<xsl:with-param name="stateOrProvince" select="loc.st"/>
				<xsl:with-param name="country" select="loc.ctry"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!--Countries-->
	<xsl:template match="cntries.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countries;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Further Info-->
	<xsl:template match="further.info">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_furtherInformation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--**********************************************************************
	**********************  "Related Individuals" section  *******************
	************************************************************************-->
	<xsl:template match="related.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_relatedIndividualsEntities;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="companies.linked.to.b" />
			<xsl:apply-templates select="company.linked.to.b" />
			<xsl:apply-templates select="na.linked.to.b" />
		</table>
	</xsl:template>

	<!--Companies linked to list-->
	<xsl:template match="companies.linked.to.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_companiesLinkedTo;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Display list-->
	<xsl:template match="company.linked.to">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--Reported to be linked to list-->
	<xsl:template match="na.linked.to.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_reportedToBeLinkedTo;'"/>
			<xsl:with-param name="selectNodes" select="associated.na"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Display list-->
	<xsl:template match="associated.na">
		<div>
			<xsl:call-template name="FormatName">
				<xsl:with-param name="lastName" select="."/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- *******************************************************************
	***********************  "Official Lists" section  *********************
	************************************************************************-->

	<xsl:template match="keywords.b">
		<xsl:if test="child::keyword.list">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_officialLists;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="keyword.list" />
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Display list-->
	<xsl:template match="keyword.list">
		<tr class="&pr_item;">
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!--**********************************************************************
	***********************  "External Sources" section  *********************
	************************************************************************-->

	<xsl:template match="external.sources">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_externalSources;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="uri"/>
		</table>
	</xsl:template>

	<xsl:template match="uri">
		<tr class="&pr_item;">
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!--Disclaimer message-->
	<xsl:template name="DisclaimerMessage">
		<xsl:call-template name="wrapPublicRecordsDisclaimers">
			<xsl:with-param name="disclaimer1">
				<xsl:text>All information identified or correlated in this profile, appears in the listed sources. World-Check is not
									responsible for the content of third party sites or sources. Information correlated is necessarily brief and
									should be read by users in the context of the fuller details available in the external sources provided.
									Users should also carry out independent checks in order to verify the information correlated.</xsl:text>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>