variable "aws_region" {
  description = "La région AWS"
  type        = string
  default     = "us-east-1"
}

variable "cluster_name" {
  description = "Nom du cluster EKS"
  type        = string
  default     = "mykubernetes"
}

variable "subnet_ids" {
  description = "IDs des sous-réseaux pour le cluster EKS (au moins 2 dans différentes AZs)"
  type        = list(string)
  default     = [
    "subnet-048a7e0f6f763cb5a",  # us-east-1a
    "subnet-0539d71142c1ad1e3",  # us-east-1b
    "subnet-0ffc030ed6e3bf549",  # us-east-1c
    "subnet-0d0cb3bd89de711da"   # us-east-1d
  ]
}

variable "role_arn" {
  description = "ARN du rôle IAM pour EKS"
  type        = string
  default     = "arn:aws:iam::922029027395:role/LabRole"
}

variable "vpc_id" {
  description = "L'ID du VPC existant pour le cluster EKS"
  type        = string
  default     = "vpc-075e51704be81700d"
}