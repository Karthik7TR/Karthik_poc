<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="CustomFunctions.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:preserve-space elements="*" />
	<xsl:variable name="preformatDeliveryStyles">
		<xsl:text>font-family: 'Courier New', monospace; font-size: 7pt;</xsl:text>
	</xsl:variable>
	<xsl:variable name="source" select="Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.filing/md.source" />
	<xsl:variable name="isGlobalFilings" select="boolean(Document/map/entry/key[text()='md.doctype.name']/following-sibling::value[text()='13B+9'])" />
	<xsl:variable name="isASXAnnouncements" select="boolean(Document/map/entry/key[text()='md.doctype.name']/following-sibling::value[text()='13B+11'])" />
	<xsl:variable name="formTypeExcludedFromQuickLoadMessage">
		<xsl:variable name="formType" select="Document/n-metadata/md.financial.info.metadata.block/md.filing.metadata.block/md.filing/md.filing.values/md.filing.value/md.normalized.formtype" />
		<xsl:variable name="royaltyCode" select="Document/n-metadata/metadata.block/md.royalty/md.royalty.code" />
		<xsl:choose>
			<xsl:when test="$formType = '3' or $formType = '4' or $formType = '5' or $formType = '144' or $formType = '144A'">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:when test="$source = 'G' or $source = 'K' or $source = 'S' or $source = 'F' or $source = 'I' or $source = 'P' or $source = 'W'">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:when test="$royaltyCode = '4482' or $royaltyCode = '6860'">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="isPreFormattedText">
		<xsl:choose>
			<xsl:when test="Document/n-docbody/filing/filing.body/text/@xml:space = 'preserve' or Document/Section//text/@xml:space = 'preserve' or Document/n-docbody//preformatted">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz'" />
	<xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
	<xsl:variable name="asFiledImagePDFAvailable">
		<xsl:choose>
			<xsl:when test="string(//md.print.rendition.id)">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- TODO: Get rid of this flag. isPreformattedText can be used instead. -->
	<xsl:variable name="isRichTextFormat">
		<xsl:choose>
			<xsl:when test="Document/n-docbody/filing/filing.body/text/@xml:space = 'preserve' or Document/Section//text/@xml:space = 'preserve' or Document/n-docbody//preformatted">
				<xsl:value-of select="false()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="true()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:variable name="documentTop" select="'Top'" />

	<!-- the main match -->
	<xsl:template match="Document">
		<!-- put doc content on display -->
		<div  id="&documentId;">
			<!-- Need document css classes added for correct alignment -->
			<xsl:choose>
				<xsl:when test="$isPreFormattedText=string(true())">
					<xsl:if test="$DeliveryMode=string(true())">
						<xsl:attribute name="style">
							<xsl:value-of select="$preformatDeliveryStyles"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeFilingsAndDisclosures; &preformattedDocument;'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="AddDocumentClasses">
						<xsl:with-param name="contentType" select="'&contentTypeFilingsAndDisclosures;'"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:call-template name="Content" />
		</div>
	</xsl:template>

	<!-- render the CONTENT view based on doctype. -->
	<xsl:template name="Content">
		<!-- Makes content div scrollable or not; default is true-->
		<xsl:param name="isScrollable" select="true()" />
		<xsl:if test="$isScrollable=true() or $isPreFormattedText=string(true())">
			<input type="hidden" id="&fdPreformattedText;" value="true" />
		</xsl:if>
		<xsl:choose>
			<xsl:when test="$isGlobalFilings">
				<xsl:call-template name="ContentGlobalFilings" />
			</xsl:when>
			<xsl:when test="$isASXAnnouncements">
				<xsl:call-template name="ContentASXAnnouncements" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="ContentFilings" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>&#13;</xsl:text>
	</xsl:template>

	<!-- render the CONTENT view for Global Filings. -->
	<xsl:template name="ContentGlobalFilings">
		<div class="&layoutBasicMargin;">
			<div class="&layoutBasicFloat;">
				<xsl:choose>
					<xsl:when test="not(contains(n-docbody,'The text version of this filing is not available.'))">
						<xsl:call-template name="ContentFilings" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'&fdFilingNotAvailableText;'"/>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</div>
	</xsl:template>

	<!-- render the CONTENT view for ASX Announcements. -->
	<xsl:template name="ContentASXAnnouncements">
		<div class="&layoutBasicMargin;">
			<div class="&layoutBasicFloat;">
				<xsl:value-of select="'&fdFilingNotAvailableText;'"/>
			</div>
		</div>
	</xsl:template>

	<!-- 
		Render the CONTENT view for Filings based on the desired VIEW. 
		There were originally 4 views for WAF:
		
			1. Snippet: renders document snippets for enhanced cite list
			2. Outline: renders document outline
			3. GatherImageMetadata: used by contentnav control to determine if  the document has a corresponding PDF image.
			4. Content: renders document (default)
		
		Kept content for Westlaw Next. Content includes rich XML display.
	-->
	<xsl:template name="ContentFilings">
		<xsl:element name="a">
			<xsl:attribute name="id">
				<xsl:value-of select="$documentTop"/>
			</xsl:attribute>
		</xsl:element>

		<xsl:choose>
			<!-- Display a product specific message if document is not available in electronic format-->
			<xsl:when test="not(//filing/*) and ($source = 'P' or $source ='W' or $source = 'F')">
				<br />
				<p>
					<xsl:value-of select="'&fdFilingElecDocNotAvail;'"/>
				</p>
			</xsl:when>
			<xsl:when test="$isRichTextFormat=string(true())">
				<div>
					<xsl:if test="$asFiledImagePDFAvailable=string(false())">
						<xsl:text>&fdAsFiledPdfMissingText;</xsl:text>
					</xsl:if>
					<br />
				</div>
				<xsl:apply-templates />
			</xsl:when>
			<!-- Use pre tags for preformatted documents. -->
			<xsl:otherwise>
				<div>
					<xsl:choose>
						<xsl:when test="$formTypeExcludedFromQuickLoadMessage=string(false())">
							<xsl:choose>
								<xsl:when test="$asFiledImagePDFAvailable=string(true())">
									<xsl:text>&fdQuickLoadVersionWithPDFText;</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>&fdQuickLoadVersionWithoutPDFText;</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
							<br />
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="$asFiledImagePDFAvailable=string(false())">
								<xsl:text>&fdAsFiledPdfMissingText;</xsl:text>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
				</div>
				<pre class="&preformattedTextClass;">
					<xsl:apply-templates />
				</pre>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!--  Processing display.name for content view  -->
	<xsl:template match="display.name">
		<!-- Document will scroll to this anchor when the respective section is clicked in document outline column -->
		<xsl:if test="name(parent::node()) != 'filing.body'">
			<xsl:element name="a">
				<xsl:attribute name="name">
					<xsl:choose>
						<xsl:when test="name(parent::node())='document'">
							<xsl:value-of select="concat('doc', parent::node()/@ID)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="parent::node()/@field.name"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<!-- IE doesn't scroll to named anchors if there is no content in them when they are in tablecells.-->
				<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
			</xsl:element>
		</xsl:if>

		<!-- Render a 'Top' link to scroll to the top of the document -->
		<xsl:if test="not(name(parent::node())='document'and parent::node()/@ID ='1') and name(parent::node()) != 'section.cov' and boolean(parent::node()/text) and name(parent::node()) !='filing.body' and ((substring(name(parent::node()),1,9) ='section.p' and string(number(substring(name(parent::node()),10,1)))!='NaN')=false)" >
			<div class="&layoutBackToTopLink;">
				<xsl:element name="a">
					<xsl:attribute name="class">
						<!--<xsl:value-of select="FeatureNavigation"/>-->
						<xsl:text>&blobLinkClass;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:value-of select="concat('#', $documentTop)"/>
					</xsl:attribute>
					<xsl:value-of select="$documentTop"/>
				</xsl:element>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- 
				*********************
				Rich XML Support Code
				********************* 
	-->

	<!--emph-->
	<!--added because not available in Universal-->
	<xsl:template match="emph">
		<i>
			<xsl:apply-templates />
		</i>
	</xsl:template>

	<!--strong-->
	<!--added because not available in Universal-->
	<xsl:template match="strong">
		<strong>
			<xsl:apply-templates/>
		</strong>
	</xsl:template>

	<xsl:template match="comment()" />

	<!--anchor-->
	<xsl:template match="anchor">
		<xsl:element name="a">
			<xsl:if test="@ID">
				<!-- @ID is required as per dtd but a check has been added just in case to avoid null attribute values. -->
				<xsl:attribute name="name">
					<xsl:value-of select="translate(@ID, $uppercase, $lowercase)" />
				</xsl:attribute>
				<xsl:attribute name="id">
					<xsl:value-of select="translate(@ID, $uppercase, $lowercase)" />
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!--text-->
	<xsl:template match="text" priority="1">
		<xsl:choose>
			<xsl:when test="$isPreFormattedText=string(true())">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="div">
					<xsl:if test="@align or @size">
						<xsl:attribute name="style">
							<xsl:if test="@align">
								<xsl:value-of select="concat('text-align:',@align,';')" />
							</xsl:if>
							<xsl:if test="@size">
								<xsl:value-of select="concat('font-size:',@size,';')" />
							</xsl:if>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		para
		made priority = 1 to avoid conflict with similar template in cobalt's para.xsl
	-->
	<xsl:template match="para" priority="1">
		<xsl:element name="p">
			<xsl:if test="@align">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('text-align:',@align,';')" />
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<!--paratext-->
	<xsl:template match="paratext" priority="1">
		<span>
			<xsl:apply-templates/>
		</span>
	</xsl:template>

	<!--textrule-->
	<xsl:template match="textrule" priority="1">
		<xsl:if test="count(preceding-sibling::node()[name()=name(current())])=0">
			<xsl:element name="span">
				<xsl:attribute name="style">
					<!-- using padding-left so it works in Netscape Navigator -->
					<xsl:value-of select="concat('padding-left:',@length,';')" />
					<!-- using width so it works in Internet Explorer -->
					<xsl:value-of select="concat('width:',@length,';')" />
					border-bottom-style:solid; border-bottom-width:thin;
				</xsl:attribute>
			</xsl:element>
			<xsl:text>&#13;</xsl:text>
		</xsl:if>
	</xsl:template>

	<!--pagebreak-->
	<xsl:template match="pagebreak">
		<span class="&layoutPageBreak;"></span>
	</xsl:template>

	<!--preformatted-->
	<xsl:template match="preformatted">
		<pre class="&preformattedTextClass;">
			<xsl:apply-templates />
		</pre>
	</xsl:template>

	<!--term.definition-->
	<xsl:template match="term.definition">
		<dl>
			<xsl:apply-templates />
		</dl>
	</xsl:template>

	<!--term.definition/term-->
	<xsl:template match="term.definition/term">
		<dt>
			<xsl:apply-templates />
		</dt>
	</xsl:template>

	<!--term.definition/definition-->
	<xsl:template match="term.definition/definition">
		<dd>
			<xsl:apply-templates />
		</dd>
	</xsl:template>

	<!--note-->
	<xsl:template match="note">
		<div class="&layoutRenderNote;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--typo.format-->
	<xsl:template match="typo.format">
		<xsl:element name="span">
			<xsl:if test="@fontname or @pointsize or @relative-size">
				<xsl:attribute name="style">
					<xsl:if test="@fontname">
						<xsl:value-of select="concat('font-family:',@fontname,';')" />
					</xsl:if>
					<xsl:if test="@pointsize">
						<xsl:choose>
							<xsl:when test="string(number(@pointsize)) = 'NaN'">
								<!-- @pointsize is not a number, so it probably has a unit value (px, pt..)-->
								<xsl:value-of select="concat('font-size:',@pointsize)" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat('font-size:',@pointsize,'pt;')" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
					<xsl:if test="@relative-size">
						<xsl:value-of select="concat('font-size:',@relative-size,'%;')" />
					</xsl:if>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- tbl -->
	<xsl:template match="tbl" priority="1">
		<xsl:element name="span">
			<xsl:if test="@align">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('text-align:',@align,';')" />
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!--head-->
	<xsl:template match="head" priority="1">
		<xsl:choose>
			<xsl:when test="@level='1'">
				<xsl:element name="h1">
					<xsl:if test="@align">
						<xsl:attribute name="style">
							<xsl:value-of select="concat('text-align:',@align,';')" />
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:when>
			<xsl:when test="@level='2'">
				<xsl:element name="h2">
					<xsl:if test="@align">
						<xsl:attribute name="style">
							<xsl:value-of select="concat('text-align:',@align,';')" />
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:when>
			<xsl:when test="@level='3'">
				<xsl:element name="h3">
					<xsl:if test="@align">
						<xsl:attribute name="style">
							<xsl:value-of select="concat('text-align:',@align,';')" />
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:when>
			<xsl:when test="@level='4'">
				<xsl:element name="h4">
					<xsl:if test="@align">
						<xsl:attribute name="style">
							<xsl:value-of select="concat('text-align:',@align,';')" />
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:when>
			<xsl:when test="@level='5'">
				<xsl:element name="h5">
					<xsl:if test="@align">
						<xsl:attribute name="style">
							<xsl:value-of select="concat('text-align:',@align,';')" />
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:when>
			<xsl:when test="@level='6'">
				<xsl:element name="h6">
					<xsl:if test="@align">
						<xsl:attribute name="style">
							<xsl:value-of select="concat('text-align:',@align,';')" />
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="div">
					<xsl:if test="@align">
						<xsl:attribute name="style">
							<xsl:value-of select="concat('text-align:',@align,';')" />
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--enhanced.format - do not display-->
	<xsl:template match="enhanced.format" />

	<xsl:template match="text()" name="SpecialCharacterTranslator">
		<xsl:param name="textToTranslate" select="." />

		<xsl:variable name="textWithEncodedDegreeReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textToTranslate" />
				<xsl:with-param name="pattern" select="'&amp;deg;'" />
				<xsl:with-param name="replacement" select="'&#176;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedPlusReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedDegreeReplaced" />
				<xsl:with-param name="pattern" select="'&amp;plus;'" />
				<xsl:with-param name="replacement" select="'&#43;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedLatinIWithAcuteReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedPlusReplaced" />
				<xsl:with-param name="pattern" select="'&amp;iacute;'" />
				<xsl:with-param name="replacement" select="'&#237;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedLatinOWithAcuteReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedLatinIWithAcuteReplaced" />
				<xsl:with-param name="pattern" select="'&amp;oacute;'" />
				<xsl:with-param name="replacement" select="'&#211;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedLatinEWithAcuteReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedLatinOWithAcuteReplaced" />
				<xsl:with-param name="pattern" select="'&amp;eacute;'" />
				<xsl:with-param name="replacement" select="'&#201;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedMDashReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedLatinEWithAcuteReplaced" />
				<xsl:with-param name="pattern" select="'&amp;mdash;'" />
				<xsl:with-param name="replacement" select="'&#8212;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedENDashReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedMDashReplaced" />
				<xsl:with-param name="pattern" select="'&amp;endash;'" />
				<xsl:with-param name="replacement" select="'&#8211;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedEMDashReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedENDashReplaced" />
				<xsl:with-param name="pattern" select="'&amp;emdash;'" />
				<xsl:with-param name="replacement" select="'&#8212;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedPeriodReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedEMDashReplaced" />
				<xsl:with-param name="pattern" select="'&amp;period;'" />
				<xsl:with-param name="replacement" select="'&#46;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedLatinIWithCircumflexReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedPeriodReplaced" />
				<xsl:with-param name="pattern" select="'&amp;Icirc;'" />
				<xsl:with-param name="replacement" select="'&#206;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedLatinAWithTildeReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedLatinIWithCircumflexReplaced" />
				<xsl:with-param name="pattern" select="'&amp;atilde;'" />
				<xsl:with-param name="replacement" select="'&#195;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedLatinCReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedLatinAWithTildeReplaced" />
				<xsl:with-param name="pattern" select="'&amp;ccedil;'" />
				<xsl:with-param name="replacement" select="'&#199;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedLatinAReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedLatinCReplaced" />
				<xsl:with-param name="pattern" select="'&amp;aacute;'" />
				<xsl:with-param name="replacement" select="'&#193;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedLatinOReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedLatinAReplaced" />
				<xsl:with-param name="pattern" select="'&amp;ouml;'" />
				<xsl:with-param name="replacement" select="'&#214;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedEmSpaceReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedLatinOReplaced" />
				<xsl:with-param name="pattern" select="'&amp;emsp;'" />
				<xsl:with-param name="replacement" select="'&#8195;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedRegisteredReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedEmSpaceReplaced" />
				<xsl:with-param name="pattern" select="'&amp;reg;'" />
				<xsl:with-param name="replacement" select="'&#174;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedPercentReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedRegisteredReplaced" />
				<xsl:with-param name="pattern" select="'&amp;percnt;'" />
				<xsl:with-param name="replacement" select="'&#37;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedYenReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedPercentReplaced" />
				<xsl:with-param name="pattern" select="'&amp;yen;'" />
				<xsl:with-param name="replacement" select="'&#165;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedPoundReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedYenReplaced" />
				<xsl:with-param name="pattern" select="'&amp;pound;'" />
				<xsl:with-param name="replacement" select="'&#163;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedDollarReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedPoundReplaced" />
				<xsl:with-param name="pattern" select="'&amp;dollar;'" />
				<xsl:with-param name="replacement" select="'&#36;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedEuroReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedDollarReplaced" />
				<xsl:with-param name="pattern" select="'&amp;euro;'" />
				<xsl:with-param name="replacement" select="'&#8364;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedBulletReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedEuroReplaced" />
				<xsl:with-param name="pattern" select="'&amp;bull;'" />
				<xsl:with-param name="replacement" select="'&#8226;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithEncodedAsteriskReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedBulletReplaced" />
				<xsl:with-param name="pattern" select="'&amp;ast;'" />
				<xsl:with-param name="replacement" select="'*'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithInvalidBulletEntityReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithEncodedAsteriskReplaced" />
				<xsl:with-param name="pattern" select="'&amp;#159;'" />
				<xsl:with-param name="replacement" select="'&#8226;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithAnotherInvalidBulletEntityReplaced">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithInvalidBulletEntityReplaced" />
				<xsl:with-param name="pattern" select="'&amp;#183;'" />
				<xsl:with-param name="replacement" select="'&#8226;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithInvisibleCharacterRemoved">
			<xsl:call-template name="replacethis">
				<xsl:with-param name="string" select="$textWithAnotherInvalidBulletEntityReplaced" />
				<!-- Note: Invisible character (soft-hyphen) alt-0173 is between the '', Needs to be removed or delivery fails -->
				<!-- It MUST be typed directly into the XSL, it does not work when trying to use &#0173; or &shy; or &npsp; -->
				<xsl:with-param name="pattern" select="'­'" />
				<xsl:with-param name="replacement" select="''"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="textWithSpacesReplaced">
			<xsl:value-of select="translate($textWithInvisibleCharacterRemoved, '&specialCharactersToBeReplaced;', '&blankSpaceCharacters;')"/>
		</xsl:variable>

		<xsl:variable name="textWithControlCharactersReplaced">
			<xsl:choose>
				<xsl:when test="$isPreFormattedText='false'">
					<!-- Reduce to normalized spaces -->
					<xsl:call-template name="normalize-space-without-trimming">
						<xsl:with-param name="string">
							<!-- The last two characters are respectively the "line feed" ("new line) and "carriage return" characters that should be removed completely... make sure they STAY LAST! -->
							<xsl:value-of select="translate($textWithSpacesReplaced, '&#x0009;&#x000A;&#x000D;', '&#x0020;')" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<!-- if pre-formatted content, then leave as-is -->
					<xsl:copy-of select="$textWithSpacesReplaced"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="replacethis">
			<xsl:with-param name="string" select="$textWithControlCharactersReplaced" />
			<xsl:with-param name="pattern" select="'&#x2610;'" />
			<xsl:with-param name="replacement">
				<img src="{$Images}&emptyCheckboxPath;" alt="&emptyCheckboxAltText;" class="&alignVerticalMiddleClass;" />
				<xsl:text>&#x200B;</xsl:text>
			</xsl:with-param>
		</xsl:call-template>

	</xsl:template>

	<!-- Replace all instances of a substring with another string-->
	<xsl:template name="replacethis">
		<xsl:param name="string" select="." />
		<xsl:param name="pattern" select="''" />
		<xsl:param name="replacement" select="''" />

		<xsl:choose>
			<xsl:when test="contains($string, $pattern)">
				<xsl:value-of select="substring-before($string, $pattern)" />
				<xsl:copy-of select="$replacement" />
				<xsl:call-template name="replacethis">
					<xsl:with-param name="string" select="substring-after($string, $pattern)" />
					<xsl:with-param name="pattern" select="$pattern" />
					<xsl:with-param name="replacement" select="$replacement" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$string"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="md.print.rendition.id">
		<div id="&documentLinksContainer;" class="&layoutTextAlignLeft;">
			<xsl:choose>
				<xsl:when test="$DeliveryMode and not($DisplayOriginalImageLink)">
					<!-- Do nothing -->
				</xsl:when>
				<xsl:when test="not(string(.))">
					<!-- Show message when guid is not yet available -->
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createDocumentBlobLink">
						<xsl:with-param name="guid" select="."/>
						<xsl:with-param name="targetType" select="@ttype"/>
						<xsl:with-param name="mimeType" select="'&pdfMimeType;'" />
						<xsl:with-param name="contents">
							<xsl:text>&fdOriginalDocumentLinkText;</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
						<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
						<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
						<xsl:with-param name="originationContext" select="'&docOriginalImageOriginationContext;'" />
						<xsl:with-param name="prettyName" select="translate(/Document/document-data/cite//text(),'&space;', '&lowline;')" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>


	<!--Task to remove the ownership data elements - task xyz-->
	<xsl:template match="Document/n-docbody/ownership"/>

	<!--internal.reference-->
	<xsl:template match="internal.reference" name="internalReference">
		<xsl:param name="id" />
		<xsl:param name="refid" select="@refid" />
		<xsl:param name="additionalClass"/>
		<xsl:param name="contents" />
		<xsl:param name="forceLink" select="false()"/>
		<a href="{concat('#', translate($refid, $uppercase, $lowercase))}">
			<xsl:attribute name="class">
				<xsl:text>&internalLinkClass;</xsl:text>
				<xsl:if test="string-length($additionalClass) &gt; 0">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:value-of select="$additionalClass"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="string-length($id) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</a>
	</xsl:template>

	<xsl:template match="tgroup" name="TGroupTemplate">
		<!--if checkNoColWidthExists is true, check if the colWidth attribute is not provided, then assume a 100% width. This is a bandaide to make sure other tests are not effected.-->
		<xsl:param name="checkNoColWidthExists"/>
		<xsl:if test=".//text()">
			<table>
				<xsl:variable name="class">
					<xsl:choose>
						<xsl:when test="parent::table/@frame='top' and not(preceding-sibling::tgroup)">
							<!-- if there are multiple tgroups in a table we only want to put a border on the top one -->
							<xsl:text>&borderTopClass;</xsl:text>
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:when>
						<xsl:when test="parent::table/@frame='bottom' and not(following-sibling::tgroup)">
							<!-- if there are multiple tgroups in a table we only want to put a border on the bottom one -->
							<xsl:text>&borderBottomClass;</xsl:text>
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:when>
						<xsl:when test="parent::table/@frame='topbot'">
							<xsl:if test="not(preceding-sibling::tgroup)">
								<!-- if there are multiple tgroups in a table we only want to put a border on the top one -->
								<xsl:text>&borderTopClass;</xsl:text>
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
							<xsl:if test="not(following-sibling::tgroup)">
								<!-- if there are multiple tgroups in a table we only want to put a border on the bottom one -->
								<xsl:text>&borderBottomClass;</xsl:text>
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
						</xsl:when>
						<xsl:when test="parent::table/@frame='all'">
							<xsl:if test="not(preceding-sibling::tgroup)">
								<!-- if there are multiple tgroups in a table we only want to put a border on the top one -->
								<xsl:text>&borderTopClass;</xsl:text>
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
							<xsl:if test="not(following-sibling::tgroup)">
								<!-- if there are multiple tgroups in a table we only want to put a border on the bottom one -->
								<xsl:text>&borderBottomClass;</xsl:text>
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
							<xsl:text>&borderLeftClass;</xsl:text>
							<xsl:text><![CDATA[ ]]></xsl:text>
							<xsl:text>&borderRightClass;</xsl:text>
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:when>
						<xsl:when test="parent::table/@frame='sides'">
							<xsl:text>&borderLeftClass;</xsl:text>
							<xsl:text><![CDATA[ ]]></xsl:text>
							<xsl:text>&borderRightClass;</xsl:text>
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:when>
					</xsl:choose>
				</xsl:variable>

				<xsl:if test="string-length($class) &gt; 0">
					<xsl:attribute name="class">
						<xsl:value-of select="$class"/>
					</xsl:attribute>
				</xsl:if>

				<!-- table width -->
				<!--<xsl:choose>
					<xsl:when test="$checkNoColWidthExists  and descendant::colspec[not(@colwidth)]">
						<xsl:attribute name="style">
							<xsl:text>width:100%;</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="descendant::colspec/@colwidth[contains(.,'%')] ">
					<xsl:attribute name="style">
						<xsl:text>width:100%;</xsl:text>
					</xsl:attribute>
					</xsl:when>
          
				</xsl:choose>-->

				<xsl:attribute name="style">
					<xsl:text>width:100%;</xsl:text>
				</xsl:attribute>

				<xsl:variable name="colspecCount" select="count(colspec)" />

				<xsl:variable name="moreColumnsThanDefined" select="node()[self::thead or self::tbody]/row[count(entry) &gt; $colspecCount][1]" />

				<xsl:variable name="columnInfo" select="colspec[not($moreColumnsThanDefined)]" />

				<xsl:variable name="proportionalTotal">
					<xsl:choose>
						<xsl:when test="contains($columnInfo/@colwidth, '*')">
							<xsl:call-template name="sumProportionalWidths">
								<xsl:with-param name="nodes" select="$columnInfo/@colwidth"/>
								<xsl:with-param name="total" select="0"/>
								<xsl:with-param name="index" select="1"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>0</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:apply-templates>
					<xsl:with-param name="columnInfo" select="$columnInfo"/>
					<xsl:with-param name="proportionalTotal" select="$proportionalTotal"/>
				</xsl:apply-templates>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="EndOfDocumentHeader">
		<xsl:text disable-output-escaping="yes">&lt;!--</xsl:text>
		<xsl:value-of select="'&EndOfDocumentHead;'"/>
		<xsl:text disable-output-escaping="yes">--&gt;</xsl:text>
	</xsl:template>

</xsl:stylesheet>
