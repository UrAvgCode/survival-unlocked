plugins {
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.resource.factory)
    alias(libs.plugins.run.paper)
}

group = "com.uravgcode"
version = "1.5.4"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.api)
}

paperPluginYaml {
    main = "com.uravgcode.survivalunlocked.SurvivalUnlocked"
    bootstrapper = "com.uravgcode.survivalunlocked.SurvivalUnlockedBootstrap"
    foliaSupported = true
    apiVersion = "1.21.10"

    name = "survival-unlocked"
    description = "vanilla friendly survival gameplay improvements"
    website = "https://uravgcode.com"
    authors.add("UrAvgCode")
}

runPaper {
    folia.registerTask()
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    processResources {
        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("config.yml") {
            expand(props)
        }
    }

    runServer {
        minecraftVersion("1.21.10")
    }
}
