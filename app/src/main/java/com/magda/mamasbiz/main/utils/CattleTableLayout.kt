package com.magda.mamasbiz.main.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.magda.mamasbiz.R

class CattleTableLayout (context: Context, attrs: AttributeSet?): LinearLayout(context, attrs){
    constructor(context: Context):this(context, null)
    private var mCattleType: TextView
    private var mAmount: TextView
    private var mQty: EditText
    private var mPrice: EditText
    private var mCancel: ImageView
    private lateinit var qty : String
    private lateinit var price : String
    private lateinit var callback: (String) -> Unit
    private lateinit var removeCallback: (String) -> Unit
    private val TAG = "CattleTableLayout"

    init {
        val view = inflate(context, R.layout.cattle_layout,this)
        mCattleType = findViewById(R.id.tvCattleType)
        mAmount = findViewById(R.id.tvCattleAmount)
        mQty = findViewById(R.id.etQty)
        mPrice = findViewById(R.id.etPrice)
        mCancel = findViewById(R.id. ivCancel)
        toCalculate()
        mCancel.setOnClickListener { removeCallback(mAmount.text.toString()) }
    }



    private fun toCalculate(){
        val textWatcher = object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Method sub
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                qty = if(mQty.text.toString().isEmpty()){
                    "0"
                } else mQty.text.toString().trim()
                price = if(mPrice.text.toString().isEmpty()){
                    "0"
                } else mPrice.text.toString().trim()
                val amt = qty.toInt().times(price.toInt()).toString()
                mAmount.text = amt
                callback(amt)



            }

            override fun afterTextChanged(s: Editable?) {
                //Method sub
            }



        }

        mQty.addTextChangedListener(textWatcher)
        mPrice.addTextChangedListener(textWatcher)
    }
    fun getAmount ():String{
        val string = mAmount.text.toString()
        Log.d(TAG, "getAmount: $string")
        return string
    }
    fun onAmountChangeListener (callback:(String)-> Unit){
        this.callback = callback

    }
    fun onRemoveAmountListener (callback:(String)-> Unit){
        this.removeCallback = callback

    }




}
