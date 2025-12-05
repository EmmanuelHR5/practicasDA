package com.example.practicas.utils

fun cleanTitle(input: String): String =
    input.replace(Regex("\\(.*?\\)"), "")
        .replace(Regex("- .*"), "")
        .replace(Regex("feat\\..*", RegexOption.IGNORE_CASE), "")
        .replace(Regex("ft\\..*", RegexOption.IGNORE_CASE), "")
        .replace(Regex("x .*", RegexOption.IGNORE_CASE), "")
        .trim()

fun cleanArtist(input: String): String =
    input.replace(",", "")
        .replace("&", "")
        .replace("x", "")
        .trim()
