package com.example.shoutbox

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RecyclerAdapter(val data: ArrayList<Posts>?, val login: String) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.username.text = data?.get(position)?.login
        holder.date.text = data?.get(position)?.changeDate()
        holder.message.text = data?.get(position)?.content

        holder.itemView.setOnClickListener {
            val context = holder.username.context
            if(login == holder.username.text) {
                val intent = Intent(context, EditActivity::class.java)
                intent.putExtra("username", holder.username.text)
                intent.putExtra("date", holder.date.text)
                intent.putExtra("message", holder.message.text)
                intent.putExtra("id", data?.get(position)?.id)
                context.startActivity(intent)
            }
            else{
                Toast.makeText(context, "Brak uprawnień",Toast.LENGTH_LONG).show();
            }
        }
    }

    override fun getItemCount(): Int {
        return data?.size!!
    }

    fun deleteItem(pos: Int) {
        var id = data?.get(pos)?.id
        data?.removeAt(pos)
        deletePost(id)
        notifyItemRemoved(pos)
        notifyDataSetChanged()
    }

    fun addAllData(data2: ArrayList<Posts>) {
        data?.clear()
        data?.addAll(data2)
        notifyDataSetChanged()
    }

    fun deleteAll()
    {
        data?.clear()
        notifyDataSetChanged()
    }

    fun deletePost(id: String?)
    {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://tgryl.pl/shoutbox/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val postApi = retrofit.create(PostApi::class.java)
        val call = postApi.deletePost(id!!)

        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                Log.i("SUCESSFUL DELETE DATA", "DATA WAS SUCCESFULL DELETED")
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Log.e("PROBLEM WITH API CONNECTION", t.message.toString())
            }

        })
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var username: TextView
        var date: TextView
        var message: TextView

        init {
            username = itemView.findViewById(R.id.usernamField)
            date = itemView.findViewById(R.id.dataField)
            message = itemView.findViewById(R.id.messageField)

        }

    }

}
