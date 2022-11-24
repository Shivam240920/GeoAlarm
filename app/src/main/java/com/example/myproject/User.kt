package com.example.myproject

class User {
    var id : Int = 0
    var msg : String = ""
    var lat : Double = 0.0
    var lng : Double = 0.0

    constructor(lat:Double,lng:Double,msg:String){
        this.msg = msg
        this.lat = lat
        this.lng = lng
    }
    constructor(){

    }

}