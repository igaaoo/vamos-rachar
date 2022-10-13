package com.example.progmob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

var tts: TextToSpeech? = null

var voiceButton : FloatingActionButton? = null
var shareButton : FloatingActionButton? = null

var splitValue = ""

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TTS config
        tts = TextToSpeech(this,this)

        Log.i("idioma", Locale.getDefault().getDisplayLanguage())


        //Views
        var moneyValue = findViewById<EditText>(R.id.moneyValue)
        var peopleValue = findViewById<EditText>(R.id.peopleValue)
        var splitMoney  = findViewById<TextView>(R.id.splitValue)


        //Buttons
        shareButton = findViewById<FloatingActionButton>(R.id.shareButton)
        voiceButton = findViewById<FloatingActionButton>(R.id.voiceButton)
        voiceButton!!.isEnabled = false


        //Split bill
        fun dividir(entrada: CharSequence?): Double{
            return (entrada.toString().toDouble() / peopleValue.text.toString().toDouble())
        }

        //Set individual bill
        moneyValue.addTextChangedListener (
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (!peopleValue.text.isNullOrEmpty() && !moneyValue.text.isNullOrEmpty()) {
                        if (peopleValue.text.toString() == "0") {
                            splitMoney.text = "Informe a quantidade de pessoas"
                        }else {
                            splitValue = String.format("%.2f", dividir(c))
                            splitMoney.text = "R$ " + splitValue
                        }
                    }

                }


                override fun afterTextChanged(c: Editable?) {
                }
            }
        )


        //Send message on Whatsapp or others
        fun sendMessage(message:String){
            //Declara intenção de enviar
            val intent = Intent()
            intent.setAction(Intent.ACTION_SEND)

            //Mensagem a ser enviada
            intent.putExtra(Intent.EXTRA_TEXT, "Ficou: " + message + " para cada.")

            //Tipo da intenção
            intent.setType("text/plain")

            //Inicia o menu de seleção
            startActivity(intent)
        }


        //Button to share
        shareButton!!.setOnClickListener {
            val message = splitValue
            sendMessage(message)
        }

        //Button to Text-to-Speak
        voiceButton!!.setOnClickListener {
            speakText(splitValue)
        }
    }


    //Setting Text-to-Speak configs
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            var result : Int? = null

            //Setting language of tts by device language
            if(Locale.getDefault().getDisplayLanguage() != "English"){
                result = tts!!.setLanguage(Locale("pt", "BR"))
            }else {
                result = tts!!.setLanguage(Locale.US)
            }


            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                //Activating button after loading tts
                voiceButton!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    //Message that will be spoken
    fun speakText(value: String){
        var message = "Ficou: " + value + " reais para cada."
        tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    //Turning off Text-to-Speak
    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}