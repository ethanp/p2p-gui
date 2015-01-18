### Plan for managing files and transfers

* I *don't* want to host local files available for upload within the program's
  memory
* This means that *instead* I must "serve" *specifiable* chunk-indexes
  directly from the filesystem.
* And I must be able to *write* to specifiable chunk-indices, even when
  previous indices have not been written.
* Let's say that ***within* a `Chunk` bytes are transferred in-order**
* This means I can write bytes *as they're received* into the file
* This will require a remarkably small amount of RAM
* This will likely easily accommodate a caching mechanism for frequently-
  served chunks
