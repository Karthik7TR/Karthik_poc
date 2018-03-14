<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="Suppressed.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="documentType" select="/Document/document-data/doc-type"/>
	<xsl:key name="distinctEligibleStarPages" use="concat(@pageset, concat('_', text()))" match="starpage.anchor[@pageset]" />
	<xsl:key name="distinctEligibleStarPagesForDisplay" use="concat(@pageset, concat('_', text()))" match="starpage.anchor[@pageset and not(ancestor::docket.block or ancestor::date.block or ancestor::court.block or ancestor::message.block or ancestor::headnote.block or ancestor::court.headnote.block or ancestor::synopsis or ancestor::title.block or ancestor::archive.headnote.block or ancestor::trial.type.block or ancestor::headnote.publication.block or ancestor::layout.control.block or ancestor::content.layout.block or ancestor::error.block or ancestor::withdrawn.block or ancestor::archive.brief.reference.block)]" />
	<xsl:key name="distinctEligibleStarPagesForDisplayCaselawNY" use="concat(@pageset, concat('_', text()))" match="starpage.anchor[@pageset and not(ancestor::docket.block or ancestor::date.block or ancestor::message.block or ancestor::headnote.block or ancestor::trial.type.block or ancestor::headnote.publication.block or ancestor::layout.control.block or ancestor::error.block or ancestor::withdrawn.block or ancestor::archive.brief.reference.block)]" />
	<xsl:key name="distinctEligibleStarPagesForDisplayTrialCourtOrders" use="concat(@pageset, concat('_', text()))" match="starpage.anchor[@pageset and not(ancestor::docket.block or ancestor::date.block or ancestor::court.block or ancestor::message.block or ancestor::headnote.block or ancestor::court.headnote.block or ancestor::synopsis or ancestor::archive.headnote.block or ancestor::trial.type.block or ancestor::headnote.publication.block or ancestor::layout.control.block or ancestor::content.layout.block or ancestor::error.block or ancestor::withdrawn.block or ancestor::archive.brief.reference.block)]" />
	<xsl:key name="distinctEligibleStarPagesForDisplayForDissenting" use="concat(@pageset, concat('_', text()))" match="starpage.anchor[@pageset and ancestor::opinion.dissent]" />
	<xsl:variable name="displayableCitesWithoutStatus" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info/md.display.primarycite[@display = 'Y' and not(@status)] | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info/md.display.parallelcite[@display = 'Y' and not(@status)]" />
	<xsl:variable name="eligiblePagesets" select="/Document/n-metadata/metadata.block/md.descriptions/md.westlawdescrip/md.starpages[@pageset and text() = 'Y']" />
	<xsl:variable name="eligibleCites" select="$displayableCitesWithoutStatus[@ID = $eligiblePagesets/@pageset and @type != 'Westlaw']" />
	<xsl:variable name="eligibleWestlawCites" select="$displayableCitesWithoutStatus[@ID = $eligiblePagesets/@pageset and @type = 'Westlaw']" />
	<xsl:variable name="displayableCitesForStarPaging" select="$eligibleCites | $eligibleWestlawCites[count($eligibleCites) = 0]" />
	<xsl:variable name="displayWestlawCiteOnly" select="count($eligibleCites) = 0 and count($eligibleWestlawCites) &gt; 0"/>

	<!--
		Do not render Star Pages that are in any of the following blocks.
		This must be the first check in order to handle hidden duplicates correctly,
		per Web2 stylesheets, Dan Dodge, Cobalt NPD, and this old documentation:
			http://wlnv.roc.westgroup.com/Cases/Shared%20Documents/Star%20Page.doc
	-->
	<xsl:template match="starpage.anchor" />

	<!-- Handle Star Pages that do NOT have "bad" ancestors -->
	<xsl:template match="starpage.anchor[@pageset]" name="starpageWithPageSet">
		<xsl:call-template name="starpageWithPageSetWithParams">
			<xsl:with-param name="displayableCitesForStarPagingParam" select="$displayableCitesForStarPaging" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="starpageWithPageSetWithParams">
		<xsl:param name="displayableCitesForStarPagingParam" />
		<xsl:variable name="sp_pageset" select="@pageset" />
		<xsl:variable name="sp_numberText" select="text()" />
		<xsl:variable name="sp_keyData" select="concat($sp_pageset, concat('_', $sp_numberText))" />
		<xsl:variable name="whatEligibleStarPagesToBeDiplayed">
				<xsl:choose>
					<xsl:when test="string($documentType) = 'Caselaw - NY Official'">
						 <xsl:value-of select="'distinctEligibleStarPagesForDisplayCaselawNY'"/>
					</xsl:when>
					<xsl:when test="string($documentType) = 'Trial Court Orders'">
						 <xsl:value-of select="'distinctEligibleStarPagesForDisplayTrialCourtOrders'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'distinctEligibleStarPagesForDisplay'"/>
					</xsl:otherwise>
				</xsl:choose>
		</xsl:variable>
		<!--
		  Ensure this star page meets the following requirements:
		    1. Its pageset attribute matches the ID attribute of some displayable citation that is eligible for star paging
				2a. Its generated ID matches that of one of the generated IDs in the distinctEligibleStarPages key
				    (meaning it is the first starpage in the document for that pageset and page number combination and the starpage is in the opinion.dissent element.)						
				  -OR-
				2b. It is a descendant of the dissenting opinion
		-->
		<xsl:if test="$displayableCitesForStarPagingParam[@ID = $sp_pageset]">
			<xsl:choose>
				<!-- Only display the star page itself if it is NOT within one of these "bad ancestors" (see comment preceding this template for more information) -->				
				<xsl:when test="generate-id(.) = generate-id(key($whatEligibleStarPagesToBeDiplayed, $sp_keyData)) or generate-id(.) = generate-id(key('distinctEligibleStarPagesForDisplayForDissenting', $sp_keyData))">
					<xsl:variable name="citeIndex">
						<xsl:for-each select="$displayableCitesForStarPagingParam">
							<xsl:if test="@ID = $sp_pageset">
								<xsl:number value="position()" />
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>
					<xsl:call-template name="displayStarPage">
						<xsl:with-param name="starPageText" select="$sp_numberText" />
						<xsl:with-param name="numberOfStars" select="$citeIndex" />
						<xsl:with-param name="pageset" select="$sp_pageset" />
						<xsl:with-param name="id">
							<xsl:value-of select="concat('&pinpointIdPrefix;', @ID)"/>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:if test="$IncludeCopyWithRefLinks = true()">
						<xsl:call-template name="generateCopyWithReferenceLink" />
					</xsl:if>
				</xsl:when>
				<!-- Do this even if it is in a bad ancestor in order to offer more accurate page numbers during Copy With Reference -->
				<xsl:when test="generate-id(.) = generate-id(key('distinctEligibleStarPages', $sp_keyData))">
					<xsl:if test="$IncludeCopyWithRefLinks = true()">
						<xsl:call-template name="generateCopyWithReferenceLink" />
					</xsl:if>
				</xsl:when>
				<xsl:otherwise />
			</xsl:choose>
		</xsl:if>
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
	
	<xsl:template name="displayStarPageWithParams">
		<xsl:param name="starPageText" />
		<xsl:param name="numberOfStars" />
		<xsl:param name="pageset" />
		<xsl:param name="id" />

		<xsl:if test="string-length($starPageText) &gt; 0">
			<xsl:variable name="starPageMetadataItemHiddenInput">
				<xsl:call-template name="StarPageMetadataItem">
					<xsl:with-param name="pageset" select="$pageset" />
					<xsl:with-param name="pageNumber" select="$starPageText" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="stars">
				<xsl:call-template name="repeat">
					<xsl:with-param name="contents">
						<xsl:text>*</xsl:text>
					</xsl:with-param>
					<xsl:with-param name="repetitions" select="$numberOfStars" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($stars) &gt; 0">
				<xsl:call-template name="insertHardSpace"/>
				<span>
					<xsl:attribute name="class">
						<xsl:text>&starPageClass;</xsl:text>
					</xsl:attribute>
					<xsl:if test="string-length($id) &gt; 0">
						<xsl:attribute name="id">
							<xsl:value-of select="$id"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:copy-of select="$starPageMetadataItemHiddenInput"/>
					<xsl:value-of select="$stars"/>
					<xsl:value-of select="$starPageText"/>
				</span>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="insertHardSpace">
			<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<!-- StarPage metadata for a single instance of the starpage.anchor element -->
	<xsl:template name="StarPageMetadataItem">
		<xsl:param name="pageset" />
		<xsl:param name="pageNumber" />
		
		<xsl:variable name="jsonObject">
			<xsl:text>{ "&pagesetJsonPropertyName;": "</xsl:text>
			<xsl:value-of select="$pageset" />
			<xsl:text>", "&pageNumberJsonPropertyName;": "</xsl:text>
			<xsl:value-of select="$pageNumber" />
			<xsl:text>" }</xsl:text>
		</xsl:variable>
		
		<input type="hidden" class="&starPageMetadataItemClass;" value="{$jsonObject}" alt="&metadataAltText;"/>
	</xsl:template>
	
	<!-- StarPage metadata for the whole document -->
	<xsl:template name="StarPageMetadata" match="testStarpageMetadata">
		<xsl:call-template name="StarPageMetadataWithParams">
			<xsl:with-param name="displayableCitesForStarPagingParams" select="$displayableCitesForStarPaging" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="StarPageMetadataWithParams">
		<xsl:param name="displayableCitesForStarPagingParams" />
		<xsl:variable name="jsonObject">
			<xsl:text>{ "&citationMapJsonPropertyName;": { </xsl:text>
			<xsl:for-each select="$displayableCitesForStarPaging">
				<xsl:value-of select="concat('&quot;', @ID, '&quot;:&quot;')" />
				<!-- Apply templates to the md.primarycite|md.parallelcite grandparent (rather than doing a
				     value-of on ".") to account for adjusted cites, jurisdictions, labels, etc. -->
				<xsl:apply-templates select="parent::node()/parent::node()" />
				<xsl:text>"</xsl:text>
				<xsl:if test="position() != last()">
					<xsl:text>, </xsl:text>
				</xsl:if>
			</xsl:for-each>
			<xsl:text> }</xsl:text>
			<xsl:if test="$displayWestlawCiteOnly = true()">
				<xsl:text>, &westlawCiteOnlyJsonProperty;</xsl:text>
			</xsl:if>
			<xsl:text>, "&pubNumberMapJsonPropertyName;": { </xsl:text>
			<xsl:for-each select="$displayableCitesForStarPagingParams">
				<xsl:value-of select="concat('&quot;', @ID, '&quot;:&quot;')" />
				<!-- Get the value of the pubid associated with this cite -->
				<xsl:value-of select="following-sibling::md.pubid"/>
				<xsl:text>"</xsl:text>
				<xsl:if test="position() != last()">
					<xsl:text>, </xsl:text>
				</xsl:if>
			</xsl:for-each>
			<xsl:text> }</xsl:text>
			<xsl:text> }</xsl:text>
		</xsl:variable>
		
		<xsl:if test="$IncludeCopyWithRefLinks = true()">
			<xsl:call-template name="generateCopyWithReferenceLink" />
		</xsl:if>
		<input type="hidden" id="&starPageMetadataId;" value="{$jsonObject}" alt="&metadataAltText;" />
	</xsl:template>

	<!-- Suppress starpages from composite header -->
	<xsl:template match="Title//starpage.anchor" priority="2" />

	<!-- Suppress starpages from title metadata -->
	<xsl:template match="/Document/document-data/title//starpage.anchor" priority="2" />

	
	<xsl:template name="generateCopyWithReferenceLink">
		<xsl:param name="startNode" select="." />
		<xsl:call-template name="generateCopyWithReferenceLinkWithParams">
			<xsl:with-param name="startNode" select="$startNode" />
			<xsl:with-param name="displayableCitesForStarPagingParam" select="$displayableCitesForStarPaging" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="generateCopyWithReferenceLinkWithParams">
		<xsl:param name="startNode" select="." />
		<xsl:param name="displayableCitesForStarPagingParam" />
		<xsl:variable name="current_sp_pageset" select="$startNode/@pageset" />

		<xsl:variable name="citationsAndPageNumbers">
			<xsl:for-each select="$displayableCitesForStarPagingParam">
				<!-- Display citation -->
				<!-- Apply templates to the md.primarycite|md.parallelcite grandparent (rather than doing a
							 value-of on ".") to account for adjusted cites, jurisdictions, labels, etc. -->
				<xsl:apply-templates select="parent::node()/parent::node()" />

				<!-- Only add page numbers if this is being called with a starpage.anchor -->
				<xsl:if test="$startNode/self::starpage.anchor">
					<xsl:variable name="stars">
						<xsl:call-template name="repeat">
							<xsl:with-param name="contents">
								<xsl:text>*</xsl:text>
							</xsl:with-param>
							<xsl:with-param name="repetitions" select="position()" />
						</xsl:call-template>
					</xsl:variable>
					
					<!-- Display stars and page numbers -->
					<xsl:variable name="current_id" select="@ID" />
					<xsl:choose>
						<xsl:when test="@ID = $current_sp_pageset">
							<xsl:text>, </xsl:text>
							<xsl:value-of select="$stars" />
							<xsl:value-of select="$startNode/text()" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:variable name="starpageForThisPageset" select="$startNode/preceding::starpage.anchor[@pageset = $current_id][1]" />
							<xsl:if test="$starpageForThisPageset">
								<xsl:text>, </xsl:text>
								<xsl:value-of select="$stars" />
								<xsl:value-of select="$starpageForThisPageset/text()" />
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
				<xsl:if test="position() != last()">
					<!-- IMPORTANT: The comma must be followed by TWO spaces -->
					<xsl:text>,  </xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		
		<xsl:if test="string-length($citationsAndPageNumbers) &gt; 0">
			<xsl:variable name="hrefValue">
				<xsl:variable name="copyWithRefLink" select="concat('&copyWithRefLinkPrefix;', concat($citationsAndPageNumbers, '&copyWithRefLinkSuffix;'))" />
				<xsl:value-of select="translate($copyWithRefLink, ' ', '+')"/>
			</xsl:variable>
			<a href="{$hrefValue}" />
		</xsl:if>
	</xsl:template>
	
	<!-- Special footnote-matching mode to insert hidden star page anchors into the content
	     for Copy With Reference page calculations when the footnotes have been moved! -->
	<xsl:template match="footnote | form.footnote | endnote | form.endnote" mode="starPageCalculation">
		<xsl:call-template name="starPageFootnoteCalculationWithParams">
			<xsl:with-param name="displayableCitesForStarPagingParam" select="$displayableCitesForStarPaging" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="starPageFootnoteCalculationWithParams">
		<xsl:param name="displayableCitesForStarPagingParam" />
		<xsl:variable name="currentNode" select="." />
		<xsl:for-each select="$displayableCitesForStarPagingParam">
			<xsl:variable name="citeId" select="@ID" />
			<xsl:variable name="nearestStarPage" select="$currentNode/preceding::starpage.anchor[@pageset = $citeId][1]" />
			<xsl:if test="not($DeliveryMode)">
				<xsl:call-template name="StarPageMetadataItem">
					<xsl:with-param name="pageset" select="$nearestStarPage/@pageset" />
					<xsl:with-param name="pageNumber" select="$nearestStarPage/text()" />
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$IncludeCopyWithRefLinks = true()">
				<xsl:call-template name="generateCopyWithReferenceLink">
					<xsl:with-param name="startNode" select="$nearestStarPage" />
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
