<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="ContentsForFootnotesSection" select="//updatenote[not(.=preceding::updatenote) and not(../ins/update/del and not(../ins/update/ins) and not(../ins/*[name()!='update']) and not(../ins/text())) and not(ancestor::snippet-text) and not(ancestor::del)]
				|//footnote-text[not(ancestor::del) and (not(.=preceding::footnote-text))]|//footnote[not(ancestor::del) and (not(.=preceding::footnote))]
				|//mnote[not(ancestor::del) and (not(.=preceding::mnote))]" />

	<xsl:variable name="ListOfFootnotesVar">
		<xsl:for-each select="$ContentsForFootnotesSection">
			-<xsl:value-of select="position()"/>-<xsl:apply-templates select="text()|child::*" />
		</xsl:for-each>
	</xsl:variable>

	<xsl:template name="RenderFootnotes">
		<xsl:param name="footNoteTitle" />
		<!-- Render the footnotes at the bottom of the document (if any) -->
		<xsl:if test="string-length($ListOfFootnotesVar) &gt; 0">
			<div class="&paraMainClass;">&#160;</div>
			<xsl:call-template name="RenderFootnoteSectionMarkup">
				<xsl:with-param name="footNoteTitle" select="$footNoteTitle" />
				<xsl:with-param name="contents">
					<xsl:for-each select="$ContentsForFootnotesSection">
						<xsl:choose>
							<xsl:when test="$DeliveryMode">
								<tr>
									<td>
										<xsl:element name="span">
											<!-- The footnote id attribute needs to be placed on a parent of the reference number as it is used in the pop-up box -->
											<!-- The footnote id attribute needs to be placed on the parent of the <a> element to correctly link in delivered word documents -->
											<xsl:attribute name="id">
												<xsl:text>FN</xsl:text>
												<xsl:value-of select="position()"/>
											</xsl:attribute>
											<xsl:element name="a">
												<xsl:attribute name="href">
													<xsl:text>&#35;co_footnoteReference_</xsl:text>
													<xsl:value-of select="position()"/>
												</xsl:attribute>


												<xsl:value-of select="position()"/>
											</xsl:element>
										</xsl:element>
									</td>
									<td>
										<xsl:apply-templates select="text()|child::*" />
									</td>
								</tr>
							</xsl:when>
							<xsl:otherwise>
								<!--e.g.:  <a href="#FN1">1</a>-->
								<xsl:element name="div">
									<xsl:attribute name="class">&paraMainClass;</xsl:attribute>
									<xsl:element name="div">
										<xsl:attribute name="class">
											<xsl:text>&footnoteNumberClass;</xsl:text>
										</xsl:attribute>
										<xsl:element name="span">
											<xsl:attribute name="id">
												<xsl:text>FN</xsl:text>
												<xsl:value-of select="position()"/>
											</xsl:attribute>
											<xsl:element name="a">
												<xsl:attribute name="href">
													<xsl:text>&#35;co_footnoteReference_</xsl:text>
													<xsl:value-of select="position()"/>
												</xsl:attribute>
												<!-- The footnote id attribute needs to be placed on a parent of the reference number as it is used in the pop-up box -->

												<xsl:value-of select="position()"/>
											</xsl:element>
										</xsl:element>
									</xsl:element>
									<xsl:element name="div">
										<xsl:attribute name="class">&footnoteBodyClass;</xsl:attribute>
										<xsl:apply-templates select="text()|child::*" />
									</xsl:element>
								</xsl:element>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</xsl:with-param>
			</xsl:call-template>

			<div class="&paraMainClass;">&#160;</div>

		</xsl:if>
	</xsl:template>

	<!-- 
	     Render the footnotes section 
		 Note that the markup for footnotes in UK statutes is quite different from other footnote markup.
     -->
	<xsl:template match="update">
		<xsl:variable name="updateId" select="@id" />
		<!--   do not display the update if it's a delete with no insert and has an insert with the same ID immediately following -->
		<xsl:if test="not(del and not(ins) and following-sibling::update[@id=$updateId]/ins)">
			<xsl:choose>
				<!--  if this is an insert containing only a deletion (and no other text/elements), ignore it -->
				<xsl:when test="ins/update/del and not(ins/update/ins) and not(ins/*[name()!='update']) and not(ins/text())">
					<xsl:apply-templates select="del" />
					<xsl:apply-templates select="ins" />
				</xsl:when>
				<xsl:when test="ancestor::para-text or ancestor::title or ancestor::longtitle or ancestor::number or ancestor::defnlist-item or parent::item">
					<xsl:apply-templates select="del" />
					<xsl:apply-templates select="ins" />
				</xsl:when>
				<xsl:when test="ancestor::table and not(ancestor::row)">
					<xsl:apply-templates select="del" />
					<xsl:apply-templates select="ins" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="$DeliveryMode">
							<p>
								<xsl:apply-templates select="del" />
								<xsl:apply-templates select="ins" />
							</p>
						</xsl:when>
						<xsl:otherwise>
							<div class="&paraMainClass;">
								<xsl:apply-templates select="del" />
								<xsl:apply-templates select="ins" />
							</div>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="footnote-text">
		<xsl:call-template name="RenderFootnoteSuperscript" >
			<xsl:with-param name="currentFootnote">
				<xsl:apply-templates select="text()|child::*" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- 
	 *********************************************************************************
	   Render the footnote references in superscript inside the document body. 
		 Since there are no footnotes per se in the XML markup (such as references, numbers, etc.)
		 doing some string manipulation from the list of footnotes variable.
		 Sample format:
		 -1-S.21(2A)-(2C) substituted for s.21(2A) and (2B) by Legal Services Act 2007 (The Law Society and The Council for Licensed Conveyancers) (Modification of Functions) Ord
		 
		 Using the first 100 characters of each footnote to avoid string length issues.
		 If less than 100 characters, use actual string length.
		 
		 NOTE: it would be a better solution, if feasible, to use a third-party node-set() function.
	 *********************************************************************************		 
	-->
	<xsl:template name="RenderFootnoteSuperscript">
		<xsl:param name="currentFootnote"/>

		<xsl:variable name="substringLength">
			<xsl:choose>
				<xsl:when test="string-length($currentFootnote) &lt; 100">
					<xsl:value-of select="string-length($currentFootnote)"/>
				</xsl:when>
				<xsl:otherwise>100</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="footnoteTextBefore" select="substring-before($ListOfFootnotesVar, concat('-', $currentFootnote))"/>
		<!-- footnote number is from 1 to 999 -->
		<xsl:variable name="footnoteTextAfter" select="substring($footnoteTextBefore, string-length($footnoteTextBefore) - 3, 4)"/>
		<xsl:variable name="currentCount" select="substring-after($footnoteTextAfter, '-')"/>

		<xsl:if test="string-length($currentCount) &gt; 0">
			<xsl:variable name="foonoteReference">
				<xsl:text>co_footnoteReference_</xsl:text>
				<xsl:value-of select="$currentCount"/>
			</xsl:variable>
			<sup>
				<!-- id attribute needs to be placed on parent element to enable linking in delivered Word documents -->
				<xsl:attribute name="id">
					<xsl:value-of select="$foonoteReference"/>
				</xsl:attribute>
				<xsl:element name="a">
					<xsl:attribute name="href">
						<xsl:text>&#35;FN</xsl:text>
						<xsl:value-of select="$currentCount"/>
					</xsl:attribute>
					<!-- co_footnoteReference class enables the footnote pop-up box when you hover over the superscript footnote number -->
					<xsl:attribute name="class">&footnoteReferenceClass;</xsl:attribute>
					<xsl:value-of select="$currentCount"/>
				</xsl:element>
			</sup>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderFootnoteSectionMarkup">
		<xsl:param name="footNoteTitle" />
		<xsl:param name="contents"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="RenderFootnoteSectionMarkupTable">
					<xsl:with-param name="contents" select="$contents"/>
					<xsl:with-param name="footNoteTitle" select="$footNoteTitle"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderFootnoteSectionMarkupDiv">
					<xsl:with-param name="contents" select="$contents"/>
					<xsl:with-param name="footNoteTitle" select="$footNoteTitle"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderFootnoteSectionMarkupDiv">
		<xsl:param name="footNoteTitle" />
		<xsl:param name="contents"/>
		<div id="&footnoteSectionId;" class="&footnoteSectionClass;">
			<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
				<xsl:value-of select="$footNoteTitle" />
			</h2>
			<xsl:copy-of select="$contents"/>
		</div>
	</xsl:template>

	<xsl:template name="RenderFootnoteSectionMarkupTable">
		<xsl:param name="footNoteTitle" />
		<xsl:param name="contents"/>
		<table id="&footnoteSectionId;" class="&footnoteSectionClass;">
			<tr>
				<td colspan="2" class="&footnoteSectionTitleClass; &printHeadingClass;">
					<xsl:value-of select="$footNoteTitle" />
				</td>
			</tr>
			<xsl:copy-of select="$contents"/>
		</table>
	</xsl:template>

	<!-- End Render FootNote -->


	<xsl:template match="ins">
		<xsl:choose>
			<xsl:when test="parent::update/parent::thead or parent::update/parent::tbody">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="tbl//thead/row/entry and not(parent::update)">
				<xsl:if test="not(tbl//thead/row/preceding-sibling::tbl//thead/row) and not(tbl//thead/row/entry/preceding-sibling::tbl//thead/row/entry)">
					<xsl:text>[</xsl:text>
				</xsl:if>
				<xsl:apply-templates />
				<xsl:if test="not(tbl//thead/row/following-sibling::tbl//thead/row) and not(tbl//thead/row/entry/following-sibling::tbl//thead/row/entry)">
					<xsl:text>]</xsl:text>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>[</xsl:text>
				<xsl:apply-templates />
				<xsl:text>]</xsl:text>
				<xsl:call-template name="RenderFootnoteSuperscript" >
					<xsl:with-param name="currentFootnote">
						<xsl:apply-templates select="preceding-sibling::updatenote/text()|preceding-sibling::updatenote/child::*"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="entry[ancestor::ins]">
		<xsl:choose>
			<xsl:when test="not(parent::row/preceding-sibling::row) and not(preceding-sibling::entry)">
				<td>
					<div class="&paraMainClass;">
						<xsl:text>[</xsl:text>
						<xsl:apply-templates />
					</div>
				</td>
			</xsl:when>
			<xsl:when test="not(parent::row/following-sibling::row) and not(following-sibling::entry)">
				<td>
					<div class="&paraMainClass;">
						<xsl:apply-templates />
						<xsl:text>]</xsl:text>
						<xsl:call-template name="RenderFootnoteSuperscript" >
							<xsl:with-param name="currentFootnote">
								<xsl:apply-templates select="../../preceding-sibling::updatenote/text()|../../preceding-sibling::updatenote/child::* | preceding-sibling::updatenote/text() | preceding-sibling::updatenote/child::*"/>
							</xsl:with-param>
						</xsl:call-template>
					</div>
				</td>
			</xsl:when>
			<xsl:otherwise>
				<td>
					<div class="&paraMainClass;">
						<xsl:apply-templates />
					</div>
				</td>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="del">
		<xsl:if test="not(preceding-sibling::ins) and not(following-sibling::ins)">
			<xsl:choose>
				<xsl:when test="parent::update/parent::thead or parent::update/parent::tbody">
					<tr>
						<td colspan="{ancestor::tgroup/@cols}" style="vertical-align: top;text-align: left;padding: 0px 0.18in 0px 0px;">
							<div class="&paraMainClass;">
								[...]
								<xsl:call-template name="RenderFootnoteSuperscript" >
									<xsl:with-param name="currentFootnote">
										<xsl:apply-templates select="preceding-sibling::updatenote/text()|preceding-sibling::updatenote/child::*"/>
									</xsl:with-param>
								</xsl:call-template>
							</div>
						</td>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>[...]</xsl:text>
					<xsl:call-template name="RenderFootnoteSuperscript" >
						<xsl:with-param name="currentFootnote">
							<xsl:apply-templates select="preceding-sibling::updatenote/text()|preceding-sibling::updatenote/child::*"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
