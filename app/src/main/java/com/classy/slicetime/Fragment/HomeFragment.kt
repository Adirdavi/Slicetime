package com.classy.slicetime.Fragment

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.classy.slicetime.R
import com.classy.slicetime.databinding.FragmentHomeBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var timer: CountDownTimer? = null
    private var isRunning = false

    private var startTimeInMillis: Long = 0
    private var timeLeftInMillis: Long = 0
    private var isBreakMode = false

    private val CHANNEL_ID = "slicetime_timer_channel"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startTimer()
        } else {
            startTimer()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createNotificationChannel()

        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        updateStatsUI()

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_settings)
        }

        binding.fabStart.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                checkPermissionAndStart()
            }
        }

        binding.btnReset.setOnClickListener {
            resetTimer()
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatsUI()

        if (!isRunning) {
            val sharedPreferences = requireActivity().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE)
            isBreakMode = sharedPreferences.getBoolean("IS_BREAK_MODE", false)

            if (isBreakMode) {
                setupBreakMode(sharedPreferences)
            } else {
                setupFocusMode(sharedPreferences)
            }

            timeLeftInMillis = startTimeInMillis
            updateCountDownText()
            binding.fabStart.setImageResource(android.R.drawable.ic_media_play)
        }
    }

    private fun checkPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startTimer()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startTimer()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer Notifications"
            val descriptionText = "Notifies when timer finishes"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendTimerNotification() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val title = if (isBreakMode) "Break Over! 🍅" else "Focus Session Complete! 🎉"
        val message = if (isBreakMode) "Time to get back to work." else "Great job! Take a break."

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.tomato)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(1001, builder.build())
        }
    }

    private fun updateStatsUI() {
        val prefs = requireActivity().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE)
        val sessions = prefs.getInt("TOTAL_SESSIONS", 0)
        val focusMinutes = prefs.getInt("TOTAL_FOCUS_MINUTES", 0)

        binding.tvSessionCount.text = sessions.toString()
        binding.tvFocusTimeTotal.text = "${focusMinutes}m"
    }

    private fun setupFocusMode(prefs: android.content.SharedPreferences) {
        val minutes = prefs.getInt("FOCUS_TIME", 25)
        startTimeInMillis = minutes * 60 * 1000L

        binding.tvModeSubtitle.text = "Focus Session"
        binding.fabStart.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FF6B6B"))
        binding.imgTimerIcon.setImageResource(R.drawable.tomato)
    }

    private fun setupBreakMode(prefs: android.content.SharedPreferences) {
        val minutes = prefs.getInt("BREAK_TIME", 5)
        startTimeInMillis = minutes * 60 * 1000L

        binding.tvModeSubtitle.text = "Take a Break"
        binding.fabStart.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#4ADE80"))
        binding.imgTimerIcon.setImageResource(R.drawable.happy_tomato)
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                isRunning = false
                timer = null

                sendTimerNotification()

                if (!isBreakMode) {
                    val prefs = requireActivity().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE)
                    val currentSessions = prefs.getInt("TOTAL_SESSIONS", 0)
                    val currentMinutes = prefs.getInt("TOTAL_FOCUS_MINUTES", 0)
                    val sessionDuration = prefs.getInt("FOCUS_TIME", 25)

                    prefs.edit()
                        .putInt("TOTAL_SESSIONS", currentSessions + 1)
                        .putInt("TOTAL_FOCUS_MINUTES", currentMinutes + sessionDuration)
                        .apply()
                }

                findNavController().navigate(R.id.action_home_to_complete)
            }
        }.start()

        isRunning = true
        binding.fabStart.setImageResource(android.R.drawable.ic_media_pause)
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        binding.fabStart.setImageResource(android.R.drawable.ic_media_play)
    }

    private fun resetTimer() {
        timer?.cancel()
        isRunning = false

        val sharedPreferences = requireActivity().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE)
        if (isBreakMode) {
            setupBreakMode(sharedPreferences)
        } else {
            setupFocusMode(sharedPreferences)
        }

        timeLeftInMillis = startTimeInMillis
        updateCountDownText()
        binding.fabStart.setImageResource(android.R.drawable.ic_media_play)
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        binding.tvTimer.text = timeFormatted
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        isRunning = false
        _binding = null
    }
}