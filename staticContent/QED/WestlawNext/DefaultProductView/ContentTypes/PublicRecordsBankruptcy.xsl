<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsMaskedData.xsl"/>

	<!-- Do not render these nodes -->
	<xsl:template match="p|pc|c|pre|upd.d|ld.d|upd.freq|dis.d|s|col.key|v|trste.phn|filg.off.duns|court.id|label|atty.zip.ext|dbtr.zip.ext|trste.zip.ext|trste.str2"/>

	<!-- Variables -->
	<xsl:variable name="pcValue" select="/Document/n-docbody/r/pc" />
	<xsl:variable name="vValue" select="/Document/n-docbody/r/v" />

	<xsl:variable name="isChunked" >
		<xsl:choose>
			<xsl:when test ="/Document/map/entry[key = 'md.chunk.number']/value > 1">
				<xsl:value-of select ="true"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select ="false"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- ************************************************
			(B) Debtor Information
			(C) Creditor Information
			(D) Filing Information
			(E) Assets and Liabilities Information
			(F) Plan Information
			(G) Comment Information
			(H) History Information
			(I) Federal Bankruptcy Trustee Information
			(J) Federal Bankruptcy Creditor Information
			************************************************* -->

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsBankruptcyClass;'" />
			<xsl:with-param name="dualColumn" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_bankruptcyFilingRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsMainColumn">
		<xsl:call-template name="DebtorSubheader"/>
		<xsl:apply-templates select="dbtr.b"/>
		<xsl:apply-templates select="debtor.info.block"/>

		<xsl:apply-templates select="cred.info.b"/>

		<xsl:apply-templates select="filg.info.b"/>
		<xsl:apply-templates select="case.information.block"/>

		<!-- Dockets Filing Information -->
		<xsl:apply-templates select="court.block"/>
		<xsl:apply-templates select="panel.block"/>
		<xsl:apply-templates select="docket.block"/>
		<xsl:apply-templates select="case.type.block"/>
		<xsl:apply-templates select="filing.date.block"/>
		<xsl:apply-templates select="case.type.block/case.type"/>
		<xsl:apply-templates select="status.block"/>
		<xsl:apply-templates select="discharge.date.block"/>
		<xsl:apply-templates select="final.decree.date.block"/>

		<xsl:apply-templates select="aset.liab.b"/>
		<xsl:apply-templates select="plan.info.b"/>
		<xsl:apply-templates select="cmnts.b"/>
		<xsl:apply-templates select="hist.info.b"/>
		<xsl:apply-templates select="trustee.info.block"/>
		<xsl:apply-templates select="creditor.info.block"/>

		<xsl:if test ="/Document/map/entry[key = 'md.chunk.number']/value > 1 and docket.entries.block">
			<xsl:apply-templates select ="docket.entries.block"/>
		</xsl:if>
		<xsl:call-template name="outputOrderDocumentsSection"/>
		<xsl:call-template name="AddlNotes"/>
	</xsl:template>

	<!-- ********************************************************************** 
	******************** (B)"Debtor Information" section *********************
	************************************************************************-->
	<xsl:template name="DebtorSubheader">
		<xsl:variable name="FilingType" select="descendant::filg.typ" />

		<xsl:if test="(contains($FilingType,'SCH 341') and normalize-space(dbtr.b)) or
							(contains($FilingType,'TRUSTEE') and normalize-space(dbtr.b)) or
						normalize-space(dbtr.b) or normalize-space(debtor.info.block)">

			<xsl:variable name="debtorSubheader">
				<xsl:choose>
					<xsl:when test='contains($FilingType,"SCH 341")'>
						<xsl:text>&pr_schedule341Information;</xsl:text>
					</xsl:when>
					<xsl:when test='contains($FilingType,"TRUSTEE")'>
						<xsl:text>&pr_trusteeInformation;</xsl:text>
					</xsl:when>
					<xsl:when test="normalize-space(dbtr.b) or normalize-space(debtor.info.block)">
						<xsl:text>&pr_debtorInformation;</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>

			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="$debtorSubheader" />
			</xsl:call-template>

		</xsl:if>
	</xsl:template>

	<!-- Debtor block -->
	<xsl:template match="dbtr.b">
		<table class="&pr_table;">
			<xsl:if test="dbtr.nme.b or dbtr.addr.b">
				<tr>
					<xsl:call-template name="wrapWithTableHeader">
						<xsl:with-param name="class" select="'&pr_itemKey;'" />
						<xsl:with-param name="contents" select="'&pr_debtor;'" />
					</xsl:call-template>
					<td>
						<xsl:apply-templates select="dbtr.nme.b"/>
						<xsl:apply-templates select="dbtr.addr.b"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:apply-templates select="fein"/>
			<xsl:apply-templates select="bus.duns.b"/>
			<xsl:apply-templates select="sgnr.b"/>
			<xsl:apply-templates select="dbtr.atty.b"/>
		</table>
	</xsl:template>

	<!-- Debtor Name -->
	<xsl:template match="dbtr.nme.b">
		<xsl:apply-templates select="dbtr.nme"/>
		<xsl:apply-templates select="dbtr.nm.desc"/>
		<xsl:apply-templates select="gen.cd"/>
	</xsl:template>

	<!-- Generation code -->
	<xsl:template match="gen.cd[normalize-space(.)]">
		<xsl:if test="preceding-sibling::dbtr.nme">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="dbtr.addr.b">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="fullStreet" select="dbtr.str"/>
				<xsl:with-param name="streetLineTwo" select="dbtr.str2"/>
				<xsl:with-param name="city" select="dbtr.cty"/>
				<xsl:with-param name="stateOrProvince" select="dbtr.st"/>
				<xsl:with-param name="zip" select="dbtr.zip"/>
				<xsl:with-param name="country" select="dbtr.cntry"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="sch341.d[normalize-space(.)]">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_schedule341Location;'" />
			</xsl:call-template>
			<td>
				<xsl:apply-templates select="../../dbtr.b/dbtr.addr.b/dbtr.str"/>
			</td>
		</tr>
	</xsl:template>

	<!-- Debtor DUNS Number -->
	<xsl:template match="dbtr.b/bus.duns.b[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_debtorDunsNumber;'"/>
			<xsl:with-param name="selectNodes" select="bus.duns"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Creditor DUNS Number -->
	<xsl:template match="cred.info.b/bus.duns.b[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_creditorDunsNumber;'"/>
			<xsl:with-param name="selectNodes" select="bus.duns"/>
			<xsl:with-param name="nodeType" select="$DUNS"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Debtor Attorney block -->
	<xsl:template match="dbtr.atty.b">
		<xsl:apply-templates select="atty.nme"/>
		<xsl:call-template name="DebtorFirmInfo"/>
		<xsl:apply-templates select="atty.phn"/>
	</xsl:template>

	<!-- Debtor attorney name -->
	<xsl:template match="dbtr.atty.b/atty.nme[normalize-space(.)]">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_attorney;'" />
			</xsl:call-template>
			<xsl:call-template name="wrapWithTableValue"/>
		</tr>
	</xsl:template>

	<!-- Firm information -->
	<xsl:template name="DebtorFirmInfo">
		<xsl:if test="atty.firm or firm.addr.b">
			<tr>
				<th>
					<xsl:if test="atty.firm">
						<xsl:text>&pr_firm;</xsl:text>
					</xsl:if>
				</th>
				<td>
					<xsl:apply-templates select="atty.firm"/>
					<xsl:apply-templates select="firm.addr.b"/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Firm Address block -->
	<xsl:template match="firm.addr.b|rfre.addr.b|trste.addr.b">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="fullStreet" select="atty.str | rfre.str | trste.str"/>
				<xsl:with-param name="streetLineTwo" select="atty.str2 | rfre.str2 | trste.str2"/>
				<xsl:with-param name="city" select="atty.cty | rfre.cty | trste.cty"/>
				<xsl:with-param name="stateOrProvince" select="atty.st | rfre.st | trste.st"/>
				<xsl:with-param name="zip" select="atty.zip | rfre.zip | trste.zip"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Attorney Phone -->
	<xsl:template match="atty.phn[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Debtor County -->
	<xsl:template match="dbtr.cnty[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_countyOfResidence;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Federal Employer Identification Number-->
	<xsl:template match="fein[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_federalEmployeeIdentificationNumber;'"/>
			<xsl:with-param name="nodeType" select="$FEIN"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Petition signer block -->
	<xsl:template match="sgnr.b[normalize-space(.)]">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_petitionSigner;'" />
			</xsl:call-template>
			<td>
				<xsl:value-of select="sgnr.indv"/>
				<div>
					<xsl:if test="sgnr.ti">
						<xsl:value-of select="sgnr.ti"/>
						<xsl:text><![CDATA[ ]]></xsl:text>
						<xsl:value-of select="sgnr.desc"/>
					</xsl:if>
					<xsl:value-of select="sgnr.desc"/>
				</div>
			</td>
		</tr>
	</xsl:template>

	<!-- SSN-->
	<xsl:template match="ssn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ssn;'"/>
			<xsl:with-param name="nodeType" select="$SSN"/>
		</xsl:call-template>
	</xsl:template>



	<xsl:template match="debtor.info.block">
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!--  Fed. debtor info-->
	<xsl:template match="debtor">
		<xsl:choose>
			<xsl:when test="$pcValue='BKY'">
				<xsl:call-template name="SuperiorDebtorInfo"/>
			</xsl:when>
			<xsl:when test="$pcValue='FBR' or $isChunked">
				<xsl:call-template name ="DocketsDebtorInfo"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Superior Debtor information -->
	<xsl:template name="SuperiorDebtorInfo">
		<xsl:if test="party.name or party.name.block">
			<xsl:call-template name="SuperiorDebtorNameAddress"/>
		</xsl:if>
		<xsl:apply-templates select="party.attorney.block"/>
	</xsl:template>

	<!-- Superior name and address -->
	<xsl:template name="SuperiorDebtorNameAddress">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_name;'" />
			</xsl:call-template>
			<td>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="firstName" select="party.name"/>
				</xsl:call-template>
				<xsl:apply-templates select ="party.address.block"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="DocketsDebtorInfo">
		<div>
			<xsl:if test="preceding-sibling::debtor">
				<xsl:attribute name="class">
					<xsl:value-of select="'&pr_marginTop;'"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="party.name.block or party.address.block">
				<xsl:call-template name="DebtorNameAddress"/>
			</xsl:if>
			<xsl:apply-templates select="party.ssn.block/ssn.frag"/>
			<xsl:apply-templates select="party.name.block/party.aka.block"/>
			<xsl:apply-templates select="party.attorney.block" mode="dockets"/>
		</div>
	</xsl:template>


	<xsl:template match="ssn.frag">
		<xsl:param name="isPrivacyProtected"/>
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_ssn;'" />
			</xsl:call-template>
			<td>
				<xsl:call-template name="FormatSSN">
					<xsl:with-param name="nodevalue">
						<xsl:value-of select="concat('XXX-XX-',normalize-space(.))"/>
					</xsl:with-param>
					<xsl:with-param name="isPrivacyProtected" select="$isPrivacyProtected"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="DebtorNameAddress">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_name;'" />
			</xsl:call-template>
			<td>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="firstName" select="party.name.block/party.name"/>
				</xsl:call-template>
				<xsl:apply-templates select ="party.address.block"/>
			</td>
		</tr>
		<xsl:if test="not(party.name.block/party.aka.block)">
			<xsl:apply-templates select="party.phone.block"/>
		</xsl:if>
	</xsl:template>


	<!--  Fed. debtor address block-->
	<xsl:template match="party.address.block">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="fullStreet" select="address.description | street"/>
				<xsl:with-param name="streetLineTwo" select="street2"/>
				<xsl:with-param name="city" select="city"/>
				<xsl:with-param name="stateOrProvince" select="state"/>
				<xsl:with-param name="zip" select="zip"/>
			</xsl:call-template>
		</div>
	</xsl:template>


	<!-- Dockets debtor attorney name -->
	<xsl:template match="party.attorney.block" mode="dockets">
		<xsl:apply-templates select="descendant::attorney.name" />
		<xsl:apply-templates select="descendant::firm.name.block"/>
		<xsl:apply-templates select="attorney.address.block"/>
		<xsl:apply-templates select="firm.address.combined"/>
		<xsl:apply-templates select="firm.address.block"/>
		<xsl:apply-templates select="descendant::attorney.phone"/>
		<xsl:apply-templates select="descendant::firm.phone"/>
		<xsl:apply-templates select="descendant::firm.fax"/>
	</xsl:template>

	<xsl:template match="party.name.block">
		<xsl:call-template name="FormatName">
			<xsl:with-param name="firstName" select="party.name"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="firm.name.block">
		<xsl:apply-templates select="firm.name"/>
	</xsl:template>

	<xsl:template match="firm.address.combined">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="' '"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Firm address block -->
	<xsl:template match="firm.address.block">
		<xsl:variable name="addressLabel">
			<xsl:choose>
				<xsl:when test="firm.address.block and not(preceding-sibling::firm.name.block)">
					<xsl:text>&pr_firm;<![CDATA[ ]]></xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="defaultLabel" select="$addressLabel"/>
			<xsl:with-param name="street" select="street"/>
			<xsl:with-param name="streetLineTwo" select="street2"/>
			<xsl:with-param name="city" select="city"/>
			<xsl:with-param name="stateOrProvince" select="state"/>
			<xsl:with-param name="zip" select="zip"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.aka.block">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_akaNames;'"/>
		</xsl:call-template>
		<xsl:apply-templates select="ancestor::debtor/party.phone.block"/>
	</xsl:template>

	<xsl:template match="party.aka">
		<xsl:call-template name="wrapPublicRecordsName"/>
	</xsl:template>

	<!--  party phone block-->
	<xsl:template match="party.phone">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!--  Fed. debtor attorney block-->
	<xsl:template match="party.attorney.block">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_attorney;'" />
			</xsl:call-template>
			<td>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="firstName" select="attorney.name.block/attorney.name"/>
				</xsl:call-template>
				<xsl:apply-templates select="attorney.address.block"/>
			</td>
		</tr>
		<xsl:apply-templates select="attorney.phone.block/attorney.phone"/>
	</xsl:template>

	<xsl:template match="attorney.address.block">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="fullStreet" select="street"/>
				<xsl:with-param name="streetLineTwo" select="street2"/>
				<xsl:with-param name="city" select="city"/>
				<xsl:with-param name="stateOrProvince" select="state"/>
				<xsl:with-param name="zip" select="zip"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Attorney name -->
	<xsl:template match="attorney.name">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_attorney;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Firm Name -->
	<xsl:template match="firm.name">
		<xsl:if test ="ancestor::party.attorney.block/attorney.name">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_firm;'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Attorney fax -->
	<xsl:template match="attorney.fax|firm.fax">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fax;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Firm phone -->
	<xsl:template match="firm.phone|attorney.phone">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	******************** (C)"Creditor Information" section **********************
	************************************************************************-->
	<!-- Creditor information block -->
	<xsl:template match="cred.info.b">
		<xsl:if test="not(preceding-sibling::cred.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_creditorInformation;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<tr>
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="class" select="'&pr_itemKey;'" />
					<xsl:with-param name="contents" select="'&pr_creditor;'" />
				</xsl:call-template>
				<td>
					<xsl:apply-templates select="cred.nm"/>
					<xsl:apply-templates select="cred.addr.b"/>
				</td>
			</tr>
			<xsl:apply-templates select="bus.duns.b"/>
			<xsl:apply-templates select="clm.scrd"/>
			<xsl:apply-templates select="clm.unscrd"/>
			<xsl:apply-templates select="clm.oth"/>
			<xsl:apply-templates select="clm.oth.desc"/>
			<xsl:apply-templates select="cred.atty.b"/>
		</table>
	</xsl:template>

	<!-- Creditor address block -->
	<xsl:template match="cred.addr.b">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="fullStreet" select="cred.str"/>
				<xsl:with-param name="city" select="cred.cty"/>
				<xsl:with-param name="stateOrProvince" select="cred.st"/>
				<xsl:with-param name="zip" select="cred.zip"/>
				<xsl:with-param name="country" select="cred.cntry"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Secured Claim amount -->
	<xsl:template match="clm.scrd[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_securedClaims;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Unsecured Claim amount -->
	<xsl:template match="clm.unscrd[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_unsecuredClaims;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Other Claim amount -->
	<xsl:template match="clm.oth[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_otherClaims;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Claim description -->
	<xsl:template match="clm.oth.desc[normalize-space(.)]">
		<xsl:if test="preceding-sibling::clm.oth">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Creditor Attorney block -->
	<xsl:template match="cred.atty.b">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_creditorAttorney;'" />
			</xsl:call-template>
			<td>
				<xsl:value-of select="atty.nme"/>
				<xsl:apply-templates select="atty.firm"/>
				<xsl:apply-templates select="firm.addr.b"/>
			</td>
		</tr>
	</xsl:template>


	<!-- ********************************************************************** 
	*************************  (D)"Filing Information" section  *************************
	************************************************************************-->
	<xsl:template match="filg.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_filingInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="filg.loc.b"/>
			<xsl:apply-templates select="jdg"/>
			<xsl:apply-templates select="rfre.b"/>
			<xsl:apply-templates select="trste.b"/>
			<xsl:apply-templates select="filg.typ.b"/>
			<xsl:apply-templates select="filg.d"/>
			<xsl:apply-templates select="filg.nbr"/>
			<xsl:apply-templates select="stat"/>
			<xsl:apply-templates select="stat.d"/>
			<xsl:apply-templates select="sch341.d"/>
		</table>
	</xsl:template>

	<!-- Filing location block -->
	<xsl:template match="filg.loc.b">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'"/>
				<xsl:with-param name="contents" select="'&pr_filingOffice;'"/>
			</xsl:call-template>
			<td>
				<xsl:apply-templates select="filg.off.nme"/>
				<xsl:apply-templates select="filg.addr.b"/>
			</td>
		</tr>
		<xsl:apply-templates select="filg.district"/>
	</xsl:template>

	<xsl:template match="filg.addr.b">
		<div>
			<xsl:call-template name="FormatAddress">
				<xsl:with-param name="fullStreet" select="filg.off.str"/>
				<xsl:with-param name="streetLineTwo" select="filg.off.str2"/>
				<xsl:with-param name="city" select="filg.off.cty"/>
				<xsl:with-param name="stateOrProvince" select="filg.off.st"/>
				<xsl:with-param name="zip" select="filg.off.zip"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Filing District -->
	<xsl:template match="filg.district">
		<xsl:if test='not(filg.info.b/court.id="TNMADF1")'>
			<xsl:if test='not(filg.info.b/court.id="TNGREF1")'>
				<xsl:if test='not(filg.info.b/filg.off.duns="362719593")'>
					<xsl:if test='not(filg.info.b/filg.off.duns="824800379")'>
						<xsl:call-template name="wrapPublicRecordsItem">
							<xsl:with-param name="defaultLabel" select="'&pr_filingDistrict;'"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Filing Judge -->
	<xsl:template match="jdg">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_judge;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="rfre.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_referee;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="rfre.bus">
		<xsl:call-template name="FormatCompany">
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Trustee block -->
	<xsl:template match="trste.b[normalize-space(.)]">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_trustee;'" />
			</xsl:call-template>
			<td>
				<xsl:apply-templates select="trste.indv"/>
				<xsl:if test="trste.bus">
					<div>
						<xsl:apply-templates select="trste.bus"/>
					</div>
				</xsl:if>

				<xsl:apply-templates select="trste.addr.b"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="trste.bus">
		<xsl:call-template name="FormatCompany">
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="filg.typ.b">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Filing Chapter -->
	<xsl:template match="filg.ch[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingChapter;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Type -->
	<xsl:template match="vol|filg.typ[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Date -->
	<xsl:template match="filg.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Number -->
	<xsl:template match="filg.nbr[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Status -->
	<xsl:template match="stat[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Filing Status Date -->
	<xsl:template match="stat.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingStatusDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Schedule 341 Date -->
	<xsl:template match="sch341.d[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_schedule341Date;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Motion to dismiss -->
	<xsl:template match="dis.flag[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_motionToDismissByDebtor;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Federal case information block -->
	<xsl:template match="case.information.block">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_filingInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="court.block"/>
			<xsl:apply-templates select="judge.block"/>
			<xsl:apply-templates select="docket.number.block"/>
			<xsl:apply-templates select="case.type.block"/>
			<xsl:apply-templates select="filing.date.block/filing.date"/>
			<xsl:apply-templates select="status.block"/>
			<xsl:apply-templates select="discharge.date.block/discharge.date"/>
			<xsl:apply-templates select="final.decree.date.block/final.decree.date"/>
		</table>
	</xsl:template>

	<!-- Federal Filing office -->
	<xsl:template match="court.block[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingOffice;'"/>
			<xsl:with-param name ="selectNodes" select="court"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Federal judge -->
	<xsl:template match="judge.block[normalize-space(.)]|panel.block[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_judge;'"/>
			<xsl:with-param name="firstName" select="judge"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Federal filing number -->
	<xsl:template match="docket.number.block[normalize-space(.)] | docket.block[normalize-space(.)]">
		<!-- build <tr> manually because wrapPublicRecordsItem template applies the wrong label here-->
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="'&pr_filingNumber;'" />
			</xsl:call-template>
			<td>
				<xsl:apply-templates select="docket.number"/>
			</td>
		</tr>
	</xsl:template>

	<!-- Federal Filing Chapter -->
	<xsl:template match="case.type.block/chapter[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingChapter;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Federal Filing Type -->
	<xsl:template match="case.type.block/case.type[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Federal Filing Date -->
	<xsl:template match="filing.date.block/filing.date[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_filingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Federal Filing status -->
	<xsl:template match="status.block">
		<xsl:if test="status.description != ''">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_filingStatus;'"/>
				<xsl:with-param name="selectNodes" select="status.description"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Federal Discharge Date -->
	<xsl:template match="discharge.date.block/discharge.date[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dischargeDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Federal Final decree date -->
	<xsl:template match="final.decree.date.block/final.decree.date[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_finalDecreeDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	*************************  (E)"Assets and Liabilities Information" section 
	************************************************************************-->
	<!-- Asset and Liabilities information block -->
	<xsl:template match="aset.liab.b[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_assetsAndLiabilities;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Exempt Assets -->
	<xsl:template match="exm.aset[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_exemptAssets;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Other Assets -->
	<xsl:template match="oth.aset[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_otherAssets;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Total Assets -->
	<xsl:template match="tot.aset[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalAssets;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Secured Liabilities -->
	<xsl:template match="scrd.liab[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_securedLiabilities;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Unsecured Liabilities -->
	<xsl:template match="unscrd.liab[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_unsecuredLiabilities;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Other Liabilities -->
	<xsl:template match="oth.liab[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_otherLiabilities;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Total Liabilities -->
	<xsl:template match="tot.liab[normalize-space(.)]">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalLiabilities;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Schedule Date -->
	<xsl:template match="schd.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_scheduleDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- asset distribution flag  -->
	<xsl:template match="aset.dist">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_assetsAvailableForDistribution;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	*************************  (F)"Plan Information" section  *************************
	************************************************************************-->
	<!-- Plan information block-->
	<xsl:template match="plan.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_planInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Plan filing date -->
	<xsl:template match="plan.filg.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_planFilingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Plan status -->
	<xsl:template match="plan.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_planStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Plan status date -->
	<xsl:template match="plan.stat.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_planStatusDate;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Author -->
	<xsl:template match="auth">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_planAuthor;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Author type, title, description -->
	<xsl:template match="auth.typ|auth.ti|auth.ti.desc">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<!-- Plan details -->
	<xsl:template match="plan.det.txt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_planDetails;'"/>
		</xsl:call-template>
	</xsl:template>



	<!-- ********************************************************************** 
	*************************  (G)"Comment Information" section  *************************
	************************************************************************-->
	<!-- Comment information block -->
	<xsl:template match="cmnts.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_commentInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- Comment text -->
	<xsl:template match="cmnt|cmnt.txt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_planComment;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Comment date -->
	<xsl:template match="cmnt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_planCommentDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Source name -->
	<xsl:template match="src.nme">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commentSource;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Source name -->
	<xsl:template match="src.ti|src.ti.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commentSourceTitle;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Source title 2 -->
	<xsl:template match="src.ti2">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commentSourceTitle2;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Source name -->
	<xsl:template match="src.ti3">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commentSourceTitle3;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Source company -->
	<xsl:template match="src.bus">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_commentSourceCompany;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*************************  (H)"History Information" section  *************************
	************************************************************************-->
	<!-- History information block -->
	<xsl:template match="hist.info.b">
		<xsl:if test="not(preceding-sibling::hist.info.b)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_history;'" />
			</xsl:call-template>
		</xsl:if>

		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<!-- History events -->
	<xsl:template match="hist.evnt | hist.evnt.desc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_historyEvent;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- History events date -->
	<xsl:template match="hist.evnt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_eventDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>



	<!-- ********************************************************************** 
	*********  (I)"Federal Bankruptcy Trustee Information" section  ***********
	************************************************************************-->
	<!-- Federal Trustee information block -->
	<xsl:template match="trustee.info.block">
		<xsl:if test="matched.trustee.block or trustee/party.name or trustee/party.name.block">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_trusteeInformation;'" />
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="matched.trustee.block"/>
				<xsl:apply-templates select="trustee"/>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="matched.trustee.block">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- Trustee -->
	<xsl:template match="trustee">
		<xsl:if test="party.type and (party.name.block or party.name)">
			<xsl:call-template name="TrusteeNameAddressPhone"/>
		</xsl:if>
		<xsl:apply-templates select="added.date.block"/>
	</xsl:template>

	<!-- Trustee name and address -->
	<xsl:template name="TrusteeNameAddressPhone">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_name;'" />
			</xsl:call-template>
			<td>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="firstName" select="party.name.block/party.name"/>
				</xsl:call-template>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="firstName" select="party.name"/>
				</xsl:call-template>
				<xsl:apply-templates select ="party.address.block"/>
			</td>
		</tr>
		<xsl:apply-templates select="party.phone.block"/>
	</xsl:template>

	<!-- Date assigned -->
	<xsl:template match="added.date.block">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateAssigned;'"/>
			<xsl:with-param name="selectNodes" select="added.date"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>



	<!-- ********************************************************************** 
	*********** (J)"Federal Bankruptcy Creditor Information" section  *********
	************************************************************************-->
	<!-- Federal Bankruptcy Creditor information block -->
	<xsl:template match="creditor.info.block">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_creditorInformation;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>


	<!--  matched creditor block -->
	<xsl:template match="matched.creditor.block">
		<xsl:call-template name ="MatchedCreditorBlock"/>
	</xsl:template>

	<!--  Fed. creditor info-->
	<xsl:template match="creditor">
		<xsl:if test="$pcValue='BKY'">
			<xsl:call-template name="SuperiorCreditorInfo"/>
		</xsl:if>
		<xsl:if test="$pcValue='FBR' or $isChunked">
			<xsl:call-template name ="DocketsCreditorInfo"/>
		</xsl:if>
	</xsl:template>

	<!-- Superior creditor information -->
	<xsl:template name="SuperiorCreditorInfo">
		<xsl:if test="party.name or party.address.block">
			<xsl:call-template name="SuperiorCreditorNameAddress"/>
		</xsl:if>
	</xsl:template>

	<!-- Creditor name and address -->
	<xsl:template name="SuperiorCreditorNameAddress">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_name;'" />
			</xsl:call-template>
			<td>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="firstName" select="party.name"/>
				</xsl:call-template>
				<xsl:apply-templates select ="party.address.block"/>
			</td>
		</tr>
	</xsl:template>

	<!-- Matched creditor block (Superior) information -->
	<xsl:template name="MatchedCreditorBlock">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_name;'" />
			</xsl:call-template>
			<td>
				<xsl:apply-templates select ="creditor/party.name"/>
				<xsl:apply-templates select ="creditor/party.address.block"/>
			</td>
		</tr>
		<xsl:apply-templates select="party.attorney.block"/>
	</xsl:template>

	<!-- Dockets Creditor information -->
	<xsl:template name="DocketsCreditorInfo">
		<xsl:if test="party.name.block or party.address.block">
			<xsl:call-template name="CreditorNameAddress"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="CreditorNameAddress">
		<tr>
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_name;'" />
			</xsl:call-template>
			<td>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="firstName" select="party.name.block/party.name"/>
				</xsl:call-template>
				<xsl:apply-templates select ="party.address.block"/>
			</td>
		</tr>
		<xsl:apply-templates select="party.attorney.block" mode="creditor"/>
	</xsl:template>

	<xsl:template match="party.attorney.block" mode="creditor">
		<xsl:apply-templates select="descendant::attorney.name"/>
		<xsl:apply-templates select="attorney.address.block"/>
		<xsl:apply-templates select="firm.name"/>
		<xsl:apply-templates select="firm.address.block"/>
		<xsl:apply-templates select="descendant::attorney.phone"/>
		<xsl:apply-templates select="descendant::attorney.fax"/>
		<xsl:apply-templates select="descendant::firm.phone"/>
		<xsl:apply-templates select="descendant::firm.fax"/>
	</xsl:template>


	<!-- Docket Proceedings code -->
	<xsl:template match="docket.entry">
		<xsl:if test="not(preceding-sibling::docket.entry)">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_docketProceedings;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates select="number.block/number"/>
			<xsl:apply-templates select="docket.entries.block/date"/>
			<xsl:apply-templates select="docket.description"/>
		</table>
	</xsl:template>

	<xsl:template match="number.block/number">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_entryNum;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="docket.entries.block/date">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_date;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="docket.description">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_description;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="docket.description/text()">
		<xsl:value-of disable-output-escaping="yes" select="."/>
	</xsl:template>

	<xsl:template name="AddlNotes">
		<xsl:call-template name="wrapPublicRecordsDisclaimers">
			<xsl:with-param name="disclaimer1">
				<xsl:text>THE PRECEDING PUBLIC RECORD DATA IS FOR INFORMATION PURPOSES ONLY AND IS NOT THE OFFICIAL RECORD. CERTIFIED COPIES CAN ONLY BE OBTAINED FROM THE OFFICIAL SOURCE.</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="disclaimer2">
				<xsl:text>THE PUBLIC RECORD ITEMS REPORTED ABOVE MAY HAVE BEEN PAID, TERMINATED, VACATED OR RELEASED PRIOR TO TODAY'S DATE.</xsl:text>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>