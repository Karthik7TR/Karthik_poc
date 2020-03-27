#!/usr/bin/env python3
# This script publishes images to Docker to the accounts and regions
# mentioned in the deploy spec.
# It then sets the relevant published images to the stack parameters in the deployspec
# under the specified path.
#
# Usage:
# python3 bake_helper.py --image <IMAGE_NAME> --tag <TAG> --param-path <PARAM_PATH>
#
# Example:
# python3 bake_helper.py --image my-docker-image --tag 1444 --param-path DeployerParameters.StackParameters.ImageUrl
#
# Requirements:
# boto3 docker pyyaml

import argparse
import base64
import pathlib

import boto3
import docker
import yaml

parser = argparse.ArgumentParser("Publish Docker images and update the deploy spec.")
parser.add_argument("--image", nargs=1, required=True)
parser.add_argument("--tag", nargs=1, required=True)
parser.add_argument("--param-path", nargs=1, required=True)
args = parser.parse_args()

docker_client = docker.from_env()
image_name = args.image[0]
tag = args.tag[0]
update_path = args.param_path[0].split(".")


# Function for setting values deep in a dict even if some parts are missing
def deepset(dict_, item, *path):
    for key in path[:-1]: dict_ = dict_.setdefault(key, {})
    dict_[path[-1]] = item

# Load deployspec
deployspec_file = pathlib.Path("cumulus-deployspec.yaml")
deployspec = yaml.safe_load(deployspec_file.read_text())

# Push image to all environments (regions & accounts)
defaults = deployspec["Defaults"]
for env, env_values in deployspec.items():
    if env == "Defaults": continue

    env_acc = env_values.get("AccountId", defaults.get("AccountId"))
    env_region = env_values.get("AccountRegion", defaults.get("AccountRegion"))

    # Log in to relevant ECR for account+region
    ecr = boto3.client("ecr", region_name=env_region)
    resp = ecr.get_authorization_token(registryIds=[env_acc])["authorizationData"][0]
    token = base64.decodebytes(resp["authorizationToken"].encode()).decode()
    [username, password] = token.split(":")
    registry = resp["proxyEndpoint"]
    docker_client.login(username, password, registry=registry)

    # Tag and push image
    registry = registry.replace("https://", "", 1)
    image_uri = f"{registry}/{image_name}"
    docker_client.images.get(image_name).tag(image_uri, tag=tag)
    image_uri = f"{image_uri}:{tag}"
    docker_client.images.push(image_uri)

    deepset(env_values, image_uri, *update_path)

deployspec_file.write_text(yaml.dump(deployspec))
