<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<!--Liens-judgements has the following products - CIVIL JUDGEMENT, LIEN, JUDGEMENT-->
	<!-- ************************************************
			This content is comprised of Three major sections:
			(1) Document Header
			(2) LeftMainColumn
				(A) FILING INFORMATION
				(B) DEBTOR INFORMATION
			(3) RightMainColumn
				(C) CREDITOR INFORMATION
				(D) THIRD PARTY INFORMATION
				(E) DOCUMENT DETAILS
				(F) JUDGMENT INFORMATION
				(G) COMMENT INFORMATION
				(H) REMARKS INFORMATION
				(I) ORDER DOCUMENT
				(J) ADDNOTES
			(4) Document Footer
			(5) Position the Copyright
			************************************************* -->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Variable(s) -->
	<xsl:variable name="colkeyvalue" select="/Document/n-docbody/r/col.key" />
	<xsl:variable name="pcvalue" select="/Document/n-docbody/r/pc" />
	<xsl:variable name="pvalue" select="/Document/n-docbody/r/p" />
	<xsl:variable name="restrictvalue" select="/Document/n-docbody/r/restrict" />
	<xsl:variable name="vendorkeyvalue" select="/Document/n-docbody/r/vendor" />
	<xsl:variable name="vvalue" select="/Document/n-docbody/r/v" />

	<!-- Parameter(s) -->

	<!-- Do not render these nodes -->
	<xsl:template match="legacy.id|col.key|p|pc|c|v|pre|prism-clipdate|restrict|cmnt.src.b|owd.amt.tot|tot.oblgn|tot.oblgn.desc|hldr.ser.nbr|nbr.of.subjudge" />

	<xsl:template match="ssn.b|ssn">
		<!-- Do not display SSN (changes for no PRACCESS). This was not displayed before either. -->
	</xsl:template>
	
	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<!-- Render the XML based on the desired VIEW. -->
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsLiensAndJudgementsClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="(p='JUDGMENT') or (p='CIVIL JUDGMENT')">
					<xsl:text>&pr_civilJudgmentFilingRecord;</xsl:text>
				</xsl:when>
				<xsl:when test="(p='LIEN')">
					<xsl:text>&pr_lienFilingRecord;</xsl:text>
				</xsl:when>
				<xsl:when test="(p='NYDocket Judgment')">
					<xsl:text>&pr_newYorkJudgmentDocketAndLienRecords;</xsl:text>
				</xsl:when>
				<xsl:when test="(col.key='EvictionNDR') or (p='EVICTION')">
					<xsl:text>&pr_evictionFilingRecord;</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="$label"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!-- Display coverage information -->
		<xsl:if test="$coverage-block">
			<!--skip coverage information -->
		</xsl:if>
		
		<!-- START: Filing info subheader -->
		<xsl:apply-templates select="filg.info.b|ctrl.nbr.b"/>
		<xsl:apply-templates select="filg.typ"/>
		<xsl:apply-templates select="filg.cnty"/>
		<xsl:apply-templates select="entry.d"/>
		<xsl:apply-templates select="cs.nbr"/>
		<!-- END: Filing info subheader -->

		<xsl:choose>
			<xsl:when test="count(subjudge.b) > 1">
				<xsl:apply-templates select="subjudge.b[not(preceding-sibling::subjudge.b)]"/>
			</xsl:when>
			<xsl:otherwise>
				<!-- DEBTOR INFORMATION-->
				<xsl:apply-templates select="dbtr.b | subjudge.b/dbtr.b" />
				<xsl:apply-templates select="debt.b" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:choose>
			<xsl:when test="count(subjudge.b) > 1">
				<xsl:apply-templates select="subjudge.b[preceding-sibling::subjudge.b]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test='(col.key="judgeSA") or (col.key="liensSA") or (col.key="judgeSUPER") or (col.key="liensSUPER") or (col.key="LiensNDR") or (col.key="JudgeNDR") or (col.key="EvictionNDR")'>
						<xsl:apply-templates select="subjudge.b/cred.info.b" />
						<xsl:apply-templates select="subjudge.b/lien.info.b"/>
						<xsl:apply-templates select="subjudge.b/jdg.info.b">
							<xsl:with-param name="mode" select="'subjudge'"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="cred.info.b"/>
						<xsl:apply-templates select="cred.b"/>
						<xsl:apply-templates select="lien.info.b"/>
						<xsl:apply-templates select="jdg.info.b"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
		<!--THIRD PARTY INFORMATION-->
		<xsl:apply-templates select="thd.prty.b"/>
		<!-- DOCUMENT DETAILS-->
		<xsl:apply-templates select="lien.info.b" mode="DocumentDetails"/>
		<!-- COMMENT INFORMATION-->
		<xsl:apply-templates select="cmnts.b"/>
		<!-- REMARKS INFORMATION-->
		<xsl:apply-templates select="remark.b"/>
		<xsl:call-template name="outputOrderDocumentsSection"/>
		<xsl:call-template name="outputDisclaimers"/>
	</xsl:template>

	<!-- ********************************************************************** 
	*********************  "FILING INFORMATION" section  *******************
	************************************************************************-->
	<xsl:template match="filg.info.b">
		<xsl:call-template name="FilingInfoSubheader"/>
		<table class="&pr_table;">
			<xsl:apply-templates select="fln.b/filg.nbr|filg.nbr"/>
			<xsl:apply-templates select="fln.b/cert.nbr"/>
			<xsl:apply-templates select="fln.b/irs.ser.nbr"/>
			<xsl:apply-templates select="flt.b"/>
			<xsl:apply-templates select="fln.b"/>
			<xsl:apply-templates select="cmplt.d"/>
			<xsl:apply-templates select="dispo.cd"/>
			<xsl:apply-templates select="dispo.d"/>
			<xsl:apply-templates select="filg.loc.b"/>
			<xsl:apply-templates select="filg.d"/>
			<xsl:apply-templates select="../bk.nm"/>
			<xsl:apply-templates select="rels.d"/>
			<xsl:apply-templates select="prfct.d"/>
			<xsl:apply-templates select="exp.d"/>
			<xsl:apply-templates select="../nbr.of.subjudge"/>
		</table>
	</xsl:template>

	<xsl:template name="FilingInfoSubheader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_filingInfo;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Number -->
	<xsl:template match="filg.nbr|ctrl.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Certificate Number -->
	<xsl:template match="cert.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_certificateNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--IRS Serial Number-->
	<xsl:template match="irs.ser.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_irsSerialNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Type block-->
	<xsl:template match="flt.b">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_filingType;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="filg.typ"/>
				<xsl:if test="../../cred.info.b/hldr.lvl.cd">
					<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="../../cred.info.b/hldr.lvl.cd"/>
				</xsl:if>
				<xsl:if test="normalize-space(../../cred.info.b/hldr.lvl.desc) and string(number(../../cred.info.b/hldr.lvl.desc))='NaN'">
					<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="../../cred.info.b/hldr.lvl.desc"/>
				</xsl:if>
				<xsl:if test="normalize-space(../unlw.dtnr)='YES'">
					<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
					<xsl:text>&pr_unlawfulDetainer;</xsl:text>
				</xsl:if>
				<xsl:if test="action//text() != ''">
					<div>
						<xsl:apply-templates select="action"/>
					</div>
				</xsl:if>
			</td>
		</tr>
		<xsl:apply-templates select="act.typ"/>
	</xsl:template>

	<xsl:template match="fln.b">
		<xsl:apply-templates select="vol.nbr"/>
		<xsl:apply-templates select="pg.nbr"/>
		<xsl:apply-templates select="orig.case"/>
		<xsl:apply-templates select="orig.bk"/>
		<xsl:apply-templates select="orig.pg"/>
	</xsl:template>

	<!-- Volume or Book -->
	<xsl:template match="vol.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_volumeOrBook;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Page -->
	<xsl:template match="pg.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_pageLabel;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Original Case Number -->
	<xsl:template match="orig.case">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalCaseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Original Volume or Book-->
	<xsl:template match="orig.bk">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalVolumeOrBook;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Original Page -->
	<xsl:template match="orig.pg">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalPage;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Date-->
	<xsl:template match="cmplt.d">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="col.key = 'EvictionNDR'">
					<xsl:text>&pr_filingDate;</xsl:text>
				</xsl:when>
				<xsl:when test="(//r/filg.info.b/cmplt.d) and ($colkeyvalue='JudgeNDR')">
					<xsl:text>&pr_complaintDate;</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Disposition-->
	<xsl:template match="dispo.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_disposition;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Disposition Date-->
	<xsl:template match="dispo.d">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="col.key='EvictionNDR'">
					<xsl:text>&pr_satisfiedDate;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_dispositionDate;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Debtor Disposition Date -->
	<xsl:template match="dbtr.dispo.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorDispositionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Office-->
	<xsl:template match="filg.loc.b">
		<xsl:if test ="filg.off.nme | filg.addr.b">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_filingOffice;'"/>
				<xsl:with-param name="selectNodes" select="filg.off.nme | filg.addr.b"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates select="filg.off.cnty"/>
	</xsl:template>

	<xsl:template match="filg.addr.b">
		<xsl:choose>
			<xsl:when test="preceding-sibling::filg.off.nme or following-sibling::filg.off.nme">
				<div>
					<xsl:call-template name="FormatAddress">
						<xsl:with-param name="street" select="filg.off.str"/>
						<xsl:with-param name="city" select="filg.off.cty"/>
						<xsl:with-param name="stateOrProvince" select="filg.off.st"/>
						<xsl:with-param name="zip" select="filg.off.zip"/>
					</xsl:call-template>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatAddress">
					<xsl:with-param name="street" select="filg.off.str"/>
					<xsl:with-param name="city" select="filg.off.cty"/>
					<xsl:with-param name="stateOrProvince" select="filg.off.st"/>
					<xsl:with-param name="zip" select="filg.off.zip"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- Filing Date -->
	<xsl:template match="filg.d | entry.d">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="(name()='filg.d') and ($colkeyvalue='EvictionNDR')">
					<xsl:text>&pr_judgmentDate;</xsl:text>
				</xsl:when>
				<xsl:when test="(name()='filg.d') and (//filg.info.b/rels.d) and (($colkeyvalue='DOLAN') or ($vendorkeyvalue='DOLAN'))">
					<xsl:text>&pr_originalFilingDate;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_filingDate;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Book Name for NY Docket-->
	<xsl:template match="bk.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bookName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Release Date-->
	<xsl:template match="rels.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_releaseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Perfected Date -->
	<xsl:template match="prfct.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_perfectedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Render ctrl.nbr.b -->
	<xsl:template match="ctrl.nbr.b">
		<xsl:call-template name="FilingInfoSubheader"/>
		<table class="&pr_table;">
			<xsl:apply-templates select="ctrl.nbr"/>
		</table>
	</xsl:template>

	<!--Filing Type-->
	<xsl:template match="filg.typ[not(ancestor::filg.info.b)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Court Index Number-->
	<xsl:template match="cs.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_courtIndexNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Lien Number-->
	<xsl:template match="fed.lien.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lienNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing County-->
	<xsl:template match="filg.off.cnty|filg.cnty">
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test='(col.key="liensSUPER") and (//filg.info.b/filg.loc.b/court.id="NJ000DJ")'>
					<xsl:text>&pr_countyOfOriginalFiling;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&pr_filingCounty;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$label"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Office for NY Docket-->
	<xsl:template match="filg.cnty">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_filingOffice;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
				<xsl:text><![CDATA[ ]]>&pr_countyClerk;</xsl:text>
			</td>
		</tr>
	</xsl:template>

	<!--Action Type-->
	<xsl:template match="act.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actionType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	***********************  "DEBTOR INFORMATION" section  *********************
	************************************************************************-->
	<!--DEBTOR INFORMATION-->
	<xsl:template match="dbtr.b">
		<xsl:param name="subjudgmentMode" select="false()"/>

		<xsl:if test="not(preceding-sibling::dbtr.b) and not($subjudgmentMode)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_debtorInformationSubheader;'"/>
			</xsl:call-template>
			<xsl:if test="$subjudgmentMode=false()">
				<xsl:if test="$pcvalue='DKT' and (($colkeyvalue='Dolan' or $vendorkeyvalue='Dolan') or $colkeyvalue='NY')">
					<table class="&pr_table;">
						<xsl:text>&pr_additionalDebtorInformationMayExist;</xsl:text>
					</table>
				</xsl:if>
			</xsl:if>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:if test="dbtr.nme.b/dbtr.nme | dbtr.nme.b/co.dbtr.nme | dbtr.nme.b/gen.cd | dbtr.addr.b">
				<xsl:variable name="paddingTop" select="'&pr_paddingTop;'"/>
				<tr>
					<xsl:attribute name="class">
						<xsl:text>&pr_item;</xsl:text>
						<xsl:if test="preceding-sibling::dbtr.b">
							<xsl:value-of select="concat(' ', $paddingTop)"/>
						</xsl:if>
					</xsl:attribute>
					<th>
						<xsl:choose>
							<xsl:when test="(dbtr.nme.b/dbtr.nme) and (col.key='NDR') and ($subjudgmentMode=false())">
								<xsl:text>&pr_defendantOrLessee;</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>&pr_debtor;</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</th>
					<td>
						<xsl:apply-templates select="dbtr.nme.b/dbtr.nme"/>
						<xsl:if test="$subjudgmentMode=false()">
							<xsl:apply-templates select="dbtr.nme.b/co.dbtr.nme"/>
						</xsl:if>
						<xsl:apply-templates select="dbtr.nme.b/gen.cd"/>
						<xsl:if test ="not(dbtr.nme.b/dbtr.nm.desc) or ($subjudgmentMode=false())">
							<div>
								<xsl:apply-templates select="dbtr.addr.b"/>
							</div>
						</xsl:if>
					</td>
				</tr>
			</xsl:if>
			<xsl:apply-templates select="dbtr.nme.b/dbtr.nm.desc"/>
			<xsl:apply-templates select="dbtr.nme.b/dbtr.aka.nme"/>
			<xsl:apply-templates select="dbtr.typ.b"/>
			<xsl:apply-templates select="fein"/>
			<xsl:if test="(dbtr.typ.b/dbtr.typ!='PERSONAL') and (dbtr.typ.b/dbtr.typ!='CONSUMER') and (dbtr.typ.b/dbtr.typ!='INDIVIDUAL')">
				<xsl:apply-templates select="ssn.fein"/>
			</xsl:if>
			<xsl:if test="$subjudgmentMode=false()">
				<xsl:apply-templates select="bus.duns.b"/>
				<xsl:apply-templates select="hq.duns.b"/>
			</xsl:if>
			<xsl:apply-templates select="dbtr.lic.st"/>
			<xsl:apply-templates select="dbtr.lic.nbr"/>
			<xsl:apply-templates select="dbtr.amt"/>
			<xsl:apply-templates select="dbtr.dispo.cd"/>
			<xsl:apply-templates select="dbtr.dispo.d"/>
			<xsl:if test="$subjudgmentMode=false()">
				<xsl:apply-templates select="oblgn.b"/>
				<xsl:apply-templates select="owd.amt"/>
			</xsl:if>
			<xsl:apply-templates select="dbtr.cmnt"/>
			<xsl:apply-templates select="dbtr.atty.b"/>
		</table>
	</xsl:template>

	<xsl:template match="debt.fst.nm">
		<xsl:if test="name(preceding-sibling::*[1]) = 'debt.lst.nm'">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<!--Debtor Type-->
	<xsl:template match="debt.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Debtor Type-->
	<xsl:template match="dbtr.typ.b[normalize-space(.)]">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_debtorType;</xsl:text>
			</th>
			<td>
				<xsl:choose>
					<xsl:when test='(dbtr.typ="PERSONAL")'>
						<xsl:text>&pr_consumer;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="dbtr.typ"/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="dbtr.typ.desc">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="dbtr.typ.desc"/>
				</xsl:if>
				<xsl:if test="dbtr.desc">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="dbtr.desc"/>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="dbtr.addr.b">
		<xsl:apply-templates select="dbtr.care.of"/>
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="street" select="dbtr.str"/>
			<xsl:with-param name="streetLineTwo" select="dbtr.str2"/>
			<xsl:with-param name="city" select="dbtr.cty"/>
			<xsl:with-param name="stateOrProvince" select="dbtr.st"/>
			<xsl:with-param name="zip" select="dbtr.zip"/>
			<xsl:with-param name="zipExt" select="dbtr.zip.ext"/>
			<xsl:with-param name="country" select="dbtr.cntry"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dbtr.care.of[normalize-space(.)] | cred.care.of[normalize-space(.)]">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--gen.cd-->
	<xsl:template match="gen.cd">
		<xsl:if test="preceding-sibling::*//text() != ''">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Debtor Alias-->
	<xsl:template match="dbtr.aka.nme">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_debtorAlias;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Debtor Name Description-->
	<xsl:template match="dbtr.nm.desc">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_debtorNameDescription;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
				<div>
					<xsl:apply-templates select ="ancestor::dbtr.b/dbtr.addr.b"/>
				</div>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match='dbtr.typ.desc|dbtr.desc'>
		<xsl:if test="preceding-sibling::*//text() != ''">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Tax Payer ID Number-->
	<xsl:template match="fein">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxPayerIdNumber;'"/>
			<xsl:with-param name="nodeType" select="$FEIN"/>
		</xsl:call-template>
	</xsl:template>

	<!--Mask Tax Payer Number if $SHOW-SSN is false-->
	<xsl:template match="ssn.fein">
		<xsl:if test="contains(substring(., 1, 3), '-') and not(text()='00-0000000')">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_taxPayerIdNumber;'"/>
				<xsl:with-param name="nodeType" select="$SSN"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--Driver's License State-->
	<xsl:template match="dbtr.lic.st">
		<xsl:variable name="driversLicenseState">
			<xsl:text>&pr_driversLicenseState;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$driversLicenseState"/>
		</xsl:call-template>
	</xsl:template>

	<!--Driver's License Number-->
	<xsl:template match="dbtr.lic.nbr[encrypted != '000000000000000']">
		<xsl:variable name="driversLicenseNumber">
			<xsl:text>&pr_driversLicenseNumber;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$driversLicenseNumber"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Do not render any dbtr.lic.nbr other than when encrypted != '000000000000000' -->
	<xsl:template match="dbtr.lic.nbr"/>

	<!--Debtor Amount-->
	<xsl:template match="dbtr.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Debtor Disposition-->
	<xsl:template match="dbtr.dispo.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorDisposition;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Debtor DUNS Number-->
	<xsl:template match="bus.duns">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorDunsNumber;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!--Headquarters DUNS Number-->
	<xsl:template match="hq.duns">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_headquartersDunsNumber;'"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!--Debtor Comment(s)-->
	<xsl:template match="dbtr.cmnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorComments;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Debtor Attorney-->
	<xsl:template match="dbtr.atty.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorAttorney;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Debtor Attorney License Number-->
	<xsl:template match="dbtr.atty.lic.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorAttorneyLicenseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Debtor Attorney/Firm -->
	<xsl:template match="dbtr.atty.firm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorFirmName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Debtor Attorney/Firm Address-->
	<xsl:template match="dbtr.atty.addr.b">
		<xsl:variable name="label">
			<xsl:choose>
				<!--If there is NOT a firm name (dbtr.atty.firm), display the 
						'Debtor Attorney/Firm Address:' label, otherwise 
						display an empty cell and put the address under the firm name -->
				<xsl:when test="name(preceding-sibling::*[1]) != 'dbtr.atty.firm'">
					<xsl:text>&pr_debtorAttorneyOrFirmAddress;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="streetNum" select="dbtr.atty.str1"/>
			<xsl:with-param name="street" select="dbtr.atty.str2"/>
			<xsl:with-param name="city" select="dbtr.atty.cty"/>
			<xsl:with-param name="stateOrProvince" select="dbtr.atty.st"/>
			<xsl:with-param name="zip" select="dbtr.atty.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!--Debtor Attorney Phone-->
	<xsl:template match="dbtr.atty.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorAttorneyPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Remedy-->
	<xsl:template match="oblgn | oblgn.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_remedy;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Amount owed-->
	<xsl:template match="oblgn.amt ">
		<!-- TODO: Make sure this work for negative amounts. -->
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_amountOwed;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Total Amount Owed by Debtor-->
	<xsl:template match="owd.amt">
		<!-- TODO: Make sure this work for negative amounts. -->
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalAmountOwedByDebtor;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--DEBTOR INFORMATION-->
	<xsl:template match="debt.b">
		<!--Tracker 116275-->
		<table class="&pr_table;">
			<xsl:if test="not(preceding-sibling::debt.b)">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_debtorInformationSubheader;'"/>
				</xsl:call-template>
				<!--Undo Tracker #122575-->
				<xsl:if test="$pcvalue='DKT' and (($colkeyvalue='Dolan' or $vendorkeyvalue='Dolan') or $colkeyvalue='NY')">
					<xsl:text>&pr_additionalDebtorInformationMayExist;</xsl:text>
				</xsl:if>
			</xsl:if>
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_debtor;</xsl:text>
				</th>
				<td>
					<xsl:apply-templates select="debt.nm.b|debt.corp.nm"/>
					<xsl:apply-templates select="debt.addr.b"/>
				</td>
			</tr>
			<xsl:apply-templates select="debt.t"/>
			<xsl:apply-templates select="fein"/>
			<xsl:apply-templates select="bus.duns.b"/>
			<xsl:apply-templates select="dbtr.amt"/>
			<xsl:apply-templates select="oblgn.amt|oblgn|oblgn.desc|oblgn.b"/>
			<xsl:apply-templates select="owd.amt"/>
		</table>
	</xsl:template>

	<!-- ********************************************************************** 
	*********************** "CREDITOR INFORMATION" ***************************
	************************************************************************-->
	<!--CREDITOR INFORMATION-->
	<xsl:template match="cred.info.b">
		
			<xsl:if test="not(preceding-sibling::cred.info.b)">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_creditorInformation;'"/>
				</xsl:call-template>
			</xsl:if>
		<table class="&pr_table;">
			<xsl:choose>
				<xsl:when test="not(name(..)='subjudge.b')">
					<xsl:apply-templates select="cred.b/cred.nm"/>
					<xsl:apply-templates select="cred.b/hldr.nm"/>
					<xsl:apply-templates select="cred.b/cred.typ"/>
					<xsl:apply-templates select="cred.atty.b"/>
				</xsl:when>
				<xsl:otherwise>
					<!--Apply all templates except 'hldr.lvl.cd' and 'hldr.lvl.desc' and 'bus.duns.b' -->
					<xsl:apply-templates select="node()[name() != 'hldr.lvl.cd' and name() !='hldr.lvl.desc' and name() != 'bus.duns.b']"/>
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>

	<!--CREDITOR INFORMATION-->
	<xsl:template match="cred.b[not(name(..)='cred.info.b')]">
		<table class="&pr_table;">
			<xsl:if test="not(preceding-sibling::cred.b)">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_creditorInformation;'"/>
				</xsl:call-template>
			</xsl:if>
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_creditor;</xsl:text>
				</th>
				<td>
					<xsl:apply-templates select="cred.corp.nm|cred.nm.b"/>
					<div>
						<xsl:apply-templates select="cred.addr.b"/>
					</div>
				</td>
			</tr>
			<xsl:apply-templates select="cred.atty.b"/>
			<xsl:apply-templates select="atty.b"/>
		</table>
	</xsl:template>

	<xsl:template match="cred.addr.b">
		<xsl:apply-templates select="cred.care.of"/>
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="streetNum" select="cred.str.nbr"/>
			<xsl:with-param name="street" select="cred.str | cred.str.nm"/>
			<xsl:with-param name="streetLineTwo" select="cred.str2"/>
			<xsl:with-param name="city" select="cred.cty"/>
			<xsl:with-param name="stateOrProvince" select="cred.st"/>
			<xsl:with-param name="zip" select="cred.zip | cred.zip.b/cred.zip"/>
			<xsl:with-param name="zipExt" select="cred.zip.b/cred.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!--Creditor Block-->
	<xsl:template match="cred.b">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_creditor;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="cred.nm|hldr.nm"/>
				<div>
					<xsl:apply-templates select="cred.addr.b"/>
				</div>
			</td>
		</tr>
		<xsl:apply-templates select="cred.typ"/>
		<xsl:apply-templates select="cred.stat"/>
	</xsl:template>

	<!--Creditor Type-->
	<xsl:template match="cred.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_creditorType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Creditor Status-->
	<xsl:template match="cred.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_creditorStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Creditor Comment(s)-->
	<xsl:template match="cred.cmnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_creditorComments;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Creditor Attorney-->
	<xsl:template match="cred.atty.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_creditorAttorney;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Creditor Attorney License Number-->
	<xsl:template match="cred.atty.lic.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_creditorAttorneyLicenseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Creditor Attorney Firm-->
	<xsl:template match="cred.atty.firm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_creditorFirmName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Creditor Attorney Address-->
	<xsl:template match="cred.atty.addr.b">
		<xsl:variable name="label">
			<!--If there is NOT a firm name, then display the label, 
				otherwise display and empty table cell-->
			<xsl:choose>
				<xsl:when test="(name(preceding-sibling::*[1]) != 'cred.atty.firm') and (name(preceding-sibling::*[1]) != 'cred.atty.nm')">
					<xsl:text>&pr_creditorFirmName;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="streetNum" select="cred.atty.str1"/>
			<xsl:with-param name="street" select="cred.atty.str2"/>
			<xsl:with-param name="city" select="cred.atty.cty"/>
			<xsl:with-param name="stateOrProvince" select="cred.atty.st"/>
			<xsl:with-param name="zip" select="cred.atty.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!--Creditor Attorney Phone-->
	<xsl:template match="cred.atty.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_creditorAttorneyPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Creditor Attorney-->
	<xsl:template match="atty.b">
		<xsl:if test="(atty.nm.b) or (firm.nm)">
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_creditorAttorney;</xsl:text>
				</th>
				<td>
					<xsl:apply-templates select="atty.nm.b/atty.lst.nm"/>
					<xsl:if test="(atty.nm.b/atty.lst.nm) and (atty.nm.b/atty.fst.nm)">
						<xsl:text><![CDATA[ ]]></xsl:text>
					</xsl:if>
					<xsl:apply-templates select="atty.nm.b/atty.fst.nm"/>
					<xsl:if test="firm.nm">
						<xsl:apply-templates select="firm.nm"/>
					</xsl:if>
				</td>
			</tr>
		</xsl:if>
		<xsl:apply-templates select="atty.addr.b"/>
	</xsl:template>

	<xsl:template match="atty.addr.b">
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="streetNum" select="atty.str.nbr | atty.sstr.nm"/>
			<xsl:with-param name="street" select="atty.str.nm"/>
			<xsl:with-param name="city" select="atty.cty"/>
			<xsl:with-param name="stateOrProvince" select="atty.st"/>
			<xsl:with-param name="zip" select="atty.zip.b/atty.zip"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	***********************  "LIEN INFORMATION" section  *********************
	************************************************************************-->
	<!--LIEN INFORMATION-->
	<xsl:template match="lien.info.b[not(//r/s = 'NEW YORK JUDGMENT DOCKET &amp; LIEN BOOKS')]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_lienInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="amt.awd"/>
			<xsl:apply-templates select="total.amt"/>
			<xsl:apply-templates select="principal.amt"/>
			<xsl:apply-templates select="cred.amt"/>
			<xsl:apply-templates select="amt"/>
			<xsl:apply-templates select="tax.amt"/>
			<xsl:apply-templates select="../../filg.info.b/orig.filg.d"/>
			<xsl:apply-templates select="../../filg.info.b/cmplt.d"/>
			<xsl:apply-templates select="sub.dispo.cd"/>
			<xsl:apply-templates select="stat"/>
			<xsl:apply-templates select="sub.stat"/>
			<xsl:apply-templates select="stat.d"/>
			<xsl:apply-templates select="ind.stat.d"/>
			<xsl:apply-templates select="sub.dispo.d"/>
			<xsl:apply-templates select="filg2.d"/>
			<xsl:apply-templates select="sub.cmnt"/>
		</table>
	</xsl:template>

	<!--LIEN INFORMATION-->
	<xsl:template match="lien.info.b[(name(parent::node())='subjudge.b') or ((//filg.info.b/rels.d) and (col.key='liensSA'))]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_lienInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="total.amt"/>
			<xsl:apply-templates select="principal.amt"/>
			<xsl:apply-templates select="cred.amt"/>
			<xsl:apply-templates select="amt.awd"/>
			<xsl:apply-templates select="amt"/>
			<xsl:apply-templates select="tax.amt"/>
			<xsl:apply-templates select="sub.dispo.cd"/>
			<xsl:apply-templates select="stat">
				<xsl:with-param name="testNodes" select="true()"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="sub.stat"/>
			<xsl:apply-templates select="stat.d"/>
			<xsl:apply-templates select="ind.stat.d"/>
			<xsl:apply-templates select="sub.dispo.d"/>
			<xsl:apply-templates select="filg2.d"/>
			<xsl:apply-templates select="sub.cmnt"/>
			<xsl:apply-templates select="../../filg.info.b/orig.filg.d"/>
		</table>
	</xsl:template>

	<!--Lien Amount-->
	<xsl:template match="tax.amt|amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lienAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	********************  "JUDGMENT INFORMATION" section  *********************
	************************************************************************-->
	<xsl:template match="jdg.info.b">
		<xsl:param name="mode" select="'default'"/>
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_judgmentInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:if test="$mode='default'">
				<xsl:apply-templates select="amt.awd"/>
			</xsl:if>
			<xsl:apply-templates select="total.amt"/>
			<xsl:apply-templates select="principal.amt"/>
			<xsl:apply-templates select="cred.amt"/>
			<xsl:choose>
				<xsl:when test="$mode='default'">
					<xsl:apply-templates select="jdg.amt.b"/>
					<xsl:apply-templates select="sub.dispo.cd"/>
					<xsl:apply-templates select="stat"/>
				</xsl:when>
				<xsl:when test="$mode='subjudge'">
					<xsl:apply-templates select="amt.awd"/>
					<xsl:apply-templates select="jdg.amt.b"/>
					<xsl:apply-templates select="sub.dispo.cd"/>
					<xsl:apply-templates select="stat">
						<xsl:with-param name="testNodes" select="true()"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="amt.awd"/>
					<xsl:apply-templates select="stat">
						<xsl:with-param name="testNodes" select="true()"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="($mode='default') or ($mode='subjudge')">
				<xsl:apply-templates select="sub.stat"/>
			</xsl:if>
			<xsl:apply-templates select="stat.d"/>
			<xsl:if test="$mode='subjudgmentinfo'">
				<xsl:apply-templates select="sub.dispo.cd"/>
			</xsl:if>
			<xsl:apply-templates select="sub.dispo.d"/>
			<xsl:apply-templates select="filg2.d"/>
			<xsl:if test="$mode='subjudge'">
				<xsl:apply-templates select="../../filg.info.b/orig.filg.d" />
				<xsl:apply-templates select="../../filg.info.b/cmplt.d"/>
			</xsl:if>
			<xsl:apply-templates select="sub.cmnt"/>
		</table>
	</xsl:template>


	<!--Amount Awarded-->
	<xsl:template match="amt.awd">
		<!-- TODO: Make sure this work for negative amounts. -->
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_amountAwarded;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Total Amount-->
	<xsl:template match="total.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Principal Amount-->
	<xsl:template match="principal.amt">
		<xsl:if test="$colkeyvalue != 'judgeSUPER'">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_principalAmount;'"/>
				<xsl:with-param name="nodeType" select="$CURRENCY"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--Credited Amount-->
	<xsl:template match="cred.amt">
		<xsl:if test="$colkeyvalue != 'judgeSUPER'">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_creditedAmount;'"/>
				<xsl:with-param name="nodeType" select="$CURRENCY"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--Original Filing Date-->
	<xsl:template match="orig.filg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalFilingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Disposition/Remedy-->
	<xsl:template match="sub.dispo.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dispositionOrRemedy;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Disposition Date-->
	<xsl:template match="sub.dispo.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dispositionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Status-->
	<xsl:template match="stat|sub.stat">
		<xsl:param name="testNodes" select="false()"/>
		<!-- TODO: Debug why this doesn't display when testNodes is true() -->
		<!--Display the status only when the col.key matches one of these-->
		<xsl:variable name="shouldDisplay">
			<xsl:choose>
				<xsl:when test="$testNodes=1">
					<xsl:value-of select="(col.key='.') or (col.key='liensSUPER') or (col.key='judgeSUPER')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="true()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$shouldDisplay=true()">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_status;'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--Status Date-->
	<xsl:template match="stat.d | ind.stat.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_statusDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Subsequent Docket Date-->
	<xsl:template match="filg2.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_subsequentDocketDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	******************** "THIRD PARTY INFORMATION" ************************
	************************************************************************-->
	<xsl:template match="thd.prty.b">
		<table class="&pr_table;">
			<xsl:if test="not(preceding-sibling::*[name() = 'thd.prty.b'])">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_subheader;'"/>
					<xsl:with-param name="contents" select="'&pr_thirdPartyInformation;'"/>
				</xsl:call-template>
			</xsl:if>
			<tr class="&pr_item;">
				<th>
					<xsl:text>&pr_thirdPartyName;</xsl:text>
				</th>
				<td>
					<xsl:apply-templates select="thd.prty.nm.b|thd.prty.corp.nm"/>
					<xsl:apply-templates select="thd.prty.addr.b"/>
				</td>
			</tr>
			<xsl:apply-templates select="thd.prty.t"/>
		</table>
	</xsl:template>

	<!--Third Party Type-->
	<xsl:template match="thd.prty.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_thirdPartyType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*********************  "Document Details" section  *******************
	************************************************************************-->

	<xsl:template match="lien.info.b[not(name(..)='subjudge.b') and (srce.ct or srce.cnty or dnt.d or debt.b.nbr or debt.lot or amt or exp.d or sat.d or sat.t)]" mode="DocumentDetails">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_documentDetails;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="srce.ct"/>
			<xsl:apply-templates select="srce.cnty"/>
			<xsl:apply-templates select="dnt.d"/>
			<xsl:apply-templates select="debt.b.nbr"/>
			<xsl:apply-templates select="debt.lot"/>
			<xsl:apply-templates select="amt" mode="DocumentDetails"/>
			<xsl:apply-templates select="exp.d"/>
			<xsl:apply-templates select="sat.d"/>
			<xsl:apply-templates select="sat.t"/>
		</table>
	</xsl:template>

	<!--Source Court-->
	<xsl:template match="srce.ct">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sourceCourt;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Source County-->
	<xsl:template match="srce.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sourceCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Accident Date-->
	<xsl:template match="dnt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_accidentDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Block Number of Subject Property-->
	<xsl:template match="debt.b.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_blockNumberOfSubjectProperty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Lot Number of Subject Property-->
	<xsl:template match="debt.lot">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lotNumberOfSubjectProperty;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Total Amount Awarded-->
	<xsl:template match="amt" mode="DocumentDetails">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalAmountAwarded;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!--Expiration Date-->
	<xsl:template match="exp.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_expirationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Satisfaction Date-->
	<xsl:template match="sat.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_satisfactionDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Type of Satisfaction-->
	<xsl:template match="sat.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_typeOfSatisfaction;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*********************  "SUBJUDGEMENT " section  *******************
	************************************************************************-->

	<!--SUBJUDGEMENT INFORMATION - This template is only called when
	there are multiple subjudge.b block-->
	<xsl:template match="subjudge.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'"/>
			<xsl:with-param name="contents" select="'&pr_subjudgmentInformation;'"/>
		</xsl:call-template>
		<xsl:if test="dbtr.b">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_debtorInformationSubheader;'"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates select="dbtr.b">
			<xsl:with-param name="subjudgmentMode" select="true()"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="cred.info.b"/>
		<xsl:apply-templates select="lien.info.b"/>
		<xsl:apply-templates select="jdg.info.b">
			<xsl:with-param name="mode" select="'subjudgmentinfo'"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--Creditor Name-->
	<xsl:template match="cred.nm[not(ancestor::subjudge.b)]|hldr.nm">
		<tr class="&pr_item;">
			<th>
				<xsl:text>&pr_creditor;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
				<div>
					<xsl:apply-templates select ="following-sibling::cred.addr.b"/>
				</div>
			</td>
		</tr>
	</xsl:template>

	<!--Number of Subjudgements - only shown when 2 or more subjudge.b blocks-->
	<xsl:template match="nbr.of.subjudge[. >= '2']">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfSubjudgments;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	************************* "COMMENT INFORMATION" section ******************
	************************************************************************-->

	<xsl:template match="cmnts.b">
		<xsl:if test="not(preceding-sibling::cmnts.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_commentInformation;'"/>
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--Filing Comment-->
	<xsl:template match="cmnt | cmnt.txt">
		<xsl:variable name="tableRowClass">
			<xsl:if test="../preceding-sibling::cmnts.b">
				<xsl:text>&pr_paddingTop;</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingComment;'"/>
			<xsl:with-param name="divClass" select="$tableRowClass"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Comment Date-->
	<xsl:template match="cmnt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingCommentDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Comment Source Company-->
	<xsl:template match="src.bus">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commentSourceCompany;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Comment Source-->
	<xsl:template match="src.nme">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commentSource;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Comment(s)-->
	<xsl:template match="sub.cmnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_comments;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Comment Source Title-->
	<xsl:template match="src.ti|src.ti2|src.ti3|src.ti.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commentSourceTitle;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	****************************** "REMARKS" section *************************
	************************************************************************-->

	<!--REMARKS-->
	<xsl:template match="remark.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_remarks;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:choose>
				<xsl:when test="/Document/n-docbody/r/pc = 'DKT'">
					<xsl:apply-templates select="rmk.d"/>
					<xsl:text><![CDATA[ ]]><![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="rmk"/>
				</xsl:when>
				<xsl:otherwise>
					<div>
						<xsl:apply-templates />
						<xsl:text><![CDATA[ ]]><![CDATA[ ]]></xsl:text>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>

	<xsl:template match="rmk.d">
		<!--Tracker 117284-->
		<xsl:value-of select="substring(., 5, 2)"/>
		<xsl:text>/</xsl:text>
		<xsl:value-of select="substring(., 7, 2)"/>
		<xsl:text>/</xsl:text>
		<xsl:value-of select="substring(., 1, 4)"/>
	</xsl:template>

	<xsl:template match="rmk">
		<xsl:text>----</xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Put a comma before these fields-->
	<xsl:template match="dbtr.nme|gen.cd">
		<xsl:if test="preceding-sibling::*//text() != ''">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--Put a space before these fields-->
	<xsl:template match="cred.frgn">
		<xsl:if test="preceding-sibling::*//text() != ''">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template name="outputDisclaimers">
		<xsl:if test="($colkeyvalue='.') and ($vvalue='BUSINESS')">
			<xsl:variable name="disclaimer1">
				<xsl:text>THE PRECEDING PUBLIC RECORD DATA IS FOR INFORMATION PURPOSES ONLY AND IS NOT THE OFFICIAL RECORD. 
					CERTIFIED COPIES CAN ONLY BE OBTAINED FROM THE OFFICIAL SOURCE.</xsl:text>
			</xsl:variable>
			<xsl:variable name="disclaimer2">
				<xsl:text>THE PUBLIC RECORD ITEMS REPORTED ABOVE MAY HAVE BEEN PAID, TERMINATED, 
					VACATED OR RELEASED PRIOR TO TODAY'S DATE.</xsl:text>
			</xsl:variable>
			<xsl:variable name="disclaimer3">
				<xsl:if test="$pvalue='JUDGMENT'">
					<xsl:text>THE FACT THAT A BUSINESS IS NAMED AS A JUDGMENT DEBTOR DOES NOT NECESSARILY IMPLY A CLAIM FOR MONEY 
						OR PERFORMANCE AGAINST THAT BUSINESS. SOME LAWSUITS ARE ACTIONS TO CLEAR TITLE TO PROPERTY AND BUSINESSES MAY BE NAMED 
						AS PARTIES BECAUSE THEY THEMSELVES HAVE A LIEN OR CLAIM AGAINST THE PROPERTY. 
						THIS SITUATION IS A POSSIBILITY PARTICULARLY IF THERE ARE MULTIPLE JUDGMENT DEBTORS.</xsl:text>
				</xsl:if>
				<xsl:if test="$pvalue='LIEN'">
					<xsl:text>A LIENHOLDER CAN RECORD THE SAME LIEN IN MORE THAN ONE FILING LOCATION. 
						THE APPEARANCE OF MULTIPLE LIENS RECORDED BY THE SAME LIENHOLDER AGAINST A DEBTOR MAY BE INDICATIVE OF SUCH AN OCCURRENCE.</xsl:text>
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="wrapPublicRecordsDisclaimers">
				<xsl:with-param name="disclaimer1" select="$disclaimer1"/>
				<xsl:with-param name="disclaimer2" select="$disclaimer2"/>
				<xsl:with-param name="disclaimer3" select="$disclaimer3"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="EndOfDocument" priority="1">
		<xsl:param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText"/>
		<xsl:choose>
			<xsl:when test="$PreviewMode = 'True'">
				<xsl:call-template name="AdditionalContent" />
				<xsl:if test="$DeliveryMode = 'True' ">
					<xsl:call-template name="LinkBackToDocDisplay" />
				</xsl:if>
			</xsl:when>
			<xsl:when test="not($EasyEditMode)">
				<table class="&endOfDocumentId;">
					<xsl:if test="($restrictvalue='ILBLN1') or ($restrictvalue='ILBJU1')">
						<tr>
							<td/>
							<td class="&endOfDocumentCopyrightClass;">
								&copy;<xsl:text><![CDATA[ ]]></xsl:text>2006<xsl:text><![CDATA[ ]]></xsl:text>By Law Bull. Publ. Co.
							</td>
						</tr>
					</xsl:if>
					<tr>
						<td>&endOfDocumentText;</td>
						<td class="&endOfDocumentCopyrightClass;">
							&copy;<xsl:text><![CDATA[ ]]></xsl:text><xsl:value-of select="$currentYear"/><xsl:text><![CDATA[ ]]></xsl:text><xsl:copy-of select="$endOfDocumentCopyrightText"/>
						</td>
					</tr>
				</table>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
