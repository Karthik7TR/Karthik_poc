<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r" priority="1">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsHealthcareSanctionsClass;'" />
			<xsl:with-param name="dualColumn" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_healthcareProviderSanctionsRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsMainColumn">
		<!-- Coverage / Source Information -->
		<xsl:apply-templates select="$coverage-block"/>		
		
		<!-- Provider Information Section-->
		<xsl:apply-templates select="prov.info.b"/>
		<!--Education Information Section-->
		<xsl:apply-templates select="ed.info.b"/>
		<!--Reporting Authority Information-->
		<xsl:apply-templates select="s.info.b"/>
		<!-- Sanction Information Section -->
		<xsl:apply-templates select="sanc.info.b"/>
	</xsl:template>

	<!-- Product: Health Care Sanctions Record -->

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
			<xsl:apply-templates select ="na.b"/>
			<xsl:apply-templates select ="ent.typ"/>
			<xsl:apply-templates select ="tin.num"/>
			<xsl:apply-templates select ="tin.typ"/>
			<xsl:apply-templates select="birth.d"/>
			<xsl:apply-templates select="phn1.b/phn1"/>
			<xsl:apply-templates select="phn1.b/phn1.typ"/>
			<xsl:apply-templates select ="phn2.b/phn2"/>
			<xsl:apply-templates select ="phn2.b/phn2.typ"/>
			<xsl:apply-templates select ="addr1.b"/>
			<xsl:apply-templates select="last.rpt.d"/>
			<xsl:apply-templates select="prov.typ.b/prov.typ"/>
			<xsl:apply-templates select ="prov.typ.b/prov.typ.cat"/>
			<xsl:apply-templates select ="add.info"/>
		</table>
	</xsl:template>

	<!--Names-->
	<!--Individual Name-->
	<xsl:template match ="na.b/full.na.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="prefixName" select="na.prefix"/>
			<xsl:with-param name="firstName" select="f.name"/>
			<xsl:with-param name="middleName" select="m.name"/>
			<xsl:with-param name="lastName" select="l.name"/>
			<xsl:with-param name="suffixName" select="na.suf"/>
		</xsl:call-template>
	</xsl:template>

	<!--Company Name  -->
	<xsl:template match ="na.b/na">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_name;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="na.b/na.cite.query">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_name;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Entity Type-->
	<xsl:template match="ent.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_entityType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Tax Identity Number-->
	<xsl:template match="tin.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxIdNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Tax Identity Number Type-->
	<xsl:template match="tin.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxIdType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date of Birth-->
	<xsl:template match="birth.d">
		<xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
    </xsl:call-template>
	</xsl:template>

	<!--Phone 1-->
	<xsl:template match ="phn1.b/phn1">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone1;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Phone 1 Type-->
	<xsl:template match="phn1.b/phn1.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone1Type;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Phone 2-->
	<xsl:template match ="phn2.b/phn2">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone2;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Phone 2 Type-->
	<xsl:template match="phn2.b/phn2.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone2Type;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Provider Address-->
	<!--Address  -->
	<xsl:template match="addr1.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_address;'"/>
			<xsl:with-param name="street" select="addr.str"/>
			<xsl:with-param name="city" select="addr.cty"/>
			<xsl:with-param name="stateOrProvince" select="addr.st"/>
			<xsl:with-param name="zip" select="addr.zip.b/addr.zip"/>
			<xsl:with-param name="zipExt" select="addr.zip.b/addr.zip4"/>
			<xsl:with-param name="country" select="addr.ctry"/>
		</xsl:call-template>
	</xsl:template>

	<!--Last Reported-->
	<xsl:template match="last.rpt.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastReported;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Provider Type-->
	<xsl:template match="prov.typ.b/prov.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_providerType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Provider Category-->
	<xsl:template match ="prov.typ.b/prov.typ.cat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_providerCategory;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Additional Information-->
	<xsl:template match ="add.info">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_additionalInformationText;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	********************** Education Information section ********************
	************************************************************************-->
	<!-- Education Information -->
	<xsl:template match ="ed.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_educationInfo;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="inst"/>
			<xsl:apply-templates select="grad.d"/>
		</table>
	</xsl:template>

	<!--Institution -->
	<xsl:template match="inst">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_institution;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Graduation Date  -->
	<xsl:template match="grad.d">
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
			<xsl:apply-templates select="rpt.authority"/>
			<xsl:apply-templates select="s.st"/>
		</table>
	</xsl:template>

	<!--Publication-->
	<xsl:template match ="rpt.pub">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_publication;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Authority-->
	<xsl:template match ="rpt.authority">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_authority;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--State-->
	<xsl:template match ="s.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_state;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	************************ Sanctions Information Section **********************
	************************************************************************-->
	<xsl:template match="sanc.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_sanctionInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="sanc.typ"/>
			<xsl:apply-templates select="title"/>
			<xsl:apply-templates select="occup"/>
			<xsl:apply-templates select="cs.title"/>
			<xsl:apply-templates select="cs.nbr"/>
			<xsl:apply-templates select="docket.nbr"/>
			<xsl:apply-templates select="action.sum"/>
			<xsl:apply-templates select="ext.doc.ref"/>
			<xsl:apply-templates select="related.ent"/>
			<xsl:apply-templates select="position"/>
			<xsl:apply-templates select="action1.b/act.typ"/>
			<xsl:apply-templates select ="action1.b/act.term"/>
			<xsl:apply-templates select ="action1.b/act.cd"/>
			<xsl:apply-templates select="action1.b/act.beg.d"/>
			<xsl:apply-templates select ="action1.b/act.end.d"/>
			<xsl:apply-templates select ="chrg1"/>
			<xsl:apply-templates select="finding1"/>
			<xsl:apply-templates select="note1.b/note"/>
			<xsl:apply-templates select="note1.b/note.d"/>
			<xsl:apply-templates select ="note1.b/note.src"/>
			<xsl:apply-templates select="note2.b/note"/>
			<xsl:apply-templates select ="note2.b/note.d"/>
			<xsl:apply-templates select ="note2.b/note.src"/>
			<xsl:apply-templates select="note3.b/note"/>
			<xsl:apply-templates select ="note3.b/note.d"/>
			<xsl:apply-templates select ="note3.b/note.src"/>
		</table>
	</xsl:template>

	<!--Sanction Type-->
	<xsl:template match ="sanc.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sanctionType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Title-->
	<xsl:template match ="title">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_title;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Occupation-->
	<xsl:template match ="occup">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_occupation;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Legal Title-->
	<xsl:template match ="cs.title">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseLegalTitle;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Number-->
	<xsl:template match ="cs.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Docket Number-->
	<xsl:template match ="docket.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_docketNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Action Summary-->
	<xsl:template match ="action.sum">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actionSummary;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--External Doc Reference-->
	<xsl:template match ="ext.doc.ref">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_externalDocReference;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Related Entity Description-->
	<xsl:template match ="related.ent">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_relatedEntityDescription;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Subject Position Held-->
	<xsl:template match ="position">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_subjectPositionHeld;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Action Type-->
	<xsl:template match ="action1.b/act.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actionType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Action Term-->
	<xsl:template match ="action1.b/act.term">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actionTerm;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Action Code-->
	<xsl:template match ="action1.b/act.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actionCode;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Action Start Date-->
	<xsl:template match ="action1.b/act.beg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actionStartDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Action End Date-->
	<xsl:template match ="action1.b/act.end.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actionEndDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Charge-->
	<xsl:template match ="chrg1">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_charge;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Finding-->
	<xsl:template match ="finding1">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_finding;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Note 1-->
	<xsl:template match ="note1.b/note">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_note1;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Note 1 Date-->
	<xsl:template match ="note1.b/note.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_note1Date;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Note 1 Source-->
	<xsl:template match ="note1.b/note.src">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_note1Source;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Note 2-->
	<xsl:template match ="note2.b/note">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_note2;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Note 2 Date-->
	<xsl:template match ="note2.b/note.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_note2Date;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Note 2 Source-->
	<xsl:template match ="note2.b/note.src">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_note2Source;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Note 3-->
	<xsl:template match ="note3.b/note">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_note3;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Note 3 Date-->
	<xsl:template match ="note3.b/note.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_note3Date;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Note 3 Source-->
	<xsl:template match ="note3.b/note.src">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_note3Source;'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
