<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!--

Copyright Antenna House, Inc. (http://www.antennahouse.com) 2001, 2002.

Since this stylesheet is originally developed by Antenna House to be used with XSL Formatter, it may not be compatible with another XSL-FO processors.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, provided that the above copyright notice(s) and this permission notice appear in all copies of the Software and that both the above copyright notice(s) and this permission notice appear in supporting documentation.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR HOLDERS INCLUDED IN THIS NOTICE BE LIABLE FOR ANY CLAIM, OR ANY SPECIAL INDIRECT OR CONSEQUENTIAL DAMAGES, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:html="http://www.w3.org/1999/xhtml">

	<xsl:output method="xml"
				version="1.0"
				encoding="UTF-8"
				indent="no"/>


	<!--======================================================================
      Parameters
  =======================================================================-->

	<!-- Amar added these for form types.  -->
	<xsl:param name="formType"/>
	<xsl:param name="bookmark"/>
	<!-- 
  AP 4/20/2011 B-04829 
  Parameter for maximum columns allowed in a table before width is removed 
  -->
	<xsl:param name="overflow-column-count"/>

	<!--
  DSHA 05/17/2011 
  Parameter to override the default image width when none is specified 
   -->
	<xsl:param name="imageWidthDefaultOverride"/>

	<!-- page size -->
	<xsl:param name="page-width">auto</xsl:param>
	<xsl:param name="page-height">auto</xsl:param>
	<!--
  <xsl:param name="page-margin-top">0.5in</xsl:param>
  <xsl:param name="page-margin-bottom">0.5in</xsl:param>
-->

	<!--
DSHA 05/05/2011
Use these variables to convert from upper to lower in XLST 1.0
convert to lower case
 select="translate('My Text',$upper,$lower)"/>
 convert to upper case       
 select="translate('My Text',$lower,$upper)"/>
 -->

	<xsl:variable name="lower">
		abcdefghijklmnopqrstuvwxyz
	</xsl:variable>
	<xsl:variable name="upper">
		ABCDEFGHIJKLMNOPQRSTUVWXYZ
	</xsl:variable>


	<!-- Amar added these to increase page size for general RTF conversion, keeping unchanged for insider form types.  -->
	<xsl:param name="page-margin-top">
		<xsl:choose>
			<xsl:when test="$formType = 'exists'">
				<xsl:text>0.5in</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>0.25in</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:param>

	<xsl:param name="page-margin-bottom">
		<xsl:choose>
			<xsl:when test="$formType = 'exists'">
				<xsl:text>0.5in</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>0.25in</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:param>
	<!--<xsl:param name="page-margin-left">0.5in</xsl:param>
  -->
	<!--<xsl:param name="page-margin-right">0.5in</xsl:param>-->


	<xsl:variable name="page-margin-left">
		<xsl:choose>
			<xsl:when test="$formType = 'exists'">
				<xsl:text>0.1in</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>0.3in</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:variable name="page-margin-right">
		<xsl:choose>
			<xsl:when test="$formType = 'exists'">
				<xsl:text>0.1in</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>0.3in</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- page header and footer -->
	<!--<xsl:param name="page-header-margin">0.5in</xsl:param>
  <xsl:param name="page-footer-margin">0.5in</xsl:param>
  
   -->
	<!-- Amar added these to increase page size for general RTF conversion, keeping unchanged for insider form types.  -->
	<xsl:param name="page-header-margin">
		<xsl:choose>
			<xsl:when test="$formType = 'exists'">
				<xsl:text>0.5in</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>0.25in</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:param>

	<xsl:param name="page-footer-margin">
		<xsl:choose>
			<xsl:when test="$formType = 'exists'">
				<xsl:text>0.5in</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>0.25in</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:param>

	<xsl:param name="title-print-in-header">false</xsl:param>
	<xsl:param name="page-number-print-in-footer">false</xsl:param>

	<!-- multi column -->
	<xsl:param name="column-count">1</xsl:param>
	<xsl:param name="column-gap">12pt</xsl:param>

	<!-- writing-mode: lr-tb | rl-tb | tb-rl -->
	<xsl:param name="writing-mode">lr-tb</xsl:param>

	<!-- text-align: justify | start -->
	<xsl:param name="text-align">start</xsl:param>

	<!-- hyphenate: true | false -->
	<xsl:param name="hyphenate">false</xsl:param>


	<!-- Following are the default page width and height value in inches -->
	<!--<xsl:variable name="default-page-width" select="'8.5in'"/>
   <xsl:variable name="default-page-height" select="'11in'"/>
   
   -->

	<!-- Amar added these for form types.  -->
	<xsl:variable name="default-page-width">
		<xsl:choose>
			<xsl:when test="$formType = 'exists'">
				<xsl:text>11.69in</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>8.5in</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:variable name="default-page-height">
		<xsl:choose>
			<xsl:when test="$formType = 'exists'">
				<xsl:text>9in</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>11in</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:variable name="ptToInchesValue" select="1 div 72"/>
	<xsl:variable name="actualPageWidthCovered" select="normalize-space(substring-before($default-page-width,'in')) - (2 * normalize-space(substring-before($page-margin-left,'in')))"/>

	<!--
   Default image width when none is specified.
   This is done because some images will not fit on portrait page
   see two filings for reference story B-05999
   0001157523-10-004346
   0001140361-10-042928 
 -->
	<xsl:variable name="imageWidthDefault">
		<xsl:choose>
			<xsl:when test="string-length($imageWidthDefaultOverride) > 0">
				<xsl:value-of select="$imageWidthDefaultOverride"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>75%</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>



	<!--======================================================================
      Attribute Sets
  =======================================================================-->

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Root
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="root">
		<xsl:attribute name="writing-mode">
			<xsl:value-of select="$writing-mode"/>
		</xsl:attribute>
		<xsl:attribute name="hyphenate">
			<xsl:value-of select="$hyphenate"/>
		</xsl:attribute>
		<xsl:attribute name="text-align">
			<xsl:value-of select="$text-align"/>
		</xsl:attribute>
		<!-- specified on fo:root to change the properties' initial values -->
	</xsl:attribute-set>

	<xsl:attribute-set name="page">
		<!-- Changed the default page width and height......Can be sent programaticaaly
   Lets see whats reauired...Kamakhya Das -->
		<xsl:attribute name="page-width">
			<xsl:value-of select="$default-page-width"/>
		</xsl:attribute>
		<xsl:attribute name="page-height">
			<xsl:value-of select="$default-page-height"/>
		</xsl:attribute>
		<!-- Amar added these for form types.  -->
		<xsl:attribute name="margin-bottom">
			<xsl:choose>
				<xsl:when test="$formType = 'exists'">
					<xsl:text>0.5in</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>0.25in</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>

		<xsl:attribute name="margin-left">
			<xsl:choose>
				<xsl:when test="$formType = 'exists'">
					<xsl:text>0.3in</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>0.0in</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>

		<xsl:attribute name="margin-right">
			<xsl:choose>
				<xsl:when test="$formType = 'exists'">
					<xsl:text>0.3in</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<!-- Changed this value because of defect D-01634- Kamakhya Das -->
					<xsl:text>0.0in</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>

		<xsl:attribute name="margin-top">
			<xsl:choose>
				<xsl:when test="$formType = 'exists'">
					<xsl:text>0.5in</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>0.25in</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
		<!-- specified on fo:simple-page-master -->
	</xsl:attribute-set>

	<xsl:attribute-set name="body">
		<!-- specified on fo:flow's only child fo:block -->
	</xsl:attribute-set>

	<xsl:attribute-set name="page-header">
		<!-- specified on (page-header)fo:static-content's only child fo:block -->
		<xsl:attribute name="font-size">small</xsl:attribute>
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="page-footer">
		<!-- specified on (page-footer)fo:static-content's only child fo:block -->
		<xsl:attribute name="font-size">small</xsl:attribute>
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Block-level
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="h1">
		<xsl:attribute name="font-size">2em</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="space-before">0.67em</xsl:attribute>
		<xsl:attribute name="space-after">0.67em</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="h2">
		<xsl:attribute name="font-size">1.5em</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="space-before">0.83em</xsl:attribute>
		<xsl:attribute name="space-after">0.83em</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="h3">
		<xsl:attribute name="font-size">1.17em</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="space-before">1em</xsl:attribute>
		<xsl:attribute name="space-after">1em</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="h4">
		<xsl:attribute name="font-size">1em</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="space-before">1.17em</xsl:attribute>
		<xsl:attribute name="space-after">1.17em</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="h5">
		<xsl:attribute name="font-size">0.83em</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="space-before">1.33em</xsl:attribute>
		<xsl:attribute name="space-after">1.33em</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="h6">
		<xsl:attribute name="font-size">0.67em</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="space-before">1.67em</xsl:attribute>
		<xsl:attribute name="space-after">1.67em</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="p">
		<!-- AP 5/23/2011 B-06270 Changed to match PDF 0000034088-11-000016 -->
		<xsl:attribute name="space-before">0em</xsl:attribute>
		<xsl:attribute name="space-after">1em</xsl:attribute>
		<!-- e.g.,
    <xsl:attribute name="text-indent">1em</xsl:attribute>
    -->
	</xsl:attribute-set>

	<xsl:attribute-set name="p-initial" use-attribute-sets="p">
		<!-- initial paragraph, preceded by h1..6 or div -->
		<!-- e.g.,
    <xsl:attribute name="text-indent">0em</xsl:attribute>
    -->
	</xsl:attribute-set>

	<xsl:attribute-set name="p-initial-first" use-attribute-sets="p-initial">
		<!-- initial paragraph, first child of div, body or td -->
	</xsl:attribute-set>

	<xsl:attribute-set name="blockquote">
		<xsl:attribute name="start-indent">inherited-property-value(start-indent) + 24pt</xsl:attribute>
		<xsl:attribute name="end-indent">inherited-property-value(end-indent) + 24pt</xsl:attribute>
		<xsl:attribute name="space-before">1em</xsl:attribute>
		<xsl:attribute name="space-after">1em</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="pre">
		<xsl:attribute name="font-size">0.83em</xsl:attribute>
		<xsl:attribute name="font-family">Courier</xsl:attribute>
		<xsl:attribute name="white-space">pre</xsl:attribute>
		<xsl:attribute name="space-before">1em</xsl:attribute>
		<xsl:attribute name="white-space-collapse">false</xsl:attribute>
		<xsl:attribute name="space-after">1em</xsl:attribute>
		<xsl:attribute name="wrap-option">no-wrap</xsl:attribute>
	</xsl:attribute-set>



	<xsl:attribute-set name="address">
		<xsl:attribute name="font-style">italic</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="hr">
		<xsl:attribute name="border">1px inset</xsl:attribute>
		<xsl:attribute name="space-before">0.67em</xsl:attribute>
		<xsl:attribute name="space-after">0.67em</xsl:attribute>
	</xsl:attribute-set>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       List
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="ul">
		<xsl:attribute name="space-before">1em</xsl:attribute>
		<xsl:attribute name="space-after">1em</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="ul-nested">
		<xsl:attribute name="space-before">0pt</xsl:attribute>
		<xsl:attribute name="space-after">0pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="ol">
		<xsl:attribute name="space-before">1em</xsl:attribute>
		<xsl:attribute name="space-after">1em</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="ol-nested">
		<xsl:attribute name="space-before">0pt</xsl:attribute>
		<xsl:attribute name="space-after">0pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="ul-li">
		<!-- for (unordered)fo:list-item -->
		<xsl:attribute name="relative-align">baseline</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="ol-li">
		<!-- for (ordered)fo:list-item -->
		<xsl:attribute name="relative-align">baseline</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="dl">
		<xsl:attribute name="space-before">1em</xsl:attribute>
		<xsl:attribute name="space-after">1em</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="dt">
		<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="dd">
		<xsl:attribute name="start-indent">inherited-property-value(start-indent) + 24pt</xsl:attribute>
	</xsl:attribute-set>

	<!-- list-item-label format for each nesting level -->

	<xsl:param name="ul-label-1">&#x2022;</xsl:param>
	<xsl:attribute-set name="ul-label-1">
		<xsl:attribute name="font">1em serif</xsl:attribute>
	</xsl:attribute-set>

	<xsl:param name="ul-label-2">o</xsl:param>
	<xsl:attribute-set name="ul-label-2">
		<!-- 
    AP 5/17/2011 B-05841
    Increase bullet font size so that instead of a solid
    dot it looks like an "o"
    
    001157523-10-004346
    -->
		<xsl:attribute name="font">1em monospace</xsl:attribute>
		<xsl:attribute name="baseline-shift">0.25em</xsl:attribute>
	</xsl:attribute-set>

	<xsl:param name="ul-label-3">-</xsl:param>
	<xsl:attribute-set name="ul-label-3">
		<xsl:attribute name="font">bold 0.9em Times New Roman</xsl:attribute>
		<xsl:attribute name="baseline-shift">0.05em</xsl:attribute>
	</xsl:attribute-set>

	<xsl:param name="ol-label-1">1.</xsl:param>
	<xsl:attribute-set name="ol-label-1"/>

	<xsl:param name="ol-label-2">a.</xsl:param>
	<xsl:attribute-set name="ol-label-2"/>

	<xsl:param name="ol-label-3">i.</xsl:param>
	<xsl:attribute-set name="ol-label-3"/>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Table
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="inside-table">
		<!-- prevent unwanted inheritance -->
		<xsl:attribute name="start-indent">0pt</xsl:attribute>
		<xsl:attribute name="end-indent">0pt</xsl:attribute>
		<xsl:attribute name="text-indent">0pt</xsl:attribute>
		<xsl:attribute name="last-line-end-indent">0pt</xsl:attribute>
		<xsl:attribute name="text-align">start</xsl:attribute>
		<xsl:attribute name="text-align-last">relative</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="table-and-caption" >
		<!-- horizontal alignment of table itself
    <xsl:attribute name="text-align">center</xsl:attribute>
    -->
		<!-- vertical alignment in table-cell -->
		<!-- 
    AP 5/5/2011 B-05825
    Disabled vertical alignment to fix bullets being vertically
    centered on a multiline text block
    
    0001144204-11-012733
    -->
		<!--  <xsl:attribute name="display-align">center</xsl:attribute> -->
	</xsl:attribute-set>

	<xsl:attribute-set name="table">
		<xsl:attribute name="border-collapse">separate</xsl:attribute>
		<xsl:attribute name="border-spacing">2px</xsl:attribute>
		<xsl:attribute name="border">1px</xsl:attribute>
		<!--
    <xsl:attribute name="border-style">outset</xsl:attribute>
    -->
	</xsl:attribute-set>

	<xsl:attribute-set name="table-caption" use-attribute-sets="inside-table">
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="table-column">
	</xsl:attribute-set>

	<xsl:attribute-set name="thead" use-attribute-sets="inside-table">
	</xsl:attribute-set>

	<xsl:attribute-set name="tfoot" use-attribute-sets="inside-table">
	</xsl:attribute-set>

	<xsl:attribute-set name="tbody" use-attribute-sets="inside-table">
	</xsl:attribute-set>

	<xsl:attribute-set name="tr">
	</xsl:attribute-set>

	<xsl:attribute-set name="th">
		<xsl:attribute name="font-weight">bolder</xsl:attribute>
		<xsl:attribute name="text-align">center</xsl:attribute>
		<xsl:attribute name="border">1px</xsl:attribute>
		<!--
    <xsl:attribute name="border-style">inset</xsl:attribute>
    -->
		<xsl:attribute name="padding">1px</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="td">
		<xsl:attribute name="border">1px</xsl:attribute>
		<!--
    <xsl:attribute name="border-style">inset</xsl:attribute>
    -->
		<xsl:attribute name="padding">1px</xsl:attribute>
	</xsl:attribute-set>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Inline-level
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="b">
		<xsl:attribute name="font-weight">bolder</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="strong">
		<xsl:attribute name="font-weight">bolder</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="strong-em">
		<xsl:attribute name="font-weight">bolder</xsl:attribute>
		<xsl:attribute name="font-style">italic</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="i">
		<xsl:attribute name="font-style">italic</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="cite">
		<xsl:attribute name="font-style">italic</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="em">
		<xsl:attribute name="font-style">italic</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="var">
		<xsl:attribute name="font-style">italic</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="dfn">
		<xsl:attribute name="font-style">italic</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="tt">
		<xsl:attribute name="font-family">monospace</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="code">
		<xsl:attribute name="font-family">monospace</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="kbd">
		<xsl:attribute name="font-family">monospace</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="samp">
		<xsl:attribute name="font-family">monospace</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="big">
		<xsl:attribute name="font-size">larger</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="small">
		<xsl:attribute name="font-size">smaller</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="sub">
		<xsl:attribute name="baseline-shift">sub</xsl:attribute>
		<xsl:attribute name="font-size">smaller</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="sup">
		<xsl:attribute name="baseline-shift">super</xsl:attribute>
		<xsl:attribute name="font-size">smaller</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="s">
		<xsl:attribute name="text-decoration">line-through</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="strike">
		<xsl:attribute name="text-decoration">line-through</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="del">
		<xsl:attribute name="text-decoration">line-through</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="u">
		<xsl:attribute name="text-decoration">underline</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="ins">
		<xsl:attribute name="text-decoration">underline</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="abbr">
		<!-- e.g.,
    <xsl:attribute name="font-variant">small-caps</xsl:attribute>
    <xsl:attribute name="letter-spacing">0.1em</xsl:attribute>
    -->
	</xsl:attribute-set>

	<xsl:attribute-set name="acronym">
		<!-- e.g.,
    <xsl:attribute name="font-variant">small-caps</xsl:attribute>
    <xsl:attribute name="letter-spacing">0.1em</xsl:attribute>
    -->
	</xsl:attribute-set>

	<xsl:attribute-set name="q"/>
	<xsl:attribute-set name="q-nested"/>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Image
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="img">
	</xsl:attribute-set>

	<xsl:attribute-set name="img-link">
		<xsl:attribute name="border">2px solid</xsl:attribute>
	</xsl:attribute-set>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Link
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="a-link">
		<xsl:attribute name="text-decoration">underline</xsl:attribute>
		<xsl:attribute name="color">blue</xsl:attribute>
	</xsl:attribute-set>


	<!--======================================================================
      Templates
  =======================================================================-->

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Root
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template match="html:html">
		<fo:root xsl:use-attribute-sets="root">
			<xsl:call-template name="process-common-attributes"/>
			<xsl:call-template name="make-layout-master-set"/>
			<xsl:call-template name="createBookMark"/>
			<xsl:apply-templates/>
		</fo:root>
	</xsl:template>

	<xsl:template name="make-layout-master-set">
		<!--<fo:layout-master-set>
			<fo:simple-page-master master-name="all-pages"
								   xsl:use-attribute-sets="page">
				<fo:region-body margin-top="{$page-margin-top}"
								margin-right="{$page-margin-right}"
								margin-bottom="{$page-margin-bottom}"
								margin-left="{$page-margin-left}"
								column-count="{$column-count}"
								column-gap="{$column-gap}"/>
				<xsl:choose>
					<xsl:when test="$writing-mode = 'tb-rl'">
						<fo:region-before extent="{$page-margin-right}"
										  precedence="true"/>
						<fo:region-after  extent="{$page-margin-left}"
										  precedence="true"/>
						<fo:region-start  region-name="page-header"
										  extent="{$page-margin-top}"
										  writing-mode="lr-tb"
										  display-align="before"/>
						<fo:region-end    region-name="page-footer"
										  extent="{$page-margin-bottom}"
										  writing-mode="lr-tb"
										  display-align="after"/>
					</xsl:when>
					<xsl:when test="$writing-mode = 'rl-tb'">
						<fo:region-before region-name="page-header"
										  extent="{$page-margin-top}"
										  display-align="before"/>
						<fo:region-after  region-name="page-footer"
										  extent="{$page-margin-bottom}"
										  display-align="after"/>
						<fo:region-start  extent="{$page-margin-right}"/>
						<fo:region-end    extent="{$page-margin-left}"/>
					</xsl:when>
					<xsl:otherwise>
						-->
		<!-- $writing-mode = 'lr-tb' -->
		<!--
						<fo:region-before region-name="page-header"
										  extent="{$page-margin-top}"
										  display-align="before"/>
						<fo:region-after  region-name="page-footer"
										  extent="{$page-margin-bottom}"
										  display-align="after"/>
						<fo:region-start  extent="{$page-margin-left}"/>
						<fo:region-end    extent="{$page-margin-bottom}"/>
					</xsl:otherwise>
				</xsl:choose>
			</fo:simple-page-master>
		</fo:layout-master-set>-->
	</xsl:template>

	<xsl:template match="html:head | html:script"/>

	<xsl:template match="html:body">
		<fo:page-sequence master-reference="Portrait">
			<!--<fo:page-sequence master-reference="all-pages">-->
			<fo:title>
				<xsl:value-of select="/html:html/html:head/html:title"/>
			</fo:title>
			<!--<fo:static-content flow-name="page-header">
				<fo:block space-before.conditionality="retain"
						  space-before="{$page-header-margin}"
						  xsl:use-attribute-sets="page-header">
					<xsl:if test="$title-print-in-header = 'true'">
						<xsl:value-of select="/html:html/html:head/html:title"/>
					</xsl:if>
				</fo:block>
			</fo:static-content>-->
			<!--<fo:static-content flow-name="page-footer">
				<fo:block space-after.conditionality="retain"
						  space-after="{$page-footer-margin}"
						  xsl:use-attribute-sets="page-footer">
					<xsl:if test="$page-number-print-in-footer = 'true'">
						<xsl:text>- </xsl:text>
						<fo:page-number/>
						<xsl:text> -</xsl:text>
					</xsl:if>
				</fo:block>
			</fo:static-content>-->
			<fo:flow flow-name="xsl-region-body">
				<fo:block xsl:use-attribute-sets="body" linefeed-treatment="preserve">
					<xsl:call-template name="process-common-attributes"/>
					<xsl:apply-templates/>
				</fo:block>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   process common attributes and children
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template name="process-common-attributes-and-children">
		<xsl:call-template name="process-common-attributes"/>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template name="process-common-attributes">
		<xsl:attribute name="role">
			<xsl:value-of select="concat('html:', local-name())"/>
		</xsl:attribute>

		<xsl:choose>
			<xsl:when test="@xml:lang">
				<xsl:attribute name="xml:lang">
					<xsl:value-of select="@xml:lang"/>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="@lang">
				<xsl:attribute name="xml:lang">
					<xsl:value-of select="@lang"/>
				</xsl:attribute>
			</xsl:when>
		</xsl:choose>

		<xsl:choose>
			<xsl:when test="@id">
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="self::html:a/@name">
				<xsl:attribute name="id">
					<xsl:value-of select="@name"/>
				</xsl:attribute>
			</xsl:when>
		</xsl:choose>

		<!-- 
	AP 5/24/2011 B-06227
	Reduced font-size to match PDF stylesheet.
	0001157523-09-008087
	0001157523-10-000921
	0001157523-10-003229
	-->
		<xsl:if test="@size or @SIZE">
			<xsl:attribute name="font-size">
				<xsl:choose>
					<xsl:when test="@size = '-1'">
						<xsl:value-of select="'10pt'"/>
					</xsl:when>
					<xsl:when test="@size = '1'">
						<xsl:value-of select="'7pt'"/>
					</xsl:when>
					<xsl:when test="@size = '2'">
						<xsl:value-of select="'8.5pt'"/>
					</xsl:when>
					<xsl:when test="@size = '3'">
						<xsl:value-of select="'10pt'"/>
					</xsl:when>
					<xsl:when test="@size = '4'">
						<xsl:value-of select="'12pt'"/>
					</xsl:when>
					<xsl:when test="@size = '5'">
						<xsl:value-of select="'14.5pt'"/>
					</xsl:when>
					<xsl:when test="@size = '6'">
						<xsl:value-of select="'18pt'"/>
					</xsl:when>
					<xsl:when test="@size = '7'">
						<xsl:value-of select="'20pt'"/>
					</xsl:when>
					<xsl:when test="@size = '8'">
						<xsl:value-of select="'22pt'"/>
					</xsl:when>
				</xsl:choose>
			</xsl:attribute>
		</xsl:if>

		<!-- Added by Kamakhya Das........-->
		<xsl:if test="@bgcolor">
			<xsl:attribute name="background-color">
				<xsl:value-of select="@bgcolor"/>
			</xsl:attribute>
		</xsl:if>

		<!-- DSHA 04/25/2011 Added backGround-color and other variations for 0000950123-10-074181  -->
		<xsl:if test="@background-color">
			<xsl:attribute name="background-color">
				<xsl:value-of select="@background-color"/>
			</xsl:attribute>
		</xsl:if>

		<xsl:if test="@BACKGROUND-COLOR">
			<xsl:attribute name="background-color">
				<xsl:value-of select="@BACKGROUND-COLOR"/>
			</xsl:attribute>
		</xsl:if>

		<!-- DSHA 04/25/2011 Added backGround-color and other variations for 0000950123-10-074181  -->
		<xsl:if test="@backGround-color">
			<xsl:attribute name="background-color">
				<xsl:value-of select="@backGround-color"/>
			</xsl:attribute>
		</xsl:if>



		<xsl:if test="@align">
			<xsl:choose>
				<xsl:when test="self::html:caption">
				</xsl:when>
				<xsl:when test="self::html:img or self::html:object">
					<xsl:if test="@align = 'bottom' or @align = 'middle' or @align = 'top'">
						<xsl:attribute name="vertical-align">
							<xsl:value-of select="@align"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="process-cell-align">
						<xsl:with-param name="align" select="@align"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="@valign">
			<xsl:call-template name="process-cell-valign">
				<xsl:with-param name="valign" select="@valign"/>
			</xsl:call-template>
		</xsl:if>

		<xsl:if test="@style">
			<xsl:call-template name="process-style">
				<!-- DSHA 05/05/2011 Fixes all cases and mix case issues -->
				<xsl:with-param name="style" select="translate(@style,$upper,$lower)"/>
			</xsl:call-template>
		</xsl:if>

		<!-- Amar Modifying to exclude 'Symbol' IM2365787 -->
		<xsl:if test="@face">
			<xsl:choose>
				<xsl:when test="contains(@face,',')">
					<xsl:if test="not(contains(substring-before(@face,','), 'Symbol'))">
						<xsl:attribute name="font-family">
							<xsl:value-of select="substring-before(@face,',')"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="not(contains(@face,'Symbol'))">
						<xsl:attribute name="font-family">
							<xsl:value-of select="@face"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>

	</xsl:template>

	<!-- Added by Amar Das to activate font-weight to bold excluding the symbols such as bullets.-->
	<xsl:template match="html:font[not(contains(@face,'Symbol') or contains(@FACE,'Symbol'))]">
		<fo:inline>
			<!-- Tim S. - Without fo:inline .NET blows up, it looks like all these rules are the same now. -->
			<xsl:call-template name="process-common-attributes"/>
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:font[parent::html:font and not((contains(@face,'Symbol') or contains(@FACE,'Symbol')))]">
		<fo:inline>
			<xsl:call-template name="process-common-attributes"/>
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:font[parent::html:p]">
		<fo:inline>
			<xsl:call-template name="process-common-attributes"/>
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>




	<!-- 
Added by David Shapiro for Tracker#142387 to handle fonts inside TD cells
Prior to this if td cell had a bgcolor and font tag inside td would not be processed
 #IM2475486- See filing 0001206774-10-002604 table contents page Condensed Consolidated Balance... 
and other shaded cells 
 -->
	<xsl:template match="html:font[parent::html:td]">
		<fo:inline>
			<xsl:call-template name="process-common-attributes"/>
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>


	<!--  Added by David Shapiro for Tracker#142387 to handle fonts in pre tags 
 prior to change the font tag in body of pre was ignored unless it directly followed pre tag
 Sample filing 0001157523-10-000921 -->
	<xsl:template match="html:font[parent::html:pre]">
		<fo:inline>
			<xsl:call-template name="process-common-attributes"/>
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>


	<!-- Amar added 'or contains(@style,'wingdings 2')' on next line here -->
	<xsl:template match="html:font[contains(@style,'WINGDINGS') or contains(@style,'wingdings 2') or contains(@style,'Wingdings') or contains(@style,'Wingdings 2') or contains(@face,'WINGDINGS') or contains(@face,'wingdings') or contains(@face,'Wingdings 2') or contains(@face,'Wingdings') or contains(@FACE,'Wingdings') or contains(@FACE,'WINGDINGS')]" priority="2">
		<fo:inline>
			<xsl:call-template name="process-common-attributes"/>
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>


	<!--DSHA 4/25/2011 To process font tag properly see 0001206774-11-000057 Page labeled 6-8, 16-18, 25, 39, 42-46, 48-51 - paragraphs are all bold instead of just first name at beginning of each paragraph. paragraphs are all italicized instead of just the beginning of each paragraph
Page 30 - 33 - Financial table formatting -->
	<xsl:template match="html:font[parent::html:div]">
		<fo:inline>
			<xsl:call-template name="process-common-attributes"/>
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>


	<xsl:template name="process-style">
		<xsl:param name="style"/>
		<!-- e.g., style="text-align: center; color: red"
         converted to text-align="center" color="red" -->
		<xsl:variable name="name" select="normalize-space(substring-before($style, ':'))"/>

		<xsl:if test="$name">
			<xsl:variable name="value-and-rest"
						  select="normalize-space(substring-after($style, ':'))"/>
			<!-- 
	    AP 6/1/2011
	    normalize values to lowercase so its easier to do compares against
	  	-->
			<xsl:variable name="value">
				<xsl:choose>
					<xsl:when test="contains($value-and-rest, ';')">
						<xsl:value-of select="translate(normalize-space(substring-before(
                                  $value-and-rest, ';')),$upper,$lower)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="translate($value-and-rest,$upper,$lower)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$name = 'width' and (self::html:col or self::html:colgroup)">
					<xsl:attribute name="column-width">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<!-- *********************************************************************************-->
				<!-- Following are the attributes that Apache FOP doesn't want to included in the document
           Inclusion of these attributes would cause org.apache.fop.fo.ValidationException -->
				<xsl:when test="$name = 'display'"> </xsl:when>
				<xsl:when test="$name = 'text-autospace'"></xsl:when>
				<xsl:when test="$name = 'word-wrap'"></xsl:when>
				<xsl:when test="$name = 'position'"></xsl:when>
				<xsl:when test="$name = 'vertical-align'"></xsl:when>
				<xsl:when test="$name = 'list-style'"></xsl:when>
				<!-- ********************************** By Kamakhya Das ******************************-->


				<!-- Added by Amar Das c084205 for the same above reason  start -->
				<xsl:when test="$name = 'text-justify'"></xsl:when>
				<xsl:when test="$name = 'margin-center'"></xsl:when>
				<xsl:when test="$name = 'punctuation-wrap'"></xsl:when>
				<xsl:when test="$name = 'text-justify-trim'"></xsl:when>
				<xsl:when test="$name = 'layout-grid-mode'"></xsl:when>
				<xsl:when test="$name = 'Font-family'"></xsl:when>
				<xsl:when test="$name = 'font-Size'"></xsl:when>
				<xsl:when test="$name = 'align-type'"></xsl:when>
				<xsl:when test="$name = 'float'"></xsl:when>
				<!-- 
		    AP 5/5/2011 B-05825
		    Disabled to fix width not being pulled from style
		    
		    0001144204-11-012733
		   -->
				<!-- <xsl:when test="$name = 'width'"></xsl:when>  -->
				<!-- Added by Amar Das c084205 for the same above reason  end -->

				<!-- Added by Kamakhya Das.. Prism doesn't recognize the style
        values in Upper case hence changed them to lower case. -->
				<xsl:when test="$name = 'font-size' or $name = 'font-family'">
					<xsl:choose>
						<xsl:when test="$name = 'font-size'">
							<xsl:attribute name="font-size">
								<xsl:choose>
									<!-- Amar: Fonts reduced everywhere to match with LE as much as possible D-02058, 2059, 2060 to 2067 -->
									<xsl:when test="$value = '24pt'">
										<xsl:text>20.5pt</xsl:text>
									</xsl:when>
									<xsl:when test="$value = '18pt'">
										<xsl:text>17.5pt</xsl:text>
									</xsl:when>
									<xsl:when test="$value = '14pt'">
										<xsl:text>12pt</xsl:text>
									</xsl:when>
									<xsl:when test="$value = '12pt'">
										<xsl:text>10pt</xsl:text>
									</xsl:when>
									<xsl:when test="$value = '10pt'">
										<xsl:text>8.5pt</xsl:text>
									</xsl:when>
									<xsl:when test="$value = '9pt'">
										<xsl:text>8.5pt</xsl:text>
									</xsl:when>
									<xsl:when test="$value = '8pt'">
										<xsl:text>7pt</xsl:text>
									</xsl:when>
									<xsl:when test="$value = '7pt'">
										<xsl:text>6pt</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$value"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="font-family">
								<!-- Amar added for wingdings which appear this way: style="DISPLAY: inline; FONT-FAMILY: wingdings 2, serif"> -->
								<xsl:choose>
									<xsl:when test="contains($value,'wingdings 2')">
										<xsl:value-of select="normalize-space(substring-before($value,','))"/>
									</xsl:when>
									<xsl:otherwise>
										<!-- Amar to avoid ArrayIndex exception when FONT-FAMILY doesn't have a value -->
										<xsl:choose>
											<xsl:when test="$value = ''">
												<!-- Assign a default value -->
												<xsl:text>serif</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$value"/>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
								<!-- Amar added end here-->
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>

				<!-- Amar : Fixing defect D-01725 -->
				<xsl:when test="$name = 'text-decoration'">
					<xsl:attribute name="text-decoration">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'font-weight'">
					<xsl:attribute name="font-weight">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'border-right'">
					<xsl:attribute name="border-right">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'height'">
					<xsl:attribute name="height">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'border-top'">
					<xsl:attribute name="border-top">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'border-left'">
					<xsl:attribute name="border-left">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'border-bottom'">
					<xsl:attribute name="border-bottom">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'line-height'">
					<xsl:attribute name="line-height">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'letter-spacing'">
					<xsl:attribute name="letter-spacing">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'color'">
					<xsl:attribute name="color">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>


				<!-- DSHA 04/25/2011 Added backGround-color and other variations for 0000950123-10-074181  -->
				<xsl:when test="$name = 'background' or $name = 'background-color'">
					<xsl:attribute name="background-color">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>



				<!-- Amar changed here for handling -(negative)pixels causing blank pages. -->
				<xsl:when test="$name = 'text-indent'">
					<xsl:attribute name="text-indent">
						<xsl:choose>
							<xsl:when test="contains($value,'-') and contains($value,'px')">
								<xsl:text>0px</xsl:text>
							</xsl:when>
							<xsl:when test="contains($value,'-') and contains($value,'pt')">
								<xsl:text>0pt</xsl:text>
							</xsl:when>
							<xsl:when test="contains($value,'-') and contains($value,'em')">
								<xsl:text>0em</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$value"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:when>

				<!-- 
            AP 5/9/2011
            Remove margins from being put into table cells.  
            Margins are not acceptable attributes for a table-cell object.
            0001144204-11-012733
            0000797468-11-000034
            0001157523-10-004346
            -->
				<!-- Amar changed here for handling -(negative)pixels causing blank pages. -->
				<xsl:when test="$name = 'margin-left'">
					<xsl:choose>
						<xsl:when test="not(self::html:td)">
							<xsl:attribute name="margin-left">
								<xsl:choose>
									<xsl:when test="contains($value,'-') and contains($value,'px')">
										<xsl:text>0px</xsl:text>
									</xsl:when>
									<xsl:when test="contains($value,'-') and contains($value,'pt')">
										<xsl:text>0pt</xsl:text>
									</xsl:when>
									<xsl:when test="contains($value,'-') and contains($value,'em')">
										<xsl:text>0em</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$value"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</xsl:when>
					</xsl:choose>
				</xsl:when>

				<!-- Amar changed here for handling -(negative)pixels causing blank pages. -->
				<xsl:when test="$name = 'margin-bottom'">
					<xsl:choose>
						<xsl:when test="not(self::html:td)">
							<xsl:attribute name="margin-bottom">
								<xsl:choose>
									<xsl:when test="contains($value,'-') and contains($value,'px')">
										<xsl:text>0px</xsl:text>
									</xsl:when>
									<xsl:when test="contains($value,'-') and contains($value,'pt')">
										<xsl:text>0pt</xsl:text>
									</xsl:when>
									<xsl:when test="contains($value,'-') and contains($value,'em')">
										<xsl:text>0em</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$value"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</xsl:when>
					</xsl:choose>
				</xsl:when>

				<!-- Amar changed here for handling -(negative)pixels causing blank pages. -->
				<xsl:when test="$name = 'margin-top'">
					<xsl:choose>
						<xsl:when test="not(self::html:td)">
							<xsl:attribute name="margin-top">
								<xsl:choose>
									<xsl:when test="contains($value,'-') and contains($value,'px')">
										<xsl:text>0px</xsl:text>
									</xsl:when>
									<xsl:when test="contains($value,'-') and contains($value,'pt')">
										<xsl:text>0pt</xsl:text>
									</xsl:when>
									<xsl:when test="contains($value,'-') and contains($value,'em')">
										<xsl:text>0em</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$value"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</xsl:when>
					</xsl:choose>
				</xsl:when>

				<!-- Amar changed here for handling -(negative)pixels causing blank pages. -->
				<xsl:when test="$name = 'margin-right'">
					<xsl:choose>
						<xsl:when test="not(self::html:td)">
							<xsl:attribute name="margin-right">
								<xsl:choose>
									<xsl:when test="contains($value,'-') and contains($value,'px')">
										<xsl:text>0px</xsl:text>
									</xsl:when>
									<xsl:when test="contains($value,'-') and contains($value,'pt')">
										<xsl:text>0pt</xsl:text>
									</xsl:when>
									<xsl:when test="contains($value,'-') and contains($value,'em')">
										<xsl:text>0em</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$value"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</xsl:when>
					</xsl:choose>
				</xsl:when>

				<!-- Added this to assign a default unit to the padding value when there isn't one. D-01737 -->
				<xsl:when test="$name = 'padding'">
					<xsl:attribute name="padding">
						<xsl:choose>
							<xsl:when test="contains($value,'px') or contains($value,'%') or contains($value,'pt') or contains($value,'em') or contains($value,'in') or contains($value,'mm')">
								<xsl:value-of select="$value"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat(normalize-space($value), 'pt')"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:when>

				<!-- Amar changed here for handling -(negative)pixels causing blank pages. -->
				<xsl:when test="$name = 'padding-left'">
					<xsl:attribute name="padding-left">
						<xsl:choose>
							<xsl:when test="contains($value,'-') and contains($value,'px')">
								<xsl:text>0px</xsl:text>
							</xsl:when>
							<xsl:when test="contains($value,'-') and contains($value,'pt')">
								<xsl:text>0pt</xsl:text>
							</xsl:when>
							<xsl:when test="contains($value,'-') and contains($value,'em')">
								<xsl:text>0em</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$value"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:when>


				<xsl:when test="$name = 'padding-bottom'">
					<xsl:attribute name="padding-bottom">
						<xsl:value-of select="$value"/>
					</xsl:attribute>

				</xsl:when>

				<xsl:when test="$name = 'padding-top'">
					<xsl:attribute name="padding-top">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'padding-right'">
					<xsl:attribute name="padding-right">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'padding-left'">
					<xsl:attribute name="padding-left">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>


				<xsl:when test=" $name ='text-align' and self::html:td and $value = 'justify' and not(@width) and not(contains(translate(self::html:td/@style,$upper,$lower),'width'))">
					<!-- TFS# 42880
          JUSTIFY NOT BEING HANDLED PROPERLY BY AH FO ENGINE IT SHOULD JUSTIFY AND TAKE UP REMAING SPACE FOR CELL.
          INSTEAD JUSTIFY APPEARS TO BE IGNORED CAUSED TEXT TO BE RELATIVELY PLACED INTEAD.
           IF JUSTIFY AND WIDTH NOT PRESENT IN STYLE TAG EITHER. 
          NOTE TRANSLATE TO LOWER SO ALL VERSION OF CASE ON WIDTH ARE HANDLED
          ADDITIONALLY CHOSE 100% because 85%  didin't work thisw may cause content to go OFF PAGE SO MAY NEED TO TWEAK
          THIS SETTING
           -->

					<xsl:attribute name="width">
						<xsl:value-of select="'100%'"/>
					</xsl:attribute>
					<xsl:attribute name="text-align">
						<xsl:value-of select="$value"/>
					</xsl:attribute>

				</xsl:when>

				<xsl:when test="$name = 'color'">
					<xsl:attribute name="color">
						<xsl:value-of select="$value"/>
					</xsl:attribute>

				</xsl:when>

				<xsl:when test="$name = 'word-wrap'">
				</xsl:when>

				<xsl:when test="$name = 'margin-right'">
					<xsl:attribute name="margin-right">
						<xsl:value-of select="$value"/>
					</xsl:attribute>

				</xsl:when>

				<xsl:when test="$name = 'margin-top'">
					<xsl:attribute name="margin-top">
						<xsl:value-of select="$value"/>
					</xsl:attribute>

				</xsl:when>


				<xsl:when test="$name = 'page-break-after'">
					<xsl:attribute name="page-break-after">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'page-break-before'">
					<xsl:attribute name="page-break-before">
						<xsl:choose>
							<xsl:when test="contains($value,'ALWAYS')">
								<xsl:text>always</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$value"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'font-weight'">
					<xsl:attribute name="font-weight">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'font-style'">
					<xsl:attribute name="font-style">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<!-- 
			AP 4/20/2011 B-04829 
            Added support for width attribute removal if #
            of columns in table row exceeds overflow-column-count
            
		    AP 5/5/2011 B-05825
		    Added to support width in lowercase form
		    
		    0001144204-11-012733
		    
		    AP 6/1/2011 B-06394
		    Add px to end of width if it doesn't have a unit of measurement
		    0001513162-11-000060
		    -->
				<xsl:when test="$name = 'width'">
					<xsl:variable name="width-value" select="translate($value,$upper,$lower)" />
					<xsl:choose>
						<!-- TFS# 43308 if this is an image tag use content-width tag instead of width   -->
						<xsl:when test="self::html:img">
							<xsl:attribute name="content-width">
								<!--     <xsl:message>picked content-width<xsl:value-of select="$value"/></xsl:message>  -->
								<xsl:choose>

									<xsl:when test="contains($width-value,'%') or contains($width-value,'pc')
                           or contains($width-value,'px') or contains($width-value,'pt') 
                           or contains($width-value,'ex') or contains($width-value,'em') 
                           or contains($width-value,'in') or contains($width-value,'pts') 
                           or contains($width-value,'cm') or contains($width-value,'mm')">
										<xsl:value-of select="$width-value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$width-value" />
										<xsl:text>px</xsl:text>
									</xsl:otherwise>
								</xsl:choose>

							</xsl:attribute>
						</xsl:when>

						<xsl:otherwise>

							<xsl:choose>
								<xsl:when test="self::html:td and contains($value, '%')">
									<xsl:choose>
										<xsl:when test="$overflow-column-count != ''">
											<xsl:variable name="columncnt" select="count((parent::html:tr[1])/ html:td)" />
											<xsl:choose>
												<xsl:when test="$columncnt > $overflow-column-count">
												</xsl:when>
												<xsl:otherwise>
													<xsl:attribute name="width">
														<xsl:value-of select="$value" />
													</xsl:attribute>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="width">
												<xsl:value-of select="$value" />
											</xsl:attribute>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<!-- if not a td tag that contains % just apply width -->
								<xsl:otherwise>
									<xsl:attribute name="width">
										<xsl:choose>
											<xsl:when test="contains($value,'%') or contains($value,'px') 
											or contains($value,'pt') or contains($value,'em') 
											or contains($value,'in') or contains($value,'pts') 
											or contains($value,'cm') or contains($value,'mm') 
											or contains($value,'ex') or contains($value,'pc')">
												<xsl:value-of select="$value" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$value" />
												<xsl:text>px</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>

						</xsl:otherwise>
					</xsl:choose>

				</xsl:when>
				<!-- 
            AP 5/23/2011 B-06270 
            Remove space-after if in a p tag and margin is 0.
            0000034088-11-000016 (pt)
        	0001513162-11-000060 (px)
            -->
				<xsl:when test="$name = 'margin' or $name = 'MARGIN'">
					<xsl:choose>
						<xsl:when test="self::html:p and ($value = '0pt' or $value = '0px')">
							<xsl:attribute name="space-after">
								<xsl:text>0em</xsl:text>
							</xsl:attribute>
							<xsl:attribute name="margin">
								<xsl:choose>
									<xsl:when test="contains($value, 'px') or contains($value, 'em') or contains($value, 'in') or contains($value, 'pt') or contains($value,'mm')">
										<xsl:value-of select="$value"/>
									</xsl:when>
									<xsl:otherwise>
										<!-- to do fix this later to add px to end of marge numbers -->
										<!-- <xsl:value-of select="replace(($value,' ','px ')"/>  -->
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="margin">
								<xsl:choose>
									<xsl:when test="contains($value, 'px') or contains($value, 'em') or contains($value, 'in') or contains($value, 'pt') or contains($value,'mm')">
										<xsl:value-of select="$value"/>
									</xsl:when>
									<xsl:otherwise>
										<!-- to do fix this later to add px to end of marge numbers -->
										<!-- <xsl:value-of select="replace(($value,' ','px ')"/>  -->
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>

				<xsl:when test="$name = 'left' or $name = '-left'">
					<xsl:attribute name="left">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'top' or $name = '-top'">
					<xsl:attribute name="top">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<!-- Added for fixing IM2293053 : Amar -->
				<xsl:when test="$name = 'right' or $name = '-right'">
					<xsl:attribute name="right">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'background-color'">
					<xsl:attribute name="background-color">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'table-layout'">
					<xsl:attribute name="table-layout">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'border-right-style'">
					<xsl:attribute name="border-right-style">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'border-left-style'">
					<xsl:attribute name="border-left-style">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'border-top-style'">
					<xsl:attribute name="border-top-style">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'border'">
					<xsl:attribute name="border">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'border-top'">
					<xsl:attribute name="border-top">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'border-bottom'">
					<xsl:attribute name="border-bottom">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'border-top'">
					<xsl:attribute name="border-top">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'background'">
					<xsl:attribute name="background">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'margin-bottom'">
					<xsl:attribute name="margin-bottom">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'bottom' or $name = '-bottom'">
					<xsl:attribute name="bottom">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<xsl:when test="$name = 'font-variant'">
					<xsl:attribute name="font-variant">
						<xsl:value-of select="$value"/>
					</xsl:attribute>
				</xsl:when>

				<!-- Added by Amar Das c084205  end -->


				<!-- Added by Kamakhya Das.. Neither Prism nor Apache FOP recognize the style
        values in Upper case hence changed them to lower case. -->


				<!-- Added by Kamakhya Das .... because throws errors when the following
         two attribute are used.-->
				<xsl:when test="contains($name,'border-collapse')"></xsl:when>
				<xsl:when test="contains($name,'border-bottom')"></xsl:when>

				<xsl:when test="$name = 'vertical-align' and (
                                 self::html:table or self::html:caption or
                                 self::html:thead or self::html:tfoot or
                                 self::html:tbody or self::html:colgroup or
                                 self::html:col or self::html:tr or
                                 self::html:th or self::html:td)">
					<xsl:choose>
						<xsl:when test="$value = 'top'">
							<xsl:attribute name="display-align">before</xsl:attribute>
						</xsl:when>
						<xsl:when test="$value = 'bottom'">
							<xsl:attribute name="display-align">after</xsl:attribute>
						</xsl:when>
						<xsl:when test="$value = 'middle'">
							<xsl:attribute name="display-align">center</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="display-align">auto</xsl:attribute>
							<xsl:attribute name="relative-align">baseline</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>

				<!--Skip IE7 attibutes-->
				<xsl:when test="starts-with($name, '*')"></xsl:when>

				<xsl:otherwise>
						<xsl:attribute name="{$name}">
							<xsl:value-of select="$value"/>
						</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:variable name="rest"
					  select="normalize-space(substring-after($style, ';'))"/>
		<xsl:if test="$rest">
			<xsl:call-template name="process-style">
				<xsl:with-param name="style" select="$rest"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>


	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Block-level
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template match="html:h1">
		<fo:block xsl:use-attribute-sets="h1">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:h2">
		<fo:block xsl:use-attribute-sets="h2">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:h3">
		<fo:block xsl:use-attribute-sets="h3">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:h4">
		<fo:block xsl:use-attribute-sets="h4">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:h5">
		<fo:block xsl:use-attribute-sets="h5">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:h6">
		<fo:block xsl:use-attribute-sets="h6">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<!-- 
	AP 5/24/2011 B-06227
	Reduced font-size to match PDF stylesheet.
	0001157523-09-008087
	0001157523-10-000921
	0001157523-10-003229
	-->
	<xsl:template match="html:p">
		<fo:block xsl:use-attribute-sets="p">
			<xsl:if test="child::html:a/@href and descendant::html:font/@size">
				<xsl:attribute name="font-size">
					<xsl:choose>
						<xsl:when test="child::html:a/@href and (descendant::html:font/@size =  '1' or descendant::html:font/@SIZE = '1' or descendant::html:font/@size = '-2' or descendant::html:font/@SIZE = '-2')">
							<xsl:value-of select="'8pt'"/>
						</xsl:when>
						<xsl:when test="child::html:a/@href and (descendant::html:font/@size = '2' or descendant::html:font/@SIZE = '2' or descendant::html:font/@size = '-1' or descendant::html:font/@SIZE = '-1')">
							<xsl:value-of select="'10pt'"/>
						</xsl:when>
						<xsl:when test="child::html:a/@href and (descendant::html:font/@size = '3' or descendant::html:font/@SIZE = '3')">
							<xsl:value-of select="'12pt'"/>
						</xsl:when>
						<xsl:when test="child::html:a/@href and (descendant::html:font/@size = '4' or descendant::html:font/@SIZE = '4')">
							<xsl:value-of select="'14pt'"/>
						</xsl:when>
						<xsl:when test="child::html:a/@href and (descendant::html:font/@size = '5' or descendant::html:font/@SIZE = '5')">
							<xsl:value-of select="'14.5pt'"/>
						</xsl:when>
						<xsl:when test="child::html:a/@href and (descendant::html:font/@size = '6' or descendant::html:font/@SIZE = '6')">
							<xsl:value-of select="'18pt'"/>
						</xsl:when>
						<xsl:when test="child::html:a/@href and (descendant::html:font/@size = '7' or descendant::html:font/@SIZE = '7')">
							<xsl:value-of select="'20pt'"/>
						</xsl:when>
						<xsl:when test="child::html:a/@href and (descendant::html:font/@size = '-3' or descendant::html:font/@SIZE = '-3' 
                                                           or descendant::html:font/@size = '-4' or descendant::html:font/@SIZE = '-4' 
                                                           or descendant::html:font/@size = '-5' or descendant::html:font/@SIZE = '-5' 
                                                           or descendant::html:font/@size = '-6' or descendant::html:font/@SIZE = '-6' 
                                                           or descendant::html:font/@size = '-7' or descendant::html:font/@SIZE = '-7')">
							<xsl:value-of select="'8pt'"/>
						</xsl:when>
					</xsl:choose>

				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<!-- initial paragraph, preceded by h1..6 or div -->
	<xsl:template match="html:p[preceding-sibling::*[1][
                       self::html:h1 or self::html:h2 or self::html:h3 or
                       self::html:h4 or self::html:h5 or self::html:h6 or
                       self::html:div]]">
		<xsl:if test="child::html:a/@href and descendant::html:font/@size">

		</xsl:if>
		<fo:block xsl:use-attribute-sets="p-initial">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<!-- initial paragraph, first child of div, body or td -->
	<!-- 
	AP 5/24/2011 B-06227
	Reduced font-size to match PDF stylesheet.
	0001157523-09-008087
	0001157523-10-000921
	0001157523-10-003229
	-->
	<xsl:template match="html:p[not(preceding-sibling::*) and (
                       parent::html:div or parent::html:body )]">
		<fo:block xsl:use-attribute-sets="p-initial-first">
			<xsl:if test="descendant::html:font/@size">
				<xsl:attribute name="font-size">
					<xsl:choose>
						<xsl:when test="descendant::html:font/@size = '1' or descendant::html:font/@size = '-2'">
							<xsl:value-of select="'7pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '2' or descendant::html:font/@size = '-1'">
							<xsl:value-of select="'8.5pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '3'">
							<xsl:value-of select="'10pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '4'">
							<xsl:value-of select="'14pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '5'">
							<xsl:value-of select="'14.5pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '6'">
							<xsl:value-of select="'18pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '7'">
							<xsl:value-of select="'20pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '8'">
							<xsl:value-of select="'22pt'"/>
						</xsl:when>
						<xsl:when  test="descendant::html:font/@size = '-3' or descendant::html:font/@SIZE = '-3' 
                                   or descendant::html:font/@size = '-4' or descendant::html:font/@SIZE = '-4' 
                                   or descendant::html:font/@size = '-5' or descendant::html:font/@SIZE = '-5' 
                                   or descendant::html:font/@size = '-6' or descendant::html:font/@SIZE = '-6' 
                                   or descendant::html:font/@size = '-7' or descendant::html:font/@SIZE = '-7'">
							<xsl:value-of select="'8pt'"/>
						</xsl:when>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<!-- Added by Kamakhya Das to get rid of space-after and space-before in the cells for the tables. 
  These spaces made the table cells look broader.-->
	<!-- 
	AP 5/24/2011 B-06227
	Reduced font-size to match PDF stylesheet.
	0001157523-09-008087
	0001157523-10-000921
	0001157523-10-003229
	-->
	<xsl:template match="html:p[not(preceding-sibling::*) and (parent::html:td)]">
		<fo:block >
			<xsl:if test="descendant::html:font/@size">
				<xsl:attribute name="font-size">
					<xsl:choose>
						<xsl:when test="descendant::html:font/@size = '1' or descendant::html:font/@size = '-2'">
							<xsl:value-of select="'7pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '2' or descendant::html:font/@size = '-1'">
							<xsl:value-of select="'8.5pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '3'">
							<xsl:value-of select="'10pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '4'">
							<xsl:value-of select="'14pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '5'">
							<xsl:value-of select="'14.5pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '6'">
							<xsl:value-of select="'18pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '7'">
							<xsl:value-of select="'20pt'"/>
						</xsl:when>
						<xsl:when test="descendant::html:font/@size = '8'">
							<xsl:value-of select="'22pt'"/>
						</xsl:when>
						<xsl:when  test="descendant::html:font/@size = '-3' or descendant::html:font/@SIZE = '-3' 
                                   or descendant::html:font/@size = '-4' or descendant::html:font/@SIZE = '-4' 
                                   or descendant::html:font/@size = '-5' or descendant::html:font/@SIZE = '-5' 
                                   or descendant::html:font/@size = '-6' or descendant::html:font/@SIZE = '-6' 
                                   or descendant::html:font/@size = '-7' or descendant::html:font/@SIZE = '-7'">
							<xsl:value-of select="'8pt'"/>
						</xsl:when>

					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>


	<xsl:template match="html:blockquote">
		<fo:block xsl:use-attribute-sets="blockquote">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:pre">
		<fo:block xsl:use-attribute-sets="pre">
			<xsl:call-template name="process-pre"/>
		</fo:block>
	</xsl:template>


	<xsl:template name="process-pre">
		<xsl:call-template name="process-common-attributes"/>
		<!-- remove leading CR/LF/CRLF char -->
		<xsl:variable name="crlf">
			<xsl:text>&#xD;&#xA;</xsl:text>
		</xsl:variable>
		<xsl:variable name="lf">
			<xsl:text>&#xA;</xsl:text>
		</xsl:variable>
		<xsl:variable name="cr">
			<xsl:text>&#xD;</xsl:text>
		</xsl:variable>
		<xsl:for-each select="node()">
			<xsl:choose>
				<xsl:when test="position() = 1 and self::text()">
					<xsl:choose>
						<xsl:when test="starts-with(., $lf)">
							<xsl:value-of select="substring(., 2)"/>
						</xsl:when>
						<xsl:when test="starts-with(., $crlf)">
							<xsl:value-of select="substring(., 3)"/>
						</xsl:when>
						<xsl:when test="starts-with(., $cr)">
							<xsl:value-of select="substring(., 2)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="."/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="html:address">
		<fo:block xsl:use-attribute-sets="address">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:hr">
		<fo:block xsl:use-attribute-sets="hr">
			<xsl:call-template name="process-common-attributes"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:div">
		<!-- need fo:block-container? or normal fo:block -->
		<xsl:variable name="need-block-container">
			<xsl:call-template name="need-block-container"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$need-block-container = 'true'">
				<fo:block-container>
					<xsl:if test="@dir">
						<xsl:attribute name="writing-mode">
							<xsl:choose>
								<xsl:when test="@dir = 'rtl'">rl-tb</xsl:when>
								<xsl:otherwise>lr-tb</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="process-common-attributes"/>
					<fo:block start-indent="0pt" end-indent="0pt">
						<xsl:apply-templates/>
					</fo:block>
				</fo:block-container>
			</xsl:when>
			<xsl:otherwise>
				<!-- normal block -->
				<fo:block>
					<xsl:call-template name="process-common-attributes"/>
					<xsl:apply-templates/>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="need-block-container">
		<xsl:choose>
			<xsl:when test="@dir">true</xsl:when>
			<xsl:when test="@style">
				<!-- DSHA 05/05/2011 
      Added for case insentive change for PPT 
      and this will also fix other issues where case is different or mixed
       -->
				<xsl:variable name="s"
							  select="translate((concat(';', translate(normalize-space(@style),
                                                    ' ', ''))),$upper,$lower)"/>
				<xsl:choose>
					<xsl:when test="contains($s, ';width:') or
                          contains($s, ';height:') or
                          contains($s, ';position:absolute') or
                          contains($s, ';position:fixed') or
                          contains($s, ';writing-mode:')">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="html:center">
		<fo:block text-align="center">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:fieldset | html:form | html:dir | html:menu">
		<fo:block space-before="1em" space-after="1em">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       List
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template match="html:ul">
		<fo:list-block xsl:use-attribute-sets="ul">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:list-block>
	</xsl:template>

	<xsl:template match="html:li//html:ul">
		<fo:list-block xsl:use-attribute-sets="ul-nested">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:list-block>
	</xsl:template>

	<xsl:template match="html:ol">
		<fo:list-block xsl:use-attribute-sets="ol">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:list-block>
	</xsl:template>

	<xsl:template match="html:li//html:ol">
		<fo:list-block xsl:use-attribute-sets="ol-nested">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:list-block>
	</xsl:template>

	<xsl:template match="html:ul/html:li">
		<fo:list-item xsl:use-attribute-sets="ul-li">
			<xsl:call-template name="process-ul-li"/>
		</fo:list-item>
	</xsl:template>

	<xsl:template name="process-ul-li">
		<xsl:call-template name="process-common-attributes"/>
		<fo:list-item-label end-indent="label-end()"
							text-align="end" wrap-option="no-wrap">
			<fo:block>
				<xsl:variable name="depth" select="count(ancestor::html:ul)" />
				<xsl:choose>
					<xsl:when test="$depth = 1">
						<fo:inline xsl:use-attribute-sets="ul-label-1">
							<xsl:value-of select="$ul-label-1"/>
						</fo:inline>
					</xsl:when>
					<xsl:when test="$depth = 2">
						<fo:inline xsl:use-attribute-sets="ul-label-2">
							<xsl:value-of select="$ul-label-2"/>
						</fo:inline>
					</xsl:when>
					<xsl:otherwise>
						<fo:inline xsl:use-attribute-sets="ul-label-3">
							<xsl:value-of select="$ul-label-3"/>
						</fo:inline>
					</xsl:otherwise>
				</xsl:choose>
			</fo:block>
		</fo:list-item-label>
		<fo:list-item-body start-indent="body-start()">
			<fo:block>
				<xsl:apply-templates/>
			</fo:block>
		</fo:list-item-body>
	</xsl:template>

	<xsl:template match="html:ol/html:li">
		<fo:list-item xsl:use-attribute-sets="ol-li">
			<xsl:call-template name="process-ol-li"/>
		</fo:list-item>
	</xsl:template>

	<xsl:template name="process-ol-li">
		<xsl:call-template name="process-common-attributes"/>
		<fo:list-item-label end-indent="label-end()"
							text-align="end" wrap-option="no-wrap">
			<fo:block>
				<xsl:variable name="depth" select="count(ancestor::html:ol)" />
				<xsl:choose>
					<xsl:when test="$depth = 1">
						<fo:inline xsl:use-attribute-sets="ol-label-1">
							<xsl:number format="{$ol-label-1}"/>
						</fo:inline>
					</xsl:when>
					<xsl:when test="$depth = 2">
						<fo:inline xsl:use-attribute-sets="ol-label-2">
							<xsl:number format="{$ol-label-2}"/>
						</fo:inline>
					</xsl:when>
					<xsl:otherwise>
						<fo:inline xsl:use-attribute-sets="ol-label-3">
							<xsl:number format="{$ol-label-3}"/>
						</fo:inline>
					</xsl:otherwise>
				</xsl:choose>
			</fo:block>
		</fo:list-item-label>
		<fo:list-item-body start-indent="body-start()">
			<fo:block>
				<xsl:apply-templates/>
			</fo:block>
		</fo:list-item-body>
	</xsl:template>

	<xsl:template match="html:dl">
		<fo:block xsl:use-attribute-sets="dl">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:dt">
		<fo:block xsl:use-attribute-sets="dt">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:dd">
		<fo:block xsl:use-attribute-sets="dd">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Table
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->
	<!-- Amar: Need this for pdf cover page. -->
	<xsl:template match="html:table">
		<xsl:choose>
			<xsl:when test="$formType = 'exists'">
				<xsl:call-template name="make-table-caption"/>
				<fo:table xsl:use-attribute-sets="table">
					<xsl:call-template name="process-table"/>
				</fo:table>
			</xsl:when>
			<xsl:otherwise>
				<fo:table-and-caption xsl:use-attribute-sets="table-and-caption">
					<xsl:call-template name="make-table-caption"/>
					<fo:table xsl:use-attribute-sets="table">
						<xsl:call-template name="process-table"/>
					</fo:table>
				</fo:table-and-caption>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="make-table-caption">
		<xsl:if test="html:caption/@align">
			<xsl:attribute name="caption-side">
				<xsl:value-of select="html:caption/@align"/>
			</xsl:attribute>
		</xsl:if>

		<xsl:if test="@style">
			<xsl:call-template name="process-table-caption-style">
				<xsl:with-param name="style" select="translate(@style,$upper,$lower)"/>
			</xsl:call-template>
		</xsl:if>

		<xsl:apply-templates select="html:caption"/>
	</xsl:template>
	<!-- 
    AP 5/12/2011 B-05825 
    Adds text-align from html tables to fo table-and-caption
    
    0001144204-11-012733
    -->
	<xsl:template name="process-table-caption-style">
		<xsl:param name="style"/>
		<!-- e.g., style="text-align: center; color: red"
         converted to text-align="center" color="red" -->
		<xsl:variable name="name" select="normalize-space(substring-before($style, ':'))"/>
		<xsl:if test="$name">
			<xsl:variable name="value-and-rest" select="normalize-space(substring-after($style, ':'))" />
			<xsl:variable name="value">
				<xsl:choose>
					<xsl:when test="contains($value-and-rest, ';')">
						<xsl:value-of select="normalize-space(substring-before($value-and-rest, ';'))" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$value-and-rest" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$name = 'text-align'">
					<xsl:attribute name="text-align">
						<xsl:value-of select="$value" />
					</xsl:attribute>
				</xsl:when>
				<!-- 
		    AP 5/12/2011 B-05825 
		    Defaults text-align to left if none is set
		    
		    0001144204-11-012733
		    -->
				<xsl:otherwise>
					<xsl:attribute name="text-align">
						<xsl:text>left</xsl:text>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:variable name="rest" select="normalize-space(substring-after($style, ';'))" />
		<xsl:if test="$rest">
			<xsl:call-template name="process-table-caption-style">
				<xsl:with-param name="style" select="$rest" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="process-table">
		<!-- 
    AP 4/20/2011 B-04829 
    Added support for width attribute removal if #
    of columns in table row exceeds overflow-column-count
    
    AP 6/1/2011 B-06394
	Add px to end of width if it doesn't have a unit of measurement
	0001513162-11-000060
    -->
		<xsl:if test="@width">
			<xsl:variable name="width-value" select="translate(@width,$upper,$lower)" />
			<!-- 
	    AP 5/12/2011 B-05825 
	    Changed attribute name from inline-progression-dimension
	    to width
	    0001144204-11-012733
	    -->
			<xsl:choose>
				<xsl:when test="contains($width-value,'%')">
					<xsl:attribute name="width">
						<xsl:choose>
							<xsl:when test="$overflow-column-count != ''">
								<xsl:variable name="tid" select="@zidz" />
								<xsl:variable name="columncnt" select="count(html:tr[1]/html:td)" />
								<!--   <xsl:message>Table:<xsl:value-of select="$tid"/> column count:<xsl:value-of select="$columncnt"/></xsl:message>  -->
								<xsl:choose>
									<xsl:when test="$columncnt > $overflow-column-count">
										<xsl:text>100%</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$width-value" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$width-value" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="width">
						<xsl:choose>
							<xsl:when test="contains($width-value,'%') or contains($width-value,'px') 
									or contains($width-value,'pt') or contains($width-value,'em') 
									or contains($width-value,'in') or contains($width-value,'pts') 
									or contains($width-value,'cm') or contains($width-value,'mm') 
									or contains($width-value,'ex') or contains($width-value,'pc')">
								<xsl:value-of select="$width-value" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$width-value" />
								<xsl:text>px</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>

		<xsl:if test="@border or @frame">
			<xsl:choose>
				<xsl:when test="@border &gt; 0">
					<xsl:attribute name="border">
						<xsl:value-of select="@border"/>
						<xsl:text>px</xsl:text>
					</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="parent::html:table/@border = '0' or parent::html:table/@frame = 'void' ">
					<!-- <xsl:attribute name="border-style">hidden</xsl:attribute> -->
					<!-- For apache FOP, if the border has to be hidden then the border-style should be none
          in the following way....Kamakhya Das -->
					<xsl:attribute name="border-style">none</xsl:attribute>
				</xsl:when>
				<xsl:when test="@border = '0' or @frame = 'void'">
					<!-- For apache FOP, if the border has to be hidden then the border-style should be none
          in the following way....Kamakhya Das -->
					<!--  <xsl:attribute name="border-style">hidden</xsl:attribute>-->
					<xsl:attribute name="border-style">none</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'above'">
					<xsl:attribute name="border-style">outset hidden hidden hidden</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'below'">
					<xsl:attribute name="border-style">hidden hidden outset hidden</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'hsides'">
					<xsl:attribute name="border-style">outset hidden</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'vsides'">
					<xsl:attribute name="border-style">hidden outset</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'lhs'">
					<xsl:attribute name="border-style">hidden hidden hidden outset</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'rhs'">
					<xsl:attribute name="border-style">hidden outset hidden hidden</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="border-style">outset</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="@cellspacing">
			<xsl:attribute name="border-spacing">
				<xsl:value-of select="@cellspacing"/>
				<xsl:text>px</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="border-collapse">separate</xsl:attribute>
		</xsl:if>
		<xsl:if test="@rules and (@rules = 'groups' or
                      @rules = 'rows' or
                      @rules = 'cols' or
                      @rules = 'all' and (not(@border or @frame) or
                          @border = '0' or @frame and
                          not(@frame = 'box' or @frame = 'border')))">
			<xsl:attribute name="border-collapse">collapse</xsl:attribute>
			<xsl:if test="not(@border or @frame)">
				<xsl:attribute name="border-style">hidden</xsl:attribute>
			</xsl:if>
		</xsl:if>
		<!-- <xsl:attribute name="table-layout">fixed</xsl:attribute> -->
		<xsl:call-template name="process-common-attributes"/>
		<xsl:apply-templates select="html:col | html:colgroup"/>
		<xsl:apply-templates select="html:thead"/>
		<xsl:apply-templates select="html:tfoot"/>
		<xsl:choose>
			<xsl:when test="html:tbody">
				<xsl:apply-templates select="html:tbody"/>
			</xsl:when>
			<xsl:otherwise>
				<fo:table-body xsl:use-attribute-sets="tbody">
					<xsl:apply-templates select="html:tr"/>
				</fo:table-body>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="html:caption">
		<fo:table-caption xsl:use-attribute-sets="table-caption">
			<xsl:call-template name="process-common-attributes"/>
			<fo:block>
				<xsl:apply-templates/>
			</fo:block>
		</fo:table-caption>
	</xsl:template>

	<xsl:template match="html:thead">
		<fo:table-header xsl:use-attribute-sets="thead">
			<xsl:call-template name="process-table-rowgroup"/>
		</fo:table-header>
	</xsl:template>

	<xsl:template match="html:tfoot">
		<fo:table-footer xsl:use-attribute-sets="tfoot">
			<xsl:call-template name="process-table-rowgroup"/>
		</fo:table-footer>
	</xsl:template>

	<xsl:template match="html:tbody">
		<fo:table-body xsl:use-attribute-sets="tbody">
			<xsl:call-template name="process-table-rowgroup"/>
		</fo:table-body>
	</xsl:template>

	<xsl:template name="process-table-rowgroup">
		<xsl:if test="ancestor::html:table[1]/@rules = 'groups'">
			<xsl:attribute name="border">1px solid</xsl:attribute>
		</xsl:if>
		<xsl:call-template name="process-common-attributes-and-children"/>
	</xsl:template>

	<xsl:template match="html:colgroup">
		<fo:table-column xsl:use-attribute-sets="table-column">
			<xsl:call-template name="process-table-column"/>
		</fo:table-column>
	</xsl:template>

	<xsl:template match="html:colgroup[html:col]">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="html:col">
		<fo:table-column xsl:use-attribute-sets="table-column">
			<xsl:call-template name="process-table-column"/>
		</fo:table-column>
	</xsl:template>

	<xsl:template name="process-table-column">
		<xsl:if test="parent::html:colgroup">
			<xsl:call-template name="process-col-width">
				<xsl:with-param name="width" select="../@width"/>
			</xsl:call-template>
			<xsl:call-template name="process-cell-align">
				<xsl:with-param name="align" select="../@align"/>
			</xsl:call-template>
			<xsl:call-template name="process-cell-valign">
				<xsl:with-param name="valign" select="../@valign"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="@span">
			<xsl:attribute name="number-columns-repeated">
				<xsl:value-of select="@span"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:call-template name="process-col-width">
			<xsl:with-param name="width" select="@width"/>
			<!-- it may override parent colgroup's width -->
		</xsl:call-template>
		<xsl:if test="ancestor::html:table[1]/@rules = 'cols'">
			<xsl:attribute name="border">1px solid</xsl:attribute>
		</xsl:if>
		<xsl:call-template name="process-common-attributes"/>
		<!-- this processes also align and valign -->
	</xsl:template>

	<xsl:template match="html:tr">
		<fo:table-row xsl:use-attribute-sets="tr">
			<xsl:call-template name="process-table-row"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template match="html:tr[parent::html:table and html:th and not(html:td)]">
		<fo:table-row xsl:use-attribute-sets="tr" keep-with-next="always">
			<xsl:call-template name="process-table-row"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="process-table-row">
		<xsl:if test="ancestor::html:table[1]/@rules = 'rows'">
			<xsl:attribute name="border">1px solid</xsl:attribute>
		</xsl:if>
		<xsl:call-template name="process-common-attributes-and-children"/>
	</xsl:template>

	<xsl:template match="html:th">
		<fo:table-cell xsl:use-attribute-sets="th">
			<xsl:call-template name="process-table-cell"/>
		</fo:table-cell>
	</xsl:template>

	<xsl:template match="html:td">
		<fo:table-cell xsl:use-attribute-sets="td">
			<xsl:call-template name="process-table-cell"/>
		</fo:table-cell>
	</xsl:template>

	<xsl:template name="process-table-cell">
		<xsl:if test="@colspan">
			<xsl:attribute name="number-columns-spanned">
				<xsl:value-of select="@colspan"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="@rowspan">
			<xsl:attribute name="number-rows-spanned">
				<xsl:value-of select="@rowspan"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:for-each select="ancestor::html:table[1]">
			<xsl:if test="(@border or @rules) and (@rules = 'all' or
                    not(@rules) and not(@border = '0'))">
				<xsl:attribute name="border-style">inset</xsl:attribute>
			</xsl:if>
			<xsl:if test="@cellpadding">
				<xsl:attribute name="padding">
					<xsl:choose>
						<xsl:when test="contains(@cellpadding, '%')">
							<xsl:value-of select="@cellpadding"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@cellpadding"/>
							<xsl:text>px</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
		</xsl:for-each>
		<xsl:if test="not(@align or ../@align or
                      ../parent::*[self::html:thead or self::html:tfoot or
                      self::html:tbody]/@align) and
                  ancestor::html:table[1]/*[self::html:col or
                      self::html:colgroup]/descendant-or-self::*/@align">
			<xsl:attribute name="text-align">from-table-column()</xsl:attribute>
		</xsl:if>
		<xsl:if test="not(@valign or ../@valign or
                      ../parent::*[self::html:thead or self::html:tfoot or
                      self::html:tbody]/@valign) and
                  ancestor::html:table[1]/*[self::html:col or
                      self::html:colgroup]/descendant-or-self::*/@valign">
			<xsl:attribute name="display-align">from-table-column()</xsl:attribute>
			<xsl:attribute name="relative-align">from-table-column()</xsl:attribute>
		</xsl:if>

		<!-- Amar: adding these two tests here. These are very essential properties which were missing from the code so far. Strange !! -->
		<!--  
    <xsl:if test="@width">   
      <xsl:attribute name="width">     
         <xsl:choose>
            <xsl:when test="contains(@width,'%')">
               <xsl:value-of select=" concat(normalize-space(substring-before(@width,'%')) div 100 *(normalize-space(substring-before($default-page-width,'in'))- (2 * normalize-space(substring-before($page-margin-left,'in')))),'in')"/>
            </xsl:when>           
            <xsl:otherwise>
               <xsl:value-of select="@width"/>
            </xsl:otherwise>
            </xsl:choose>
      </xsl:attribute>
   </xsl:if>
   -->

		<!-- 
	AP 4/20/2011 B-04829 
	Added support for width attribute removal if #
	of columns in table row exceeds overflow-column-count
	
	AP 6/1/2011 B-06394
	Add px to end of width if it doesn't have a unit of measurement
	0001513162-11-000060
	-->
		<xsl:if test="@width">
			<xsl:variable name="width-value" select="translate(@width,$upper,$lower)" />
			<xsl:choose>
				<xsl:when test="$overflow-column-count != ''">
					<!-- if first time thru AH Fo engine make sure width is applied if found then second time we know if it overflowed -->
					<xsl:variable name="tid" select="ancestor::html:table/@zidz" />
					<xsl:variable name="columncnt" select="count((parent::html:tr[1])/ html:td)" />
					<!-- <xsl:message>Table2:<xsl:value-of select="$tid"/> column count2:<xsl:value-of select="$columncnt"/></xsl:message>  -->
					<xsl:choose>
						<xsl:when test="$columncnt > $overflow-column-count">
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="width">
								<xsl:choose>
									<xsl:when test="contains($width-value,'%') or contains($width-value,'px') 
												or contains($width-value,'pt') or contains($width-value,'em') 
												or contains($width-value,'in') or contains($width-value,'pts') 
												or contains($width-value,'cm') or contains($width-value,'mm') 
												or contains($width-value,'ex') or contains($width-value,'pc')">
										<xsl:value-of select="$width-value"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$width-value"/>
										<xsl:text>px</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="width">
						<xsl:choose>
							<xsl:when test="contains($width-value,'%') or contains($width-value,'px') 
									or contains($width-value,'pt') or contains($width-value,'em') 
									or contains($width-value,'in') or contains($width-value,'pts') 
									or contains($width-value,'cm') or contains($width-value,'mm') 
									or contains($width-value,'ex') or contains($width-value,'pc')">
								<xsl:value-of select="$width-value"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$width-value"/>
								<xsl:text>px</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>


		<!--
   <xsl:if test="@WIDTH">   
      <xsl:attribute name="width">     
         <xsl:choose>
            <xsl:when test="contains(@WIDTH,'%')">
               <xsl:value-of select=" concat(normalize-space(substring-before(@WIDTH,'%')) div 100 *(normalize-space(substring-before($default-page-width,'in'))- (2 * normalize-space(substring-before($page-margin-left,'in')))),'in')"/>
            </xsl:when>           
            <xsl:otherwise>
               <xsl:value-of select="@WIDTH"/>
            </xsl:otherwise>
            </xsl:choose>
      </xsl:attribute>
   </xsl:if>
   -->

		<!-- 
	AP 4/20/2011 B-04829 
	Added support for width attribute removal if #
	of columns in table row exceeds overflow-column-count
	
	AP 6/1/2011 B-06394
	Add px to end of width if it doesn't have a unit of measurement
	0001513162-11-000060
	-->
		<xsl:if test="@WIDTH">
			<xsl:variable name="width-value" select="translate(@WIDTH,$upper,$lower)" />
			<xsl:choose>
				<xsl:when test="$overflow-column-count != ''">
					<!-- if first time thru AH Fo engine make sure width is applied if found then second time we know if it overflowed -->
					<xsl:variable name="tid" select="ancestor::html:table/@zidz" />
					<xsl:variable name="columncnt" select="count((parent::html:tr[1])/ html:td)" />
					<!-- <xsl:message>Table2:<xsl:value-of select="$tid"/> column count2:<xsl:value-of select="$columncnt"/></xsl:message>  -->
					<xsl:choose>
						<xsl:when test="$columncnt > $overflow-column-count">
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="width">
								<xsl:choose>
									<xsl:when test="contains($width-value,'%') or contains($width-value,'px') 
												or contains($width-value,'pt') or contains($width-value,'em') 
												or contains($width-value,'in') or contains($width-value,'pts') 
												or contains($width-value,'cm') or contains($width-value,'mm') 
												or contains($width-value,'ex') or contains($width-value,'pc')">
										<xsl:value-of select="$width-value"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$width-value"/>
										<xsl:text>px</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="width">
						<xsl:choose>
							<xsl:when test="contains($width-value,'%') or contains($width-value,'px') 
										or contains($width-value,'pt') or contains($width-value,'em') 
										or contains($width-value,'in') or contains($width-value,'pts') 
										or contains($width-value,'cm') or contains($width-value,'mm') 
										or contains($width-value,'ex') or contains($width-value,'pc')">
								<xsl:value-of select="$width-value"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$width-value"/>
								<xsl:text>px</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>

		<!--  
    <xsl:if test="starts-with(descendant::*/text(),')%') and string-length(descendant::*/text()) &lt; 4">
      <xsl:attribute name="keep-together">always</xsl:attribute>
    </xsl:if>
    -->
		<!--Amar: Apply nowrap : D-02112 -->
		<!-- 
    <xsl:if test="@nowrap or @NOWRAP">
      <xsl:attribute name="keep-together">always</xsl:attribute>
    </xsl:if>
     -->

		<xsl:call-template name="process-common-attributes"/>
		<fo:block>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>

	<xsl:template name="process-col-width">
		<xsl:param name="width"/>
		<xsl:if test="$width and $width != '0*'">
			<xsl:attribute name="column-width">
				<xsl:choose>
					<xsl:when test="contains($width, '*')">
						<xsl:text>proportional-column-width(</xsl:text>
						<xsl:value-of select="substring-before($width, '*')"/>
						<xsl:text>)</xsl:text>
					</xsl:when>
					<xsl:when test="contains($width, '%')">
						<xsl:value-of select="$width"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$width"/>
						<xsl:text>px</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<xsl:template name="process-cell-align">
		<xsl:param name="align"/>
		<xsl:if test="$align">
			<xsl:attribute name="text-align">
				<xsl:choose>
					<xsl:when test="$align = 'char'">
						<xsl:choose>
							<xsl:when test="$align/../@char">
								<xsl:value-of select="$align/../@char"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="'.'"/>
								<!-- todo: it should depend on xml:lang ... -->
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$align"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<xsl:template name="process-cell-valign">
		<xsl:param name="valign"/>
		<xsl:if test="$valign">
			<xsl:attribute name="display-align">
				<xsl:choose>
					<xsl:when test="$valign = 'middle'">center</xsl:when>
					<xsl:when test="$valign = 'bottom'">after</xsl:when>
					<xsl:when test="$valign = 'baseline'">auto</xsl:when>
					<xsl:otherwise>before</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$valign = 'baseline'">
				<xsl:attribute name="relative-align">baseline</xsl:attribute>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Inline-level
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<!--<xsl:template match="html:b">
    <fo:inline xsl:use-attribute-sets="b">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>
  
  -->
	<!-- 
	AP 5/24/2011 B-06227
	Reduced font-size to match PDF stylesheet.
	0001157523-09-008087
	0001157523-10-000921
	0001157523-10-003229
	-->
	<xsl:template match="html:b">
		<fo:inline xsl:use-attribute-sets="b">
			<xsl:attribute name="font-weight">bold</xsl:attribute>
			<xsl:choose>
				<xsl:when test="parent::node()/@size">
					<xsl:attribute name="font-size">
						<xsl:choose>
							<xsl:when test="parent::node()/@size = '1' or parent::node()/@size = '-2'">
								<xsl:value-of select="'7pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '2' or parent::node()/@size = '-1'">
								<xsl:value-of select="'8.5pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '3'">
								<xsl:value-of select="'10pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '4'">
								<xsl:value-of select="'14pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '5'">
								<xsl:value-of select="'14.5pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '6'">
								<xsl:value-of select="'16pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '7'">
								<xsl:value-of select="'18pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '8'">
								<xsl:value-of select="'20pt'"/>
							</xsl:when>
							<xsl:when  test="parent::node()/@size = '-3' or parent::node()/@size = '-4'
                                   or parent::node()/@size = '-5' or parent::node()/@size = '-6'
                                   or parent::node()/@size = '-7' or parent::node()/@size = '-8'">
								<xsl:value-of select="'8pt'"/>
							</xsl:when>
						</xsl:choose>
					</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<!-- Added this templtate because when 'br' is rendered by the processor then a fo:inline tag is
  generated which suppresses all the new lines created by the new 'fo:block' elements and this was causing 
  no new line feed whenever there was something in like. e.g. document in the following link:
  http://ir.int.westlawbusiness.com/document/v1/0001047469-10-000976/doc/0001047469-10-000976.rtf
  
  With this change the header or the title comes with the line feed.
   -->
	<xsl:template match="html:b[descendant::html:br]">
		<fo:inline xsl:use-attribute-sets="b">
			<xsl:attribute name="font-weight">bold</xsl:attribute>
			<!-- 0000950123-10-044635 removed this to fix For Example see the CONDENSED CONSOLIDATED BALANCE SHEETS on page 4 
    and the table within the Fair Value of Financial Instruments note on page 8. instead added br:parent div handler-->
			<xsl:attribute name="linefeed-treatment">preserve</xsl:attribute>
			<xsl:choose>
				<xsl:when test="parent::node()/@size">
					<xsl:attribute name="font-size">
						<xsl:choose>
							<xsl:when test="parent::node()/@size = '1' or parent::node()/@size = '-2'">
								<xsl:value-of select="'7pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '2' or parent::node()/@size = '-1'">
								<xsl:value-of select="'8.5pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '3'">
								<xsl:value-of select="'10pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '4'">
								<xsl:value-of select="'14pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '5'">
								<xsl:value-of select="'14.5pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '6'">
								<xsl:value-of select="'16pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '7'">
								<xsl:value-of select="'18pt'"/>
							</xsl:when>
							<xsl:when test="parent::node()/@size = '8'">
								<xsl:value-of select="'20pt'"/>
							</xsl:when>
							<xsl:when  test="parent::node()/@size = '-3' or parent::node()/@size = '-4'
                                   or parent::node()/@size = '-5' or parent::node()/@size = '-6'
                                   or parent::node()/@size = '-7' or parent::node()/@size = '-8'">
								<xsl:value-of select="'8pt'"/>
							</xsl:when>
						</xsl:choose>
					</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>




	<xsl:template match="html:strong">
		<fo:inline xsl:use-attribute-sets="strong">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:strong//html:em | html:em//html:strong">
		<fo:inline xsl:use-attribute-sets="strong-em">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:i">
		<fo:inline xsl:use-attribute-sets="i">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:cite">
		<fo:inline xsl:use-attribute-sets="cite">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:em">
		<fo:inline xsl:use-attribute-sets="em">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:var">
		<fo:inline xsl:use-attribute-sets="var">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:dfn">
		<fo:inline xsl:use-attribute-sets="dfn">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:tt">
		<fo:inline xsl:use-attribute-sets="tt">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:code">
		<fo:inline xsl:use-attribute-sets="code">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:kbd">
		<fo:inline xsl:use-attribute-sets="kbd">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:samp">
		<fo:inline xsl:use-attribute-sets="samp">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:big">
		<fo:inline xsl:use-attribute-sets="big">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:small">
		<fo:inline xsl:use-attribute-sets="small">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:sub">
		<fo:inline xsl:use-attribute-sets="sub">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:sup">
		<fo:inline xsl:use-attribute-sets="sup">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:s">
		<fo:inline xsl:use-attribute-sets="s">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:strike">
		<fo:inline xsl:use-attribute-sets="strike">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:del">
		<fo:inline xsl:use-attribute-sets="del">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:u">
		<fo:inline xsl:use-attribute-sets="u">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:ins">
		<fo:inline xsl:use-attribute-sets="ins">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:abbr">
		<fo:inline xsl:use-attribute-sets="abbr">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:acronym">
		<fo:inline xsl:use-attribute-sets="acronym">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:span">
		<fo:inline>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:span[@dir]">
		<fo:bidi-override direction="{@dir}" unicode-bidi="embed">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:bidi-override>
	</xsl:template>

	<xsl:template match="html:span[@style and contains(@style, 'writing-mode')]">
		<fo:inline-container alignment-baseline="central"
							 text-indent="0pt"
							 last-line-end-indent="0pt"
							 start-indent="0pt"
							 end-indent="0pt"
							 text-align="center"
							 text-align-last="center">
			<xsl:call-template name="process-common-attributes"/>
			<fo:block wrap-option="no-wrap" line-height="1">
				<xsl:apply-templates/>
			</fo:block>
		</fo:inline-container>
	</xsl:template>

	<xsl:template match="html:bdo">
		<fo:bidi-override direction="{@dir}" unicode-bidi="bidi-override">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:bidi-override>
	</xsl:template>

	<!-- 
    AP 4/26/2011 B-04775 
    Convert all <br/> elements to line feeds.  All line feeds besides ones that
    exist in <pre> elements should have been removed prior to this. This is 
    being done to handle <br/> tags since fo doesn't support line break objects.
    
    Existing defects this fixes:
    
    D-01673 Kamakhya Das
    Handles AT&T case br tags inside div tag and they are the only tag
  	0000732717-10-000013- see page labled 5 where we are missing line 
  	breks before ADVERTISING SOLUTION
  	
  	D-02037 Amar
  	
  	D-02040 Amar
  	Too much white space is applied after br tags within financial tables. 
  	For Example see the CONDENSED CONSOLIDATED BALANCE SHEETS on page 4 and 
  	the table within the Fair Value of Financial Instruments note on page 8. 
  	0000950123-10-044635 
  	
  	Added this for a fix at page 11 of the 0001104659-10-021956, spacing in 
  	table headings issue
    -->
	<xsl:template match="html:br">
		<!-- 
	AP 5/18/2011 
	Changed to fix bug with a only br(s) in a div
	causing a double new line instead of single
	0001451505-10-000069
	 -->
		<xsl:choose>
			<!-- 
	 	AP 5/23/2011 B-06270 
        If block level element, use fo:block for newline, otherwise newline char
        0000034088-11-000016
        -->
			<xsl:when test="parent::html:p or parent::html:div 
	 					or parent::html:h1 or parent::html:h2 
	 					or parent::html:h3 or parent::html:h4 
	 					or parent::html:h5 or parent::html:h6 
	 					or parent::html:li or parent::html:th 
	 					or parent::html:td or parent::html:blockquote">
				<fo:block>&#160;</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>&#xA;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="html:q">
		<fo:inline xsl:use-attribute-sets="q">
			<xsl:call-template name="process-common-attributes"/>
			<xsl:choose>
				<xsl:when test="lang('ja')">
					<xsl:text>ã€Œ</xsl:text>
					<xsl:apply-templates/>
					<xsl:text>ã€</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<!-- lang('en') -->
					<xsl:text>â€œ</xsl:text>
					<xsl:apply-templates/>
					<xsl:text>â€</xsl:text>
					<!-- todo: other languages ...-->
				</xsl:otherwise>
			</xsl:choose>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:q//html:q">
		<fo:inline xsl:use-attribute-sets="q-nested">
			<xsl:call-template name="process-common-attributes"/>
			<xsl:choose>
				<xsl:when test="lang('ja')">
					<xsl:text>ã€Ž</xsl:text>
					<xsl:apply-templates/>
					<xsl:text>ã€</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<!-- lang('en') -->
					<xsl:text>â€˜</xsl:text>
					<xsl:apply-templates/>
					<xsl:text>â€™</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</fo:inline>
	</xsl:template>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Image
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template match="html:img">
		<fo:external-graphic xsl:use-attribute-sets="img">
			<xsl:call-template name="process-img"/>
		</fo:external-graphic>
	</xsl:template>

	<xsl:template match="html:img[ancestor::html:a/@href]">
		<fo:external-graphic xsl:use-attribute-sets="img-link">
			<xsl:call-template name="process-img"/>
		</fo:external-graphic>
	</xsl:template>

	<xsl:template name="process-img">
		<xsl:attribute name="src">
			<xsl:text>url('</xsl:text>
			<xsl:value-of select="@src"/>
			<xsl:text>')</xsl:text>
		</xsl:attribute>
		<xsl:if test="@alt">
			<xsl:attribute name="role">
				<xsl:value-of select="@alt"/>
			</xsl:attribute>
		</xsl:if>

		<xsl:if test="@width">
			<xsl:choose>
				<xsl:when test="contains(@width, '%')">
					<xsl:attribute name="width">
						<xsl:value-of select="@width"/>
					</xsl:attribute>
					<xsl:attribute name="content-width">scale-to-fit</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="content-width">
						<xsl:value-of select="@width"/>px
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>

		<!-- 
        DSHA: 05/17/2011 if width is not specified, then default the image to have a width
        of 75%.  This addresses defects D-02089
        TRI filing 0001140361-10-042928 and 02090
        Removed this change to set width to 75% to 0001178913-11-000774  org chart where text doesn't line up
        because image was scaled down.
        For TRI filing add width="75%" to img tag in content
        adding back in 75% default because many PPT require until better solution
        see 0000025475-10-000124,0001157523-10-004346, 0001171200-11-000106
        also see story B-05999 for 0001157523-10-004346        
    -->
		<xsl:if test="not(@width)">
			<xsl:attribute name="content-width">
				<xsl:value-of select="$imageWidthDefault"/>
			</xsl:attribute>
		</xsl:if>

		<xsl:if test="contains(@src, '.jpg')">
			<xsl:attribute name="content-type">
				<xsl:text>content-type:image/jpeg</xsl:text>
			</xsl:attribute>
		</xsl:if>

		<xsl:if test="@height">
			<xsl:choose>
				<xsl:when test="contains(@height, '%')">
					<xsl:attribute name="height">
						<xsl:value-of select="@height"/>
					</xsl:attribute>
					<xsl:attribute name="content-height">scale-to-fit</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="content-height">
						<xsl:value-of select="@height"/>
						<xsl:text>px</xsl:text>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="@border">
			<xsl:attribute name="border">
				<xsl:value-of select="@border"/>
				<xsl:text>px solid</xsl:text>
			</xsl:attribute>
		</xsl:if>
		<xsl:call-template name="process-common-attributes"/>
	</xsl:template>

	<xsl:template match="html:object">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="html:param"/>
	<xsl:template match="html:map"/>
	<xsl:template match="html:area"/>
	<xsl:template match="html:label"/>
	<xsl:template match="html:input"/>
	<xsl:template match="html:select"/>
	<xsl:template match="html:optgroup"/>
	<xsl:template match="html:option"/>
	<xsl:template match="html:textarea"/>
	<xsl:template match="html:legend"/>
	<xsl:template match="html:button"/>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Link
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template match="html:a">
		<fo:inline>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:a[@href]">
		<fo:basic-link xsl:use-attribute-sets="a-link">
			<xsl:call-template name="process-a-link"/>
		</fo:basic-link>
	</xsl:template>

	<xsl:template name="process-a-link">
		<xsl:call-template name="process-common-attributes"/>
		<xsl:choose>
			<xsl:when test="starts-with(@href,'#')">
				<xsl:attribute name="internal-destination">
					<xsl:value-of select="substring-after(@href,'#')"/>
				</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="external-destination">
					<xsl:text>url('</xsl:text>
					<xsl:value-of select="@href"/>
					<xsl:text>')</xsl:text>
				</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="@title">
			<xsl:attribute name="role">
				<xsl:value-of select="@title"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Ruby
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template match="html:ruby">
		<fo:inline-container alignment-baseline="central"
							 block-progression-dimension="1em"
							 text-indent="0pt"
							 last-line-end-indent="0pt"
							 start-indent="0pt"
							 end-indent="0pt"
							 text-align="center"
							 text-align-last="center">
			<xsl:call-template name="process-common-attributes"/>
			<fo:block font-size="50%"
					  wrap-option="no-wrap"
					  line-height="1"
					  space-before.conditionality="retain"
					  space-before="-1.1em"
					  space-after="0.1em"
					  role="html:rt">
				<xsl:for-each select="html:rt | html:rtc[1]/html:rt">
					<xsl:call-template name="process-common-attributes"/>
					<xsl:apply-templates/>
				</xsl:for-each>
			</fo:block>
			<fo:block wrap-option="no-wrap" line-height="1" role="html:rb">
				<xsl:for-each select="html:rb | html:rbc[1]/html:rb">
					<xsl:call-template name="process-common-attributes"/>
					<xsl:apply-templates/>
				</xsl:for-each>
			</fo:block>
			<xsl:if test="html:rtc[2]/html:rt">
				<fo:block font-size="50%"
						  wrap-option="no-wrap"
						  line-height="1"
						  space-before="0.1em"
						  space-after.conditionality="retain"
						  space-after="-1.1em"
						  role="html:rt">
					<xsl:for-each select="html:rt | html:rtc[2]/html:rt">
						<xsl:call-template name="process-common-attributes"/>
						<xsl:apply-templates/>
					</xsl:for-each>
				</fo:block>
			</xsl:if>
		</fo:inline-container>
	</xsl:template>

	<!-- This is applicable for PDF insider. -->
	<xsl:template name="createBookMark">
		<xsl:if test="$bookmark != ''">
			<fo:bookmark-tree>
				<xsl:call-template name="loadBookmarks" >
					<xsl:with-param name="remainingBookmark" select="$bookmark"/>
				</xsl:call-template>
			</fo:bookmark-tree>
		</xsl:if>
	</xsl:template>


	<!-- Recursive template -->
	<xsl:template name="loadBookmarks">

		<!-- Initial values here -->
		<xsl:param name="remainingBookmark"/>
		<!--
           <xsl:message>Remaining bookmark:<xsl:value-of select="$remainingBookmark"/>:</xsl:message>
      -->
		<!-- Check if there is anything for bookmark, in case it's there sub string before '%' -->
		<xsl:if test="contains($remainingBookmark, '%')">

			<!-- Call template to create the bookmark for this sub string -->
			<xsl:call-template name="buildBookmark">
				<xsl:with-param name="currentBookmark" select="substring-before($remainingBookmark, '%')"/>
			</xsl:call-template>

			<!-- Call the template as recursive till the last '%' in the string. -->
			<xsl:call-template name="loadBookmarks">
				<xsl:with-param name="remainingBookmark" select="substring-after($remainingBookmark, '%')"/>
			</xsl:call-template>

		</xsl:if>

	</xsl:template>


	<!-- Build bookmark code here the data here is in 'fsdfe|fwu' format -->
	<xsl:template name="buildBookmark">

		<xsl:param name="currentBookmark"/>
		<!--<xsl:message>Next bookmark link:<xsl:value-of select="$currentBookmark"/></xsl:message>    
       -->
		<xsl:if test="contains($currentBookmark, '|')">

			<fo:bookmark internal-destination="{substring-before($currentBookmark, '|')}">
				<fo:bookmark-title font-weight="bold">
					<xsl:value-of select="substring-after($currentBookmark, '|')" />
				</fo:bookmark-title>
			</fo:bookmark>

		</xsl:if>

	</xsl:template>

</xsl:stylesheet>
