package com.example.jarassignment.data.model

data class EducationResponse(
    val success: Boolean,
    val data: EducationDataContainer
)

data class EducationDataContainer(
    val manualBuyEducationData: ManualBuyEducationData
)

data class ManualBuyEducationData(
    val toolBarText: String?,
    val introTitle: String?,
    val introSubtitle: String?,
    val educationCardList: List<EducationCard> = emptyList(),
    val saveButtonCta: ButtonCta?,
    val ctaLottie: String?,
    val screenType: String?,
    val cohort: String?,
    val combination: String?, // Nullable
    val collapseCardTiltInterval: Int = 0,
    val collapseExpandIntroInterval: Int = 0,
    val bottomToCenterTranslationInterval: Int = 0,
    val expandCardStayInterval: Int = 0,
    val seenCount: Int? = null,
    val actionText: String?,
    val shouldShowOnLandingPage: Boolean = false,
    val toolBarIcon: String?,
    val introSubtitleIcon: String?,
    val shouldShowBeforeNavigating: Boolean = false
)

data class EducationCard(
    val image: String?,
    val collapsedStateText: String?,
    val expandStateText: String?,
    val backGroundColor: String?,
    val strokeStartColor: String?,
    val strokeEndColor: String?,
    val startGradient: String?,
    val endGradient: String?
)

data class ButtonCta(
    val text: String?,
    val deeplink: String?,
    val backgroundColor: String?,
    val textColor: String?,
    val strokeColor: String?,
    val icon: String?,
    val order: Int? = 0
)