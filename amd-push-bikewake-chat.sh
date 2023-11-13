#!/bin/sh

docker image build -t amd64-chat-bikewake:development .
docker tag amd64-chat-bikewake:development bikewake/amd64-chat-bikewake:0.0.1
docker push bikewake/amd64-chat-bikewake:0.0.1

