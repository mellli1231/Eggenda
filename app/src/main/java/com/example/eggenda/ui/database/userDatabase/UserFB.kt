package com.example.eggenda.ui.database.userDatabase

data class UserFB(var id: String? = null,
                  var username: String? = "",
                  var password: String? = "",
                  var points: Int? = 0){
    constructor() : this("", "", "", 0)
}