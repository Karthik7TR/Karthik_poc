#!/bin/bash -xe

# Zips up the repo and uploads it to the pipeline source location so that the pipeline starts.

source dojo/extra/scripts/variables.sh

zip -q -r ${SOURCE_ZIPFILE_NAME} . -x '.git/*'
aws --profile ${AWS_PROFILE_CICD} --region ${REGION} s3 cp ${SOURCE_ZIPFILE_NAME} s3://${S3_BUCKET_NAME}/${S3_SOURCE_BUCKET_DIR}/
rm ${SOURCE_ZIPFILE_NAME}
