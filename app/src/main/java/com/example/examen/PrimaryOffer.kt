package com.example.examen

class PrimaryOffer(val listPrice:Integer,val offerPrice:Double,val maxPrice:Double,val minPrice:Double) {
    override fun toString(): String {
        return "PrimaryOffer(listPrice=$listPrice, offerPrice=$offerPrice, maxPrice=$maxPrice, minPrice=$minPrice)"
    }
}