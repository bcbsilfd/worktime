package com.example.worktime

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.worktime.databinding.FragmentTimerBinding
import java.text.SimpleDateFormat
import java.util.*

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private var timer: CountDownTimer? = null

    private var lastTime: Long = 0L

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        val rootView = binding.root

        //Add media player
        var mediaPlayer: MediaPlayer? = null


        //Init timePicker
        val timePicker = binding.timerTp
        timePicker.setIs24HourView(true)
        timePicker.hour = 0
        timePicker.minute = 45
        timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            lastTime = getLong(hourOfDay.toString(), minute.toString())
        }

        //Set sound for media player
        Log.i("Time", "Sound button is: ${binding.soundIb.isActivated}")
        binding.soundIb.isActivated = true
        binding.soundIb.setOnClickListener {
            if (it.isActivated) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
                Log.i("Time", "Sound button is: ${it.isActivated}")
                it.isActivated = false
            } else {
                it.isActivated = true
                Log.i("Time", "Sound button is: ${it.isActivated}")
            }
            Log.i("Time", "${it.isActivated}")


        }
        mediaPlayer = if (binding.soundIb.isActivated) {
            MediaPlayer.create(context, R.raw.ship_bell)
        } else {
            mediaPlayer?.release()
            null
        }
        Log.i("Time", "Sound button is: ${binding.soundIb.isActivated}")

        //Set timer with start click method
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        binding.timerBtnStart.setOnClickListener {
            binding.timerBtnStop.isEnabled = true

            //Changes button text
            if (binding.timerBtnStart.text == getString(R.string.start_btn) &&
                getLong(timePicker.hour.toString(), timePicker.minute.toString()) > 0
            ) {
                binding.timerBtnStart.text = getString(R.string.pause_btn)

                binding.timerHourTv.visibility = View.GONE
                binding.timerMinuteTv.visibility = View.GONE

                mediaPlayer = if (binding.soundIb.isActivated) {
                    MediaPlayer.create(context, R.raw.ship_bell)
                } else {
                    mediaPlayer?.release()
                    null
                }

                if (lastTime != getLong(
                        timePicker.hour.toString(),
                        timePicker.minute.toString()
                    ) && lastTime > 0
                ) {
                    timer = object : CountDownTimer(
                        lastTime, 1000
                    ) {
                        override fun onTick(millisUntilFinished: Long) {
                            //Color changes before 1 min and 10 seconds timer off
                            if (millisUntilFinished < 60000) {
                                binding.timerTv.setTextColor(Color.BLACK)
                                if (millisUntilFinished < 10000) {
                                    binding.timerTv.setTextColor(Color.RED)
                                }
                            }
                            Log.i("TimerFragment", simpleDateFormat.format((millisUntilFinished)))
                            binding.timerTv.visibility = View.VISIBLE
                            timePicker.visibility = View.INVISIBLE
                            binding.timerTv.text = simpleDateFormat.format(millisUntilFinished)

                            //Set temporary var long time, fot last timer value
                            lastTime = millisUntilFinished

                        }

                        override fun onFinish() {
                            lastTime = 0
                            Log.i("TimerFragment", "Timer: Finished")
                            binding.timerTv.text = getString(R.string.timer_off)
                            //TODO add play sound
                            mediaPlayer?.isLooping = true
                            mediaPlayer?.start()

                        }
                    }.start()
                } else {


                    timer = object : CountDownTimer(
                        getLong(timePicker.hour.toString(), timePicker.minute.toString()), 1000
                    ) {
                        override fun onTick(millisUntilFinished: Long) {
                            //Color changes before 1 min and 10 seconds timer off
                            if (millisUntilFinished < 60000) {
                                binding.timerTv.setTextColor(Color.BLACK)
                                if (millisUntilFinished < 10000) {
                                    binding.timerTv.setTextColor(Color.RED)
                                }
                            }
                            Log.i("TimerFragment", simpleDateFormat.format((millisUntilFinished)))
                            binding.timerTv.visibility = View.VISIBLE
                            timePicker.visibility = View.INVISIBLE
                            binding.timerTv.text = simpleDateFormat.format(millisUntilFinished)

                            //Set temporary var long time, fot last timer value
                            lastTime = millisUntilFinished

                        }

                        override fun onFinish() {
                            lastTime = 0
                            Log.i("TimerFragment", "Timer: Finished")
                            binding.timerTv.text = getString(R.string.timer_off)
                            mediaPlayer?.isLooping = true
                            mediaPlayer?.start()

                        }
                    }.start()

                }
            } else {
                if (lastTime > 0){
                    binding.timerBtnStart.text = getString(R.string.start_btn)
                } else {
                    Toast.makeText(context, "Set time!",Toast.LENGTH_SHORT).show()
                }
                timer?.cancel()
            }

        }
        //Set stop button disabled
        //TODO add enable/disable the button

        binding.timerBtnStop.setOnClickListener {
            Log.i("TimerFragment", "Timer: Stopped")

            timer?.cancel()

            lastTime = getLong(timePicker.hour.toString(), timePicker.minute.toString())

            binding.timerHourTv.visibility = View.VISIBLE
            binding.timerMinuteTv.visibility = View.VISIBLE

            binding.timerTv.visibility = View.GONE
            timePicker.visibility = View.VISIBLE
            binding.timerBtnStart.text = getString(R.string.start_btn)
            mediaPlayer?.stop()
            mediaPlayer = null
        }













        return rootView
    }

    private fun getLong(hour: String, minute: String): Long {
        return ((hour.toInt() * 3600 * 1000) + (minute.toInt() * 60 * 1000)).toLong()
    }


}