# Spring-Boot-Full-Stack

### For spring security logging :
    Note : Add below lines in application.yml file at root level
```yaml
logging:
  level:
    org:
      springframework:
        security: TRACE
```

### Dockerrun.aws.json file container definition example for react:
```json
{
  "name": "shreyas-full-stack-professional-react",
  "image": "shreyas957/shreyas-full-stack-professional-react:23.06.2024.05.14.12",
  "essential": true,
  "memory": 256,
  "portMappings": [
    {
      "hostPort": 80,
      "containerPort": 5173
    }
  ]
}
```

