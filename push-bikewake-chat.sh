#!/bin/sh

docker image build -t chat-bikewake:development .
docker tag chat-bikewake:development bikewake/chat-bikewake:0.0.1
docker push bikewake/chat-bikewake:0.0.1

