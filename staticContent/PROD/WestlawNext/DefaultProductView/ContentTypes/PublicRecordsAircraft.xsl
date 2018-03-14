<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="Copyright.xsl"/>
  <xsl:include href="PublicRecords.xsl"/>
  <xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <!-- Fullpath to the value of md.document.guid -->
  <xsl:variable name="haveRegion" select="/Document/n-docbody/r/registnt.info/reg.region"/>

  <!-- Do not render these nodes -->
  <xsl:template match="map|p|pc|c|s|col.key|n-folder|n-docref|n-view|pre|flag|party.id.nbr|reg.region"/>


  <xsl:template match="Document" priority="1">
    <xsl:call-template name="PublicRecordsContent">
      <xsl:with-param name="container" select="'&contentTypePublicRecordsAircraftClass;'" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="PublicRecordsHeader">
    <xsl:call-template name="wrapPublicRecordsSection">
      <xsl:with-param name="class" select="'&pr_header;'" />
      <xsl:with-param name="contents" select="'&pr_aircraftRecord;'" />
		</xsl:call-template>
  </xsl:template>

  <xsl:template name="PublicRecordsLeftColumn">
    <!-- Render the "Coverage" section -->
    <xsl:apply-templates select="$coverage-block">
      <xsl:with-param name="displayCoverageBeginDate" select="false()"/>
	  <xsl:with-param name="updateFrequencyValue" select="'&pr_monthly;'"/>
    </xsl:apply-templates>
	  
    <xsl:if test="not(count(descendant::registnt.info/*) = 1 and
              descendant::registnt.info/reg.region)">
      <xsl:apply-templates select="descendant::registnt.info"/>
    </xsl:if>
  </xsl:template>

  <xsl:template name="PublicRecordsRightColumn">
    <xsl:apply-templates select="descendant::regis.info"/>
    <xsl:if test="not(descendant::regis.info) and $haveRegion">
      <xsl:call-template name="RegionLimitByField"/>
    </xsl:if>
    <xsl:apply-templates select="descendant::aircrft.info"/>
  </xsl:template>


  <!-- **********************************************************************
	*************************** Registrant Information *************************
	************************************************************************-->
  <xsl:template match="registnt.info">
    <xsl:call-template name="wrapPublicRecordsSection">
      <xsl:with-param name="class" select="'&pr_subheader;'"/>
      <xsl:with-param name="contents" select="'&pr_registrantInformation;'"/>
    </xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="reg.name"/>
			<xsl:apply-templates select="addr.b"/>
			<xsl:apply-templates select="cnty.mail"/>
			<xsl:apply-templates select="cntry.mail"/>
			<xsl:apply-templates select="frac.own"/>
			<xsl:apply-templates select="other.own.na"/>
		</table>
  </xsl:template>

  <!--Registration:Name-->
  <xsl:template match="reg.name">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="."/>
		</xsl:call-template>
  </xsl:template>
  
  <!--Registration:Address-->
  <xsl:template match="addr.b">
    <xsl:call-template name="wrapPublicRecordsAddress">
      <xsl:with-param name="defaultLabel" select="'&pr_address;'"/>
      <xsl:with-param name="street" select="str.1"/>
      <xsl:with-param name="streetLineTwo" select="str.2"/>
      <xsl:with-param name="city" select="reg.cty"/>
      <xsl:with-param name="stateOrProvince" select="reg.st"/>
      <xsl:with-param name="zip" select="reg.zip"/>
    </xsl:call-template>
  </xsl:template>

  <!--Registration:County-->
  <xsl:template match="cnty.mail">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Registration:Country-->
  <xsl:template match="cntry.mail">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_country;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Registration:Fractional Ownership-->
  <xsl:template match="frac.own">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_fractionalOwnership;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Registration:Additional Registrant Names-->
  <xsl:template match="other.own.na">
    <xsl:if test=". != '' and . != ','">
      <tr>
        <xsl:choose>
          <xsl:when test="preceding-sibling::*[1]/self::other.own.na">
						<td></td>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="wrapWithTableHeader">
              <xsl:with-param name="contents" select="'&pr_additionalRegistrantNames;'" />
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
        <td>
          <xsl:if test="descendant::N-HIT">
            <a>
              <xsl:attribute name="name">
                <xsl:text>SR;</xsl:text>
                <xsl:value-of select="descendant::N-HIT/@n-wordpos" />
              </xsl:attribute>
            </a>
          </xsl:if>
          <xsl:apply-templates />
        </td>
      </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template match="other.own.na/text()">
    <xsl:variable name="otherownna" select="." />
    <xsl:choose>
      <xsl:when test="contains($otherownna,',')">
        <xsl:value-of select="substring-after($otherownna,',')" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$otherownna"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates />
  </xsl:template>

  <!--************************** Registration Information  ********************
	************************* Region - Limit By Field ONLY *****************-->
  <xsl:template name="RegionLimitByField">
    <xsl:call-template name="wrapPublicRecordsSection">
      <xsl:with-param name="class" select="'&pr_subheader;'"/>
      <xsl:with-param name="contents" select="'&pr_registrationInformation;'"/>
    </xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="descendant::registnt.info/reg.region"/>
		</table>
  </xsl:template>

  <!-- ********************************************************************** 
	************************  Registration Information  ***********************
	************************************************************************-->
  <xsl:template match="regis.info">
    <xsl:call-template name="wrapPublicRecordsSection">
      <xsl:with-param name="class" select="'&pr_subheader;'"/>
      <xsl:with-param name="contents" select="'&pr_registrationInformation;'"/>
    </xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="type.reg"/>
			<xsl:apply-templates select="n.nbr"/>
			<xsl:apply-templates select="ancestor::node()/registnt.info/reg.region"/>
			<xsl:apply-templates select="stat.cd"/>
			<xsl:apply-templates select="last.act.d"/>
			<xsl:apply-templates select="cert.iss.d"/>
		</table>
  </xsl:template>

  <!--Registration:Registration Type-->
  <xsl:template match="type.reg">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_registrationType;'"/>
    </xsl:call-template>
  </xsl:template>


  <!--Registration:Mark (N-Num)-->
  <xsl:template match="n.nbr">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_registrationMark;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Registration:FFA Region-->
  <xsl:template match="reg.region">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_faaRegionOfRegistration;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Registration:Status-->
  <xsl:template match="stat.cd">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_registrationStatus;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Registration:Last Registration Activity-->
  <xsl:template match="last.act.d">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_lastRegistrationActivity;'"/>
      <xsl:with-param name="nodeType" select="$DATE"/>
    </xsl:call-template>
  </xsl:template>

  <!--<xsl:template match="@iso.d">
    <xsl:call-template name="FormatNonSensitiveDate"/>
  </xsl:template>-->

  <!--Registration:Latest Registration Cert-->
  <xsl:template match="cert.iss.d">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_latestRegistrationCert;'"/>
      <xsl:with-param name="nodeType" select="$DATE"/>
    </xsl:call-template>
  </xsl:template>


  <!-- ********************************************************************** 
	**************************  Aircraft Information  *************************
	************************************************************************-->
  <xsl:template match="aircrft.info">
    <xsl:call-template name="wrapPublicRecordsSection">
      <xsl:with-param name="class" select="'&pr_subheader;'"/>
      <xsl:with-param name="contents" select="'&pr_aircraftInformation;'"/>
    </xsl:call-template>
		<table class="&pr_table;">
			<xsl:apply-templates select="aircrft.manufact"/>
			<xsl:apply-templates select="serial.nbr"/>
			<xsl:apply-templates select="air.mdl.ser.b/mdl.cd"/>
			<xsl:apply-templates select="yr.mfr"/>
			<xsl:apply-templates select="type.aircrft"/>
			<xsl:apply-templates select="cert.air.class.cd"/>
			<xsl:apply-templates select="airwthy.d"/>
			<xsl:apply-templates select="aircrft.wght"/>
			<xsl:apply-templates select="aircrft.cruising.spd"/>
			<xsl:apply-templates select="nbr.seats"/>
			<xsl:apply-templates select="engine.manufact"/>
			<xsl:apply-templates select="engine.mdl.nm"/>
			<xsl:apply-templates select="type.engine"/>
			<xsl:apply-templates select="engine.thrust"/>
			</table>
  </xsl:template>

  <!--Information:Manufacturer-->
  <xsl:template match="aircrft.manufact">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_manufacturer;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Manufacturer Serial Number-->
  <xsl:template match="serial.nbr">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_manufacturerSerialNumber;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Manufacturer Model and Series-->
  <xsl:template match="air.mdl.ser.b/mdl.cd">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_modelAndSeries;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Year of Manufacturer-->
  <xsl:template match="yr.mfr">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_yearOfManufacture;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Aircraft Type-->
  <xsl:template match="type.aircrft">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_aircraftType;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Airworthiness Class-->
  <xsl:template match="cert.air.class.cd">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_airWorthinessClass;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Airworthiness Date-->
  <xsl:template match="airwthy.d">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_airWorthinessDate;'"/>
      <xsl:with-param name="nodeType" select="$DATE"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Take-Off Weight-->
  <xsl:template match="aircrft.wght">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_maximumTakeOffWeight;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Cruising Speed-->
  <xsl:template match="aircrft.cruising.spd">
    <tr>
      <xsl:call-template name="wrapWithTableHeader">
        <xsl:with-param name="contents" select="'&pr_averageCruisingSpeed;'" />
      </xsl:call-template>
			<td>
				<xsl:apply-templates/>
				<xsl:text><![CDATA[ ]]>MPH</xsl:text>
			</td>
    </tr>
  </xsl:template>

  <!--Information:Seats-->
  <xsl:template match="nbr.seats">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_numberOfSeats;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Engine Manufacturer-->
  <xsl:template match="engine.manufact">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_engineManufacturer;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Model Name-->
  <xsl:template match="engine.mdl.nm">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_modelName;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Engine Type-->
  <xsl:template match="type.engine">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_engineType;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--Information:Engine Horsepower-->
  <xsl:template match="engine.thrust">
    <xsl:call-template name="wrapPublicRecordsItem">
      <xsl:with-param name="defaultLabel" select="'&pr_engineHorsepower;'"/>
    </xsl:call-template>
  </xsl:template>

  <!-- *********************END OF TEMPLATES AIRCRAFT **************************-->

</xsl:stylesheet>