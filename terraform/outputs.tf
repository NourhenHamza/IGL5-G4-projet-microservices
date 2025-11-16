output "EC2_public_ip" {
  value = aws_instance.docker_host.public_ip
}

output "vpc_id" {
  value = aws_vpc.main.id
}
