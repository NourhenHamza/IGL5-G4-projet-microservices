terraform {
  required_version = ">= 1.2.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = ">= 2.0"
    }
  }
}

provider "aws" {
  region                  = var.aws_region
  access_key              = var.aws_access_key != "" ? var.aws_access_key : null
  secret_key              = var.aws_secret_key != "" ? var.aws_secret_key : null
  token                   = var.aws_session_token != "" ? var.aws_session_token : null
  skip_metadata_api_check = true
}

# Kubernetes provider will be configured after cluster creation via provider alias in outputs / in Jenkins
