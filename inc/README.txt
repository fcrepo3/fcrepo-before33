Instructions for creating the keystore and truststore.

Generate the certificate keystore for Tomcat. 
	keytool -genkey -alias tomcat -keyalg RSA -validity 365 -keystore keystore
When prompted for the keystore password, enter "changeit" (without the quotes).

	

Generate the client truststore
	keytool -export -keystore keystore -alias tomcat -file tomcat.cer
	keytool -import -alias tomcat -keystore truststore -trustcacerts -file tomcat.cer
When prompted for the truststore password, use "tomcat"

References:
http://tomcat.apache.org/tomcat-5.5-doc/ssl-howto.html
