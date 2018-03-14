<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Prelim.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="registeredCountry">
		<xsl:value-of select="//regis.country.b/regis.country"/>
	</xsl:variable>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses" />
			<div class="&sazanamiMinchoClass; &contentTypeTrademarkClass; &contentTypeIPDocumentClass;">
				<xsl:call-template name="displayDocumentHeader" />
				<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				<table class="&layout_table;">
					<xsl:apply-templates />
				</table>
				<xsl:call-template name="EndOfDocument" />
			</div>
		</div>
	</xsl:template>

	<xsl:template name="displayDocumentHeader">
		<xsl:choose>
			<xsl:when test="not($DeliveryMode)">
				<table class="&layout_table; &layout_headerTable;">
					<tr>
						<td>
							<xsl:call-template name="displayHeaderText" />
						</td>
						<td align="right">
							<xsl:apply-templates select="//image.block" mode="headerInlineImage" />
						</td>
					</tr>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="displayHeaderText" />
				<div class="&panelBlockClass;">
					<center>
						<xsl:apply-templates select="//image.block" mode="headerInlineImage" />
					</center>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="displayHeaderText">
		<xsl:variable name="title" select="//md.ip.descriptions/md.ip.title" />
		<div>
			<h2>
				<xsl:choose>
					<xsl:when test="$title and $title != ''">
						<xsl:value-of select="$title" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="//tm.name" />
					</xsl:otherwise>
				</xsl:choose>
			</h2>
		</div>
		<div>
			<xsl:apply-templates select="//cmd.cites" mode="displayTrademarkHeader" />
		</div>
		<div>
			<xsl:choose>
				<xsl:when test="//md.ip.application.date">
					<xsl:text>Filed Date: </xsl:text>
					<xsl:apply-templates select="//md.ip.application.date" />
				</xsl:when>
				<xsl:when test="//md.ip.registration.date">
					<xsl:text>Registration Date: </xsl:text>
					<xsl:apply-templates select="//md.ip.registration.date" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="//md.filedate" />
				</xsl:otherwise>
			</xsl:choose>
		</div>
		<div>
			<xsl:apply-templates select="//md.ip.most.recent.status" />
		</div>
	</xsl:template>
	
	<xsl:template match="own.name.china" priority="1" />

	<xsl:template match="cmd.cites" priority="2" />

	<!-- Suppress these elements from display. -->
	<xsl:template match="prelim/database" />

	<xsl:template match="prelim/database" mode="displayTrademarkHeader">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="cite.query[@w-ref-type = 'MK']">
		<xsl:call-template name="citeQuery">
			<xsl:with-param name="citeQueryElement" select="." />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="md.filedate | md.ip.application.date | md.ip.registration.date">
		<xsl:call-template name="parseYearMonthDayDateFormat">
			<xsl:with-param name="displayDay" select="'true'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="cmd.cites" mode="displayTrademarkHeader">
		<xsl:if test="cmd.first.line.cite and string-length(normalize-space(cmd.first.line.cite)) &gt; 0">
			<xsl:apply-templates select="cmd.first.line.cite" />
			<xsl:if test="cmd.second.line.cite and string-length(normalize-space(cmd.second.line.cite)) &gt; 0">
				<xsl:text> | </xsl:text>	
			</xsl:if>
		</xsl:if>
		<xsl:if test="cmd.second.line.cite and string-length(normalize-space(cmd.second.line.cite)) &gt; 0">
				<xsl:apply-templates select="cmd.second.line.cite" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="prelim">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="label" priority="1">
		<strong>
			<xsl:apply-templates />
		</strong>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="date.info">
		<xsl:if test="descendant::*[contains(name(),'.b')]">
			<tr class="&borderTopClass;">
				<td>
					<xsl:variable name="nodeNameKey" select="translate(name(.), '&#46;', '')" />
					<xsl:variable name="defaultText" select="./head/headtext" />
					<strong>
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
					</strong>
				</td>
				<td>
					<xsl:apply-templates select="./node()[not(name() = 'head')]" />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="mark.info | status.info | identify.info | journal.info | goods.services | usage.info
							| owner.info | owner.affiliate.info | assignment.info | exhibition.info | interested.parties
							| usage.parties | historical.info | conflict.info | international.info | display.info | notes | goods.services">
		<xsl:if test="./*[not(self::head) and not(self::bos) and not(self::eos)]">
			<tr class="&borderTopClass;">
				<td>
					<xsl:variable name="nodeNameKey" select="translate(name(.), '&#46;', '')" />
					<xsl:variable name="defaultText" select="./head/headtext" />
					<strong>
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
					</strong>
				</td>
				<td>
					<xsl:apply-templates select="./node()[not(name() = 'head')]" />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="class.info | priority.info">
		<tr class="&borderTopClass;">
			<td>
				<!-- For Patents we have different heading text for class.info and priority.info so we need to differentiate
						the keys that are used to retrieve the display text here. -->
				<xsl:variable name="nodeNameKey" select="concat(translate(name(.), '&#46;', ''), 'trademarks')" />
				<xsl:variable name="defaultText" select="./head/headtext" />
				<strong>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
				</strong>
			</td>
			<td>
				<xsl:apply-templates select="./node()[not(name() = 'head')]" />
			</td>
		</tr>
	</xsl:template>
		
	<xsl:template match="display.info">
		<!-- Since we pulled the image out of the Display section; if there are no other elements in that
				section then we do not want to display it at all. -->
		<xsl:if test="./*[not(self::head) and not(self::image.block) and not(self::bos) and not(self::eos)]">
			<tr class="&borderTopClass;">
				<td>
					<xsl:variable name="nodeNameKey" select="translate(name(.), '&#46;', '')" />
					<xsl:variable name="defaultText" select="./head/headtext" />
					<strong>
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
					</strong>
				</td>
				<td>
					<xsl:apply-templates select="./node()[not(name() = 'head')]" />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="hist.act.descr" priority="1">
		<!-- Sometimes this element shows up without any text inside of it.
				We use this check so that it doesn't display the colon without any text in front of it. -->
		<xsl:if test="./text()">
			<strong>
				<xsl:apply-templates />
				<xsl:text>:</xsl:text>
			</strong>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="label[parent::goods.b or parent::goods.trans.b or parent::goods.notes.b or parent::goods.notes.trans.b or parent::current.owner.b or parent::owner.b or parent::ren.owner.b or parent::corres.b or parent::intl.class.b or parent::line.stip.b or parent::des.descrip.b or parent::vienna.code.b or parent::asn.corres.b or parent::manner.display.b or parent::services.b or parent::services.trans.b or parent::registrant.b or parent::subject.b or parent::agent.info.b or parent::ip.rep.b or parent::madrid.agree.b or parent::madrid.protoc.b]" priority="1">
		<div>
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>

	<xsl:template match="node()[preceding-sibling::head or (ancestor::*[preceding-sibling::head] and child::label) or preceding-sibling::*[preceding-sibling::label]]">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="owner.b | current.owner.b | assignment.b | corres.b | des.descrip.b | design.code.grp | goods.b | goods.trans.b | line.stip.b | agent.info.b | natl.class.b | intl.class.b | intl.reclass.b | manner.display.b | services.b | services.trans.b | ip.b | ip.agent.b | ip.rep.b | registrant.b | subject.b | agent.info.b | ip.rep.b | ren.owner.b | madrid.agree.b | madrid.protoc.b" priority="2">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="own.city | asn.city | asr.city | ren.own.city | corres.city" priority="1">
		<div>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::own.juris or following-sibling::ren.own.juris or following-sibling::asn.state or following-sibling::corres.state or following-sibling::asr.state">
				<xsl:text>, </xsl:text>
			</xsl:if>
			<xsl:if test="following-sibling::own.ctry">
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:if>
			<xsl:apply-templates select="following-sibling::own.juris" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::ren.own.juris" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::own.ctry" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::ren.own.ctry" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::asr.state" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::corres.state" mode="customDisplay" />
			<xsl:apply-templates select="following-sibling::asn.state" mode="customDisplay"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:apply-templates select="following-sibling::own.zip" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::ren.own.zip" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::asn.zip" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::asr.zip" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::corres.zip" mode="customDisplay" />
		</div>
	</xsl:template>

	<xsl:template match="own.ctry | own.juris" priority="3">
		<xsl:if test="not(preceding-sibling::own.city)">
			<div>
				<xsl:apply-templates />
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:apply-templates select="following-sibling::own.zip" mode="customDisplay" />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="regist.juris" priority="3">
		<xsl:if test="not(preceding-sibling::regist.city)">
			<div>
				<xsl:apply-templates />
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:apply-templates select="following-sibling::regist.zip" mode="customDisplay" />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ren.own.ctry" priority="2">
		<xsl:if test="not(preceding-sibling::ren.own.city)">
			<div>
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="own.juris | own.zip | own.ctry | asn.state | asr.state | asn.zip | asr.zip | ren.own.juris | ren.own.ctry | ren.own.zip | corres.state | corres.zip | regist.zip" priority="2" />

	<xsl:template match="own.juris | own.zip | own.ctry | asn.state | asn.zip | asr.state | asr.zip | ren.own.juris | ren.own.ctry | ren.own.zip | corres.state | corres.zip | regist.zip" mode="customDisplay">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="dur.txt" priority="2">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="col.key | accession.no | word.count | intl.class.cnt | des.code.count | db.id | application.no.extra | regis.no.extra | cm.des.code.cat1 | 
								cm.des.code.cat2 | date.entered.saegis | des.code.cat1 | des.code.cat2 | date.updated | own.party | asn.state | asn.zip | asr.state | asr.zip" priority="2"/>


	<xsl:template match="hist.pub.src | hist.pub.num.txt | hist.pub.num | hist.pub.pg.txt | hist.pub.pg | hist.pub.date.txt | hist.pub.date | dur.value 
		| hisp.act.info | hist.resp.date | priority.type | intl.class.code" priority="2">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="lang[preceding-sibling::lang]">
		<xsl:text>, </xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="hist.act.date">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()">
			<xsl:text>, </xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="hist.act.country">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="des.code.b" priority="1">
		<xsl:for-each select="us.des.code.b">
			<xsl:sort select="des.code" data-type="number" order="ascending"/>
			<div>
				<xsl:apply-templates select="."/>
			</div>
		</xsl:for-each>
		<xsl:for-each select="cm.des.code.b">
			<xsl:sort select="cm.des.code" data-type="number" order="ascending"/>
			<div>
				<xsl:apply-templates select="."/>
			</div>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="des.code">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::des.code.txt">
			<xsl:text> <![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="des.code.txt">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="cm.des.code">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="pto.status.b" priority="1">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="pto.status.code" priority="1" />

	<xsl:template match="pto.status" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ttab.item" priority="1">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="ttab.ref" priority="1">
		<xsl:call-template name="citeQuery">
			<xsl:with-param name="citeQueryElement" select="cite.query"/>
		</xsl:call-template>
		<xsl:apply-templates select="text()[position() &gt; 1]"/>
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ttab.doc.type | ttab.filed.date">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="line.stip.b" priority="1">
		<div>
			<xsl:apply-templates />
		</div>
		<xsl:if test="following-sibling::design.code.grp">
			<div>&#160;</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="priority.country">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="priority.number.b">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="priority.date">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!--<xsl:template match="date.info" priority="1">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>-->

	<xsl:template match="date.pub.wdrwn.b" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="date.pub.wdrwn" priority="1">
		<xsl:for-each select=".">
			<xsl:value-of select="."/>
			<xsl:if test="following-sibling::date.pub.wdrwn">
				<xsl:text>, </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="natl.class.b" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="natl.class" priority="1">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="natl.class.code" priority="1">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::natl.class.desc">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>


	<xsl:template match="other.reg.no">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="vienna.code">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="vienna.code.grp" priority="1">
		<div class="&paraMainClass;">
			<xsl:apply-templates select="label"/>
			<table  class="&viennaCode;" >
				<xsl:apply-templates select="vienna.code.b"/>
			</table>
		</div>
	</xsl:template>

	<xsl:template match="vienna.code.b" priority="1">
		<tr>
			<xsl:apply-templates/>
		</tr>
	</xsl:template>

	<xsl:template match="vienna.code" priority="1">
		<td>
			<xsl:apply-templates/>
		</td>
	</xsl:template>

	<xsl:template match="vienna.code.txt" priority="1">
		<td>
			<xsl:apply-templates/>
		</td>
	</xsl:template>

	<xsl:template match="misc.b" priority="1">
		<xsl:choose>
			<xsl:when test="position() > 1 and not(preceding-sibling::head)">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&panelBlockClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- The first goods.info.hist.b element already has a space above it so we just want to space out the following instances. -->
	<xsl:template match="*/goods.info.hist.b[position() > 1]">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="misc.b/misc.text">
		<xsl:choose>
			<xsl:when test="$registeredCountry = 'Singapore'">
				<xsl:call-template name="wrapWithDiv" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="misc.code">
		<xsl:choose>
			<xsl:when test="$registeredCountry = 'International Register'">
				<div>
					<strong>
						<xsl:apply-templates />
						<xsl:text>:</xsl:text>
					</strong>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
				<xsl:text>:</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="include.copyright" />

	<xsl:template match="goods.country">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&cobaltUnderlineClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- The first goods.info.hist.b element already has a space above it so we just want to space out the following instances. -->
	<xsl:template match="*/goods.info.hist.b[position() > 1]">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="goods.hist.entry">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="*/goods.limit.b[position() > 1]">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="goods.info.hist.publ.b">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="goods.info.hist.publ.b/*" priority="1">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="tm.type.b/tm.type">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()[1][self::tm.type]">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="tm.name.japan.b/tm.name.japan">
		<xsl:apply-templates />
		<!-- If there are any more phonetics, then comma separate this one from the following one -->
		<xsl:if test="following-sibling::*[1][self::tm.name.japan]">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="assignor.b/*[position() = 2]">
		<xsl:choose>
			<xsl:when test="following-sibling::*[not(child::label)]">
				<div>
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="assignee.b/*[position() = 2] | ip.b/*[position() = 2] | ip.agent.b/*[position() = 2]">
		<xsl:choose>
			<xsl:when test="following-sibling::*[not(child::label)]">
				<div>
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="juris.copyright" />

	<xsl:template match="journal.ref.blk/*[not(self::label)]">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="footnote.b/footnote">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="journal.ref.blk/journ.yr">
		(<xsl:apply-templates />)
	</xsl:template>

	<xsl:template match="licensee.b | pawnee.b" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="licensee/* | pawnee/*">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="date.ren" priority="1">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::date.ren">
			<xsl:text>, </xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="opposition.blk/label" priority="1" />

	<xsl:template match="image.block" priority="1" />
	
	<xsl:template match="image.block" mode="headerInlineImage">
		<xsl:variable name="guid" select="./image.link/@tuuid" />
		<xsl:variable name="imageMeta" select="/*/ImageMetadata/n-metadata[@guid=$guid]" />
		<xsl:variable name="renderType" select="$imageMeta/md.image.renderType" />
		<xsl:choose>
			<xsl:when test="$renderType = 'BlobError'">
				<img src="{$Images}&noImageAvailableImagePath;" class="&trademarkImageNotAvailableClass;" alt=""/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="imageBlock">
					<xsl:with-param name="suppressNoImageDisplay" select="true()" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="madrid.agree.cry" priority="1">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()[1][self::madrid.agree.cry]">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="madrid.protoc.cry" priority="1">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()[1][self::madrid.protoc.cry]">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
