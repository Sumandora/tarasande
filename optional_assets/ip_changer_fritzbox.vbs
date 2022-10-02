On Error Resume Next

Set http = Nothing
Set http = CreateObject("WinHttp.WinHttpRequest.5.1")
If http Is Nothing Then Set http = CreateObject("WinHttp.WinHttpRequest.5")
If http Is Nothing Then Set http = CreateObject("WinHttp.WinHttpRequest")
If http Is Nothing Then Set http = CreateObject("MSXML2.ServerXMLHTTP")
If http Is Nothing Then Set http = CreateObject("Microsoft.XMLHTTP")
If http Is Nothing Then
 MsgBox "Kein HTTP-Objekt verfügbar!",16,"Fehler:"
Else
'On Error Goto 0
 body =	"<?xml version=""1.0"" encoding=""utf-8""?>" _
  & "<s:Envelope xmlns:s=""http://schemas.xmlsoap.org/soap/envelope/"" s:encodingStyle=""http://schemas.xmlsoap.org/soap/encoding/"">" _
  & "<s:Body><u:ForceTermination xmlns:u=""urn:schemas-upnp-org:service:WANIPConnection:1"" /></s:Body>" _
  & "</s:Envelope>"
 For Each url In Array("igd","")
  With http
   .Open "POST", "http://fritz.box:49000/" & url & "upnp/control/WANIPConn1",false
   .setRequestHeader "Content-Type", "text/xml; charset=""utf-8"""
   .setRequestHeader "Connection", "close"
   .setRequestHeader "Content-Length", Len(body)
   .setRequestHeader "HOST", "fritz.box:49000"
   .setRequestHeader "SOAPACTION", """urn:schemas-upnp-org:service:WANIPConnection:1#ForceTermination"""
   .Send body
  End With
 Next
End If