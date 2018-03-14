<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!--

Copyright Antenna House, Inc. (http://www.antennahouse.com) 2001, 2002.

Since this stylesheet is originally developed by Antenna House to be used with XSL Formatter, it may not be compatible with another XSL-FO processors.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, provided that the above copyright notice(s) and this permission notice appear in all copies of the Software and that both the above copyright notice(s) and this permission notice appear in supporting documentation.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR HOLDERS INCLUDED IN THIS NOTICE BE LIABLE FOR ANY CLAIM, OR ANY SPECIAL INDIRECT OR CONSEQUENTIAL DAMAGES, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fo="http://www.w3.org/1999/XSL/Format"
				xmlns:html="http://www.w3.org/1999/xhtml"
				xmlns:pcl="http://xmlgraphics.apache.org/fop/extensions/pcl"
				exclude-result-prefixes="html">

	<xsl:output method="xml"
				version="1.0"
				encoding="UTF-8"
				indent="no"
				omit-xml-declaration="yes" />

	<!--======================================================================
		Parameters
	=======================================================================-->

	<xsl:param name="UseNewCssInlining"/>
	<xsl:param name="ShowDraftingNotes" select ="false()"/>

	<!-- page size -->
	<!--
	<xsl:param name="page-width" select="'auto'" />
	<xsl:param name="page-height" select="'auto'" />
	<fo:simple-page-master page-width="8.5in" page-height="11.0in" master-name="all-pages">
	-->

	<xsl:param name="page-master-reference" select="'Portrait'" />
	<xsl:param name="page-master-referencePortrait" select="'PortraitDualColumn'" />
	<xsl:param name="page-master-referencePortraitDualColumn" select="'PortraitDualColumn'" />
	<xsl:param name="page-master-referencePortraitDualColumnRightNoteMargin" select="'PortraitDualColumnRightNoteMargin'" />
	<xsl:param name="page-master-referencePortraitRightNoteMargin" select="'PortraitRightNoteMargin'" />
	<xsl:param name="page-master-referencePreFormattedPortrait" select="'PreFormattedPortrait'" />

	<xsl:param name="page-width" select="'8.5in'" />
	<xsl:param name="page-height" select="'11.0in'" />
	<xsl:param name="page-margin-top" select="'1in'" />
	<xsl:param name="page-margin-bottom" select="'1in'" />
	<xsl:param name="page-margin-left" select="'1in'" />
	<xsl:param name="page-margin-right" select="'1in'" />

	<!-- page header and footer -->
	<xsl:param name="page-header-margin" select="'0.5in'" />
	<xsl:param name="page-footer-margin" select="'0.5in'" />
	<xsl:param name="title-print-in-header" select="true()" />
	<xsl:param name="page-number-print-in-footer" select="true()" />

	<!-- multi column -->
	<xsl:param name="column-count" select="'1'" />
	<xsl:param name="column-gap" select="'12pt'" />

	<!-- writing-mode: lr-tb | rl-tb | tb-rl -->
	<xsl:param name="writing-mode" select="'lr-tb'" />

	<!-- text-align: justify | start -->
	<xsl:param name="text-align" select="'start'" />

	<!-- hyphenate: true | false -->
	<xsl:param name="hyphenate" select="'false'" />

	<xsl:param name="convertPixelsToMM" select="'3.779527559'" />

	<!-- page width -->
	<xsl:param name="usable-page-width-px" select="'504'" />
	<!-- This is the space between the two columns -->
	<xsl:param name="dualColumnGutter" select="20" />
	<xsl:param name="usable-dualColumn-width-px" select="number(number($usable-page-width-px) div 2) - number($dualColumnGutter)"/>

	<xsl:param name="outputLayoutMasterSet" select="false()"/>
	<xsl:param name="pageHeaderFooter"/>
	<xsl:param name="notesHeaderFooter"/>

	<xsl:param name="hrefLimit" />

	<xsl:param name="isRTF" select="false()" />
	<xsl:param name="isMSWord" select="false()" />
	<xsl:param name="isWordPerfect" select="false()" />
	<xsl:param name="isDuplexPrint" select="false()" />

	<xsl:param name="productView" />

	<!--======================================================================
		Attribute Sets
	=======================================================================-->

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		Root
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="root">
		<xsl:attribute name="writing-mode">
			<xsl:value-of select="$writing-mode" />
		</xsl:attribute>
		<xsl:attribute name="hyphenate">
			<xsl:value-of select="$hyphenate" />
		</xsl:attribute>
		<xsl:attribute name="text-align">
			<xsl:value-of select="$text-align" />
		</xsl:attribute>
		<!-- specified on fo:root to change the properties' initial values -->
	</xsl:attribute-set>

	<xsl:attribute-set name="page">
		<xsl:attribute name="page-width">
			<xsl:value-of select="$page-width"/>
		</xsl:attribute>
		<xsl:attribute name="page-height">
			<xsl:value-of select="$page-height"/>
		</xsl:attribute>
		<!-- specified on fo:simple-page-master -->
	</xsl:attribute-set>

	<xsl:attribute-set name="body">
		<!-- specified on fo:flow's only child fo:block -->
	</xsl:attribute-set>

	<xsl:attribute-set name="page-header">
		<!-- specified on (page-header)fo:static-content's only child fo:block -->
		<xsl:attribute name="font-size">
			<xsl:text>small</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="text-align">
			<xsl:text>center</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="page-footer">
		<!-- specified on (page-footer)fo:static-content's only child fo:block -->
		<xsl:attribute name="font-size">
			<xsl:text>small</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="text-align">
			<xsl:text>center</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		Block-level
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="h1">
		<xsl:attribute name="font-size">
			<xsl:text>2em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-weight">
			<xsl:text>bold</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-before">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="h2">
		<xsl:attribute name="font-size">
			<xsl:text>1.5em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-weight">
			<xsl:text>bold</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-before">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="h3">
		<xsl:attribute name="font-size">
			<xsl:text>1.17em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-weight">
			<xsl:text>bold</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-before">
			<xsl:text>0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="h4">
		<xsl:attribute name="font-size">
			<xsl:text>1em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-weight">
			<xsl:text>bold</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-before">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="h5">
		<xsl:attribute name="font-size">
			<xsl:text>0.83em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-weight">
			<xsl:text>bold</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-before">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="h6">
		<xsl:attribute name="font-size">
			<xsl:text>0.67em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="space-before">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-with-next.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="p">
		<xsl:attribute name="space-before">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>0.0em</xsl:text>
		</xsl:attribute>
		<!-- e.g.,
		<xsl:attribute name="text-indent">
			<xsl:text>1em</xsl:text>
		</xsl:attribute>-->
	</xsl:attribute-set>

	<xsl:attribute-set name="p-initial" use-attribute-sets="p">
		<!-- initial paragraph, preceded by h1..6 or div -->
		<!-- e.g.,
		<xsl:attribute name="text-indent">
			<xsl:text>0em</xsl:text>
		</xsl:attribute>	-->
	</xsl:attribute-set>

	<xsl:attribute-set name="p-initial-first" use-attribute-sets="p-initial">
		<!-- initial paragraph, first child of div, body or td -->
	</xsl:attribute-set>

	<xsl:attribute-set name="blockquote">
		<xsl:attribute name="start-indent">
			<xsl:text>24pt</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="end-indent">
			<xsl:text>24pt</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-before">
			<xsl:text>1em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>1em</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="pre">
		<xsl:attribute name="font-size">
			<xsl:text>0.83em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-family">
			<xsl:text>monospace</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="white-space">
			<xsl:text>pre</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-before">
			<xsl:text>1em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>1em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="white-space-collapse">
			<xsl:text>false</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="address">
		<xsl:attribute name="font-style">
			<xsl:text>italic</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="hr">
		<xsl:attribute name="leader-pattern">
			<xsl:text>rule</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="rule-thickness">
			<xsl:text>0.25pt</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		List
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="dl">
		<xsl:attribute name="space-before">
			<xsl:text>1em</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="space-after">
			<xsl:text>1em</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="dt">
		<xsl:attribute name="keep-with-next.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="keep-together.within-column">
			<xsl:text>always</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="dd">
		<xsl:attribute name="start-indent">
			<xsl:text>inherited-property-value(start-indent) + 24pt</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<!-- list-item-label format for each nesting level -->

	<xsl:param name="ul-label-1" select="'&#x2022;'" />

	<xsl:param name="ol-label-1" select="'1.'" />

  <xsl:param name="ua-label-1" select="'A.'" />

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	   Table
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="inside-table">
		<!-- prevent unwanted inheritance -->
		<xsl:attribute name="start-indent">
			<xsl:text>0pt</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="end-indent">
			<xsl:text>0pt</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="text-indent">
			<xsl:text>0pt</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="last-line-end-indent">
			<xsl:text>0pt</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="text-align">
			<xsl:text>start</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="table">
		<xsl:attribute name="table-layout">
			<xsl:text>fixed</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="border-collapse">
			<xsl:text>separate</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="border-spacing">
			<xsl:text>2px</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="border">
			<xsl:text>1px</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="table-caption" use-attribute-sets="inside-table">
		<xsl:attribute name="text-align">
			<xsl:text>center</xsl:text>
		</xsl:attribute>
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
		<xsl:attribute name="font-weight">
			<xsl:text>bold</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="text-align">
			<xsl:text>center</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="border">
			<xsl:text>1px</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="padding">
			<xsl:text>1px</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="td">
		<xsl:attribute name="border">
			<xsl:text>1px</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="padding">
			<xsl:text>1px</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		Inline-level
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="b">
		<xsl:attribute name="font-weight">
			<xsl:text>bold</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="strong">
		<xsl:attribute name="font-weight">
			<xsl:text>bold</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="strong-em">
		<xsl:attribute name="font-weight">
			<xsl:text>bold</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-style">
			<xsl:text>italic</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="i">
		<xsl:attribute name="font-style">
			<xsl:text>italic</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="cite">
		<xsl:attribute name="font-style">
			<xsl:text>italic</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="em">
		<xsl:attribute name="font-style">
			<xsl:text>italic</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="var">
		<xsl:attribute name="font-style">
			<xsl:text>italic</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="dfn">
		<xsl:attribute name="font-style">
			<xsl:text>italic</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="tt">
		<xsl:attribute name="font-family">
			<xsl:text>monospace</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="code">
		<xsl:attribute name="font-family">
			<xsl:text>monospace</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="kbd">
		<xsl:attribute name="font-family">
			<xsl:text>monospace</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="samp">
		<xsl:attribute name="font-family">
			<xsl:text>monospace</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="big">
		<xsl:attribute name="font-size">
			<xsl:text>larger</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="small">
		<xsl:attribute name="font-size">
			<xsl:text>smaller</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="sub">
		<xsl:attribute name="baseline-shift">
			<xsl:text>sub</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-size">
			<xsl:text>smaller</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="sup">
		<xsl:attribute name="baseline-shift">
			<xsl:text>super</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="font-size">
			<xsl:text>smaller</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="s">
		<xsl:attribute name="text-decoration">
			<xsl:text>line-through</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="strike">
		<xsl:attribute name="text-decoration">
			<xsl:text>line-through</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="del">
		<xsl:attribute name="text-decoration">
			<xsl:text>line-through</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="u">
		<xsl:attribute name="text-decoration">
			<xsl:text>underline</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="ins">
		<xsl:attribute name="text-decoration">
			<xsl:text>underline</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="abbr">
		<!-- e.g.,
		<xsl:attribute name="font-variant">small-caps</xsl:attribute>
		<xsl:attribute name="letter-spacing">0.1em</xsl:attribute>-->
	</xsl:attribute-set>

	<xsl:attribute-set name="acronym">
		<!-- e.g.,
		<xsl:attribute name="font-variant">small-caps</xsl:attribute>
		<xsl:attribute name="letter-spacing">0.1em</xsl:attribute>-->
	</xsl:attribute-set>

	<xsl:attribute-set name="q"/>
	<xsl:attribute-set name="q-nested"/>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		Image
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="img">
	</xsl:attribute-set>

	<xsl:attribute-set name="img-link">
		<xsl:attribute name="border">
			<xsl:text>2px solid</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		Link
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:attribute-set name="a-link">
		<!--
		Prism custom schema doesn't support this...
		<xsl:attribute name="text-decoration">underline</xsl:attribute>
		-->
		<xsl:attribute name="color">
			<xsl:text>blue</xsl:text>
		</xsl:attribute>
	</xsl:attribute-set>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		Superscript Link
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->
	<xsl:attribute-set name="sup-link">
		<xsl:attribute name="baseline-shift">
			<xsl:text>super</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="color">
			<xsl:text>blue</xsl:text>
		</xsl:attribute>
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
			<xsl:apply-templates/>
		</fo:root>
	</xsl:template>

	<xsl:template name="make-layout-master-set">
		<fo:layout-master-set>
			<fo:simple-page-master master-name="{$page-master-reference}"
								   xsl:use-attribute-sets="page">
				<fo:region-body margin-top="{$page-margin-top}"
								margin-right="{$page-margin-right}"
								margin-bottom="{$page-margin-bottom}"
								margin-left="{$page-margin-left}"
								column-count="{$column-count}"
								column-gap="{$column-gap}" />
				<xsl:choose>
					<xsl:when test="$writing-mode = 'tb-rl'">
						<fo:region-before extent="{$page-margin-right}"
										  precedence="true" />
						<fo:region-after extent="{$page-margin-left}"
										 precedence="true" />
						<fo:region-start region-name="page-header"
										 extent="{$page-margin-top}"
										 writing-mode="lr-tb"
										 display-align="before" />
						<fo:region-end region-name="page-footer"
									   extent="{$page-margin-bottom}"
									   writing-mode="lr-tb"
									   display-align="after" />
					</xsl:when>
					<xsl:when test="$writing-mode = 'rl-tb'">
						<fo:region-before region-name="page-header"
										  extent="{$page-margin-top}"
										  display-align="before" />
						<fo:region-after region-name="page-footer"
										 extent="{$page-margin-bottom}"
										 display-align="after" />
						<fo:region-start extent="{$page-margin-right}" />
						<fo:region-end extent="{$page-margin-left}" />
					</xsl:when>
					<xsl:otherwise>
						<!-- $writing-mode = 'lr-tb' -->
						<fo:region-before region-name="page-header"
										  extent="{$page-margin-top}"
										  display-align="before" />
						<fo:region-after region-name="page-footer"
										 extent="{$page-margin-bottom}"
										 display-align="after" />
						<fo:region-start extent="{$page-margin-left}" />
						<fo:region-end extent="{$page-margin-bottom}" />
					</xsl:otherwise>
				</xsl:choose>
			</fo:simple-page-master>
		</fo:layout-master-set>
	</xsl:template>

	<xsl:template match="html:head | html:script"/>

	<xsl:template match="html:div[@id='co_wrapper'] | html:div[@id='co_pagesWithSearchTermWrapper']" priority="2">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="html:div[@id='co_wrapper']/html:div[contains(@id, 'co_document_')] | html:div[@id='co_pagesWithSearchTermWrapper']/html:div[contains(@id, 'co_document_')] | html:div[@id='co_document'] | html:div[@id='co_notesPage'] | html:div[@id='co_doc_coverpage'] | html:div[html:ul[@id='co_docTocOverlay_list']]" priority="2">
		<xsl:call-template name="buildPageSequence" />
	</xsl:template>

	<xsl:template name="buildPageSequence">
		<fo:page-sequence>
			<xsl:attribute name="master-reference">
				<xsl:choose>
					<xsl:when test="contains(@class, 'co_preformattedDocument')">
						<xsl:value-of select="$page-master-referencePreFormattedPortrait" />
					</xsl:when>
					<xsl:when test="@id ='co_notesPage'">
						<xsl:text>Portrait</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$page-master-reference" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="not(contains(@id, 'co_document_')) or position() = 1">
				<xsl:attribute name="initial-page-number">
					<xsl:text>1</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="force-page-count">
				<xsl:text>no-force</xsl:text>
			</xsl:attribute>
			<xsl:if test="$isDuplexPrint">
				<xsl:attribute name="pcl:duplex-front">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<fo:title>
				<xsl:value-of select="/html:html/html:head/html:title" />
			</fo:title>

			<xsl:choose>
				<xsl:when test="@id ='co_notesPage' and $notesHeaderFooter">
					<xsl:value-of select="$notesHeaderFooter" disable-output-escaping="yes" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="$pageHeaderFooter">
							<xsl:value-of select="$pageHeaderFooter" disable-output-escaping="yes" />
						</xsl:when>
						<xsl:otherwise>
							<fo:static-content flow-name="page-header">
								<fo:block space-before.conditionality="retain"
										  space-before="{$page-header-margin}"
										  xsl:use-attribute-sets="page-header">
									<xsl:if test="$title-print-in-header">
										<xsl:value-of select="/html:html/html:head/html:title" />
									</xsl:if>
								</fo:block>
							</fo:static-content>
							<fo:static-content flow-name="page-footer">
								<fo:block space-after.conditionality="retain"
										  space-after="{$page-footer-margin}"
										  xsl:use-attribute-sets="page-footer">
									<xsl:if test="$page-number-print-in-footer">
										<xsl:text>- </xsl:text>
										<fo:page-number />
										<xsl:text> -</xsl:text>
									</xsl:if>
								</fo:block>
							</fo:static-content>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<fo:flow flow-name="xsl-region-body">
				<fo:block xsl:use-attribute-sets="body">
					<xsl:call-template name="process-common-attributes" />
					<xsl:apply-templates />
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

	<xsl:template name="process-common-id">
		<xsl:param name="contextNode" select="." />
		<xsl:param name="idPostfix" select="''" />

		<xsl:choose>
			<xsl:when test="$contextNode/@id">
				<xsl:attribute name="id">
					<xsl:value-of select="$contextNode/@id" />
					<xsl:copy-of select="$idPostfix" />
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="$contextNode/self::html:a/@name">
				<xsl:attribute name="id">
					<xsl:value-of select="$contextNode/@name" />
					<xsl:copy-of select="$idPostfix" />
				</xsl:attribute>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="process-id">
		<xsl:param name="contextNode" select="." />
		<xsl:param name="idPostfix" select="''" />

		<xsl:call-template name="process-common-id">
			<xsl:with-param name="contextNode" select="$contextNode" />
			<xsl:with-param name="idPostfix" select="$idPostfix" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="process-common-attributes">
		<xsl:param name="contextNode" select="." />
		<xsl:param name="idPostfix" select="''" />
		<xsl:choose>
			<xsl:when test="$contextNode/@xml:lang">
				<xsl:attribute name="xml:lang">
					<xsl:value-of select="$contextNode/@xml:lang" />
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="$contextNode/@lang">
				<xsl:attribute name="xml:lang">
					<xsl:value-of select="$contextNode/@lang" />
				</xsl:attribute>
			</xsl:when>
		</xsl:choose>

		<xsl:call-template name="process-id">
			<xsl:with-param name="contextNode" select="$contextNode" />
			<xsl:with-param name="idPostfix" select="$idPostfix" />
		</xsl:call-template>

		<xsl:if test="$contextNode/@align">
			<xsl:choose>
				<xsl:when test="$contextNode/self::html:caption">
				</xsl:when>
				<xsl:when test="$contextNode/self::html:img or $contextNode/self::html:object">
					<xsl:if test="$contextNode/@align = 'bottom' or $contextNode/@align = 'middle' or $contextNode/@align = 'top' or contains(@class, 'co_trademarkImageScanned')">
						<xsl:attribute name="vertical-align">
							<xsl:value-of select="$contextNode/@align" />
						</xsl:attribute>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="process-cell-align">
						<xsl:with-param name="align" select="$contextNode/@align" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="$contextNode/@valign">
			<xsl:call-template name="process-cell-valign">
				<xsl:with-param name="valign" select="$contextNode/@valign" />
			</xsl:call-template>
		</xsl:if>

		<xsl:if test="$contextNode/@style">
			<xsl:call-template name="process-style">
				<xsl:with-param name="style" select="$contextNode/@style" />
			</xsl:call-template>
		</xsl:if>

		<xsl:if test="$contextNode/@break-after">
			<xsl:attribute name="break-after">
				<xsl:value-of select="$contextNode/@break-after" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<!--For testing purposes to test the named template getValueOfStyleFromStyleAttribute-->
	<xsl:template match="listStyleType">
		<xsl:call-template name="getValueOfStyleFromStyleAttribute">
			<xsl:with-param name="styleAttribute" select="string(@style)" />
			<xsl:with-param name="styleName" select="'list-style-type'" />
		</xsl:call-template>
	</xsl:template>

	<!--Gets the value of a style that is within the style attribute -->
	<xsl:template name="getValueOfStyleFromStyleAttribute">
		<xsl:param name="styleAttribute" />
		<xsl:param name="styleName" />
		<!-- e.g., style="text-align: center; color: red"
		 converted to text-align="center" color="red" -->
		<xsl:variable name="name"
		              select="normalize-space(substring-before($styleAttribute, ':'))" />
		<xsl:if test="$name">
			<xsl:variable name="value-and-rest"
			              select="normalize-space(substring-after($styleAttribute, ':'))" />
			<xsl:variable name="value">
				<xsl:choose>
					<xsl:when test="contains($value-and-rest, ';')">
						<xsl:value-of select="normalize-space(substring-before(
								  $value-and-rest, ';'))" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$value-and-rest" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$name = $styleName">
					<xsl:value-of select="$value" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="rest"
					              select="normalize-space(substring-after($styleAttribute, ';'))" />
					<xsl:if test="$rest">
						<xsl:call-template name="getValueOfStyleFromStyleAttribute">
							<xsl:with-param name="styleAttribute" select="$rest" />
							<xsl:with-param name="styleName" select="$styleName" />
						</xsl:call-template>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="process-style">
		<xsl:param name="style" />
		<!-- e.g., style="text-align: center; color: red"
		 converted to text-align="center" color="red" -->
		<xsl:variable name="name"
					  select="normalize-space(substring-before($style, ':'))" />
		<xsl:if test="$name">
			<xsl:variable name="value-and-rest"
						  select="normalize-space(substring-after($style, ':'))" />
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
				<xsl:when test="$name = 'width' and (self::html:col or self::html:colgroup)">
					<xsl:attribute name="column-width">
						<xsl:value-of select="$value" />
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="$name = 'width' and self::html:hr">
					<xsl:attribute name="leader-length">
						<xsl:value-of select="$value" />
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="$name = 'vertical-align' and (
								self::html:table or self::html:caption or
								self::html:thead or self::html:tfoot or
								self::html:tbody or self::html:colgroup or
								self::html:col or self::html:tr or
								self::html:th or self::html:td)">
					<xsl:choose>
						<xsl:when test="$value = 'top'">
							<xsl:attribute name="display-align">
								<xsl:text>before</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="$value = 'bottom'">
							<xsl:attribute name="display-align">
								<xsl:text>after</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="$value = 'middle'">
							<xsl:attribute name="display-align">
								<xsl:text>center</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="display-align">
								<xsl:text>auto</xsl:text>
							</xsl:attribute>
							<xsl:attribute name="relative-align">
								<xsl:text>baseline</xsl:text>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="($name = 'width') and self::html:img">
					<xsl:call-template name="img-width-and-height">
						<xsl:with-param name="value" select="$value" />
						<xsl:with-param name="isWidth" select="true()" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="($name = 'height') and self::html:img">
					<xsl:call-template name="img-width-and-height">
						<xsl:with-param name="value" select="$value" />
						<xsl:with-param name="isWidth" select="false()" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="($name = 'max-width') and self::html:img">
					<xsl:call-template name="img-max-width">
						<xsl:with-param name="maxWidth" select="$value" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="($name = 'max-height') and self::html:img">
					<xsl:call-template name="img-max-height">
						<xsl:with-param name="maxHeight" select="$value" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="($name = 'height' or $name = 'width') and not(self::html:img)" />
				<xsl:when test="($name = 'border-top' or $name = 'border-right' or $name = 'border-bottom' or $name = 'border-left' or $name = 'border-color') and not(self::html:td or self::html:th)" />

				<!-- START: new styles we are now ducking in FO xsl - used to be in the C# css parser code -->
				<xsl:when test="$name = 'border-width' and $value = '0px'" />
				<xsl:when test="$name = 'border-style' and contains(@style, 'border-width:0px')" />
				<xsl:when test="$name = 'Clear'" />
				<xsl:when test="$name = 'float'" />
				<xsl:when test="$name = 'cursor'" />
				<xsl:when test="$name = 'vertical-align'" />
				<xsl:when test="$name = 'baseline'" />
				<xsl:when test="$name = 'box-shadow'" />
				<!-- END: new styles we are now ducking in FO xsl - used to be in the C# css parser code -->

				<!-- START: new styles we change from one attribute to another in FO xsl - used to be in the C# css parser code -->
				<xsl:when test="$name = 'margin-top'">
					<xsl:attribute name="margin-top">
						<xsl:value-of select="$value" />
					</xsl:attribute>
					<xsl:attribute name="space-before">
						<xsl:value-of select="$value" />
					</xsl:attribute>
				</xsl:when>
				<!-- END: new styles we change from one attribute to another in FO xsl - used to be in the C# css parser code -->
				<xsl:when test="$name = 'background-position-top'">
					<xsl:attribute name="background-position-vertical">
						<xsl:value-of select="$value" />
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="$name = 'background-position-left'">
					<xsl:attribute name="background-position-horizontal">
						<xsl:value-of select="$value" />
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="$name = 'list-style-type'" />
				<xsl:when test="$name = 'list-style-image'" />
				<xsl:when test="$name = 'list-style-position'" />
				<xsl:when test="$name = 'list-style'" />
				<xsl:when test="$name = 'display'" />
				<xsl:when test="$name = 'margin-left'">
					<xsl:variable name="inline">
						<xsl:call-template name="isInlineElement" />
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="$inline = 'true'">
							<xsl:attribute name="space-start">
								<xsl:value-of select="$value" />
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="{$name}">
								<xsl:value-of select="$value" />
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$name = 'margin-right'">
					<xsl:variable name="inline">
						<xsl:call-template name="isInlineElement" />
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="$inline = 'true'">
							<xsl:attribute name="space-end">
								<xsl:value-of select="$value" />
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="{$name}">
								<xsl:value-of select="$value" />
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$name = 'padding-left'">
					<xsl:variable name="inline">
						<xsl:call-template name="isInlineElement" />
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="$inline = 'true'">
							<xsl:attribute name="{$name}">
								<xsl:value-of select="$value" />
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="$UseNewCssInlining = 'true'">
									<xsl:attribute name="padding-left">
										<xsl:value-of select="$value" />
									</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="margin-left">
										<xsl:value-of select="$value" />
									</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$name = 'padding-right'">
					<xsl:variable name="inline">
						<xsl:call-template name="isInlineElement" />
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="$inline = 'true'">
							<xsl:attribute name="{$name}">
								<xsl:value-of select="$value" />
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="$UseNewCssInlining = 'true'">
									<xsl:attribute name="padding-right">
										<xsl:value-of select="$value" />
									</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="margin-right">
										<xsl:value-of select="$value" />
									</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="{$name}">
						<xsl:value-of select="$value" />
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:variable name="rest" select="normalize-space(substring-after($style, ';'))" />
		<xsl:if test="$rest">
			<xsl:call-template name="process-style">
				<xsl:with-param name="style" select="$rest" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="html:testInlineElement">
		<xsl:variable name="result">
			<xsl:call-template name="isInlineElement">
				<xsl:with-param name="element" select="*[1]"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="$result"/>
	</xsl:template>
	
	<xsl:template name="isInlineElement">
		<xsl:param name="element" select="."/>
		<xsl:variable name="elementName">;<xsl:value-of select="local-name($element)" />;</xsl:variable>
		<xsl:choose>
			<xsl:when test="$element[contains(@style, 'display:block')]">
				<xsl:value-of select="'false'"/>
			</xsl:when>
			<xsl:when test="$element[contains(@style, 'display:inline')]">
				<xsl:value-of select="'true'"/>
			</xsl:when>
			<xsl:when test="contains('&inlineElements;', $elementName)">
				<xsl:value-of select="'true'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'false'"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		Block-level
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template match="html:*[contains(@style, 'display:none')]" priority="1"/>

	<xsl:template match="html:*[contains(@style, 'display:inline')]" priority="1">
		<fo:inline>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
		<xsl:if test="@class and (contains(@class, 'co_searchTerm') or contains(@class, 'co_hl'))">
			<fo:inline background-color="#FFFFFF" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="html:input[@id = 'co_keyCiteFlagPlaceHolder']" priority="2"/>

	<xsl:template match="html:*[contains(@style, 'display:block') and contains('&inlineElements;', local-name())]" priority="1">
		<fo:block>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:ul[html:li[contains(@style, 'display:inline')]] | html:ol[html:li[contains(@style, 'display:inline')]]" priority="1">
		<fo:block>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>
	
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

	<xsl:template match="html:p">
		<fo:block xsl:use-attribute-sets="p">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<!-- initial paragraph, preceded by h1..6 or div -->
	<xsl:template match="html:p[preceding-sibling::*[1][self::html:h1 or self::html:h2 or self::html:h3 or self::html:h4 or self::html:h5 or self::html:h6 or self::html:div]]">
		<fo:block xsl:use-attribute-sets="p-initial">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:block>
	</xsl:template>

	<!-- initial paragraph, first child of div, body or td -->
	<xsl:template match="html:p[not(preceding-sibling::*) and (parent::html:div or parent::html:body or parent::html:td)]">
		<fo:block xsl:use-attribute-sets="p-initial-first">
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
		<xsl:for-each select="node()">
			<xsl:choose>
				<!-- remove leading CR/LF/CRLF char -->
				<xsl:when test="position() = 1 and self::text()">
					<xsl:choose>
						<!-- LF -->
						<xsl:when test="starts-with(., '&#xA;')">
							<xsl:value-of select="substring(., 2)"/>
						</xsl:when>
						<!-- CRLF -->
						<xsl:when test="starts-with(., '&#xD;&#xA;')">
							<xsl:value-of select="substring(., 3)"/>
						</xsl:when>
						<!-- CR -->
						<xsl:when test="starts-with(., '&#xD;')">
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

	<xsl:template match="html:hr" name="defaultHrStyle">
		<fo:block>
			<fo:leader xsl:use-attribute-sets="hr">
				<xsl:call-template name="process-common-attributes"/>
			</fo:leader>
		</fo:block>
	</xsl:template>

	<!-- =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		DraftingNotes section
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- -->
	<xsl:template match="html:hr[@class='&draftingNotesDeliveryLineBreak;']" priority="1">
		<xsl:if test="$ShowDraftingNotes">
			<xsl:choose>
				<xsl:when test="ancestor::html:div[contains(@class, '&khContent;')]">
					<xsl:call-template name="renderHrLine" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="defaultHrStyle"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="html:div[@class='&draftingNotesDelivery;']">
		<xsl:if test="$ShowDraftingNotes">
			<xsl:call-template name="htmlDiv" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="renderHrLine">
		<xsl:call-template name="defaultHrStyle"/>
	</xsl:template>

	<xsl:template match="html:div" name="htmlDiv">
		<!-- normal block -->
		<fo:block>
			<xsl:call-template name="addDualColumnInstructions"/>
			<xsl:call-template name="process-common-attributes"/>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>

	<xsl:template name="addDualColumnInstructions">
		<xsl:if test="contains($page-master-reference, 'DualColumn') and contains(@class,'co_imageBlock') and .//html:img[number(@width) &gt; number($usable-dualColumn-width-px)] and not(ancestor::html:td)">
			<xsl:attribute name="breakDualColumn">true</xsl:attribute>
		</xsl:if>
	</xsl:template>
	<xsl:template name="need-block-container">
		<xsl:choose>
			<xsl:when test="@dir">
				<xsl:text>true</xsl:text>
			</xsl:when>
			<xsl:when test="@style">
				<xsl:variable name="s" select="concat(';', translate(normalize-space(@style), ' ', ''))"/>
				<xsl:choose>
					<xsl:when test="contains($s, ';width:') or contains($s, ';height:') or contains($s, ';position:absolute') or contains($s, ';position:fixed') or contains($s, ';writing-mode:')">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>false</xsl:text>
			</xsl:otherwise>
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

	<xsl:template match="html:ul/html:div" />
	
	<xsl:template match="html:ul/html:div" mode="list">
		<xsl:call-template name="htmlDiv" />
	</xsl:template>

	<xsl:template match="html:ul">
		<xsl:if test="html:div">
			<xsl:apply-templates select="html:div" mode="list" />
		</xsl:if>
		<xsl:variable name="content">
			<fo:list-block><xsl:call-template name="process-common-attributes-and-children"/></fo:list-block>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0 ">
				<xsl:copy-of select="$content"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="html:ol">
		<xsl:variable name="content">
			<fo:list-block><xsl:call-template name="process-common-attributes-and-children"/></fo:list-block>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0 ">
				<xsl:copy-of select="$content"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="html:ul/html:li[contains(@style, 'display:block')]" priority="2">
		<xsl:call-template name="normal-ul-li"/>
	</xsl:template>

	<xsl:template match="html:ol/html:li[contains(@style, 'display:block')]" priority="2">
		<xsl:call-template name="normal-ol-li"/>
	</xsl:template>

	<xsl:template match="html:ul/html:li" name="normal-ul-li">
		<fo:list-item >
			<xsl:call-template name="process-li">
				<xsl:with-param name="isOrderedList" select="false()"/>
			</xsl:call-template>
		</fo:list-item>
	</xsl:template>

	<xsl:template match="html:ol/html:li" name="normal-ol-li">
		<fo:list-item >
			<xsl:call-template name="process-li"/>
		</fo:list-item>
	</xsl:template>

	<!--
		12/19/2013: Tim S. - Fairly certain the styling on this is incorrect. list-item-label attributes should just be end-indent="label-end()
		And list-item-body should be start-indent="body-start(). Currently the bullets are aligning to the right. This is overwritten in WLN.
	-->
	<xsl:template name="process-li">
		<xsl:param name="isOrderedList" select="true()"/>
		<xsl:call-template name="process-common-attributes"/>
		<fo:list-item-label text-align="end" wrap-option="no-wrap">
			<fo:block>
				<xsl:variable name="listItemType">
					<xsl:variable name="nodeListItemType">
						<xsl:call-template name="getValueOfStyleFromStyleAttribute">
							<xsl:with-param name="styleAttribute" select="@style"/>
							<xsl:with-param name="styleName" select="'list-style-type'"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="string-length($nodeListItemType) &gt; 0">
							<xsl:value-of select="$nodeListItemType"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="$isOrderedList">
									<xsl:call-template name="getValueOfStyleFromStyleAttribute">
										<xsl:with-param name="styleAttribute" select="ancestor::html:ol[1]/@style"/>
										<xsl:with-param name="styleName" select="'list-style-type'"/>
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="getValueOfStyleFromStyleAttribute">
										<xsl:with-param name="styleAttribute" select="ancestor::html:ul[1]/@style"/>
										<xsl:with-param name="styleName" select="'list-style-type'"/>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$listItemType = 'none'">
						<fo:inline>&#160;</fo:inline>
					</xsl:when>
					<xsl:when test="$listItemType = 'disc'">
						<fo:inline>&#8226;</fo:inline>
					</xsl:when>
					<xsl:when test="$listItemType = 'circle'">
						<fo:inline>&#x006F;</fo:inline>
					</xsl:when>
					<xsl:otherwise>
						<fo:inline>
							<xsl:choose>
								<xsl:when test="$isOrderedList">
									<xsl:number format="{$ol-label-1}"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$ul-label-1"/>
								</xsl:otherwise>
							</xsl:choose>
						</fo:inline>
					</xsl:otherwise>
				</xsl:choose>
			</fo:block>
		</fo:list-item-label>
		<fo:list-item-body>
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

	<!-- Mark Nordstrom - Bug #735738 - 2/17/2015 - sample document DRUGDEX-EV 2433 (Guid=I38cf2e2290af11dba168de33dfaf5627) -->
	<!-- Whitespace nodes inside table rows (tr) create a column that should not be in the table -->
	<!--<xsl:strip-space elements="html:tr"/>-->

	<!--Use leader element for leader characters-->
	<xsl:template match="html:div[@class and contains(@class, '&leaderTableFullWidthClass;') and not(contains(@class, '&leaderHiddenClass;'))]">
		<fo:block text-align-last="justify">
			
			<xsl:if test="not(contains(ancestor::html:td[1]/@class, '&alignHorizontalRightClass;'))">
				<xsl:apply-templates select="self::node()/html:div/node()"/>	
			</xsl:if>
			
			<fo:leader leader-pattern-width="2px">
				<xsl:attribute name="leader-pattern">
					<xsl:choose>
						<xsl:when test=".//html:div[@class and contains(@class, '&leaderUnderlineClass;')]">
							<xsl:value-of select="'rule'"/>
						</xsl:when>
						<xsl:when test=".//html:div[@class and contains(@class, '&leaderDashesClass;')]">
							<xsl:value-of select="'use-content'"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="'dots'"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test=".//html:div/@class = '&leaderDashesClass;'">
					<xsl:text> _ </xsl:text>
				</xsl:if>
			</fo:leader>
			
			<xsl:if test="contains(ancestor::html:td[1]/@class, '&alignHorizontalRightClass;')">
				<xsl:apply-templates select="self::node()/html:div/node()"/>
			</xsl:if>
			
		</fo:block>
	</xsl:template>

	<xsl:template match="html:table[not(descendant::html:td) and not(descendant::html:th)]" priority="1"/>

	<xsl:template match="html:table">
		<xsl:variable name="contents">
			<fo:block>
			<fo:table xsl:use-attribute-sets="table">
				<xsl:call-template name="process-table" />
			</fo:table>
			</fo:block>
		</xsl:variable>
		<xsl:copy-of select="$contents"/>
	</xsl:template>

	<xsl:template name="make-table-caption">
		<xsl:if test="html:caption/@align">
			<xsl:attribute name="caption-side">
				<xsl:value-of select="html:caption/@align"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:apply-templates select="html:caption"/>
	</xsl:template>

	<xsl:template name="process-table">
		<xsl:if test="@width">
			<xsl:attribute name="inline-progression-dimension">
				<xsl:choose>
					<xsl:when test="contains(@width, '%')">
						<xsl:value-of select="@width"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat(@width, 'px')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="@border or @frame">
			<xsl:choose>
				<xsl:when test="@border &gt; 0">
					<xsl:attribute name="border">
						<xsl:value-of select="concat(@border, 'px')"/>
					</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="@border = '0' or @frame = 'void'">
					<xsl:attribute name="border-style">
						<xsl:text>hidden</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'above'">
					<xsl:attribute name="border-style">
						<xsl:text>outset hidden hidden hidden</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'below'">
					<xsl:attribute name="border-style">
						<xsl:text>hidden hidden outset hidden</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'hsides'">
					<xsl:attribute name="border-style">
						<xsl:text>outset hidden</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'vsides'">
					<xsl:attribute name="border-style">
						<xsl:text>hidden outset</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'lhs'">
					<xsl:attribute name="border-style">
						<xsl:text>hidden hidden hidden outset</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@frame = 'rhs'">
					<xsl:attribute name="border-style">
						<xsl:text>hidden outset hidden hidden</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="border-style">
						<xsl:text>outset</xsl:text>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="@cellspacing">
			<xsl:attribute name="border-spacing">
				<xsl:value-of select="concat(@cellspacing, 'px')"/>
			</xsl:attribute>
			<xsl:attribute name="border-collapse">
				<xsl:text>separate</xsl:text>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="@rules and (@rules = 'groups' or @rules = 'rows' or @rules = 'cols' or @rules = 'all' and (not(@border or @frame) or @border = '0' or @frame and not(@frame = 'box' or @frame = 'border')))">
			<xsl:attribute name="border-collapse">
				<xsl:text>collapse</xsl:text>
			</xsl:attribute>
			<xsl:if test="not(@border or @frame)">
				<xsl:attribute name="border-style">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
		</xsl:if>
		<xsl:call-template name="process-common-attributes"/>
		<xsl:variable name="tableWidths">
			<xsl:call-template name="getTableWidths">
				<xsl:with-param name="table" select="." />
				<xsl:with-param name="columnData">
					<xsl:call-template name="getColumnData">
						<xsl:with-param name="table" select="." />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="getColGroupElements">
			<xsl:with-param name="tableWidths" select="$tableWidths" />
		</xsl:call-template>
		<xsl:apply-templates select="html:thead"/>
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
		<xsl:apply-templates select="html:tfoot"/>
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
			<xsl:attribute name="border">
				<xsl:text>1px solid</xsl:text>
			</xsl:attribute>
		</xsl:if>
		<xsl:call-template name="process-common-attributes-and-children"/>
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
			<xsl:attribute name="border">
				<xsl:text>1px solid</xsl:text>
			</xsl:attribute>
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
			<xsl:attribute name="border">
				<xsl:text>1px solid</xsl:text>
			</xsl:attribute>
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
		<xsl:if test="@rowspan and contains(ancestor::html:table[1]/@id, 'co_doc_coverpageTable')">
			<xsl:attribute name="number-rows-spanned">
				<xsl:value-of select="@rowspan"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:for-each select="ancestor::html:table[1]">
			<xsl:if test="(@border or @rules) and (@rules = 'all' or not(@rules) and not(@border = '0'))">
				<xsl:attribute name="border-style">
					<xsl:text>inset</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@cellpadding">
				<xsl:attribute name="padding">
					<xsl:choose>
						<xsl:when test="contains(@cellpadding, '%')">
							<xsl:value-of select="@cellpadding"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat(@cellpadding, 'px')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
		</xsl:for-each>
		<xsl:if test="not(@align or ../@align or ../parent::*[self::html:thead or self::html:tfoot or self::html:tbody]/@align)
				and ancestor::html:table[1]/*[self::html:col or self::html:colgroup]/descendant-or-self::*/@align">
			<xsl:attribute name="text-align">
				<xsl:text>from-table-column()</xsl:text>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="not(@valign or ../@valign or ../parent::*[self::html:thead or self::html:tfoot or self::html:tbody]/@valign)
				and ancestor::html:table[1]/*[self::html:col or self::html:colgroup]/descendant-or-self::*/@valign">
			<xsl:attribute name="display-align">
				<xsl:text>from-table-column()</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="relative-align">
				<xsl:text>from-table-column()</xsl:text>
			</xsl:attribute>
		</xsl:if>
		<xsl:call-template name="process-common-attributes"/>
		<fo:block>
			<xsl:call-template name="process-cell" />
		</fo:block>
	</xsl:template>

	<xsl:template name="process-cell">
		<xsl:choose>
			<xsl:when test="contains(ancestor::html:table[1]/@class,'co_ampexHorizRule') and string-length(normalize-space(self::node())) = 0">
				<fo:block>
					<xsl:text>&#160;</xsl:text>
					<xsl:apply-templates/>
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="process-col-width">
		<xsl:param name="width"/>
		<xsl:if test="$width and not($width = '0*')">
			<xsl:attribute name="column-width">
				<xsl:choose>
					<xsl:when test="contains($width, '*')">
						<xsl:text>proportional-column-width(</xsl:text>
						<xsl:value-of select="substring-before($width, '*')"/>
						<xsl:text>)</xsl:text>
					</xsl:when>
					<xsl:when test="contains($width, 'NaN')">
						<xsl:value-of select="'100px'"/><!-- We're having NaN come through a lot, we need to track this down but this will fix the larger issue for now. -->
					</xsl:when>
					<xsl:when test="contains($width, '%')">
						<xsl:value-of select="$width"/>
					</xsl:when>
					<xsl:when test="contains($width, 'px')">
						<xsl:value-of select="$width"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($width, 'px')"/>
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
					<xsl:when test="$valign = 'middle'">
						<xsl:text>center</xsl:text>
					</xsl:when>
					<xsl:when test="$valign = 'bottom'">
						<xsl:text>after</xsl:text>
					</xsl:when>
					<xsl:when test="$valign = 'baseline'">
						<xsl:text>auto</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>before</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$valign = 'baseline'">
				<xsl:attribute name="relative-align">
					<xsl:text>baseline</xsl:text>
				</xsl:attribute>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		Named templates for colgroups
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template name="getColGroupElements">
		<xsl:param name="tableWidths" />

		<xsl:variable name="width">
			<xsl:variable name="tempWidth" select="substring-before($tableWidths, '|')" />
			<xsl:choose>
				<xsl:when test="string-length($tempWidth) &gt; 0">
					<xsl:value-of select="$tempWidth"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$tableWidths"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<fo:table-column xsl:use-attribute-sets="table-column">
			<xsl:call-template name="process-col-width">
				<xsl:with-param name="width" select="$width"/>
			</xsl:call-template>
		</fo:table-column>

		<xsl:variable name="remainingTableWidths" select="substring-after($tableWidths, '|')" />

		<xsl:if test="string-length($remainingTableWidths) &gt; 0">
			<xsl:call-template name="getColGroupElements">
				<xsl:with-param name="tableWidths" select="$remainingTableWidths" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getColumnData">
		<xsl:param name="table" />

		<!-- Dig through the table and get the data -->
		<xsl:call-template name="compileColumnData">
			<xsl:with-param name="table" select="$table" />
			<xsl:with-param name="totalColumns">
				<xsl:choose>
					<xsl:when test="$table/html:tr">
						<xsl:call-template name="countColumns">
							<xsl:with-param name="row" select="$table/html:tr[1]" />
							<xsl:with-param name="currentCell" select="1" />
							<xsl:with-param name="count" select="0" />
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$table/html:thead/html:tr">
						<xsl:call-template name="countColumns">
							<xsl:with-param name="row" select="$table/html:thead/html:tr[1]" />
							<xsl:with-param name="currentCell" select="1" />
							<xsl:with-param name="count" select="0" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="countColumns">
							<xsl:with-param name="row" select="$table/node()/html:tr[1]" />
							<xsl:with-param name="currentCell" select="1" />
							<xsl:with-param name="count" select="0" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="currentColumn" select="1" />
			<xsl:with-param name="countChars" select="false()" />
			<xsl:with-param name="columnData" select="''" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="countColumnsInColumnData">
		<xsl:param name="columnData" />
		<xsl:param name="total" />

		<xsl:choose>
			<xsl:when test="contains($columnData, '|')">

				<xsl:call-template name="countColumnsInColumnData">
					<xsl:with-param name="columnData" select="substring-after($columnData, '|')" />
					<xsl:with-param name="total" select="number($total) + 1" />
				</xsl:call-template>

			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="number($total) + 1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="addColumnWidthsInColumnData">
		<xsl:param name="columnData" />
		<xsl:param name="total" />

		<xsl:choose>
			<xsl:when test="string-length($columnData) &gt; 0">

				<xsl:variable name="width">
					<xsl:variable name="tempWidth" select="substring-before($columnData, '|')" />
					<xsl:choose>
						<xsl:when test="string-length($tempWidth) &gt; 0">
							<xsl:value-of select="number(translate($tempWidth, 'pxPXemsEMSptPT%', ''))"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="number(translate($columnData, 'pxPXemsEMSptPT%', ''))"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:call-template name="addColumnWidthsInColumnData">
					<xsl:with-param name="columnData" select="substring-after($columnData, '|')" />
					<xsl:with-param name="total" select="number($total) + number($width)" />
				</xsl:call-template>

			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="number($total)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getTableWidths">
		<xsl:param name="table" />
		<xsl:param name="columnData" />

		<xsl:variable name="pixelWidth">
			<xsl:variable name="parentTable" select="$table/ancestor::html:table[1]" />

			<xsl:choose>
				<xsl:when test="$parentTable">
					<!-- If there is a parent table, first we need to get the column widths for it -->
					<xsl:variable name="parentWidths">
						<xsl:call-template name="getTableWidths">
							<xsl:with-param name="table" select="$parentTable" />
							<xsl:with-param name="columnData">
								<xsl:call-template name="getColumnData">
									<xsl:with-param name="table" select="$parentTable" />
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:variable>
					<!-- Get the index for the parent column -->
					<xsl:variable name="parentCellIndex">
						<xsl:call-template name="findParentCellIndex">
							<xsl:with-param name="tr" select="$table/ancestor::html:tr[1]"/>
							<xsl:with-param name="cell" select="$table/ancestor::node()[self::html:td or self::html:th][1]" />
							<xsl:with-param name="currentCell" select="1" />
							<xsl:with-param name="currentColumn" select="1" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="number($parentCellIndex) = 0">
							<!-- Couldn't find a parent index, default width -->
							<xsl:value-of select="number($usable-page-width-px)"/>
						</xsl:when>
						<xsl:otherwise>
							<!-- Find the width for the column from the list using the index -->
							<xsl:call-template name="findParentCellWidthFromIndex">
								<xsl:with-param name="parentWidths" select="$parentWidths" />
								<xsl:with-param name="index" select="$parentCellIndex" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="number($usable-page-width-px)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="goodWidths" select="not(starts-with($columnData, '|') or substring($columnData, string-length($columnData)) = '|' or contains($columnData, '||') or string-length($columnData) = 0)" />

		<xsl:variable name="totalWidth">

			<xsl:choose>
				<xsl:when test="$goodWidths">
					<xsl:call-template name="addColumnWidthsInColumnData">
						<xsl:with-param name="columnData" select="$columnData" />
						<xsl:with-param name="total" select="0" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<!-- If we can't add up the widths we need to count columns -->
					<xsl:call-template name="countColumnsInColumnData">
						<xsl:with-param name="columnData" select="$columnData" />
						<xsl:with-param name="total" select="0" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="contains($table/@class, 'co_detailsTable')">
				<xsl:value-of select="$columnData"/>
			</xsl:when>
			<xsl:when test="contains($table/@id, 'headnotesTable') or contains($table/@id, 'co_inlineFootnote')">
				<xsl:call-template name="determineColumnWidthsFromTotalsForHeadnotes">
					<xsl:with-param name="columnData" select="$columnData" />
					<xsl:with-param name="totalWidth" select="number($totalWidth)" />
					<xsl:with-param name="pixelWidth">
						<xsl:choose>
							<xsl:when test="contains($page-master-reference, 'DualColumn')">
								<xsl:value-of select="number(($pixelWidth) div 2) - number($dualColumnGutter)"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="number($pixelWidth)"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="goodWidths" select="$goodWidths" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="determineColumnWidthsFromTotals">
					<xsl:with-param name="columnData" select="$columnData" />
					<xsl:with-param name="totalWidth" select="number($totalWidth)" />
					<xsl:with-param name="pixelWidth" select="number($pixelWidth)" />
					<xsl:with-param name="goodWidths" select="$goodWidths" />
				</xsl:call-template>				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="findParentCellIndex">
		<xsl:param name="tr"/>
		<xsl:param name="cell"/>
		<xsl:param name="currentCell"/>
		<xsl:param name="currentColumn"/>

		<xsl:variable name="cellNode" select="$tr/node()[number($currentCell)]" />

		<xsl:choose>
			<xsl:when test="number($currentCell) &gt; count($tr/node())">
				<xsl:text>0</xsl:text>
			</xsl:when>
			<xsl:when test="generate-id($cellNode) = generate-id($cell)">
				<xsl:value-of select="number($currentColumn)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="findParentCellIndex">
					<xsl:with-param name="tr" select="$tr"/>
					<xsl:with-param name="cell" select="$cell" />
					<xsl:with-param name="currentCell" select="number($currentCell) + 1" />
					<xsl:with-param name="currentColumn">
						<xsl:choose>
							<xsl:when test="$cellNode/@colspan">
								<xsl:value-of select="number($currentColumn) + number($cellNode/@colspan)"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="number($currentColumn) + 1"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template name="findParentCellWidthFromIndex">
		<xsl:param name="parentWidths"/>
		<xsl:param name="index"/>

		<xsl:choose>
			<xsl:when test="number($index) = 1">
				<xsl:variable name="tryToGrab" select="substring-before($parentWidths, '|')" />
				<xsl:choose>
					<xsl:when test="string-length($tryToGrab) &gt; 0">
						<xsl:value-of select="number(translate($tryToGrab, 'pxpt', ''))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="number(translate($parentWidths, 'pxpt', ''))"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="findParentCellWidthFromIndex">
					<xsl:with-param name="parentWidths" select="substring-after($parentWidths, '|')" />
					<xsl:with-param name="index" select="$index - 1" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="determineColumnWidthsFromTotals">
		<xsl:param name="columnData" />
		<xsl:param name="totalWidth" />
		<xsl:param name="pixelWidth" />
		<xsl:param name="goodWidths" />

		<xsl:variable name="width">
			<xsl:choose>
				<xsl:when test="$goodWidths">
					<xsl:variable name="tempWidth" select="substring-before($columnData, '|')" />
					<xsl:choose>
						<xsl:when test="string-length($tempWidth) &gt; 0">
							<xsl:value-of select="number(translate($tempWidth, 'pxPXemsEMSptPT%', ''))"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="number(translate($columnData, 'pxPXemsEMSptPT%', ''))" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>1</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="percentage" select="number($width) div number($totalWidth)" />

		<xsl:value-of select="round($pixelWidth * $percentage)"/>
		<xsl:text>px</xsl:text>

		<xsl:if test="contains($columnData, '|')">
			<xsl:text>|</xsl:text>
			<xsl:call-template name="determineColumnWidthsFromTotals">
				<xsl:with-param name="columnData" select="substring-after($columnData, '|')" />
				<xsl:with-param name="totalWidth" select="number($totalWidth)" />
				<xsl:with-param name="pixelWidth" select="number($pixelWidth)" />
				<xsl:with-param name="goodWidths" select="$goodWidths" />				
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="determineColumnWidthsFromTotalsForHeadnotes">
		<xsl:param name="columnData" />
		<xsl:param name="totalWidth" />
		<xsl:param name="pixelWidth" />
		<xsl:param name="goodWidths" />
		<xsl:param name="previousColumnsWidth" select="0" />
		
		<xsl:variable name="width">
			<xsl:choose>
				<xsl:when test="$goodWidths">
					<xsl:variable name="tempWidth" select="substring-before($columnData, '|')" />
					<xsl:choose>
						<xsl:when test="string-length($tempWidth) &gt; 0">
							<xsl:value-of select="number(translate($tempWidth, 'pxPXemsEMSptPT%', ''))"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="number(translate($columnData, 'pxPXemsEMSptPT%', ''))" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>1</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="contains($columnData, '|')">
				<xsl:value-of select="$width"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="number($pixelWidth) - number($previousColumnsWidth)"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>px</xsl:text>

		<xsl:if test="contains($columnData, '|')">
			<xsl:text>|</xsl:text>
			<xsl:call-template name="determineColumnWidthsFromTotalsForHeadnotes">
				<xsl:with-param name="columnData" select="substring-after($columnData, '|')" />
				<xsl:with-param name="totalWidth" select="number($totalWidth)" />
				<xsl:with-param name="pixelWidth" select="number($pixelWidth)" />
				<xsl:with-param name="goodWidths" select="$goodWidths" />
				<xsl:with-param name="previousColumnsWidth" select="number($previousColumnsWidth + $width)" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="getWidthFromCellStyleAttribute">
		<xsl:param name="style" />
		<xsl:variable name="name"
					  select="normalize-space(substring-before($style, ':'))" />
		<xsl:if test="$name">
			<xsl:variable name="value-and-rest"
						  select="normalize-space(substring-after($style, ':'))" />
			<xsl:variable name="value">
				<xsl:choose>
					<xsl:when test="contains($value-and-rest, ';')">
						<xsl:value-of select="normalize-space(substring-before(
								  $value-and-rest, ';'))" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$value-and-rest" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$name = 'width'">
					<xsl:value-of select="$value" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="rest"
								  select="normalize-space(substring-after($style, ';'))" />
					<xsl:if test="$rest">
						<xsl:call-template name="getWidthFromCellStyleAttribute">
							<xsl:with-param name="style" select="$rest" />
						</xsl:call-template>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template name="countColumns">
		<xsl:param name="row" />
		<xsl:param name="currentCell" />
		<xsl:param name="count" />

		<!-- The first row should have all the information needed to count the columns -->

		<xsl:variable name="cell" select="$row/node()[number($currentCell)]" />

		<xsl:choose>
			<xsl:when test="$cell">
				<xsl:call-template name="countColumns">
					<xsl:with-param name="row" select="$row" />
					<xsl:with-param name="currentCell" select="number($currentCell) + 1" />
					<xsl:with-param name="count">
						<xsl:choose>
							<xsl:when test="$cell/@colspan">
								<xsl:value-of select="number($count) + number($cell/@colspan)"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="number($count) + 1"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="number($count)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="compileColumnData">
		<xsl:param name="table" />
		<xsl:param name="totalColumns" />
		<xsl:param name="currentColumn" />
		<xsl:param name="countChars" />
		<xsl:param name="columnData" />

		<xsl:choose>
			<!-- Get coverpage table column data separately because it has rowspans which table processor can't handle -->
			<xsl:when test="contains($table/@id, 'co_doc_coverpageTable')">
				<xsl:call-template name="getCoverpageColumnData">
					<xsl:with-param name="table" select="$table" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="columnWidth">
					<xsl:choose>
						<xsl:when test="$table/html:tr">
							<xsl:call-template name="findIndividualColumnData">
								<xsl:with-param name="rows" select="$table/html:tr" />
								<xsl:with-param name="currentRow" select="1" />
								<xsl:with-param name="currentColumn" select="number($currentColumn)" />
								<xsl:with-param name="charCount" select="''" />
								<xsl:with-param name="countChars" select="$countChars" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="findIndividualColumnData">
								<xsl:with-param name="rows" select="$table/node()/html:tr" />
								<xsl:with-param name="currentRow" select="1" />
								<xsl:with-param name="currentColumn" select="number($currentColumn)" />
								<xsl:with-param name="charCount" select="''" />
								<xsl:with-param name="countChars" select="$countChars" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:variable name="newColumnData">
					<xsl:if test="number($currentColumn) != 1">
						<xsl:value-of select="$columnData"/>
						<xsl:text>|</xsl:text>
					</xsl:if>
					<xsl:value-of select="$columnWidth"/>
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="not($countChars) and string-length($columnWidth) = 0">
						<!-- We missed a width so start over counting chars -->
						<xsl:call-template name="compileColumnData">
							<xsl:with-param name="table" select="$table" />
							<xsl:with-param name="totalColumns" select="number($totalColumns)" />
							<xsl:with-param name="currentColumn" select="1" />
							<xsl:with-param name="countChars" select="true()" />
							<xsl:with-param name="columnData" select="''" />
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="number($currentColumn) != number($totalColumns)">
						<xsl:call-template name="compileColumnData">
							<xsl:with-param name="table" select="$table" />
							<xsl:with-param name="totalColumns" select="number($totalColumns)" />
							<xsl:with-param name="currentColumn" select="number($currentColumn) + 1" />
							<xsl:with-param name="countChars" select="$countChars" />
							<xsl:with-param name="columnData" select="$newColumnData" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$newColumnData"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getCoverpageColumnData">
		<xsl:param name="table" />
		<xsl:call-template name="getWidthFromCellStyleAttribute">
			<xsl:with-param name="style" select="$table//html:td[contains(@class, 'co_coverPageFlagCol')][1]/@style" />
		</xsl:call-template>
		<xsl:text>|</xsl:text>
		<xsl:call-template name="getWidthFromCellStyleAttribute">
			<xsl:with-param name="style" select="$table//html:td[contains(@class, 'co_documentCoverPageTableLabel')][1]/@style" />
		</xsl:call-template>
		<xsl:text>|</xsl:text>
		<xsl:call-template name="getWidthFromCellStyleAttribute">
			<xsl:with-param name="style" select="$table//html:td[contains(@class, 'co_documentCoverPageTableField')][1]/@style" />
		</xsl:call-template>
		<xsl:text>|</xsl:text>
		<xsl:call-template name="getWidthFromCellStyleAttribute">
			<xsl:with-param name="style" select="$table//html:td[contains(@class, 'co_coverPageOutlineCol')][1]/@style" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="findIndividualColumnData">
		<xsl:param name="rows" />
		<xsl:param name="currentRow" />
		<xsl:param name="currentColumn" />
		<xsl:param name="charCount" />
		<xsl:param name="countChars" />

		<xsl:variable name="row" select="$rows[number($currentRow)]" />

		<xsl:choose>
			<xsl:when test="$row">
				<xsl:variable name="cellData">
					<xsl:call-template name="searchCellsForColumnData">
						<xsl:with-param name="cells" select="$row/node()" />
						<xsl:with-param name="currentCell" select="1" />
						<xsl:with-param name="currentColumn" select="number($currentColumn)" />
						<xsl:with-param name="columnCount" select="0" />
						<xsl:with-param name="countChars" select="$countChars" />
					</xsl:call-template>
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="string-length($cellData) &gt; 0 and contains($cellData, 'ch')">
						<xsl:call-template name="findIndividualColumnData">
							<xsl:with-param name="rows" select="$rows" />
							<xsl:with-param name="currentRow" select="number($currentRow) + 1" />
							<xsl:with-param name="currentColumn" select="number($currentColumn)" />
							<xsl:with-param name="charCount">
								<xsl:choose>
									<xsl:when test="number(translate($charCount, 'ch', '')) &gt; number(translate($cellData, 'ch', ''))">
										<xsl:value-of select="$charCount"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$cellData"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
							<xsl:with-param name="countChars" select="$countChars" />
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="string-length($cellData) &gt; 0">
						<xsl:value-of select="$cellData"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="findIndividualColumnData">
							<xsl:with-param name="rows" select="$rows" />
							<xsl:with-param name="currentRow" select="number($currentRow) + 1" />
							<xsl:with-param name="currentColumn" select="number($currentColumn)" />
							<xsl:with-param name="charCount" select="$charCount" />
							<xsl:with-param name="countChars" select="$countChars" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$countChars and string-length($charCount) &gt; 0">
				<xsl:choose>
					<xsl:when test="number(translate($charCount, 'ch', '')) &lt; 20">
						<xsl:value-of select="number(translate($charCount, 'ch', ''))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>20</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="searchCellsForColumnData">
		<xsl:param name="cells" />
		<xsl:param name="currentCell" />
		<xsl:param name="currentColumn" />
		<xsl:param name="columnCount" />
		<xsl:param name="countChars" />

		<xsl:variable name="cell" select="($cells[text()])[number($currentCell)] | $cells[number($currentCell)]" />

		<xsl:if test="$cell">
			<xsl:choose>
				<xsl:when test="$cell/@colspan">
					<!-- Can't get width from a cell with a colspan, keep going -->
					<xsl:call-template name="searchCellsForColumnData">
						<xsl:with-param name="cells" select="$cells" />
						<xsl:with-param name="currentCell" select="number($currentCell) + 1" />
						<xsl:with-param name="currentColumn" select="number($currentColumn)" />
						<xsl:with-param name="columnCount" select="number($columnCount) + number($cell/@colspan)" />
						<xsl:with-param name="countChars" select="$countChars" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="number($columnCount) + 1 = number($currentColumn)">
					<!-- Found it -->
					<xsl:choose>
						<xsl:when test="$countChars">
							<xsl:variable name="cellContent">
								<xsl:value-of select="$cell"/>
							</xsl:variable>
							<xsl:value-of select="string-length($cellContent)"/>
							<xsl:text>ch</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="getWidthFromCellStyleAttribute">
								<xsl:with-param name="style" select="$cell/@style" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="number($currentColumn) &lt;= number($columnCount)">
					<!-- Went past what we wanted -->
				</xsl:when>
				<xsl:otherwise>
					<!-- Not there yet -->
					<xsl:call-template name="searchCellsForColumnData">
						<xsl:with-param name="cells" select="$cells" />
						<xsl:with-param name="currentCell" select="number($currentCell) + 1" />
						<xsl:with-param name="currentColumn" select="number($currentColumn)" />
						<xsl:with-param name="columnCount" select="number($columnCount) + 1" />
						<xsl:with-param name="countChars" select="$countChars" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		Inline-level
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template match="html:b">
		<fo:inline xsl:use-attribute-sets="b">
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
			<xsl:call-template name="process-common-attributes-and-children" />
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:s">
		<fo:inline xsl:use-attribute-sets="s">
			<xsl:call-template name="process-common-attributes-and-children" />
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

	<xsl:template match="html:label">
		<fo:inline>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>
	
	<xsl:template match="html:span">
		<fo:inline>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
		<xsl:if test="@class and (contains(@class, 'co_searchTerm')  or contains(@class, 'co_searchWithinTerm')  or contains(@class, 'co_hl'))">
			<fo:inline background-color="#FFFFFF" />
		</xsl:if>
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
							 text-align="center">
			<xsl:call-template name="process-common-attributes" />
			<fo:block wrap-option="no-wrap" line-height="1">
				<xsl:apply-templates />
			</fo:block>
		</fo:inline-container>
	</xsl:template>

	<xsl:template match="html:bdo">
		<fo:bidi-override direction="{@dir}" unicode-bidi="bidi-override">
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:bidi-override>
	</xsl:template>

	<xsl:template match="html:br">
		<fo:block>
			<xsl:call-template name="process-common-attributes"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="html:q">
		<fo:inline xsl:use-attribute-sets="q">
			<xsl:call-template name="process-common-attributes"/>
			<xsl:choose>
				<xsl:when test="lang('ja')">
					<xsl:text>「</xsl:text>
					<xsl:apply-templates/>
					<xsl:text>」</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<!-- lang('en') -->
					<xsl:text>“</xsl:text>
					<xsl:apply-templates/>
					<xsl:text>”</xsl:text>
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
					<xsl:text>『</xsl:text>
					<xsl:apply-templates/>
					<xsl:text>』</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<!-- lang('en') -->
					<xsl:text>‘</xsl:text>
					<xsl:apply-templates/>
					<xsl:text>’</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</fo:inline>
	</xsl:template>

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		Image
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template match="html:img" priority="2">
		<fo:external-graphic xsl:use-attribute-sets="img">
			<xsl:call-template name="process-img"/>
		</fo:external-graphic>
	</xsl:template>

	<xsl:template match="html:img[ancestor::html:a/@href]" priority="2">
		<fo:external-graphic xsl:use-attribute-sets="img-link">
			<xsl:call-template name="process-img"/>
		</fo:external-graphic>
	</xsl:template>

	<xsl:template name="process-img">
		<xsl:attribute name="border-top-width">
			<xsl:text>0px</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="border-bottom-width">
			<xsl:text>0px</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="border-left-width">
			<xsl:text>0px</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="border-right-width">
			<xsl:text>0px</xsl:text>
		</xsl:attribute>
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
		<xsl:call-template name="img-width-and-height">
			<xsl:with-param name="value" select="@width"/>
			<xsl:with-param name="isWidth" select="true()"/>
		</xsl:call-template>
		<xsl:call-template name="img-width-and-height">
			<xsl:with-param name="value" select="@height"/>
			<xsl:with-param name="isWidth" select="false()"/>
		</xsl:call-template>
		<xsl:if test="@border">
			<xsl:attribute name="border">
				<xsl:value-of select="concat(@border, 'px solid')"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:call-template name="process-common-attributes"/>
	</xsl:template>

	<xsl:template name="img-width-and-height">
		<xsl:param name="value"/>
		<xsl:param name="isWidth" select="true()"/>
		
		<xsl:if test="not(contains(@class, 'co_trademarkScanImage')) and not(contains(@class, 'co_dwpiImage'))">
			<xsl:if test="string-length($value) &gt; 0">
				<xsl:if test="$isWidth">
					<xsl:variable name="width">
						<xsl:call-template name="ConvertImageSizeToMM">
							<xsl:with-param name="value" select="$value"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="contains($value, '%')">
							<xsl:attribute name="width">
								<xsl:value-of select="$value"/>
							</xsl:attribute>
							<xsl:attribute name="content-width">
								<xsl:text>scale-to-fit</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="contains($page-master-reference, 'DualColumn')">
							<xsl:call-template name="dual-column-img-width">
								<xsl:with-param name="width" select="$width"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="content-width">
								<xsl:value-of select="$width"/>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
				<xsl:if test="not($isWidth)">
					<xsl:variable name="height">
						<xsl:call-template name="ConvertImageSizeToMM">
							<xsl:with-param name="value" select="@height"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="contains($value, '%')">
							<xsl:attribute name="height">
								<xsl:value-of select="$value"/>
							</xsl:attribute>
							<xsl:attribute name="content-height">
								<xsl:text>scale-to-fit</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="contains($page-master-reference, 'DualColumn')">
							<xsl:call-template name="dual-column-img-height">
								<xsl:with-param name="height" select="$height"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="content-height">
								<xsl:value-of select="$height"/>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="dual-column-img-width">
		<xsl:param name="width" />
		<xsl:attribute name="content-width">
			<xsl:value-of select="$width" />
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="dual-column-img-height">
		<xsl:param name="height" />
		<xsl:attribute name="content-height">
			<xsl:value-of select="$height" />
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="img-max-width">
		<xsl:param name="maxWidth" />

		<xsl:if test="not(contains($maxWidth, '%'))">
			<xsl:variable name="width">
				<xsl:call-template name="ConvertImageSizeToMM">
					<xsl:with-param name="value" select="$maxWidth" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:attribute name="width">
				<xsl:value-of select="$width" />
			</xsl:attribute>
			<xsl:attribute name="content-width">
				<xsl:text>scale-down-to-fit</xsl:text>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="img-max-height">
		<xsl:param name="maxHeight" />

		<xsl:if test="not(contains($maxHeight, '%'))">
			<xsl:variable name="height">
				<xsl:call-template name="ConvertImageSizeToMM">
					<xsl:with-param name="value" select="$maxHeight" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="not(contains(@class, 'co_dwpiImage'))">
				<xsl:attribute name="height">
					<xsl:value-of select="$height" />
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="content-height">
				<xsl:text>scale-down-to-fit</xsl:text>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="ConvertImageSizeToMM">
		<xsl:param name="value"/>
		<xsl:choose>
			<xsl:when test="contains($value, 'px')">
				<xsl:value-of select="concat(string(number(substring-before($value, 'px')) div number($convertPixelsToMM)), 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="html:object">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="html:param" />
	<xsl:template match="html:map" />
	<xsl:template match="html:area" />
	<xsl:template match="html:input" />
	<xsl:template match="html:select" />
	<xsl:template match="html:optgroup" />
	<xsl:template match="html:option" />
	<xsl:template match="html:textarea" />
	<xsl:template match="html:legend" />
	<xsl:template match="html:button" />

	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		Link
	=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	<xsl:template match="html:a" name="aWithoutHrefTemplate">
		<fo:inline>
			<xsl:call-template name="process-common-attributes-and-children"/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="html:a[string-length(@href) &gt; 0]" priority="2">
		<xsl:choose>
			<xsl:when test="starts-with(@href,'#')">
				<xsl:choose>
					<xsl:when test="string-length(@href) &gt; 1">
						<xsl:call-template name="process-sup-link"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="aWithoutHrefTemplate"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$hrefLimit and string-length(@href) &gt; number($hrefLimit)">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="process-a-link"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="process-a-link">
		<xsl:choose>
			<xsl:when test="./node()[name() = 'img']">
				<!-- We need to break this link into multiple parts, as the text and image need to be processed separately. -->
				<xsl:for-each select="./node()">
					<xsl:if test="self::* or string-length(normalize-space(.)) &gt; 0">
						<xsl:choose>
							<xsl:when test="$isWordPerfect">
								<xsl:apply-templates select="." />
							</xsl:when>
							<xsl:when test="self::*[name() = 'input' and @type='hidden']" />
							<xsl:otherwise>
								<fo:basic-link xsl:use-attribute-sets="a-link">
									<xsl:choose>
										<xsl:when test="position() = 1">
											<xsl:call-template name="process-common-attributes">
												<xsl:with-param name="contextNode" select=".." />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="process-common-attributes">
												<xsl:with-param name="contextNode" select=".." />
												<xsl:with-param name="idPostfix" select="''" />
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>

									<xsl:choose>
										<xsl:when test="starts-with(../@href,'#')">
											<xsl:attribute name="internal-destination">
												<xsl:value-of select="substring-after(../@href,'#')" />
											</xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="external-destination">
												<xsl:value-of select="../@href" />
											</xsl:attribute>
										</xsl:otherwise>
									</xsl:choose>
									<xsl:if test="../@title">
										<xsl:attribute name="role">
											<xsl:value-of select="../@title" />
										</xsl:attribute>
									</xsl:if>
									<xsl:variable name="fontSizeWithUnit">
										<xsl:choose>
											<xsl:when test="string-length(substring-before(substring-after(@style, 'height:'), ';')) &gt; 0">
												<xsl:value-of select="substring-before(substring-after(@style, 'height:'), ';')" />
											</xsl:when>
											<xsl:when test="string-length(@height) &gt; 0">
												<xsl:value-of select="@height" />
											</xsl:when>
										</xsl:choose>
									</xsl:variable>
									<xsl:variable name="fontSizeValue">
										<xsl:if test="string-length($fontSizeWithUnit) &gt; 0">
											<xsl:choose>
												<xsl:when test="string(number($fontSizeWithUnit))='NaN'">
													<!-- might contain the unit at the end?-->
													<xsl:variable name="validSizeToUse"
													              select="substring($fontSizeWithUnit, 0, string-length($fontSizeWithUnit)-2)" />
													<xsl:choose>
														<xsl:when test="substring($fontSizeWithUnit,string-length($fontSizeWithUnit)-1)='mm'">
															<!-- Convert mm to pixels -->
															<!-- pixels = (mm * dpi)/25.4 -->
															<xsl:value-of select="round((number($validSizeToUse) * 96) div 25.4)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="string(round(number($validSizeToUse) * .1 + number($validSizeToUse)))" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="string(round(number($fontSizeWithUnit) * .1 + number($fontSizeWithUnit)))" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:if>
									</xsl:variable>
									<xsl:if test="string-length(normalize-space($fontSizeValue)) &gt; 0 ">
										<xsl:attribute name="font-size">
											<xsl:value-of select="$fontSizeValue" />
											<xsl:text>px</xsl:text>
										</xsl:attribute>
										<xsl:attribute name="alignment-adjust">
											<xsl:text>after-edge</xsl:text>
										</xsl:attribute>
									</xsl:if>
									<xsl:apply-templates select="." />
								</fo:basic-link>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<fo:basic-link xsl:use-attribute-sets="a-link">
					<xsl:call-template name="process-common-attributes">
						<xsl:with-param name="contextNode" select="." />
					</xsl:call-template>
					<xsl:choose>
						<xsl:when test="starts-with(@href,'#')">
							<xsl:attribute name="internal-destination">
								<xsl:value-of select="substring-after(@href,'#')" />
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="external-destination">
								<xsl:value-of select="@href" />
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="@title">
						<xsl:attribute name="role">
							<xsl:value-of select="@title" />
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</fo:basic-link>
				<xsl:if test="starts-with(@href,'#') and @addPageNumber = 'true'">
					<xsl:text> (p.</xsl:text>
					<fo:page-number-citation ref-id="{substring-after(@href,'#')}" />
					<xsl:text>)</xsl:text>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="process-sup-link">
		<xsl:choose>
			<xsl:when test="./node()[name() = 'img']">
				<!-- We need to break this link into multiple parts, as the text and image need to be processed separately. -->
				<xsl:for-each select="./node()">
					<xsl:if test="self::* or string-length(normalize-space(.)) &gt; 0">
						<xsl:choose>
							<xsl:when test="$isWordPerfect">
								<!-- No processing for links	-->
								<xsl:apply-templates select="." />
							</xsl:when>
							<xsl:when test="self::*[name() = 'input' and @type='hidden']" />
							<xsl:otherwise>
								<fo:basic-link xsl:use-attribute-sets="a-link">
									<xsl:choose>
										<xsl:when test="position() = 1">
											<xsl:call-template name="process-common-attributes">
												<xsl:with-param name="contextNode" select=".." />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="process-common-attributes">
												<xsl:with-param name="contextNode" select=".." />
												<xsl:with-param name="idPostfix" select="''" />
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
									<xsl:choose>
										<xsl:when test="starts-with(../@href,'#')">
											<xsl:attribute name="internal-destination">
												<xsl:value-of select="substring-after(../@href,'#')" />
											</xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="external-destination">
												<xsl:value-of select="../@href" />
											</xsl:attribute>
										</xsl:otherwise>
									</xsl:choose>
									<xsl:if test="../@title">
										<xsl:attribute name="role">
											<xsl:value-of select="../@title" />
										</xsl:attribute>
									</xsl:if>
									<xsl:variable name="fontSizeWithUnit">
										<xsl:choose>
											<xsl:when test="string-length(substring-before(substring-after(@style, 'height:'), ';')) &gt; 0">
												<xsl:value-of select="substring-before(substring-after(@style, 'height:'), ';')" />
											</xsl:when>
											<xsl:when test="string-length(@height) &gt; 0">
												<xsl:value-of select="@height" />
											</xsl:when>
										</xsl:choose>
									</xsl:variable>
									<xsl:variable name="fontSizeValue">
										<xsl:if test="string-length($fontSizeWithUnit) &gt; 0">
											<xsl:choose>
												<xsl:when test="string(number($fontSizeWithUnit))='NaN'">
													<!-- might contain the unit at the end?-->
													<xsl:variable name="validSizeToUse"
													              select="substring($fontSizeWithUnit, 0, string-length($fontSizeWithUnit)-2)" />
													<xsl:choose>
														<xsl:when test="substring($fontSizeWithUnit,string-length($fontSizeWithUnit)-1)='mm'">
															<!-- Convert mm to pixels -->
															<!-- pixels = (mm * dpi)/25.4 -->
															<xsl:value-of select="round((number($validSizeToUse) * 96) div 25.4)" />
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="string(round(number($validSizeToUse) * .1 + number($validSizeToUse)))" />
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="string(round(number($fontSizeWithUnit) * .1 + number($fontSizeWithUnit)))" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:if>
									</xsl:variable>
									<xsl:if test="string-length(normalize-space($fontSizeValue)) &gt; 0 ">
										<xsl:attribute name="font-size">
											<xsl:value-of select="$fontSizeValue" />
											<xsl:text>px</xsl:text>
										</xsl:attribute>
										<xsl:attribute name="alignment-adjust">
											<xsl:text>after-edge</xsl:text>
										</xsl:attribute>
									</xsl:if>
									<xsl:apply-templates select="." />
								</fo:basic-link>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$isRTF or $isMSWord">
						<fo:basic-link xsl:use-attribute-sets="sup-link">
							<xsl:call-template name="process-common-attributes">
								<xsl:with-param name="contextNode" select="." />
							</xsl:call-template>
							<xsl:choose>
								<xsl:when test="starts-with(@href,'#')">
									<xsl:attribute name="internal-destination">
										<xsl:value-of select="substring-after(@href,'#')" />
									</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="external-destination">
										<xsl:value-of select="@href" />
									</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:if test="@title">
								<xsl:attribute name="role">
									<xsl:value-of select="@title" />
								</xsl:attribute>
							</xsl:if>
							<xsl:apply-templates />
						</fo:basic-link>
					</xsl:when>
					<xsl:otherwise>
						<fo:basic-link xsl:use-attribute-sets="a-link">
							<xsl:call-template name="process-common-attributes">
								<xsl:with-param name="contextNode" select="." />
							</xsl:call-template>
							<xsl:choose>
								<xsl:when test="starts-with(@href,'#')">
									<xsl:attribute name="internal-destination">
										<xsl:value-of select="substring-after(@href,'#')" />
									</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="external-destination">
										<xsl:value-of select="@href" />
									</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:if test="@title">
								<xsl:attribute name="role">
									<xsl:value-of select="@title" />
								</xsl:attribute>
							</xsl:if>
							<xsl:apply-templates />
						</fo:basic-link>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="starts-with(@href,'#') and @addPageNumber = 'true'">
					<xsl:text> (p.</xsl:text>
					<fo:page-number-citation ref-id="{substring-after(@href,'#')}" />
					<xsl:text>)</xsl:text>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
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
							 text-align="center">
			<xsl:call-template name="process-common-attributes" />
			<fo:block font-size="50%"
					  wrap-option="no-wrap"
					  line-height="1"
					  space-before.conditionality="retain"
					  space-before="-1.1em"
					  space-after="0.1em"
					  role="html:rt">
				<xsl:for-each select="html:rt | html:rtc[1]/html:rt">
					<xsl:call-template name="process-common-attributes" />
					<xsl:apply-templates />
				</xsl:for-each>
			</fo:block>
			<fo:block wrap-option="no-wrap" line-height="1" role="html:rb">
				<xsl:for-each select="html:rb | html:rbc[1]/html:rb">
					<xsl:call-template name="process-common-attributes" />
					<xsl:apply-templates />
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
						<xsl:call-template name="process-common-attributes" />
						<xsl:apply-templates />
					</xsl:for-each>
				</fo:block>
			</xsl:if>
		</fo:inline-container>
	</xsl:template>

	<xsl:template match="comment()">
		<xsl:comment>
			<xsl:value-of select="." />
		</xsl:comment>
	</xsl:template>

</xsl:stylesheet>