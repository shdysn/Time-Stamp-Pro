package com.example.data.model

data class TemplateField(
    val key: String,
    val label: String,
    val value: String,
    val isEditable: Boolean = true
)

data class TemplateData(
    val id: String,
    val name: String,
    val category: String,
    val iconName: String,
    val description: String,
    val badgeTitle: String,
    val primaryColorHex: Long,
    val defaultProject: String,
    val defaultInspector: String,
    val defaultNotes: String,
    val fields: List<TemplateField> = emptyList()
) {
    companion object {
        val ALL_TEMPLATES = listOf(
            TemplateData(
                id = "construction",
                name = "Jobsite / Construction",
                category = "Engineering",
                iconName = "construction",
                description = "For site logs, structural verification, daily work progress reports.",
                badgeTitle = "",
                primaryColorHex = 0xFFFF9800,
                defaultProject = "",
                defaultInspector = "",
                defaultNotes = ""
            ),
            TemplateData(
                id = "inspection",
                name = "Field Inspection & QA",
                category = "Quality Assurance",
                iconName = "verified",
                description = "For QA/QC audits, building diagnostics, code compliance photos.",
                badgeTitle = "",
                primaryColorHex = 0xFF2196F3,
                defaultProject = "",
                defaultInspector = "",
                defaultNotes = ""
            ),
            TemplateData(
                id = "security",
                name = "Security & Patrol",
                category = "Security",
                iconName = "security",
                description = "Proof of patrol rounds, checkpoint verification, incident documentation.",
                badgeTitle = "",
                primaryColorHex = 0xFFE91E63,
                defaultProject = "",
                defaultInspector = "",
                defaultNotes = ""
            ),
            TemplateData(
                id = "survey",
                name = "Land Survey & Topo",
                category = "Geomatics",
                iconName = "terrain",
                description = "Boundary demarcation, GIS field tagging, elevation benchmark.",
                badgeTitle = "",
                primaryColorHex = 0xFF4CAF50,
                defaultProject = "",
                defaultInspector = "",
                defaultNotes = ""
            ),
            TemplateData(
                id = "delivery",
                name = "Logistics & Delivery",
                category = "Logistics",
                iconName = "local_shipping",
                description = "Proof of drop-off, freight condition check, package delivery confirmation.",
                badgeTitle = "",
                primaryColorHex = 0xFF9C27B0,
                defaultProject = "",
                defaultInspector = "",
                defaultNotes = ""
            ),
            TemplateData(
                id = "custom",
                name = "Custom Watermark",
                category = "General",
                iconName = "edit",
                description = "Freely customizable labels, notes, author tag and location stamps.",
                badgeTitle = "",
                primaryColorHex = 0xFF00BCD4,
                defaultProject = "",
                defaultInspector = "",
                defaultNotes = ""
            )
        )

        fun getById(id: String): TemplateData {
            return ALL_TEMPLATES.find { it.id == id } ?: ALL_TEMPLATES.first()
        }
    }
}
