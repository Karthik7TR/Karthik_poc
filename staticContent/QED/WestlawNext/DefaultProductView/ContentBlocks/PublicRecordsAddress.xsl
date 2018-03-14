<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="wrapPublicRecordsAddress">
		<xsl:param name="label" select="'&pr_address;'"/>
		<xsl:param name="fullStreet" select="/.."/>
		<xsl:param name="streetNum" select="/.."/>
		<xsl:param name="streetDirection" select="/.."/>
		<xsl:param name="street" select="/.."/>
		<xsl:param name="streetSuffix" select="/.."/>
		<xsl:param name="streetDirectionSuffix" select="/.."/>
        <xsl:param name="streetUnitType" select="/.."/>
		<xsl:param name="streetUnitNumber" select="/.."/>
		<xsl:param name="streetUnit" select="/.."/>
		<xsl:param name="streetLineTwo" select="/.."/>
		<xsl:param name="city" select="/.."/>
		<xsl:param name="stateOrProvince" select="/.."/>
		<xsl:param name="zip" select="/.."/>
		<xsl:param name="zipExt" select="/.."/>
		<xsl:param name="country" select="/.."/>
		<xsl:param name="carrierRoute" select="/.."/>
		<xsl:param name="oneLine" select="false()"/>
        <xsl:param name="searchableLink" select="'TRUE'"/> <!-- this is always true for addresses since they can also have an "address map" link in addtion to a "search" link-->
		<xsl:param name="divClass"/>
		<!-- TODO:TM - Include County? -->

		<tr>
			<xsl:attribute name="class">
				<xsl:text>&pr_item;</xsl:text>
				<xsl:if test="$divClass">
					<xsl:value-of select="concat(' ', $divClass)"/>
				</xsl:if>
			</xsl:attribute>
			<th>
				<xsl:value-of select="$label"/>
			</th>
			<td>
				<xsl:call-template name="FormatAddress">
					<xsl:with-param name="fullStreet" select="$fullStreet"/>
					<xsl:with-param name="streetNum" select="$streetNum"/>
					<xsl:with-param name="streetDirection" select="$streetDirection"/>
					<xsl:with-param name="street" select="$street"/>
					<xsl:with-param name="streetSuffix" select="$streetSuffix"/>
					<xsl:with-param name="streetDirectionSuffix" select="$streetDirectionSuffix"/>
          <xsl:with-param name="streetUnitType" select="$streetUnitType"/>
					<xsl:with-param name="streetUnitNumber" select="$streetUnitNumber"/>
					<xsl:with-param name="streetUnit" select="$streetUnit"/>
					<xsl:with-param name="streetLineTwo" select="$streetLineTwo"/>
					<xsl:with-param name="city" select="$city"/>
					<xsl:with-param name="stateOrProvince" select="$stateOrProvince"/>
					<xsl:with-param name="zip" select="$zip"/>
					<xsl:with-param name="zipExt" select="$zipExt"/>
					<xsl:with-param name="country" select="$country"/>
					<xsl:with-param name="carrierRoute" select="$carrierRoute"/>
					<xsl:with-param name="oneLine" select="$oneLine"/>
					<xsl:with-param name="searchableLink" select="$searchableLink"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="FormatAddress">
		<xsl:param name="fullStreet" select="/.."/>
		<xsl:param name="streetNum" select="/.."/>
		<xsl:param name="streetDirection" select="/.."/>
		<xsl:param name="street" select="/.."/>
		<xsl:param name="streetSuffix" select="/.."/>
		<xsl:param name="streetDirectionSuffix" select="/.."/>
        <xsl:param name="streetUnitType" select="/.."/>
		<xsl:param name="streetUnitNumber" select="/.."/>
		<xsl:param name="streetUnit" select="/.."/>
		<xsl:param name="streetLineTwo" select="/.."/>
		<xsl:param name="city" select="/.."/>
		<xsl:param name="stateOrProvince" select="/.."/>
		<xsl:param name="zip" select="/.."/>
		<xsl:param name="zipExt" select="/.."/>
		<xsl:param name="country" select="/.."/>
		<xsl:param name="carrierRoute" select="/.."/>
		<xsl:param name="oneLine" select="false()"/>
        <xsl:param name="searchableLink" select="'TRUE'"/> <!-- this is always true for addresses since they can also have an "address map" link in addtion to a "search" link-->

		<xsl:variable name="addressLineOne">
			<xsl:choose>
				<xsl:when test="normalize-space($fullStreet)">
					<xsl:apply-templates select="$fullStreet"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="normalize-space($streetNum)">
						<xsl:apply-templates select="$streetNum"/>
						<xsl:if test="normalize-space($streetDirection) or normalize-space($street) or normalize-space($streetSuffix)
										or normalize-space($streetDirectionSuffix) or normalize-space($streetUnitNumber) or normalize-space($streetUnit)">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test="normalize-space($streetDirection)">
						<xsl:apply-templates select="$streetDirection"/>
						<xsl:if test="normalize-space($street) or normalize-space($streetSuffix) or normalize-space($streetDirectionSuffix)
										or normalize-space($streetUnitNumber) or normalize-space($streetUnit)">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test="normalize-space($street)">
						<xsl:apply-templates select="$street"/>
						<xsl:if test="normalize-space($streetSuffix) or normalize-space($streetDirectionSuffix) or normalize-space($streetUnitNumber)
										or normalize-space($streetUnit)">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test="normalize-space($streetSuffix)">
						<xsl:apply-templates select="$streetSuffix"/>
						<xsl:if test="normalize-space($streetDirectionSuffix) or normalize-space($streetUnitNumber)	or normalize-space($streetUnit)">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test="normalize-space($streetDirectionSuffix)">
						<xsl:apply-templates select="$streetDirectionSuffix"/>
						<xsl:if test="normalize-space($streetUnitNumber) or normalize-space($streetUnit)">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:if>
          <xsl:if test="normalize-space($streetUnitType)">
            <xsl:apply-templates select="$streetUnitType"/>
            <xsl:if test="normalize-space($streetUnitType)">
              <xsl:text><![CDATA[ ]]></xsl:text>
            </xsl:if>
          </xsl:if>         
					<xsl:if test="normalize-space($streetUnitNumber)">
						<xsl:apply-templates select="$streetUnitNumber"/>
						<xsl:if test="normalize-space($streetUnit)">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test="normalize-space($streetUnit)">
						<xsl:apply-templates select="$streetUnit"/>
					</xsl:if>
				</xsl:otherwise>

			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="addressLineTwo">
			<xsl:if test="normalize-space($streetLineTwo)">
				<xsl:apply-templates select="$streetLineTwo"/>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="addressCityStateZipLine">
			<xsl:apply-templates select="$city"/>
			<xsl:if test="normalize-space($city) and ( normalize-space($stateOrProvince) or normalize-space($zip) or normalize-space($country) )">
				<xsl:text>,<![CDATA[ ]]></xsl:text>
			</xsl:if>
			<xsl:if test="normalize-space($stateOrProvince)">
				<xsl:apply-templates select="$stateOrProvince"/>
				<xsl:if test="(string-length(normalize-space($zip))&gt;4 and not(normalize-space($zip)='00000')) or normalize-space($country)">
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:if>
			</xsl:if>
			<xsl:if test="string-length(normalize-space($zip))&gt;4 and not(normalize-space($zip)='00000')">
				<xsl:apply-templates select="$zip"/>
				<xsl:if test="string-length(normalize-space($zipExt))=4 and not(normalize-space($zipExt)='0000')">
					<xsl:text>-</xsl:text>
					<xsl:apply-templates select="$zipExt"/>
				</xsl:if>
				<xsl:if test="normalize-space($carrierRoute)">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:apply-templates select="$carrierRoute"/>
				</xsl:if>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="linkContents">
			<xsl:copy-of select="$addressLineOne"/>

			<xsl:if test="normalize-space($addressLineTwo)">
				<xsl:choose>
					<xsl:when test="$oneLine=true()">
						<xsl:if test="normalize-space($addressLineOne)">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
						<xsl:copy-of select="$addressLineTwo"/>
					</xsl:when>
					<xsl:when test="not(normalize-space($addressLineOne))">
						<xsl:copy-of select="$addressLineTwo"/>
					</xsl:when>
					<xsl:otherwise>
						<div>
							<xsl:copy-of select="$addressLineTwo"/>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>

			<xsl:if test="normalize-space($addressCityStateZipLine)">
				<xsl:choose>
					<xsl:when test="$oneLine=true()">
						<xsl:if test="normalize-space($addressLineOne) or normalize-space($addressLineTwo)">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
						<xsl:copy-of select="$addressCityStateZipLine"/>
					</xsl:when>
					<xsl:when test="not(normalize-space($addressLineOne)) and not(normalize-space($addressLineTwo))">
						<xsl:copy-of select="$addressCityStateZipLine"/>
					</xsl:when>
					<xsl:otherwise>
						<div>
							<xsl:copy-of select="$addressCityStateZipLine"/>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>

			<xsl:if test="normalize-space($country)">
				<xsl:choose>
					<xsl:when test="$oneLine=true()">
						<xsl:if test="normalize-space($addressLineOne) or normalize-space($addressLineTwo)
								or normalize-space($addressCityStateZipLine)">
							<xsl:text>,<![CDATA[ ]]></xsl:text>
							<xsl:apply-templates select="$country"/>
						</xsl:if>
					</xsl:when>
					<xsl:when test="not(normalize-space($addressLineOne)) and not(normalize-space($addressLineTwo))
							  and not(normalize-space($addressCityStateZipLine))">
						<xsl:apply-templates select="$country"/>
					</xsl:when>
					<xsl:otherwise>
						<div>
							<xsl:apply-templates select="$country"/>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$searchableLink='true' or $searchableLink='TRUE'">
				<xsl:variable name="searchUrl">
					<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'searchType=Address', concat('address=', $addressLineOne), concat('city=', $city), concat('state=', $stateOrProvince), concat('zip=', $zip), concat('PU=', $PermissibleUse), $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
				</xsl:variable>
				<a>
					<xsl:attribute name="class">
						<xsl:text>&pr_link;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="$searchUrl"/>
					</xsl:attribute>
					<xsl:copy-of select="$linkContents"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$linkContents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
