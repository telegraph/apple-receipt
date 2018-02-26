#!/usr/bin/env bash


k6 run --vus 500 --duration 30s perf-test.js