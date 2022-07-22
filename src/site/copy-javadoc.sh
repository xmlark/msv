#!/bin/bash

set -euo pipefail


echo "Copy generated JavaDoc API from projects into /docs - must been built earlier with 'mvn install'..."
rm -rf ../../docs/core/api
mv ../../msv/target/apidocs ../../docs/core/api

rm -rf ../../docs/xsdlib/api
mv ../../xsdlib/target/apidocs ../../docs/xsdlib/api
