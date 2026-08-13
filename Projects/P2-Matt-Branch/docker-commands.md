### Employee app image build
docker build -f employee_app/Dockerfile -t employee-app .

### Manager app image build
docker build -t manager-app .

### Frontend app image build
docker build -t frontend . 2>&1 | tail -8

### Build jenkins docker compose
docker compose -f Jenkins/docker-compose.yml up -d --build

### Get jenkins admin password
docker exec jenkins-p1 cat /var/jenkins_home/secrets/initialAdminPassword

### Follow jenkins logs when restarting
docker logs -f jenkins-p1
