# sns

{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "s3.amazonaws.com"
      },
      "Action": "SNS:Publish",
      "Resource": "arn:aws:sns:us-east-1:YOUR_ACCOUNT_ID:FileUploadTopic",
      "Condition": {
        "ArnLike": {
          "aws:SourceArn": "arn:aws:s3:::cc-lambda-s32dynamodb"
        }
      }
    }
  ]
}

# sqs
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "sns.amazonaws.com"
      },
      "Action": "sqs:SendMessage",
      "Resource": "arn:aws:sqs:us-east-1:YOUR_ACCOUNT_ID:FileProcessingQueue",
      "Condition": {
        "ArnEquals": {
          "aws:SourceArn": "arn:aws:sns:us-east-1:YOUR_ACCOUNT_ID:FileUploadTopic"
        }
      }
    }
  ]
}

# lambda
import json
import boto3
import urllib.parse
from datetime import datetime, timezone

dynamodb = boto3.resource('dynamodb')
table    = dynamodb.Table('FileMetadata')

def lambda_handler(event, context):
    print("Lambda triggered from SQS")
    print("Raw event:", json.dumps(event))

    for sqs_record in event['Records']:

        # SQS wraps the SNS message as a string — parse it
        sns_message = json.loads(sqs_record['body'])
        s3_event    = json.loads(sns_message['Message'])

        print("S3 Event:", json.dumps(s3_event))

        for s3_record in s3_event['Records']:
            bucket_name = s3_record['s3']['bucket']['name']
            file_name   = urllib.parse.unquote_plus(
                              s3_record['s3']['object']['key'])
            file_size   = s3_record['s3']['object']['size']
            upload_time = datetime.now(timezone.utc).isoformat()

            print(f"Processing: {file_name} ({file_size} bytes) from {bucket_name}")

            # Save metadata to DynamoDB
            table.put_item(Item={
                'fileName'   : file_name,
                'bucketName' : bucket_name,
                'fileSize'   : file_size,
                'uploadTime' : upload_time,
                'status'     : 'processed'
            })

            print(f"Metadata saved to DynamoDB: {file_name}")

    return {'statusCode': 200, 'body': 'All files processed'}
