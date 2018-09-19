package com.mercury.ubermechanic.Model;


public class History {

    private String jobId;

        public History(String JobId){
            this.jobId = jobId;

        }

        public String getJobId() {
            return jobId;
        }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
