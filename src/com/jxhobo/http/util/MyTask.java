package com.jxhobo.http.util;

import android.os.Handler;
import android.os.Message;
import com.jxhobo.app.BaseApplication;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MyTask {


    public enum Status {
        FINISHED, PENDING, RUNNING;
    }

    private static final int MESSAGE_POST_PROGRESS = 2;
    private static final int MESSAGE_POST_START = 3;
    private static final int MESSAGE_POST_RESULT = 1;
    private static final InternalHandler sHandler = new InternalHandler();
    private final FutureTask mFuture;
    private volatile Status mStatus;
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();
    private final WorkerRunnable mWorker = new WorkerRunnable() {
        public Object call() throws Exception {
            mTaskInvoked.set(true);
            return postResult(doInBackground(mParams));
        }

    };

    public MyTask() {
        mStatus = Status.PENDING;
        mFuture = new FutureTask(mWorker) {
            protected void done() {
                try {
                    postResultIfNotInvoked(get());
                } catch (Exception e) {
                    Log.v("cancel");
                }
            }
        };
    }

    private void finish(Object data) {
        if (!isCancelled()) {
            onPostExecute(data);
        } else {
            onCancelled(data);
        }
        mStatus = Status.FINISHED;
    }

//    public void finished(Object data){
//        sHandler.obtainMessage(MESSAGE_POST_RESULT, new AsyncTaskResult(this, new Object[]{data})).sendToTarget();
//    }

    private Object postResult(Object data) {
        sHandler.obtainMessage(MESSAGE_POST_RESULT, new AsyncTaskResult(this, new Object[]{data})).sendToTarget();
        return data;
    }

    private void postResultIfNotInvoked(Object obj) {
        if (!mTaskInvoked.get()) {
            postResult(obj);
        }
    }

    /**
     * 取消任务
     *
     * @param flag
     * @return
     */
    public final boolean cancel(boolean flag) {
        return mFuture.cancel(flag);
    }


    public final MyTask executeOnExecutor(Executor executor, Object params[]) {
        if (mStatus == Status.PENDING) {
            mStatus = Status.RUNNING;
            sHandler.obtainMessage(MESSAGE_POST_START, new AsyncTaskResult(this, null)).sendToTarget();
            mWorker.mParams = params;
            executor.execute(mFuture);
            return this;
        } else if (mStatus == Status.RUNNING) {
            throw new IllegalStateException("Cannot execute task: the task is already running.");
        } else if (mStatus == Status.FINISHED) {
            throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
        }
        return null;
    }

    public final Object get()
            throws InterruptedException, ExecutionException {
        return mFuture.get();
    }

    public final Object get(long l, TimeUnit timeunit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return mFuture.get(l, timeunit);
    }

    public final Status getStatus() {
        return mStatus;
    }

    public final boolean isCancelled() {
        return mFuture.isCancelled();
    }

    protected void onCancelled() {
    }

    protected void onCancelled(Object obj) {
        onCancelled();
    }

    protected void onPostExecute(Object obj) {
    }

    protected void onPreExecute() {

    }

    protected void onProgressUpdate(Object aobj[]) {
    }

    protected abstract Object doInBackground(Object aobj[]);

    protected final void publishProgress(Object aobj[]) {
        if (!isCancelled()) {
            sHandler.obtainMessage(MESSAGE_POST_PROGRESS, new AsyncTaskResult(this, aobj)).sendToTarget();
        }
    }

    private static class AsyncTaskResult {

        final Object mData[];
        final MyTask mTask;

        AsyncTaskResult(MyTask mytask, Object mData[]) {
            mTask = mytask;
            this.mData = mData;
        }
    }

    private static abstract class WorkerRunnable implements Callable {
        Object mParams[];

        private WorkerRunnable() {
        }

    }

    private static class InternalHandler extends Handler {
        public void handleMessage(Message message) {
            AsyncTaskResult asynctaskresult = (AsyncTaskResult) message.obj;
            switch (message.what) {
                case MESSAGE_POST_RESULT:
                    asynctaskresult.mTask.finish(asynctaskresult.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    asynctaskresult.mTask.onProgressUpdate(asynctaskresult.mData);
                    break;
                case MESSAGE_POST_START:
                    asynctaskresult.mTask.onPreExecute();
                    break;
            }
        }

        public InternalHandler() {
            super(BaseApplication.instance().getMainLooper());
        }
    }

}