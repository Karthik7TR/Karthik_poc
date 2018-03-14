<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Prelim.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses" />
			<!--<xsl:call-template name="DisplayPublisherLogo" />-->
			<xsl:apply-templates/>
			<xsl:call-template name="EndOfDocument" />
			<!--<xsl:call-template name="DisplayPublisherLogo" />-->
		</div>
	</xsl:template>

	<xsl:template match="prelim">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="head" priority="1">
		<div class="&underlineClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="label" priority="1">
		<strong>
			<xsl:apply-templates />
		</strong>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="hist.act.descr" priority="1">
		<strong>
			<xsl:apply-templates />
			<xsl:text>:</xsl:text>
		</strong>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="label[parent::goods.b or parent::goods.trans.b or parent::goods.notes.b or parent::goods.notes.trans.b or parent::current.owner.b or parent::owner.b or parent::assignee.b or parent::corres.b or parent::intl.class.b or parent::line.stip.b or parent::des.descrip.b or parent::vienna.code.b or parent::asn.corres.b or parent::assignor.b or parent::ttab.party.info.b | parent::ttab.info.b]" priority="1">
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

	<xsl:template match="owner.b | current.owner.b | assignment.b | corres.b | des.descrip.b | design.code.grp | ttab.properties | ttab.info.b | ttab.party.info.b | ttab.attorney.name.b | ttab.action.b | ttab.ser.no.b" priority="2">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="own.city | asn.city | asr.city | ttab.city" priority="1">
		<div>
			<xsl:apply-templates />
			<xsl:text>, </xsl:text>
			<xsl:apply-templates select="following-sibling::own.juris" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::own.ctry" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::asn.state" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::asr.state" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::ttab.state" mode="customDisplay"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:apply-templates select="following-sibling::own.zip" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::asn.zip" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::asr.zip" mode="customDisplay"/>
			<xsl:apply-templates select="following-sibling::ttab.zip" mode="customDisplay"/>
		</div>
	</xsl:template>

	<xsl:template match="own.ctry" priority="2">
		<xsl:if test="not(preceding-sibling::own.city)">
			<div>
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="own.juris | own.zip | own.ctry | asn.state | asn.zip | asr.state | asr.zip | ttab.city | ttab.state | ttab.zip | ttab.country " mode="customDisplay">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="dur.txt" priority="2">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="col.key | accession.no | word.count | intl.class.cnt | des.code.count | db.id | application.no.extra | regis.no.extra | cm.des.code.cat1 | 
								cm.des.code.cat2 | date.entered.saegis | des.code.cat1 | des.code.cat2 | date.updated | own.juris | own.zip | own.party | asn.state | asn.zip | asr.state | asr.zip | ttab.state | ttab.zip" priority="2"/>

	<xsl:template match="hist.pub.src | hist.pub.num.txt | hist.pub.num | hist.pub.pg.txt | hist.pub.pg | hist.pub.date.txt | hist.pub.date | dur.value | hist.act.date 
		| hisp.act.info | hist.resp.date | priority.type | intl.class.code" priority="2">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="lang[preceding-sibling::lang]">
		<xsl:text>, </xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="des.code.b" priority="1">
		<xsl:for-each select="us.des.code.b">
				<xsl:sort select="des.code" data-type="number" order="ascending"/>
			<div><xsl:apply-templates select="."/></div>
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

	<xsl:template match="cm.des.code.b">
		<tr>
			<xsl:apply-templates />
		</tr>
	</xsl:template>

	<xsl:template match="cm.des.code | cm.des.text">
		<td>
			<xsl:apply-templates />
		</td>
	</xsl:template>
	
	<xsl:template match="pto.status.b" priority="1">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="pto.status.code" priority="1">
		<xsl:apply-templates />
		<!--<xsl:if test="following-sibling::pto.status">-->
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="pto.status" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ttab.item" priority="1">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="ttab.ref" priority="1">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	
	<xsl:template match="ttab.doc.type" priority="1">
		<xsl:apply-templates/>
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
		<!--	<xsl:if test="following-sibling::priority.date"> -->
			<xsl:if test="following-sibling::node()">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="date.info" priority="1">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>		
	
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
		<div>
			<xsl:call-template name="OtherReg" >
				<xsl:with-param name="OtherRegNo" select="."/>
			</xsl:call-template>
			<xsl:if test ="not(following-sibling::other.reg.no)">
				<br/>
			</xsl:if>
		</div>
	</xsl:template>
	

	<xsl:template name="OtherReg">
		<xsl:param name="OtherRegNo" />
		<xsl:choose>
			<xsl:when test="contains($OtherRegNo,', ')">
				<xsl:value-of select="substring-before($OtherRegNo,', ')"/>
				<br/>
				<xsl:call-template name="OtherReg" >
					<xsl:with-param name="OtherRegNo" select="substring-after($OtherRegNo,', ')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>

				<xsl:value-of select="$OtherRegNo"/>
			</xsl:otherwise>
		</xsl:choose>
		<br />
	</xsl:template>

</xsl:stylesheet>