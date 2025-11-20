variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "aws_access_key" {
  description = "AWS access key"
  type        = string
  default     = ""
}

variable "aws_secret_key" {
  description = "AWS secret key"
  type        = string
  default     = ""
}

variable "aws_session_token" {
  description = "AWS session token (optional)"
  type        = string
  default     = ""
}

variable "project_name" {
  description = "Project prefix/name"
  type        = string
  default     = "spring-app"
}

variable "node_instance_type" {
  description = "EKS node instance type"
  type        = string
  default     = "t3.medium"
}

variable "node_desired" {
  description = "Desired EKS node count"
  type        = number
  default     = 1
}

variable "node_min" {
  type    = number
  default = 1
}

variable "node_max" {
  type    = number
  default = 2
}
