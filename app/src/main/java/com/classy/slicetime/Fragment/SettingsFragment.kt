package com.classy.slicetime.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.classy.slicetime.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE)

        val savedFocusTime = sharedPreferences.getInt("FOCUS_TIME", 25)
        val savedBreakTime = sharedPreferences.getInt("BREAK_TIME", 5)
        val isBreakMode = sharedPreferences.getBoolean("IS_BREAK_MODE", false)

        binding.sliderFocus.value = savedFocusTime.toFloat()
        binding.tvFocusVal.text = "${savedFocusTime}m"

        binding.sliderBreak.value = savedBreakTime.toFloat()
        binding.tvBreakVal.text = "${savedBreakTime}m"

        binding.switchMode.isChecked = isBreakMode

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.switchMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("IS_BREAK_MODE", isChecked).apply()
        }

        binding.sliderFocus.addOnChangeListener { _, value, _ ->
            val minutes = value.toInt()
            binding.tvFocusVal.text = "${minutes}m"
            sharedPreferences.edit().putInt("FOCUS_TIME", minutes).apply()
        }

        binding.sliderBreak.addOnChangeListener { _, value, _ ->
            val minutes = value.toInt()
            binding.tvBreakVal.text = "${minutes}m"
            sharedPreferences.edit().putInt("BREAK_TIME", minutes).apply()
        }

        binding.btnLogin.setOnClickListener {
            Toast.makeText(context, "Login feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}