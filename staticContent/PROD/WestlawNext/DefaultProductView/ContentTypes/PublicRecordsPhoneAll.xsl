<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsDate.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<!-- Comprehensive Phone Record - PHONE-ALL -->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Do not render these nodes -->
	<xsl:template match="p|pc|c|col.key|coll.nm|map|coverageData|s|ext.addr|del|r8|r9|r10|ttl.cdc|cnty.nmc|prm.sic.b|bus.sic.b|ind.cdc|pop.cdc|sls.vol.cdc|
				  emp.sz.cdc|adsz.cdc|mrc|funct.ti|van.ti|l|duns.b|incorp.b|cnty.nm|ind.cd|sls.vol.cd|emp.sz.cd|pop.cd|adsz.cd|ssn.b|birth.b|rpt.d|
				  demo.b|m.id|relate|lati|long|res.typ|arrv.d|svc.typ|ctry.b|label|gender|race.b|gender.b|birth.d.b|chg.d.b|prev.cnty.b|prev.ctry.b|prof.ti.b|
				  fax.b|email.b|prac.cnty.b|sec.nm|mail.addr.b|frn.spec.desc|hdq.bus.cdc|ttl.cd|hdq.bus.cd|pub.cd|addr.t.b|ind.spec.cd"/>

	<!-- Desired output view: content - renders document (default) -->
	<xsl:variable name ="pcVal" select ="/Document/n-docbody/r/pc"/>
	<xsl:variable name ="pVal" select ="/Document/n-docbody/r/p"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsBusinessProfileClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_comprehensivePhoneRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!--  Source Information  -->
		<xsl:apply-templates select="$coverage-block"/>
		
		<!-- Render Phone Information -->
		<xsl:call-template name="PhoneInformation"/>
	</xsl:template>

	<!-- **********************************************************************
	******************** "Phone Information" section **************************
	************************************************************************-->
	<xsl:template name="PhoneInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_phoneInformation;'"/>
		</xsl:call-template>

		<table class="&pr_table;">
			<!-- Render data for People-Find records -->
			<xsl:if test ="$pcVal='PFR'">
				<xsl:apply-templates select ="list.b/name.b"/>
				<xsl:apply-templates select ="list.b/curr.addr.b/addr.b"/>
				<xsl:apply-templates select ="list.b/curr.addr.b/phn.b"/>
				<xsl:apply-templates select ="list.b/hist.phn"/>
				<xsl:apply-templates select ="list.b/prev.addr.b"/>
			</xsl:if>
			<!-- Render data for DMI records -->
			<xsl:if test ="$pcVal='DMI'">
				<xsl:apply-templates select ="execs.b/exec"/>
				<xsl:apply-templates select ="co.info.b"/>
			</xsl:if>
			<!-- Render data for People-HH and Person-Phone records -->
			<xsl:if test ="$pcVal='PEO'">
				<xsl:apply-templates select ="member"/>
				<xsl:apply-templates select ="pub.d"/>
				<xsl:apply-templates select ="addr.info/addr.b"/>
				<xsl:apply-templates select ="addr.info/cnty"/>
				<xsl:apply-templates select ="addr.info/phn.b/phn[position()=1]/phn.nbr"/>
			</xsl:if>
			<!-- Bus Find US records -->
			<xsl:if test="$pcVal='BUF'">
				<xsl:apply-templates select="nm.info.b"/>
				<xsl:apply-templates select="cn.info.b/co.nm"/>
				<xsl:apply-templates select="cn.info.b/addr.b"/>
				<xsl:apply-templates select="cn.info.b/cnty.nm"/>
				<xsl:apply-templates select="cn.info.b/ph.nbr"/>
			</xsl:if>
			<xsl:if test="$pcVal='WAF' or $pcVal='SUB'">
				<xsl:apply-templates select="name.b"/>
				<xsl:apply-templates select="home.addr.b"/>
				<xsl:apply-templates select="home.phn"/>
				<xsl:apply-templates select="cell.phn"/>
				<xsl:apply-templates select="bus.phn"/>
			</xsl:if>
			<xsl:if test="not($pcVal='BUF')">
				<!-- Render data for all remaining content types (Prof. License, Bus Find US, People-Canada) -->
				<xsl:apply-templates select="con.loc.b|na.prof.info.b|ind.info.b|nm.info.b"/>
				<xsl:apply-templates select="cn.info.b|historical.b"/>
			</xsl:if>
		</table>
	</xsl:template>

	<!-- **********************************************************************
	******************** "Consumer Phone Information" section *****************
	************************************************************************-->
	<!-- Submarine Name -->
	<xsl:template match="name.b[/Document/n-docbody/r/pc='WAF' or /Document/n-docbody/r/pc='SUB']">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="f.nm"/>
			<xsl:with-param name="middleName" select="m.nm"/>
			<xsl:with-param name="lastName" select="l.nm"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Submarine Phones -->
	<xsl:template match="home.phn | cell.phn | bus.phn">
		<xsl:variable name="phoneLabel">
			<xsl:text>&pr_phonePrefix;</xsl:text>
			<xsl:value-of select="position()" />:
			<xsl:text>:</xsl:text>
		</xsl:variable>

		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$phoneLabel"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Submarine Address -->
	<xsl:template match="home.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_address;'"/>
			<xsl:with-param name="streetNum" select="hse.nbr"/>
			<xsl:with-param name="street" select="home.str" />
			<xsl:with-param name="city" select="home.cty"/>
			<xsl:with-param name="stateOrProvince" select="home.st"/>
			<xsl:with-param name="zip" select="home.zip.b/home.zip"/>
			<xsl:with-param name="zipExt" select="home.zip.b/home.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!--End Consumer Phone template-->

	<!-- DMI Name(s) Field - Only display the first name given in the XML -->
	<xsl:template match="exec[position() = 1]">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Name(s) -->
	<xsl:template match="name.b | con.na.b | name | na.b | member">
		<xsl:if test="not(./optout.encrypted) and (na or ln or last.nmc or last or lna or fn or fna or first.nmc or first or mid or suf or first.nm or last.nm or name.b/lna or name.b/fna)">
			<xsl:choose>
				<xsl:when test ="$pcVal='DMI'">
					<xsl:call-template name="wrapPublicRecordsName">
						<xsl:with-param name="label" select="'&pr_names;'"/>
						<xsl:with-param name="firstName" select ="first"/>
						<xsl:with-param name="middleName" select ="mid"/>
						<xsl:with-param name="lastName" select ="last"/>
						<xsl:with-param name="lastNameFirst" select ="true()"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test ="($pVal='PROF. LICENSES' or $pVal='Prof. Licenses' or $pVal='PROF.LICENSES')">
					<xsl:if test ="not(na/following-sibling::full.na)">
						<xsl:call-template name="wrapPublicRecordsName">
							<xsl:with-param name="label" select="'&pr_names;'"/>
							<xsl:with-param name="lastName" select ="na"/>
						</xsl:call-template>
					</xsl:if>
					<xsl:call-template name="wrapPublicRecordsName">
						<xsl:with-param name="label" select="'&pr_names;'"/>
						<xsl:with-param name="firstName" select ="fna"/>
						<xsl:with-param name="middleName" select ="mna"/>
						<xsl:with-param name="lastName" select ="lna"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="wrapPublicRecordsName">
						<xsl:with-param name="label" select="'&pr_names;'"/>
						<xsl:with-param name="prefixName" select ="pref | na.t | na | name.b/na.t"/>
						<xsl:with-param name="firstName" select="fn | first.nmc | first | fna | first.nm | name.b/fna"/>
						<xsl:with-param name="middleName" select="mid | mna | name.b/mid"/>
						<xsl:with-param name="lastName" select="ln | last.nmc | last | lna | last.nm | name.b/lna"/>
						<xsl:with-param name="suffixName" select="suf | na.suf"/>
						<xsl:with-param name="professionalSuffixName" select ="pro.ttl.cd"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:apply-templates select ="aka.b"/>
	</xsl:template>

	<!-- Filing Acquired Date-->
	<xsl:template match="pub.d[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fileAcquiredDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- AKA(s) -->
	<xsl:template match="aka.b[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_akaNames;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Business Name -->
	<xsl:template match="co.nm[not(optout.encrypted)] | co.nmc[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_businessName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Address  -->
	<xsl:template match="addr.b[not(optout.encrypted)] | pri.addr.b[not(optout.encrypted)] | personal.addr.b[not(optout.encrypted)]">
		<xsl:variable name="label">
			<xsl:choose>
				<!-- Display label City/State for People-Canada if theres no street address -->
				<xsl:when test ="($pcVal='CWP' or $pcVal='DMI') and not(descendant::str)">
					<xsl:text>&pr_cityState;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_address;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="streetNum" select="hse.nbr | apt.nbr"/>
			<xsl:with-param name="streetDirection" select="str.dir"/>
			<xsl:with-param name="street" select="addr| str | addrc | pri.addr/str"/>
			<xsl:with-param name="streetSuffix" select="str.typ"/>
			<xsl:with-param name="streetLineTwo" select="str.2 | unit "/>
			<xsl:with-param name="city" select="cty | ctyc  | pri.addr/cty.st.b/cty "/>
			<xsl:with-param name="stateOrProvince" select="st | st.abbr | provc | prov | pri.addr/cty.st.b/st"/>
			<xsl:with-param name="zip" select="zip | canzip.cd | zip.5.cd | pri.addr/zip | zip.b/zip.5.cd"/>
			<xsl:with-param name="zipExt" select="zip.ext | own.zip.ext | zip.4.cd | zip.b/zip.4.cd"/>
		</xsl:call-template>
		<xsl:apply-templates select ="cnty | pri.addr/cnty.b/cnty"/>
	</xsl:template>

	<!--  Previous Address -->
	<xsl:template match="pvaddr.b[not(optout.encrypted)] | prev.addr.b[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_previousAddress;'"/>
			<xsl:with-param name="streetNum" select="pvaddr.b/hse.nbr"/>
			<xsl:with-param name="streetDirection" select="pvaddr.b/str.dir"/>
			<xsl:with-param name="street" select="pvaddr.b/str | prev.addr | addr | addrc "/>
			<xsl:with-param name="streetSuffix" select="pvaddr.b/str.typ"/>
			<xsl:with-param name="streetLineTwo" select="pvaddr.b/str.2 | unit "/>
			<xsl:with-param name="city" select="pvaddr.b/cty | prev.cty"/>
			<xsl:with-param name="stateOrProvince" select="pvaddr.b/st | st.abbr | prev.st| provc | prov"/>
			<xsl:with-param name="zip" select="pvaddr.b/zip | prev.zip| canzip.cd | zip.5.cd "/>
			<xsl:with-param name="zipExt" select="pvaddr.b/zip.ext | own.zip.ext | zip.4.cd"/>
		</xsl:call-template>
		<xsl:apply-templates select ="phn.b"/>
	</xsl:template>

	<!-- County -->
	<xsl:template match="cnty.b[not(optout.encrypted)] | cnty[not(optout.encrypted)] | cn.info.b/cnty.nm[not(optout.encrypted)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Telephone or $pVal='Canada Business Finder'-->
	<xsl:template match="phn.nbr[not(optout.encrypted)] | ph.nbrc[not(optout.encrypted)] | phone.b[not(optout.encrypted)] | ph.nbr[not(optout.encrypted)] | phn.b[not(optout.encrypted)] | hist.phn[not(optout.encrypted)]">
		<xsl:choose>
			<xsl:when test ="$pcVal='PFR' and not(name(.)='hist.phn')">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="label">
					<xsl:choose>
						<xsl:when test ="$pVal='People Household-Centric Document' or $pVal='People Household Document'">
							<xsl:choose>
								<xsl:when test ="position()=1">
									<xsl:text>&pr_telephone;</xsl:text>
								</xsl:when>
								<xsl:otherwise>&pr_additionalTelephone;</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test ="($pVal='People Phone-Centric Document') or ($pVal='People Phone Number Documents')">
							<xsl:text>&pr_telephone;</xsl:text>
							<xsl:value-of select ="parent::phn/@no"/>
							<xsl:text>:</xsl:text>
						</xsl:when>
						<xsl:when test ="name(.)='hist.phn'">
							<xsl:text>&pr_historicPhoneNumber;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&pr_telephone;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test ="$pcVal='DMI'">
						<xsl:call-template name="wrapPublicRecordsItem">
							<xsl:with-param name="defaultLabel" select="$label"/>
							<xsl:with-param name="nodeType" select="$PHONE"/>
							<xsl:with-param name="selectNodes" select="phone"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="wrapPublicRecordsItem">
							<xsl:with-param name="defaultLabel" select="$label"/>
							<xsl:with-param name="nodeType" select="$PHONE"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- PFR (People-Find) Telephone 
	<xsl:template match ="telco.phn.1 | telco.phn.2">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test ="name(.)='telco.phn.1'">
					<xsl:text>&pr_telephone1;</xsl:text>
				</xsl:when>
				<xsl:when test ="name(.)='telco.phn.2'">
					<xsl:text>&pr_telephone2;</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>
-->

	<!-- Phones -->
	<xsl:template match="telco.phn.1 | telco.phn.2">
		<xsl:variable name="phoneLabel">
			<xsl:text>&pr_phonePrefix;</xsl:text>
			<xsl:value-of select="position()" />:
			<xsl:text>:</xsl:text>
		</xsl:variable>

		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$phoneLabel"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>


	<!-- AKA(s) -->
	<xsl:template match ="aka1 | aka2 | aka3 | aka">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>