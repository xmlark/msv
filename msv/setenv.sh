
root=`pwd`
root=`dirname $root`

export CLASSPATH=`tr '\n' ':' << EOF
$root/msv/bin
$root/testharness/bin
$root/xsdlib/bin
$root/xsdlib/src
$root/shared/lib/isorelax.jar
$root/shared/lib/relaxngDatatype.jar
$root/shared/lib/xercesImpl.jar
$root/shared/lib/junit.jar
$root/shared/lib/resolver.jar
EOF`
