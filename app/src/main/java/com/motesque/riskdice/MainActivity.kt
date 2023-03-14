package com.motesque.riskdice

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.motesque.riskdice.databinding.ActivityMainBinding
import java.util.*
import kotlin.random.Random
import android.speech.tts.TextToSpeech
import android.util.Log

fun activeCount(input : Array<Boolean>): Int {
    var counter : Int = 0
    for (i in input) {
        if (i) {
            counter++
        }
    }
    return counter
}
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    var attackDice =  Array<ImageView?>(3) {null}
    var attackDiceUsed = Array<Boolean>(3) {true}
    var attackImages =  arrayOf(R.drawable.dice_comic_red_1,R.drawable.dice_comic_red_2,R.drawable.dice_comic_red_3,R.drawable.dice_comic_red_4,R.drawable.dice_comic_red_5,R.drawable.dice_comic_red_6)

    var defendDice =  Array<ImageView?>(2) {null}
    var defendDiceUsed = Array<Boolean>(2) {true}
    var defendImages =  arrayOf(R.drawable.dice_comic_blue_1,R.drawable.dice_comic_blue_2,R.drawable.dice_comic_blue_3,R.drawable.dice_comic_blue_4,R.drawable.dice_comic_blue_5,R.drawable.dice_comic_blue_6)
    var rollButton : MaterialButton? = null
    var settingsButton : ImageView? = null
    val RollDelayMax: Int = 1000
    val RollDelayMin: Int = 200
    private lateinit var binding : ActivityMainBinding
    private lateinit var tts: TextToSpeech
    private var readAloud = false

    fun diceClickHandler(i: Int, diceViews: Array<ImageView?>, diceUsed: Array<Boolean> ) {
        diceUsed[i] = !(diceUsed[i] && activeCount(diceUsed) > 1)
        if (diceUsed[i]) {
            diceViews[i]?.imageTintList = null
        }
        else {
            diceViews[i]?.imageTintList =  ColorStateList.valueOf(getResources().getColor(R.color.overlay))
        }
    }
    fun rollClickHander() {
        val current: Locale = getResources().getConfiguration().getLocales().get(0)
        // reset any ongoing speech
        tts.speak("", TextToSpeech.QUEUE_FLUSH, null,"")
        tts.setLanguage(current)
        if (readAloud) tts.speak(getString(R.string.speech_red_dice), TextToSpeech.QUEUE_ADD, null,"")
        for (i in 0..attackDiceUsed.size - 1) {
            if (attackDiceUsed[i]) {
                val value = Random.nextInt(1, 7)
                if (readAloud) tts.speak("$value", TextToSpeech.QUEUE_ADD, null,"")
                setDiceImageAttack(attackDice[i], value)
            }
        }
        if (readAloud) tts.speak(getString(R.string.speech_blue_dice), TextToSpeech.QUEUE_ADD, null,"")
        for (i in 0..defendDiceUsed.size - 1) {
            if (defendDiceUsed[i]) {
                val value = Random.nextInt(1, 7)
                if (readAloud) tts.speak("$value", TextToSpeech.QUEUE_ADD, null,"")
                setDiceImageDefend(defendDice[i], value)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        collectWidgets()
        tts = TextToSpeech(this, this)
        for (i in 0..attackDice.size-1) {
            attackDice[i]?.setOnClickListener { diceClickHandler(i,attackDice, attackDiceUsed) }
        }
        for (i in 0..defendDice.size-1) {
            defendDice[i]?.setOnClickListener { diceClickHandler(i,defendDice, defendDiceUsed) }
        }
        rollButton?.setOnClickListener {
            rollClickHander()
        }
        settingsButton?.setOnClickListener {
            val activityIntent = Intent(this@MainActivity, LaplaceActivity::class.java)
            startActivity(activityIntent)
        }
        binding.imageViewReadAloud.setOnClickListener {
            readAloud = !readAloud

            if (readAloud) {
                binding.imageViewReadAloud.setImageResource(R.drawable.baseline_volume_up_24)
            }
            else {
                binding.imageViewReadAloud.setImageResource(R.drawable.baseline_volume_off_24)
                // immediately stock speaking
                tts.speak("", TextToSpeech.QUEUE_FLUSH, null,"")
            }

        }

    }
    fun setDiceImageAttack(imageView: ImageView?, value:Int) {
        imageView?.setImageResource(R.drawable.dice_comic_red_blank)
        imageView?.postDelayed({imageView?.setImageResource(attackImages[value-1])},
            Random.nextInt(RollDelayMin,RollDelayMax).toLong()
        )

    }
    fun setDiceImageDefend(imageView: ImageView?, value:Int) {
        imageView?.setImageResource(R.drawable.dice_comic_blue_blank)
        imageView?.postDelayed({imageView?.setImageResource(defendImages[value-1])},
            Random.nextInt(RollDelayMin,RollDelayMax).toLong()
        )
    }
    fun collectWidgets() {
        attackDice[0] = binding.imageViewAttack0
        attackDice[1] = binding.imageViewAttack1
        attackDice[2] = binding.imageViewAttack2
        defendDice[0] = binding.imageViewDefend0
        defendDice[1] = binding.imageViewDefend1
        rollButton = binding.buttonRoll
       // settingsButton = findViewById(R.id.image_view_settings)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            tts.setLanguage(Locale.US)
        }
    }
    public override fun onDestroy() {
        // Shutdown TTS
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
//
}