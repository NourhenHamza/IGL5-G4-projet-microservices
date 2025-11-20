# ----------------------
# Networking
# ----------------------
resource "aws_vpc" "main" {
  cidr_block         = "10.0.0.0/16"
  enable_dns_support = true
  enable_dns_hostnames = true
  tags = {
    Name = "${var.project_name}-vpc"
  }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
  tags = {
    Name = "${var.project_name}-subnet"
    # Required EKS tag for automatic discovery
    "kubernetes.io/cluster/${var.project_name}-eks" = "shared"
    "kubernetes.io/role/elb"                       = "1"
  }
}

resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
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
  name        = "${var.project_name}-eks-sg"
  description = "Security group for EKS cluster"
  vpc_id      = aws_vpc.main.id

  # Ingress for application port 8080
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Ingress for SSH
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Egress (Allow all outbound traffic)
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-sg"
  }
}

# ----------------------
# IAM Role Lookup (Using existing roles provided by ARNs)
# NOTE: This replaces the resource creation blocks that failed due to permission errors.
# The ARNs are supplied via variables from the Jenkinsfile.
# ----------------------
data "aws_iam_role" "eks_role_lookup" {
  # This performs a lookup using the ARN provided by the Jenkins pipeline.
  # The role must exist and have the AmazonEKSClusterPolicy attached.
  arn = var.eks_cluster_role_arn
}

data "aws_iam_role" "eks_node_role_lookup" {
  # This performs a lookup using the ARN provided by the Jenkins pipeline.
  # The role must exist and have EKS worker policies attached.
  arn = var.eks_node_role_arn
}


# ----------------------
# EKS Cluster
# ----------------------
resource "aws_eks_cluster" "eks" {
  name     = "${var.project_name}-eks"
  # Reference the ARN from the data source lookup
  role_arn = data.aws_iam_role.eks_role_lookup.arn
  version  = "1.29"

  vpc_config {
    subnet_ids         = [aws_subnet.public.id]
    security_group_ids = [aws_security_group.eks_sg.id]
  }

  # Removed IAM role policy attachments from depends_on, as they are now external.
  tags = {
    Name = "${var.project_name}-eks"
  }
}

# ----------------------
# EKS Node Group
# ----------------------
resource "aws_eks_node_group" "nodes" {
  cluster_name    = aws_eks_cluster.eks.name
  node_group_name = "${var.project_name}-nodes"
  # Reference the ARN from the data source lookup
  node_role_arn   = data.aws_iam_role.eks_node_role_lookup.arn
  subnet_ids      = [aws_subnet.public.id]
  instance_types  = ["t3.medium"]
  ami_type        = "AL2_x86_64"

  scaling_config {
    desired_size = 1
    min_size     = 1
    max_size     = 2
  }

  depends_on = [
    aws_eks_cluster.eks
  ]
}

# ----------------------
# Docker Host (EC2 Instance)
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
  # NOTE: key_name is left blank; you may need to specify an existing key pair here.

  tags = {
    Name = "${var.project_name}-docker-host"
  }
}