<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Variables primaryCite, firstParallelCite, regionalCite are defined in OriginalImage.xsl -->
	<xsl:variable name="nRSCite" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info[md.display.parallelcite/@type = 'West_special']
																			| /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info[md.display.parallelcite/@type = 'West_court_special']"/>
	<xsl:variable name="displayParallelAtTop" select="not($primaryCite) or $primaryCite[md.display.primarycite/@display = 'N' or md.display.primarycite/@status]" />
	<xsl:variable name="displayableParallelCites" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info[md.display.parallelcite/@display = 'Y']" />

	<xsl:template match="md.cites" mode="top">
		<div class="&citesClass;">
			<xsl:choose>
				<xsl:when test="$regionalCite">
					<xsl:apply-templates select="$regionalCite" />
				</xsl:when>
				<xsl:when test="$primaryCite/md.display.primarycite[@display = 'N' or @status = 'nr' or @status = 'slip' or @status = 'dash']">
					<xsl:apply-templates select="$firstParallelCite"/>
				</xsl:when>
				<xsl:otherwise>					
					<xsl:apply-templates select="$primaryCite" />					
				</xsl:otherwise>
			</xsl:choose>
		</div>
		
		<!-- For CWR paragraph pinpointing -->
		<xsl:call-template name="InjectParaNumbersSourceMetadata" />
	</xsl:template>

	<xsl:variable name="parallelCites">
		<xsl:choose>
			<xsl:when test="$nRSCite/md.display.parallelcite/@display = 'Y'">
				<xsl:variable name="renderedPrimaryCite">
					<xsl:apply-templates select="$primaryCite" />
				</xsl:variable>
				<xsl:copy-of select="$renderedPrimaryCite" />
				<xsl:for-each select="$displayableParallelCites">
					<xsl:if test="not(self::node()[md.display.parallelcite/@type = 'West_special'])">
						<xsl:text>,<![CDATA[ ]]></xsl:text>
						<xsl:apply-templates select="." />
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="$displayableParallelCites">
					<xsl:if test="($displayParallelAtTop and position() != 1) or not($displayParallelAtTop)">
						<xsl:apply-templates select="." />
						<xsl:if test="position() != last()">
							<xsl:text>,<![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="md.cites" mode="bottom">
		<xsl:if test="string-length($parallelCites) &gt; 0 and not($PreviewMode)">
			<div class="&parallelCitesClass;">
				<h2 id="&parallelCitationsId;" class="&parallelCitesBlockLabelClass; &printHeadingClass;">
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&parallelCitationsHeaderKey;', '&parallelCitationsHeader;')"/>
				</h2>
				<xsl:copy-of select="$parallelCites" />
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Paragraph Numbering Source Cite Metadata - for Copy With Reference paragraph pinpointing -->
	<xsl:template name="ParaNumbersSourceCiteMetadataItem">
		<xsl:param name="cite" />
		<xsl:param name="publicationNumber" />

		<xsl:variable name="jsonObject">
			<xsl:text>{ "&paraNumbersSourceCiteJsonPropertyName;": "</xsl:text>
			<xsl:value-of select="$cite" />
			<xsl:text>", "&publicationNumberJsonPropertyName;": "</xsl:text>
			<xsl:value-of select="$publicationNumber" />
			<xsl:text>" }</xsl:text>
		</xsl:variable>

		<input type="hidden" id="&paraNumbersSourceCiteMetadataId;" value="{$jsonObject}" alt="&metadataAltText;"/>
	</xsl:template>

	<xsl:template name="ProcessCitationNode">
		<xsl:param name="citations" />
		<xsl:param name="targetType" />

		<xsl:call-template name="ParaNumbersSourceCiteMetadataItem">
			<xsl:with-param name="cite" select="$citations[md.display.primarycite/@type = $targetType]/md.display.primarycite
																					| $citations[md.display.parallelcite/@type = $targetType][1]/md.display.parallelcite" />
			<xsl:with-param name="publicationNumber" select="$citations[md.display.primarycite/@type = $targetType]/md.pubid
																												| $citations[md.display.parallelcite/@type = $targetType][1]/md.pubid" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="SelectParaNumbersSource">
		<xsl:param name="possibleParaNumbersSources"/>

		<xsl:if test="$possibleParaNumbersSources">
			<xsl:choose>
				<xsl:when test="$possibleParaNumbersSources[md.display.primarycite/@type = 'obsolete_reporter' or md.display.parallelcite/@type = 'obsolete_reporter']">
					<xsl:call-template name="ProcessCitationNode">
						<xsl:with-param name="citations" select="$possibleParaNumbersSources" />
						<xsl:with-param name="targetType" select="'obsolete_reporter'" />
					</xsl:call-template>
				</xsl:when>

				<xsl:when test="$possibleParaNumbersSources[md.display.primarycite/@type = 'obsolete_case_slug_reporter' or md.display.parallelcite/@type = 'obsolete_case_slug_reporter']">
					<xsl:call-template name="ProcessCitationNode">
						<xsl:with-param name="citations" select="$possibleParaNumbersSources" />
						<xsl:with-param name="targetType" select="'obsolete_case_slug_reporter'" />
					</xsl:call-template>
				</xsl:when>

				<xsl:when test="$possibleParaNumbersSources[md.display.primarycite/@type = 'official_reporter' or md.display.parallelcite/@type = 'official_reporter']">
					<xsl:call-template name="ProcessCitationNode">
						<xsl:with-param name="citations" select="$possibleParaNumbersSources" />
						<xsl:with-param name="targetType" select="'official_reporter'" />
					</xsl:call-template>
				</xsl:when>

				<xsl:when test="$possibleParaNumbersSources[md.display.primarycite/@type = 'official_court' or md.display.parallelcite/@type = 'official_court']">
					<xsl:call-template name="ProcessCitationNode">
						<xsl:with-param name="citations" select="$possibleParaNumbersSources" />
						<xsl:with-param name="targetType" select="'official_court'" />
					</xsl:call-template>
				</xsl:when>

				<xsl:when test="$possibleParaNumbersSources[md.display.primarycite/@type = 'case_slug_hold_court' or md.display.parallelcite/@type = 'case_slug_hold_court']">
					<xsl:call-template name="ProcessCitationNode">
						<xsl:with-param name="citations" select="$possibleParaNumbersSources" />
						<xsl:with-param name="targetType" select="'case_slug_hold_court'" />
					</xsl:call-template>
				</xsl:when>

				<xsl:when test="$possibleParaNumbersSources[md.display.primarycite/@type = 'West_regional' or md.display.parallelcite/@type = 'West_regional']">
					<xsl:call-template name="ProcessCitationNode">
						<xsl:with-param name="citations" select="$possibleParaNumbersSources" />
						<xsl:with-param name="targetType" select="'West_regional'" />
					</xsl:call-template>
				</xsl:when>

				<xsl:when test="$possibleParaNumbersSources[md.display.primarycite/@type = 'West_court_regional' or md.display.parallelcite/@type = 'West_court_regional']">
					<xsl:call-template name="ProcessCitationNode">
						<xsl:with-param name="citations" select="$possibleParaNumbersSources" />
						<xsl:with-param name="targetType" select="'West_court_regional'" />
					</xsl:call-template>
				</xsl:when>

				<xsl:when test="$possibleParaNumbersSources[md.display.primarycite/@type = 'West_special' or md.display.parallelcite/@type = 'West_special']">
					<xsl:call-template name="ProcessCitationNode">
						<xsl:with-param name="citations" select="$possibleParaNumbersSources" />
						<xsl:with-param name="targetType" select="'West_special'" />
					</xsl:call-template>
				</xsl:when>

				<xsl:when test="$possibleParaNumbersSources[md.display.primarycite/@type = 'West_court_special' or md.display.parallelcite/@type = 'West_court_special']">
					<xsl:call-template name="ProcessCitationNode">
						<xsl:with-param name="citations" select="$possibleParaNumbersSources" />
						<xsl:with-param name="targetType" select="'West_court_special'" />
					</xsl:call-template>
				</xsl:when>

				<xsl:when test="$possibleParaNumbersSources[md.display.primarycite/@type = 'Westlaw' or md.display.parallelcite/@type = 'Westlaw']">
					<xsl:call-template name="ProcessCitationNode">
						<xsl:with-param name="citations" select="$possibleParaNumbersSources" />
						<xsl:with-param name="targetType" select="'Westlaw'" />
					</xsl:call-template>
				</xsl:when>

				<xsl:when test="$possibleParaNumbersSources[md.display.primarycite/@type = 'official_nonwest' or md.display.parallelcite/@type = 'official_nonwest']">
					<xsl:call-template name="ProcessCitationNode">
						<xsl:with-param name="citations" select="$possibleParaNumbersSources" />
						<xsl:with-param name="targetType" select="'official_nonwest'" />
					</xsl:call-template>
				</xsl:when>

				<xsl:otherwise>
					<xsl:call-template name="ParaNumbersSourceCiteMetadataItem">
						<xsl:with-param name="cite" select="$possibleParaNumbersSources[1]/md.display.primarycite 
																							| $possibleParaNumbersSources[1]/md.display.parallelcite" />
						<xsl:with-param name="publicationNumber" select="$possibleParaNumbersSources[1]/md.pubid" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="InjectParaNumbersSourceMetadata">
		<xsl:variable name="possibleParaNumbersSources" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info[md.display.primarycite/@display = 'Y']
																														| /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info[md.display.parallelcite/@display = 'Y']" />
		<xsl:call-template name="SelectParaNumbersSource">
			<xsl:with-param name="possibleParaNumbersSources" select="$possibleParaNumbersSources"/>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
