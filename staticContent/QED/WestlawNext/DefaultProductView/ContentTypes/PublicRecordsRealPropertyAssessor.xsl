<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<!--Real Property Records - Property Assesor-->

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- Do not render these nodes -->
	<xsl:template match="legacy.id|legacyId|col.key|p|pc|c|pre|key|map|s" priority="1"/>
	<xsl:template match="own.na1.b/own.na1.oth.b" priority="1"/>
	<xsl:template match="short.st|yr.sld.st|val.mthd" priority="1"/>

	<xsl:template match="Document" priority="1">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsRealPropertyAssessorClass;'" />
			<xsl:with-param name="dualColumn" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsMainColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="coverageBeginDateLabel" select="'&pr_taxRollCertificationDate;'"/>
			<xsl:with-param name="currentThroughDateLabel" select="'&pr_ownerInformationCurrentThrough;'"/>
			<xsl:with-param name="databaseLastUpdatedLabel" select="'&pr_countyLastUpdated;'"/>
			<xsl:with-param name="displayUpdateFrequency" select="false()"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="n-docbody/r"/>
	</xsl:template>

	<!--Document Header-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_realPropertyTaxAssessorRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsEpilog"> 
		<xsl:call-template name="RPAssessorSearch">
			<xsl:with-param name="novusQuery" select="n-docbody/r/rpts/link[@key='RPAddProp']/@retrv.qry"/>
			<xsl:with-param name="linkContents" select="'ADDITIONAL PROPERTIES POSSIBLY CONNECTED TO OWNER may have been located. The owner’s mailing address is associated with other properties as indicated by tax assessor records. Additional charges may apply.'"/>
		</xsl:call-template>

		<xsl:call-template name="RPTransactionSearch">
			<xsl:with-param name="novusQuery" select="n-docbody/r/rpts/link[@key='RPHistory']/@retrv.qry"/>
			<xsl:with-param name="linkContents" select="'TRANSACTION HISTORY REPORT may be available for this property. The report contains details about all available transactions associated with this property. The report may include information about sales, ownership transfers, refinances, construction loans, 2nd mortgages, or equity loans based on recorded deeds. Additional charges may apply.'"/>
		</xsl:call-template>
	</xsl:template>		

	<xsl:template match="s/fip.state.cd">
		<xsl:if test="preceding-sibling::fip.cd">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="r">
		<xsl:apply-templates select="*[(name() != 'rpts') and (name() != 'pre')]"/>
		<xsl:apply-templates select="rpts"/>
	</xsl:template>

	<!-- OWNER INFORMATION -->
	<xsl:template match="own.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_ownerInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="own.na1.b/own.na1">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_owners;'"/>
			<xsl:with-param name="selectNodes" select="following-sibling::own.na1.oth.b/own.etal.ind"/>
		</xsl:call-template>
	</xsl:template>

	<!--Other owners-->
	<xsl:template match="own.na2.b/own.na2">
		<xsl:call-template name="wrapPublicRecordsItem"/>
	</xsl:template>

	<xsl:template match="own.rltn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ownerRelationship;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="own.rt.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ownershipRights;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="own.cd.ind">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_corporateOwner;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="abst.own">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_absenteeOwner;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="prp.add.b">
			<xsl:call-template name="wrapPublicRecordsAddress">
				<xsl:with-param name="label" select="'&pr_propertyAddress;'"/>
				<xsl:with-param name="fullStreet" select="prp.full.str.add"/>
				<xsl:with-param name="streetNum" select="prp.hs.num"/>
				<xsl:with-param name="street" select="prp.str.na"/>
				<xsl:with-param name="streetSuffix" select="prp.md"/>
				<xsl:with-param name="city" select="prp.cty"/>
				<xsl:with-param name="stateOrProvince" select="prp.st"/>
				<xsl:with-param name="zip" select="prp.zip"/>
				<xsl:with-param name="zipExt" select="prp.zip.ext"/>
			</xsl:call-template>
	</xsl:template>

	<xsl:template match="mail.add.b">
		<xsl:if test="mail.full.str.add != 'N/AVAIL'">
			<xsl:call-template name="wrapPublicRecordsAddress">
				<xsl:with-param name="label" select="'&pr_mailingAddress;'"/>
				<xsl:with-param name="fullStreet" select="mail.full.str.add"/>
				<xsl:with-param name="streetNum" select="mail.hs.num"/>
				<xsl:with-param name="street" select="mail.str.na"/>
				<xsl:with-param name="streetSuffix" select="mail.md"/>
				<xsl:with-param name="city" select="mail.cty"/>
				<xsl:with-param name="stateOrProvince" select="mail.st"/>
				<xsl:with-param name="zip" select="mail.zip"/>
				<xsl:with-param name="zipExt" select="mail.zip.ext"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="own.phn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="own.etal.ind">
		<xsl:text>,<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!--PROPERTY INFORMATION-->
	<xsl:template match="prp.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_propertyInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="fip.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="prp.ind">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_propertyType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="land.use">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_landUse;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mun.na.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_municipality;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sdiv.trct.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_tractOrSubdivisionNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sdiv.na">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_subdivision;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="zon">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_zoning;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="land.sq.ft">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lotSize;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="acs">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lotAcreage;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fr.ft">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_widthFootage;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dp.ft">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_depthFootage;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="loc.info">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_locationAttributes;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lgl.desc1.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_legalDescription;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lot.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lotNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="blk.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_blockNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="rng">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_range;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="twnshp">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_township;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sect">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_section;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="qtr.sect">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_quarterSection;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hmstd.exmpt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_homesteadExempt;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="view">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_propertyView;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sdiv.plt.bk.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_platRecording;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="orig.rec.bk.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_originalRecording;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Only display the Assessor's Parcel Number once based on which one is given -->
	<xsl:template match="orig.apn.b">
		<xsl:variable name="assessorsParcelNumber">
			<xsl:text>&pr_assessorsParcelNumber;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$assessorsParcelNumber"/>
			<xsl:with-param name="selectNodes" select="fmt.apn | unft.apn | orig.apn | acct.num"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="unft.apn[preceding-sibling::fmt.apn or following-sibling::fmt.apn]"/>

	<xsl:template match="orig.apn[preceding-sibling::fmt.apn or following-sibling::fmt.apn 
													or preceding-sibling::unft.apn or following-sibling::unft.apn]"/>
	
	<xsl:template match="acct.num[preceding-sibling::fmt.apn or following-sibling::fmt.apn 
													or preceding-sibling::unft.apn or following-sibling::unft.apn
													or preceding-sibling::orig.apn or following-sibling::orig.apn]"/>
	
	<!-- For this content type, always append a space after a text node if it
			 has other siblings after it that contain text. -->
	<xsl:template match="text()[../following-sibling::*/text()]" priority="1">
		<xsl:if test="normalize-space(.)">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="normalize-space(.)"/>
			</xsl:call-template>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Since cities have a comma inserted after them, don't let the text node
				 get processed and simply take the text. -->
	<xsl:template match="mail.cty | hist.mail.cty | hist.prp.cty | prp.cty">
		<xsl:call-template name="SpecialCharacterTranslator">
			<xsl:with-param name="textToTranslate" select="normalize-space(text())"/>
		</xsl:call-template>
	</xsl:template>

	<!--TAX ASSESSMENT INFORMATION-->
	<xsl:template match="tax.assr.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_taxAssessmentInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="tax.yr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxYear;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="est.tax.yr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_estimatedTaxYear;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="land.val.cal">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_calculatedLandValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="imp.val.cal">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_calculatedImprovementValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cal.tot.val">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_calculatedTotalValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="land.val.assd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_assessedLandValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="imp.val.assd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_assessedImprovementValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="assd.tot.val">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_assessedTotalValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="land.val.mkt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_marketLandValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="imp.val.mkt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_marketImprovementValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mkt.tot.val">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_marketTotalValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="land.val.appr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_appraisedLandValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="imp.val.appr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_appraisedImprovementValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="appr.tot.val">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_appraisedTotalValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="tot.val.cal.ind">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_valuationMethod;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="tax.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="tax.cd.ar">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxCodeArea;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--BUILDING/IMPROVEMENT CHARACTERISTICS-->
	<xsl:template match="bldg.imp.char.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_buildingOrImprovementCharacteristics;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="engy.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_electricity;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="wtr.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_water;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sew.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sewer;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="num.bldg">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfBuildings;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="yr.blt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_yearBuilt;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bed.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfBedrooms;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lvng.sq.ft">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_livingSquareFeet;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="tot.bath">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfBathrooms;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="full.bath">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fullBaths;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="frplc.ind">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fireplace;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="grge.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_garageType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sty.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfStories;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ext.wall">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_exteriorWallType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fnd.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_foundationType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ht.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_heat;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="air.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_airConditioningType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bldg.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_buildingType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lot.ar">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalArea;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="tot.rms">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_totalNumberOfRooms;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bsmt.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_basementType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bsmt.sq.ft">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_basementSquareFeet;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="prkg.spc.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_parkingSpaces;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pool.ind">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_pool;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pool.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_poolType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="unt.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfUnits;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bldg.style">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_styleOrShape;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bldg.imp.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_improvementType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cons.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_constructionType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="qlty.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_constructionQuality;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="roof.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_roofType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fuel.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_fuel;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--LAST SALE INFORMATION-->
	<xsl:template match="last.sl.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_lastMarketSaleInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="sl.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_saleDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sell.na">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_sellerName;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sl.prc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_salePrice;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sls.de.cat.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_deedType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sls.trn.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_typeOfSale;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort1.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort1.trm.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageTerm;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fac.mort1.ln.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageLoanType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="int.rate.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_interestRate;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort1.de.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageDeedType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lend1.na">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lenderName;'"/>
			<xsl:with-param name="nodeType" select="$COMPANY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mlt.apn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_multipleParcelSale;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="r.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_recordingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="doc.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_documentNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ti.co.na">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_titleCompany;'"/>
			<xsl:with-param name="nodeType" select="$COMPANY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bk.pg.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_recordingBookOrPage;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sl.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_consideration;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort2.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_secondMortgageAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort2.ln.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_secondMortgageType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mort2.de.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_secondDeedType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mlt.apn.cnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfParcels;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--PREVIOUS TRANSACTION INFORMATION-->
	<xsl:template match="prev.trn.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_previousTransactionInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="pr.sl.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_saleDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pr.sl.prc">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_salePrice;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pr.sl.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_consideration;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pr.doc.num">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_previousDocumentNumber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pr.de.t">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_deedType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pr.sls.trn.cd">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_typeOfSale;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pr.mort.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_mortgageAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pr.mlt.apn">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_multipleParcelSale;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pr.mlt.apn.cnt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_numberOfParcels;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pr.r.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_recordingDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pr.bk.pg.b">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_recordingBookOrPage;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--HISTORICAL TAX ASSESSOR INFORMATION-->
	<xsl:template match="hist.tax.assr.b">
		<xsl:variable name="count">
			<xsl:number />
		</xsl:variable>

		<xsl:if test="$count = 1">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_historicalTaxAssessorInformation;'"/>
			</xsl:call-template>
		</xsl:if>
		<table class="&pr_table;">
			<tr>
				<th>
					<xsl:text>&pr_historicalTaxAssessorRecord; </xsl:text>
					<xsl:value-of select="$count"/>
					<xsl:text>.</xsl:text>
				</th>
				<th></th>
			</tr>
			<xsl:apply-templates/>
		</table>
	</xsl:template>

	<xsl:template match="hist.tax.yr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxYear;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.est.tax.yr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_estimatedTaxYear;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.land.val.cal">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_calculatedLandValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.imp.val.cal">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_calculatedImprovementValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.cal.tot.val">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_calculatedTotalValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.assd.tot.val">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_assessedTotalValue;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.tax.amt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_taxAmount;'"/>
			<xsl:with-param name="nodeType" select="$CURRENCY"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Only display the Assessor's Parcel Number once based on which one is given -->
	<xsl:template match="hist.assr.prcl.b">
		<xsl:variable name="assessorsParcelNumber">
			<xsl:text>&pr_assessorsParcelNumber;</xsl:text>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$assessorsParcelNumber"/>
			<xsl:with-param name="selectNodes" select="hist.fmt.apn | hist.unft.apn | hist.orig.apn"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.unft.apn[preceding-sibling::hist.fmt.apn or following-sibling::hist.fmt.apn]"/>

	<xsl:template match="hist.orig.apn[preceding-sibling::hist.fmt.apn or following-sibling::hist.fmt.apn
															 or preceding-sibling::hist.unft.apn or following-sibling::hist.unft.apn]"/>
	
	<xsl:template match="hist.hmstd.exmpt">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_homesteadExempt;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.abst.own">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_absenteeOwner;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.own.b">
		<tr>
			<th>&pr_owner;</th>
			<td>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="firstName" select="hist.own.na1"/>
				</xsl:call-template>
				<xsl:if test="hist.own.na2">
					<xsl:text>,<![CDATA[ ]]></xsl:text>
					<xsl:call-template name="FormatName">
						<xsl:with-param name="firstName" select="hist.own.na2"/>
					</xsl:call-template>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="hist.prop.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_propertyAddress;'"/>
			<xsl:with-param name="fullStreet" select="hist.prp.full.str.add"/>
			<xsl:with-param name="streetNum" select="hist.prp.hs.num"/>
			<xsl:with-param name="street" select="hist.prp.str.na"/>
			<xsl:with-param name="streetSuffix" select="hist.prp.md"/>
			<xsl:with-param name="city" select="hist.prp.cty"/>
			<xsl:with-param name="stateOrProvince" select="hist.prp.st"/>
			<xsl:with-param name="zip" select="hist.prp.zip"/>
			<xsl:with-param name="zipExt" select="hist.prp.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hist.mail.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="label" select="'&pr_mailingAddress;'"/>
			<xsl:with-param name="fullStreet" select="hist.mail.full.str.add"/>
			<xsl:with-param name="streetNum" select="hist.mail.hs.num"/>
			<xsl:with-param name="street" select="hist.mail.str.na"/>
			<xsl:with-param name="streetSuffix" select="hist.mail.md"/>
			<xsl:with-param name="city" select="hist.mail.cty"/>
			<xsl:with-param name="stateOrProvince" select="hist.mail.st"/>
			<xsl:with-param name="zip" select="hist.mail.zip"/>
			<xsl:with-param name="zipExt" select="hist.mail.zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!--Format book-->
	<xsl:template match="bk|pr.bk|sdiv.plt.bk|orig.rec.bk ">
		<xsl:text>&pr_book;<![CDATA[ ]]></xsl:text>
		<!-- Call SpecialCharacterTranslator separately such that no space is inserted afterward. -->
		<xsl:call-template name="SpecialCharacterTranslator">
			<xsl:with-param name="textToTranslate" select="normalize-space(.)"/>
		</xsl:call-template>
	</xsl:template>

	<!--Format Page-->
	<xsl:template match="pg|pr.pg|sdiv.plt.pg|orig.rec.pg">
		<xsl:if test="(name(preceding-sibling::*[1])= 'bk') or (name(preceding-sibling::*[1])='pr.bk') or (name(preceding-sibling::*[1])='sdiv.plt.bk') or (name(preceding-sibling::*[1])='orig.rec.bk')">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:text>&pr_page;<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>
	
</xsl:stylesheet>
