## JDT demo assignment
This repository includes the early implementation of a Simple plugin to reuse JDT component of Eclipse project to parse a sample Java project and count the following stats:
- The list of application classes  
- For each class, the list of attributes and the list of methods.  
- For each method M, the list of methods called by this method M.  
- For each method, the list of its parameters
## The sample outputs
Project CK (which could be cloned from https://github.com/mauricioaniche/ck) was the sample Java project to be parsed. File **ck-master_stats.txt**   under the *output* folder provides the details of CK project classes, their fields and methods.
