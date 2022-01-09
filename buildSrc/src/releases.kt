const val RELEASE_GROUP = "com.hendraanggrian"
const val RELEASE_ARTIFACT = "generating-gradle-plugin"
const val RELEASE_VERSION = "0.2"
const val RELEASE_DESCRIPTION = "BuildConfig and R class generator for non-Android projects"
const val RELEASE_GITHUB = "https://github.com/hendraanggrian/$RELEASE_ARTIFACT"

fun getGithubRemoteUrl(artifact: String = RELEASE_ARTIFACT) =
    `java.net`.URL("$RELEASE_GITHUB/tree/main/$artifact/src")