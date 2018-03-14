<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="ccbValue" select="Document/n-docbody/doc/ccb/d5" />
	
	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayPublisherLogo" />
			<xsl:call-template name="StarPageMetadata" />
			
			<!-- header slightly different for LRI -->
			<xsl:if test="document-data/collection = 'w_3rd_lri4'">
				<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" mode="LRIJCR" />
				<div class="&headtextClass; &centerClass;"><xsl:text>&jlrLegalResourceIndexText;</xsl:text></div>								
				<xsl:apply-templates select="n-docbody/doc/ti" mode="LRIJCR"/>
			</xsl:if>
			<!-- end LRI-specific header -->

			<!-- header slightly different for JCR -->
			<xsl:if test="document-data/collection = 'w_cs_mjcr'">
				<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" mode="LRIJCR" />
				<div class="&headtextClass; &centerClass;"><xsl:text>&jlrJudicialConductReporterText;</xsl:text></div>
				<xsl:apply-templates select="n-docbody/doc/dl" mode="JCR" />
				<xsl:apply-templates select="n-docbody/doc/ti" mode="LRIJCR"/>
				<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			</xsl:if>
			<!-- end JCR-specific header -->
			
			<xsl:apply-templates />
			<xsl:call-template name="FooterCitation" />
			<xsl:call-template name="EndOfDocument" />
			<xsl:call-template name="DisplayPublisherLogo" />
		</div>
	</xsl:template>

	
	<!-- CITE: line in Legal Resources Index -->
	<xsl:template match="cite.info.generated[//Document/document-data/collection = 'w_3rd_lri4']">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
		
	<!-- TITLE: line in Legal Resources Index -->
	<xsl:template match="ti[//Document/document-data/collection = 'w_3rd_lri4']" priority="5">
		<xsl:apply-templates />
	</xsl:template>
	
	<!-- JCR and LRI Title in header -->
	<xsl:template match="ti" mode="LRIJCR">
		<div class="&titleClass;"><xsl:apply-templates /></div>
	</xsl:template>	
	
	<xsl:template match="dl" mode="JCR">
		<div class="&centerClass;"><xsl:apply-templates /></div>
	</xsl:template>		
	
	

	<xsl:template match="md.cites" mode="LRIJCR">
		<xsl:variable name="displayableCites" select="md.primarycite/md.primarycite.info[md.display.primarycite/@display = 'Y']" />
		<div class="&citesClass;">
			<xsl:apply-templates select="md.primarycite/md.primarycite.info[md.display.primarycite/@display = 'Y']"/>
		</div>
	</xsl:template>	
	
	<xsl:template match="dpa1[child::d6 and //Document/document-data/collection = 'w_3rd_mcilp' and position() = 1]" priority="5">
		<div class="&centerClass; &titleClass;">
			<xsl:value-of select="$ccbValue"/>
		</div>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="dpa1[child::d6 and //Document/document-data/collection = 'w_3rd_mcilp' and position() &gt; 1]" priority="5">
		<xsl:apply-templates />
	</xsl:template>
		
	<xsl:template match="ccb/d5">
		<div class="&headtextClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>	
		
	<xsl:template match="tl/d6">		
		<xsl:if test="string-length(normalize-space(./text())) &gt; 0">		
			<div class="&paraMainClass;">
				<xsl:apply-templates />
			</div>
		</xsl:if>
		<div>&#160;</div>
	</xsl:template>

	<xsl:template match="hla">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>		
	</xsl:template>
	
	<!-- Delete these -->
	<xsl:template match="rc.gen" priority="5" />
	<!-- cites for LRI and JCR are processed differently. Prevent cites from outputting twice -->
	<xsl:template match="md.cites[//Document/document-data/collection = 'w_3rd_lri4']" priority="5" />
	<xsl:template match="md.cites[//Document/document-data/collection = 'w_cs_mjcr']" priority="5" />
	<xsl:template match="ti[//Document/document-data/collection = 'w_cs_mjcr']" priority="5" />
	<xsl:template match="dl[//Document/document-data/collection = 'w_cs_mjcr']" priority="5" />					
	<xsl:template match="pcb[//Document/document-data/collection = 'w_cs_mjcr']" priority="5" />	
	
</xsl:stylesheet>
