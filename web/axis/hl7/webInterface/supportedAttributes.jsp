<%@ page import="org.hl7.CTSMAPI.*"%>
<%@ page import="org.hl7.CTSMAPI.refImpl.*"%>
<%@ page import="org.hl7.utility.JSPUtility"%>
<%@ page errorPage="../sorry.html" %>
<HTML>
<HEAD>
<TITLE> HL7Interface </TITLE>
<link rel="stylesheet" href="../main.css" type="text/css">
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
        window.setTimeout('start()', 60);
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
<H1 ALIGN="CENTER">Supported Attributes</H1>
<H3 ALIGN="CENTER">HL7 version <%=browserOperationsImpl.getHL7ReleaseVersion().getV()%></H3>
<TABLE WIDTH="100%" BORDER="2" CELLSPACING="2" CELLPADDING="2" ALIGN="CENTER" onMouseOut='javascript:message=""'>
  <TR ALIGN="CENTER">
    <TH><B>Attribute</B></TH>
    <TH><B>Domain</B></TH>
    <TH><B>Context</B></TH>
    <TH><B>ValueSet</B></TH>
  </TR>
<%
        RIMCodedAttribute[] rimAttributes = browserOperationsImpl.getSupportedAttributes(null, null, 0, 0);
        for (int i=0; i < rimAttributes.length; i++)
        {
        	try
        	{
            VocabularyDomainDescription vocabularyDomain = browserOperationsImpl.lookupVocabularyDomain(rimAttributes[i].getVocabularyDomain_name());
%>
            <TR onMouseOver='javascript:setMessage("<%=JSPUtility.convertQuotes(JSPUtility.escapeChars(vocabularyDomain.getDescription()))%>")'>
            <TD><%=rimAttributes[i].getRIMAttribute_id().getClass_name().getV()%>.<%=rimAttributes[i].getRIMAttribute_id().getAttribute_name().getV()%>(<%=rimAttributes[i].getCodingStrength_code().getV()%>)</TD>

            <TD><ACRONYM TITLE='<%=JSPUtility.escapeChars(vocabularyDomain.getDescription())%>'><%=JSPUtility.escapeChars(vocabularyDomain.getVocabularyDomain_name())%></ACRONYM></TD>
<%
            VocabularyDomainValueSet[] valueSets = vocabularyDomain.getRepresentedByValueSets();
            if (valueSets.length == 0)
            {
%>
                <TD>&nbsp;</TD>
                <TD>&nbsp;</TD>
                </TR>
<%
            }
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
				        <%=valueSets[j].getApplicationContext_code().getV() %>
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
                    <TD><%=valueSets[j].getDefinedByValueSet().getValueSet_name().getV()%></TD>
<%

                    if (j != valueSets.length - 1)
                    {
%>
                    <TR>
                    <TD>&nbsp;</TD>
                    <TD>&nbsp;</TD>
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
<p><BR>
  <br>
</p>
<p><br>
</p>
<p align="left"><font size="-1">last revised 4/6/04
  </font><br>
  <font size="-1">contact the <a href="mailto:informatics-support@mayo.edu">webmaster</a><br>
  </font>
</body>
</html>
