<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0"	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsStringUtilities.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="BusinessInvestigatorName.xsl"/>
	
	
	<!--NPI - National Provider Identifier Registry-->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- Do not render these nodes -->
	<xsl:template match="prim.cnty.cd | leg.immed.no | leg.ult.no | aff.immed.no | aff.ult.no |aff.glob.ult.ind | aff.par.ind | aff.link.ind |
												leg.ult.ind | leg.par.ind | leg.link.ult.ind | leg.entity.ind | efx.id | new.efx.id | efx.delta"/>

	<!-- Render the CONTENT view. -->
	<xsl:template match="Document">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsNationalProviderIdentifierClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_nationalProviderIdentifierRegistryRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
			<xsl:with-param name="displaySource" select="false()"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="n-docbody/r/provider.b"/>
		<xsl:apply-templates select="n-docbody/r/auth.off.b"/>
		<xsl:apply-templates select="n-docbody/r/npi.b"/>
		<xsl:apply-templates select="n-docbody/r/mail.addr.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="n-docbody/r/prac.addr.b"/>
		<xsl:if test ="count(n-docbody/r/lic.b) > 0">
			<xsl:call-template name="lic_b"/>
		</xsl:if>
		<xsl:if test ="count(n-docbody/r/oth.prov.b) > 0">
			<xsl:call-template name="oth_prov_b"/>
		</xsl:if>
	</xsl:template>

	<!-- ********************************************************************** 
	*******Provider Information section  **************************************
	************************************************************************-->

	<xsl:template match ="provider.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_providerInformation;'"/>
		</xsl:call-template>

		<table class="&pr_table;">
			<!--Individual display-->
			<xsl:apply-templates select="name.b"/>
			<xsl:apply-templates select="oth.l.nm.typ"/>
			<xsl:apply-templates select="gender"/>
			<xsl:apply-templates select="sole.prop"/>

			<!--Organization display-->
			<xsl:apply-templates select="org"/>
			<xsl:apply-templates select="nm.typ.descr"/>
			<xsl:apply-templates select="par.org"/>
			<xsl:apply-templates select="org.subpart"/>
		</table>
	</xsl:template>

	<!-- Provider name information-->
	<!--Start Individual template-->
	<xsl:template match="name.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="prefixName" select="nm.pre"/>
			<xsl:with-param name="firstName" select="f.nm"/>
			<xsl:with-param name="middleName" select="m.nm"/>
			<xsl:with-param name="lastName" select="l.nm"/>
			<xsl:with-param name="suffixName" select="nm.suf"/>
			<xsl:with-param name="professionalSuffixName" select="following-sibling::creds"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Provider Professional Name information-->
	<xsl:template match="oth.l.nm.typ">
		<xsl:variable name="label">
			<xsl:call-template name="splitAndFixCase">
				<xsl:with-param name="string" select="."/>
			</xsl:call-template>
			<xsl:text>:</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="prefixName" select="following-sibling::alias.name.b/alias.nm.pre"/>
			<xsl:with-param name="firstName" select="following-sibling::alias.name.b/alias.f.nm"/>
			<xsl:with-param name="middleName" select="following-sibling::alias.name.b/alias.m.nm"/>
			<xsl:with-param name="lastName" select="following-sibling::alias.name.b/alias.l.nm"/>
			<xsl:with-param name="suffixName" select="following-sibling::alias.name.b/alias.nm.suf"/>
			<xsl:with-param name="professionalSuffixName" select="following-sibling::other.creds"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Provider Gender information-->
	<xsl:template match="gender">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Sole Proprietor information-->
	<xsl:template match="sole.prop">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_soleProprietor;'"/>
		</xsl:call-template>
	</xsl:template>
	<!--End Individual Template-->

	<!--Start Organization template-->

	<!--Organization Name(LBN)-->
	<xsl:template match="org">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_organizationNameLbn;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Former Legal Business Name, Doing Business As or Other Name-->
	<xsl:template match="nm.typ.descr">
		<xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>
		<xsl:if test="normalize-space(following-sibling::alias.org)">
			<tr>
				<th>
					<xsl:call-template name="splitAndFixCase">
						<xsl:with-param name="string" select="."/>
					</xsl:call-template>
					<xsl:text>:</xsl:text>
				</th>
				<td>
					<xsl:choose>
						<!--xsl:when test="$searchableLink='nope' or $searchableLink='nope'"-->
						<xsl:when test="$searchableLink='true' or $searchableLink='TRUE'">
							<xsl:call-template name="CreateLinkedBusiness">
								<xsl:with-param name="companyName" select="following-sibling::alias.org"/>
								<xsl:with-param name="searchableLink" select="$searchableLink"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="following-sibling::alias.org"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!--Parent Organization Name(LBN)-->
	<xsl:template match="par.org">
		<xsl:call-template name="wrapBusinessInvestigatorName">
			<xsl:with-param name="label" select="'&pr_parentOrganizationNameLbn;'"/>
			<xsl:with-param name="companyName" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!--Organization Subpart-->
	<xsl:template match="org.subpart">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_organizationSubpart;'"/>
		</xsl:call-template>
	</xsl:template>
	<!--End Organization Template-->

	<!-- ********************************************************************** 
	*******NPI Information section  *******************************************
	************************************************************************-->

	<xsl:template match="npi.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_nationalProviderIdentifierInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="npi.no"/>
			<xsl:apply-templates select="entity.type"/>
			<xsl:apply-templates select="npi.issue.d"/>
			<xsl:apply-templates select="lst.upd.d"/>
			<xsl:apply-templates select="repl.npi.no"/>
			<xsl:apply-templates select="deact.cd"/>
			<xsl:apply-templates select="deact.d"/>
			<xsl:apply-templates select="react.d"/>
		</table>
	</xsl:template>

	<!-- NPI-->
	<xsl:template match="npi.no">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nationalProviderIdentifier;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Entity Type-->
	<xsl:template match="entity.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_entityType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Enumeration Date-->
	<xsl:template match="npi.issue.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_enumerationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Last Update Date-->
	<xsl:template match="lst.upd.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastUpdateDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Replacement NPI-->
	<xsl:template match="repl.npi.no">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_replacementNationalProviderIdentifier;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Deactivation Reason-->
	<xsl:template match="deact.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_deactivationReason;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Deactivation Date-->
	<xsl:template match="deact.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_deactivationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!--Reactivation Date-->
	<xsl:template match="react.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_reactivationDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******Provider Business Mailing Address section  *************************
	************************************************************************-->

	<xsl:template match="mail.addr.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_providerBusinessMailingAddress;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:if test="normalize-space(mail.str1) or normalize-space(mail.str2) or normalize-space(mail.cty)
										or normalize-space(mail.st) or normalize-space(mail.zip.b/mail.zip) or normalize-space(mail.cntry)">
				<xsl:call-template name="wrapPublicRecordsAddress">
					<xsl:with-param name="label" select="'&pr_address;'"/>
					<xsl:with-param name="street" select="mail.str1"/>
					<xsl:with-param name="streetLineTwo" select="mail.str2"/>
					<xsl:with-param name="city" select="mail.cty"/>
					<xsl:with-param name="stateOrProvince" select="mail.st"/>
					<xsl:with-param name="zip" select="mail.zip.b/mail.zip"/>
					<xsl:with-param name="zipExt" select="mail.zip.b/mail.zip.ext"/>
					<xsl:with-param name="country" select="mail.cntry"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:apply-templates select="mail.phone"/>
			<xsl:apply-templates select="mail.fax"/>
		</table>
	</xsl:template>

	<!-- Mailing Address Phone Number / Authorized Official Phone Number / Location Address Phone Number-->
	<xsl:template match="mail.phone | off.phone | prac.phone">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phoneNumber;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Mailing Address Fax Number / Authorized Official Fax Number / Location Address Fax Number -->
	<xsl:template match="mail.fax | off.fax | prac.fax">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_faxNumber;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******Authorized Official Information  ***********************************
	************************************************************************-->
	<xsl:template match="auth.off.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_authorizedOfficialInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="off.name.b"/>
			<xsl:apply-templates select="off.title"/>
			<xsl:apply-templates select="off.phone"/>
			<xsl:apply-templates select="off.fax"/>
		</table>
	</xsl:template>

	<!--Authorized Official Name-->
	<xsl:template match="off.name.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="prefixName" select="off.nm.pre"/>
			<xsl:with-param name="firstName" select="off.f.nm"/>
			<xsl:with-param name="middleName" select="off.m.nm"/>
			<xsl:with-param name="lastName" select="off.l.nm"/>
			<xsl:with-param name="suffixName" select="off.nm.suf"/>
			<xsl:with-param name="professionalSuffixName" select="off.cred.txt"/>
		</xsl:call-template>
	</xsl:template>

	<!--Title/Position-->
	<xsl:template match="off.title">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_titleOrPosition;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******Provider Business Practice Location Address section  ***************
	************************************************************************-->

	<xsl:template match="prac.addr.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_providerBusinessPracticeLocationAddress;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:call-template name="wrapPublicRecordsAddress">
				<xsl:with-param name="label" select="'&pr_address;'"/>
				<xsl:with-param name="street" select="prac.str1"/>
				<xsl:with-param name="streetLineTwo" select="prac.str2"/>
				<xsl:with-param name="city" select="prac.cty"/>
				<xsl:with-param name="stateOrProvince" select="prac.st"/>
				<xsl:with-param name="zip" select="prac.zip.b/prac.zip"/>
				<xsl:with-param name="zipExt" select="prac.zip.b/prac.zip.ext"/>
				<xsl:with-param name="country" select="prac.cntry"/>
			</xsl:call-template>
			<xsl:apply-templates select="prac.phone"/>
			<xsl:apply-templates select="prac.fax"/>
		</table>
	</xsl:template>

	<!-- ********************************************************************** 
	*******Provider Taxonomy section  *****************************************
	************************************************************************-->

	<xsl:template name ="lic_b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_providerTaxonomy;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:for-each select ="n-docbody/r/lic.b">
				<xsl:apply-templates select="taxon.b"/>
				<xsl:apply-templates select="lic.st"/>
				<xsl:apply-templates select="lic.no"/>
			</xsl:for-each>
		</table>
	</xsl:template>

	<!--Selected Taxonomy-->
	<xsl:template match="taxon.b">
		<xsl:apply-templates select="prim.taxon"/>
		<tr>
			<th>
				<xsl:text>&pr_selectedTaxonomy;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates select="taxon.cd"/>
				<xsl:for-each select="tax.prov | tax.class | tax.spec">
					<xsl:if test =".">
						<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
						<xsl:apply-templates select="."/>
					</xsl:if>
				</xsl:for-each>
			</td>
		</tr>
	</xsl:template>

	<!--Primary Taxonomy-->
	<xsl:template match="prim.taxon">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_primaryTaxonomy;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Provider Taxonomy State-->
	<xsl:template match="lic.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_state;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Provider Taxonomy License Number-->
	<xsl:template match="lic.no">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_licenseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ********************************************************************** 
	*******Other Provider Identifier section  *********************************
	************************************************************************-->

	<xsl:template name ="oth_prov_b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_otherProviderInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:for-each select="n-docbody/r/oth.prov.b">
				<xsl:apply-templates select="iss.type"/>
				<xsl:apply-templates select="iss.id.no"/>
				<xsl:apply-templates select="iss.st"/>
				<xsl:apply-templates select="issuer"/>
			</xsl:for-each>
		</table>
	</xsl:template>

	<!--Issuer Type-->
	<xsl:template match="iss.type">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_issuerType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Number-->
	<xsl:template match="iss.id.no">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_number;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Other Provider Identifier State-->
	<xsl:template match="iss.st">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_state;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Issuer-->
	<xsl:template match="issuer">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_issuer;'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
