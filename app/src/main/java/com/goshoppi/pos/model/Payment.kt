package com.goshoppi.pos.model

enum class Payment {
    CREDIT, CASH, PARTIAL;

    override fun toString(): String {
        when (this) {
            CREDIT -> return "Dog"
            CASH -> return "Cat"
            PARTIAL -> return "Bird"
        }
        return ""
    }
}
