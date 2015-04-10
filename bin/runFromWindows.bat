
rem if failing for SSL Handshake...
rem 1) open link in browser, click on the padlock symbol, view certificate then export/download/save as c:\share1.dexcom.com.pem

rem if you are using a different version of java use that path instead
rem 2) cd C:\Program Files\Java\jre7\bin

rem run the following command, when prompted for password enter: changeit
rem 3) .\keytool -import -noprompt -trustcacerts -alias  Dexcom -file 'C:\share1.dexcom.com.pem' -keystore "..\lib\security\cacerts"

                                                                                       
"C:\Program Files\Java\jre7\bin\java.exe" -cp ".\nsBridge.jar;..\lib\gson-2.3.1.jar;..\lib\mongo-java-driver-2.12.3.jar;"  com.mltkm.nightscout.Xshare2mongo C:/doNotSync/bridge.properties 