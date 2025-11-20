output "eks_cluster_name" {
  description = "The name of the EKS cluster."
  value       = aws_eks_cluster.eks.name
}

output "aws_region" {
  description = "The AWS region where resources are deployed."
  value       = var.aws_region
}

output "EC2_public_ip" {
  description = "The public IP address of the general-purpose Docker host EC2 instance."
  value       = aws_instance.docker_host.public_ip
}

output "vpc_id" {
  description = "The ID of the VPC created."
  value       = aws_vpc.main.id
}