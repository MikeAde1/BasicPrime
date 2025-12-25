package com.example.basicprime.prime

val Int.isPrime: Boolean
    get() {
        if (this <= 1) return false
        if (this <= 3) return true
        if (this % 2 == 0 || this % 3 == 0) return false

        var i = 5
        while (i * i <= this) {
            if (this % i == 0 || this % (i + 2) == 0) return false
            i += 6
        }

        return true
    }