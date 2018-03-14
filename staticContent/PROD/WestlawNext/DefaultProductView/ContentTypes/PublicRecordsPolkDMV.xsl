<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:msxsl="urn:schemas-microsoft-com:xslt">
    <xsl:include href="SimpleContentBlocks.xsl"/>
    <xsl:include href="Copyright.xsl"/>
    <xsl:include href="PublicRecords.xsl"/>
    <xsl:include href="PublicRecordsAddress.xsl"/>
    <xsl:include href="PublicRecordsName.xsl"/>
    <xsl:include href="BusinessInvestigatorName.xsl"/>
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
    <xsl:template match="polkSearchResultItem">
        <xsl:call-template name="PublicRecordsContent">
            <xsl:with-param name="container" select="'&contentTypePublicRecordsPolkClass;'"/>
            <xsl:with-param name="dualColumn" select="false()"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="PublicRecordsHeader">
    </xsl:template>
    <xsl:template name="PublicRecordsMainColumn">
        <xsl:call-template name="DMVInfo"/>
    </xsl:template>
    <!-- ==================================================================================================== -->
    <!-- =========================================== Main Template ========================================== -->
    <!-- ==================================================================================================== -->
    <xsl:template name="DMVInfo">
        <xsl:call-template name="wrapPublicRecordsSection">
            <xsl:with-param name="class" select="'&pr_header;'"/>
            <xsl:with-param name="contents" select="'Real-Time Motor Vehicle Record'"/>
        </xsl:call-template>
        <!-- =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= -->
        <!-- Source Information subSection -->
        <!-- =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= -->
        <xsl:call-template name="wrapPublicRecordsSection">
            <xsl:with-param name="class" select="'&pr_subheader;'"/>
            <xsl:with-param name="contents" select="'&pr_sourceInformation;'"/>
        </xsl:call-template>
        <table class="&pr_table;">
            <xsl:call-template name="displayLabelValue">
                <xsl:with-param name="label" select="'Current Date:'"/>
                <xsl:with-param name="value" select="$currentDate"/>
            </xsl:call-template>
            <xsl:call-template name="displayLabelValue">
                <xsl:with-param name="label" select="'Source:'"/>
                <xsl:with-param name="value" select="'Real-Time Motor Vehicle Gateway'"/>
            </xsl:call-template>
        </table>
        <!-- =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= -->
        <!-- Vehicle Information subSection -->
        <!-- =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= -->
        <xsl:call-template name="wrapPublicRecordsSection">
            <xsl:with-param name="class" select="'&pr_subheader;'"/>
            <xsl:with-param name="contents" select="'&pr_vehicleInformation;'"/>
        </xsl:call-template>
        <table class="&pr_table;">
            <xsl:call-template name="addFieldIfExists">
                <xsl:with-param name="label" select="'VIN:'"/>
                <xsl:with-param name="selectNode" select="field[@name='vin']"/>
            </xsl:call-template>
            <xsl:call-template name="addFieldIfExists">
                <xsl:with-param name="label" select="'Model Year:'"/>
                <xsl:with-param name="selectNode" select="field[@name='modelYear']"/>
            </xsl:call-template>
            <xsl:call-template name="displayMakeIfExists">
                <xsl:with-param name="selectNode" select="field[@name='make']"/>
            </xsl:call-template>
            <xsl:call-template name="addFieldIfExists">
                <xsl:with-param name="label" select="'Model/Series:'"/>
                <xsl:with-param name="selectNode" select="field[@name='modelSeriesCode']"/>
            </xsl:call-template>
            <xsl:call-template name="displayBodyStyleIfExists">
                <xsl:with-param name="selectNode" select="field[@name='bodyStyleCode']"/>
            </xsl:call-template>
        </table>
        <!-- =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= -->
        <!-- Vehicle Registration subSection -->
        <!-- =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= -->
        <xsl:call-template name="wrapPublicRecordsSection">
            <xsl:with-param name="class" select="'&pr_subheader;'"/>
            <xsl:with-param name="contents" select="'Registration Information'"/>
        </xsl:call-template>
        <table class="&pr_table;">
            <xsl:call-template name="addFieldIfExists">
                <xsl:with-param name="label" select="'License Plate Number:'"/>
                <xsl:with-param name="selectNode" select="field[@name='licensePlateNumber']"/>
            </xsl:call-template>
            <xsl:call-template name="displayPlateTypeIfExists">
                <xsl:with-param name="selectNode" select="field[@name='plateType']"/>
            </xsl:call-template>
            <xsl:call-template name="addFieldIfExists">
                <xsl:with-param name="selectNode" select="field[@name='stateOfRegistration']"/>
                <xsl:with-param name="label" select="'Issuing State:'"/>
            </xsl:call-template>
            <xsl:call-template name="displayTransactionDate">
                <xsl:with-param name="docTypeCode" select="field[@name='docTypeCode']"/>
                <xsl:with-param name="displayDate" select="field[@name='transactionDate']"/>
            </xsl:call-template>
            <xsl:if test="field[@name='expirationDate']/text()">
                <xsl:call-template name="parsePolkDate">
                    <xsl:with-param name="date" select="field[@name='expirationDate']"/>
                    <xsl:with-param name="label" select="'Expiration Date:'"/>
                </xsl:call-template>
            </xsl:if>
            <!-- Brand Info 1 -->
            <xsl:call-template name="displayBrandReasonIfFound">
                <xsl:with-param name="code" select="field[@name='brandCode1']"/>
                <xsl:with-param name="label" select="'Branded Title Description:'"/>
            </xsl:call-template>
            <xsl:call-template name="parseYYMMDate">
                <xsl:with-param name="label" select="'Date Brand Added:'"/>
                <xsl:with-param name="date" select="field[@name='brandDate1']"/>
            </xsl:call-template>
            <xsl:call-template name="addFieldIfExists">
                <xsl:with-param name="label" select="'Branded Title State:'"/>
                <xsl:with-param name="selectNode" select="field[@name='brandState1']"/>
            </xsl:call-template>
            <!-- Brand Info 2 -->
            <xsl:call-template name="displayBrandReasonIfFound">
                <xsl:with-param name="code" select="field[@name='brandCode2']"/>
                <xsl:with-param name="label" select="'Branded Title Description:'"/>
            </xsl:call-template>
            <xsl:call-template name="parseYYMMDate">
                <xsl:with-param name="label" select="'Date Brand Added:'"/>
                <xsl:with-param name="date" select="field[@name='brandDate2']"/>
            </xsl:call-template>
            <xsl:call-template name="addFieldIfExists">
                <xsl:with-param name="label" select="'Branded Title State:'"/>
                <xsl:with-param name="selectNode" select="field[@name='brandState2']"/>
            </xsl:call-template>
            <!-- Owner/Registrant 1 Information -->
            <xsl:call-template name="addFieldIfExists">
                <xsl:with-param name="label" select="'Owner/Registrant Information Name:'"/>
                <xsl:with-param name="selectNode" select="field[@name='firmName1']"/>
            </xsl:call-template>
            <xsl:if test="(field[@name='firstName1']/text()) and (field[@name='surname1']/text())">
                <xsl:call-template name="wrapPublicRecordsName">
                    <xsl:with-param name="label" select="'Owner/Registrant Information Name:'"/>
                    <xsl:with-param name="firstName" select="field[@name='firstName1']"/>
                    <xsl:with-param name="middleName" select="field[@name='middleInitial1']"/>
                    <xsl:with-param name="lastName" select="field[@name='surname1']"/>
                    <xsl:with-param name="suffixName" select="field[@name='nameSuffix1']"/>
                    <xsl:with-param name="searchableLink" select="'TRUE'"/>
                </xsl:call-template>
            </xsl:if>
            <xsl:call-template name="displayLeaseIndicatorIfFound">
                <xsl:with-param name="label" select="'Lease Indicator:'"/>
                <xsl:with-param name="code" select="field[@name='leaseInd']"/>
            </xsl:call-template>
            <xsl:call-template name="wrapPublicRecordsAddress">
                <xsl:with-param name="label" select="'Address:'"/>
                <xsl:with-param name="fullStreet" select="field[@name='streetAddress']"/>
                <xsl:with-param name="streetNum" select="field[@name='houseNumber']"/>
                <xsl:with-param name="streetDirection" select="field[@name='streetPrefixDirection']"/>
                <xsl:with-param name="street" select="field[@name='streetName']"/>
                <xsl:with-param name="streetSuffix" select="field[@name='streetSuffixAbbr']"/>
                <xsl:with-param name="streetDirectionSuffix" select="field[@name='streetSuffixDirection']"/>
                <xsl:with-param name="streetUnitNumber" select="field[@name='secondaryUnit']"/>
                <xsl:with-param name="streetUnit" select="field[@name='secondaryUnitDesg']"/>
                <xsl:with-param name="streetLineTwo" select="field[@name='houseFraction']"/>
                <xsl:with-param name="city" select="field[@name='city']"/>
                <xsl:with-param name="stateOrProvince" select="field[@name='state']"/>
                <xsl:with-param name="zip" select="field[@name='zipCode']"/>
                <xsl:with-param name="zipExt" select="field[@name='addrZipPlus4Code']"/>
            </xsl:call-template>
            <!-- Owner/Registrant 2 Information -->
            <xsl:call-template name="displayBusinessInvestigatorName">
                <xsl:with-param name="label" select="'Firm Name 2:'"/>
                <xsl:with-param name="companyName" select="field[@name='firmName2']"/>
            </xsl:call-template>
            <xsl:if test="(field[@name='firstName2']/text()) and (field[@name='surname2']/text())">
                <xsl:call-template name="wrapPublicRecordsName">
                    <xsl:with-param name="label" select="'Name 2:'"/>
                    <xsl:with-param name="firstName" select="field[@name='firstName2']"/>
                    <xsl:with-param name="middleName" select="field[@name='middleInitial2']"/>
                    <xsl:with-param name="lastName" select="field[@name='surname2']"/>
                    <xsl:with-param name="suffixName" select="field[@name='nameSuffix2']"/>
                    <xsl:with-param name="searchableLink" select="'TRUE'"/>
                </xsl:call-template>
            </xsl:if>
            <xsl:call-template name="displayInterestFieldIfMappingFound">
                <xsl:with-param name="label" select="'Interest:'"/>
                <xsl:with-param name="code" select="field[@name='name2Code']"/>
            </xsl:call-template>
            <!-- Owner/Registrant 3 Information -->
            <xsl:call-template name="displayBusinessInvestigatorName">
                <xsl:with-param name="label" select="'Firm Name 3:'"/>
                <xsl:with-param name="companyName" select="field[@name='firmName3']"/>
            </xsl:call-template>
            <xsl:if test="(field[@name='firstName3']/text()) and (field[@name='surname3']/text())">
                <xsl:call-template name="wrapPublicRecordsName">
                    <xsl:with-param name="label" select="'Name 3:'"/>
                    <xsl:with-param name="firstName" select="field[@name='firstName3']"/>
                    <xsl:with-param name="middleName" select="field[@name='middleInitial3']"/>
                    <xsl:with-param name="lastName" select="field[@name='surname3']"/>
                    <xsl:with-param name="suffixName" select="field[@name='nameSuffix3']"/>
                    <xsl:with-param name="searchableLink" select="'TRUE'"/>
                </xsl:call-template>
            </xsl:if>
            <xsl:call-template name="displayInterestFieldIfMappingFound">
                <xsl:with-param name="label" select="'Interest:'"/>
                <xsl:with-param name="code" select="field[@name='name3Code']"/>
            </xsl:call-template>
        </table>
    </xsl:template>
    <!-- ==================================================================================================== -->
    <!-- =========================================== Sub Templates ========================================== -->
    <!-- ==================================================================================================== -->
    <xsl:template name="displayTransactionDate">
        <xsl:param name="docTypeCode"/>
        <xsl:param name="displayDate"/>
        <xsl:if test="$docTypeCode/text()">
            <xsl:variable name="lookupValue">
                <xsl:call-template name="transactionDateLabelLookup">
                    <xsl:with-param name="lookupKey" select="$docTypeCode"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:call-template name="parsePolkDate">
                <xsl:with-param name="date" select="$displayDate"/>
                <xsl:with-param name="label" select="$lookupValue"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template name="addFieldIfExists">
        <xsl:param name="label"/>
        <xsl:param name="selectNode"/>
        <xsl:if test="$selectNode/text()">
            <xsl:if test="$selectNode/text() != '0'">
                <xsl:variable name="upperCaseVar">
                    <xsl:call-template name="toUpper">
                        <xsl:with-param name="lowerCaseVar" select="$selectNode"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:call-template name="displayLabelValue">
                    <xsl:with-param name="label" select="$label"/>
                    <xsl:with-param name="value" select="$upperCaseVar"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    <xsl:template name="displayLabelValue">
        <xsl:param name="label" select="'label'"/>
        <xsl:param name="value" select="'value'"/>
        <xsl:if test="$value != ''">
            <xsl:variable name="upperCaseVar">
                <xsl:call-template name="toUpper">
                    <xsl:with-param name="lowerCaseVar" select="$value"/>
                </xsl:call-template>
            </xsl:variable>
            <tr class="&pr_item;">
                <xsl:call-template name="wrapWithTableHeader">
                    <xsl:with-param name="contents" select="$label"/>
                </xsl:call-template>
                <td>
                    <xsl:value-of select="$upperCaseVar"/>
                </td>
            </tr>
        </xsl:if>
    </xsl:template>
    <xsl:template name="displayBusinessInvestigatorName">
        <xsl:param name="label"/>
        <xsl:param name="companyName"/>
        <xsl:if test="$companyName/text()">
            <xsl:call-template name="wrapBusinessInvestigatorName">
                <xsl:with-param name="label" select="$label"/>
                <xsl:with-param name="companyName" select="$companyName"/>
                <xsl:with-param name="searchableLink" select="'TRUE'"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template name="displayPlateTypeIfExists">
        <xsl:param name="selectNode"/>
        <xsl:if test="$selectNode/text()">
            <xsl:variable name="thingType">
                <xsl:call-template name="mapPlateValue">
                    <xsl:with-param name="mapKey" select="$selectNode"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:call-template name="displayLabelValue">
                <xsl:with-param name="label" select="'License Plate Type:'"/>
                <xsl:with-param name="value" select="$thingType"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template name="displayMakeIfExists">
        <xsl:param name="selectNode"/>
        <xsl:if test="$selectNode/text()">
            <xsl:variable name="thingType">
                <xsl:call-template name="mapMakeValue">
                    <xsl:with-param name="mapKey" select="$selectNode"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:call-template name="displayLabelValue">
                <xsl:with-param name="label" select="'Make:'"/>
                <xsl:with-param name="value" select="$thingType"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template name="displayBodyStyleIfExists">
        <xsl:param name="selectNode"/>
        <xsl:if test="$selectNode/text()">
            <xsl:variable name="thingType">
                <xsl:call-template name="mapBodyStyleValue">
                    <xsl:with-param name="mapKey" select="$selectNode"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:call-template name="displayLabelValue">
                <xsl:with-param name="label" select="'Body Style:'"/>
                <xsl:with-param name="value" select="$thingType"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template name="displayBrandReasonIfFound">
        <xsl:param name="label"/>
        <xsl:param name="code"/>
        <xsl:if test="$code/text()">
            <xsl:variable name="lookupValue">
                <xsl:call-template name="brandCodeLookup">
                    <xsl:with-param name="mapKey" select="$code"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:if test="$lookupValue != 'notFound'">
                <xsl:call-template name="displayLabelValue">
                    <xsl:with-param name="label" select="$label"/>
                    <xsl:with-param name="value" select="$lookupValue"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    <xsl:template name="parseYYMMDate">
        <xsl:param name="date" select="."/>
        <xsl:param name="label"/>
        <xsl:if test="string-length($date) = 4 and number($date) != 'NaN'">
            <xsl:variable name="year" select="substring($date,1,2)"/>
            <xsl:variable name="month" select="substring($date,3,2)"/>
            <tr>
                <th>
                    <xsl:value-of select="$label"/>
                </th>
                <td>
                    <xsl:value-of select="$month"/>
                    <xsl:text>/</xsl:text>
                    <xsl:value-of select="$year"/>
                </td>
            </tr>
        </xsl:if>
    </xsl:template>
    <xsl:template name="parsePolkDate">
        <xsl:param name="date" select="."/>
        <xsl:param name="label"/>
        <xsl:if test="string-length($date) = 4 and number($date) != 'NaN'">
            <xsl:variable name="month" select="substring($date,1,2)"/>
            <xsl:variable name="year" select="substring($date,3,2)"/>
            <tr>
                <th>
                    <xsl:value-of select="$label"/>
                </th>
                <td>
                    <xsl:value-of select="$month"/>
                    <xsl:text>/</xsl:text>
                    <xsl:value-of select="$year"/>
                </td>
            </tr>
        </xsl:if>
        <xsl:if test="string-length($date) = 8 and number($date) != 'NaN'">
            <xsl:variable name="year" select="substring($date,1,4)"/>
            <xsl:variable name="month" select="substring($date,5,2)"/>
            <xsl:variable name="day" select="substring($date,7,2)"/>
            <tr>
                <th>
                    <xsl:value-of select="$label"/>
                </th>
                <td>
                    <xsl:value-of select="$month"/>
                    <xsl:text>/</xsl:text>
                    <xsl:value-of select="$day"/>
                    <xsl:text>/</xsl:text>
                    <xsl:value-of select="$year"/>
                </td>
            </tr>
        </xsl:if>
    </xsl:template>
    <xsl:template name="toUpper">
        <xsl:param name="lowerCaseVar"/>
        <xsl:variable name="lowerCase" select="'abcdefghijklmnopqrstuvwxyz'"/>
        <xsl:variable name="upperCase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
        <xsl:value-of select="translate($lowerCaseVar,$lowerCase,$upperCase)"/>
    </xsl:template>
    <xsl:template name="displayInterestFieldIfMappingFound">
        <xsl:param name="label"/>
        <xsl:param name="code"/>
        <xsl:if test="$code/text()">
            <xsl:variable name="lookupValue">
                <xsl:call-template name="dmvNameCodeLookup">
                    <xsl:with-param name="mapKey" select="$code"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:if test="$lookupValue != 'notFound'">
                <xsl:call-template name="displayLabelValue">
                    <xsl:with-param name="label" select="$label"/>
                    <xsl:with-param name="value" select="$lookupValue"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    <xsl:template name="displayLeaseIndicatorIfFound">
        <xsl:param name="label"/>
        <xsl:param name="code"/>
        <xsl:if test="$code/text()">
            <xsl:variable name="lookupValue">
                <xsl:call-template name="dmvLeaseIndLookup">
                    <xsl:with-param name="mapKey" select="$code"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:if test="$lookupValue != 'notFound'">
                <xsl:call-template name="displayLabelValue">
                    <xsl:with-param name="label" select="$label"/>
                    <xsl:with-param name="value" select="$lookupValue"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    <!-- ==================================================================================================== -->
    <!-- ============================================= Data Maps ============================================ -->
    <!-- ==================================================================================================== -->
    <xsl:template name="mapMakeValue">
        <xsl:param name="mapKey"/>
        <xsl:choose>
            <xsl:when test="$mapKey = 'ACUR'">ACURA</xsl:when>
            <xsl:when test="$mapKey = 'ALFA'">ALFA ROMEO</xsl:when>
            <xsl:when test="$mapKey = 'AMGN'">AM GENERAL</xsl:when>
            <xsl:when test="$mapKey = 'AMER'">AMERICAN MOTORS</xsl:when>
            <xsl:when test="$mapKey = 'ASTO'">ASTON MARTIN</xsl:when>
            <xsl:when test="$mapKey = 'ASUN'">ASUNA</xsl:when>
            <xsl:when test="$mapKey = 'AUDI'">AUDI</xsl:when>
            <xsl:when test="$mapKey = 'AUST'">AUSTIN</xsl:when>
            <xsl:when test="$mapKey = 'AUTC'">AUTOCAR LLC</xsl:when>
            <xsl:when test="$mapKey = 'AZU'">AZURE</xsl:when>
            <xsl:when test="$mapKey = 'BENT'">BENTLEY</xsl:when>
            <xsl:when test="$mapKey = 'BMW'">BMW</xsl:when>
            <xsl:when test="$mapKey = 'BUIC'">BUICK</xsl:when>
            <xsl:when test="$mapKey = 'CADI'">CADILLAC</xsl:when>
            <xsl:when test="$mapKey = 'CAP'">CAPRI</xsl:when>
            <xsl:when test="$mapKey = 'CAT'">CATERPILLAR</xsl:when>
            <xsl:when test="$mapKey = 'CHEV'">CHEVROLET</xsl:when>
            <xsl:when test="$mapKey = 'CHRY'">CHRYSLER</xsl:when>
            <xsl:when test="$mapKey = 'CPIU'">CPI MOTOR COMPANY</xsl:when>
            <xsl:when test="$mapKey = 'DAEW'">DAEWOO</xsl:when>
            <xsl:when test="$mapKey = 'DAIH'">DAIHATSU</xsl:when>
            <xsl:when test="$mapKey = 'DIAR'">DIAMOND REO</xsl:when>
            <xsl:when test="$mapKey = 'DODG'">DODGE</xsl:when>
            <xsl:when test="$mapKey = 'EGIL'">EAGLE</xsl:when>
            <xsl:when test="$mapKey = 'FERR'">FERRARI</xsl:when>
            <xsl:when test="$mapKey = 'FIAT'">FIAT</xsl:when>
            <xsl:when test="$mapKey = 'FSKR'">FISKER</xsl:when>
            <xsl:when test="$mapKey = 'FORD'">FORD</xsl:when>
            <xsl:when test="$mapKey = 'FRHT'">FREIGHTLINER</xsl:when>
            <xsl:when test="$mapKey = 'FWD'">FWD</xsl:when>
            <xsl:when test="$mapKey = 'GEM'">GEM</xsl:when>
            <xsl:when test="$mapKey = 'GEO'">GEO</xsl:when>
            <xsl:when test="$mapKey = 'GM'">GM</xsl:when>
            <xsl:when test="$mapKey = 'GMC'">GMC</xsl:when>
            <xsl:when test="$mapKey = 'HD'">HARLEY-DAVIDSON</xsl:when>
            <xsl:when test="$mapKey = 'HINO'">HINO</xsl:when>
            <xsl:when test="$mapKey = 'HOND'">HONDA</xsl:when>
            <xsl:when test="$mapKey = 'HUMM'">HUMMER</xsl:when>
            <xsl:when test="$mapKey = 'HYOS'">HYOSUNG</xsl:when>
            <xsl:when test="$mapKey = 'HYUN'">HYUNDAI</xsl:when>
            <xsl:when test="$mapKey = 'INFI'">INFINITI</xsl:when>
            <xsl:when test="$mapKey = 'INTL'">INTERNATIONAL</xsl:when>
            <xsl:when test="$mapKey = 'ISU'">ISUZU</xsl:when>
            <xsl:when test="$mapKey = 'IVEC'">IVECO</xsl:when>
            <xsl:when test="$mapKey = 'JAGU'">JAGUAR</xsl:when>
            <xsl:when test="$mapKey = 'JEEP'">JEEP</xsl:when>
            <xsl:when test="$mapKey = 'JENS'">JENSEN</xsl:when>
            <xsl:when test="$mapKey = 'KAWK'">KAWASAKI</xsl:when>
            <xsl:when test="$mapKey = 'KIA'">KIA</xsl:when>
            <xsl:when test="$mapKey = 'KW'">KENWORTH</xsl:when>
            <xsl:when test="$mapKey = 'IVCM'">MAGIRUS</xsl:when>
            <xsl:when test="$mapKey = 'SPNR'">DCX SPRINTER</xsl:when>
            <xsl:when test="$mapKey = 'LADA'">LADA</xsl:when>
            <xsl:when test="$mapKey = 'LAMO'">LAMBORGHINI</xsl:when>
            <xsl:when test="$mapKey = 'LEXS'">LEXUS</xsl:when>
            <xsl:when test="$mapKey = 'LNCI'">LANCIA</xsl:when>
            <xsl:when test="$mapKey = 'LNDR'">LAND ROVER</xsl:when>
            <xsl:when test="$mapKey = 'LINC'">LINCOLN</xsl:when>
            <xsl:when test="$mapKey = 'LOTU'">LOTUS</xsl:when>
            <xsl:when test="$mapKey = 'MACK'">MACK</xsl:when>
            <xsl:when test="$mapKey = 'MAHI'">MAHINDRA</xsl:when>
            <xsl:when test="$mapKey = 'MASE'">MASERATI</xsl:when>
            <xsl:when test="$mapKey = 'MAYB'">MAYBACH</xsl:when>
            <xsl:when test="$mapKey = 'MAZD'">MAZDA</xsl:when>
            <xsl:when test="$mapKey = 'MCLA'">MCLAREN</xsl:when>
            <xsl:when test="$mapKey = 'MERC'">MERCURY</xsl:when>
            <xsl:when test="$mapKey = 'MERK'">MERKUR</xsl:when>
            <xsl:when test="$mapKey = 'MERZ'">MERCEDES-BENZ</xsl:when>
            <xsl:when test="$mapKey = 'MG'">MG</xsl:when>
            <xsl:when test="$mapKey = 'MNNI'">MINI</xsl:when>
            <xsl:when test="$mapKey = 'MIFU'">MITSUBISHI FUSO</xsl:when>
            <xsl:when test="$mapKey = 'MITS'">MITSUBISHI</xsl:when>
            <xsl:when test="$mapKey = 'NISS'">NISSAN</xsl:when>
            <xsl:when test="$mapKey = 'OLDS'">OLDSMOBILE</xsl:when>
            <xsl:when test="$mapKey = 'OPEL'">OPEL</xsl:when>
            <xsl:when test="$mapKey = 'OSHK'">OSHKOSH</xsl:when>
            <xsl:when test="$mapKey = 'PASS'">PASSPORT</xsl:when>
            <xsl:when test="$mapKey = 'PEUG'">PEUGEOT</xsl:when>
            <xsl:when test="$mapKey = 'PLYM'">PLYMOUTH</xsl:when>
            <xsl:when test="$mapKey = 'PTRB'">PETERBILT</xsl:when>
            <xsl:when test="$mapKey = 'POLS'">POLARIS</xsl:when>
            <xsl:when test="$mapKey = 'PONT'">PONTIAC</xsl:when>
            <xsl:when test="$mapKey = 'PORS'">PORSCHE</xsl:when>
            <xsl:when test="$mapKey = 'RAM'">RAM</xsl:when>
            <xsl:when test="$mapKey = 'RENA'">RENAULT</xsl:when>
            <xsl:when test="$mapKey = 'ROL'">ROLLS-ROYCE</xsl:when>
            <xsl:when test="$mapKey = 'SAA'">SAAB</xsl:when>
            <xsl:when test="$mapKey = 'STRN'">SATURN</xsl:when>
            <xsl:when test="$mapKey = 'SMRT'">SMART</xsl:when>
            <xsl:when test="$mapKey = 'SSI'">SSI</xsl:when>
            <xsl:when test="$mapKey = 'STLG'">STERLING</xsl:when>
            <xsl:when test="$mapKey = 'STRG'">STERLING</xsl:when>
            <xsl:when test="$mapKey = 'SUBA'">SUBARU</xsl:when>
            <xsl:when test="$mapKey = 'SUZI'">SUZUKI</xsl:when>
            <xsl:when test="$mapKey = 'TOYT'">TOYOTA</xsl:when>
            <xsl:when test="$mapKey = 'TRIU'">TRIUMPH</xsl:when>
            <xsl:when test="$mapKey = 'TRUM'">TRIUMPH</xsl:when>
            <xsl:when test="$mapKey = 'UNIG'">UNIMOG</xsl:when>
            <xsl:when test="$mapKey = 'VCTY'">VICTORY</xsl:when>
            <xsl:when test="$mapKey = 'VOLK'">VOLKSWAGEN</xsl:when>
            <xsl:when test="$mapKey = 'VOLV'">VOLVO</xsl:when>
            <xsl:when test="$mapKey = 'WHIT'">WHITE</xsl:when>
            <xsl:when test="$mapKey = 'WHGM'">WHITEGMC</xsl:when>
            <xsl:when test="$mapKey = 'WINN'">WINNEBAGO</xsl:when>
            <xsl:when test="$mapKey = 'YAMA'">YAMAHA</xsl:when>
            <xsl:when test="$mapKey = 'YUGO'">YUGO</xsl:when>
            <xsl:when test="$mapKey = 'ZONG'">ZONGSHEN</xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="field[@name='make']"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="mapPlateValue">
        <xsl:param name="mapKey"/>
        <xsl:choose>
            <xsl:when test="$mapKey = '0'"/>
            <xsl:when test="$mapKey = '1'">REGULAR PASSENGER</xsl:when>
            <xsl:when test="$mapKey = '2'">REGULAR TRUCK</xsl:when>
            <xsl:when test="$mapKey = '3'">REGULAR</xsl:when>
            <xsl:when test="$mapKey = '4'">OFFICIAL</xsl:when>
            <xsl:when test="$mapKey = '5'">EXEMPT</xsl:when>
            <xsl:when test="$mapKey = '6'">DEALER</xsl:when>
            <xsl:when test="$mapKey = '7'">VANITY</xsl:when>
            <xsl:when test="$mapKey = '8'">HAM RADIO</xsl:when>
            <xsl:when test="$mapKey = '9'">ANTIQUE</xsl:when>
            <xsl:when test="$mapKey = 'A'">HANDICAP</xsl:when>
            <xsl:when test="$mapKey = 'B'">DISABLED VETERAN</xsl:when>
            <xsl:when test="$mapKey = 'C'">PRISONER OF WAR</xsl:when>
            <xsl:when test="$mapKey = 'D'">FARM</xsl:when>
            <xsl:when test="$mapKey = 'E'">PRO-RATE</xsl:when>
            <xsl:when test="$mapKey = 'F'">COMMERCIAL</xsl:when>
            <xsl:when test="$mapKey = 'G'">RECREATIONAL / OFF ROAD</xsl:when>
            <xsl:when test="$mapKey = 'H'">SPECIAL INTEREST GROUP</xsl:when>
            <xsl:when test="$mapKey = 'J'">TEMPORARY</xsl:when>
            <xsl:when test="$mapKey = 'P'">ENVIRONMENTAL</xsl:when>
            <xsl:when test="$mapKey = 'R'">TEMPORARY TAG</xsl:when>
            <xsl:when test="$mapKey = 'Z'"/>
            <xsl:otherwise>$mapKey</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="mapBodyStyleValue">
        <xsl:param name="mapKey"/>
        <xsl:choose>
            <xsl:when test="$mapKey = 'AM'">Ambulance</xsl:when>
            <xsl:when test="$mapKey = 'CB'">Cab &amp; Chassis</xsl:when>
            <xsl:when test="$mapKey = 'CP'">Coupe</xsl:when>
            <xsl:when test="$mapKey = 'CV'">Convertible</xsl:when>
            <xsl:when test="$mapKey = 'C4'">Coupe 4 Dr.</xsl:when>
            <xsl:when test="$mapKey = 'HB'">Hatchback</xsl:when>
            <xsl:when test="$mapKey = 'HR'">Hearse</xsl:when>
            <xsl:when test="$mapKey = 'HT'">Hardtop</xsl:when>
            <xsl:when test="$mapKey = 'IN'">Incomplete Passenger</xsl:when>
            <xsl:when test="$mapKey = 'LB'">Liftback</xsl:when>
            <xsl:when test="$mapKey = 'LM'">Limousine</xsl:when>
            <xsl:when test="$mapKey = 'NB'">Notchback</xsl:when>
            <xsl:when test="$mapKey = 'PK'">Pickup</xsl:when>
            <xsl:when test="$mapKey = 'PN'">Panel</xsl:when>
            <xsl:when test="$mapKey = 'P2'">2 Passenger Low speed</xsl:when>
            <xsl:when test="$mapKey = 'P4'">2 Passenger Low speed</xsl:when>
            <xsl:when test="$mapKey = 'RD'">Roadster</xsl:when>
            <xsl:when test="$mapKey = 'SB'">Sport Hatchback</xsl:when>
            <xsl:when test="$mapKey = 'RD'">Roadster</xsl:when>
            <xsl:when test="$mapKey = 'SB'">Sport Hatchback</xsl:when>
            <xsl:when test="$mapKey = 'SC'">Sport Coupe</xsl:when>
            <xsl:when test="$mapKey = 'SD'">Sedan</xsl:when>
            <xsl:when test="$mapKey = 'SV'">Sport Van</xsl:when>
            <xsl:when test="$mapKey = 'SW'">Station Wagon</xsl:when>
            <xsl:when test="$mapKey = 'UT'">Utility</xsl:when>
            <xsl:when test="$mapKey = 'WW'">Wide Wheel Wagon</xsl:when>
            <xsl:when test="$mapKey = '2D'">Sedan 2 Dr.</xsl:when>
            <xsl:when test="$mapKey = '2F'">Formal Hardtop 2 Dr.</xsl:when>
            <xsl:when test="$mapKey = '2H'">Hatchback 2 Dr.</xsl:when>
            <xsl:when test="$mapKey = '2L'">Liftback 3 Dr.</xsl:when>
            <xsl:when test="$mapKey = '2P'">Pillard Hardtop 2 Dr.</xsl:when>
            <xsl:when test="$mapKey = '2T'">Hardtop 2 Dr.</xsl:when>
            <xsl:when test="$mapKey = '2W'">Wagon 2 Dr.</xsl:when>
            <xsl:when test="$mapKey = '3D'">Runabout 3 Dr.</xsl:when>
            <xsl:when test="$mapKey = '3P'">Coupe 3 Dr.</xsl:when>
            <xsl:when test="$mapKey = '4D'">Sedan 4 Dr.</xsl:when>
            <xsl:when test="$mapKey = '4H'">Hatchback 4 Dr.</xsl:when>
            <xsl:when test="$mapKey = '4L'">Liftback 5 Dr.</xsl:when>
            <xsl:when test="$mapKey = '4P'">Pillard Hardtop 4 Dr.</xsl:when>
            <xsl:when test="$mapKey = '4T'">Hardtop 4 Dr.</xsl:when>
            <xsl:when test="$mapKey = '4W'">Wagon 4 Dr.</xsl:when>
            <xsl:when test="$mapKey = '5D'">Sedan 5 Dr.</xsl:when>
            <xsl:when test="$mapKey = 'AC'">Auto Carrier</xsl:when>
            <xsl:when test="$mapKey = 'AR'">Armored Truck</xsl:when>
            <xsl:when test="$mapKey = 'BU'">Bus</xsl:when>
            <xsl:when test="$mapKey = 'CB'">Chassis and Cab</xsl:when>
            <xsl:when test="$mapKey = 'CC'">Conventional Cab</xsl:when>
            <xsl:when test="$mapKey = 'CG'">Cargo Van</xsl:when>
            <xsl:when test="$mapKey = 'CH'">Crew Chassis</xsl:when>
            <xsl:when test="$mapKey = 'CL'">Club Chassis</xsl:when>
            <xsl:when test="$mapKey = 'CM'">Concrete or Transit Mixer</xsl:when>
            <xsl:when test="$mapKey = 'CR'">Crane</xsl:when>
            <xsl:when test="$mapKey = 'CS'">Super Cab / Chassis Pickup</xsl:when>
            <xsl:when test="$mapKey = 'CU'">Custom Pickup</xsl:when>
            <xsl:when test="$mapKey = 'CV'">Convertible</xsl:when>
            <xsl:when test="$mapKey = 'CW'">Crew Pickup</xsl:when>
            <xsl:when test="$mapKey = 'CY'">Cargo Cutaway</xsl:when>
            <xsl:when test="$mapKey = 'DP'">Dump</xsl:when>
            <xsl:when test="$mapKey = 'DS'">Tractor Truck</xsl:when>
            <xsl:when test="$mapKey = 'EC'">Extended Cargo Van</xsl:when>
            <xsl:when test="$mapKey = 'ES'">Extended Sport Van</xsl:when>
            <xsl:when test="$mapKey = 'EV'">Ext Van</xsl:when>
            <xsl:when test="$mapKey = 'EW'">Extended Window Van</xsl:when>
            <xsl:when test="$mapKey = 'FB'">Flat-bed or Platform</xsl:when>
            <xsl:when test="$mapKey = 'FC'">Forward Control</xsl:when>
            <xsl:when test="$mapKey = 'FT'">Fire Truck</xsl:when>
            <xsl:when test="$mapKey = 'GG'">Garbage or Refuse</xsl:when>
            <xsl:when test="$mapKey = 'MY'">Motorized Cutaway</xsl:when>
            <xsl:when test="$mapKey = 'PC'">Club Cab PickUp</xsl:when>
            <xsl:when test="$mapKey = 'PD'">Parcel Delivery</xsl:when>
            <xsl:when test="$mapKey = 'PK'">Pickup</xsl:when>
            <xsl:when test="$mapKey = 'PM'">Pickup with Camper</xsl:when>
            <xsl:when test="$mapKey = 'PN'">Panel</xsl:when>
            <xsl:when test="$mapKey = 'PS'">Super Cab Pickup</xsl:when>
            <xsl:when test="$mapKey = 'RD'">Roadster</xsl:when>
            <xsl:when test="$mapKey = 'SN'">Step Van</xsl:when>
            <xsl:when test="$mapKey = 'SP'">Sport Pickup</xsl:when>
            <xsl:when test="$mapKey = 'ST'">Stake or Rack</xsl:when>
            <xsl:when test="$mapKey = 'SV'">Sports Van</xsl:when>
            <xsl:when test="$mapKey = 'SW'">Station Wagon</xsl:when>
            <xsl:when test="$mapKey = 'S1'">One Seat</xsl:when>
            <xsl:when test="$mapKey = 'S2'">Two Seat</xsl:when>
            <xsl:when test="$mapKey = 'TB'">Tilt Cab</xsl:when>
            <xsl:when test="$mapKey = 'TL'">Tilt Tandem</xsl:when>
            <xsl:when test="$mapKey = 'TM'">Tandem</xsl:when>
            <xsl:when test="$mapKey = 'TN'">Tank</xsl:when>
            <xsl:when test="$mapKey = 'TR'">Tractor Truck</xsl:when>
            <xsl:when test="$mapKey = 'UT'">Utility</xsl:when>
            <xsl:when test="$mapKey = 'VC'">Van Camper</xsl:when>
            <xsl:when test="$mapKey = 'VD'">Display Van</xsl:when>
            <xsl:when test="$mapKey = 'VN'">Van</xsl:when>
            <xsl:when test="$mapKey = 'VT'">Vanette</xsl:when>
            <xsl:when test="$mapKey = 'GL'">Gliders</xsl:when>
            <xsl:when test="$mapKey = 'GN'">Grain</xsl:when>
            <xsl:when test="$mapKey = 'HO'">Hopper</xsl:when>
            <xsl:when test="$mapKey = 'IC'">Incomplete Chassis</xsl:when>
            <xsl:when test="$mapKey = 'IE'">Incomplete Ext Van</xsl:when>
            <xsl:when test="$mapKey = 'LG'">Logger</xsl:when>
            <xsl:when test="$mapKey = 'LL'">Suburban &amp; Carry All</xsl:when>
            <xsl:when test="$mapKey = 'LM'">Limousine</xsl:when>
            <xsl:when test="$mapKey = 'MH'">Motorized Home</xsl:when>
            <xsl:when test="$mapKey = 'MP'">Multi-purpose</xsl:when>
            <xsl:when test="$mapKey = 'MV'">Maxi Van</xsl:when>
            <xsl:when test="$mapKey = 'MW'">Maxi Wagon</xsl:when>
            <xsl:when test="$mapKey = 'VW'">Window Van</xsl:when>
            <xsl:when test="$mapKey = 'WK'">Tow Truck Wrecker</xsl:when>
            <xsl:when test="$mapKey = 'WW'">Wide Wheel Wagon</xsl:when>
            <xsl:when test="$mapKey = 'XT'">Travelall</xsl:when>
            <xsl:when test="$mapKey = 'YY'">Cutaway</xsl:when>
            <xsl:when test="$mapKey = '2W'">2 Dr. Wagon / Sport Utility</xsl:when>
            <xsl:when test="$mapKey = '3B'">3 Dr. Extended Cab / Chassis</xsl:when>
            <xsl:when test="$mapKey = '3C'">3 Dr. Extended Cab Pickup</xsl:when>
            <xsl:when test="$mapKey = '4B'">4 Dr. Extended Cab / Chassis</xsl:when>
            <xsl:when test="$mapKey = '4C'">4 Dr. Extended Cab Pickup</xsl:when>
            <xsl:when test="$mapKey = '4W'">4 Dr. Wagon / Sport Utility</xsl:when>
            <xsl:when test="$mapKey = '8V'">8 Passenger Sport Van</xsl:when>
            <xsl:when test="$mapKey = 'AT'">All Terrain</xsl:when>
            <xsl:when test="$mapKey = 'EN'">Enduro</xsl:when>
            <xsl:when test="$mapKey = 'MK'">Mini Bike</xsl:when>
            <xsl:when test="$mapKey = 'MM'">Mini Moto Cross</xsl:when>
            <xsl:when test="$mapKey = 'MP'">Moped</xsl:when>
            <xsl:when test="$mapKey = 'MR'">Mini Road/Trail</xsl:when>
            <xsl:when test="$mapKey = 'MS'">Motor Scooter</xsl:when>
            <xsl:when test="$mapKey = 'MX'">Moto Cross</xsl:when>
            <xsl:when test="$mapKey = 'MY'">Mini Cycle</xsl:when>
            <xsl:when test="$mapKey = 'RC'">Racer</xsl:when>
            <xsl:when test="$mapKey = 'RS'">Road/Street</xsl:when>
            <xsl:when test="$mapKey = 'RT'">Road/Trail</xsl:when>
            <xsl:when test="$mapKey = 'T'">Dirt</xsl:when>
            <xsl:when test="$mapKey = 'TL'">Trail/Dirt</xsl:when>
            <xsl:when test="$mapKey = 'TR'">Trial</xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="field[@name='bodyStyleCode']"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="mapAddressTypeValue">
        <xsl:param name="mapKey"/>
        <xsl:choose>
            <xsl:when test="$mapKey = '0'"/>
            <xsl:when test="$mapKey = '1'">NO ADDRESS</xsl:when>
            <xsl:when test="$mapKey = '2'">POST OFFICE BOX</xsl:when>
            <xsl:when test="$mapKey = '3'">ROUTE ONLY</xsl:when>
            <xsl:when test="$mapKey = '4'">ROUTE AND BOX</xsl:when>
            <xsl:when test="$mapKey = '5'">GENERAL DELIVERY</xsl:when>
            <xsl:when test="$mapKey = '6'">STREET NAME WITH HOUSE NUMBER</xsl:when>
            <xsl:when test="$mapKey = '7'">NAMED STAR ROUTE</xsl:when>
            <xsl:when test="$mapKey = '8'">STREET NAME ONLY</xsl:when>
            <xsl:when test="$mapKey = '9'">STAR ROUTE</xsl:when>
            <xsl:otherwise>$mapKey</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="brandCodeLookup">
        <xsl:param name="mapKey"/>
        <xsl:choose>
            <xsl:when test="$mapKey = 'A'">ASSEMBLED</xsl:when>
            <xsl:when test="$mapKey = 'B'">HOMEMADE</xsl:when>
            <xsl:when test="$mapKey = 'C'">KIT</xsl:when>
            <xsl:when test="$mapKey = 'D'">REPLICA</xsl:when>
            <xsl:when test="$mapKey = 'E'">DISMANTLED</xsl:when>
            <xsl:when test="$mapKey = 'F'">FLOOD</xsl:when>
            <xsl:when test="$mapKey = 'G'">JUNK</xsl:when>
            <xsl:when test="$mapKey = 'H'">REBUILT</xsl:when>
            <xsl:when test="$mapKey = 'I'">RECONSTRUCTED</xsl:when>
            <xsl:when test="$mapKey = 'J'">SALVAGE</xsl:when>
            <xsl:when test="$mapKey = 'K'">MISCELLANEOUS (EX: FIRE, HAIL)</xsl:when>
            <xsl:when test="$mapKey = 'L'">NOT ACTUAL MILEAGE</xsl:when>
            <xsl:when test="$mapKey = 'M'">EXCEEDS MECHANICAL LIMITS</xsl:when>
            <xsl:when test="$mapKey = 'N'">REACQUIRED</xsl:when>
            <xsl:when test="$mapKey = 'O'">REPOSSESSION</xsl:when>
            <xsl:when test="$mapKey = 'P'">STOLEN</xsl:when>
            <xsl:when test="$mapKey = 'Q'">MANUFACTURER BUYBACK, LEMON LAW</xsl:when>
            <xsl:when test="$mapKey = 'R'">RECOVERED STOLEN</xsl:when>
            <xsl:when test="$mapKey = 'S'">NON-HIGHWAY/FORMER NON-HIGHWAY</xsl:when>
            <xsl:when test="$mapKey = 'T'">ABANDONED</xsl:when>
            <xsl:when test="$mapKey = 'U'">DAMAGED</xsl:when>
            <xsl:when test="$mapKey = 'Z'">EXCEEDS MECHANICAL LIMITS (MILES > 99,999)</xsl:when>
            <xsl:otherwise>notFound</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="dmvNameCodeLookup">
        <xsl:param name="mapKey"/>
        <xsl:choose>
            <xsl:when test="$mapKey = '0'"/>
            <xsl:when test="$mapKey = '1'">JOINT OWNER</xsl:when>
            <xsl:when test="$mapKey = '2'">DBA</xsl:when>
            <xsl:when test="$mapKey = '3'">C/O</xsl:when>
            <xsl:when test="$mapKey = '4'">LESSOR</xsl:when>
            <xsl:when test="$mapKey = '5'">LESSEE</xsl:when>
            <xsl:when test="$mapKey = '6'">LIEN HOLDER</xsl:when>
            <xsl:otherwise>notFound</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="mapNamePrefix">
        <xsl:param name="mapKey"/>
        <xsl:choose>
            <xsl:when test="$mapKey = '0'"/>
            <xsl:when test="$mapKey = '1'">MR.</xsl:when>
            <xsl:when test="$mapKey = '2'">MRS.</xsl:when>
            <xsl:when test="$mapKey = '3'">MISS/MS.</xsl:when>
            <xsl:when test="$mapKey = '4'">MR. AND MRS.</xsl:when>
            <xsl:when test="$mapKey = '5'">INITIALS</xsl:when>
            <xsl:when test="$mapKey = '6'">DOCTOR</xsl:when>
            <xsl:when test="$mapKey = '7'">REVEREND</xsl:when>
            <xsl:when test="$mapKey = '8'">FIRM NAME</xsl:when>
            <xsl:when test="$mapKey = '9'">MISS</xsl:when>
            <xsl:when test="$mapKey = 'A'">MS.</xsl:when>
            <xsl:when test="$mapKey = 'B'"/>
            <xsl:when test="$mapKey = 'C'">CITY AND COUNTY GOVERNMENT</xsl:when>
            <xsl:when test="$mapKey = 'D'">DEALER NAME</xsl:when>
            <xsl:when test="$mapKey = 'M'">MANUFACTURER</xsl:when>
            <xsl:when test="$mapKey = 'S'">STATE GOVERNMENT</xsl:when>
            <xsl:when test="$mapKey = 'R'">FEDERAL GOVERNMENT</xsl:when>
            <xsl:when test="$mapKey = 'T'">TRUST NAME</xsl:when>
            <xsl:when test="$mapKey = 'U'"/>
            <xsl:when test="$mapKey = 'Z'"/>
            <xsl:otherwise>$mapKey</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="mapNamePrefixIfExists">
        <xsl:param name="selectNode"/>
        <xsl:if test="$selectNode/text()">
            <xsl:call-template name="mapNamePrefix">
                <xsl:with-param name="mapKey" select="$selectNode"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template name="dmvLeaseIndLookup">
        <xsl:param name="mapKey"/>
        <xsl:choose>
            <xsl:when test="$mapKey = '0'">Name unassigned /address unassigned</xsl:when>
            <xsl:when test="$mapKey = '1'">Lessee name / address unassigned</xsl:when>
            <xsl:when test="$mapKey = '2'">Lessor name / address unassigned</xsl:when>
            <xsl:when test="$mapKey = '3'">Name unassigned / lessee address</xsl:when>
            <xsl:when test="$mapKey = '4'">Lessee name / lessee address</xsl:when>
            <xsl:when test="$mapKey = '5'">Lessor name / lessee address</xsl:when>
            <xsl:when test="$mapKey = '6'">Name unassigned / lessor address</xsl:when>
            <xsl:when test="$mapKey = '7'">Lessee name / lessor address</xsl:when>
            <xsl:when test="$mapKey = '8'">Lessor name / lessor address</xsl:when>
            <xsl:otherwise>notFound</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="transactionDateLabelLookup">
        <xsl:param name="lookupKey"/>
        <xsl:choose>
            <xsl:when test="$lookupKey = '1'">Title Date:</xsl:when>
            <xsl:otherwise>Registration Date:</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
