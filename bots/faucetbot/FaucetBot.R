infile <- file("stdin")
open(infile)
repeat{
    input <- readLines(infile,1)
    args <- strsplit(input," ")[[1]]
    if(args[1]=="BEGIN"){
        L <- as.integer(args[4])
        M <- N <- matrix(0,nrow=L,ncol=L)
        x0 <- sample(2:(L-1),1)
        }
    if(args[1]=="DESTROY"){
        if(args[2]==0){
            X <- x0
            Y <- 2
            }else{
                free <- which(M[,2] == 0)
                mine <- which(N[,2] == 1)
                X <- free[which.min(abs(free-mine))]
                Y <- 2
                }
        cat(sprintf("VERTEX %s,%s\n",X-1,Y-1))
        flush(stdout())
        }
    if(args[1]=="BROKEN"){
        b <- strsplit(args[args!="N"][-(1:2)],",")
        o <- strsplit(args[3],",")[[1]]
        b <- lapply(b,as.integer)
        if(o[1]!="N") N[as.integer(o[1])+1,as.integer(o[2])+1] <- -1
        for(i in seq_along(b)){M[b[[i]][1]+1,b[[i]][2]+1] <- -1}
        }
    if(args[1]=="ACTIVATE"){
        if(args[2]==0){
            broken <- which(M[,2] == -1)
            free <- which(M[,2] == 0)
            X <- free[which.min(abs(broken-free))]
            Y <- 2
            }else{
                y <- 3
                X <- NULL
                while(length(X)<1){
                    lastrow <- which(N[,y-1]==1)
                    newrow <- unlist(sapply(lastrow,function(x)which(N[,y]==0 & abs((1:L)-x)<2)))
                    if(length(newrow)){
                        X <- sample(newrow,1)
                        Y <- y
                        }
                    y <- y+1
                    }
                }
        cat(sprintf("VERTEX %s,%s\n",X-1,Y-1))
        flush(stdout())
        }
    if(args[1]=="OWNED"){
        b <- strsplit(args[args!="N"][-(1:2)],",")
        o <- strsplit(args[3],",")[[1]]
        b <- lapply(b,as.integer)
        if(o[1]!="N") N[as.integer(o[1])+1,as.integer(o[2])+1] <- 1
        for(i in seq_along(b)){M[b[[i]][1]+1,b[[i]][2]+1] <- 1}
        }
    if(args[1]=="SCORE") q(save="no")
    }