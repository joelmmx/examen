package com.example.examen

class Producto(val title:String,val imageUrl:String,val primaryOffer:PrimaryOffer) {

    override fun toString(): String {
        return "Producto(title='$title', imageUrl='$imageUrl', primaryOffer=$primaryOffer)"
    }
}