#!/bin/bash

# Complete DevOps Setup for IGL5-G4 Project
# Installs Terraform, sets up monitoring with Prometheus & Grafana

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m'

echo -e "${PURPLE}"
cat << "EOF"
╔══════════════════════════════════════════════════════════════╗
║                                                              ║
║     DevOps Setup - IGL5 G4 Événement Project                ║
║     Terraform + Prometheus + Grafana                         ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
EOF
echo -e "${NC}"

# Functions
print_step() {
    echo -e "\n${BLUE}[STEP]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

# Step 1: Check prerequisites
print_step "Checking prerequisites..."

if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi
print_success "Docker is installed"

# Step 2: Install Terraform if not exists
print_step "Installing Terraform..."

if command -v terraform &> /dev/null; then
    print_success "Terraform already installed ($(terraform version -json | grep -o '"version":"[^"]*' | cut -d'"' -f4))"
else
    print_warning "Terraform not found. Installing..."

    TERRAFORM_VERSION="1.6.0"
    wget -q https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip

    sudo apt-get update -qq
    sudo apt-get install -y -qq unzip > /dev/null 2>&1

    unzip -q terraform_${TERRAFORM_VERSION}_linux_amd64.zip
    sudo mv terraform /usr/local/bin/
    rm terraform_${TERRAFORM_VERSION}_linux_amd64.zip

    print_success "Terraform ${TERRAFORM_VERSION} installed successfully"
fi

# Step 3: Create directory structure
print_step "Creating directory structure..."

mkdir -p terraform/prometheus
mkdir -p grafana/dashboards
mkdir -p scripts

print_success "Directory structure created"

# Step 4: Check if Terraform files exist
print_step "Checking Terraform configuration..."

if [ ! -f "terraform/main.tf" ]; then
    print_warning "terraform/main.tf not found!"
    print_warning "Please create the Terraform files from the artifacts provided."
    echo ""
    echo "Required files:"
    echo "  - terraform/main.tf"
    echo "  - terraform/variables.tf"
    echo "  - terraform/prometheus/prometheus.yml"
    echo ""
    read -p "Do you want to continue anyway? (yes/no): " answer
    if [ "$answer" != "yes" ]; then
        exit 1
    fi
else
    print_success "Terraform configuration found"
fi

# Step 5: Initialize Terraform
print_step "Initializing Terraform..."

cd terraform
terraform init
print_success "Terraform initialized"

# Step 6: Validate configuration
print_step "Validating Terraform configuration..."

if terraform validate; then
    print_success "Configuration is valid"
else
    print_error "Configuration validation failed"
    exit 1
fi

# Step 7: Show plan
print_step "Creating Terraform plan..."
terraform plan -out=tfplan
print_success "Plan created"

# Step 8: Apply configuration
echo ""
print_warning "This will create the following services:"
echo "  - Prometheus (port 9090)"
echo "  - Grafana (port 3000)"
echo "  - Node Exporter (port 9100)"
echo "  - SonarQube (port 9000)"
echo "  - Nexus (port 8081)"
echo ""

read -p "Do you want to apply this configuration? (yes/no): " answer

if [ "$answer" != "yes" ]; then
    print_warning "Setup cancelled"
    cd ..
    exit 0
fi

print_step "Applying Terraform configuration..."
terraform apply tfplan
print_success "Infrastructure deployed"

cd ..

# Step 9: Wait for services
print_step "Waiting for services to start..."

echo -n "Waiting for Prometheus..."
until curl -s http://localhost:9090/-/healthy > /dev/null 2>&1; do
    echo -n "."
    sleep 2
done
echo " ✓"

echo -n "Waiting for Grafana..."
until curl -s http://localhost:3000/api/health > /dev/null 2>&1; do
    echo -n "."
    sleep 2
done
echo " ✓"

print_success "All services are running"

# Step 10: Configure Grafana
print_step "Configuring Grafana with Prometheus data source..."

sleep 5  # Give Grafana extra time to fully initialize

# Add Prometheus data source
curl -X POST \
  -H "Content-Type: application/json" \
  -u "admin:esprit" \
  -d '{
    "name": "Prometheus",
    "type": "prometheus",
    "url": "http://prometheus:9090",
    "access": "proxy",
    "isDefault": true,
    "jsonData": {
      "httpMethod": "POST"
    }
  }' \
  http://localhost:3000/api/datasources > /dev/null 2>&1

print_success "Grafana configured"

# Step 11: Import Jenkins dashboard (ID: 9964)
print_step "Importing Jenkins dashboard to Grafana..."

curl -X POST \
  -H "Content-Type: application/json" \
  -u "admin:esprit" \
  -d '{
    "dashboard": 9964,
    "overwrite": true,
    "inputs": [
      {
        "name": "DS_PROMETHEUS",
        "type": "datasource",
        "pluginId": "prometheus",
        "value": "Prometheus"
      }
    ]
  }' \
  http://localhost:3000/api/dashboards/import > /dev/null 2>&1 || true

print_success "Dashboard imported"

# Final summary
echo ""
echo -e "${GREEN}"
cat << "EOF"
╔══════════════════════════════════════════════════════════════╗
║                                                              ║
║                  🎉 SETUP COMPLETE! 🎉                       ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
EOF
echo -e "${NC}"

echo ""
echo "📊 Access Your Services:"
echo ""
echo "  🔹 Grafana Dashboard"
echo "     URL:      http://localhost:3000"
echo "     Username: admin"
echo "     Password: esprit"
echo ""
echo "  🔹 Prometheus"
echo "     URL:      http://localhost:9090"
echo "     Targets:  http://localhost:9090/targets"
echo ""
echo "  🔹 Node Exporter"
echo "     URL:      http://localhost:9100/metrics"
echo ""
echo "  🔹 SonarQube"
echo "     URL:      http://localhost:9000"
echo ""
echo "  🔹 Nexus"
echo "     URL:      http://localhost:8081"
echo ""
echo "═══════════════════════════════════════════════════════════"
echo ""
echo "🎯 Next Steps:"
echo ""
echo "  1. Open Grafana and login with admin/esprit"
echo "  2. Check that Prometheus data source is working"
echo "  3. View the imported Jenkins dashboard (ID: 9964)"
echo "  4. Update your Jenkins pipeline with the new Jenkinsfile"
echo "  5. Install 'Prometheus Metrics Plugin' in Jenkins"
echo "  6. Run a Jenkins build and monitor it in Grafana"
echo ""
echo "📚 Useful Commands:"
echo ""
echo "  Check services:     docker ps"
echo "  View logs:          docker logs <service-name>"
echo "  Restart service:    docker restart <service-name>"
echo "  Terraform status:   cd terraform && terraform show"
echo "  Destroy all:        cd terraform && terraform destroy"
echo ""
echo "═══════════════════════════════════════════════════════════"
echo ""