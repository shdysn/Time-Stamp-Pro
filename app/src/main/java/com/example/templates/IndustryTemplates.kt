package com.example.templates

import com.example.data.model.TemplateData

object ConstructionTemplate {
    fun get() = TemplateData.getById("construction")
}

object InspectionTemplate {
    fun get() = TemplateData.getById("inspection")
}

object SecurityTemplate {
    fun get() = TemplateData.getById("security")
}

object SurveyTemplate {
    fun get() = TemplateData.getById("survey")
}

object CustomTemplate {
    fun get() = TemplateData.getById("custom")
}
