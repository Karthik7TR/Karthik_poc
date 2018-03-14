<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 2/10/2014 -->
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
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="primarycite" select="Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite" />

	<xsl:variable name="content-metadata-block" select="Document/n-metadata/metadata.block" />
	<xsl:variable name="infoType" select="$content-metadata-block/md.infotype/text()" />
	<xsl:variable name="pubname" select="$content-metadata-block/md.publications/md.publication/md.pubname/text()" />
	<xsl:variable name="country" select="Document/n-docbody/header/prelim/country" />
	<xsl:variable name="docType" select="Document/map/entry[key='md.doctype.name']/value" />
	<xsl:variable name="icdrInfoType" select="'wl-icdr'"/>
	<xsl:variable name="icdrIntlRulesInfoType" select="'wli_icdr'"/>
	<xsl:variable name="otherInfoType" select="'Other'"/>
	<xsl:variable name="parisRulesInfoType" select="'wl-caprule'"/>
	<xsl:variable name="parisAwardsInfoType" select="'wl-capawd'"/>
	<xsl:variable name="appawdInfoType" select="'wli_newsl_appawd'"/>
	<xsl:variable name="appDigInfoType" select="'wli_treatise_apple'"/>
	<xsl:variable name="iccawdInfoType" select="'wli_newsl_iccawd'"/>
	<xsl:variable name="cietacawdInfoType" select="'wli_newsl_cietacawd'"/>
	<xsl:variable name="icaWatchInfoType" select="'wli_legis_icawatch'"/>
	<xsl:variable name="espicaInfoType" select="'wli_legis_espica'"/>
	<xsl:variable name="sgicaInfoType" select="'wli_legis_sgica'"/>
	<xsl:variable name="closaInfoType" select="'wli_closa'"/>
	<xsl:variable name="australiaTreatises" select="'wli_treatise_compau'"/>


	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<xsl:apply-templates select ="n-docbody"/>
			<!-- Render copyright message -->
			<xsl:apply-templates select="n-docbody/copyright-message" />
			<xsl:if test="$infoType = $parisRulesInfoType or $infoType = $parisAwardsInfoType">
				<xsl:apply-templates select="n-docbody/header/prelim/copyright" mode="parisRules" />
			</xsl:if>
			<xsl:call-template name="RenderFootnote" />

			<div class="&alignHorizontalLeftClass;">
				<div class="&paratextMainClass;">&#160;</div>
				<xsl:apply-templates select="$primarycite" />
			</div>

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>

	<xsl:template match="n-docbody" priority="2">
		<!-- Render document header -->
		<xsl:apply-templates select ="header" />
		<!-- Render internal links (Outline) -->
		<xsl:if test="$infoType != $espicaInfoType and $infoType != $appDigInfoType">
			<!-- Outline is rendered within the header for espicaInfoType -->
			<xsl:apply-templates select ="/Document/n-metadata/metadata.block/md.outline.block"/>
		</xsl:if>
		<!-- Render the rest of the document -->
		<xsl:apply-templates select ="*[not(self::header)]" />
	</xsl:template>

	<xsl:template match="md.outline.block" mode="addDig">
		<xsl:apply-templates />
	</xsl:template>
	

	<!-- paragraph -->
	<xsl:template match="p" priority="2">
		<xsl:choose>
			<xsl:when test="child::sup[child::a[starts-with(@name, 'f')]]" >
				<!-- suppress footnotes, they are rendered by a separate template-->
			</xsl:when>
			<xsl:when test="parent::source | parent::case_history | parent::related_case | parent::citations | parent::cited | parent::treaty">
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="&indentLeft1Class; &paraMainClass;">
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="u">
		<span class="&underlineClass;">
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<xsl:template match="b">
		<strong>
			<xsl:apply-templates />
		</strong>
		<!-- Bug# 510078 to fix no-spacing issue when delivered to msword -->
		<xsl:if test="$DeliveryMode and string-length(parent::p[not(.)]/text())&lt;1">
			<xsl:text>&#160;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="p/c" >
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Italics -->
	<xsl:template match="p/i">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<em>
				<xsl:copy-of select="$contents"/>
			</em>
		</xsl:if>
	</xsl:template>

	<!-- Outline -->
	<xsl:template match="n-metadata//md.outline.block[//md.infotype/text() = 'wli_treatise_compau']" >
		<div>
			<xsl:for-each select="//dest-id[ancestor::text and (parent::p or parent::article or parent::head1 or parent::head2 or parent::head3 or parent::head4 or parent::head5 or parent::head6 or parent::head7 or parent::head8 or parent::head9 or parent::head10)]">
				<div>
					<a>
						<xsl:attribute name="href">
							<xsl:text>#</xsl:text>
							<xsl:value-of select="concat('&internalLinkIdPrefix;',translate(translate(@dest,'(','_'),')',''))"/>
						</xsl:attribute>
						<xsl:value-of select="following-sibling::*[1]" />
					</a>
				</div>
			</xsl:for-each>
		</div>
		<div>&#160;</div>
	</xsl:template>

	<xsl:template match="dest-id[not(parent::article or parent::head1 or parent::head2 or parent::head3 or parent::head4 or parent::head5 or parent::head6 or parent::head7 or parent::head8 or parent::head9 or parent::head10)]">
		<a>
			<xsl:attribute name="id">
				<xsl:value-of select="concat('&internalLinkIdPrefix;',translate(translate(@dest,'(','_'),')',''))"/>
			</xsl:attribute>
		</a>
	</xsl:template>

	<!-- anchor for external pinpoint links throught cite.query-->
	<xsl:template match="pn_number">
		<xsl:variable name="mdPubId" select="concat(//md.publication//md.pubid/text(),'_')"/>
		<xsl:variable name="pinpointNumber" select="normalize-space(translate(translate(.,'\[','_'),'\]',''))"/>
		<xsl:variable name="prefix" select="concat('&pinpointIdPrefix;','sp_')"/>
		<a>
			<xsl:attribute name="id">
				<xsl:value-of select="concat(concat($prefix,$mdPubId),$pinpointNumber)"/>
			</xsl:attribute>
		</a>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="head1 | head2 | head3 | head4 | head5 | head6 | head7 | head8 | head9 | head10 | article">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;',translate(translate(dest-id/@dest,'(','_'),')',''))"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="RenderTextOutline">
		<xsl:if test="count(//dest-id[ancestor::text and (parent::article or parent::head1 or parent::head2 or parent::head3 or parent::head4 or parent::head5 or parent::head6 or parent::head7 or parent::head8 or parent::head9 or parent::head10)]) > 0">
			<div id="co_textOutline">
				<xsl:if test="not($infoType=$cietacawdInfoType) and not($infoType=$appDigInfoType)" >
					<div class="&centerClass;">
						<xsl:text>&intlTreatDocumentOutlineText;</xsl:text>
					</div>
				</xsl:if>
				<xsl:if test="$infoType=$appDigInfoType">
					<div>&#160;</div>
					<div class="&paraMainClass;">
						<xsl:text>&intlTreatArticleOutlineText;</xsl:text>
					</div>
				</xsl:if>				
				<xsl:for-each select="//dest-id[ancestor::text and (parent::article or parent::head1 or parent::head2 or parent::head3 or parent::head4 or parent::head5 or parent::head6 or parent::head7 or parent::head8 or parent::head9 or parent::head10)]">
					<div>
						<a>
							<xsl:attribute name="href">
								<xsl:text>#</xsl:text>
								<xsl:value-of select="concat('&internalLinkIdPrefix;',translate(translate(@dest,'(','_'),')',''))"/>
							</xsl:attribute>
							<xsl:value-of select="following-sibling::*[1]" />
						</a>
					</div>
				</xsl:for-each>
			</div>
			<div>&#160;</div>
		</xsl:if>
	</xsl:template>

	<!-- header -->
	<xsl:template match="header">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="prelim/image.block"/>

			<xsl:choose>
				<xsl:when test="$infoType = 'wli_bitreaty'">
					<xsl:apply-templates select="citation"/>
					<xsl:apply-templates select="language"/>
					<xsl:apply-templates select="title"/>
					<xsl:apply-templates select="prelim/country"/>
					<xsl:apply-templates select="dates"/>
					<xsl:apply-templates select="prelim/copyright"/>
					<xsl:apply-templates select="keywords"/>
					<!-- End of document head -->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				</xsl:when>
				<!--Australia treatises-->
				<xsl:when test="$infoType = $australiaTreatises">
					<xsl:apply-templates select="citation"/>
					<xsl:apply-templates select="prelim"/>
					<xsl:apply-templates select="author"/>
					<xsl:apply-templates select="title"/>
					<xsl:apply-templates select="source"/>
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/copyright"/>
					</div>
					<!-- End of document head -->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				</xsl:when>
				<!-- STAIDWK Infotype - State Aid Weekly e-News -->
				<xsl:when test="$infoType = 'wli_newsl_staidwk'">
					<xsl:apply-templates select="citation"/>
					<div class="&centerClass;">
						<xsl:apply-templates select="source"/>
						<xsl:apply-templates select="prelim/book_title"/>
						<xsl:apply-templates select="prelim/edition"/>
						<xsl:apply-templates select="prelim/year"/>
						<div class="&copyrightClass;">
							<xsl:apply-templates select="prelim/copyright"/>
						</div>
					</div>
					<!-- End of document head -->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				</xsl:when>
				<!-- EU Press Infotype -->
				<xsl:when test="$infoType = 'wli_newsl_eupress'">
					<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite"/>
					<xsl:apply-templates select="prelim/document.type"/>
					<div class="&centerClass;">
						<xsl:apply-templates select="title"/>
						<xsl:apply-templates select="source"/>
						<xsl:apply-templates select="citation"/>
						<div>&#160;</div>
						<xsl:apply-templates select="//dateDisplay"/>
						<div class="&copyrightClass;">
							<xsl:apply-templates select="prelim/copyright"/>
						</div>
					</div>
					<!-- End of document head -->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				</xsl:when>

				<!-- 
				**************************************************
				*            Appleton Awards                     *
				* World Trade Organization and General Agreement *
				* on Tariffs and Trade Secretariat Awards        *
				**************************************************
			-->
				<xsl:when test="$infoType=$appawdInfoType">
					<!-- 
					Header section
				-->
					<xsl:apply-templates select="citation" />
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/institution" />
						<xsl:text>&#160;</xsl:text>
						<xsl:apply-templates select="prelim/institution-acronym"/>
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/copyright" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/dtype" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="title" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="party"/>
					</div>
					<!-- 
					End of document head 
				-->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
					<!-- 
					Arbitration section 
				-->
					<div>&#160;</div>
					<div class="&paraMainClass;">
						<strong>
							<xsl:apply-templates select="arbitration.industry.code" />
							<div>
								<xsl:apply-templates select="prelim/ctype" />
							</div>
							<div>
								<xsl:apply-templates select="arbitration.award.amount.plaintiff" />
							</div>
							<div>
								<xsl:apply-templates select="attorney.plaintiff" />
							</div>
							<div>
								<xsl:apply-templates select="attorney.defendant" />
							</div>
							<div>
								<xsl:apply-templates select="dates/date" />
							</div>
							<div>
								<xsl:apply-templates select="arbitrator" />
							</div>
							<div>
								<xsl:apply-templates select="prelim/country" />
							</div>
							<div>
								<xsl:apply-templates select="place" />
							</div>
							<div>
								<xsl:apply-templates select="language" mode="prelim" />
							</div>
						</strong>
					</div>

					<xsl:if test="case_history">
						<div>
							<xsl:apply-templates select="case_history" />
						</div>
					</xsl:if>
					<xsl:if  test="related_case" >
						<div>
							<xsl:apply-templates select="related_case" />
						</div>
					</xsl:if>
					<xsl:if test="item">
						<div>
							<xsl:apply-templates select="item" />
						</div>
					</xsl:if>
				</xsl:when>

			  <!-- 
				**************************************************
				*            Appleton Digest                     *
				*      infotype = wli_treatise_apple             *
				**************************************************
			  -->
				<xsl:when test="$infoType=$appDigInfoType">
					<!-- 
					Header section
					-->
					<div class="&centerClass;">
						<xsl:apply-templates select="citation" mode="noStyling" />
						<xsl:text>, </xsl:text><xsl:apply-templates select="second_citation"/>
						<xsl:text> (</xsl:text><xsl:apply-templates select="//md.juris.code"/><xsl:text>)</xsl:text>
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="source" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="title" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/copyright"/>
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/year"/>
					</div>
					<!-- 
					End of document head 
				  -->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
					<div class="&paraMainClass;">
						<xsl:call-template name="RenderTextOutline" />
					</div>
					<div class="&paraMainClass;">
						<xsl:apply-templates select="keywords" mode="appDigInfoType" />
					</div>
				</xsl:when>
				
				<!-- 
					*******************************************
				  *            Cietac Awards                *
					* China International Economic            *
					* Trade & Arbitration Commission - Awards *
					*******************************************
				-->
				<xsl:when test="$infoType=$cietacawdInfoType">
					<!-- 
						Header section
					-->
					<xsl:apply-templates select="citation" />
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/institution" />
						<xsl:text>&#160;</xsl:text>
						<xsl:apply-templates select="prelim/institution-acronym"/>
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/copyright" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/dtype" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="title" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="party"/>
					</div>
					<!-- 
						End of document head 
					-->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
					<!-- 
						Arbitration section 
					-->
					<div class="&paraMainClass;">
						<strong>
							<xsl:apply-templates select="arbitration.industry.code" />
							<div>
								<xsl:apply-templates select="prelim/ctype" />
							</div>
							<div>
								<xsl:apply-templates select="arbitration.award.amount.plaintiff" />
							</div>
							<div>
								<xsl:apply-templates select="attorney.plaintiff" />
							</div>
							<div>
								<xsl:apply-templates select="attorney.defendant" />
							</div>
							<div>
								<xsl:apply-templates select="dates/date" />
							</div>
							<div>
								<xsl:apply-templates select="arbitrator" />
							</div>
							<div>
								<xsl:apply-templates select="prelim/country" />
							</div>
							<div>
								<xsl:apply-templates select="place" />
							</div>
							<div>
								<xsl:apply-templates select="language" mode="prelim" />
							</div>
						</strong>
					</div>
				</xsl:when>


				<!-- 
					**********************************************************
					* Spain's ICA legislation Doctype 8L signon espica-Legis *
					* Spanish International Commercial Arbitration -         *
					* Legislation (Spanish & English) (ESPICA-LEGIS)         *
					**********************************************************
				-->
				<xsl:when test="$infoType=$espicaInfoType">
					<!-- 
						Header section
					-->
					<div>
						<xsl:apply-templates select="citation" />
					</div>
					<div>
						<xsl:apply-templates select="$content-metadata-block/md.publications/md.publication/md.pubname/text()" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/institution" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/dtype" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/country" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="title" />
					</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="dates" />
					</div>
					<!-- 
						End of document head 
					-->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
					<!-- 
						Arbitration section 
					-->
					<div>
						<xsl:apply-templates select="organisation" />
					</div>
					<div>
						<xsl:apply-templates select="place" />
					</div>
					<div>
						<xsl:apply-templates select="language" />
					</div>
					<div>
						<xsl:apply-templates select="author" />
					</div>
					<div>
						<xsl:call-template name="RenderTextOutline" />
					</div>
					<div>
						<xsl:apply-templates select="source" />
					</div>
					<div>
						<xsl:apply-templates select="related_case" />
					</div>
					<div>
						<xsl:apply-templates select="item" />
					</div>
					<div>
						<xsl:apply-templates select="case_history" />
					</div>
				</xsl:when>


				<!-- 
					***************************************************
					* Singapore's ICA legislation Doctype 8L          *
					* signon sgica-Legis                              *
					***************************************************
				-->
				<xsl:when test="$infoType=$sgicaInfoType">
					<!-- 
					Header section
				-->
					<xsl:apply-templates select="citation" />
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/institution" />
						<div>
							<xsl:apply-templates select="prelim/copyright" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/dtype" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/country" />
						</div>
						<div>
							<xsl:apply-templates select="title" />
						</div>
						<div>
							<xsl:apply-templates select="dates" />
						</div>
					</div>
					<!-- 
					End of document head 
				-->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
					<div>
						<xsl:apply-templates select="organisation" />
					</div>
					<div>
						<xsl:apply-templates select="place" />
					</div>
					<div>
						<xsl:apply-templates select="language" />
					</div>
					<div>
						<xsl:apply-templates select="source" />
					</div>
					<div>
						<xsl:apply-templates select="related_case" />
					</div>
					<div>
						<xsl:apply-templates select="item" />
					</div>
					<div>
						<xsl:apply-templates select="case_history" />
					</div>
				</xsl:when>


				<!-- 
					***************************************************
					* ICA-Watch Doctype 8G signon ICA-WATCH,          *
					* ICA-ATISSUE and ICA-INTLAWNEWS                  *
					***************************************************
				-->
				<xsl:when test="contains($docType, '8G')and ($infoType=$otherInfoType)" >
					<xsl:apply-templates select="citation" />
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/institution" />
						<div>
							<xsl:apply-templates select="prelim/copyright" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/dtype" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/country" />
						</div>
						<div>
							<xsl:apply-templates select="title" />
						</div>
					</div>
				</xsl:when>


				<!-- 
					***************************************************
					* ICA-Watch Doctype 8G signon ICA-WATCH,          *
					* ICA-ATISSUE and ICA-INTLAWNEWS                  *
					***************************************************
				-->
				<xsl:when test="$infoType=$icaWatchInfoType">
					<xsl:apply-templates select="citation" />
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/institution" />
						<div>
							<xsl:apply-templates select="prelim/copyright" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/dtype" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/country" />
						</div>
						<div>
							<xsl:apply-templates select="title" />
						</div>
					</div>
					<!-- 
					End of document head 
				-->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
					<xsl:if test="organisation">
						<div>
							<xsl:apply-templates select="organisation" />
						</div>
					</xsl:if>
					<xsl:if test="place">
						<div>
							<xsl:apply-templates select="place" />
						</div>
					</xsl:if>
					<xsl:if test="language">
						<div>
							<xsl:apply-templates select="language" />
						</div>
					</xsl:if>
					<xsl:if test="author">
						<div>
							<xsl:apply-templates select="author" />
						</div>
					</xsl:if>
					<xsl:if test="dates/date">
						<xsl:apply-templates select="dates/date" />
						<br />
						<br />
					</xsl:if>
					<!-- Tracker #138970: added contributor -->
					<xsl:apply-templates select="contributor" />
					<xsl:if test="source">
						<xsl:apply-templates select="source" />
					</xsl:if>
					<xsl:if  test="related_case" >
						<div>
							<xsl:apply-templates select="related_case" />
						</div>
					</xsl:if>
					<xsl:if test="item">
						<div>
							<xsl:apply-templates select="item" />
						</div>
					</xsl:if>
					<xsl:if test="case_history">
						<div>
							<xsl:apply-templates select="case_history" />
						</div>
					</xsl:if>
				</xsl:when>

				<!-- 
					****************************
					* US ICDR                  *
					****************************
				-->
				<xsl:when test="$infoType=$icdrInfoType">
					<div class="&centerClass;">
						<div>
							<xsl:apply-templates select="citation" />
							<xsl:text> (ICDR)</xsl:text>
						</div>
						<div>
							<xsl:apply-templates select="prelim/dtype" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/institution" />
							<xsl:apply-templates select="source" />
						</div>
						<div>
							<xsl:apply-templates select="court" />
						</div>
						<div>
							<xsl:apply-templates select="title" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/docket.number" />
						</div>
					</div>
					<!-- 
						End of document head 
					-->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
					<div>
						<strong>
							<div>
								<xsl:apply-templates select="arbitration.industry.code" />
							</div>
							<div>
								<xsl:apply-templates select="prelim/ctype" />
							</div>
							<div>
								<xsl:apply-templates select="arbitration.award.amount.plaintiff" />
							</div>
							<div>
								<xsl:apply-templates select="attorney.plaintiff" />
							</div>
							<div>
								<xsl:apply-templates select="attorney.defendant" />
							</div>
							<div>
								<xsl:apply-templates select="dates" />
							</div>
							<div>
								<xsl:apply-templates select="arbitrator" />
							</div>
							<div>
								<xsl:apply-templates select="prelim/country" />
							</div>
						</strong>
					</div>
				</xsl:when>

				<!-- 
					*******************************************
				  *        US Paris Arbitration.            *
					*******************************************
				-->
				<xsl:when test="$infoType=$parisRulesInfoType">
					<div class="&centerClass;">
						<div>
							<xsl:apply-templates select="citation" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/institution" />
							<xsl:text> (CAP)</xsl:text>
						</div>
						<div>
							<xsl:apply-templates select="prelim/dtype" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/country" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/head1" />
							<xsl:apply-templates select="prelim/head2" />
						</div>
						<div>
							<xsl:apply-templates select="dates"/>
						</div>
					</div>
					<div>
						<xsl:apply-templates select="title" />
					</div>
					<!-- 
						End of document head 
					-->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				</xsl:when>

				<!-- 
					*******************************************
				  *        International ICDR Rules.          *
					*******************************************
				-->
				<xsl:when test="$infoType=$icdrIntlRulesInfoType">
					<div class="&centerClass;">
						<div>
							<xsl:apply-templates select="citation" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/institution" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/dtype" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/country" />
						</div>
						<div>
							<xsl:apply-templates select="prelim/head1" />
							<xsl:apply-templates select="prelim/head2" />
							<xsl:apply-templates select="prelim/head3" />
							<xsl:apply-templates select="prelim/head4" />
							<xsl:apply-templates select="prelim/head5" />
							<xsl:apply-templates select="prelim/head6" />
							<xsl:apply-templates select="prelim/head7" />
							<xsl:apply-templates select="prelim/head8" />
						</div>
						<div>
							<xsl:apply-templates select="dates"/>
						</div>
					</div>
					<div>
						<xsl:apply-templates select="title" />
					</div>
				</xsl:when>

				<!-- 
					*******************************************
				  *        the ICC Awards.                  *
					*******************************************
				-->
				<xsl:when test="$infoType=$iccawdInfoType" >
					<xsl:apply-templates select="citation" />
					<br />
					<br />
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/institution" />
						<xsl:text>&#160;</xsl:text>
						<xsl:apply-templates select="prelim/institution-acronym"/>
						<br />
						<xsl:apply-templates select="prelim/copyright" />
						<br />
						<xsl:apply-templates select="prelim/dtype" />
						<br />
						<xsl:apply-templates select="title" />
						<br />
						<xsl:apply-templates select="party"/>
						<br />
						<br />
					</div>
					<div class="&paraMainClass;">
						<strong>
							<div>
								<xsl:apply-templates select="arbitration.industry.code" />
							</div>
							<div>
								<xsl:apply-templates select="prelim/ctype" />
							</div>
							<div>
								<xsl:apply-templates select="arbitration.award.amount.plaintiff" />
							</div>
							<div>
								<xsl:apply-templates select="attorney.plaintiff" />
							</div>
							<div>
								<xsl:apply-templates select="attorney.defendant" />
							</div>
							<div>
								<xsl:apply-templates select="dates/date" />
							</div>
							<div>
								<xsl:apply-templates select="arbitrator" />
							</div>
							<div>
								<xsl:apply-templates select="prelim/country" />
							</div>
							<div>
								<xsl:apply-templates select="place" />
							</div>
							<div>
								<xsl:apply-templates select="language" mode="prelim" />
							</div>
						</strong>
					</div>

					<xsl:if test="source">
						<div>&#160;</div>
						<xsl:apply-templates select="source" />
					</xsl:if>

					<xsl:if test="case_history">
						<div>&#160;</div>
						<div>
							<xsl:apply-templates select="case_history" />
						</div>
					</xsl:if>

					<xsl:if  test="related_case" >
						<div>&#160;</div>
						<div>
							<xsl:apply-templates select="related_case" />
						</div>
					</xsl:if>

					<xsl:if test="item">
						<div>
							<xsl:apply-templates select="item" />
						</div>
					</xsl:if>
				</xsl:when>


				<!--This code is for the US Paris Arbitration Awards. -->
				<xsl:when test="$infoType=$parisAwardsInfoType">
					<xsl:apply-templates select="citation" />
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/dtype" />
						<div>
							<xsl:apply-templates select="prelim/institution" />
						</div>
						<div>
							<xsl:apply-templates select="court"/>
						</div>
						<div>
							<xsl:apply-templates select="title"/>
						</div>
						<div>
							<xsl:apply-templates select="prelim/docket.number" />
						</div>
					</div>
					<!-- 
						End of document head 
					-->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
					<div>
						<xsl:apply-templates select="arbitration.industry.code" />
						<div>
							<strong>
								<xsl:apply-templates select="prelim/ctype" />
							</strong>
						</div>
						<div>
							<strong>
								<xsl:apply-templates select="arbitration.award.amount.plaintiff" />
							</strong>
						</div>
						<div>
							<strong>
								<xsl:apply-templates select="attorney.plaintiff" />
							</strong>
						</div>
						<div>
							<strong>
								<xsl:apply-templates select="attorney.defendant" />
							</strong>
						</div>
						<div>
							<strong>
								<xsl:apply-templates select="dates/date" />
							</strong>
						</div>
						<div>
							<strong>
								<xsl:apply-templates select="arbitrator" />
							</strong>
						</div>
						<div>
							<strong>
								<xsl:apply-templates select="prelim/country" mode="plain"/>
							</strong>
						</div>
					</div>

				</xsl:when>

				<!-- 
					**********************************************************
					* Constitutional Law of South Africa - (CLOSA)					 *
					* <md.infotype>wli_closa</md.infotype>                   *
					**********************************************************
				-->

				<xsl:when test="$infoType=$closaInfoType">
					<!-- Header section	-->
					<xsl:apply-templates select="citation"/>
					<div>&#160;</div>
					<div class="&centerClass;">
						<xsl:apply-templates select="prelim/book_title" />
						<xsl:apply-templates select="prelim/edition" />
						<xsl:for-each select="prelim/child::node()[substring(name(), 1, 4) = 'head']">
							<div>
								<xsl:apply-templates />
							</div>
						</xsl:for-each>
						<div>&#160;</div>
						<xsl:apply-templates select="prelim/year" />
						<div>&#160;</div>
						<xsl:apply-templates select="prelim/copyright" />
					</div>
					<xsl:apply-templates select = "title" />
					<xsl:if test ="author">
						<xsl:apply-templates select = "author" />
					</xsl:if>

					<!-- End of document head -->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				</xsl:when>

				<!-- 
					*******************************************
				  *        OTHER MISC. Awards               *
					*******************************************
				-->
				<xsl:otherwise>
					<!-- 
						Header section
					-->
					<xsl:apply-templates select="citation"/>
					<xsl:apply-templates select="prelim" mode="otherAwards"/>
					<xsl:if test="prelim/copyright">
						<div class="&centerClass;">
							<xsl:apply-templates select="prelim/copyright"/>
						</div>
					</xsl:if>
					<xsl:apply-templates select="title"/>
					<div class="&centerClass;">
						<xsl:apply-templates select="party"/>
					</div>
					<xsl:apply-templates select="author"/>
					<xsl:apply-templates select="dates"/>

					<!-- 
						Organization section only for these collections:
					-->
					<div class="&paraMainClass;">
						<xsl:apply-templates select="organisation" />
						<div>
							<xsl:apply-templates select="place"/>
						</div>
						<div>
							<xsl:apply-templates select="language" mode="prelim"/>
						</div>
						<div>
							<xsl:apply-templates select="dates/date"/>
						</div>
					</div>
					<xsl:if test="source">
						<div class="&paraMainClass;">
							<xsl:apply-templates select="source"/>
						</div>
						<div>&#160;</div>
					</xsl:if>
					
					<xsl:if test="keywords">
						<div class="&paraMainClass;">
							<xsl:apply-templates select="keywords"/>
						</div>
						<div>&#160;</div>
					</xsl:if>
					
					<!-- End of document head -->
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				</xsl:otherwise>
			</xsl:choose>

		</div>

	<xsl:if test="$infoType != $australiaTreatises and $infoType != $espicaInfoType and $infoType != $appDigInfoType">		
			<xsl:call-template name="RenderTextOutline" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="language" mode="prelim">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- header - prelim -->
	<xsl:template match="prelim[//md.infotype/text() != 'wli_bitreaty']">
		<div class="&centerClass;">
			<div>
				<xsl:apply-templates select="institution"/>
				<xsl:apply-templates select="institution-acronym"/>
			</div>
			<xsl:apply-templates select="copyright"/>
			<xsl:apply-templates select="dtype"/>
			<xsl:apply-templates select="country"/>
		</div>
	</xsl:template>

	<xsl:template match="prelim" mode="otherAwards">
		<div class="&centerClass;">
			<div>
				<xsl:apply-templates select="institution"/>
				<xsl:apply-templates select="institution-acronym"/>
			</div>
			<xsl:apply-templates select="dtype"/>
			<xsl:apply-templates select="country"/>
		</div>
	</xsl:template>
		
	<!--<xsl:template match="prelim[//md.infotype/text() = 'wli_treatise_compau']">-->
	<xsl:template match="prelim[child::book_title]">
		<div class="&centerClass;">
			<xsl:apply-templates select="book_title"/>
			<xsl:apply-templates select="*[starts-with(local-name(), 'head')]"/>
		</div>
	</xsl:template>


	<xsl:template match="source">
		<xsl:apply-templates />
	</xsl:template>

	<!--<xsl:template match="book_title | edition | year | prelim//head1[//md.infotype/text() = 'wli_treatise_compau']">-->
	<xsl:template match="book_title | edition | year | prelim//head1[preceding-sibling::book_title]">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="author">
		<div>
			<xsl:apply-templates />
		</div>
		<!-- Bug# 510078 to fix no-spacing issue when delivered to msword -->
		<xsl:if test="$DeliveryMode">
			<xsl:text>&#160;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="copyright" priority="4">
		<xsl:if test="preceding-sibling::copyright">
			<div>&#160;</div>
		</xsl:if>
		<span>
			<xsl:attribute name="class">
				<xsl:text>&copyrightClass;</xsl:text>
				<xsl:if test="//md.infotype/text() = 'wli_bitreaty'">
					<xsl:text><![CDATA[ ]]>&centerClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<xsl:template match="citation">
		<div class="&citesClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="citation" mode="noStyling">
		<xsl:apply-templates />
	</xsl:template>	

	<xsl:template match="keywords">
		<div class="&keywordsBlockClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="dates">
		<div class="&dateClass;">
			<xsl:apply-templates select="date-adopted | date-effective | date-inforce | date-signed"/>
		</div>
	</xsl:template>

	<xsl:template match="date-adopted | date-effective | date-inforce | date-signed">
		<div class="&centerClass;">
			<xsl:apply-templates select="preceding-sibling::text()"/>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="institution">
		<span id="co_institution">
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<xsl:template match="institution-acronym">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<span>
			<xsl:attribute name="id">
				<xsl:value-of select="concat('co_', translate(text(),'-','_'))"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<xsl:template match="dtype | country">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="country" mode="plain">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="language | country[//md.infotype/text() = 'wli_bitreaty']">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&centerClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Display the included copyright message -->
	<xsl:template match="copyright-message">
		<div>&#160;</div>
		<div class="&centerClass;">
			<xsl:apply-templates/>
		</div>
		<!-- Added extra spacing in delivery to fix issue with Word Delivery -->
		<!-- Bug #511964:Copyright message was overlapping the "End of Document" text-->
		<xsl:if test="$DeliveryMode">
			<div>&#160;</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="copyright" mode="parisRules">
		<div>&#160;</div>
		<div class="&centerClass;">
			<xsl:apply-templates/>
		</div>

		<!-- Added extra spacing in delivery to fix issue with Word Delivery -->
		<!-- Bug #511964:Copyright message was overlapping the "End of Document" text-->
		<xsl:if test="$DeliveryMode">
			<div>&#160;</div>
		</xsl:if>
	</xsl:template>


	<!-- document reference -->
	<xsl:template match="xref">
		<xsl:call-template name="xrefLink"/>
	</xsl:template>

	<xsl:template match="iref">
		<a>
			<xsl:attribute name="href">
				<xsl:text>#</xsl:text>
				<xsl:value-of select="concat('&internalLinkIdPrefix;',translate(translate(@dest,'(','_'),')',''))"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>

	<xsl:template match="sup[child::a[starts-with(@name, 'r')]]">
		<xsl:variable name="footnoteNo" select="a/text()" />
		<xsl:variable name="footnoteLink" select="substring(a/@href, 2)" />
		<sup>
			<xsl:attribute name="id">
				<xsl:value-of select="concat('&footnoteReferenceIdPrefix;', a/@name)"/>
			</xsl:attribute>
			<a href="#co_footnote_{$footnoteLink}" class="&footnoteReferenceClass;">
				<xsl:value-of select="$footnoteNo"/>
			</a>
		</sup>
	</xsl:template>

	<xsl:template match="p[child::sup[child::a[starts-with(@name, 'f')]]]"/>

	<!-- lists -->
	<xsl:template match="list[not(child::item)]" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="item[not(parent::list)]" >
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="list" priority="1">
		<xsl:if test="item">
			<ul class="&listClass;">
				<xsl:apply-templates select="node()[not(self::list)]" />
			</ul>
		</xsl:if>
	</xsl:template>

	<xsl:template match="list/sublist">
		<xsl:if test="item">
			<li>
				<ul class="&listClass;">
					<xsl:apply-templates select="node()[not(self::sublist)]" />
				</ul>
				<xsl:if test="following-sibling::node()[1][self::sublist]">
					<xsl:apply-templates select="following-sibling::node()[1]" />
				</xsl:if>
			</li>
		</xsl:if>
	</xsl:template>

	<xsl:template match="list/item">
		<li class="&paraMainClass;">
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="sublist/item">
		<li>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::sublist]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</li>
	</xsl:template>

	<!-- foot note-->
	<xsl:template name="RenderFootnote">
		<xsl:param name="renderHorizontalRule"/>
		<xsl:if test=".//sup[child::a[starts-with(@name, 'f')]]">
			<xsl:if test="$renderHorizontalRule">
				<hr class="&horizontalRuleClass;"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<table id="&footnoteSectionId;" class="&footnoteSectionClass;">
						<tr>
							<td colspan="2" class="&footnoteSectionTitleClass; &printHeadingClass;">
                <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
              </td>
						</tr>
						<xsl:for-each select="//sup[child::a[starts-with(@name, 'f')]]">
							<xsl:variable name="footnoteNo" select="a/text()" />
							<xsl:variable name="footnoteLink" select="substring(a/@href, 2)" />
							<tr>
								<td class="&footnoteNumberClass;">
									<span>
										<xsl:attribute name="id">
											<xsl:value-of select="concat('&footnoteIdPrefix;',  a/@name)"/>
										</xsl:attribute>
										<a href="#co_footnoteReference_{$footnoteLink}">
											<xsl:value-of select="$footnoteNo"/>
										</a>
									</span>
								</td>
								<td class="&footnoteBodyClass;">
									<xsl:apply-templates select="following-sibling::node()" />
								</td>
							</tr>
						</xsl:for-each>
					</table>
				</xsl:when>
				<xsl:otherwise>
					<div id="&footnoteSectionId;" class="&footnoteSectionClass;">
						<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
              <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
            </h2>
						<xsl:for-each select=".//sup[child::a[starts-with(@name, 'f')]]">
							<xsl:variable name="footnoteLink" select="substring(a/@href, 2)" />
							<xsl:variable name="footnoteNo" select="a/text()" />
							<div>
								<div class="&footnoteNumberClass;">
									<span>
										<xsl:attribute name="id">
											<xsl:value-of select="concat('&footnoteIdPrefix;', a/@name)"/>
										</xsl:attribute>
										<a href="#co_footnoteReference_{$footnoteLink}">
											<xsl:value-of select="$footnoteNo"/>
										</a>
									</span>
								</div>
								<div class="&footnoteBodyClass;">
									<xsl:apply-templates select="following-sibling::node()" />
								</div>
							</div>
						</xsl:for-each>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>


	<!--<xsl:template match="arbitration.award.amount.plaintiff | arbitration.award.amount.defendant | arbitration.industry.code | arbitration.award.range">
		<div>
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>-->

	<!--Arbitration Information Block -->
	<xsl:template match="arbitration.block">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--Render Arbitration Industry-->
	<xsl:template match="arbitration.industry.code">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<!--Render Arbitration Case Type-->
	<xsl:template match="arbitration.case.type">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<!--Render Arbitration Award Amount-->
	<xsl:template match="arbitration.award.amount">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<!--Render Plaintiff-->
	<xsl:template match="plaintiff.attorney.line">
		<strong>
			<xsl:apply-templates />
		</strong>
		<xsl:if test="text()">
			<div>&#160;</div>
		</xsl:if>
	</xsl:template>

	<!--Render Defendant-->
	<xsl:template match="defendant.attorney.line">
		<strong>
			<xsl:apply-templates />
		</strong>
		<xsl:if test="text()">
			<div>&#160;</div>
		</xsl:if>
	</xsl:template>

	<!--Render Arbitration Date-->
	<xsl:template match="arbitration.award.date">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<!--Render Arbitrator Name-->
	<xsl:template match="arbitrator.name">
		<xsl:choose>
			<xsl:when test="parent::arbitration.block">
				<strong>
					<xsl:apply-templates />
				</strong>
			</xsl:when>
			<xsl:otherwise>
				<div>
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Document Header Information Block -->
	<xsl:template match="document.head.block">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--Render Document Header-->
	<xsl:template match="document.head">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--Attorney Information Block -->
	<xsl:template match="attorney.block">
		<xsl:choose>
			<xsl:when test="parent::arbitration.block">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<div>
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="keywords" mode="appDigInfoType">
		<xsl:apply-templates mode="appDigInfoType" />
	</xsl:template>
	
	<xsl:template match="label" mode="appDigInfoType">
		<div>
			<strong>
				<xsl:apply-templates mode="appDigInfoType" />
			</strong>
		</div>
	</xsl:template>

	<xsl:template match="keyword" mode="appDigInfoType">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="citations">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="cited">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="item | award | case | rule | law | journal | treaty">
		<xsl:variable name="thisNodeName" select="local-name()"/>
		<xsl:choose>
			<xsl:when test="following-sibling::node()[local-name()=$thisNodeName]">
				<div>
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
				<!--<div>&#160;</div>-->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="i">
		<em>
			<xsl:apply-templates/>
		</em>
	</xsl:template>

	<xsl:template match="prelim.head" priority="5">
		<div class="&centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="journal.date">
		<div class="&centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
