package com.survey.hujuhj;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class ServiceToCheckTheChildsLocation extends JobService {

    private boolean mSuccess = false;
    private boolean isJobCalled = false;

    @Override
    public boolean onStartJob(JobParameters params) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                for(int i=0; i<=10;i++){
                    Log.d("myTag", "run: "+i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(isJobCalled){
                        return;
                    }
                }
                mSuccess= true;
                jobFinished(params, mSuccess);
            }
        });


        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        isJobCalled = true;
        return false;
    }
}
