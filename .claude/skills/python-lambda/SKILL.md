---
name: aws-lambda-python
description: |
  Rapidly create, test, and deploy Python 3.12 Lambda functions for interview prep and production tasks. Covers all major use cases: API Gateway REST endpoints, event-driven architectures (S3/SQS/SNS/DynamoDB), AWS SDK operations, scheduled tasks, and data processing. Includes ready-to-use templates, AWS CLI one-liner deployments, pytest+moto local testing, and interview-specific DSA problem wrappers. Use this skill whenever you need to: write a Lambda function from scratch, deploy to AWS quickly, set up local testing, optimize for cold starts, handle errors gracefully, or structure DSA problems as serverless endpoints. Always includes visual architecture diagrams (Excalidraw) for invoke chains and data flows.
compatibility: AWS CLI v2, Python 3.12, boto3, pytest, moto
---

# AWS Lambda Python Skill

**Fast. Tested. Interview-Ready.** Quickly scaffold, locally test, and deploy Lambda functions in Python 3.12 using AWS CLI commands.

---

## Quick Start: 3-Minute Lambda

### 1. Choose Your Use Case

Pick one below, then jump to that section:

| Use Case | Best For | Complexity |
|----------|----------|-----------|
| **API Gateway → Lambda** | REST endpoints, webhooks, microservices | ⭐ Low |
| **Event-Driven (S3/SQS/SNS)** | File processing, async messaging, real-time workflows | ⭐⭐ Medium |
| **AWS SDK Ops** | CRUD on DynamoDB, S3, Secrets Manager | ⭐ Low |
| **Scheduled Tasks** | Cron-like jobs via EventBridge/CloudWatch | ⭐ Low |
| **Data Processing** | CSV/JSON transforms, batch jobs | ⭐⭐ Medium |
| **Interview DSA Wrapper** | LeetCode problems as Lambda endpoints | ⭐ Low |

### 2. Generate Template

When you ask me to create a Lambda for one of these use cases, I will:
- Generate the handler code (Python 3.12)
- Create AWS CLI deployment commands
- Add pytest + moto test cases
- Include Excalidraw diagrams showing data flow and invoke chains
- Provide error handling, logging, and best practices

### 3. Deploy in One Command

```bash
# Package and deploy your function in 30 seconds
zip lambda_function.zip lambda_function.py
aws lambda create-function \
  --function-name my-function \
  --runtime python3.12 \
  --role arn:aws:iam::ACCOUNT_ID:role/lambda-execution-role \
  --handler lambda_function.handler \
  --zip-file fileb://lambda_function.zip
```

---

## Core Patterns: Python 3.12 Lambda Handlers

### Pattern 1: API Gateway (REST Endpoint)

**When to use**: Building microservices, webhooks, serverless APIs.

```python
import json
from aws_lambda_powertools import Logger

logger = Logger()

def handler(event, context):
    """API Gateway → Lambda handler"""
    try:
        # Parse request
        body = json.loads(event.get('body', '{}'))
        path = event['requestContext']['resourcePath']
        method = event['httpMethod']
        
        logger.info(f"{method} {path}", extra={"body": body})
        
        # Business logic here
        result = {"statusCode": 200, "message": "Success"}
        
        return {
            "statusCode": result["statusCode"],
            "headers": {"Content-Type": "application/json"},
            "body": json.dumps(result)
        }
    except Exception as e:
        logger.exception("Handler failed")
        return {
            "statusCode": 500,
            "body": json.dumps({"error": str(e)})
        }
```

**Key points**:
- Always parse `event['body']` as JSON
- Return `{"statusCode": int, "headers": dict, "body": str}` format
- Log with `Logger` from aws-lambda-powertools (already in Lambda)
- Use `statusCode` 4xx for client errors, 5xx for server errors

---

### Pattern 2: Event-Driven (S3, SQS, SNS, DynamoDB Streams)

**When to use**: File triggers, async messaging, real-time processing.

```python
import json
import boto3
from aws_lambda_powertools import Logger

logger = Logger()
s3 = boto3.client('s3')

def handler(event, context):
    """
    Triggered by S3:ObjectCreated, SQS SendMessage, SNS Publish, or DynamoDB Stream.
    Each invocation receives array of 'Records'.
    """
    for record in event.get('Records', []):
        try:
            # Identify source
            if 'S3' in record:
                bucket = record['s3']['bucket']['name']
                key = record['s3']['object']['key']
                logger.info(f"S3 trigger: {bucket}/{key}")
                # Process file: s3.get_object(Bucket=bucket, Key=key)
                
            elif 'sqs' in record.get('eventSource', ''):
                body = json.loads(record['body'])
                logger.info(f"SQS message: {body}")
                # Process message
                
            elif 'sns' in record.get('eventSource', ''):
                message = json.loads(record['Sns']['Message'])
                logger.info(f"SNS message: {message}")
                # Process message
                
            elif 'dynamodb' in record.get('eventSource', ''):
                keys = record['dynamodb'].get('Keys', {})
                logger.info(f"DynamoDB stream: {keys}")
                # Process stream record
                
        except Exception as e:
            logger.exception(f"Failed to process record: {record}")
            # Decide: raise to retry, or log and continue
            raise
```

**Key points**:
- Always iterate over `Records` array—event-driven can batch multiple triggers
- Identify source by checking `eventSource` or presence of specific keys
- For SQS/SNS, `body` and `Message` are JSON strings—parse them
- On failure: `raise` to retry, or log and `continue` to skip (depends on use case)

---

### Pattern 3: AWS SDK Operations (DynamoDB, S3, Secrets Manager)

**When to use**: CRUD ops, reading secrets, interacting with AWS services.

```python
import json
import boto3
from botocore.exceptions import ClientError
from aws_lambda_powertools import Logger

logger = Logger()
dynamodb = boto3.resource('dynamodb')
s3 = boto3.client('s3')
secrets_client = boto3.client('secretsmanager')

def handler(event, context):
    """Example: Read secret, Query DynamoDB, Store in S3"""
    try:
        # 1. Get secret from Secrets Manager
        secret_response = secrets_client.get_secret_value(SecretId='my-secret')
        secret = json.loads(secret_response['SecretString'])
        logger.info("Secret retrieved")
        
        # 2. Query DynamoDB
        table = dynamodb.Table('my-table')
        response = table.query(
            KeyConditionExpression='pk = :pk',
            ExpressionAttributeValues={':pk': 'user-123'}
        )
        items = response.get('Items', [])
        logger.info(f"DynamoDB query returned {len(items)} items")
        
        # 3. Store results in S3
        s3.put_object(
            Bucket='my-bucket',
            Key=f'results/{event.get("run_id")}.json',
            Body=json.dumps(items),
            ContentType='application/json'
        )
        
        return {"statusCode": 200, "message": "Success", "items_count": len(items)}
        
    except ClientError as e:
        logger.error(f"AWS error: {e.response['Error']['Code']}")
        return {"statusCode": 500, "error": str(e)}
```

**Key points**:
- Use `boto3.resource()` for higher-level APIs (DynamoDB, S3)
- Use `boto3.client()` for low-level APIs (Secrets Manager, SNS publish, etc.)
- Always catch `ClientError` to handle AWS-specific errors
- Log errors explicitly—CloudWatch is your only visibility in production

---

### Pattern 4: Scheduled Tasks (EventBridge / CloudWatch Events)

**When to use**: Cron-like jobs, periodic cleanup, health checks.

```python
import json
from datetime import datetime
from aws_lambda_powertools import Logger

logger = Logger()

def handler(event, context):
    """
    Triggered by EventBridge rule (e.g., rate(5 minutes) or cron).
    Event contains minimal data; use context to detect if scheduled.
    """
    logger.info(f"Triggered at {datetime.utcnow().isoformat()}")
    
    # Event from EventBridge contains mostly metadata
    detail = event.get('detail', {})
    source = event.get('source', 'unknown')
    
    logger.info(f"Source: {source}, Detail: {detail}")
    
    # Business logic: cleanup, check health, sync data, etc.
    result = perform_scheduled_task()
    
    return {
        "statusCode": 200,
        "message": "Task completed",
        "result": result,
        "timestamp": datetime.utcnow().isoformat()
    }

def perform_scheduled_task():
    """Placeholder for your scheduled work"""
    return {"action": "completed", "duration_seconds": 5}
```

**Key points**:
- Event structure from EventBridge is minimal—check `event.get('detail')`
- Use `datetime.utcnow()` or `time.time()` to track execution
- Always return a response with timestamp and status (for monitoring)
- Consider idempotency if this could be triggered multiple times

---

### Pattern 5: Data Processing (CSV/JSON Transforms)

**When to use**: Batch data jobs, ETL workflows, format conversions.

```python
import json
import csv
import io
import boto3
from aws_lambda_powertools import Logger

logger = Logger()
s3 = boto3.client('s3')

def handler(event, context):
    """
    Example: Read CSV from S3, transform, write JSON back.
    """
    try:
        bucket = event['bucket']
        key = event['key']
        
        # 1. Read CSV from S3
        logger.info(f"Reading {bucket}/{key}")
        obj = s3.get_object(Bucket=bucket, Key=key)
        csv_data = obj['Body'].read().decode('utf-8')
        
        # 2. Parse and transform
        reader = csv.DictReader(io.StringIO(csv_data))
        transformed = []
        for row in reader:
            transformed.append({
                "id": row.get('id'),
                "name": row.get('name', '').upper(),
                "score": float(row.get('score', 0))
            })
        logger.info(f"Transformed {len(transformed)} rows")
        
        # 3. Write JSON back to S3
        output_key = key.replace('.csv', '.json')
        s3.put_object(
            Bucket=bucket,
            Key=output_key,
            Body=json.dumps(transformed),
            ContentType='application/json'
        )
        
        return {
            "statusCode": 200,
            "rows_processed": len(transformed),
            "output_key": output_key
        }
        
    except Exception as e:
        logger.exception("Data processing failed")
        return {"statusCode": 500, "error": str(e)}
```

**Key points**:
- For CSV: Use `csv.DictReader()` with `io.StringIO()` for in-memory parsing
- Always decode bytes from S3 with `.decode('utf-8')`
- Transform data step-by-step; log progress for long-running tasks
- Write results back to S3 with descriptive `Key` names (e.g., with timestamps)

---

### Pattern 6: Interview DSA Problem Wrapper

**When to use**: Testing LeetCode solutions, system design scenarios, prep.

```python
import json
from typing import List
from aws_lambda_powertools import Logger

logger = Logger()

def handler(event, context):
    """
    Example: Two Sum problem wrapped as Lambda.
    Input: {"nums": [2, 7, 11, 15], "target": 9}
    Output: [0, 1] (indices of nums that sum to target)
    """
    try:
        body = json.loads(event.get('body', '{}')) if isinstance(event.get('body'), str) else event
        nums = body.get('nums', [])
        target = body.get('target', 0)
        
        logger.info(f"Solving Two Sum: nums={nums}, target={target}")
        
        # Validate input
        if not isinstance(nums, list) or not isinstance(target, (int, float)):
            return error_response(400, "Invalid input format")
        
        if len(nums) < 2:
            return error_response(400, "Need at least 2 numbers")
        
        # Solve problem
        result = two_sum(nums, target)
        
        if result:
            return success_response(200, {"result": result, "explanation": "Indices that sum to target"})
        else:
            return error_response(404, "No solution found")
            
    except Exception as e:
        logger.exception("DSA handler failed")
        return error_response(500, str(e))

def two_sum(nums: List[int], target: int) -> List[int]:
    """Two Sum: Return indices [i, j] where nums[i] + nums[j] == target"""
    seen = {}
    for i, num in enumerate(nums):
        complement = target - num
        if complement in seen:
            return [seen[complement], i]
        seen[num] = i
    return []

def success_response(status_code: int, data: dict) -> dict:
    """Format success response"""
    return {
        "statusCode": status_code,
        "headers": {"Content-Type": "application/json"},
        "body": json.dumps({"success": True, "data": data})
    }

def error_response(status_code: int, message: str) -> dict:
    """Format error response"""
    return {
        "statusCode": status_code,
        "headers": {"Content-Type": "application/json"},
        "body": json.dumps({"success": False, "error": message})
    }
```

**Key points**:
- Wrap each DSA solution in a `handler()` that parses input, validates, solves, returns JSON
- Always include input validation (type, length, range checks)
- Provide helper functions like `success_response()` and `error_response()` for consistency
- Test locally with pytest + moto before submitting code to interview platform

---

## Local Testing: pytest + moto

### Test File Structure

Create `test_lambda_function.py` next to your `lambda_function.py`:

```python
import json
import pytest
from moto import mock_s3, mock_dynamodb
import boto3
from lambda_function import handler, two_sum  # Import your handler + logic

# Mock S3 for local testing
@mock_s3
def test_s3_read_write():
    """Test Lambda that reads/writes S3"""
    # Setup
    s3 = boto3.client('s3', region_name='us-east-1')
    s3.create_bucket(Bucket='test-bucket')
    s3.put_object(Bucket='test-bucket', Key='input.json', Body=json.dumps({"data": "test"}))
    
    # Call handler
    event = {'bucket': 'test-bucket', 'key': 'input.json'}
    response = handler(event, None)
    
    # Assert
    assert response['statusCode'] == 200
    # Verify output was written
    result = s3.get_object(Bucket='test-bucket', Key='output.json')
    assert json.loads(result['Body'].read()) == expected_output

# Mock DynamoDB for local testing
@mock_dynamodb
def test_dynamodb_query():
    """Test Lambda that queries DynamoDB"""
    # Setup
    dynamodb = boto3.resource('dynamodb', region_name='us-east-1')
    table = dynamodb.create_table(
        TableName='test-table',
        KeySchema=[{'AttributeName': 'pk', 'KeyType': 'HASH'}],
        AttributeDefinitions=[{'AttributeName': 'pk', 'AttributeType': 'S'}],
        BillingMode='PAY_PER_REQUEST'
    )
    table.put_item(Item={'pk': 'user-123', 'name': 'Alice'})
    
    # Call handler
    event = {'pk': 'user-123'}
    response = handler(event, None)
    
    # Assert
    assert response['statusCode'] == 200
    assert response['items_count'] == 1

# Test pure Python logic (no AWS calls)
def test_two_sum():
    """Test DSA solution"""
    assert two_sum([2, 7, 11, 15], 9) == [0, 1]
    assert two_sum([3, 2, 4], 6) == [1, 2]
    assert two_sum([1, 2, 3], 7) == []

# Test API Gateway event format
def test_api_gateway_handler():
    """Test handler with API Gateway event"""
    event = {
        'httpMethod': 'POST',
        'body': json.dumps({'name': 'Alice', 'age': 30}),
        'requestContext': {'resourcePath': '/users'}
    }
    response = handler(event, None)
    
    assert response['statusCode'] == 200
    body = json.loads(response['body'])
    assert body['success'] == True
```

### Run Tests

```bash
# Install test dependencies
pip install pytest moto boto3

# Run all tests
pytest test_lambda_function.py -v

# Run single test
pytest test_lambda_function.py::test_two_sum -v

# Run with coverage
pytest test_lambda_function.py --cov=lambda_function
```

---

## Deployment: AWS CLI One-Liners

### Step 1: Set Up IAM Role (One-Time)

```bash
# Create Lambda execution role with basic permissions
aws iam create-role \
  --role-name lambda-execution-role \
  --assume-role-policy-document '{
    "Version": "2012-10-17",
    "Statement": [{
      "Effect": "Allow",
      "Principal": {"Service": "lambda.amazonaws.com"},
      "Action": "sts:AssumeRole"
    }]
  }'

# Attach basic Lambda execution policy (logs, etc.)
aws iam attach-role-policy \
  --role-name lambda-execution-role \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

# If your Lambda needs S3 access
aws iam attach-role-policy \
  --role-name lambda-execution-role \
  --policy-arn arn:aws:iam::aws:policy/AmazonS3FullAccess

# If your Lambda needs DynamoDB access
aws iam attach-role-policy \
  --role-name lambda-execution-role \
  --policy-arn arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess
```

Get your AWS Account ID:

```bash
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ROLE_ARN="arn:aws:iam::${ACCOUNT_ID}:role/lambda-execution-role"
echo $ROLE_ARN
```

### Step 2: Package & Deploy

```bash
# Package your function
zip lambda_function.zip lambda_function.py

# Create function (first time)
aws lambda create-function \
  --function-name my-function \
  --runtime python3.12 \
  --role $ROLE_ARN \
  --handler lambda_function.handler \
  --zip-file fileb://lambda_function.zip \
  --timeout 30 \
  --memory-size 256

# Update function code (subsequent times)
aws lambda update-function-code \
  --function-name my-function \
  --zip-file fileb://lambda_function.zip
```

### Step 3: Test Locally Before Deploying

```bash
# Invoke function with test event
aws lambda invoke \
  --function-name my-function \
  --payload '{"key": "value"}' \
  response.json

cat response.json
```

---

## Best Practices & Optimization

### 1. Cold Start Optimization

**Problem**: First invocation is slow (~1-3 seconds for Python).

**Solutions**:
- **Provisioned Concurrency**: Keep a few instances warm
  ```bash
  aws lambda put-provisioned-concurrency-config \
    --function-name my-function \
    --provisioned-concurrent-executions 5
  ```
- **Memory Tuning**: Increase memory (also increases CPU)—test to find sweet spot (1024 MB often optimal)
  ```bash
  aws lambda update-function-configuration \
    --function-name my-function \
    --memory-size 1024
  ```
- **Lazy Imports**: Only import heavy modules when needed
  ```python
  def handler(event, context):
      import pandas  # Import only if needed
      return pandas.something()
  ```

### 2. Error Handling & Retries

**SQS/SNS**: If handler raises exception, AWS automatically retries
```python
def handler(event, context):
    for record in event['Records']:
        try:
            process(record)
        except Exception as e:
            logger.exception("Processing failed, will retry")
            raise  # Raises to trigger SQS/SNS retry
```

**DynamoDB Streams**: Failed records go to DLQ (set via EventSourceMapping)
**S3 Events**: No retry—handle transient failures gracefully

### 3. Timeouts & Limits

- **Timeout**: Default 3 seconds, max 15 minutes
  ```bash
  aws lambda update-function-configuration \
    --function-name my-function \
    --timeout 60
  ```
- **Memory**: 128 MB – 10,240 MB (CPU scales with memory)
- **Temp Storage (/tmp)**: 512 MB max—use for downloads, processing

### 4. Logging & Debugging

Use `aws-lambda-powertools` Logger (free, included):
```python
from aws_lambda_powertools import Logger
logger = Logger()

logger.info("This is info", extra={"user_id": "123"})
logger.error("Error occurred", extra={"error_code": 5})

# View logs
aws logs tail /aws/lambda/my-function --follow
```

### 5. Environment Variables

Store secrets, config, toggles:
```bash
aws lambda update-function-configuration \
  --function-name my-function \
  --environment Variables={ENV=prod,DEBUG=false}
```

Access in code:
```python
import os
env = os.getenv('ENV', 'dev')
debug = os.getenv('DEBUG', 'false').lower() == 'true'
```

---

## Interview Checklist: Before Submitting

Use this checklist for every Lambda you build in an interview:

- [ ] **Input Validation**: Check type, length, range of all inputs
- [ ] **Error Handling**: Catch exceptions, return meaningful error messages
- [ ] **Logging**: Log important steps, especially failures
- [ ] **Edge Cases**: Test empty input, null values, boundary conditions
- [ ] **Performance**: O(n), O(n log n)? Acceptable for constraints?
- [ ] **AWS Best Practices**: 
  - [ ] Use IAM roles (never hardcode credentials)
  - [ ] Return correct HTTP status codes (200, 400, 404, 500)
  - [ ] Handle timeouts gracefully (don't start 10-minute tasks)
- [ ] **Local Testing**: Run pytest before deploying
- [ ] **Idempotency**: Can this be safely called twice? (For event-driven)

---

## Visual Architecture Reference

See `references/invoke-chains.md` for Excalidraw diagrams showing:
- API Gateway → Lambda → DynamoDB data flow
- S3 → Lambda → SNS event chain
- EventBridge (scheduled) → Lambda → S3 workflow
- DSA solution wrapped in API Gateway → Lambda → CloudWatch Logs

---

## File Structure & Next Steps

When I generate a Lambda for your use case, I'll provide:

```
my-function/
├── lambda_function.py          # Your handler + business logic
├── test_lambda_function.py     # pytest + moto tests
├── deploy.sh                   # One-liner AWS CLI deployment
└── README.md                   # Use-case specific notes
```

**To use:**
1. Run tests: `pytest test_lambda_function.py -v`
2. Deploy: `bash deploy.sh`
3. Check logs: `aws logs tail /aws/lambda/my-function --follow`

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "Role not found" | Check role ARN, wait 10 seconds for IAM propagation |
| "Timeout" | Increase `--timeout` or optimize code (remove heavy loops) |
| "Module not found" | Add dependency to requirements.txt, zip together with handler |
| "Permission denied" | Check IAM role has correct policy attached |
| "Cold start slow" | Increase memory or use provisioned concurrency |

---

## References & Resources

- **AWS Lambda**: https://docs.aws.amazon.com/lambda/
- **boto3 docs**: https://boto3.amazonaws.com/v1/documentation/api/latest/index.html
- **aws-lambda-powertools**: https://docs.powertools.aws.dev/lambda/python/
- **LeetCode**: Test DSA solutions as Lambda endpoints!

---

**Ready?** Just ask: "Create a Lambda for [use case]" and I'll scaffold the full stack: code, tests, deployment commands, and architecture diagram.
