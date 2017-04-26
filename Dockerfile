#
# Abstracta AutomationFramework Dockerfile
# Based on Ubuntu
#
# https://github.com/abstracta/SeleniumAutomationFW
#

# Pull base image
FROM ubuntu:16.04

# File Author / Maintainer
MAINTAINER Abstracta Inc.

# Update and upgrade repository sources list
RUN apt-get update
#RUN apt-get -y upgrade

# Install java and maven
RUN apt-get install -y default-jdk maven

# Install curl and wget
RUN apt-get install -y curl wget

# Install browsers
RUN apt-get install -y firefox
RUN apt install -y libappindicator1 libdbusmenu-glib4 libdbusmenu-gtk4 libindicator7 libpango1.0-0 libxss1 xdg-utils fonts-liberation
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb -O /root/google-chrome-stable_current_amd64.deb
RUN dpkg -i /root/google-chrome-stable_current_amd64.deb

# Install tar and unzip
RUN apt-get install -y tar unzip

# Set environment variables.
ENV HOME /root

# Define working directory.
WORKDIR /root/automationFramework

# Create folder for drivers
RUN mkdir -p /root/drivers

# Download drivers
RUN wget https://github.com/mozilla/geckodriver/releases/download/v0.16.1/geckodriver-v0.16.1-linux64.tar.gz -O /root/drivers/geckodriver-v0.16.1.tar.gz
RUN wget http://chromedriver.storage.googleapis.com/2.29/chromedriver_linux64.zip -O /root/drivers/chromedriver.zip

# Unzip drivers
RUN tar xvzf /root/drivers/geckodriver-v0.16.1.tar.gz
RUN unzip /root/drivers/chromedriver.zip

# Delete files
RUN rm -rf /root/drivers/geckodriver-v0.16.1.tar.gz
RUN rm -rf /root/drivers/chromedriver.zip

# Add driver folder to PATH
ENV PATH="/root/drivers:${PATH}"

# Define default command.
CMD ["bash"]

# Prepare by downloading dependencies
ADD pom.xml /root/automationFramework/pom.xml
RUN ["mvn", "dependency:resolve"]

# Adding source
ADD src /root/automationFramework/src

# Execute tests
#RUN mvn clean test -Dsuite="SMOKE_TEST" -Denvironment="QA" 