<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Index.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">	
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			
			<!--Section: Citation-->
		 <xsl:apply-templates select="n-docbody//content.metadata.block/cmd.identifiers/cmd.cites" mode="headerCustomCitation" />
			
			<!--Section: Prelim block-->
			<xsl:apply-templates select="n-docbody/rule/rule.front/prelim.block" />
			<xsl:apply-templates select="n-docbody//prop.block"/>
			<xsl:apply-templates select="n-docbody/arbitration.decision/front"/>


			<!--Section: Title-->
			<xsl:call-template name="renderTitle"/>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			
			<!--Section-->
			<xsl:apply-templates select="n-docbody//section/section.body"/>
			<xsl:apply-templates select="n-docbody/arbitration.decision/body" />

			<!--Section: Rule Body Text-->
			<xsl:apply-templates select="n-docbody/rule/rule.body" />
			
			<!--Section: Matter-->
			<xsl:apply-templates select="n-docbody//end.matter"/>

			<!--Section: Copy right-->
			<xsl:apply-templates select="n-docbody/rule/rule.front/include.copyright" />
			
			<!--Section: Citation-->
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" mode="footerCustomCitation" />
			
			<!--Section: Footnote-->
			<xsl:call-template name="RenderFootnoteSection"/>
		
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
	<!-- Section:Citation-->
	<xsl:template match="n-docbody//content.metadata.block/cmd.identifiers/cmd.cites" mode="headerCustomCitation">
		<div class="&citesClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="/Document/n-metadata/metadata.block/md.identifiers/md.cites" >
		<xsl:apply-templates />
	</xsl:template>

<xsl:template match="caption/expandedcite" >
	<div class="&citesClass;">		
 <xsl:apply-templates />
	</div>
	</xsl:template>
	
	<!--Section:Prelim block-->
	<xsl:template match="n-docbody/rule/rule.front/prelim.block">
			<div class="&preFrontMatterClass;">
					<xsl:apply-templates />
			</div>
	</xsl:template>

	<xsl:template match="prop.block" priority="1">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="section/section.body" priority="1">
		<div class="&sectionClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
		
	<!--Section: Title-->
	<xsl:template name="renderTitle" >
		<xsl:variable name="collection" select="/Document/document-data/collection" />
		<xsl:if test="$collection = 'w_3rd_aaarules' or $collection = 'w_3rd_fin2009' or $collection = 'w_3rd_finabrls' or $collection = 'w_3rd_nyserules' or $collection = 'w_3rd_gmarbrl'">
			<xsl:choose>
				<xsl:when test="n-docbody/rule/rule.front/doc.title">
					<xsl:apply-templates select="n-docbody/rule/rule.front/doc.title"  />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="document-data/title"  />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="document-data/title" priority="1">
		<div class ="&headtextClass; &documentHeadClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
		
	<xsl:template match="n-docbody/rule/rule.front/doc.title" priority="1">
		<div class ="&headtextClass; &documentHeadClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	
 <!--Section: Para Text-->
	<xsl:template match="n-docbody/rule/rule.body">
			<xsl:apply-templates/>
	</xsl:template>
	
	<!--Section: End Matter -->
	<xsl:template match="n-docbody//end.matter">
		<xsl:apply-templates/>
	</xsl:template>
		
	<!-- Section: Footer Citation -->
	<xsl:template match="n-metadata/metadata.block/md.identifiers/md.cites" mode="footerCustomCitation">
		<div class="&citationClass;">
			<xsl:apply-templates select ="md.primarycite/md.primarycite.info"/>
	  </div>
	</xsl:template>
	
	<!--Section: Footnote-->
  <!-- For few GUID Label.Designator is with  the paratext ,due to this Footnote doesnot display proper links & indentation goes wrong-->
	<!--Bug : 314226-->
	<xsl:template match="footnote/para/paratext/label.designator">
		<xsl:variable name="refNumberOutputText">
			<xsl:call-template name="footnoteCleanup">
				<xsl:with-param name="refNumberTextParam" select="." />
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="generateLinkBackToFootnoteRef">
			<xsl:with-param name="refNumberText" select="$refNumberOutputText" />
		  <xsl:with-param name="footnoteId" select="../../../@ID" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="generateLinkBackToFootnoteRef">
		<xsl:param name="refNumberText" select="''" />
		<xsl:param name="footnoteId" select="''" />
		<xsl:param name="pertinentFootnote" select="ancestor-or-self::node()[self::footnote or self::form.footnote or self::endnote or self::form.endnote][1]" />
		<xsl:if test="string-length($refNumberText) &gt; 0">
				<xsl:apply-templates select="$pertinentFootnote" mode="starPageCalculation" />
				<span>
					<xsl:if test="$footnoteId">
						<xsl:attribute name="id">
							<xsl:value-of select="concat('&footnoteIdPrefix;', $footnoteId)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="not(/*[1]/self::summary or /*[1]/self::summaries) and string-length($footnoteId) &gt; 0">
							<xsl:choose>
								<xsl:when test="string-length(key('distinctFootnoteReferenceRefIds', $footnoteId)) &gt; 0">
									<a href="#co_footnoteReference_{$footnoteId}_{generate-id(key('distinctFootnoteReferenceRefIds', $footnoteId)[1])}">
										<xsl:value-of select="$refNumberText"/>
									</a>
								</xsl:when>
								<xsl:when test="string-length(key('distinctFootnoteAnchorReferenceRefIds', $footnoteId)) &gt; 0">
									<a href="#co_footnoteReference_{$footnoteId}">
										<xsl:value-of select="$refNumberText"/>
									</a>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$refNumberText"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$refNumberText"/>
						</xsl:otherwise>
					</xsl:choose>
				</span>
		</xsl:if>
	</xsl:template>
	
	<!--Section:Credit-->
	<xsl:template match="n-docbody/rule/rule.body/credit" priority="1">
		<div class="&partyLineClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<!--Section: Copy right-->
  <xsl:template match="include.copyright" name="CopyRight">
		<div class="&copyrightClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>		
	
	<xsl:template match="n-docbody//include.ed.note">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<xsl:template match="related.index//headtext">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="front/caption/prelim.block | front/caption/source">
		<div class="&simpleContentBlockClass; &prelimBlockClass;">
			<div class="&centerClass;">
				<xsl:apply-templates select="node()"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="caption/date.line">
		<div class="&centerClass;">
			<xsl:call-template name="dateLine"/>
		</div>
	</xsl:template>

	<xsl:template match="related.index">
		<div class="&indexClass;">
			<xsl:apply-templates select="node()"/>
		</div>
	</xsl:template>


	<xsl:template match="summary//headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="body//headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
			<xsl:text>&#160;</xsl:text>
		</div>
	</xsl:template>
	
<xsl:template match="body//arbitrator">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="address">
		<div>&#160;</div>
		<div>&#160;</div>
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="address.text|phone|fax|email|url">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="tbl" priority="5">
		<xsl:call-template name="wrapWithDiv" >
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<!-- 
		This is a fix for Bug #532242, where there is unknown unicode character representing a bullet symbol.
		This character is replaced with a bullet symbol that most fonts support.
		Ex: I6b66768c760311de9b8c850332338889
	-->
	<xsl:template match="list.item/text()[/Document/document-data/collection = 'w_3rd_gmarbrl']" priority="5">
		<xsl:value-of select="translate(.,'&#58502;','&bull;')"/>
	</xsl:template>

	<!--
		Footnotes markup in arbitration documents creates issues in document delivery 
		(bad tables FO markup causing delivery to fail. For instance, no footnote.body element).
		Remove tables FO markup for arbitration docs only. Follow doc display rendering.
	-->
	
	<xsl:template name="RenderFootnoteMarkup" priority="5">
		<xsl:param name="contents"/>
		<xsl:call-template name="RenderFootnoteMarkupDiv">
			<xsl:with-param name="contents" select="$contents"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="RenderFootnoteBodyMarkup" priority="5">
		<xsl:param name="contents"/>
		<xsl:call-template name="RenderFootnoteBodyMarkupDiv">
			<xsl:with-param name="contents" select="$contents"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="RenderFootnoteNumberMarkup" priority="5">
		<xsl:param name="contents"/>
		<xsl:call-template name="RenderFootnoteNumberMarkupDiv">
			<xsl:with-param name="contents" select="$contents"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="RenderFootnoteSectionMarkup" priority="5">
		<xsl:param name="contents"/>
		<xsl:call-template name="RenderFootnoteSectionMarkupDiv">
			<xsl:with-param name="contents" select="$contents"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
