package com.parabank.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Classe executora (Test Runner) central para a suíte de testes do Cucumber.
 * Utiliza o JUnit Platform Suite (JUnit 5) para orquestrar a execução das 
 * especificações Gherkin (.feature) e o mapeamento dos cenários em Java.
 */
@Suite
@IncludeEngines("cucumber") // Habilita e direciona a execução para a engine nativa do Cucumber no JUnit 5
@SelectClasspathResource("features") // Aponta o diretório raiz de recursos no classpath que contém os cenários

// Define o diretório físico no projeto onde estão localizados os arquivos .feature
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources/features")

// Mapeia os pacotes Java onde o Cucumber deve buscar as Step Definitions e as classes de Hooks (@Before/@After)
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.parabank.steps, com.parabank.hooks")

// Configura os plugins de saída: terminal formatado (pretty) e geração automática de relatórios em HTML e JSON
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME, 
    value = "pretty, html:target/cucumber-reports.html, json:target/cucumber.json"
)
public class TestRunner {
    /*
     * A classe é mantida intencionalmente vazia.
     * Ela atua apenas como ponto de entrada estrutural para que a suíte seja 
     * interpretada pela IDE ou pela pipeline de CI/CD (ex: Maven/Gradle).
     */
}