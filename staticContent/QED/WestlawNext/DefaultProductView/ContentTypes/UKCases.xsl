<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SimpleContentBlocks.xsl"/>
  <xsl:include href="Cites.xsl"/>
  <xsl:include href="Title.xsl"/>
  <xsl:include href="PreformattedText.xsl"/>
  <xsl:include href="Prelim.xsl"/>
  <xsl:include href="Copyright.xsl"/>
  <xsl:include href="InternationalFootnote.xsl"/>
  <xsl:include href="InternationalLogos.xsl"/>

  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:variable name="analysisDocGuid" select="Document/n-metadata/metadata.block/md.references/md.locatordoc/@href"/>
  <xsl:variable name="databaseIdentifier" select="normalize-space(/Document/n-metadata/metadata.block/md.identifiers/md.westlawids/md.wl.database.identifier)"/>

  <xsl:variable name="UKSessionReports" select="'UK-SESSION-RPTS'"/>
  <xsl:variable name="UKBLR" select="'UK-BLR-RPTS'"/>
  <xsl:variable name="UKClassLegal" select="'UK-CLR-RPTS'"/>
  <xsl:variable name="primarycite" select="Document/n-docbody/document/metadata.block/md.identifiers/md.cites/md.primarycite" />
  <xsl:variable name="RoyaltyCopyright" select="Document/n-metadata/metadata.block/md.royalty/md.copyright"/>
  <xsl:variable name="CopyrightMessage" select ="Document/n-docbody/copyright-message"/>

  <xsl:template match="Document">
    <div id="&documentClass;">
      <xsl:call-template name="AddDocumentClasses"/>
      <xsl:call-template name="StarPageMetadata" />
      <xsl:call-template name="DisplayInternationalPublisherLogo" />

      <xsl:apply-templates select="n-docbody/document/report"/>
      <div class="&alignHorizontalLeftClass;">
        <xsl:apply-templates select="$primarycite" />
      </div>

      <xsl:call-template name="EndOfDocument">
        <xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="DisplayInternationalPublisherLogo" />
    </div>
  </xsl:template>

  <!-- Match on main report element -->
  <xsl:template match="report">
    <xsl:apply-templates select="practice"/>
    <xsl:apply-templates select="prize"/>
    <xsl:apply-templates select="maintitle/partya"/>
    <xsl:apply-templates select="maintitle/othername"/>
    <xsl:apply-templates select="maintitle/shipname"/>
    <xsl:apply-templates select="maintitle/subnom"/>
    <xsl:apply-templates select="maintitle/joincase/partya"/>
    <xsl:apply-templates select="maintitle/titlecaseno"/>
    <div class="&centerClass;">
      <xsl:apply-templates select="image.block/image.link"/>
    </div>
    <xsl:apply-templates select="court"/>
    <xsl:apply-templates select="maintitle[@cccjudgdate]" />
    <xsl:apply-templates select="maintitle/title-paracite"/>
    <xsl:apply-templates select="maintitle/titlecite"/>
    <xsl:apply-templates select="judge" />
    <xsl:apply-templates select="date" />
    <xsl:comment>&EndOfDocumentHead;</xsl:comment>

    <div class="&paraMainClass;">&#160;</div>
    <xsl:if test="string-length($analysisDocGuid) &gt; 0">
      <div class="&headtextClass; &centerClass;">
        <a id="&linkIdPrefix;" class="&linkClass;">
          <xsl:attribute name="href">
            <xsl:call-template name="GetDocumentUrl">
              <xsl:with-param name ="documentGuid" select="$analysisDocGuid" />
            </xsl:call-template>
          </xsl:attribute>
          <span>
            <xsl:text>&ukAnalysis;</xsl:text>
          </span>
        </a>
      </div>
      <div class="&paraMainClass;">&#160;</div>
    </xsl:if>

    <xsl:apply-templates select="reference"/>
    <xsl:apply-templates select="intro"/>
    <xsl:apply-templates select="judgment/preceding-sibling::opinion" />
    <xsl:apply-templates select="judgment" />
    <xsl:apply-templates select="representation"/>
    <xsl:apply-templates select="costs"/>
    <xsl:apply-templates select="ruling"/>
    <xsl:apply-templates select="judgment/following-sibling::opinion" />
    <xsl:apply-templates select="quest"/>
    <xsl:apply-templates select="final"/>
    <xsl:apply-templates select="commentary"/>
    <xsl:apply-templates select="appendix"/>

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
        <xsl:with-param name="copyrightNode" select="$RoyaltyCopyright" />
      </xsl:call-template>
      <xsl:call-template name="copyrightBlock">
        <xsl:with-param name="copyrightNode" select="$CopyrightMessage" />
      </xsl:call-template>
    </div>
  </xsl:template>


  <!-- Display party names -->
  <xsl:template match="partya">
    <div class="&headtextClass; &centerClass;">
      <xsl:apply-templates />
      <xsl:if test="../partyb">
        <xsl:text> v. </xsl:text>
        <xsl:apply-templates select="../partyb"/>
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template match="othername|shipname">
    <xsl:if test=".!=''">
      <xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
        <xsl:with-param name="additionalClass" select="'&centerClass;'" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template match="subnom">
    <xsl:if test=".!=''">
      <xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
        <xsl:with-param name="additionalClass" select="'&centerClass;'" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!-- Display unformatted dates -->
  <xsl:template match="date">
    <xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
      <xsl:with-param name="additionalClass" select="'&centerClass;'" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="judge">
    <xsl:if test="string-length(normalize-space(.)) &gt; 0">
      <xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
        <xsl:with-param name="additionalClass" select="'&centerClass;'" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template match="maintitle[@cccjudgdate]">
    <div class="&centerClass;">
      <xsl:apply-templates select="@cccjudgdate"/>
    </div>
  </xsl:template>

  <xsl:template match="judge/name">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="judgment | paragrp | subgroup">
    <xsl:apply-templates  />
  </xsl:template>

  <xsl:template match="nsa-best">
    <xsl:apply-templates  />
  </xsl:template>

  <xsl:template match="header/title">
    <div class="&headtextClass;">
      <strong>
        <xsl:choose>
          <xsl:when test="ancestor::caselist/@number">
            <xsl:value-of select="ancestor::caselist/@number" />
            <xsl:text>&#160;</xsl:text>
          </xsl:when>
          <xsl:when test="(count(./parent::*/preceding-sibling::*) = 0) and parent::header/parent::*/@number">
            <xsl:value-of select="parent::header/parent::*/@number" />
            <xsl:text>&#160;</xsl:text>
          </xsl:when>
        </xsl:choose>
        <xsl:apply-templates />
      </strong>
    </div>
  </xsl:template>

  <xsl:template match="role">
    <xsl:text> </xsl:text>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="titlecaseno|court">
    <xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
      <xsl:with-param name="additionalClass" select="'&centerClass;'" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="titlecite|ptitle">
    <div class="&headtextClass;">
      <xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
        <xsl:with-param name="additionalClass" select="'&centerClass;'" />
      </xsl:call-template>
    </div>
  </xsl:template>

  <xsl:template match="title-paracite">
    <div class="&headtextClass; &centerClass;">
      <xsl:choose>
        <xsl:when test="contains(.,';')">
          <xsl:value-of select="substring-before(.,';')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template match="para-text | p">
    <xsl:choose>
      <!--If paragraph is empty do not display it-->
      <xsl:when test="count(child::*)=0 and .=''">&#160;</xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <!--If there are only list or table child elements ignore this paragraph and process children separately-->
          <xsl:when test="count(child::*)=1 and (child::list or child::table)">
            <xsl:apply-templates/>
          </xsl:when>
          <xsl:otherwise>
            <div class="&paraMainClass;">
              <!--Check for indent and align attributes-->
              <xsl:if test="@indent and @indent!='0'">
                <xsl:choose>
                  <xsl:when test="@align">
                    <!--The attribute for centred text is unhelpfully called 'centre' - rename it to 'center' for display use-->
                    <xsl:variable name="display-align">
                      <xsl:choose>
                        <xsl:when test="@align='centre'">&paraIndentHangingClass;</xsl:when>
                        <xsl:when test="@align='left'">
                          <xsl:if test="@indent='1'">&indentLeft1Class;</xsl:if>
                          <xsl:if test="@indent='2'">&indentLeft2Class;</xsl:if>
                          <xsl:if test="@indent='3'">&indentLeft3Class;</xsl:if>
                          <xsl:if test="@indent='3'">&indentLeft4Class;</xsl:if>
                        </xsl:when>
                        <xsl:when test="@align='right'">&paraIndentRightClass;</xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="&paraIndentFirstLineClass;"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:variable>
                    <xsl:attribute name="class">
                      <xsl:value-of select="$display-align"/>
                    </xsl:attribute>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:attribute name="class">
                      <xsl:value-of select="&indentLeft2Class;"/>
                    </xsl:attribute>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:if>
              <xsl:if test="parent::*/@number and (position()=1 or (position()=2 and preceding-sibling::header/number))">
                <xsl:value-of select="parent::*/@number"/>&#160;
              </xsl:if>
              <xsl:if test="name(preceding-sibling::*[position()=1])='number'">
                <xsl:value-of select="preceding-sibling::number"/>&#160;
              </xsl:if>
              <xsl:if test="name(preceding-sibling::*[position()=1])='header' and preceding-sibling::header/number">
                <xsl:value-of select="preceding-sibling::header/number"/>&#160;
              </xsl:if>
              <xsl:if test="parent::narr-paragraph/@type='held' and position()=1">
                <xsl:text>&ukHeld;</xsl:text>
                <!--the stylesheet used to output a comma after the held
								     label, however lots of fulltext have a comma in the text
								     and so 2 commas were displayed, so only output comma if 
								     there isn't one there-->
                <xsl:if test="substring(normalize-space(.), 1, 1) != ','">
                  <xsl:text>, </xsl:text>
                </xsl:if>
              </xsl:if>
              <xsl:apply-templates/>
            </div>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="catchphr | name">
    <div class="&paraMainClass;">
      <xsl:if test="@number">
        <xsl:value-of select="@number"/>
        <xsl:text>&nbsp;</xsl:text>
      </xsl:if>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="sub1">
    <xsl:if test="parent::narr-paragraph/@type='held' and position()=1">
      <div class="&paraMainClass;">
        <xsl:text>&ukHeld;</xsl:text>:
      </div>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:template>

  <!-- Common formatting templates -->
  <xsl:template match="emphasis">
    <xsl:choose>
      <xsl:when test="@type = 'strong'">
        <strong>
          <xsl:apply-templates />
        </strong>
      </xsl:when>
      <xsl:when test="@type = 'italic'">
        <em>
          <xsl:apply-templates />
        </em>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="caselist">
    <div class="&paraMainClass;">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="list">
    <xsl:if test="not(preceding-sibling::list)">
      <div>&#160;</div>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="$DeliveryMode">
        <div class="&paraMainClass;">
          <xsl:apply-templates select="item" mode="listDelivery" />
        </div>
      </xsl:when>
      <xsl:otherwise>
        <div class="&paraMainClass;">
          <xsl:variable name="listClasses">
            &bullListClass;
            <xsl:if test="count(ancestor::list) &gt; 0"> &indentLeft2Class;</xsl:if>
          </xsl:variable>
          <div class="&indentLeft2Class;">
            <ul>
              <xsl:attribute name="class">
                <xsl:value-of select="normalize-space($listClasses)"/>
              </xsl:attribute>
              <xsl:apply-templates select="item" mode="listDisplay" />
            </ul>
          </div>
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="item" mode="listDelivery">
    <div class="&indentLeft2Class;">
      <span class="&excludeFromAnnotationsClass;">&bull;</span>
      <xsl:apply-templates />
      <xsl:if test="following-sibling::node()[1][self::list]">
        <xsl:apply-templates select="following-sibling::node()[1]" />
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template match="item" mode="listDisplay">
    <li>
      <xsl:apply-templates />
      <xsl:if test="following-sibling::node()[1][self::list]">
        <xsl:apply-templates select="following-sibling::node()[1]" />
      </xsl:if>
    </li>
  </xsl:template>

  <xsl:template match="final | taxonomy.keywords">
    <div class="&paraMainClass;">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="taxonomy.keywords/keyword">
    <xsl:apply-templates/>
    <xsl:if test="position()!=last()">; </xsl:if>
  </xsl:template>

  <xsl:template match="title">
    <xsl:choose>
      <xsl:when test="ancestor::table">
        <caption>
          <xsl:if test="ancestor::narr-paragraph/@number and position()=1">
            <xsl:value-of select="ancestor::narr-paragraph/@number"/>
            <xsl:text> </xsl:text>
          </xsl:if>
          <xsl:apply-templates/>
        </caption>
      </xsl:when>
      <xsl:otherwise>
        <h3>
          <xsl:if test="ancestor::*/@number and position()=1">
            <xsl:value-of select="ancestor::*/@number"/>
            <xsl:text> </xsl:text>
          </xsl:if>
          <xsl:apply-templates/>
        </h3>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="narr-paragraph">
    <xsl:if test="not(preceding-sibling::narr-paragraph)">
      <div>&#160;</div>
    </xsl:if>
    <div class="&paraMainClass;">
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="latin">
    <em>
      <xsl:apply-templates/>
    </em>
  </xsl:template>

  <xsl:template match="longquotation">
    <blockquote>
      <xsl:choose>
        <xsl:when test="para-text or narr-paragraph/para-text or list or table">
          <xsl:apply-templates/>
        </xsl:when>
        <xsl:otherwise>
          <div class="&paraMainClass;">
            <xsl:apply-templates/>
          </div>
        </xsl:otherwise>
      </xsl:choose>
    </blockquote>
    <xsl:if test="parent::para-text">
      <div class="&paratextMainClass;">&#160;</div>
    </xsl:if>
  </xsl:template>

  <xsl:template match="quotation">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- Representation Section -->
  <xsl:template match="representation">
    <div class="&headtextClass;">
      <strong>
        <xsl:if test="@number">
          <xsl:value-of select="@number"/>&#160;
        </xsl:if>
        <xsl:text>&ukRepresentation;</xsl:text>
      </strong>
    </div>
    <xsl:choose>
      <xsl:when test="descendant::list">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <div class="&paraMainClass;">
          <xsl:apply-templates />
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Superscript (not in CommonInlineTemplates) -->
  <xsl:template match="superscript">
    <xsl:variable name="contents">
      <xsl:apply-templates />
    </xsl:variable>
    <xsl:if test="string-length($contents) &gt; 0">
      <sup>
        <xsl:copy-of select="$contents"/>
      </sup>
    </xsl:if>
  </xsl:template>

  <!-- Star Pages -->
  <xsl:template match="starpage.anchor" priority="5">
    <xsl:call-template name="displayStarPage">
      <xsl:with-param name="starPageText">
        <xsl:apply-templates />
      </xsl:with-param>
      <xsl:with-param name="numberOfStars" select="1" />
      <!--<xsl:with-param name="pageset" select="" />-->
    </xsl:call-template>
    <xsl:if test="$IncludeCopyWithRefLinks = true()">
      <xsl:call-template name="generateCopyWithReferenceLink" />
    </xsl:if>
  </xsl:template>

  <xsl:template match="number">
    <xsl:choose>
      <xsl:when test="parent::item">
        <xsl:apply-templates />
        <xsl:text>&#160;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Delete these elements -->
  <xsl:template match="line|number[not(parent::item)]"/>
  <!--<xsl:template match="line"/>-->


</xsl:stylesheet>
