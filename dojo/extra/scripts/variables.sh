#!/bin/bash -e

# Common variables that are used by the other scripts in this folder.

export AWS_PROFILE_CICD="tr-tax-prof-cicd-sandbox"
export AWS_PROFILE_PREPROD="tr-tax-prof-sandbox"

export SOURCE_ZIPFILE_NAME="pipeline-source.zip"
export S3_SOURCE_BUCKET_DIR="TEN-Acct-Id" # FIXME Change this to your u ID (e.x. u0106226)
export S3_BUCKET_NAME="a206296-tr-tax-prof-cicd-sandbox-eu-west-1-dojo"
export REGION="eu-west-1"

export RESOURCE_OWNER="firstname.lastname@tr.com" # FIXME Change to your email.
export TEN_ACCOUNT_ID="TEN-Acct-Id" # FIXME Change this to your u ID (e.x. u0106226)
export ASSET_ID="206296"
export STACK_NAME="a${ASSET_ID}-${TEN_ACCOUNT_ID}-cumulus-pipeline-pipeline"
export ENVIRONMENT_TYPE="DEVELOPMENT"
export TEMPLATE_FILENAME="pipeline-cfn.yaml"
