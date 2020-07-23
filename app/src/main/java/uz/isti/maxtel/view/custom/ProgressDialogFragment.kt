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

import uz.isti.maxtel.R

/**
 * A simple [Fragment] subclass.
 */
interface ProgressDialogListener{
    fun onClickReload()
    fun onClickFinishView()
}
class ProgressDialogFragment : DialogFragment() {
    private lateinit var mTitle: String
    private lateinit var listener: ProgressDialogListener

    companion object {
        private const val ARG_TITLE = "TITLE"

        fun newInstance(title: String, progressDialogListener: ProgressDialogListener): ProgressDialogFragment {
            val progressDialogFragment = ProgressDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            progressDialogFragment.arguments = args
            progressDialogFragment.listener = progressDialogListener
            return progressDialogFragment
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

        val rootView = inflater.inflate(R.layout.fragment_progress_dialog, container, false)

        return rootView
    }

    public fun visibleReloadButton(){

    }
}