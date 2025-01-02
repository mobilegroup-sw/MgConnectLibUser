pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BTCommandSender"
include(":app",":MgConnectLibUser", ":MgConnectLibUser:core", ":MgConnectLibUser:mgbluetooth",":MgConnectLibUser:mgnetwork")


