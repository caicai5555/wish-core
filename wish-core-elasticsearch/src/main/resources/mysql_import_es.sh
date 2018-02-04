#!/bin/sh
bin=$JDBC_IMPORTER_HOME/bin
lib=$JDBC_IMPORTER_HOME/lib
echo '{
"type": "jdbc",
"jdbc": {
"elasticsearch.autodiscover":true,
"elasticsearch.cluster":"my-application",
"url":"jdbc:mysql://192.168.1.55:3306/bsp",
"user":"root", 
"password":"xxx",
"sql":"select *,id as _id from bio_sip_storage limit 0,100 ",
"elasticsearch" : {
	"host" : "192.168.1.55",
	"port" : 9300
	},
"index" : "bsp",
"type" : "bio_sip_storage"
}
}'| java \
  -cp "${lib}/*" \
  -Dlog4j.configurationFile=${bin}/log4j2.xml \
  org.xbib.tools.Runner \
  org.xbib.tools.JDBCImporter
