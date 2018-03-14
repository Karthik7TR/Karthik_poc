<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="DocLinks.xsl"/>
	<xsl:include href="PublicRecordsMaskedData.xsl"/>
	<xsl:include href="PublicRecordsAddress.xsl"/>
	<xsl:include href="PublicRecordsDate.xsl"/>
	<xsl:include href="PublicRecordsName.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="ssn">
		<xsl:call-template name="SSNProcess">
			<xsl:with-param name="ssnvalue" select="normalize-space(.)"/>
			<xsl:with-param name="ShowSensitivePublicRecordsData" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="prp.hs.num.pre">
		<xsl:apply-templates/>
		<xsl:if test="normalize-space(following-sibling::prp.hs.num) or normalize-space(preceding-sibling::prp.hs.num) or normalize-space(following-sibling::prp.hs.num.suf) 
				      or normalize-space(preceding-sibling::prp.hs.num.suf)">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="prp.hs.num">
		<xsl:apply-templates/>
		<xsl:if test="normalize-space(following-sibling::prp.hs.num.suf) or normalize-space(preceding-sibling::prp.hs.num.suf)">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="prp.add.b">
		<xsl:choose>
			<xsl:when test="normalize-space(full.str.add) or normalize-space(hs.num) or normalize-space(str.nm) or normalize-space(own.cty) or normalize-space(own.st) 
					        or normalize-space(own.zip)">
				<xsl:choose>
					<xsl:when test="normalize-space(full.str.add)">
						<xsl:call-template name="FormatAddress">
							<xsl:with-param name="fullStreet" select="full.str.add"/>
							<xsl:with-param name="city" select="own.cty"/>
							<xsl:with-param name="stateOrProvince" select="own.st"/>
							<xsl:with-param name="zip" select="own.zip"/>
							<xsl:with-param name="oneLine" select="true()"/>
							<xsl:with-param name="searchableLink" select="$SearchableLink"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="FormatAddress">
							<xsl:with-param name="streetNum" select="hs.num"/>
							<xsl:with-param name="street" select="str.nm"/>
							<xsl:with-param name="city" select="own.cty"/>
							<xsl:with-param name="stateOrProvince" select="own.st"/>
							<xsl:with-param name="zip" select="own.zip"/>
							<xsl:with-param name="oneLine" select="true()"/>
							<xsl:with-param name="searchableLink" select="$SearchableLink"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="normalize-space(prp.full.str.add) or normalize-space(prp.hs.num.pre) or normalize-space(prp.hs.num) or normalize-space(pr.hs.numsuf) 
					        or normalize-space(prp.dir) or normalize-space(prp.str.na) or normalize-space(prp.md) or normalize-space(prp.qud) or normalize-space(prp.apt.unt.num) 
					        or normalize-space(prp.cty) or normalize-space(prp.st) or normalize-space(prp.zip)">
				<xsl:choose>
					<xsl:when test="normalize-space(prp.full.str.add)">
						<xsl:call-template name="FormatAddress">
							<xsl:with-param name="fullStreet" select="prp.full.str.add"/>
							<xsl:with-param name="city" select="prp.cty"/>
							<xsl:with-param name="stateOrProvince" select="prp.st"/>
							<xsl:with-param name="zip" select="prp.zip"/>
							<xsl:with-param name="oneLine" select="true()"/>
							<xsl:with-param name="searchableLink" select="$SearchableLink"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="FormatAddress">
							<xsl:with-param name="streetNum" select="prp.hs.num.pre | prp.hs.num | prp.hs.num.suf" />
							<xsl:with-param name="streetDirection" select="prp.dir"/>
							<xsl:with-param name="street" select="prp.str.na" />
							<xsl:with-param name="streetSuffix" select="prp.md" />
							<!-- This is really the street type. -->
							<xsl:with-param name="streetDirectionSuffix" select="prp.qud"/>
							<!-- This is really the street suffix. -->
							<xsl:with-param name="streetUnitNumber" select="prp.apt.unt.num"/>
							<xsl:with-param name="city" select="prp.cty"/>
							<xsl:with-param name="stateOrProvince" select="prp.st"/>
							<xsl:with-param name="zip" select="prp.zip"/>
							<xsl:with-param name="oneLine" select="true()"/>
							<xsl:with-param name="searchableLink" select="$SearchableLink"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="normalize-space(de.prp.full.str.add)">
						<xsl:call-template name="FormatAddress">
							<xsl:with-param name="fullStreet" select="de.prp.full.str.add"/>
							<xsl:with-param name="city" select="de.prp.cty"/>
							<xsl:with-param name="stateOrProvince" select="de.prp.st"/>
							<xsl:with-param name="zip" select="de.prp.zip"/>
							<xsl:with-param name="oneLine" select="true()"/>
							<xsl:with-param name="searchableLink" select="$SearchableLink"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="FormatAddress">
							<xsl:with-param name="streetNum" select="de.prp.hs.num.pre | de.prp.hs.num | de.prp.hs.num.suf" />
							<xsl:with-param name="streetDirection" select="de.prp.dir"/>
							<xsl:with-param name="street" select="de.prp.str.na" />
							<xsl:with-param name="streetSuffix" select="de.prp.md" />
							<!-- This is really the street type. -->
							<xsl:with-param name="streetDirectionSuffix" select="de.prp.qud"/>
							<!-- This is really the street suffix. -->
							<xsl:with-param name="streetUnitNumber" select="de.prp.apt.unt.num"/>
							<xsl:with-param name="city" select="de.prp.cty"/>
							<xsl:with-param name="stateOrProvince" select="de.prp.st"/>
							<xsl:with-param name="zip" select="de.prp.zip"/>
							<xsl:with-param name="oneLine" select="true()"/>
							<xsl:with-param name="searchableLink" select="$SearchableLink"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="prp.hs.num.pre">
		<xsl:apply-templates/>
		<xsl:if test="normalize-space(following-sibling::prp.hs.num) or normalize-space(preceding-sibling::prp.hs.num) or normalize-space(following-sibling::prp.hs.num.suf) 
				      or normalize-space(preceding-sibling::prp.hs.num.suf)">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="prp.hs.num">
		<xsl:apply-templates/>
		<xsl:if test="normalize-space(following-sibling::prp.hs.num.suf) or normalize-space(preceding-sibling::prp.hs.num.suf)">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="de.prp.hs.num.pre">
		<xsl:apply-templates/>
		<xsl:if test="normalize-space(following-sibling::de.prp.hs.num) or normalize-space(preceding-sibling::de.prp.hs.num) or normalize-space(following-sibling::de.prp.hs.num.suf) 
				      or normalize-space(preceding-sibling::de.prp.hs.num.suf)">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="de.prp.hs.num">
		<xsl:apply-templates/>
		<xsl:if test="normalize-space(following-sibling::de.prp.hs.num.suf) or normalize-space(preceding-sibling::de.prp.hs.num.suf)">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="addr.b[normalize-space(full.str)]">
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="fullStreet" select="full.str"/>
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.b/zip.ext"/>
			<xsl:with-param name="oneLine" select="true()"/>
			<xsl:with-param name="searchableLink" select="$SearchableLink"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="addr.b | pri.addr | serv.addr.b | pers.addr.info | bill.addr.b | address">
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="streetNum" select="hse.nbr | str.num | str.nbr"/>
			<xsl:with-param name="streetDirection" select="str.pre.dir | str.dir[name(..)='addr.b']"/>
			<xsl:with-param name="street" select="str.na | str | str/ln1 | addr | addrc | str.name | def.str | str.1 | street"/>
			<xsl:with-param name="streetLineTwo" select="str/ln2"/>
			<xsl:with-param name="streetSuffix" select="str.typ | sfx | str.type | str.suf"/>
			<xsl:with-param name="streetDirectionSuffix" select="str.post.dir | str.dir[not(name(..)='addr.b')]"/>
			<xsl:with-param name="streetUnitNumber" select="unit.nbr | apt.nbr | unit | unit.b/unit.typ"/>
			<xsl:with-param name="streetUnit" select="unit.type | unit.b/unit.id"/>
			<xsl:with-param name="city" select="cty | cty | ctyc | cty.st.b/cty | def.cty | reg.cty | city"/>
			<xsl:with-param name="stateOrProvince" select="st | st.abbr | provc | prov | cty.st.b/st | reg.st | state"/>
			<xsl:with-param name="zip" select="zip.b/zip.5.cd | canzip.cd | zip | zip.b[not(normalize-space(zip.5.cd) or normalize-space(zip))] 
												| reg.zip | zip.b/zip"/>
			<xsl:with-param name="zipExt" select="zip.ext | zip.b/zip.4.cd | zip.b/zip.ext | zipext"/>
			<xsl:with-param name="country" select="ctry"/>
			<xsl:with-param name="oneLine" select="true()"/>
			<xsl:with-param name="searchableLink" select="$SearchableLink"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="apt.nbr | prp.apt.unt.num | de.prp.apt.unt.num | unit.nbr">
		<xsl:text>UNIT<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="str[normalize-space(ln1) or normalize-space(ln2)]"/>

	<xsl:template match="prim.addr.b | res.addr.b | filg.addr.b | bus.addr.b | mail.addr.b | prac.addr.b | agt.addr.b | home.addr.b | emp.addr.b">
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="street" select="prim.str | str1 | filg.str | bus.str | prac.str1 | agt.str | bus.str | home.str | emp.addr"/>
			<xsl:with-param name="streetSuffix" select="str2 | filg.str2"/>
			<xsl:with-param name="streetLineTwo" select="po.box | prac.str2 | agt.str2 | bus.str2"/>
			<xsl:with-param name="city" select="prim.cty | cty | filg.cty | bus.cty | prac.cty | agt.cty | bus.cty | home.cty | emp.cty"/>
			<xsl:with-param name="stateOrProvince" select="prim.st | st | filg.st | bus.st | prac.st | agt.st | bus.st.abbr | emp.st"/>
			<xsl:with-param name="zip" select="prim.zip.b/prim.zip | zip.b/zip | filg.zip | filg.zip.b/filg.zip | bus.zip.b/bus.zip | prac.zip.b/prac.zip | agt.zip.b/agt.zip
							                   | agt.zip[not(normalize-space(agt.zip.b/agt.zip))] | bus.zip | home.zip.b/home.zip | emp.zip"/>
			<xsl:with-param name="zipExt" select="prim.zip.b/prim.zip.ext | zip.b/zip.ext | filg.zip.b/filg.zip.ext | bus.zip.b/bus.zip.ext | prac.zip.b/prac.zip.ext
							                      | agt.zip.b/agt.zip.ext | bus.zip.ext | home.zip.b/home.zip.ext"/>
			<xsl:with-param name="country" select="filg.cntry | prac.cntry | agt.cntry"/>
			<xsl:with-param name="oneLine" select="true()"/>
			<xsl:with-param name="searchableLink" select="$SearchableLink"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="addr[not(parent::addr.b)]">
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="street" select="str" />
			<xsl:with-param name="city" select="cty"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
			<xsl:with-param name="oneLine" select="true()"/>
			<xsl:with-param name="searchableLink" select="$SearchableLink"/>
		</xsl:call-template>
	</xsl:template>

	<!-- used by People Historic and Address Historic  -->
	<xsl:template match="hist.addr.b">
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="streetNum" select="addr.b[position()=1]/str.nbr"/>
			<xsl:with-param name="streetDirection" select="addr.b[position()=1]/str.pre.dir"/>
			<xsl:with-param name="street" select="addr.b[position()=1]/str.name"/>
			<xsl:with-param name="streetSuffix" select="addr.b[position()=1]/str.type"/>
			<xsl:with-param name="streetDirectionSuffix" select="addr.b[position()=1]/str.post.dir"/>
			<xsl:with-param name="streetUnitNumber" select="addr.b[position()=1]/unit.type"/>
			<xsl:with-param name="streetUnit" select="addr.b[position()=1]/unit.nbr"/>
			<xsl:with-param name="city" select="addr.b[position()=1]/city"/>
			<xsl:with-param name="stateOrProvince" select="addr.b[position()=1]/st"/>
			<xsl:with-param name="zip" select="addr.b[position()=1]/zip.b/zip"/>
			<xsl:with-param name="zipExt" select="addr.b[position()=1]/zip.b/zip.ext"/>
			<xsl:with-param name="oneLine" select="true()"/>
			<xsl:with-param name="searchableLink" select="$SearchableLink"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="death.d | filg.d | iss.d | exp.d | mar.d | decree.d | conn.d | r.dt | reg.d | cod.iss.d | lic.iss.d | lic.exp.d | date | vsl.bld.yr | lvote.d | last.contrib.d | de.r.d | crim.d | arr.d | profile.created | filing.date">
		<xsl:call-template name="parseYearMonthDayDateFormat">
			<xsl:with-param name="suppressZeros" select="$SupressZerosInDates"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="assd.tot.val | de.sl.prc | amt | dbtr.amt | dbtr.amt | as.value | de.mort1.amt">
		<xsl:call-template name="FormatCurrency"/>
	</xsl:template>

	<xsl:template match="na.b | name.b | full.na.b | dbtr.nme.b | name.info | nm.b | off.nm | na | con.na.b | name | cont.na.b | persons.nm | own.nm1.b | personname | debt.nm.b | cred.nm.b">
		<xsl:choose>
			<xsl:when test="full.na[normalize-space(na.prefix) or normalize-space(fna) or normalize-space(mna) or normalize-space(lna) or normalize-space(na.suf)]">
				<xsl:call-template name="FormatName">
					<xsl:with-param name="prefixName" select="full.na/na.prefix"/>
					<xsl:with-param name="firstName" select="full.na/fna"/>
					<xsl:with-param name="middleName" select="full.na/mna"/>
					<xsl:with-param name="lastName" select="full.na/lna"/>
					<xsl:with-param name="suffixName" select="full.na/na.suf"/>
					<xsl:with-param name="searchableLink" select="$SearchableLink"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="full.na[normalize-space(.)] or fl.nm[normalize-space(.)] or full.nm[normalize-space(.)]">
				<xsl:call-template name="FormatName">
					<xsl:with-param name="lastName" select="full.na | dbtr.nme | dbtr.bus.nme | fl.nm | full.nm"/>
					<xsl:with-param name="suffixName" select="gen.cd"/>
					<xsl:with-param name="searchableLink" select="$SearchableLink"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dbtr.bus.nme">
				<xsl:call-template name="FormatBusinessName">
					<xsl:with-param name="cite" select="dbtr.bus.nme/cite.query"/>
					<xsl:with-param name="text" select="dbtr.bus.nme"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dbtr.nme">
				<xsl:call-template name="FormatBusinessName">
					<xsl:with-param name="cite" select="dbtr.nme/cite.query"/>
					<xsl:with-param name="text" select="dbtr.nme"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="prefixName" select="na.pre | na.prefix | pref | prefix | title | nm.pre | sal | na.t"/>
					<xsl:with-param name="firstName" select="fna | fn | f.name | f.nm | dbtr.name.b/dbtr.fst.nme | fname | fst.nm | first | first.nm | first.nmc | firstname | debt.fst.nm | cred.fst.nm"/>
					<xsl:with-param name="middleName" select="mna | mid | m.name | m.nm | dbtr.name.b/dbtr.mid | mname | mid.name | na.mid | mid.nm | middlename | debt.m.nm | cred.m.nm"/>
					<xsl:with-param name="lastName" select="lna | ln | l.name | l.nm | full.na | dbtr.name.b/dbtr.last.nme | lname | lst.nm | last | last.nm | last.nmc | na | lastname | debt.lst.nm | cred.lst.nm"/>
					<xsl:with-param name="suffixName" select="na.suf | suf | suffix | nm.suf | suf.nm | dbtr.name.b/dbtr.suf | gen.cd"/>
					<xsl:with-param name="professionalSuffixName" select="na.prof.suf | pro.ttl.cd | pro.ttl.cdc"/>
					<xsl:with-param name="searchableLink" select="$SearchableLink"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="exec.info.b">
		<xsl:choose>
			<xsl:when test="normalize-space(na.b)">
				<xsl:apply-templates select="na.b"/>
			</xsl:when>
			<xsl:when test="normalize-space(fna) or normalize-space(lna) or normalize-space(exec.name)">
				<xsl:choose>
					<xsl:when test="normalize-space(fna) or normalize-space(lna)">
						<xsl:call-template name="FormatName">
							<xsl:with-param name="firstName" select="fna"/>
							<xsl:with-param name="lastName" select="lna"/>
							<xsl:with-param name="searchableLink" select="$SearchableLink"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="exec.name"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="exn.b">
				<xsl:call-template name="FormatName">
					<xsl:with-param name="prefixName" select="exn.b/nm.pre"/>
					<xsl:with-param name="firstName" select="exn.b/nm.b/first.nm"/>
					<xsl:with-param name="middleName" select="exn.b/nm.b/mid.nm"/>
					<xsl:with-param name="lastName" select="exn.b/nm.b/last.nm"/>
					<xsl:with-param name="suffixName" select="exn.b/suf.nm"/>
					<xsl:with-param name="searchableLink" select="$SearchableLink"/>
				</xsl:call-template>
				<xsl:if test="not(exn.b/nm.b)">
					<xsl:apply-templates select="exn.b/full.exec.nm"/>
				</xsl:if>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="nm.info.b | con.loc.b">
		<xsl:choose>
			<xsl:when test="pltf.info.b and def.info.b">
				<xsl:apply-templates select="pltf.info.b[1]"/>
				<xsl:text>,<![CDATA[ ]]></xsl:text>
				<xsl:apply-templates select="def.info.b[1]"/>
			</xsl:when>
			<xsl:when test="pltf.info.b">
				<xsl:apply-templates select="pltf.info.b[1]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="con.na.b"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="mdl.yr | model.yr">
		<xsl:value-of select="substring(., 1, 4)"/>
	</xsl:template>

	<xsl:template match="own.info.b | ttl.info.b">
		<xsl:choose>
			<xsl:when test ="org.na">
				<xsl:apply-templates select="org.na"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="na.b"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="dob">
		<xsl:call-template name="FormatNonSensitiveDate"/>
	</xsl:template>


	<!-- *****************************************************
	******************** Bankruptcy & UCC Debtors ************
	**********************************************************-->
	<xsl:template match="dbtr.b">
		<xsl:apply-templates select="dbtr.nme.b"/>
		<xsl:if test="following-sibling::dbtr.b">
			<xsl:text><![CDATA[ ]]>;<![CDATA[ ]]></xsl:text>
			<xsl:apply-templates select="following-sibling::dbtr.b"/>
		</xsl:if>
	</xsl:template>

	<!--handle Superior & Dockets Bankruptcy types-->
	<xsl:template match="debtor.info.block">
		<xsl:apply-templates select="debtor[position() &lt; 3] | matched.debtor.block"/>
	</xsl:template>

	<xsl:template match="matched.debtor.block">
		<xsl:apply-templates select="debtor" />
	</xsl:template>

	<xsl:template match="debtor">
		<!--Superior bankruptcy debtor names-->
		<xsl:apply-templates select="party.name"/>

		<!--Dockets bankruptcy debtor names-->
		<xsl:apply-templates select="party.name.block/party.name"/>

		<xsl:if test="following-sibling::debtor">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="primary.title">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- for UCC NY-->
	<xsl:template match="debt.info.b">
		<xsl:apply-templates select="debt.b[position() &lt; 3]"/>
	</xsl:template>

	<xsl:template match="debt.b">
		<xsl:apply-templates select="party.nme"/>
		<xsl:if test="following-sibling::debt.b">
			<xsl:text><![CDATA[ ]]>;<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- For Work Affiliations -->
	<xsl:template match="co.nm">
		<xsl:choose>
			<xsl:when test="pri">
				<xsl:apply-templates select="pri"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- For Healthcare Licenses -->
	<xsl:template match="prov.info.b">
		<xsl:choose>
			<xsl:when test="normalize-space(na.b/full.na.b)">
				<xsl:apply-templates select="na.b/full.na.b" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="lastName" select="na.b/na"/>
					<xsl:with-param name="searchableLink" select="$SearchableLink"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- For Healthcare Licenses and Sanctions-->
	<xsl:template match="addr1.b">
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="fullStreet" select="addr.str"/>
			<xsl:with-param name="city" select="addr.cty"/>
			<xsl:with-param name="stateOrProvince" select="addr.st"/>
			<xsl:with-param name="zip" select="addr.zip.b/addr.zip"/>
			<xsl:with-param name="zipExt" select="addr.zip.b/addr.zip4"/>
			<xsl:with-param name="country" select="addr.ctry"/>
			<xsl:with-param name="oneLine" select="true()"/>
			<xsl:with-param name="searchableLink" select="$SearchableLink"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- For Real Property Foreclosure -->
	<xsl:template match="own.nm.b">
		<xsl:choose>
			<xsl:when test="normalize-space(own.nm1.b/lst.nm)">
				<xsl:apply-templates select="own.nm1.b" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="own.nm1.b/co.nm" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- For Watercraft -->
	<xsl:template match="registnt.info">
		<xsl:choose>
			<xsl:when test="normalize-space(nm.b/persons.nm)">
				<xsl:apply-templates select="nm.b/persons.nm"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="nm.b/co.nm"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- People Household-->

	<xsl:template match="arrv.d">
		<xsl:call-template name="FormatDate"/>
	</xsl:template>

	<xsl:template match="member">
		<xsl:variable name="Role">
			<xsl:choose>
				<xsl:when test="@no = '1'">
					<xsl:value-of select="'Head of Household'"/>
				</xsl:when>
				<xsl:when test="relate">
					<xsl:value-of select="'Spouse'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'Other'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="DOB">
			<xsl:choose>
				<xsl:when test="demo.b/birth.d">
					<xsl:call-template name="FormatDate">
						<xsl:with-param name="dateNode" select="demo.b/birth.d"></xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'N/A'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="VerifiedDate">
			<xsl:choose>
				<xsl:when test="demo.b/indiv.ver.d">
					<xsl:call-template name="FormatDate">
						<xsl:with-param name="dateNode" select="demo.b/indiv.ver.d"></xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'N/A'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>{'Name':'</xsl:text>
		<xsl:variable name="apos">'</xsl:variable>
		<xsl:variable name="aposCorrected"><![CDATA[&#039;]]></xsl:variable>
		<xsl:call-template name="FormatName">
			<xsl:with-param name="prefixName">
				<xsl:if test="not(na.t/optout.encrypted)">
					<xsl:value-of select="name.b/na.t"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="firstName">
				<xsl:if test="not(fna/optout.encrypted) and name.b/fna">
					<xsl:call-template name="replace">
						<xsl:with-param name="string" select="name.b/fna" />
						<xsl:with-param name="pattern" select="$apos" />
						<xsl:with-param name="replacement" select="$aposCorrected"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="middleName">
				<xsl:if test="not(mid/optout.encrypted) and name.b/mid">
					<xsl:call-template name="replace">
						<xsl:with-param name="string" select="name.b/mid" />
						<xsl:with-param name="pattern" select="$apos" />
						<xsl:with-param name="replacement" select="$aposCorrected"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="lastName">
				<xsl:if test="not(lna/optout.encrypted) and name.b/lna">
					<xsl:call-template name="replace">
						<xsl:with-param name="string" select="name.b/lna" />
						<xsl:with-param name="pattern" select="$apos" />
						<xsl:with-param name="replacement" select="$aposCorrected"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="suffixName">
				<xsl:if test="not(na.suf/optout.encrypted)">
					<xsl:value-of select="name.b/na.suf"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>','Role':'</xsl:text>
		<xsl:value-of select="$Role" />
		<xsl:text>','Date of Birth':'</xsl:text>
		<xsl:value-of select="$DOB" />
		<xsl:text>','Name/Address Confirmed:':'</xsl:text>
		<xsl:value-of select="$VerifiedDate" />
		<xsl:text>'}</xsl:text>
	</xsl:template>

	<!-- Watchlists -->
	<xsl:template match="locations.b">
		<xsl:apply-templates select="location[1]/loc.ctry"/>
	</xsl:template>

	<!-- NPI -->
	<xsl:template match="provider.b">
		<xsl:apply-templates select="org"/>
		<xsl:if test="not(normalize-space(org))">
			<xsl:call-template name="FormatName">
				<xsl:with-param name="prefixName" select="name.b/nm.pre"/>
				<xsl:with-param name="firstName" select="name.b/f.nm"/>
				<xsl:with-param name="middleName" select="name.b/m.nm"/>
				<xsl:with-param name="lastName" select="name.b/l.nm"/>
				<xsl:with-param name="suffixName" select="name.b/nm.suf"/>
				<xsl:with-param name="professionalSuffixName" select="creds"/>
				<xsl:with-param name="searchableLink" select="$SearchableLink"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- OFAC -->
	<xsl:template match="off.info.b">
		<xsl:apply-templates select="addr.b[1]" />
	</xsl:template>

	<!-- Real Property Transactions (Deeds) -->
	<xsl:template match="de.own.na.b">
		<xsl:call-template name="FormatName">
			<xsl:with-param name="firstName" select="buy.fst.na"/>
			<xsl:with-param name="middleName" select="buy.mid"/>
			<xsl:with-param name="lastName" select="buy.lst.na"/>
			<xsl:with-param name="searchableLink" select="$SearchableLink"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Experian Real Time -->
	<xsl:template match="personnames">
		<xsl:apply-templates select="personname[1]"></xsl:apply-templates>
	</xsl:template>

	<!-- Experian Real Time -->
	<xsl:template match="addresses">
		<xsl:apply-templates select="address[1]"></xsl:apply-templates>
	</xsl:template>

	<!-- Criminal Records Summary -->
	<xsl:template match="crt.info.b">
		<xsl:choose>
			<xsl:when test="normalize-space(chrg.file.d)">
				<xsl:apply-templates select="chrg.file.d"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="disp.d"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- CORPORATE  -->
	<xsl:template match="off.addr.b">
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="fullStreet" select="off.str"/>
			<xsl:with-param name="streetLineTwo" select="off.str2"/>
			<xsl:with-param name="city" select="off.cty"/>
			<xsl:with-param name="stateOrProvince" select="off.st"/>
			<xsl:with-param name="zip" select="off.zip | off.zip.b/off.zip"/>
			<xsl:with-param name="oneLine" select="true()"/>
			<xsl:with-param name="searchableLink" select="$SearchableLink"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- UCC  -->
	<xsl:template match="scrd.b">
		<xsl:choose>
			<xsl:when test="normalize-space(scrd.nme.b)">
				<xsl:choose>
					<xsl:when test="normalize-space(scrd.nme.b/scrd.bus.nme)">
						<xsl:call-template name="FormatBusinessName">
							<xsl:with-param name="cite" select="scrd.nme.b/scrd.bus.nme/cite.query"/>
							<xsl:with-param name="text" select="scrd.nme.b/scrd.bus.nme"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="normalize-space(scrd.nme.b/scrd.nme)">
						<xsl:call-template name="FormatBusinessName">
							<xsl:with-param name="cite" select="scrd.nme.b/scrd.nme/cite.query"/>
							<xsl:with-param name="text" select="scrd.nme.b/scrd.nme"/>
						</xsl:call-template>
					</xsl:when>	
					<xsl:otherwise>
						<xsl:call-template name="FormatName">
							<xsl:with-param name="firstName" select="scrd.nme.b/scrd.name.b/scrd.fst.nme"/>
							<xsl:with-param name="middleName" select="scrd.nme.b/scrd.name.b/scrd.mid"/>
							<xsl:with-param name="lastName" select="scrd.nme.b/scrd.name.b/scrd.last.nme"/>
							<xsl:with-param name="suffixName" select="scrd.nme.b/scrd.name.b/srcd.suf"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatBusinessName">
					<xsl:with-param name="cite" select="scrd.nme/cite.query"/>
					<xsl:with-param name="text" select="scrd.nme"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="asgnor.nme.b">
		<xsl:choose>
			<xsl:when test="normalize-space(asgnor.bus.nme)">
				<xsl:call-template name="FormatBusinessName">
					<xsl:with-param name="cite" select="asgnor.bus.nme/cite.query"/>
					<xsl:with-param name="text" select="asgnor.bus.nme"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="normalize-space(asgnor.nme)">
				<xsl:call-template name="FormatBusinessName">
					<xsl:with-param name="cite" select="asgnor.nme/cite.query"/>
					<xsl:with-param name="text" select="asgnor.nme"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatName">
					<xsl:with-param name="firstName" select="asgnor.name.b/asgnor.fst.nme"/>
					<xsl:with-param name="middleName" select="asgnor.name.b/asgnor.mid"/>
					<xsl:with-param name="lastName" select="asgnor.name.b/asgnor.last.nme"/>
					<xsl:with-param name="suffixName" select="asgnor.name.b/asgnor.suf"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Equifax Credit Header  -->
	<xsl:template match="primary.na.b">
		<xsl:call-template name="FormatName">
			<xsl:with-param name="firstName" select="first"/>
			<xsl:with-param name="middleName" select="na.mid"/>
			<xsl:with-param name="lastName" select="last"/>
			<xsl:with-param name="suffixName" select="na.suf"/>
			<xsl:with-param  name="lastNameFirst" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="curr.addr.b">
		<xsl:call-template name="FormatAddress">
			<xsl:with-param name="fullStreet" select="full.addr"/>
			<xsl:with-param name="streetLineTwo" select="off.str2"/>
			<xsl:with-param name="city" select="city"/>
			<xsl:with-param name="stateOrProvince" select="st"/>
			<xsl:with-param name="zip" select="zip"/>
			<xsl:with-param name="oneLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Motor Vehicle Service & Warranty   -->
	<xsl:template match="vehicle.serv.rec">
		<xsl:call-template name="FormatName">
			<xsl:with-param name="firstName" select="person.b/name.b/first"/>
			<xsl:with-param name="middleName" select="person.b/name.b/middle"/>
			<xsl:with-param name="lastName" select="person.b/name.b/last"/>
			<xsl:with-param name="suffixName" select="person.b/name.b/suffix"/>
			<xsl:with-param  name="lastNameFirst" select="true()"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
