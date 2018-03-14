<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:template name="DisplayPublisherLogo">
		<xsl:param name="PublisherType" select="/Document/n-metadata/metadata.block/md.subjects/md.subject/md.pubtype.name" />
		<xsl:choose>
			<xsl:when test="$PublisherType = '&PublisherALM;'">
				<div class="&treatisesHeaderImageClass;">
					<img src="{$Images}&almPublishingPath;" alt="&almPublishingText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherALMExperts;'">
				<div class="&almPublishingClass;">
					<img src="{$Images}&almExpertsPath;" alt="&almExpertsText;" />
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherResearchInstituteOfAmericaInc;'">
				<div class="&treatisesHeaderImageClass;">
					<img src="{$Images}&riaPath;" alt="&riaText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherECRI;'">
				<div class="&ecriHeaderImageClass;">
					<img src="{$Images}&ecriPath;" alt="&ecriText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherBIN;'">
				<div class="&newsroomClass;">
					<img src="{$Images}&newsroomPath;" alt="&newsroomText;" />
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherBIN2;'">
				<div class="&newsroomClass;">
					<img src="{$Images}&newsroomPath;" alt="&newsroomText;" />
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherPRAG;'">
				<div class="&puertoRicoImageClass;">
					<img src="{$Images}&puertoRicoAGLogoPath;" alt="&puertoRicoAGText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherNetScan;'">
				<div class="&netscanClass;">
					<img src="{$Images}&netScanLogoPath;" alt="&netscanText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherResearchInstituteOfAmericaInc;'">
				<div class="&treatisesHeaderImageClass;">
					<img src="{$Images}&riaPath;" alt="&riaText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherStateNet;'">
				<div class="&netscanClass;">
					<img src="{$Images}&stateNetPath;" alt="&stateNetText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherThomson;'">
				<div class="&compumarkClass;">
					<img src="{$Images}&compumarkPath;" alt="&compumarkText;" />
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherEllis;'">
				<div class="&ellisClass;">
					<img src="{$Images}&ellisLogoPath;" alt="&ellisPubText;" />
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherBureauofNationalAffairsInc;'">
				<div class="&bnaHeaderImageClass;">
					<img src="{$Images}&bnaPath;" alt="&bnaText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherPractisingLawInstitute;'">
				<div class="&treatisesHeaderImageClass;">
					<img src="{$Images}&pliPath;" alt="&pliText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherAspen;' or $PublisherType = '&PublisherAsp;'">
				<div class="&treatisesHeaderImageClass;">
					<img src="{$Images}&aspenPath;" alt="&aspenText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherSec;'">
				<div class="&bnaHeaderImageClass;">
					<img src="{$Images}&secPath;" alt="&secText;"/>					
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherSweetMaxwell;'">
				<div class="&smgHeaderImageClass;">
					<img src="{$Images}&smgPath;" alt="&smgText;"/>					
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherSweetMaxwellAsia;'">
				<div class="&pubLogoClass;">
					<img src="{$Images}&smaPath;" alt="&smaText;"/>					
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherSessionCases;'">
				<div class="&pubLogoClass;">
					<img src="{$Images}&sessionCasesPath;" alt="&sessionCasesText;"/>					
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherTheBusinessLawReportsUK;'">
				<div class="&pubLogoClass;">
					<img src="{$Images}&ukBLRPath;" alt="&ukBLRText;"/>					
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherClassLegal;'">
				<div class="&pubLogoClass;">
					<img src="{$Images}&classLegalPath;" alt="&classLegalText;"/>					
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&PublisherCUP;'">
				<div class="&pubLogoClass;">
					<img src="{$Images}&cupPath;" alt="&cupText;"/>					
				</div>
			</xsl:when>					
			<xsl:when test="$PublisherType = '&CommerceClearingHouse;'">
				<div class="&pubLogoClass;">
					<img src="{$Images}&cchPath;" alt="&cchText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&CongressionalQuarterly;'">
				<div class="&pubLogoClass;">
					<img src="{$Images}&cqPath;" alt="&cqText;"/>
				</div>
			</xsl:when>
			<xsl:when test="$PublisherType = '&AlisonFrankel;'">
				<div class="&pubLogoClass;">
					<img src="{$Images}&a_frankelPath;" alt="&a_frankelText;"/>
				</div>
			</xsl:when>				
			<xsl:otherwise>
				<!-- Do nothing -->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>