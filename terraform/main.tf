terraform {
  required_version = ">= 1.0"

  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "docker" {
  host = "unix:///var/run/docker.sock"
}

# Docker Network for all services
resource "docker_network" "devops_network" {
  name   = "devops-network"
  driver = "bridge"
}

# ============================================
# PROMETHEUS - Monitoring & Metrics Collection
# ============================================

resource "docker_image" "prometheus" {
  name = "prom/prometheus:latest"
}

resource "docker_volume" "prometheus_data" {
  name = "prometheus-data"
}

resource "docker_container" "prometheus" {
  name  = "prometheus"
  image = docker_image.prometheus.image_id

  restart = "unless-stopped"

  ports {
    internal = 9090
    external = 9090
  }

  # Mount the whole directory instead of a single file
  volumes {
    host_path      = abspath("${path.module}/prometheus")
    container_path = "/etc/prometheus"
    read_only      = true
  }

  volumes {
    volume_name    = docker_volume.prometheus_data.name
    container_path = "/prometheus"
  }

  networks_advanced {
    name = docker_network.devops_network.name
  }

  command = [
    "--config.file=/etc/prometheus/prometheus.yml",
    "--storage.tsdb.path=/prometheus",
    "--web.console.libraries=/usr/share/prometheus/console_libraries",
    "--web.console.templates=/usr/share/prometheus/consoles",
    "--web.enable-lifecycle"
  ]
}


# ============================================
# GRAFANA - Dashboards & Visualization
# ============================================

resource "docker_image" "grafana" {
  name = "grafana/grafana:latest"
}

resource "docker_volume" "grafana_data" {
  name = "grafana-data"
}

resource "docker_container" "grafana" {
  name  = "grafana"
  image = docker_image.grafana.image_id

  restart = "unless-stopped"

  ports {
    internal = 3000
    external = 3000
  }

  volumes {
    volume_name    = docker_volume.grafana_data.name
    container_path = "/var/lib/grafana"
  }

  networks_advanced {
    name = docker_network.devops_network.name
  }

  env = [
    "GF_SECURITY_ADMIN_USER=admin",
    "GF_SECURITY_ADMIN_PASSWORD=esprit",
    "GF_INSTALL_PLUGINS=grafana-piechart-panel",
    "GF_USERS_ALLOW_SIGN_UP=false"
  ]
}

# ============================================
# NODE EXPORTER - System Metrics
# ============================================

resource "docker_image" "node_exporter" {
  name = "prom/node-exporter:latest"
}

resource "docker_container" "node_exporter" {
  name  = "node-exporter"
  image = docker_image.node_exporter.image_id

  restart = "unless-stopped"

  ports {
    internal = 9100
    external = 9100
  }

  networks_advanced {
    name = docker_network.devops_network.name
  }

  volumes {
    host_path      = "/proc"
    container_path = "/host/proc"
    read_only      = true
  }

  volumes {
    host_path      = "/sys"
    container_path = "/host/sys"
    read_only      = true
  }

  volumes {
    host_path      = "/"
    container_path = "/rootfs"
    read_only      = true
  }

  command = [
    "--path.procfs=/host/proc",
    "--path.sysfs=/host/sys",
    "--collector.filesystem.mount-points-exclude=^/(sys|proc|dev|host|etc)($$|/)"
  ]
}

# ============================================
# SONARQUBE - Already exists in your setup
# ============================================

resource "docker_image" "sonarqube" {
  name = "sonarqube:latest"
}

resource "docker_volume" "sonarqube_data" {
  name = "sonarqube-data"
}

resource "docker_volume" "sonarqube_logs" {
  name = "sonarqube-logs"
}

resource "docker_volume" "sonarqube_extensions" {
  name = "sonarqube-extensions"
}

resource "docker_container" "sonarqube" {
  name  = "sonarqube"
  image = docker_image.sonarqube.image_id

  restart = "unless-stopped"

  ports {
    internal = 9000
    external = 9000
  }

  volumes {
    volume_name    = docker_volume.sonarqube_data.name
    container_path = "/opt/sonarqube/data"
  }

  volumes {
    volume_name    = docker_volume.sonarqube_logs.name
    container_path = "/opt/sonarqube/logs"
  }

  volumes {
    volume_name    = docker_volume.sonarqube_extensions.name
    container_path = "/opt/sonarqube/extensions"
  }

  networks_advanced {
    name = docker_network.devops_network.name
  }

  env = [
    "SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true"
  ]
}

# ============================================
# MYSQL DATABASE - For Application & Tests
# ============================================

resource "docker_image" "mysql" {
  name = "mysql:8.0"
}

resource "docker_volume" "mysql_data" {
  name = "mysql-data"
}

resource "docker_container" "mysql" {
  name  = "mysql-db"
  image = docker_image.mysql.image_id

  restart = "unless-stopped"

  ports {
    internal = 3306
    external = 3306
  }

  volumes {
    volume_name    = docker_volume.mysql_data.name
    container_path = "/var/lib/mysql"
  }

  networks_advanced {
    name = docker_network.devops_network.name
    aliases = ["mysql-db", "host.docker.internal"]
  }

  env = [
    "MYSQL_ROOT_PASSWORD=root",
    "MYSQL_DATABASE=gestionevenement",
    "MYSQL_USER=esprit",
    "MYSQL_PASSWORD=esprit"
  ]

  # Wait for MySQL to be ready
  healthcheck {
    test     = ["CMD", "mysqladmin", "ping", "-h", "localhost", "-uroot", "-proot"]
    interval = "10s"
    timeout  = "5s"
    retries  = 5
  }
}

# ============================================
# NEXUS - Already exists in your setup
# ============================================

resource "docker_image" "nexus" {
  name = "sonatype/nexus3:latest"
}

resource "docker_volume" "nexus_data" {
  name = "nexus-data"
}

resource "docker_container" "nexus" {
  name  = "nexus"
  image = docker_image.nexus.image_id

  restart = "unless-stopped"

  ports {
    internal = 8081
    external = 8081
  }

  volumes {
    volume_name    = docker_volume.nexus_data.name
    container_path = "/nexus-data"
  }

  networks_advanced {
    name = docker_network.devops_network.name
  }
}

# ============================================
# OUTPUTS
# ============================================

output "services_info" {
  value = {
    prometheus_url = "http://localhost:9090"
    grafana_url    = "http://localhost:3000"
    grafana_user   = "admin"
    grafana_pass   = "esprit"
    node_exporter  = "http://localhost:9100"
    sonarqube_url  = "http://localhost:9000"
    nexus_url      = "http://localhost:8081"
    mysql_url      = "jdbc:mysql://localhost:3306/gestionevenement"
    mysql_user     = "root"
    mysql_pass     = "root"
    app_url        = "http://localhost:8082/GestionEvenement"
  }
  description = "Access URLs for all services"
  sensitive   = true
}