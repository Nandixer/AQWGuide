package ml.nandixer.aqwguide.domain.model

data class AppVersion(
    val breaking: Boolean,
    val changelog: String,
    val number: Int
)