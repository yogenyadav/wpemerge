### The design of this module is driven by following tenets:
1. Solution should work any size of input therefore stream based approach is taken.
2. Solution should be able to generate any size of output which may not fit in memory, not even on disk therefore a 
stream based approach is taken here as well. As well as chunked processing of file.
3. Solution should be extendable for any kind of input and output serialization formats (schemas).

### Build and run
- Build using maven.
- use wpe_merge shell script to run.

 