<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Date.xsl"/>
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

    <xsl:variable name="originationContextParamValue" select="'napaDocument'"/>

    <xsl:template match="Document">
        <!-- make sure the napa document doesn't get chunked -->
        <xsl:call-template name="startUnchunkableBlock" />
        <div id="&documentClass;">
            <xsl:call-template name="AddDocumentClasses"/>
            <xsl:comment> &genericStylesheetMessageText; </xsl:comment>
            <xsl:call-template name="StarPageMetadata" />
            <xsl:apply-templates select="n-docbody/napa.document" />
            <xsl:call-template name="EndOfDocument" />
        </div>
        <xsl:call-template name="endUnchunkableBlock" />
    </xsl:template>

    <xsl:template match="napa.document">
        <xsl:if test="front.matter/article.block">
            <xsl:call-template name="RenderAttorneyBio">
                <xsl:with-param name="articleBlock" select="front.matter/article.block" />
            </xsl:call-template>
        </xsl:if>
        <br />
        <div id="co_article">
            <xsl:apply-templates />
        </div>

        <div id="co_additionalResearchPlaceholder" />
        
        <div id="co_acknowledgementsPlaceholder" />
        <xsl:call-template name="RenderAcknowledgements" />
        
        <div id="co_feedbackPlaceholder" />
        <div id="co_copyrightPlaceholder" />
        
        <div id="napaHiddenToc" style="display:none">
            <div id="co_articleTocContainer">
                <div id="co_articleToc">
                    <div id="co_articleTocInner">
                        <div class="&genericBox;">
                            <xsl:call-template name="RenderTOC">
                                <xsl:with-param name="document" select="." />
                            </xsl:call-template>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>

    <xsl:template name="RenderAcknowledgements">
        <xsl:param name="footnote" select="/Document/n-docbody/napa.document//footnote" />
        <div id="co_acknowledgments">
            <h3>
                <xsl:value-of select="$footnote/label.designator"/>
            </h3>
            <xsl:for-each select="$footnote/footnote.body/para">
                <p>
                    <xsl:attribute name="id">co_acknowledgment_<xsl:value-of select="position()"/></xsl:attribute>
                    <sup>
                        <a href="#co_top_acknowledgement_1">1</a>
                    </sup>
                    <xsl:value-of select="."/>
                </p>
            </xsl:for-each>
        </div>
    </xsl:template>
    
    <xsl:template match="footnote">
        <!-- do nothing -->
    </xsl:template>
    
    <!-- Attorney/Firm bio information located at the top of the document (not fixed header)-->
    <xsl:template name="RenderAttorneyBio">
        <xsl:param name="articleBlock" select="/Document/n-docbody/napa.document/front.matter/article.block" />
        <div id="co_articleHead">
            <xsl:if test="$DeliveryMode">
                <div style="color: #F79200; font-size: 2em; font-family: Knowledge Medium, Helvetica; font-weight: bold;">
                    <xsl:value-of select="/Document/n-docbody/napa.document/front.matter/article.block/product.title" />
                </div>
            </xsl:if>
          
            <h1 class="&practitionerInsightsLogo;">Practitioner Insights</h1>
          
            <div class="&changeJurisdiction;" noDeliver="true">
                <xsl:if test="not(/Document/otherJurisdictions/juris)">
                    <xsl:attribute name="class">
                        <xsl:text>co_changeJurisdiction co_disabled</xsl:text>
                    </xsl:attribute>
                </xsl:if>
                <a href="#" id="co_changeJurisLink">
                    Change Jurisdiction
                </a>
                <div class="&dropdownMenu;">
                    <!-- The “co_expanded” class controls the visibility of the menu, JS takes care of removing/adding co_expanded-->
                    <ul>
                        <xsl:if test="/Document/otherJurisdictions/juris">
                            <xsl:for-each select="/Document/otherJurisdictions/juris">
                                <li>
                                    <a>
                                        <xsl:variable name="additionalDocGuid">
                                            <xsl:value-of select="@guid"/>
                                        </xsl:variable>
                                        <xsl:variable name="persistentURL">
                                            <xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.Document', concat('guid=',$additionalDocGuid), 'viewType=FullText', concat('originationContext=', $originationContextParamValue), '&transitionTypeParamName;=&transitionTypeDocumentItem;' )"/>
                                        </xsl:variable>
                                        <xsl:attribute name="href">
                                            <xsl:value-of select ="$persistentURL"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="@state"/>
                                    </a>
                                </li>
                            </xsl:for-each>
                        </xsl:if>
                    </ul>
                    <div class="&dropdownMenuFooter;"></div>
                </div>
            </div>

            <h2 id="co_articleTitle">
                <xsl:value-of select="$articleBlock/article.title"/>
            </h2>
            <ul class="&inlineList;">
                <li class="&updated;">
                    <strong>UPDATED: </strong>
                    <xsl:call-template name="parseYearMonthDayDateFormat">
                        <xsl:with-param name="date" select="/Document/n-metadata/prism-clipdate"/>
                        <xsl:with-param name="displayDay" select="'true'" />                        
                    </xsl:call-template>
                </li>
                <!--
                <li>
                    <a href="#">View Article Archive</a>
                </li>
                -->
                
                <li noDeliver="true" class="&manageAlerts;">
                    <a href="#" class="&alertsWidgetDrawOnClickClass;">
                        Manage Alerts
                    </a>
                </li>
            </ul>

            <xsl:variable name="authorGuid" select="$articleBlock/image.block/image.link[@ttype='Napa_author']/@target" />
            <xsl:variable name="firmGuid" select="$articleBlock/image.block/image.link[@ttype='Napa_firm']/@target" />

            <div id="&author;" class="&summaryBox;">
                <div class="&summaryBox_tr;">
                    <div class="&summaryBox_br;">
                        <div class="&summaryBox_bl;">
                            <div class="&authorThumb;">
                                <xsl:if test="/Document/ImageMetadata/n-metadata[@ttype='Napa_author'] != ''">
                                    <xsl:variable name="attorneyImageSource">
                                        <xsl:choose>
                                            <xsl:when test="$DeliveryMode">
                                                <xsl:call-template name="createBlobLink">
                                                    <xsl:with-param name="guid" select="$authorGuid"/>
                                                    <xsl:with-param name="mimeType" select="'image/x-png'"/>
                                                    <xsl:with-param name="targetType" select="'Napa_author'" />
                                                    <xsl:with-param name="forImgTag" select="'true'" />
                                                </xsl:call-template>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:call-template name="createBlobLink">
                                                    <xsl:with-param name="guid" select="$authorGuid"/>
                                                    <xsl:with-param name="mimeType" select="'image/x-png'"/>
                                                    <xsl:with-param name="targetType" select="'Napa_author'" />
                                                </xsl:call-template>
                                            </xsl:otherwise>
                                      </xsl:choose>
                                  </xsl:variable>
                                    <xsl:choose>
                                        <xsl:when test="/Document/ImageMetadata/n-metadata[@ttype='Napa_author']/author.md.block/website.md.url != ''">
                                            <a class="&pauseSessionOnClickClass;" target="_blank">
                                                <xsl:attribute name="href">
                                                    <xsl:value-of select="/Document/ImageMetadata/n-metadata[@ttype='Napa_author']/author.md.block/website.md.url"/>
                                                </xsl:attribute>
                                                <img>
                                                    <xsl:attribute name="src">
                                                        <xsl:value-of select="$attorneyImageSource"/>
                                                    </xsl:attribute>
                                                    <xsl:attribute name="alt">
                                                        <xsl:value-of select="/Document/ImageMetadata/n-metadata[@ttype='Napa_author']/author.md.block/author.md.full.name"/>
                                                        <xsl:text>&apos;s website</xsl:text>
                                                    </xsl:attribute>
                                                </img>
                                            </a>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <img>
                                                <xsl:attribute name="src">
                                                    <xsl:value-of select="$attorneyImageSource"/>
                                                </xsl:attribute>
                                            </img>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:if>
                            </div>
                            <div class="&authorLogo;">
                                <xsl:if test="/Document/ImageMetadata/n-metadata[@ttype='Napa_firm'] != ''">
                                    <xsl:variable name="firmImageSource">
                                        <xsl:choose>
                                            <xsl:when test="$DeliveryMode">
                                                <xsl:call-template name="createBlobLink">
                                                    <xsl:with-param name="guid" select="$firmGuid"/>
                                                    <xsl:with-param name="mimeType" select="'image/x-png'"/>
                                                    <xsl:with-param name="targetType" select="'Napa_firm'" />
                                                    <xsl:with-param name="forImgTag" select="'true'" />
                                                </xsl:call-template>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:call-template name="createBlobLink">
                                                    <xsl:with-param name="guid" select="$firmGuid"/>
                                                    <xsl:with-param name="mimeType" select="'image/x-png'"/>
                                                    <xsl:with-param name="targetType" select="'Napa_firm'" />
                                                    </xsl:call-template>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:variable>
                                    <xsl:choose>
                                        <xsl:when test="/Document/ImageMetadata/n-metadata[@ttype='Napa_firm']/firm.md.block/website.md.url != ''">
                                            <a class="&pauseSessionOnClickClass;" target="_blank">
                                                <xsl:attribute name="href">
                                                    <xsl:value-of select="/Document/ImageMetadata/n-metadata[@ttype='Napa_firm']/firm.md.block/website.md.url"/>
                                                </xsl:attribute>
                                                <img>
                                                    <xsl:attribute name="src">
                                                        <xsl:value-of select="$firmImageSource"/>
                                                    </xsl:attribute>
                                                    <xsl:attribute name="alt">
                                                        <xsl:value-of select="/Document/ImageMetadata/n-metadata[@ttype='Napa_firm']/firm.md.block/firm.md.name"/>
                                                        <xsl:text>&apos;s website</xsl:text>
                                                    </xsl:attribute>
                                                </img>
                                            </a>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <img>
                                                <xsl:attribute name="src">
                                                    <xsl:value-of select="$firmImageSource"/>
                                                </xsl:attribute>
                                            </img>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:if>
                            </div>
                            <ul>
                                <li class="&byLine;">
                                    by
                                    <xsl:choose>
                                        <xsl:when test="/Document/ImageMetadata/n-metadata[@ttype='Napa_author']/author.md.block/website.md.url != ''">
                                            <a target="_blank" class="&pauseSessionOnClickClass; &authorName;">
                                                <xsl:attribute name="href">
                                                    <xsl:value-of select="/Document/ImageMetadata/n-metadata[@ttype='Napa_author']/author.md.block/website.md.url"/>
                                                </xsl:attribute>
                                                <span id="co_authorName" class="&authorName;">
                                                    <xsl:value-of select="/Document/ImageMetadata/n-metadata[@ttype='Napa_author']/author.md.block/author.md.full.name"/>
                                                </span>
                                            </a>
                                            <xsl:if test="//footnote">
                                                <sup>
                                                    <xsl:attribute name="title">
                                                        <xsl:value-of select="//footnote/footnote.body/para"/>
                                                    </xsl:attribute>
                                                    <a id="co_top_acknowledgement_1" href="#co_acknowledgment_1">1</a>
                                                </sup>
                                            </xsl:if>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <span id="co_authorName" class="&authorName;">
                                                <xsl:value-of select="/Document/ImageMetadata/n-metadata[@ttype='Napa_author']/author.md.block/author.md.full.name"/>
                                            </span>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:if test="/Document/ImageMetadata/n-metadata[@ttype='Napa_author']/author.md.block/author.md.title != ''">
                                        <xsl:text><![CDATA[, ]]></xsl:text>
                                        <span id="co_authorTitle">
                                            <xsl:value-of select="/Document/ImageMetadata/n-metadata[@ttype='Napa_author']/author.md.block/author.md.title"/>
                                        </span>
                                    </xsl:if>
                                    <xsl:if test="/Document/ImageMetadata/n-metadata[@ttype='Napa_firm']/firm.md.block/firm.md.name != ''">
                                        <br/>
                                        <span id="co_firmName">
                                            <xsl:value-of select="/Document/ImageMetadata/n-metadata[@ttype='Napa_firm']/firm.md.block/firm.md.name"/>
                                        </span>
                                    </xsl:if>
                                </li>
                                <xsl:if test="/Document/ImageMetadata/n-metadata[@ttype='Napa_author']/author.md.block/contact.md.block/contact.md.phone != ''">
                                    <li>
                                        <i class="&phoneSmall;"></i>
                                        <span id="co_authorPhoneNumber">
                                            <xsl:value-of select="/Document/ImageMetadata/n-metadata[@ttype='Napa_author']/author.md.block/contact.md.block/contact.md.phone"/>
                                        </span>
                                    </li>
                                </xsl:if>
                            </ul>
                        </div>
                    </div>
                </div>

            </div>
        </div>        
    </xsl:template>

    <!-- render a segment 3 or more levels deep -->
    <xsl:template match="segment/segment//segment">
        <xsl:call-template name="RenderSegment">
            <xsl:with-param name="segmentLevel" select="'3'" />
        </xsl:call-template>
    </xsl:template>

    <!-- render a segment 2 levels deep -->
    <xsl:template match="segment/segment">
        <xsl:call-template name="RenderSegment">
            <xsl:with-param name="segmentLevel" select="'2'" />
        </xsl:call-template>
    </xsl:template>

    <!-- render a segment 1 level deep -->
    <xsl:template match="segment">
        <xsl:call-template name="RenderSegment">
            <xsl:with-param name="segmentLevel" select="'1'" />
        </xsl:call-template>
    </xsl:template>

    <!-- function for rendering a segment -->
    <xsl:template name="RenderSegment">
        <xsl:param name="segmentLevel" select="1" />
        
        <div class="&sectionClass;">
            <xsl:attribute name="segmentId"><xsl:value-of select="./@ID"/></xsl:attribute>
            <xsl:if test="body/head/headtext[text()='Overview']">
                <xsl:attribute name="id">co_articleOverview</xsl:attribute>
            </xsl:if>

            <xsl:call-template name="RenderLeveledHeader">
                <xsl:with-param name="segmentLevel">
                    <xsl:value-of select="$segmentLevel"/>
                </xsl:with-param>
                <xsl:with-param name="text">
                    <xsl:value-of select="body/head/headtext"/>
                </xsl:with-param>
                <xsl:with-param name="segmentId">
                    <xsl:value-of select="./@ID"/>
                </xsl:with-param>
            </xsl:call-template>
            
            <xsl:apply-templates select="body/*[not(self::head)]" />

            <!-- render the citation block-->
            <xsl:if test="body/issue.citation.block">
                <ul>
                    <xsl:for-each select="body/issue.citation.block/issue.citation">
                        <li>
                            <xsl:apply-templates select="." />
                        </li>
                    </xsl:for-each>
                </ul>
            </xsl:if>
            
            <!-- for overview render the updated static info -->
            <xsl:apply-templates select="segment" />
        </div>
    </xsl:template>

    <!-- override issue citation block -->
    <xsl:template match="segment/body/issue.citation.block" />

    <xsl:template match="segment.level.ref.block">
        <ul>
            <xsl:for-each select="segment.level.ref">
                <li>
                    <xsl:apply-templates select="." />
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>
    
    <!-- override para and paratext -->
    <xsl:template match="segment/body/para">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="segment/body/para/paratext">
        <p>
            <xsl:apply-templates />
        </p>
    </xsl:template>
    
    <xsl:template name="RenderLeveledHeader">
        <xsl:param name="segmentLevel" select="1" />
        <xsl:param name="text" />
        <xsl:param name="segmentId" />

        <!-- write out the anchor that is used to navigate to each section.  Chrome requires
             there to be content in the anchor for the navigation to work, thus the empty <ul> -->
        <a class="&napaHeaderAnchor;">
            <xsl:attribute name="name">a_<xsl:value-of select="$segmentId"/></xsl:attribute>
            <ul></ul>
        </a>
        <xsl:choose>
            <xsl:when test="$segmentLevel='1'">
                <h2>                    
                    <xsl:apply-templates select="body/head/headtext"/>
                </h2>
            </xsl:when>
            <xsl:when test="$segmentLevel='2'">
                <h3>
                    <xsl:apply-templates select="body/head/headtext"/>
                </h3>
            </xsl:when>
            <xsl:otherwise>
                <h4>
                    <xsl:apply-templates select="body/head/headtext"/>
                </h4>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="body/head/headtext">
        <xsl:apply-templates />
    </xsl:template>
    
    <!-- Renders a Napa TOC -->
    <xsl:template name="RenderTOC">
        <xsl:param name="document" />

        <h3 class="&genericBoxHeader;">
            <xsl:attribute name="name">toc_<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.uuid"/></xsl:attribute>
            <xsl:attribute name="title">Create Alert for this document</xsl:attribute>
            In this Article
            <a class="&alertIcon;" href="javascript:void(0);">Add Alert</a>
        </h3>
        <div class="&genericBoxContent;">
            <!-- render the toc item for each segment -->
            <xsl:if test="$document/segment">
                <ul>
                <xsl:for-each select="$document/segment">
                    <xsl:call-template name="RenderTOCSegment">
                        <xsl:with-param name="segment" select="." />
                    </xsl:call-template>
                </xsl:for-each>
                    <li name="toc_additionalResearch">
                        <div class="&tocItem;">
                            <a href="#a_additionalResearch">Additional Research</a>
                        </div>
                    </li>
                </ul>
            </xsl:if>
        </div>
    </xsl:template>

    <!-- Renders a Napa TOC Segment -->
    <xsl:template name="RenderTOCSegment">
        <xsl:param name="segment" />
            <li>
                <xsl:attribute name="name">toc_<xsl:value-of select="$segment/@ID"/></xsl:attribute>
                <xsl:choose>
                    <xsl:when test="$segment/body/head/headtext='Federal Aspects'">
                        <xsl:attribute name="id">co_articleToc_federalAspects</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$segment/body/head/headtext='State Aspects'">
                        <xsl:attribute name="id">co_articleToc_stateAspects</xsl:attribute>
                    </xsl:when>
                </xsl:choose>

                <xsl:call-template name="RenderTOCSegmentContent">
                    <xsl:with-param name="segment" select="$segment" />
                </xsl:call-template>
            </li>        
    </xsl:template>

    <xsl:template name="RenderTOCSegmentContent">
        <xsl:param name="segment" />

        <!-- render the segment title -->
        <div class="&tocItem;">
            <a>
                <xsl:attribute name="href">#a_<xsl:value-of select="$segment/@ID"/></xsl:attribute>
                <xsl:value-of select="$segment/body/head/headtext"/>
            </a>
            <a class="&alertIcon;" href="javascript:void(0);">Add Alert</a>
            <xsl:if test="($segment/body/head/headtext='Federal Aspects' or $segment/body/head/headtext='State Aspects') and $segment/segment">
                <div class="&pointer;"></div>
            </xsl:if>
        </div>

        <xsl:choose>
            <!-- Render the federal and state segments with special divs surrounding the <ul> -->
            <xsl:when test="$segment/body/head/headtext='Federal Aspects' or $segment/body/head/headtext='State Aspects'">
                <xsl:variable name="flyOutHeaderName" select="$segment/body/head/headtext" />
                <!-- if there is are subsegments, render those -->
                <xsl:if test="$segment/segment">
                    <div class="&flyOutContainer;">
                        <div class="&flyOut;">
                            <div class="&flyOutInner;">
                                    <div class="&flyOutHeader;">
                                        <h2><xsl:value-of select="$flyOutHeaderName"/></h2>
                                        <ul class="&inlineList;">
                                            <li>
                                                <a href="#">Monitor All</a>
                                            </li>
                                            <li>
                                                <a href="#">Monitor None</a>
                                            </li>
                                        </ul>
                                    </div>
                                    <ul class="&flyOutList;">
                                        <xsl:for-each select="$segment/segment">
                                            <xsl:call-template name="RenderTOCSegment">
                                                <xsl:with-param name="segment" select="." />
                                            </xsl:call-template>
                                        </xsl:for-each>
                                    </ul>
                            </div>
                        </div>
                    </div>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <!-- if there is are subsegments, render those -->
                <xsl:if test="$segment/segment">
                    <ul>
                        <xsl:for-each select="$segment/segment">
                            <xsl:call-template name="RenderTOCSegment">
                                <xsl:with-param name="segment" select="." />
                            </xsl:call-template>
                        </xsl:for-each>
                    </ul>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>       
    </xsl:template>
    
    <!-- Ignore the segment metadata. e.g. keywords, dates, et al. -->
    <xsl:template match="segment.metadata.block" />
    <xsl:template match="segment.prism.clipdate" />
    <xsl:template match="md.first.line.cite" />
    <xsl:template match="md.second.line.cite" />
    <xsl:template match="front.matter" />

    <xsl:template match="cite.query" priority="1">
        <xsl:choose>
            <xsl:when test="$DeliveryMode and $DeliveryFormat = 'Rtf'">
                <span>
                    <xsl:apply-templates />
                </span>
            </xsl:when>
            <xsl:when test="@w-ref-type='AA' and starts-with(@w-normalized-cite, 'PracInsights')">
                <xsl:variable name="citeQueryElement" select="."/>
                <xsl:variable name="linkContents">
                    <xsl:apply-templates select="$citeQueryElement/node()[not(self::starpage.anchor)]" />
                </xsl:variable>
                <xsl:variable name="transitionType" select="'&transitionTypeDocumentItem;'" />

                <xsl:variable name="fullLinkContents">
                    <xsl:choose>
                        <xsl:when test="string-length($SourceSerial) &gt; 0 and ($citeQueryElement/@w-serial-number = $SourceSerial or $citeQueryElement/@w-normalized-cite = $SourceSerial)">
                            <xsl:call-template name="markupSourceSerialSearchTerm">
                                <xsl:with-param name="linkContents" select="$linkContents"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="$linkContents"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:variable name="isDraggable">
                    <xsl:choose>
                        <xsl:when test="$citeQueryElement/@w-ref-type = 'KD' or $citeQueryElement/@w-ref-type = 'KW'">
                            <xsl:text>false</xsl:text>
                        </xsl:when>
                        <xsl:when test="not($AllowLinkDragAndDrop)">
                            <xsl:text>false</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>true</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:apply-templates select="$citeQueryElement/starpage.anchor"/>

                <xsl:variable name="sourceCite">
                    <xsl:call-template name="SpecialCharacterTranslator">
                        <xsl:with-param name="textToTranslate" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.first.line.cite[not(/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite)]" />
                    </xsl:call-template>
                </xsl:variable>

                <xsl:if test="string-length($fullLinkContents) &gt; 0">
                    <xsl:variable name="persistentUrl">
                        <xsl:call-template name="replace">
                            <xsl:with-param name="string" select="CiteQuery:GetCiteQueryLink($citeQueryElement, $Guid, '', $sourceCite, concat('originationContext=', $originationContextParamValue), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&transitionTypeParamName;=', $transitionType))"/>
                            <xsl:with-param name="pattern" select="'&amp;cite'" />
                            <xsl:with-param name="replacement" select="'&amp;normalizedCite'"/>
                        </xsl:call-template>                
                    </xsl:variable>
                    <xsl:choose>
                        <xsl:when test="string-length($persistentUrl) &gt; 0 and $DisplayLinksInDocument">
                            <a>
                                <xsl:attribute name="id">
                                    <xsl:text>&linkIdPrefix;</xsl:text>
                                    <xsl:choose>
                                        <xsl:when test="string-length($citeQueryElement/@ID) &gt; 0">
                                            <xsl:value-of select="$citeQueryElement/@ID"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="generate-id($citeQueryElement)"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:attribute>
                                <xsl:attribute name="class">
                                    <xsl:text>&linkClass;</xsl:text>
                                    <xsl:if test="$isDraggable = 'true'">
                                        <xsl:text><![CDATA[ ]]>&linkDraggableClass;</xsl:text>
                                    </xsl:if>
                                </xsl:attribute>
                                <xsl:attribute name="href">
                                    <xsl:copy-of select="$persistentUrl"/>
                                </xsl:attribute>
                                <xsl:copy-of select="$fullLinkContents"/>
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="$fullLinkContents"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>

                <!-- Add a space if the following sibling is a cite.query -->
                <xsl:if test="$citeQueryElement/following-sibling::node()[1]/self::cite.query">
                    <xsl:text><![CDATA[ ]]></xsl:text>
                </xsl:if>
            </xsl:when>
            <!-- Render the external links on the document which will be <cite.query> with a ref type of 'FW' -->
            <xsl:when test="@w-ref-type='FW'">
                <a class="&pauseSessionOnClickClass;" target="_blank">
                    <xsl:attribute name="href">
                        <xsl:if test="not(starts-with(@w-normalized-cite, 'http://')) and not(starts-with(@w-normalized-cite, 'https://'))">
                            <xsl:text>http://</xsl:text>
                        </xsl:if>
                        <xsl:value-of select="@w-normalized-cite"/>
                    </xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                </a>   
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="citeQuery">
                    <xsl:with-param name="originationContext" select="$originationContextParamValue"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- You have to explicitly have your content type include copyrights -->
    <xsl:template match="message.block/include.copyright" priority="1">
        <div class="&copyrightClass;">
            <xsl:apply-templates />
        </div>
    </xsl:template>

    <xsl:template match="cite.query" name="citeQueryUsingNormalizedCite">
    </xsl:template>
</xsl:stylesheet>
