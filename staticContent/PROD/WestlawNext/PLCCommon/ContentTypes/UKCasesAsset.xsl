<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternationalLogos.xsl" forceDefaultProduct="true"/>
	<xsl:include href="UKCasesAssetToc.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name ="contentType" select ="'&ukCaseAssetContent;'"/>

	<xsl:template match="Document" priority="1">
		<xsl:call-template name="GeneralDocument" />
	</xsl:template>

	<xsl:template name="ShowWestlawUkLogo"/>
	<xsl:template name="BuildLoggedOutDocumentHeaderContent"/>

	<xsl:template name="BuildDocumentHeaderContent">
		<xsl:apply-templates select="n-metadata/metadata.block/md.descriptions/md.title" />
	</xsl:template>
	
	<xsl:template name="BuildDocumentTypeAttribute">
		<xsl:attribute name ="data-documenttype">
			<xsl:value-of select="$contentType"/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="BuildDocumentBody">
		<xsl:apply-templates select="descendant::case.page"/>
	</xsl:template>

	<xsl:template name="BuildEndOfDocument">
		<xsl:call-template name="EmptyEndOfDocument">
			<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.page">
		<xsl:apply-templates select="body/legal.update"/>
		<xsl:call-template name="BuildLinksToCase"/>
	</xsl:template>

	<xsl:template match="legal.update">
		<div id="&legalUpdatesSection;" class="&docDivision;">
			<xsl:call-template name="BuildHeading">
				<xsl:with-param name="headingAnchorText">&legalUpdatesAnchorText;</xsl:with-param>
				<xsl:with-param name="headingCaption">&legalUpdatesSectionHeading;</xsl:with-param>
			</xsl:call-template>

			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="legal.update/list">
		<ul class="&coAssetList;">
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<xsl:template match="legal.update/list/list.item/para">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="legal.update/list/list.item/para/paratext">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template name="BuildLinksToCase">
		<div id="&linksToPageSection;" class="&docDivision;">
			<xsl:call-template name="BuildHeading">
				<xsl:with-param name="headingAnchorText">&caseLinksAnchorText;</xsl:with-param>
				<xsl:with-param name="headingCaption">&caseLinksSectionHeading;</xsl:with-param>
				<xsl:with-param name="useRule" select="'true'"/>
			</xsl:call-template>

			<ul class ="&coAssetList;">
				<xsl:call-template name="WestlawUKLinks" />
				<xsl:apply-templates select="//cmd.primary.source.uris" />
			</ul>
		</div>
	</xsl:template>

	<xsl:template name="BuildMetaInfoColumnContent">
		<xsl:apply-templates select="//case.page//resource.type" />
		<!--<xsl:apply-templates select="//case.page//tribunal" />
		<xsl:apply-templates select="//case.page//chamber" />
		<xsl:apply-templates select="//case.page//specialist.tribunal" />-->
		<xsl:apply-templates select="//case.page//court.name" />
		<xsl:apply-templates select="//case.page//court.division" />
		<xsl:apply-templates select="//case.page//specialist.court" />
		<!--<xsl:apply-templates select="//case.page//forum" />-->
		<xsl:apply-templates select="//case.page//case.date" />
		<xsl:apply-templates select="//case.page//court.jurisdiction" />
		<xsl:apply-templates select="//case.page//cmd.related.citations"/>
	</xsl:template>

	<xsl:template match="court.name">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaCourt;'" />
			<xsl:with-param name="fieldCaption" select="'&ukCourtText;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="court.division">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaDivision;'" />
			<xsl:with-param name="fieldCaption" select="'&divisionText;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="court.jurisdiction">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaJurisdiction;'" />
			<xsl:with-param name="fieldCaption" select="'&jurisdictionOfCourtText;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.date">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaDate;'" />
			<xsl:with-param name="fieldCaption" select="'&judgmentCaptionDate;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:call-template name="formatYearMonthDayToDDMMMYYYY">
					<xsl:with-param name="date" select="./@formatted"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="specialist.court">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaSpecialistCourt;'" />
			<xsl:with-param name="fieldCaption" select="'&caseMetaSpecialistCourt;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="tribunal">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaTribunal;'" />
			<xsl:with-param name="fieldCaption" select="'&caseMetaTribunal;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="specialist.tribunal">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaSpecialistTribunal;'" />
			<xsl:with-param name="fieldCaption" select="'&caseMetaSpecialistTribunal;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="chamber">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaChamber;'" />
			<xsl:with-param name="fieldCaption" select="'&caseMetaChamber;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="forum">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaForum;'" />
			<xsl:with-param name="fieldCaption" select="'&caseMetaForum;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cmd.related.citations">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaWhereReported;'" />
			<xsl:with-param name="fieldCaption" select="'&judgmentCaptionWhereReported;'"/>
			<xsl:with-param name="fieldContent">
				<ul class="&whereReportedList;">
					<xsl:for-each select="./cmd.related.citation" >
						<li>
							<xsl:value-of select="."/>
						</li>
					</xsl:for-each>
				</ul>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
