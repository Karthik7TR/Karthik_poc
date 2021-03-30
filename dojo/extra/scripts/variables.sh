#!/bin/bash -e

# Common variables that are used by the other scripts in this folder.

export AWS_PROFILE_CICD="tr-tax-prof-cicd-sandbox"
export AWS_PROFILE_PREPROD="tr-tax-prof-sandbox"

export SOURCE_ZIPFILE_NAME="pipeline-source.zip"
export S3_SOURCE_BUCKET_DIR="TEN-Acct-Id" # FIXME Change this to your u ID (e.x. u0123456)
export S3_BUCKET_NAME="a206296-tr-tax-prof-cicd-sandbox-eu-west-1-dojo"
export REGION="eu-west-1"

export TEN_ACCOUNT_ID="TEN-Acct-Id" # FIXME Change this to your u ID (e.x. u0123456)
export ASSET_ID="206296"
