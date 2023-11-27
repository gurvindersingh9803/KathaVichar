package com.example.kathavichar.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.kathavichar.R

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

// Set of Material typography styles to start with
val Typography = Typography(
    defaultFontFamily = CustomFont,
    h1 = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Thin,
        fontSize = 16.sp,
    ),
    body1 = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
    ),
    body2 = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Thin,
        fontSize = 10.sp,
    ),
    button = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
    ),
    subtitle1 = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Thin,
        fontSize = 16.sp,
    ),
)
