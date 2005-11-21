<%@ page import="org.hl7.CTSMAPI.*"%>
<%@ page import="org.hl7.CTSMAPI.refImpl.*"%>
<%@ page import="org.hl7.utility.JSPUtility"%>
<%@ page import="org.hl7.types.*"%>
<%@ page errorPage="../sorry.html" %>
<HTML>
<HEAD>
<link rel="stylesheet" href="../main.css" type="text/css">
<TITLE> HL7Interface </TITLE>
<SCRIPT LANGUAGE="JavaScript">
<!--
var smessage="";
var message= "";
message=''


var y = 0;
var maxLength = 75;
function start()
{

    if (y > message.length)
    {
        y = 0;
        smessage = "";
        window.setTimeout('start()', 0);
    }

    else
    {
        if (y + maxLength > message.length)
        {
            smessage = message.substring(y, message.length)+ message.substring(0, maxLength - (message.length - y));
        }
        else
        {
            smessage=message.substring(y,y + maxLength);
        }
        window.status = smessage;
        y++;
        window.setTimeout('start()', 100);
    }
}

function setMessage(newMessage)
{
    smessage = "";
    y = 0;
    message = "      Description:  " + newMessage;

}
-->
</SCRIPT>

</HEAD>
<jsp:useBean id="JSPUtilityBean" scope="application" class="org.hl7.utility.JSPUtility"/>
<BODY onLoad="start()">
<%
	BrowserOperations browserOperationsImpl = JSPUtilityBean.getBrowserOperations();
%>
<H1 ALIGN="CENTER">Supported Vocabulary Domains</H1>
<H3 ALIGN="CENTER">HL7 version <%=browserOperationsImpl.getHL7ReleaseVersion().getV()%></H3>
<TABLE WIDTH="100%" BORDER="2" CELLSPACING="2" CELLPADDING="2" ALIGN="CENTER" onMouseOut='javascript:message=""'>
  <TR ALIGN="CENTER">
    <TH><B>Domain</B></TH>
    <TH><B>Attribute</B></TH>
	<TH><B>Realm</B></TH>
	<TH><B>ValueSet</B></TH>
  </TR>
<%
	    ST[] supportedVocabularyDomains = browserOperationsImpl.getSupportedVocabularyDomains(null, null, 0, 0);
        for (int i=0; i < supportedVocabularyDomains.length; i++)
        {
        	try
        	{
			VocabularyDomainDescription vocabularyDomain = browserOperationsImpl.lookupVocabularyDomain(supportedVocabularyDomains[i]);
			VocabularyDomainValueSet[] valueSets = vocabularyDomain.getRepresentedByValueSets();
			int rowSpan = valueSets.length;
			if (rowSpan < 1)
			   rowSpan = 1;
%>
		    <!--Domain-->
            <TR onMouseOver='javascript:setMessage("<%=JSPUtility.convertQuotes(JSPUtility.escapeChars(vocabularyDomain.getDescription()))%>")'>
            <TD rowspan="<%=rowSpan%>"><ACRONYM TITLE='<%=JSPUtility.escapeChars(vocabularyDomain.getDescription())%>'><%=JSPUtility.escapeChars(vocabularyDomain.getVocabularyDomain_name())%></ACRONYM></TD>
            <!--<TD></TD>-->
<%
            RIMCodedAttribute[] rimAttributes = vocabularyDomain.getConstrainsAttributes();
            if (rimAttributes.length == 0)
            {
%>
                <TD rowspan="<%=rowSpan%>">&nbsp;</TD>
<%
            }
			else
			{
%>
				<!-- Attributes-->
                <TD rowspan="<%=rowSpan%>">
<%              for (int k = 0; k < rimAttributes.length; k++)
	            {
%>
				    <%=rimAttributes[k].getRIMAttribute_id().getClass_name().getV()%>.<%=rimAttributes[k].getRIMAttribute_id().getAttribute_name().getV()%>(<%=rimAttributes[k].getCodingStrength_code().getV()%>)

<%
				    if (k + 1 < rimAttributes.length)
					{
%>
						<br>
<%
					}
				}
%>
				</TD>
<%
			}

			if (valueSets.length == 0)
			{
%>
				<TD>&nbsp;</TD>
				<TD>&nbsp;</TD>
				</TR>
<%			}
			else
			{
			    for (int j=0; j < valueSets.length; j++)
			    {
%>
					<!--Realm-->
					<TD>
<%
					if ((valueSets[j].getApplicationContext_code() != null) && (valueSets[j].getApplicationContext_code().getV().length() != 0))
					{
%>
						<%=valueSets[j].getApplicationContext_code().getV()%>
<%
					}
					else
					{
%>
						universal
<%
					}
%>
					</TD>
					<!--Value Set-->
					<TD><%=valueSets[j].getDefinedByValueSet().getValueSet_name().getV()%></TD></TR>

<%
					if (j != valueSets.length - 1)
					{
%>
						<TR>
						<!--<TD>&nbsp;</TD>
						<TD>&nbsp;</TD>-->
<%
					}
				}
            }

        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        }
%>


</TABLE>
<BR>
<br>
<br>

<p align="left"><font size="-1">last revised 4/6/04
  </font><br>
  <font size="-1">contact the <a href="mailto:informatics-support@mayo.edu">webmaster</a><br>
  </font>
</body>
</html>
