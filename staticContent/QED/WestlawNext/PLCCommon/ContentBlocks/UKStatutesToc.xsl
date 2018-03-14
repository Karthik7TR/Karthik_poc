<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKGeneralBlocks.xsl"/>
	<xsl:include href="UkStatutesDocumentType.xsl"/>


	<xsl:template name ="BuildDocumentTocContent">
		<xsl:call-template name="WriteTocListOpen">
			<xsl:with-param name="includeClass" select="'&coKhTocOlList; &ukResearchToCList;'"/>
		</xsl:call-template>

		<xsl:choose>
			<xsl:when test="$isArrangementDocument=true()">
				<xsl:call-template name="ArrangementToc" />
			</xsl:when>
			<xsl:when test="$documentType = '&legisTypeBill;'">
				<xsl:call-template name="ProvisionBillToc" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="ProvisionToc" />
			</xsl:otherwise>
		</xsl:choose>

		<xsl:call-template name="WriteTocListClose"/>
	</xsl:template>

	<xsl:variable name ="annotationGuid" select ="//n-docbody/document/metadata.block/md.history/md.events/md.event/md.identifier.of.cited.doc"/>

	<xsl:template name="BuildJurisdictionsTocList">
		<xsl:param name="jurisdictionsListSelector"/>
		<xsl:for-each select ="$jurisdictionsListSelector">
			<xsl:sort select="@application"/>
			<xsl:call-template name="WriteTocItem">
				<xsl:with-param name="TocItemAnchor">
					<xsl:call-template name="GetAnchorId">
						<xsl:with-param name="section" select ="./@application"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="TocItemCaption">
					<xsl:call-template name="putJurisdictionName"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name ="ProvisionBillToc">
		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor" select="'#'" />
			<xsl:with-param name="IsAnchor" select="false()"/>
			<xsl:with-param name="TocItemCaption" select="'&legisTypeBill;'"/>
			<xsl:with-param name="TocItemHideBody" select="false()"/>
		</xsl:call-template>

		<xsl:if test ="//n-docbody/document/metadata.block/md.references/md.locatordoc">
			<xsl:call-template name="WriteTocItem">
				<xsl:with-param name="TocItemAnchor">
					<xsl:call-template name="GetDocumentUrl">
						<xsl:with-param name ="documentGuid" select="//n-docbody/document/metadata.block/md.references/md.locatordoc/@href" />
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="IsAnchor" select="false()"/>
				<xsl:with-param name="TocItemCaption" select="concat('&legisTypeBill;','&detailsPrimaryMenu;')"/>
				<xsl:with-param name="TocItemHideBody" select="true()"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name ="ProvisionToc">
		<!-- Provision -->
		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor" select="'#'" />
			<xsl:with-param name="IsAnchor" select="false()"/>
			<xsl:with-param name="TocItemCaption" select="'&provisionPrimaryMenu;'"/>
			<xsl:with-param name="TocItemClose" select="false()"/>
			<xsl:with-param name="TocItemHideBody" select="false()"/>
		</xsl:call-template>

		<xsl:call-template name="WriteTocListOpen">
			<xsl:with-param name="includeClass" select="''"/>
		</xsl:call-template>

		<!-- Jurisdictions -->
		<xsl:choose>
			<!-- for several jurisdictions -->
			<xsl:when test="count(//n-docbody/document/fulltext) > 1">
				<!--"Other Application" by default should be always be at the bottom of the various jurisdictions – England, Wales, Scotland-->
				<xsl:call-template name="BuildJurisdictionsTocList">
					<xsl:with-param name="jurisdictionsListSelector" select="//n-docbody/document/fulltext[@application != '&ukOtherApplicationCode;']"/>
				</xsl:call-template>
				<xsl:call-template name="BuildJurisdictionsTocList">
					<xsl:with-param name="jurisdictionsListSelector" select="//n-docbody/document/fulltext[@application = '&ukOtherApplicationCode;'][1]"/>
				</xsl:call-template>
			</xsl:when>
			<!-- for all in one link jurisdiction -->
			<xsl:when test="count(//n-docbody/document/fulltext) = 1">
				<xsl:call-template name="WriteTocItem">
					<xsl:with-param name="TocItemAnchor">
						<xsl:call-template name="GetAnchorId">
							<xsl:with-param name="section" select ="//n-docbody/document/fulltext/@application"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="TocItemCaption" select="'&allJurisdictionsSectionHeading;'" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>

		<!-- Notes -->
		<xsl:if test ="count(n-docbody/descendant::updatenote) > 0">
			<xsl:call-template name="WriteTocItem">
				<xsl:with-param name="TocItemAnchor">
					<xsl:call-template name="GetAnchorId">
						<xsl:with-param name="section" select ="'&notesSecondaryMenu;'"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="TocItemCaption" select="'&notesSecondaryMenu;'"/>
			</xsl:call-template>
		</xsl:if>

		<!--Statutory Annotations-->
		<xsl:if test ="//metadata.block/md.history/md.events/md.event/md.identifier.of.cited.doc/@type='annotation' and $annotationGuid != $Guid">
			<xsl:call-template name="WriteTocItem">

				<xsl:with-param name="TocItemAnchor">
					<xsl:call-template name="GetAnchorId">
						<xsl:with-param name="section" select ="'&annotationSecondaryMenu;'"/>
					</xsl:call-template>
				</xsl:with-param>

				<xsl:with-param name="TocItemCaption" select="'&annotationSecondaryMenu;'"/>
			</xsl:call-template>
		</xsl:if>

		<xsl:call-template name="WriteTocListClose"/>

		<xsl:call-template name="WriteTocItemClose"/>

		
		<!-- Provision details -->
		<xsl:if test ="//n-docbody/document/metadata.block/md.references/md.locatordoc[@anchorref='true']">
			<xsl:call-template name="WriteTocItem">
				<xsl:with-param name="TocItemAnchor">
					<xsl:call-template name="GetDocumentUrl">
						<xsl:with-param name ="documentGuid" select="//n-docbody/document/metadata.block/md.references/md.locatordoc/@href" />
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="IsAnchor" select="false()"/>
				<xsl:with-param name="TocItemCaption" select="concat('&provisionPrimaryMenu;','&detailsPrimaryMenu;')"/>
				<xsl:with-param name="TocItemHideBody" select="true()"/>
			</xsl:call-template>
		</xsl:if>

	</xsl:template>

	<xsl:template name ="ArrangementToc">
		<xsl:variable name ="arrangementType">
			<xsl:choose>
				<xsl:when test="$isArrangmentOfAct">
					<xsl:value-of select="'&legisTypeAct;'"/>
				</xsl:when>
				<xsl:when test="$isArrangmentOfSI">
					<xsl:value-of select="'&legisTypeSI;'"/>
				</xsl:when>
				<xsl:when test="$isArrangmentOfBill">
					<xsl:value-of select="'&legisTypeBill;'"/>
				</xsl:when>
				<xsl:when test="$isArrangmentOfProvisions">
					<xsl:value-of select="'&legisTypeProvisions;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&legisTypeDocument;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor" select="'#'" />
			<xsl:with-param name="IsAnchor" select="false()"/>
			<xsl:with-param name="TocItemCaption" select="concat('&arrangmentOfPrimaryMenu;',$arrangementType)"/>
			<xsl:with-param name="TocItemHideBody" select="false()"/>
		</xsl:call-template>

		<xsl:if test ="(//n-docbody/document/metadata.block/md.references/md.locatordoc) and not($isArrangmentOfProvisions=true())">
			<xsl:call-template name="WriteTocItem">
				<xsl:with-param name="TocItemAnchor">
					<xsl:call-template name="GetDocumentUrl">
						<xsl:with-param name ="documentGuid" select="//n-docbody/document/fulltext_metadata/overview/link/@tuuid" />
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="IsAnchor" select="false()"/>
				<xsl:with-param name="TocItemCaption" select="concat($arrangementType,'&detailsPrimaryMenu;')"/>
				<xsl:with-param name="TocItemHideBody" select="true()"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>




</xsl:stylesheet>
