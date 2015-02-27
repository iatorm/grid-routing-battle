#!/bin/bash

declare -A avail
broken=
owned=

while read c p
    case "$c" in
        ACTIVATE|BROKEN) v=broken;;
        *) v=owned
    esac
    case "$c" in
        BEGIN)
            read b t n <<<"$p"
            list=$(
                eval "echo {0..$((n-1))},{0..$((n-1))}\$'\\n'" |
                shuf
            )
            for i in $list; do
                avail[$i]=1
            done;;
        DESTROY|ACTIVATE)
            for t in $(
                for i in ${!v}; do
                    [ "$i" != N ] &&
                    if [ "$c" = ACTIVATE ]; then
                        echo $(((${i%,*}+2)%n)),${i#*,}
                        echo $(((${i%,*}-2+n)%n)),${i#*,}
                    else
                        echo ${i%,*},$(((${i#*,}+1)%n))
                        echo ${i%,*},$(((${i#*,}-1+n)%n))
                    fi
                done |
                shuf
            ) $list; do
                [ "${avail[$t]}" ] && echo VERTEX $t && break
            done ||
            echo NONE;;
        BROKEN|OWNED)
            read x m $v <<<"$p";
            for i in $m ${!v}; do
                unset avail[$i]
            done;;
        SCORE)! :
    esac
do :;done