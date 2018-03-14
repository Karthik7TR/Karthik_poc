<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="StarPages.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="displayableCitesForContentType" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info/md.display.primarycite[@display = 'Y'] | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info/md.display.parallelcite[@display = 'Y']" />
	<xsl:variable name="displayableCite" select="$displayableCitesForContentType[1]" />
	<xsl:variable name="displayableCiteId">
		<xsl:choose>
			<xsl:when test="$displayableCite/@ID">
				<xsl:value-of select="$displayableCite/@ID" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="generate-id($displayableCite)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="isWestlawCite" select="$displayableCite/@type = 'Westlaw'"/>

	<!-- Do not process the Star Pages with the normal rules, just display them outright! -->
	<xsl:template match="starpage.anchor" priority="1">
		<xsl:call-template name="spWoRulesAnchorWithParams">
			<xsl:with-param name="displayableCiteIdParam" select="$displayableCiteId" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="spWoRulesAnchorWithParams">
		<xsl:param name="displayableCiteIdParam" />
		<xsl:call-template name="displayStarPage">
			<xsl:with-param name="starPageText">
				<xsl:apply-templates />
			</xsl:with-param>
			<xsl:with-param name="numberOfStars" select="1" />
			<xsl:with-param name="pageset" select="$displayableCiteIdParam" />
		</xsl:call-template>
		
		<xsl:if test="$IncludeCopyWithRefLinks = true()">
			<xsl:call-template name="generateCopyWithReferenceLink" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="StarPageMetadataForContentType">
		<xsl:call-template name="spWoRulesMetadataForContentTypeWithParams">
			<xsl:with-param name="displayableCiteParam" select="$displayableCite" />
			<xsl:with-param name="displayableCiteIdParam" select="$displayableCiteId" />
			<xsl:with-param name="isWestlawCiteParam" select="$isWestlawCite" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="spWoRulesMetadataForContentTypeWithParams">
		<xsl:param name="displayableCiteParam" />
		<xsl:param name="displayableCiteIdParam" />
		<xsl:param name="isWestlawCiteParam" />
		<xsl:variable name="jsonObject">
			<xsl:text>{ "&citationMapJsonPropertyName;": { </xsl:text>
			<xsl:value-of select="concat('&quot;', $displayableCiteIdParam, '&quot;:&quot;')" />
			<!-- Apply templates to the md.primarycite|md.parallelcite grandparent (rather than doing a
					 value-of on "$displayableCite") to account for adjusted cites, jurisdictions, labels, etc. -->
			<xsl:apply-templates select="$displayableCiteParam/parent::node()/parent::node()" />
			<xsl:text>" }</xsl:text>
			<xsl:if test="$isWestlawCiteParam = true()">
				<xsl:text>, &westlawCiteOnlyJsonProperty;</xsl:text>
			</xsl:if>
			<xsl:text>, "&pubNumberMapJsonPropertyName;": { </xsl:text>
			<xsl:value-of select="concat('&quot;', $displayableCiteIdParam, '&quot;:&quot;')" />
			<!-- Get the value of the pubid associated with this cite -->
			<xsl:value-of select="$displayableCiteParam/following-sibling::md.pubid"/>
			<xsl:text>"</xsl:text>
			<xsl:text> }</xsl:text>
			<xsl:text> }</xsl:text>
		</xsl:variable>

		<xsl:if test="$IncludeCopyWithRefLinks = true()">
			<xsl:call-template name="generateCopyWithReferenceLink" />
		</xsl:if>
		<input type="hidden" id="&starPageMetadataId;" value="{$jsonObject}" alt="&metadataAltText;" />
	</xsl:template>
	
	<!-- Special footnote-matching mode to insert hidden star page anchors into the content
	     for Copy With Reference page calculations when the footnotes have been moved! -->
	<xsl:template match="footnote | form.footnote | endnote | form.endnote" mode="starPageCalculation" priority="1">
		<xsl:call-template name="spWoRulesCalculationWithParams">
			<xsl:with-param name="displayableCiteIdParam" select="$displayableCiteId" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="spWoRulesCalculationWithParams" priority="1">
		<xsl:variable name="nearestStarPage" select="./preceding::starpage.anchor[1]" />
		<xsl:if test="not($DeliveryMode)">
			<xsl:call-template name="StarPageMetadataItem">
				<xsl:with-param name="pageset" select="$displayableCiteId" />
				<xsl:with-param name="pageNumber">
					<xsl:apply-templates select="$nearestStarPage/node()" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$IncludeCopyWithRefLinks = true()">
			<xsl:call-template name="generateCopyWithReferenceLink">
				<xsl:with-param name="startNode" select="$nearestStarPage" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
