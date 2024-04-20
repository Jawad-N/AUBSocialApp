package com.joudysabbagh.frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.joudysabbagh.frontend.api.model.Room

class FilteredRoom : AppCompatActivity() {

    private var listview: ListView? = null
    private var rooms: ArrayList<Room>? = null
    private var adapter: RoomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filteredroom)

        // Retrieve the list of rooms from the intent
        rooms = intent.getSerializableExtra("filtered_rooms") as? ArrayList<Room>

        // Initialize ListView and adapter
        listview = findViewById(R.id.listview)
        adapter = RoomAdapter(layoutInflater, rooms ?: ArrayList())
        listview?.adapter = adapter
    }

    class RoomAdapter(
        private val inflater: LayoutInflater,
        private val dataSource: List<Room>
    ) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView
            if (view == null) {
                view = inflater.inflate(R.layout.item_room, parent, false)
            }

            val roomTextView = view?.findViewById<TextView>(R.id.txtInptRoom)

            val room = getItem(position) as Room

            roomTextView?.text = "Room: ${room.room}"

            return view!!
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return 0L
        }

        override fun getCount(): Int {
            return dataSource.size
        }
    }
}
