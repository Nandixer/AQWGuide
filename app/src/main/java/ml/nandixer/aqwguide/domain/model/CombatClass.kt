package ml.nandixer.aqwguide.domain.model

data class CombatClass(
    val abbr: String,
    val tags: List<String>,
    val enhancements: List<Enhancement>,
    val names: List<String>,
    val ratings: Ratings,
    val combo: List<String>,
    val best: String?,
    val description: String?,
    val video: String?,
    val videos: List<String>?,
    val icons: SpellIconSet?
){
    fun maxPerformance(): Dps {

        return Dps(

            classhall = enhancements.mapNotNull { it.dps.classhall }.maxOrNull(),
            classhallNsod = enhancements.mapNotNull { it.dps.classhallNsod }.maxOrNull(),
            revenant = enhancements.mapNotNull { it.dps.revenant }.maxOrNull(),
            revenantNsod = enhancements.mapNotNull { it.dps.revenantNsod }.maxOrNull(),
            icestorm = enhancements.mapNotNull { it.dps.icestorm }.maxOrNull(),
            icestormNsod = enhancements.mapNotNull { it.dps.icestormNsod }.maxOrNull(),

            )
    }
}
