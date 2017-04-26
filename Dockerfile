#
# Abstracta AutomationFramework Dockerfile
#
# https://github.com/abstracta/SeleniumAutomationFW
#

# Pull base image
FROM ubuntu:16.04

# Update repos
RUN apt-get update
RUN apt-get -y upgrade

# Install java and maven
RUN apt-get install -y default-jdk
RUN apt-get install -y maven

# Set environment variables.
ENV HOME /root

# Define working directory.
WORKDIR /root/automationFramework

# Define default command.
CMD ["bash"]

# Prepare by downloading dependencies
ADD pom.xml /root/automationFramework/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source
ADD src /root/automationFramework/src
