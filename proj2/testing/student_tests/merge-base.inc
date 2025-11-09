#               "split"
#               |
#               v
# init --- orig.txt --- a.txt <- "a"
#               |
#               +----------- b.txt <- "b"
> init
<<<
+ orig.txt wug.txt
> add orig.txt
<<<
> commit "add orig.txt"
<<<
> branch split
<<<
> branch a
<<<
> branch b
<<<
> checkout a
<<<
+ a.txt wug.txt
> add a.txt
<<<
> commit "add a.txt"
<<<
> checkout b
<<<
+ b.txt wug.txt
> add b.txt
<<<
> commit "add b.txt"
<<<
> checkout master
<<<