<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Briefs/Cases Cites -->
	<xsl:template match="md.cites" priority="1">
		<xsl:if test="not(/Document/n-docbody/*/content.metadata.block/cmd.identifiers/cmd.cites)">
			<xsl:variable name="displayableCites" select="md.primarycite/md.primarycite.info[md.display.primarycite/@display = 'Y'] | md.parallelcite/md.parallelcite.info[md.display.parallelcite/@display = 'Y']" />
			<xsl:if test="string-length($displayableCites) &gt; 0">
				<div class="&citesClass;">
					<xsl:for-each select="$displayableCites">
						<xsl:apply-templates select="." />
						<xsl:if test="position() != last()">
							<xsl:text>,<![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:for-each>
					<xsl:call-template name="docLabelName" />
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="cmd.cites">
		<xsl:variable name="contents">
			<xsl:choose>
				<xsl:when test="cmd.second.line.cite and string-length(normalize-space(cmd.second.line.cite)) &gt; 0">
					<xsl:apply-templates select="cmd.second.line.cite"/>
				</xsl:when>
				<xsl:when test="cmd.third.line.cite and string-length(normalize-space(cmd.third.line.cite)) &gt; 0">
					<xsl:apply-templates select="cmd.third.line.cite"/>
				</xsl:when>
				<xsl:when test="cmd.first.line.cite and string-length(normalize-space(cmd.first.line.cite)) &gt; 0">
					<xsl:apply-templates select="cmd.first.line.cite"/>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="cmd.formercite and string-length(normalize-space(cmd.formercite)) &gt; 0">
				<div>
					<xsl:apply-templates select="cmd.formercite" mode="cmdCites"/>
				</div>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div class="&citesClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Primary Cites -->
	<xsl:template match="md.primarycite/md.primarycite.info">
		<xsl:if test="md.display.primarycite/@display = 'Y'">
			<xsl:choose>
				<xsl:when test="md.adj.display.primarycite">
					<xsl:apply-templates select="md.adj.display.primarycite" />
					<xsl:call-template name="jurisdiction">
						<xsl:with-param name="citeType" select="md.adj.display.primarycite/@type"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="md.display.primarycite">
					<xsl:apply-templates select="md.display.primarycite" />
					<xsl:call-template name="jurisdiction">
						<xsl:with-param name="citeType" select="md.display.primarycite/@type"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- Parallel Cites -->
	<xsl:template match="md.parallelcite/md.parallelcite.info">
		<xsl:if test="md.display.parallelcite/@display = 'Y'">
			<xsl:choose>
				<xsl:when test="md.adj.display.parallelcite">
					<xsl:apply-templates select="md.adj.display.parallelcite" />
					<xsl:call-template name="jurisdiction">
						<xsl:with-param name="citeType" select="md.adj.display.parallelcite/@type"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="md.display.parallelcite">
					<xsl:apply-templates select="md.display.parallelcite" />
					<xsl:if test="not(ancestor::md.parallelcite/preceding-sibling::md.primarycite//md.display.primarycite[@type='Westlaw' and @display='Y'])">
					<xsl:call-template name="jurisdiction">
						<xsl:with-param name="citeType" select="md.display.parallelcite/@type"/>
					</xsl:call-template>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getCitation">
		<xsl:variable name="cite">
			<xsl:choose>
				<xsl:when test="md.second.line.cite">
					<xsl:apply-templates select="md.second.line.cite"/>
				</xsl:when>
				<xsl:when test="md.first.line.cite">
					<xsl:apply-templates select="md.first.line.cite"/>
				</xsl:when>
				<xsl:when test="md.third.line.cite">
					<xsl:apply-templates select="md.third.line.cite"/>
				</xsl:when>
				<xsl:when test="md.primarycite/md.primarycite.info/md.display.primarycite[@display='Y']">
					<xsl:apply-templates select="md.primarycite/md.primarycite.info/md.display.primarycite[@display='Y']"/>
				</xsl:when>
				<xsl:when test="md.parallelcite/md.parallelcite.info[md.display.parallelcite[@display='Y']]">
					<xsl:apply-templates select="md.parallelcite[md.parallelcite.info/md.display.parallelcite[@display='Y']][1]"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="string-length($cite) &gt; 0">
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="'&citesClass;'"/>
				<xsl:with-param name="contents" select="$cite"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Checking Citation Type and getting the Jurisdiction court -->
	<xsl:template name="jurisdiction">
		<xsl:param name="citeType" />
		<xsl:if test="$citeType = 'Westlaw'">
			<xsl:variable name="jurisdictionCourt" select="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.juriscourt"/>
			<xsl:if test="string-length($jurisdictionCourt) &gt; 0 and not(contains(.., $jurisdictionCourt))">
				<xsl:choose>
					<xsl:when test="starts-with($jurisdictionCourt, '(') and substring($jurisdictionCourt, string-length($jurisdictionCourt)) = ')'">
						<xsl:value-of select="$jurisdictionCourt" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat(' (', $jurisdictionCourt, ')')" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Checking and retrieving -->
	<xsl:template name="docLabelName">
		<xsl:variable name="docLabel" select="/Document/n-metadata/metadata.block/md.subjects/md.subject/md.doclabel/md.doclabelname"/>
		<xsl:if test="string-length($docLabel) &gt; 0">
			<xsl:value-of select="concat(' (', $docLabel, ')')" />
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
