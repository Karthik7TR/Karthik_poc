<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="FootnoteBlock.xsl"/>
	<xsl:include href="Prelim.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>
	<!--<xsl:include href="List.xsl"/>-->
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<!-- Render Citation Heading -->
			<!--<xsl:apply-templates select ="descendant::citation.block"/>-->

			<!-- Render PDF links first (if any) -->
			<xsl:if test="n-docbody/decision/content.block/image.block">
				<xsl:apply-templates select="n-docbody/decision/content.block/image.block" />
			</xsl:if>

			<!-- Render Parallel Cites -->
			<xsl:apply-templates select="n-docbody/decision/content.block/citation.block/cite.line" />

			<!-- Render internal links (Outline) -->
			<xsl:apply-templates select ="n-metadata/metadata.block/md.outline.block/md.entry"/>

			<!-- Render the rest of the document -->
			<xsl:apply-templates select ="n-docbody/decision/content.block" />

			<xsl:apply-templates select="n-docbody/decision/content.block/citation.block/cite.line/primary.cite" />

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>

	<!-- Citation Heading  -->
	<!--
	<xsl:template match ="citation.block//primary.cite | citation.block//parallel.cite" priority="1">
		<xsl:apply-templates />
		<xsl:if test ="following-sibling::parallel.cite">
			<xsl:text>, </xsl:text>
		</xsl:if>
	</xsl:template>-->


	<!-- Render the rest of the document-->
	<xsl:template match ="content.block">

		<xsl:apply-templates select ="title.block|court.block|date.block|catchphrase.block|panel.block|synopsis|action.summary.block|
								 docket.block|editorial.note.block"/>

		<xsl:comment>&EndOfDocumentHead;</xsl:comment>

		<xsl:apply-templates select ="reference.block|action.block|attorney.block|result.block|opinion.block|solicitor.block|
								 reporter.block|footnote.block|message.block|argument.block"/>
	</xsl:template>

	<!-- Parallel Cites -->
	<xsl:template match="cite.line">
		<div class="&paraMainClass;">
			<xsl:apply-templates select="parallel.cite" />
		</div>
	</xsl:template>

	<xsl:template match="parallel.cite">
		<xsl:if test="preceding-sibling::parallel.cite">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Image -->
	<xsl:template match="image.block">
		<xsl:if test="string-length(. &gt; 0)">
			<div class="&paraMainClass;">
				<xsl:call-template name="imageBlock"/>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Title -->
	<xsl:template match ="title.block" priority="1">
		<div class="&paraMainClass;" style="text-align: center">
			<xsl:if test ="nickname">
				<xsl:apply-templates select ="nickname"/>
				<br/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test ="primary.title">
					<div style="font-style:italic">
						<xsl:apply-templates select ="primary.title"/>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select ="short.title"/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<!-- Party Line -->
	<xsl:template match ="short.title//party.line">
		<!-- Hardcode an 'and' value for chunked docs that are missing the element -->
		<xsl:if test ="preceding-sibling::party.line">
			<br/>
			<xsl:text>and</xsl:text>
		</xsl:if>
		<br/>
		<!--<xsl:apply-templates />-->
		<xsl:value-of select="."/>
	</xsl:template>


	<!-- Judge -->
	<xsl:template match ="panel.block">
		<div class="&paraMainClass;" style="text-align: center">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match ="judge">
		<xsl:apply-templates/>
		<!--<xsl:text> </xsl:text>-->
	</xsl:template>

	<xsl:template match ="judicial.term">
		<em>
			<xsl:apply-templates/>
		</em>
	</xsl:template>

	<!-- Date -->
	<xsl:template match ="date.block" priority="5">
		<div class="&paraMainClass;" style="text-align: center">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match ="date.line" priority="5">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match ="date" priority="5">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Court Line -->
	<xsl:template match ="court.block">
		<!--<br/>-->
		<div style="text-align: center">
			<xsl:apply-templates select ="court.line"/>
		</div>
	</xsl:template>

	<xsl:template match ="court.line">
		<div class="&paraMainClass;">
			<xsl:apply-templates select="court|court.division|court.bench|registry"/>
		</div>
	</xsl:template>

	<!-- **********************************************************************
	****************** Detailed Case Information Section **********************
	************************************************************************-->
	<!-- Synopsis -->
	<xsl:template match ="synopsis">
		<div class="&indentLeft2Class;">
			<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		</div>
	</xsl:template>

	<xsl:template match ="synopsis.body">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- action.block-->
	<xsl:template match="action.block" >
		<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="reporter.block | order.block | opinion.block | date.line">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="attorney.line | attorney.block | result.block | result.line ">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:strip-space elements="panel.line date.line"/>

	<!-- Judge-->
	<xsl:template match ="opinion//author.line ">
		<div class="&layoutTextAlignLeft; &paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Opinion -->
	<!--<xsl:template match ="opinion.body">
		<br/>
		<div class="&indentLeft2Class;">
			<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		</div>
	</xsl:template>-->

	<xsl:template match ="section.body[not(ancestor::reference.block)]">
		<div class="&indentLeft2Class; &paraMainClass;">
			<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		</div>
	</xsl:template>

	<!-- para -->
	<xsl:template match ="para[@style='block_quote']">
		<br/>
		<xsl:call-template name="renderParagraphTextDiv">
			<xsl:with-param name="contents">
				<blockquote>
					<div>
						<xsl:apply-templates/>
					</div>
				</blockquote>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="para" mode="listpara">
		<li>
			<xsl:apply-templates />
		</li>
	</xsl:template>

	<xsl:template match="para//para[preceding-sibling::paratext and not(child::list) and not(@style='block_quote')]">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Representation -->
	<xsl:template match="solicitor.block">
		<!--Display order.block content above solicitor-->
		<div class="&paraMainClass; &indentLeft2Class;">
			<br/>
			<xsl:apply-templates select ="parent::content.block/order.block"/>
		</div>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Attorney Line -->
	<xsl:template match ="solicitor.line">
		<br/>
		<br/>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Docket Number-->
	<xsl:template match ="docket.block">
		<xsl:if test ="docket.number">
			<div class="&paraMainClass;" style="text-align: center">
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match ="docket.number">
		<xsl:if test="preceding-sibling::docket.number">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Keywords -->
	<xsl:template match ="catchphrase.block">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Keywords -->
	<xsl:template match ="catchphrase.para">
		<br/>
		<br/>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Reference -->
	<xsl:template match ="reference.block">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Header Text -->
	<!-- Added the &headtextClass; class to add a little spacing above these elements -->
	<xsl:template match="headtext">
		<div class="&paraMainClass; &headtextClass;">
			<strong>
				<xsl:apply-templates/>
			</strong>
		</div>
	</xsl:template>

	<!-- create internal doc link for outline -->
	<xsl:template match="dest-id">
		<xsl:if test="@dest and string-length(@dest &gt; 0)">
			<xsl:variable name="destId" select="@dest" />
			<span class="&paraMainClass;" id="{$destId}"></span>
		</xsl:if>
	</xsl:template>

	<xsl:template match="list">
		<br />
		<ul class="&listClass;">
			<xsl:apply-templates mode="listpara"/>
		</ul>
	</xsl:template>

	<!-- Outline -->
	<xsl:template match="md.entry">
		<xsl:choose>
			<xsl:when test="@refid and string-length(@refid &gt; 0)">
				<xsl:variable name="href" select="@refid"/>
				<div class="&paratextMainClass;">
					<a href="#{$href}">
						<xsl:apply-templates />
					</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Create External Links-->
	<xsl:template match ="cite">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="section">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="section.body//paratext">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- START OF SPECIAL LABEL HANDLING. Be careful with what you change here, it may have unintended side-effects. -->
	<!-- There are many use cases handled here and starpage elements can appear anywhere. -->

	<xsl:template match ="paratext[preceding-sibling::pdc.number] | paratext[preceding-sibling::label]">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Do not create divs if the previous sibling was some sort of label. Extra divs will create an undesirable line break after the label. -->
	<xsl:template match ="para[preceding-sibling::*[1][(local-name() = 'pdc.number') or (local-name() = 'label')]]">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Do not create divs if the parent's previous sibling was not a para or paratext element. Extra divs will create an undesirable line breaks. -->
	<xsl:template match ="paratext[parent::para[preceding-sibling::*[1][(local-name() != 'para') and (local-name() != 'paratext')]]]">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Do not create divs if the parent's previous sibling was a paratext element that only contains starpage information. -->
	<xsl:template match ="paratext[parent::para[preceding-sibling::*[1][(local-name() = 'paratext') and starpage and not(./text())]]]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match ="pdc.number | label ">
		<xsl:apply-templates />
		<xsl:text>&nbsp;&nbsp;</xsl:text>
	</xsl:template>

	<!-- END OF SPECIAL LABEL HANDLING. -->

	<!-- **********************************************************************-->

	<!-- Reporter Line -->
	<xsl:template match ="reporter.line">
		<br/>
		<br/>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- Action Line -->
	<xsl:template match="action.line">
		<div class="&paraMainClass;" style="text-align: center">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--
	  ******************************************************************************************************
		* Backlog Item 506268: 
		* Remove all logos from International content. 
		* Add copyright message from royality block and message block centered at the bottom of the document.
		******************************************************************************************************
	-->
	<xsl:template match="message.block">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>

</xsl:stylesheet>