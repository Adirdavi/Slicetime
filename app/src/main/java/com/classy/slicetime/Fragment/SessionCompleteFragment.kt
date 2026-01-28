package com.classy.slicetime.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.classy.slicetime.R
import com.classy.slicetime.databinding.FragmentSessionCompleteBinding
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class SessionCompleteFragment : Fragment() {

    private var _binding: FragmentSessionCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE)


        checkAndShowRandomPopup()


        binding.btnStartBreak.setOnClickListener {
            sharedPreferences.edit().putBoolean("IS_BREAK_MODE", true).apply()
            findNavController().navigate(R.id.action_complete_to_home)
        }

        binding.btnSkipBreak.setOnClickListener {
            sharedPreferences.edit().putBoolean("IS_BREAK_MODE", false).apply()
            findNavController().navigate(R.id.action_complete_to_home)
        }
    }

    private fun checkAndShowRandomPopup() {
        val chance = Random.nextInt(1, 100)

        if (chance <= 50) {


            if (Random.nextBoolean()) {
                showRateDialog()
            } else {
                showShareDialog()
            }
        }
    }

    private fun showRateDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rate, null)
        val builder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btnSubmit = dialogView.findViewById<MaterialButton>(R.id.btnSubmitRate)
        val btnLater = dialogView.findViewById<TextView>(R.id.btnLaterRate)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

        btnSubmit.setOnClickListener {

            val appPackageName = requireContext().packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
            dialog.dismiss()
        }

        btnLater.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showShareDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_share, null)
        val builder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btnShare = dialogView.findViewById<MaterialButton>(R.id.btnShareAction)
        val btnLater = dialogView.findViewById<TextView>(R.id.btnLaterShare)

        btnShare.setOnClickListener {

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Slicetime App")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! I'm using Slicetime to stay focused. Check it out!")
            startActivity(Intent.createChooser(shareIntent, "Share via"))
            dialog.dismiss()
        }

        btnLater.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}