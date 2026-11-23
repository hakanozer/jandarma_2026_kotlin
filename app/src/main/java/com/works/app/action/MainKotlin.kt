package com.works.app.action

import android.util.Log

class MainKotlin {

    fun call() {

        // val -> sabit
        // var -> değişken
        val name = "Erkan"
        var surname = "Bilmem"

        var obj: Any
        obj = 10
        obj = "Ali"

        // nullable
        var age: Int? = null
        // age!!.dec() // bu sen karaışma ben burayı doldurdum.
        age?.let { // gizli bir it objesi vardır it = age
            it.dec()
        } // null kontolü sağlandı
        age = 10
        Log.d("TAG", ""+ age.dec())

        val sm = sum(100,500)
        Log.d("TAG", "toplam: " + sm)

        val smx = sumX(100, 500)
        Log.d("TAG", smx.second + " "+ smx.first)

        //val products = mutableListOf<Product>() // bir list oluşturma biçimidir.
        //products.add()
        val order = Order(100, "Pizza", 100.0, 2)
        val p1 = Product(1, "Pizza", 100.0)
        val p2 = Product(2, "Hamburger", 50.0)

        order.products.add(p1)
        order.products.add(p2)

        Log.d("TAG", "order:" + order)

    }

    fun sum( num1: Int, num2: Int ) : Int {
        // num1 = 10 // sabit değer değiştirilemez
        val sm = num1 + num2
        return sm
    }

    fun sumX(num1: Int, num2: Int): Pair<Int, String> {
        val sm = num1 + num2
        return Pair(sm, "Toplam")
    }

}