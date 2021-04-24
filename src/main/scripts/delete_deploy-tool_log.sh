#!/bin/bash
find $(cd $(dirname $0); pwd) -name "*.succ" -type f -print -exec rm -rf {} \;
find $(cd $(dirname $0); pwd) -name "*.fail" -type f -print -exec rm -rf {} \;
find $(cd $(dirname $0); pwd) -name "*.get.log" -type f -print -exec rm -rf {} \;
find $(cd $(dirname $0); pwd) -name "*.deploy.log" -type f -print -exec rm -rf {} \;