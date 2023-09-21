# Spring Batch

* Types of Steps:
- Job -> tasks(multiple tasks) -> each task is each step
- chunk(100 records out of 10000) oriented steps 
  
   step => item reader(csv) -> item processor (spring batch(app)) -> item writer(DB)  

- job -> step -> tasklet (chunk-oriented-step-without-item-processor)
