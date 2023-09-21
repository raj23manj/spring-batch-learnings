# Spring Batch

* Types of Steps:
- Job -> tasks(multiple tasks) -> each task is each step
- chunk(100 records out of 10000) oriented steps 
  
   step => item reader(csv) -> item processor (spring batch(app)) -> item writer(DB)  

- job -> step -> tasklet (chunk-oriented-step-without-item-processor)
- job has its own context
- step has its own context
  
    job --- step1
            step2

    step1 --- tasklet1
              tasklet2 
