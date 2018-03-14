<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Title -->
  
  <xsl:template match="title.block" priority="2">
    <xsl:apply-templates />
  </xsl:template> 
 
	<xsl:template match="title.block/primary.title | article/article.title" name="HeadlineDisplaying">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>&simpleContentBlockClass;</xsl:text>
			</xsl:attribute>
			<xsl:call-template name="displayHeadlineHeading">
				<xsl:with-param name="headingSize" select="1"/>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>

  <xsl:template name="displayHeadlineHeading">
    <xsl:param name="headingSize"/>
    <xsl:element name="h{$headingSize}">
      <xsl:attribute name="class">
        <xsl:text>&documentHeader; &alignHorizontalCenterClass; &indentBottomClass;</xsl:text>
      </xsl:attribute>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <xsl:template name="displayHeadingElement">
    <xsl:param name="headingSize"/>
    <xsl:element name="h{$headingSize}">
      <xsl:attribute name="class">
        <xsl:text>&documentHeader; &alignHorizontalCenterClass; &indentTopClass; &indentBottomClass;</xsl:text>
      </xsl:attribute>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

	<!-- Document -->
	
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
					<xsl:value-of select="' &contentTypeNewsAlertClass;'"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="not($DeliveryMode) and n-metadata/metadata.block/md.infotype='CQDAILIES'">
				<xsl:call-template name="DisplayPublisherLogo">
					<xsl:with-param name="PublisherType" select="'&CongressionalQuarterly;'" />
				</xsl:call-template>
			</xsl:if>	
			<xsl:apply-templates />
			<xsl:if test="n-metadata/metadata.block/md.infotype='CQDAILIES'">
				<xsl:call-template name="displayCopyright"/>
			</xsl:if>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="article/article.date">
	</xsl:template>

	<xsl:template match="article/article.date" mode="render">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="article/lead.para">
		<xsl:call-template name="para" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="article/lead.para/paratext">
		<xsl:call-template name="renderParagraphTextDiv">
			<xsl:with-param name="contents">
				<xsl:variable name="dateCheck">
					<xsl:apply-templates select="/Document/n-docbody/news.alert.document/article/article.date" mode="render"/>
				</xsl:variable>
				<xsl:if test="string-length($dateCheck) &gt; 0">
					<xsl:text>(</xsl:text>
					<xsl:copy-of select="$dateCheck"/>
					<xsl:text>) - </xsl:text>
				</xsl:if>
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="author.byline">
		<xsl:call-template name="wrapWithDiv" >
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="author.biography">
		<xsl:call-template name="wrapWithDiv" >
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>		

	<!-- Related Articles Section at bottom of page-->
	<xsl:template match="related.article.block">
		<xsl:if test="child::node()">
			<h2>
				<xsl:text>Related Articles</xsl:text>
			</h2>
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<xsl:template match="related.article.cite">
		<div>
			<strong>
				<xsl:text>Article: </xsl:text>
			</strong>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="related.article.cite/article.title">
		<xsl:apply-templates />
		<xsl:text>&nbsp;</xsl:text>
	</xsl:template>

	<xsl:template match="article.date">
		<div>
			<strong>
				<xsl:text>Date: </xsl:text>
			</strong>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="related.article/para">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Article Deatils Section (Blue Box) on right side of page JS-->
	<xsl:template match="article.detail.block">
		<xsl:if test="child::node()">
			<ul class="&hideStateClass;" id="&detailBlockWidgetId;">
				<xsl:apply-templates />
			</ul>
		</xsl:if>
	</xsl:template>

	<xsl:template match="article.detail.block/topic.block">
		<li class="&containerRelatedInfoItemClass;">
			&topicsText;
		</li>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="topic.block/topic">
		<li>
			<ul class="&containerRelatedInfoItemListClass;">
				<xsl:apply-templates />
			</ul>
		</li>
	</xsl:template>

	<xsl:template match="topic.block/topic/topic.line">
		<li class="&containerRelatedInfoItemClass;">
			<xsl:call-template name="createNewsAlertSearchAnchor">
				<xsl:with-param name ="query">
					<xsl:text>Topic("</xsl:text>
					<xsl:apply-templates />
					<xsl:text>")</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template match="topic.block/topic/general.description">
		<li class="&containerRelatedInfoItemClass;">
			<xsl:apply-templates />
		</li>
	</xsl:template>

	<xsl:template match="topic/subtopic">
		<li>
			<ul class="&containerRelatedInfoItemListClass;">
				<xsl:apply-templates />
			</ul>
		</li>
	</xsl:template>

	<xsl:template match="subtopic/subtopic.line">
		<li class="&containerRelatedInfoItemClass;">
			<xsl:call-template name="createNewsAlertSearchAnchor">
				<xsl:with-param name ="query">
					<xsl:text>Topic("</xsl:text>
					<xsl:apply-templates select="parent::subtopic/preceding-sibling::topic.line"/>
					<xsl:text>")&amp;Subtopic("</xsl:text>
					<xsl:apply-templates />
					<xsl:text>")</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template match="subtopic/subtopic2">
		<li>
			<ul class="&containerRelatedInfoItemListClass;">
				<xsl:apply-templates />
			</ul>
		</li>
	</xsl:template>

	<xsl:template match="subtopic2/subtopic2.line">
		<li class="&containerRelatedInfoItemClass;">
			<xsl:call-template name="createNewsAlertSearchAnchor">
				<xsl:with-param name ="query">
					<xsl:text>Topic("</xsl:text>
					<xsl:apply-templates select="ancestor::subtopic/preceding-sibling::topic.line"/>
					<xsl:text>")&amp;Subtopic("</xsl:text>
					<xsl:apply-templates select="parent::subtopic2/preceding-sibling::subtopic.line"/>
					<xsl:text>")&amp;Subtopic2("</xsl:text>
					<xsl:apply-templates />
					<xsl:text>")</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template name="createNewsAlertSearchAnchor">
		<xsl:param name="query"/>
		<a>
			<xsl:attribute name="href">
				<xsl:call-template name="GetSearchResultUrl">
					<xsl:with-param name="query" select="$query" />
					<xsl:with-param name="categoryPageUrl">
						<xsl:choose>
							<xsl:when test="/Document/n-metadata/metadata.block/md.infotype='CQDAILIES'">
								<xsl:text>Home/SecondarySources/LegalNewspapersNewsletters/CQRollCallWashingtonBriefings</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>Home/SecondarySources/LegalNewspapersNewsletters/WestlawDailyBriefings</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="simpleSearch" select="'true'" />
				</xsl:call-template>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>

	<xsl:template match="related.filings.block">
		<xsl:if test="child::node()">
			<li class="&containerRelatedInfoItemClass;">
				&relatedFilings;
			</li>
			<li>
				<xsl:apply-templates />
			</li>
		</xsl:if>
	</xsl:template>

	<xsl:template match="related.filings.block/related.filing">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="judge.block">
		<xsl:if test="child::node()">
			<li class="&containerRelatedInfoItemClass;">
				&judicialProfiles;
			</li>
			<li>
				<xsl:apply-templates />
			</li>
		</xsl:if>
	</xsl:template>

	<xsl:template match="judge.line">
		<xsl:apply-templates select="judge" />
		<xsl:apply-templates select="title" />
		<xsl:apply-templates select="jurisdiction" />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="judge.line/title | judge.line/jurisdiction">
		<xsl:text>, </xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="firm.attorney.block">
		<xsl:if test="child::node()">
			<li class="&containerRelatedInfoItemClass;">
				&attorneyProfiles;
			</li>
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<xsl:template match="firm.attorney.block/party.block">
		<li>
			<xsl:apply-templates />
			<xsl:text> for </xsl:text>
			<xsl:value-of select="@role"/>
		</li>
	</xsl:template>
	
	<xsl:template match="firm.attorney.group/attorney.block">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="firm.attorney.group[position() > 1]">
		<xsl:text>, </xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="attorney.block/attorney[position() > 1]">
		<xsl:text>, </xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="firm.block/firm">
		<xsl:if test="ancestor::firm.attorney.group/attorney.block/attorney and string-length(normalize-space(ancestor::firm.attorney.group/attorney.block/attorney)) &gt; 0">
			<xsl:text>; </xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="firm.block/address.block">
	</xsl:template>

	<xsl:template match="firm.attorney.group/additional.info">
	</xsl:template>

	<xsl:template match="organization.block">
		<xsl:if test="child::node()">
			<li class="&containerRelatedInfoItemClass;">
				&companyReports;
			</li>
			<li>
				<xsl:apply-templates />
			</li>
		</xsl:if>
	</xsl:template>

	<xsl:template match="organization.line">
		<xsl:if test="position() > 1">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:apply-templates select="organization" />
	</xsl:template>

	<!--Additional copyright message for Congressional Quarterly documents-->
	<xsl:template name="displayCopyright">
		<xsl:variable name="copyright_node" select="concat('&copy; ', $currentYear, ' &cqCopyright;') "/>
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="$copyright_node"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
