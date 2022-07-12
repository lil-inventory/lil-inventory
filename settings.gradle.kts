rootProject.name = "lil-inventory"
include("app:inventory")
include("app:auth")
include("app:common")
include("app:email")

enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {

            // MyBatis
            alias("mybatis.mybatis").to("org.mybatis:mybatis:3.5.10")
            alias("mybatis.springBootStarter").to("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.2")
            bundle("mybatis", listOf(
                "mybatis.mybatis",
                "mybatis.springBootStarter"
            ))

            // MySQL
            alias("mysql.connector").to("mysql:mysql-connector-java:8.0.29")

            // SpringDoc
            alias("springdoc.openApiUi").to("org.springdoc:springdoc-openapi-ui:1.6.8")
            alias("springdoc.openApiDataRest").to("org.springdoc:springdoc-openapi-data-rest:1.6.8")
            alias("springdoc.openApiKotlin").to("org.springdoc:springdoc-openapi-kotlin:1.6.8")
            bundle("springdoc", listOf(
                "springdoc.openApiUi",
                "springdoc.openApiDataRest",
                "springdoc.openApiKotlin"
            ))

            // Mail
            alias("javax.mail").to("javax.mail:mail:1.4.7")

            // Freemarker
            alias("freemarker.freemarker").to("org.freemarker:freemarker:2.3.31")

            // JJWT
            alias("jsonwebtoken.jjwt").to("io.jsonwebtoken:jjwt:0.9.1")
        }
    }
}
