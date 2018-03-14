<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:include href="Universal.xsl" />
	<xsl:include href="ProvisionsTable.xsl" />
	<xsl:include href="FlatListTransform.xsl" />

	<!-- Document header block -->
	<xsl:template name="HeadNotes">
		<div class="&headnotesClass; &centerClass;">
			<!-- Document headers -->
			<xsl:call-template name="Headers" />
		</div>
	</xsl:template>
	
	<!-- Header -->
	<xsl:template name="Headers">
		<div class="&documentHeadClass;">
			<xsl:for-each select="//Document/n-docbody/document/fulltext_metadata/headings/heading">
				<div class="&titleClass;">
					<xsl:apply-templates />
				</div>
			</xsl:for-each>
			
			<!-- Commencement -->
			<xsl:call-template name="Commencement" />
		</div>
		
		<!-- End of a header -->
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
	
	<!-- Commencement block definition -->
	<xsl:template name="Commencement">
		<div class="&commencementCssClass;">
			<xsl:value-of select="/Document/n-docbody/document/fulltext_metadata/commencement/start-date/@date" />
		</div>
	</xsl:template>

	<!-- List item definition  -->
	<xsl:template name="ListItems">
		<li>
			<xsl:choose>
				<xsl:when test="not(./para-text)">
					<!-- If there are no para-text within a list item, add a parapgraph anyway -->
					<div class="&paratextMainClass;">
						<xsl:if test="./@number">
							<xsl:value-of select="./@number"/>
							<xsl:value-of select="' '"/>
						</xsl:if>
						
						<xsl:apply-templates />
					</div>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</li>
	</xsl:template>
	
	<!-- Group flat list elements to valid html lists -->
	<xsl:template match="*[(starts-with(name(), 'sub') or starts-with(name(), 'defn-sub'))]">
		<!-- Suppress -->
	</xsl:template>
	
	<!-- List start definition (matches first list item of a flat list) -->
	<xsl:template match="*[
			(starts-with(name(), 'sub') or starts-with(name(), 'defn-sub'))
			and 
			(
				not(preceding-sibling::*[1][
					(starts-with(name(), 'sub') or starts-with(name(), 'defn-sub'))
				])
			)
		]">
		<xsl:call-template name="TransformFlatListToHTMLList" />
	</xsl:template>

	<!-- Hide act number and shorttitle -->
	<xsl:template match="act-begin/act-number | act-begin/shorttitle">
		<!-- Suppress -->
	</xsl:template>

	<!-- Title after the number (suppressed by default) -->
	<xsl:template match="title">
		<xsl:param name="plainTextTitle" select="false()"></xsl:param>

		<xsl:if test="$plainTextTitle">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<!-- Render standalone title -->
	<xsl:template match="title[not(./preceding-sibling::*[1][self::number])]">
		<h4>
			<xsl:apply-templates />
		</h4>
	</xsl:template>
	
	<!--Render standalone number content -->
	<xsl:template name="RenderStandaloneNumberContent">
		<xsl:apply-templates />
		<xsl:value-of select="' '" />
	</xsl:template>
	
	<!-- Number node -->
	<xsl:template match="number" priority="1">
		<xsl:param name="suppressNumberBeforeParaText" select="true()" />
			
		<xsl:choose>
			<!-- number + title pair -->
			<xsl:when test="following-sibling::*[1][self::title]">
				<h4>
					<xsl:apply-templates />
					<xsl:value-of select="' '" />
					<xsl:apply-templates select="./following-sibling::*[1]">
						<xsl:with-param name="plainTextTitle" select="true()" />
					</xsl:apply-templates>
				</h4>
			</xsl:when>
			<!-- number + para-text pair -->
			<xsl:when test="$suppressNumberBeforeParaText and following-sibling::*[1][self::para-text]">
				<!-- Suppress it, later on the para-text match will grab this number -->
			</xsl:when>
			<xsl:otherwise> <!-- Standalone number -->
				<xsl:choose>
					<xsl:when test="./parent::*[1][self::narrative-paragraph or self::paragraph]">
						<h4>
							<xsl:call-template name="RenderStandaloneNumberContent" />
						</h4>
					</xsl:when>
					<xsl:otherwise>
						<span>
							<xsl:call-template name="RenderStandaloneNumberContent" />
						</span>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Wrap paragraph content -->
	<xsl:template match="
		narrative-paragraph
		| narrative-paragraph-title
		| unnumbered-para
		| *[starts-with(name(), 'narrative-sub')]
	">
		<div class="&paraMainClass;">
			<div class="&paratextMainClass;">
				<xsl:apply-templates />
			</div>
		</div>
	</xsl:template>
	
	<!-- Wrap enacting text -->
	<xsl:template match="enacting-text">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<!-- Wrap assent (align to the right) -->
	<xsl:template match="assent">
		<div class="&paraMainClass;">
			<div class="&paratextMainClass; &alignHorizontalRightClass;">
				<xsl:apply-templates />
			</div>
		</div>
	</xsl:template>
	
	<!-- Transform align attribute value to a corresponding CSS class -->
	<xsl:template name="ProcessAlignAttribute">
		<xsl:choose>
			<xsl:when test="./@align='left'">
				<xsl:text>&alignHorizontalLeftClass;</xsl:text>
			</xsl:when>
			
			<xsl:when test="./@align='center'">
				<xsl:text>&alignHorizontalCenterClass;</xsl:text>
			</xsl:when>
				
			<xsl:when test="./@align='right'">
				<xsl:text>&alignHorizontalRightClass;</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!-- Paragraph block definition -->
	<xsl:template match="para-text">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&paratextMainClass;</xsl:text>
				<xsl:if test="./@align">
					<xsl:value-of select="' '" />
					<xsl:call-template name="ProcessAlignAttribute" />
				</xsl:if>
			</xsl:attribute>
			
			<!-- Optional number of a paragraph -->
			<xsl:variable name="optionalNumber" select="./preceding-sibling::*[1][self::number] | ../@number" />
			
			<!-- Paragraph content -->
			<xsl:variable name="content">
				<xsl:apply-templates select="$optionalNumber">
					<xsl:with-param name="suppressNumberBeforeParaText" select="false()" />
				</xsl:apply-templates>
				<xsl:value-of select="' '"/>
				<xsl:apply-templates />
			</xsl:variable>
			
			<xsl:choose>
				<xsl:when test="preceding-sibling::extract/act | following-sibling::extract/act">
					<!-- Bold text for annotations -->
					<strong>
						<xsl:copy-of select="$content" />
					</strong>
				</xsl:when>
				<xsl:otherwise> <!-- Regular text otherwise -->
					<xsl:copy-of select="$content" />
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="para-group-title[string(.) and following-sibling::*[1][self::table[descendant::table-title]]]">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates select="following-sibling::table/table-title">
			<xsl:with-param name="isSuppressed" select="false()" />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="table-title[parent::table[preceding-sibling::*[1][string(self::para-group-title)]]]" priority="1">
		<xsl:param name="isSuppressed" select="true()" />

		<xsl:if test="not($isSuppressed)">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<!-- Table blocks definitions -->

	<xsl:template match="table-title | narrative-paragraph-title">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<!-- Enable borders for all tables, except for those having frame='none' attribute -->
	<xsl:template match="table">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&tableGroupClass;</xsl:text>
				<xsl:if test="not(./@frame='none')">
					<xsl:value-of select="' '" />
					<xsl:text>&tableGroupBorderClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Field length attribute processor -->
	<xsl:template name="ProcessFieldLengthAttribute">
		<xsl:choose>
			<xsl:when test="./@length='year'">
				<xsl:text>&fieldElementYearClass;</xsl:text>
			</xsl:when>
			<xsl:when test="./@length='small'">
				<xsl:text>&fieldElementSmallClass;</xsl:text>
			</xsl:when>
			<xsl:when test="./@length='medium'">
				<xsl:text>&fieldElementMediumClass;</xsl:text>
			</xsl:when>
			<xsl:when test="./@length='fillout'">
				<xsl:text>&fieldElementFilloutClass;</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!-- Field length attribute processor -->
	<xsl:template name="ProcessFieldStyleAttribute">
		<xsl:choose>
			<xsl:when test="./@style='dots'">
				<xsl:text>&fieldElementDotsClass;</xsl:text>
			</xsl:when>
			<xsl:when test="./@style='line'">
				<xsl:text>&fieldElementLineClass;</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!-- Repeat string str length times -->
	<xsl:template name="RepeatString">
		<xsl:param name="str" select="' '" />
		<xsl:param name="length" select="0" />

		<xsl:if test="$length &gt; 0">
			<xsl:value-of select="$str" />
			<xsl:variable name="newLength" select="$length - 1" />
			<xsl:call-template name="RepeatString">
				<xsl:with-param name="str" select="$str" />
				<xsl:with-param name="length" select="$newLength" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- Process field style workaround for delivery -->
	<xsl:template name="ProcessFieldStyleDeliveryWorkaround">
		<!-- Setup field placeholder symbol -->
		<xsl:variable name="str">
			<xsl:choose>
				<xsl:when test="./@style='dots'">
					<xsl:value-of select="'&dotsPlaceholder;'"/>
				</xsl:when>
				<xsl:when test="./@style='line'">
					<xsl:value-of select="'&linePlaceholder;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="' '"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!-- Setup field placeholder length -->
		<!-- Values are set in accordance with relevant css classes which specify the width of a field -->
		<xsl:variable name="length">
			<xsl:choose>
				<xsl:when test="./@length='year'">
					4
				</xsl:when>
				<xsl:when test="./@length='small'">
					5
				</xsl:when>
				<xsl:when test="./@length='medium'">
					10
				</xsl:when>
				<xsl:when test="./@length='fillout'">
					20
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:call-template name="RepeatString">
			<xsl:with-param name="length" select="$length" />
			<xsl:with-param name="str" select="$str" />
		</xsl:call-template>
	</xsl:template>
	
	<!-- Form field placeholder definition (text rule) -->	
	<xsl:template match="field">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&fieldElementClass;</xsl:text>
				<xsl:if test="not($DeliveryMode)">
					<xsl:value-of select="' '"/>
					<xsl:call-template name="ProcessFieldLengthAttribute" />
					<xsl:value-of select="' '"/>
					<xsl:call-template name="ProcessFieldStyleAttribute" />
				</xsl:if>
			</xsl:attribute>

			<!-- Delivery-only workaround -->
			<xsl:if test="$DeliveryMode">
				<xsl:call-template name="ProcessFieldStyleDeliveryWorkaround" />
			</xsl:if>
		</div>
	</xsl:template>
	
	<!-- Suppress elements which are suppressed in Westlaw Ireland -->
	<xsl:template match="si-numbers | si-begin-title">
		<!-- Suppress -->
	</xsl:template>

	<!-- Longquote -->
	<xsl:template match="longquote">
		<blockquote>
			<div class="&paratextMainClass;">
				<xsl:apply-templates />
			</div>
		</blockquote>
	</xsl:template>
	
	<!-- Definition list / list definition -->
	<xsl:template match="defnlist | list">
		<ul>
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<!-- Definition list item / item definition -->
	<xsl:template match="defnlist-item | item">
		<xsl:call-template name="ListItems" />
	</xsl:template>

	<!-- Links definition -->
	<xsl:template match="link[@tuuid]">
		<a>
			<xsl:attribute name="href">
				<xsl:call-template name="GetDocumentUrl">
					<xsl:with-param name="documentGuid" select="@tuuid"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>
	
	<xsl:template match="heading/link[@tuuid]">
		<!-- Suppress links in header -->
		<xsl:apply-templates />
	</xsl:template>
	
	<!-- Transform a header to a footnote header -->
	<xsl:template match="narrative-paragraph/*[
			starts-with(name(), 'narrative-sub')
		]/title">
		<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
			<xsl:apply-templates />
		</h2>
	</xsl:template>
	
	<!-- Wrap a header and its content in order to make it look like a footnote bar -->
	<xsl:template match="narrative-paragraph/*[
		starts-with(name(), 'narrative-sub')
		and child::*[self::title]
	]">
		<div class="&paratextMainClass;">
			<div class="&footnoteSectionClass;">
				<!-- The header -->
				<xsl:apply-templates select="title" />

				<!-- The content -->
				<div class="&footnoteSectionClass;">
					<xsl:apply-templates select="*[not(self::title)]" />
				</div>
			</div>
		</div>
	</xsl:template>
	
	<!-- Entry point -->
	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeIEStatutesClass;'"/>
			</xsl:call-template>

			<!-- Document header -->
			<xsl:call-template name="HeadNotes" />

			<!-- Content -->
			<xsl:apply-templates select="
				/Document/n-docbody/document/fulltext
				| /Document/n-docbody/document/provisions-table
			"/>

			<!-- Copyright block -->
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText" select="/Document/n-docbody/copyright-message/text()" />
				<xsl:with-param name="endOfDocumentCopyrightTextVerbatim" select="true()" />
			</xsl:call-template>
		</div>
	</xsl:template>
</xsl:stylesheet>
