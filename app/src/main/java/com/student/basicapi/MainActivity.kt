package com.student.basicapi

import android.os.Build.ID
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import org.w3c.dom.Text
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnGetLoan).setOnClickListener { getLoanById() }
        findViewById<Button>(R.id.btnDeleteLoan).setOnClickListener { deleteLoanById() }
        findViewById<Button>(R.id.btnAddLoan).setOnClickListener { addLoan() }
        Read()
        Post()
    }

    fun GetByMemeber(){
        try {
            val url = URL("https://opsc20240710154110.azurewebsites.net/GetLoansByMember/${ID}")
            val json = url.readText()
            if (json.equals("[]")) {
                Handler(Looper.getMainLooper()).post {
                    var Text = findViewById<TextView>(R.id.txtOutput)
                    Text.setText("Member not found")
                }
            }
                else {
                    val userList = Gson().fromJson(json,Array<Loan>::class.java).toList()
                Handler(Looper.getMainLooper()).post{
                    var output: String =""
                    for (user in userList){
                        output+= "Loan ID" + user.loanID + "\nLoan Amount:" + user.amount + "\nMemberID" + user.memberID + "\nMessage:" + user.message + "\n\n"
                    }
                    var Text = findViewById<TextView>(R.id.txtOutput)
                    Text.setText(output)
                }
            }
        } catch (e:Exception){
            var Text = findViewById<TextView>(R.id.txtOutput)
            Text.text= e.toString()
        }
    }

    fun GetLoanbyID(){
        try {
            val url = URL("https://opsc20240710154110.azurewebsites.net/GetLoansByMember/${ID}")
            val json = url.readText()
            if (json.equals("[]")) {
                Handler(Looper.getMainLooper()).post {
                    var Text = findViewById<TextView>(R.id.txtOutput)
                    Text.setText("Member not found")
                }
            }
            else {
                val userList = Gson().fromJson(json,Array<Loan>::class.java).toList()
                Handler(Looper.getMainLooper()).post{
                    var output: String =""
                    for (user in userList){
                        output+= "Loan ID" + user.loanID + "\nLoan Amount:" + user.amount + "\nMemberID" + user.memberID + "\nMessage:" + user.message + "\n\n"
                    }
                    var Text = findViewById<TextView>(R.id.txtOutput)
                    Text.setText(output)
                }
            }
        } catch (e:Exception){
            var Text = findViewById<TextView>(R.id.txtOutput)
            Text.text= e.toString()
        }
    }

    fun Read() {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val url = URL("https://opsc20240710154110.azurewebsites.net/GetAllLoans")
                val json = url.readText()
                val userList = Gson().fromJson(json, Array<Loan>::class.java).toList()

                Handler(Looper.getMainLooper()).post {
                    Log.d("ReadLoans", "Plain Json Vars:" + json)
                    Log.d("ReadLoans", "Converted Json:" + userList)
                    val text = findViewById<TextView>(R.id.txtOutput)
                    text.text = userList.toString()
                }
            } catch (e: Exception) {
                Log.d("ReadLoans", "Error:" + e)
                val text = findViewById<TextView>(R.id.txtOutput)
                text.text = "Error:" + e
            }
        }
    }

    fun Post() {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val loanPost = LoanPost("1500", "M10085591", "Added by ST10085591")

            val (_, _, result) = "https://opsc20240710154110.azurewebsites.net/AddLoan".httpPost()
                .jsonBody(Gson().toJson(loanPost)).responseString()
            val json = "[" + result.component1() + "]"
            val loanList = Gson().fromJson(json, Array<Loan>::class.java).toList()

            Handler(Looper.getMainLooper()).post {
                val text = findViewById<TextView>(R.id.txtOutput)
                text.text = loanList.toString()
            }
        }
    }

    fun getLoanById() {
        val loanId = findViewById<EditText>(R.id.editTextLoanId).text.toString()
        if (loanId.isEmpty()) {
            findViewById<TextView>(R.id.txtOutput).text = "Please enter a Loan ID"
            return
        }

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val url = URL("https://opsc20240710154110.azurewebsites.net/GetLoanById?id=$loanId")
                val json = url.readText()
                val loan = Gson().fromJson(json, Loan::class.java)

                Handler(Looper.getMainLooper()).post {
                    val text = findViewById<TextView>(R.id.txtOutput)
                    text.text = loan.toString()
                }
            } catch (e: Exception) {
                Log.d("GetLoanById", "Error:" + e)
                val text = findViewById<TextView>(R.id.txtOutput)
                text.text = "Error:" + e
            }
        }
    }

    fun Delete(){
        val executor = Executors.newSingleThreadExecutor()
        val ID = "3001"

        executor.execute{
            try{
                val url = URL("\"https://opsc20240710154110.azurewebsites.net/DeleteLoan/$ID/")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "DELETE"
                connection.connect()

                val responseCode = connection.responseCode
                val responseMessege = connection.inputStream.bufferedReader().use {it.readText()}

                Handler(Looper.getMainLooper()).post{
                    val text = findViewById<TextView>(R.id.txtOutput)
                    if (responseCode ==200 && responseMessege.contains("1")){
                        text.text = "User Deleted"

                    } else{
                        text.text = "User not found"
                    }
                }
            } catch (e: Exception){
                Handler(Looper.getMainLooper()).post{
                    val text = findViewById<TextView>(R.id.txtOutput)
                    text.text = "Error: ${e.message}"
                }
            }
        }
    }

    fun deleteLoanById() {
        val loanId = findViewById<EditText>(R.id.editTextLoanId).text.toString()
        if (loanId.isEmpty()) {
            findViewById<TextView>(R.id.txtOutput).text = "Please enter a Loan ID"
            return
        }

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val (_, _, result) = "https://opsc20240710154110.azurewebsites.net/DeleteLoan?id=$loanId"
                    .httpPost().responseString()
                val response = result.component1()

                Handler(Looper.getMainLooper()).post {
                    val text = findViewById<TextView>(R.id.txtOutput)
                    text.text = response
                }
            } catch (e: Exception) {
                Log.d("DeleteLoanById", "Error:" + e)
                val text = findViewById<TextView>(R.id.txtOutput)
                text.text = "Error:" + e
            }
        }
    }

    fun addLoan() {
        val amount = findViewById<EditText>(R.id.editTextNewLoanAmount).text.toString()
        val memberId = findViewById<EditText>(R.id.editTextMemberId).text.toString()
        val message = findViewById<EditText>(R.id.editTextMessage).text.toString()

        if (amount.isEmpty() || memberId.isEmpty() || message.isEmpty()) {
            findViewById<TextView>(R.id.txtOutput).text = "Please enter all fields"
            return
        }

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val loanPost = LoanPost(amount, memberId, message)

            val (_, _, result) = "https://opsc20240710154110.azurewebsites.net/AddLoan".httpPost()
                .jsonBody(Gson().toJson(loanPost)).responseString()
            val json = "[" + result.component1() + "]"
            val loanList = Gson().fromJson(json, Array<Loan>::class.java).toList()

            Handler(Looper.getMainLooper()).post {
                val text = findViewById<TextView>(R.id.txtOutput)
                text.text = loanList.toString()
            }
        }
    }
}