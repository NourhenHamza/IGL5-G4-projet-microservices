variable "project_name" {
  description = "The prefix name for all resources."
  default     = "spring-app"
}

variable "aws_region" {
  description = "AWS region"
  default     = "us-east-1"
}

// --- NEW VARIABLES FOR EXISTING ROLES ---
variable "eks_cluster_role_arn" {
  description = "ARN of the existing IAM Role for the EKS Cluster (must allow 'eks.amazonaws.com' to assume role)."
  type        = string
}

variable "eks_node_role_arn" {
  description = "ARN of the existing IAM Role for the EKS Worker Nodes (must allow 'ec2.amazonaws.com' to assume role and have EKS Worker policies attached)."
  type        = string
}
// ----------------------------------------

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