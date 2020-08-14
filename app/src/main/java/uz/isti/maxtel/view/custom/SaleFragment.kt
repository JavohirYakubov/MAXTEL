package uz.isti.maxtel.view.custom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_sale.*
import uz.isti.maxtel.R
import uz.isti.maxtel.utils.Prefs

/**
 * A simple [Fragment] subclass.
 */
class SaleFragment : DialogFragment() {
    private lateinit var mTitle: String
    private lateinit var listener: ProgressDialogListener

    companion object {
        private const val ARG_TITLE = "TITLE"

        fun newInstance(title: String): SaleFragment {
            val dialog = SaleFragment()
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mTitle = arguments?.getString(ARG_TITLE)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val rootView = inflater.inflate(R.layout.fragment_sale, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgDismiss.setOnClickListener {
            dismiss()
        }
    }
}