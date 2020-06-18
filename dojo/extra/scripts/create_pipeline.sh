#!/bin/bash -xe

# Creates and deploys a Cumulus pipeline based on the cumulus-pipelinespec.yaml file.

source ./dojo/extra/scripts/variables.sh

export AWS_PROFILE="${AWS_PROFILE_CICD}"
cumulus pipelines generate --input-file cumulus-pipelinespec.yaml --output-file pipeline-cfn.yaml

aws cloudformation deploy --template-file ${TEMPLATE_FILENAME} \
--stack-name ${STACK_NAME} \
--tags \
    "tr:application-asset-insight-id=${ASSET_ID}" \
    "tr:environment-type=${ENVIRONMENT_TYPE}" \
    "tr:resource-owner=${RESOURCE_OWNER}" \
--capabilities CAPABILITY_IAM
