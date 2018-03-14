<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Index.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="FooterCitation" />
			<xsl:apply-templates select="n-docbody/header/prelim/copyright"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="profile.email">
		<xsl:choose>
			<xsl:when test="cite.query">
				<a class="&pauseSessionOnClickClass;">
					<xsl:attribute name="href">mailto:<xsl:value-of select="cite.query/@w-normalized-cite"/></xsl:attribute>
					<!-- apply templates to everything under cite.query.  we don't want the normal cite query templates to render this
					because it'll generate a bad WestlawNext link. Using node() should supply apply-templates with everything underneath
					the cite.query node, that way for example if there is a highlight on the text it'll get rendered. -->
					<xsl:apply-templates select="cite.query/node()" />
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="biographical.information">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates select="head/headtext"/>
		</div>
		<table>
			<tbody>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="arbitrator.number/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="arbitrator.number/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="arbitrator.block/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="arbitrator.block/arbitrator"/>
					</td>
				</tr>
				<xsl:apply-templates select="business.address"/>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="home.address/label"/>
						</b>
					</td>
				</tr>
				<tr>
					<td>
						<xsl:apply-templates select="home.address/address/label"/>
					</td>
					<td>
						<div>
							<xsl:if test="home.address/address/street">
								<xsl:apply-templates select="home.address/address/street"/>
							</xsl:if>
						</div>
						<div>
							<xsl:if test="home.address/address/city">
								<xsl:variable name="homeCity" select="home.address/address/city"/>
								<xsl:if test ="$homeCity != ''">
									<xsl:apply-templates select="home.address/address/city"/>
									<xsl:text>, </xsl:text>
								</xsl:if>
							</xsl:if>
							<xsl:if test="home.address/address/state">
								<xsl:variable name="homeState" select="home.address/address/state"/>
								<xsl:if test ="$homeState != ''">
									<xsl:apply-templates select="home.address/address/state"/>&nbsp;
								</xsl:if>
							</xsl:if>
							<xsl:if test="home.address/address/zip">
								<xsl:variable name="homeZip" select="home.address/address/zip"/>
								<xsl:if test ="$homeZip != ''">
									<xsl:apply-templates select="home.address/address/zip"/>
								</xsl:if>
							</xsl:if>
							<xsl:if test="home.address/address/country">
								<xsl:variable name="homeCountry" select="home.address/address/country"/>
								<xsl:if test ="$homeCountry != ''">
									<xsl:apply-templates select="home.address/address/country"/>
								</xsl:if>
							</xsl:if>
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<xsl:apply-templates select="home.address/phone/label"/>
					</td>
					<td>
						<xsl:apply-templates select="home.address/phone/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<xsl:apply-templates select="home.address/fax/label"/>
					</td>
					<td>
						<xsl:apply-templates select="home.address/fax/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<xsl:apply-templates select="home.address/email/label"/>
					</td>
					<td>
						<xsl:apply-templates select="home.address/email/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="status/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="status/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="dob/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="dob/text"/>
					</td>
				</tr>

				<tr>
					<td>
						<b>
							<xsl:apply-templates select="birth.place/label"/>
						</b>
					</td>
					<td>
						<xsl:variable name="city" select="birth.place/city"/>
						<xsl:if test ="string-length($city) &gt; 0" >
							<xsl:apply-templates select="birth.place/city"/>
							<xsl:variable name="state" select="birth.place/state"/>
							<xsl:if test ="string-length($state) &gt; 0" >
								<xsl:text>, </xsl:text>
								<xsl:apply-templates select="birth.place/state"/>
								<xsl:variable name="country" select="birth.place/country"/>
								<xsl:if test ="string-length($country) &gt; 0" >
									<xsl:text>, </xsl:text>
									<xsl:apply-templates select="birth.place/country"/>
								</xsl:if>
							</xsl:if>
						</xsl:if>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="occupation/label"/>
						</b>
					</td>
					<td>
						<xsl:for-each select="occupation/text">
							<div>
								<xsl:apply-templates select="."/>
							</div>
						</xsl:for-each>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="cancel.fee/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="cancel.fee/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="cancel.fee.amount/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="cancel.fee.amount/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="cancel.fee.reason/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="cancel.fee.reason/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="perdiem/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="perdiem/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="perdiem.item/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="perdiem.item/text"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="hearing.procedure/label"/>
						</b>
					</td>
					<td>
						<xsl:for-each select="hearing.procedure/text">
							<div>
								<xsl:apply-templates select="."/>
							</div>
						</xsl:for-each>
					</td>
				</tr>
				<tr>
					<td>
						<b>
							<xsl:apply-templates select="national.academy.member/label"/>
						</b>
					</td>
					<td>
						<xsl:apply-templates select="national.academy.member/text"/>
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="issues.industries">
		<table width="600" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<xsl:apply-templates select="issues/head/headtext"/>
				</td>
				<td>
					<xsl:apply-templates select="industries/head/headtext"/>
				</td>
			</tr>
			<tr  valign="top">
				<td>
					<xsl:apply-templates select="issues/list"/>
				</td>
				<td>
					<xsl:apply-templates select="industries/list"/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="issues/list | industries/list">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="list.item" mode="listDelivery" />
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<xsl:apply-templates select="list.item" mode="listDisplay" />
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="list.item" mode="listDelivery">
		<div>
			<span class="&excludeFromAnnotationsClass;">&bull;</span>
			<xsl:text>&#160;</xsl:text>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="list.item" mode="listDisplay">
		<li>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="education.block/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="education">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<div>
					<span class="&excludeFromAnnotationsClass;">&bull;</span>
					<xsl:text>&#160;</xsl:text>
					<xsl:if test="degree">
						<xsl:apply-templates select="degree"/>
						<xsl:text> , </xsl:text>
					</xsl:if>
					<xsl:if test="major">
						<xsl:apply-templates select="major"/>
						<xsl:text> , </xsl:text>
					</xsl:if>
					<xsl:if test="school">
						<xsl:apply-templates select="school"/>
						<xsl:text> , </xsl:text>
					</xsl:if>
					<xsl:if test="date">
						<xsl:apply-templates select="date"/>
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<li>
						<xsl:if test="degree">
							<xsl:apply-templates select="degree"/>
							<xsl:text> , </xsl:text>
						</xsl:if>
						<xsl:if test="major">
							<xsl:apply-templates select="major"/>
							<xsl:text> , </xsl:text>
						</xsl:if>
						<xsl:if test="school">
							<xsl:apply-templates select="school"/>
							<xsl:text> , </xsl:text>
						</xsl:if>
						<xsl:if test="date">
							<xsl:apply-templates select="date"/>
						</xsl:if>
					</li>
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="certification.block/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="certification">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<div>
					<span class="&excludeFromAnnotationsClass;">&bull;</span>
					<xsl:text>&#160;</xsl:text>
					<xsl:if test="state">
						<xsl:apply-templates select="state"/>
						<xsl:text> ; </xsl:text>
					</xsl:if>
					<xsl:if test="year">
						<xsl:apply-templates select="year"/>
						<xsl:text> ; </xsl:text>
					</xsl:if>
					<xsl:if test="certificate">
						<xsl:apply-templates select="certificate"/>
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<li>
						<xsl:if test="state">
							<xsl:apply-templates select="state"/>
							<xsl:text> ; </xsl:text>
						</xsl:if>
						<xsl:if test="year">
							<xsl:apply-templates select="year"/>
							<xsl:text> ; </xsl:text>
						</xsl:if>
						<xsl:if test="certificate">
							<xsl:apply-templates select="certificate"/>
						</xsl:if>
					</li>
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="panel.memberships/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="panel.memberships/list">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="list.item" mode="listDelivery" />
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<xsl:apply-templates select="list.item" mode="listDisplay" />
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="employment.history//headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates/>
			</b>
		</div>
	</xsl:template>

	<xsl:template match="employment.history//employment">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<div>
					<span class="&excludeFromAnnotationsClass;">&bull;</span>
					<xsl:text>&#160;</xsl:text>
					<xsl:if test="employer">
						<xsl:apply-templates select="employer"/>
						<xsl:text> , </xsl:text>
					</xsl:if>
					<xsl:if test="from.date">
						<xsl:apply-templates select="from.date"/>
						<xsl:text> , </xsl:text>
					</xsl:if>
					<xsl:if test="to.date">
						<xsl:apply-templates select="to.date"/>
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<li>
						<xsl:if test="employer">
							<xsl:apply-templates select="employer"/>
							<xsl:text> , </xsl:text>
						</xsl:if>
						<xsl:if test="from.date">
							<xsl:apply-templates select="from.date"/>
							<xsl:text> , </xsl:text>
						</xsl:if>
						<xsl:if test="to.date">
							<xsl:apply-templates select="to.date"/>
						</xsl:if>
					</li>
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="labor.relations/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="labor.relations/list">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="list.item" mode="listDelivery" />
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<xsl:apply-templates select="list.item" mode="listDisplay"/>
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="professional.memberships/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="professional.memberships/list">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="list.item" mode="listDelivery" />
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<xsl:apply-templates select="list.item"  mode="listDisplay" />
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="awards/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates />
			</b>
		</div>
	</xsl:template>

	<xsl:template match="awards/list">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="list.item" mode="listDelivery" />
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<xsl:apply-templates select="list.item"  mode="listDisplay" />
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="publications/head/headtext">
		<xsl:text>&#160;</xsl:text>
		<div class="&alignHorizontalLeftClass;">
			<b>
				<xsl:apply-templates/>
			</b>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="publication">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<div>
					<span class="&excludeFromAnnotationsClass;">&bull;</span>
					<xsl:text>&#160;</xsl:text>
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bullListClass;">
					<li>
						<xsl:apply-templates/>
					</li>
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="article">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match ="business.address">
		<xsl:variable name ="numOfBus">
			<xsl:value-of select ="count(preceding-sibling::business.address) + 1"/>
		</xsl:variable>
		<tr>
			<td>
				<xsl:choose>
					<xsl:when test ="$numOfBus = 2">
						<b>
							<xsl:text>2nd </xsl:text>
						</b>
					</xsl:when>
					<xsl:when test ="$numOfBus > 2">
						<b>
							<xsl:text>Additional </xsl:text>
						</b>
					</xsl:when>
					<xsl:otherwise/>
				</xsl:choose>
				<b>
					<xsl:apply-templates select ="label" />
				</b>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="professional.title/label"/>
			</td>
			<td>
				<xsl:apply-templates select="professional.title/text"/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="organization/label"/>
			</td>
			<td>
				<xsl:apply-templates select="organization/text"/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="address/label"/>
			</td>
			<td>
				<div>
					<xsl:if test="address/street">
						<xsl:apply-templates select="address/street"/>
					</xsl:if>
				</div>
				<div>
					<xsl:if test="address/city">
						<xsl:if test =". != ''">
							<xsl:apply-templates select="address/city"/>
							<xsl:text>, </xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test="address/state">
						<xsl:apply-templates select="address/state"/>&nbsp;
					</xsl:if>
					<xsl:if test="address/zip">
						<xsl:apply-templates select="address/zip"/>
					</xsl:if>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="phone/label"/>
			</td>
			<td>
				<xsl:apply-templates select="phone/text"/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="fax/label"/>
			</td>
			<td>
				<xsl:apply-templates select="fax/text"/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="email/label"/>
			</td>
			<td>
				<xsl:apply-templates select="email/text"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="arbitrator.block/arbitrator">
		<xsl:call-template name="citeQuery">
			<xsl:with-param name="citeQueryElement" select="cite.query"/>
			<xsl:with-param name="linkContents">
				<xsl:choose>
					<xsl:when test="cite.query/node()">
						<xsl:for-each select="cite.query/node()">
							<xsl:apply-templates select="."/>
							<xsl:if test="position() != last()">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="cite.query/node()[not(self::starpage.anchor)]"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:apply-templates select="text()[position() &gt; 1]"/>
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>
	
	<!-- Fix for the defect 372278-->
	<xsl:template name="FooterCitation">
		<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites"/>
	</xsl:template>
	
	<!-- Arbitrator Lias templates -->
	<xsl:template match="arbitrator.block" />

	<xsl:template match="prelim.block" />

	<xsl:template match="profile.block" >
		<xsl:variable name="bio" select="/Document/n-docbody/biography" />
		<h1 class="&arbTitle;">
			<xsl:apply-templates select="$bio/prelim.block/prelim.head/head/headtext/node()[not(self::bop or self::bos or self::eos or self::eop)]" />
		</h1>
		<div>
			<xsl:apply-templates select="$bio/profile.block/profile.arbitrator.name" />
		</div>
		<div>
			<xsl:apply-templates select="$bio/profile.block/profile.organization" />
			<!--<xsl:choose>
				<xsl:when test="$bio/profile.block/profile.organization/cite.query">
					<xsl:apply-templates select="$bio/profile.block/profile.organization/node()[not(self::cite.query)] |
										 $bio/profile.block/profile.organization/cite.query/node()" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="$bio/profile.block/profile.organization" />
				</xsl:otherwise>
			</xsl:choose>-->
      
		</div>
		<xsl:apply-templates select="$bio/profile.block/profile.business.address" />
		<div>
			<xsl:apply-templates select="$bio/profile.block/profile.phone" />
		</div>
		<div>
			<xsl:apply-templates select="$bio/profile.block/profile.fax" />
		</div>
		<div>
			<xsl:apply-templates select="$bio/profile.block/profile.email" />
		</div>
	</xsl:template>

	<xsl:template match="profile.business.address">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="individual.stats.doc">
		<xsl:call-template name="renderStatistics" />
	</xsl:template>

	<!-- Suppress this to prevent duplicate data -->
	<xsl:template match="natl.avg.stats.doc" />

	<!-- For rendering the Box container the statistics chart-->
	<xsl:template name="renderStatistics">
		<div id="co_allTableChartContainer">
			<!-- provide the selector -->
			<xsl:if test="not($IsMobile)">
				<div id="arbChartSelectorWrapper" class="&formTextSelect;">
					<label for="arbChartSelector" class="&accessiblityLabel;">Filter Dataset</label>
					<select id="arbChartSelector">
						<option value="LaisIndividualAllYears|LaisNationalPastYear|LaisNationalAllYears" selected="true">Compare All Data</option>
						<option value="LaisAllCases">All Cases</option>
						<option value="LaisDisciplineCases">Discipline Cases</option>
						<option value="LaisNonDisciplineCases">Non-Discipline Cases</option>
						<option value="LaisFederalCases">Federal Cases</option>
					</select>
				</div>
				<div class="&clearClass;"></div>
			</xsl:if>
			
			<!-- add in the tables -->
			<xsl:call-template name="RenderAllTables" />
		</div>
	</xsl:template>

	<xsl:template name="RenderAllTables">
		<!-- 
			There's seven tables in total that need to get built.  They are:
				Individual (all years)
				National (past year)
				National (all years)
				
				All Cases
				Discipline
				Non-Discipline
				Federal
		-->

		<div id="co_arbChartContainerLaisIndividualAllYears" class="&arbChartSection;" name="LaisIndividualAllYears">
			<h2 class="&arbChartSectionHeading;">
				<xsl:value-of select="$arbitratorName"/>
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderIndividualAllYearsTable" />
		</div>

		<div id="co_arbChartContainerLaisNationalPastYear" class="&arbChartSection;" name="LaisNationalPastYear">
			<h2 class="&arbChartSectionHeading;">
				All LAIS Arbitrators: 
				<span>
					<xsl:value-of select="$nationalPastYearDateRange"/>
				</span>
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderNationalPastYearTable" />
		</div>

		<div id="co_arbChartContainerLaisNationalAllYears" class="&arbChartSection;" name="LaisNationalAllYears">
			<h2 class="&arbChartSectionHeading;">
				All LAIS Arbitrators: 
				<span>
					<xsl:value-of select="$nationalAllYearsDateRange"/>
				</span>
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderNationalAllYearsTable" />
		</div>

		<div id="co_arbChartContainerLaisAllCases" class="&arbChartSection;">
			<h2 class="&arbChartSectionHeading;">
				All Cases
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderAllCasesTable" />
		</div>

		<div id="co_arbChartContainerLaisDisciplineCases" class="&arbChartSection;">
			<h2 class="&arbChartSectionHeading;">
				Discipline Cases
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderDisciplineCasesTable" />
		</div>

		<div id="co_arbChartContainerLaisNonDisciplineCases" class="&arbChartSection;">
			<h2 class="&arbChartSectionHeading;">
				Non-Discipline Cases
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderNonDisciplineCasesTable" />
		</div>

		<div id="co_arbChartContainerLaisFederalCases" class="&arbChartSection;">
			<h2 class="&arbChartSectionHeading;">
				Federal Cases
			</h2>
			<xsl:call-template name="PrevailingText"/>
			<xsl:call-template name="RenderFederalCasesTable" />
		</div>

		<div id="co_asteriskText">
			<xsl:if test="not($IsMobile)">
				<p>*Statistics are present for each prevailing party.</p>
			</xsl:if>
		</div>
		
		<input id="documentGuid" type="hidden">
			<xsl:attribute name="value">
				<xsl:value-of select="$Guid"/>
			</xsl:attribute>
		</input>
		<input id="websiteHost" type="hidden">
			<xsl:attribute name="value">
				<xsl:value-of select="$Website"/>
			</xsl:attribute>
		</input>
	</xsl:template>

	<xsl:template name="PrevailingText">
		<xsl:if test="not($IsMobile)">
		<div class="&centerClass;">
			Prevailing Party*
		</div>
		</xsl:if>
	</xsl:template>

	<!--
		Gather the table data.
	
		These are declared at the global level so that the multiple table
		building functions (there are 7 of them) can all access the data.  Some of the tables
		are just a different view of the data and reuse the same xpath data points.
		
		Declaring these globally reduces parameter and xpath code duplication as well as reducing the
		xpath execution time.
	-->
	<xsl:variable name="individualSourceTable" select="//individual.stats.doc/tbl/table/tgroup/tbody" />
	<xsl:variable name="pastYearSourceTable" select="//natl.avg.stats.doc/tbl[1]/table/tgroup/tbody" />
	<xsl:variable name="allYearsSourceTable" select="//natl.avg.stats.doc/tbl[2]/table/tgroup/tbody" />

	<!-- Individual - All Cases -->
	<xsl:variable name="individualAllCasesManagementCount" select="$individualSourceTable/row[2]/entry[3]" />
	<xsl:variable name="individualAllCasesUnionCount" select="$individualSourceTable/row[2]/entry[4]" />
	<xsl:variable name="individualAllCasesSplitCount" select="$individualSourceTable/row[2]/entry[5]" />
	<xsl:variable name="individualAllCasesInterestCount" select="$individualSourceTable/row[2]/entry[6]" />
	<xsl:variable name="individualAllCasesJurisdictionalDisputeCount" select="$individualSourceTable/row[2]/entry[7]" />

	<xsl:variable name="individualAllCasesManagementPercentage" select="$individualSourceTable/row[3]/entry[3]" />
	<xsl:variable name="individualAllCasesUnionPercentage" select="$individualSourceTable/row[3]/entry[4]" />
	<xsl:variable name="individualAllCasesSplitPercentage" select="$individualSourceTable/row[3]/entry[5]" />
	<xsl:variable name="individualAllCasesInterestPercentage" select="$individualSourceTable/row[3]/entry[6]" />
	<xsl:variable name="individualAllCasesJurisdictionalDisputePercentage" select="$individualSourceTable/row[3]/entry[7]" />

	<!-- Individual - Discipline Cases-->
	<xsl:variable name="individualDisciplineCasesManagementCount" select="$individualSourceTable/row[4]/entry[3]" />
	<xsl:variable name="individualDisciplineCasesUnionCount" select="$individualSourceTable/row[4]/entry[4]" />
	<xsl:variable name="individualDisciplineCasesSplitCount" select="$individualSourceTable/row[4]/entry[5]" />
	<xsl:variable name="individualDisciplineCasesInterestCount" select="$individualSourceTable/row[4]/entry[6]" />
	<xsl:variable name="individualDisciplineCasesJurisdictionalDisputeCount" select="$individualSourceTable/row[4]/entry[7]" />

	<xsl:variable name="individualDisciplineCasesManagementPercentage" select="$individualSourceTable/row[5]/entry[3]" />
	<xsl:variable name="individualDisciplineCasesUnionPercentage" select="$individualSourceTable/row[5]/entry[4]" />
	<xsl:variable name="individualDisciplineCasesSplitPercentage" select="$individualSourceTable/row[5]/entry[5]" />
	<xsl:variable name="individualDisciplineCasesInterestPercentage" select="$individualSourceTable/row[5]/entry[6]" />
	<xsl:variable name="individualDisciplineCasesJurisdictionalDisputePercentage" select="$individualSourceTable/row[5]/entry[7]" />

	<!-- Individual - Nondiscipline Cases-->
	<xsl:variable name="individualNonDisciplineCasesManagementCount" select="$individualSourceTable/row[6]/entry[3]" />
	<xsl:variable name="individualNonDisciplineCasesUnionCount" select="$individualSourceTable/row[6]/entry[4]" />
	<xsl:variable name="individualNonDisciplineCasesSplitCount" select="$individualSourceTable/row[6]/entry[5]" />
	<xsl:variable name="individualNonDisciplineCasesInterestCount" select="$individualSourceTable/row[6]/entry[6]" />
	<xsl:variable name="individualNonDisciplineCasesJurisdictionalDisputeCount" select="$individualSourceTable/row[6]/entry[7]" />

	<xsl:variable name="individualNonDisciplineCasesManagementPercentage" select="$individualSourceTable/row[7]/entry[3]" />
	<xsl:variable name="individualNonDisciplineCasesUnionPercentage" select="$individualSourceTable/row[7]/entry[4]" />
	<xsl:variable name="individualNonDisciplineCasesSplitPercentage" select="$individualSourceTable/row[7]/entry[5]" />
	<xsl:variable name="individualNonDisciplineCasesInterestPercentage" select="$individualSourceTable/row[7]/entry[6]" />
	<xsl:variable name="individualNonDisciplineCasesJurisdictionalDisputePercentage" select="$individualSourceTable/row[7]/entry[7]" />

	<!-- Individual - Federal Cases-->
	<xsl:variable name="individualFederalCasesManagementCount" select="$individualSourceTable/row[8]/entry[3]" />
	<xsl:variable name="individualFederalCasesUnionCount" select="$individualSourceTable/row[8]/entry[4]" />
	<xsl:variable name="individualFederalCasesSplitCount" select="$individualSourceTable/row[8]/entry[5]" />
	<xsl:variable name="individualFederalCasesInterestCount" select="$individualSourceTable/row[8]/entry[6]" />
	<xsl:variable name="individualFederalCasesJurisdictionalDisputeCount" select="$individualSourceTable/row[8]/entry[7]" />

	<xsl:variable name="individualFederalCasesManagementPercentage" select="$individualSourceTable/row[9]/entry[3]" />
	<xsl:variable name="individualFederalCasesUnionPercentage" select="$individualSourceTable/row[9]/entry[4]" />
	<xsl:variable name="individualFederalCasesSplitPercentage" select="$individualSourceTable/row[9]/entry[5]" />
	<xsl:variable name="individualFederalCasesInterestPercentage" select="$individualSourceTable/row[9]/entry[6]" />
	<xsl:variable name="individualFederalCasesJurisdictionalDisputePercentage" select="$individualSourceTable/row[9]/entry[7]" />

	<!-- National Past Year - All Cases -->
	<xsl:variable name="nationalPastYearAllCasesManagementCount" select="$pastYearSourceTable/row[2]/entry[3]" />
	<xsl:variable name="nationalPastYearAllCasesUnionCount" select="$pastYearSourceTable/row[2]/entry[4]" />
	<xsl:variable name="nationalPastYearAllCasesSplitCount" select="$pastYearSourceTable/row[2]/entry[5]" />
	<xsl:variable name="nationalPastYearAllCasesInterestCount" select="$pastYearSourceTable/row[2]/entry[6]" />
	<xsl:variable name="nationalPastYearAllCasesJurisdictionalDisputeCount" select="$pastYearSourceTable/row[2]/entry[7]" />

	<xsl:variable name="nationalPastYearAllCasesManagementPercentage" select="$pastYearSourceTable/row[3]/entry[3]" />
	<xsl:variable name="nationalPastYearAllCasesUnionPercentage" select="$pastYearSourceTable/row[3]/entry[4]" />
	<xsl:variable name="nationalPastYearAllCasesSplitPercentage" select="$pastYearSourceTable/row[3]/entry[5]" />
	<xsl:variable name="nationalPastYearAllCasesInterestPercentage" select="$pastYearSourceTable/row[3]/entry[6]" />
	<xsl:variable name="nationalPastYearAllCasesJurisdictionalDisputePercentage" select="$pastYearSourceTable/row[3]/entry[7]" />

	<!-- National Past Year - Discipline Cases-->
	<xsl:variable name="nationalPastYearDisciplineCasesManagementCount" select="$pastYearSourceTable/row[4]/entry[3]" />
	<xsl:variable name="nationalPastYearDisciplineCasesUnionCount" select="$pastYearSourceTable/row[4]/entry[4]" />
	<xsl:variable name="nationalPastYearDisciplineCasesSplitCount" select="$pastYearSourceTable/row[4]/entry[5]" />
	<xsl:variable name="nationalPastYearDisciplineCasesInterestCount" select="$pastYearSourceTable/row[4]/entry[6]" />
	<xsl:variable name="nationalPastYearDisciplineCasesJurisdictionalDisputeCount" select="$pastYearSourceTable/row[4]/entry[7]" />

	<xsl:variable name="nationalPastYearDisciplineCasesManagementPercentage" select="$pastYearSourceTable/row[5]/entry[3]" />
	<xsl:variable name="nationalPastYearDisciplineCasesUnionPercentage" select="$pastYearSourceTable/row[5]/entry[4]" />
	<xsl:variable name="nationalPastYearDisciplineCasesSplitPercentage" select="$pastYearSourceTable/row[5]/entry[5]" />
	<xsl:variable name="nationalPastYearDisciplineCasesInterestPercentage" select="$pastYearSourceTable/row[5]/entry[6]" />
	<xsl:variable name="nationalPastYearDisciplineCasesJurisdictionalDisputePercentage" select="$pastYearSourceTable/row[5]/entry[7]" />

	<!-- National Past Year - Nondiscipline Cases-->
	<xsl:variable name="nationalPastYearNonDisciplineCasesManagementCount" select="$pastYearSourceTable/row[6]/entry[3]" />
	<xsl:variable name="nationalPastYearNonDisciplineCasesUnionCount" select="$pastYearSourceTable/row[6]/entry[4]" />
	<xsl:variable name="nationalPastYearNonDisciplineCasesSplitCount" select="$pastYearSourceTable/row[6]/entry[5]" />
	<xsl:variable name="nationalPastYearNonDisciplineCasesInterestCount" select="$pastYearSourceTable/row[6]/entry[6]" />
	<xsl:variable name="nationalPastYearNonDisciplineCasesJurisdictionalDisputeCount" select="$pastYearSourceTable/row[6]/entry[7]" />

	<xsl:variable name="nationalPastYearNonDisciplineCasesManagementPercentage" select="$pastYearSourceTable/row[7]/entry[3]" />
	<xsl:variable name="nationalPastYearNonDisciplineCasesUnionPercentage" select="$pastYearSourceTable/row[7]/entry[4]" />
	<xsl:variable name="nationalPastYearNonDisciplineCasesSplitPercentage" select="$pastYearSourceTable/row[7]/entry[5]" />
	<xsl:variable name="nationalPastYearNonDisciplineCasesInterestPercentage" select="$pastYearSourceTable/row[7]/entry[6]" />
	<xsl:variable name="nationalPastYearNonDisciplineCasesJurisdictionalDisputePercentage" select="$pastYearSourceTable/row[7]/entry[7]" />

	<!-- National Past Year - Federal Cases-->
	<xsl:variable name="nationalPastYearFederalCasesManagementCount" select="$pastYearSourceTable/row[8]/entry[3]" />
	<xsl:variable name="nationalPastYearFederalCasesUnionCount" select="$pastYearSourceTable/row[8]/entry[4]" />
	<xsl:variable name="nationalPastYearFederalCasesSplitCount" select="$pastYearSourceTable/row[8]/entry[5]" />
	<xsl:variable name="nationalPastYearFederalCasesInterestCount" select="$pastYearSourceTable/row[8]/entry[6]" />
	<xsl:variable name="nationalPastYearFederalCasesJurisdictionalDisputeCount" select="$pastYearSourceTable/row[8]/entry[7]" />

	<xsl:variable name="nationalPastYearFederalCasesManagementPercentage" select="$pastYearSourceTable/row[9]/entry[3]" />
	<xsl:variable name="nationalPastYearFederalCasesUnionPercentage" select="$pastYearSourceTable/row[9]/entry[4]" />
	<xsl:variable name="nationalPastYearFederalCasesSplitPercentage" select="$pastYearSourceTable/row[9]/entry[5]" />
	<xsl:variable name="nationalPastYearFederalCasesInterestPercentage" select="$pastYearSourceTable/row[9]/entry[6]" />
	<xsl:variable name="nationalPastYearFederalCasesJurisdictionalDisputePercentage" select="$pastYearSourceTable/row[9]/entry[7]" />

	<!-- National All Years - All Cases -->
	<xsl:variable name="nationalAllYearsAllCasesManagementCount" select="$allYearsSourceTable/row[2]/entry[3]" />
	<xsl:variable name="nationalAllYearsAllCasesUnionCount" select="$allYearsSourceTable/row[2]/entry[4]" />
	<xsl:variable name="nationalAllYearsAllCasesSplitCount" select="$allYearsSourceTable/row[2]/entry[5]" />
	<xsl:variable name="nationalAllYearsAllCasesInterestCount" select="$allYearsSourceTable/row[2]/entry[6]" />
	<xsl:variable name="nationalAllYearsAllCasesJurisdictionalDisputeCount" select="$allYearsSourceTable/row[2]/entry[7]" />

	<xsl:variable name="nationalAllYearsAllCasesManagementPercentage" select="$allYearsSourceTable/row[3]/entry[3]" />
	<xsl:variable name="nationalAllYearsAllCasesUnionPercentage" select="$allYearsSourceTable/row[3]/entry[4]" />
	<xsl:variable name="nationalAllYearsAllCasesSplitPercentage" select="$allYearsSourceTable/row[3]/entry[5]" />
	<xsl:variable name="nationalAllYearsAllCasesInterestPercentage" select="$allYearsSourceTable/row[3]/entry[6]" />
	<xsl:variable name="nationalAllYearsAllCasesJurisdictionalDisputePercentage" select="$allYearsSourceTable/row[3]/entry[7]" />

	<!-- National All Years - Discipline Cases-->
	<xsl:variable name="nationalAllYearsDisciplineCasesManagementCount" select="$allYearsSourceTable/row[4]/entry[3]" />
	<xsl:variable name="nationalAllYearsDisciplineCasesUnionCount" select="$allYearsSourceTable/row[4]/entry[4]" />
	<xsl:variable name="nationalAllYearsDisciplineCasesSplitCount" select="$allYearsSourceTable/row[4]/entry[5]" />
	<xsl:variable name="nationalAllYearsDisciplineCasesInterestCount" select="$allYearsSourceTable/row[4]/entry[6]" />
	<xsl:variable name="nationalAllYearsDisciplineCasesJurisdictionalDisputeCount" select="$allYearsSourceTable/row[4]/entry[7]" />

	<xsl:variable name="nationalAllYearsDisciplineCasesManagementPercentage" select="$allYearsSourceTable/row[5]/entry[3]" />
	<xsl:variable name="nationalAllYearsDisciplineCasesUnionPercentage" select="$allYearsSourceTable/row[5]/entry[4]" />
	<xsl:variable name="nationalAllYearsDisciplineCasesSplitPercentage" select="$allYearsSourceTable/row[5]/entry[5]" />
	<xsl:variable name="nationalAllYearsDisciplineCasesInterestPercentage" select="$allYearsSourceTable/row[5]/entry[6]" />
	<xsl:variable name="nationalAllYearsDisciplineCasesJurisdictionalDisputePercentage" select="$allYearsSourceTable/row[5]/entry[7]" />

	<!-- National All Years - Nondiscipline Cases-->
	<xsl:variable name="nationalAllYearsNonDisciplineCasesManagementCount" select="$allYearsSourceTable/row[6]/entry[3]" />
	<xsl:variable name="nationalAllYearsNonDisciplineCasesUnionCount" select="$allYearsSourceTable/row[6]/entry[4]" />
	<xsl:variable name="nationalAllYearsNonDisciplineCasesSplitCount" select="$allYearsSourceTable/row[6]/entry[5]" />
	<xsl:variable name="nationalAllYearsNonDisciplineCasesInterestCount" select="$allYearsSourceTable/row[6]/entry[6]" />
	<xsl:variable name="nationalAllYearsNonDisciplineCasesJurisdictionalDisputeCount" select="$allYearsSourceTable/row[6]/entry[7]" />

	<xsl:variable name="nationalAllYearsNonDisciplineCasesManagementPercentage" select="$allYearsSourceTable/row[7]/entry[3]" />
	<xsl:variable name="nationalAllYearsNonDisciplineCasesUnionPercentage" select="$allYearsSourceTable/row[7]/entry[4]" />
	<xsl:variable name="nationalAllYearsNonDisciplineCasesSplitPercentage" select="$allYearsSourceTable/row[7]/entry[5]" />
	<xsl:variable name="nationalAllYearsNonDisciplineCasesInterestPercentage" select="$allYearsSourceTable/row[7]/entry[6]" />
	<xsl:variable name="nationalAllYearsNonDisciplineCasesJurisdictionalDisputePercentage" select="$allYearsSourceTable/row[7]/entry[7]" />

	<!-- National All Years - Federal Cases-->
	<xsl:variable name="nationalAllYearsFederalCasesManagementCount" select="$allYearsSourceTable/row[8]/entry[3]" />
	<xsl:variable name="nationalAllYearsFederalCasesUnionCount" select="$allYearsSourceTable/row[8]/entry[4]" />
	<xsl:variable name="nationalAllYearsFederalCasesSplitCount" select="$allYearsSourceTable/row[8]/entry[5]" />
	<xsl:variable name="nationalAllYearsFederalCasesInterestCount" select="$allYearsSourceTable/row[8]/entry[6]" />
	<xsl:variable name="nationalAllYearsFederalCasesJurisdictionalDisputeCount" select="$allYearsSourceTable/row[8]/entry[7]" />

	<xsl:variable name="nationalAllYearsFederalCasesManagementPercentage" select="$allYearsSourceTable/row[9]/entry[3]" />
	<xsl:variable name="nationalAllYearsFederalCasesUnionPercentage" select="$allYearsSourceTable/row[9]/entry[4]" />
	<xsl:variable name="nationalAllYearsFederalCasesSplitPercentage" select="$allYearsSourceTable/row[9]/entry[5]" />
	<xsl:variable name="nationalAllYearsFederalCasesInterestPercentage" select="$allYearsSourceTable/row[9]/entry[6]" />
	<xsl:variable name="nationalAllYearsFederalCasesJurisdictionalDisputePercentage" select="$allYearsSourceTable/row[9]/entry[7]" />

	<!-- Totals -->
	<xsl:variable name="individualAllCasesTotalCount" select="$individualSourceTable/row[2]/entry[2]" />
	<xsl:variable name="individualDisciplineCasesTotalCount" select="$individualSourceTable/row[4]/entry[2]" />
	<xsl:variable name="individualNonDisciplineCasesTotalCount" select="$individualSourceTable/row[6]/entry[2]" />
	<xsl:variable name="individualFederalCasesTotalCount" select="$individualSourceTable/row[8]/entry[2]" />

	<xsl:variable name="nationalPastYearAllCasesTotalCount" select="$pastYearSourceTable/row[2]/entry[2]" />
	<xsl:variable name="nationalPastYearDisciplineCasesTotalCount" select="$pastYearSourceTable/row[4]/entry[2]" />
	<xsl:variable name="nationalPastYearNonDisciplineCasesTotalCount" select="$pastYearSourceTable/row[6]/entry[2]" />
	<xsl:variable name="nationalPastYearFederalCasesTotalCount" select="$pastYearSourceTable/row[8]/entry[2]" />

	<xsl:variable name="nationalAllYearsAllCasesTotalCount" select="$allYearsSourceTable/row[2]/entry[2]" />
	<xsl:variable name="nationalAllYearsDisciplineCasesTotalCount" select="$allYearsSourceTable/row[4]/entry[2]" />
	<xsl:variable name="nationalAllYearsNonDisciplineCasesTotalCount" select="$allYearsSourceTable/row[6]/entry[2]" />
	<xsl:variable name="nationalAllYearsFederalCasesTotalCount" select="$allYearsSourceTable/row[8]/entry[2]" />
	
	<!-- Column Data -->
	<xsl:variable name="arbitratorName" select="//individual.stats.doc/tbl/head/headtext" />
	<xsl:variable name="nationalPastYearDateRange" select="//natl.avg.stats.doc/tbl[1]/head/headtext" />
	<xsl:variable name="nationalAllYearsDateRange" select="//natl.avg.stats.doc/tbl[2]/head/headtext" />

	<xsl:template name="RenderIndividualAllYearsTable">
		<table id="co_arb_individualAllYears">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2"><xsl:call-template name="mobilePrevailingPartyText" /></th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Cases</th>
					<th class="&arbThGreen;" colspan="2">Management</th>
					<th class="&arbThBlue;" colspan="2">Union</th>
					<th class="&arbThOrange;" colspan="2">Split</th>
					<th class="&arbThPurple;" colspan="2">Interest</th>
					<th class="&arbThRed;" colspan="2">Jurisdictional</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							All Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
					<xsl:choose>
						<xsl:when test="$individualAllCasesManagementCount = '0' and $individualAllCasesUnionCount = '0' and $individualAllCasesSplitCount = '0' and $individualAllCasesInterestCount = '0' and $individualAllCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualAllCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Discipline Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$individualDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$individualDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$individualDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$individualDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$individualDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualDisciplineCasesManagementCount = '0' and $individualDisciplineCasesUnionCount = '0' and $individualDisciplineCasesSplitCount = '0' and $individualDisciplineCasesInterestCount = '0' and $individualDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Non-Discipline Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualNonDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$individualNonDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualNonDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualNonDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$individualNonDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualNonDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualNonDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$individualNonDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualNonDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualNonDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$individualNonDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualNonDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualNonDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$individualNonDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualNonDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualNonDisciplineCasesManagementCount = '0' and $individualNonDisciplineCasesUnionCount = '0' and $individualNonDisciplineCasesSplitCount = '0' and $individualNonDisciplineCasesInterestCount = '0' and $individualNonDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualNonDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Federal Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualFederalCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$individualFederalCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualFederalCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualFederalCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$individualFederalCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualFederalCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualFederalCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$individualFederalCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualFederalCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualFederalCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$individualFederalCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualFederalCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualFederalCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$individualFederalCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualFederalCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualFederalCasesManagementCount = '0' and $individualFederalCasesUnionCount = '0' and $individualFederalCasesSplitCount = '0' and $individualFederalCasesInterestCount = '0' and $individualFederalCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualFederalCasesTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderNationalPastYearTable">
		<table id="co_arb_nationalPastYear">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Cases</th>
					<th class="&arbThGreen;" colspan="2">Management</th>
					<th class="&arbThBlue;" colspan="2">Union</th>
					<th class="&arbThOrange;" colspan="2">Split</th>
					<th class="&arbThPurple;" colspan="2">Interest</th>
					<th class="&arbThRed;" colspan="2">Jurisdictional</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							All Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalPastYearAllCasesManagementCount = '0' and $nationalPastYearAllCasesUnionCount = '0' and $nationalPastYearAllCasesSplitCount = '0' and $nationalPastYearAllCasesInterestCount = '0' and $nationalPastYearAllCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearAllCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Discipline Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
					<xsl:choose>
						<xsl:when test="$nationalPastYearDisciplineCasesManagementCount = '0' and $nationalPastYearDisciplineCasesUnionCount = '0' and $nationalPastYearDisciplineCasesSplitCount = '0' and $nationalPastYearDisciplineCasesInterestCount = '0' and $nationalPastYearDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Non-Discipline Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearNonDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearNonDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearNonDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearNonDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearNonDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearNonDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearNonDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearNonDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearNonDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearNonDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalPastYearNonDisciplineCasesManagementCount = '0' and $nationalPastYearNonDisciplineCasesUnionCount = '0' and $nationalPastYearNonDisciplineCasesSplitCount = '0' and $nationalPastYearNonDisciplineCasesInterestCount = '0' and $nationalPastYearNonDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Federal Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearFederalCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearFederalCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearFederalCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearFederalCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearFederalCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearFederalCasesUnionCount"/>
					</td>
					<td  class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearFederalCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearFederalCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearFederalCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearFederalCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearFederalCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearFederalCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearFederalCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearFederalCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearFederalCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalPastYearFederalCasesManagementCount = '0' and $nationalPastYearFederalCasesUnionCount = '0' and $nationalPastYearFederalCasesSplitCount = '0' and $nationalPastYearFederalCasesInterestCount = '0' and $nationalPastYearFederalCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearFederalCasesTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderNationalAllYearsTable">
		<table id="co_arb_nationalAllYears">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Cases</th>
					<th class="&arbThGreen;" colspan="2">Management</th>
					<th class="&arbThBlue;" colspan="2">Union</th>
					<th class="&arbThOrange;" colspan="2">Split</th>
					<th class="&arbThPurple;" colspan="2">Interest</th>
					<th class="&arbThRed;" colspan="2">Jurisdictional</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							All Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsAllCasesManagementCount = '0' and $nationalAllYearsAllCasesUnionCount = '0' and $nationalAllYearsAllCasesSplitCount = '0' and $nationalAllYearsAllCasesInterestCount = '0' and $nationalAllYearsAllCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsAllCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Discipline Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsDisciplineCasesManagementCount = '0' and $nationalAllYearsDisciplineCasesUnionCount = '0' and $nationalAllYearsDisciplineCasesSplitCount = '0' and $nationalAllYearsDisciplineCasesInterestCount = '0' and $nationalAllYearsDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Non-Discipline Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsNonDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsNonDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsNonDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsNonDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsNonDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsNonDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsNonDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsNonDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsNonDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsNonDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
							<xsl:choose>
						<xsl:when test="$nationalAllYearsNonDisciplineCasesManagementCount = '0' and $nationalAllYearsNonDisciplineCasesUnionCount = '0' and $nationalAllYearsNonDisciplineCasesSplitCount = '0' and $nationalAllYearsNonDisciplineCasesInterestCount = '0' and $nationalAllYearsNonDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							Federal Cases
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsFederalCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsFederalCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsFederalCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsFederalCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsFederalCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsFederalCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsFederalCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsFederalCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsFederalCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsFederalCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsFederalCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsFederalCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsFederalCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsFederalCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsFederalCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
							<xsl:choose>
						<xsl:when test="$nationalAllYearsFederalCasesManagementCount = '0' and $nationalAllYearsFederalCasesUnionCount = '0' and $nationalAllYearsFederalCasesSplitCount = '0' and $nationalAllYearsFederalCasesInterestCount = '0' and $nationalAllYearsFederalCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsFederalCasesTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderAllCasesTable">
		<table id="co_arb_allCases">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">All Cases</th>
					<th class="&arbThGreen;" colspan="2">Management</th>
					<th class="&arbThBlue;" colspan="2">Union</th>
					<th class="&arbThOrange;" colspan="2">Split</th>
					<th class="&arbThPurple;" colspan="2">Interest</th>
					<th class="&arbThRed;" colspan="2">Jurisdictional</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$arbitratorName"/>
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualAllCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$individualAllCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualAllCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualAllCasesManagementCount = '0' and $individualAllCasesUnionCount = '0' and $individualAllCasesSplitCount = '0' and $individualAllCasesInterestCount = '0' and $individualAllCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualAllCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalPastYearDateRange"/>
						</strong>
						<br/>LAIS Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearAllCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearAllCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearAllCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
					<xsl:choose>
						<xsl:when test="$nationalPastYearAllCasesManagementCount = '0' and $nationalPastYearAllCasesUnionCount = '0' and $nationalPastYearAllCasesSplitCount = '0' and $nationalPastYearAllCasesInterestCount = '0' and $nationalPastYearAllCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearAllCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							All Years
						</strong>
						<br/>LAIS Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsAllCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsAllCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsAllCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsAllCasesManagementCount = '0' and $nationalAllYearsAllCasesUnionCount = '0' and $nationalAllYearsAllCasesSplitCount = '0' and $nationalAllYearsAllCasesInterestCount = '0' and $nationalAllYearsAllCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsAllCasesTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderDisciplineCasesTable">
		<table id="co_arb_disciplineCases">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Discipline Cases</th>
					<th class="&arbThGreen;" colspan="2">Management</th>
					<th class="&arbThBlue;" colspan="2">Union</th>
					<th class="&arbThOrange;" colspan="2">Split</th>
					<th class="&arbThPurple;" colspan="2">Interest</th>
					<th class="&arbThRed;" colspan="2">Jurisdictional</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$arbitratorName"/>
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$individualDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$individualDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$individualDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$individualDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$individualDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualDisciplineCasesManagementCount = '0' and $individualDisciplineCasesUnionCount = '0' and $individualDisciplineCasesSplitCount = '0' and $individualDisciplineCasesInterestCount = '0' and $individualDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalPastYearDateRange"/>
						</strong>
						<br/>LAIS Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalPastYearDisciplineCasesManagementCount = '0' and $nationalPastYearDisciplineCasesUnionCount = '0' and $nationalPastYearDisciplineCasesSplitCount = '0' and $nationalPastYearDisciplineCasesInterestCount = '0' and $nationalPastYearDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							All Years
						</strong>
						<br/>LAIS Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsDisciplineCasesManagementCount = '0' and $nationalAllYearsDisciplineCasesUnionCount = '0' and $nationalAllYearsDisciplineCasesSplitCount = '0' and $nationalAllYearsDisciplineCasesInterestCount = '0' and $nationalAllYearsDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderNonDisciplineCasesTable">
		<table id="co_arb_nonDisciplineCases">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Non-Discipline Cases</th>
					<th class="&arbThGreen;" colspan="2">Management</th>
					<th class="&arbThBlue;" colspan="2">Union</th>
					<th class="&arbThOrange;" colspan="2">Split</th>
					<th class="&arbThPurple;" colspan="2">Interest</th>
					<th class="&arbThRed;" colspan="2">Jurisdictional</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$arbitratorName"/>
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualNonDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$individualNonDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualNonDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualNonDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$individualNonDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualNonDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualNonDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$individualNonDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualNonDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualNonDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$individualNonDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualNonDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualNonDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$individualNonDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualNonDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualNonDisciplineCasesManagementCount = '0' and $individualNonDisciplineCasesUnionCount = '0' and $individualNonDisciplineCasesSplitCount = '0' and $individualNonDisciplineCasesInterestCount = '0' and $individualNonDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualNonDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalPastYearDateRange"/>
						</strong>
						<br/>LAIS Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearNonDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearNonDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearNonDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearNonDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearNonDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearNonDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearNonDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearNonDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearNonDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearNonDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalPastYearNonDisciplineCasesManagementCount = '0' and $nationalPastYearNonDisciplineCasesUnionCount = '0' and $nationalPastYearNonDisciplineCasesSplitCount = '0' and $nationalPastYearNonDisciplineCasesInterestCount = '0' and $nationalPastYearNonDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearNonDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							All Years
						</strong>
						<br/>LAIS Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsNonDisciplineCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsNonDisciplineCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsNonDisciplineCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsNonDisciplineCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsNonDisciplineCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsNonDisciplineCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsNonDisciplineCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsNonDisciplineCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsNonDisciplineCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsNonDisciplineCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$nationalAllYearsNonDisciplineCasesManagementCount = '0' and $nationalAllYearsNonDisciplineCasesUnionCount = '0' and $nationalAllYearsNonDisciplineCasesSplitCount = '0' and $nationalAllYearsNonDisciplineCasesInterestCount = '0' and $nationalAllYearsNonDisciplineCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsNonDisciplineCasesTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="RenderFederalCasesTable">
		<table id="co_arb_federalCases">
			<thead>
				<tr>
					<th class="&arbThTitle;"></th>
					<th class="&arbThGreenBG;" colspan="2">
						<xsl:call-template name="mobilePrevailingPartyText" />
					</th>
					<th class="&arbThBlueBG;" colspan="2"></th>
					<th class="&arbThOrangeBG;" colspan="2"></th>
					<th class="&arbThPurpleBG;" colspan="2"></th>
					<th class="&arbThRedBG;" colspan="2"></th>
					<th class="&arbThTotal;" colspan="2"></th>
				</tr>
				<tr>
					<th class="&arbThTitle;">Federal Cases</th>
					<th class="&arbThGreen;" colspan="2">Management</th>
					<th class="&arbThBlue;" colspan="2">Union</th>
					<th class="&arbThOrange;" colspan="2">Split</th>
					<th class="&arbThPurple;" colspan="2">Interest</th>
					<th class="&arbThRed;" colspan="2">Jurisdictional</th>
					<th class="&arbThTotal;" colspan="2">Total Cases</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$arbitratorName"/>
						</strong>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualFederalCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$individualFederalCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualFederalCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualFederalCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$individualFederalCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualFederalCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualFederalCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$individualFederalCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualFederalCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualFederalCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$individualFederalCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualFederalCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$individualFederalCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$individualFederalCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$individualFederalCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
						<xsl:when test="$individualFederalCasesManagementCount = '0' and $individualFederalCasesUnionCount = '0' and $individualFederalCasesSplitCount = '0' and $individualFederalCasesInterestCount= '0' and $individualFederalCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					  </xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$individualFederalCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							<xsl:value-of select="$nationalPastYearDateRange"/>
						</strong>
						<br/>LAIS Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearFederalCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearFederalCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearFederalCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearFederalCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearFederalCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearFederalCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearFederalCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearFederalCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearFederalCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearFederalCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearFederalCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearFederalCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalPastYearFederalCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalPastYearFederalCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalPastYearFederalCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
					<xsl:choose>
						<xsl:when test="$nationalPastYearFederalCasesManagementCount = '0' and $nationalPastYearFederalCasesUnionCount = '0' and $nationalPastYearFederalCasesSplitCount = '0' and $nationalPastYearFederalCasesInterestCount = '0' and $nationalPastYearFederalCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
						<xsl:otherwise>100%</xsl:otherwise>
					</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalPastYearFederalCasesTotalCount"/>
					</td>
				</tr>
				<tr>
					<td class="&arbTdTitle;">
						<strong>
							All Years
						</strong>
						<br/>LAIS Arbitrators
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsFederalCasesManagementPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsFederalCasesManagementCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsFederalCasesManagementCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsFederalCasesUnionPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsFederalCasesUnionCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsFederalCasesUnionCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsFederalCasesSplitPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsFederalCasesSplitCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsFederalCasesSplitCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsFederalCasesInterestPercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsFederalCasesInterestCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsFederalCasesInterestCount"/>
					</td>
					<td class="&arbTdPercentage;">
						<xsl:call-template name="determinePercentage">
							<xsl:with-param name="percentage" select="$nationalAllYearsFederalCasesJurisdictionalDisputePercentage"/>
							<xsl:with-param name="count" select="$nationalAllYearsFederalCasesJurisdictionalDisputeCount"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="$nationalAllYearsFederalCasesJurisdictionalDisputeCount"/>
					</td>
					<td class="&arbTdTotalPercentage;">
						<xsl:choose>
							<xsl:when test="$nationalAllYearsFederalCasesManagementCount = '0' and $nationalAllYearsFederalCasesUnionCount = '0' and $nationalAllYearsFederalCasesSplitCount = '0' and $nationalAllYearsFederalCasesInterestCount = '0' and $nationalAllYearsFederalCasesJurisdictionalDisputeCount = '0'">0%</xsl:when>
							<xsl:otherwise>100%</xsl:otherwise>
						</xsl:choose>
					</td>
					<td class="&arbTdTotalCount;">
						<xsl:value-of select="$nationalAllYearsFederalCasesTotalCount"/>
					</td>
				</tr>
				<xsl:call-template name="mobileTableFooter" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template name="determinePercentage">
		<xsl:param name="percentage" />
		<xsl:param name="count" />

		<xsl:choose>
			<xsl:when test ="$percentage = 'NaN%'">
				<xsl:text>0%</xsl:text>
			</xsl:when>
			<xsl:when test="$percentage = '0%' and $count &gt; 0">
				<xsl:text>&lt;1%</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$percentage"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="mobileTableFooter">
		<xsl:if test="$IsMobile">
			<tfoot>
				<tr>
					<td colspan="13">*Statistics are present for each prevailing party.</td>
				</tr>
			</tfoot>
		</xsl:if>
	</xsl:template>

	<xsl:template name="mobilePrevailingPartyText">
		<xsl:if test="$IsMobile">
			<span style="font-weight:normal">Prevailing Party*</span>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>