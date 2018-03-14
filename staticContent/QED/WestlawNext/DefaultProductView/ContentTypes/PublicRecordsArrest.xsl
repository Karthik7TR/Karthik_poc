<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<!-- Do not render these nodes -->
	<xsl:template match="map|p|pc|pubdate|scrp.d|s|legacy.id|book.loc"/>
	<xsl:template match="off.info.b/st.id.nbr"/>
	<xsl:template match="chrg.info.b/cs.hid.nbr"/>
	<xsl:template match="off.info.b/image.block" priority="2"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>
	
	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsArrestClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_arrestRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block"/>
		<xsl:apply-templates select="off.info.b"/>
		<xsl:apply-templates select="arr.info.b"/>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:apply-templates select="crt.info.b"/>
		<xsl:call-template name="ChargeInformation"/>
		<xsl:call-template name="HoldingInformation"/>
		<xsl:call-template name="Message"/>
	</xsl:template>

	<!-- ********************************************************************** 
	***********************  (B) Offender Information  ************************
	************************************************************************-->
	<xsl:template match="off.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_offenderInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="image.block" mode="OffenderInformation"/>
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="nm.b">
		<xsl:choose>
			<xsl:when test="fl.nm != ''">
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="'&pr_offenderName;'"/>
					<xsl:with-param name="firstName" select="fl.nm"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsName">
					<xsl:with-param name="label" select="'&pr_offenderName;'"/>
					<xsl:with-param name="firstName" select="fst.nm"/>
					<xsl:with-param name="middleName" select="mid.nm"/>
					<xsl:with-param name="lastName" select="lst.nm"/>
					<xsl:with-param name="suffixName" select="suf.nm"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_address;'"/>
			<xsl:with-param name="street" select="def.str"/>
			<xsl:with-param name="city" select="def.cty"/>
			<xsl:with-param name="stateOrProvince" select="def.st"/>
			<xsl:with-param name="zip" select="def.zip"/>
			<xsl:with-param name="zipExt" select="def.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="fbi.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fbiNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="st.id.nbr.full">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_offenderId;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="dob">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$BIRTHDATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="age">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_age;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="aka.b[1]">
		<xsl:if test="not(preceding-sibling::aka.b)">
			<xsl:call-template name="wrapPublicRecordsItem">
				<xsl:with-param name="defaultLabel" select="'&pr_akaNames;'"/>
				<xsl:with-param name="selectNodes" select="../aka.b/aka.fl.nm"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="aka.b[position()!=1]"/>
	
	<xsl:template match="aka.fl.nm">
		<div>
			<xsl:call-template name="FormatName">
				<xsl:with-param name="lastName" select="."/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="sex">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_gender;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="race">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_race;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ethnic">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ethnicity;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="eyes">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_eyeColor;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hair">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_hairColor;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="skin">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_skinTone;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bld">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_build;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ht.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_height;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Height In Feet-->
	<xsl:template match="ht.ft">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]>&pr_ft;<![CDATA[ ]]></xsl:text>
	</xsl:template>

	<!--Height In Inches-->
	<xsl:template match="ht.in">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]>&pr_in;</xsl:text>
	</xsl:template>

	<xsl:template match="wt">
		<tr class="&pr_item;">
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="class" select="'&pr_itemKey;'" />
				<xsl:with-param name="contents" select="'&pr_weight;'" />
			</xsl:call-template>
			<td>
				<xsl:choose>
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


	<!-- ********************************************************************** 
	*********************  (C) Arrest Information Block  **********************
	************************************************************************-->
	<xsl:template match="arr.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_arrestInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="arr.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfArrest;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arr.time">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_arrestTime;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arr.loc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_arrestLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="crim.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_offenseCounty;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arr.agcy.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_arrestingAgency;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arr.agcy.txt[preceding-sibling::arr.agcy]">
		<xsl:text><![CDATA[ ]]>-<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="book.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bookingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="book.time">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bookingTime;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="book.loc.txt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bookingLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="book.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bookingNumber;'"/>
		</xsl:call-template>
	</xsl:template>



	<!-- ********************************************************************** 
	*******************  (D) Court & Attorny Information  *********************
	************************************************************************-->
	<xsl:template match="crt.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_courtAndAttorneyInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="court">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_court;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="def.aty.nm">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_attorney;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="def.addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_attorneyAddress;'"/>
			<xsl:with-param name="street" select="def.aty.str"/>
			<xsl:with-param name="city" select="def.aty.cty"/>
			<xsl:with-param name="stateOrProvince" select="def.aty.st"/>
			<xsl:with-param name="zip" select="def.aty.zip"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="def.aty.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_attorneyPhoneNumber;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	**************  (E) Current Charge or Offense Information  ****************
	************************************************************************-->
	<xsl:template name="ChargeInformation">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'" />
			<xsl:with-param name="contents" select="'&pr_currentChargeOrOffenseInfo;'" />
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="chrg.info.b"/>
		</table>
	</xsl:template>

	<xsl:template match="stat.viol">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_statuteCode;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="crim.off">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_offense;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="crim.cls">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_offenseClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="crim.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_offenseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="curr.sts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_currentStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="crim.cnts">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_chargeNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="misc.info.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_chargeComments;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="warr.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_warrantNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bail.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bailAmount;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cs.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_caseNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dock.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_docketNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="nxt.crt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nextCourtDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="nxt.crt.time">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_nextCourtTime;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="disp.fndg">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_disposition;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="proj.rlse.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_projectedReleaseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="act.rlse.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actualReleaseDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="rlse.time">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_actualReleaseTime;'"/>
		</xsl:call-template>
	</xsl:template>


	<!-- ********************************************************************** 
	***********************  (F) Holding Information  *************************
	************************************************************************-->
	<xsl:template name="HoldingInformation">
		<xsl:if test="loc.info.b">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'" />
				<xsl:with-param name="contents" select="'&pr_holdingInformation;'" />
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<xsl:apply-templates select="loc.info.b"/>
		</table>
	</xsl:template>

	<xsl:template match="inst.loc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_housingLocation;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="inst.unt.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_floor;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="inst.cell.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_cell;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="inst.addr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_housingAddress;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="inst.cty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_housingCity;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="image.block" mode="OffenderInformation">
		<tr>
			<td colspan="2">
			<xsl:call-template name="imageBlock">
				<xsl:with-param name="suppressNoImageDisplay" select="true()"/>
			</xsl:call-template>
			</td>
		</tr>
	</xsl:template>
	
	<!--Message-->
	<xsl:template name="Message">
		<xsl:call-template name="wrapPublicRecordsDisclaimers">
			<xsl:with-param name="disclaimer1">
				<xsl:text>The preceding record is for informational purposes only and is not the official record.
					This information is not warranted for accuracy or completeness. For copies of the official record, contact the arresting agency.</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="disclaimer2">
				<xsl:text>This information is not to be used for any purpose regulated by the fair credit reporting	act including employment
					screening or in violation of any local or state law.</xsl:text>
			</xsl:with-param>
		</xsl:call-template>
		<!-- Display of Order Documents Message -->
		<xsl:call-template name="outputOrderDocumentsSection"/>
	</xsl:template>

</xsl:stylesheet>