<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Prelim.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="judgmentDate" select="/Document/n-docbody/case.head/date.group/date.line/date[@significance='judgment']" />
	<xsl:variable name="primaryCitation" select="/Document/n-docbody/case.head/citation.group/primary.citation" />
	<xsl:variable name="courtAbbreviation" select="/Document/n-docbody/case.head/court.line/court/@abbrev" />

	<!--*************************** MAIN DOC STRUCTURE ***************************-->
	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
			
			<xsl:apply-templates select="n-docbody/case.head"/>
			<xsl:apply-templates select="n-docbody/ref.group"/>
			<xsl:apply-templates select="n-docbody/catchwords.group"/>
			<xsl:apply-templates select="n-docbody/headnotes"/>
			<xsl:apply-templates select="n-docbody/judgment"/>

			<!--
				******************************************************************************************************
				* Backlog Item 506268: 
				* Remove all logos from International content. 
				* Add copyright message from royality block and message block centered at the bottom of the document.
				******************************************************************************************************
			-->
			<xsl:apply-templates select="n-docbody/copyright"/>

			<div class="&alignHorizontalLeftClass;">
				<xsl:apply-templates select="$primaryCitation"/>
			</div>
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>

	<!--*************************** CASE.HEAD Block ***************************-->

	<xsl:template match="case.head">
		<div class="&centerClass;">
			<xsl:apply-templates select="citation.group"/>
			<xsl:apply-templates select="party.line"/>
			<xsl:apply-templates select="../image.block" />
			<xsl:apply-templates select="$judgmentDate"/>
			<div class="&paratextMainClass;">&#160;</div>
			<xsl:apply-templates select="court.line/court"/>
			<xsl:call-template name="chooseCaseRefNo" />
		</div>

		<xsl:apply-templates select="judge.line"/>
		<xsl:apply-templates select="counsel.group"/>

		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="citation.group">
		<div class="&citesClass; &centerClass;">
			<xsl:apply-templates select="parallel.citation">
				<xsl:with-param name="type">WL.cite</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="parallel.citation">
				<xsl:with-param name="type">additional</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="primary.citation"/>
			<xsl:apply-templates select="parallel.citation">
				<xsl:with-param name="type">unreported</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="parallel.citation">
				<xsl:with-param name="type">parallelColumn</xsl:with-param>
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<xsl:template match="parallel.citation">
		<xsl:param name="type" />
		<xsl:if test="$type='WL.cite'">
			<xsl:if test="./@type ='WL.cite'">
				<xsl:apply-templates />
				<xsl:text> (</xsl:text>
				<xsl:value-of select="$courtAbbreviation" />
				<xsl:text>),&#13;</xsl:text>
			</xsl:if>
		</xsl:if>
		<xsl:if test="$type='additional'">
			<xsl:if test="./@type ='report' or ./@type ='digest' or ./@type ='PD' or ./@type ='Other'">
				<xsl:apply-templates />
				<xsl:text>, </xsl:text>
			</xsl:if>
		</xsl:if>
		<xsl:if test="$type='unreported'">
			<xsl:if test="./@type ='unreported'">
				<xsl:text>, </xsl:text>
				<xsl:apply-templates />
			</xsl:if>
		</xsl:if>
		<xsl:if test="$type='parallelColumn'">
			<xsl:choose>
				<!-- Swallow WL.cite in parallel citataion column-->
				<xsl:when test="./@type ='WL.cite'" />
				<!-- Swallow West serial # in parallel citataion column-->
				<xsl:when test="./@type ='West.serial'" />
				<xsl:otherwise>
					<div>
						<xsl:apply-templates />
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="primary.citation">
		<xsl:apply-templates/>
	</xsl:template>

	<!--*************** ADDITIONAL CASE.HEAD elements ***************-->

	<xsl:template match="case.head/party.line">
		<div class="&titleClass;">
			<div class="&suitClass;">
				<div class="&partyLineClass;">
					<xsl:apply-templates />
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="date" priority="2">
		<div class="&dateClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="court">
		<div class="&simpleContentBlockClass; &centerClass;">
			<xsl:apply-templates />
			<div>
				<xsl:value-of select="$courtAbbreviation"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="chooseCaseRefNo">
		<!-- case ref group: content may contain multiple case.ref formats-->
		<!-- within multiple case.ref.group elements -->
		<!-- we only want ONE case.ref format (prioritize: long / short / abbrev) -->		
		<xsl:choose>
			<xsl:when test="case.ref.no.group/case.ref.no[@type='long']">
				<xsl:apply-templates select="case.ref.no.group/case.ref.no[@type='long']"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="case.ref.no.group/case.ref.no[@type='short']">
						<xsl:apply-templates select="case.ref.no.group/case.ref.no[@type='short']"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="case.ref.no.group/case.ref.no[@type='abbrev']"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="case.ref.no.group">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="case.ref.no">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="judge.line">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="heading">
		<div class="&headtextClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="judge.body">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="counsel.group">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="counsel.line">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="case.ref[not(parent::xref)]">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="case.ref">
		<xsl:apply-templates />
	</xsl:template>

	<!--*************** CATCHWORDS.GROUP Block ***************-->	
	<xsl:template match="catchwords.group">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="catchwords">
		<div class="&paraMainClass;">
			<xsl:apply-templates select="catchword" />
		</div>
	</xsl:template>
	
	<xsl:template match="catchword">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::catchword">
			<xsl:text> - </xsl:text>
		</xsl:if>
	</xsl:template>

	
	<!--*************** HEADNOTES Block ***************-->
	<xsl:template match="headnotes">
		<xsl:apply-templates />
		<div>&#160;</div>
	</xsl:template>

	
	<!--*************** JUDGMENT Block ***************-->
	<xsl:template match="judgment">
		<xsl:apply-templates />
	</xsl:template>

	
	<!--*************** LIST elements ***************-->
	<xsl:template match="list">
		<xsl:if test="not(parent::para) or self::node()[string-length(parent::para)!=0]">
			<div>&#160;</div>
		</xsl:if>
		<ul class="&listClass; &paraMainClass;">
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<xsl:template match="list/list.item">
		<li class="&paraMainClass;">
			<xsl:apply-templates/>
		</li>
	</xsl:template>


	<!--*************** General text elements ***************-->
	<xsl:template match="para.group" priority="2">
		<div class ="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="para" priority="2">
		<xsl:choose>
			<xsl:when test="parent::block.quote">
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass;">
					<xsl:apply-templates/>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	

	<xsl:template match="label">
		<xsl:apply-templates />
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	
	<!--****************Text Styling**************-->
	<xsl:template match="emphasis">
		<xsl:choose>
			<xsl:when test="@type='italic'">
				<em>
					<xsl:apply-templates />
				</em>
			</xsl:when>
			<xsl:when test="@type='bold'">
				<strong>
					<xsl:apply-templates />
				</strong>
			</xsl:when>
			<xsl:when test="@type='underline'">
				<span class="&underlineClass;">
					<xsl:apply-templates />
				</span>
			</xsl:when>
			<xsl:when test="@type='bold_italic'">
				<strong>
					<em>
						<xsl:apply-templates />
					</em>
				</strong>
			</xsl:when>
			<xsl:when test="@type='bold_underline'">
				<strong>
					<span class="&underlineClass;">
						<xsl:apply-templates />
					</span>
				</strong>
			</xsl:when>
			<xsl:when test="@type='italic_underline'">
				<em>
					<span class="&underlineClass;">
						<xsl:apply-templates />
					</span>
				</em>
			</xsl:when>
			<xsl:when test="@type='bold_italic_underline'">
				<strong>
					<em>
						<span class="&underlineClass;">
							<xsl:apply-templates />
						</span>
					</em>
				</strong>
			</xsl:when>
			<xsl:when test="@type='sup'">
				<sup>
					<xsl:apply-templates />
				</sup>
			</xsl:when>
			<xsl:when test="@type='sub'">
				<sub>
					<xsl:apply-templates />
				</sub>
			</xsl:when>
			<xsl:when test="@type='double'">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--*****************Links in document**********************-->
	<xsl:template match="a[starts-with(@name,'r')]">
		<a class="&footnoteReferenceClass;">
			<xsl:attribute name ="href">
				<xsl:value-of select="normalize-space(@href)"/>
			</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="normalize-space(@name)"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>

	<xsl:template match="md.primarycite">
		<div class="&citesClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="link[@tuuid]">
		<a>
			<xsl:attribute name="href">
				<xsl:call-template name="GetDocumentUrl">
					<xsl:with-param name="documentGuid" select="@tuuid"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:apply-templates select="child::node()" />
		</a>
	</xsl:template>

	<!-- west.level element usually found to be empty -->
	<xsl:template match="west.level">
		<xsl:apply-templates />
	</xsl:template>


	<xsl:template match="tbody/row/entry" priority="5">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<xsl:choose>
			<xsl:when test="string-length(.) &gt; 0">
				<td>
					<xsl:call-template name="RenderTableCell">
						<xsl:with-param name="columnInfo" select="$columnInfo"/>
						<xsl:with-param name="colalign" select="$colalign" />
						<xsl:with-param name="colposition" select="$colposition" />
						<xsl:with-param name="colwidth" select="$colwidth" />
					</xsl:call-template>
				</td>
			</xsl:when>
			<xsl:otherwise>
				<td>
					<xsl:call-template name="RenderTableCell">
						<xsl:with-param name="columnInfo" select="$columnInfo"/>
						<xsl:with-param name="colalign" select="$colalign" />
						<xsl:with-param name="colposition" select="$colposition" />
						<xsl:with-param name="colwidth" select="$colwidth" />
					</xsl:call-template>
					&#160;
				</td>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--******************Copyright Message************************-->
	<xsl:template match="copyright">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>	

</xsl:stylesheet>