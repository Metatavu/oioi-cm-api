#!/bin/sh
openssl enc -d -aes-256-cbc -d -pass file:key.txt -in secured.tar.gz | tar xz