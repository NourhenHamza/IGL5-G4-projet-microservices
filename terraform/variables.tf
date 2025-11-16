variable "aws_region" {
  description = "AWS region"
  default     = "eu-east-1"
}

variable "aws_access_key" {
  description = "AWS access key"
  type        = string
}

variable "aws_secret_key" {
  description = "AWS secret key"
  type        = string
}

variable "aws_session_token" {
  description = "AWS session token"
  type        = string
  default     = ""
}

variable "project_name" {
  default = "spring-app"
}
