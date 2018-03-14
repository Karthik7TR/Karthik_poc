<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="indexingTemplate">
		<div class="&indexClass;">
			<div class="&indexHeaderClass;">&indexReferencesLabel;</div>
			<xsl:call-template name="normCompany" />
			<xsl:call-template name="newsSubject"/>
			<xsl:call-template name="industry" />
			<xsl:call-template name="region" />
			<xsl:call-template name="language" />
			<xsl:call-template name="otherIndexing"/>
			<xsl:call-template name="keywords"/>
			<xsl:call-template name="companyTerms"/>
			<xsl:call-template name="product"/>
			<xsl:call-template name="sic"/>
			<xsl:call-template name="naics"/>
			<xsl:call-template name="duns"/>
			<xsl:call-template name="ticker"/>
			<xsl:call-template name="legal"/>
			<xsl:call-template name="substance"/>
			<xsl:call-template name="edition"/>
		</div>
	</xsl:template>

	<!-- Company -->
	<xsl:template name="normCompany">
		<div>
			<xsl:apply-templates select="indexing/extraction-terms/extr-company-block/norm-company" />
		</div>
	</xsl:template>

	<xsl:template match="norm-company">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="not(preceding-sibling::norm-company)">
				<xsl:text>&companyLabel;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
			<xsl:if test="following-sibling::norm-company">
					<xsl:text>&semiColon;</xsl:text>
			</xsl:if>
		</xsl:if>		
	</xsl:template>

	<!-- News Subject -->
	<xsl:template name="newsSubject">
		<div>
			<xsl:apply-templates select="indexing/classification-terms/pres-subject-block/pres-subject-wrap"/>
		</div>
	</xsl:template>

	<xsl:template match="pres-subject-wrap">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="not(preceding-sibling::pres-subject-wrap)">
				<xsl:text>&newsSubjectLabel;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
			<xsl:choose>
				<xsl:when test="following-sibling::pres-subject-wrap">
					<xsl:text>&semiColon;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&rightParenthesis;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>		
	</xsl:template>

	<xsl:template match="pres-subject">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="alt1-subject-code">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:text>&leftParenthesis;</xsl:text>
			<xsl:copy-of select="$content"/>
			<xsl:text>&rightParenthesis;</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Industry -->
	<xsl:template name="industry">
		<div>
			<xsl:apply-templates select="indexing/classification-terms/pres-industry-block/pres-industry-wrap"/>
		</div>
	</xsl:template>

	<xsl:template match="pres-industry-wrap">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="not(preceding-sibling::pres-industry-wrap)">
				<xsl:text>&industryLabel;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
			<xsl:choose>
				<xsl:when test="following-sibling::pres-industry-wrap">
					<xsl:text>&semiColon;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&rightParenthesis;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="alt1-industry-code">
		<xsl:variable name="content">
			<xsl:apply-templates/>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:text>&leftParenthesis;</xsl:text>
			<xsl:copy-of select="$content"/>
			<xsl:text>&rightParenthesis;</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Region -->
	<xsl:template name="region">
		<div>
			<xsl:apply-templates select="indexing/classification-terms/pres-location-block/pres-location-wrap"/>
		</div>
	</xsl:template>

	<xsl:template match="pres-location-wrap">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="not(preceding-sibling::pres-location-wrap)">
				<xsl:text>&regionLabel;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
			<xsl:choose>
				<xsl:when test="following-sibling::pres-location-wrap">
					<xsl:text>&semiColon;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&rightParenthesis;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>	
		</xsl:if>
	</xsl:template>

	<xsl:template match="alt1-location-code">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:text>&leftParenthesis;</xsl:text>
			<xsl:copy-of select="$content"/>
			<xsl:text>&rightParenthesis;</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Language -->
	<xsl:template name="language">
		<xsl:if test="content/@iso-language">
			<div>
					<xsl:text>&languageLabel;</xsl:text>
					<xsl:copy-of select ="translate(content/@iso-language,
										'abcdefghijklmnopqrstuvwxyz',
										'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Other Indexing -->
	<xsl:template name="otherIndexing">
		<xsl:variable name="content">
			<xsl:apply-templates select="indexing/extraction-terms/extr-company-block/extr-company"/>
			<xsl:apply-templates select="indexing/extraction-terms/extr-person-block/extr-person"/>
			<xsl:apply-templates select="indexing/index-terms/geographic-term"/>
			<xsl:apply-templates select="indexing/index-terms/descriptor-wrap"/>
			<xsl:apply-templates select="indexing/doc-focus"/>
			<xsl:apply-templates select="indexing/business-terms/industry-wrap"/>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:text>&otherIndexingLabel;</xsl:text>
				<xsl:copy-of select="$content"/>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Extr Company, Extr Person, Geographic Term, Descriptor Wrap, Doc Focus, Industry Wrap -->
	<xsl:template match="extr-company | extr-person | geographic-term | descriptor-wrap | doc-focus | industry-wrap">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="not(preceding-sibling::extr-company | preceding-sibling::extr-person | preceding-sibling::geographic-term
								| preceding-sibling::descriptor-wrap | preceding-sibling::doc-focus | preceding-sibling::industry-wrap)">
				<xsl:text>&leftParenthesis;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
			<xsl:choose>
				<xsl:when test="following-sibling::extr-company | following-sibling::extr-person | following-sibling::geographic-term
										| following-sibling::descriptor-wrap | following-sibling::doc-focus | following-sibling::industry-wrap">
					<xsl:text>&semiColon;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&rightParenthesis;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- Keywords -->
	<xsl:template name="keywords">
		<xsl:if test="indexing/index-terms/term | indexing/index-terms/term-code">
			<div>
				<xsl:apply-templates select="indexing/index-terms"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="index-terms">
		<xsl:if test="not(preceding-sibling::index-terms) or string-length(preceding-sibling::index-terms)&lt;1">
				<xsl:text>&keywordsLabel;</xsl:text>
		</xsl:if>
		<xsl:apply-templates select="term"/>
		<xsl:apply-templates select="term-code"/>
	</xsl:template>

	<xsl:template match="term">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">			
			<xsl:if test="preceding-sibling::term">
				<xsl:text>&semiColon;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="term-code">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="preceding-sibling::term-code">
				<xsl:text>&semiColon;</xsl:text>
			</xsl:if>
			<xsl:text>&leftParenthesis;</xsl:text>
			<xsl:copy-of select="$content"/>
			<xsl:text>&rightParenthesis;</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Company Terms -->	
	<xsl:template name="companyTerms">
		<xsl:variable name="content">
			<xsl:apply-templates select="indexing/business-terms/company | indexing/business-terms/company-family | 
													 indexing/business-terms/company-wrap/company | indexing/business-terms/company-wrap/address"/>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:text>&companyTermsLabel;</xsl:text>
				<xsl:copy-of select="$content"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="company | company-family | company-wrap">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="preceding::company">
				<xsl:text>&semiColon;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
		</xsl:if>		
	</xsl:template>

	<xsl:template match="address | modifier-term | industry-code | modifier-code">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:text>&leftParenthesis;</xsl:text>
			<xsl:copy-of select="$content"/>
			<xsl:text>&rightParenthesis;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="city | country | state">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="self::state">
				<xsl:text>&comma;</xsl:text>
				<xsl:copy-of select="$content"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$content"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	

	<xsl:template match="cusip-num">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:text>&cusipNumberLabel;</xsl:text>
			<xsl:copy-of select="$content"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="company-num">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:text>&companyNumberLabel;</xsl:text>
			<xsl:copy-of select="$content"/>
		</xsl:if>		
	</xsl:template>

	<xsl:template match="org-num">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:text>&orgNumLabel;</xsl:text>
			<xsl:copy-of select="$content"/>
		</xsl:if>		
	</xsl:template>

	<!-- Product -->
	<xsl:template name="product">
		<xsl:variable name="content">
			<xsl:apply-templates select="descendant::product | indexing/business-terms/product-wrap/product-code"/>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:text>&productLabel;</xsl:text>
				<xsl:copy-of select="$content" />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="product | product-code">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="preceding::product-wrap">
				<xsl:text>&semiColon;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
		</xsl:if>
	</xsl:template>

	<!-- Sic -->
	<xsl:template name="sic">
		<xsl:variable name="content">
			<xsl:apply-templates select="descendant::sic-code"/>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:text>&sicLabel;</xsl:text>
				<xsl:copy-of select="$content" />
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="sic-code">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="preceding::sic-code">
				<xsl:text>&semiColon;</xsl:text>				
			</xsl:if>
			<xsl:copy-of select="$content"/>			
		</xsl:if>
	</xsl:template>

	<!-- Naics Code -->
	<xsl:template name="naics">
		<xsl:variable name="content">
			<xsl:apply-templates select="descendant::naics-code"/>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:text>&naicsLabel;</xsl:text>
				<xsl:copy-of select="$content" />
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="naics-code">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="preceding::naics-code">
				<xsl:text>&semiColon;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
		</xsl:if>
	</xsl:template>
	
	<!-- Duns -->
	<xsl:template name="duns">
		<xsl:variable name="content">
			<xsl:apply-templates select="descendant::duns-num"/>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:text>&dunsLabel;</xsl:text>
				<xsl:copy-of select="$content" />
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="duns-num">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="preceding::duns-num">
				<xsl:text>&semiColon;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
		</xsl:if>
	</xsl:template>
	
	<!-- Ticker Symbol -->
	<xsl:template name="ticker">
		<xsl:variable name="content">
			<xsl:apply-templates select="descendant::ticker-symbol"/>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:text>&tickerSymbolLabel;</xsl:text>
				<xsl:copy-of select="$content" />
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="ticker-symbol">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="preceding::ticker-symbol">
				<xsl:text>&semiColon;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
		</xsl:if>
	</xsl:template>
	
	<!-- Legal Terms -->
	<xsl:template name="legal">
		<xsl:variable name="content">
			<xsl:apply-templates select="legal-terms"/>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:text>&legalTermsLabel;</xsl:text>
				<xsl:copy-of select="$content" />
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="legal-terms">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="preceding-sibling::legal-terms">
				<xsl:text>&semiColon;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content"/>
		</xsl:if>
	</xsl:template>
	
	<!-- Substance Terms -->
	<xsl:template name="substance">
		<xsl:variable name="content">
			<xsl:apply-templates select="substance-terms"/>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:text>&substanceTermsLabel;</xsl:text>
				<xsl:copy-of select="$content" />
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="substance-terms">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:text>&leftParenthesis;</xsl:text>
			<xsl:copy-of select="$content"/>
			<xsl:text>&rightParenthesis;</xsl:text>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="substance | cas-reg-num">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:text>&leftParenthesis;</xsl:text>
			<xsl:copy-of select="$content"/>
			<xsl:text>&rightParenthesis;</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Edition -->
	<xsl:template name="edition">
		<xsl:variable name="content">
			<xsl:apply-templates select="pub-info/edition"/>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:text>&editionLabel;</xsl:text>
				<xsl:copy-of select="$content" />
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="edition">
		<xsl:variable name="content">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0">
			<xsl:if test="preceding-sibling::edition">
				<xsl:text>&semiColon;</xsl:text>
			</xsl:if>
			<xsl:copy-of select="$content" />
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
