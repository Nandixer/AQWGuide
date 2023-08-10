package ml.nandixer.aqwguide.domain.model

data class CombatClass(
    val abbr: String,
    val tags: List<String>,
    val enhancements: List<Enhancement>,
    val names: List<String>,
    val ratings: Ratings
)