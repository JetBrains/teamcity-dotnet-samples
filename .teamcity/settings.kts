import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.*
import java.util.*

// TeamCity Kotlin DSL version
version = "2020.1"

// Build settings
open class Settings {
    companion object {
        const val configuration = "Release"

        const val gitRepo = "https://github.com/JetBrains/teamcity-dotnet-samples.git"
        const val gitBranch = "refs/heads/master"

        // You can use versionPrefix to set the "base" version number for your library/app. https://andrewlock.net/version-vs-versionsuffix-vs-packageversion-what-do-they-all-mean/#versionprefix
        const val versionPrefix = "1.0.0"
        // Is used to set the pre-release label of the version number, if there is one, such as alpha or beta. https://andrewlock.net/version-vs-versionsuffix-vs-packageversion-what-do-they-all-mean/#versionsuffix
        const val versionSuffix = "beta%build.number%"

        // Cross platform version of dotCover to gather a code coverage statistics. This version should be installed on TeamCity server.
        const val dotCoverVersion = "2020.2.2"

        // TeamCity docker hub connection identifier. https://www.jetbrains.com/help/teamcity/integrating-teamcity-with-docker.html#Docker+Connection+for+a+Project
        const val dockerRegistryId = "PROJECT_EXT_728"
        // Docker hub https://hub.docker.com/u/nikolayp/
        const val dockerTargetRepo = "nikolayp"

        // https://docs.microsoft.com/en-us/nuget/nuget-org/publish-a-package#command-line
        const val nugetServer = "https://teamcity.jetbrains.com/httpAuth/app/nuget/feed/DemoProjects_TeamCity/clock/v3/index.json"
        const val nugetApiKey = "%teamcity.nuget.feed.api.key%"
    }
}

project {
    vcsRoot(SamplesRepo)
    subProject(BuildingProject)
    subProject(DeploymentProject)
    buildType(Build)
    buildType(Deploy)
}

object BuildingProject : Project({
    name = "Building"
    buildType(LinuxTests)
    buildType(WindowsTests)
    buildType(BuildConsoleAndWebLinux64)
    buildType(BuildConsoleAndWebWindows64)
    buildType(BuildDesktop)
    buildType(BuildAndroid)
    buildType(Pack)
})

object DeploymentProject : Project({
    name = "Deployment"
    buildType(PushConsoleUbuntu2004)
    buildType(PushConsoleWindows2004)
    buildType(PushConsoleMultiArch)
    buildType(PushWebUbuntu2004)
    buildType(PushWebWindows2004)
    buildType(PushWebMultiArch)
    buildType(PublishToNuget)
})

object SamplesRepo : GitVcsRoot({
    name = "TeamCity .NET samples"
    url = Settings.gitRepo
    branch = Settings.gitBranch
})

// Base configuration for builds and tests
open class BuildBase(
        // True to add agents requirements
        requiresSdk: Boolean)
    : BuildType() {
    constructor(requiresSdk: Boolean, init: BuildBase.() -> Unit): this(requiresSdk) {
        init()
    }
    init {
        vcs { root(SamplesRepo) }
        features {
            // Clear the checkout directory before building
            swabra {
                forceCleanCheckout = true
            }
        }
        if (requiresSdk) {
            // Agents requirement to have .NET Core SDK 3.1.402, also see global.json
            requirements {
                exists("DotNetCoreSDK3.1.402_Path")
            }
        }
        // Customize base build details
        params {
            // The equivalent of /p:configuration={configuration} for all build steps
            param("system.configuration", Settings.configuration)

            // The equivalent of /p:VersionPrefix=...
            param("system.VersionPrefix", Settings.versionPrefix)

            // The equivalent of /p:VersionSuffix=...
            param("system.VersionSuffix", Settings.versionSuffix)
        }
    }
}

// Base configuration for tests
open class TestBase(
        // A docker platform (windows or linux)
        platform: DotnetTestStep.ImagePlatform,
        // A docker image to use or null of docker is not required
        image: String? = null)
    : BuildBase(image.isNullOrBlank()) {
    init {
        name = "Test on ${platform.name}"
        steps {
            dotnetTest {
                name = "Test"
                projects = "Clock.Tests/Clock.Tests.csproj"
                coverage = dotcover {
                    // To add the assembly Clock for code coverage
                    assemblyFilters = "+:Clock"
                    // To exclude some classes marked by [ExcludeFromCodeCoverage] from code coverage
                    attributeFilters = "-:Clock.ExcludeFromCodeCoverageAttribute"
                    toolPath = "%teamcity.tool.JetBrains.dotCover.DotNetCliTool.${Settings.dotCoverVersion}%"
                }
                dockerImagePlatform = platform
                dockerImage = image
            }
        }
    }
}

// Base configuration to build cross-platform applications
open class BuildConsoleAndWebBase(
        // RID values are used to identify target platforms where the application runs.
        // https://docs.microsoft.com/en-us/dotnet/core/rid-catalog#using-rids
        runtimeId: String,
        // Test to run before building
        testBuildType: TestBase)
    : BuildBase(true) {
    init {
        name = "Build console and web for $runtimeId"
        dependencies {
            // Run tests before build
            snapshot(testBuildType) {
                onDependencyFailure = FailureAction.FAIL_TO_START
                onDependencyCancel = FailureAction.CANCEL
            }
        }
        params {
            // The equivalent of /p:PublishTrimmed=true https://docs.microsoft.com/en-us/dotnet/core/deploying/trim-self-contained#trim-your-app---cli
            param("system.PublishTrimmed", "true")

            // The equivalent of /p:PublishSingleFile=true https://docs.microsoft.com/en-us/dotnet/core/whats-new/dotnet-core-3-0#single-file-executables
            param("system.PublishSingleFile", "true")

            // The equivalent of /p:InvariantGlobalization=true
            // https://docs.microsoft.com/en-us/dotnet/core/run-time-config/globalization#invariant-mode
            // Determines whether a .NET app runs in globalization-invariant mode without access to culture-specific data and behavior. True - run in invariant mode.
            param("system.InvariantGlobalization", "true")
        }
        steps {
            dotnetPublish {
                name = "Build console app"
                projects = "Clock.Console/Clock.Console.csproj"
                runtime = runtimeId
                outputDir = "bin/Clock.Console/$runtimeId"
            }
            dotnetPublish {
                name = "Build web app"
                projects = "Clock.Web/Clock.Web.csproj"
                runtime = runtimeId
                outputDir = "bin/Clock.Web/$runtimeId"
            }
        }
        // Publish the content of bin directory as build artifacts
        artifactRules = "bin => bin"
    }
}

// Test on Linux
object LinuxTests: TestBase(DotnetTestStep.ImagePlatform.Linux, "mcr.microsoft.com/dotnet/core/sdk:3.1.402")

// Test on Windows
object WindowsTests: TestBase(DotnetTestStep.ImagePlatform.Windows)

// Build Linux applications
object BuildConsoleAndWebLinux64: BuildConsoleAndWebBase("linux-x64", LinuxTests)

// Build Windows applications
object BuildConsoleAndWebWindows64: BuildConsoleAndWebBase("win-x64", WindowsTests)

// Build Windows desktop applications
object BuildDesktop: BuildBase (true, {
    name = "Build Windows desktop"
    dependencies {
        // Run tests on Windows before build
        snapshot(WindowsTests) {
            onDependencyFailure = FailureAction.FAIL_TO_START
            onDependencyCancel = FailureAction.CANCEL
        }
    }
    // Set up the output paths to the "bin" directory
    params {
        // The equivalent of /p:PublishDir=../bin/Clock.Desktop/win/ to configure the default publish path for Desktop
        param("system.PublishDir", "../bin/Clock.Desktop/win/")
        // https://docs.microsoft.com/en-us/windows/uwp/packaging/auto-build-package-uwp-apps#configure-the-build-solution-build-task
        // The equivalent of /p:AppxPackageDir=../bin/Clock.Desktop.Uwp/win/ to configure the default publish path for Desktop.Uwp
        param("system.AppxPackageDir", "../bin/Clock.Desktop.Uwp/win/")
    }
    steps {
        dotnetMsBuild {
            name = "Build"
            projects =
                    "Clock.Desktop/Clock.Desktop.csproj" +
                    "\nClock.Desktop.Uwp/Clock.Desktop.Uwp.csproj"
            // MSBuild targets to execute
            targets = "Restore;Rebuild;Publish"
            // Use MSBuild for Windows
            version = DotnetMsBuildStep.MSBuildVersion.V16
        }
    }
    // Publish the content of bin directory as build artifacts
    artifactRules =
            "bin/Clock.Desktop/win/**/*.* => bin/Clock.Desktop.zip" +
            "\nbin/Clock.Desktop.Uwp/win/**/*.* => bin/Clock.Desktop.Uwp.zip"
})

// Configuration to build Android application
object BuildAndroid: BuildBase (true, {
    name = "Build Android app"
    dependencies {
        // Runs tests on Linux before build
        snapshot(LinuxTests) {
            onDependencyFailure = FailureAction.FAIL_TO_START
            onDependencyCancel = FailureAction.CANCEL
        }
    }
    steps {
        dotnetMsBuild {
            name = "Build"
            projects = "Clock.Xamarin.Android/Clock.Xamarin.Android.csproj"
            // https://docs.microsoft.com/en-us/xamarin/android/deploy-test/building-apps/build-process#build-targets
            targets ="Restore;Rebuild;SignAndroidPackage"
            version = DotnetMsBuildStep.MSBuildVersion.V16
        }
    }
    // Publish apk as artifact
    artifactRules="Clock.Xamarin.Android/bin/${Settings.configuration}/com.Clock-Signed.apk => bin/Clock.Android"
})

// Build nuget packages
object Pack: BuildBase (true, {
    name = "Pack"
    dependencies {
        listOf(WindowsTests, LinuxTests)
                .map {
                    dependency(it) {
                        snapshot { onDependencyFailure = FailureAction.FAIL_TO_START }
                    }
                }
    }
    params {
        // https://docs.microsoft.com/en-us/dotnet/core/tools/csproj
        param("system.Copyright", "Copyright © ${Calendar.getInstance().get(Calendar.YEAR)} JetBrains")
        param("system.Title", "TeamCity .NET sample")
        param("system.RepositoryType", "git")
        param("system.RepositoryUrl", Settings.gitRepo)
        param("system.RepositoryBranch", Settings.gitBranch)
    }
    steps {
        dotnetPack {
            projects =
                    "Clock/Clock.csproj" +
                    "\nClock.IoC/Clock.IoC.csproj"
        }
    }
    // Put nuget packages to the "packages" artifact directory
    artifactRules =
            "Clock/bin/${Settings.configuration}/*.nupkg => packages" +
            "\nClock.IoC/bin/${Settings.configuration}/*.nupkg => packages"
})

// Build all applications
object Build: BuildBase (false, {
    name = "Build"
    // Based on all build and pack artifacts
    dependencies {
        listOf(BuildConsoleAndWebLinux64, BuildConsoleAndWebWindows64, BuildDesktop, BuildAndroid, Pack)
                .map {
                    dependency(it) {
                        snapshot { onDependencyFailure = FailureAction.FAIL_TO_START }
                        artifacts { artifactRules = "**/*.* => ." }
                    }
                }
    }
    // Put artifacts to the "bin" artifact directory
    artifactRules =
            "bin/**/*.* => ." +
            "\npackages/**/*.* => ."
})

// Base configuration for docker
open class DockerBase(
        // The docker container platform (windows or linux)
        platform: String)
    : BuildType() {
    init {
        // Convert platform to OS name, for instance "windows" to "Windows"
        val dockerHostPlatform = String(platform.mapIndexed { index, c -> if (index == 0) c.toUpperCase() else c.toLowerCase() }.toCharArray())

        type=Type.DEPLOYMENT
        vcs { root(SamplesRepo) }
        features {
            dockerSupport {
                cleanupPushedImages = true
                loginToRegistry = on {
                    dockerRegistryId = Settings.dockerRegistryId
                }
            }
        }
        requirements {
            // Guaranteed to use the specified OS as the docker host
            startsWith("teamcity.agent.jvm.os.name", dockerHostPlatform)
        }
    }
}

// Base configuration to push docker images
open class PushImageBase(
        // Build to get the artifact used to build the docker image
        sourceBuild: BuildType,
        // The docker container platform (windows or linux)
        platform: String,
        // The dockerfile to build image, see the "context" directory
        dockerfile: String,
        // Base image name and tag passed as "baseImage" argument when creating docker image
        baseImage: String,
        // A target docker image name, for instance "clock-console"
        val targetName: String,
        // A target docker image tag, for instance "ubuntu.20.04"
        val targetTag: String)
    : DockerBase(platform) {
    init {
        val localImageName = "$targetName:$targetTag"
        val remoteImageName = "${Settings.dockerTargetRepo}/$targetName:$targetTag"

        name = "Push image $remoteImageName"
        dependencies {
            dependency(sourceBuild) {
                snapshot { onDependencyFailure = FailureAction.FAIL_TO_START }
                // Place the generated artifacts in the docker "context" directory
                artifacts { artifactRules = "bin => context" }
            }
        }
        steps {
            dockerCommand {
                name = "Pull or update $baseImage"
                commandType = other {
                    subCommand = "pull"
                    commandArgs = baseImage
                }
            }
            dockerCommand {
                name = "Build $localImageName"
                commandType = build {
                    source = file {
                        path = "context/$dockerfile"
                    }
                    // Configure the docker "context" directory
                    contextDir = "context"
                    namesAndTags = localImageName
                    param("dockerImage.platform", platform)
                    // See https://docs.docker.com/engine/reference/commandline/build/#set-build-time-variables---build-arg for details
                    commandArgs = "--build-arg baseImage=$baseImage"
                }
            }
            dockerCommand {
                name = "Re-tag $remoteImageName"
                commandType = other {
                    subCommand = "tag"
                    commandArgs = "$localImageName $remoteImageName"
                }
            }
            dockerCommand {
                name = "Push $remoteImageName to Docker Hub"
                commandType = push {
                    namesAndTags = remoteImageName
                }
            }
        }
    }
}

// Base configuration to push manifest
// https://www.docker.com/blog/multi-arch-build-and-images-the-simple-way/
open class PushMultiArchImageBase(
        sourceBuilds: List<PushImageBase>,
        // A target docker image name, for instance "clock-console"
        targetName: String,
        // A target docker image tag, for instance "latest"
        targetTag: String = "latest")
    : DockerBase("windows") {
    init {
        val images = sourceBuilds.joinToString(" ") { "${Settings.dockerTargetRepo}/${it.targetName}:${it.targetTag}" }
        val remoteImageName = "${Settings.dockerTargetRepo}/$targetName:$targetTag"
        name = "Push multi-arch image $remoteImageName"
        dependencies {
            sourceBuilds.map {
                dependency(it) {
                    snapshot { onDependencyFailure = FailureAction.FAIL_TO_START }
                }
            }
        }
        steps {
            script {
                name = "Clear manifests"
                scriptContent = "if exist \"%%USERPROFILE%%\\.docker\\manifests\\\" rmdir \"%%USERPROFILE%%\\.docker\\manifests\\\" /s /q"
            }
            dockerCommand {
                name = "Create manifest"
                commandType = other {
                    subCommand = "manifest"
                    commandArgs = "create $remoteImageName $images"
                }
            }
            dockerCommand {
                name = "Push manifest"
                commandType = other {
                    subCommand = "manifest"
                    commandArgs = "push $remoteImageName"
                }
            }
        }
    }
}

object PushConsoleUbuntu2004: PushImageBase(
        BuildConsoleAndWebLinux64,
        "linux",
        "console.linux.dockerfile",
        // This image contains the native dependencies needed by .NET Core. It does not include .NET Core. It is for self-contained applications.
        "mcr.microsoft.com/dotnet/core/runtime-deps:3.1-focal",
        "clock-console",
        "ubuntu.20.04")

object PushConsoleWindows2004: PushImageBase(
        BuildConsoleAndWebWindows64,
        "windows",
        "console.windows.dockerfile",
        "mcr.microsoft.com/windows/nanoserver:2004",
        "clock-console",
        "nanoserver.2004")

object PushConsoleMultiArch: PushMultiArchImageBase(
        listOf(PushConsoleUbuntu2004, PushConsoleWindows2004),
        "clock-console")

object PushWebUbuntu2004: PushImageBase(
        BuildConsoleAndWebLinux64,
        "linux",
        "web.linux.dockerfile",
        "mcr.microsoft.com/dotnet/core/runtime-deps:3.1-focal",
        "clock-web",
        "ubuntu.20.04")

object PushWebWindows2004: PushImageBase(
        BuildConsoleAndWebWindows64,
        "windows",
        "web.windows.dockerfile",
        "mcr.microsoft.com/windows/nanoserver:2004",
        "clock-web",
        "nanoserver.2004")

object PushWebMultiArch: PushMultiArchImageBase(
        listOf(PushWebUbuntu2004, PushWebWindows2004),
        "clock-web")

object PublishToNuget: BuildBase(true, {
    name = "Publish to NuGet"
    type = Type.DEPLOYMENT
    dependencies {
        // Run pack before publish
        dependency(Pack) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
                onDependencyCancel = FailureAction.CANCEL
            }

            artifacts { artifactRules = "packages => packages" }
        }
    }
    steps {
        dotnetNugetPush {
            packages = "packages/*.nupkg"
            serverUrl = Settings.nugetServer
            apiKey = Settings.nugetApiKey
        }
    }
})

// Root deploy configuration
object Deploy: BuildType ({
    name = "Deploy"
    type = Type.DEPLOYMENT
    // Deploy all
    dependencies {
        listOf(PushConsoleMultiArch, PushWebMultiArch, PublishToNuget)
                .map {
                    dependency(it) {
                        snapshot { onDependencyFailure = FailureAction.FAIL_TO_START }
                    }
                }
    }
})
