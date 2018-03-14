<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
  <xsl:include href="Table.xsl"/>  
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="tbl" priority="1">
    <xsl:if test=".//text()">
      <div>
        <xsl:if test="@id or @ID">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('&internalLinkIdPrefix;', @id | @ID)"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:attribute name="class">
          <xsl:value-of select="'&crswTable;'"/>
        </xsl:attribute>
        <xsl:apply-templates />
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template match="tbody/row/entry" priority="1">
    <xsl:param name="columnInfo" />
    <xsl:param name="colalign" />
    <xsl:param name="colposition" />
    <xsl:param name="colwidth" />
    <td>
      <xsl:choose>
        <!-- Check if we have just one cell in the row and this cell is empty -->
        <xsl:when test="(count(parent::entry) = 0) and (string-length(normalize-space(translate(self::*,'&#160;',' '))) = 0)">
          <xsl:variable name="spacechar">
            <xsl:text>&nbsp;</xsl:text>
          </xsl:variable>
          <xsl:call-template name="RenderTableCell">
            <xsl:with-param name="columnInfo" select="$columnInfo"/>
            <xsl:with-param name="colalign" select="$colalign" />
            <xsl:with-param name="colposition" select="$colposition" />
            <xsl:with-param name="colwidth" select="$colwidth" />
            <xsl:with-param name="contents" select="$spacechar"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="RenderTableCell">
            <xsl:with-param name="columnInfo" select="$columnInfo"/>
            <xsl:with-param name="colalign" select="$colalign" />
            <xsl:with-param name="colposition" select="$colposition" />
            <xsl:with-param name="colwidth" select="$colwidth" />
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </td>
  </xsl:template>

  <xsl:template name="RenderTableCell">
    <xsl:param name="columnInfo" />
    <xsl:param name="colalign" />
    <xsl:param name="colposition" />
    <xsl:param name="colwidth" />
    <xsl:param name="row" />
    <xsl:param name="class" />
    <xsl:param name="contents">
      <xsl:choose>
        <xsl:when test="leader">
          <xsl:call-template name="leaderContent">
            <xsl:with-param name="parent" select="." />
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:param>
    <xsl:attribute name="class">
      <xsl:if test="//leader">
        <xsl:text>&leaderTableCellClass;</xsl:text>
        <xsl:text><![CDATA[ ]]></xsl:text>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="@align = 'right'">
          <xsl:text>&alignHorizontalRightClass;</xsl:text>
        </xsl:when>
        <xsl:when test="@align = 'left'">
          <xsl:text>&alignHorizontalLeftClass;</xsl:text>
        </xsl:when>
        <xsl:when test="@align = 'center'">
          <xsl:text>&alignHorizontalCenterClass;</xsl:text>
        </xsl:when>
        <xsl:when test="$colalign = 'right'">
          <xsl:text>&alignHorizontalRightClass;</xsl:text>
        </xsl:when>
        <xsl:when test="$colalign = 'left'">
          <xsl:text>&alignHorizontalLeftClass;</xsl:text>
        </xsl:when>
        <xsl:when test="$colalign = 'center'">
          <xsl:text>&alignHorizontalCenterClass;</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>&alignHorizontalLeftClass;</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text><![CDATA[ ]]></xsl:text>
      <xsl:choose>
        <xsl:when test="@valign = 'bottom'">
          <xsl:text>&alignVerticalBottomClass;</xsl:text>
        </xsl:when>
        <xsl:when test="@valign = 'top'">
          <xsl:text>&alignVerticalTopClass;</xsl:text>
        </xsl:when>
        <xsl:when test="@valign">
          <xsl:value-of select="concat('vAlignError_', @valign)" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>&alignVerticalTopClass;</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text><![CDATA[ ]]></xsl:text>
      <xsl:if test="@rowsep = '1' or parent::row/@rowsep = '1' or ($row and $row/@rowsep = '1') or ancestor::table[1]/@rowsep = '1'">
        <xsl:text>&borderBottomClass;</xsl:text>
        <xsl:text><![CDATA[ ]]></xsl:text>
      </xsl:if>
      <xsl:if test="@colsep = '1'">
        <xsl:text>&borderRightClass;</xsl:text>
        <xsl:text><![CDATA[ ]]></xsl:text>
      </xsl:if>
      <xsl:value-of select="$class"/>
    </xsl:attribute>
    <xsl:variable name="colspan">
      <xsl:if test="@namest">
        <xsl:variable name="startCol">
          <xsl:call-template name="GetColNumber">
            <xsl:with-param name="columnInfo" select="$columnInfo" />
            <xsl:with-param name="text" select="@namest" />
          </xsl:call-template>
        </xsl:variable>
        <xsl:if test="number($startCol) = $colposition">
          <xsl:variable name="endCol">
            <xsl:call-template name="GetColNumber">
              <xsl:with-param name="columnInfo" select="$columnInfo" />
              <xsl:with-param name="text" select="@nameend" />
            </xsl:call-template>
          </xsl:variable>
          <xsl:for-each select="$columnInfo">
            <xsl:if test="number($endCol) = position()">
              <xsl:value-of select="position() - $colposition + 1"/>
            </xsl:if>
          </xsl:for-each>
        </xsl:if>
      </xsl:if>
    </xsl:variable>
    <xsl:if test="string-length($colspan) &gt; 0">
      <xsl:attribute name="colspan">
        <xsl:value-of select="$colspan"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="not(number($colspan) &gt; 1) and $colwidth and string-length($colwidth) &gt; 0">
      <xsl:attribute name="style">
        <xsl:value-of select="concat('width:', $colwidth)"/>
      </xsl:attribute>
    </xsl:if>

    <xsl:variable  select ="ancestor::tbl/@row-shade" name="alternateCheck"/>
    <xsl:choose>
      <xsl:when test ="$alternateCheck = 'alt'">
        <xsl:choose>
          <xsl:when test="normalize-space()">
            <xsl:copy-of select="$contents"/>
          </xsl:when>
          <xsl:otherwise>
            <br />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="$contents"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>
  

  <!-- Recursive function to traverse text and add non-breaking spaces.
       NOTE: This template removes any HTML tags. If you want to preserve the HTML tags, 
       use the parseCellContentsForDelivery template which is defined below.-->
  <xsl:template name="addNonBreakingSpacesForDelivery">
    <xsl:param name="str"/>
    <xsl:param name="maxLengthBeforeBreak"/>

    <xsl:choose>
      <xsl:when test="contains($str, ' ')">
        <!-- There is a space, we can traverse it -->
        <xsl:variable name="c1" select="substring-before($str,' ')"/>
        <xsl:variable name="c2" select="substring-after($str, ' ')"/>


        <xsl:call-template name="addNonBreakingSpacesForDelivery">
          <xsl:with-param name="str" select="$c1"/>
          <xsl:with-param name="maxLengthBeforeBreak" select="$maxLengthBeforeBreak"/>
        </xsl:call-template>
        <xsl:if test="string-length($c2)>0">
          <xsl:text><![CDATA[ ]]></xsl:text>
          <xsl:call-template name="addNonBreakingSpacesForDelivery">
            <xsl:with-param name="str" select="$c2"/>
            <xsl:with-param name="maxLengthBeforeBreak" select="$maxLengthBeforeBreak"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="string-length($str)>number($maxLengthBeforeBreak)">
            <xsl:call-template name="addNonBreakingSpaceToStrings">
              <xsl:with-param name="str" select="$str"/>
              <xsl:with-param name="maxLengthBeforeBreak" select="$maxLengthBeforeBreak"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <!-- There is no space, and string is less than max characters before break -->
            <xsl:value-of select="$str"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- A recursive function to add non breaking spaces for print -->
  <xsl:template name="addNonBreakingSpaceToStrings">
    <xsl:param name="str"/>
    <xsl:param name="maxLengthBeforeBreak"/>

    <xsl:if test="string-length($str) > 0">
      <xsl:variable name="c1" select="substring($str, 1, $maxLengthBeforeBreak)"/>
      <xsl:variable name="maxLengthPlusOne" select="number($maxLengthBeforeBreak) + number(1)"/>
      <xsl:variable name="c2" select="substring($str, number($maxLengthPlusOne))"/>

      <xsl:value-of select="$c1"/>
      <xsl:if test="$c2 != '' ">
        <xsl:text>&#8203;</xsl:text>
      </xsl:if>

      <xsl:call-template name="addNonBreakingSpaceToStrings">
        <xsl:with-param name="str" select="$c2"/>
        <xsl:with-param name="maxLengthBeforeBreak" select="$maxLengthBeforeBreak"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="parseCellContentsForDelivery">
    <xsl:choose>
      <!-- We have to keep [name()] here because .//node() alone also captures name-less nodes, and regular text (string) is a nameless node.
           Also, make sure the .//node() test is the first one, otherwise the .//text() test will pick up all the nodes by itself. -->
      <xsl:when test=".//node()[name()]">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test=".//text()">        
        <xsl:call-template name="addNonBreakingSpacesForDelivery">
          <xsl:with-param name="str" select="."/>
          <xsl:with-param name="maxLengthBeforeBreak" select="5"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
