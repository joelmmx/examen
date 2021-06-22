package com.example.examen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val url = "https://00672285.us-south.apigw.appdomain.cloud/demo-gapsi/search"
    var page = 0
    var call : Call? = null
    val pageSize = 40
    var productosAdapter : ProductoAdapter? = null
    var listProductos = ArrayList<Producto>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        error.visibility = View.GONE
        linearLayoutManager = LinearLayoutManager(this)
        rv_productos.layoutManager = linearLayoutManager
        button.setOnClickListener {
            Log.d(TAG, "Click on Button with find: ${tv_find.text}")
            callProductosService()
        }

    }

    fun callProductosService(){
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        val urlBuilder = HttpUrl.parse(url)!!.newBuilder()
        urlBuilder.addQueryParameter("query", tv_find.text!!.trim().toString())
        urlBuilder.addQueryParameter("page", (page+1).toString())
        val finalUrl = urlBuilder.build().toString()
        Log.d(TAG,"finalUrl: ${finalUrl}")
        val request = Request.Builder()
            .url(finalUrl)
            .addHeader("X-IBM-Client-Id","adb8204d-d574-4394-8c1a-53226a40876e")
            .build()

        val callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG,"Error en el Servicios: $e")
                runOnUiThread {
                    error.visibility = View.VISIBLE
                    error.text = e.toString()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG,"Response")
                page++;
                val bodyResponse = response.body()?.string()
                Log.d(TAG, "bodyResponseUbicacion: $bodyResponse")
                val productos = Gson().fromJson(bodyResponse,Productos::class.java)
                listProductos.addAll(productos.productDetails)
                Log.d(TAG , "listProductos.size: ${listProductos.size}")
                for (producto in listProductos){
                    Log.d(TAG,"Producto: $producto")
                }
                Log.d(TAG,productos.domainCode)
                runOnUiThread{
                    productosAdapter = ProductoAdapter(productos.productDetails.toMutableList())
                    rv_productos.setAdapter(productosAdapter)
                    setRecyclerViewScrollListener()
                }

            }



        }
        call = client.newCall(request)
        call?.enqueue(callback)
    }

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val lastVisibleItemPosition: Int
        get() = linearLayoutManager.findLastVisibleItemPosition()

    private fun setRecyclerViewScrollListener() {
        rv_productos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
//              if(position>pageSize*page-2) {
//
//              }
                Log.d(TAG,"newState: ${newState}")
                Log.d(TAG,"lastVisibleItemPosition ${lastVisibleItemPosition}")
            }
        })
    }

    inner class ProductoAdapter(private val mProductos: MutableList<Producto>) : RecyclerView.Adapter<ProductoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoHolder {
            return ProductoHolder(LayoutInflater.from(applicationContext).inflate(R.layout.product_holder, parent, false))
        }

        override fun onBindViewHolder(holder: ProductoHolder, position: Int) {
            Log.d(TAG , "position: ${position}")
            holder.bindProducto(mProductos[position])

        }

        override fun getItemCount(): Int {
            return mProductos.size
        }

        fun getmUbicaciones(): List<Producto> {
            return mProductos
        }

        fun setmUbicaciones(mProductos: List<Producto>?) {
            this.mProductos.clear()
            this.mProductos.addAll(mProductos!!)
            notifyDataSetChanged()
        }

    }

    inner class ProductoHolder(productoView: View) : RecyclerView.ViewHolder(productoView), View.OnClickListener {
        private val mTitleTextView: TextView
        private val mPrice: TextView
        private val mImage : ImageView
        private var mProducto: Producto? = null
        fun bindProducto(producto: Producto) {
            mTitleTextView.text = producto.title
            if(producto.primaryOffer.listPrice != null)
                mPrice.text         = "$ ${producto.primaryOffer.listPrice}"
            else if(producto.primaryOffer.offerPrice != null)
                mPrice.text         = "$ ${producto.primaryOffer.offerPrice}"
            else if(producto.primaryOffer.maxPrice != null)
                mPrice.text         = "$ ${producto.primaryOffer.maxPrice}"
            else if(producto.primaryOffer.minPrice != null)
                mPrice.text         = "$ ${producto.primaryOffer.minPrice}"
            else
                mPrice.text         = "N/A"
            Picasso.get().load(producto.imageUrl).resize(250, 250).into(mImage)

            mProducto = producto
        }

        init {
            mTitleTextView = productoView.findViewById(R.id.tv_title)
            mPrice = productoView.findViewById(R.id.tv_price)
            mImage   = productoView.findViewById(R.id.tv_image)
        }

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }
    }
}