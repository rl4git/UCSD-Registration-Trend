#!/bin/bash

# Final table path
TARGET_DIR=$(realpath /home/ubuntu/projects/UCSD-Registration-Trend/data/cleaned_data/ucsd/final_table)

# delete old data since s3 won't auto delete
rm -rf "$TARGET_DIR"

# download from s3
aws s3 cp s3://ucsd-registration-s3-20250609193613565500000001/ucsd/final_table "$TARGET_DIR" --recursive

echo "Sync finished, s3 data downloaded to $TARGET_DIR"

