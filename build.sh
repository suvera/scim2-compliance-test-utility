#!/usr/bin/env bash

set -e
set -x

echo "Extracting the version from the pom.xml file..."
if [ ! -f "pom.xml" ]; then
    echo "Error: pom.xml not found in the current directory."
    exit 1
fi

# Extract the version using xmllint (or similar XML parsing tool)
BUILD_VERSION=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' pom.xml 2>/dev/null)

# Handle potential errors during parsing
if [ -z "$BUILD_VERSION" ]; then
    echo "Error: Unable to extract version from pom.xml."
    exit 1
fi

# Print the extracted version
echo "Application Version: $BUILD_VERSION"

export DOCKER_DEFAULT_PLATFORM=linux/amd64
IMAGE_NAME="suvera/scim2-compliance-test-utility"

docker build -t $IMAGE_NAME:$BUILD_VERSION .

docker image push $IMAGE_NAME:$BUILD_VERSION

echo "Successfully built and pushed the Docker image."
