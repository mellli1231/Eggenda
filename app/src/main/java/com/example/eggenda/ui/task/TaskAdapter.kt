package com.example.eggenda.ui.task

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.eggenda.R
import com.example.eggenda.ui.database.EntryDatabase
import com.example.eggenda.ui.database.TaskEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Custom adapter for additional task properties (checkbox, time limit, etc.)
class TaskAdapter(
    context: Context,
    private val tasks: List<TaskEntry>
) : ArrayAdapter<TaskEntry>(context, R.layout.custom_list_item, tasks) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_list_item, parent, false)
        val checkBox = view.findViewById<CheckBox>(R.id.taskCheckBox)
        val taskText = view.findViewById<TextView>(R.id.taskItemText)

        val task = getItem(position)
        task?.let {
            taskText.text = it.title
            checkBox.isChecked = it.isChecked // Add a property `isChecked` to TaskEntry
            applyStrikeThrough(taskText, it.isChecked)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                it.isChecked = isChecked
                // Persist changes in the database
                CoroutineScope(Dispatchers.IO).launch {
                    EntryDatabase.getInstance(context).entryDatabaseDao.updateTask(it)
                }
                // Apply strike-through if the task is checked
                taskText.text = it.title
                applyStrikeThrough(taskText, it.isChecked)
            }
        }
        return view
    }

    // Helper method to apply or remove the strike-through effect
    private fun applyStrikeThrough(textView: TextView, isChecked: Boolean) {
        if (isChecked) {
            Log.d("TaskAdapter", "Strike-through applied")
            textView.paintFlags = textView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            textView.setTextColor(context.getColor(R.color.strike_through_color))
        } else {
            Log.d("TaskAdapter", "Strike-through applied")
            textView.paintFlags = textView.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.setTextColor(context.getColor(R.color.normal_text_color))
        }
        textView.invalidate() // force ui refresh
    }
}