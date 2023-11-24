package com.example.kathavichar.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.kathavichar.R

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = CustomFontStyle.CustomFont,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
    ),
)

object CustomFontStyle {
    val CustomFont = FontFamily(
        Font(R.font.robotoblack),
        Font(R.font.robotoblackitalic),
        Font(R.font.robotobold),
        Font(R.font.robotolight),
        Font(R.font.robotomedium),
        Font(R.font.robotoregular),
        Font(R.font.robotothin),
        Font(R.font.robotothinitalic),
        Font(R.font.robotomediumitalic),
        Font(R.font.robotobolditalic),
        Font(R.font.robotolightitalic),
        Font(R.font.robotoitalic),

    )
}
