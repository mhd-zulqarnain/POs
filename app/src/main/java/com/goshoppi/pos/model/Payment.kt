package com.goshoppi.pos.model

    enum class Payment {
        CREDIT, CASH, PARTIAL;

        override fun toString(): String {
            when (this) {
                CREDIT -> return "credit"
                CASH -> return "cash"
                PARTIAL -> return "partial"
            }
            return ""
        }
    }
