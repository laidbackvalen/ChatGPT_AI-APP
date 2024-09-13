package com.valenpatel.chataisol.views.activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.valenpatel.chataisol.R
import com.valenpatel.chataisol.adapter.MessageAdapter
import com.valenpatel.chataisol.databinding.ActivityMainBinding
import com.valenpatel.chataisol.model.ModelMessage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: MessageAdapter
    private lateinit var messageList: ArrayList<ModelMessage>

    private val url: String = "https://api.openai.com/v1/completions"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = getColor(R.color.theme)

        drawerLayout = binding.main

        binding.moreDrawer.setOnClickListener {
            openDrawer()
        }

        binding.imgTxtMessage.setOnClickListener {
            binding.editTxtMessage.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.editTxtMessage, InputMethodManager.SHOW_IMPLICIT)
        }

        messageList = ArrayList()
        adapter = MessageAdapter(this, messageList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.imgBtnSend.setOnClickListener {
            val message = binding.editTxtMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                addToRecyclerView(message, ModelMessage.SENT_BY_ME)
                binding.textView.visibility = View.GONE
                binding.editTxtMessage.text.clear()
                callApi(message)
            }
        }
    }

    private fun addToRecyclerView(message: String, sentBy: String) {
        messageList.add(ModelMessage(message, sentBy))
        adapter.notifyDataSetChanged()
        binding.recyclerView.smoothScrollToPosition(adapter.itemCount)
    }

    fun callApi(question: String) {
        messageList.add(ModelMessage("Fetching data", ModelMessage.SENT_BY_OTHER))
        val url = "https://api.openai.com/v1/chat/completions"

        val parametter = JSONObject().apply {
            put("model", "gpt-3.5-turbo")  // Update if needed
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", question)
                })
            })
            put("max_tokens", 1000)
            put("temperature", 0)
        }

        val request = object : JsonObjectRequest(
            Method.POST,
            url,
            parametter,
            Response.Listener { response ->
                try {
                    val choices = response.getJSONArray("choices")
                    val answer = choices.getJSONObject(0).getString("message")  // Update if needed
                    getGptResponse(answer)
                } catch (e: JSONException) {
                    Log.e("errorMessage", "JSON Parsing Error: ${e.message}")
                    getGptResponse("Failed due to: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                val errorMsg = if (error.networkResponse != null) {
                    val statusCode = error.networkResponse.statusCode
                    val errorBody = error.networkResponse.data?.let { String(it) } ?: "No response body"
                    "Error $statusCode: $errorBody"
                } else {
                    error.message ?: "Unknown error"
                }
                Log.e("errorMessage", errorMsg)
                getGptResponse("Failed due to: $errorMsg")
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                    "Authorization" to "Bearer $accessToken",
                    "Content-Type" to "application/json"
                )
            }
        }

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }



    private fun getGptResponse(answer: String) {
        messageList.removeAt(messageList.size - 1)
        addToRecyclerView(answer, ModelMessage.SENT_BY_OTHER)
    }

    private fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer(drawerLayout: DrawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onPause() {
        closeDrawer(drawerLayout)
        super.onPause()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
