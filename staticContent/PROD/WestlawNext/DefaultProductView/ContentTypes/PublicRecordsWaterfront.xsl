<!--Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="PublicRecords.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- Do not render these nodes -->
	<xsl:template match="map|p|pc|legacy.id|col.key|str.nbr|str.na|sfx|unit.nbr"/>

	<!-- Render the CONTENT view. -->
	<xsl:template match="Document">
		<xsl:call-template name="PublicRecordsContent">
			<xsl:with-param name="container" select="'&contentTypePublicRecordsWaterfrontClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!--************************************************************************
	***********************************HEADER***********************************
	*************************************************************************-->
	<xsl:template name="PublicRecordsHeader">
		<xsl:call-template name="wrapPublicRecordsSection">
			<xsl:with-param name="class" select="'&pr_header;'" />
			<xsl:with-param name="contents" select="'&pr_waterfrontResidentsRecord;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PublicRecordsLeftColumn">
		<xsl:apply-templates select="$coverage-block">
			<xsl:with-param name="displayCoverageBeginDate" select="false()"/>
			<xsl:with-param name="displaySource" select="false()"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template name="PublicRecordsRightColumn">
		<xsl:call-template name="NameInfo"/>
		<!-- TODO:TM: Need to render tax assessor link here? -->
	</xsl:template>

	<!--*********************************************************************** 
	***************************  (B)Name Information  *************************
	************************************************************************-->
	<xsl:template name="NameInfo">
		<xsl:if test="descendant::na.b | descendant::addr.b | descendant::cnty |
				descendant::res.typ | descendant::msa | descendant::res.yr">
			<xsl:call-template name="wrapPublicRecordsSection">
				<xsl:with-param name="class" select="'&pr_subheader;'"/>
				<xsl:with-param name="contents" select="'&pr_nameInformationSubheader;'"/>
			</xsl:call-template>
			<table class="&pr_table;">
				<xsl:apply-templates select="descendant::na.b"/>
				<xsl:apply-templates select="descendant::addr.b"/>
				<xsl:apply-templates select="descendant::cnty"/>
				<xsl:apply-templates select="descendant::res.typ"/>
				<xsl:apply-templates select="descendant::msa"/>
				<xsl:apply-templates select="descendant::res.yr"/>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="na.b">
		<xsl:call-template name="wrapPublicRecordsName">
			<xsl:with-param name="label" select="'&pr_name;'"/>
			<xsl:with-param name="firstName" select="fna"/>
			<xsl:with-param name="middleName" select="mna"/>
			<xsl:with-param name="lastName" select="lna"/>
		</xsl:call-template>
	</xsl:template>

	<!--Address-->
	<xsl:template match="addr.b">
		<xsl:call-template name="wrapPublicRecordsAddress">
			<xsl:with-param name="defaultLabel" select="'&pr_address;'"/>
			<xsl:with-param name="fullStreet" select="full.str"/>
			<xsl:with-param name="streetNum" select="str.nbr"/>
			<xsl:with-param name="street" select="str|str.na"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
		</xsl:call-template>
	</xsl:template>

	<!--County-->
	<xsl:template match="cnty">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_county;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Residence Type-->
	<xsl:template match="res.typ">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_residenceType;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--MSA-->
	<xsl:template match="msa">
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="'&pr_msa;'"/>
		</xsl:call-template>
	</xsl:template>

	<!--Length of Residence-->
	<xsl:template match="res.yr[node()]">
		<tr>
			<th>
				<xsl:text>&pr_lengthOfResidence;</xsl:text>
			</th>
			<td>
				<xsl:apply-templates/>
				<xsl:text><![CDATA[ ]]>&pr_years;</xsl:text>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>