# Prerequisites
* Python3 virtual environment
* [BAMS setup as pip repository](https://thehub.thomsonreuters.com/docs/DOC-2735743#jive_content_id_Windows)
* `pip install project-cumulus`
* `pip install pipeline_generator`
* `pip install cloud-iac`

# Run IaC
Let Rajesh handle this

# Projct Cumulus
**Are SNS topics for cumulus stuff being created?  (alert for testing, etc.)**


## Run installer
### Files
[installer_input.yaml](installer_input.yaml) - Modify those files with `# FIXME`

### Steps
`cumulus-installer --profile-name ${AWS_PROFILE} --installer-file installer_input.yaml`

## Create pipeline
### Files
* [`pipelinespec.yaml`](pipelinespec.yaml)
* [`DeploySpec.yaml`](DeploySpec.yaml)
* [`bakespec.yml`](bakespec.yml)
* ~[`buildspec.yml`](buildspec.yml)~

### Steps

# Cleanup
