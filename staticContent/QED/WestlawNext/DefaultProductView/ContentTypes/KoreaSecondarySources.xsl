<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<!--<xsl:include href="Cites.xsl"/>-->
	<xsl:include href="Title.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Prelim.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="DocLinks.xsl"/>
	<xsl:include href="StarPagesWithoutRules.xsl"/>
	<xsl:include href="HtmlTable.xsl"/>
	<xsl:include href="InternationalFootnote.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="IsKRJournals" select="/Document/document-data/collection = 'wli_intl_krjrnls'"/>
	<xsl:variable name="primaryCitation" select="/Document/n-docbody/data/citation/text()"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<div class="&citationClass;">
				<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.references/md.print.rendition.id[@ID='individual_article']"/>
			</div>			

			<xsl:call-template name="DocumentHeader"/>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			
			<xsl:apply-templates select="n-docbody/data/journal_section/article/content" />

			<!-- Display footnotes at bottom of page -->
			<xsl:call-template name="internationalFootnote" />

			<div class="&centerClass;">
				<xsl:call-template name="copyrightBlock">
					<xsl:with-param name="copyrightNode" select="n-docbody/copyright-message" />
				</xsl:call-template>
			</div>
			
			<div class="&alignHorizontalLeftClass;">
				<div class="&paratextMainClass;">&#160;</div>
				<xsl:value-of select="$primaryCitation"/>
			</div>

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

		</div>
	</xsl:template>	

	<xsl:template match="list" priority="5">
		<div>			
			<xsl:if test="list.item">
				<ul class="&listClass;">
					<xsl:apply-templates select="node()[not(self::list)]" />
				</ul>
			</xsl:if>
		</div>
	</xsl:template>

	<!--Starpaging Anchor-->
	<xsl:template match="star.page">
		*<xsl:apply-templates />
		<xsl:if test="following-sibling::text()">
			<xsl:text> </xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template name="HeaderCitation">
		<xsl:variable name="citation">
			<xsl:choose>
				<xsl:when test="/Document/document-data/collection = 'wli_intl_krjrnls'">
					<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites//md.first.line.cite"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="string-length($citation) &gt; 0">
			<div class="&citationClass;">
				<xsl:value-of	select="$citation"	/>
				<br/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="DocumentHeader">
		<div class="&centerClass;">
			<xsl:apply-templates select="/Document//md.display.primarycite" />
			<br/>
			<br/>
			<xsl:apply-templates select="/Document//journal_title" />
			<br/>
			<br/>
			<xsl:apply-templates select="/Document//journal_year" />
			<br/>
			<br/>
			<xsl:call-template name="title" />
			<br/>			
			<xsl:apply-templates select="/Document/n-docbody/data/journal_section/article/author" />
			<br/>
			<br/>
			<xsl:apply-templates select="/Document/n-docbody/header/copyright" />
			<br/>
			<br/>
		</div>
	</xsl:template>

	
	<xsl:template name="title">
		<div class="&centerClass;">
			<xsl:apply-templates select="/Document/n-docbody/data/articleTitle" />
		</div>
	</xsl:template>	

	<!-- Don't display these -->
	<xsl:template match="/Document/n-docbody/header//document_citation" priority="5"/>
	<xsl:template match="/Document/n-docbody/data//citation" priority="5"/>	
	<xsl:template match="/Document/n-docbody/data/contributors/author.block/author/author.description" priority="5"/>
	<xsl:template match="/Document/n-docbody/data/contributors/author.block/author/author.name" priority="5"/>
	<xsl:template match="title" priority="5" />
	<xsl:template match="contributors" priority="5"/>	
	<xsl:template match="/Document/n-docbody/data/journal_section/article/content/image.block/image.link" priority="5"/>
	<xsl:template match="/Document/n-docbody/data/journal_section/article/content/image.block/image.text" priority="5"/>
	<xsl:template match="/Document/n-docbody/data/contributors/author.block" priority="5"/>
	<xsl:template match="/Document/n-docbody/header/caseslug" priority="5"/>
	<xsl:template match="/Document/n-docbody/header/dms_number" priority="5"/>
	<xsl:template match="/Document/n-docbody/header/document_id" priority="5"/>
	<xsl:template match="/Document/n-docbody/header/views/view" priority="5"/>
	<xsl:template match="/Document/n-docbody/header/royalty" priority="5"/>
	<xsl:template match="/Document/n-docbody/data/journal_id" priority="5"/>
	<xsl:template match="/Document/n-docbody/data/volume_number" priority="5"/>
	<xsl:template match="map|read|views|sort|korean|image.text" priority="5"/>	

	<!--Starpaging Anchor-->
	<xsl:template match="star.page">
		*<xsl:apply-templates />
		<xsl:if test="following-sibling::text()">
			<xsl:text> </xsl:text>
		</xsl:if>
	</xsl:template>


	<xsl:template match ="heading">
		<b>
			<br/>
			<xsl:apply-templates />
			<br/>
			<br/>
		</b>
	</xsl:template>

	<xsl:template match="para | para.group">
		<xsl:choose>
			<xsl:when test ="parent::footnote">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>			
					<xsl:apply-templates />				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="ital">
		<xsl:if test=".!=''">
			<em>
				<xsl:apply-templates />
			</em>
		</xsl:if>
	</xsl:template>	

	<!-- Render the CONTENT view. -->
	<xsl:template name="Content">
		<xsl:apply-templates />
	</xsl:template>	

	<!--END-->

</xsl:stylesheet>