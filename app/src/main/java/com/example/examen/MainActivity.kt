package com.example.examen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val url = "https://00672285.us-south.apigw.appdomain.cloud/demo-gapsi/search"
    val page = 1
    var call : Call? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            Log.d(TAG, "Click on Button with find: ${tv_find.text}")
            callUbicacionService()
        }

    }

    fun callUbicacionService(){
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        val urlBuilder = HttpUrl.parse(url)!!.newBuilder()
        urlBuilder.addQueryParameter("query", tv_find.text!!.trim().toString())
        urlBuilder.addQueryParameter("page", page.toString())
        val finalUrl = urlBuilder.build().toString()
        Log.d(TAG,"finalUrl: ${finalUrl}")
        val request = Request.Builder()
            .url(finalUrl)
            .addHeader("X-IBM-Client-Id","adb8204d-d574-4394-8c1a-53226a40876e")
            .build()

        val callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //Mostrar Error
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyResponse = response.body()?.string()
                Log.d(TAG, "bodyResponseUbicacion: $bodyResponse")
                val productos = Gson().fromJson(bodyResponse,Productos::class.java)
                for (producto in productos.productDetails){
                    Log.d(TAG,"Producto: $producto")
                }
                Log.d(TAG,productos.domainCode)
            }

        }
        call = client.newCall(request)
        call?.enqueue(callback)
    }
}