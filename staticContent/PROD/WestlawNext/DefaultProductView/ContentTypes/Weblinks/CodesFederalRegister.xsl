<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="NotesOfDecisions.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="StarPagesWithoutRules.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesFederalRegisterClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates/>
			<xsl:call-template name="FooterCitation" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!--USCCAN Pending/Proposed Regulations Citation should display (Pres.), hence overriding the Citation templatate-->
	<xsl:template match="md.cites[/Document/document-data/collection = 'w_codesfrprespnvdp']" priority="3" >
		<xsl:variable name="contents">
			<xsl:value-of select="md.parallelcite/md.fr.cites/md.fr.cite"/>
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div class="&citesClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>
	
	<!-- Suppress displaying of Citation for w_codesfrprespnvdp collection-->
	<xsl:template match="cmd.cites[/Document/document-data/collection = 'w_codesfrprespnvdp']" priority="3" />
		
	
	<xsl:template match="n-docbody/federal.register">
		<xsl:call-template name="renderFedRegHeader"/>
		<div class="&simpleContentBlockClass;">
			<xsl:if test="@ID|@id">
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="*[not(self::prelim.block[doc.type.line]) and not(self::content.metadata.block) and not(self::text.head) and not(self::caption.line) and not(self::date.line)]"/>
		</div>
	</xsl:template>

	<xsl:template name="renderFedRegHeader">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="content.metadata.block/cmd.identifiers/cmd.cites"/>
			<xsl:apply-templates select="prelim.block[1]"/>
			<xsl:apply-templates select="text.head[1]"/>
			<xsl:apply-templates select="caption.line"/>
			<xsl:apply-templates select="date.line"/>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="federal.register/prelim.block[doc.type.line or agency.doc.id] | federal.register/text.head" priority="2">
		<div class="&simpleContentBlockClass; &prelimBlockClass;">
			<div class="&centerClass;">
				<xsl:apply-templates />
			</div>
		</div>
	</xsl:template>

	<xsl:template match="federal.register/prelim.block[not(doc.type.line or agency.doc.id)]" priority="2">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="doc.type.line | agency.doc.id[parent::prelim.block[1]] | agency.block[parent::prelim.block[1]]/head/headtext" priority="1">
		<xsl:param name="divId"/>
		<xsl:call-template name="renderHeadTextDiv">
			<xsl:with-param name="divId" select="$divId"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="federal.register/caption.line" priority="1">
		<div class="&titleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="federal.register/date.line" priority="2">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--Supress this-->
	<xsl:template match="source.line" />

	<xsl:template match="hide.historical.version" mode="docHeader">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="hide.historical.version" />

	<!--Suppress the currentness link when currency is not displayed-->
	<xsl:template match="hide.historical.version[
													not(/Document/document-data/versioned = 'True' and 
															/Document/document-data/datetime &gt; /Document/n-metadata/metadata.block/md.dates/md.starteffective and 
															/Document/document-data/datetime &lt; /Document/n-metadata/metadata.block/md.dates/md.endeffective)]" priority="1" mode="docHeader" />

	<xsl:template match="author.block" priority="1">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
		<br />
	</xsl:template>

    <!--USCCAN Pending/Proposed Regulations Citation should display at end of document--> 
	<xsl:template name="FooterCitation" >
		<xsl:variable name="contents">
			<xsl:choose>
				<xsl:when test="/Document/document-data/collection = 'w_codesfrprespnvdp'">
					<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.fr.cites/md.fr.cite"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div class="&citationClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
