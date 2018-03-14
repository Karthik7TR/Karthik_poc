<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Core things -->
	<xsl:param name="Guid" select="/Document/n-metadata/metadata.block/md.identifiers/md.uuid" />
	<xsl:param name="Cite" select="/Document/document-data/cite" />

	<!-- Create "Infinity" values using XPath 1.0 deficiencies -->
	<xsl:variable name="PositiveInfinity" select="1 div 0" />
	<xsl:variable name="NegativeInfinity" select="-1 div 0" />

	<!-- Global Parameters to be overridden from code/invocation -->
	<xsl:param name="Images" />
	<xsl:param name="ImagesPermaLink" />
	<xsl:param name="Website" />
	<xsl:param name="Document" />
	<xsl:param name="DataOrchestrationGateway" />

	<xsl:param name="SourceSerial" />
	<xsl:param name="Quotes" />
	<xsl:param name="Toggle" />
	<xsl:param name="IsSearched" />

	<xsl:param name="TOCLinks" />
	<xsl:param name="TOCNav" />

	<xsl:param name="mediaPageWidth" select="925"/>
	<xsl:param name="dualColumnGutter" select="20" />
	<xsl:param name="mediaPageHeight" select="$PositiveInfinity" />
	<xsl:param name="DeliveryMode" />
	<xsl:param name="AllowHeadnotesToChunk" select="false()" />
	<xsl:param name="HeadnoteDisplayOption" />
	<xsl:param name="ImageHost" />

	<xsl:param name="PreviewMode" />
	<xsl:param name="currentYear" />
	<xsl:param name="currentDate" />

	<!-- This is used by code and by xsl -->
	<xsl:param name="endOfDocumentCopyrightText" />
	
	<!-- Clarivate Analytics copyright text -->
	<xsl:param name="ClarivateAnalyricsCopyright" />
	
	<!-- Used to turn on/off features for WLNC Personal Injury content -->
	<xsl:variable name="PIDocument" select="false()" />

	<!-- This is primarily for building non-Cobalt (e.g. Web2) links -->
	<xsl:param name="contextualInfo" />

	<xsl:param name="DocumentCacheKey" />
	<xsl:param name="ListSource" />
	<xsl:param name="NavigationPath" />
	<xsl:param name="List" />
	<xsl:param name="Rank" />
	<xsl:param name="DocSource" />

	<!--  Used for 'pages with terms' changes in delivery  -->
	<xsl:param name="DisplayTermHighlighting" select="true()" />
	<xsl:param name="DisplayOnlyPagesWithSearchTerms" select="false()" />

	<!-- Primarily used for Case Notebook (WLX) -->
	<xsl:param name="IncludeCopyWithRefLinks" select="false()" />

	<!-- Used to update dockets -->
	<xsl:param name="DisplayDocketUpdateLink" select="false()" />
	<xsl:param name="DocketIsSlowCourt" select="false()" />
	<xsl:param name="JurisdictionNumber" select="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.jurisnum" />

	<!-- Used for dockets track -->
	<xsl:param name="DisplayDocketTrackLink" select="false()" />

	<!-- For links in Web2 -->
	<xsl:param name="SpecialVersionParam" />
	<xsl:param name="SpecialRequestSourceParam" />
	<xsl:param name="Target"/>

	<!-- For search term markup -->
	<xsl:param name="PrimaryTermsWordset" />
	<xsl:param name="SecondaryTermsWordset" />
	<xsl:param name="SearchWithinTermsWordset" />

	<!-- Display Original Image Link -->
	<xsl:param name="DisplayOriginalImageLink" select="true()"/>

	<xsl:param name="UseRelativePathForImages" select="false()" />

	<xsl:param name="UniqueIdForBlobs" />

	<!-- Allow Drag and Drop of links -->
	<xsl:param name="AllowLinkDragAndDrop" select="true()"/>

	<!-- Used to pass originating doc guid to PDF Blob controller to get Royalty Id-->
	<xsl:param name="UseBlobRoyaltyId" select="false()" />

	<!-- Dual Column Mode -->
	<xsl:param name="DualColumnMode" select="false()" />

	<!-- Delivery Format -->
	<xsl:param name="DeliveryFormat" />

	<xsl:param name="IsPersisted" select="false()" />

	<xsl:param name="EasyEditMode" select="false()" />

	<xsl:param name="DisplayEasyEditLink" select="true()" />

	<xsl:param name="ShowUnreleasedFeatures" select="false()" />

	<xsl:param name="IsIpad" select="false()" />
	<xsl:param name="IsIphone" select="false()" />
	
	<xsl:param name="StatusIndicatorHtml" select="''" />

	<xsl:param name="DeliveryPartialDocument" select="false()" />

	<!-- Delivery Styling Options -->
	<xsl:param name="LinkColor" />
	<xsl:param name="LinkUnderline" select="false()"/>
	<xsl:param name="FontSize" />

	<!-- Some documents (like easy edit dockets) need to have the links disabled and just show the text. -->
	<xsl:param name="DisplayLinksInDocument" select ="true()" />
	<xsl:param name="HasCalenderingInformation" select ="false()" />
	<xsl:param name="ListItemIdentifier" select="'Target'"/>

	<!-- StatutoryTextOnly - If this is true, we do not render Notes of Decisions in delivery. -->
	<xsl:param name="StatutoryTextOnly" select ="false()" />

	<!-- Effective dates for Versioned Statutes -->
	<xsl:param name="EffectiveStartDate" />
	<xsl:param name="EffectiveEndDate" />

	<xsl:param name="DisplayFormAssembleLink" select="true()" />
	<xsl:param name="IsMobile" select="false()" />

	<xsl:param name="HasDocketOrdersAccess" />
	<xsl:param name="HasPassThruPdfsAccess" />

	<!-- Citation Flag Parameters - citationFlagDataStatus will be set by the caller if they want to do anything with them, the other three are the allowed values we will test for in the code. -->
	<xsl:param name="citationFlagDataStatusLoading" select="'loading'" />
	<xsl:param name="citationFlagDataStatusSuccess" select="'success'" />
	<xsl:param name="citationFlagDataStatusFailed" select="'failed'" />

	<!-- Original Context (i.e tableOfAuthorities) -->
	<xsl:param name="OriginationContext" />

	<!-- Is public records workflow -->
	<xsl:param name="IsPublicRecords" select ="false()" />

	<!-- Indicates if embedded table of contents are to be shown. -->
	<xsl:param name="DisplayTableOfContents" />
	<xsl:param name ="IsQuickDraftDelivery" />

	<xsl:param name="ShowDocument" select ="true()"/>

	<xsl:param name="ShowDraftingNotes" select ="false()"/>

	<!-- Product view for Govt. Weblinks -->
	<xsl:param name="ProductView" />

	<!-- Embedded Text -->
	<xsl:param name="EmbeddedTextBasicMode" select="false()"/>
	<xsl:param name="EmbeddedTextMode" select="false()"/>

	<xsl:param name="IsAnonymousSession" select="false()" />
	
	<xsl:param name="AllowResizeImageOnDelivery" select="false()" />
	<xsl:param name="EnableBestPortionNavigation" select="false()" />
	<xsl:param name="DeliveryPageWidth" />
	<xsl:param name="DeliveryPageHeight" />

	<!-- IACs-->
	<xsl:param name="IAC-SNAP-SNIPPETS-DEBUGLOG" select="false()" />
	<xsl:param name="IAC-TRILLIUM-CRSW-DOC-ENHANCE" select="false()" />
	<xsl:param name="IAC-FAVORITE-DOCUMENT" />
	<xsl:param name="IAC-MATTERMAP-BETATEXT-OFF" select="false()" />
	<xsl:param name="IAC-PDF-MULTIPART-CHECK" select="false()" />
	<xsl:param name="IAC-DTP-BANKRUPTCY-CLAIMS" select="false()" />
	<xsl:param name="IAC-INDIGO-PPT" select="false()" />
	
	<!-- FACs-->
	<xsl:param name="DisplayInternalHeadnoteInfo" />
	<xsl:param name="AdcVersionRedliningToggle" select="false()" />
	<xsl:param name="PreReleaseContent" select="false()"/>
	<xsl:param name="IndigoDisplay" select="false()"/>
	<xsl:param name="ProceduralPostureFilter" select="false()"/>
	
	<!-- KnowHow RefId to Category Page Map -->
	<xsl:param name="RefIdCatPageMap" />
	
	<xsl:param name="lastViewed" />

	<!--CrossBorder-->
	<xsl:param name="CountryDocument" />
	<xsl:param name="IacNameCrossBorderFeature" />

	<xsl:param name="ProductViewName" />
	
	<!-- Know How filtered Nort Topics -->
	<xsl:param name="FilteredNortTopics" />
	<xsl:param name="DoTopicsFilter" select="false()" />
	
	<!-- Sign-on link for logged out document view -->
	<xsl:param name="SignOnUrl" />

	<xsl:param name="DocketGuidForLinkCreation" />

	<!-- Add resource history in document for delivery -->
	<xsl:param name="AddResourceHistory" />

	<!-- Add related content in document for delivery -->
	<xsl:param name="AddRelatedContent" />
    
	<!-- Add statutory annotations in document for delivery -->
	<xsl:param name="AddStatutoryAnnotations" />

</xsl:stylesheet>