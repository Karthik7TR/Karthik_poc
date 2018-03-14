<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>

	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsDate.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Do not render these nodes -->
	<xsl:template match="map|p|pc|col.key|coverage.id|c|dbtr.typ|act.typ|filg.hid.nbr|label|docket.number.hidden|docket.number.dc|court|court.linking|court.citelist
											 |filing.county|court.norm|court.pretty|cs.hid.nbr|off.cnty|party.type.block|firm.status|debt.desc|ss.nbr|oth.atty.b|e.disp.stat.d|cs.desc
											 |cmnt.ttl.desc|pltf.zip.ext|pltf.ctry|def.ctry|pltf.dmd.prq|ShowSearchLink"/>

	<!-- Database Signon: LS-ALL (LS-XX) -->
	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsLawsuitClass;'" />
			<xsl:with-param name="dualColumn" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_lawsuitRecords;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsMainColumn">
		<xsl:call-template name="Content"/>
	</xsl:template>

	<!-- 
			This content is comprised of four major sections:
			(A) Docket Linking Information
			(B) Case Information
			(C) Party Information
			(D) Miscellaneous Information
			-->
	<xsl:template name="Content">
		<xsl:call-template name="docket_linking"/>
		<xsl:call-template name="case_information"/>
		<xsl:apply-templates select="arb.b" />
		<xsl:call-template name="party_information"/>
		<xsl:apply-templates select="calendar.b" />
		<xsl:call-template name="additional_information"/>
		<xsl:call-template name="court_express_message"/>
		<xsl:call-template name="disclaimer"/>
	</xsl:template>

	<!-- ********************************************************************** 
	****************  (A)"DOCKET LINKING INFORMATION" section  ****************
	************************************************************************-->

	<!-- Court Express Message -->
	<xsl:template name="docket_linking">
		<xsl:variable name="docketLabel">
			<xsl:choose>
				<xsl:when test="p = 'LAWSUITS - DOLAN'">
					<xsl:choose>
						<xsl:when test="p = 'STATE DOCKETS-CIVIL' and
										descendant::full.state = 'NEW YORK' and
										descendant::source = 'SUPREME COURT'">
							<xsl:text>&pr_supremeCourtWestDocket;<![CDATA[ ]]></xsl:text>
						</xsl:when>
						<xsl:when test="p = 'STATE DOCKETS-CIVIL' and
										descendant::source = 'COUNTY CLERK CIVIL INDEX'">
							<xsl:text>&pr_civilIndexWestDocket;<![CDATA[ ]]></xsl:text>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="descendant::source = 'COUNTY CLERK CIVIL INDEX'">
							<xsl:text>&pr_newYorkCountyClerk;<![CDATA[]]></xsl:text>
						</xsl:when>
						<xsl:when test="descendant::source = 'SUPREME COURT'">
							<xsl:text>&pr_supremeCourtWestDocket;<![CDATA[ ]]></xsl:text>
						</xsl:when>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length($docketLabel) &gt; 0 ">
			<div class="&pr_item;">
				<xsl:value-of select="$docketLabel"/>
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:variable name="documentName">
					<xsl:choose>
						<xsl:when test="p = 'LAWSUITS - DOLAN'">
							<xsl:text>&pr_dn;</xsl:text>
							<xsl:value-of select="descendant::docket.number"/>
							<xsl:text>&pr_andCnty;</xsl:text>
							<xsl:value-of select="descendant::filing.county"/>
							<xsl:text>&pr_rightParenthesis;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&pr_caseNumberUpper;</xsl:text>
							<xsl:value-of select="descendant::docket.number"/>
							<xsl:text>&pr_andCnty;</xsl:text>
							<xsl:value-of select="descendant::filing.county"/>
							<xsl:text>&pr_rightParenthesis;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="databaseName">
					<xsl:choose>
						<xsl:when test="p = 'LAWSUITS - DOLAN'">
							<xsl:value-of select="'155107'"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&pr_docket;</xsl:text>
							<xsl:value-of select="descendant::court.norm"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- ********************************************************************** 
	*********************  (B)"CASE INFORMATION" section  *********************
	************************************************************************-->

	<xsl:template name="case_information">
		<!-- Build up the case informaiton into a variable -->
		<xsl:variable name="caseInfo">
			<xsl:choose>
				<xsl:when test="p = 'LAWSUITS - DOLAN'">
					<xsl:apply-templates select="filg.info.b/fln.b"/>
					<xsl:apply-templates select="filg.info.b/filg.d"/>
					<xsl:apply-templates select="filg.info.b/flt.b"/>
					<xsl:apply-templates select="filg.info.b/filg.loc.b"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="primary.title" />
					<xsl:apply-templates select="cs.nbr.b"/>
					<xsl:apply-templates select="filg.d"/>
					<xsl:apply-templates select="cs.typ.b"/>
					<xsl:apply-templates select="trial.b"/>
					<xsl:apply-templates select="award.b" />
					<xsl:apply-templates select="cs.amt.b/cs.dmd.b/cs.dmd"/>
					<xsl:apply-templates select="cs.rem.b" />
					<xsl:apply-templates select="status.b"/>
					<xsl:apply-templates select="disp.b"/>
					<xsl:apply-templates select="disp.d"/>
					<xsl:apply-templates select="court.b"/>
					<xsl:apply-templates select="judge.nm.b"/>
					<xsl:apply-templates select="case.information.block"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length($caseInfo) &gt; 0 ">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_caseInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:copy-of select="$caseInfo"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Filing Number -->
	<xsl:template match="filg.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Type -->
	<xsl:template match="filg.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Docket Number -->
	<xsl:template match="docket.number">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Case Number -->
	<xsl:template match="cs.nbr">
		<tr>
			<th>
				<xsl:text>&pr_caseNumber;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
				<xsl:if test="../following-sibling::cs.typ.b/cs.desc">
					<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
					<xsl:value-of select="../following-sibling::cs.typ.b/cs.desc"/>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!-- Filing Date -->
	<xsl:template match="filg.d|filing.date">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing County -->
	<xsl:template match="filg.off.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Case Type -->
	<xsl:template match="case.type|cs.typ">
		<tr>
			<th>
				<xsl:text>&pr_caseType;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
				<xsl:apply-templates select="../following-sibling::filg.typ" mode="casetype"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="filg.typ" mode="casetype">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Case Category -->
	<xsl:template match="cs.cat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseCategory;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="trial.b">
		<xsl:apply-templates select="trial.typ" />
		<xsl:apply-templates select="trial.schd.d" />
	</xsl:template>

	<!-- Trial -->
	<xsl:template match="trial.typ">
		<tr>
			<th>
				<xsl:text>&pr_trial;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
				<xsl:apply-templates select="../trial.schd.length" mode="render"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="trial.schd.length" />
	<xsl:template match="trial.schd.length" mode="render">
		<xsl:text>&pr_scheduledLength;<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="award.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_award;'"/>
			<xsl:with-param name="selectNodes" select="cost.b"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cost.b">
		<xsl:apply-templates select="typ"/>
		<xsl:text>:<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates select="award.amt"/>
	</xsl:template>

	<!-- Scheduled For: -->
	<xsl:template match="trial.schd.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_scheduledFor;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Demand -->
	<!--  leave this bock alone!-->
	<xsl:template match="cs.dmd|dbtr.amt|amt">
		<tr>
			<th>
				<xsl:text>&pr_demand;</xsl:text>
			</th>
			<td>
				<xsl:if test="(../cs.dmd.qual and not(../cs.dmd.qual = 'Y'))">
					<xsl:apply-templates select="../cs.dmd.qual" />
					<xsl:text>:<![CDATA[ ]]></xsl:text>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="contains(self::node(), '$')">
						<xsl:apply-templates/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="self::amt or self::cs.dmd">
								<xsl:value-of select="format-number(self::node(),'$#,##0.00')"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="format-number(self::node(),'$#,##0')"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="cs.rem.b">
		<tr>
			<th>
			</th>
			<td>
				<xsl:apply-templates select="cs.rem.cd" />
				<xsl:apply-templates select="cs.rem.desc"/>
				<xsl:apply-templates select="cs.rem" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="cs.rem.desc">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Case Status -->
	<xsl:template match="stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_status;'"/>
			<xsl:with-param name="selectNodes" select="../following-sibling::stat.d"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="stat.d">
		<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
		<xsl:call-template name="FormatNonSensitiveDate"/>
	</xsl:template>

	<xsl:template match="estate.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_statusOfEstate;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Disposition -->
	<xsl:template match="c.disp.cd">
		<tr>
			<th>
				<xsl:choose>
					<xsl:when test="parent::node()/preceding-sibling::status.b/stat">
						<xsl:text></xsl:text>
					</xsl:when>
					<xsl:otherwise>
						&pr_disposition;
					</xsl:otherwise>
				</xsl:choose>
			</th>
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- Disposition Date -->
	<xsl:template match="disp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dispositionDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="court.b">
		<!-- XML data does not come in order as displayed -->
		<xsl:apply-templates select="court"/>
		<xsl:apply-templates select="filg.off.addr.b"/>
		<xsl:apply-templates select="crt.phn.nbr"/>
		<xsl:apply-templates select="venue.cd"/>
	</xsl:template>

	<!-- Filing Office -->
	<xsl:template match="court|filg.off.nme">
		<tr>
			<th>
				<xsl:text>&pr_filingOffice;</xsl:text>
			</th>
			<td>
				<xsl:choose>
					<xsl:when test="name(self::node())='court'">
						<xsl:choose>
							<xsl:when test="ancestor::r/source = 'COUNTY CLERK CIVIL INDEX'">
								<xsl:text>&pr_newYorkCountyCivilIndex;</xsl:text>
							</xsl:when>
							<xsl:when test="ancestor::r/source = 'NEW YORK SUPREME COURT' or 
									  ((ancestor::r/source='SUPREME COURT') and (ancestor::r/state.postal='NY'))">
								<xsl:text>&pr_newYorkSupremeCourt;</xsl:text>
								<xsl:if test="following-sibling::filing.county">
									<xsl:apply-templates select="following-sibling::filing.county" mode="FilingOffice" />
								</xsl:if>
							</xsl:when>
							<xsl:otherwise>
								<xsl:apply-templates/>
								<xsl:if test="following-sibling::court2">
									<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
									<xsl:apply-templates select="following-sibling::court2"/>
								</xsl:if>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<!-- filg.off.nme -->
						<xsl:apply-templates/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="filing.county" mode="FilingOffice">
		<xsl:text>,<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Filing office Address Block -->
	<xsl:template match="filg.off.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="off.str"/>
			<xsl:with-param name="city" select="off.cty"/>
			<xsl:with-param name="stateOrProvince" select="off.st"/>
			<xsl:with-param name="zip" select="off.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Court Phone Number -->
	<xsl:template match="crt.phn.nbr">
		<tr>
			<th/>
			<td>
				<xsl:call-template name="FormatPhone"/>
			</td>
		</tr>
	</xsl:template>

	<!-- Venue Filing Address -->
	<xsl:template match="filg.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_venue;'"/>
			<xsl:with-param name="street" select="filg.off.str"/>
			<xsl:with-param name="city" select="filg.off.cty"/>
			<xsl:with-param name="stateOrProvince" select="filg.off.st"/>
			<xsl:with-param name="zip" select="filg.off.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Venue -->
	<xsl:template match="venue.cd">
		<tr>
			<th>
				<xsl:text>&pr_venue;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
				<xsl:if test="following-sibling::filg.st">
					<xsl:choose>
						<xsl:when test="parent::court.b">
							<xsl:text>,<![CDATA[ ]]></xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:apply-templates select="following-sibling::filg.st"/>
				</xsl:if>
				<xsl:if test="not(following-sibling::filg.st)">
					<xsl:choose>
						<xsl:when test="name(self::node())='filing.county'">
							<xsl:apply-templates/>
							<xsl:if test="ancestor::r/state.postal">
								<xsl:text>,<![CDATA[ ]]></xsl:text>
								<xsl:apply-templates select="ancestor::r/state.postal" />
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="filg.st">
		<xsl:choose>
			<xsl:when test="not(preceding-sibling::venue.cd)">
				<xsl:call-template name="wrapPublicRecordsItem">
					<xsl:with-param name="defaultLabel" select="'&pr_venue;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Judge -->
	<xsl:template match="judge.nm.b|panel.judge">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_judge;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.information.block">
		<xsl:apply-templates select="docket.block" />
		<xsl:apply-templates select="filing.date.block" />
		<xsl:apply-templates select="case.type.block" />
		<xsl:apply-templates select="case.status.block" />
		<xsl:apply-templates select="court.block" />
		<xsl:apply-templates select="panel.block" />
	</xsl:template>

	<!-- Title -->
	<xsl:template match="primary.title">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_title;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Case Status -->
	<xsl:template match="case.status">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_status;'"/>
		</xsl:call-template>
	</xsl:template>

<xsl:template match="arb.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_arbitration;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="arb.desig.d"/>
			<xsl:apply-templates select="arb.cert.d"/>
			<xsl:apply-templates select="arb.apnt.d"/>
			<xsl:apply-templates select="arb.awd.filed.d"/>
		</table>
	</xsl:template>

	<xsl:template match="arb.desig.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_designated;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arb.cert.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_certified;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arb.apnt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_appointed;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arb.awd.filed.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_award;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*********************  (C)"PARTY INFORMATION" section  *********************
	************************************************************************-->

	<xsl:template name="party_information">
		<!-- XML data does not come in order as displayed -->
		<xsl:choose>
			<xsl:when test="p = 'LAWSUITS - DOLAN' and (dbtr.b/dbtr.amt or cred.info.b or dbtr.b)">
				<!-- When there are multiple dbtr.b, we will have repeated same dbtr.amt info 
				Therefore, we will only retrieve the 1st dbtr.amt.
				Note: dbtr.amt is a child of dbtr.b 
				-->
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_partyInformation;'"/>
				</xsl:call-template>
				<table class="&pr_table;">
					<xsl:apply-templates select="dbtr.b/dbtr.amt[1]"/>
					<xsl:apply-templates select="cred.info.b"/>
					<xsl:apply-templates select="dbtr.b"/>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="descendant::nm.info.b">
						<xsl:apply-templates select="descendant::nm.info.b"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="descendant::party.block or descendant::attorney.block">
							<xsl:call-template name="wrapPublicRecordsSection">
								<xsl:with-param name="class" select="'&pr_subheader;'"/>
								<xsl:with-param name="contents" select="'&pr_partyInformation;'"/>
							</xsl:call-template>
							<table class="&pr_table;">
								<xsl:apply-templates select="descendant::party.block"/>
								<xsl:apply-templates select="descendant::attorney.block"/>
							</table>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="nm.info.b">
		<xsl:if test="sup.einfo.b or pltf.info.b or def.info.b or oth.nm.prty.b">
			<xsl:choose>
				<xsl:when test="preceding-sibling::nm.info.b">
					<xsl:call-template name="wrapPublicRecordsSection">
						<xsl:with-param name="class" select="'&pr_subheader;'"/>
						<xsl:with-param name="contents" select="'&pr_additionalComplaint;'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="wrapPublicRecordsSection">
						<xsl:with-param name="class" select="'&pr_subheader;'"/>
						<xsl:with-param name="contents" select="'&pr_partyInformation;'"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
			<table class="&pr_table;">
				<xsl:apply-templates select="sup.einfo.b[1]"/>
				<xsl:apply-templates select="pltf.info.b"/>
				<xsl:apply-templates select="def.info.b"/>
				<xsl:apply-templates select="oth.nm.prty.b"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Party Status -->
	<xsl:template match="e.stat">
		<tr>
			<th>
				<xsl:text>&pr_status;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
				<xsl:if test="following-sibling::e.disp.stat.d and not(following-sibling::e.disp.stat)">
					<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="following-sibling::e.disp.stat.d" mode="render"/>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="e.disp.stat">
		<xsl:if test="preceding-sibling::e.stat">
			<tr>
				<th>
				</th>
				<td>
					<xsl:apply-templates />
					<xsl:if test="following-sibling::e.disp.stat.d ">
						<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
						<xsl:apply-templates select="following-sibling::e.disp.stat.d" mode="render"/>
					</xsl:if>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="e.disp.stat.d" mode="render">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Plaintiff/Petitioner -->
	<xsl:template match="pltf.info.b|cred.info.b">
		<tr>
			<th>
				<xsl:choose>
					<xsl:when test="(not(pltf.nm.b/pltf.nm)	and not(/descendant::p = 'LAWSUITS - DOLAN')) or (not(pltf.nm.b) and not(cred.b))">
						<xsl:text><![CDATA[ ]]></xsl:text>
						<!-- Don't render a label-->
					</xsl:when>
					<xsl:when test="ancestor::r/p = 'HARPR'">
						&pr_petitioner;
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="pltf.prty.typ">
								<xsl:choose>
									<xsl:when test="pltf.prty.typ = 'PETITIONER'">
										&pr_petitioner;
									</xsl:when>
									<xsl:otherwise>
										&pr_plaintiff;
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								&pr_plaintiff;
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</th>
			<td>
				<!-- XML data does not come in order as displayed -->
				<xsl:if test="pltf.nm.b/pltf.nm">
					<xsl:call-template name="FormatName">
						<xsl:with-param name="firstName" select="pltf.nm.b/pltf.nm"/>
					</xsl:call-template>
					<xsl:apply-templates select="pltf.prty.typ"/>
					<xsl:apply-templates select="pltf.nm.cmnt" />
					<xsl:apply-templates select="pltf.addr.b"/>
					<xsl:apply-templates select="pltf.ph.nbr"/>
				</xsl:if>
				<xsl:apply-templates select="cred.b/cred.nm"/>
			</td>
		</tr>
		<xsl:apply-templates select="pltf.sex" />
		<xsl:apply-templates select="pltf.dob|dob"/>
		<xsl:if test="name(self::node()) = 'pltf.info.b'">
			<xsl:apply-templates select="pltf.bus.dun.b/pltf.bus.dun"/>
			<xsl:apply-templates select="pltf.hq.bus.dun.b/pltf.hq.bus.dun" />
		</xsl:if>
		<xsl:apply-templates select="pltf.stat"/>
		<xsl:apply-templates select="pltf.atty.b"/>
		<xsl:apply-templates select="pltf.amt.b"/>
	</xsl:template>

	<xsl:template match="pltf.prty.typ|def.prty.typ">
		<xsl:choose>
			<xsl:when test="text() = 'PETITIONER'">
				<!-- don't do anything -->
			</xsl:when>
			<xsl:otherwise>
				<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Plaintiff Address Block: -->
	<xsl:template match="pltf.addr.b">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="street" select="pltf.adr"/>
				<xsl:with-param name="city" select="pltf.cty"/>
				<xsl:with-param name="stateOrProvince" select="pltf.st"/>
				<xsl:with-param name="zip" select="pltf.zip.b/pltf.zip"/>
				<xsl:with-param name="country" select="pltf.for.reg"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Plaintiff Care Of -->
	<xsl:template match="pltf.care">
		<xsl:variable name="prefix">
			<xsl:value-of select="substring(text(),1,4)"/>
		</xsl:variable>

		<xsl:if test="$prefix != 'C/O '">
			<xsl:text>&pr_co;<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Plaintiff Name -->
	<xsl:template match="pltf.nm">
		<xsl:if test="preceding-sibling::pltf.nm.pre">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
		<xsl:if test="following-sibling::pltf.nm.suf">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="pltf.rem.dmd.b">
		<tr>
			<th>
				<xsl:text>&pr_demandByParty;</xsl:text>
			</th>
			<td>
				<xsl:if test="pltf.rem.desc">
					<xsl:apply-templates select="pltf.rem.desc" />
					<xsl:if test="pltf.rem.dmd">
						<xsl:text>:<![CDATA[ ]]></xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="pltf.rem.dmd">
					<xsl:choose>
						<xsl:when test="contains(pltf.rem.dmd, '$')">
							<xsl:apply-templates/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="format-number(pltf.rem.dmd,'$#,##0.00')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="pltf.dmd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_demandByParty;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Plaintiff's Status -->
	<xsl:template match="pltf.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_partyStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pltf.atty.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="pltf.atty.adr"/>
			<xsl:with-param name="city" select="pltf.atty.cty"/>
			<xsl:with-param name="stateOrProvince" select="pltf.atty.st"/>
			<xsl:with-param name="zip" select="pltf.atty.zip.b"/>
			<xsl:with-param name="zipExt" select="pltf.atty.zip.ext"/>
			<xsl:with-param name="country" select="pltf.atty.ctry"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Plaintiff's Attorney  -->
	<xsl:template match="pltf.atty.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_attorney;'"/>
			<xsl:with-param name="firstName" select="*[not(self::pltf.atty.bar.nbr)]"/>
		</xsl:call-template>
		<xsl:apply-templates select="pltf.firm.nm"/>
		<xsl:apply-templates select="pltf.atty.addr.b"/>
		<xsl:apply-templates select="pltf.atty.ph.nbr"/>
		<xsl:apply-templates select="pltf.atty.bar.nbr"/>
		<xsl:apply-templates select="pltf.atty.typ"/>
	</xsl:template>

	<xsl:template match="pltf.firm.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="' '"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Plaintiff's Attorney Phone Number -->
	<xsl:template match="pltf.atty.ph.nbr">
		<tr>
			<th/>
			<td>
				<xsl:call-template name="FormatPhone"/>
			</td>
		</tr>
	</xsl:template>

	<!-- Plaintiff's Attorney Bar Number -->
	<xsl:template match="pltf.atty.bar.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseNo;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Defendant -->
	<xsl:template match="def.info.b|dbtr.b">
		<tr>
			<th>
				<xsl:choose>
					<xsl:when test="ancestor::r/p = 'HARPR'">
						<xsl:choose>
							<xsl:when test="def.prty.typ = 'DECEDENT'">
								&pr_decedent;
							</xsl:when>
							<xsl:when test="def.prty.typ = 'MINOR'">
								&pr_minor;
							</xsl:when>
							<xsl:when test="def.prty.typ = 'INCAPACITATED'">
								&pr_incapacitated;
							</xsl:when>
							<xsl:when test="def.prty.typ = ''">
								&pr_other;
							</xsl:when>
							<xsl:otherwise>
								&pr_respondent;
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="(not(def.nm.b/def.nm) and not(/descendant::p = 'LAWSUITS - DOLAN')) or (not(def.nm.b/def.nm) and not(def.nm.b/def.aka.nm) and not(dbtr.nme.b) and not(dbtr.addr.b)
												 and not(def.addr.b))">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:when>
							<xsl:when test="def.prty.typ = 'RESPONDENT'">
								&pr_respondent;
							</xsl:when>
							<xsl:otherwise>
								&pr_defendant;
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</th>
			<td>
				<!-- XML data does not come in order as displayed -->
				<xsl:if test="normalize-space(def.nm.b/def.nm)">
					<xsl:call-template name="FormatName">
						<xsl:with-param name="firstName" select="def.nm.b/def.nm"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:apply-templates select="def.prty.typ"/>
				<xsl:apply-templates select="def.nm.cmnt"/>
				<xsl:apply-templates select="def.addr.b"/>
				<xsl:apply-templates select="def.for.reg"/>
				<xsl:apply-templates select="def.ph.nbr"/>
				<xsl:if test="normalize-space(def.nm.b/def.aka.nm)">
					<xsl:call-template name="FormatName">
						<xsl:with-param name="firstName" select="def.nm.b/def.aka.nm"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:apply-templates select="dbtr.nme.b"/>
				<xsl:apply-templates select="dbtr.addr.b"/>
			</td>
		</tr>
		<xsl:apply-templates select="def.sex" />
		<xsl:apply-templates select="def.stat"/>
		<xsl:apply-templates select="def.disp.cd"/>
		<xsl:apply-templates select="def.atty.b"/>
		<xsl:apply-templates select="def.bus.dun.b/def.bus.dun"/>
		<xsl:apply-templates select="def.hq.bus.dun.b/def.hq.bus.dun"/>
	</xsl:template>

	<xsl:template match ="gen.cd">
		<xsl:if test="preceding-sibling::dbtr.nme">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="dob|pltf.dob">
		<!-- Do not display DOB (changes for no PRACCESS) -->
	</xsl:template>

	<xsl:template match="def.sex|pltf.sex|oth.sex">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Defendant Name -->
	<xsl:template match="def.nm">
		<xsl:if test="preceding-sibling::def.nm.pre">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="def.nm.suf">
		<xsl:if test="preceding-sibling::def.nm or preceding-sibling::def.nm.pre">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Defendant C/O -->
	<xsl:template match="def.care">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="' '"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="def.addr.b">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="street" select="def.adr"/>
				<xsl:with-param name="city" select="def.cty"/>
				<xsl:with-param name="stateOrProvince" select="def.st"/>
				<xsl:with-param name="zip" select="def.zip.b/def.zip"/>
				<xsl:with-param name="zipExt" select="def.zip.b/def.zip.ext"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Defendant Phone Number -->
	<xsl:template match="def.ph.nbr">
		<xsl:call-template name="FormatPhone"/>
	</xsl:template>

	<!-- Defendant Status -->
	<xsl:template match="def.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_partyStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Defendant Status -->
	<xsl:template match="def.disp.cd">
		<tr>
			<th>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
				<xsl:if test="following-sibling::def.disp.d">
					<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="following-sibling::def.disp.d" />
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="def.atty.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_attorney;'"/>
			<xsl:with-param name="firstName" select="*[not(self::def.atty.bar.nbr)]"/>
		</xsl:call-template>
		<xsl:apply-templates select="def.firm.nm"/>
		<xsl:apply-templates select="def.atty.addr.b"/>
		<xsl:apply-templates select="def.atty.ph.nbr"/>
		<xsl:apply-templates select="def.atty.bar.nbr"/>
		<xsl:apply-templates select="def.atty.typ"/>
	</xsl:template>

	<xsl:template match="def.firm.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="' '"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Defendant's Attorney Addr -->
	<xsl:template match="def.atty.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="def.atty.adr"/>
			<xsl:with-param name="city" select="def.atty.cty"/>
			<xsl:with-param name="stateOrProvince" select="def.atty.st"/>
			<xsl:with-param name="zip" select="def.atty.zip.b/def.atty.zip"/>
			<xsl:with-param name="zipExt" select="def.atty.zip.b/def.atty.zip.ext"/>
			<xsl:with-param name="country" select="def.atty.ctry"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Defendant's Attorney Phone Number -->
	<xsl:template match="def.atty.ph.nbr">
		<tr>
			<th/>
			<td>
				<xsl:call-template name="FormatPhone"/>
			</td>
		</tr>
	</xsl:template>

	<!-- Defendant's Attorney Bar Number -->
	<xsl:template match="def.atty.bar.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseNo;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="def.atty.typ">
		<xsl:if test="preceding-sibling::def.atty.addr.b">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<!-- DUNS -->
	<xsl:template match="def.bus.dun.b/def.bus.dun|pltf.bus.dun.b/pltf.bus.dun">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_duns;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="def.hq.bus.dun.b/def.hq.bus.dun|pltf.hq.bus.dun.b/pltf.hq.bus.dun">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hqDuns;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Other -->
	<xsl:template match="oth.nm.prty.b">
		<xsl:apply-templates select="oth.nm.b"/>
		<xsl:apply-templates select="oth.sex"/>
	</xsl:template>

	<xsl:template match="oth.nm.b[normalize-space(oth.nm)]">
		<tr class="&pr_item;">
			<th>&pr_other;</th>
			<td>
				<xsl:apply-templates select="oth.nm"/>
				<xsl:apply-templates select="following-sibling::oth.prty.typ"/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="oth.prty.typ[normalize-space(.)]">
		<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="party.name.block">
		<xsl:apply-templates select="party.name" />
	</xsl:template>

	<xsl:template match="party.name">
		<tr>
			<th>
				<xsl:variable name="label" select="parent::party.name.block/following-sibling::party.type.block/party.type" />
				<xsl:if test="substring($label, 1, 1)!='00'">
					<xsl:value-of select="substring($label, 1, 1)"/>
				</xsl:if>
				<xsl:value-of select="substring(translate($label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 2, 20)"/>
				<xsl:text>:<![CDATA[ ]]></xsl:text>
			</th>
			<td>
				<xsl:apply-templates />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="attorney.block">
		<!-- XML data does not come in order as displayed -->

		<!-- Attorney Name -->
		<xsl:apply-templates select="attorney.name.block" />

		<!-- Firm Name -->
		<xsl:apply-templates select="firm.block" />
	</xsl:template>

	<!-- Attorney Name -->
	<xsl:template match="attorney.name">
		<tr>
			<th>
				<xsl:text>&pr_attorneyName;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates />
				<xsl:if test="not(ancestor::attorney.block/firm.block)">
					<xsl:apply-templates select="ancestor::attorney.block/attorney.status.block" />
				</xsl:if>
				<!--<xsl:apply-templates select="ancestor::attorney.block/attorney.status.block" />-->
			</td>
		</tr>
	</xsl:template>

	<!-- Firm Name -->
	<xsl:template match="firm.block">
		<tr>
			<th>
				<xsl:text>&pr_firmName;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="firm.name.block" />
				<xsl:apply-templates select="ancestor::attorney.block/attorney.status.block" />
				<xsl:apply-templates select="firm.address.block" />
				<xsl:apply-templates select="firm.phone.block" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="firm.address.block">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="street" select="firm.street"/>
			<xsl:with-param name="streetLineTwo" select="firm.city.state"/>
			<xsl:with-param name="city" select="firm.city"/>
			<xsl:with-param name="stateOrProvince" select="firm.state"/>
			<xsl:with-param name="zip" select="firm.zip"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="firm.phone">
		<xsl:call-template name="FormatPhone"/>
	</xsl:template>

	<xsl:template match="dbtr.addr.b">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="street" select="dbtr.str"/>
				<xsl:with-param name="city" select="dbtr.cty"/>
				<xsl:with-param name="stateOrProvince" select="dbtr.st"/>
				<xsl:with-param name="zip" select="dbtr.zip"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- **********************************************************************
	*******************  (D)"Calendar" section                *****************
	************************************************************************-->
	<xsl:template match="calendar.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_calendar;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="calendar.entry">
		<tr>
			<th>
				<xsl:if test="event.b/sch.d">
					<xsl:apply-templates select="event.b/sch.d" />
				</xsl:if>
			</th>
			<td>
				<xsl:apply-templates />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="event.b/sch.d">
		<xsl:call-template name="FormatNonSensitiveDate">
			<xsl:with-param name="dateNode" select="event.b/sch.d"/>
		</xsl:call-template>
	</xsl:template>

	<!-- **********************************************************************
	*******************  (E)"ADDITIONAL INFORMATION" section  *****************
	************************************************************************-->

	<xsl:template name="additional_information">
		<!-- Build up the additional informaiton into a variable -->
		<xsl:variable name="additionalInfo">
			<xsl:apply-templates select="addl.info.b/notes.b/film.cd"/>
			<xsl:apply-templates select="addl.info.b/notes.b/prop.info"/>
			<xsl:apply-templates select="addl.info.b/notes.b/cmnts"/>
			<xsl:apply-templates select="addl.info.b/notes.b/db.cmnt.b"/>
			<xsl:apply-templates select="addl.info.b/notes.b/cmnt.txt"/>
		</xsl:variable>

		<xsl:if test="string-length($additionalInfo) &gt; 0 ">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_additionalInformation;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:copy-of select="$additionalInfo"/>
			</table>
		</xsl:if>
	</xsl:template>

	<!-- Film Code -->
	<xsl:template match="notes.b/film.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filmCode;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Property Description -->
	<xsl:template match="prop.info">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_propertyDescription;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Comments -->
	<xsl:template match="cmnts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_comments;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- D&B Comments -->
	<xsl:template match="db.cmnt.b">
		<tr>
			<th>
				<xsl:if test="not(preceding-sibling::db.cmnt.b)">
					<xsl:text>&pr_dbComments;</xsl:text>
				</xsl:if>
			</th>
			<td>
				<xsl:apply-templates />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="cmnt.ttl.b">
		<xsl:if test="preceding-sibling::src.indv.nm">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="src.bus.nm">
		<xsl:if test="preceding-sibling::*">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="cmnt.txt">
		<xsl:if test="preceding-sibling::*">
			<xsl:text>:<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="cmnt.cd">
		<xsl:choose>
			<xsl:when test="preceding-sibling::cmnt.txt">
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:when>
			<xsl:when test="preceding-sibling::src.bus.nm">
				<xsl:text>:<![CDATA[ ]]></xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:apply-templates />
	</xsl:template>

	<!-- ********************************************************************** 
	*****************  (F)"MISCELLANEOUS INFORMATION" section  ****************
	************************************************************************-->

	<!-- Court Express Message -->
	<xsl:template name="court_express_message">
		<xsl:if test="p != 'LAWSUITS - DOLAN'">
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

	<!-- Disclaimer -->
	<xsl:template name="disclaimer">
		<xsl:if test="p != 'LAWSUITS - DOLAN'">
			<xsl:variable name="disclaimer1">
				<xsl:choose>
					<xsl:when test="p = 'DNBCV'">
						<xsl:text>&pr_disclaimer1;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>&pr_disclaimer3;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="disclaimer2">
				<xsl:if test="p = 'DNBCV'">
					<xsl:text>&pr_disclaimer2;</xsl:text>
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="wrapPublicRecordsDisclaimers">
				<xsl:with-param name="disclaimer1" select="$disclaimer1"/>
				<xsl:with-param name="disclaimer2" select="$disclaimer2"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>