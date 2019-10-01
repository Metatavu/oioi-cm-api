FROM jboss/wildfly:17.0.1.Final

ADD --chown=jboss target/*.war /opt/jboss/wildfly/standalone/deployments/app.war
ADD --chown=jboss ./docker/entrypoint.sh /opt/docker/entrypoint.sh 
ADD --chown=jboss ./docker/jboss-cli.properties /opt/docker/jboss-cli.properties
ADD --chown=jboss ./docker/host.cli /opt/docker/host.cli
ADD --chown=jboss ./docker/kubernets-jgroups.cli /opt/docker/kubernets-jgroups.cli
ADD --chown=jboss ./docker/jdbc.cli /opt/docker/jdbc.cli
ADD --chown=jboss ./docker/interfaces.cli /opt/docker/interfaces.cli
RUN chmod a+x /opt/docker/entrypoint.sh

ARG WILDFLY_VERSION=17.0.1.Final
ARG MYSQL_MODULE_VERSION=8.0.17
ARG KEYCLOAK_MODULE_VERSION=7.0.0

RUN curl -o /tmp/mysql-module.zip -L https://static.metatavu.io/wildfly/wildfly-${WILDFLY_VERSION}-mysql-module-${MYSQL_MODULE_VERSION}.zip
RUN curl -o /tmp/keycloak-module.zip -L https://downloads.jboss.org/keycloak/${KEYCLOAK_MODULE_VERSION}/adapters/keycloak-oidc/keycloak-wildfly-adapter-dist-${KEYCLOAK_MODULE_VERSION}.zip

RUN unzip -o /tmp/keycloak-module.zip -d /opt/jboss/wildfly/
RUN unzip -o /tmp/mysql-module.zip -d /opt/jboss/wildfly/

RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/host.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/jdbc.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/kubernets-jgroups.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/interfaces.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --properties=/opt/docker/jboss-cli.properties --file=/opt/jboss/wildfly/bin/adapter-elytron-install-offline.cli

RUN rm /tmp/*.zip

EXPOSE 8080

CMD "/opt/docker/entrypoint.sh"