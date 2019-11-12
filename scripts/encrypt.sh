#!/bin/sh
tar -czf - ./production-data/* | openssl enc -aes-256-cbc -pass file:key.txt -out secured.tar.gz