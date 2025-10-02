# Estágio 1: Build (Compilação)
# Usa a imagem oficial do Maven com o JDK 21 (TAG CORRIGIDA)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia os arquivos de configuração do projeto
COPY pom.xml .
COPY src ./src

# Roda o comando de build do Maven
RUN mvn clean package -DskipTests


# Estágio 2: Runtime (Execução)
# Usa uma imagem leve (apenas o que é necessário para rodar o JAR)
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Copia o JAR do Estágio 1 (o build) para o Estágio 2 (o runtime)
COPY --from=build /app/target/*.jar app.jar

# Configurações finais
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]