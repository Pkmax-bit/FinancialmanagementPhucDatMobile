package com.example.financialmanagement.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.core.app.NotificationManagerCompat;
import com.example.financialmanagement.services.TaskService;

/**
 * BroadcastReceiver để xử lý action "Hoàn thành nhanh" từ notification của nhiệm vụ được ghim
 */
public class PinnedTaskActionReceiver extends BroadcastReceiver {

    public static final String ACTION_COMPLETE_TASK = "com.example.financialmanagement.ACTION_COMPLETE_TASK";
    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !ACTION_COMPLETE_TASK.equals(intent.getAction())) {
            return;
        }

        String taskId = intent.getStringExtra(EXTRA_TASK_ID);
        int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1);

        if (taskId == null || taskId.isEmpty() || notificationId == -1) {
            return;
        }

        // Gọi API để hoàn thành nhiệm vụ
        TaskService taskService = new TaskService(context);
        taskService.completeTask(taskId, new TaskService.TaskCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Hủy notification sau khi hoàn thành
                NotificationManagerCompat.from(context).cancel(notificationId);
                
                // Hiển thị thông báo thành công
                Toast.makeText(context, "Đã hoàn thành nhiệm vụ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                // Hiển thị thông báo lỗi
                Toast.makeText(context, "Không thể hoàn thành nhiệm vụ: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}



