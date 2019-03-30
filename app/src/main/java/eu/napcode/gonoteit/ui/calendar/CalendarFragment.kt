package eu.napcode.gonoteit.ui.calendar

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils

import javax.inject.Inject

import dagger.android.support.AndroidSupportInjection
import eu.napcode.gonoteit.R
import eu.napcode.gonoteit.di.modules.viewmodel.ViewModelFactory
import eu.napcode.gonoteit.model.note.NoteModel
import eu.napcode.gonoteit.ui.note.NoteActivity
import eu.napcode.gonoteit.utils.getTodayCalendar
import eu.napcode.gonoteit.utils.getTomorrowCalendar
import eu.napcode.gonoteit.utils.isSameDate
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*

class CalendarFragment : Fragment(), CalendarAdapter.CalendarEventListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var viewModel: CalendarViewModel? = null

    private var calendarAdapter: CalendarAdapter? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.viewModel = ViewModelProviders
                .of(this, this.viewModelFactory)
                .get(CalendarViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        setupRecyclerView()
        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        var calendarResult = this.viewModel!!.getWeekEvents()

        calendarResult.notes.observe(this, Observer<List<NoteModel>> {
            calendarAdapter!!.calendarElements = getCalendarElementsFromResult(it!!)
        })
    }

    private fun getCalendarElementsFromResult(notes: List<NoteModel>) : List<CalendarAdapterElement> {
        val calendarElements = mutableListOf<CalendarAdapterElement>()

        addTodayDateIfShould(calendarElements, notes)

        notes.forEach { noteModel ->
            if (hasDate(calendarElements, noteModel.date!!) == false) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = noteModel.date!!
                calendarElements.add(CalendarAdapterElement(true, null, calendar))
            }

            calendarElements.add(CalendarAdapterElement(false, noteModel, null))
        }

        addTomorrowDateIfShould(calendarElements)

        return calendarElements
    }

    private fun addTodayDateIfShould(list: MutableList<CalendarAdapterElement>, notes: List<NoteModel>) {
        val firstCalendar = Calendar.getInstance()
        firstCalendar.timeInMillis = notes[0].date!!

        val todayCalendar = getTodayCalendar()

        if (isSameDate(firstCalendar, todayCalendar)) {
            return
        }

        list.add(CalendarAdapterElement(true, null, todayCalendar))
        list.add(CalendarAdapterElement(false, null, null))
    }

    private fun addTomorrowDateIfShould(list: MutableList<CalendarAdapterElement>) {

        if (hasDate(list, getTomorrowCalendar().timeInMillis)) {
            return
        }

        if (list[1].note == null) {
            list.add(2, CalendarAdapterElement(true, null, getTomorrowCalendar()))
            list.add(3, CalendarAdapterElement(false, null, null))

            return
        }

        for (i in 2 until list.size ) {

            if (list[i].isDate) {
                list.add(i, CalendarAdapterElement(true, null, getTomorrowCalendar()))
                list.add(i+1, CalendarAdapterElement(false, null, null))

                return
            }
        }
    }

    private fun hasDate(list: List<CalendarAdapterElement>, dateTimestamp: Long) : Boolean {
        val date = Calendar.getInstance()
        date.timeInMillis = dateTimestamp

        val filter = list.filter {

            if (!it.isDate) {
                return@filter false
            }

            isSameDate(date, it.date!!)
        }

        return filter.isNotEmpty()
    }

    private fun setupRecyclerView() {
        calendarRecyclerView.layoutManager = LinearLayoutManager(context)

        this.calendarAdapter = CalendarAdapter(this.context!!, this)
        calendarRecyclerView.adapter = calendarAdapter

        calendarRecyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.recycler_view_animation)
    }

    override fun onClickNote(noteModel: NoteModel, vararg sharedElementPairs: Pair<View, String>) {
        val intent = Intent(context, NoteActivity::class.java)
        intent.putExtra(NoteActivity.NOTE_ID_KEY, noteModel.id)

        val optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity!!, *sharedElementPairs)
        optionsCompat.toBundle()

        startActivity(intent, optionsCompat.toBundle())
    }
}
