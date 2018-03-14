<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Neutral Cite -->
	<xsl:template match="md.cites/md.primarycite" priority="1">
		<!-- Will use primary site for now because the neutral site is not available yet-->
		<div class="&citesClass;">
			<xsl:value-of select="." />
		</div>
		<!--
    <xsl:variable name="displayableCites" select="md.primarycite" />
    <xsl:if test="string-length($displayableCites) &gt; 0">
      <div class="&citesClass;">
        <xsl:for-each select="$displayableCites">
          <xsl:apply-templates select="." />
          <xsl:if test="position() != last()">
            <xsl:text>;<![CDATA[ ]]></xsl:text>
          </xsl:if>
        </xsl:for-each>
      </div>
    </xsl:if>
    -->
	</xsl:template>

	<!-- Parallel Cites -->
	<xsl:template match="md.cites" priority="1">
		<xsl:variable name="displayableCites" select="md.parallelcite[contains(text(),'Carswell')]" />
		<xsl:if test="string-length($displayableCites) &gt; 0">
			<div class="&parallelCitesClass;">
				<xsl:for-each select="$displayableCites">
					<xsl:apply-templates select="." />
					<xsl:if test="position() != last()">
						<xsl:text>;<![CDATA[ ]]></xsl:text>
					</xsl:if>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="md.cites" mode="caselaw" priority="1">
		<xsl:if test="md.parallelcite">
			<div class="&parallelCitesClass;">
				<xsl:for-each select="md.parallelcite">
					<xsl:apply-templates select="." />
					<xsl:if test="position() != last()">
						<xsl:text>,<![CDATA[ ]]></xsl:text>
					</xsl:if>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Legislation Cites -->
	<xsl:template match="doc_citation | stub_citation" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&citesClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- CED Cites -->
	<xsl:template match="md.identifiers/md.cites" priority="1">
		<xsl:variable name="cite" select="normalize-space(md.first.line.cite)" />
		<xsl:if test="string-length($cite) &gt; 0">
			<div class="&citesClass;">
				<xsl:value-of select="$cite"/>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Words & Phrases Cite -->
	<xsl:template match="md.primarycite | db.primarycite">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&citesClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Text and Annotations -->
	<xsl:template match="md.identifiers/md.cites" mode="TextAnnotations">
		<xsl:if test="md.first.line.cite/text()">
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="&paraMainClass;"/>
				<!--<xsl:with-param name="class" select="&crswTocParagraph;"/>-->
				<xsl:with-param name="contents" select="normalize-space(md.first.line.cite)"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="md.second.line.cite/text()">
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="&paraMainClass;"/>
				<!--<xsl:with-param name="class" select="&crswTocParagraph;"/>-->
				<xsl:with-param name="contents" select="normalize-space(md.second.line.cite)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>