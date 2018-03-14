<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="wrapPublicRecordsName">
		<xsl:param name="label" select="'&pr_name;'"/>
		<xsl:param name="prefixName" select="/.."/>
		<xsl:param name="firstName" select="/.."/>
		<xsl:param name="middleName" select="/.."/>
		<xsl:param name="lastName" select="/.."/>
		<xsl:param name="suffixName" select="/.."/>
		<xsl:param name="professionalSuffixName" select="/.."/>
		<xsl:param name="lastNameFirst" select="false()"/>
        <xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>

		<tr class="&pr_item;">
			<th>
				<xsl:value-of select="$label"/>
			</th>
			<td>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="prefixName" select="$prefixName"/>
					<xsl:with-param name="firstName" select="$firstName"/>
					<xsl:with-param name="middleName" select="$middleName"/>
					<xsl:with-param name="lastName" select="$lastName"/>
					<xsl:with-param name="suffixName" select="$suffixName"/>
					<xsl:with-param name="professionalSuffixName" select="$professionalSuffixName"/>
					<xsl:with-param name="lastNameFirst" select="$lastNameFirst"/>
					<xsl:with-param name="searchableLink" select="$searchableLink"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<!-- Various name components -->
	<xsl:template name="FormatName">
		<xsl:param name="prefixName" select="/.."/>
		<xsl:param name="firstName" select="/.."/>
		<xsl:param name="middleName" select="/.."/>
		<xsl:param name="lastName" select="/.."/>
		<xsl:param name="suffixName" select="/.."/>
		<xsl:param name="professionalSuffixName" select="/.."/>
		<xsl:param name="lastNameFirst" select="false()"/>
		<xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>

		<xsl:variable name="linkContents">
			<xsl:if test="$lastNameFirst=true() and normalize-space($lastName)">
				<xsl:value-of select="normalize-space($lastName)"/>
				<xsl:if test="normalize-space($prefixName) or normalize-space($firstName) or normalize-space($middleName) 
								or normalize-space($suffixName) or normalize-space($professionalSuffixName)">
					<xsl:text>,<![CDATA[ ]]></xsl:text>
				</xsl:if>
			</xsl:if>

			<xsl:if test="normalize-space($prefixName)">
				<xsl:value-of select="normalize-space($prefixName)"/>
				<xsl:choose>
					<xsl:when test="normalize-space($firstName) or normalize-space($middleName) or (string($lastNameFirst)='false' and normalize-space($lastName))">
						<xsl:text><![CDATA[ ]]></xsl:text>
					</xsl:when>
					<xsl:when test="normalize-space($suffixName) or normalize-space($professionalSuffixName)">
						<xsl:text>,<![CDATA[ ]]></xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:if>

			<xsl:if test="normalize-space($firstName)">
				<xsl:value-of select="normalize-space($firstName)"/>
				<xsl:choose>
					<xsl:when test="normalize-space($middleName) or (string($lastNameFirst)='false' and normalize-space($lastName))">
						<xsl:text><![CDATA[ ]]></xsl:text>
					</xsl:when>
					<xsl:when test="normalize-space($suffixName) or normalize-space($professionalSuffixName)">
						<xsl:text>,<![CDATA[ ]]></xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:if>

			<xsl:if test="normalize-space($middleName)">
				<xsl:value-of select="normalize-space($middleName)"/>
				<xsl:choose>
					<xsl:when test="string($lastNameFirst)='false' and normalize-space($lastName)">
						<xsl:text><![CDATA[ ]]></xsl:text>
					</xsl:when>
					<xsl:when test="normalize-space($suffixName) or normalize-space($professionalSuffixName)">
						<!-- lastNameFirst is true. -->
						<xsl:text>,<![CDATA[ ]]></xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:if>

			<xsl:if test="string($lastNameFirst)='false' and normalize-space($lastName)">
				<xsl:value-of select="normalize-space($lastName)"/>
				<xsl:if test="normalize-space($suffixName) or normalize-space($professionalSuffixName)">
					<xsl:text>,<![CDATA[ ]]></xsl:text>
				</xsl:if>
			</xsl:if>

			<xsl:if test="normalize-space($suffixName)">
				<xsl:value-of select="normalize-space($suffixName)"/>
				<xsl:if test="normalize-space($professionalSuffixName)">
					<xsl:text>,<![CDATA[ ]]></xsl:text>
				</xsl:if>
			</xsl:if>

			<xsl:if test="normalize-space($professionalSuffixName)">
				<xsl:value-of select="normalize-space($professionalSuffixName)"/>
			</xsl:if>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$searchableLink='true' or $searchableLink='TRUE'">
				<xsl:choose>
					<xsl:when test="contains($firstName, ' ') and string($lastName)=''">
						<xsl:variable name="lastNameLink">
							<xsl:value-of select="substring-after($firstName, ' ')"/>
						</xsl:variable>
						<xsl:variable name="firstNameLink">
							<xsl:value-of select="substring-before($firstName, ' ')"/>
						</xsl:variable>
						<xsl:call-template name="CreateLinkedName">
							<xsl:with-param name="firstName" select="$firstNameLink"/>
							<xsl:with-param name="lastName" select="$lastNameLink"/>
							<xsl:with-param name="linkContents" select="$linkContents"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="CreateLinkedName">
							<xsl:with-param name="firstName" select="$firstName"/>
							<xsl:with-param name="lastName" select="$lastName"/>
							<xsl:with-param name="linkContents" select="$linkContents"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$linkContents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="CreateLinkedName">
		<xsl:param name="firstName" select="/.."/>
		<xsl:param name="lastName" select="/.."/>
		<xsl:param name="linkContents" select="/.."/>

		<xsl:variable name="searchUrl">
			<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'searchType=Name', concat('first=', $firstName), concat('last=', $lastName), concat('PU=', $PermissibleUse), $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
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
	</xsl:template>


	<!-- Various name components -->
	<xsl:template name="FormatNameLastFirst">
		<xsl:param name="prefixName" select="/.."/>
		<xsl:param name="firstName" select="/.."/>
		<xsl:param name="middleName" select="/.."/>
		<xsl:param name="lastName" select="/.."/>
		<xsl:param name="suffixName" select="/.."/>
		<xsl:param name="professionalSuffixName" select="/.."/>
        <xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>

		<xsl:call-template name="FormatName">
			<xsl:with-param name="prefixName" select="$prefixName"/>
			<xsl:with-param name="firstName" select="$firstName"/>
			<xsl:with-param name="middleName" select="$middleName"/>
			<xsl:with-param name="lastName" select="$lastName"/>
			<xsl:with-param name="suffixName" select="$suffixName"/>
			<xsl:with-param name="professionalSuffixName" select="$professionalSuffixName"/>
			<xsl:with-param name="searchableLink" select="$searchableLink"/>
			<xsl:with-param name="lastNameFirst" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- business names -->
	<xsl:template name="FormatBusinessName">
		<xsl:param name="cite" select="."/>
		<xsl:param name="text" select="."/>
		<xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>
		<xsl:variable name="searchableCompanyName">
			<xsl:choose>
				<xsl:when test="normalize-space($cite)">
					<xsl:value-of select="$cite"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$text"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$searchableLink='true' or $searchableLink='TRUE'">
				<xsl:variable name="searchUrl">
					<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'searchType=CompanyName', concat('CompanyName=', $searchableCompanyName), concat('PU=', $PermissibleUse), $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
				</xsl:variable>
				<a>
					<xsl:attribute name="class">
						<xsl:text>&pr_link;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="$searchUrl"/>
					</xsl:attribute>
					<xsl:value-of select="$text"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
</xsl:stylesheet>