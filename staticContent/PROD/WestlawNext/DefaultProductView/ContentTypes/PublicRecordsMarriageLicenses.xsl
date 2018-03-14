<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsDate.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsMaskedData.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Database Signon: MARRIAGE-ALL -->

	<!-- Variable(s) -->
	<xsl:variable name="pcvalue" select="/Document/n-docbody/r/pc"/>

	<!--  Do not render these nodes -->
	<xsl:template match="map|p|pc|col.key|legacyId|data.yr|coverage.id|cs.hid.nbr" />

	<xsl:template match="Document" priority="1">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsMarraigeLicensesClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_marriageRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:choose>
			<xsl:when test="$pcvalue='MRG'">
				<xsl:apply-templates select="$coverage-block">
					<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
					<xsl:with-param name="databaseLastUpdatedLabel" select="'&pr_databaseUpdated;'"/>
					<!--<xsl:with-param name="displayCurrentDate" select="false()"/>-->
					<!--<xsl:with-param name="displaySource" select="false()"/>-->
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="$pcvalue='LSF'">
				<xsl:apply-templates select="$coverage-block">
					<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
					<xsl:with-param name="currentThroughDateLabel" select="'&pr_courtLastUpdated;'"/>
					<!--<xsl:with-param name="displayCurrentDate" select="false()"/>
					<xsl:with-param name="displaySource" select="false()"/>-->
					<xsl:with-param name="displayUpdateFrequency" select="false()"/>
				</xsl:apply-templates>
			</xsl:when>
		</xsl:choose>
		<!-- Render the "Name Information" section -->
		<xsl:choose>
			<xsl:when test="$pcvalue='MRG'">
				<xsl:call-template name="NameInfo"/>
			</xsl:when>
			<xsl:when test="$pcvalue='LSF'">
				<xsl:apply-templates select="descendant::nm.info.b"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="PublicRecordsRightColumn">
		<xsl:choose>
			<xsl:when test="$pcvalue='MRG'">
				<!-- Render the "Marriage Record Information" section -->
				<xsl:call-template name="mar.b" />
			</xsl:when>
			<xsl:when test="$pcvalue='LSF'">
				<!-- Render the "Filing Information" section -->
				<xsl:call-template name="FileInfo"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="NameInfo">
		<xsl:if test="descendant::grm.b or descendant::brd.b">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_nameInformationSubheader;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="descendant::grm.b"/>
				<xsl:apply-templates select="descendant::brd.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Groom or Bride Information -->
	<xsl:template match="grm.b | brd.b">
			<xsl:apply-templates select="nm"/>
			<xsl:apply-templates select="res.cty"/>
			<xsl:apply-templates select="res.st"/>
			<xsl:apply-templates select="cnty"/>
			<xsl:apply-templates select="birth.p"/>
			<xsl:apply-templates select="grm.birth.d | brd.birth.d" />
			<xsl:apply-templates select="age"/>
			<xsl:apply-templates select="prev.mar.stat"/>
			<xsl:apply-templates select="prev.mar.end.d"/>
	</xsl:template>

	<!-- Name -->
	<xsl:template match="nm">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Residential City -->
	<xsl:template match="res.cty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_cityOfResidence;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Residential State -->
	<xsl:template match="res.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateOfResidence;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Residential County -->
	<xsl:template match="cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countyOfResidence;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Birth Place -->
	<xsl:template match="birth.p">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_placeOfBirth;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Bride and groom date of birth -->
	<xsl:template match="grm.birth.d | brd.birth.d | husb.birth.d | wife.birth.d">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
    </xsl:call-template>
	</xsl:template>

	<!-- Age at the time of marriage -->
	<xsl:template match="age">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ageAtMarriage;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Previous Marital Status -->
	<xsl:template match="prev.mar.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousMaritalStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date Last Marriage Ended -->
	<xsl:template match="prev.mar.end.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateLastMarriageEnded;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
			<xsl:with-param name="selectNodes" select="prev.mar.end.d"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="mar.b">
		<xsl:if test="descendant::mar.b | descendant::st">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_marriageRecordInfo;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="descendant::mar.b/mar.cty"/>
				<xsl:apply-templates select="descendant::mar.b/mar.cnty" />
				<xsl:apply-templates select="descendant::st" />
				<xsl:apply-templates select="descendant::mar.b/mar.d"/>
				<xsl:apply-templates select="descendant::mar.b/crmy.typ" />
				<xsl:apply-templates select="descendant::mar.b/crmy.perf.by"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- City of marriage -->
	<xsl:template match="mar.cty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_cityOfMarriage;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Marriage County -->
	<xsl:template match="mar.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countyOfMarriage;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- State of marriage -->
	<xsl:template match="st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateOfMarriage;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date of Marriage -->
	<xsl:template match="mar.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfMarriage;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
			<xsl:with-param name="selectNodes" select="mar.d"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Type of Wedding Ceremony -->
	<xsl:template match="crmy.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_typeOfWeddingCeremony;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Ceremony Performed -->
	<xsl:template match="crmy.perf.by">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ceremonyPerformedBy;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="nm.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_nameInformationSubheader;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="descendant::pltf.nm.b[1]" />
			<xsl:apply-templates select="descendant::def.nm.b[1]" />
			<xsl:apply-templates select="descendant::oth.nm.b" />
		</table>
	</xsl:template>

	<!-- Name -->
	<xsl:template match="pltf.nm | def.nm | oth.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_name;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="FileInfo">
		<xsl:if test="(descendant::filg.d) or (descendant::court.b/filg.st) or (descendant::cs.typ.b/cs.typ) or (descendant::court.b/venue.cd)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_filingInfo;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="descendant::filg.d" />
				<xsl:apply-templates select="descendant::venue.cd" />
				<xsl:apply-templates select="descendant::filg.st"  mode="StateFiled"/>
				<xsl:apply-templates select="descendant::cs.nbr"/>
				<xsl:apply-templates select="descendant::cs.typ" />
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Date Filed -->
	<xsl:template match="filg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateFiled;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- County Filed -->
	<xsl:template match="venue.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countyFiled;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- State Filed -->
	<xsl:template match="filg.st" mode="StateFiled">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_stateFiled;'" />
			</xsl:call-template>
			<td>
				<xsl:choose>
					<xsl:when test="(.)">
						<xsl:if test="(.='AL')">
							<xsl:text>ALABAMA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='AK')">
							<xsl:text>ALASKA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='AZ')">
							<xsl:text>ARIZONA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='AR')">
							<xsl:text>ARKANSAS</xsl:text>
						</xsl:if>
						<xsl:if test="(.='CA')">
							<xsl:text>CALIFORNIA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='CO')">
							<xsl:text>COLORADO</xsl:text>
						</xsl:if>
						<xsl:if test="(.='CT')">
							<xsl:text>CONNECTICUT</xsl:text>
						</xsl:if>
						<xsl:if test="(.='DC')">
							<xsl:text>WASHINGTON D.C.</xsl:text>
						</xsl:if>
						<xsl:if test="(.='FL')">
							<xsl:text>FLORIDA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='GA')">
							<xsl:text>GEORGIA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='HI')">
							<xsl:text>HAWAII</xsl:text>
						</xsl:if>
						<xsl:if test="(.='ID')">
							<xsl:text>IDAHO</xsl:text>
						</xsl:if>
						<xsl:if test="(.='IL')">
							<xsl:text>ILLINOIS</xsl:text>
						</xsl:if>
						<xsl:if test="(.='IN')">
							<xsl:text>INDIANA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='IA')">
							<xsl:text>IOWA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='KS')">
							<xsl:text>KANSAS</xsl:text>
						</xsl:if>
						<xsl:if test="(.='KY')">
							<xsl:text>KENTUCKY</xsl:text>
						</xsl:if>
						<xsl:if test="(.='LA')">
							<xsl:text>LOUISIANA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='ME')">
							<xsl:text>MAINE</xsl:text>
						</xsl:if>
						<xsl:if test="(.='MD')">
							<xsl:text>MARYLAND</xsl:text>
						</xsl:if>
						<xsl:if test="(.='MA')">
							<xsl:text>MASSACHUSETTS</xsl:text>
						</xsl:if>
						<xsl:if test="(.='MI')">
							<xsl:text>MICHIGAN</xsl:text>
						</xsl:if>
						<xsl:if test="(.='MN')">
							<xsl:text>MINNESOTA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='MS')">
							<xsl:text>MISSISSIPPI</xsl:text>
						</xsl:if>
						<xsl:if test="(.='MO')">
							<xsl:text>MISSOURI</xsl:text>
						</xsl:if>
						<xsl:if test="(.='MT')">
							<xsl:text>MONTANA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='NE')">
							<xsl:text>NEBRASKA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='NV')">
							<xsl:text>NEVADA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='NH')">
							<xsl:text>NEW HAMPSHIRE</xsl:text>
						</xsl:if>
						<xsl:if test="(.='NJ')">
							<xsl:text>NEW JERSEY</xsl:text>
						</xsl:if>
						<xsl:if test="(.='NM')">
							<xsl:text>NEW MEXICO</xsl:text>
						</xsl:if>
						<xsl:if test="(.='NY')">
							<xsl:text>NEW YORK</xsl:text>
						</xsl:if>
						<xsl:if test="(.='NC')">
							<xsl:text>NORTH CAROLINA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='ND')">
							<xsl:text>NORTH DAKOTA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='OH')">
							<xsl:text>OHIO</xsl:text>
						</xsl:if>
						<xsl:if test="(.='OK')">
							<xsl:text>OKLAHOMA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='OR')">
							<xsl:text>OREGON</xsl:text>
						</xsl:if>
						<xsl:if test="(.='PA')">
							<xsl:text>PENNSYLVANIA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='PR')">
							<xsl:text>PUERTO RICO</xsl:text>
						</xsl:if>
						<xsl:if test="(.='RI')">
							<xsl:text>RHODE ISLAND</xsl:text>
						</xsl:if>
						<xsl:if test="(.='SC')">
							<xsl:text>SOUTH CAROLINA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='SD')">
							<xsl:text>SOUTH DAKOTA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='TN')">
							<xsl:text>TENNESSEE</xsl:text>
						</xsl:if>
						<xsl:if test="(.='TX')">
							<xsl:text>TEXAS</xsl:text>
						</xsl:if>
						<xsl:if test="(.='UT')">
							<xsl:text>UTAH</xsl:text>
						</xsl:if>
						<xsl:if test="(.='VT')">
							<xsl:text>VERMONT</xsl:text>
						</xsl:if>
						<xsl:if test="(.='VA')">
							<xsl:text>VIRGINIA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='VI')">
							<xsl:text>VIRGIN ISLANDS</xsl:text>
						</xsl:if>
						<xsl:if test="(.='WA')">
							<xsl:text>WASHINGTON</xsl:text>
						</xsl:if>
						<xsl:if test="(.='WV')">
							<xsl:text>WEST VIRGINIA</xsl:text>
						</xsl:if>
						<xsl:if test="(.='WI')">
							<xsl:text>WISCONSIN</xsl:text>
						</xsl:if>
						<xsl:if test="(.='WY')">
							<xsl:text>WYOMING</xsl:text>
						</xsl:if>
					</xsl:when>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!-- Case Number -->
	<xsl:template match="cs.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Case Type -->
	<xsl:template match="cs.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseType;'"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
