package com.works.app.utils

class Validation {

    companion object {

        // Email kontrolü
        fun isValidEmail(email: String): Boolean {
            val emailPattern = Regex(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            )
            return emailPattern.matches(email)
        }

        // Password en az 6 karakter olmalı
        fun isValidPassword(password: String): Boolean {
            return password.length >= 6
        }

    }
}
