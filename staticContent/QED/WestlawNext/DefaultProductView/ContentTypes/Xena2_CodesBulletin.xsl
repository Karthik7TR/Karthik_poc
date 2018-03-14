<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:include href ="InternalReferenceWLN.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayPublisherLogo" />
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="//md.cites" />
			<xsl:apply-templates select="n-docbody" />
			<xsl:call-template name="EndOfDocument" />
			<xsl:call-template name="DisplayPublisherLogo" />
		</div>
	</xsl:template>

		<xsl:template match="smp52[d6] | smp53[d6]">
			<xsl:if test="string-length(normalize-space(.//text())) &gt; 0">
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
				</xsl:if>
	</xsl:template>
	
	<xsl:template match="cr" >
		<xsl:variable name="citeText">
			<xsl:apply-templates select="//md.cites" />
		</xsl:variable>
		<!--Texst added for possible duplicate title text coming from <cr><d6>-->
		<xsl:if test="not (. = $citeText )" >
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="'&centerClass;'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="md.cites" priority="2">
		<xsl:variable name="displayableCites" select="md.primarycite/md.primarycite.info[md.display.primarycite/@display = 'Y'] | md.parallelcite/md.parallelcite.info[md.display.parallelcite/@display = 'Y' or md.display.parallelcite/@userEntered = 'Y']" />
		<div class="&citesClass;">
			<xsl:choose>
				<xsl:when test="string-length($displayableCites) &gt; 0">

					<xsl:for-each select="$displayableCites">
						<xsl:apply-templates select="." />
						<xsl:if test="position() != last()">
							<xsl:text>,<![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:when test="md.second.line.cite">
					<xsl:apply-templates select="md.second.line.cite"/>
				</xsl:when>
				<xsl:when test="md.third.line.cite">
					<xsl:apply-templates select="md.third.line.cite"/>
				</xsl:when>
				<xsl:when test="md.first.line.cite">
					<xsl:apply-templates select="md.first.line.cite"/>
				</xsl:when>
			</xsl:choose>
		</div>
	</xsl:template>

  <!-- In Codes - Historical Bills, we want to display the special markup for Added and Deleted material
				Since this content type uses the CodesStatutes stylesheet, we need templates to overwrite the
				disabling of added and deleted material that CodesStatues does -->
  <xsl:template match="added.material" priority="1">
    <xsl:call-template name="addedMaterial" />
  </xsl:template>

  <xsl:template match="deleted.material" priority="1">
    <xsl:call-template name="deletedMaterial" />
  </xsl:template>


  <xsl:template match="centv | cs | adt.gen"  />
</xsl:stylesheet>