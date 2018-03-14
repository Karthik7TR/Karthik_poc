<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<!-- Product: Criminals Record -->
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="colKey" select="/Document/n-docbody/r/col.key"/>
	<xsl:variable name="sourceType" select="/Document/n-docbody/r/c/s.typ"/>

	<!-- Do not render these nodes -->
	<xsl:template match="p|pc|pubdate|prism-clipdate|col.key|restrict|doc.id|in.nbr|c|s.st|s.typ|case.nbr.b|court.norm"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsExecutiveProfileClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:choose>
			<xsl:when test="col.key = 'OFAC'">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_officeOfForeignAssetsControlRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="(col.key='FEDAOC') or (col.key='FEDCRIM')">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_usDistrictCourtRecord;'"/>
				</xsl:call-template>

				<!--xsl:call-template name="DisplayUpdateLink">
				</xsl:call-template-->
				
			</xsl:when>
			<xsl:when test="restrict = 'SOR'">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_sexOffenderRegistryRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="(col.key='ALDOC') or (col.key='ARDOC') or	(col.key='AZDOC') or (col.key='CTDOC') or (col.key='FLDOC') or (col.key='GADOC') or
							(col.key='IADOC') or (col.key='IDDOC') or (col.key='ILDOC') or (col.key='KSDOC') or (col.key='KYDOC') or (col.key='MEDOC') or
							(col.key='MIDOC') or (col.key='MTDOC') or (col.key='MNDOC') or (col.key='MSDOC') or (col.key='NCDOC') or (col.key='NEDOC') or
							(col.key='NHDOC') or (col.key='NJDOC') or	(col.key='NVDOC') or (col.key='NYDOC') or (col.key='NYDOCP') or (col.key='OHDOC') or
							(col.key='OKDOC') or (col.key='ORDOC') or	(col.key='PADOC1') or (col.key='SCDOC') or (col.key='TNDOC') or (col.key='UTDOC') or
							(col.key='WADOC') or (col.key='WIDOC') or (col.key='CODOC') or (col.key='FLDOC1') or (col.key='GADOCWEB') or (col.key='KYDOCWEB') or
							(col.key='TXDOC') or (col.key='RIDOC') or	(col.key='WVDOC') or (col.key='MDDOC') or (col.key='NMDOC')">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_departmentOfCorrectionsRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="(col.key='PADOC')">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_historicalDepartmentOfCorrectionsRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="(starts-with(col.key, 'AKAOC')) or	(starts-with(col.key, 'ARAOC')) or (starts-with(col.key, 'AZAOC')) or	(starts-with(col.key, 'CAAOC')) or
							(starts-with(col.key, 'CTAOC')) or	(starts-with(col.key, 'FLAOC')) or	(starts-with(col.key, 'ILAOC')) or	(starts-with(col.key, 'MDAOC')) or
							(starts-with(col.key, 'MIAOC')) or	(starts-with(col.key, 'MSAOC')) or	(starts-with(col.key, 'NDAOC')) or (starts-with(col.key, 'NJAOC')) or
							(starts-with(col.key, 'PAAOC')) or	(starts-with(col.key, 'RIAOC')) or	(starts-with(col.key, 'TNAOC')) or
							(starts-with(col.key, 'TXAOC')) or (starts-with(col.key, 'UTAOC')) or	(starts-with(col.key, 'VAAOC')) or	(starts-with(col.key, 'WAAOC')) or
							(starts-with(col.key, 'IAAOC')) or	(starts-with(col.key, 'LAAOCSTTAMMANY')) or (starts-with(col.key, 'TXAOCROCKWALL')) or
							(starts-with(col.key, 'OHAOCSANDUSKY')) or (starts-with(col.key, 'MAAOC')) or (col.key='FLDUVAL') or (col.key='FLPINELLAS')">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_administrativeOfficeOfTheCourtsRecord;'"/>
				</xsl:call-template>
			</xsl:when>

			<!-- BUG 950616 -->
			<xsl:when test="(starts-with(col.key, 'NCAOC'))">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_stateCourtRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			
			<!-- BUG 950616 -->
			<xsl:when test="(starts-with(col.key, 'ORAOC'))">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_stateCourtRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			
			<xsl:when test="(col.key='MNDPS') or	(col.key='TXDPS') or	(col.key='OKDPS')">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_departmentOfPublicSafetyRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="p='STATE DOCKETS - CRIMINAL'">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_criminalDocketRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains(col.key, 'AOC')">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_administrativeOfficeOfTheCourtsRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains(col.key, 'AOCCP')">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_administrativeOfficeOfTheCourtsCourtOfCommonPleasRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains(col.key,'DOC')">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_departmentOfCorrectionsRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains(col.key, 'GBI')">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_bureauOfInvestigationRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains(col.key, 'DPS')">
				<xsl:call-template name="DisplayHeading">
					<xsl:with-param name="HeadingText" select="'&pr_departmentOfPublicSafetyRecord;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<!-- Should never make it here -->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!-- Source Information Section-->
		<xsl:apply-templates select="$coverage-block"/>
		<!-- Offender Information Section-->
		<xsl:apply-templates select="off.info.b[nm.b]"/>
		<!-- Arrest Information Section-->
		<xsl:apply-templates select="arr.info.b"/>
		<!-- Bail Information Section-->
		<xsl:apply-templates select="bail.info.b"/>
		<xsl:if test="restrict != 'SOR' or not(restrict)">
			<!-- Court & Case Information Section-->
			<xsl:apply-templates select="crt.info.b"/>
		</xsl:if>

		<!-- Defendant Information Section-->
		<xsl:apply-templates select="def.info.b"/>
		<!-- Other Information Section-->
		<xsl:apply-templates select="oth.info.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:if test="restrict = 'SOR'">
			<!-- Court & Case Information Section-->
			<xsl:apply-templates select="crt.info.b"/>
		</xsl:if>
		<!-- Current Charge or Offense Information Section -->
		<xsl:apply-templates select="chrg.info.b"/>
		<!-- Plea Information Section -->
		<xsl:apply-templates select="plea.info.b"/>
		<!-- Sentence Information Section -->
		<xsl:apply-templates select="sen.info.b"/>
		<!-- Incarceration Information Section -->
		<xsl:apply-templates select="loc.info.b"/>
		<!-- Inmate Discipline Information Section -->
		<xsl:apply-templates select="disc.info.b"/>
		<!-- Release Information Section -->
		<xsl:apply-templates select="rlse.info.b"/>
		<!-- Parole Information Section -->
		<xsl:apply-templates select="parl.info.b"/>
		<!-- Probation Information Section -->
		<xsl:apply-templates select="prob.info.b"/>
		<!-- Prior Charge or Offense Information Section -->
		<xsl:apply-templates select="pri.chrg.info.b"/>
		<!-- Miscellaneous Information Section -->
		<xsl:apply-templates select="misc.b"/>
		<xsl:apply-templates select="off.info.b[not(nm.b)]">
			<xsl:sort select="off.seq.nbr" data-type="number"/>
		</xsl:apply-templates>
		<xsl:if test="off.info.b[nm.b]">
			<!-- Display of Discalimer Message -->
			<xsl:call-template name="DisclaimerMessage"/>
		</xsl:if>
		<!-- Display of Order Documents Message -->
		<xsl:call-template name="outputOrderDocumentsSection"/>
	</xsl:template>

	<xsl:template name="DisplayHeading">
		<xsl:param name="HeadingText"/>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="$HeadingText" />
		</xsl:call-template>
	</xsl:template>


	<!-- **********************************************************************
	********************** "Source Information" section *********************
	************************************************************************-->
	<xsl:template match="CoverageMeta" priority="1">
		<xsl:variable name="currentThroughDateLabel">
			<xsl:choose>
				<xsl:when test="(starts-with(col.key, 'AKAOC')) or (starts-with(col.key, 'ARAOC')) or (starts-with(col.key, 'AZAOC')) or (starts-with(col.key, 'CAAOC')) or
									(starts-with(col.key, 'ILAOC')) or (starts-with(col.key, 'MIAOC')) or (starts-with(col.key, 'MSAOC')) or (starts-with(col.key, 'NCAOC')) or
									(starts-with(col.key, 'NDAOC')) or (starts-with(col.key, 'NJAOC')) or (starts-with(col.key, 'OKAOC')) or (starts-with(col.key, 'ORAOC')) or
									(starts-with(col.key, 'RIAOC')) or (starts-with(col.key, 'TXAOC')) or (starts-with(col.key, 'UTAOC')) or (starts-with(col.key, 'VAAOC')) or
									(starts-with(col.key, 'WAAOC')) or (starts-with(col.key, 'IAAOC')) or (starts-with(col.key, 'LAAOCSTTAMMANY')) or (starts-with(col.key, 'TXAOCROCKWALL')) or
									(starts-with(col.key, 'OHAOCSANDUSKY')) or	(col.key='NCAOC') or	(col.key='CODOC') or	(col.key='DCDOC') or	(col.key='GADOC') or	(col.key='MDDOC') or
									(col.key='MNDOC') or	(col.key='NCDOC') or	(col.key='NMDOC') or	(col.key='NVDOC') or	(col.key='OKDOC') or	(col.key='WIDOC') or	(col.key='AZPIMA') or
									(col.key='OFAC') or (col.key='OKDPS') or (col.key='FLDUVAL') or (col.key='FLPINELLAS') or (col.key='TXBEXAR') or (col.key='TXCHAMBER') or
									(col.key='TXFTBEND') or (col.key='TXMONTGOMERY') or	(col.key='TXTRAVIS')">
					<xsl:text>&pr_filingsCurrentThrough;</xsl:text>
				</xsl:when>
				<xsl:when test="(col.key='AZDOC') or (col.key='CTAOC') or (col.key='CTDOC') or (starts-with(col.key, 'FLAOC')) or (col.key='FLDOC') or (col.key='IADOC') or
									(col.key='KSDOC') or (col.key='KYDOC') or (col.key='MEDOC') or (col.key='MIDOC') or (col.key='MTDOC') or (col.key='PAAOC') or (col.key='TNAOC') or
									(col.key='VAAOC') or contains(col.key, 'AOC') or contains(col.key, 'DOC') or contains(col.key, 'GBI') or contains(col.key, 'DPS')">
					<xsl:text>&pr_offenseInformationCurrentThrough;</xsl:text>
				</xsl:when>
				<xsl:when test="(col.key='INDOC') or (col.key='MODOC') or (col.key='MSDOC') or (col.key='NEDOC') or (col.key='TXDPS') or (col.key='UTDOC') or (col.key='WADOC') or
									(col.key='TXAOCHAR')">
					<xsl:text>&pr_sentenceInformationCurrentThrough;</xsl:text>
				</xsl:when>
				<xsl:when test="(col.key='IDDOC') or (col.key='ILDOC') or (col.key='MNDPS') or (col.key='NHDOC') or (col.key='NJDOC') or (col.key='NYDOC') or (col.key='NYDOCP') or
									(col.key='OHDOC') or (col.key='ORDOC') or (col.key='SCDOC') or (col.key='ARDOC') or (col.key='TNDOC')">
					<xsl:text>&pr_admissionsInformationCurrentThrough;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<!-- The legacy xsl does not handle this scenario. -->
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
			<xsl:with-param name="currentThroughDateLabel" select="$currentThroughDateLabel"/>
			<xsl:with-param name="displayDatabaseLastUpdated" select="false()"/>
			<xsl:with-param name="displayUpdateFrequency" select="false()"/>
			<xsl:with-param name="displayCurrentDate" select="false()"/>
			<xsl:with-param name="displaySource" select="false()"/>
		</xsl:apply-templates>

		<!-- Displayed field: Inmate Status Date -->
		<xsl:apply-templates select="scrp.d[normalize-space(.)]">
			<xsl:with-param name="label" select="'&pr_inmateStatusDate;'"/>
		</xsl:apply-templates>

		<!-- Displayed field: Record Current Through -->
		<xsl:if test="normalize-space(scrp.d) and (col.key='FEDAOC' or col.key='FEDCRIM')">
			<xsl:apply-templates select="scrp.d">
				<xsl:with-param name="label" select="'&pr_recordCurrentThrough;'"/>
			</xsl:apply-templates>
		</xsl:if>

		<!-- Displayed field: Database Updated -->
		<xsl:variable name="databaseLastUpdatedLabel">
			<xsl:choose>
				<xsl:when test="col.key != 'FEDAOC' and col.key!='FEDCRIM'">
					<xsl:text>&pr_fileLastUpdated;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_sourceLastUpdated;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:apply-templates select="DatabaseLastUpdated">
			<xsl:with-param name="Label" select="databaseLastUpdatedLabel"/>
		</xsl:apply-templates>

		<!-- Displayed field: Current Date -->
		<xsl:apply-templates select="CurrentDate"/>

		<!-- Displayed field: Source -->
		<xsl:choose>
			<xsl:when test="off.info.b[nm.b]">
				<xsl:apply-templates select="Source"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="Source"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Inmate Status Date -->
	<xsl:template match="scrp.d">
		<xsl:param name="label"/>
		<xsl:if test="($label = '&pr_inmateStatusDate;' and (($colKey='PADOC') or ($colKey='ALDOC') or ($colKey='PADOC1'))) or ($label != '&pr_inmateStatusDate;')">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="$label"/>
				<xsl:with-param name="nodeType" select="$DATE"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- **********************************************************************
	********************** "Offender Information" section *********************
	************************************************************************-->

	<!-- Offender Information - (block) -->
	<xsl:template match="off.info.b[nm.b]">
		<xsl:variable name="subheaderLabel">
			<xsl:choose>
				<xsl:when test="($sourceType='OFAC')">
					<xsl:text>&pr_designatedNationalOrBlockedPerson;</xsl:text>
				</xsl:when>
				<xsl:when test="(starts-with($colKey, 'AKAOC')) or (starts-with($colKey, 'ARAOC')) or (starts-with($colKey, 'AZAOC')) or (starts-with($colKey, 'CAAOC')) or
									(starts-with($colKey, 'CTAOC')) or (starts-with($colKey, 'FLAOC')) or (starts-with($colKey, 'ILAOC')) or (starts-with($colKey, 'MDAOC')) or
									(starts-with($colKey, 'MIAOC')) or (starts-with($colKey, 'MSAOC')) or (starts-with($colKey, 'NCAOC')) or (starts-with($colKey, 'NDAOC')) or
									(starts-with($colKey, 'NJAOC')) or (starts-with($colKey, 'NVAOC')) or (starts-with($colKey, 'OKAOC')) or (starts-with($colKey, 'ORAOC')) or
									(starts-with($colKey, 'PAAOC')) or (starts-with($colKey, 'RIAOC')) or (starts-with($colKey, 'TNAOC')) or (starts-with($colKey, 'TXAOC')) or
									(starts-with($colKey, 'UTAOC')) or (starts-with($colKey, 'VAAOC')) or (starts-with($colKey, 'WAAOC')) or (starts-with($colKey, 'IAAOC')) or
									(starts-with($colKey, 'LAAOCSTTAMMANY')) or (starts-with($colKey, 'TXAOCROCKWALL')) or (starts-with($colKey, 'OHAOCSANDUSKY')) or ($colKey='AZPIMA') or
									(col.key='FEDAOC' or col.key='FEDCRIM') or ($colKey='FLDUVAL') or ($colKey='FLPINELLAS') or ($colKey='TXBEXAR') or ($colKey='TXCHAMBER') or ($colKey='TXFTBEND') or
									($colKey='TXMONTGOMERY') or ($colKey='TXTRAVIS')">
					<xsl:text>&pr_defendantInformation;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_offenderInformation;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="$subheaderLabel" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="nm.b"/>
			<xsl:apply-templates select="addr.b"/>
			<xsl:apply-templates select="crim.typ"/>
			<xsl:apply-templates select="sanc.cntry"/>
			<xsl:apply-templates select="fbi.nbr"/>
			<xsl:apply-templates select="st.id.nbr"/>
			<xsl:apply-templates select="dl.nbr"/>
			<xsl:apply-templates select="dl.st"/>
			<xsl:apply-templates select="dob"/>
			<xsl:apply-templates select="birth.cnty"/>
			<xsl:apply-templates select="birth.st"/>
			<xsl:apply-templates select="birth.cntry.b"/>
			<xsl:apply-templates select ="passport.b"/>
			<xsl:apply-templates select="ssn.b/ssn"/>
			<xsl:apply-templates select="nat.id.b"/>
			<xsl:apply-templates select="ctzn"/>
			<xsl:apply-templates select="aka.b"/>
			<xsl:apply-templates select="aln.b"/>
			<xsl:apply-templates select="phys.char.b"/>
			<xsl:apply-templates select="image.block"/>
			<xsl:apply-templates select="nxt.kin.addr"/>
			<xsl:apply-templates select="edu"/>
			<xsl:apply-templates select="mrtl.sts"/>
			<xsl:apply-templates select="dpnd.nbr"/>
			<xsl:apply-templates select="occup"/>
			<xsl:apply-templates select="mil.brnch"/>
			<xsl:apply-templates select="mil.dis.d"/>
			<xsl:apply-templates select="mil.dis.typ"/>
			<xsl:apply-templates select="vsl.own"/>
			<xsl:apply-templates select="vsl.call.sign"/>
			<xsl:apply-templates select="vsl.cntry"/>
			<xsl:apply-templates select="vsl.typ"/>
			<xsl:apply-templates select="vsl.tonn"/>
			<xsl:apply-templates select="vsl.grs.tonn"/>
			<xsl:apply-templates select="misc.info"/>
			<xsl:apply-templates select="veh.info.b"/>
			<xsl:apply-templates select="off.id"/>
		</table>
	</xsl:template>

	<!-- Offender Information - (block) -->
	<xsl:template match="off.info.b[not(nm.b)]">
		<xsl:variable name="subheaderLabel">
			<xsl:text>&pr_offensePrefix;<![CDATA[ ]]></xsl:text>
			<xsl:value-of select="off.seq.nbr"/>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="$subheaderLabel" />
		</xsl:call-template>

		<table class="&pr_table;">
			<xsl:apply-templates select="off.seq.nbr"/>
			<xsl:apply-templates select="warr.d"/>
			<xsl:apply-templates select="warr.desc"/>
			<xsl:apply-templates select="warr.iss.d"/>
			<xsl:apply-templates select="warr.flg"/>
			<xsl:apply-templates select="cit.nbr"/>
			<xsl:apply-templates select="bk.nbr"/>
			<xsl:apply-templates select="arr.d"/>
			<xsl:apply-templates select="arr.agcy"/>
			<xsl:apply-templates select="bk.d"/>
			<xsl:apply-templates select="cust.d"/>
			<xsl:apply-templates select="cust.loc"/>
			<xsl:apply-templates select="init.chrg"/>
			<xsl:apply-templates select="init.chrg.d"/>
			<xsl:apply-templates select="disp.chrg.d"/>
			<xsl:apply-templates select="disp.chrg"/>
			<xsl:apply-templates select="amd.chrg"/>
			<xsl:apply-templates select="amd.chrg.d"/>
			<xsl:apply-templates select="bailer"/>
			<xsl:apply-templates select="bail.amt"/>
			<xsl:apply-templates select="bail.typ"/>
			<xsl:apply-templates select="oth.info.b/judge.b"/>
			<xsl:apply-templates select="cs.nbr"/>
			<xsl:if test="not(normalize-space(cs.nbr))">
				<xsl:apply-templates select="dkt.nbr"/>
			</xsl:if>
			<xsl:apply-templates select="cs.title"/>
			<xsl:apply-templates select="cs.typ"/>
			<xsl:apply-templates select="cs.stat"/>
			<xsl:apply-templates select="cs.stat.d"/>
			<xsl:apply-templates select="cs.cmnt"/>
			<xsl:apply-templates select="chrg.file.d"/>
			<xsl:apply-templates select="cs.info"/>
			<xsl:apply-templates select ="stat.viol"/>
			<xsl:apply-templates select="crim.off"/>
			<xsl:apply-templates select="crim.d"/>
			<xsl:apply-templates select="cs.cat"/>
			<xsl:apply-templates select="crim.cls"/>
			<xsl:apply-templates select="off.stat"/>
			<xsl:apply-templates select="off.stat.d"/>
			<xsl:apply-templates select="disp.fndg"/>
			<xsl:apply-templates select="disp.d"/>
			<xsl:apply-templates select="off.loc"/>
			<xsl:apply-templates select="amd.crim.off"/>
			<xsl:apply-templates select="amd.crim.d"/>
			<xsl:apply-templates select="facts"/>
			<xsl:apply-templates select="orig.plea"/>
			<xsl:apply-templates select="plea.d"/>
			<xsl:apply-templates select="ver"/>
			<xsl:apply-templates select="ver.d"/>
			<xsl:apply-templates select="app.stat"/>
			<xsl:apply-templates select="app.d"/>
			<xsl:apply-templates select="court"/>
			<xsl:apply-templates select="cnty.jur"/>
			<xsl:apply-templates select="judge"/>
			<xsl:apply-templates select="fne.amt"/>
			<xsl:apply-templates select="fee.amt"/>
			<xsl:apply-templates select="rst.amt"/>
			<xsl:apply-templates select="typ.trial"/>
			<xsl:apply-templates select="argmnt.d"/>
			<xsl:apply-templates select="sen.imp.d"/>
			<xsl:apply-templates select="sen.beg.d"/>
			<xsl:apply-templates select="sen.exp.d"/>
			<xsl:apply-templates select="sen.desc"/>
			<xsl:apply-templates select="sen.max" mode="sentence_maximum"/>
			<xsl:apply-templates select="sen.min" mode="sentence_minimum"/>
			<xsl:apply-templates select ="proj.rlse.d"/>
			<xsl:apply-templates select="act.rlse.d"/>
			<xsl:apply-templates select="sen.stat"/>
			<xsl:apply-templates select="time.srv"/>
			<xsl:apply-templates select="pub.srv.hrs"/>
			<xsl:apply-templates select="sen.info"/>
			<xsl:apply-templates select="comm.sup.cnty"/>
			<xsl:apply-templates select="comm.sup"/>
			<xsl:apply-templates select="prl.beg.d"/>
			<xsl:apply-templates select="prl.rlse.d"/>
			<xsl:apply-templates select="prl.elg.d"/>
			<xsl:apply-templates select="prl.hrng.d"/>
			<xsl:apply-templates select="prl.trm" mode="parole_term"/>
			<xsl:apply-templates select="prl.stat"/>
			<xsl:apply-templates select="prl.offr"/>
			<xsl:apply-templates select="prl.offr.phn"/>
			<xsl:apply-templates select="prob.beg.d"/>
			<xsl:apply-templates select="prob.end.d"/>
			<xsl:apply-templates select="prob.time" mode="probation_time"/>
			<xsl:apply-templates select="min.prob.time"/>
			<xsl:apply-templates select="prob.stat"/>
			<xsl:apply-templates select="pri.cs.nbr"/>
			<xsl:apply-templates select="pri.off"/>
			<xsl:apply-templates select="pri.crim.d"/>
			<xsl:apply-templates select="pri.cs.cat"/>
			<xsl:apply-templates select="pri.crim.cls"/>
			<xsl:apply-templates select="pri.disp.fndg"/>
			<xsl:apply-templates select="pri.disp.d"/>
			<xsl:apply-templates select="pri.sen.imp.d"/>
			<xsl:apply-templates select="pri.sen.beg.d"/>
			<xsl:apply-templates select="pri.sen.exp.d"/>
			<xsl:apply-templates select="pri.sen.desc"/>
			<xsl:apply-templates select="pri.sen.max"/>
			<xsl:apply-templates select="pri.sen.min"/>
			<xsl:apply-templates select="pri.sch.rel.d"/>
			<xsl:apply-templates select="pri.act.rel.d"/>
			<xsl:apply-templates select="pri.stat"/>
			<xsl:apply-templates select="pri.comm.sup.cnty"/>
			<xsl:apply-templates select="pri.comm.sup"/>
			<xsl:apply-templates select="oth.info.b"/>
		</table>
	</xsl:template>

	<!-- Offender Name -->
	<xsl:template match="nm.b">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="($sourceType='OFAC') or ((starts-with($colKey, 'AKAOC')) or (starts-with($colKey, 'ARAOC')) or (starts-with($colKey, 'AZAOC')) or
										(starts-with($colKey, 'CAAOC')) or (starts-with($colKey, 'CTAOC')) or (starts-with($colKey, 'FLAOC')) or (starts-with($colKey, 'ILAOC')) or
										(starts-with($colKey, 'MDAOC')) or (starts-with($colKey, 'MIAOC')) or (starts-with($colKey, 'MSAOC')) or (starts-with($colKey, 'NCAOC')) or
										(starts-with($colKey, 'NDAOC')) or (starts-with($colKey, 'NJAOC')) or (starts-with($colKey, 'OKAOC')) or (starts-with($colKey, 'ORAOC')) or
										(starts-with($colKey, 'PAAOC')) or (starts-with($colKey, 'RIAOC')) or (starts-with($colKey, 'TNAOC')) or (starts-with($colKey, 'TXAOC')) or
										(starts-with($colKey, 'UTAOC')) or (starts-with($colKey, 'VAAOC')) or (starts-with($colKey, 'WAAOC')) or (starts-with($colKey, 'IAAOC')) or
										(starts-with($colKey, 'LAAOCSTTAMMANY')) or (starts-with($colKey, 'TXAOCROCKWALL')) or (starts-with($colKey, 'OHAOCSANDUSKY')) or ($colKey='AZPIMA') or
										(col.key='FEDAOC' or col.key='FEDCRIM') or ($colKey='FLDUVAL') or ($colKey='FLPINELLAS') or ($colKey='TXBEXAR') or ($colKey='TXCHAMBER') or ($colKey='TXFTBEND') or
										($colKey='TXMONTGOMERY') or ($colKey='TXTRAVIS'))">
					<xsl:text>&pr_name;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_offenderName;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="fl.nm">
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="$label"/>
					<xsl:with-param name="firstName" select="fl.nm"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="$label"/>
					<xsl:with-param name="firstName" select="fst.nm"/>
					<xsl:with-param name="lastName" select="lst.nm"/>
					<xsl:with-param name="middleName" select="mid.nm"/>
					<xsl:with-param name="suffixName" select="suf.nm"/>
					<xsl:with-param name="lastNameFirst" select="true()"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Criminal Type -->
	<xsl:template match="crim.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_type;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sanction Program -->
	<xsl:template match="sanc.cntry">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sanctionProgram;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- FBI Number -->
	<xsl:template match="fbi.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fbiNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- State ID Number -->
	<xsl:template match="st.id.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateIdNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Driver's License Number -->
	<xsl:template match="dl.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_driversLicenseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Driver's License State -->
	<xsl:template match="dl.st">
		<xsl:variable name="state">
			<xsl:text>&pr_driversLicenseState;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$state"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date of Birth -->
	<xsl:template match="dob">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- County of Birth -->
	<xsl:template match="birth.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countyOfBirth;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- State of Birth -->
	<xsl:template match="birth.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateOfBirth;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Country of Birth -->
	<xsl:template match="birth.cntry[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countryOfBirth;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Citizen -->
	<xsl:template match="ctzn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_citizenship;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Last Known Address -->
	<xsl:template match="addr.b | curr.addr">
		<xsl:if test="normalize-space(def.str) or normalize-space(def.cty) or normalize-space(def.st) or normalize-space(def.zip)">
			<xsl:call-template name="wrapPublicRecordsAddress">
				<xsl:with-param name="label" select="'&pr_lastKnownAddress;'"/>
				<xsl:with-param name="street" select="def.str"/>
				<xsl:with-param name="streetUnitNumber" select="def.unit"/>
				<xsl:with-param name="city" select="def.cty"/>
				<xsl:with-param name="stateOrProvince" select="def.st"/>
				<xsl:with-param name="zip" select="def.zip"/>
				<xsl:with-param name="zipExt" select="def.zip.ext"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates select="def.cnty"/>
		<xsl:apply-templates select="add.ver.d | addr.ver.d"/>
	</xsl:template>

	<xsl:template match="def.unit | nxt.kin.unit | def.aty.unit">
		<xsl:text>&pr_apartment;<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="def.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastKnownCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="add.ver.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastAddressVerification;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Also Known As: Name -->
	<xsl:template match="aka.nm.b">
		<xsl:choose>
			<xsl:when test="aka.fl.nm">
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="'&pr_akaName;'"/>
					<xsl:with-param name="firstName" select="aka.fl.nm"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="'&pr_akaName;'"/>
					<xsl:with-param name="firstName" select="aka.fst.nm"/>
					<xsl:with-param name="middleName" select="aka.mid.nm"/>
					<xsl:with-param name="lastName" select="aka.lst.nm"/>
					<xsl:with-param name="suffixName" select="aka.suf.nm"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="(/Document/n-docbody/r/pre/s/s.st = 'MN') or (/Document/n-docbody/r/pre/s/s.st/child::node()/text())">
			<xsl:if test="(contains(aka.lst.nm, '***')) or (contains(aka.fst.nm, '***'))">
				<tr class="&pr_item;">
					<th/>
					<td>
						<xsl:text>
							***CAUTION:	 An individual with this name has submitted fingerprints that indicate that he or she is NOT the subject of this record.
							The Minnesota Bureau of Criminal Apprehension has issued a letter to this individual, confirming he or she is NOT the subject of this record.
							Fingerprint verification is the only way to confirm that an individual is or is not the subject of a record.
						</xsl:text>
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Also Known As: Date of Birth -->
	<xsl:template match="aka.b/aka.dob">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_akaDateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Alien Information: Name -->
	<xsl:template match="aln.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_alienName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Alien Information: Number -->
	<!-- if value is not equal to 000000000 -->
	<xsl:template match="aln.nbr[number(.) != 0]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_alienNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Physical Character Information -->
	<xsl:template match="phys.char.b">
		<xsl:apply-templates select="sex"/>
		<xsl:apply-templates select="race"/>
		<xsl:apply-templates select="ethnic"/>
		<xsl:apply-templates select="eyes"/>
		<xsl:apply-templates select="hair"/>
		<xsl:apply-templates select="skin"/>
		<xsl:apply-templates select="bld"/>
		<xsl:apply-templates select="ht.b"/>
		<xsl:apply-templates select="wt"/>
		<xsl:apply-templates select="oth.mrk.b"/>
	</xsl:template>

	<!-- Gender -->
	<xsl:template match="sex">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Race -->
	<xsl:template match="race">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_race;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Image -->
	<xsl:template match="image.block" priority="1">
		<tr>
			<td colspan="2">
				<xsl:call-template name="imageBlock">
					<xsl:with-param name="suppressNoImageDisplay" select="true()"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<!-- Ethnicity -->
	<xsl:template match="ethnic">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ethnicity;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Eye Color -->
	<xsl:template match="eyes">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_eyeColor;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Hair Color -->
	<xsl:template match="hair">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hairColor;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Skin Color: -->
	<xsl:template match="skin">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_skinColor;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Build -->
	<xsl:template match="bld">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_build;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Height -->
	<xsl:template match="ht.b | ht">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_height;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ht.ft">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]>&pr_ft;</xsl:text>
	</xsl:template>

	<xsl:template match="ht.in">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]>&pr_in;</xsl:text>
	</xsl:template>

	<!-- Weight -->
	<xsl:template match="wt">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_weight;</xsl:text>
			</th>
			<td>
				<xsl:choose>
					<xsl:when test ="contains(., 'lbs')">
						<xsl:value-of select ="substring-before(., 'lbs')"/>
					</xsl:when>
					<xsl:when test ="contains(., 'LBS')">
						<xsl:value-of select ="substring-before(., 'LBS')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text><![CDATA[ ]]>&pr_lbs;</xsl:text>
			</td>
		</tr>
	</xsl:template>

	<!-- Scars and Tattoos -->
	<xsl:template match="oth.mrk.b[position()=1]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_scarsAndTattoos;'"/>
			<xsl:with-param name="selectNodes" select="oth.mrk | following-sibling::oth.mrk.b/oth.mrk"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="oth.mrk.b"/>

	<xsl:template match="oth.mrk">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Vehicle Information -->
	<xsl:template match="veh.info.b | veh.b">
		<xsl:call-template name="VehicleInfo"/>
		<xsl:apply-templates select="veh.lic.plt"/>
		<xsl:apply-templates select="veh.lic.st"/>
	</xsl:template>

	<!-- Vehicle Information (block) -->
	<xsl:template name="VehicleInfo">
		<xsl:if test="veh.yr or veh.clr or veh.mk or veh.mdl">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_vehicleInformationLabel;</xsl:text>
				</th>
				<td>
					<xsl:apply-templates select="veh.yr"/>
					<xsl:apply-templates select="veh.clr"/>
					<xsl:apply-templates select="veh.mk"/>
					<xsl:apply-templates select="veh.mdl"/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="veh.lic.plt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vehicleLicensePlate;'"/>
			<xsl:with-param name="nodeType" select="$LICENSEPLATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="veh.lic.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vehicleLicenseState;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Vehicle Information: Year	-->
	<xsl:template match="veh.yr[normalize-space(.)]">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Vehicle Information: Color	 -->
	<xsl:template match="veh.clr[normalize-space(.)]">
		<xsl:if test="normalize-space(preceding-sibling::veh.yr)">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Vehicle Information: Make	-->
	<xsl:template match="veh.mk[normalize-space(.)]">
		<xsl:if test="normalize-space(preceding-sibling::veh.yr) or normalize-space(preceding-sibling::veh.clr)">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Vehicle Information: Model	 -->
	<xsl:template match="veh.mdl[normalize-space(.)]">
		<xsl:if test="normalize-space(preceding-sibling::veh.yr) or normalize-space(preceding-sibling::veh.clr) or normalize-space(preceding-sibling::veh.mk)">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Next of Kin: Name	-->
	<xsl:template match="nxt.kin.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nextOfKin;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Next of Kin: Address  -->
	<xsl:template match="nxt.kin.addr[name(..)='off.info.b']">
		<!-- NOTE: Even though this says it's an address, it appears to be one entry, so just call the wrapPublicRecordsItem template. -->
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_associatedAddress;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Education Completed  -->
	<xsl:template match="edu">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_educationCompleted;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Marital Status -->
	<xsl:template match="mrtl.sts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_maritalStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Number of Dependants -->
	<xsl:template match="dpnd.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfDependants;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Occupation -->
	<xsl:template match="occup">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_occupation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Branch of Military Service -->
	<xsl:template match="mil.brnch">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_branchOfMilitaryService;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Military Discharge Date -->
	<xsl:template match="mil.dis.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_militaryDischargeDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Military Discharge Type -->
	<xsl:template match="mil.dis.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_militaryDischargeType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Vessel Owner -->
	<xsl:template match="vsl.own">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vesselOwner;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Vessel Call Sign -->
	<xsl:template match="vsl.call.sign">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vesselCallSign;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Vessel Country -->
	<xsl:template match="vsl.cntry">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vesselCountry;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Vessel Type -->
	<xsl:template match="vsl.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vesselType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Vessel Tonnage -->
	<xsl:template match="vsl.tonn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vesselTonnage;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Vessel Gross Registered Tonnage -->
	<xsl:template match="vsl.grs.tonn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vesselGrossRegisteredTonnage;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- Miscellaneous Information -->
	<xsl:template match="misc.info">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_miscellaneousInformation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Offender ID -->
	<xsl:template match="off.id">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_offenderId;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	******************** (B)"Arrest Information" section **********************
	************************************************************************-->

	<!-- Arrest Information - (block) -->
	<xsl:template match="arr.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_arrestInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Arrest Date -->
	<xsl:template match="arr.d">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="preceding-sibling::arr.d">
					<xsl:text>&pr_dateOfRearrest;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_dateOfArrest;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Name Used at Arrest -->
	<xsl:template match="arr.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nameUsedAtArrest;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Arrest Number -->
	<xsl:template match="arr.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_arrestNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Arrest Description -->
	<xsl:template match="arr.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_arrestDescription;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Arrest Agency -->
	<xsl:template match="arr.agcy">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_arrestingAgency;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Arrest County -->
	<xsl:template match="arr.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_arrestCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Arrest Disposition -->
	<xsl:template match="arr.disp">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_arrestDisposition;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Arrest Warrant -->
	<xsl:template match="warr.flag">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_arrestWarrant;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Agreement Date -->
	<xsl:template match="agmnt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_agreementDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Cancel Date -->
	<xsl:template match="can.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_cancelDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	********************** (C)"Bail Information" section **********************
	************************************************************************-->

	<!-- Bail Information - (block) -->
	<xsl:template match="bail.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_bailInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Type and Amount of Bail Set -->
	<xsl:template match="bail.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_typeAndAmountOfBailSet;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date Bail Set -->
	<xsl:template match="bail.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateBailSet;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Type and Amount of Bail Posted -->
	<xsl:template match="bail.post.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_typeAndAmountOfBailPosted;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date Bail Posted -->
	<xsl:template match="bail.post.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateBailPosted;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Person Posting Bail -->
	<!-- NOTE: Even though this says it's an name, it appears to be one entry, so just call the wrapPublicRecordsItem template. -->
	<xsl:template match="bailer">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_personPostingBail;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	****************** (D)"COURT & CASE INFORMATION" section ******************
	************************************************************************-->

	<!-- Court & Case Information - (block) -->
	<xsl:template match="crt.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_courtAndCaseInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="crt.b"/>
			<xsl:apply-templates select="crt.cnty"/>
			<xsl:apply-templates select="crt.st"/>
			<xsl:apply-templates select="judge"/>
			<xsl:apply-templates select="cs.b"/>
			<xsl:apply-templates select="cs.cat"/>
			<xsl:apply-templates select="amd.cs.typ"/>
			<xsl:apply-templates select="cs.tran.cd"/>
			<xsl:apply-templates select="cs.yr"/>
			<xsl:apply-templates select="pre.hrng"/>
			<xsl:apply-templates select="argnmt.d"/>
			<xsl:apply-templates select="chrg.file.d"/>
			<xsl:apply-templates select="fnl.pldng"/>
			<xsl:apply-templates select="typ.trial"/>
			<xsl:apply-templates select="ver"/>
			<xsl:apply-templates select="ver.d"/>
			<xsl:apply-templates select="." mode="disposition" />
			<xsl:apply-templates select="disp.cmnt"/>
			<xsl:apply-templates select="def.aty.nm"/>
			<xsl:apply-templates select="atty.typ"/>
			<xsl:apply-templates select="atty.lic.nbr"/>
			<xsl:apply-templates select="def.aty.b"/>
			<xsl:apply-templates select="prsctr.nm"/>
			<xsl:apply-templates select="prsctr.aty.b"/>
			<xsl:apply-templates select="amt.info.b"/>
		</table>
	</xsl:template>

	<!-- Court -->
	<xsl:template match="court">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_court;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Original Court -->
	<xsl:template match="ori.crt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalCourt;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Court Division -->
	<xsl:template match="crt.div">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_courtDivision;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- District or Circuit -->
	<xsl:template match="dist.cir">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_districtOrCircuit;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Court County -->
	<xsl:template match="crt.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_courtCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Court State -->
	<xsl:template match="crt.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_courtState;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Judge -->
	<xsl:template match="judge">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_judge;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="judge/cite.query">
		<xsl:if test="normalize-space(../text())">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:call-template name="citeQuery"/>
	</xsl:template>

	<!-- Case Information -->
	<xsl:template match="cs.b">
		<!-- Case Title -->
		<xsl:apply-templates select="cs.title"/>

		<!-- Case Numbers -->
		<xsl:apply-templates select="cs.nbr"/>

		<!-- Court File Number -->
		<xsl:apply-templates select="crt.file.nbr"/>
	</xsl:template>

	<!-- Case Title -->
	<xsl:template match="cs.title">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseTitle;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Case Number -->
	<xsl:template match="cs.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Court File Number -->
	<xsl:template match="crt.file.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_courtFileNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Case Type or Category -->
	<xsl:template match="cs.cat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseTypeOrCategory;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Amended Case Type -->
	<xsl:template match="amd.cs.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_amendedCaseType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Case Transfer Code -->
	<xsl:template match="cs.tran.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseTransferCode;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Case Year -->
	<xsl:template match="cs.yr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseYear;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Preliminary Hearing -->
	<xsl:template match="pre.hrng">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_preliminaryHearing;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Arraignment Date -->
	<xsl:template match="argnmt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_arraignmentDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Charges Filed Date -->
	<xsl:template match="chrg.file.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_chargesFiledDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Final Pleading -->
	<xsl:template match="fnl.pldng">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_finalPleading;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Type of Trial -->
	<xsl:template match="typ.trial">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_typeOfTrial;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Verdict -->
	<xsl:template match="ver">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_verdict;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Verdict Date -->
	<xsl:template match="ver.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_verdictDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Disposition -->
	<xsl:template match="crt.info.b" mode="disposition">
		<xsl:if test="disp.fndg or disp.d">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_disposition;</xsl:text>
				</th>
				<td>
					<xsl:apply-templates select="disp.fndg"/>
					<xsl:apply-templates select="disp.d"/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="disp.d">
		<xsl:if test="normalize-space(preceding-sibling::disp.fndg) or normalize-space(following-sibling::disp.fndg)">
			<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:call-template name="FormatNonSensitiveDate"/>
	</xsl:template>

	<!-- Disposition Comment -->
	<xsl:template match="disp.cmnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dispositionComment;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Defense Attorney Name -->
	<xsl:template match="def.aty.nm[name(../../..) != 'off.info.b']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_attorney;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Attorney Type -->
	<xsl:template match="atty.typ | def.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_attorneyType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Attorney License Number -->
	<xsl:template match="atty.lic.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_attorneyLicenseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Defense Attorney Information -->
	<xsl:template match="def.aty.b">
		<xsl:choose>
			<xsl:when test="name(..)='oth.info.b'">
				<xsl:if test="normalize-space(def.aty.nm)">
					<xsl:call-template name="wrapPublicRecordsItem">
						<xsl:with-param name="defaultLabel" select="'&pr_defendantAttorney;'"/>
						<xsl:with-param name="selectNodes" select="def.aty.nm"/>
					</xsl:call-template>
				</xsl:if>

				<xsl:apply-templates select="atty.typ"/>

				<!--Attorney Address  -->
				<xsl:apply-templates select="def.aty.addr"/>

				<!--Attorney Phone  -->
				<xsl:apply-templates select="def.atty.phn"/>
			</xsl:when>
			<xsl:otherwise>
				<tr class="&pr_item;">
					<th>
						<xsl:text>&pr_attorney;</xsl:text>
					</th>
					<td>
						<xsl:apply-templates select="def.aty"/>
						<xsl:apply-templates select="def.firm"/>
						<xsl:apply-templates select="def.addr.b"/>
					</td>
				</tr>
				<xsl:apply-templates select="def.typ"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Defense Attorney: Firm -->
	<xsl:template match="def.firm">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Defense Attorney Address (block) -->
	<xsl:template match="def.addr.b">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="street" select="def.aty.str"/>
				<xsl:with-param name="city" select="def.aty.cty"/>
				<xsl:with-param name="stateOrProvince" select="def.aty.st"/>
				<xsl:with-param name="zip" select="def.aty.zip"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Prosecutor Name -->
	<xsl:template match="prsctr.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_prosecutor;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Prosecutor Attorney Information -->
	<xsl:template match="prsctr.aty.b">
		<xsl:apply-templates select="prsctr.agcy"/>
		<xsl:if test="prsctr.aty or prsctr.firm or prsctr.addr.b">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_prosecutor;'"/>
				<xsl:with-param name="selectNodes" select="prsctr.aty | prsctr.firm | prsctr.addr.b"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Prosecuting Agency -->
	<xsl:template match="prsctr.agcy">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_prosecutingAgency;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Prosecutor -->
	<xsl:template match="prsctr.firm">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Prosecutor Attorney Address (block) -->
	<xsl:template match="prsctr.addr.b">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="street" select="prsctr.aty.str"/>
				<xsl:with-param name="city" select="prsctr.aty.cty"/>
				<xsl:with-param name="stateOrProvince" select="prsctr.aty.st"/>
				<xsl:with-param name="zip" select="prsctr.aty.zip"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Fine Amount Ordered -->
	<xsl:template match="fne.amt[normalize-space(.)='YES' or normalize-space(.)='NO']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fineAmountOrdered;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fne.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fineAmountOrdered;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="styd.fne[normalize-space(.)='YES' or normalize-space(.)='NO']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fineStayed;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="styd.fne">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fineStayed;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Imprisonment (In lieu of fine) -->
	<xsl:template match="impris.ind">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_imprisonmentInLieuOfFine;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="crt.amt[normalize-space(.)='YES' or normalize-space(.)='NO']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_courtCosts;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="crt.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_courtCosts;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="crt.pymt[normalize-space(.)='YES' or normalize-space(.)='NO']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_courtOrderedPaymentAmount;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="crt.pymt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_courtOrderedPaymentAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="rst.amt[normalize-space(.)='YES' or normalize-space(.)='NO']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_restitutionAmountOrdered;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="rst.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_restitutionAmountOrdered;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fee.amt[normalize-space(.)='YES' or normalize-space(.)='NO']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_feeAmountOrdered;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fee.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_feeAmountOrdered;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="asmt.amt[normalize-space(.)='YES' or normalize-space(.)='NO']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_assessmentAmountOrdered;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="asmt.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_assessmentAmountOrdered;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sur.amt[normalize-space(.)='YES' or normalize-space(.)='NO']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_surchargeAmountOrdered;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sur.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_surchargeAmountOrdered;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="st.tran.amt[normalize-space(.)='YES' or normalize-space(.)='NO']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateTransportationFundPayment;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="st.tran.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateTransportationFundPayment;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>


	<!-- **********************************************************************
	************ (E)"CURRENT CHARGE OR OFFENSE INFORMATION" section ***********
	************************************************************************-->

	<!-- Current Charge Information - (block) -->
	<xsl:template match="chrg.info.b">
		<xsl:if test="not(preceding-sibling::chrg.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_currentChargeOrOffenseInfo;'" />
			</xsl:call-template>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="(col.key='FEDAOC' or col.key='FEDCRIM')">
				<table class="&pr_table;">
					<xsl:apply-templates select="crim.off"/>
					<xsl:apply-templates select="crim.cls"/>
					<xsl:apply-templates select="crim.disp"/>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<table class="&pr_table;">
					<xsl:apply-templates select="crim.d"/>
					<xsl:apply-templates select="crim.crt"/>
					<xsl:apply-templates select="crim.cnty"/>
					<xsl:apply-templates select="crim.twn"/>
					<xsl:apply-templates select="tot.crim"/>
					<xsl:apply-templates select="hgh.fel.cls"/>
					<xsl:apply-templates select="crim.cnts"/>
					<xsl:choose>
						<xsl:when test="$colKey!='VAAOC'">
							<xsl:apply-templates select="stat.viol"/>
							<xsl:apply-templates select="amd.stat.viol"/>
							<xsl:apply-templates select="crim.off"/>
							<xsl:apply-templates select="amd.crim.off"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="wrapStatuteViolatedAndCriminalOffense">
								<xsl:with-param name="label" select="'&pr_offenseCharged;'"/>
								<xsl:with-param name="statuteViolated" select="stat.viol"/>
								<xsl:with-param name="criminalOffense" select="crim.off"/>
							</xsl:call-template>
							<xsl:call-template name="wrapStatuteViolatedAndCriminalOffense">
								<xsl:with-param name="label" select="'&pr_offenseCharged;'"/>
								<xsl:with-param name="statuteViolated" select="amd.stat.viol"/>
								<xsl:with-param name="criminalOffense" select="amd.crim.off"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:apply-templates select="chrg.doc.typ"/>
					<xsl:apply-templates select="crim.cls"/>
					<xsl:apply-templates select="crim.disp"/>
					<xsl:apply-templates select="curr.sts"/>
					<xsl:apply-templates select="." mode="addl_info_crime"/>
					<xsl:apply-templates select="mnr.vic.age"/>
					<xsl:apply-templates select="vict.sx"/>
					<xsl:apply-templates select="cnvt.loc"/>
					<xsl:apply-templates select="rsk.lvl"/>
				</table>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="wrapStatuteViolatedAndCriminalOffense">
		<xsl:param name="label"/>
		<xsl:param name="statuteViolated" select="/.."/>
		<xsl:param name="criminalOffense" select="/.."/>
		<tr class="&pr_item;">
			<th>
				<xsl:value-of select="$label"/>
			</th>
			<td>
				<xsl:if test="normalize-space($statuteViolated)">
					<xsl:value-of select="(translate(normalize-space($statuteViolated), 'VA ST ', ''))"/>
				</xsl:if>
				<xsl:if test="(normalize-space($statuteViolated) and normalize-space($criminalOffense)) and (normalize-space($criminalOffense)!='7455' or name($criminalOffense)!='amd.crim.off')">
					<xsl:text> - </xsl:text>
				</xsl:if>
				<xsl:if test="normalize-space($criminalOffense)!='7455' or name($criminalOffense)!='amd.crim.off'">
					<xsl:apply-templates select="$criminalOffense"/>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!-- Offense Committed -->
	<xsl:template match="crim.off[/Document/n-docbody/r/col.key!='VAAOC' or name(..)='off.info.b']">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="(col.key='FEDAOC' or col.key='FEDCRIM')">
					<xsl:text>&pr_offenseCommitted;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_offenseCharged;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Charging Document Type -->
	<xsl:template match="chrg.doc.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_chargingDocumentType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Class of Offense -->
	<xsl:template match="crim.cls">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_classOfOffense;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Offense Disposition -->
	<xsl:template match="crim.disp">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_offenseDisposition;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>

				<xsl:if test="/Document/n-docbody/r/crt.info.b/disp.d">
					<xsl:text> - </xsl:text>
					<xsl:apply-templates select="/Document/n-docbody/r/crt.info.b/disp.d"/>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>


	<!-- Date of Offense -->
	<xsl:template match="crim.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfOffense;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Court -->
	<xsl:template match="crim.crt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_court;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Location of Offense -->
	<xsl:template match="crim.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_locationOfOffense;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Town Where Offense Committed -->
	<xsl:template match="crim.twn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_townWhereOffenseCommitted;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Total Number of Offenses -->
	<xsl:template match="tot.crim">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalNumberOfOffenses;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Highest Felony Class -->
	<xsl:template match="hgh.fel.cls">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_highestFelonyClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Number of Counts/Crimes -->
	<xsl:template match="crim.cnts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfCountsOrCrimes;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Statute Violated -->
	<xsl:template match="stat.viol">
		<xsl:choose>
			<xsl:when test="$colKey!='VAAOC'">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_statuteViolated;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Statute Violated (Amended) -->
	<xsl:template match="amd.stat.viol">
		<xsl:choose>
			<xsl:when test="$colKey!='VAAOC'">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_statuteViolatedAmended;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Offense Committed (Amended) -->
	<xsl:template match="amd.crim.off">
		<xsl:choose>
			<xsl:when test="$colKey!='VAAOC' and normalize-space(.)!='7455'">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_offenseCommittedAmended;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Current Status -->
	<xsl:template match="curr.sts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_currentStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Add'l Information Related to Crime -->
	<xsl:template match="chrg.info.b" mode="addl_info_crime">
		<xsl:if test="(crim.2nd.fel.ind) or (gbmi) or (crim.att.ind)
					or (crim.hate.ind) or (sex.crim.ind) or (prp.crim.ind)
					or (prp.szd) or (drg.crim.ind) or (oth.crim.ind)
					or (wpn.crim.ind) or (inch.crim.ind) or (mnr.vic)">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_additionalInformationRelatedToCrime;</xsl:text>
				</th>
				<td>
					<xsl:apply-templates select="crim.2nd.fel.ind"/>
					<xsl:apply-templates select="gbmi"/>
					<xsl:apply-templates select="crim.att.ind"/>
					<xsl:apply-templates select="crim.hate.ind"/>
					<xsl:apply-templates select="sex.crim.ind"/>
					<xsl:apply-templates select="prp.crim.ind"/>
					<xsl:apply-templates select="prp.szd"/>
					<xsl:apply-templates select="drg.crim.ind"/>
					<xsl:apply-templates select="oth.crim.ind"/>
					<xsl:apply-templates select="wpn.crim.ind"/>
					<xsl:apply-templates select="inch.crim.ind"/>
					<xsl:apply-templates select="mnr.vic"/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>


	<!-- Additional Information related to crime -->
	<xsl:template match="gbmi">
		<xsl:if test="preceding-sibling::crim.2nd.fel.ind">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Additional Information related to crime -->
	<xsl:template match="crim.att.ind">
		<xsl:if test="preceding-sibling::crim.2nd.fel.ind or preceding-sibling::gbmi">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Additional Information related to crime -->
	<xsl:template match="crim.hate.ind">
		<xsl:if test="preceding-sibling::crim.2nd.fel.ind or preceding-sibling::gbmi or preceding-sibling::crim.att.ind">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Additional Information related to crime -->
	<xsl:template match="sex.crim.ind">
		<xsl:if test="preceding-sibling::crim.2nd.fel.ind or preceding-sibling::gbmi or preceding-sibling::crim.att.ind or preceding-sibling::crim.hate.ind">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Additional Information related to crime -->
	<xsl:template match="prp.crim.ind">
		<xsl:if test="preceding-sibling::crim.2nd.fel.ind or preceding-sibling::gbmi or preceding-sibling::crim.att.ind or preceding-sibling::crim.hate.ind
					or preceding-sibling::sex.crim.ind">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Additional Information related to crime -->
	<xsl:template match="prp.szd">
		<xsl:if test="preceding-sibling::crim.2nd.fel.ind or preceding-sibling::gbmi or preceding-sibling::crim.att.ind or preceding-sibling::crim.hate.ind
						or preceding-sibling::sex.crim.ind or preceding-sibling::prp.crim.ind">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Additional Information related to crime -->
	<xsl:template match="drg.crim.ind">
		<xsl:if test="preceding-sibling::crim.2nd.fel.ind or preceding-sibling::gbmi or preceding-sibling::crim.att.ind or preceding-sibling::crim.hate.ind
						or preceding-sibling::sex.crim.ind or preceding-sibling::prp.crim.ind or preceding-sibling::prp.szd">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Additional Information related to crime -->
	<xsl:template match="oth.crim.ind">
		<xsl:if test="preceding-sibling::crim.2nd.fel.ind or preceding-sibling::gbmi or preceding-sibling::crim.att.ind or preceding-sibling::crim.hate.ind
						or preceding-sibling::sex.crim.ind or preceding-sibling::prp.crim.ind or preceding-sibling::prp.szd or preceding-sibling::drg.crim.ind">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Additional Information related to crime -->
	<xsl:template match="wpn.crim.ind">
		<xsl:if test="preceding-sibling::crim.2nd.fel.ind or preceding-sibling::gbmi or preceding-sibling::crim.att.ind or preceding-sibling::crim.hate.ind
						or preceding-sibling::sex.crim.ind or preceding-sibling::prp.crim.ind or preceding-sibling::prp.szd or preceding-sibling::drg.crim.ind
						or preceding-sibling::oth.crim.ind">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Additional Information related to crime -->
	<xsl:template match="inch.crim.ind">
		<xsl:if test="preceding-sibling::crim.2nd.fel.ind or preceding-sibling::gbmi or preceding-sibling::crim.att.ind or preceding-sibling::crim.hate.ind
						or preceding-sibling::sex.crim.ind or preceding-sibling::prp.crim.ind or preceding-sibling::prp.szd or preceding-sibling::drg.crim.ind
						or preceding-sibling::oth.crim.ind or preceding-sibling::wpn.crim.ind">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Additional Information related to crime -->
	<xsl:template match="mnr.vic">
		<xsl:if test="preceding-sibling::crim.2nd.fel.ind or preceding-sibling::gbmi or preceding-sibling::crim.att.ind or preceding-sibling::crim.hate.ind
						or preceding-sibling::sex.crim.ind or preceding-sibling::prp.crim.ind or preceding-sibling::prp.szd or preceding-sibling::drg.crim.ind
						or preceding-sibling::oth.crim.ind or preceding-sibling::wpn.crim.ind or preceding-sibling::inch.crim.ind">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Victim's Age -->
	<xsl:template match="mnr.vic.age">
		<xsl:variable name="victimsAge">
			<xsl:text>&pr_victimsAge;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$victimsAge"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Victim's Gender -->
	<xsl:template match="vict.sx">
		<xsl:variable name="victimsGender">
			<xsl:text>&pr_victimsGender;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$victimsGender"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Conviction Place -->
	<xsl:template match="cnvt.loc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_convictionPlace;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Risk Level -->
	<xsl:template match="rsk.lvl">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_riskLevel;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	********************** (F)"PLEA INFORMATION" section **********************
	************************************************************************-->

	<!-- Plea Information - (block) -->
	<xsl:template match="plea.info.b">
		<xsl:if test="not(preceding-sibling::plea.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_pleaInformation;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Original Plea -->
	<xsl:template match="orig.plea">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalPlea;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Original Plea Date -->
	<xsl:template match="plea.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalPleaDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Plea Withdrawn Date -->
	<xsl:template match="plea.w.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_pleaWithdrawnDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- New Plea -->
	<xsl:template match="sub.new.plea">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_newPlea;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	******************** (G)"SENTENCE INFORMATION" section ********************
	************************************************************************-->

	<!-- Sentence Information - (block) -->
	<xsl:template match="sen.info.b">
		<xsl:if test="not(preceding-sibling::sen.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_sentenceInformation;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates select="sen.desc"/>
			<xsl:apply-templates select="sen.cnty"/>
			<xsl:apply-templates select="sen.imp.d"/>
			<xsl:apply-templates select="sen.mod.d"/>
			<xsl:apply-templates select="sen.beg.d"/>
			<xsl:apply-templates select="sen.exp.d"/>
			<xsl:apply-templates select="." mode="sentence_length"/>
			<xsl:apply-templates select="." mode="sentence_minimum"/>
			<xsl:apply-templates select="." mode="sentence_maximum"/>
			<xsl:apply-templates select="sen.pard"/>
			<xsl:apply-templates select="sen.deff"/>
			<xsl:apply-templates select="." mode="sentence_suspended"/>
			<xsl:apply-templates select="sen.ver"/>
			<xsl:apply-templates select="sen.cc"/>
			<xsl:apply-templates select="sen.cmpltd"/>
			<xsl:apply-templates select="sen.sts"/>
			<xsl:apply-templates select="." mode="license_suspended"/>
			<xsl:apply-templates select="." mode="community_service"/>
		</table>
	</xsl:template>

	<!-- Sentence Details/Description -->
	<xsl:template match="sen.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceDetailsOrDescription;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sentence County -->
	<xsl:template match="sen.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sentence Imposed Date -->
	<xsl:template match="sen.imp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceImposedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sentence Modification Date -->
	<xsl:template match="sen.mod.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceModificationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sentence Begin Date -->
	<xsl:template match="sen.beg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceBeginDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sentence Expiration Date -->
	<xsl:template match="sen.exp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceExpirationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sentence Length -->
	<xsl:template match="sen.info.b" mode="sentence_length">
		<xsl:if test="normalize-space(sen.lngth) or normalize-space(sen.lngth.yrs) or normalize-space(sen.lngth.mnths) or normalize-space(sen.lngth.days)">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_sentenceLength;</xsl:text>
				</th>
				<td>
					<xsl:choose>
						<xsl:when test="normalize-space(sen.lngth)">
							<xsl:apply-templates select="sen.lngth"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="sen.lngth.yrs"/>
							<xsl:apply-templates select="sen.lngth.mnths"/>
							<xsl:apply-templates select="sen.lngth.days"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="sen.lngth.yrs[normalize-space(.)] | sen.min.yrs[normalize-space(.)] | sen.max.yrs[normalize-space(.)] | sen.susp.yrs[normalize-space(.)]
								| lic.susp.yrs[normalize-space(.)] | cmnty.ser.yrs[normalize-space(.)] | prl.trm.yrs[normalize-space(.)] | prob.time.yrs[normalize-space(.)]
								| time.srv.yrs[normalize-space(.)] | comm.sup.yrs[normalize-space(.)] | min.prob.time.yrs[normalize-space(.)] | pri.sen.max.yrs[normalize-space(.)]
								| pri.sen.min.yrs[normalize-space(.)] | pri.comm.sup.yrs[normalize-space(.)]">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]>&pr_years;</xsl:text>
		<xsl:variable name="daysElementName">
			<xsl:value-of select="concat(substring(name(),1,string-length(name())-3), 'days')"/>
		</xsl:variable>
		<xsl:variable name="monthsElementName">
			<xsl:value-of select="concat(substring(name(),1,string-length(name())-3), 'mnths')"/>
		</xsl:variable>
		<xsl:if test="normalize-space(following-sibling::*[name()=$daysElementName]) or normalize-space(preceding-sibling::*[name()=$daysElementName])
						or normalize-space(following-sibling::*[name()=$monthsElementName]) or normalize-space(preceding-sibling::*[name()=$monthsElementName])">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="sen.lngth.mnths[normalize-space(.)] | sen.min.mnths[normalize-space(.)] | sen.max.mnths[normalize-space(.)] | sen.susp.mnths[normalize-space(.)]
								| lic.susp.mnths[normalize-space(.)] | cmnty.ser.mnths[normalize-space(.)] | prl.trm.mnths[normalize-space(.)] | prob.time.mnths[normalize-space(.)]
								| time.srv.mnths[normalize-space(.)] | comm.sup.mnths[normalize-space(.)] | min.prob.time.mnths[normalize-space(.)] | pri.sen.max.mnths[normalize-space(.)]
								| pri.sen.min.mnths[normalize-space(.)] | pri.comm.sup.mnths[normalize-space(.)]">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]>&pr_months;</xsl:text>
		<xsl:variable name="daysElementName">
			<xsl:value-of select="concat(substring(name(),1,string-length(name())-5), 'days')"/>
		</xsl:variable>
		<xsl:if test="normalize-space(following-sibling::*[name()=$daysElementName]) or normalize-space(preceding-sibling::*[name()=$daysElementName])">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="sen.lngth.days[normalize-space(.)] | sen.min.days[normalize-space(.)] | sen.max.days[normalize-space(.)] | sen.susp.days[normalize-space(.)]
								| lic.susp.days[normalize-space(.)] | cmnty.ser.days[normalize-space(.)] | prl.trm.days[normalize-space(.)] | prob.time.days[normalize-space(.)]
								| time.srv.days[normalize-space(.)] | comm.sup.days[normalize-space(.)] | min.prob.time.days[normalize-space(.)] | pri.sen.max.days[normalize-space(.)]
								| pri.sen.min.days[normalize-space(.)] | pri.comm.sup.days[normalize-space(.)]">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]>&pr_days;</xsl:text>
	</xsl:template>

	<!-- Sentence Minimum -->
	<xsl:template match="sen.info.b | sen.min[normalize-space(sen.min.yrs) or normalize-space(sen.min.mnths) or normalize-space(sen.min.days)]" mode="sentence_minimum">
		<xsl:if test="normalize-space(sen.min) or normalize-space(sen.min.yrs) or normalize-space(sen.min.mnths) or normalize-space(sen.min.days)">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_sentenceMinimum;</xsl:text>
				</th>
				<td>
					<xsl:choose>
						<xsl:when test="normalize-space(sen.min)">
							<xsl:apply-templates select="sen.min"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="sen.min.yrs"/>
							<xsl:apply-templates select="sen.min.mnths"/>
							<xsl:apply-templates select="sen.min.days"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Sentence Maximum -->
	<xsl:template match="sen.info.b | sen.max[normalize-space(sen.max.yrs) or normalize-space(sen.max.mnths) or normalize-space(sen.max.days)]" mode="sentence_maximum">
		<xsl:if test="normalize-space(sen.max) or normalize-space(sen.max.yrs) or normalize-space(sen.max.mnths) or normalize-space(sen.max.days)">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_sentenceMaximum;</xsl:text>
				</th>
				<td>
					<xsl:choose>
						<xsl:when test="normalize-space(sen.max)">
							<xsl:apply-templates select="sen.max"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="sen.max.yrs"/>
							<xsl:apply-templates select="sen.max.mnths"/>
							<xsl:apply-templates select="sen.max.days"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Sentence Commuted/Pardoned -->
	<xsl:template match="sen.pard">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceCommutedOrPardoned;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sentence Deferred -->
	<xsl:template match="sen.deff">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceDeferred;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sentence Suspended -->
	<xsl:template match="sen.info.b" mode="sentence_suspended">
		<xsl:if test="normalize-space(sen.susp) or normalize-space(sen.susp.yrs) or normalize-space(sen.susp.mnths) or normalize-space(sen.susp.days)">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_sentenceSuspended;</xsl:text>
				</th>
				<td>
					<xsl:choose>
						<xsl:when test="normalize-space(sen.susp)">
							<xsl:apply-templates select="sen.susp"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="sen.susp.yrs"/>
							<xsl:apply-templates select="sen.susp.mnths"/>
							<xsl:apply-templates select="sen.susp.days"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Sentence Verified -->
	<xsl:template match="sen.ver">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceVerified;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sentence Consecutive/Concurrent -->
	<xsl:template match="sen.cc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceConsecutiveOrConcurrent;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sentence Completed -->
	<xsl:template match="sen.cmpltd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceCompleted;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sentence Status -->
	<xsl:template match="sen.sts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- License Suspended -->
	<xsl:template match="sen.info.b" mode="license_suspended">
		<xsl:if test="normalize-space(lic.susp) or normalize-space(lic.susp.yrs) or normalize-space(lic.susp.mnths) or normalize-space(lic.susp.days)">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_licenseSuspended;</xsl:text>
				</th>
				<td>
					<xsl:choose>
						<xsl:when test="normalize-space(lic.susp)">
							<xsl:apply-templates select="lic.susp"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="lic.susp.yrs"/>
							<xsl:apply-templates select="lic.susp.mnths"/>
							<xsl:apply-templates select="lic.susp.days"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Hours of Community Service -->
	<xsl:template match="sen.info.b" mode="community_service">
		<xsl:if test="normalize-space(hrs.cmnty.ser) or normalize-space(cmnty.ser.yrs) or normalize-space(cmnty.ser.mnths) or normalize-space(cmnty.ser.days)">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_communityService;</xsl:text>
				</th>
				<td>
					<xsl:choose>
						<xsl:when test="normalize-space(hrs.cmnty.ser)">
							<xsl:apply-templates select="hrs.cmnty.ser"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="cmnty.ser.yrs"/>
							<xsl:apply-templates select="cmnty.ser.mnths"/>
							<xsl:apply-templates select="cmnty.ser.days"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- **********************************************************************
	****************** (H)"INCARCERATION INFORMATION" section *****************
	************************************************************************-->

	<!-- Incarceration Information - (block) -->
	<xsl:template match="loc.info.b">
		<xsl:if test="not(preceding-sibling::loc.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_incarcerationInformation;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates select="in.nbr"/>
			<xsl:apply-templates select="in.sts"/>
			<xsl:apply-templates select="cstdy.cls"/>
			<xsl:apply-templates select="inst.info.b"/>
			<xsl:apply-templates select="adm.d"/>
			<xsl:apply-templates select="adm.typ"/>
			<xsl:apply-templates select="time.cred.serv"/>
			<xsl:apply-templates select="gn.time"/>
			<xsl:apply-templates select="gn.time.eff.d"/>
			<xsl:apply-templates select="spcl.prv.cd"/>
			<xsl:apply-templates select="lst.prv.d"/>
			<xsl:apply-templates select="add.loc"/>
			<xsl:apply-templates select="add.cls.d"/>
			<xsl:apply-templates select="add.cls.typ"/>
			<xsl:apply-templates select="add.adm.typ"/>
			<xsl:apply-templates select="add.adm.d"/>
			<xsl:apply-templates select="add.asgmt.typ"/>
			<xsl:apply-templates select="lst.mvmt.typ"/>
			<xsl:apply-templates select="lst.mvmt.d"/>
			<xsl:apply-templates select="escp.hist"/>
			<xsl:apply-templates select="escp.d"/>
			<xsl:apply-templates select="escp.fac"/>
			<xsl:apply-templates select="escp.cls"/>
			<xsl:apply-templates select="recp.d"/>
		</table>
	</xsl:template>

	<!-- Inmate Number -->
	<xsl:template match="in.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_inmateNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Inmate Status -->
	<xsl:template match="in.sts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_inmateStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Inmate Custody Class -->
	<xsl:template match="cstdy.cls">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_inmateCustodyClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Institution -->
	<xsl:template match="inst.loc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_institutionName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Institution Unit Number -->
	<xsl:template match="inst.unt.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_institutionUnitNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Institution Address -->
	<xsl:template match="inst.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_institutionAddress;'"/>
			<xsl:with-param name="street" select="inst.addr"/>
			<xsl:with-param name="city" select="inst.cty"/>
			<xsl:with-param name="stateOrProvince" select="inst.st"/>
			<xsl:with-param name="zip" select="inst.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Institution County -->
	<xsl:template match="inst.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_institutionCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Institution Phone Number -->
	<xsl:template match="inst.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_institutionPhoneNumber;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Admission Date -->
	<xsl:template match="adm.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_admissionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Admission Type -->
	<xsl:template match="adm.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_admissionType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Time Credited/Served -->
	<xsl:template match="time.cred.serv">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_timeCreditedOrServed;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Gain Time -->
	<xsl:template match="gn.time">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gainTime;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Gain Time Effective Date -->
	<xsl:template match="gn.time.eff.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gainTimeEffectiveDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Last Provision (Special Provision) -->
	<xsl:template match="spcl.prv.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastProvisionSpecialProvision;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Last Provision Date -->
	<xsl:template match="lst.prv.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastProvisionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Additional Institutional Locations -->
	<xsl:template match="add.loc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalInstitutionalLocations;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Additional Classification Dates -->
	<xsl:template match="add.cls.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalClassificationDates;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Additional Classification Types -->
	<xsl:template match="add.cls.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalClassificationTypes;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Additional Admission Types -->
	<xsl:template match="add.adm.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalAdmissionTypes;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Additional Admission Dates -->
	<xsl:template match="add.adm.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalAdmissionDates;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Additional Assignment Types -->
	<xsl:template match="add.asgmt.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalAssignmentTypes;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Last Transfer or Movement Type -->
	<xsl:template match="lst.mvmt.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastTransferOrMovementType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Last Transfer or Movement Date -->
	<xsl:template match="lst.mvmt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastTransferOrMovementDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Escape History -->
	<xsl:template match="escp.hist">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_escapeHistory;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date of Escape -->
	<xsl:template match="escp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfEscape;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Facility Escaped From -->
	<xsl:template match="escp.fac">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_facilityEscapedFrom;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Class at Escape -->
	<xsl:template match="escp.cls">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_classAtEscape;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date Recaptured -->
	<xsl:template match="recp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateRecaptured;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	**************** (I)"INMATE DISCIPLINE INFORMATION" section ***************
	************************************************************************-->

	<!-- Inmate Discipline Information - (block) -->
	<xsl:template match="disc.info.b">
		<xsl:if test="not(preceding-sibling::loc.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_inmateDisciplineInformation;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Violation Date -->
	<xsl:template match="infrac.viol.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_violationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Infraction Details/Description -->
	<xsl:template match="infrac.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_infractionDetailsOrDescription;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Plea as Related to Infraction -->
	<xsl:template match="infrac.plea">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_pleaAsRelatedToInfraction;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Verdict as Related to Infraction -->
	<xsl:template match="infrac.ver">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_verdictAsRelatedToInfraction;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Hearing on Infraction -->
	<xsl:template match="infrac.hrng">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hearingOnInfraction;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Decision on Appeal -->
	<xsl:template match="infrac.appl">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_decisionOnAppeal;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Violation Status -->
	<xsl:template match="infrac.viol.sts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_violationStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Time Lost/Suspended due to Infraction -->
	<xsl:template match="infrac.time">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_timeLostOrSuspendedDueToInfraction;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	********************* (J)"RELEASE INFORMATION" section ********************
	************************************************************************-->

	<!-- Release Information - (block) -->
	<xsl:template match="rlse.info.b">
		<xsl:if test="not(preceding-sibling::rlse.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_releaseInformation;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Tentative Release Date -->
	<xsl:template match="ten.rlse.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_tentativeReleaseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Projected/Provisional Release Date -->
	<xsl:template match="proj.rlse.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_projectedOrProvisionalReleaseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Maximum Release Date -->
	<xsl:template match="max.rlse.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_maximumReleaseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Actual Release Date -->
	<xsl:template match="act.rlse.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actualReleaseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Release Reason -->
	<xsl:template match="rlse.rsn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_releaseReason;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Firearm Restriction -->
	<xsl:template match="frm.rstr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_firearmRestriction;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	********************** (K)"PAROLE INFORMATION" section ********************
	************************************************************************-->

	<!-- Parole Information - (block) -->
	<xsl:template match="parl.info.b">
		<xsl:if test="not(preceding-sibling::parl.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_paroleInformation;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates select="prob.sts"/>
			<xsl:apply-templates select="cnty.spvsn"/>
			<xsl:apply-templates select="parl.hrg.b"/>
			<xsl:apply-templates select="nxt.prl.hrng.d"/>
			<xsl:apply-templates select="." mode="parole_term"/>
			<xsl:apply-templates select="prl.beg.d"/>
			<xsl:apply-templates select="prl.elg.d"/>
			<xsl:apply-templates select="prl.amd.d"/>
			<xsl:apply-templates select="prl.proj.d"/>
			<xsl:apply-templates select="prl.rlse.d"/>
			<xsl:apply-templates select="prl.trmtn.rsn"/>
			<xsl:apply-templates select="prl.offr"/>
			<xsl:apply-templates select="prl.addr"/>
		</table>
	</xsl:template>

	<!-- Probation or Parole Status -->
	<xsl:template match="prob.sts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationOrParoleStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Supervised By -->
	<xsl:template match="cnty.spvsn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_supervisedBy;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Hearing Date -->
	<xsl:template match="prl.hrng.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleHearingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Hearing Type -->
	<xsl:template match="prl.hrng.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleHearingType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Hearing Location -->
	<xsl:template match="prl.hrng.loc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleHearingLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Board Action -->
	<xsl:template match="prl.brd.act">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleBoardAction;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Months Deferred -->
	<xsl:template match="mnths.deff">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_monthsDeferred;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Next Parole Hearing Date -->
	<xsl:template match="nxt.prl.hrng.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nextParoleHearingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Term -->
	<xsl:template match="parl.info.b | prl.trm[normalize-space(prl.trm.yrs) or normalize-space(prl.trm.mnths) or normalize-space(prl.trm.days)]" mode="parole_term">
		<xsl:if test="normalize-space(prl.trm) or normalize-space(prl.trm.yrs) or normalize-space(prl.trm.mnths) or normalize-space(prl.trm.days)">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_paroleTerm;</xsl:text>
				</th>
				<td>
					<xsl:choose>
						<xsl:when test="normalize-space(prl.trm)">
							<xsl:apply-templates select="prl.trm"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="prl.trm.yrs"/>
							<xsl:apply-templates select="prl.trm.mnths"/>
							<xsl:apply-templates select="prl.trm.days"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Parole Begin Date -->
	<xsl:template match="prl.beg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleBeginDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Eligibility Date -->
	<xsl:template match="prl.elg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleEligibilityDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Amended Date -->
	<xsl:template match="prl.amd.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleAmendedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Projected Date -->
	<xsl:template match="prl.proj.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleProjectedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Release Date -->
	<xsl:template match="prl.rlse.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleProjectedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Termination Reason -->
	<xsl:template match="prl.trmtn.rsn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleTerminationReason;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Officer -->
	<xsl:template match="prl.offr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleOfficer;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Parole Address -->
	<xsl:template match="prl.addr">
		<!-- Although this says it is an address, it is only one field, so use of the wrapPublicRecordsAddress named template does not make sense here. -->
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleAddress;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	******************** (L)"PROBATION INFORMATION" section *******************
	************************************************************************-->

	<!-- Probation Information - (block) -->
	<xsl:template match="prob.info.b">
		<xsl:if test="not(preceding-sibling::prob.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_probationInformation;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates select="prob.agcy" />
			<xsl:apply-templates select="." mode="probation_time" />
			<xsl:apply-templates select="prob.typ" />
			<xsl:apply-templates select="prob.beg.d" />
			<xsl:apply-templates select="prob.end.sch" />
			<xsl:apply-templates select="prob.end.act" />
			<xsl:apply-templates select="prob.flw.flg" />
			<xsl:apply-templates select="prob.cc" />
			<xsl:apply-templates select="prob.viol.d" />
			<xsl:apply-templates select="prob.viol" />
			<xsl:apply-templates select="rtn.d" />
			<xsl:apply-templates select="plcmt.viol" />
		</table>
	</xsl:template>

	<!-- Probation Agency -->
	<xsl:template match="prob.agcy">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationAgency;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Length of Probation -->
	<xsl:template match="prob.info.b | prob.time[normalize-space(prob.time.yrs) or normalize-space(prob.time.mnths) or normalize-space(prob.time.days)]" mode="probation_time">
		<xsl:if test="normalize-space(prob.time) or normalize-space(prob.time.yrs) or normalize-space(prob.time.mnths) or normalize-space(prob.time.days)">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_probationMaximum;</xsl:text>
				</th>
				<td>
					<xsl:choose>
						<xsl:when test="normalize-space(prob.time)">
							<xsl:apply-templates select="prob.time"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="prob.time.yrs"/>
							<xsl:apply-templates select="prob.time.mnths"/>
							<xsl:apply-templates select="prob.time.days"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Probation Type -->
	<xsl:template match="prob.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Probation Begin Date -->
	<xsl:template match="prob.beg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationBeginDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Probation Scheduled End Date -->
	<xsl:template match="prob.end.sch">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationScheduledEndDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Probation Actual End Date -->
	<xsl:template match="prob.end.act">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationActualEndDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Probation Follows Prison Term -->
	<xsl:template match="prob.flw.flg">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationFollowsPrisonTerm;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Probation Consecutive/Concurrent -->
	<xsl:template match="prob.cc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationConsecutiveOrConcurrent;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Probation Violation Date -->
	<xsl:template match="prob.viol.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationViolationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Probation Violator -->
	<xsl:template match="prob.viol">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationViolator;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date Returned to Custody -->
	<xsl:template match="rtn.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateReturnedToCustody;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Placement After Violation -->
	<xsl:template match="plcmt.viol">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_placementAfterViolation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	************* (M)"PRIOR CHARGE OR OFFENSE INFORMATION" section ************
	************************************************************************-->

	<!-- Prior Charge Information - (block) -->
	<xsl:template match="pri.chrg.info.b">
		<xsl:if test="not(preceding-sibling::pri.chrg.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_priorChargeOrOffenseInformation;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Prior Offense -->
	<xsl:template match="pri.off">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorOffense;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Number of Prior Incarcerations -->
	<xsl:template match="pri.incar">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfPriorIncarcerations;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Prior Incarceration Date -->
	<xsl:template match="pri.incar.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorIncarcerationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Prior Incarceration Status -->
	<xsl:template match="pri.sts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorIncarcerationStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Prior Incarceration Location -->
	<xsl:template match="pri.loc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorIncarcerationLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	****************** (N)"MISCELLANEOUS INFORMATION" section *****************
	************************************************************************-->

	<!-- Miscellaneous Information - (block) -->
	<xsl:template match="misc.b">
		<xsl:if test="not(preceding-sibling::loc.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_miscellaneousInformationSubheader;'" />
			</xsl:call-template>
		</xsl:if>
		<xsl:variable name="label">
			<xsl:value-of select="misc.label"/>
		</xsl:variable>
		<table class="&pr_table;">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="$label"/>
				<xsl:with-param name="selectNodes" select="misc"/>
			</xsl:call-template>
		</table>
	</xsl:template>

	<!--SSN Number-->
	<xsl:template match ="ssn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ssnNumber;'"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
		</xsl:call-template>
	</xsl:template>

	<!--National ID Number-->
	<xsl:template match ="nat.id.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nationalId;'"/>
			<xsl:with-param name="selectNodes" select="nat.id"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="nat.id">
		<xsl:apply-templates select ="nat.id.num"/>
		<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates select="nat.id.ctry"/>
	</xsl:template>

	<!--Passport number-->
	<!-- Only display the passport number when the SSN can be shown -->
	<!-- which means that the user is a government user -->
	<xsl:template match ="passport.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_passportNumber;'"/>
			<xsl:with-param name="selectNodes" select="passport"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="passport">
		<xsl:apply-templates select="pass.num"/>
		<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates select="pass.ctry"/>
	</xsl:template>

	<xsl:template name="Source">
		<tr class="&pr_item;">
			<!--Label-->
			<th>
				<xsl:text>&pr_source;</xsl:text>
			</th>
			<!--Data-->
			<td>
				<xsl:value-of select="normalize-space(s.info/s/s.nm)"/>
				<xsl:if test="normalize-space(s.info/s/s.nm) and normalize-space(s.info/s/s.cnty)">
					<xsl:text>,<![CDATA[ ]]></xsl:text>
				</xsl:if>
				<xsl:value-of select="normalize-space(s.info/s/s.cnty)"/>
				<xsl:if test="(normalize-space(s.info/s/s.nm) or normalize-space(s.info/s/s.cnty)) and normalize-space(s.info/s/s.st)">
					<xsl:text>,<![CDATA[ ]]></xsl:text>
				</xsl:if>
				<xsl:value-of select="normalize-space(s.info/s/s.st)"/>
			</td>
		</tr>
	</xsl:template>

	<!-- **********************************************************************
	********************** (B)Defendant Information section *********************
	************************************************************************-->
	<!-- Defendant Information -->
	<xsl:template match="def.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_defendantInformation;'" />
		</xsl:call-template>

		<table class="&pr_table;">
			<xsl:apply-templates select="off.nm"/>
			<xsl:apply-templates select="nm.typ"/>
			<xsl:apply-templates select="ssn.b" />
			<xsl:apply-templates select="fbi.nbr"/>
			<xsl:apply-templates select="st.id.nbr"/>
			<xsl:apply-templates select="in.nbr"/>
			<xsl:apply-templates select="aln.nbr"/>
			<xsl:apply-templates select="doc.nbr"/>
			<xsl:apply-templates select="dl.nbr"/>
			<xsl:apply-templates select="dl.st"/>
			<xsl:apply-templates select ="birth.loc"/>
			<xsl:apply-templates select="dob"/>
			<xsl:apply-templates select="ctzn"/>
			<xsl:apply-templates select="phn.nbr"/>
			<xsl:apply-templates select="def.email"/>
			<xsl:apply-templates select="curr.addr"/>
			<xsl:apply-templates select="def.cnty"/>
			<xsl:apply-templates select="hist.addr.b"/>
			<xsl:apply-templates select="aka.b"/>
			<xsl:apply-templates select="sex"/>
			<xsl:apply-templates select="race"/>
			<xsl:apply-templates select="ethnic"/>
			<xsl:apply-templates select="eyes"/>
			<xsl:apply-templates select="hair"/>
			<xsl:apply-templates select="skin"/>
			<xsl:apply-templates select="bld"/>
			<xsl:apply-templates select="ht"/>
			<xsl:apply-templates select="wt"/>
			<xsl:apply-templates select="oth.mrk.b"/>
			<xsl:apply-templates select="veh.b"/>
			<xsl:apply-templates select="death.ind"/>
			<xsl:apply-templates select="death.d"/>
			<xsl:apply-templates select="nxt.kin.b"/>
			<xsl:apply-templates select="edu"/>
			<xsl:apply-templates select="mrtl.sts"/>
			<xsl:apply-templates select="dpnd.nbr"/>
			<xsl:apply-templates select="occup"/>
			<xsl:apply-templates select="mil.info.b"/>
			<xsl:apply-templates select="misc.info"/>
			<xsl:apply-templates select="inst.loc"/>
			<xsl:apply-templates select="adm.d"/>
			<xsl:apply-templates select="image.block"/>
		</table>
	</xsl:template>

	<!--Offender Name -->
	<xsl:template match="off.nm">
		<xsl:choose>
			<xsl:when test ="normalize-space(fst.nm) and normalize-space(lst.nm)">
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="'&pr_offenderName;'"/>
					<xsl:with-param name="firstName" select="fst.nm"/>
					<xsl:with-param name="middleName" select="mid.nm"/>
					<xsl:with-param name="lastName" select="lst.nm"/>
					<xsl:with-param name="suffixName" select="suf.nm" />
					<xsl:with-param name="lastNameFirst" select="true()"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test ="normalize-space(fl.nm)">
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="defaultLabel" select="'&pr_offenderName;'"/>
					<xsl:with-param name="lastName" select="fl.nm"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!--Name Type  -->
	<xsl:template match="nm.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nameType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ssn.b">
		<xsl:apply-templates select ="ssn"/>
		<xsl:apply-templates select ="ssn.frag">
			<xsl:with-param name="isPrivacyProtected" select="ancestor::n-docbody/r/@r10='MN'"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--DOC Number  -->
	<xsl:template match="doc.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_docNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Birth Place-->
	<xsl:template match="birth.loc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_birthPlace;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date of Birth  -->
	<xsl:template match="dob">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Phone Number  -->
	<xsl:template match="phn.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Defendant Email  -->
	<xsl:template match="def.email">
		<xsl:call-template name="wrapPublicRecordsEmail">
			<xsl:with-param name="email" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Address County  -->
	<xsl:template match="def.cnty[name(..)='def.info.b']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Address Verified Date  -->
	<xsl:template match="addr.ver.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_addressVerifiedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Historical Address  -->
	<xsl:template match="hist.addr.b">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_historicalAddresses;</xsl:text>
			</th>
			<td>
				<xsl:for-each select="hist.addr">
					<div class="&paraMainClass;">
						<xsl:call-template name="FormatAddress">
							<xsl:with-param name="street" select="def.str"/>
							<xsl:with-param name="streetUnitNumber" select="def.unit"/>
							<xsl:with-param name="city" select="def.cty"/>
							<xsl:with-param name="stateOrProvince" select="def.st"/>
							<xsl:with-param name="zip" select="def.zip"/>
							<xsl:with-param name="zipExt" select="def.zip.ext"/>
						</xsl:call-template>
					</div>
				</xsl:for-each>
			</td>
		</tr>
	</xsl:template>

	<!--AKA Aliases  -->
	<xsl:template match="aka.b[normalize-space(aka)]">
		<xsl:if test="aka/aka.fl.nm[normalize-space(.)] or (aka/aka.lst.nm[normalize-space(.)] and aka/aka.fst.nm[normalize-space(.)])">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_aliases;</xsl:text>
				</th>
				<td>
					<xsl:apply-templates select="aka"/>
				</td>
			</tr>
		</xsl:if>
		<xsl:apply-templates select="aka.dob.b[normalize-space(aka.dob)]"/>
	</xsl:template>

	<xsl:template match="aka.nm.b[normalize-space(aka.fl.nm) or normalize-space(aka.lst.nm)]">
		<xsl:choose>
			<xsl:when test="normalize-space(aka.fl.nm)">
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="'&pr_akaName;'"/>
					<xsl:with-param name="firstName" select="aka.fl.nm"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<!-- Must be a case where we have the name split into first/middle/last. -->
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="'&pr_akaName;'"/>
					<xsl:with-param name="firstName" select="aka.fst.nm"/>
					<xsl:with-param name="middleName" select="aka.mid.nm"/>
					<xsl:with-param name="lastName" select="aka.lst.nm"/>
					<xsl:with-param name="suffixName" select="aka.suf.nm"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="aka">
		<div>
			<xsl:choose>
				<xsl:when test="normalize-space(aka.lst.nm) and normalize-space(aka.fst.nm)">
					<xsl:call-template name="FormatName">
						<xsl:with-param name="firstName" select="aka.fst.nm"/>
						<xsl:with-param name="lastName" select="aka.lst.nm"/>
						<xsl:with-param name="middleName" select="aka.mid.nm"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="FormatName">
						<xsl:with-param name="lastName" select="aka.fl.nm"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="aka.dob.b/aka.dob">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_alternateDob;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Death Indicator  -->
	<xsl:template match="death.ind">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_deathIndicator;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date of Death  -->
	<xsl:template match="death.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfDeath;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="nxt.kin.b">
		<xsl:apply-templates select="nxt.kin.nm"/>
		<xsl:apply-templates select="nxt.kin.addr"/>
		<xsl:apply-templates select="nxt.kin.phn"/>
		<xsl:apply-templates select="nxt.kin.typ"/>
	</xsl:template>

	<!--Next of Kin Address  -->
	<xsl:template match="nxt.kin.addr">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_nextOfKinAddress;'"/>
			<xsl:with-param name="street" select="nxt.kin.str"/>
			<xsl:with-param name="streetUnitNumber" select="next.kin.unit"/>
			<xsl:with-param name="city" select="nxt.kin.cty"/>
			<xsl:with-param name="stateOrProvince" select="nxt.kin.st"/>
			<xsl:with-param name="zip" select="nxt.kin.zip"/>
			<xsl:with-param name="zipExt" select="nxt.kin.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!--Next of Kin Phone  -->
	<xsl:template match="nxt.kin.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nextOfKinPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Next of Kin Type  -->
	<xsl:template match="nxt.kin.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nextOfKinType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mil.info.b">
		<xsl:apply-templates select="mil.brnch"/>
		<xsl:apply-templates select="mil.dis.d"/>
		<xsl:apply-templates select="mil.dis.typ"/>
	</xsl:template>

	<!-- **********************************************************************
	********************** (C)OTHER INFORMATION section ********************
	************************************************************************-->

	<!-- Other Information -->
	<xsl:template match="r/oth.info.b">
		<xsl:if test="name(preceding-sibling::parl.info.b) != name(self::node()) and name(..) != 'off.info.b'">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_otherInformationSubheader;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:call-template name="processOtherInfoData"/>
		</table>
	</xsl:template>

	<xsl:template match="off.info.b/oth.info.b">
		<xsl:call-template name="processOtherInfoData"/>
	</xsl:template>

	<xsl:template name="processOtherInfoData">
		<xsl:apply-templates select="def.aty.b"/>
		<xsl:if test="judge[normalize-space(.)]">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_judgeNames;'"/>
				<xsl:with-param name="selectNodes" select="judge[normalize-space(.)]"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates select="officer.b"/>
	</xsl:template>

	<xsl:template match="judge.b[normalize-space(judge)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_judgeNames;'"/>
			<xsl:with-param name="selectNodes" select="judge[normalize-space(.)]"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="def.aty.addr">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_attorneyAddress;'"/>
			<xsl:with-param name="street" select="def.aty.str"/>
			<xsl:with-param name="streetUnitNumber" select="def.aty.unit"/>
			<xsl:with-param name="city" select="def.aty.cty"/>
			<xsl:with-param name="stateOrProvince" select="def.aty.st"/>
			<xsl:with-param name="zip" select="def.aty.zip"/>
			<xsl:with-param name="zipExt" select="def.aty.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="def.atty.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_attorneyPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>


	<!--Other Information Judge  -->
	<xsl:template match="oth.info.b/judge.b/judge">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="officer.b">
		<xsl:apply-templates select="officer"/>
		<xsl:apply-templates select="badge"/>
	</xsl:template>

	<!--Officer Involved  -->
	<xsl:template match="officer">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_officerInvolved;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Officer Badge Number  -->
	<xsl:template match="badge">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_officerBadgeNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Offense Number  -->
	<xsl:template match="off.seq.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_offenseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Warrant Date  -->
	<xsl:template match="warr.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_warrantDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Warrant Description  -->
	<xsl:template match="warr.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_warrantDescription;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Warrant Issue Date  -->
	<xsl:template match="warr.iss.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_warrantIssueDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Warrant Status  -->
	<xsl:template match="warr.flg">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_warrantStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Citation Number  -->
	<xsl:template match="cit.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_citationNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Booking Number  -->
	<xsl:template match="bk.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bookingNumber;'"/>
		</xsl:call-template>
	</xsl:template>


	<!--Booking Date  -->
	<xsl:template match="bk.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bookingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Custody Date  -->
	<xsl:template match="cust.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_custodyDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Custody Location  -->
	<xsl:template match="cust.loc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_custodyLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Initial Charge  -->
	<xsl:template match="init.chrg">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_initialCharge;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Initial Charge Date  -->
	<xsl:template match="init.chrg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_initialChargeDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Charge Disposed Date  -->
	<xsl:template match="disp.chrg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_chargeDisposedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Charge Disposition  -->
	<xsl:template match="disp.chrg">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_chargeDisposition;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Amended Charge  -->
	<xsl:template match="amd.chrg">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_amendedCharge;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Amended Charge Date  -->
	<xsl:template match="amd.chrg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_amendedChargeDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Bond Amount  -->
	<xsl:template match="bail.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bailAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Jurisdiction County  -->
	<xsl:template match="cnty.jur">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_jurisdictionCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Docket Number  -->
	<xsl:template match="dkt.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_docketNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Type  -->
	<xsl:template match="cs.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Status  -->
	<xsl:template match="cs.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Status Date  -->
	<xsl:template match="cs.stat.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseStatusDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Comment  -->
	<xsl:template match="cs.cmnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseComment;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Information  -->
	<xsl:template match="cs.info">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseInformationLabel;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Statute Violated  -->
	<xsl:template match="stat.viol">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_statuteViolated;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Offense Status  -->
	<xsl:template match="off.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_offenseStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Offense Status Date  -->
	<xsl:template match="off.stat.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_offenseStatusDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Disposition  -->
	<xsl:template match="disp.fndg[name(..)='off.info.b']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_disposition;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Disposition Date  -->
	<xsl:template match="disp.d[name(..)='off.info.b']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dispositionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Offense Location  -->
	<xsl:template match="off.loc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_offenseLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Final Offense Date  -->
	<xsl:template match="amd.crim.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_amendedCommittedOffenseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Victim Under 18  -->
	<xsl:template match="facts">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_offenseFactors;</xsl:text>
			</th>
			<td>
				<xsl:value-of select ="mnr.vic"/>
				<xsl:text>;<![CDATA[ ]]></xsl:text>
				<xsl:value-of select ="pri.off.ind"/>
			</td>
		</tr>
	</xsl:template>

	<!--Appeal Status  -->
	<xsl:template match="app.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_appealStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Appeal Date  -->
	<xsl:template match="app.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_appealDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Court Date  -->
	<xsl:template match="argmnt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_courtDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Sentence Status  -->
	<xsl:template match="sen.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Time Served  -->
	<xsl:template match="time.srv">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_timeServed;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="time.srv.yrs"/>
				<xsl:apply-templates select="time.srv.mnths"/>
				<xsl:apply-templates select="time.srv.days"/>
			</td>
		</tr>
	</xsl:template>

	<!--Public Service Hours  -->
	<xsl:template match="pub.srv.hrs">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_publicServiceHours;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Sentence Additional Info  -->
	<xsl:template match="sen.info">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sentenceAdditionalInformation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Community Supervision County  -->
	<xsl:template match="comm.sup.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_communitySupervisionCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Community Supervision Term  -->
	<xsl:template match="comm.sup">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_communitySupervisionTerm;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="comm.sup.yrs"/>
				<xsl:apply-templates select="comm.sup.mnths"/>
				<xsl:apply-templates select="comm.sup.days"/>
			</td>
		</tr>
	</xsl:template>

	<!--Parole Status  -->
	<xsl:template match="prl.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Parole Officer Phone  -->
	<xsl:template match="prl.offr.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_paroleOfficerPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Probation End Date  -->
	<xsl:template match="prob.end.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationEndDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Probation Min  -->
	<xsl:template match="min.prob.time">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_probationMinimum;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="min.prob.time.yrs"/>
				<xsl:apply-templates select="min.prob.time.mnths"/>
				<xsl:apply-templates select="min.prob.time.days"/>
			</td>
		</tr>
	</xsl:template>

	<!--Probation Status  -->
	<xsl:template match="prob.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_probationStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Case Number  -->
	<xsl:template match="pri.cs.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorCaseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Offense Date  -->
	<xsl:template match="pri.crim.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorOffenseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Offense Type  -->
	<xsl:template match="pri.cs.cat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorOffenseType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Offense Degree  -->
	<xsl:template match="pri.crim.cls">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorOffenseLevel;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Disposition  -->
	<xsl:template match="pri.disp.fndg">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorDisposition;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Disposition Date  -->
	<xsl:template match="pri.disp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorDispositionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Sentence Date  -->
	<xsl:template match="pri.sen.imp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorSentencingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Sentence Begin Date  -->
	<xsl:template match="pri.sen.beg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorSentenceBeginDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Sentence End Date  -->
	<xsl:template match="pri.sen.exp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorSentenceEndDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Sentence Type  -->
	<xsl:template match="pri.sen.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorSentenceType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Sentence Maximum  -->
	<xsl:template match="pri.sen.max">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_priorSentenceMaximum;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="pri.sen.max.yrs"/>
				<xsl:apply-templates select="pri.sen.max.mnths"/>
				<xsl:apply-templates select="pri.sen.max.days"/>
			</td>
		</tr>
	</xsl:template>

	<!--Prior Sentence Minimum  -->
	<xsl:template match="pri.sen.min">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_priorSentenceMinimum;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="pri.sen.min.yrs"/>
				<xsl:apply-templates select="pri.sen.min.mnths"/>
				<xsl:apply-templates select="pri.sen.min.days"/>
			</td>
		</tr>
	</xsl:template>

	<!--Prior Scheduled Release Date  -->
	<xsl:template match="pri.sch.rel.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorScheduledReleaseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Actual Release Date  -->
	<xsl:template match="pri.act.rel.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorActualReleaseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Sentence Status  -->
	<xsl:template match="pri.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorSentenceStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Prior Community Supervision County  -->
	<xsl:template match="pri.comm.sup.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_priorCommunitySupervisionCounty;'"/>
		</xsl:call-template>
	</xsl:template>


	<!--Prior Community Supervision Term  -->
	<xsl:template match="pri.comm.sup">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_priorCommunitySupervisionLength;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="pri.comm.sup.yrs"/>
				<xsl:apply-templates select="pri.comm.sup.mnths"/>
				<xsl:apply-templates select="pri.comm.sup.days"/>
			</td>
		</tr>
	</xsl:template>

	<!-- **********************************************************************
	************************ Common formatting templates **********************
	************************************************************************-->
	<xsl:template name="DisclaimerMessage">
		<xsl:if test="(/Document/n-docbody/r/p='CRIMINAL RECORDS') or
					  (/Document/n-docbody/r/p='STATE DOCKETS - CRIMINAL')">
			<xsl:choose>
				<xsl:when test="/Document/n-docbody/r/restrict='SOR'">
					<xsl:call-template name="wrapPublicRecordsDisclaimers">
						<xsl:with-param name="displayWarning" select="true()"/>
						<xsl:with-param name="disclaimer1">
							<xsl:text>This database does not contain information on all convicted sex offenders in the included states.	 Information provided varies by state.	Some states include only information on high risk offenders released or convicted of an offense on or after a specific date.</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="disclaimer2">
							<xsl:text>The information in this database is information of record and may not reflect the current residence, status, or other information regarding a registrant.	 Unless specifically indicated these individuals are not wanted by the police at this time.	 Unlawful use of this information to threaten, intimidate, harass or injure a registered sex offender is prohibited by state statute.  If you have concerns about a sex offender, have information on the whereabouts of a wanted sex offender, believe any information on this website is in error or if you would like further information please contact your local law enforcement agency.	Information on the law enforcement agencies responsible for the maintenance of the included state registries can be found on the SCOPE screen.</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="disclaimer3">
							<xsl:text>The preceding record is for informational purposes only and is not the official record. This information is not warranted for accuracy or completeness. For copies of the official record (of conviction or incarceration), contact the agency or court.</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="disclaimer4">
							<xsl:text>This information is not to be used for any purpose regulated by the fair credit reporting act including employment screening or in violation of any local or state law.</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="wrapPublicRecordsDisclaimers">
						<xsl:with-param name="disclaimer1">
							<xsl:text>The preceding record is for informational purposes only and is not the official record. This information is not warranted for accuracy or completeness. For copies of the official record (of conviction or incarceration), contact the agency or court.</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="disclaimer2">
							<xsl:text>This information is not to be used for any purpose regulated by the fair credit reporting act including employment screening or in violation of any local or state law.</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="DisplayUpdateLink">
		<div class="&docketsCurrentBlockClass;">

		<div class="&docketsScrapeDateClass;">			
		<xsl:call-template name="FormatScrapeDateWithLabel">
			<xsl:with-param name="label" select="'&docketsScrape;'"/>
			<xsl:with-param name="date" select="/Document/n-docbody/r/scrp.d"/>
		</xsl:call-template>
		</div>
			
		<xsl:variable name="docketNumber" select="//cs.nbr[1]"/>
		<xsl:variable name="normJurisdiction" select="//court.norm[1]"/>


		<xsl:if test="$DisplayDocketUpdateLink = true()">				
			<div class="&docketsToUpdateClass;">
				<xsl:text>&docketsToUpdate;</xsl:text>

				<a>
					<xsl:attribute name="href">
						<xsl:text>javascript:void(0);</xsl:text>
					</xsl:attribute>

					<xsl:attribute name="data-alternateevent">
						<xsl:text>&CriminalDocketUpdateEvent;</xsl:text>
					</xsl:attribute>
					
					<xsl:attribute name="id">&docketsUpdateId;</xsl:attribute>
					<xsl:text>&docketsUpdate;</xsl:text>
				</a>
			</div>
		</xsl:if>
			
		<input>
			<xsl:attribute name="type">hidden</xsl:attribute>
			<xsl:attribute name="id">pr_crimDocUpdateDocketNumber</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:value-of select="$docketNumber" />
			</xsl:attribute>
		</input>
		<input>
			<xsl:attribute name="type">hidden</xsl:attribute>
			<xsl:attribute name="id">pr_crimDocUpdateNormJurisdiction</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:value-of select="$normJurisdiction" />
			</xsl:attribute>
		</input>
		</div>

	</xsl:template>

	<xsl:template name="FormatScrapeDateWithLabel">
		<xsl:param name="date"/>
		<xsl:param name="label"/>

		<xsl:value-of select ="$label"/>
		<xsl:choose>
			<xsl:when test="string-length($date) = 8 and number($date) != 'NaN'">
				<xsl:variable name ="year" select ="substring($date,1,4)"/>
				<xsl:variable name ="month" select ="substring($date,5,2)"/>
				<xsl:variable name ="day" select ="substring($date,7,2)"/>
				<xsl:value-of select ="$month"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select ="$day"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select ="$year"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$date"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>


</xsl:stylesheet>
