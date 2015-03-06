#!/usr/bin/env bash
scp -i /home/bossola/.ssh/singapore_rsa target/ms-nos-usvc-client-0.0.1-SNAPSHOT.jar ubuntu@54.66.198.2:/home/ubuntu/
scp -i /home/bossola/.ssh/singapore_rsa target/ms-nos-usvc-client-0.0.1-SNAPSHOT.jar ubuntu@54.169.180.250:/home/ubuntu/
scp -i /home/bossola/.ssh/singapore_rsa target/ms-nos-usvc-client-0.0.1-SNAPSHOT.jar ubuntu@bb-chinatest.cloudapp.net:/home/ubuntu/
scp target/ms-nos-usvc-client-0.0.1-SNAPSHOT.jar bbossola@microservices.dev2.workshare.com:/home/bbossola/

