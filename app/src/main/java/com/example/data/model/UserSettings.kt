package com.example.data.model

enum class StampPosition(val displayName: String) {
    BOTTOM_LEFT("Bottom Left"),
    BOTTOM_RIGHT("Bottom Right"),
    TOP_LEFT("Top Left"),
    TOP_RIGHT("Top Right"),
    BOTTOM_BANNER("Full Bottom Banner")
}

enum class CoordinateFormat(val displayName: String) {
    DECIMAL("Decimal (°DD.dddd)"),
    DMS("DMS (° ' \" N/S)"),
    UTM("UTM (Grid Zone)")
}

enum class AltitudeUnit(val displayName: String, val symbol: String) {
    METERS("Meters", "m"),
    FEET("Feet", "ft")
}

enum class StampFontSize(val displayName: String, val scale: Float) {
    SMALL("Small", 0.85f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.25f),
    EXTRA_LARGE("Extra Large", 1.5f)
}

enum class StampColor(val displayName: String, val colorHex: Long) {
    GOLD("Safety Gold", 0xFFFFC107),
    WHITE("Crisp White", 0xFFFFFFFF),
    CYBER_YELLOW("Vibrant Yellow", 0xFFFFEB3B),
    SAFETY_ORANGE("High-Vis Orange", 0xFFFF5722),
    NEON_GREEN("Survey Green", 0xFF00E676),
    ELECTRIC_CYAN("Data Cyan", 0xFF00E5FF)
}

data class UserSettings(
    val dateFormat: String = "yyyy-MM-dd HH:mm:ss",
    val coordinateFormat: CoordinateFormat = CoordinateFormat.DECIMAL,
    val altitudeUnit: AltitudeUnit = AltitudeUnit.METERS,
    val stampPosition: StampPosition = StampPosition.BOTTOM_LEFT,
    val textColorHex: Long = 0xFFFFC107, // Safety Gold default
    val backgroundOpacity: Float = 0.65f,
    val fontSize: StampFontSize = StampFontSize.MEDIUM,
    val showAddress: Boolean = true,
    val showCoordinates: Boolean = true,
    val showAltitude: Boolean = true,
    val showCompass: Boolean = true,
    val showTimestamp: Boolean = true,
    val showProjectBadge: Boolean = true,
    val projectName: String = "Apex Site Build #402",
    val inspectorName: String = "Engineer J. Davis",
    val customNotes: String = "Phase 2 Structural Inspection",
    val selectedTemplateId: String = "construction",
    val saveOriginalCopy: Boolean = true,
    val shutterSound: Boolean = true
)
