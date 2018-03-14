<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:msxsl="urn:schemas-microsoft-com:xslt">

	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="result">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsQsentClass;'" />
			<xsl:with-param name="dualColumn" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsHeader">
		<!--
		    Summary?
			"/result/resultDisplayName"
			"/result/resultDisplayPhone"
		-->
	</xsl:template>

	<xsl:template name="PublicRecordsMainColumn">
		<xsl:call-template name="QSentInfo"/>
	</xsl:template>

	<xsl:template name="QSentInfo">

		<xsl:choose>
			<xsl:when test="contains(/result/dataSourceDataSource, 'SS7')">
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_header;'" />
					<xsl:with-param name="contents" select="'Real-Time Phone'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapPublicRecordsSection">
					<xsl:with-param name="class" select="'&pr_header;'" />
					<xsl:with-param name="contents" select="'Transunion Phone Record'" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
			
		<xsl:if test="/result/dataSourceCreationDate or /result/dataSourceTransactionDate or /result/dataSourceDataSource or /result/dataSourceListingType">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'Data Source'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Source:'"/>
					<xsl:with-param name="selectNode" select="/result/dataSourceDataSource"/>
				</xsl:call-template>

				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Listing Type:'"/>
					<xsl:with-param name="selectNode" select="/result/dataSourceListingType"/>
				</xsl:call-template>

				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Listing Transaction Date:'"/>
					<xsl:with-param name="selectNode" select="/result/dataSourceTransactionDate"/>
				</xsl:call-template>
				
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Creation Date:'"/>
					<xsl:with-param name="selectNode" select="/result/dataSourceCreationDate"/>
				</xsl:call-template>
			</table>		
		</xsl:if>
			
		<xsl:if test="/result/phone or /result/phoneGenericName or /result/phoneStatusCode or /result/phoneServiceClass or /result/phonePortingCode or /result/contactPrivacyIndicator">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'Phone'"/>
			</xsl:call-template>
			<table class="&pr_table;">	
				<xsl:call-template name="displayPhone">
					<xsl:with-param name="rawPhone" select="/result/phone"/>
					<xsl:with-param name="label" select="'Phone:'"/>
				</xsl:call-template>	
			
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Generic Name:'"/>
					<xsl:with-param name="selectNode" select="/result/phoneGenericName"/>
				</xsl:call-template>
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Status:'"/>
					<xsl:with-param name="selectNode" select="/result/phoneStatusCode"/>
				</xsl:call-template>	
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Line Type:'"/>
					<xsl:with-param name="selectNode" select="/result/phoneServiceClass"/>
				</xsl:call-template>			
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Porting:'"/>
					<xsl:with-param name="selectNode" select="/result/phonePortingCode"/>
				</xsl:call-template>
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Listing Category:'"/>
					<xsl:with-param name="selectNode" select="/result/contactPrivacyIndicator"/>
				</xsl:call-template>
			</table>
		</xsl:if>

		<xsl:if test="/result/contactFirstName or /result/contactLastName or /result/contactListingName or /result/contactAddress or /result/contactCity or /result/contactState">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'Subscriber'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:call-template name="wrapPublicRecordsNameWrapper">
					<xsl:with-param name="label" select="'&pr_name;'"/>
					<xsl:with-param name="firstName" select ="/result/contactFirstName"/>
					<xsl:with-param name="lastName" select ="/result/contactLastName"/>
				</xsl:call-template>
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Listing Name:'"/>
					<xsl:with-param name="selectNode" select="/result/contactListingName"/>
				</xsl:call-template>
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Listing Description:'"/>
					<xsl:with-param name="selectNode" select="/result/contactListingDescription"/>
				</xsl:call-template>

				<xsl:call-template name="displayEmail">
					<xsl:with-param name="email" select="/result/contactEmail"/>
				</xsl:call-template>
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Deceased Date:'"/>
					<xsl:with-param name="selectNode" select="/result/contactDeceasedDate"/>
				</xsl:call-template>
			
				<xsl:if test="/result/contactAddress or /result/contactCity or /result/contactState">
					<xsl:call-template name="wrapPublicRecordsAddress">
						<xsl:with-param name="street" select="/result/contactAddress"/>
						<xsl:with-param name="city" select="/result/contactCity"/>
						<xsl:with-param name="stateOrProvince" select="/result/contactState"/>
						<xsl:with-param name="zip" select="/result/contactPostalCode"/>
						<xsl:with-param name="zipExt" select="/result/contactPostalCodeDetails"/>
					</xsl:call-template>
				</xsl:if>	
			
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Address Type:'"/>
					<xsl:with-param name="selectNode" select="/result/contactAddressType"/>
				</xsl:call-template>
			
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Latitude:'"/>
					<xsl:with-param name="selectNode" select="/result/contactLatitude"/>
				</xsl:call-template>
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Longitude:'"/>
					<xsl:with-param name="selectNode" select="/result/contactLongitude"/>
				</xsl:call-template>

				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'MSA:'"/>
					<xsl:with-param name="selectNode" select="/result/contactMSA"/>
				</xsl:call-template>
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'CMSA:'"/>
					<xsl:with-param name="selectNode" select="/result/contactCMSA"/>
				</xsl:call-template>
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'HHD:'"/>
					<xsl:with-param name="selectNode" select="/result/contactHHD"/>
				</xsl:call-template>

				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'FIPS:'"/>
					<xsl:with-param name="selectNode" select="/result/contactFIPS"/>
				</xsl:call-template>		
			</table>
		</xsl:if>


		<xsl:if test="/result/carrierName or /result/carrierAddress or /result/carrierCity or /result/carrierState">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'Carrier'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Name:'"/>
					<xsl:with-param name="selectNode" select="/result/carrierName"/>
				</xsl:call-template>
			
				<xsl:if test="/result/carrierAddress or /result/carrierCity or /result/carrierState">
					<xsl:call-template name="wrapPublicRecordsAddress">
						<xsl:with-param name="label" select="'Address:'"/>
						<xsl:with-param name="street" select="/result/carrierAddress"/>
						<xsl:with-param name="city" select="/result/carrierCity"/>
						<xsl:with-param name="stateOrProvince" select="/result/carrierState"/>
						<xsl:with-param name="zip" select="/result/carrierPostalCode"/>
					</xsl:call-template>
				</xsl:if>
			
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Affiliate:'"/>
					<xsl:with-param name="selectNode" select="/result/carrierAffiliate"/>
				</xsl:call-template>
			
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'OCN:'"/>
					<xsl:with-param name="selectNode" select="/result/carrierOcn"/>
				</xsl:call-template>
			</table>
		</xsl:if>

		<xsl:if test="/result/carrierContactFirstName or /result/carrierContactLastName or /result/carrierContactAddress or /result/carrierContactCity or /result/carrierContactState or /result/carrierContactEmail or /result/carrierContactPhone">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'Subpoena Contact'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:call-template name="wrapPublicRecordsNameWrapper">
					<xsl:with-param name="label" select="'&pr_name;'"/>
					<xsl:with-param name="firstName" select ="/result/carrierContactFirstName"/>
					<xsl:with-param name="lastName" select ="/result/carrierContactLastName"/>
				</xsl:call-template>
	
				<xsl:if test="/result/carrierContactAddress or /result/carrierContactCity or /result/carrierContactState">
					<xsl:call-template name="wrapPublicRecordsAddress">
						<xsl:with-param name="label" select="'Address:'"/>
						<xsl:with-param name="street" select="/result/carrierContactAddress"/>
						<xsl:with-param name="city" select="/result/carrierContactCity"/>
						<xsl:with-param name="stateOrProvince" select="/result/carrierContactState"/>
						<xsl:with-param name="zip" select="/result/carrierContactPostalCode"/>
					</xsl:call-template>
				</xsl:if>
			
				<xsl:call-template name="displayEmail">
					<xsl:with-param name="email" select="/result/carrierContactEmail"/>
				</xsl:call-template>

				<xsl:call-template name="displayPhone">
					<xsl:with-param name="rawPhone" select="/result/carrierContactPhone"/>
					<xsl:with-param name="label" select="'Phone:'"/>
				</xsl:call-template>	
			
				<xsl:call-template name="addFieldIfExists">
					<xsl:with-param name="label" select="'Extension:'"/>
					<xsl:with-param name="selectNode" select="/result/carrierContactExtension"/>
				</xsl:call-template>

				<xsl:call-template name="displayPhone">
					<xsl:with-param name="rawPhone" select="/result/carrierContactFax"/>
					<xsl:with-param name="label" select="'Fax:'"/>
				</xsl:call-template>	
			</table>
		</xsl:if>
			
	</xsl:template>

	<xsl:template name="addFieldIfExists">
		<xsl:param name="label" />
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

	<xsl:template name="toUpper">
		<xsl:param name="lowerCaseVar"/>
		<xsl:variable name="lowerCase" select="'abcdefghijklmnopqrstuvwxyz'" />
		<xsl:variable name="upperCase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
		<xsl:value-of select="translate($lowerCaseVar,$lowerCase,$upperCase)"/>
	</xsl:template>	
		
	<xsl:template name="displayPhone">
		<xsl:param name="rawPhone"/>
		<xsl:param name="label"/>		
		
		<xsl:if test="string-length($rawPhone) = 10 and number($rawPhone) != 'NaN'">
			
			<xsl:variable name="formattedPhone">
				<xsl:variable name ="NPA" select ="substring($rawPhone,1,3)"/>
				<xsl:variable name ="NNX" select ="substring($rawPhone,4,3)"/>
				<xsl:variable name ="XXXX" select ="substring($rawPhone,7,4)"/>

				<xsl:value-of select ="$NPA"/>
				<xsl:text>-</xsl:text>
				<xsl:value-of select ="$NNX"/>
				<xsl:text>-</xsl:text>
				<xsl:value-of select ="$XXXX"/>
			</xsl:variable>
	
			<xsl:variable name="searchUrl">
				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'searchType=Phone', concat('phone=', $formattedPhone), concat('PU=', $PermissibleUse), $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
			</xsl:variable>
			
			<tr class="&pr_item;">
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="contents" select="$label"/>
				</xsl:call-template>
				<td>
					<a>
						<xsl:attribute name="class">
							<xsl:text>&pr_link;</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="href">
							<xsl:copy-of select="$searchUrl"/>
						</xsl:attribute>
						<xsl:copy-of select="$formattedPhone"/>
					</a>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
		
	<xsl:template name="wrapPublicRecordsNameWrapper">
		<xsl:param name="label"/>
		<xsl:param name="firstName"/>
		<xsl:param name="lastName"/>

		<xsl:if test="$lastName/text()">
			<xsl:variable name="ucFirstName">
				<xsl:call-template name="toUpper">
					<xsl:with-param name="lowerCaseVar" select="$firstName"/>
				</xsl:call-template>
			</xsl:variable>
	
			<xsl:variable name="ucLastName">
				<xsl:call-template name="toUpper">
					<xsl:with-param name="lowerCaseVar" select="$lastName"/>
				</xsl:call-template>
			</xsl:variable>
			
			<xsl:call-template name="wrapPublicRecordsName">
				<xsl:with-param name="label" select="$label"/>
				<xsl:with-param name="firstName" select ="$ucFirstName"/>
				<xsl:with-param name="lastName" select ="$ucLastName"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="displayEmail">
		<xsl:param name="email"/>
		<xsl:if test="$email/text()">
			<tr class="&pr_item;">
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="contents" select="'Email:'"/>
				</xsl:call-template>
				<td>
					<a>
					<xsl:attribute name="href">
						mailto:<xsl:value-of select="$email" />
					</xsl:attribute>
					<xsl:value-of select="$email" />
					</a>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
