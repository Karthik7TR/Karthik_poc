#!/bin/bash -e

# Common variables that are used by the other scripts in this folder.

export AWS_PROFILE_CICD="tr-ihn-cicd-sandbox"
export AWS_PROFILE_PREPROD="tr-ihn-sandbox"

export SOURCE_ZIPFILE_NAME="pipeline-source.zip"
export S3_SOURCE_BUCKET_DIR="u6121086" # FIXME Change this to your u ID (e.x. u0123456)
export S3_BUCKET_NAME="a206296-tr-ihn-cicd-sandbox-eu-west-1-dojo"
export REGION="eu-west-1"

export TEN_ACCOUNT_ID="u6121086" # FIXME Change this to your u ID (e.x. u0123456)
export ASSET_ID="206296"
