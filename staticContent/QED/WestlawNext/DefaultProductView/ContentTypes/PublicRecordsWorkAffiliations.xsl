<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>

	<!-- Work Affiliations -->
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--Variables-->
	<xsl:variable name="lowerCase" select="'abcdefghijklmnopqrstuvwxyz'"/>
	<xsl:variable name="upperCase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
	<xsl:variable name="pvalue" select="translate(normalize-space(/Document/n-docbody/r/p),$lowerCase,$upperCase)"/>
	<xsl:variable name="pcvalue" select="translate(normalize-space(/Document/n-docbody/r/pc),$lowerCase,$upperCase)"/>
	<xsl:variable name="cvalue" select="translate(normalize-space(/Document/n-docbody/r/coll.nm),$lowerCase,$upperCase)"/>

	<!-- Do not render these nodes -->
	<xsl:template match="bus.descr.b | prim.cnty.cd | leg.immed.no | leg.ult.no | aff.immed.no | aff.ult.no |aff.glob.ult.ind | aff.par.ind
					| aff.link.ind | leg.ult.ind | leg.par.ind | leg.link.ult.ind | leg.entity.ind | efx.id | new.efx.id | efx.delta"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsWorkAffiliationsClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader" priority="1">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_workAffiliationsRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn" priority="1">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
		</xsl:apply-templates>
		<xsl:if test="($pcvalue='EXE') or ($pcvalue='EXR')">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_workAffiliationInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="bus.info.b"/>
			</table>
		</xsl:if>
		<xsl:if test="($pvalue='PROF. LICENSES') and (emp.info.b/emp.b/emp.na.b or emp.info.b/emp.b/org.ind.b or emp.info.b/emp.b/emp.addr.b or emp.info.b/emp.b/emp.cnty.b or emp.info.b/emp.b/emp.ctry.b or 
								emp.info.b/emp.b/emp.phn.b or emp.info.b/emp.b/emp.sts.b or historical.b/chg.d.b/chg.d or historical.b/prev.na.b or historical.b/prev.addr.b or 
								historical.b/prev.cnty.b or historical.b/prev.ctry.b or historical.b/prev.phn.b or na.prof.info.b/birth.d.b or na.prof.info.b/gender.b or na.prof.info.b/race.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_occupationOrLicenseInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="na.prof.info.b/na.b"/>
				<xsl:apply-templates select="licensing.info.b/license.info.b/lic.agcy.b"/>
				<xsl:apply-templates select="licensing.info.b/license.info.b/certified.spec.info/lic.t.b"/>
				<xsl:apply-templates select="na.prof.info.b/personal.addr.b"/>
				<xsl:apply-templates select="na.prof.info.b/ctry.b/ctry"/>
				<xsl:apply-templates select="na.prof.info.b/phn.b/phn"/>
				<xsl:apply-templates select="na.prof.info.b/email.b/eml"/>
				<xsl:apply-templates select="licensing.info.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn" priority="1">
		<xsl:choose>
			<xsl:when test="$pcvalue='FBN' or $pvalue='WORK-AFFILIATIONS' or (($pvalue='PROF. LICENSES') and (emp.info.b/emp.b/emp.na.b or emp.info.b/emp.b/org.ind.b or
						emp.info.b/emp.b/emp.addr.b or emp.info.b/emp.b/emp.cnty.b or emp.info.b/emp.b/emp.ctry.b or emp.info.b/emp.b/emp.phn.b or
						emp.info.b/emp.b/emp.sts.b)) or $pcvalue='COP' or $pcvalue='DMI' or $pcvalue='BUF' or $pcvalue='BUC' or $pcvalue='EXR' or $pcvalue='EQF' or $cvalue='EXAFF'">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_workAffiliationInformation;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$pvalue='PROF. LICENSES' and not(emp.info.b/emp.b/emp.na.b or emp.info.b/emp.b/org.ind.b or emp.info.b/emp.b/emp.addr.b or emp.info.b/emp.b/emp.cnty.b or emp.info.b/emp.b/emp.ctry.b or 
								emp.info.b/emp.b/emp.phn.b or emp.info.b/emp.b/emp.sts.b or historical.b/chg.d.b/chg.d or historical.b/prev.na.b or historical.b/prev.addr.b or 
								historical.b/prev.cnty.b or historical.b/prev.ctry.b or historical.b/prev.phn.b or na.prof.info.b/birth.d.b or na.prof.info.b/gender.b or na.prof.info.b/race.b)">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_occupationOrLicenseInformation;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$pcvalue='EXE'">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_individualInformation;'"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
		<table class="&pr_table;">
			<xsl:choose>
				<xsl:when test="$pcvalue='EXE' or $pcvalue='EXR'">
					<xsl:apply-templates select="exec.info.b"/>
					<xsl:apply-templates select="home.info.b"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="exec.info.b"/>
					<xsl:apply-templates select="cont.info.b"/>
					<xsl:apply-templates select="bus.info.b"/>
					<xsl:apply-templates select="rec.updt.b"/>
					<xsl:apply-templates select="nm.info.b"/>
					<xsl:apply-templates select="con.loc.b"/>
					<xsl:apply-templates select="cn.info.b"/>
					<xsl:apply-templates select="execs.b"/>
					<xsl:apply-templates select="prin.info/prin.info.b"/>
					<xsl:apply-templates select="co.info.b"/>
					<xsl:if test="$pcvalue='COP'">
						<xsl:apply-templates select="pre/upd.d.b/upd.d"/>
					</xsl:if>
					<xsl:if test="$pcvalue='DMI'">
						<xsl:apply-templates select="relation.b/rpt.d.b/rpt.d"/>
					</xsl:if>
					<xsl:apply-templates select="exec.info.b/ttl.b/ceo.ind"/>
					<xsl:apply-templates select="mgmt.resp.b"/>
					<xsl:apply-templates select="exec.bio"/>
					<xsl:apply-templates select="exec.info.b/dob"/>
					<xsl:apply-templates select="exaff.b/exec.info.b"/>
					<xsl:apply-templates select="exaff.b/co.info.b"/>
					<xsl:choose>
						<xsl:when test="($pvalue='PROF. LICENSES') and (emp.info.b/emp.b/emp.na.b or emp.info.b/emp.b/org.ind.b or emp.info.b/emp.b/emp.addr.b or emp.info.b/emp.b/emp.cnty.b or emp.info.b/emp.b/emp.ctry.b or 
								emp.info.b/emp.b/emp.phn.b or emp.info.b/emp.b/emp.sts.b or historical.b/chg.d.b/chg.d or historical.b/prev.na.b or historical.b/prev.addr.b or 
								historical.b/prev.cnty.b or historical.b/prev.ctry.b or historical.b/prev.phn.b or na.prof.info.b/birth.d.b or na.prof.info.b/gender.b or na.prof.info.b/race.b)">
							<xsl:apply-templates select="emp.info.b"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="na.prof.info.b/na.b"/>
							<xsl:apply-templates select="licensing.info.b/license.info.b/lic.agcy.b"/>
							<xsl:apply-templates select="licensing.info.b/license.info.b/certified.spec.info/lic.t.b"/>
							<xsl:apply-templates select="na.prof.info.b/personal.addr.b"/>
							<xsl:apply-templates select="na.prof.info.b/ctry.b/ctry"/>
							<xsl:apply-templates select="na.prof.info.b/phn.b/phn"/>
							<xsl:apply-templates select="na.prof.info.b/email.b/eml"/>
							<xsl:apply-templates select="licensing.info.b"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:apply-templates select="name.b"/>
					<xsl:apply-templates select="title"/>
					<xsl:apply-templates select="bus.name"/>
					<xsl:apply-templates select="bus.addr.b"/>
					<xsl:apply-templates select="bus.phn.b"/>
					<xsl:apply-templates select="bus.phn.fgn.b"/>
					<xsl:apply-templates select="bus.email.b"/>
					<xsl:apply-templates select="update.d"/>
					<xsl:apply-templates select="inquiry.d"/>
					<xsl:apply-templates select="bus.phn"/>
					<xsl:apply-templates select="mnths.emp"/>
				</xsl:otherwise>
			</xsl:choose>
		</table>
		<xsl:if test="($pvalue='PROF. LICENSES') and (historical.b/chg.d.b/chg.d or historical.b/prev.na.b or historical.b/prev.addr.b or historical.b/prev.cnty.b or 
									historical.b/prev.ctry.b or historical.b/prev.phn.b or na.prof.info.b/birth.d.b or na.prof.info.b/gender.b or na.prof.info.b/race.b)">
			<xsl:apply-templates select="na.prof.info.b"/>
			<xsl:apply-templates select="historical.b"/>

		</xsl:if>
		<xsl:if test="$pvalue='WORK-AFFILIATIONS' and $cvalue!='WAFBF'">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_individualInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="birth.d"/>
				<xsl:apply-templates select="dob"/>
				<xsl:apply-templates select="lic.iss.st"/>
				<xsl:apply-templates select="mil.serv"/>
				<xsl:apply-templates select="home.addr.b"/>
				<xsl:apply-templates select="home.phn"/>
				<xsl:apply-templates select="cell.phn"/>
				<xsl:apply-templates select="email.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="CoverageMeta" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_sourceInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="CurrentThroughDate"/>
			<xsl:apply-templates select="DatabaseLastUpdated"/>
			<xsl:apply-templates select="UpdateFrequency"/>
			<xsl:apply-templates select="CurrentDate"/>
			<xsl:apply-templates select="Source"/>
		</table>
	</xsl:template>

	<!-- Source -->
	<xsl:template match="Source" priority="1">
		<xsl:param name="Label"/>

		<xsl:if test="normalize-space(.)">
			<tr>
				<!--Label-->
				<th>
					<xsl:choose>
						<xsl:when test="$Label">
							<xsl:value-of select="$Label"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&pr_source;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</th>
				<!--Data-->
				<td>
					<xsl:choose>
						<xsl:when test="contains($pcvalue, 'EXB')">
							<xsl:text>EXECUTIVE BIOGRAPHY</xsl:text>
						</xsl:when>
						<xsl:when test="contains($pcvalue, 'DMI')">
							<xsl:text>D &amp; B MARKET IDENTIFIERS</xsl:text>
						</xsl:when>
						<xsl:when test="contains($pcvalue, 'FBN')">
							<xsl:text>FICTITIOUS BUSINESS NAMES</xsl:text>
						</xsl:when>
						<xsl:when test="contains($pcvalue, 'COP')">
							<xsl:text>CORPORATE FILINGS</xsl:text>
						</xsl:when>
						<xsl:when test="contains($pcvalue, 'BUC')">
							<xsl:text>CANADA BUSINESS FINDER</xsl:text>
						</xsl:when>
						<xsl:when test="contains($pcvalue, 'BUF')">
							<xsl:text>US BUSINESS FINDER</xsl:text>
						</xsl:when>
						<xsl:when test="contains($pvalue, 'PROF. LICENSES')">
							<xsl:text>PROFESSIONAL LICENSES</xsl:text>
						</xsl:when>
						<xsl:when test="contains($pcvalue, 'EQF')">
							<xsl:text>BUSINESS PROFILE</xsl:text>
						</xsl:when>
						<xsl:when test="contains($pcvalue, 'EXE') or contains($pcvalue, 'EXR')">
							<xsl:text>EXECUTIVE PROFILE</xsl:text>
						</xsl:when>
						<xsl:when test="($cvalue='EXAFF') or ($pcvalue='EXA')">
							<xsl:text>D&amp;B EXEC FILE!</xsl:text>
						</xsl:when>
					</xsl:choose>
					<div>
						<xsl:apply-templates/>
					</div>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- ********************************************************************** 
	*******************  PROFLICENSE - WAF INFO  *************************************
	************************************************************************-->

	<xsl:template match="emp.info.b[emp.b/emp.na.b or emp.b/org.ind.b or emp.b/emp.addr.b or emp.b/emp.cnty.b or emp.b/emp.ctry.b or emp.b/emp.phn.b or emp.b/emp.sts.b]">
		<xsl:apply-templates select="emp.b"/>
	</xsl:template>

	<xsl:template match="emp.b">
		<xsl:apply-templates select="emp.na.b"/>
		<xsl:apply-templates select="org.ind.b"/>
		<xsl:apply-templates select="emp.addr.b">
			<xsl:with-param name="labelParam" select="'&pr_businessAddress;'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="emp.cnty.b"/>
		<xsl:apply-templates select="emp.ctry.b"/>
		<xsl:apply-templates select="emp.phn.b"/>
		<xsl:apply-templates select="emp.sts.b"/>
	</xsl:template>

	<xsl:template match="emp.na.b">
		<xsl:apply-templates select="emp"/>
	</xsl:template>

	<!-- Name information-->
	<xsl:template match="emp | co.nm[not(pri)] | pri | co.name">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessName;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="org.ind.b">
		<xsl:apply-templates select="org.ind"/>
	</xsl:template>

	<!-- Title information-->
	<xsl:template match="org.ind | ind.cd | ind.cdc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_organization;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- County information-->
	<xsl:template match="emp.cnty.b | cnty.nm | cnty.nmc | pri.addr.b/pri.addr/cnty.b/cnty | bus.cnty | co.info.b/cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessCounty;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- Primary Country information-->
	<xsl:template match="emp.ctry.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessCountry;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Employee status information-->
	<xsl:template match="emp.b/emp.sts.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_employmentStatus;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- *********************************************************************
	************************  PROFLICENSE - Historical Information  ************************
	***********************************************************************-->
	<xsl:template match="historical.b">
		<xsl:if test="chg.d.b/chg.d or prev.na.b or prev.addr.b or prev.cnty.b or prev.ctry.b or prev.phn.b">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_historicalInfo;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="chg.d.b/chg.d"/>
				<xsl:apply-templates select="prev.na.b"/>
				<xsl:apply-templates select="prev.addr.b">
					<xsl:with-param name="label" select="'&pr_previousAddress;'"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="prev.cnty.b"/>
				<xsl:apply-templates select="prev.ctry.b"/>
				<xsl:apply-templates select="prev.phn.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Changed Date information-->
	<xsl:template match="chg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_informationChangedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Previous employer information-->
	<xsl:template match="prev.na.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousEmployer;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Previous address information-->
	<xsl:template match="pers.addr.info | emp.addr.b | prev.addr.b | supv.md.addr.b | outlet.addr.b">
		<xsl:param name="label" select="'&pr_address;'"/>
		<xsl:if test="count(addr | prev.addr | emp.addr | outlet.addr) > 1">
			<xsl:for-each select="addr[position() != last()] | prev.addr[position() != last()] | emp.addr[position() != last()] | outlet.addr[position() != last()]">
				<xsl:choose>
					<xsl:when test="position()=1">
						<xsl:call-template name="wrapPublicRecordsItem">
							<xsl:with-param name="defaultLabel" select="$label"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="wrapPublicRecordsItem"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:if>
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label">
				<xsl:if test="count(addr | prev.addr | emp.addr | outlet.addr) &lt; 2">
					<xsl:value-of select="$label"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="street" select="addr[position()=last()] | emp.addr[position()=last()] | prev.addr[position()=last()] | supv.md.addr | outlet.addr[position()=last()]"/>
			<xsl:with-param name="city" select="cty | emp.cty | prev.cty | supv.md.cty | outlet.cty"/>
			<xsl:with-param name="stateOrProvince" select="st | emp.st | prev.st | prev.prov | supv.md.st | outlet.st | prov | emp.prov
											| prev.prov	| supv.md.prov | outlet.prov"/>
			<xsl:with-param name="zip" select="zip | emp.zip | prev.zip | supv.md.zip | outlet.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Previous County information-->
	<xsl:template match="prev.cnty.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Previous Country information-->
	<xsl:template match="prev.ctry.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousCountry;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone information-->
	<xsl:template match="prev.phn.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>


	<!-- *********************************************************************
	************************  PROFLICENSE - Individual Information  ************************
	***********************************************************************-->
	<xsl:template match="na.prof.info.b">
		<xsl:if test="birth.d.b or gender.b or race.b">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_individualInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="birth.d.b"/>
				<xsl:apply-templates select="gender.b"/>
				<xsl:apply-templates select="race.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="gender.b">
		<xsl:apply-templates select="sex"/>
	</xsl:template>

	<xsl:template match="race.b">
		<xsl:apply-templates select="race"/>
	</xsl:template>

	<!-- Previous employer information-->
	<xsl:template match="sex">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Previous County information-->
	<xsl:template match="race">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_race;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- *********************************************************************
	***************  PROFLICENSE - Occupation/License Information  ***************
	***********************************************************************-->
	<xsl:template match="na.b">
		<xsl:choose>
			<xsl:when test="full.na">
				<xsl:apply-templates select="full.na"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="na"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="full.na | name.b | cont.na.b | con.na.b | name | na.b[name(..)='exec.info.b']">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="prefixName" select="na.prefix | na.pre"/>
			<xsl:with-param name="firstName" select="fna | f.nm | first.nm | first.nmc | first"/>
			<xsl:with-param name="middleName" select="mna"/>
			<xsl:with-param name="lastName" select="lna | l.nm | last.nm | last.nmc | last"/>
			<xsl:with-param name="suffixName" select="na.suf"/>
			<xsl:with-param name="professionalSuffixName" select="pro.ttl.cd | pro.ttl.cdc"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="personal.addr.b">
		<xsl:apply-templates select="pers.addr.info"/>
		<xsl:apply-templates select="cnty.b/cnty"/>
	</xsl:template>

	<xsl:template match="cnty[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseeCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ctry">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseeCountry;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="licensing.info.b">
		<xsl:apply-templates select="license.info.b/certified.spec.info/brd.cert.b/brd.cert"/>
		<xsl:apply-templates select="oth.lic.info/lic.add.info.b/lic.add.info | license.info.b/certified.spec.info/lic.cert.b/lic.cert"/>
		<xsl:apply-templates select="license.info.b/certified.spec.info/lic.nbr.b/lic.nbr"/>
		<xsl:apply-templates select="license.info.b/certified.spec.info/lic.stat.b/lic.stat"/>
		<xsl:apply-templates select="license.info.b/specialty.b/spec"/>
		<xsl:apply-templates select="lic.clss.info/lic.st.b/lic.st"/>
		<xsl:apply-templates select="lic.desc.b/lic.desc"/>
	</xsl:template>

	<!-- Do not display -->
	<xsl:template match="lic.t.msg"/>
	<xsl:template match="lic.nbr.msg"/>

	<xsl:template match="lic.agcy.b">
		<xsl:apply-templates select="lic.agcy"/>
	</xsl:template>

	<!-- License Agency information-->
	<xsl:template match="lic.agcy">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licensingAgency;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- License Type information-->
	<xsl:template match="lic.t.b">
		<xsl:apply-templates select="lic.t"/>
	</xsl:template>

	<xsl:template match="lic.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Board Certification information-->
	<xsl:template match="brd.cert">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_boardCertification;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Certification Board information-->
	<xsl:template match="lic.add.info | lic.cert">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_certificationBoard;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- License Number information-->
	<xsl:template match="lic.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- License Status information-->
	<xsl:template match="lic.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Specialty information-->
	<xsl:template match="spec">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_specialty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- License State information-->
	<xsl:template match="lic.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseState;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- License Description information-->
	<xsl:template match="lic.desc.b/lic.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseDescription;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	******************* BUSINESS PROFILE  *************************************
	************************************************************************-->

	<xsl:template match="exec.info.b">
		<xsl:choose>
			<xsl:when test="$pcvalue='EXE'">
				<xsl:apply-templates select="gender"/>
				<xsl:apply-templates select="exec.age"/>
				<xsl:apply-templates select="mr.stat"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="exec.name"/>
				<xsl:apply-templates select="title"/>
				<xsl:apply-templates select="na.b"/>
				<xsl:apply-templates select="exn.b/full.exec.nm"/>
				<xsl:apply-templates select="ttl.b/full.exec.ttl"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Name information-->
	<xsl:template match="exec.name | off.name | full.exec.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_name;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Title information-->
	<xsl:template match="title | ttl.cd | ttl.cdc | funct.ti | exec.title | ti | full.exec.ttl">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_title;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bus.info.b">
		<xsl:apply-templates select="bus.name"/>
		<xsl:apply-templates select="leg.bus.name"/>
		<xsl:apply-templates select="prim.addr.b"/>
		<xsl:apply-templates select="prim.cnty.nm"/>
		<xsl:apply-templates select="secd.addr.b"/>
		<xsl:if test="$pcvalue='EXE' or $pcvalue='EXR'">
			<xsl:apply-templates select="../exec.info.b/exec.name"/>
		</xsl:if>
		<xsl:apply-templates select="exec.title"/>
		<xsl:apply-templates select="co.name"/>
		<xsl:apply-templates select="bus.nm.b/bus.nm"/>
		<xsl:apply-templates select="../filg.info.b/bus.typ.b/bus.typ"/>
		<xsl:apply-templates select="bus.addr.b"/>
		<xsl:apply-templates select="bus.cnty"/>
		<xsl:apply-templates select="bus.phn"/>
		<xsl:apply-templates select="bus.fax"/>
		<xsl:apply-templates select="bus.email.b"/>
		<xsl:apply-templates select="bus.url"/>
		<xsl:apply-templates select="yr.est"/>
		<xsl:apply-templates select="bus.addr.b/cnty.b/cnty"/>
		<xsl:apply-templates select="bus.phn.b"/>
	</xsl:template>

	<!-- Business name information-->
	<xsl:template match="bus.name | bus.nm.b/bus.nm">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_businessName;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Legal Business name information-->
	<xsl:template match="leg.bus.name">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_legalBusinessName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Primary address information-->
	<xsl:template match="prim.addr.b | filg.addr.b | co.info.b/addr | pri.addr">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_businessAddress;'"/>
			<xsl:with-param name="street" select="prim.str | str | filg.str | addr"/>
			<xsl:with-param name="streetLineTwo" select="filg.str2"/>
			<xsl:with-param name="city" select="prim.cty | cty.st.b/cty | filg.cty | cty"/>
			<xsl:with-param name="stateOrProvince" select="prim.st | cty.st.b/st | filg.st | st"/>
			<xsl:with-param name="zip" select="prim.zip.b/prim.zip | zip | filg.zip[not(filg.zip.b/filg.zip)] | filg.zip.b/filg.zip | zip"/>
			<xsl:with-param name="zipExt" select="prim.zip.b/prim.zip.ext | filg.zip.b/filg.zip.ext"/>
			<xsl:with-param name="country" select="../prim.cntry.nm | filg.cntry | ctry"/>
		</xsl:call-template>
	</xsl:template>

	<!-- County information-->
	<xsl:template match="prim.cnty.nm | cnty[name(../..)='bus.addr.b']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Secondary address information-->
	<xsl:template match="secd.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_businessAddressTwo;'"/>
			<xsl:with-param name="street" select="secd.str"/>
			<xsl:with-param name="city" select="secd.cty"/>
			<xsl:with-param name="stateOrProvince" select="secd.st"/>
			<xsl:with-param name="zip" select="secd.zip.b/secd.zip"/>
			<xsl:with-param name="zipExt" select="secd.zip.b/secd.zip.ext"/>
			<xsl:with-param name="country" select="../secd.cntry.nm"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Fax information-->
	<xsl:template match="bus.fax">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessFax;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Email information-->
	<xsl:template match="bus.email.b">
		<xsl:call-template name="wrapPublicRecordsEmail">
			<xsl:with-param name="label" select="'&pr_businessEmail;'"/>
			<xsl:with-param name="email" select="bus.email"/>
			<xsl:with-param name="user" select="user.nm"/>
			<xsl:with-param name="domain" select="domain.nm"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Web Address information-->
	<xsl:template match="bus.url">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessWebAddress;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Year Established information-->
	<xsl:template match="yr.est">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_yearEstablished;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Record Last Updated information-->
	<xsl:template match="rec.updt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_recordLastUpdated;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- *********************************************************************
	************************  WAF - Historical Information  ************************
	***********************************************************************-->
	<!-- Home/Personal Phone information-->
	<xsl:template match="home.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_homePhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Cell Phone information-->
	<xsl:template match="cell.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_cellPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date information-->
	<xsl:template match="inquiry.d | update.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_date;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Employment information -->
	<xsl:template match="mnths.emp">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_monthsEmployed;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Home address information-->
	<xsl:template match="home.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_homeAddress;'"/>
			<xsl:with-param name="street" select="home.str"/>
			<xsl:with-param name="city" select="home.cty"/>
			<xsl:with-param name="stateOrProvince" select="home.st"/>
			<xsl:with-param name="zip" select="home.zip.b/home.zip"/>
			<xsl:with-param name="zipExt" select="home.zip.b/home.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Previous County information-->
	<xsl:template match="email.b">
		<xsl:call-template name="wrapPublicRecordsEmail">
			<xsl:with-param name="email" select="email"/>
			<xsl:with-param name="user" select="user.nm"/>
			<xsl:with-param name="domain" select="domain.nm"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone information-->
	<xsl:template match="prev.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>


	<!-- Driver's License State information-->
	<xsl:template match="lic.iss.st">
		<xsl:variable name="label">
			<xsl:text>&pr_driversLicenseState;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Military Service information-->
	<xsl:template match="mil.serv">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_militaryService;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Date of Birth information-->
	<xsl:template match="birth.d.b">
		<xsl:apply-templates select="birth.d"/>
	</xsl:template>
	<xsl:template match="birth.d | dob">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
			<xsl:with-param name="selectNodes" select="birth.d"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dob">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Home address information-->
	<xsl:template match="bus.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_businessAddress;'"/>
			<xsl:with-param name="street" select="bus.str"/>
			<xsl:with-param name="city" select="bus.cty"/>
			<xsl:with-param name="stateOrProvince" select="bus.st | st.abbr"/>
			<xsl:with-param name="zip" select="bus.zip.b/bus.zip | bus.zip"/>
			<xsl:with-param name="zipExt" select="bus.zip.b/bus.zip.ext | bus.zip.ext"/>
			<xsl:with-param name="country" select="bus.cntry"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone information-->
	<xsl:template match="bus.phn | bus.phn.fgn.b | emp.phn.b | ph.nbr | ph.nbrc | phone.b/phone | co.info.b/phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Phone Ext information-->
	<xsl:template match="bus.phn.fgn.ext | bus.phn.ext">
		<xsl:text><![CDATA[ ]]><![CDATA[ ]]>EXT:<![CDATA[ ]]><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<!-- ********************************************************************** 
	*******************  Business Find - WAF INFO  *************************************
	************************************************************************-->

	<xsl:template match="nm.info.b | con.loc.b">
		<xsl:apply-templates select="con.na.b"/>
		<xsl:apply-templates select="ttl.cd | ttl.cdc"/>
	</xsl:template>


	<xsl:template match="cn.info.b">
		<xsl:apply-templates select="co.nm | co.nmc"/>
		<xsl:apply-templates select="ind.cd | ind.cdc"/>
		<xsl:apply-templates select="addr.b"/>
		<xsl:apply-templates select="cnty.nm | cnty.nmc"/>
		<xsl:apply-templates select="ph.nbr | ph.nbrc"/>
	</xsl:template>

	<!-- Business address information-->
	<xsl:template match="addr.b">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="$pcvalue='COP'">
					<xsl:text>&pr_address;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_businessAddress;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="fullStreet" select="addr | addrc | str"/>
			<xsl:with-param name="city" select="cty | ctyc"/>
			<xsl:with-param name="stateOrProvince" select="st.abbr | provc | st"/>
			<xsl:with-param name="zip" select="zip.b/zip.5.cd | canzip.cd | post.cd | post.cd.b/post.cd | zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.4.cd | post.cd.b/post.cd.ext"/>
			<xsl:with-param name="carrierRoute" select="car.cd"/>
			<xsl:with-param name="country" select="ctry"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******************  DMI - WAF INFO  **************************************
	************************************************************************-->

	<xsl:template match="execs.b">
		<xsl:apply-templates select="exec"/>
	</xsl:template>

	<!-- Executive information-->
	<xsl:template match="exec">
		<xsl:apply-templates select="name"/>
		<xsl:apply-templates select="funct.ti"/>
	</xsl:template>


	<xsl:template match="co.info.b">
		<xsl:apply-templates select="co.name.b/co.name"/>
		<xsl:apply-templates select="filg.addr.b"/>
		<xsl:apply-templates select="co.nm.b/co.nm/pri"/>
		<xsl:apply-templates select="co.nm.b/co.nm[not(pri)]"/>
		<xsl:apply-templates select="co.nm.b/sec.nm"/>
		<xsl:apply-templates select="pri.addr.b/pri.addr"/>
		<xsl:apply-templates select="pri.addr.b/pri.addr/cnty.b/cnty"/>
		<xsl:apply-templates select="phone.b/phone"/>
		<xsl:apply-templates select="co.nm" />
		<xsl:apply-templates select="addr" />
		<xsl:apply-templates select="cnty" />
		<xsl:apply-templates select="phn" />
		<xsl:apply-templates select="trd.sty.b/trd.sty"/>
		<xsl:apply-templates select="addr.b"/>
		<xsl:apply-templates select="phn.b/phn"/>
	</xsl:template>

	<!-- Related Name information-->
	<xsl:template match="sec.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_relatedNames;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sec[position()>1]">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="rpt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_latestUpdateToRecord;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	*******************  Executive Profile - WAF INFO  ************************
	************************************************************************-->

	<!-- Gender information-->
	<xsl:template match="gender">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Age information-->
	<xsl:template match="exec.age">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_age;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Marital Status information-->
	<xsl:template match="mr.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_maritalStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="home.info.b">
		<xsl:apply-templates select="home.addr.b"/>
		<xsl:apply-templates select="home.cnty"/>
		<xsl:apply-templates select="home.phn"/>
	</xsl:template>

	<!-- Home County information-->
	<xsl:template match="home.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_homeCounty;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	*******************  Fictitious Business Name - WAF INFO  *****************
	************************************************************************-->

	<xsl:template match="cont.info.b">
		<xsl:apply-templates select="cont.nme.b"/>
		<xsl:apply-templates select="cont.addr.b"/>
		<xsl:apply-templates select="cont.phn.b/cont.phn"/>
	</xsl:template>

	<!-- Name information-->
	<xsl:template match="cont.nme.b">
		<xsl:apply-templates select="cont.na.b | cont.nme"/>
	</xsl:template>

	<!-- Name information-->
	<xsl:template match="cont.nme">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_name;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address information-->
	<xsl:template match="cont.addr.b">
		<xsl:apply-templates select="cont.addr | con.addr"/>
	</xsl:template>

	<xsl:template match="con.addr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_address;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address information-->
	<xsl:template match="cont.addr">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="cont.str"/>
			<xsl:with-param name="streetLineTwo" select="cont.str2"/>
			<xsl:with-param name="city" select="cont.cty"/>
			<xsl:with-param name="stateOrProvince" select="cont.st"/>
			<xsl:with-param name="zip" select="cont.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Phone information-->
	<xsl:template match="cont.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Type information-->
	<xsl:template match="filg.desc | bus.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Record Last Updated-->
	<xsl:template match="upd.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_thisRecordLastUpdated;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******************  Corporate Reports - WAF INFO  *************************************
	************************************************************************-->

	<xsl:template match="prin.info.b">
		<xsl:apply-templates select="prin"/>
	</xsl:template>

	<!-- Name information-->
	<xsl:template match="prin">
		<xsl:apply-templates select="off.name.b/off.name"/>
		<xsl:apply-templates select="ti.b/ti"/>
		<xsl:apply-templates select="addr.b"/>
	</xsl:template>


	<!-- ********************************************************************** 
	*******************  Exec Biography - WAF INFO  *************************************
	************************************************************************-->

	<!-- Management Responsibilities information-->
	<xsl:template match="mgmt.resp.b[normalize-space(mrc.code.b/mrc.desc)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_managementResponsibilities;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mrc.desc[position()>1]">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Biography information-->
	<xsl:template match="exec.bio">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_biography;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="exec.info.b/ttl.b/ceo.ind">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_otherRole;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="exec.info.b/dob">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_individualInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_yearOfBirth;'"/>
				<xsl:with-param name="nodeType" select="$DATE"/>
			</xsl:call-template>
		</table>
	</xsl:template>


	<!-- ********************************************************************** 
	*******************  D&B Executive - WAF INFO  *************************************
	************************************************************************-->

	<!-- Trade Name information-->
	<xsl:template match="trd.sty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_tradeName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Phone information-->
	<xsl:template match="phn.b/phn">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="$pvalue='PROF. LICENSES'">
					<xsl:text>&pr_phone;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_businessPhone;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="eml">
		<xsl:call-template name="wrapPublicRecordsEmail">
			<xsl:with-param name="label" select="'&pr_email;'"/>
			<xsl:with-param name="email" select="."/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
