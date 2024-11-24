package com.peter.ujian2.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.peter.ujian2.R
import com.peter.ujian2.viewmodel.FileViewModel

class FeedsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var fileViewModel: FileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        fileViewModel = ViewModelProvider(requireActivity()).get(FileViewModel::class.java)

        val postImageViewDetail: ImageView = view.findViewById(R.id.imgPostDetail)
        val postDescriptionTextView: TextView = view.findViewById(R.id.txtPostDetailDescription)
        val feedbackTextView: TextView = view.findViewById(R.id.txtPostDetailFeedback)
        val btnDownload: Button = view.findViewById(R.id.btnDownload)

        dialog?.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.peekHeight = 600 // Set initial peek height
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                behavior.isDraggable = true // Ensure it can be dragged

                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            dialog.window?.setDimAmount(0.5f)
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        // Implement additional behavior if needed
                    }
                })
            }
        }
    }

    companion object {
        fun newInstance(): FeedsBottomSheetDialogFragment {
            return FeedsBottomSheetDialogFragment()
        }
    }
}
