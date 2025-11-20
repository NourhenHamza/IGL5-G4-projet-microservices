output "eks_cluster_name" {
  description = "EKS cluster name"
  value       = aws_eks_cluster.eks.name
}

output "eks_cluster_endpoint" {
  description = "EKS cluster endpoint"
  value       = aws_eks_cluster.eks.endpoint
}

output "eks_cluster_ca" {
  description = "Base64 encoded cluster CA cert"
  value       = aws_eks_cluster.eks.certificate_authority[0].data
}

output "aws_region" {
  description = "AWS region used"
  value       = var.aws_region
}

output "ec2_public_ip" {
  description = "Docker host EC2 public IP (if created)"
  value       = aws_instance.docker_host.public_ip
  depends_on  = [aws_instance.docker_host]
}
