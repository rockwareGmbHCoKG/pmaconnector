#!/bin/bash

source environment.sh

cp configuration/target/configs/${CONFIGURATION_TYPE}/* volumes/shared/
cp configuration/target/mappings/${MAPPINGS_TYPE}/* volumes/shared/