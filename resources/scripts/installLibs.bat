REM Install libraries not in maven2 central repository

call mvn install:install-file -Dfile=../../lib/abdera-core-0.4.0-incubating.jar -DgroupId=duraspace -DartifactId=abdera-core-0.4.0-incubating -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/abdera-extensions-main-0.4.0-incubating.jar -DgroupId=duraspace -DartifactId=abdera-extensions-main-0.4.0-incubating -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/abdera-i18n-0.4.0-incubating.jar -DgroupId=duraspace -DartifactId=abdera-i18n-0.4.0-incubating -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/abdera-parser-0.4.0-incubating.jar -DgroupId=duraspace -DartifactId=abdera-parser-0.4.0-incubating -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/activation-1.1.1.jar -DgroupId=duraspace -DartifactId=activation-1.1.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/activemq-all-5.1.0.jar -DgroupId=duraspace -DartifactId=activemq-all-5.1.0 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/akubra-core-0.1.jar -DgroupId=duraspace -DartifactId=akubra-core-0.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/akubra-fs-0.1.jar -DgroupId=duraspace -DartifactId=akubra-fs-0.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/akubra-map-0.1.jar -DgroupId=duraspace -DartifactId=akubra-map-0.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/akubra-mem-0.1.jar -DgroupId=duraspace -DartifactId=akubra-mem-0.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/asm-3.1.jar -DgroupId=duraspace -DartifactId=asm-3.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/avalon-framework-4.2.0.jar -DgroupId=duraspace -DartifactId=avalon-framework-4.2.0 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/axiom-api-1.2.5.jar -DgroupId=duraspace -DartifactId=axiom-api-1.2.5 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/axiom-impl-1.2.5.jar -DgroupId=duraspace -DartifactId=axiom-impl-1.2.5 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/axis-ant.jar -DgroupId=duraspace -DartifactId=axis-ant -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/axis.jar -DgroupId=duraspace -DartifactId=axis -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/batik-all.jar -DgroupId=duraspace -DartifactId=batik-all -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/carol-2.0.5.jar -DgroupId=duraspace -DartifactId=carol-2.0.5 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/castor-1.2-codegen.jar -DgroupId=duraspace -DartifactId=castor-1.2-codegen -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/castor-1.2-xml.jar -DgroupId=duraspace -DartifactId=castor-1.2-xml -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/castor-1.2-xml-schema.jar -DgroupId=duraspace -DartifactId=castor-1.2-xml-schema -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-beanutils.jar -DgroupId=duraspace -DartifactId=commons-beanutils -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-betwixt-0.7.jar -DgroupId=duraspace -DartifactId=commons-betwixt-0.7 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-codec-1.3.jar -DgroupId=duraspace -DartifactId=commons-codec-1.3 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-collections-3.1.jar -DgroupId=duraspace -DartifactId=commons-collections-3.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-dbcp-1.2.1.jar -DgroupId=duraspace -DartifactId=commons-dbcp-1.2.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-digester-1.7.jar -DgroupId=duraspace -DartifactId=commons-digester-1.7 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-discovery.jar -DgroupId=duraspace -DartifactId=commons-discovery -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-fileupload-1.2.1.jar -DgroupId=duraspace -DartifactId=commons-fileupload-1.2.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-httpclient-3.1.jar -DgroupId=duraspace -DartifactId=commons-httpclient-3.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-io-1.4.jar -DgroupId=duraspace -DartifactId=commons-io-1.4 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-logging.jar -DgroupId=duraspace -DartifactId=commons-logging -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/commons-pool-1.2.jar -DgroupId=duraspace -DartifactId=commons-pool-1.2 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/connector-1_5.jar -DgroupId=duraspace -DartifactId=connector-1_5 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/derbyclient.jar -DgroupId=duraspace -DartifactId=derbyclient -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/derby.jar -DgroupId=duraspace -DartifactId=derby -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/dom4j-1.6.1.jar -DgroupId=duraspace -DartifactId=dom4j-1.6.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/emory-util-2.1-remote.jar -DgroupId=duraspace -DartifactId=emory-util-2.1-remote -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/fop.jar -DgroupId=duraspace -DartifactId=fop -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/google-collections-1.0-rc1.jar -DgroupId=duraspace -DartifactId=google-collections-1.0-rc1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/GroboTestingJUnit-1.2.1-core.jar -DgroupId=duraspace -DartifactId=GroboTestingJUnit-1.2.1-core -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/howl-logger-0.1.11.jar -DgroupId=duraspace -DartifactId=howl-logger-0.1.11 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/ij.jar -DgroupId=duraspace -DartifactId=ij -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jai_codec.jar -DgroupId=duraspace -DartifactId=jai_codec -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jai_core.jar -DgroupId=duraspace -DartifactId=jai_core -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jakarta-oro-2.0.5.jar -DgroupId=duraspace -DartifactId=jakarta-oro-2.0.5 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/java-getopt-1.0.11.jar -DgroupId=duraspace -DartifactId=java-getopt-1.0.11 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jaxb-api.jar -DgroupId=duraspace -DartifactId=jaxb-api -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jaxb-impl.jar -DgroupId=duraspace -DartifactId=jaxb-impl -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jaxen-1.1.1.jar -DgroupId=duraspace -DartifactId=jaxen-1.1.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jaxrpc-api-1.1.jar -DgroupId=duraspace -DartifactId=jaxrpc-api-1.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jdom-1.0.jar -DgroupId=duraspace -DartifactId=jdom-1.0 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jersey-bundle-1.0.1.jar -DgroupId=duraspace -DartifactId=jersey-bundle-1.0.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jettison-1.0.1.jar -DgroupId=duraspace -DartifactId=jettison-1.0.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jhbasic.jar -DgroupId=duraspace -DartifactId=jhbasic -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jmx.jar -DgroupId=duraspace -DartifactId=jmx -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/joda-time-1.5.2.jar -DgroupId=duraspace -DartifactId=joda-time-1.5.2 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jotm-2.0.10.jar -DgroupId=duraspace -DartifactId=jotm-2.0.10 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jotm_jrmp_stubs-2.0.10.jar -DgroupId=duraspace -DartifactId=jotm_jrmp_stubs-2.0.10 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jsp-api.jar -DgroupId=duraspace -DartifactId=jsp-api -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jsr173_1.0_api.jar -DgroupId=duraspace -DartifactId=jsr173_1.0_api -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jsr311-api-1.0.jar -DgroupId=duraspace -DartifactId=jsr311-api-1.0 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/jta-spec1_0_1.jar -DgroupId=duraspace -DartifactId=jta-spec1_0_1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/junit-4.5.jar -DgroupId=duraspace -DartifactId=junit-4.5 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/log4j-1.2.15.jar -DgroupId=duraspace -DartifactId=log4j-1.2.15 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/lucene-core-2.4.0.jar -DgroupId=duraspace -DartifactId=lucene-core-2.4.0 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/mail-1.4.1.jar -DgroupId=duraspace -DartifactId=mail-1.4.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/md5.jar -DgroupId=duraspace -DartifactId=md5 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/mkjdbc.jar -DgroupId=duraspace -DartifactId=mkjdbc -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/mptstore-0.9.1.jar -DgroupId=duraspace -DartifactId=mptstore-0.9.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/mulgara-core-2.1.1.jar -DgroupId=duraspace -DartifactId=mulgara-core-2.1.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/mysql-connector-java-5.1.6-bin.jar -DgroupId=duraspace -DartifactId=mysql-connector-java-5.1.6-bin -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/one-jar-boot-0.96.jar -DgroupId=duraspace -DartifactId=one-jar-boot-0.96 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/openrdf-sesame-2.2.1-onejar.jar -DgroupId=duraspace -DartifactId=openrdf-sesame-2.2.1-onejar -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/persistence-api-1.0.2.jar -DgroupId=duraspace -DartifactId=persistence-api-1.0.2 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/postgresql-8.3-603.jdbc3.jar -DgroupId=duraspace -DartifactId=postgresql-8.3-603.jdbc3 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/rome-0.9.jar -DgroupId=duraspace -DartifactId=rome-0.9 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/saaj-api-1.3.jar -DgroupId=duraspace -DartifactId=saaj-api-1.3 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/saxon9-dom.jar -DgroupId=duraspace -DartifactId=saxon9-dom -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/saxon9.jar -DgroupId=duraspace -DartifactId=saxon9 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/servlet-api.jar -DgroupId=duraspace -DartifactId=servlet-api -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/sjsxp.jar -DgroupId=duraspace -DartifactId=sjsxp -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/slf4j-api-1.5.2.jar -DgroupId=duraspace -DartifactId=slf4j-api-1.5.2 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/slf4j-jdk14-1.5.2.jar -DgroupId=duraspace -DartifactId=slf4j-jdk14-1.5.2 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/spring-beans-2.5.6.jar -DgroupId=duraspace -DartifactId=spring-beans-2.5.6 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/spring-core-2.5.6.jar -DgroupId=duraspace -DartifactId=spring-core-2.5.6 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/stax-utils.jar -DgroupId=duraspace -DartifactId=stax-utils -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/sunxacml-patched.jar -DgroupId=duraspace -DartifactId=sunxacml-patched -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/Tidy.jar -DgroupId=duraspace -DartifactId=Tidy -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/trippi-1.4.1-core.jar -DgroupId=duraspace -DartifactId=trippi-1.4.1-core -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/trippi-1.4.1-mptstore.jar -DgroupId=duraspace -DartifactId=trippi-1.4.1-mptstore -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/trippi-1.4.1-mulgara.jar -DgroupId=duraspace -DartifactId=trippi-1.4.1-mulgara -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/trove-2.0.4.jar -DgroupId=duraspace -DartifactId=trove-2.0.4 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/tt-bytecode.jar -DgroupId=duraspace -DartifactId=tt-bytecode -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/wsdl4j-1.5.1.jar -DgroupId=duraspace -DartifactId=wsdl4j-1.5.1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/xercesImpl.jar -DgroupId=duraspace -DartifactId=xercesImpl -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/xml-apis.jar -DgroupId=duraspace -DartifactId=xml-apis -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/xmlpull_1_1_3_4a.jar -DgroupId=duraspace -DartifactId=xmlpull_1_1_3_4a -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/xmlunit-1.2.jar -DgroupId=duraspace -DartifactId=xmlunit-1.2 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../../lib/xpp3_min-1.1.3.4.K.jar -DgroupId=duraspace -DartifactId=xpp3_min-1.1.3.4.K -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
