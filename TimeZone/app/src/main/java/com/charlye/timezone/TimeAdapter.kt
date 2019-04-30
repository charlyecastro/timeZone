package com.charlye.timezone

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import kotlinx.android.synthetic.main.time_row.view.*

class TimeAdapter(val clockList : MutableList<CityClock?>): RecyclerView.Adapter<TimeViewHolder>() {

    //returns clocklist size
    override fun getItemCount(): Int {
        return clockList.size
    }

    // returns a timeViewHolder
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TimeViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context )
        val row = layoutInflater.inflate(R.layout.time_row, p0, false)
        return TimeViewHolder(row)
    }


    //binds the timeViewHolder with the ClockList data
    override fun onBindViewHolder(p0: TimeViewHolder, p1: Int) {
        p0?.itemView.cityText.text = clockList[p1]?.city
        p0?.itemView.countryText.text = clockList[p1]?.country
        p0?.itemView.timeText.text = clockList[p1]?.time
        p0?.itemView.dateText.text = clockList[p1]?.zone
    }

}

class TimeViewHolder(v: View) : RecyclerView.ViewHolder(v) {

}