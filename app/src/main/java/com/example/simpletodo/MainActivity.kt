package com.example.simpletodo

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var editTextTask: android.widget.EditText
    private lateinit var buttonAdd: FloatingActionButton
    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var layoutEmptyState: View
    private lateinit var textTotalTasks: TextView
    private lateinit var textCompletedTasks: TextView

    private val tasks = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupClickListeners()
        updateStatistics()
    }

    private fun initViews() {
        editTextTask = findViewById(R.id.editTextTask)
        buttonAdd = findViewById(R.id.buttonAdd)
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        textTotalTasks = findViewById(R.id.textTotalTasks)
        textCompletedTasks = findViewById(R.id.textCompletedTasks)
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter()
        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = adapter
    }

    private fun setupClickListeners() {
        buttonAdd.setOnClickListener {
            addTask()
        }

        editTextTask.setOnEditorActionListener { _, _, _ ->
            addTask()
            true
        }
    }

    private fun addTask() {
        val taskText = editTextTask.text.toString().trim()
        if (taskText.isNotEmpty()) {
            val newTask = Task(
                id = System.currentTimeMillis(),
                text = taskText,
                isCompleted = false
            )
            tasks.add(0, newTask)
            adapter.notifyItemInserted(0)
            editTextTask.text?.clear()
            updateStatistics()
            showEmptyState(false)
            recyclerViewTasks.scrollToPosition(0)

            Toast.makeText(this, "Задача добавлена!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Введите текст задачи", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTask(position: Int) {
        if (position in 0 until tasks.size) {
            tasks.removeAt(position)
            adapter.notifyItemRemoved(position)
            updateStatistics()
            showEmptyState(tasks.isEmpty())
            Toast.makeText(this, "Задача удалена", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleTaskCompletion(position: Int) {
        if (position in 0 until tasks.size) {
            tasks[position].isCompleted = !tasks[position].isCompleted
            adapter.notifyItemChanged(position)
            updateStatistics()
        }
    }

    private fun updateStatistics() {
        val total = tasks.size
        val completed = tasks.count { it.isCompleted }

        textTotalTasks.text = total.toString()
        textCompletedTasks.text = completed.toString()
    }

    private fun showEmptyState(show: Boolean) {
        layoutEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        recyclerViewTasks.visibility = if (show) View.GONE else View.VISIBLE
    }

    data class Task(
        val id: Long,
        val text: String,
        var isCompleted: Boolean
    )

    private inner class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, parent, false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            holder.bind(tasks[position])
        }

        override fun getItemCount(): Int = tasks.size

        inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
            private val checkBoxTask: CheckBox = itemView.findViewById(R.id.checkBoxTask)
            private val textTask: TextView = itemView.findViewById(R.id.textTask)
            private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)

            fun bind(task: Task) {
                textTask.text = task.text
                checkBoxTask.isChecked = task.isCompleted

                if (task.isCompleted) {
                    textTask.paintFlags = textTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    textTask.alpha = 0.6f
                    cardView.alpha = 0.8f
                } else {
                    textTask.paintFlags = textTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    textTask.alpha = 1.0f
                    cardView.alpha = 1.0f
                }

                checkBoxTask.setOnClickListener {
                    toggleTaskCompletion(adapterPosition)
                }

                buttonDelete.setOnClickListener {
                    deleteTask(adapterPosition)
                }

                itemView.setOnClickListener {
                    toggleTaskCompletion(adapterPosition)
                }
            }
        }
    }
}