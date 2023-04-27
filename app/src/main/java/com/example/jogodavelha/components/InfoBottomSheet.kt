package com.example.jogodavelha.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.jogodavelha.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoBottomSheet(text: Int, gif: Int) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

    companion object {
        const val TAG = "InfoBottomSheet"
    }

    private val gifImage = gif
    private val textDescription = text

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val lottieImage = view.findViewById(R.id.animationView) as LottieAnimationView
        val textView = view.findViewById(R.id.sheetInfoText) as TextView

        lottieImage.setAnimation(gifImage)
        textView.text = getString(textDescription)
        super.onViewCreated(view, savedInstanceState)

    }
}