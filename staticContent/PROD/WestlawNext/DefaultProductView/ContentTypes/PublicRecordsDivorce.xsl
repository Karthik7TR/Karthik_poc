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

	<!-- Variable(s) -->
	<xsl:variable name="pcvalue" select="/Document/n-docbody/r/pc" />

	<!-- Never render these elements -->
	<xsl:template match="map|p|pc|legacy.id|update.link.block|source|full.state|col.key|scrape.date|publish.date|convert.date"/>
	<xsl:template match="title.block|division.block|jury.demand.block|panel.block|docket.proceedings.block|location.block|case.disposition.block"/>
	<xsl:template match="case.misc.block|attorney.status.block|firm.block|attorney.name.block|attorney.department.block|calendar.block"/>
	<xsl:template match="lower.court.block/lower.court.information/docket.block/docket.number"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r" priority="1">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsDivorceClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_divorceRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!--  Source Information  -->
		<xsl:apply-templates select="$coverage-block"/>
		
		<xsl:call-template name="DocketSection"/>
		<xsl:call-template name="NameInformationSection"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:call-template name="FilingOrDecreeInformationSection"/>
		<xsl:call-template name="OrderDocuments"/>
	</xsl:template>

	<!--Docket Current Through-->
	<xsl:template name="DocketSection">
		<xsl:if test="descendant::court.norm and not(descendant::court.norm=('FL-DADE') or descendant::court.norm=('IL-COOK'))">
			<!--This docket is current through !-->
			<xsl:text>&pr_docketCurrentThrough;<![CDATA[ ]]></xsl:text>
			<xsl:choose>
				<xsl:when test="descendant::index.scrape.date">
					<xsl:call-template name="FormatDate">
						<xsl:with-param name="displaySensitive" select="true()"/>
						<xsl:with-param name="dateNode" select ="descendant::index.scrape.date"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="descendant::scrape.date">
					<xsl:call-template name="FormatDate">
						<xsl:with-param name="displaySensitive" select="true()"/>
						<xsl:with-param name ="dateNode" select ="descendant::scrape.date"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="descendant::publish.date">
					<xsl:call-template name="FormatDate">
						<xsl:with-param name="displaySensitive" select="true()"/>
						<xsl:with-param name ="dateNode" select ="descendant::publish.date"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- *********************************************************************
	*********************  (B)"NAME INFORMATION" section  ********************
	************************************************************************-->
	<xsl:template name="NameInformationSection">
		<xsl:if test="(descendant::matched.party.name) or (descendant::party.name) or (descendant::pltf.nm.b) or (descendant::def.nm.b) 
					  or (descendant::petitioner.b) or (descendant::respondent.b) or (descendant::husband.b) or (descendant::wife.b) or (descendant::primary.title)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_nameInformationSubheader;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:if test="(descendant::matched.party.name) or (descendant::party.name) or (descendant::primary.title)">
					<xsl:choose>
						<xsl:when test="(descendant::matched.party.name) or (descendant::party.name)">
							<xsl:apply-templates select="descendant::matched.party.name"/>
							<xsl:apply-templates select="descendant::party.name"/>
						</xsl:when>
						<xsl:when test="(not(descendant::matched.party.name) or not(descendant::party.name)) and (//pc='SDK') and (descendant::primary.title)">
							<xsl:apply-templates select="descendant::primary.title"/>
						</xsl:when>
					</xsl:choose>
				</xsl:if>
				<xsl:apply-templates select="descendant::pltf.nm.b"/>
				<xsl:apply-templates select="descendant::def.nm.b"/>
				<xsl:apply-templates select="descendant::petitioner.b"/>
				<xsl:apply-templates select="descendant::respondent.b"/>
				<xsl:apply-templates select="descendant::husband.b"/>
				<xsl:apply-templates select="descendant::wife.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!--matched.party.name for name information -->
	<xsl:template match="matched.party.name">
		<xsl:variable name="name" select="preceding-sibling::label" />
		<xsl:variable name="type" select="parent::matched.party.name.block/following-sibling::matched.party.type.block/matched.party.type" />
		<xsl:variable name="nametype">
			<xsl:value-of select="concat($type, ' ', $name)"/>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="$nametype"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--party.name for name information -->
	<xsl:template match="party.name">
		<xsl:variable name="partyname" select="preceding-sibling::label" />
		<xsl:variable name="partytype" select="parent::party.name.block/following-sibling::party.type.block/party.type" />
		<xsl:variable name="partynametype">
			<xsl:value-of select="concat($partytype, ' ', $partyname)"/>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'$partynametype;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--primary.title for name information -->
	<xsl:template match="primary.title">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_names;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--pltf.nm.b for name information -->
	<xsl:template match="pltf.nm.b">
		<xsl:if test="position()=1">
			<xsl:apply-templates select="pltf.nm"/>
		</xsl:if>
		<xsl:apply-templates select="pltf.cty"/>
		<xsl:apply-templates select="pltf.st"/>
	</xsl:template>

	<!--def.nm.b for name information -->
	<xsl:template match="def.nm.b">
		<xsl:if test="position()=1">
			<xsl:apply-templates select="def.nm"/>
		</xsl:if>
		<xsl:apply-templates select="def.cty"/>
		<xsl:apply-templates select="def.st"/>
	</xsl:template>

	<!--petitioner.b block for name information -->
	<xsl:template match="petitioner.b">
		<xsl:apply-templates select="nm"/>
		<xsl:apply-templates select="res.cty"/>
		<xsl:apply-templates select="cnty"/>
		<xsl:apply-templates select="res.st"/>
		<xsl:apply-templates select="birth.p"/>
		<xsl:apply-templates select="birth.d"/>
		<xsl:apply-templates select="age"/>
	</xsl:template>

	<!--respondent.b block for name information -->
	<xsl:template match="respondent.b">
		<xsl:apply-templates select="nm"/>
		<xsl:apply-templates select="cnty"/>
		<xsl:apply-templates select="birth.d"/>
		<xsl:apply-templates select="age"/>
	</xsl:template>

	<!--husband.b block for name information -->
	<xsl:template match="husband.b">
		<xsl:apply-templates select="nm"/>
		<xsl:apply-templates select="res.cty"/>
		<xsl:apply-templates select="cnty"/>
		<xsl:apply-templates select="res.st"/>
		<xsl:apply-templates select="birth.p"/>
		<xsl:apply-templates select="husb.birth.d"/>
		<xsl:apply-templates select="age"/>
		<xsl:apply-templates select="edu"/>
	</xsl:template>

	<!--wife.b block for name information -->
	<xsl:template match="wife.b">
		<xsl:apply-templates select="nm"/>
		<xsl:apply-templates select="res.cty"/>
		<xsl:apply-templates select="res.st"/>
		<xsl:apply-templates select="cnty"/>
		<xsl:apply-templates select="birth.p"/>
		<xsl:apply-templates select="wife.birth.d"/>
		<xsl:apply-templates select="age"/>
		<xsl:apply-templates select="edu"/>
	</xsl:template>

	<!--Name -->
	<xsl:template match="pltf.nm|def.nm|nm">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--City of Residence -->
	<xsl:template match="pltf.cty|def.cty|res.cty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_cityOfResidence;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--County of Residence -->
	<xsl:template match="cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countyOfResidence;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--State of Residence -->
	<xsl:template match="pltf.st|def.st|res.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateOfResidence;'"/>
			<xsl:with-param name="nodeType" select="$STATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Place of Birth -->
	<xsl:template match="birth.p">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_placeOfBirth;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date of Birth -->
	<xsl:template match="birth.d|husb.birth.d|wife.birth.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Age at Divorce-->
	<xsl:template match="age">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ageAtDivorce;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Education-->
	<xsl:template match="edu">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_education;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- *********************************************************************
	**************  (C)"FILING OR DECREE INFORMATION" section  ***************
	************************************************************************-->
	<xsl:template name="FilingOrDecreeInformationSection">
		<xsl:choose>
			<xsl:when test="(descendant::filg.d) or (descendant::filg.st) or(descendant::cs.typ) or (descendant::venue.cd)
						or (descendant::filing.date.block) or (descendant::filing.county) or (descendant::state.postal)
						or (descendant::docket.number) or (descendant::case.type.block) or (descendant::case.subtype.block)
						or (descendant::key.nature.of.suit.block) or (descendant::case.status.block)
						or (descendant::disposition.date.block)">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_filingInfo;'"/>
				</xsl:call-template>
				<table class="&pr_table;">
					<xsl:choose>
						<xsl:when test="$pcvalue='SDK'">
							<xsl:apply-templates select="descendant::filing.date"/>
							<xsl:apply-templates select="descendant::filing.county"/>
							<xsl:apply-templates select="descendant::state.postal"/>
							<xsl:apply-templates select="descendant::docket.number"/>
							<xsl:apply-templates select="descendant::case.type"/>
							<xsl:apply-templates select="descendant::case.subtype"/>
							<xsl:apply-templates select="descendant::key.nature.of.suit.block"/>
							<xsl:apply-templates select="descendant::case.status"/>
							<xsl:apply-templates select="descendant::disposition.date"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="descendant::filg.d"/>
							<xsl:apply-templates select="descendant::venue.cd"/>
							<xsl:apply-templates select="descendant::filg.st"/>
							<xsl:apply-templates select="descendant::cs.nbr"/>
							<xsl:apply-templates select="descendant::cs.typ"/>
						</xsl:otherwise>
					</xsl:choose>
				</table>
			</xsl:when>
			<xsl:when test="(descendant::decree.dis.info) or (descendant::filg.info)">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_disolutionInfo;'"/>
				</xsl:call-template>
				<table class="&pr_table;">
					<xsl:apply-templates select="descendant::petitioner.typ"/>
					<xsl:apply-templates select="descendant::decree.cnty"/>
					<xsl:apply-templates select="descendant::decree.d"/>
					<xsl:apply-templates select="descendant::decree.typ"/>
					<xsl:apply-templates select="descendant::mar.cnty"/>
					<xsl:apply-templates select="descendant::mar.st"/>
					<xsl:apply-templates select="descendant::mar.d"/>
					<xsl:apply-templates select="descendant::separation.d"/>
					<xsl:apply-templates select="descendant::filg.cnty"/>
					<xsl:apply-templates select="descendant::filg.d"/>
				</table>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!--Date Filed -->
	<xsl:template match="filing.date">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--County Filed -->
	<xsl:template match="filing.county|venue.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countyFiled;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- State Filed -->
	<xsl:template match="state.postal">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateFiled;'"/>
			<xsl:with-param name="nodeType" select="$STATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Number -->
	<xsl:template match="docket.number">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="preceding-sibling::label"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Type -->
	<xsl:template match="case.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="preceding-sibling::label"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Subtype -->
	<xsl:template match="case.subtype">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="preceding-sibling::label"/>
		</xsl:call-template>
	</xsl:template>

	<!--Key Nature of Suit -->
	<xsl:template match="key.nature.of.suit.block">
		<!--According to DTD, there can be up to 3 knos.level block-->
		<xsl:variable name="knos1" select="knos.level1.block/knos.level1" />
		<xsl:variable name="knos2" select="knos.level2.block/knos.level2" />
		<xsl:variable name="knos3" select="knos.level3.block/knos.level3" />
		<xsl:variable name="knosCode" select="knos.code" />

		<tr>
			<th>
				<xsl:text>&pr_knos;</xsl:text>
			</th>
			<td>
				<!--<xsl:apply-templates select="msxsl:node-set($knos)/text()" />-->
				<xsl:choose>
					<xsl:when test="$knos1 and $knos2 and $knos3">
						<xsl:apply-templates select="knos.level1.block/knos.level1" />
						<xsl:text>;<![CDATA[ ]]></xsl:text>
						<xsl:apply-templates select="knos.level2.block/knos.level2" />
						<xsl:text>;<![CDATA[ ]]></xsl:text>
						<xsl:apply-templates select="knos.level3.block/knos.level3" />
						<xsl:text><![CDATA[ ]]>(</xsl:text>
						<xsl:apply-templates select="knos.code" />
						<xsl:text>)</xsl:text>
					</xsl:when>
					<xsl:when test="$knos1 and $knos2 and not($knos3)">
						<xsl:apply-templates select="knos.level1.block/knos.level1" />
						<xsl:text>;<![CDATA[ ]]></xsl:text>
						<xsl:apply-templates select="knos.level2.block/knos.level2" />
						<xsl:text><![CDATA[ ]]>(</xsl:text>
						<xsl:apply-templates select="knos.code" />
						<xsl:text>)</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="knos.level1.block/knos.level1" />
						<xsl:text><![CDATA[ ]]>(</xsl:text>
						<xsl:apply-templates select="knos.code" />
						<xsl:text>)</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!--Case Status -->
	<xsl:template match="case.status">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="preceding-sibling::label"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Disposition Date -->
	<xsl:template match="disposition.date">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dispositionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--State Filed when node is filg.st -->
	<xsl:template match="filg.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateFiled;'"/>
			<xsl:with-param name="nodeType" select="$STATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Number when node is cs.nbr -->
	<xsl:template match="cs.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Case Type when node is cs.typ -->
	<xsl:template match="cs.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Petitioner-->
	<xsl:template match="petitioner.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_petitioner;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--County of Decree-->
	<xsl:template match="decree.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countyOfDecree;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--State of Decree-->
	<xsl:template match="decree.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateOfDecree;'"/>
			<xsl:with-param name="nodeType" select="$STATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date of Decree-->
	<xsl:template match="decree.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfDecree;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Type of Decree-->
	<xsl:template match="decree.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_typeOfDecree;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--County of Marriage-->
	<xsl:template match="mar.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countyOfMarriage;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--State of Marriage-->
	<xsl:template match="mar.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_stateOfMarriage;'"/>
			<xsl:with-param name="nodeType" select="$STATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date of Marriage-->
	<xsl:template match="mar.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfMarriage;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Date Couple Separated-->
	<xsl:template match="separation.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateCoupleSeperated;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing County for decree & dissolution information-->
	<xsl:template match="filg.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Date for decree & dissolution information or Date Filed when node is filg.d-->
	<xsl:template match="filg.d">
		<xsl:choose>
			<xsl:when test="parent::filg.info">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_fileDate;'"/>
					<xsl:with-param name="nodeType" select="$DATE"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_dateFiled;'"/>
					<xsl:with-param name="nodeType" select="$DATE"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="OrderDocuments">
		<xsl:if test="not(/Document/n-docbody/r/restrict='SOR')">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_orderDocuments;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:call-template name="FormatOrderDocs">
				</xsl:call-template>
			</table>
		</xsl:if>
	</xsl:template>


</xsl:stylesheet>