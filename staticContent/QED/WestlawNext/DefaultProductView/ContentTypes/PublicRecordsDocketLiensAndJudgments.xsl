<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<!-- ************************************************
			This content is comprised of Three major sections:
			(1) Document Header
			(2) LeftMainColumn
				(A) FILING INFORMATION
				(B) DEBTOR INFORMATION
			(3) RightMainColumn
				(C) CREDITOR INFORMATION
				(D) THIRD PARTY INFORMATION
				(E) REMARKS INFORMATION
				(F) ORDER DOCUMENT
				(G) ADDNOTES
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
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_newYorkJudgmentDocketAndLienRecords;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<!-- Display coverage information -->
		<xsl:if test="$coverage-block">
			<!--skip coverage information -->
		</xsl:if>

		<xsl:call-template name="FilingInfoSubheader"/>
		<table class="&pr_table;">
			<xsl:apply-templates select="ctrl.nbr.b/ctrl.nbr"/>
			<xsl:apply-templates select="filg.typ"/>
			<xsl:call-template name="FilingOffice"/>
			<xsl:apply-templates select="filg.cnty"/>
			<xsl:apply-templates select="entry.d"/>
			<xsl:apply-templates select="cs.nbr"/>
			<xsl:apply-templates select="bk.nm"/>
		</table>			

		<xsl:apply-templates select="debt.b"/>			
	
	</xsl:template>

	<xsl:template name="FilingInfoSubheader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_filingInfo;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">

		<!--CREDITOR INFORMATION-->		
		<xsl:apply-templates select="cred.b"/>
		
		<!--THIRD PARTY INFORMATION-->
		<xsl:apply-templates select="thd.prty.b"/>
		
		<!-- DOCUMENT DETAILS-->
		<xsl:apply-templates select="lien.info.b" mode="DocumentDetails"/>
			
		<!-- REMARKS INFORMATION-->
		<xsl:apply-templates select="remark.b"/>
		
		<xsl:call-template name="outputOrderDocumentsSection"/>
		
		<xsl:call-template name="outputDisclaimers"/>
	</xsl:template>



	<!-- Filing Type block-->
	<xsl:template name="FilingOffice">
		<tr class="&pr_item;">
			<th>Filing Office:</th>
			<td>NEW YORK COUNTY CLERK</td>
		</tr>
	</xsl:template>

	<xsl:template match="filg.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingType;'"/>
			<xsl:with-param name="selectNodes" select="filg.typ"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing County block-->
	<xsl:template match="filg.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingCounty;'"/>
			<xsl:with-param name="selectNodes" select="filg.cnty"/>
		</xsl:call-template>
	</xsl:template>

	<!--Court Index Number-->
	<xsl:template match="cs.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_courtIndexNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing County block-->
	<xsl:template match="ctrl.nbr.b/ctrl.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingNumber;'"/>
			<xsl:with-param name="selectNodes" select="ctrl.nbr.b/ctrl.nbr"/>
		</xsl:call-template>
	</xsl:template>

	<!--Book Name for NY Docket-->
	<xsl:template match="bk.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bookName;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Filing Date-->
	<xsl:template match="entry.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	***********************  "DEBTOR INFORMATION" section  *********************
	************************************************************************-->

	<!--DEBTOR INFORMATION-->
	<xsl:template match="debt.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_debtorInformationSubheader;'"/>
		</xsl:call-template>
		<table class="&pr_table;">

			<tr><td colspan="2"><xsl:text>&pr_additionalDebtorInformationMayExist;</xsl:text></td></tr>

			<xsl:apply-templates select="debt.corp.nm|debt.nm.b"/>
			<xsl:apply-templates select="debt.addr.b"/>		
			<xsl:apply-templates select="debt.t"/>
		</table>
	</xsl:template>

	<!--Debtor Type-->
	<xsl:template match="debt.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="debt.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_address;'"/>
			<xsl:with-param name="streetNum" select="debt.str.nbr"/>
			<xsl:with-param name="street" select="debt.str.nm"/>
			<xsl:with-param name="streetLineTwo" select="debt.str2"/>
			<xsl:with-param name="city" select="debt.cty"/>
			<xsl:with-param name="stateOrProvince" select="debt.st"/>
			<xsl:with-param name="zip" select="debt.zip.b/debt.zip"/>
			<xsl:with-param name="zipExt" select="debt.zip.b/debt.zip.ext"/>
		</xsl:call-template>
	</xsl:template>


	<xsl:template match="debt.corp.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtor;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="debt.nm.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_debtor;'"/>
			<xsl:with-param name="firstName" select="debt.fst.nm"/>
			<xsl:with-param name="middleName" select="debt.m.nm"/>
			<xsl:with-param name="lastName" select="debt.lst.nm"/>
		</xsl:call-template>
	</xsl:template>

	
	<!-- ********************************************************************** 
	*********************** "CREDITOR INFORMATION" ***************************
	************************************************************************-->

	<!--CREDITOR INFORMATION-->
	<xsl:template match="cred.b">
		<xsl:if test="not(preceding-sibling::cred.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_creditorInformation;'"/>
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates select="cred.corp.nm"/>
			<xsl:apply-templates select="cred.nm.b"/>
			<xsl:apply-templates select="cred.addr.b"/>
			<xsl:apply-templates select="cred.atty.b"/>
			<xsl:apply-templates select="atty.b"/>
		</table>
	</xsl:template>

	<xsl:template match="cred.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_address;'"/>
			<xsl:with-param name="streetNum" select="cred.str.nbr"/>
			<xsl:with-param name="street" select="cred.str | cred.str.nm"/>
			<xsl:with-param name="streetLineTwo" select="cred.str2"/>
			<xsl:with-param name="city" select="cred.cty"/>
			<xsl:with-param name="stateOrProvince" select="cred.st"/>
			<xsl:with-param name="zip" select="cred.zip | cred.zip.b/cred.zip"/>
			<xsl:with-param name="zipExt" select="cred.zip.b/cred.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cred.corp.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_creditor;'"/>
			<xsl:with-param name="selectNodes" select="cred.corp.nm"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cred.nm.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_creditor;'"/>
			<xsl:with-param name="firstName" select="cred.fst.nm"/>
			<xsl:with-param name="middleName" select="cred.m.nm"/>
			<xsl:with-param name="lastName" select="cred.lst.nm"/>
		</xsl:call-template>
	</xsl:template>

	<!--Creditor Attorney-->
	<xsl:template match="atty.b">
		<xsl:apply-templates select="firm.nm"/>
		<xsl:apply-templates select="atty.nm.b"/>
		<xsl:apply-templates select="atty.addr.b"/>
	</xsl:template>

	<xsl:template match="firm.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_creditorAttorney;'"/>
			<xsl:with-param name="selectNodes" select="firm.nm"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="atty.nm.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_creditorAttorney;'"/>
			<xsl:with-param name="firstName" select="atty.fst.nm"/>
			<xsl:with-param name="middleName" select="atty.m.nm"/>
			<xsl:with-param name="lastName" select="atty.lst.nm"/>
		</xsl:call-template>
	</xsl:template>	
	

	<xsl:template match="atty.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_attorneyAddress;'"/>
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
	
	<!--Lien Amount-->
	<xsl:template match="amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lienAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	******************** "THIRD PARTY INFORMATION" ************************
	************************************************************************-->
	<xsl:template match="thd.prty.b">
		<xsl:if test="not(preceding-sibling::thd.prty.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_thirdPartyInformation;'"/>
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates select="thd.prty.nm.b|thd.prty.corp.nm"/>
			<xsl:apply-templates select="thd.prty.addr.b"/>
		</table>
	</xsl:template>

		<xsl:template match="thd.prty.corp.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_thirdPartyName;'"/>
		</xsl:call-template>
	</xsl:template>
	
<xsl:template match="thd.prty.nm.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_thirdPartyName;'"/>
			<xsl:with-param name="firstName" select="thd.prty.fst.nm"/>
			<xsl:with-param name="middleName" select="thd.prty.m.nm"/>
			<xsl:with-param name="lastName" select="thd.prty.lst.nm"/>
		</xsl:call-template>
	</xsl:template>
	
<xsl:template match="thd.prty.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_address;'"/>
			<xsl:with-param name="streetNum" select="thd.prty.str.nbr"/>
			<xsl:with-param name="street" select="thd.prty.str.nm"/>
			<xsl:with-param name="city" select="thd.prty.cty"/>
			<xsl:with-param name="stateOrProvince" select="thd.prty.st"/>
			<xsl:with-param name="zip" select="thd.prty.zip.b/thd.prty.zip"/>
			<xsl:with-param name="zipExt" select="thd.prty.zip.b/thd.prty.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

		
	<!-- ********************************************************************** 
	*********************  "Document Details" section  *******************
	************************************************************************-->

	<xsl:template match="lien.info.b" mode="DocumentDetails">
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
	****************************** "REMARKS" section *************************
	************************************************************************-->

	<!--REMARKS-->
	<xsl:template match="remark.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_remarks;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<tr><td>
			<xsl:apply-templates select="rmk.d"/>
			<xsl:text><![CDATA[ ]]><![CDATA[ ]]></xsl:text>
			<xsl:apply-templates select="rmk"/>
			</td></tr>
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
