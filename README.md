# FlowFX (SOAP-only)

This repository contains the FlowFX server-side SOAP web service artifacts and supporting configuration. The project is focused on a WSDL-first JAX-WS SOAP service implemented for Jakarta EE 10 (Payara 6) with persistence backed by Oracle XE.

This README is intentionally SOAP-only: it documents the WSDL-first workflow, JAX-WS generation, Payara deployment, Oracle XE persistence setup, and how to run/verify service tests.

## What's in this module

- WSDLs and schemas: `src/main/wsdl/` (server contract)
- Service implementations and endpoints: `src/main/java/cr/ac/una/flowfx/ws` and related packages
- JPA entities and persistence wiring: `src/main/java/.../model` and `ora-db/` DDL scripts
- Build output and generated client/server stubs: `target/generated-sources/jaxws/` after a build

## Prerequisites

- JDK 17+
- Maven 3.6+
- Payara Server 6 (or compatible Jakarta EE 10 runtime)
- Oracle XE 18c/21c (XEPDB1) or an equivalent Oracle-compatible database
- Oracle JDBC driver (ojdbc8/ojdbc11) — obtain from Oracle and install into Payara (not distributed in repo)

## WSDL-first workflow (recommended)

1. Keep the WSDL(s) authoritative in `src/main/wsdl/` and treat them as the contract.
2. Generate server skeletons and client stubs using `wsimport` (for clients) or `wsgen`/jaxws-maven-plugin during the Maven build.
3. Implement the business logic in the generated server-side classes or in delegated service classes. Keep generated classes separated (do not edit generated files directly).
4. Package and deploy the WAR on Payara; Payara will publish the SOAP endpoints automatically when `@WebService` annotated classes are present.

Example Maven wsimport plugin to generate client stubs (place inside the module `pom.xml`):

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>jaxws-maven-plugin</artifactId>
  <version>2.6</version>
  <executions>
    <execution>
      <id>generate-ws-client</id>
      <goals>
        <goal>wsimport</goal>
      </goals>
      <configuration>
        <wsdlUrls>
          <wsdlUrl>src/main/wsdl/FlowFXWS.wsdl</wsdlUrl>
        </wsdlUrls>
        <packageName>cr.ac.una.flowfx.ws.client</packageName>
        <keep>true</keep>
        <sourceDestDir>${project.build.directory}/generated-sources/jaxws</sourceDestDir>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Server-side generation (if you prefer to generate skeletons) can be done with `wsimport` (with `-server` style options) or use `wsgen` paired with your `@WebService` annotated endpoint implementations.

Notes:
- Use `<keep>true</keep>` to preserve generated sources for inspection. Do not edit generated files; delegate to service classes.
- Set package names and targetNamespace carefully to match your WSDL.

## Building

From the module root (where `pom.xml` is located):

```bash
mvn clean package
```

- After a successful build, check `target/generated-sources/jaxws/` for generated artifacts and `target/*.war` for deployable archives.

## Payara 6 deployment and Oracle XE setup

1) Install Oracle JDBC driver

Copy the driver JAR (`ojdbc8.jar` or `ojdbc11.jar`) to the domain libraries of your Payara installation (e.g. `<PAYARA>/glassfish/domains/domain1/lib/`) and restart Payara.

2) Create a JDBC connection pool and JDBC resource

Run `asadmin` commands (from Payara's `bin` directory). Replace DB credentials and host values.

```bash
asadmin create-jdbc-connection-pool \
  --datasourceclassname=oracle.jdbc.pool.OracleDataSource \
  --restype=javax.sql.DataSource \
  --property user=FLOWFX_USER:password=FLOWFX_PASS:url=jdbc:oracle:thin:@//DB_HOST:1521/XEPDB1 \
  FlowFXPool

asadmin create-jdbc-resource --connectionpoolid FlowFXPool jdbc/FlowFXDS
```

Recommended pool tuning properties example:

```bash
--property InitialLimit=5:MaxLimit=50:ConnectionValidationMethod=table
```

3) `persistence.xml`

Ensure the module's `persistence.xml` (under `META-INF/`) uses the JTA datasource name created above:

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.0">
  <persistence-unit name="FlowFXPU" transaction-type="JTA">
    <jta-data-source>jdbc/FlowFXDS</jta-data-source>
    <properties>
      <property name="jakarta.persistence.schema-generation.database.action" value="none"/>
    </properties>
  </persistence-unit>
</persistence>
```

4) Deploy the WAR

Copy the generated WAR to Payara or deploy via `asadmin deploy target/your-war.war`. Confirm the endpoint address in server logs or the admin console (typically under `http://<host>:8080/<context>/services` or similar depending on configuration).

## Testing SOAP services

- Unit tests: test small service logic with JUnit 5 and Mockito.
- Integration tests: run against a deployed Payara instance. Use Arquillian with Payara managed adapters for true in-container tests, or start a local Payara instance and run client tests against it.
- A minimal client test using generated stubs:

```java
FlowFXService service = new FlowFXService();
FlowFX port = service.getFlowFXPort();
((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/FlowFXWS/FlowFXService");
ResponseType resp = port.someOperation(request);
assertNotNull(resp);
```

Tip: set timeouts on the client BindingProvider properties and handle `SOAPFaultException` explicitly in tests.

## Troubleshooting

- JDBC ClassNotFound: verify `ojdbc*.jar` is in Payara domain libs and server restarted.
- Endpoint not published: ensure `@WebService` is present and no conflicting JAX-WS implementations are bundled.
- WSDL mismatch: the WSDL targetNamespace and service/port names must match the `@WebService` annotations or your generated artifacts; regenerate stubs if you change WSDL.

## Testing files

This module contains a minimal SOAP client test (check `src/test/java`) that uses generated stubs to call the deployed endpoint. Modify the test endpoint address to match your local Payara instance before running integration tests.
