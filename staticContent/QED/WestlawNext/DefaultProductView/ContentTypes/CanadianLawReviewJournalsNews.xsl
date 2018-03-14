<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="AppendixToc.xsl"/>
  <xsl:include href="FootnoteBlock.xsl"/>
  <xsl:include href="Title.xsl"/>
  <xsl:include href="CanadianUniversal.xsl"/>
  <xsl:include href="CanadianCites.xsl"/>
  <xsl:include href="CanadianFootnotes.xsl"/>
  <xsl:include href="CanadianDate.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="Document">

    <xsl:variable name="isLawReviwOrJournal">
      <xsl:choose>
        <xsl:when test="/document-data/collection = 'crsw_lrevcars' and //md.case.slug = 'LRJ'">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <div id="&documentClass;">
      <xsl:call-template name="AddDocumentClasses">
        <xsl:with-param name="contentType" select="'&crswLawReviewJournalsNewslettersClass;'"/>
      </xsl:call-template>
      
      <xsl:call-template name="StarPageMetadata" />

      <xsl:choose>
        <xsl:when test="n-docbody/comment" >
          <!--Document Prelim-->
          <div class="&documentHeadClass;">
            <div class="&headnotesClass; &centerClass;">
              <xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites"/>
              <xsl:apply-templates select="n-docbody/comment/doc_heading/toc_headings"/>
              <xsl:apply-templates select="n-docbody/comment/doc_heading/doc_date | n-docbody/comment/doc_heading/ul/date"/>
              <xsl:apply-templates select="n-docbody/comment/ul/ul/ul/p/date" mode="ShowDate"/>
              <xsl:apply-templates select="n-docbody/comment/doc_heading/doc_title"/>							
              <xsl:apply-templates select="n-docbody/comment/doc_heading/doc_authors"/>

              <!-- Render prelim copyright for journals and law reviews -->
              <xsl:if test="$isLawReviwOrJournal">
                <xsl:apply-templates select="n-docbody/comment/doc_heading/message.block.carswell"/>
              </xsl:if>
            </div>
          </div>

					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
					
          <!--Document Body-->
          <xsl:apply-templates select="n-docbody/comment/node()[not(self::doc_heading | self::p//sup/a[starts-with(@name, 'f')])]"/>

          <!--Document Footnotes-->
          <xsl:if test="not(n-docbody/comment/footnote.block)">
            <!--If there is no footnote.block element, then render the footnotes as usual-->
            <xsl:call-template name="RenderFootnoteSection" />
          </xsl:if>
        </xsl:when>
        <xsl:when test="n-docbody/docwrapper/classact" >
          <!-- Kim Orr -->
          <xsl:apply-templates select="n-docbody/docwrapper/classact/tocblock"/>
          <xsl:apply-templates select="n-docbody/docwrapper/classact/freeform"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="PublisherLogo" />
          <xsl:apply-templates select="n-docbody/doc/node()" />
        </xsl:otherwise>
      </xsl:choose>

      <xsl:call-template name="EndOfDocument" />
      <xsl:call-template name="PublisherLogo" />
    </div>
  </xsl:template>
	
	<xsl:template match="n-docbody/comment/doc_heading/doc_title">
						<xsl:call-template name="wrapWithDiv">
						<xsl:with-param name="class" select="'&titleClass;'"/>
						</xsl:call-template>
	</xsl:template>

  <xsl:template match="n-docbody/comment/ul/ul/ul/p/date | content.metadata.block" />

  <xsl:template match="n-docbody/comment/ul/ul/ul/p/date" mode="ShowDate">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&dateClass;'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="toc_headings">
    <xsl:for-each select="*">     
      <xsl:if test="name() = 'toc_heading_0'">
        <xsl:call-template name="wrapWithDiv">
          <xsl:with-param name="class" select="'&titleClass;'"/>
        </xsl:call-template>
      </xsl:if>
			<xsl:if test="name() = 'toc_heading_1'">
        <xsl:call-template name="wrapWithDiv">
          <xsl:with-param name="class" select="'&titleClass;'"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="doc_title">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&titleClass;'" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="doc_authors" priority="1">
    <xsl:variable name="authors">

      <xsl:variable name="authorsList" select="doc_author"/>
      <xsl:if test="string-length($authorsList) &gt; 0">
        <xsl:for-each select="$authorsList">
          <xsl:apply-templates select="."/>
          <xsl:if test="position() != last()">
            <xsl:text>,<![CDATA[ ]]></xsl:text>
          </xsl:if>
        </xsl:for-each>
      </xsl:if>
    </xsl:variable>

    <xsl:if test="string-length($authors) &gt; 0">
      <xsl:call-template name="wrapWithDiv">
        <xsl:with-param name="class" select="'&author;'"/>
        <xsl:with-param name="contents" select="$authors"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template match="paragr | section">
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="sup">
    <xsl:choose>
      <!-- Check if it is not a footnote -->
      <xsl:when test="not(./a)">
        <sup>
          <xsl:apply-templates />
        </sup>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="n-docbody/comment//title" priority="1">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template name="PublisherLogo">
    <xsl:choose>
      <xsl:when test="/Document/n-metadata/metadata.block/md.subjects/md.subject/md.pubtype.name">
        <xsl:call-template name="DisplayPublisherLogo" />
      </xsl:when>
      <xsl:when test="/Document/n-metadata/metadata.block/md.subjects/md.subject/md.view[last()-2] = '&PublisherAsp;'">
        <xsl:call-template name="DisplayPublisherLogo">
          <xsl:with-param name="PublisherType" select="'&PublisherAsp;'" />
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!--Need to catch the footnote.block here and resend it with the correct id for inline footnotes to work.-->
  <xsl:template match="footnote.block" priority="1">
    <xsl:call-template name="footnoteBlock">
      <xsl:with-param name="id" select="'&footnoteSectionId;'"/>
    </xsl:call-template>
  </xsl:template>

  <!-- Copyright -->
  <xsl:template match="message.block.carswell/include.copyright">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&copyrightClass;'"/>
    </xsl:call-template>
  </xsl:template>

  <!--*******************************-->
  <!--Criminal Journals Specific Code-->
  <!--*******************************-->

  <!--Ignore the title element in the footnote block-->
  <xsl:template match="footnote.block/title" priority="1"/>

  <xsl:template match="blkti">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&paraMainClass; &headtextClass; &crswTopMargin;'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="sup" priority="1">
    <xsl:choose>
      <!--No need to add the sup HTML tag for footnote.refereence - it will be added later-->
      <xsl:when test="./footnote.reference or ./a">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <sup>
          <xsl:apply-templates/>
        </sup>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>



  <!-- ***************************** -->
  <!-- Begin Kim Orr Specific Styles -->
  <!-- ***************************** -->

  <!-- Blue Title & Sub Header-->
  <xsl:template match="tocblock">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&crswBlueTitle; &crswPageBreakDivBorder;'"/>
      <xsl:with-param name="contents" select="substring-before(tocblockti,'—')"/>
    </xsl:call-template>

    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="contents">
        <xsl:value-of select="substring-after(tocblockti,'—')"/>
        <!-- XSL Fo won't put in a css divider, manually add one -->
        <xsl:text><![CDATA[ ]]>|<![CDATA[ ]]> </xsl:text>
        <xsl:call-template name="wrapWithSpan">
          <xsl:with-param name="class" select="'&titleClass;'"/>
          <xsl:with-param name="contents">
            <!-- parse off the Substring for the issue number -->
            <xsl:value-of select="substring-after(issueinfo/p[1],', ')"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <!-- Render the Tocblock table header -->
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&crswTwoColumnTOCHeader;'"/>
      <xsl:with-param name="id" select="'&crswTwoColumnTOCHeader;'"/>
      <xsl:with-param name="contents">
        <xsl:value-of select="issueinfo/p[2]"/>
      </xsl:with-param>
    </xsl:call-template>

    <!-- Render the Tocblock table body -->
    <xsl:call-template name="renderTwoColumnOverview"/>
  </xsl:template>

  <xsl:template name="renderTwoColumnOverview">
    <!-- Get the list of all sections -->
    <xsl:variable name="ToLeft" select="processing-instruction('break')/preceding-sibling::doctoc"/>
    <xsl:variable name="ToRight" select="processing-instruction('break')/following-sibling::doctoc"/>

    <!-- Table required for 2 column layout in delivered content works fine on web however we do it -->
    <div class="&crswTwoColumnTOCBody;">
      <table class="&crswTwoColumnTOCBlock;">
        <tbody>
          <tr>
            <xsl:choose>
              <!--  Before break is first column, after is the second column   -->
              <xsl:when test="child::processing-instruction('break')">
                <td class="&crswTwoColumnTOCBodyLeft; &alignVerticalTopClass;">
                  <xsl:call-template name="ApplySpaceForDelivery"/>
                  <xsl:apply-templates select="$ToLeft"/>
                </td>
                <td class="&crswTwoColumnTOCBodyRight; &alignVerticalTopClass;">
                  <xsl:call-template name="ApplySpaceForDelivery"/>
                  <xsl:apply-templates select="$ToRight"/>
                  <xsl:apply-templates select="toclink"/>
                </td>
              </xsl:when>

              <!-- Unless there is no break, if so image by itself goes in second column -->
              <xsl:otherwise>
                <td class="&crswTwoColumnTOCBodyLeft; &alignVerticalTopClass;">
                  <xsl:call-template name="ApplySpaceForDelivery"/>
                  <xsl:apply-templates select="doctoc"/>
                </td>
                <td class="&crswTwoColumnTOCBodyRight; &alignVerticalTopClass;">
                  <xsl:call-template name="ApplySpaceForDelivery"/>
                  <xsl:apply-templates select="toclink"/>
                </td>
              </xsl:otherwise>
            </xsl:choose>
          </tr>
        </tbody>
      </table>
    </div>
    <!-- Add page break at end of TOC -->
    <xsl:call-template name="classActionPageDivider"/>
  </xsl:template>

  <xsl:template match="doctoc">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&crswTwoColumnTOCBodySubGroup;'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="doctoc/title">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&titleClass;'"/>
      <xsl:with-param name="contents">
        <xsl:value-of select="b"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="tocp">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&crswBottomMargin;'"/>
      <xsl:with-param name="contents">
        <a href="#&internalLinkIdPrefix;{@tocidref}" class="&crswDisableSuperLink; &crswTwoColumnTOCBlock;">
          <xsl:value-of select="*"/>
        </a>
        <xsl:call-template name="ApplySpaceForDelivery"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="classact//block1">
    <xsl:apply-templates/>

    <xsl:if test="following-sibling::*[1][self::block1]">
      <xsl:call-template name="classActionPageDivider"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="classact//block1/title">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&titleClass; &crswKimOrrH3;'"/>
      <xsl:with-param name="contents">
        <xsl:value-of select="b/u"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="classact//block2/title">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&titleClass; &crswKimOrrH3;'"/>
      <xsl:with-param name="id" select="concat('&internalLinkIdPrefix;', @tocid)"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="classact//block3/title">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&titleClass; &crswKimOrrH3;'"/>
      <xsl:with-param name="contents">
        <xsl:value-of select="b"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="classact//block2">
    <xsl:apply-templates/>
    <a class="&underlineClass; &crswDisableSuperLink;" href="#&crswTwoColumnTOCHeader;">TOP</a>

    <xsl:if test="following-sibling::*[1][self::block2]">
      <xsl:call-template name="classActionCircleDividers"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="classact//block3">
    <xsl:call-template name="wrapWithDiv">
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="toclink">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&crswTwoColumnTOCBodySubGroup;'"/>
    </xsl:call-template>
  </xsl:template>

  <!-- Priority 1 required for document body paragraph elements to override platform -->
  <!-- Paragraph + paragraph matches do not flow through to delivered content, 
       adjusted to using margins after all paragraph elements. -->
  <xsl:template match="docwrapper/classact//p" priority="1">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&crswBottomMargin;'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="classActionCircleDividers">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&crswBottomMargin; &titleClass; &alignHorizontalCenterClass;'"/>
      <xsl:with-param name="contents">
        <!-- &#8226; is a Bullet -->
        <xsl:text>&#8226;&#8226;&#8226;</xsl:text>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="classActionPageDivider">
    <!-- Can't use calltemplate because apply-templates would get called twice.
         Can't use the apply templates from wrapWithDiv because other blocks inherit the border in delivery -->
    <div class="&crswPageBreakDivBorder;">
      <!-- Add space for download so it renders the block for delivery-->
      <xsl:call-template name="ApplySpaceForDelivery"/>
    </div>
  </xsl:template>

  <!--First and Last rows in tables contain a NL special character 
      that is not expected to be rendered.  Remove the rows-->
  <xsl:template match="docwrapper/classact//tbody/row[position()=1 or position()=last()]"/>

  <!-- Date object causes a div inside of a link, which breaks integration tests -->
  <xsl:template match="link/date">
    <xsl:value-of select="."/>
  </xsl:template>
  
  <!--Employement Spectrum Specific-->
  <!--Fix for issue 633349-->
  <xsl:template match="Document//p/list/n[following-sibling::listtext and not(substring(text(),string-length(text()),string-length(text()-1)) = ' ')]">
    <xsl:apply-templates/>
    <xsl:text><![CDATA[ ]]></xsl:text>
  </xsl:template>
  
</xsl:stylesheet>
