<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
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

	<!-- Don't render these elements -->
	<xsl:template match="map|metadata.block|narrative-section-title|title|book-part-title|narrative-chapter-title|fulltext_metadata|authors|currcom|sub-book-part-title|narrative-section-group-title|narrative-para-group-title|headers|rule-number|rule-title|narrative-paragraph-title" />

	<xsl:variable name="IsUKJournalsIndex" select="/Document/document-data/collection = 'UK_SMG_JRNLINDEX'"/>

	<xsl:variable name="primarycite" select="Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite" />

	<xsl:variable name="displayableCitesForContentType" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info/md.display.primarycite[@display = 'Y'] | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info/md.display.parallelcite[@display = 'Y' or @userEntered = 'Y'] | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite.wl/md.primarycite.info/md.display.primarycite[@display = 'Y']" />

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadataForContentType" />
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<xsl:apply-templates select="n-docbody/document/data"/>

			<!-- A)  Render the top -->
			<xsl:choose>
				<xsl:when test="/Document/document-data/collection = 'UK_SMG_COMMENTARY'">
					<xsl:call-template name="Prelim" />
					<xsl:choose>
						<xsl:when test ="not(descendant::ubrfile)">
							<xsl:apply-templates select ="descendant::document"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="descendant::ubrfile" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>

			<!-- Display footnotes at bottom of page -->
			<xsl:call-template name="internationalFootnote" />

			<!--
				******************************************************************************************************
				* Backlog Item 506268: 
				* Remove all logos from International content. 
				* Add copyright message from royality block and message block centered at the bottom of the document.
				******************************************************************************************************
			-->
			<div class="&centerClass;">
				<xsl:call-template name="copyrightBlock">
					<xsl:with-param name="copyrightNode" select="n-docbody/copyright-message" />
				</xsl:call-template>
			</div>

			<div class="&alignHorizontalLeftClass;">
				<xsl:apply-templates select="$primarycite" />
			</div>

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

		</div>
	</xsl:template>

	<!-- A)  Render the top START-->

	<xsl:template name="Prelim">
		<xsl:apply-templates select="descendant::ubrfile/title|descendant::ubrfile/book-part-title|descendant::ubrfile/narrative-chapter-title|descendant::ubrfile/sub-book-part-title|descendant::ubrfile/narrative-section-title|descendant::ubrfile/headers|descendant::copyright-message | descendant::document/headers" mode="render" />
		<xsl:apply-templates select="descendant::currcom" mode="render" />
		<xsl:apply-templates select="descendant::ubrfile/narrative-para-group-title|descendant::ubrfile/rule-number|descendant::ubrfile/narrative-paragraph-title" mode="render" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="title|book-part-title|copyright-message|narrative-chapter-title|narrative-section-title|sub-book-part-title|currcom" mode="render">
		<xsl:if test="not(ancestor::map)">
			<div class="&centerClass;">
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template match="headers" mode="render">
		<xsl:for-each select="node()">
			<div class="&centerClass;">
				<xsl:apply-templates/>
			</div>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="narrative-para-group-title | rule-number | narrative-paragraph-title | paragraph" mode="render">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="section">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>&paraMainClass;</xsl:text>
				<xsl:choose>
					<xsl:when test="parent::legis-extract">
						<xsl:text> &indentLeft2Class;</xsl:text>
					</xsl:when>
					<xsl:when test="ancestor::extract">
						<xsl:text> &indentLeft1Class;</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="rule-title" mode="render">
		<xsl:text> </xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- A)  Render the top-END-->

	<!-- B)  Render the content-START -->
	<xsl:template match="narrative-sub1 | narrative-sub2">
		<div class="&paraIndentClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="narrative-paragraph-number[name(following-sibling::*[1]) = 'narrative-sub2'] | number[name(following-sibling::*[1]) = 'sub2']">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="narrative-paragraph-number | narrative-section-title | para-number">
		<div class="&headtextClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match ="number">
		<span class="&headtextClass;">
			<xsl:apply-templates/>
		</span>
	</xsl:template>

	<!-- and not(preceding-sibling::para)-->
	<xsl:template match="para-text|para">
		<xsl:if test="not(preceding-sibling::para-text) and not(parent::narrative-sub1) and not(preceding-sibling::para) and not(parent::sub1 or parent::sub2) or preceding-sibling::table">
			<xsl:text>&#160;</xsl:text>
		</xsl:if>
		<xsl:choose>
			<xsl:when test ="ancestor::Document/descendant::ubrfile">
				<div class="&paraIndentClass;">
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="name(following-sibling::*[1]) = 'table'">
			<xsl:text>&#160;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="para-text[ancestor::table]">
		<xsl:if test ="preceding-sibling::para-text">
			<xsl:text>&#160;</xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>


	<xsl:template match="legis-link">
		<div class="&paraMainClass;">
			<xsl:choose>
				<xsl:when test="not(preceding-sibling::legis-link)">

				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<!-- B)  Render the content-END -->
	<xsl:template match="data">
		<xsl:call-template name="DisplayHeader"/>
		<div class="&paraMainClass;">&#160;</div>
		<xsl:apply-templates select="//n-docbody/document/metadata.block/cases"/>
		<xsl:apply-templates select="//n-docbody/document/metadata.block/legislation"/>
		<!-- the following have been suppressed from the full text at AB's request
		<xsl:apply-templates select="abstract"/>
		-->
		<xsl:apply-templates select="subjects"/>
		<xsl:apply-templates select="keywords"/>
		<xsl:apply-templates select="abstract"/>
		<xsl:apply-templates select="legislation" />
		<xsl:apply-templates select="cases" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:apply-templates select="journal-section"/>

	</xsl:template>

	<!-- Header-->
	<xsl:template name="DisplayHeader">
		<xsl:choose>
			<xsl:when test="$IsUKJournalsIndex = true()">
				<div class="&centerClass;">
					<div class="&headtextClass; &centerClass;">
						<xsl:apply-templates select="//header//identifier"/>
					</div>
					<br/>
					<xsl:apply-templates select ="//copyright-message"/>
					<br/>
					<xsl:apply-templates select ="entry_type"/>
					<br/>
					<xsl:apply-templates select ="title"  mode="JournalIndex"/>
					<br/>
					<xsl:apply-templates select ="contributors"/>
					<xsl:apply-templates select ="citation"/>
					<xsl:apply-templates select ="journal_indexed"/>
					<br/>
					<xsl:apply-templates select ="publication_date"/>

				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="citation" />
				<xsl:apply-templates select="journal_title"/>
				<xsl:apply-templates select="journal_year"/>

				<xsl:apply-templates select="title" mode="heading"/>
				<xsl:apply-templates select="contributors"/>
				<!-- Copyright message -->
				<xsl:apply-templates select="//copyright-message"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- START Commentary section -->
	<xsl:template match="anchors">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&indentLeft1Class;</xsl:text>
			</xsl:attribute>
			<xsl:if test="link">
				<div class="&headtextClass;">
					<strong>
						<xsl:text>&ukCommentary;</xsl:text>
					</strong>
				</div>
				<xsl:for-each select="link">
					<xsl:if test="string-length(.) &gt; 0">
						<a>
							<xsl:attribute name="href">
								<xsl:text>#</xsl:text>
								<xsl:call-template name="remove-punctuation-and-enforce-camel-case">
									<xsl:with-param name="string" select="translate(@tanchor, ' ', '')" />
								</xsl:call-template>
							</xsl:attribute>
							<xsl:value-of select="./text()"/>
						</a>
						<br/>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="annotation">
		<span>
			<xsl:attribute name="id">
				<xsl:call-template name="remove-punctuation-and-enforce-camel-case">
					<xsl:with-param name="string" select="translate(@id, ' ', '')" />
				</xsl:call-template>
			</xsl:attribute>
			<xsl:apply-templates />
		</span>
	</xsl:template>
	<!-- END Commentary section -->

	<xsl:template match="sub1">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<p>
					<xsl:apply-templates />
				</p>
			</xsl:when>
			<xsl:otherwise>
				<div class="&indentLeft2Class; &paraMainClass;">
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="sub2">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>&indentLeft2Class;</xsl:text>
				<xsl:if test="not(parent::update)">
					<xsl:text> &paraMainClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::text() and not(following-sibling::sub2)">
				<div class="&paraMainClass;">&#160;</div>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<xsl:template match="sub3">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>&indentLeft2Class;</xsl:text>
				<xsl:if test="not(parent::update)">
					<xsl:text> &paraMainClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::text()">
				<div class="&paraMainClass;">&#160;</div>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!-- start-contributors -->
	<xsl:template match="contributors">
		<xsl:apply-templates select="author"/>
		<xsl:apply-templates select="editor"/>
		<xsl:apply-templates select="reviewer"/>
	</xsl:template>

	<xsl:template match="editor">
		<br/>Edited by <xsl:apply-templates />
	</xsl:template>

	<xsl:template match="author">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="reviewer">
		<br/>Reviewed by <xsl:apply-templates />
	</xsl:template>
	<!-- end-contributors -->

	<xsl:template match="journal_indexed">
		[<xsl:apply-templates />]
	</xsl:template>

	<xsl:template match="publication_date">
		Publication Date: <xsl:value-of select="@year" />
	</xsl:template>

	<xsl:template match="identifier">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="copyright-message">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="title"  mode="JournalIndex">
		<xsl:apply-templates />
	</xsl:template>


	<xsl:template match="citation">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Header section -->
	<xsl:template match="journal_title">
		<div class="&headtextClass; &centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="title" mode="heading">
		<div class="&centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="journal_year|author">
		<div class="&centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="cases">
		<div class="&paraMainClass;">
			<strong>
				<xsl:choose>
					<xsl:when test="count(child::case)=1">
						<xsl:text>&ukCase;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>&ukCases;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="case">
		<xsl:if test="position()!=1">
			<xsl:choose>
				<xsl:when test="$IsUKJournalsIndex = true()">
					<br/>
				</xsl:when>
				<xsl:otherwise>
					<div class="&paraMainClass;">&#160;</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="legislation">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukLegislation;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="leg_citation">
		<xsl:apply-templates/>
		<xsl:if test="position()=1">&#160;</xsl:if>
	</xsl:template>

	<xsl:template match="leg_referred">
		<xsl:if test="position()!=1">
			<div class="&paraMainClass;">&#160;</div>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="subjects">
		<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="main_subject">
		<strong>
			<xsl:text>&ukSubject;</xsl:text>
		</strong>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="subject">
		<xsl:choose>
			<xsl:when test="preceding-sibling::subject">
				<xsl:apply-templates/>
				<xsl:if test="position()!=last()">
					<xsl:text>. </xsl:text>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<br/>
				<strong>
					<xsl:text>&ukOtherRelatedSubject;</xsl:text>
				</strong>
				<xsl:apply-templates/>
				<xsl:if test="position()!=last()">
					<xsl:text>. </xsl:text>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="keywords">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&ukKeywords;</xsl:text>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="keyword">
		<xsl:apply-templates/>
		<xsl:if test="position()!=last()">; </xsl:if>
	</xsl:template>

	<xsl:template match="abstract">
		<!-- This was suppressed for other collections, so doing it only for journel index-->
		<xsl:choose>
			<xsl:when test="/Document/document-data/collection = 'UK_SMG_JRNLINDEX'">
				<div class="&paraMainClass;">
					<strong>
						<xsl:text>&ukAbstract;</xsl:text>
					</strong>
					<xsl:apply-templates/>
				</div>

			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Document body -->

	<xsl:template match="title">
		<div class="&paraMainClass; &headerClass;">&#160;</div>
		<strong>
			<xsl:apply-templates/>
		</strong>

	</xsl:template>

	<xsl:template match="ital">
		<xsl:if test=".!=''">
			<em>
				<xsl:apply-templates/>
			</em>
		</xsl:if>
	</xsl:template>

	<xsl:template match="citation">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="tbl">
		<xsl:apply-templates />
	</xsl:template>

	<!--********************************************
	         CITE.QUERY AND STARPAGE
	    ********************************************-->



	<!--Suppress article/docid -->
	<xsl:template match="journal-section/article/docid" />
	<xsl:template match="n-docbody/document/ubrfile/title"/>

	<!-- start page supression , here we are suppressing star page info when it contains anything other than numbers.-->
	<xsl:variable name="number"
                select="'0123456789'"/>
	<xsl:template match="starpage.anchor" priority="5">
		<xsl:if test="(string-length(translate(./text(), $number, '')) = 0)">
			<xsl:call-template name="displayStarPage">
				<xsl:with-param name="starPageText">
					<xsl:apply-templates />
				</xsl:with-param>
				<xsl:with-param name="numberOfStars" select="1" />
				<xsl:with-param name="pageset" select="$displayableCiteId" />
			</xsl:call-template>

			<xsl:if test="$IncludeCopyWithRefLinks = true()">
				<xsl:call-template name="generateCopyWithReferenceLink" />
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="item">
		<div>
			<xsl:apply-templates  />
		</div>
	</xsl:template>

	<!--Supressing RTF links-->
	<xsl:template match="image.link[contains(@ID,'.rtf')]"  priority="2"/>

	<xsl:template match="image.block"  priority="1">
		<div class="&centerClass;">
			<xsl:apply-templates/>
		</div>
		<br />
	</xsl:template>

	<!-- M<ade as Italic to display document similar to Web2, this needs to be revisited-->
	<xsl:template match="emphasis">
		<em>
			<xsl:apply-templates />
		</em>
	</xsl:template>

	<xsl:template match="item/para" priority="1">
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template name="listItemsDisplay">
		<xsl:for-each select="item">
			<li>
				<xsl:apply-templates/>
			</li>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="list[@prefix-rules='unordered']">
		<div>
			<ul class="&docUnorderedList;">
				<xsl:call-template name="listItemsDisplay"/>
			</ul>
		</div>
	</xsl:template>

	<xsl:template match="list[@prefix-rules='none']">
		<div>
			<ul>
				<xsl:call-template name="listItemsDisplay"/>
			</ul>
		</div>
	</xsl:template>

	<xsl:template match="list[@prefix-rules='ordered']">
		<div>
			<ol class="&decimalListClass; &indentLeft2Class;">
				<xsl:call-template name="listItemsDisplay"/>
			</ol>
		</div>
	</xsl:template>

	<xsl:template match="list[@prefix-rules='specified']">
		<div>
			<ul>
				<xsl:for-each select="item">
					<li>
						<div class="&docListBullet;">
							<xsl:value-of select="./@prefix"/>
						</div>
						<xsl:apply-templates/>
					</li>
				</xsl:for-each>
			</ul>
		</div>
	</xsl:template>

</xsl:stylesheet>