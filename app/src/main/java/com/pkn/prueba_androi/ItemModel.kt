package com.pkn.prueba_androi

data class ItemModel(
    val id: String = "",          // El ID único del documento en Firestore
    val nombre: String = "",
    val descripcion: String = "",
    val correoCreador: String = ""
)