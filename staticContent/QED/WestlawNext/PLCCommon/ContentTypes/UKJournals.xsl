<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="UKJournals.xsl" forceDefaultProduct="true"/>
  <xsl:include href="UKJournalsToc.xsl" />
  <xsl:include href="InternationalFootnote.xsl"/>
  <xsl:include href="PrevNextNavigation.xsl"/>

  <xsl:param name="PrevProvisionTitle" />
  <xsl:param name="NextProvisionTitle" />

  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:variable name ="contentType" select ="'&ukWestlawContent;'"/>
  <xsl:variable name="documentType">
    <xsl:value-of select ="$infoType"/>
  </xsl:variable>

  <xsl:variable name ="showLoading" select="true()"/>

  <!--Document structure-->
  <xsl:template name="BuildSpecificDocument">
    <xsl:call-template name="GeneralDocument" />
  </xsl:template>

  <!--document header-->
  <xsl:template name="BuildDocumentTypeAttribute">
    <xsl:attribute name ="data-documenttype">
      <xsl:value-of select="$documentType"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="BuildJournalHeaderContent">
    <xsl:param name="titleSelector"/>
    <h1 class="&title;">
      <xsl:apply-templates select="$titleSelector" />
    </h1>
    <div class="&coProductName;">
      <xsl:apply-templates select="//contributors" />
    </div>
  </xsl:template>

  <xsl:template name="BuildDocumentHeaderContent">
    <xsl:call-template name="BuildJournalHeaderContent">
      <xsl:with-param name="titleSelector" select="//data/title"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="BuildLoggedOutDocumentHeaderContent">
    <xsl:call-template name="BuildJournalHeaderContent">
      <xsl:with-param name="titleSelector" select="n-docbody/title"/>
    </xsl:call-template>
  </xsl:template>
  <!--end of document header-->

  <xsl:template match="author_footnote"/>

  <xsl:template match="data/title | n-docbody/title" priority="2">
    <xsl:if test="position() != 1">
      <xsl:text disable-output-escaping="yes"><![CDATA[ ]]></xsl:text>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="data/title/text() | n-docbody/title/text()">
    <xsl:call-template name="RightTrim">
      <xsl:with-param name="str" select="string(.)"/>
      <xsl:with-param name="chars" select="'.'"/>
    </xsl:call-template>
  </xsl:template>

  <!--document body-->
  
  <xsl:template name="BuildDocumentBody">
    <xsl:call-template name="StarPageMetadata" />
    <xsl:if test="not($DeliveryMode) and //md.infotype!='&fullTextType;' and DocumentExtension:HasFacGranted('JOURNAL PURCHASE FORM')">
      <xsl:call-template name="fullTextRequestButton"/>
    </xsl:if>
    <div class="&paraMainClass; &docTableDisplay;">
      <xsl:choose>
        <xsl:when test="//md.infotype='&fullTextType;'">
          <xsl:apply-templates select=".//contributors/author" mode="reviewer"/>
          <xsl:apply-templates select=".//journal-section/article" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select=".//data"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="AttachedFileForDocument"/>
    </div>
  </xsl:template>

  <!--end of doc body-->

  <xsl:template name="BuildFooter">
    <xsl:call-template name="internationalFootnote" />
  </xsl:template>

  <xsl:template name="BuildEndOfDocument">
    <xsl:call-template name="EmptyEndOfDocument">
      <xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="buildContentOfBody">
    <xsl:param name="nameTitle"/>
    <xsl:param name="isNewline" select="false()"/>
    <xsl:param name="additionalValue"/>

    <xsl:variable name="contents">
      <xsl:choose>
        <xsl:when test="string-length($additionalValue) &gt; 0">
          <xsl:value-of select="$additionalValue"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:if test="string-length($contents) &gt; 0">
      <div class="&docRowTableDisplay;">
        <div>
          <xsl:attribute name="class">
            <xsl:text>&docContentTitle;</xsl:text>
            <xsl:if test="$isNewline!='true'">
              <xsl:text><![CDATA[ ]]>&docCellTableDisplay;</xsl:text>
            </xsl:if>
          </xsl:attribute>
          <xsl:value-of select="$nameTitle"/>
        </div>
        <div>
          <xsl:copy-of select="$contents"/>
        </div>
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template match="list[@prefix-rules='unordered']">
    <div>
      <xsl:element name="ul">
        <xsl:attribute name="class">
          <xsl:text>&docUnorderedList;</xsl:text>
          <xsl:if test="$DeliveryMode">
            <xsl:text> &coAssetList;</xsl:text>
          </xsl:if>
        </xsl:attribute>
        <xsl:call-template name="listItemsDisplay"/>
      </xsl:element>
    </div>
  </xsl:template>

  <!--abstract and fulltext-->

  <xsl:template match="data">
    <xsl:apply-templates select=".//abstract"/>
    <xsl:if test="not(.//sec_entry_type and .//sec_entry_type='&publiacationReviewText;')">
      <xsl:apply-templates select=".//urls"/>
    </xsl:if>
    <xsl:apply-templates select=".//cases"/>
    <xsl:apply-templates select=".//legislation"/>
    <xsl:apply-templates select=".//companies"/>
    <xsl:apply-templates select=".//contributors/editor"/>
    <xsl:apply-templates select=".//contributors/reviewer"/>
    <xsl:apply-templates select=".//contributors/author" mode="reviewer"/>

    <xsl:choose>
      <xsl:when test="//md.infotype='&pubIndexType;'">
        <xsl:apply-templates select="$primarycite" />
        <xsl:apply-templates select=".//no_punctuation_ref" />
        <xsl:apply-templates select=".//isbn" />
        <xsl:apply-templates select=".//indexed_in" />
        <xsl:apply-templates select=".//publisher" />

        <xsl:if test="string-length(.//address1) &gt; 0">
          <div class="&docRowTableDisplay;">
            <div class="&docContentTitle; &docCellTableDisplay;">
              <xsl:text>&publishersHouseText;</xsl:text>
            </div>
            <div class='&docCellTableDisplay;'>
              <xsl:value-of select=".//address1"/>
              <xsl:apply-templates select=".//address2"/>
              <xsl:apply-templates select=".//address3"/>
              <xsl:apply-templates select=".//address4"/>
              <xsl:apply-templates select=".//postcode"/>
            </div>
          </div>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select=".//publisher"/>
        <xsl:apply-templates select=".//edition"/>
        <xsl:apply-templates select=".//volumes"/>
        <xsl:apply-templates select=".//series" />
        <xsl:apply-templates select=".//isbn" />
      </xsl:otherwise>
    </xsl:choose>

    <xsl:apply-templates select=".//fax"/>
    <xsl:apply-templates select=".//phone"/>
    <xsl:apply-templates select=".//frequency"/>
    <xsl:apply-templates select=".//price"/>
    <xsl:apply-templates select=".//format"/>
    <xsl:apply-templates select=".//pages"/>
  </xsl:template>

  <xsl:template match="abstract">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&ukAbstract;</xsl:with-param>
      <xsl:with-param name="isNewline" select="true()"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="data//urls">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&usefulLinksText;</xsl:with-param>
      <xsl:with-param name="isNewline" select="true()"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="cases">
    <xsl:variable name="cases">
      <xsl:choose>
        <xsl:when test="//md.infotype='&fullTextType;'">&ukCases;</xsl:when>
        <xsl:otherwise>&keyCasesCitedText;</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle" select="$cases"/>
      <xsl:with-param name="isNewline" select="true()"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="legislation">
    <xsl:variable name="legislation">
      <xsl:choose>
        <xsl:when test="//md.infotype='&fullTextType;'">&ukLegislation;</xsl:when>
        <xsl:otherwise>&keyLegislationsCitedText;</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle" select="$legislation"/>
      <xsl:with-param name="isNewline" select="true()"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="companies">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&organisationsCitedText;</xsl:with-param>
      <xsl:with-param name="isNewline" select="true()"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="case | leg_referred">
    <div>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="data//url">
    <xsl:variable name="content">
      <xsl:choose>
        <xsl:when test="starts-with(text(), '&linkHttp;')">
          <xsl:value-of select="."/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat('&linkHttp;', .)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <div>
      <xsl:call-template name="LinkOpensInNewTab">
        <xsl:with-param name="href" select="$content"/>
      </xsl:call-template>
    </div>
  </xsl:template>

  <xsl:template match="Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&referenceText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="no_punctuation_ref">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&noPunctuationRefText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="editor">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&editedByText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="reviewer">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&reviewedByText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="publisher">
    <xsl:variable name="title">
      <xsl:choose>
        <xsl:when test="//md.infotype='&pubIndexType;'">&publishersText;</xsl:when>
        <xsl:otherwise>&ukPublisher;</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle" select="$title"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="edition">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&editionText;</xsl:with-param>
      <xsl:with-param name="additionalValue">
        <xsl:apply-templates/>
        <xsl:text><![CDATA[ ]]></xsl:text>
        <xsl:value-of select="//data/publication_date/@year"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="volumes">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&noOfVolumesText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="series">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&seriesText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="isbn">
    <xsl:variable name="title">
      <xsl:choose>
        <xsl:when test="starts-with(text(), '&IssnText;')">&IssnIsbnText;</xsl:when>
        <xsl:otherwise>&IsbnText;</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle" select="$title"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="indexed_in">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&dbIndexedInText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="*[starts-with(name(), 'address')] | postcode">
    <xsl:variable name="content" select="./text()"/>
    <xsl:if test="string-length($content) &gt; 0">
      <xsl:value-of select="concat(', ', $content)"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="fax">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&publishersFaxNoText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="phone">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&publishersTelephoneNoText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="frequency">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&frequencyText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="price">
    <xsl:variable name="title">
      <xsl:choose>
        <xsl:when test="//md.infotype='&pubIndexType;'">&subscriptionCostText;</xsl:when>
        <xsl:otherwise>&ukPrice;</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle" select="$title"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="format">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&formatText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="pages">
    <xsl:call-template name="buildContentOfBody">
      <xsl:with-param name="nameTitle">&noOfPagesText;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="title[bold]">
    <a>
      <xsl:attribute name="id">
        <xsl:call-template name="anchor"/>
      </xsl:attribute>
    </a>
    <h2 class="&headtextClass; &docHeadText;">
      <xsl:apply-templates select="./starpage.anchor"/>
      <xsl:apply-templates select="./bold"/>
    </h2>
  </xsl:template>

  <xsl:template match="title[ital]">
    <h3 class="&headtextClass;">
      <i>
        <xsl:apply-templates select="./starpage.anchor"/>
        <xsl:value-of select="./ital"/>
      </i>
    </h3>
  </xsl:template>

  <xsl:template match="title[not(ital) and not(bold)]">
    <h3 class="&headtextClass;">
      <xsl:apply-templates />
    </h3>
  </xsl:template>

  <xsl:template match="contributors">
    <xsl:for-each select="author[not (@type = 'reviewer' or starts-with(text(), '&reviewedByTextForAuthor;'))]">
      <xsl:if test="string-length(.) &gt; 0">
        <xsl:if test="position() > 1">
          <xsl:choose>
            <xsl:when test="position() != last()">
              <xsl:text>, </xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text> and </xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:apply-templates/>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="author | caseref | legis-cite | item">
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="author" mode="reviewer">
    <xsl:if test="@type = 'reviewer' or starts-with(., '&reviewedByTextForAuthor;')">
      <xsl:call-template name="BuildMetaField">
        <xsl:with-param name="fieldClass" select="'&docReviewedByTextForAuthor;'"/>
        <xsl:with-param name="fieldCaption" select="'&reviewedByText;'"/>
        <xsl:with-param name="fieldContent">
          <xsl:call-template name="replace">
            <xsl:with-param name="string" select="." />
            <xsl:with-param name="pattern" select="'&reviewedByTextForAuthor;'" />
            <xsl:with-param name="replacement" select="''"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template match="level1">
    <xsl:if test="string-length(.) &gt; 0">
      <xsl:choose>
        <xsl:when test=".//title/bold">
          <div class="&docDivision;">
            <xsl:apply-templates select="*[not(name()='image.block')]"/>
          </div>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[not(name()='image.block')]"/>
        </xsl:otherwise>
      </xsl:choose>

    </xsl:if>
  </xsl:template>

  <xsl:template match="graphic">
    <xsl:apply-templates select="following-sibling::image.block[1]"/>
    <span class="&docFigureDescription;">
      <xsl:apply-templates select="caption"/>
    </span>
  </xsl:template>

  <xsl:template match="image.block" priority="1">
    <div class="&paraMainClass; &docImageResearch;">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="starpage.anchor" priority="5">
    <xsl:call-template name="displayStarPages">
      <xsl:with-param name="starPageText">
        <xsl:value-of select="preceding-sibling::starpage[1]"/>
      </xsl:with-param>
      <xsl:with-param name="pageset" select="$displayableCiteId" />
      <xsl:with-param name="pageNumber">
        <xsl:value-of select="translate(., translate(., '0123456789', ''), '')"/>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:if test="$IncludeCopyWithRefLinks = true()">
      <xsl:call-template name="generateCopyWithReferenceLink" />
    </xsl:if>
  </xsl:template>

  <xsl:template name="displayStarPages">
    <xsl:param name="starPageText" />
    <xsl:param name="pageset" />
    <xsl:param name="pageNumber" />

    <xsl:variable name="displayableStarPageText">
      <xsl:choose>
        <xsl:when test="string-length($starPageText) &gt; 0">
          <xsl:copy-of select="$starPageText"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:copy-of select="$pageNumber"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="starPageMetadataItemHiddenInput">
      <xsl:call-template name="StarPageMetadataItem">
        <xsl:with-param name="pageset" select="$pageset" />
        <xsl:with-param name="pageNumber" select="$pageNumber" />
      </xsl:call-template>
    </xsl:variable>

    <xsl:text><![CDATA[ ]]></xsl:text>
    <span>
      <xsl:attribute name="class">
        <xsl:text>&starPageClass;</xsl:text>
      </xsl:attribute>
      <xsl:copy-of select="$starPageMetadataItemHiddenInput"/>
      <xsl:text>*</xsl:text>
      <xsl:value-of select="$displayableStarPageText"/>
    </span>
    <xsl:text><![CDATA[ ]]></xsl:text>
  </xsl:template>

  <xsl:template name="StarPageMetadataItem">
    <xsl:param name="pageset" />
    <xsl:param name="pageNumber" />

    <xsl:variable name="jsonObject">
      <xsl:text>{ "&pagesetJsonPropertyName;": "</xsl:text>
      <xsl:value-of select="$pageset" />
      <xsl:text>", "&pageNumberJsonPropertyName;": "</xsl:text>
      <xsl:value-of select="$pageNumber" />
      <xsl:text>" }</xsl:text>
    </xsl:variable>

    <xsl:variable name="starPage">
      <xsl:text>*</xsl:text>
      <xsl:value-of select="$pageNumber" />
    </xsl:variable>

    <input type="hidden" class="&starPageMetadataItemClass;" value="{$jsonObject}" alt="&metadataAltText;" data-star-page="{$starPage}" />
  </xsl:template>

  <xsl:template name="AttachedFileForDocument">
    <xsl:variable name="guid" select="//document/metadata.block/md.references/md.print.rendition.id"/>

    <xsl:if test="string-length($guid) &gt; 0">
      <div class="&standardDocAttachment; &hideState;">
        <xsl:call-template name="createDocumentBlobLink">
          <xsl:with-param name="guid" select="$guid"/>
          <xsl:with-param name="targetType" select="'&inlineParagraph;'"/>
          <xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
          <xsl:with-param name="contents" select="'&viewPdfOfEntireIssue;'"/>
        </xsl:call-template>
      </div>
    </xsl:if>
  </xsl:template>

  <!--metadata-->

  <xsl:template name="BuildMetaInfoColumnContent">
    <xsl:if test="//metadata.block/md.infotype != '&pubIndexType;'">
      <div class="&metaBlockBorderBottom;">
        <xsl:call-template name="BuildMetaField">
          <xsl:with-param name="fieldClass" select="'&metaArticleType;'" />
          <xsl:with-param name="fieldContent">
            <xsl:choose>
              <xsl:when test="string-length(//data/sec_entry_type) &gt; 0">
                <xsl:value-of select="//data/sec_entry_type"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>&journalArticleText;</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:apply-templates select="//journal_indexed" />
        <xsl:if test="not(//data/journal_indexed)">
          <xsl:apply-templates select="//journal_title"/>
        </xsl:if>

        <xsl:apply-templates select="//journal_article"/>
        <xsl:if test="not(//data/journal_article)">
          <xsl:apply-templates select="//citation"/>
        </xsl:if>
      </div>

      <xsl:if test="//main_subject | //subjects | //keywords">
        <div class="&metaBlockBorderBottom;">
          <xsl:apply-templates select="//main_subject"/>
          <xsl:apply-templates select="//subjects"/>
          <xsl:apply-templates select="//keywords"/>
        </div>
      </xsl:if>
    </xsl:if>
  </xsl:template>

  <xsl:template match="journal_indexed">
    <xsl:call-template name="BuildMetaField">
      <xsl:with-param name="fieldClass" select="'&metaJournalIndexed;'" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="journal_title">
    <xsl:call-template name="BuildMetaField">
      <xsl:with-param name="fieldClass" select="'&metaJournalTitle;'" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="journal_article">
    <xsl:call-template name="BuildMetaField">
      <xsl:with-param name="fieldClass" select="'&metaJournalArticle;'" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="citation">
    <xsl:call-template name="BuildMetaField">
      <xsl:with-param name="fieldClass" select="'&metaCitation;'" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="main_subject">
    <xsl:call-template name="BuildMetaField">
      <xsl:with-param name="fieldClass" select="'&metaMainSubject;'" />
      <xsl:with-param name="fieldCaption" select="'&ukSubject;'"/>
      <xsl:with-param name="fieldContent">
        <xsl:apply-templates/>
        <xsl:text>.</xsl:text>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="subjects">
    <xsl:call-template name="BuildMetaField">
      <xsl:with-param name="fieldClass" select="'&metaSubjects;'" />
      <xsl:with-param name="fieldCaption" select="'&otherRelatedSubjectsText;'"/>
      <xsl:with-param name="fieldContent">
        <xsl:for-each select="subject">
          <xsl:apply-templates/>
          <xsl:if test="position()!=last()">
            <xsl:text>. </xsl:text>
          </xsl:if>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="keywords">
    <xsl:call-template name="BuildMetaField">
      <xsl:with-param name="fieldClass" select="'&metaKeywords;'" />
      <xsl:with-param name="fieldCaption" select="'&ukKeywords;'"/>
      <xsl:with-param name="fieldContent">
        <xsl:for-each select="keyword">
          <xsl:apply-templates/>
          <xsl:if test="position()!=last()">
            <xsl:text>; </xsl:text>
          </xsl:if>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="fullTextRequestButton">
    <xsl:if test="(((//md.fulltext/@href='') or (//md.fulltext[not(@href)]) ) and (//journal_indexed/@cla_exclusion='N'))">
      <div>
        <input id="&requestFullTextButton;" type="button" value="&requestFullTextButtonText;" class="&primaryButtonClass; &floatRight;" >
          <xsl:attribute name="data-articleTitle">
            <xsl:value-of select="//title"/>
          </xsl:attribute>
          <xsl:attribute name="data-citation">
            <xsl:value-of select="//citation"/>
          </xsl:attribute>
        </input>
      </div>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>