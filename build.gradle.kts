plugins {
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.resource.factory)
    alias(libs.plugins.run.paper)
}

group = "com.uravgcode"
version = "1.7.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.api)
}

paperPluginYaml {
    main = "com.uravgcode.survivalunlocked.SurvivalUnlocked"
    bootstrapper = "com.uravgcode.survivalunlocked.SurvivalUnlockedBootstrap"
    foliaSupported = true
    apiVersion = "26.2"

    name = "survival-unlocked"
    description = "vanilla friendly survival gameplay improvements"
    website = "https://uravgcode.com"
    authors.add("UrAvgCode")
}

runPaper {
    folia.registerTask()
}

tasks {
    processResources {
        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("config.yml") {
            expand(props)
        }
    }

    runServer {
        minecraftVersion("26.2")
    }
}
