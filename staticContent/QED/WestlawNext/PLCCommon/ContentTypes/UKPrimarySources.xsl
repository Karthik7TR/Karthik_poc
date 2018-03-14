<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternationalLogos.xsl" forceDefaultProduct="true"/>
	<xsl:include href="UKPrimarySourcesToc.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name ="contentType" select ="'&ukPrimarySourcesContent;'"/>

	<xsl:template match="Document" priority="1">
		<xsl:call-template name="GeneralDocument" />
	</xsl:template>
	
	<xsl:template name="BuildDocumentTypeAttribute">
		<xsl:attribute name ="data-documenttype">
			<xsl:value-of select="$contentType"/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="ShowWestlawUkLogo"/>
	<xsl:template name="BuildLoggedOutDocumentHeaderContent"/>

	<xsl:template name="BuildDocumentHeaderContent">
		<xsl:apply-templates select="n-metadata/metadata.block/md.descriptions/md.title" />
	</xsl:template>

	<xsl:template name="BuildDocumentBody">
		<xsl:apply-templates select="descendant::primary.source"/>
	</xsl:template>

	<xsl:template name="BuildEndOfDocument">
		<xsl:call-template name="EmptyEndOfDocument">
			<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="primary.source">
		<xsl:apply-templates select="abstract"/>
		<xsl:call-template name="BuildMaterials"/>
		<xsl:apply-templates select="body/primary.source.related.links"/>
	</xsl:template> 

	<xsl:template name="BuildMaterials">
		<div id="&linksToPageSection;" class="&docDivision;">
			<xsl:call-template name="BuildHeading">
				<xsl:with-param name="headingAnchorText">&primarySourcesMaterialsAnchorText;</xsl:with-param>
				<xsl:with-param name="headingCaption">&primarySourcesMaterialsSectionHeading;</xsl:with-param>
				<xsl:with-param name="useRule" select="'true'"/>
			</xsl:call-template>

			<ul class ="&coAssetList;">
				<xsl:call-template name="WestlawUKLinks" />
				<xsl:apply-templates select="//cmd.primary.source.uris" />
			</ul>
		</div>
	</xsl:template>

	<xsl:template match="primary.source.related.links/head">
		<div class="&docDivision;">
			<xsl:call-template name="BuildHeading">
				<xsl:with-param name="headingAnchorText">&primarySourcesProvisionsAnchorText;</xsl:with-param>
				<xsl:with-param name="headingCaption">&primarySourcesProvisionsSectionHeading;</xsl:with-param>
				<xsl:with-param name="useRule" select="'true'"/>
			</xsl:call-template>
		</div>
	</xsl:template>
	

	<xsl:template match="primary.source.related.links[count(child::*) > 0]">
		<xsl:element name="div">
			<xsl:attribute name = "class">
				<xsl:value-of select = "'&specificProvisionCoverageSection; '"/>
				<xsl:value-of select = "'&docDivision;'"/>
			</xsl:attribute>

			<xsl:apply-templates />

		</xsl:element>
	</xsl:template>

	<xsl:template name="BuildMetaInfoColumnContent">
		<xsl:apply-templates select="//plcmd.is.maintained[.='true']" />
		<xsl:apply-templates select="//primary.source//resource.type" />
		<xsl:apply-templates select="//md.jurisdictions" />
	</xsl:template>

	<xsl:template match="plcmd.is.maintained[.='true']">
		<div>
			<span class="&maintainedStatus;">&MaintainedText;</span>
		</div>
	</xsl:template>

	<xsl:template match="metadata.block/md.jurisdictions">
		<xsl:if test="count(/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction[md.juriscountry.fullname != '&jurisdictionIgnore;'] ) > 0">
			<xsl:variable name="jurisdictionsHeader">
				<xsl:choose>
					<xsl:when test="count(/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction[md.juriscountry.fullname != '&jurisdictionIgnore;'] ) = 1">
						&jurisdictionHeader;
					</xsl:when>
					<xsl:otherwise>
						&jurisdictionsHeader;
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:call-template name="BuildMetaField">
				<xsl:with-param name="fieldClass" select="'&metaJurisdiction;'" />
				<xsl:with-param name="fieldCaption" select="$jurisdictionsHeader"/>
				<xsl:with-param name="fieldContent">
					<xsl:for-each select="md.jurisdiction[md.juriscountry.fullname != '&jurisdictionIgnore;' and not(md.juriscountry.fullname=preceding::md.juriscountry.fullname)]">
						<xsl:sort select="concat(md.jurisstate.fullname, md.juriscountry.fullname)" />

						<xsl:choose>
							<xsl:when test="md.jurisstate.fullname">
								<xsl:value-of select="md.jurisstate.fullname"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="md.juriscountry.fullname"/>
							</xsl:otherwise>
						</xsl:choose>

						<xsl:if test="following-sibling::md.jurisdiction[md.juriscountry.fullname != '&jurisdictionIgnore;' and not(md.juriscountry.fullname=preceding::md.juriscountry.fullname)]">
							<xsl:text>, </xsl:text>
						</xsl:if>
					</xsl:for-each>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="primary.source.related.links/list">
		<ul class="&coAssetList;">
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<xsl:template match="primary.source.related.links/list/list.item/para">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="primary.source.related.links/list/list.item/para/paratext">
		<xsl:apply-templates />
	</xsl:template>

</xsl:stylesheet>
