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
                badgeTitle = "SITE INSPECTION LOG",
                primaryColorHex = 0xFFFF9800,
                defaultProject = "Project Alpha - Tower B",
                defaultInspector = "Site Sup. R. Martinez",
                defaultNotes = "Foundation concrete pour check passed"
            ),
            TemplateData(
                id = "inspection",
                name = "Field Inspection & QA",
                category = "Quality Assurance",
                iconName = "verified",
                description = "For QA/QC audits, building diagnostics, code compliance photos.",
                badgeTitle = "QA COMPLIANCE AUDIT",
                primaryColorHex = 0xFF2196F3,
                defaultProject = "HVAC & Electrical Audit",
                defaultInspector = "Lead Auditor K. Smith",
                defaultNotes = "Conduit seals verified compliant"
            ),
            TemplateData(
                id = "security",
                name = "Security & Patrol",
                category = "Security",
                iconName = "security",
                description = "Proof of patrol rounds, checkpoint verification, incident documentation.",
                badgeTitle = "PATROL VERIFICATION",
                primaryColorHex = 0xFFE91E63,
                defaultProject = "Perimeter Checkpoint 04",
                defaultInspector = "Officer #8429",
                defaultNotes = "South entrance secure, gates locked"
            ),
            TemplateData(
                id = "survey",
                name = "Land Survey & Topo",
                category = "Geomatics",
                iconName = "terrain",
                description = "Boundary demarcation, GIS field tagging, elevation benchmark.",
                badgeTitle = "GEODETIC SURVEY BENCHMARK",
                primaryColorHex = 0xFF4CAF50,
                defaultProject = "Sector 14 Grid Topo",
                defaultInspector = "Surveyor M. Chen",
                defaultNotes = "Benchmark monument #BM-102"
            ),
            TemplateData(
                id = "delivery",
                name = "Logistics & Delivery",
                category = "Logistics",
                iconName = "local_shipping",
                description = "Proof of drop-off, freight condition check, package delivery confirmation.",
                badgeTitle = "PROOF OF DELIVERY",
                primaryColorHex = 0xFF9C27B0,
                defaultProject = "Waybill #WB-99201",
                defaultInspector = "Courier Unit 12",
                defaultNotes = "Left with building reception"
            ),
            TemplateData(
                id = "custom",
                name = "Custom Watermark",
                category = "General",
                iconName = "edit",
                description = "Freely customizable labels, notes, author tag and location stamps.",
                badgeTitle = "FIELD EVIDENCE RECORD",
                primaryColorHex = 0xFF00BCD4,
                defaultProject = "Field Operation",
                defaultInspector = "Operator",
                defaultNotes = "General field record"
            )
        )

        fun getById(id: String): TemplateData {
            return ALL_TEMPLATES.find { it.id == id } ?: ALL_TEMPLATES.first()
        }
    }
}
