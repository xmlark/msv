#!/bin/bash

set -euo pipefail


echo "Copy generated JavaDoc API from projects into /docs - must been built earlier with 'mvn install'..."
rm -rf ../../docs/api/msv
mv ../../msv/target/apidocs ../../docs/api/msv

rm -rf ../../docs/api/xsdlib
mv ../../xsdlib/target/apidocs ../../docs/api/xsdlib
