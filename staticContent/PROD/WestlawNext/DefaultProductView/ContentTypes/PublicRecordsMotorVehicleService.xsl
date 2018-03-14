<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Do not render these nodes -->
	<xsl:template match="legacy.id|col.key|p|pc"/>

	<!--  apply templates to just n-docbody (not CoverageData) as well -->
	<xsl:template match="n-docbody">
		<xsl:apply-templates select="r"/>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsMotorVehicleServiceClass;'"/>
			<xsl:with-param name="dualColumn" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'"/>
			<xsl:with-param name="contents" select="'&pr_motorVehicleServiceWarrantyRecord;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsMainColumn">
		<!--  Source Information will come later
		<xsl:apply-templates select="$coverage-block"/>		  -->
		<xsl:apply-templates select="vehicle.serv.rec/veh.info.b"/>
		<xsl:apply-templates select="vehicle.serv.rec/trn.info.b"/>
		<xsl:apply-templates select="vehicle.serv.rec/person.b"/>
	</xsl:template>

	<!--************************************************************************
	*********************** Vehicle Information ****************************
	*************************************************************************-->
	<xsl:template match="veh.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_vehicleInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="vin"/>
			<xsl:apply-templates select="model.yr"/>
			<xsl:apply-templates select="make"/>
			<xsl:apply-templates select="body.style"/>
			<xsl:call-template name="ModelSeries">
				<xsl:with-param name="Model" select="model"/>
				<xsl:with-param name="Series" select="series"/>
			</xsl:call-template>
		</table>
	</xsl:template>

	<xsl:template match="vin">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vin;'"/>
			<xsl:with-param name="nodeType" select="$VINNUMBER"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="model.yr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_modelYear;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="make">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_make;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="body.style">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_bodyStyle;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="ModelSeries">
		<xsl:param name="Model" select="/.."/>
		<xsl:param name="Series" select="/.."/>
		<xsl:if test="$Model or $Series">
			<tr class="&pr_item;">
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="contents" select="'&pr_modelSeries;'"/>
				</xsl:call-template>
				<td>
					<xsl:if test="$Model">
						<xsl:apply-templates select="$Model"/>
					</xsl:if>
					<xsl:if test="$Series">
						<xsl:if test="$Model">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
						<xsl:apply-templates select="$Series"/>
					</xsl:if>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!--************************************************************************
	*********************** Transaction Information ****************************
	*************************************************************************-->
	<xsl:template match="trn.info.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_transactionInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="first.rpt.d"/>
			<xsl:apply-templates select="last.rpt.d"/>
			<xsl:apply-templates select="purch.typ"/>
			<xsl:apply-templates select="lien.stat"/>
			<xsl:apply-templates select="veh.mileage"/>
		</table>
	</xsl:template>

	<xsl:template match="first.rpt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_firstReportedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="last.rpt.d">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_lastReportedDate;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="purch.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_autoPurchaseType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lien.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_autoLienStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="veh.mileage">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_vehicleMileage;'"/>
			<xsl:with-param name="nodeType" select="$NUMBER"/>
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	****************** Associated Person Information ********************
	*************************************************************************-->
	<xsl:template match="person.b">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_subheader;'"/>
			<xsl:with-param name="contents" select="'&pr_associatedPersonInformation;'"/>
		</xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="name.b"/>
			<xsl:apply-templates select="dob"/>
			<xsl:apply-templates select="mar.stat"/>
			<xsl:apply-templates select="addr.b"/>
			<xsl:variable name="addressType">
				<xsl:call-template name="mapAddressTypeValue">
					<xsl:with-param name="mapKey" select="addr.b/addr.typ"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:call-template name="displayLabelValue">
				<xsl:with-param name="label" select="'&pr_addressType;'"/>
				<xsl:with-param name="value" select="$addressType"/>
			</xsl:call-template>
			<xsl:apply-templates select="ncoa.del.typ"/>
			<xsl:apply-templates select="own.stat"/>
			<xsl:apply-templates select="addr.b/addr.cnty"/>
			<xsl:apply-templates select="phn.b/phn.nbr"/>
			<xsl:apply-templates select="phn.b/m.phn.nbr"/>
			<xsl:apply-templates select="phn.b/m.phn.carrier"/>
			<xsl:apply-templates select="email.b/email/full.email"/>
		</table>
	</xsl:template>

	<xsl:template name="mapAddressTypeValue">
		<xsl:param name="mapKey"/>
		<xsl:choose>
			<xsl:when test="$mapKey = 'F'">ZIP+4 MATCH ON THE COMPANY NAME</xsl:when>
			<xsl:when test="$mapKey = 'G'">GENERAL DELIVERY RECORD</xsl:when>
			<xsl:when test="$mapKey = 'H'">HIGH RISE RECORD</xsl:when>
			<xsl:when test="$mapKey = 'P'">PO BOX RECORD</xsl:when>
			<xsl:when test="$mapKey = 'R'">RURAL ROUTE RECORD</xsl:when>
			<xsl:when test="$mapKey = 'S'">STREET RECORD</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="$mapKey"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="name.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="firstName" select="first"/>
			<xsl:with-param name="middleName" select="middle"/>
			<xsl:with-param name="lastName" select="last"/>
			<xsl:with-param name="suffixName" select="suffix"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="dob">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_dateOfBirth;'"/>
			<xsl:with-param name="nodeType" select="$DATE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="mar.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_maritalStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="fullStreet" select="full.str"/>
			<xsl:with-param name="city" select="city"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ncoa.del.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ncoaDeliveryType;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="own.stat">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_ownershipStatus;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="addr.cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="phn.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_phone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="m.phn.nbr">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_cellPhone;'"/>
			<xsl:with-param name="nodeType" select="$PHONE"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="m.phn.carrier">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_cellPhoneCarrier;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="full.email">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_emailAddress;'"/>
		</xsl:call-template>

	</xsl:template>
</xsl:stylesheet>
