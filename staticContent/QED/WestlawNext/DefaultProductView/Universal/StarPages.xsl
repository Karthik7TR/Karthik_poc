<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="StarPages.xsl" forcePlatform="true" />
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="Suppressed.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="displayableCitesWithoutStatus" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info/md.display.primarycite[@display = 'Y' and not(@status)] | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info/md.display.parallelcite[(@display = 'Y' or @userEntered = 'Y') and not(@status)]" />
	<xsl:variable name="eligiblePagesets" select="/Document/n-metadata/metadata.block/md.descriptions/md.westlawdescrip/md.starpages[@pageset and text() = 'Y']" />
	<xsl:variable name="eligibleCites" select="$displayableCitesWithoutStatus[@ID = $eligiblePagesets/@pageset and @type != 'Westlaw']" />
	<xsl:variable name="eligibleWestlawCites" select="$displayableCitesWithoutStatus[@ID = $eligiblePagesets/@pageset and @type = 'Westlaw']" />
	<xsl:variable name="displayableCitesForStarPaging" select="$eligibleCites | $eligibleWestlawCites[count($eligibleCites) = 0 or count($eligibleWestlawCites[@userEntered = 'Y' or @display = 'Y']) != 0]" />
	<xsl:variable name="displayWestlawCiteOnly" select="count($eligibleCites) = 0 and count($eligibleWestlawCites) &gt; 0"/>

	<!-- Handle Star Pages that do NOT have "bad" ancestors -->
	<xsl:template match="starpage.anchor[@pageset]" name="starpageWithPageSet">
		<xsl:call-template name="starpageWithPageSetWithParams">
			<xsl:with-param name="displayableCitesForStarPagingParam" select="$displayableCitesForStarPaging" />
		</xsl:call-template>
	</xsl:template>

	<!-- Callable template to actually display the Star Page XHTML when appropriate.
	     This is directly leveraged by ContentTypes which disregard the normal Star Paging rules, e.g. Briefs. -->
	<xsl:template name="displayStarPage">
		<xsl:param name="starPageText" />
		<xsl:param name="numberOfStars" />
		<xsl:param name="pageset" />
		<xsl:param name="id" />
		
		<xsl:call-template name="displayStarPageWithParams">
			<xsl:with-param name="starPageText" select="$starPageText" />
			<xsl:with-param name="numberOfStars" select="$numberOfStars" />
			<xsl:with-param name="pageset" select="$pageset" />
			<xsl:with-param name="id" select="$id" />
		</xsl:call-template>
	</xsl:template>
	
	<!-- StarPage metadata for the whole document -->
	<xsl:template name="StarPageMetadata" match="testStarpageMetadata">
		<xsl:call-template name="StarPageMetadataWithParams">
			<xsl:with-param name="displayableCitesForStarPagingParams" select="$displayableCitesForStarPaging" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="generateCopyWithReferenceLink">
		<xsl:param name="startNode" select="." />
		<xsl:call-template name="generateCopyWithReferenceLinkWithParams">
			<xsl:with-param name="startNode" select="$startNode" />
			<xsl:with-param name="displayableCitesForStarPagingParam" select="$displayableCitesForStarPaging" />
		</xsl:call-template>
	</xsl:template>
	
	<!-- Special footnote-matching mode to insert hidden star page anchors into the content
	     for Copy With Reference page calculations when the footnotes have been moved! -->
	<xsl:template match="footnote | form.footnote | endnote | form.endnote" mode="starPageCalculation">
		<xsl:call-template name="starPageFootnoteCalculationWithParams">
			<xsl:with-param name="displayableCitesForStarPagingParam" select="$displayableCitesForStarPaging" />
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
