package id.forum.core.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReFetchQueriesWorker(context: Context, param: WorkerParameters) : Worker(context, param) {
    override fun doWork(): Result {

        return Result.success()
    }
}