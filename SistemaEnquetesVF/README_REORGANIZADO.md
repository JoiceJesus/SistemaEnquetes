# SistemaEnquetes - reorganized Eclipse/Tomcat 9 project

## Target stack
- Java 21
- Eclipse IDE for Enterprise Java and Web Developers
- Apache Tomcat 9 / Servlet 4.0 (`javax.servlet.*`)
- JSP 2.x
- MySQL
- Separate CSS and JavaScript assets

## Project layout
```
SistemaEnquetes/
├── .project / .classpath / .settings/
├── src/main/java/
│   ├── controller/
│   ├── dao/
│   ├── model/
│   └── util/
├── src/main/webapp/
│   ├── WEB-INF/web.xml
│   ├── assets/css/style.css
│   ├── assets/js/script.js
│   ├── assets/img/logo.png
│   ├── index.jsp
│   ├── usuario.jsp
│   ├── usuario_adm.jsp
│   ├── usuario_comum.jsp
│   ├── categoria.jsp
│   ├── categoria-form.jsp
│   ├── enquete.jsp
│   ├── enquete-form.jsp
│   └── opcao.jsp
├── database/01_create_database.sql
├── prototipos/           # old static HTML mockups, not deployed
├── docs/                 # project PDFs/XLSX
└── modelo/               # DER / model assets
```

## Important changes already made
1. Removed duplicate `bin/`, `.class`, and `module-info.java` artifacts from the deployable project.
2. Moved Java sources into `src/main/java` and web resources into `src/main/webapp`.
3. Moved CSS, JavaScript and image assets into `assets/`.
4. Changed Servlet imports from `jakarta.servlet.*` to `javax.servlet.*` because this project is targeted at Tomcat 9.
5. Added Eclipse WTP Dynamic Web Project metadata and `WEB-INF/web.xml`.
6. Added working authentication CRUD methods to `UsuarioDAO` and fixed the `UsuarioController` URL to `/usuario`.
7. Added missing JSPs expected by the controllers.
8. Normalized database identifiers to ASCII (`permissoes`, `descricao_opcao`) so Java/SQL naming matches.
9. Made primary identifiers auto-increment where the application generates IDs.
10. Added `data_cadastro` and `data_criacao` defaults at the database layer.
11. Added vote IP tracking and support for both single and multiple choice submissions at the data-model level.
12. Added automatic closing of expired polls.
13. Moved DB credentials to environment variables with local defaults.

## Eclipse / Tomcat setup
1. Install/configure JDK 21.
2. Configure Apache Tomcat 9.0 in Eclipse.
3. Import this folder as an existing Eclipse project.
4. If Eclipse does not recognize it as a Dynamic Web Project, right-click the project -> Properties -> Project Facets and ensure Dynamic Web Module 4.0 and Java 21 are enabled.
5. Add MySQL Connector/J to the project runtime. Connector/J is published as `com.mysql:mysql-connector-j`; the 8.4.0 artifact is known-good for MySQL 8+ and Java JDBC. See the official MySQL Maven installation documentation.
6. Execute `database/01_create_database.sql` in MySQL.
7. Start Tomcat and open `/SistemaEnquetes/`.

## Remaining feature-level work
The supplied specification goes beyond the current UI/backend and still requires, for a complete production-quality system:
- real-time result refresh (WebSocket/AJAX/polling),
- final and partial reports/graphs,
- search/filter by category,
- visual poll customization,
- password recovery workflow,
- complete admin user-management UI,
- stronger validation and authorization on every write endpoint,
- password hashing instead of plain-text storage,
- more complete handling of the configured IP-vote limit,
- improved JSP/view separation (avoid scriptlets as the project grows).
