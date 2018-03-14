<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternalReferenceWLN.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="IsDOJ" select="contains('|w_3rd_b10|w_3rd_b11|w_3rd_br1|w_3rd_br2|w_3rd_br3|w_3rd_br4|w_3rd_br5|w_3rd_br6|w_3rd_br7|w_3rd_br8|w_3rd_br9|w_3rd_brmono|w_3rd_csrab|w_3rd_crt|w_3rd_enrdb|w_3rd_fedprb|w_3rd_fedb|w_3rd_immb|w_3rd_sgb|w_3rd_dcb|w_3rd_dcb2|w_3rd_ustb|w_3rd_stabr|', concat('|', /Document/document-data/collection, '|'))"/>
	<xsl:variable name="IsIRS" select="contains('|w_3rd_fasbcode|w_3rd_ibfdtax|w_3rd_ibfdtxdy|w_3rd_irsaicpa|w_3rd_irsfgasb|w_3rd_irspriv|w_3rd_ppcp|w_3rd_riawwtcl|w_3rd_wglcirc|w_3rd_wgltaxd|w_3rd_abataxce|', concat('|', /Document/document-data/collection, '|'))"/>
	<xsl:variable name="IsAICPA" select="contains('|w_3rd_irsaicpa|', concat('|', /Document/document-data/collection, '|'))"/>
	<xsl:variable name="IsIrsFASBorGASB" select="contains('|w_3rd_irsfgasb|w_3rd_fasbcode|', concat('|', /Document/document-data/collection, '|'))"/>
	<xsl:variable name="mdview" select="/Document/n-metadata/metadata.block/md.subjects/md.subject/md.view[2]" />
	<xsl:variable name="IsGASB" select="boolean('true')"/>
	<xsl:variable name="IsFASB" select="boolean('true')"/>
	<xsl:variable name="RequiresStarPaging" select="$IsDOJ or contains('|w_3rd_riawwtcl|', concat('|', /Document/document-data/collection, '|'))" />


	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayPublisherLogo" />
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="/Document/n-docbody/doc/crdms/d1[/Document/document-data/collection = 'w_3rd_ibfdtxdy']"/>
			<xsl:apply-templates />
			<xsl:call-template name="FooterCitation" />
			<xsl:choose>
				<xsl:when test="$IsAICPA">
					<xsl:call-template name="EndOfDocument">
						<xsl:with-param name="endOfDocumentCopyrightText">&AICPA;</xsl:with-param>
						<xsl:with-param name="endOfDocumentCopyrightTextVerbatim" select="$IsAICPA" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$IsIrsFASBorGASB">
					<xsl:call-template name="IrsFGASBCopyRightMessages"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="EndOfDocument" />
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="DisplayPublisherLogo" />
			<xsl:if test="not($IsIRS = true())">
				<xsl:call-template name="RemoveSaveToWidget" />
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="IrsFGASBCopyRightMessages">
		<xsl:choose>
			<xsl:when test="starts-with($mdview,'GSB')">
				<xsl:call-template name="EndOfDocument">
					<xsl:with-param name="endOfDocumentCopyrightText">&GASB;</xsl:with-param>
					<xsl:with-param name="endOfDocumentCopyrightTextVerbatim" select="$IsGASB" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="starts-with($mdview,'FSB')">
				<xsl:call-template name="EndOfDocument">
					<xsl:with-param name="endOfDocumentCopyrightText">&FASB;</xsl:with-param>
					<xsl:with-param name="endOfDocumentCopyrightTextVerbatim" select="$IsFASB" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="EndOfDocument" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--	For the element of interest (d1) with the parent of interest (crdms) in the collection of 
				interest (w_3rd_ibfdtxdy), show the citation (crdms/d1) -->
	<xsl:template match="d1[parent::crdms and /Document/document-data/collection = 'w_3rd_ibfdtxdy']" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!--	For the element of interest (d1) with the parent of interest (til) in the collection of 
				interest (w_3rd_fasbcode), show the title (til/d1) -->
	<xsl:template match="d1[parent::til and /Document/document-data/collection = 'w_3rd_fasbcode']" priority="2">
		<div>&#160;</div>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="dpa0[d6] | dpa1[d6] | dpa2[d6]">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="rc.gen[not(/Document/document-data/collection = 'w_3rd_nlrdb2')]">
		<div>&#160;</div>
		<div class="&paraMainClass;">
			<xsl:apply-templates select="d1 | d6"/>
		</div>
	</xsl:template>

	<xsl:template match="rc.gen/d1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ti1" priority="2">
		<xsl:apply-templates />
		<xsl:if test="not(following-sibling::ti1)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="spa | sp">
		<xsl:call-template name="d7"/>
	</xsl:template>

	<xsl:template match="fnax">
		<div class="&xenaD8;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="message.block">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- This template was put in place because the <tk><d6> element structure for about 50% of the DOJ documents contained "lm" and "line" attributes that
			caused less than desirable styling.  This does not match Westlaw Classic either.  Because of the effort to change all the pathways 
			to update they styling to remove the values for "lm" and "line" attributes was too large and risky, we force them to both be 0.  BUG 610343.  We have already
			gone back and forth about honoring the "lm" (left margin) and "line" (first line indent) with private files.  The decision is to 
			appropriately show/indent as the content specifies........except for here ;-) -->
	<xsl:template match="tk/d6">
		<xsl:choose>
			<xsl:when test="$IsDOJ">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="0" />
					<xsl:with-param name="line" select="0" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="@lm" />
					<xsl:with-param name="line" select="@line" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- add additional space to title in document header for ti/d8 tag so that it styles like a d7 tag for DOJ collections. -->
	<xsl:template match="ti/d8">
		<xsl:choose>
			<xsl:when test="$IsDOJ">
				<xsl:call-template name="d7">
					<xsl:with-param name="lm" select="@lm" />
					<xsl:with-param name="line" select="@line" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="d8">
					<xsl:with-param name="lm" select="@lm" />
					<xsl:with-param name="line" select="@line" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Show Starpaging for the following collections  -->
	<xsl:template match="starpage.anchor" priority="4">
		<xsl:choose>
			<xsl:when test="$RequiresStarPaging">
				<xsl:call-template name="starpageWithPageSet"/>
			</xsl:when>
			<xsl:otherwise />
		</xsl:choose>
	</xsl:template>

	<!-- Show cite information only for the following collections -->
	<xsl:template match="md.display.primarycite">
		<xsl:choose>
			<xsl:when test="$RequiresStarPaging or contains('|w_3rd_fasbcode|w_3rd_irsaicpa|w_3rd_irsfgasb|w_3rd_wgltaxd|', concat('|', /Document/document-data/collection, '|'))">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise />
		</xsl:choose>
	</xsl:template>

	<!-- Supress these nodes -->
	<xsl:template match="coh | crh | origid | crdms.gen | dncl | dlcl" priority="3"/>
	
	<!-- Suppress these hidden elements -->
	<xsl:template match="tih | dlh" priority="3"/>

	<!-- Suppress selected elements in the collection of interest. -->
	<xsl:template match="vw[/Document/document-data/collection = 'w_3rd_irspriv']" priority="3"/>

	<!-- Suppress the rc tag as it is duplicating the cite -->
	<xsl:template match="rc[/Document/document-data/collection = 'w_3rd_fasbcode']" priority="3"/>

</xsl:stylesheet>
