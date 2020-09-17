# What is this?
These templates also exist in an S3 bucket, which is referenced in `cumulus-deployspec.yaml`.  The templates in this repo are not used for anything other than source control.

# fargate_service.yaml
This template is used to define the ECS service that is stood up as part of the green deployment.  It also contains the task definition which requires the container image which is generated in the bake spec.

# target_group.yaml
This template is used to create the target group that is created for the green environment.
