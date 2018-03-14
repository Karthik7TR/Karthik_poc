<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="primarycite" select="Document/n-docbody/document/metadata.block/md.identifiers/md.cites/md.primarycite" />
	<!-- DO NOT RENDER -->
	<xsl:template match="fullcasename | citnotes | proceeding_parties | transcript.reference | transcript.reference | transcript.pages | court.abbrev | media_neutral_citation | jurisdiction.abbrev | jurisdiction.longname" />
	<xsl:template match="judgment_status" />
	<xsl:template match="prism-clipsubsections | svg | services" />
	<xsl:template match="starpage.anchor" priority="5"/>
	<!--  casename elements in the abstract - suppressed as already part of the link text -->
	<xsl:template match="abstract//caseref/casename" />
	<!--Suppress the Cite.Query Links for UO ref type, Fix for the issue 804199-->
	<xsl:template match="cite.query[@w-ref-type='UO']" priority="3">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<xsl:apply-templates select="n-docbody/document/casegroup"/>
			<!--
				******************************************************************************************************
				* Backlog Item 506268: 
				* Remove all logos from International content. 
				* Add copyright message from royality block and message block centered at the bottom of the document.
				******************************************************************************************************
			-->
			<xsl:apply-templates select="n-docbody/copyright-message"/>

			<div class="&alignHorizontalLeftClass;">
				<xsl:apply-templates select="$primarycite" />
			</div>

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>

	<xsl:template match="casegroup">
		<xsl:apply-templates select="parties | subnom" />
		<xsl:apply-templates select="proceeding/structural/court/court.name" />
		<xsl:apply-templates select="proceeding/structural/judgment_date" />
		<div class="&headtextClass; &centerClass;">
			<xsl:text>&ukCaseAnalysis;</xsl:text>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<table cellpadding="0" cellspacing="0">
			<tbody>
				<xsl:apply-templates select="proceeding/structural/where_reported" />
				<xsl:apply-templates select="proceeding/content" />
				<xsl:apply-templates select="proceeding/structural/direct_history_effect" />
				<xsl:apply-templates select="proceeding/relationships/node()[not(self::commentary_citing_this)] | proceeding/caselegislation" />
				<xsl:apply-templates select="proceeding/articles_commenting_this" />
				<xsl:apply-templates select="proceeding/relationships/commentary_citing_this" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="parties">
		<div class="&headtextClass; &centerClass;">
			<xsl:apply-templates select="partya"/>
			<xsl:apply-templates select="partyb"/>
		</div>
	</xsl:template>

	<xsl:template match="partyb">
		<xsl:if test=".!=''">
			<xsl:text>&ukVersus;</xsl:text>
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="othername|shipname">
		<xsl:if test=".!=''">
			<div class="&headtextClass; &centerClass;">
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="subnom">
		<xsl:if test=".!=''">
			<xsl:if test="not(preceding-sibling::subnom)">
				<div class="&headtextClass; &centerClass;">
					<xsl:text>&ukAlsoKnownAs;</xsl:text>
				</div>
			</xsl:if>
			<div class="&headtextClass; &centerClass;">
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="court.name | judgment_date">
		<div class="&centerClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="where_reported">
		<xsl:if test="citation and string-length(citation &gt; 0)">
			<tr>
				<td colspan="3">
					<xsl:text>&#160;</xsl:text>
				</td>
			</tr>
			<tr>
				<td class="&noWrapClass; &alignVerticalTopClass;">
					<strong>
						<xsl:text>&ukWhereReported;</xsl:text>
					</strong>
				</td>
				<td>
					<xsl:text>&#160;&#160;</xsl:text>
				</td>
				<td>
					<xsl:apply-templates/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="citation">
		<xsl:apply-templates/>
		<xsl:if test="position()!=last()">; </xsl:if>
	</xsl:template>


	<!-- Case digest section -->
	<xsl:template match="content">
		<xsl:if test="taxonomy/subjects/subject.principal/subjectterm | taxonomy/keywords/keyword | taxonomy/catchphrase/keyword | abstract">
			<tr>
				<td colspan="3">
					<xsl:text>&#160;</xsl:text>
				</td>
			</tr>
			<tr>
				<td class="&noWrapClass; &alignVerticalTopClass;">
					<strong>
						<xsl:text>&ukCaseDigest;</xsl:text>
					</strong>
				</td>
				<td>
					<xsl:text>&#160;&#160;</xsl:text>
				</td>
				<td>
					<xsl:if test="taxonomy/subjects/subject.principal/subjectterm">
						<strong>
							<xsl:text>&ukSubject;</xsl:text>
						</strong>
						<xsl:apply-templates select="taxonomy/subjects/subject.principal/subjectterm"/>
					</xsl:if>
				</td>
			</tr>

			<xsl:if test="taxonomy/keywords/keyword">
				<tr>
					<td colspan="3">
						<xsl:text>&#160;</xsl:text>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<xsl:text>&#160;&#160;</xsl:text>
					</td>
					<td>
						<strong>
							<xsl:text>&ukKeywords;</xsl:text>
						</strong>
						<xsl:apply-templates select="taxonomy/keywords/keyword"/>
					</td>
				</tr>
			</xsl:if>

			<xsl:if test="taxonomy/catchphrase/keyword or abstract/summary">
				<xsl:choose>
					<xsl:when test="abstract/summary">
						<xsl:apply-templates select="abstract/summary/para"/>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td colspan="3">
								<xsl:text>&#160;</xsl:text>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<xsl:text>&#160;&#160;</xsl:text>
							</td>
							<td>
								<strong>
									<xsl:text>&ukSummary;</xsl:text>
								</strong>
								<xsl:apply-templates select="taxonomy/catchphrase/keyword|taxonomy/catchphrase/phraseitem"/>
							</td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>

			<xsl:if test="abstract">
				<xsl:apply-templates select="abstract/*[name()!='summary']/para" mode="abstract"/>
			</xsl:if>

			<xsl:if test="../structural/judges">
				<tr>
					<td colspan="3">
						<xsl:text>&#160;</xsl:text>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<xsl:text>&#160;&#160;</xsl:text>
					</td>
					<td>
						<strong>
							<xsl:text>&ukJudge;</xsl:text>
						</strong>
						<xsl:apply-templates select="../structural/judges"/>
					</td>
				</tr>
			</xsl:if>

			<xsl:if test="appearances/appearance.counsel">
				<tr>
					<td colspan="3">
						<xsl:text>&#160;</xsl:text>
					</td>
				</tr>

				<tr>
					<td colspan="2">
						<xsl:text>&#160;&#160;</xsl:text>
					</td>
					<td>
						<strong>
							<xsl:text>&ukCounsel;</xsl:text>
						</strong>
						<xsl:for-each select="appearances/appearance.counsel">
							<xsl:text>&ukFor;</xsl:text>
							<xsl:apply-templates select="counsel.Actingfor"/>
							<xsl:text>: </xsl:text>
							<xsl:apply-templates select="counsel.identity"/>
							<xsl:text> </xsl:text>
						</xsl:for-each>
					</td>
				</tr>
			</xsl:if>

			<xsl:if test="appearances/appearance.solicitors">
				<tr>
					<td colspan="3">
						<xsl:text>&#160;</xsl:text>
					</td>
				</tr>

				<tr>
					<td colspan="2">
						<xsl:text>&#160;&#160;</xsl:text>
					</td>
					<td>
						<strong>
							<xsl:text>&ukSolicitor;</xsl:text>
						</strong>
						<xsl:for-each select="appearances/appearance.solicitors">
							<xsl:text>&ukFor;</xsl:text>
							<xsl:apply-templates select="solicitors.Actingfor"/>
							<xsl:text>: </xsl:text>
							<xsl:apply-templates select="solicitors.identity"/>
							<xsl:text> </xsl:text>
						</xsl:for-each>
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="abstract/summary/para">
		<tr>
			<td colspan="3">
				<xsl:text>&#160;</xsl:text>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<xsl:text>&#160;&#160;</xsl:text>
			</td>
			<td>
				<xsl:if test="position()=1">
					<strong>
						<xsl:text>&ukSummary;</xsl:text>
					</strong>
				</xsl:if>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="para" mode="abstract">
		<tr>
			<td colspan="3">
				<xsl:text>&#160;</xsl:text>
			</td>
		</tr>
		<tr>
			<td colspan="2">&#160;&#160;</td>
			<td>
				<xsl:if test="position()=1">
					<strong>
						<xsl:text>&ukAbstract;</xsl:text>
					</strong>
				</xsl:if>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>

	<!-- Appellate History Section -->
	<xsl:template match="structural/direct_history_effect/appellate | direct_history/appellate">
		<xsl:if test=".!=''">
			<tr>
				<td colspan="3">
					<xsl:text>&#160;</xsl:text>
				</td>
			</tr>
			<tr>
				<td class="&noWrapClass; &alignVerticalTopClass;">
					<strong>&ukAppellateHistory;</strong>
				</td>
				<td>
					<xsl:text>&#160;</xsl:text>
				</td>
				<td>
					<xsl:for-each select="relationship">
						<xsl:sort select="position()" data-type="number" order="descending"/>

						<!--Order them backwards-->
						<xsl:if test="caseref[2]/@long_court and caseref[1]/@long_court !=''">
							<div class="&paraMainClass;">
								<xsl:value-of select="caseref[2]/@long_court"/>
							</div>
						</xsl:if>

						<xsl:apply-templates select="caseref[2]" />

						<xsl:if test="@effect!=''">
							<div class="&paratextMainClass; &headtextClass;">
								<xsl:call-template name="capitalize">
									<xsl:with-param name="caseref" select="@passive_effect"/>
								</xsl:call-template>
								<xsl:text>&ukBy;</xsl:text>
								<div class="&paratextMainClass;">&#160;</div>
							</div>
						</xsl:if>

						<xsl:if test="position() = last()">
							<xsl:if test="caseref[1]/@long_court and caseref[1]/@long_court !=''">
								<div class="&paratextMainClass;">
									<xsl:value-of select="caseref[1]/@long_court"/>
								</div>
							</xsl:if>
							<xsl:apply-templates select="caseref[1]" />
						</xsl:if>
					</xsl:for-each>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template name="add_rowsection">
		<xsl:param name="strong-text"/>
		<xsl:if test=". != ''">
			<tr>
				<td colspan="3">
					<xsl:text>&#160;</xsl:text>
				</td>
			</tr>
			<tr>
				<td class="&noWrapClass; &alignVerticalTopClass;">
					<strong>
						<xsl:value-of select="string($strong-text)"/>
					</strong>
				</td>
				<td>
					<xsl:text>&#160;&#160;</xsl:text>
				</td>
				<td>
					<xsl:apply-templates/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="structural/direct_history_effect/references | direct_history/references">
		<xsl:call-template name="add_rowsection">
			<xsl:with-param name ="strong-text" select="'&ukRelatedCases;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="references/caseref">
		<xsl:choose>
			<xsl:when test="preceding-sibling::caseref">
				<div class="&paratextMainClass; &headtextClass;">
					<xsl:value-of select="casename"/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass; &headtextClass;">
					<xsl:value-of select="casename"/>
				</div>
			</xsl:otherwise>
		</xsl:choose>


		<!--<div class="&paratextMainClass; &headtextClass;">
			<xsl:value-of select="casename"/>
		</div>-->
		<div class="&paraMainClass;">
			<xsl:choose>
				<xsl:when test="count(link/cite.query) > 0">
					<xsl:apply-templates select="link/cite.query"/>
					<xsl:text>; </xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="link"/>
					<xsl:text>; </xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="@court"/>
		</div>
	</xsl:template>

	<!-- Significant cases cited in this judgment section -->
	<xsl:template match="cases_cited">
		<xsl:call-template name="add_rowsection">
			<xsl:with-param name ="strong-text" select="'&ukCasesCited;'"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Significant cases citing this case Section -->
	<xsl:template match="cases_citing_this">
		<xsl:call-template name="add_rowsection">
			<xsl:with-param name ="strong-text" select="'&ukCasesCitingThisCase;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="topics_citing_this">
		<xsl:call-template name="add_rowsection">
			<xsl:with-param name ="strong-text" select="'&ukCasesTopicsCitingThis;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cases_cited/caseref|cases_citing_this/caseref">

		<xsl:if test="count(preceding-sibling::caseref)='0' or @effect!=preceding-sibling::caseref[1]/@effect">
			<xsl:if test="@effect!=''">
				<xsl:choose>
					<xsl:when test="position() = 1">
						<!-- align with label in left column -->
						<div class="&paraMainClass; &headtextClass;">
							<xsl:call-template name="capitalize">
								<xsl:with-param name="caseref" select="@effect"/>
							</xsl:call-template>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div class="&paratextMainClass; &headtextClass;">
							<xsl:call-template name="capitalize">
								<xsl:with-param name="caseref" select="@effect"/>
							</xsl:call-template>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:if>

		<xsl:apply-templates/>

		<xsl:if test="@court and @court!=''">
			<div class="&paraMainClass;">
				<xsl:value-of select="@court"/>
			</div>
		</xsl:if>

	</xsl:template>


	<xsl:template match="direct_history_effect/caseref">
		<xsl:if test="@effect!=''">
			<xsl:choose>
				<xsl:when test="preceding-sibling::caseref">
					<div class="&paratextMainClass; &headtextClass;">
						<xsl:call-template name="capitalize">
							<xsl:with-param name="caseref" select="@effect"/>
						</xsl:call-template>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div class="&paraMainClass; &headtextClass;">
						<xsl:call-template name="capitalize">
							<xsl:with-param name="caseref" select="@effect"/>
						</xsl:call-template>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:apply-templates/>
		<div class="&paratextMainClass;">
			<xsl:if test="@court and @court!=''">
				<xsl:value-of select="@court"/>
			</xsl:if>
		</div>
	</xsl:template>

	<!--  appellate history and cases citing casenames - bold -->
	<xsl:template match="direct_history_effect/caseref/casename | cases_cited/caseref/casename">
		<xsl:if test=".!=''">
			<xsl:choose>
				<xsl:when test="../preceding-sibling::caseref">
					<!-- align with label in left column -->
					<div class="&paratextMainClass; &headtextClass;">
						<xsl:apply-templates  />
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div class="&paraMainClass; &headtextClass;">
						<xsl:apply-templates/>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="relationship/caseref/casename">
		<xsl:if test=".!=''">
			<div class="&paraMainClass; &headtextClass;">
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>

	<!--  cases citing casenames - not bold -->
	<xsl:template match="cases_citing_this/caseref/casename">
		<xsl:if test=".!=''">
			<xsl:choose>
				<xsl:when test="../preceding-sibling::caseref">
					<!-- align with label in left column -->
					<div class="&paratextMainClass;">
						<xsl:apply-templates  />
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div class="&paraMainClass;">
						<xsl:apply-templates/>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- Legislation cited section -->
	<xsl:template match="legis_cited">
		<xsl:if test="legis-cite">
			<xsl:call-template name="add_rowsection">
				<xsl:with-param name ="strong-text" select="'&ukLegislationCited;'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="legis_cited/legis-cite | topics_citing_this/topic_ref">
		<xsl:if test=".!=''">
			<div class="&paraMainClass;">
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Journal articles -->
	<xsl:template match="articles_commenting_this">
		<xsl:if test="article">
			<xsl:call-template name="add_rowsection">
				<xsl:with-param name ="strong-text" select="'&ukJournalArticles;'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="article">
		<xsl:if test=".!=''">
			<xsl:choose>
				<xsl:when test="preceding-sibling::article">
					<!-- align with label in left column -->
					<div class="&paratextMainClass; &headtextClass;">
						<xsl:apply-templates select="title" />
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div class="&paraMainClass; &headtextClass;">
						<xsl:apply-templates select="title" />
					</div>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="keywords/keyword"/>
			<xsl:if test="keywords/keyword">
				<xsl:text>.</xsl:text>
				<xsl:text>&#160;</xsl:text>
			</xsl:if>
			<div class="&paraMainClass;">
				<xsl:apply-templates select="journal-cite"/>
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match ="cite.query[./@w-ref-type = 'UG']">
		<xsl:variable name="href">
			<xsl:call-template name="CreateUrl">
				<xsl:with-param name ="cite" select="@w-normalized-cite" />
				<xsl:with-param name="originatingDoc" select="@ID" />
			</xsl:call-template>
		</xsl:variable>
		<a class="&linkClass;">
			<xsl:attribute name="href">
				<xsl:copy-of select="$href"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>
	
	<xsl:template name="CreateUrl">
		<xsl:param name="cite"/>
		<xsl:param name="originatingDoc"/>
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.Document', 'viewType=FullText',concat('guid=',$cite),concat('originatingDoc=',$originatingDoc), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>

	<!-- Books -->
	<xsl:template match="commentary_citing_this">
			<xsl:call-template name="add_rowsection">
				<xsl:with-param name ="strong-text" select="'&ukBooks;'"/>
			</xsl:call-template>
	</xsl:template>

	<xsl:template match="commentary_ref">
		<xsl:if test=".!=''">
			<xsl:choose>
				<xsl:when test="preceding-sibling::commentary_ref">
					<!-- align with label in left column -->
					<div class="&paratextMainClass; &headtextClass;">
						<xsl:apply-templates select="title" />
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div class="&paraMainClass; &headtextClass;">
						<xsl:apply-templates select="title" />
					</div>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:for-each select="node()[not(self::title) and not(self::ordersequence)]">
				<div class="&paraMainClass;">
					<xsl:choose>
						<xsl:when test="local-name() = 'chapter'">
							<xsl:text>&ukChapter;</xsl:text>
							<xsl:apply-templates />
						</xsl:when>
						<xsl:when test="local-name() = 'link'">
							<xsl:text>&ukDocuments;</xsl:text>
							<xsl:apply-templates />
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates />
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<xsl:template match="subjectterm[position()!=last()] | keyword[position()!=last() or following-sibling::*]  | phraseitem[position()!=last()]">
		<!--This matches many different apply-templates above-->
		<xsl:apply-templates/>
		<xsl:text>; </xsl:text>
	</xsl:template>

	<!-- Only display the judge name -->
	<xsl:template match="judge">
		<xsl:apply-templates select="judge.name" />
		<xsl:if test="position() != last()">
			<xsl:text>; </xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="caselegislation">
		<xsl:if test=".!=''">
			<tr>
				<td class="&noWrapClass; &alignVerticalTopClass;">
					<xsl:text>&#160;</xsl:text>
				</td>
				<td>
					<xsl:apply-templates/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="copyright-message">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>


	<!-- Helper Templates -->
	<xsl:template name="capitalize">
		<xsl:param name="caseref"/>
		<xsl:variable name="lc" select="'abcdefghijklmnopqrstuvwxyz'"/>
		<xsl:variable name="uc" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
		<xsl:variable name="firstchar" select="substring($caseref,1,1)"/>
		<xsl:variable name="upfirstchar" select="translate($firstchar,$lc,$uc)"/>
		<!-- the function returns this value -->
		<xsl:value-of select="concat($upfirstchar,substring($caseref,2))"/>
	</xsl:template>

</xsl:stylesheet>