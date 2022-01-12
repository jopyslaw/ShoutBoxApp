package com.example.shoutbox

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.shoutbox.databinding.ActivityDrawerBinding
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.*

class DrawerActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDrawerBinding
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null
    private var dataPosts: ArrayList<Posts>? = null
    private var online: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarDrawer.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.Shoutbox, R.id.nav_gallery
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        online = isOnline(this)
        var get: Boolean = true

        if(!online) {
            Toast.makeText(this, "Brak połączenia z internetem",Toast.LENGTH_LONG).show()
            offlineMode()
            get = false
        }
        else
        {
            getData(false)
            onlineMode()
        }

        val swipeToRefresh: SwipeRefreshLayout = findViewById(R.id.refresh)
        swipeToRefresh.setOnRefreshListener {
            online = isOnline(this)
            if(online) {
                onlineMode()
                if(!get) {
                    getData(false)
                    get = true
                }
                else{
                    getData(true)
                }
            }
            else
            {
                Toast.makeText(this, "Brak połączenia z internetem",Toast.LENGTH_LONG).show()
                (adapter as RecyclerAdapter).deleteAll()
                offlineMode()
            }
            swipeToRefresh.isRefreshing = false
        }



    }

    fun back(item: MenuItem) {
        var intent: Intent = Intent(this, MainActivity::class.java)
        intent.putExtra("CHANGE_DATA", false)
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun rawJSON(view: View)
    {
        val sharedPreferences: SharedPreferences = getSharedPreferences("LOGIN_DATA", MODE_PRIVATE)
        val login = sharedPreferences.getString("LOGIN_VALUE","NULL").toString()
        val msg: EditText = findViewById(R.id.addMessage)
        val msgcontent: String = msg.text.toString()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://tgryl.pl/shoutbox/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val postApi = retrofit.create(PostApi::class.java)
        val call = postApi.addPost(msgcontent,login)

        call.enqueue(object : Callback<DefaultResponse>{
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                Log.i("SUCESSFUL ADD DATA", "DATA WAS SUCCESFULL ADDED")
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Log.e("PROBLEM WITH API CONNECTION", t.message.toString())
            }

        })
        msg.setText(" ")
        closeKeyboard(view)

    }

    fun getData(refreshData: Boolean){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://tgryl.pl/shoutbox/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var postApi = retrofit.create(PostApi::class.java)
        val call = postApi.getPosts()
        call.enqueue(object: Callback<ArrayList<Posts>>{
            override fun onResponse(call: Call<ArrayList<Posts>>, response: Response<ArrayList<Posts>>) {
                if(response.isSuccessful){
                    dataPosts = response.body()
                    if(refreshData)
                    {
                        (adapter as RecyclerAdapter).addAllData(dataPosts!!)
                    }
                    else {
                        initializeRecycler(dataPosts!!)
                        refreshData()
                    }
                }
            }


            override fun onFailure(call: Call<ArrayList<Posts>>, t: Throwable) {
                Log.e("API ERROR","PROBLEM WITH API")
            }
        })
    }

    fun closeKeyboard(view: View)
    {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
    }

    fun refreshData()
    {

        val thread = Thread {
            while(true) {
                getData(true)
                Thread.sleep(60000)
            }
        }
        thread.start()
    }

    fun initializeRecycler(data: ArrayList<Posts>)
    {
        val SharedPreferences = getSharedPreferences("LOGIN_DATA", MODE_PRIVATE)
        val login = SharedPreferences.getString("LOGIN_VALUE", "NULL")
        var rec: RecyclerView = findViewById(R.id.rec)
        layoutManager = LinearLayoutManager(DrawerActivity())
        rec.layoutManager = layoutManager
        adapter = RecyclerAdapter(data, login!!)
        var itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter as RecyclerAdapter))
        itemTouchHelper.attachToRecyclerView(rec)
        rec?.adapter = adapter
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun offlineMode()
    {

        var imgBtn: ImageButton = findViewById(R.id.imageButton)
        var edit: EditText = findViewById(R.id.addMessage)
        edit.focusable = NOT_FOCUSABLE
        imgBtn.isClickable = false
    }

    fun onlineMode()
    {
        var imgBtn: ImageButton = findViewById(R.id.imageButton)
        var edit: EditText = findViewById(R.id.addMessage)
        edit.isFocusableInTouchMode = true
        imgBtn.isClickable = true
    }

}

