#!/usr/bin/env bash
# Deploys a CloudFormation stack with the template artifactory.yaml to create
# the artifactory read-only token.

if [[ -z "$1" ]]; then
  echo "Expected the secret string to be provided as the first parameter to this script."
  exit 1
fi

PROFILE=tr-ihn-cicd-sandbox
ASSET_ID=206296
RESOURCE_OWNER="trtacloudscrumteam@thomsonreuters.com"
ENVIRONMENT_TYPE=LAB
STACK_NAME_SUFFIX=artifactory-read-only
STACK_NAME=a${ASSET_ID}-${STACK_NAME_SUFFIX}
TEMPLATE_FILENAME="artifactory.yaml"

aws --profile ${PROFILE} cloudformation deploy \
  --template-file ${TEMPLATE_FILENAME} \
  --stack-name ${STACK_NAME} \
  --tags \
    "tr:application-asset-insight-id=${ASSET_ID}" \
    "tr:environment-type=${ENVIRONMENT_TYPE}" \
    "tr:resource-owner=${RESOURCE_OWNER}" \
    "Name=${STACK_NAME_SUFFIX}" \
  --capabilities CAPABILITY_IAM \
  --parameter-overrides \
    "ArtifactoryToken=$1"
