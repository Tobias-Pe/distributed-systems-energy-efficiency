FROM grafana/grafana:10.0.12

COPY ./docker/grafana/datasource.yml /etc/grafana/provisioning/datasources
COPY ./docker/grafana/dashboard.yml /etc/grafana/provisioning/dashboards
COPY ./docker/grafana/dashboards /etc/grafana/dashboards