<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="Dtags.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="aept | b | bid | browse | brwse | btbl | cn | cn.gen | cr3 | crcl | crdms | crx | ctab | cy | cy2 | cy3 | dbark | dbdd | dbdl | dbid | dbry | dbxrk | dd | djcl | dl2 | dldms | docbrk | docid | doclev | doclev.gen | doclevel.gen | dpge1 | dpge2 | dpge3 | dpge4 | dpge5 | dpsy1 | dpsy2 | dpsy3 | dtbl | efm | ert | etbl | etblf | f | find | findorig | fm.tbl1w | fm.tbl2w | fm.tbl3w | fm.tbl4w | fm.tblbw | fm.tblew | fm.tblfw | fm.tblhw | fm.tblmw | fm.tblpw | fm.tblsw | fm.tblw | fnhctk | fnhltk | fntk | fntk1 | fntk2 | fntk3 | fntkc1 | fntkc2 | fntkc3 | ftbl | ftsi | h | hcbcl | hcge | hg0toc | hg10toc | hg11toc | hg12toc | hg1toc | hg2toc | hg3toc | hg4toc | hg5toc | hg6toc | hg7toc | hg8toc | hg9toc | hist | histlink | hlb3cl | hlb4cl | hlge | hlsy | hlsy1 | hlsy2 | htbl | img.gen | indx | ir | l | linkd | linkh | links | n.tbl1w | n.tbl2w | n.tbl3w | n.tbl4w | n.tblbw | n.tblew | n.tblfw | n.tblhw | n.tblmw | n.tblpw | n.tblsw | n.tblw | nbid | note | null | orgid | ph13 | ph15 | photoi | prsaq | rbid | rc | rc.gen | rcorig | rd | re.tbl1w | re.tbl2w | re.tbl3w | re.tbl4w | re.tblbw | re.tblew | re.tblfw | re.tblhw | re.tblmw | re.tblpw | re.tblsw | re.tblw | read | ry | sfm | sort | sort1 | sortex | sortx | spsy | st | stbl | stblf | tbi | tbl1w | tbl2w | tbl3w | tbl4w | tblbw | tblew | tblfw | tblhw | tblmw | tblpw | tblsw | tblw | tbsi | tem | ticl | tih | tilcl | tilh | tndx | tndx.chapter | tndx.name | tndx.page | tndx.title | tndx.vol | tndx.volpag | tndx.volpage | uuid | vol | vols | volume | vw | wip.flag | wtbli | year">
		<xsl:call-template name="d1"/>
	</xsl:template>
	<xsl:template match="ccj | dj1 | dj2 | hca1 | hcb10 | hcb2 | hcb3 | hcb7 | hcb8 | hcb9 | hccm1 | hcfm1 | hcn1 | hcre1 | hct1 | hctk1 | hg0 | hg1 | hg10 | hg11 | hg12 | hg13 | hg14 | hg15 | hg16 | hg17 | hg18 | hg19 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | hg8 | hg9 | mt | nul1 | pr1 | pr2 | tca | tccm | tcfm | tcfmsp | tcn | tcre | tct | tctk">
		<xsl:call-template name="d2"/>
	</xsl:template>
	<xsl:template match="dj3 | fnaend | fnfmend | fnnend | fnreend | fntiend | hca2 | hccm2 | hcfm2 | hcn2 | hcre2 | hct2 | hctk2 | pr">
		<xsl:call-template name="d3"/>
	</xsl:template>
	<xsl:template match="commfh | dlc | hca3 | hccm3 | hcfm3 | hcn3 | hcre3 | hct3 | hctk3 | thc">
		<xsl:call-template name="d4"/>
	</xsl:template>
	<xsl:template match="cca | cop | fnhcb | fnhcfm | fnhcn | fnhcre | hccm | hcfm | hcfmsp | hcn | hcre | hcsy | hct | hctk | ln | los | nthca | nthccm | nthcfm | nthcn | nthcre | nthct | ti1">
		<xsl:call-template name="d5"/>
	</xsl:template>
	<xsl:template match="dlx | dptc1 | dptk0 | dptkc1 | fm.tblb | fm.tblbg | fm.tblbn | fm.tbled | fm.tblen | fm.tblf | fm.tblfn | fm.tblh | fm.tblhn | fm.tblp | fm.tblpn | fm.tblsn | fnac1 | fnac6 | fnc1 | fnfmc1 | fnmemc1 | fnnc1 | fnrec1 | fnti2x | fntic1 | ftac1 | hlb1 | hlb2 | hlb3 | hlb4 | hln1 | hlre1 | hltk1 | n.tblb | n.tblbg | n.tblbn | n.tbled | n.tblen | n.tblf | n.tblfn | n.tblh | n.tblhn | n.tblp | n.tblpn | n.tblsn | re.tblb | re.tblbg | re.tblbn | re.tbled | re.tblen | re.tblf | re.tblfn | re.tblh | re.tblhn | re.tblp | re.tblpn | re.tblsn | rhd | spcm | spdj | spfm | spn | spre | spt | sptk | tblb | tblbg | tblbn | tble | tbled | tblen | tblf | tblfn | tblh | tblhn | tblp | tblpn | tblsn | tcr | tl | tlcm | tlfm | tln | tlre | tlt | tltk | tlx | toc | tra | trcm | trfm | trn | trre">
		<xsl:call-template name="d6"/>
	</xsl:template>
	<xsl:template match="dpdclc1 | dpfmspc1 | dpmemc1 | dprclc1 | dptrsmc1 | fm.tbl2 | fm.tbl2n | fm.tbl3 | fm.tbl3n | fm.tbl4 | fm.tbl4n | hlfmsp | hlfmsp1 | hlfmsp2 | hlfmsp3 | hlfmsp4 | hln2 | hlre2 | hlt2 | hltk2 | n.tbl2 | n.tbl2n | n.tbl3 | n.tbl3n | n.tbl4 | n.tbl4n | re.tbl2 | re.tbl2n | re.tbl3 | re.tbl3n | re.tbl4 | re.tbl4n | sol1 | spb | tbl2 | tbl2n | tbl3 | tbl3n | tbl4 | tbl4n">
		<xsl:call-template name="d7"/>
	</xsl:template>
	<xsl:template match="comm0 | dpn0 | dpt0 | fm.tbl1 | fm.tbl1n | fnb | fnbx | fndj | fndj0 | fndj1x | fndj2x | fndjx | fndl | fndlx | fnfm | fnfmx | fnn | fnnx | fnre | fnrex | fnso | fnsox | fta | ftax | hln3 | hlre3 | hlren | hlt3 | hltk3 | n.tbl1 | n.tbl1n | no | re.tbl1 | re.tbl1n | smpl | tbl1 | tbl1n">
		<xsl:call-template name="d8"/>
	</xsl:template>
	<xsl:template match="cha | fm.tblm | fm.tblmn | fnhlb | fnhlfm | fnhln | fnhlre | hla | hldcl | hldcl1 | hlfm | hlmem1 | hlmem2 | hlmem3 | hlmem4 | hln | hlrcl | hlrcl1 | hlre | hltrsm | n.tblm | n.tblmn | pca | re.tblm | re.tblmn | sol | tblm | tblmn | toc">
		<xsl:call-template name="d9"/>
	</xsl:template>
	<xsl:template match="pb1el">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dptc2 | dptkc2 | fnfmc2 | fnnc2 | fnrec2 | ftac2 | pb2el">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="hpa1 | hpcm1 | hpfm1 | hpn1 | hpre1">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="2" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpcm2 | dpfm2 | dpn2 | dpre2 | fnfm2 | fnn2 | fnre2 | fntil | fta2">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="2" />
			<xsl:with-param name="line" select="2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpcm1 | dpfm1 | dpn1 | dpre1 | fnb1 | fnfm1 | fnn1 | fnre1 | fnx | fta1">
		<xsl:call-template name="d6">
			<xsl:with-param name="line" select="2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="ph13">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="3" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dptc3 | dptkc3 | fnfmc3 | fnnc3 | fnrec3 | ftac3 | pb4el">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="hpa2 | hpcm2 | hpfm2 | hpn2 | hpre2">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="4" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="ph14">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="4" />
			<xsl:with-param name="line" select="-3" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="lstcm1">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="4" />
			<xsl:with-param name="line" select="-4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mp54">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="4" />
			<xsl:with-param name="line" select="1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpcm3 | dpfm3 | dpn3 | dpre3 | fnfm3 | fnn3 | fnre3 | fta3">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="4" />
			<xsl:with-param name="line" select="2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pb5el">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="5" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="ph15">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="5" />
			<xsl:with-param name="line" select="-4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dptc4 | dptkc4 | fnfmc4 | fnnc4 | fnrec4 | ftac4">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="6" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="hpa3 | hpcm3 | hpfm3 | hpn3 | hpre3">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="6" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="lstfm1 | lstn1 | lstre1">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="6" />
			<xsl:with-param name="line" select="-4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="ph16">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="6" />
			<xsl:with-param name="line" select="-5" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpcm4 | dpfm4 | dpn4 | dpre4 | fnfm4 | fnn4 | fnre4 | fta4">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="6" />
			<xsl:with-param name="line" select="2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpfmc5 | dptc5 | fnfmc5 | fnnc5 | fnrec5">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="8" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="hpa4 | hpcm4 | hpfm4 | hpn4 | hpre4">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="8" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="lstcm2">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="8" />
			<xsl:with-param name="line" select="-4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpfm5 | dpn5 | dpre5 | fnfm5 | fnn5 | fnre5 | fta5">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="8" />
			<xsl:with-param name="line" select="2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="fta6">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="9" />
			<xsl:with-param name="line" select="2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="hpa5 | hpcm5 | hpfm5 | hpn5">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="10" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="lstfm2 | lstn2 | lstre2">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="10" />
			<xsl:with-param name="line" select="-4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpfm6 | dpre6 | dptk6 | fnfm6 | fnn6 | fnre6 | fta7">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="10" />
			<xsl:with-param name="line" select="2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="hpcm6 | hpfm6 | hpn6">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="12" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="lstcm3">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="12" />
			<xsl:with-param name="line" select="-4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="hpt7">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="14" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="lstfm3 | lstn3 | lstre3">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="14" />
			<xsl:with-param name="line" select="-4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="hpt8">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="16" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="lstcm4">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="16" />
			<xsl:with-param name="line" select="-4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac10">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="18" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="hpt9">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="18" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="lsta5">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="20" />
			<xsl:with-param name="line" select="-4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="hpa7">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="20" />
			<xsl:with-param name="line" select="-20" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="lsta6">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="24" />
			<xsl:with-param name="line" select="-4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="tr">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="25" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="hpa8">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="28" />
			<xsl:with-param name="line" select="-11" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dptkr">
		<xsl:call-template name="d6">
			<xsl:with-param name="lm" select="40" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="tblnt">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pb3el">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="3" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpdclc2 | dpfmspc2 | dpmemc2 | dprclc2">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpdclc3 | dpfmspc3 | dpmemc3 | dprclc3">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="8" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpfmspc4 | hla7">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="12" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpfmspc5">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="16" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="lstfm4 | lstfmsp4">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="18" />
			<xsl:with-param name="line" select="-4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac11">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="20" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac12">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="21" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac13">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="22" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac14">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="23" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac15">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="24" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac16">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="25" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac17">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="26" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac18">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="28" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac19">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="30" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac20">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="32" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac21">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="34" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac22">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="36" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac23">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="38" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac24">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="40" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dpac25">
		<xsl:call-template name="d7">
			<xsl:with-param name="lm" select="42" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comm1 | pb1">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comml1 | ph1">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="1" />
			<xsl:with-param name="line" select="1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pb2">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="ph12">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="2" />
			<xsl:with-param name="line" select="-1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="ph2s">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="2" />
			<xsl:with-param name="line" select="1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comm2 | pb3">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="3" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comml2 | ph2 | ph23">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="3" />
			<xsl:with-param name="line" select="-1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pb4">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="4" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comml3 | ph3 | ph34">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="4" />
			<xsl:with-param name="line" select="-1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dan1">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="4" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="danc1">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="4" />
			<xsl:with-param name="line" select="2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comm3 | pb5">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="5" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comml4 | ph4 | ph45">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="5" />
			<xsl:with-param name="line" select="-1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="danc2 | pb6">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="6" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comml5 | ph5">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="6" />
			<xsl:with-param name="line" select="-1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dan2">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="6" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comm4 | pb7">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="7" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comml6 | ph6 | ph67">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="7" />
			<xsl:with-param name="line" select="-1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="danc3 | pb8">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="8" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comml7 | ph7 | ph78">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="8" />
			<xsl:with-param name="line" select="-1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dan3">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="8" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comm5">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="9" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="ph89 | ph9">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="9" />
			<xsl:with-param name="line" select="-1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="danc4">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="10" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dan4">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="10" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comm6">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="11" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="ph11">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="11" />
			<xsl:with-param name="line" select="-1" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="danc5">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="12" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dan5">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="12" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comm7">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="13" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="danc6">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="14" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dan6">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="14" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="comm8">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="15" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="danc7">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="16" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dan7">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="16" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="dan8">
		<xsl:call-template name="d8">
			<xsl:with-param name="lm" select="18" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="snl">
		<xsl:call-template name="d9">
			<xsl:with-param name="lm" select="2" />
			<xsl:with-param name="line" select="-2" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="ntcm1 | ntfm1 | nthla | nthlcm | nthlfm | nthln | nthlre | nthlt | ntn1 | ntre1 | ntt1">
		<xsl:call-template name="d9">
			<xsl:with-param name="lm" select="5" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pha">
		<xsl:call-template name="d9">
			<xsl:with-param name="lm" select="6" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="ntcm2 | ntfm2 | ntn2 | ntre2 | ntt2">
		<xsl:call-template name="d9">
			<xsl:with-param name="lm" select="10" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="ntac3">
		<xsl:call-template name="d9">
			<xsl:with-param name="lm" select="14" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="nta3">
		<xsl:call-template name="d9">
			<xsl:with-param name="lm" select="15" />
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>