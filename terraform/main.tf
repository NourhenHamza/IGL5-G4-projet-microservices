provider "aws" {
  region = var.aws_region
}

# Reference existing VPC
data "aws_vpc" "existing" {
  id = var.vpc_id
}

# Security group for EKS cluster
resource "aws_security_group" "eks_cluster_sg" {
  name        = "eks-cluster-sg-${var.cluster_name}"
  description = "Security group for EKS cluster ${var.cluster_name}"
  vpc_id      = var.vpc_id

  # Application port (Spring Boot runs on 8082)
  ingress {
    description = "Spring Boot Application"
    from_port   = 8082
    to_port     = 8082
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # NodePort range for Kubernetes services
  ingress {
    description = "Kubernetes NodePort Services"
    from_port   = 30000
    to_port     = 32767
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTPS for Kubernetes API
  ingress {
    description = "Kubernetes API HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow all outbound traffic
  egress {
    description = "Allow all outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "eks-cluster-sg-${var.cluster_name}"
  }
}

# Security group for EKS worker nodes
resource "aws_security_group" "eks_worker_sg" {
  name        = "eks-worker-sg-${var.cluster_name}"
  description = "Security group for EKS worker nodes ${var.cluster_name}"
  vpc_id      = var.vpc_id

  # Application port
  ingress {
    description = "Spring Boot Application"
    from_port   = 8082
    to_port     = 8082
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # NodePort range
  ingress {
    description = "Kubernetes NodePort Services"
    from_port   = 30000
    to_port     = 32767
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow all traffic within the security group (pod-to-pod communication)
  ingress {
    description = "Node to node communication"
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    self        = true
  }

  # Allow traffic from cluster security group
  ingress {
    description     = "Allow cluster control plane"
    from_port       = 0
    to_port         = 65535
    protocol        = "tcp"
    security_groups = [aws_security_group.eks_cluster_sg.id]
  }

  # Allow all outbound traffic
  egress {
    description = "Allow all outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "eks-worker-sg-${var.cluster_name}"
  }
}

# EKS Cluster
resource "aws_eks_cluster" "my_cluster" {
  name     = var.cluster_name
  role_arn = var.role_arn
  version  = "1.30"

  vpc_config {
    subnet_ids         = var.subnet_ids
    security_group_ids = [aws_security_group.eks_cluster_sg.id]
  }

  depends_on = [
    aws_security_group.eks_cluster_sg
  ]

  tags = {
    Name = var.cluster_name
  }
  timeouts {
    create = "30m"
    update = "30m"
    delete = "30m"
  }
}

# EKS Node Group
resource "aws_eks_node_group" "my_node_group" {
  cluster_name    = aws_eks_cluster.my_cluster.name
  node_group_name = "noeud1"
  node_role_arn   = var.role_arn
  subnet_ids      = var.subnet_ids

  scaling_config {
    desired_size = 2
    max_size     = 3
    min_size     = 1
  }

  # Instance type for worker nodes
  instance_types = ["t3.medium"]

  # Disk size for worker nodes
  disk_size = 20

  depends_on = [
    aws_eks_cluster.my_cluster
  ]

  tags = {
    Name = "noeud1"
  }
}