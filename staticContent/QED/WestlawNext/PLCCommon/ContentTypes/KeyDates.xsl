<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="KnowHow.xsl"/>
  <xsl:include href="Date.xsl"/>

  <xsl:template match="prelim" priority="1">
    <div id="&coDocContentHeaderPrelim;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
      <xsl:if test="not($DeliveryMode)">
        <div id="&codocumentHeaderBorder;" class="&documentHeaderBorderClass;"></div>
      </xsl:if>

        <!--Document Title-->
        <div id="&coDocHeaderContainer;">

          <!-- Event date -->
          <xsl:call-template name="eventDate"/>
          
          <xsl:if test="title" >
            <h1 class="&titleClass;">
              <xsl:value-of select="string(title)"/>
            </h1>
          </xsl:if>
          <!-- Document Author -->
          <xsl:if test="author and author != ''">
            <div class="&coProductName;">
              &byText; <xsl:apply-templates select="author"/>
            </div>
          </xsl:if>
        </div>
    </div>

  </xsl:template>

  <xsl:template name="EventDetails">
   
    <div class="&paraMainClass;">
      <xsl:value-of select="//n-docbody/calendar.event/event.details/body"/>
    </div>
    <div class="&resourceLinksClass;">
      <xsl:variable name="resourceLink" select="//n-docbody/calendar.event/event.details/resource.links/resource.link"/>
      <xsl:if  test="count($resourceLink) &gt; 0">
        <h2 class="&headText;">
          <xsl:value-of select="'&relatedContent;'"/>
        </h2>
        <h3 class="&headText;">
          <xsl:value-of select="'&resourceLinksText;'"/>
        </h3>
        <ul>
          <xsl:for-each select="$resourceLink">
            <li>
              <!--Related content internal link -->
              <xsl:apply-templates select="title//cite.query"/>

              <!--Related content external link (open page in the new window). -->
              <a>
                <xsl:attribute name="target">
                  <xsl:text>&linkTargetNewWindow;</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="href">
                  <xsl:value-of select="@href"/>
                </xsl:attribute>
                <xsl:apply-templates select="title/web.address"/>
              </a>
            </li>
          </xsl:for-each>
        </ul>
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template name="eventType">

    <xsl:variable name="EventType">
      <b>
        <xsl:value-of select="'&eventTypeText;'"/>
      </b>
      <span>
        <xsl:value-of select="string(//n-metadata/plc.metadata.block/plcmd.eventTypes/plcmd.eventType)"/>
      </span>
    </xsl:variable>

    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&coDocumentType;'" />
      <xsl:with-param name="contents" select="$EventType" />
    </xsl:call-template>

  </xsl:template>


  <xsl:template name="eventDate">
    <div id="&eventDateId;" class="&eventDateClass;">
      <xsl:value-of select="string(//n-docbody/calendar.event/display.date)"/>
    </div>     
  </xsl:template>

  <xsl:template name="eventDateIcon">

    <xsl:if test="not($DeliveryMode)">
      <xsl:variable name="hasDateRange" select="//n-docbody/calendar.event/date.isrange"/>
      <xsl:variable name="hasNoDate" select="//n-docbody/calendar.event/date.iswithout"/>
      <xsl:variable name="startDate" select="//n-docbody/calendar.event/start.date"/>
      <xsl:variable name="startDateDay" select="number(substring($startDate, 7, 2))"/>

      <!-- We are just using this template to get the month name corresponding to its number -->
      <xsl:variable name="formattedDate">
            <xsl:call-template name="parseYearMonthDayDateFormat">
              <xsl:with-param name="date" select="$startDate" />
              <xsl:with-param name="displayDay" select="'false'" />
              <xsl:with-param name="displayTime" select="'false'" />
            </xsl:call-template>
      </xsl:variable>
    
      <xsl:variable name="startDateShortMonth" select="substring($formattedDate, 1, 3)"/>
      
      <div id="&eventDateIconId;" class="&eventDateIconClass;">

      <xsl:choose>
        <xsl:when test="$hasDateRange = 'Y' or $hasNoDate = 'Y'">
          <div id="&eventDateIconTextId;" class="&eventDateIconTextClass;">
            <xsl:value-of select="//n-docbody/calendar.event/display.date"/>
          </div>
        </xsl:when>
        <xsl:otherwise>
          <div id="&eventDateIconMonthId;" class="&eventDateIconMonthClass;">
            <xsl:value-of select="$startDateShortMonth"/>           
          </div>
          <div id="&eventDateIconDayId;" class="&eventDateIconDayClass;">
            <xsl:value-of select="$startDateDay"/> 
          </div>
        </xsl:otherwise>
      </xsl:choose>

      </div>
    </xsl:if>
  </xsl:template>  

  <xsl:template name="knowhowStatus">
    <!--We don't want to display status on event calendar-->
  </xsl:template>

  <xsl:template name="AlsoFound">
    <!--We don't want to display "also found in" on event calendar-->
  </xsl:template> 


  <xsl:template name="ShowCalendarEventContent">
    <xsl:call-template name="EventDetails"/>
  </xsl:template>

</xsl:stylesheet>