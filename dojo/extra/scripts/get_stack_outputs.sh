#!/bin/bash -e

# Gets the outputs from the Cloud-IaC stacks that you need to fill in the FIXME values in the deploy spec.

source ./dojo/extra/scripts/variables.sh

DEV_ECS_STACK="a${ASSET_ID}-${TEN_ACCOUNT_ID}-ems-dojo-dev-ecs-cluster-ecsconfig"
DEV_ALB_STACK="a${ASSET_ID}-${TEN_ACCOUNT_ID}-ems-dojo-dev-alb-ecs-allconfig"

QA_ECS_STACK="a${ASSET_ID}-${TEN_ACCOUNT_ID}-ems-dojo-qa-ecs-cluster-ecsconfig"
QA_ALB_STACK="a${ASSET_ID}-${TEN_ACCOUNT_ID}-ems-dojo-qa-alb-ecs-allconfig"

function get_outputs() {
    aws --profile ${AWS_PROFILE_PREPROD} --region ${REGION} cloudformation describe-stacks \
        --stack-name $1 \
        --query 'Stacks[0].Outputs[?OutputKey==`FargateContainerSecurityGroup`||OutputKey==`ECSTaskExecutionRoleArn`][OutputKey,OutputValue]' \
        --output text
    aws --profile ${AWS_PROFILE_PREPROD} --region ${REGION} cloudformation describe-stacks \
        --stack-name $2 \
        --query 'Stacks[0].Outputs[?OutputKey==`HTTPAlbListener`||OutputKey==`DNSName`][OutputKey,OutputValue]' \
        --output text
}

echo ""
echo "============ DEV STACK OUTPUTS ============"
get_outputs $DEV_ECS_STACK $DEV_ALB_STACK
echo ""
echo "============ QA STACK OUTPUTS ============"
get_outputs $QA_ECS_STACK $QA_ALB_STACK
echo ""
