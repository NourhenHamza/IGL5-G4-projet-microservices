variable "aws_region" {
  description = "AWS region to deploy resources in"
  type        = string
  default     = "us-east-1" # or any region you want
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
  default     = 2
}

variable "node_min" {
  type    = number
  default = 1
}

variable "node_max" {
  type    = number
  default = 3
}

variable "eks_role_arn" {
  description = "ARN of pre-created IAM role for EKS control plane"
  type        = string
  default     = "arn:aws:iam::969827070936:role/c180773a4650446l12670702t1w969827-LabEksClusterRole-JaOBDjYahmup"
}

variable "eks_node_role_arn" {
  description = "ARN of pre-created IAM role for EKS worker nodes"
  type        = string
  default     = "arn:aws:iam::969827070936:role/c180773a4650446l12670702t1w969827070-LabEksNodeRole-YxRjn9DxG14b"
}
