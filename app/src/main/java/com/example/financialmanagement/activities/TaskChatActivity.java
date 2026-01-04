package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.ChatMessageAdapter;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.models.TaskComment;
import com.example.financialmanagement.services.TaskService;
import java.util.ArrayList;
import java.util.List;

public class TaskChatActivity extends AppCompatActivity {

    private String taskId;
    private RecyclerView recyclerView;
    private ChatMessageAdapter adapter;
    private List<TaskComment> messageList = new ArrayList<>();
    private TaskService taskService;
    private AuthManager authManager;
    private EditText editMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_chat);

        taskId = getIntent().getStringExtra("task_id");
        if (taskId == null) {
            taskId = getIntent().getStringExtra("taskId");
        }
        String taskTitle = getIntent().getStringExtra("task_title");

        setupToolbar(taskTitle);
        initializeViews();

        taskService = new TaskService(this);
        authManager = new AuthManager(this);
        
        loadMessages();
    }

    private void setupToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title != null ? title : "Trao đổi");
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        editMessage = findViewById(R.id.edit_chat_message);
        ImageButton btnSend = findViewById(R.id.btn_send_message);
        ImageButton btnAttach = findViewById(R.id.btn_attach_file);
        
        btnSend.setOnClickListener(v -> sendMessage());
        btnAttach.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng đính kèm file sẽ sớm được cập nhật", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadMessages() {
        taskService.getTaskComments(taskId, new TaskService.TaskCallback<List<TaskComment>>() {
            @Override
            public void onSuccess(List<TaskComment> result) {
                messageList.clear();
                messageList.addAll(result);
                
                if (adapter == null) {
                    adapter = new ChatMessageAdapter(TaskChatActivity.this, messageList, authManager.getUserId());
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                
                if (!messageList.isEmpty()) {
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskChatActivity.this, "Lỗi tải tin nhắn: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String content = editMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        taskService.sendTaskComment(taskId, content, new TaskService.TaskCallback<TaskComment>() {
            @Override
            public void onSuccess(TaskComment result) {
                editMessage.setText("");
                messageList.add(result);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskChatActivity.this, "Không thể gửi tin nhắn: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
