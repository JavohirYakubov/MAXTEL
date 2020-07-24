package uz.isti.maxtel.view.custom

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_date_range.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.showError
import uz.isti.maxtel.utils.DateUtils
import java.util.*

interface DateRangeFragmentListener{
    fun onResult(startDate: String, endDate: String)
}

class DateRangeFragment(val listener: DateRangeFragmentListener) : BottomSheetDialogFragment(), CalendarListener {
    var startDateValue = ""
    var endDateValue = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_range, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar.setCalendarListener(this)

        cardViewOk.setOnClickListener {
            if (startDateValue.isNotEmpty() && endDateValue.isNotEmpty()){
                listener.onResult(startDateValue, endDateValue)
                dismiss()
            }else{
                activity?.showError(getString(R.string.please_select_two_interval))
            }
        }
    }

    override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
        startDateValue = DateUtils.getDateFromCalender(startDate)
        endDateValue = DateUtils.getDateFromCalender(endDate)
        tvStartDate.text = startDateValue
        tvEndDate.text = endDateValue
    }

    override fun onFirstDateSelected(startDate: Calendar) {
        startDateValue = DateUtils.getDateFromCalender(startDate)
        tvStartDate.text = startDateValue
    }
}
