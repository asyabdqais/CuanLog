package com.example.finrecapp.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.*

fun formatRupiah(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return format.format(amount).replace("Rp", "Rp ")
}

class ThousandSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val formattedText = StringBuilder()
        for (i in originalText.indices) {
            formattedText.append(originalText[i])
            val remainingDigits = originalText.length - 1 - i
            if (remainingDigits > 0 && remainingDigits % 3 == 0) {
                formattedText.append(".")
            }
        }

        val annotatedString = AnnotatedString(formattedText.toString())

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                var dotsBefore = 0
                for (i in 0 until offset) {
                    val remainingDigits = originalText.length - 1 - i
                    if (remainingDigits > 0 && remainingDigits % 3 == 0) {
                        dotsBefore++
                    }
                }
                return offset + dotsBefore
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                var dotsBefore = 0
                var transformedIndex = 0
                for (i in originalText.indices) {
                    transformedIndex++ 
                    if (transformedIndex > offset) break
                    val remainingDigits = originalText.length - 1 - i
                    if (remainingDigits > 0 && remainingDigits % 3 == 0) {
                        transformedIndex++ 
                        dotsBefore++
                        if (transformedIndex > offset) break
                    }
                }
                return offset - dotsBefore
            }
        }

        return TransformedText(annotatedString, offsetMapping)
    }
}
