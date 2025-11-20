locals {
  name_prefix = "${var.project_name}"
}


# Pre-created IAM roles
variable "eks_role_arn" {
  description = "ARN of pre-created IAM role for EKS control plane"
  type        = string
  default     = "arn:aws:iam::969827070936:role/c180773a4650446l12670702t1w969827-LabEksClusterRole-S2evLHWJHH8q"
}

variable "eks_node_role_arn" {
  description = "ARN of pre-created IAM role for EKS worker nodes"
  type        = string
  default     = "arn:aws:iam::969827070936:role/c180773a4650446l12670702t1w969827070-LabEksNodeRole-XM0TklYMNNkG"
}

# ----------------------
# Networking
# ----------------------
data "aws_availability_zones" "available" {
  state = "available"
}

resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${local.name_prefix}-vpc"
  }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = data.aws_availability_zones.available.names[0]
  map_public_ip_on_launch = true

  tags = {
    Name                                            = "${local.name_prefix}-subnet"
    "kubernetes.io/cluster/${local.name_prefix}-eks" = "shared"
    "kubernetes.io/role/elb"                         = "1"
  }
}

resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "${local.name_prefix}-public-rt"
  }
}

resource "aws_route_table_association" "assoc" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public_rt.id
}

# ----------------------
# Security Group
# ----------------------
resource "aws_security_group" "eks_sg" {
  name        = "${local.name_prefix}-eks-sg"
  description = "Security group for EKS cluster and nodes"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${local.name_prefix}-sg"
  }
}

# ----------------------
# EKS Cluster
# ----------------------
resource "aws_eks_cluster" "eks" {
  name     = "${local.name_prefix}-eks"
  role_arn = var.eks_role_arn
  version  = "1.29"

  vpc_config {
    subnet_ids         = [aws_subnet.public.id]
    security_group_ids = [aws_security_group.eks_sg.id]
    endpoint_private_access = false
    endpoint_public_access  = true
  }

  tags = {
    Name = "${local.name_prefix}-eks"
  }
}

# ----------------------
# EKS Managed Node Group
# ----------------------
resource "aws_eks_node_group" "nodes" {
  cluster_name    = aws_eks_cluster.eks.name
  node_group_name = "${local.name_prefix}-nodes"
  node_role_arn   = var.eks_node_role_arn
  subnet_ids      = [aws_subnet.public.id]
  instance_types  = [var.node_instance_type]
  ami_type        = "AL2_x86_64"

  scaling_config {
    desired_size = var.node_desired
    min_size     = var.node_min
    max_size     = var.node_max
  }

  depends_on = [
    aws_eks_cluster.eks
  ]
}

# ----------------------
# Optional EC2 docker host
# ----------------------
data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}

resource "aws_instance" "docker_host" {
  ami                         = data.aws_ami.amazon_linux.id
  instance_type               = "t2.micro"
  subnet_id                   = aws_subnet.public.id
  associate_public_ip_address = true
  vpc_security_group_ids      = [aws_security_group.eks_sg.id]

  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              yum install -y docker
              systemctl enable docker
              systemctl start docker
              EOF

  tags = {
    Name = "${local.name_prefix}-docker-host"
  }
}
